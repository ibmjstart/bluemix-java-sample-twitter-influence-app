/*-------------------------------------------------------------------*/
/* Copyright IBM Corp. 2013 All Rights Reserved                      */
/*-------------------------------------------------------------------*/

package com.sampleapp.db;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Servlet implementation class ClearAll
 */
public class ClearAll extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBUtil du = DBUtil.getInstance();
		du.clearAll();
		
		DBCursor cr = du.getCursor();
		List<DBObject> records =  cr.toArray();
		request.setAttribute("records", records);
		request.setAttribute("totinf", du.getCount());
		request.getRequestDispatcher("/displayall.jsp").forward(request, response);
	}
}
