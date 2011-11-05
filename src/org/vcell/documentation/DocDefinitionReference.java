package org.vcell.documentation;

public class DocDefinitionReference extends DocTextComponent {
	private String defTarget;
	private String text;

	public DocDefinitionReference(String defTarget, String text) {
		super();
		this.defTarget = defTarget;
		this.text = text;
	}

	public String getDefinitionTarget() {
		return defTarget;
	}

	public String getText() {
		return text;
	}
	
	@Override
	public void add(DocTextComponent docComponent) {
		throw new RuntimeException("children not supported");
	}
	
	
}
