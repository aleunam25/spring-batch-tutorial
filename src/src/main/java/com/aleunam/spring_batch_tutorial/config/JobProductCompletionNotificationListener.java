package com.aleunam.spring_batch_tutorial.config;

import com.aleunam.spring_batch_tutorial.model.Product;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JobProductCompletionNotificationListener implements JobExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobProductCompletionNotificationListener.class);
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            LOGGER.info("!!! JOB FINISHED!!! Time to verify the results.");

            String query = "SELECT name, quantity, company, price, section FROM products";
            jdbcTemplate
                    .query(query, new DataClassRowMapper<>(Product.class))
                    .forEach(product -> LOGGER.info("Found <{{}}> in the database.", product));
        }
    }
}