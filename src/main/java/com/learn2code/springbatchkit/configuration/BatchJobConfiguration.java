package com.learn2code.springbatchkit.configuration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

import com.learn2code.springbatchkit.Interceptors.JobInterceptor;
import com.learn2code.springbatchkit.batch.DBWriter;
import com.learn2code.springbatchkit.constant.Constants;
import com.learn2code.springbatchkit.models.User;

@Configuration
public class BatchJobConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(BatchJobConfiguration.class);
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private JobInterceptor jobInterceptor;

	@Bean
	JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
		logger.info("jobRegistryBeanPostProcessor method invoked");
		JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
		postProcessor.setJobRegistry(jobRegistry);
		logger.info("jobRegistryBeanPostProcessor method invoked");
		return postProcessor;
	}

	@Bean
	public Job job(Step step) throws Exception {
		logger.info("job method invoked for the creation of the Job");
		return this.jobBuilderFactory
				.get(Constants.JOB_NAME)
				.listener(jobInterceptor)
				.preventRestart()
				.incrementer(new RunIdIncrementer())
				.validator(validator())
				.start(step)
				.build();
	}

	@Bean
	public JobParametersValidator validator() {
		logger.info("validator method invoked for validation");
		return new JobParametersValidator() {
			@Override
			public void validate(JobParameters parameters) throws JobParametersInvalidException {
				String fileName = parameters.getString(Constants.FILENAME);
				logger.info("createJobLauncher method invoked");
				if (fileName != null && fileName.isEmpty()) {
					throw new JobParametersInvalidException(
							"The patient-batch-loader.fileName parameter is required.");
				}
				try {
					Path file = Paths.get(Constants.INPUTPATH + 
							File.separator + fileName);
					if (Files.notExists(file) || !Files.isReadable(file)) {
						throw new Exception("File did not exist or was not readable");
					}
				} catch (Exception e) {
					throw new JobParametersInvalidException(
							"The input path + patient-batch-loader.fileName parameter needs to " + 
							"be a valid file location.");
				}
			}

		};
	}

	@Bean
	public Step step(ItemReader<User> itemReader,
			ItemProcessor<User, User> processor,
			ItemWriter<User> dBWriter) throws Exception {
			logger.info("step method invoked for step creation");
			return this.stepBuilderFactory
				.get(Constants.STEP_NAME)
				.<User, User>chunk(100)
				.reader(itemReader)
				.processor(processor)
				.writer(dBWriter)
				.build();
	}
	
	

	@Bean
	@StepScope
	public FlatFileItemReader<User> itemReader( @Value("#{jobParameters['" + Constants.FILENAME + "']}")String fileName) {
		logger.info("itemReader method invoked with fileName " + fileName);
		FlatFileItemReader<User> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setResource(new PathResource(Paths.get(Constants.INPUTPATH+File.separator+fileName)));
		flatFileItemReader.setName("CSV-Reader");
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
	}

	@Bean
	public LineMapper<User> lineMapper() {
		logger.info("lineMapper method invoked");
		DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames(new String[]{"id", "name", "dept", "salary"});

		BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(User.class);

		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		logger.info("lineMapper method end");
		return defaultLineMapper;
	}


}
