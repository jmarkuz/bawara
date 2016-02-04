package com.kuziv.onlineshop.bawara.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "role")
public class Role extends Model {

	private static final long serialVersionUID = 5700180119859374531L;

	@Column(name = "title")
	private RoleList title;

	@Column(name = "description")
	private String description;

	@ManyToMany(mappedBy = "roles")
	private Set<User> users = new HashSet<>();

	public Role() {
		super();
	}

	public Role(Long id) {
		super(id);
	}

	public RoleList getTitle() {
		return title;
	}

	public void setTitle(RoleList title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

}
