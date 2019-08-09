package org.zlx.mongo.mongodb;


import com.mongodb.MongoClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.net.UnknownHostException;

public class SpringDataMongoDBMain {

	public static final String DB_NAME = "journaldev";
	public static final String PERSON_COLLECTION = "Person";
	public static final String MONGO_HOST = "localhost";
	public static final int MONGO_PORT = 27017;

	MongoClient mongo;
	MongoOperations mongoOps;
	
	@Before
	public void init() throws UnknownHostException{
		 mongo = new MongoClient(
				MONGO_HOST, MONGO_PORT);
		 mongoOps = new MongoTemplate(mongo, DB_NAME);
	}
	
	@Test
	public  void  set(){
		Person p = new Person("113", "PankajKr", "Bangalore, India");
		mongoOps.insert(p, PERSON_COLLECTION);

	}
	
	@Test
	public void get(){
		Person p1 = mongoOps.findOne(
				new Query(Criteria.where("name").is("PankajKr")),
				Person.class, PERSON_COLLECTION);

		System.out.println(p1);
	}

	
	
	@Test
	public void  remov() {

		mongoOps.dropCollection(PERSON_COLLECTION);
		mongo.close();
		
	}
}