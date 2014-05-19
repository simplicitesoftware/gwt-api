/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.core;

/**
 * <p>Field area defintion</p>
 */
public class FieldArea implements Comparable<FieldArea> {
	/**
	 * Field area number
	 */
	public int area;
	/**
	 * Field area label
	 */
	public String label;
	// TODO : to be completed...

	/**
	 * <p>Comparator based on area number comparision</p>
	 */
	@Override
	public int compareTo(FieldArea a) {
		return area - a.area;
	}

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