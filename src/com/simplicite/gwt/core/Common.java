/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.core;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * <p>Common abstract class from which all services classes inherit</p>
 */
public abstract class Common {
	protected boolean _internal = false;
	
	/**
	 * <p>User login (null for internal usage within generic web UI)</p>
	 */
	protected String _login;
	/**
	 * <p>User password (null for internal usage within generic web UI)</p>
	 */
	protected String _password;
	
	/**
	 * <p>Base URL of JSON services</p>
	 */
	protected String _baseURL;
	
	/**
	 * <p>Service URI (to be specialised in each sub class)</p>
	 */
	protected String _serviceURI;

	/**
	 * <p>Constructor for internal usage with generic web UI</p>
	 * @param baseURL Base URL of current application generic web UI (e.g. http://myserver:8080/myapp)
	 */
	public Common(String baseURL) {
		this(null, null, baseURL);
	}

	/**
	 * <p>Constructor for external usage outside of generic web UI</p>
	 * @param login User login
	 * @param password user password
	 * @param baseURL Base URL of current application generic web services gataway (e.g. http://myserver:8080/myapp<u>ws</u>)
	 */
	public Common(String login, String password, String baseURL) {
		_login = login == null ? null : login.trim();
		_password = _login == null || password == null ? null : password.trim();
		_internal = _login == null && _password == null;

		_baseURL = baseURL.trim();
	}

	/**
	 * <p>Response parser (to be extended in each sub class)</p>
	 * @param json Incoming JSON message
	 * @return Parsed message as simple Java Object
	 * @throws JSONException
	 */
	protected Object _parse(String json) throws JSONException {
		JSONObject res = JSONParser.parseStrict(json).isObject();
		String type = res.get("type").isString().stringValue();
		if (type.equals("error")) {
			String msg = "";
			JSONString message = res.containsKey("message") ? res.get("message").isString() : null;
			if (message != null) {
				msg = message.stringValue();
			} else {
				JSONArray messages = res.containsKey("messages") ? res.get("messages").isArray() : null;
				for (int i = 0; messages != null && i < messages.size(); i++) {
					msg += (i == 0 ? "" : "\n") + messages.get(i).isString().stringValue();
				}
			}
			GWT.log(msg);
			throw new JSONException(msg);
		}
		return res;
	}

	/**
	 * <p>Parse JSONValue as Document</p>
	 * @param v JSONValue data
	 * @return Parsed document as Document
	 * @throws JSONException
	 */
	protected static Document _parseDocument(JSONValue v)  throws JSONException {
		Document d = null;
		if (v.isString() != null) {
			d = new Document();
			d.id = v.isString().stringValue();
		} else {
			JSONObject doc = v.isObject();
			if (v != null) {
				d = new Document();
				d.id = doc.get("id").isString().stringValue();
				d.name = doc.get("name").isString().stringValue();
				d.path = doc.get("path").isString().stringValue();
				d.content = null;
				try { d.content = doc.get("content").isString().stringValue(); } catch (Exception e) {}
				d.mime = doc.get("mime").isString().stringValue();
				d.size = (int)doc.get("size").isNumber().doubleValue();
				try {
					d.object = doc.get("object").isString().stringValue();
					d.field = doc.get("field").isString().stringValue();
					d.rowid = doc.get("rowid").isString().stringValue();
				} catch (Exception e) {}
			}
		}
		return d;
	}
	

	/**
	 * <p>Parse JSONValue as ObjectItem</p>
	 * @param v JSONValue data
	 * @return Parsed value as ObjectItem
	 * @throws JSONException
	 */
	protected static ObjectItem _parseObjectItem(JSONValue v)  throws JSONException {
		ObjectItem o = null;
		if (v.isString() != null) {
			String[] vs = v.isString().stringValue().split(":");
			if (vs.length == 2) {
				o = new ObjectItem();
				o.object = vs[0];
				o.rowid = vs[1];
			}
		} else {
			JSONObject oi = v.isObject();
			if (v != null) {
				o = new ObjectItem();
				o.object = oi.get("object").isString().stringValue();
				JSONString s = oi.get("row_id").isString();
				if (s == null) {
					s = oi.get("id").isString();
					if (s != null) s.stringValue();
				} else
					s.stringValue();
			}
		}
		return o;
	}
	/**
	 * <p>Service call (using GET HTTP method)</p>
	 * @param serviceParams Service parameters (syntax: a=b&c=d)
	 * @param callback Callback
	 */
	protected void _get(String serviceParams, final AsyncCallback<Object> callback) {
		_call(RequestBuilder.GET, serviceParams, null, callback);
	}
	
	/**
	 * <p>Service call (using POST HTTP method)</p>
	 * @param serviceParams Service parameters (syntax: a=b&c=d)
	 * @param callback Callback
	 */
	protected void _post(String serviceParams, String data, final AsyncCallback<Object> callback) {
		_call(RequestBuilder.POST, serviceParams, data, callback);
	}
	
	/**
	 * <p>Service call</p>
	 * @param method HTTP method (one of RequestBuilder.GET, POST, ...)
	 * @param serviceParams Service parameters (appended to base URL + service URI)
	 * @param data Data (only for POST method)
	 * @param callback Asynchronous callback
	 */
	protected void _call(Method method, String serviceParams, String data, final AsyncCallback<Object> callback) {
		RequestBuilder builder = new RequestBuilder(method, URL.encode(_baseURL + "/" + _serviceURI + serviceParams));
		if (!_internal) {
			builder.setUser(_login);
			builder.setPassword(_password);
		}

		try {
			if (method.equals(RequestBuilder.POST)) builder.setHeader("Content-type", "application/x-www-form-urlencoded");
			
			builder.sendRequest(method.equals(RequestBuilder.POST) ? data : null, new RequestCallback() {
				@Override
				public void onError(Request request, Throwable e) {
					callback.onFailure(e);
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (response.getStatusCode() == 200) {
						try {
							Object obj = _parse(response.getText());
							if (obj != null) {
								callback.onSuccess(obj);
							}
						} catch (Exception e) {
							callback.onFailure(e);
						}
					} else {
						callback.onFailure(new RequestException("HTTP status " + response.getStatusCode()));
					}
				}
			});
		} catch (RequestException e) {
			callback.onFailure(e);
		}
	}
}