package com.startupbidder.datamodel;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
@Entity
@Indexed
@Cached(expirationSeconds=30*24*60*30)
public class Category {
	@Id public Long id;
	public String name;
	public int count = 0;
	
	public Category() {
	}
	
	public Category(long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String toString() {
		return "Category(id=" + id + ", name=" + name + ", count=" + count + ")";
	}
}
