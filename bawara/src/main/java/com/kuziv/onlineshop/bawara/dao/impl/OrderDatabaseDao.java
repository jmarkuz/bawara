package com.kuziv.onlineshop.bawara.dao.impl;
import org.springframework.stereotype.Repository;

import com.kuziv.onlineshop.bawara.dao.OrderDao;
import com.kuziv.onlineshop.bawara.models.Order;

@Repository(value="orderDatabaseDao")
public class OrderDatabaseDao extends HibernateAbstractDao<Order> implements OrderDao {

	public OrderDatabaseDao() {
		
	}

}