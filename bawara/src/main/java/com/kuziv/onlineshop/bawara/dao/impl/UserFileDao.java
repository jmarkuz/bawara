package com.kuziv.onlineshop.bawara.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kuziv.onlineshop.bawara.dao.UserDao;
import com.kuziv.onlineshop.bawara.models.User;

@Repository(value = "userFileDao")
public class UserFileDao extends FileAbstractDao<User> implements UserDao {

	@Override
	public List<User> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDatasourceName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

}
