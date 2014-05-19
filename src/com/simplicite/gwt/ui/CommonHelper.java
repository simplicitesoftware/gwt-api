/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.ui;

import com.google.gwt.core.client.GWT;

/**
 * <p>Common UI helper class</p>
 */
public abstract class CommonHelper {
	private boolean debug = false;
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	protected void debug(String message) {
		if (debug)
			GWT.log(message);
	}
}
