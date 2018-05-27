package com.n26.challenge.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.challenge.objects.TransactionEvent;
import com.n26.challenge.objects.TransactionStatistics;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Calendar;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
public class TransactionsControllerTest {

    @Autowired
    private TransactionsController transactionsController;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test_that_adding_transaction_works() throws Exception {
        TransactionEvent transactionEvent = new TransactionEvent(1D, new Date());

        String json = mapper.writeValueAsString(transactionEvent);
        this.mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isCreated());
    }


    @Test
    public void test_that_adding_expired_transaction_fails() throws Exception {
        Date currentDate = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.SECOND, -65);

        Date pastDate = c.getTime();

        TransactionEvent transactionEvent = new TransactionEvent(1D, pastDate);

        String json = mapper.writeValueAsString(transactionEvent);
        this.mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isNoContent());
    }

    @Test
    public void test_that_adding__transaction_updates_statistics() throws Exception {
        TransactionEvent transactionEvent1 = new TransactionEvent(1D, new Date());
        TransactionEvent transactionEvent2 = new TransactionEvent(3000D, new Date());

        String json1 = mapper.writeValueAsString(transactionEvent1);
        String json2 = mapper.writeValueAsString(transactionEvent2);

        this.mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json1)
        ).andExpect(status().isCreated());

        this.mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json2)
        ).andExpect(status().isCreated());

        MvcResult statistics = this.mockMvc.perform(get("/statistics")).andExpect(status().isOk()).andReturn();

        String content = statistics.getResponse().getContentAsString();
        TransactionStatistics transactionStatistics = mapper.readValue(content, TransactionStatistics.class);

        Assertions.assertThat(transactionStatistics.getCount()).isEqualTo(2);
        Assertions.assertThat(transactionStatistics.getSum()).isEqualTo(3001);
        Assertions.assertThat(transactionStatistics.getAvg()).isEqualTo(1500.5);
        Assertions.assertThat(transactionStatistics.getMin()).isEqualTo(1);
        Assertions.assertThat(transactionStatistics.getMax()).isEqualTo(3000);
    }
}
