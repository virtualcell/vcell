package org.vcell.documentation;

import java.util.ArrayList;

public class DocumentPage {
	private String templateFilename;
	private String templateFileDir;
	private String title;
	private String target;
	private ArrayList<DocTextComponent> introduction;
	private ArrayList<DocTextComponent> appearance;
	private ArrayList<DocTextComponent> operations;
	private ArrayList<DocTextComponent> properties;
	
	public DocumentPage(String templateFilename, String templateFileDir, String title, String target,
			ArrayList<DocTextComponent> introduction, 
			ArrayList<DocTextComponent> appearance,
			ArrayList<DocTextComponent> operations,
			ArrayList<DocTextComponent> properties) {
	
		this.templateFilename = templateFilename;
		this.templateFileDir = templateFileDir;
		this.title = title;
		this.target = target;
		this.introduction = introduction;
		this.appearance = appearance;
		this.operations = operations;
		this.properties = properties;
	}

	public String getTemplateFilename() {
		return templateFilename;
	}

	public String getTemplateFileDir() {
		return templateFileDir;
	}
	
	public String getTitle() {
		return title;
	}

	public String getTarget() {
		return target;
	}

	public ArrayList<DocTextComponent> getIntroduction() {
		return introduction;
	}

	public ArrayList<DocTextComponent> getAppearance() {
		return appearance;
	}

	public ArrayList<DocTextComponent> getOperations() {
		return operations;
	}

	public ArrayList<DocTextComponent> getProperties() {
		return properties;
	}
}
