package com.kuziv.onlineshop.bawara.dao.impl;

import com.kuziv.onlineshop.bawara.dao.accessors.FileDatasourceAccessor;
import com.kuziv.onlineshop.bawara.models.Model;

public abstract class FileAbstractDao<T extends Model> extends
		FileDatasourceAccessor {

	public void add(T model) {
		// TODO Auto-generated method stub

	}

	public void update(T model) {
		// TODO Auto-generated method stub

	}

	public void remove(T model) {
		remove(model.getId());
	}

	public void remove(Long id) {

	}

}
