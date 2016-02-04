package com.kuziv.onlineshop.bawara.dao.impl;

import org.springframework.stereotype.Repository;

import com.kuziv.onlineshop.bawara.dao.RoleDao;
import com.kuziv.onlineshop.bawara.models.Role;

@Repository(value = "roleDatabaseDao")
public class RoleDatabaseDao extends HibernateAbstractDao<Role> implements
		RoleDao {

	public RoleDatabaseDao() {

	}

}