package com.learn2code.springbatchkit.batch;

import java.util.List;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.HibernateItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import  com.learn2code.springbatchkit.models.User;

@Component
public class DBWriter implements ItemWriter<User> {

	Logger logger = LoggerFactory.getLogger(DBWriter.class);

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void write(List<? extends User> users) throws Exception {
		logger.info("In the write method of DBWriter");
		HibernateItemWriter<User> hibernateItemWriter = new HibernateItemWriter<>();
		hibernateItemWriter.setSessionFactory(sessionFactory);
		hibernateItemWriter.write(users);
	}
	
}
