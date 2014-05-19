/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.ui;

import com.simplicite.gwt.core.*;

/**
 * <p>Business process UI helper class</p>
 */
public class BusinessProcessHelper extends CommonHelper {
	private AppSession app;
	private BusinessProcess pcs;
	
	public BusinessProcessHelper(AppSession app, BusinessProcess pcs) {
		this.app = app;
		this.pcs = pcs;
		debug("Business process + " + this.pcs.name + " instanciated for " + this.app.grant.login);
	}
}