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

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

import twitter4j.JSONArray;
import twitter4j.JSONObject;

public class DBUtil {
    
	protected CloudantClient client;                         // For interacting with Cloudant
	protected Database db;                                   //	protected StdCouchDbInstance dbInstance
	private String dbname = "twitter-influence-analyzer";    // database name (can be what you want just make it unique)
	private static DBUtil instance;                          // Make this a singleton

    public static synchronized DBUtil getInstance() {
        if (instance == null) {
            instance = new DBUtil();
        }
        return instance;
    }
	
	private DBUtil() {
		Map<String, String> env;env = System.getenv();
		String vcap = env.get("VCAP_SERVICES");
		
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
			
			// Get all VCAP_SERVICES credentials
			String host = credentials.getString("host");
			String port = credentials.getString("port");
			String username = credentials.getString("username");
			String password = credentials.getString("password");
			String url = credentials.getString("url");

			System.out.println("Found all the params: "+username+ " "+password+" "+url);
			
			// Create the client connection the the database and then create the database
			client = new CloudantClient(url,username,password);
			db = client.database(dbname, true);			
			
			System.out.println("Connected to cloudant on " + host + ":" + port);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// saves the data in the CloudantDB
	public void saveData(String t_name, int totalscore, int fcount, int fscore, int rtcount, int rtscore, int mcount) {
		// check whether the document is present in the database
		// if not present just insert new doc or else just update the existing doc. 
		
		boolean documentExists = db.contains(t_name);		
		
		if (documentExists) {
			// Update the existing record
			@SuppressWarnings("unchecked")
			Map<String, Object> doc = db.find(Map.class, t_name);
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

			db.save(doc);

			System.out.println("New record successfully inserted");
		}
	}
	
	// delete the selected record from mongoDB
	public void delSelected(String twitname){
		@SuppressWarnings("unchecked")
		Map<String, Object> doc = db.find(Map.class, twitname);
		db.remove(doc);
		System.out.println(twitname + " record deleted"); 
	}
	
	// deletes all the records from mongoDB
	public void clearAll() {
	    client.deleteDB(dbname,"delete database");
	    db = client.database(dbname, true);
	}
	
	@SuppressWarnings("rawtypes")
	public List<Map> getCursor () { 
		List<Map> docs = db.view("_all_docs")
                .includeDocs(true)
                .query(Map.class);
		return docs;
	}
	
	public int getCount() {
		List<Map> docs = db.view("_all_docs")
                .includeDocs(true)
                .query(Map.class);
		
		return docs.size(); 
	}
}
