/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.core;

//import java.util.Date;

/**
 * <p>News</p>
 */
public class News /*implements Comparable<News>*/ {
	/**
	 * <p>Date</p>
	 */
	//public Date date;
	public String date;

	/**
	 * <p>Expiry date</p>
	 */
	//public Date expdate;
	public String expdate;

	/**
	 * <p>Title</p>
	 */
	public String title;

	/**
	 * <p>Image</p>
	 */
	public Document image;

	/**
	 * <p>Content</p>
	 */
	public String content;

	/**
	 * <p>Comparator based on field order comparision</p>
	 */
	/*public int compareTo(News n) {
		long d = date.getTime() - n.date.getTime();
		return d > 0 ? 1 : d < 0 ? -1 : 0;
	}*/

	/**
	 * <p>String representation</p>
	 */
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		// TODO : to be completed...
		return s.toString();
	}
}