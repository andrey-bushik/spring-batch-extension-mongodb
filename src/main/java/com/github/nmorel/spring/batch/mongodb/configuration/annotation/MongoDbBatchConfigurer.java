package com.github.nmorel.spring.batch.mongodb.configuration.annotation;

import com.github.nmorel.spring.batch.mongodb.repository.support.MongoDbJobRepositoryFactoryBean;
import com.github.nmorel.spring.batch.mongodb.explore.support.MongoDbJobExplorerFactoryBean;
import com.mongodb.DB;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;

/** Implementation of {@link BatchConfigurer} for MongoDB */
@Component
public class MongoDbBatchConfigurer implements BatchConfigurer
{
    private DB db;

    private String collectionPrefix;

    private PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();

    private JobRepository jobRepository;

    private JobLauncher jobLauncher;

    private JobExplorer jobExplorer;

    protected MongoDbBatchConfigurer() {}

    public MongoDbBatchConfigurer( DB db )
    {
        setDb(db);
    }

    public MongoDbBatchConfigurer( DB db, String collectionPrefix )
    {
        setDb(db);
        setCollectionPrefix(collectionPrefix);
    }

    public void setDb( DB db )
    {
        this.db = db;
    }

    public void setCollectionPrefix(String collectionPrefix)
    {
        this.collectionPrefix = collectionPrefix;
    }

    @Override
    public JobRepository getJobRepository()
    {
        return jobRepository;
    }

    @Override
    public PlatformTransactionManager getTransactionManager()
    {
        return transactionManager;
    }

    @Override
    public JobLauncher getJobLauncher()
    {
        return jobLauncher;
    }

    @Override
    public JobExplorer getJobExplorer()
    {
        return jobExplorer;
    }

    @PostConstruct
    public void initialize() throws Exception
    {
        this.jobRepository = createJobRepository();
        this.jobLauncher = createJobLauncher();
        this.jobExplorer = createJobExplorer();
    }

    private JobExplorer createJobExplorer() throws Exception
    {
        MongoDbJobExplorerFactoryBean jobExplorerFactoryBean = new MongoDbJobExplorerFactoryBean();
        jobExplorerFactoryBean.setDb(db);
        if (collectionPrefix != null) {
            jobExplorerFactoryBean.setCollectionPrefix(collectionPrefix);
        }
        jobExplorerFactoryBean.afterPropertiesSet();
        return jobExplorerFactoryBean.getObject();
    }

    private JobLauncher createJobLauncher() throws Exception
    {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    private JobRepository createJobRepository() throws Exception
    {
        MongoDbJobRepositoryFactoryBean factory = new MongoDbJobRepositoryFactoryBean();
        factory.setDb(db);
        if (collectionPrefix != null) {
            factory.setCollectionPrefix(collectionPrefix);
        }
        factory.afterPropertiesSet();
        return (JobRepository) factory.getObject();
    }
}
