/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.core;

import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;

/**
 * <p>Business process</p>
 */
public class BusinessProcess extends Common {
	public BusinessProcess(String name, String login, String password, String baseURL) {
		super(login, password, baseURL);
		_serviceURI = _internal ? "jsp/PCS_json.jsp" : "jsonpcsservice.jsp";
		this.name = name;
		clear();
	}
	
	/**
	 * <p>Clears all meta data and current data</p>
	 */
	public void clear() {
		id = null;
		label = null;
	}
	
	/**
	 * <p>Logical business process name</p>
	 */
	public String name;

	/**
	 * <p>Unique business process ID</p>
	 */
	public String id;
	/**
	 * <p>Translated label</p>
	 */
	public String label;

	@Override
	protected BusinessProcess _parse(String json) throws JSONException {
		JSONObject res = (JSONObject)super._parse(json);
		String type = res.get("type").isString().stringValue();
		JSONObject obj = res.get("response").isObject();
		if (obj != null) {
			if (type.equals("metadata")) {
				// TODO : to be completed...
				
				return this;
			} else
				throw new JSONException("Unhandled " + type + " response type");
		} else 
			throw new JSONException("No response data");
	}
}