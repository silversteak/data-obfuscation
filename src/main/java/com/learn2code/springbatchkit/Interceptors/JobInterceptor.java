package com.learn2code.springbatchkit.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobInterceptor implements JobExecutionListener {

	private static final Logger logger = LoggerFactory.getLogger(JobInterceptor.class);

	private long startTime;
	private long endTime;
	@Override
	public void beforeJob(JobExecution jobExecution) {
		logger.info("The name of thread " + Thread.currentThread().getName());
		startTime = System.currentTimeMillis();
		logger.info("The current execution " + jobExecution.getJobConfigurationName() + " "+ jobExecution.getJobParameters().toString());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		endTime = System.currentTimeMillis();
		logger.info("The time taken to run the job " + (endTime - startTime) / 100);
		
		if( jobExecution.getStatus() == BatchStatus.COMPLETED ){
			logger.info("The job execution is " + BatchStatus.COMPLETED);
		}
		else if(jobExecution.getStatus() == BatchStatus.FAILED){
			logger.info("The job execution is " + BatchStatus.FAILED);
		}

	}

}
