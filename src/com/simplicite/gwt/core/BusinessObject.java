/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * <p>Business object</p>
 */
public class BusinessObject extends Common {
	/*
	 * Contexts (to be documented)
	 */
	public static final int  CONTEXT_NONE            = 0;
	public static final int  CONTEXT_SEARCH          = 1;
	public static final int  CONTEXT_LIST            = 2;
	public static final int  CONTEXT_CREATE          = 3;
	public static final int  CONTEXT_COPY            = 4;
	public static final int  CONTEXT_UPDATE          = 5;
	public static final int  CONTEXT_DELETE          = 6;
	public static final int  CONTEXT_GRAPH           = 7;
	public static final int  CONTEXT_CROSSTAB        = 8;
	public static final int  CONTEXT_PRINTTMPL       = 9;
	public static final int  CONTEXT_UPDATEALL       = 10;
	public static final int  CONTEXT_REFSELECT       = 11;
	public static final int  CONTEXT_DATAMAPSELECT   = 12;
    public static final int  CONTEXT_PREVALIDATE     = 13;
    public static final int  CONTEXT_POSTVALIDATE    = 14;
    public static final int  CONTEXT_STATETRANSITION = 15;
    public static final int  CONTEXT_EXPORT          = 16;
    public static final int  CONTEXT_IMPORT          = 17;
    public static final int  CONTEXT_ASSOCIATE       = 18;
    public static final int  CONTEXT_PANELLIST       = 19;
	
	/**
	 * <p>Constructor for internal usage with generic web UI</p>
	 * @param name Object logical name
	 * @param instance Object logical instance name
	 * @param baseURL Base URL of current application generic web UI (e.g. http://myserver:8080/myapp)
	 */
	public BusinessObject(String name, String instance, String baseURL) {
		this(name, instance, null, null, baseURL);
	}
	
	/**
	 * </p>Constructor for external usage outside of generic web UI</p>
	 * @param name Object logical name
	 * @param instance Object logical instance name
	 * @param login User login
	 * @param password user password
	 * @param baseURL Base URL of current application generic web services gataway (e.g. http://myserver:8080/myapp<u>ws</u>)
	 */
	public BusinessObject(String name, String instance, String login, String password, String baseURL) {
		super(login, password, baseURL);
		if (instance == null || instance.length() == 0) instance = "gwt_" + name;
		_serviceURI = (_internal ? "jsp/ALL_json.jsp" : "jsonservice.jsp") + "?object=" + name + "&inst=" + instance;
		this.name = name;
		this.instance = instance;
		clear();
	}

	/**
	 * <p>Clears all meta data and current data</p>
	 */
	public void clear() {
		id = null;
		label = null;
		help = null;

		readonly = false;
		create = false;
		update = false;
		del = false;
		copy = false;
		export = false;
		print = false;
		updateAll = false;

		template = "";
		areas = new ArrayList<FieldArea>();
		
		rowidfield = "";
		fields = new HashMap<String, Field>();
		
		actions = new ArrayList<Action>();

		crosstabs = new ArrayList<Crosstab>();

		item = new HashMap<String, Object>();
		
		filters = new HashMap<String, String>();

		count = 0;
		page = 0;
		maxpage = 0;
		list = new ArrayList<HashMap<String,Object>>();
	}
	
	/**
	 * <p>Logical business object name</p>
	 */
	public String name;
	/**
	 * <p>Logical business object instance name</p>
	 */
	public String instance;
	
	/**
	 * <p>Unique business object ID</p>
	 */
	public String id;
	/**
	 * <p>Translated label</p>
	 */
	public String label;
	/**
	 * <p>Translated contextual help</p>
	 */
	public String help;

	/**
	 * <p>Is object read only ?</p>
	 */
	public boolean readonly;
	/**
	 * <p>Can create ?</p>
	 */
	public boolean create;
	/**
	 * <p>Can update ?</p>
	 */
	public boolean update;
	/**
	 * <p>Can delete ?</p>
	 */
	public boolean del;
	/**
	 * <p>Can copy ?</p>
	 */
	public boolean copy;
	/**
	 * <p>Can export ?</p>
	 */
	public boolean export;
	/**
	 * <p>Can publish ?</p>
	 */
	public boolean print;
	/**
	 * <p>Can bulk update ?</p>
	 */
	public boolean updateAll;
	
	/**
	 * <p>Form template</p>
	 */
	public String template;
	/**
	 * <p>Form areas</p>
	 */
	public ArrayList<FieldArea> areas;
	
	/**
	 * <p>Name of the object row ID field</p>
	 */
	public String rowidfield;
	/**
	 * <p>Object fields map</p>
	 */
	public HashMap<String, Field> fields;
	/**
	 * <p>Object fields ordered array</p>
	 */
	public ArrayList<Field> fieldsByOrder;

	/**
	 * <p>Actions</p>
	 */
	public ArrayList<Action> actions;

	/**
	 * <p>Cross tables</p>
	 */
	public ArrayList<Crosstab> crosstabs;

	/**
	 * <p>Current item</p>
	 */
	public HashMap<String, Object> item;
	
	/**
	 * <p>Current filters</p>
	 */
	public HashMap<String, String> filters;
	
	/**
	 * <p>Current list count</p>
	 */
	public int count;
	/**
	 * <p>Current list page (if current list is resulting from a paginated search)</p>
	 */
	public int page;
	/**
	 * <p>Current list max page (if current list is resulting from a paginated search)</p>
	 */
	public int maxpage;
	/**
	 * <p>Current list (limited to current page items in case of a paginated search)</p>
	 */
	public ArrayList<HashMap<String, Object>> list;

	private Object _parseValue(JSONValue val) {
		if (val == null) return null;
		JSONString s = val.isString();
		if (s != null) return s.stringValue(); 
		JSONNumber n = val.isNumber();
		if (n != null) return new Double(n.doubleValue()); 
		JSONBoolean b = val.isBoolean();
		if (b != null) return new Boolean(b.booleanValue());
		Document d = _parseDocument(val);
		if (d != null) return d;
		ObjectItem o = _parseObjectItem(val);
		if (o != null) return o;
		return null;
	}
	
	private void _parseItem(HashMap<String, Object> item, JSONObject obj) {
		for (Iterator<String> i = fields.keySet().iterator(); i.hasNext();) {
			String name = i.next();
			JSONValue val = obj == null ? null : obj.get(name);
			item.put(name, _parseValue(val));
		}
	}
	
	@Override
	protected BusinessObject _parse(String json) throws JSONException {
		JSONObject res = (JSONObject)super._parse(json);
		String type = res.get("type").isString().stringValue();
		JSONValue response = res.get("response");
		if (response != null) {
			if (type.equals("objectMetaData")) {
				JSONObject obj = response.isObject();

				id = obj.get("id").isString().stringValue();
				label = obj.get("label").isString().stringValue();
				rowidfield = obj.get("rowidfield").isString().stringValue();
				help = obj.get("help").isString().stringValue();
				// TODO : to be completed...
				
				template = obj.get("template").isString().stringValue();
				
				JSONArray as = obj.get("areas").isArray();
				if (as != null) {
					areas.clear();
					for (int i = 0; i < as.size(); i++) {
						JSONObject a = as.get(i).isObject();
						if (a == null) throw new JSONException("Empty field area definition");
						FieldArea area = new FieldArea();
						area.area = (int)a.get("area").isNumber().doubleValue();
						area.label = a.get("label").isString().stringValue();
						areas.add(area);
					}
				}
				
				JSONArray fs = obj.get("fields").isArray();
				if (fs == null) throw new JSONException("No field definitions");
				fields.clear();
				for (int i = 0; i < fs.size(); i++) {
					JSONObject f = fs.get(i).isObject();
					if (f == null) throw new JSONException("Empty field definition");
					
					Field field = new Field();
					String name = f.get("name").isString().stringValue();
					field.name = name;
					// ZZZ Upward compatibility
					field.order = f.containsKey("order") ? (int)f.get("order").isNumber().doubleValue() : i;
					field.label = f.get("label").isString().stringValue();
					field.help = f.get("help").isString().stringValue();
					// ZZZ Upward compatibility
					field.refId = f.get(f.containsKey("refId") ? "refId" : "id").isBoolean().booleanValue();
					field.ref = f.get("ref").isBoolean().booleanValue();
					if (field.ref)
						field.refObject = f.get("refObject").isString().stringValue();
					field.key = f.get("key").isBoolean().booleanValue();
					field.required = f.get("required").isBoolean().booleanValue();
					field.updatable = f.get("updatable").isBoolean().booleanValue();
					field.visible = (int)f.get("visible").isNumber().doubleValue();
					field.searchable = (int)f.get("searchable").isNumber().doubleValue();
					// ZZZ Upward compatibility
					field.searchOrder = f.containsKey("searchOrder") ? (int)f.get("searchOrder").isNumber().doubleValue() : 0;
					field.rendering = f.get("rendering").isString().stringValue();
					field.extended = f.get("extended").isBoolean().booleanValue();
					field.type = (int)f.get("type").isNumber().doubleValue();
					field.length = (int)f.get("length").isNumber().doubleValue();
					field.precision = (int)f.get("precision").isNumber().doubleValue();
					field.area = (int)f.get("area").isNumber().doubleValue();
					field.defaultValue = Field.stringValue(_parseValue(f.get("defaultValue")));

					JSONValue l = f.get("listOfValues");
					if (l != null) {
						JSONArray lovs = l.isArray();
						if (lovs == null) throw new JSONException("Empty list of values");

						field.listOfValues = new HashMap<String, String>();
						for (int j = 0; lovs != null && j < lovs.size(); j++) {
							JSONObject lov = lovs.get(j).isObject();
							if (lov == null) throw new JSONException("Empty list of values item");
							field.listOfValues.put(lov.get("code").isString().stringValue(), lov.get("value").isString().stringValue());
						}
					}
					
					fields.put(name, field);
				}

				JSONArray acs = obj.get("actions").isArray();
				if (acs != null) {
					actions.clear();
					for (int i = 0; i < acs.size(); i++) {
						JSONObject ac = acs.get(i).isObject();
						if (ac == null) throw new JSONException("Empty action definition");
						Action action = new Action();
						action.name = ac.get("name").isString().stringValue();
						action.label = ac.get("label").isString().stringValue();
						action.confirm = ac.get("confirm").isBoolean().booleanValue();
						action.listVisible = ac.get("listVisible").isBoolean().booleanValue();
						action.listItemVisible = ac.get("listItemVisible").isBoolean().booleanValue();
						action.formVisible = ac.get("formVisible").isBoolean().booleanValue();
						actions.add(action);
					}
				}
				
				JSONArray cts = obj.get("crosstabs").isArray();
				if (cts != null) {
					crosstabs.clear();
					for (int i = 0; i < cts.size(); i++) {
						JSONObject ct = cts.get(i).isObject();
						if (ct == null) throw new JSONException("Empty crosstable definition");
						Crosstab crosstab = new Crosstab();
						crosstab.name = ct.get("name").isString().stringValue();
						crosstab.label = ct.get("label").isString().stringValue();
						// TODO : to be completed...
						crosstabs.add(crosstab);
					}
				}
				
				fieldsByOrder = new ArrayList<Field>();
				fieldsByOrder.addAll(fields.values());
				Collections.sort(fieldsByOrder);

				return this;
			} else if (type.equals("filters")) {
				JSONObject fs = response.isObject();
				filters.clear();
				for (Iterator<String> i = fs.keySet().iterator(); i.hasNext();) {
					String name = i.next();
					filters.put(name, fs.get(name).isString().stringValue());
				}
				return this;
			} else if (type.equals("search")) {
				JSONObject r = response.isObject();
				JSONNumber n = r.get("count").isNumber();
				count = n == null ? 0 : (int)n.doubleValue();
				n = r.get("page").isNumber();
				page = n == null ? 0 : (int)n.doubleValue();
				n = r.get("maxpage").isNumber();
				maxpage = n == null ? 0 : (int)n.doubleValue();
				JSONArray objs = r.get("list").isArray();
				list.clear();
				for (int i = 0; i < objs.size(); i++) {
					HashMap<String, Object> it = new HashMap<String, Object>();
					_parseItem(it, objs.get(i).isObject());
					list.add(it);
				}
				return this;
			} else if (type.equals("get") || type.equals("populate") || type.equals("create") || type.equals("update")) {
				_parseItem(item, response.isObject());
				return this;
			} else if (type.equals("delete")) {
				_parseItem(item, null);
				return this;
			} else
				throw new JSONException("Unhandled " + type + " response type");
		} else 
			throw new JSONException("No response data");
	}
	
	private String itemAsParam() {
		StringBuffer p = new StringBuffer();
		boolean first = true;
		for (Iterator<String> i = item.keySet().iterator(); i.hasNext();) {
			String name = i.next();
			String val = Field.stringValue(item.get(name));
			if (val != null) {
				p.append((first ? "" : "&") + name + "=" + URL.encode(val));
				first = false;
			}
		}
		return p.toString();
	}
	
	private String filtersAsParam() {
		StringBuffer p = new StringBuffer();
		boolean first = true;
		for (Iterator<String> i = filters.keySet().iterator(); i.hasNext();) {
			String name = i.next();
			String val = filters.get(name);
			if (val != null && val.length() > 0) {
				p.append((first ? "" : "&") + name + "=" + URL.encode(val));
				first = false;
			}
		}
		return p.toString();
	}
	
	/**
	 * <p>Retreives meta data for specified context</p>
	 * @param context Usage context (one of CONTEXT_* constant)
	 * @param contextParam contextParam Usage context parameter (only useful for CONTEXT_GRAPH, CONTEXT_CROSSTAB and CONTEXT_PRINTTMPL usage contexts for indicating the corresponding item)
	 * @param callback Callback
	 */
	public void getMetaData(int context, String contextParam, final AsyncCallback<BusinessObject> callback) {
		_get("&action=metadata&context=" + context + (contextParam != null && contextParam.length() != 0 ? "&contextparam=" + contextParam : ""), new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			@Override
			public void onSuccess(Object obj) { callback.onSuccess((BusinessObject)obj); }
		});
	}
	
	/**
	 * <p>Retreives current filters for specified context</p>
	 * @param context Usage context (one of CONTEXT_* constant, should normally be CONTEXT_SEARCH)
	 * @param reset Reset filters ?
	 * @param callback Callback
	 */
	public void getFilters(boolean reset, int context, final AsyncCallback<BusinessObject> callback) {
		_get("&action=filters&reset=" + reset + "&context=" + context, new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			@Override
			public void onSuccess(Object obj) { callback.onSuccess((BusinessObject)obj); }
		});
	}
	
	/**
	 * <p>Searches using current filters</p>
	 * @param page Page index (must be between 1 and maxpage, 0 means no pagination)
	 * @param context Usage context (one of CONTEXT_* constant, should normally be CONTEXT_LIST)
	 * @param inlineDocs Inline document data for document/image fields
	 * @param inlineThumbs Inline document thumbnails data for image fields
	 * @param callback Callback
	 */
	public void search(int page, int context, boolean inlineDocs, boolean inlineThumbs, final AsyncCallback<BusinessObject> callback) {
		String data = filtersAsParam();
		
		_post("&action=search" + (page > 0 ? "&page=" + page : "") + "&context=" + context + "&inline_documents=" + inlineDocs + "&inline_thumbnails=" + inlineThumbs, data, new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			@Override
			public void onSuccess(Object obj) { callback.onSuccess((BusinessObject)obj); }
		});
	}
	
	/**
	 * <p>Get current item using specified row ID</p>
	 * @param rowId Row ID
	 * @param inlineDocs Inline document data for document/image fields
	 * @param inlineThumbs Inline document thumbnails data for image fields
	 * @param callback Callback
	 */
	public void get(String rowId, boolean inlineDocs, boolean inlineThumbs, final AsyncCallback<BusinessObject> callback) {
		_get("&action=get&" + rowidfield + "=" + rowId + "&inline_documents=" + inlineDocs + "&inline_thumbnails=" + inlineThumbs, new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			@Override
			public void onSuccess(Object obj) { callback.onSuccess((BusinessObject)obj); }
		});
	}
	
	
	/**
	 * <p>Get current item using specified row ID</p>
	 * @param rowId Row ID (as long for upward compatibility)
	 * @param inlineDocs Inline document data for document/image fields
	 * @param inlineThumbs Inline document thumbnails data for image fields
	 * @param callback Callback
	 */
	public void get(long rowId, boolean inlineDocs, boolean inlineThumbs, final AsyncCallback<BusinessObject> callback) {
		get(String.valueOf(rowId), inlineDocs, inlineThumbs, callback);
	}
	
	/**
	 * <p>Populate item using current item data</p>
	 * @param callback Callback
	 */
	public void populate(final AsyncCallback<BusinessObject> callback) {
		String data = itemAsParam();

		_post("&action=populate", data, new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			@Override
			public void onSuccess(Object obj) { callback.onSuccess((BusinessObject)obj); }
		});
	}
	
	/**
	 * <p>Create item using current item data (generated row ID is set when the callback is called)</p>
	 * @param callback Callback
	 */
	public void create(final AsyncCallback<BusinessObject> callback) {
		item.put(rowidfield, Field.DEFAULT_ROW_ID);
		String data = itemAsParam();

		_post("&action=create", data, new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			@Override
			public void onSuccess(Object obj) { callback.onSuccess((BusinessObject)obj); }
		});
	}
	
	/**
	 * <p>Update current item using current item data</p>
	 * @param callback Callback
	 */
	public void update(final AsyncCallback<BusinessObject> callback) {
		String data = itemAsParam();

		_post("&action=update", data, new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			@Override
			public void onSuccess(Object obj) { callback.onSuccess((BusinessObject)obj); }
		});
	}
	
	/**
	 * <p>Delete current item</p>
	 * @param callback Callback
	 */
	public void del(final AsyncCallback<BusinessObject> callback) {
		String data = rowidfield + "=" + URL.encode((String)item.get(rowidfield));

		_post("&action=delete", data, new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			@Override
			public void onSuccess(Object obj) { item.clear(); callback.onSuccess((BusinessObject)obj); }
		});
	}
		
	/**
	 * <p>Execute action</p>
	 * @param action Action name
	 * @param callback Callback
	 */
	public void action(String action, final AsyncCallback<String> callback) {
		String data = itemAsParam();

		_post("&action=" + action, data, new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			@Override
			public void onSuccess(Object res) { item.clear(); callback.onSuccess((String)res); }
		});
	}
	
	/**
	 * <p>Is specified field the row ID field ?</p>
	 * @param field Field
	 */
	public boolean isRowIdField(Field field) {
		return !field.ref && field.name.equals(rowidfield);
	}
	
	/**
	 * <p>Is specified field one of the timestamp fields ?</p>
	 * @param field Field
	 */
	public boolean isTimestampField(Field field) {
		return !field.ref && (
				field.name.equals("created_dt")
			||	field.name.equals("created_by")
			||	field.name.equals("updated_dt")
			||	field.name.equals("updated_by")
			);
	}
}