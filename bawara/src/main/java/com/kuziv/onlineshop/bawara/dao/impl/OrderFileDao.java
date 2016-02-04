package com.kuziv.onlineshop.bawara.dao.impl;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kuziv.onlineshop.bawara.dao.OrderDao;
import com.kuziv.onlineshop.bawara.models.Order;


@Repository(value = "orderFileDao")
public class OrderFileDao extends FileAbstractDao<Order> implements OrderDao {

	public OrderFileDao() {
		
	}
	
	@Override
	public List<Order> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDatasourceName() {
		// TODO Auto-generated method stub
		return null;
	}

}

