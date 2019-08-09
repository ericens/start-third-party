package org.zlx.mongo.mongodb.xmlSpring;


import org.zlx.mongo.mongodb.Person;

public interface PersonDAO {

	public void create(Person p);
	
	public Person readById(String id);
	
	public void update(Person p);
	
	public int deleteById(String id);
}
