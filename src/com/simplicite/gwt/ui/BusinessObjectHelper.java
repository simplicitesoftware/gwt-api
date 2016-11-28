/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.simplicite.gwt.core.*;

/**
 * <p>Business object UI helper class</p>
 */
public class BusinessObjectHelper extends CommonHelper {
	private AppSession app;
	private BusinessObject obj;

	/**
	 * <p>Constructor</p>
	 * @param app Application user session
	 * @param obj Business object
	 */
	public BusinessObjectHelper(final AppSession app, BusinessObject obj) {
		this.app = app;
		this.obj = obj;
		debug("Business object + " + this.obj.name + " instanciated for " + this.app.grant.login);

		searchButton.setText(app.texts.get("SEARCH"));
		resetButton.setText(app.texts.get("RESET"));
		createButton.setText(app.texts.get("CREATE"));
		updateButton.setText(app.texts.get("UPDATE"));
		delButton.setText(app.texts.get("DELETE"));

		firstButton.setText("<<");
		firstButton.setTitle(app.texts.get("FIRST"));
		prevButton.setText("<");
		prevButton.setTitle(app.texts.get("BACK"));
		nextButton.setText(">");
		nextButton.setTitle(app.texts.get("NEXT"));
		lastButton.setText(">>");
		lastButton.setTitle(app.texts.get("LAST"));
	}

	/**
	 * <p>Search form</p>
	 */
	public FormPanel searchForm = new FormPanel();
	
	/**
	 * <p>Search form items</p>
	 */
	public ArrayList<FieldHelper> searchFormItems = new ArrayList<FieldHelper>();

	/**
	 * <p>Search button (used on search form)</p>
	 */
	public Button searchButton = new Button();
	
	/**
	 * <p>Reset search button (used on search form)</p>
	 */
	public Button resetButton = new Button();
	
	/**
	 * <p>Dynamically (re)uilds search form based on current business object's meta data and filters</p>
	 * @param callback Callback
	 */
	public void buildSearchForm(final AsyncCallback<FormPanel> callback) {
		obj.getMetaData(BusinessObject.CONTEXT_SEARCH, null, new AsyncCallback<BusinessObject>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			
			@Override
			public void onSuccess(BusinessObject o) {
				searchForm.clear();
				searchFormItems.clear();
				VerticalPanel p = new VerticalPanel();
				for (int i = 0; i < obj.fieldsByOrder.size(); i++) {
					FieldHelper fh = new FieldHelper(app, obj.fieldsByOrder.get(i));
					if (fh.field.isSearchable()) {
						searchFormItems.add(fh);
						p.add(fh.getLabel(false));
						p.add(fh.buildSearchFormWidget(o.filters.get(fh.field.name)));
					}
				}
				HorizontalPanel bp = new HorizontalPanel();
				bp.addStyleName("s_toolbar");
				bp.add(searchButton);
				bp.add(new HTML("&nbsp;"));
				bp.add(resetButton);
				p.add(bp);
				searchForm.add(p);
				
				callback.onSuccess(searchForm);
			}
		});
	}
	
	/**
	 * <p>Sets all business object's filters using search form data</p>
	 */
	public void setFiltersFromSearchForm() {
		obj.filters.clear();
		for (Iterator<FieldHelper> i = searchFormItems.iterator(); i.hasNext();) {
			FieldHelper fh = i.next();
			obj.filters.put(fh.field.name, fh.getFilterFromSearchFormWidget());
		}
	}
	
	/**
	 * <p>Create form</p>
	 */
	public FormPanel createForm = new FormPanel();

	/**
	 * <p>Create form items</p>
	 */
	public ArrayList<FieldHelper> createFormItems = new ArrayList<FieldHelper>();

	public Button createButton = new Button();

	/**
	 * <p>Dynamically (re)uilds create form based on current business object's meta data</p>
	 */
	public void buildCreateForm(final AsyncCallback<Panel> callback) {
		buildEditForm(true, callback);
	}
	
	/**
	 * <p>Sets business object's current item using create form data</p>
	 */
	public void setItemFromCreateForm() {
		setItemFromEditForm(true);
	}
	
	/**
	 * <p>Update form</p>
	 */
	public FormPanel updateForm = new FormPanel();

	/**
	 * <p>Update form items</p>
	 */
	public ArrayList<FieldHelper> updateFormItems = new ArrayList<FieldHelper>();

	/**
	 * <p>Update button (used on update form)</p>
	 */
	public Button updateButton = new Button();
	
	/**
	 * <p>Delete button (used on update form)</p>
	 */
	public Button delButton = new Button();
	
	/**
	 * <p>Dynamically (re)uilds update form based on current business object's meta data</p>
	 */
	public void buildUpdateForm(final AsyncCallback<Panel> callback) {
		buildEditForm(false, callback);
	}

	/**
	 * <p>Sets business object's current item using create form data</p>
	 */
	public void setItemFromUpdateForm() {
		setItemFromEditForm(false);
	}
	
	private void buildEditForm(final boolean create, final AsyncCallback<Panel> callback) {
		obj.getMetaData(BusinessObject.CONTEXT_LIST, null, new AsyncCallback<BusinessObject>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			
			@Override
			public void onSuccess(BusinessObject o) {
				FormPanel editForm = create ? createForm : updateForm;
				ArrayList<FieldHelper> editFormItems = create ? createFormItems : updateFormItems;
		
				editForm.clear();
				editFormItems.clear();
				VerticalPanel p = new VerticalPanel();
				for (int i = 0; i < obj.fieldsByOrder.size(); i++) {
					FieldHelper fh = new FieldHelper(app, obj.fieldsByOrder.get(i));
					editFormItems.add(fh);
					if (fh.field.isFormVisible()) p.add(fh.getLabel(true));
					p.add(fh.buildEditFormWidget(o.item.get(fh.field.name), create));
				}
				HorizontalPanel bp = new HorizontalPanel();
				bp.addStyleName("s_toolbar");
				bp.add(create ? createButton : updateButton);
				if (!create) {
					bp.add(new HTML("&nbsp;"));
					bp.add(delButton);
				}
				p.add(bp);
				editForm.add(p);
				
				callback.onSuccess(editForm);
			}
		});
	}

	private void setItemFromEditForm(boolean create) {
		ArrayList<FieldHelper> editFormItems = create ? createFormItems : updateFormItems;

		obj.item.clear();
		for (Iterator<FieldHelper> i = editFormItems.iterator(); i.hasNext();) {
			FieldHelper fh = i.next();
			obj.item.put(fh.field.name, fh.getValueFromEditFormWidget());
		}
	}
	
	/**
	 * <p>List panel</p>
	 */
	public VerticalPanel listPanel = new VerticalPanel();
	
	/**
	 * <p>List table</p>
	 */
	public FlexTable listTable = new FlexTable();

	/**
	 * <p>First button (used on lists)</p>
	 */
	public Button firstButton = new Button();
	
	/**
	 * <p>Previous button (used on lists)</p>
	 */
	public Button prevButton = new Button();
	
	/**
	 * <p>Next button (used on lists)</p>
	 */
	public Button nextButton = new Button();
	
	/**
	 * <p>Last button (used on lists)</p>
	 */
	public Button lastButton = new Button();
	
	/**
	 * <p>Dynamically (re)uilds list table based on current business object's meta data</p>
	 * @param callback Callback
	 */
	public void buildList(final AsyncCallback<Panel> callback) {
		obj.getMetaData(BusinessObject.CONTEXT_LIST, null, new AsyncCallback<BusinessObject>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }
			
			@Override
			public void onSuccess(BusinessObject o) {
				listTable.removeAllRows();
				listTable.addStyleName("s_list");
				RowFormatter rf = listTable.getRowFormatter();
				CellFormatter cf = listTable.getCellFormatter();
				
				int col = 0;
				for (int i = 0; i < obj.fieldsByOrder.size(); i++) {
					Field f = obj.fieldsByOrder.get(i);
					if (f.isListVisible()) {
						HTML label = new HTML(f.label);
						label.addStyleName("s_list_head");
						listTable.setWidget(0, col, label);
						cf.addStyleName(0, col, "s_list_head");
						col++;
					}
				}
				rf.addStyleName(0, "s_list_head");
				int row = 1;
				for (int i = 0; i < obj.list.size(); i++) {
					HashMap<String, Object> item = obj.list.get(i);
					col = 0;
					for (int j = 0; j < obj.fieldsByOrder.size(); j++) {
						Field f = obj.fieldsByOrder.get(j);
						if (f.isListVisible()) {
							HTML value = new HTML(Field.stringValue(item.get(f.name)));
							value.addStyleName("s_list_value");
							listTable.setWidget(row, col, value);
							cf.addStyleName(row, col, "s_list_value");
							col++;
						}
					}
					rf.addStyleName(row, "s_list_value_" + (row % 2));
					row++;
				}
				
				listPanel.clear();
				
				HorizontalPanel bp = new HorizontalPanel();
				bp.addStyleName("s_toolbar");
				bp.add(new HTML(app.texts.get("TOTAL") + " " + obj.count));
				if (obj.maxpage > 0) {
					bp.add(new HTML("&nbsp;"));
					bp.add(firstButton);
					firstButton.setEnabled(obj.page > 1);

					bp.add(new HTML("&nbsp;"));
					bp.add(prevButton);
					prevButton.setEnabled(obj.page > 1);
					
					bp.add(new HTML("&nbsp;" + app.texts.get("PAGE") + " " + obj.page + " " + app.texts.get("PAGE_OF") + " " + obj.maxpage + "&nbsp;"));
					bp.add(nextButton);
					nextButton.setEnabled(obj.page < obj.maxpage);
					
					bp.add(new HTML("&nbsp;"));
					bp.add(lastButton);
					lastButton.setEnabled(obj.page < obj.maxpage);
				}
				listPanel.add(bp);
				listPanel.add(listTable);
				
				callback.onSuccess(listPanel);
			}
		});
	}
}