/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.core;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>User session data and rights</p>
 */
public class Grant {
	/**
	 * <p>User ID</p>
	 */
	public String userid;
	/**
	 * <p>User login</p>
	 */
	public String login;
	/**
	 * <p>User language</p>
	 */
	public String lang;
	/**
	 * <p>User first name</p>
	 */
	public String firstname;
	/**
	 * <p>User last name</p>
	 */
	public String lastname;
	/**
	 * <p>User email</p>
	 */
	public String email;
	
	/**
	 * <p>User responsibilities</p>
	 */
	public ArrayList<String> responsibilities;
	
	/**
	 * <p>Constructor</p>
	 */
	public Grant() {
		responsibilities = new ArrayList<String>();
	}

	/**
	 * <p>Check wether the user has specified resposibility</p>
	 * @param responsibility Responsibility
	 */
	public boolean hasResponsibility(String responsibility) {
		return responsibilities.contains(responsibility);
	}
	
	/**
	 * <p>Checks wether user has administrative rights</p>
	 */
	public boolean isAdmin() {
		return hasResponsibility("ADMIN");
	}

	/**
	 * <p>String representation</p>
	 */
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("Id: " + userid + "\n");
		s.append("Login: " + login + "\n");
		s.append("Language: " + lang + "\n");
		s.append("First name: " + firstname + "\n");
		s.append("Last name: " + lastname + "\n");
		s.append("Email: " + email + "\n");
		s.append("Responsibilities:\n");
		for (Iterator<String> i = responsibilities.iterator(); i.hasNext();)
			s.append("\t" + i.next() + "\n");
		return s.toString();
	}
}