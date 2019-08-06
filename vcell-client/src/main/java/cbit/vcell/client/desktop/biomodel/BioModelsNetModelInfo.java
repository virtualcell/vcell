/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

public class BioModelsNetModelInfo {
	private String id;
	private String name;
	private String link;
	private boolean supported;
	
	public BioModelsNetModelInfo(String id, String name, String link) {
		this(id, name, link, true);
	}
	public BioModelsNetModelInfo(String id, String name, String link, boolean supported) {
		super();
		this.id = id;
		this.name = name;
		this.link = link;
		this.supported = supported;
	}
	public final String getId() {
		return id;
	}
	public final String getName() {
		return name;
	}
	public final String getLink() {
		return link;
	}
	public final boolean isSupported() {
		return supported;
	}
	
}
