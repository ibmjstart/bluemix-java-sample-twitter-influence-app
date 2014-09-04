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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import com.mongodb.*;
import org.ektorp.ViewQuery;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import twitter4j.JSONArray;
import twitter4j.JSONObject;

public class DBUtil {

	// For grabbing vcap_services info
	private Map<String, String> env;
	private String vcap;
	private String host, port, url;
	// username and password not currently used
	//private String host, port, username, password, url;
	private String dbname = "twitter-influence-analyzer";
	
	// For interacting with Cloudant
	protected StdCouchDbInstance dbInstance;
	protected StdCouchDbConnector db;
	
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
			
			@SuppressWarnings("rawtypes")
			Iterator iter = vcap_services.keys();
			JSONArray cloudant = null;
			
			// find instance of cloudant bound to app
			while(iter.hasNext()){
				String key = (String)iter.next();
				if(key.startsWith("cloudantNoSQLDB")){
					cloudant = vcap_services.getJSONArray(key);
				}
			}
			
			JSONObject instance = cloudant.getJSONObject(0); // Grab the first instance of mongoDB for this app (there is only one)
			JSONObject credentials = instance.getJSONObject("credentials");
			host = credentials.getString("host");
			port = credentials.getString("port");
			// not currently in use, maybe in future versions of cloudant
			//username = credentials.getString("username");
			//password = credentials.getString("password");
			url = credentials.getString("url");

			System.out.println("Found all the params");
			
			// cloudant initialization
		    HttpClient httpClient = new StdHttpClient.Builder().url(url).build();
		    dbInstance = new StdCouchDbInstance(httpClient);
		    db = new StdCouchDbConnector(dbname, dbInstance);
		    
		    db.createDatabaseIfNotExists();
		                
			System.out.println("Connected to cloudant on " + host + ":" + port);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// saves the data in the mongoDB
	public void saveData(String t_name, int totalscore, int fcount, int fscore, int rtcount, int rtscore, int mcount) {
		// check whether the document is present in the database
		// if not present just insert new doc or else just update the existing doc. 
//		boolean documentExists = repo.contains(t_name);
		//BasicDBObject query = new BasicDBObject("twitname", t_name);
		
		//boolean documentExists = infColl.find(query).count() != 0;
		
		boolean documentExists = db.contains(t_name);
		
		if (documentExists) {
			// Update the existing record
			@SuppressWarnings("unchecked")
			Map<String, Object> doc = db.get(Map.class, t_name);
			//Map<String, Object> doc = new HashMap<String, Object>();
			doc.put("_id", t_name);
			doc.put("totalscore", totalscore);
			doc.put("fcount", fcount);
			doc.put("fscore", fscore);
			doc.put("rtcount",  rtcount);
			doc.put("rtscore", rtscore);
			doc.put("mcount", mcount);
			db.update(doc);
			
			System.out.println("Existing document updated");
		} else {
			// Insert the new record
			Map<String, Object> doc = new HashMap<String, Object>();
			doc.put("_id",  t_name);
			doc.put("totalscore", totalscore);
			doc.put("fcount", fcount);
			doc.put("fscore", fscore);
			doc.put("rtcount", rtcount);
			doc.put("rtscore", rtscore);
			doc.put("mcount", mcount);
			db.create(doc);

			System.out.println("New record successfully inserted");
		}
	}
	
	// delete the selected record from mongoDB
	public void delSelected(String twitname){
		@SuppressWarnings("unchecked")
		Map<String, Object> doc = db.get(Map.class, twitname);
		db.delete(doc);
		System.out.println(twitname + " record deleted");
	}
	
	// deletes all the records from mongoDB
	public void clearAll() {
		dbInstance.deleteDatabase(dbname);
		db = new StdCouchDbConnector(dbname, dbInstance);
	    db.createDatabaseIfNotExists();
		System.out.println("Deleted all records");
	}
	
	@SuppressWarnings("rawtypes")
	public List<Map> getCursor () { 
		ViewQuery q = new ViewQuery().allDocs().includeDocs(true);
		return db.queryView(q, Map.class);
	}
	
	public int getCount() {
		return db.getAllDocIds().size();
	}
}
