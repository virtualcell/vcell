package org.vcell.documentation;

import java.io.File;

public class DocumentImage {
	private File sourceFilename;
	private String target;
	private int fileWidth;
	private int fileHeight;
	private int displayWidth;
	private int displayHeight;
	
	public DocumentImage(File sourceFilename, String target, int fileWidth, int fileHeight,
			int displayWidth, int displayHeight) {
		super();
		this.sourceFilename = sourceFilename;
		this.target = target;
		this.fileWidth = fileWidth;
		this.fileHeight = fileHeight;
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
	}

	public File getSourceFile() {
		return sourceFilename;
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
	
	public String getTarget(){
		return target;
	}
	
}
