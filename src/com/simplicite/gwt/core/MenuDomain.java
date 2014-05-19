/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.core;

import java.util.ArrayList;

/**
 * <p>Menu domain definition</p>
 */
public class MenuDomain {
	public String name;
	public String label;

	/**
	 * <p>Menu domain items</p>
	 */
	public ArrayList<MenuItem> items;

	/**
	 * <p>Constructor</p>
	 */
	public MenuDomain() {
		items = new ArrayList<MenuItem>();
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