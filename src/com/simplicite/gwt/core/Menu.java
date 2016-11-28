/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.core;

import java.util.ArrayList;

/**
 * <p>Main menu definition</p>
 */
public class Menu {
	/**
	 * <p>Menu domains</p>
	 */
	public ArrayList<MenuDomain> domains;
	
	/**
	 * <p>Constructor</p>
	 */
	public Menu() {
		domains = new ArrayList<MenuDomain>();
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