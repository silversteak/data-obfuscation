package com.learn2code.springbatchkit.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.learn2code.springbatchkit.constant.Constants;

@RestController
@RequestMapping("/api/load")
public class Controller {

	private static final Logger logger = LoggerFactory.getLogger(Controller.class);


	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	JobRepository JobRepository;
	
	@Autowired
	Job job;

	private JobExecution execution = null;
	
	@GetMapping(("/{fileName:.+}"))
	@ResponseBody
	public ResponseEntity<String> load(@PathVariable String fileName) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		
		Map<String, JobParameter> maps = new HashMap<>();
		maps.put("time", new JobParameter(System.currentTimeMillis()));
		maps.put(Constants.FILENAME, new JobParameter(fileName));
		JobParameters parameters = new JobParameters(maps);
		try {
			logger.info("JOB Started");
			logger.info("The name of thread " + Thread.currentThread().getName());
			execution = jobLauncher.run(job, parameters);
			logger.info("The name of thread after launching the job" + Thread.currentThread().getName());
		} catch (Exception e) {
			return new ResponseEntity<String>("Failure: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(execution.getStatus().toString(), HttpStatus.OK);
	}

}
