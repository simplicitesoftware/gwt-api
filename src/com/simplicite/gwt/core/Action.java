/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.core;

/**
 * <p>Business object action</p>
 */
public class Action {
	/**
	 * <p>Name</p>
	 */
	public String name;

	/**
	 * <p>Translated label</p>
	 */
	public String label;

	/**
	 * <p>With confirmation ?</p>
	 */
	public boolean confirm;

	/**
	 * <p>Visible on list ?</p>
	 */
	public boolean listVisible;

	/**
	 * <p>Visible on list items ?</p>
	 */
	public boolean listItemVisible;

	/**
	 * <p>Visible on form ?</p>
	 */
	public boolean formVisible;

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