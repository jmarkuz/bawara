package com.kuziv.onlineshop.bawara.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kuziv.onlineshop.bawara.dao.RoleDao;
import com.kuziv.onlineshop.bawara.models.Role;

@Repository(value = "roleFileDao")
public class RoleFileDao extends FileAbstractDao<Role> implements RoleDao {

	public RoleFileDao() {

	}

	@Override
	public List<Role> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDatasourceName() {
		// TODO Auto-generated method stub
		return null;
	}

}
