/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.core;

//import java.text.ParseException;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * <p>Application user session data</p>
 */
public class AppSession extends Common {
	/**
	 * <p>Constructor for internal usage with generic web UI</p>
	 * @param baseURL Base URL of current application generic web UI (e.g. http://myserver:8080/myapp)
	 */
	public AppSession(String baseURL) {
		this(null, null, baseURL);
	}
	
	/**
	 * </p>Constructor for external usage outside of generic web UI</p>
	 * @param login User login
	 * @param password user password
	 * @param baseURL Base URL of current application generic web services gateway (e.g. http://myserver:8080/myapp<u>ws</u>)
	 */
	public AppSession(String login, String password, String baseURL) {
		super(login, password, baseURL);
		_serviceURI = _internal ? "jsp/SYS_json.jsp" : "jsonappservice.jsp";
		clear();
	}

	/**
	 * <p>Clears all session data</p>
	 */
	public void clear() {
		session = null;
		grant = null;
		menu = null;
		texts = null;
		sysparams = null;
		news = null;
		
		businessObjects = new HashMap<String, BusinessObject>();
		businessProcesses = new HashMap<String, BusinessProcess>();
	}
	
	/**
	 * <p>Session ID</p>
	 */
	public String session;

	/**
	 * <p>User data</p>
	 */
	public Grant grant;
	
	/**
	 * <p>Menu</p>
	 */
	public Menu menu;

	/**
	 * <p>System parameters</p>
	 */
	public HashMap<String, String> sysparams;

	/**
	 * <p>Texts</p>
	 */
	public HashMap<String, String> texts;

	/**
	 * <p>List of values</p>
	 */
	//public HashMap<String, HashMap<String, String>> listOfValues;

	/**
	 * <p>News</p>
	 */
	public ArrayList<News> news;
	
	@Override
	protected Object _parse(String json) throws JSONException {
		JSONObject msg = (JSONObject)super._parse(json);
		String type = msg.get("type").isString().stringValue();
		JSONValue res = msg.get("response");
		if (res != null) {
			if (type.equals("session")) {
				session = res.isObject().get("id").isString().stringValue();
				return session;
			} else if (type.equals("grant")) {
				JSONObject g = res.isObject();
				if (g == null) throw new JSONException("Empty grant data");
				
				session = g.get("sessionid").isString().stringValue();
				
				grant = new Grant();
				
				grant.userid    = new Long((long)g.get("userid").isNumber().doubleValue()).toString();
				grant.login     = g.get("login").isString().stringValue();
				grant.lang      = g.get("lang").isString().stringValue();
				grant.firstname = g.get("firstname").isString().stringValue();
				grant.lastname  = g.get("lastname").isString().stringValue();
				grant.email     = g.get("email").isString().stringValue();
	
				grant.responsibilities.clear();
				JSONArray rs = g.get("responsibilities").isArray();
				for (int i = 0; i < rs.size(); i++) {
					grant.responsibilities.add(rs.get(i).isString().stringValue());
				}
				
				return grant;
			} else if (type.equals("menu")) {
				JSONArray ds = res.isArray();
				if (ds == null) throw new JSONException("Empty menu");

				menu = new Menu();
				for (int i = 0; i < ds.size(); i++) {
					JSONObject d = ds.get(i).isObject();
					if (d == null) throw new JSONException("Empty menu domain");

					MenuDomain domain = new MenuDomain();
					domain.name = d.get("name").isString().stringValue();
					domain.label = d.get("label").isString().stringValue();
					
					JSONArray its = d.get("items").isArray();
					if (its == null) throw new JSONException("Empty menu domain items");

					for (int j = 0; j < its.size(); j++) {
						JSONObject it = its.get(j).isObject();
						if (it == null) throw new JSONException("Empty menu item");

						MenuItem menuitem = new MenuItem();
						menuitem.type = it.get("type").isString().stringValue();
						menuitem.name = it.get("name").isString().stringValue();
						menuitem.label = it.get("label").isString().stringValue();
						
						domain.items.add(menuitem);
					}

					menu.domains.add(domain);
				}
				
				return menu;
			} else if (type.equals("sysparams")) {
				JSONArray sps = res.isArray();
				if (sps == null) throw new JSONException("Empty system parameters");

				sysparams = new HashMap<String, String>();
				for (int i = 0; i < sps.size(); i++) {
					JSONObject sp = sps.get(i).isObject();
					if (sp == null) throw new JSONException("Empty system parameter");
					sysparams.put(sp.get("name").isString().stringValue(), sp.get("value").isString().stringValue());
				}

				return sysparams;
			} else if (type.equals("sysparam")) {
				if (sysparams == null) sysparams = new HashMap<String, String>();

				JSONObject sp = res.isObject();
				if (sp == null) throw new JSONException("Empty system parameter");
				String val = sp.get("value").isString().stringValue();
				sysparams.put(sp.get("name").isString().stringValue(), val);

				return val;
			} else if (type.equals("sysparamdb")) {
				// TODO : to be completed...
				return null;
			} else if (type.equals("listofvalue")) {
				// TODO : to be completed...
				return null;
			} else if (type.equals("texts")) {
				JSONArray ts = res.isArray();
				if (ts == null) throw new JSONException("Empty texts");

				texts = new HashMap<String, String>();
				for (int i = 0; i < ts.size(); i++) {
					JSONObject t = ts.get(i).isObject();
					if (t == null) throw new JSONException("Empty text");
					texts.put(t.get("code").isString().stringValue(), t.get("value").isString().stringValue());
				}

				return texts;
			} else if (type.equals("text")) {
				if (texts == null) texts = new HashMap<String, String>();
				
				JSONObject t = res.isObject();
				if (t == null) throw new JSONException("Empty text");
				String val = t.get("value").isString().stringValue();
				texts.put(t.get("code").isString().stringValue(), val);

				return val;
			} else if (type.equals("news")) {
				JSONArray ns = res.isArray();

				news = new ArrayList<News>();
				for (int i = 0; ns != null && i < ns.size(); i++) {
					JSONObject n = ns.get(i).isObject();
					if (n == null) throw new JSONException("Empty news");
					News nw = new News();
					//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					//nw.date = null;
					//try { nw.date = df.parse(n.get("date").isString().stringValue()); } catch (ParseException e) {}
					nw.date = n.get("date").isString().stringValue();
					//nw.expdate = null;
					//try { nw.expdate = df.parse(n.get("expdate").isString().stringValue()); } catch (ParseException e) {}
					nw.expdate = n.get("expdate").isString().stringValue();
					nw.title = n.get("title").isString().stringValue();
					nw.image = _parseDocument(n.get("image"));
					nw.content = n.get("content").isString().stringValue();
					news.add(nw);
				}

				return news;
			} else
				throw new JSONException("Unhandled " + type + " response type");
		} else 
			throw new JSONException("No response data");
	}
	
	/**
	 * <p>Gets user session ID</p>
	 * @param callback Callback
	 */
	public void getSession(final AsyncCallback<String> callback) {
		if (session != null) {
			callback.onSuccess(session);
			return;
		}
		_get("?data=session", new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			@Override
			public void onSuccess(Object session) { callback.onSuccess((String)session); }
		});
	}

	/**
	 * <p>Gets user grant data</p>
	 * @param callback Callback
	 */
	public void getGrant(final AsyncCallback<Grant> callback) {
		if (grant != null) {
			callback.onSuccess(grant);
			return;
		}
		_get("?data=grant", new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			@Override
			public void onSuccess(Object grant) { callback.onSuccess((Grant)grant); }
		});
	}
	
	/**
	 * <p>Gets user menu</p>
	 * @param callback Callback
	 */
	public void getMenu(final AsyncCallback<Menu> callback) {
		if (menu != null) {
			callback.onSuccess(menu);
			return;
		}
		_get("?data=menu", new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			@Override
			public void onSuccess(Object menu) { callback.onSuccess((Menu)menu); }
		});
	}
	
	/**
	 * <p>Gets translated texts in user's language</p>
	 * @param callback Callback
	 */
	public void getTexts(final AsyncCallback<HashMap<String, String>> callback) {
		if (texts != null) {
			callback.onSuccess(texts);
			return;
		}
		_get("?data=texts", new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(Object texts) { callback.onSuccess((HashMap<String, String>)texts); }
		});
	}
	
	/**
	 * <p>Gets translated texts in user's language</p>
	 * @param callback Callback
	 */
	public void getSysParams(final AsyncCallback<HashMap<String, String>> callback) {
		if (sysparams != null) {
			callback.onSuccess(sysparams);
			return;
		}
		_get("?data=sysparams", new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(Object sysparams) { callback.onSuccess((HashMap<String, String>)sysparams); }
		});
	}
	
	// TODO : to be completed...
	
	/**
	 * <p>Gets news</p>
	 * @param callback Callback
	 */
	public void getNews(boolean inlineImages, final AsyncCallback<ArrayList<News>> callback) {
		if (news != null) {
			callback.onSuccess(news);
			return;
		}
		_get("?data=news" + (inlineImages ? "&inline_images=true" : ""), new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(Object news) { callback.onSuccess((ArrayList<News>)news); }
		});
	}
	
	private HashMap<String, BusinessObject> businessObjects;
	
	/**
	 * <p>Gets a business object instance for current user session</p>
	 * @param name Business object logical name
	 * @param instance Business object logical instance name
	 */
	public BusinessObject getBusinessObject(String name, String instance) {
		BusinessObject obj = businessObjects.get(name + ":" + instance);
		if (obj == null) {
			obj = new BusinessObject(name, instance, _login, _password, _baseURL);
			businessObjects.put(name + ":" + instance, obj);
		}
		return obj;
	}

	private HashMap<String, BusinessProcess> businessProcesses;
	
	/**
	 * <p>Gets a business process for current user session</p>
	 * @param name Business process logical name
	 */
	public BusinessProcess getBusinessProcess(String name) {
		BusinessProcess pcs = businessProcesses.get(name);
		if (pcs == null) {
			pcs = new BusinessProcess(name, _login, _password, _baseURL);
			businessProcesses.put(name, pcs);
		}
		return pcs;
	}
}