package org.vcell.documentation;

public class DocLink extends DocTextComponent {
	private String target;

	private String text;
	
	public DocLink(String target, String text) {
		super();
		this.target = target;
		this.text = text;
	}
	
	
	
	public String getTarget() {
		return target;
	}
	
	public String getText() {
		return text;
	}



	@Override
	public void add(DocTextComponent docComponent) {
		throw new RuntimeException("not supported");
	}
}
