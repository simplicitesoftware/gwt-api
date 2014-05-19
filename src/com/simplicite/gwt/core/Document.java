/*
 * Simplicite(R) for Google WebToolkit(R)
 * http://www.simplicite.fr
 */
package com.simplicite.gwt.core;

/**
 * <p>Document</p>
 */
public class Document {
	/**
	 * <p>Document ID</p>
	 */
	public String id;
	/**
	 * <p>Name</p>
	 */
	public String name;
	/**
	 * <p>Relative path</p>
	 */
	public String path;
	/**
	 * <p>MIME type</p>
	 */
	public String mime;
	/**
	 * <p>Size</p>
	 */
	public int size;
	/**
	 * <p>Content (base64 encoded)</p>
	 */
	public String content;

	/**
	 * <p>Object name (only if the document is an obejct field value)</p>
	 */
	public String object;
	/**
	 * <p>Object row ID (only if the document is an obejct field value)</p>
	 */
	public String rowid;
	/**
	 * <p>Field name (only if the document is an obejct field value)</p>
	 */
	public String field;
}