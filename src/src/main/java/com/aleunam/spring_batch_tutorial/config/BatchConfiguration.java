package com.aleunam.spring_batch_tutorial.config;

import com.aleunam.spring_batch_tutorial.model.Product;
import com.aleunam.spring_batch_tutorial.step.ProductItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {

    private static final int PRODUCT_CHUNK_SIZE = 10;

    @Bean
    public FlatFileItemReader<Product> productItemReader() {

        return new FlatFileItemReaderBuilder<Product>()
                .name("csvProductReader")
                .resource(new ClassPathResource("products-data.csv"))
                .linesToSkip(1)
                .delimited().names("name", "quantity", "company", "price", "section") //.delimiter(",")
                .targetType(Product.class).build();
    }

    @Bean
    public ProductItemProcessor productItemProcessor() {

        return new ProductItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Product> productItemWriter(DataSource h2DataSource) {

        return new JdbcBatchItemWriterBuilder<Product>()
                .dataSource(h2DataSource) // H2 Database
                .sql("INSERT INTO products (name, quantity, company, price, section) VALUES (:name, :quantity, :company, :price, :section)")
                .beanMapped()
                .build();
    }

    @Bean
    public Step step1ProductLoading(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
            FlatFileItemReader<Product> productItemReader, ProductItemProcessor productItemProcessor,
            JdbcBatchItemWriter<Product> productItemWriter) {
        return new StepBuilder("csvStep", jobRepository)
                .<Product, Product> chunk(PRODUCT_CHUNK_SIZE, transactionManager)
                .reader(productItemReader)
                .processor(productItemProcessor)
                .writer(productItemWriter)
                .build();
    }

    @Bean
    public Job inventoryManagementJob(JobRepository jobRepository, Step step1ProductLoading,
            JobProductCompletionNotificationListener listenerCompletion) {

        return new JobBuilder("inventoryManagementJob", jobRepository)
                .start(step1ProductLoading)
                //.next(step2VolumePriceRecalculation)
                //.next(step3StockNotification)
                .listener(listenerCompletion)
                .build();
    }
}
