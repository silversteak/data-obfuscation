package com.learn2code.springbatchkit.configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Component;

@Component
@EnableBatchProcessing
public class BatchConfiguration implements BatchConfigurer {

	Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);

	
	/*Persits the meta-data about batch jobs*/
	private JobRepository jobRepository;
	/*Retrives meta data from Job Repository*/
	private JobExplorer jobExplorer;
	/*Runs jobs with the given paramaters*/
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier(value = "transactionManager")
	private HibernateTransactionManager transactionManager;

	@Autowired
	@Qualifier(value = "dataSource")
	private DataSource datasource;
	
	@Override
	public JobRepository getJobRepository() throws Exception {
		return this.jobRepository;
	}

	@Override
	public HibernateTransactionManager getTransactionManager() throws Exception {
		return this.transactionManager;
	}

	@Override
	public JobLauncher getJobLauncher() throws Exception {
		return this.jobLauncher;
	}

	@Override
	public JobExplorer getJobExplorer() throws Exception {
		return this.jobExplorer;
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	protected JobLauncher createJobLauncher() throws Exception {
		logger.info("createJobLauncher method invoked");
	    SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
	    jobLauncher.setJobRepository(jobRepository);
	    
	    /*Running the job on different threads*/
	    //jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
	    
	    jobLauncher.afterPropertiesSet();
		logger.info("createJobLauncher method end");
	    return jobLauncher;
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	protected JobRepository createJobRepository() throws Exception {
		logger.info("createJobRepository method invoked");
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
	    factory.setDataSource(this.datasource);
	    factory.setTransactionManager(getTransactionManager());
	    factory.afterPropertiesSet();
		logger.info("createJobRepository method end");
	    return factory.getObject();
	}
	
	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		logger.info("afterPropertiesSet method invoked");
	    this.jobRepository = createJobRepository();
	    JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
	    jobExplorerFactoryBean.setDataSource(this.datasource);
	    jobExplorerFactoryBean.afterPropertiesSet();
	    this.jobExplorer = jobExplorerFactoryBean.getObject();
	    this.jobLauncher = createJobLauncher();
		logger.info("afterPropertiesSet method invoked");

	}
	

}
