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

public class DocumentDefinition {
	private File sourceFile;
	private String target;
	private String label;
	private String text;
	
	public DocumentDefinition(File sourceFile, String target, String label, String text) {
		super();
		this.sourceFile = sourceFile;
		this.target = target;
		this.label = label;
		this.text = text;
	}

	public File getSourceFile() {
		return sourceFile;
	}
	
	public String getTarget() {
		return target;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getText(){
		return text;
	}
}
