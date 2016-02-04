package com.kuziv.onlineshop.bawara.dao.impl;

import java.util.List;

import com.kuziv.onlineshop.bawara.dao.accessors.DatabaseDatasourceAccessor;
import com.kuziv.onlineshop.bawara.models.Model;

public abstract class HibernateAbstractDao<T extends Model> extends DatabaseDatasourceAccessor {

	@Override
	public String getHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public List<T> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public T getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void add(T model) {
		// TODO Auto-generated method stub
		
	}

	public void update(T model) {
		// TODO Auto-generated method stub
		
	}

	public void remove(T model) {
		// TODO Auto-generated method stub
		
	}

}
