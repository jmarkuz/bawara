package com.kuziv.onlineshop.bawara.dao;

import com.kuziv.onlineshop.bawara.models.User;

public interface UserDao extends ItemDao<User> {
	
	public User getByUsername(String username);

}