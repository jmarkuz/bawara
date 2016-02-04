package com.kuziv.onlineshop.bawara.dao.impl;

import org.springframework.stereotype.Repository;

import com.kuziv.onlineshop.bawara.dao.ProductDao;
import com.kuziv.onlineshop.bawara.models.Product;

@Repository(value = "productDatabaseDao")
public class ProductDatabaseDao extends HibernateAbstractDao<Product> implements
		ProductDao {

	public ProductDatabaseDao() {

	}

}
