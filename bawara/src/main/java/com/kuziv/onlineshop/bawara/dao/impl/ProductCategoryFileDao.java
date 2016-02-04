package com.kuziv.onlineshop.bawara.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kuziv.onlineshop.bawara.dao.ProductCategoryDao;
import com.kuziv.onlineshop.bawara.models.ProductCategory;

@Repository(value = "productCategoryFileDao")
public class ProductCategoryFileDao extends FileAbstractDao<ProductCategory>
		implements ProductCategoryDao {

	public ProductCategoryFileDao() {

	}

	@Override
	public List<ProductCategory> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProductCategory getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDatasourceName() {
		// TODO Auto-generated method stub
		return null;
	}

}
