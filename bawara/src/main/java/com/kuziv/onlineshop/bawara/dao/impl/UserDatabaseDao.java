package com.kuziv.onlineshop.bawara.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.kuziv.onlineshop.bawara.dao.UserDao;
import com.kuziv.onlineshop.bawara.models.User;

@Repository("userDatabaseDao")
public class UserDatabaseDao extends HibernateAbstractDao<User> implements UserDao {

	public UserDatabaseDao() {

	}

	@Override
	public User getByUsername(String username) {
		Criteria criteria = getSession().createCriteria(User.class);
		criteria.add(Restrictions.eq("username", username));
		return (User) criteria.uniqueResult();
	}
}
