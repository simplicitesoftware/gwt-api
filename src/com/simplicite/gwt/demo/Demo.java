/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.demo;

import java.util.HashMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.simplicite.gwt.core.*;
import com.simplicite.gwt.ui.AppSessionHelper;
import com.simplicite.gwt.ui.BusinessObjectHelper;

/**
 * <p>Simple demo of Simplicit&eacute;&reg; GWT APIs</p>
 */
public class Demo implements EntryPoint {
	private boolean debug = true;
	private boolean displayNewsImages = true;
	
	private native String getLogin()/*-{
		return $wnd.LOGIN;
	}-*/;
	
	private native String getPassword()/*-{
		return $wnd.PASSWORD;
	}-*/;
	
	private native String getApplication()/*-{
		return $wnd.APPLICATION;
	}-*/;

	private native String getLogoutURL()/*-{
		return $wnd.LOGOUT_URL;
	}-*/;

	private void displayError(Throwable e) {
		StackTraceElement err = e.getStackTrace()[0];
		displayError("Class: " + err.getClassName() + "\nMethod: " + err.getMethodName() + "\nLine: " + err.getLineNumber() + "\nMessage: " + e.getMessage());
	}
	
	private void displayError(String err) {
		Window.alert(err);	
	}
	
	private void loading(Panel p) {
		p.clear();
		p.add(new Image("images/loading.gif"));
	}
	
	@Override
	public void onModuleLoad() {
		String login = getLogin();
		if (login == null)
			login = Location.getParameter("login");
		
		String password = getPassword();
		if (password == null)
			password = Location.getParameter("password");
		
		String application = getApplication();
		if (application == null)
			application = Location.getParameter("application");

		String baseURL = Location.getProtocol() + "//" + Location.getHost() + "/" + (application == null ? "" : (login != null && password != null ? application + "ws" : application));
		
		int width = 1000;
		int height = 800;
		int headHeight = 60;
		int menuWidth = 200;
		
		final HorizontalPanel headPanel = new HorizontalPanel();
		headPanel.addStyleName("s_head");
		headPanel.setWidth(width + "px");
		headPanel.setHeight(headHeight + "px");
		
		final HorizontalPanel headContentPanel = new HorizontalPanel();
		headContentPanel.setWidth("100%");
		headContentPanel.setHeight("100%");
		headContentPanel.add(new Image("images/logo.png"));
		headPanel.add(headContentPanel);
		
		final HorizontalPanel userPanel = new HorizontalPanel();
		userPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		headContentPanel.add(userPanel);
		
		final ScrollPanel workPanel = new ScrollPanel();
		workPanel.addStyleName("s_work");
		workPanel.setHeight((width - menuWidth) + "px");
		workPanel.setHeight((height - headHeight) + "px");

		final ScrollPanel menuPanel = new ScrollPanel();
		menuPanel.addStyleName("s_menu");
		menuPanel.setWidth(menuWidth + "px");
		
		DockPanel dockPanel = new DockPanel();
		dockPanel.add(headPanel, DockPanel.NORTH);
		dockPanel.setCellHeight(headPanel, headHeight + "px");
		dockPanel.add(menuPanel, DockPanel.WEST);
		dockPanel.setCellWidth(menuPanel, menuWidth + "px");
		dockPanel.add(workPanel, DockPanel.CENTER);
		dockPanel.setCellWidth(workPanel, (width - menuWidth) + "px");
		dockPanel.setCellWidth(workPanel, (height - headHeight) + "px");
		RootPanel.get(null).add(dockPanel);
		
		final AppSession app = new AppSession(login, password, baseURL);
		final AppSessionHelper apph = new AppSessionHelper(app);
		apph.setDebug(debug);
		
		app.getGrant(new AsyncCallback<Grant>() {
			@Override
			public void onFailure(Throwable e) { displayError(e); }

			@Override
			public void onSuccess(Grant g) {
				userPanel.clear();
				userPanel.add(new Image("images/" + g.lang + ".gif"));
				userPanel.add(new HTML("&nbsp;<b>" + g.firstname + " " + g.lastname + "</b>"));

				app.getTexts(new AsyncCallback<HashMap<String,String>>() {
					@Override
					public void onFailure(Throwable e) { displayError(e); }
					
					@Override
					public void onSuccess(HashMap<String, String> texts) {
						String u = getLogoutURL();
						if (u != null)
							userPanel.add(new HTML("&nbsp;<a href=\"" + u + "\">" + app.texts.get("QUIT") + "</a>"));

						loading(menuPanel);
						apph.buildMenuAsTree(new AsyncCallback<Tree>() {
							@Override
							public void onFailure(Throwable e) { displayError(e); }
		
							@Override
							public void onSuccess(Tree t) {
								menuPanel.clear();
								menuPanel.add(t);
							}
						});
					}
				});

				loading(workPanel);
				apph.buildNewsPanel(displayNewsImages, new AsyncCallback<VerticalPanel>() {
					@Override
					public void onFailure(Throwable e) { displayError(e); }

					@Override
					public void onSuccess(VerticalPanel p) {
						workPanel.clear();
						workPanel.add(p);
					}
				});
			}
		});
			
		apph.menuTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				TreeItem ti = event.getSelectedItem();
				MenuItem mi = (MenuItem)ti.getUserObject();
				if (mi == null) {
					// Nothing to do...
				} else if (mi.type.equals("object") || mi.type.equals("statusobject")) {
					String objName = mi.name;
					final BusinessObject o = app.getBusinessObject(objName, null);
					final BusinessObjectHelper oh = new BusinessObjectHelper(app, o);
					oh.setDebug(debug);
					
					loading(workPanel);
					oh.buildSearchForm(new AsyncCallback<FormPanel>() {
						@Override
						public void onFailure(Throwable e) { displayError(e); }

						@Override
						public void onSuccess(FormPanel f) {
							workPanel.clear();
							workPanel.add(f);
						}
					});
					
					oh.searchButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							oh.setFiltersFromSearchForm();
							
							loading(workPanel);
							o.search(1, BusinessObject.CONTEXT_LIST, false, false, new AsyncCallback<BusinessObject>() {
								@Override
								public void onFailure(Throwable e) { displayError(e); }
								
								@Override
								public void onSuccess(BusinessObject o) {
									oh.buildList(new AsyncCallback<Panel>() {
										@Override
										public void onFailure(Throwable e) { displayError(e); }
										
										@Override
										public void onSuccess(Panel l) {
											workPanel.clear();
											workPanel.add(l);
										}
									});
								}
							});
						}
					});
					
					oh.firstButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							loading(workPanel);
							o.search(1, BusinessObject.CONTEXT_LIST, false, false, new AsyncCallback<BusinessObject>() {
								@Override
								public void onFailure(Throwable e) { displayError(e); }
								
								@Override
								public void onSuccess(BusinessObject o) {
									oh.buildList(new AsyncCallback<Panel>() {
										@Override
										public void onFailure(Throwable e) { displayError(e); }
										
										@Override
										public void onSuccess(Panel l) {
											workPanel.clear();
											workPanel.add(l);
										}
									});
								}
							});
						}
					});
					
					oh.prevButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							loading(workPanel);
							o.search(Math.max(o.page - 1, 1), BusinessObject.CONTEXT_LIST, false, false, new AsyncCallback<BusinessObject>() {
								@Override
								public void onFailure(Throwable e) { displayError(e); }
								
								@Override
								public void onSuccess(BusinessObject o) {
									oh.buildList(new AsyncCallback<Panel>() {
										@Override
										public void onFailure(Throwable e) { displayError(e); }
										
										@Override
										public void onSuccess(Panel l) {
											workPanel.clear();
											workPanel.add(l);
										}
									});
								}
							});
						}
					});
					
					oh.nextButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							loading(workPanel);
							o.search(Math.min(o.page + 1, o.maxpage), BusinessObject.CONTEXT_LIST, false, false, new AsyncCallback<BusinessObject>() {
								@Override
								public void onFailure(Throwable e) { displayError(e); }
								
								@Override
								public void onSuccess(BusinessObject o) {
									oh.buildList(new AsyncCallback<Panel>() {
										@Override
										public void onFailure(Throwable e) { displayError(e); }
										
										@Override
										public void onSuccess(Panel l) {
											workPanel.clear();
											workPanel.add(l);
										}
									});
								}
							});
						}
					});
					
					oh.lastButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							loading(workPanel);
							o.search(o.maxpage, BusinessObject.CONTEXT_LIST, false, false, new AsyncCallback<BusinessObject>() {
								@Override
								public void onFailure(Throwable e) { displayError(e); }
								
								@Override
								public void onSuccess(BusinessObject o) {
									oh.buildList(new AsyncCallback<Panel>() {
										@Override
										public void onFailure(Throwable e) { displayError(e); }
										
										@Override
										public void onSuccess(Panel l) {
											workPanel.clear();
											workPanel.add(l);
										}
									});
								}
							});
						}
					});

					oh.resetButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							loading(workPanel);
							o.getFilters(true, BusinessObject.CONTEXT_SEARCH, new AsyncCallback<BusinessObject>() {
								@Override
								public void onFailure(Throwable e) { displayError(e); }
								
								@Override
								public void onSuccess(BusinessObject o) {
									oh.buildSearchForm(new AsyncCallback<FormPanel>() {
										@Override
										public void onFailure(Throwable e) { displayError(e); }

										@Override
										public void onSuccess(FormPanel f) {
											workPanel.clear();
											workPanel.add(f);
										}
									});
								}
							});
						}
					});

					oh.listTable.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							Cell cell = oh.listTable.getCellForEvent(event);
							int row = cell.getRowIndex();
							if (row > 0) {
								String rowId = Field.stringValue(o.list.get(row - 1).get(o.rowidfield));
								
								loading(workPanel);
								o.get(rowId, false, false, new AsyncCallback<BusinessObject>() {
									@Override
									public void onFailure(Throwable e) { displayError(e); }

									@Override
									public void onSuccess(BusinessObject o) {
										oh.buildUpdateForm(new AsyncCallback<Panel>() {
											@Override
											public void onFailure(Throwable e) { displayError(e); }
											
											@Override
											public void onSuccess(Panel f) {
												workPanel.clear();
												workPanel.add(f);
											}
										});
									}
								});
							}
						}
					});
					
					oh.createButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							oh.setItemFromCreateForm();
							loading(workPanel);
							o.create(new AsyncCallback<BusinessObject>() {
								@Override
								public void onFailure(Throwable e) { displayError(e); }

								@Override
								public void onSuccess(BusinessObject o) {
									oh.buildUpdateForm(new AsyncCallback<Panel>() {
										@Override
										public void onFailure(Throwable e) { displayError(e); }
										
										@Override
										public void onSuccess(Panel f) {
											workPanel.clear();
											workPanel.add(f);
										}
									});
								}
							});
						}
					});

					oh.updateButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							oh.setItemFromUpdateForm();
							loading(workPanel);
							o.update(new AsyncCallback<BusinessObject>() {
								@Override
								public void onFailure(Throwable e) { displayError(e); }

								@Override
								public void onSuccess(BusinessObject o) {
									oh.buildUpdateForm(new AsyncCallback<Panel>() {
										@Override
										public void onFailure(Throwable e) { displayError(e); }
										
										@Override
										public void onSuccess(Panel f) {
											workPanel.clear();
											workPanel.add(f);
										}
									});
								}
							});
						}
					});

					oh.delButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							if (Window.confirm(app.texts.get("CONFIRM_DEL"))) {
								loading(workPanel);
								o.del(new AsyncCallback<BusinessObject>() {
									@Override
									public void onFailure(Throwable e) { displayError(e); }
	
									@Override
									public void onSuccess(BusinessObject o) {
										oh.buildList(new AsyncCallback<Panel>() {
											@Override
											public void onFailure(Throwable e) { displayError(e); }
											
											@Override
											public void onSuccess(Panel l) {
												workPanel.clear();
												workPanel.add(l);
											}
										});
									}
								});
							}
						}
					});
				} else if (mi.type.equals("workflow") || mi.type.equals("process")) {
					workPanel.clear();
					workPanel.add(new HTML("<p><i>Not implemented yet...</i></p>"));
				}
			}
		});

		final Button grantButton = new Button("Grant");
		//workPanel.add(grantButton);

		grantButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				StringBuffer html = new StringBuffer(app.grant.firstname + " " + app.grant.lastname + ")");
				for (int i = 0; i < app.grant.responsibilities.size(); i++)
					html.append("<br/><i>" + app.grant.responsibilities.get(i) + "</i>");

				final DialogBox d = new DialogBox();
				d.setText(app.grant.login);
				d.setAnimationEnabled(true);
				final Button b = new Button("OK");
				VerticalPanel p = new VerticalPanel();
				p.add(new HTML(html.toString()));
				p.add(b);
				d.add(p);
				d.setPopupPosition(grantButton.getAbsoluteLeft(), grantButton.getAbsoluteTop());
				d.show();
				b.setFocus(true);

				b.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						d.hide();
					}
				});
			}
		});	
	}
}
