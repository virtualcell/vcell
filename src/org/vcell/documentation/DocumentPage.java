package org.vcell.documentation;

public class DocumentPage {
	private String templateFilename;
	private String templateFileDir;
	private String title;
	private String target;
	private DocSection introduction;
	private DocSection appearance;
	private DocSection operations;
	
	public DocumentPage(String templateFilename, String templateFileDir, String title, String target,
			DocSection introduction, 
			DocSection appearance,
			DocSection operations) {
	
		this.templateFilename = templateFilename;
		this.templateFileDir = templateFileDir;
		this.title = title;
		this.target = target;
		this.introduction = introduction;
		this.appearance = appearance;
		this.operations = operations;
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

	public DocSection getIntroduction() {
		return introduction;
	}

	public DocSection getAppearance() {
		return appearance;
	}

	public DocSection getOperations() {
		return operations;
	}

}
