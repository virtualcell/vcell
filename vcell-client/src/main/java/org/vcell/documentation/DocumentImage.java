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

public class DocumentImage {
	private File sourceFile;
	private String target;
	private int fileWidth;
	private int fileHeight;
	private int displayWidth;
	private int displayHeight;
	
	public DocumentImage(File sourceFile, int fileWidth, int fileHeight,
			int displayWidth, int displayHeight) {
		super();
		this.sourceFile = sourceFile;
		this.target = target;
		this.fileWidth = fileWidth;
		this.fileHeight = fileHeight;
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
	}

	public File getSourceFile() {
		return sourceFile;
	}
	
	public int getFileWidth() {
		return fileWidth;
	}

	public int getFileHeight() {
		return fileHeight;
	}

	public int getDisplayWidth() {
		return displayWidth;
	}

	public int getDisplayHeight() {
		return displayHeight;
	}
		
}
