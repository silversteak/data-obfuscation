package com.learn2code.springbatchkit.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.learn2code.springbatchkit.models.User;

@Repository
public class UserRepository {

	Logger logger = LoggerFactory.getLogger(UserRepository.class);

	@Autowired
	private SessionFactory sessionFactory;

	public UserRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional
	public boolean save(List<User> users) {
		Session session = this.sessionFactory.getCurrentSession();
		for(User user : users) {
		    session.save(user);
		}
		return true;
	}
	
	@Transactional
	public List<User> getListUser(){
		Session session = this.sessionFactory.getCurrentSession();
		String sql = " FROM User u";
		Query<User> query = session.createQuery(sql, User.class);
		return query.getResultList();
	}
	

}
