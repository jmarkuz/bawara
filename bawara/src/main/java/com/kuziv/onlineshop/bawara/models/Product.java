package com.kuziv.onlineshop.bawara.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "product")
public class Product extends Model {

	private static final long serialVersionUID = 3563148616780649153L;

	@Column(name = "title", length = 50)
	private String title;

	@Column(name = "description", length = 1024)
	private String description;

	@Column(name = "count")
	private int count; // quantity

	@Column(name = "price")
	private BigDecimal price;

	@ManyToOne
	private ProductCategory productCategory;

	public Product() {
		super();
	}

	public Product(Long id) {
		super(id);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

}
