package org.vcell.documentation;

public class DocImageReference extends DocTextComponent {
	private String imageTarget;

	public DocImageReference(String imageTarget) {
		super();
		this.imageTarget = imageTarget;
	}

	public String getImageTarget() {
		return imageTarget;
	}

	@Override
	public void add(DocTextComponent docComponent) {
		throw new RuntimeException("children not supported");
	}
	
	
}
