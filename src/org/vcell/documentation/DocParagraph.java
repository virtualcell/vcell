package org.vcell.documentation;

public class DocParagraph extends DocTextComponent {

	@Override
	public void add(DocTextComponent docComponent) {
		if (docComponent instanceof DocText || docComponent instanceof DocImageReference || docComponent instanceof DocList || docComponent instanceof DocLink){
			components.add(docComponent);
		}else{
			throw new RuntimeException(docComponent.getClass().getName()+" not supported");
		}
	}

}
