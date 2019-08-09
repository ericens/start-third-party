package org.zlx.mongo;

import com.mongodb.*;

public class MongoDBJDBC {
   public static void main( String args[] ){
      try{   
		 // To connect to mongodb server
         MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
         // Now connect to your databases
         DB db = mongoClient.getDB( "test" );
		 System.out.println("Connect to database successfully");
//         boolean auth = db.authenticate(myUserName, myPassword);
//		 System.out.println("Authentication: "+auth);
		 
		 // 创建表 
		 DBCollection coll = db.createCollection("mycol", null);
         System.out.println("Collection created successfully");
         
         //定位表
         coll = db.getCollection("mycol");
         System.out.println("Collection mycol selected successfully");
         
         
         // 插入
         
         BasicDBObject doc = new BasicDBObject("title", "MongoDB").
                 append("description", "database").
                 append("likes", 100).
                 append("url", "http://www.tutorialspoint.com/mongodb/").
                 append("by", "tutorials point");
        coll.insert(doc);
        System.out.println("Document inserted successfully");
        
        //更新
        DBCursor cursor = coll.find();
        while (cursor.hasNext()) { 
           DBObject updateDocument = cursor.next();
           updateDocument.put("likes","200");
           coll.update(updateDocument, updateDocument); 
        }
        // 查找
        
        //删除
         DBObject myDoc = coll.findOne();
         coll.remove(myDoc);
         
         cursor = coll.find();
        int i=1;
        while (cursor.hasNext()) { 
           System.out.println("Inserted Document: "+i);
           System.out.println(cursor.next());
           i++;
        }
              
      }catch(Exception e){
	     System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	  }
   }
}
