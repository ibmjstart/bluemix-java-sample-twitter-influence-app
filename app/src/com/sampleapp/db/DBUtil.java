/*-------------------------------------------------------------------*/
/*                                                                   */
/*                                                                   */
/* Copyright IBM Corp. 2013 All Rights Reserved                      */
/*                                                                   */
/*                                                                   */
/*-------------------------------------------------------------------*/
/*                                                                   */
/*        NOTICE TO USERS OF THE SOURCE CODE EXAMPLES                */
/*                                                                   */
/* The source code examples provided by IBM are only intended to     */
/* assist in the development of a working software program.          */
/*                                                                   */
/* International Business Machines Corporation provides the source   */
/* code examples, both individually and as one or more groups,       */
/* "as is" without warranty of any kind, either expressed or         */
/* implied, including, but not limited to the warranty of            */
/* non-infringement and the implied warranties of merchantability    */
/* and fitness for a particular purpose. The entire risk             */
/* as to the quality and performance of the source code              */
/* examples, both individually and as one or more groups, is with    */
/* you. Should any part of the source code examples prove defective, */
/* you (and not IBM or an authorized dealer) assume the entire cost  */
/* of all necessary servicing, repair or correction.                 */
/*                                                                   */
/* IBM does not warrant that the contents of the source code         */
/* examples, whether individually or as one or more groups, will     */
/* meet your requirements or that the source code examples are       */
/* error-free.                                                       */
/*                                                                   */
/* IBM may make improvements and/or changes in the source code       */
/* examples at any time.                                             */
/*                                                                   */
/* Changes may be made periodically to the information in the        */
/* source code examples; these changes may be reported, for the      */
/* sample code included herein, in new editions of the examples.     */
/*                                                                   */
/* References in the source code examples to IBM products, programs, */
/* or services do not imply that IBM intends to make these           */
/* available in all countries in which IBM operates. Any reference   */
/* to the IBM licensed program in the source code examples is not    */
/* intended to state or imply that IBM's licensed program must be    */
/* used. Any functionally equivalent program may be used.            */
/*-------------------------------------------------------------------*/
package com.sampleapp.db;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.json.*;

import com.mongodb.*;

public class DBUtil {

	// For grabbing vcap_services info
	private Map<String, String> env;
	private String vcap;
	private String host, port, username, password, database;
	
	// For interacting with mongo
	private Mongo mongoClient;
	private DB db;
	private DBCollection infColl; // influencer collection
	
	// Make this a singleton
	private static DBUtil instance;

    public static synchronized DBUtil getInstance() {
        if (instance == null) {
            instance = new DBUtil();
        }
        return instance;
    }
	
	private DBUtil() {
		env = System.getenv();
		vcap = env.get("VCAP_SERVICES");
		
		if (vcap == null) {
			System.out.println("No VCAP_SERVICES found");
			return;
		}

		System.out.println("VCAP_SERVICES found");
		
		try {
			JSONObject vcap_services = new JSONObject(vcap);
			
			Iterator iter = vcap_services.keys();
			JSONArray mongo = null;
			
			// find instance of mongodb bound to app
			while(iter.hasNext()){
				String key = (String)iter.next();
				if(key.startsWith("mongodb")){
					mongo = vcap_services.getJSONArray(key);
				}
			}
			
			JSONObject instance = mongo.getJSONObject(0); // Grab the first instance of mongoDB for this app (there is only one)
			JSONObject credentials = instance.getJSONObject("credentials");
			host = credentials.getString("hostname");
			port = credentials.getString("port");
			username = credentials.getString("username");
			password = credentials.getString("password");
			database = credentials.getString("db");

			System.out.println("Found all the params");
			
			// Mongo initialization
			mongoClient = new Mongo(host,Integer.parseInt(port));
			db = mongoClient.getDB(database);
			
			System.out.println("Connected to mongoDB on " + host + ":" + port);

			if (db.authenticate(username, password.toCharArray())) {
				infColl = db.getCollection("infcollection");
				System.out.println("Authenticated with mongoDB successfully");
			} else {
				throw new Exception("Authentication Failed");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// saves the data in the mongoDB
	public void saveData(String t_name, int totalscore, int fcount, int fscore, int rtcount, int rtscore, int mcount) {
		// check whether the document is present in the database
		// if not present just insert new doc or else just update the existing doc. 
		BasicDBObject query = new BasicDBObject("twitname", t_name);
		
		boolean documentExists = infColl.find(query).count() != 0;
		
		if (documentExists) {
			// Update the existing record
			infColl.update(new BasicDBObject().append("twitname",t_name), new BasicDBObject().append("$set",new BasicDBObject().append("totalscore",totalscore)));
			infColl.update(new BasicDBObject().append("twitname",t_name), new BasicDBObject().append("$set",new BasicDBObject().append("fcount",fcount)));
			infColl.update(new BasicDBObject().append("twitname",t_name), new BasicDBObject().append("$set",new BasicDBObject().append("fscore",fscore)));
			infColl.update(new BasicDBObject().append("twitname",t_name), new BasicDBObject().append("$set",new BasicDBObject().append("rtcount",rtcount)));
			infColl.update(new BasicDBObject().append("twitname",t_name), new BasicDBObject().append("$set",new BasicDBObject().append("rtscore",rtscore)));
			infColl.update(new BasicDBObject().append("twitname",t_name), new BasicDBObject().append("$set",new BasicDBObject().append("mcount",mcount)));
			
			System.out.println("Existing document updated");
		} else {
			// Insert the new record
			BasicDBObject doc = new BasicDBObject("twitname", t_name)
					.append("totalscore", totalscore)
					.append("fcount", fcount)
					.append("fscore", fscore)
					.append("rtcount", rtcount)
					.append("rtscore", rtscore)
					.append("mcount", mcount);
			
			infColl.insert(doc);
			System.out.println("New record successfully inserted");
		}
	}
	
	// delete the selected record from mongoDB
	public void delSelected(String twitname){
		infColl.remove(new BasicDBObject().append("twitname", twitname));
		System.out.println(twitname + " record deleted");
	}
	
	// deletes all the records from mongoDB
	public void clearAll() {
		infColl.remove(new BasicDBObject());
		System.out.println("Deleted all records");
	}
	
	public DBCursor getCursor () { 
		return infColl.find();
	}
	
	public int getCount() {
		return (int) infColl.getCount();
	}
}
