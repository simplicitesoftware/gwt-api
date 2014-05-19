/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import com.simplicite.gwt.core.*;

/**
 * <p>Field UI helper class</p>
 */
public class FieldHelper extends CommonHelper {
	public Field field;

	private String YES;
	private String NO;
	
	/**
	 * <p>Constructor</p>
	 * @param app Application session
	 */
	public FieldHelper(final AppSession app, Field field) {
		this.field = field;
		
		YES = app.texts.get("YES");
		NO = app.texts.get("NO");
	}

	/**
	 * <p>Composite widget for booleans</p>
	 */
	public static class BooleanField extends Composite {
		private String name;
		private RadioButton yes;
		private RadioButton no;
		
		/**
		 * <p>Constructor</p>
		 * @param field Field definition
		 */
		public BooleanField(String yesLabel, String noLabel, Field field) {
			setName(field.name);
			yes = new RadioButton(getName(), yesLabel);
			no = new RadioButton(getName(), noLabel);
			setReadOnly(field.ref || !field.updatable);
			
			HorizontalPanel h = new HorizontalPanel();
			h.add(yes);
			h.add(no);
			
			initWidget(h);
		}
		
		/**
		 * <p>Sets name</p>
		 */
		public void setName(String name) {
			this.name = name;
		}
		
		/**
		 * <p>Gets name</p>
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * <p>Sets value</p>
		 * @param value
		 */
		public void setValue(Boolean value) {
			yes.setValue(value != null && value.booleanValue());
			no.setValue(value != null && !value.booleanValue());
		}
		
		/**
		 * <p>Gets value</p>
		 */
		public Boolean getValue() {
			boolean y = yes.getValue();
			boolean n = no.getValue();
			
			return y ? new Boolean(true) : n ? new Boolean(false) : null;
		}
		
		private void setReadOnly(boolean readOnly) {
			yes.setEnabled(!readOnly);
			no.setEnabled(!readOnly);
		}
	}
	
	/**
	 * <p>Returns formatted field label</p>
	 * @param decorate Add required/reference decoration
	 * @return HTML widget
	 */
	public HTML getLabel(boolean decorate) {
		HTML label = new HTML(field.label);
		label.addStyleName("s_label");
		if (decorate && field.required) label.addStyleName("s_req_label");
		if (decorate && field.ref) label.addStyleName("s_ref_label");
		return label;
	}
	
	private Widget searchFormWidget;

	/**
	 * <p>Builds an appropriate search form widget</p>
	 * @param flt Filter value
	 * @return Widget
	 */
	public Widget buildSearchFormWidget(String flt) {
		if (field.type == Field.TYPE_BOOLEAN) {
			BooleanField bf = new BooleanField(YES, NO, field);
			bf.setValue(flt == null || flt.length() == 0 ? null : Boolean.parseBoolean(flt));
			searchFormWidget = bf;
		} else if (field.type == Field.TYPE_DATE) {
			DateBox.Format fmt = new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd"));
			DateBox db = new DateBox();
			db.getTextBox().setName(field.name);
			db.setFormat(fmt);
			Date d = null;
			try { d = fmt.parse(db, flt, false); } catch (Exception e) {}
			if (d != null) db.setValue(d);
			searchFormWidget = db;
		} else if (field.type == Field.TYPE_DATETIME) {
			DateBox.Format fmt = new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss"));
			DateBox db = new DateBox();
			db.getTextBox().setName(field.name);
			db.setFormat(fmt);
			Date d = null;
			try { d = fmt.parse(db, flt, false); } catch (Exception e) {}
			if (d != null) db.setValue(d);
			searchFormWidget = db;
		} else if (field.type == Field.TYPE_ENUM) {
			ListBox lb = new ListBox();
			lb.setVisibleItemCount(1);
			lb.setName(field.name);
			lb.addItem("");
			lb.setSelectedIndex(0);
			int n = 1;
			for (Iterator<String> i = field.listOfValues.keySet().iterator(); i.hasNext();) {
				String code = i.next();
				lb.addItem(field.listOfValues.get(code), code);
				if (flt != null && flt.equals(code)) lb.setSelectedIndex(n);
				n++;
			}
			searchFormWidget = lb;
		} else if (field.type == Field.TYPE_ENUM_MULTI) {
			ListBox lb = new ListBox(true);
			lb.setVisibleItemCount(Math.min(field.listOfValues.size(), 5));
			lb.setName(field.name);
			int n = 0;
			ArrayList<String> flts = null;
			if (flt != null && flt.length() > 0) {
				flts = new ArrayList<String>();
				String[] vs = flt.split(";");
				for (int k = 0; k < vs.length; k++) flts.add(vs[k]);
			}
			for (Iterator<String> i = field.listOfValues.keySet().iterator(); i.hasNext();) {
				String code = i.next();
				lb.addItem(field.listOfValues.get(code), code);
				if (flts != null && flts.contains(code)) lb.setItemSelected(n, true);
				n++;
			}
			searchFormWidget = lb;
		} else {
			TextBox tb = new TextBox();
			tb.setName(field.name);
			tb.setValue(flt);
			searchFormWidget = tb;
		}
		// TODO : Generate other widgets depending on field type
		searchFormWidget.addStyleName("s_value");
		
		return searchFormWidget;
	}
	
	/**
	 * <p>Sets business object filter value from search form widget</p>
	 */
	public String getFilterFromSearchFormWidget() {
		if (searchFormWidget instanceof BooleanField) {
			BooleanField bf = (BooleanField)searchFormWidget;
			return Field.stringValue(bf.getValue());
		} else if (searchFormWidget instanceof DateBox) {
			TextBox tb = ((DateBox)searchFormWidget).getTextBox();
			return tb.getValue();
		} else if (searchFormWidget instanceof ListBox) {
			ListBox lb = (ListBox)searchFormWidget;
			if (lb.isMultipleSelect()) {
				String vals = null;
				for (int i = 0; i < lb.getItemCount(); i++) {
					if (lb.isItemSelected(i)) {
						if (vals == null)
							vals = lb.getValue(i);
						else
							vals += ";" + lb.getValue(i);
					}
				}
				return vals;
			} else {
				int n = lb.getSelectedIndex();
				return n < 0 ? null : lb.getValue(n);
			}
		} else if (searchFormWidget instanceof TextBox) {
			TextBox tb = (TextBox)searchFormWidget;
			return tb.getValue();
		} else
			return null;
		// TODO : handle other widgets
	}
	
	private Widget editFormWidget;

	/**
	 * <p>Builds edit form widget</p>
	 * @param val Value
	 * @return Widget
	 */
	public Widget buildEditFormWidget(Object val, boolean create) {
		if (field.refId || field.visible == Field.VIS_NOT) {
			Hidden h = new Hidden();
			h.setName(field.name);
			h.setValue(create ? field.defaultValue : Field.stringValue(val, ""));
			return h;
		} else {
			if (field.type == Field.TYPE_BOOLEAN) {
				BooleanField bf = new BooleanField(YES, NO, field);
				bf.setValue(create ? new Boolean(field.defaultValue) : (Boolean)val);
				editFormWidget = bf;
			} else if (field.type == Field.TYPE_LONG_STRING || field.type == Field.TYPE_NOTEPAD || field.type == Field.TYPE_HTML) {
				TextArea ta = new TextArea();
				ta.setName(field.name);
				if (field.ref || !field.updatable) ta.setReadOnly(true);
				ta.setValue(create ? field.defaultValue : Field.stringValue(val, ""));
				ta.setSize("400px", "3em");
				editFormWidget = ta;
			} else if (field.type == Field.TYPE_DATE) {
				DateBox.Format fmt = new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd"));
				DateBox db = new DateBox();
				db.getTextBox().setName(field.name);
				db.setFormat(fmt);
				Date d = null;
				try { d = fmt.parse(db, Field.stringValue(val, ""), false); } catch (Exception e) {}
				if (d != null) db.setValue(d);
				editFormWidget = db;
			} else if (field.type == Field.TYPE_DATETIME) {
				DateBox.Format fmt = new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss"));
				DateBox db = new DateBox();
				db.getTextBox().setName(field.name);
				db.setFormat(fmt);
				Date d = null;
				try { d = fmt.parse(db, Field.stringValue(val, ""), false); } catch (Exception e) {}
				if (d != null) db.setValue(d);
				editFormWidget = db;
			} else if (field.type == Field.TYPE_ENUM) {
				ListBox lb = new ListBox();
				lb.setVisibleItemCount(1);
				lb.setName(field.name);
				lb.addItem("");
				lb.setSelectedIndex(0);
				int n = 1;
				for (Iterator<String> i = field.listOfValues.keySet().iterator(); i.hasNext();) {
					String code = i.next();
					lb.addItem(field.listOfValues.get(code), code);
					if (val != null && val.equals(code)) lb.setSelectedIndex(n);
					n++;
				}
				editFormWidget = lb;
			} else if (field.type == Field.TYPE_ENUM_MULTI) {
				ListBox lb = new ListBox(true);
				lb.setVisibleItemCount(Math.min(field.listOfValues.size(), 5));
				lb.setName(field.name);
				int n = 0;
				ArrayList<String> vals = null;
				String v = (String)val;
				if (v != null && v.length() > 0) {
					vals = new ArrayList<String>();
					String[] vs = v.split(";");
					for (int k = 0; k < vs.length; k++) vals.add(vs[k]);
				}
				for (Iterator<String> i = field.listOfValues.keySet().iterator(); i.hasNext();) {
					String code = i.next();
					lb.addItem(field.listOfValues.get(code), code);
					if (vals != null && vals.contains(code)) lb.setItemSelected(n, true);
					n++;
				}
				editFormWidget = lb;
			} else {
				TextBox tb = new TextBox();
				tb.setName(field.name);
				if (field.ref || !field.updatable) tb.setReadOnly(true);
				tb.setValue(create ? field.defaultValue : Field.stringValue(val, ""));
				tb.setMaxLength(field.length);
				tb.setSize(Math.min(10 * field.length, 300) + "px", "1em");
				editFormWidget = tb;
			}
			// TODO : Generate other widgets depending on field type
			editFormWidget.addStyleName("s_value");
			
			return editFormWidget;
		}
	}

	/**
	 * <p>Sets business object's item value from create/update form widget</p>
	 */
	public Object getValueFromEditFormWidget() {
		if (editFormWidget instanceof Hidden) {
			Hidden h = (Hidden)editFormWidget;
			if (field.type == Field.TYPE_BOOLEAN)
				return new Boolean(h.getValue());
			else
				return h.getValue();
			// TODO : handle other raw types (numbers)
		} else if (editFormWidget instanceof BooleanField) {
			BooleanField bf = (BooleanField)editFormWidget;
			return bf.getValue();
		} else if (editFormWidget instanceof TextArea) {
			TextArea ta = (TextArea)editFormWidget;
			return ta.getValue();
		} else if (editFormWidget instanceof DateBox) {
			TextBox tb = ((DateBox)editFormWidget).getTextBox();
			return tb.getValue();
		} else if (editFormWidget instanceof ListBox) {
			ListBox lb = (ListBox)editFormWidget;
			if (lb.isMultipleSelect()) {
				String vals = null;
				for (int i = 0; i < lb.getItemCount(); i++) {
					if (lb.isItemSelected(i)) {
						if (vals == null)
							vals = lb.getValue(i);
						else
							vals += ";" + lb.getValue(i);
					}
				}
				return vals;
			} else {
				int n = lb.getSelectedIndex();
				return n < 0 ? null : lb.getValue(n);
			}
		} else if (editFormWidget instanceof TextBox) {
			TextBox tb = (TextBox)editFormWidget;
			return tb.getValue();
		} else 
			return null;
		// TODO : handle other widgets
	}	
}