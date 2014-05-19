/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.ui;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.simplicite.gwt.core.AppSession;
import com.simplicite.gwt.core.Document;
import com.simplicite.gwt.core.Menu;
import com.simplicite.gwt.core.News;

/**
 * <p>Application session UI helper class</p>
 */
public class AppSessionHelper extends CommonHelper {
	private AppSession app;
	
	/**
	 * <p>Constructor</p>
	 * @param app Application user session
	 */
	public AppSessionHelper(AppSession app) {
		this.app = app;
		debug("Application helper instanciated");
	}

	/**
	 * <p>Menu tree widget</p>
	 */
	public final Tree menuTree = new Tree();

	/**
	 * <p>Dynamically builds the menu as a tree widget</p>
	 */
	public void buildMenuAsTree(final AsyncCallback<Tree> callback) {
		app.getMenu(new AsyncCallback<Menu>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }

			@Override
			public void onSuccess(Menu m) {
				menuTree.clear();
				
				for (int i = 0; i < app.menu.domains.size(); i++) {
					menuTree.addItem("<b>" + app.menu.domains.get(i).label + "</b>");
					for (int j = 0; j < app.menu.domains.get(i).items.size(); j++) {
						TreeItem ti = new TreeItem();
						ti.setHTML(app.menu.domains.get(i).items.get(j).label);
						ti.setUserObject(app.menu.domains.get(i).items.get(j));
						menuTree.getItem(i).addItem(ti);
					}
				}
				
				callback.onSuccess(menuTree);
			}
		});
	}

	/**
	 * <p>News panel</p>
	 */
	public final VerticalPanel newsPanel = new VerticalPanel();

	/**
	 * <p>Dynamically builds the menu as a tree widget</p>
	 */
	public void buildNewsPanel(final boolean displayImages, final AsyncCallback<VerticalPanel> callback) {
		app.getNews(displayImages, new AsyncCallback<ArrayList<News>>() {
			@Override
			public void onFailure(Throwable e) { callback.onFailure(e); }

			@Override
			public void onSuccess(ArrayList<News> nl) {
				newsPanel.clear();
				
				for (int i = 0; i < app.news.size(); i++) {
					News n = app.news.get(i);
					if (displayImages && n.image != null && n.image.content != null)
						newsPanel.add(getImage(n.image));
					Label l = new Label(n.title);
					l.addStyleName("s_news_title");
					newsPanel.add(l);
					Label d = new Label(n.date);
					d.addStyleName("s_news_date");
					newsPanel.add(d);
					HTML c = new HTML(n.content + "<br/>&nbsp;");
					c.addStyleName("s_news_content");
					newsPanel.add(c);
				}
				
				callback.onSuccess(newsPanel);
			}
		});
	}
	
	public Image getImage(Document i) {
		Image img = null;
		if (i != null && i.content != null) {
			img = new Image();
			img.setUrl("data:" + i.mime + ";base64," + i.content);
			img.setAltText(i.name);
		}
		return img;
	}
}
