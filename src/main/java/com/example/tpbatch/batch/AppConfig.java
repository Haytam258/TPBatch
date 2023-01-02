package com.example.tpbatch.batch;

import com.example.tpbatch.batch.launcher.BatchLauncher;
import com.example.tpbatch.batch.processor.TransferDTOProcessor;
import com.example.tpbatch.batch.writer.TransferDTOWriter;
import com.example.tpbatch.dto.TransferDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableBatchProcessing
@Configuration
@EnableScheduling
@EnableJpaRepositories("com.example.tpbatch.repositories")
@EntityScan("com.example.tpbatch.entities")
public class AppConfig {

    @Value("classpath:/input/transaction.csv")
    private Resource inputResource;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;


    @Bean
    public JobRepository jobRepository() throws Exception {
        ResourcelessTransactionManager transactionManager = new ResourcelessTransactionManager();
        MapJobRepositoryFactoryBean jobRepository =  new MapJobRepositoryFactoryBean(transactionManager);
        return jobRepository.getObject();
    }


    @Bean
    public SimpleJobLauncher jobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository());
        return jobLauncher;
    }

    @Bean
    public FlatFileItemReader<TransferDto> reader(){
        FlatFileItemReader<TransferDto> itemReader = new FlatFileItemReader<TransferDto>();
        itemReader.setLineMapper(lineMapper());
        itemReader.setResource(inputResource);
        return itemReader;
    }

    @Bean
    public LineMapper<TransferDto> lineMapper(){
        DefaultLineMapper<TransferDto> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("idTransaction", "idCompte", "montant", "dateTransaction");
        BeanWrapperFieldSetMapper<TransferDto> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(TransferDto.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public ItemWriter<TransferDto> writer() {
        return new TransferDTOWriter();
    }

    @Bean
    public ItemProcessor<TransferDto, TransferDto> processor() {
        return new TransferDTOProcessor();
    }

    @Bean
    public Step step(){
        return stepBuilderFactory.get("step").<TransferDto, TransferDto> chunk(2).reader(reader()).writer(writer()).build();
    }

    @Bean(name = "importTransactions")
    public Job importTransactions(JobBuilderFactory jobs){
        return jobs.get("importTransactions").start(step()).build();
    }


    @Bean
    public BatchLauncher launchBatch() {
        return new BatchLauncher();

    }

    @Scheduled(cron = "0,30 * * * * *")
    public void scheduleFixedDelayTask() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        launchBatch().run();
    }
}
