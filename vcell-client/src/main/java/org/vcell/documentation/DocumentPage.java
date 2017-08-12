/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.documentation;

import java.io.File;

public class DocumentPage {
	private File templateFile;
	private String title;
	private String target;
	private DocSection introduction;
	private DocSection appearance;
	private DocSection operations;
	
	public DocumentPage(File templateFile, String title,
			DocSection introduction, 
			DocSection appearance,
			DocSection operations) {
	
		this.templateFile = templateFile;
		this.title = title;
		this.introduction = introduction;
		this.appearance = appearance;
		this.operations = operations;
	}

	public File getTemplateFile() {
		return templateFile;
	}

	public String getTitle() {
		return title;
	}

	public String getTarget(){
		return templateFile.getName().replace(".xml","");
	}
	public DocSection getIntroduction() {
		return introduction;
	}

	public DocSection getAppearance() {
		return appearance;
	}

	public DocSection getOperations() {
		return operations;
	}
	
	public String toString(){
		return getTarget();
	}

}
