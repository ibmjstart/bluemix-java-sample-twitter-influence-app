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
package com.sampleapp.web;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.klout4j.Klout;
import org.klout4j.KloutScore;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

/**
 * Servlet implementation class DispCalc
 */
public class DispCalc extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DispCalc() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String twitterUsername = request.getParameter("twitter_name");
		
		// The factory instance is re-useable and thread safe.
	    Twitter twitter = new TwitterFactory().getInstance();
	    
		try {
			User a_name = twitter.showUser(twitterUsername);
			int followerCount = a_name.getFollowersCount();
			List<Status> retweets = twitter.getUserTimeline(twitterUsername, new Paging(1, 10)); // get the first ten tweets
			int retweetCount = 0;

			for (Status tweet : retweets) {
				retweetCount += (int) tweet.getRetweetCount();
			}
			
			System.out.println("The rtcount is: " + retweetCount);
			
			int mentionCount = 0;
			
			int retweetScore = 0;
			int followerScore = 0;
			
			if (retweetCount >= 100000)
				retweetScore = 60;
			else if (retweetCount >= 20000)
				retweetScore = 50;
			else if (retweetCount >= 10000)
				retweetScore = 40;
			else if (retweetCount >= 5000)
				retweetScore = 30;
			else if (retweetCount >= 1000)
				retweetScore = 20;
			else if (retweetCount >= 500)
				retweetScore = 10;
			else if (retweetCount >= 100)
				retweetScore = 5;
			else if (retweetCount >= 10)
				retweetScore = 1;
			
			if (followerCount >= 10000000)
				followerScore = 40;
			else if (followerCount >= 1000000)
				followerScore = 35;
			else if (followerCount >= 500000)
				followerScore = 30;
			else if (followerCount >= 100000)
				followerScore = 25;
			else if (followerCount >= 1000)
				followerScore = 20;
			else if (followerCount >= 500)
				followerScore = 15;
			else if (followerCount >= 100)
				followerScore = 10;
			else if (followerCount >= 10)
				followerScore = 5;
			
			
			// Search API call to calculate the mentions out of 100
			Query query = new Query("@" + twitterUsername);
			query.setCount(100);
			query.setResultType(Query.RECENT);
		    QueryResult result = twitter.search(query);
		    mentionCount += result.getTweets().size();
		    
			System.out.println("the mcount is: " + mentionCount);
		    
			// Calculate the total score of the user.
			int totalscore = retweetScore + followerScore + mentionCount;
			
			System.out.println("The total score is: " + totalscore);
			
			// tweets to be displayed on the google maps 
			Query query1 = new Query("@" + twitterUsername);
			query.setCount(20);
			query.setResultType(Query.RECENT);
		    QueryResult result1 = twitter.search(query1);
		    
		    //  Klout API calls 
		    Properties prop = new Properties();
		    //load a properties file from the classpath
    		prop.load(getClass().getClassLoader().getResourceAsStream("klout.properties"));
		    String kloutKey = prop.getProperty("kloutkey"); 
		    
			Klout klout = new Klout(kloutKey);
			
			String kloutScore = "";
			
			try {
				KloutScore kScore = klout.kloutScore(twitterUsername);
				kloutScore = Double.toString(kScore.getKscore());
			} catch (Exception e){
				e.printStackTrace();
				kloutScore = "n/a";
			}
			
		    request.setAttribute("totalscore", totalscore);
		    request.setAttribute("t_name", twitterUsername);
		    request.setAttribute("fcount", followerCount);
		    request.setAttribute("fscore", followerScore);
		    request.setAttribute("rtcount", retweetCount);
		    request.setAttribute("rtscore", retweetScore);
		    request.setAttribute("mcount", mentionCount);
		    request.setAttribute("rtweets", retweets);
		    request.setAttribute("result1", result1);
		    request.setAttribute("score", kloutScore);  
		     
		    request.getRequestDispatcher("/result.jsp").forward(request, response);
		} catch (TwitterException e) {
			e.printStackTrace();
			
			throw new ServletException("Encountered a problem fetching data from Twitter - " + e.getErrorMessage());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
}