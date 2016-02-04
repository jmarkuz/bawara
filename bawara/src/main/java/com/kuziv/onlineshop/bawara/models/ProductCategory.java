package com.kuziv.onlineshop.bawara.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "product_category")
public class ProductCategory extends Model {

	private static final long serialVersionUID = 6076895028121326242L;

	@Column(name = "title", length = 50)
	private String title;

	@OneToMany(mappedBy = "productCategory", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Product> products = new HashSet<>();

	public ProductCategory() {
		super();
	}

	public ProductCategory(Long id) {
		super(id);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Set<Product> getProducts() {
		return products;
	}

}