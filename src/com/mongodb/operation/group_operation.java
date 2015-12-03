package com.mongodb.operation;

import java.util.Iterator;
import java.util.List;

import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.spring.Entity.GroupRequest;

public class group_operation {

	
	public Document find(String groupname,BasicDBObject fields){


		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		MongoDatabase database = mongoClient.getDatabase("register");
		MongoCollection<Document> collection = database.getCollection("group_control");
		
		Document doc = collection.find(new Document("groupname",groupname)).projection(fields).first();
		
		mongoClient.close();
		return doc;
	}
	
	public void singleupdate(String epn, String srcpath, String srcval){
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		MongoDatabase database = mongoClient.getDatabase("register");
		MongoCollection<Document> collection = database.getCollection("device_status");
		
		collection.updateOne(new Document("endpoint_client_name",epn), 
				new Document("$set", new Document(srcpath,srcval)));
		mongoClient.close();
	}
	
	
	public void newgroup(String gpname, String gpdtl){
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		MongoDatabase database = mongoClient.getDatabase("register");
		MongoCollection<Document> collection = database.getCollection("group_control");

		Document myDoc = Document.parse(gpdtl);
		
		collection.deleteOne(new Document("groupname",myDoc.get("endpoint_client_name")));
		collection.insertOne(myDoc);

		mongoClient.close();
	}
	
	public void updategroup(String gpname, List<String> srcval){
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		MongoDatabase database = mongoClient.getDatabase("register");
		MongoCollection<Document> collection = database.getCollection("group_control");
		
		collection.updateOne(new Document("groupname",gpname), 
				new Document("$set", new Document("devices",srcval)));

		mongoClient.close();
	}
	
	public void listupdate(String epn, String srcpath, List<String> srcval){
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		MongoDatabase database = mongoClient.getDatabase("register");
		MongoCollection<Document> collection = database.getCollection("device_status");
		
		collection.updateOne(new Document("endpoint_client_name",epn), 
				new Document("$set", new Document(srcpath,srcval)));
		mongoClient.close();
	}
	
	public void gprstlistupdate(String epn, String srcpath, List<DBObject> srcval){
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		MongoDatabase database = mongoClient.getDatabase("register");
		MongoCollection<Document> collection = database.getCollection("device_status");
		
		collection.updateOne(new Document("endpoint_client_name",epn), 
				new Document("$set", new Document(srcpath,srcval)));
		mongoClient.close();
	}
	
	public void gpmsglistupdate(String gpname, String srcpath, List<DBObject> srcval){
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		MongoDatabase database = mongoClient.getDatabase("register");
		MongoCollection<Document> collection = database.getCollection("group_control");
		
		collection.updateOne(new Document("groupname",gpname), 
				new Document("$set", new Document(srcpath,srcval)));
		mongoClient.close();
	}
	
	
	public void gpwholeupdate(String epn, String srcpath, String gpdtl){
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		MongoDatabase database = mongoClient.getDatabase("register");
		MongoCollection<Document> collection = database.getCollection("device_status");
		Document myDoc = Document.parse(gpdtl);
		collection.updateOne(new Document("endpoint_client_name",epn), 
				new Document("$set", new Document(srcpath,myDoc)));
		mongoClient.close();
	}
	
	
	public void deleterggroup(String gpname){
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		MongoDatabase database = mongoClient.getDatabase("register");
		MongoCollection<Document> collection = database.getCollection("group_control");
		
		collection.deleteOne(new Document("groupname",gpname));

		mongoClient.close();
	}
	
	
	public Iterator<Document> find_by_owner(String owner,BasicDBObject fields){


		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		MongoDatabase database = mongoClient.getDatabase("register");
		MongoCollection<Document> collection = database.getCollection("group_control");
		
		Iterator<Document> docs = collection.find(new Document("endpoint_client_name",owner)).projection(fields).iterator();
		
		mongoClient.close();
		return docs;
	}
	
	

}
