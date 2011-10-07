package org.vcell.documentation;

import java.util.ArrayList;

public class DocumentPage {
	private String templateFilename;
	private String templateFileDir;
	private String title;
	private String target;
	private ArrayList<DocumentCompiler.Section> introduction;
	private ArrayList<DocumentCompiler.Section> appearance;
	private ArrayList<DocumentCompiler.Section> operations;
	private ArrayList<DocumentCompiler.Section> properties;
	
	public DocumentPage(String templateFilename, String templateFileDir, String title, String target,
			ArrayList<DocumentCompiler.Section> introduction, 
			ArrayList<DocumentCompiler.Section> appearance,
			ArrayList<DocumentCompiler.Section> operations,
			ArrayList<DocumentCompiler.Section> properties) {
	
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

	public ArrayList<DocumentCompiler.Section> getIntroduction() {
		return introduction;
	}

	public ArrayList<DocumentCompiler.Section> getAppearance() {
		return appearance;
	}

	public ArrayList<DocumentCompiler.Section> getOperations() {
		return operations;
	}

	public ArrayList<DocumentCompiler.Section> getProperties() {
		return properties;
	}
}
