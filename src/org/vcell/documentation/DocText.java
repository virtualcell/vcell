package org.vcell.documentation;

public class DocText extends DocTextComponent {
	private String text;
	private boolean bBold;
	
	public DocText(String text, boolean argBold){
		this.text = text;
		this.bBold = argBold;
	}

	public String getText() {
		return text;
	}
	
	public boolean getBold(){
		return bBold;
	}

	@Override
	public void add(DocTextComponent docComponent) {
		throw new RuntimeException("children not supported");	
	}
	
}
