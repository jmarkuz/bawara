package com.kuziv.onlineshop.bawara.dao.impl;

import org.springframework.stereotype.Repository;

import com.kuziv.onlineshop.bawara.dao.ProductCategoryDao;
import com.kuziv.onlineshop.bawara.models.ProductCategory;

@Repository(value="productCategoryDatabaseDao")
public class ProductCategoryDatabaseDao extends HibernateAbstractDao<ProductCategory> implements ProductCategoryDao {

	public ProductCategoryDatabaseDao() {
		
	}

}
