package org.vcell.documentation;

import java.util.ArrayList;

public class DocParagraph extends DocTextComponent {

	@Override
	public void add(DocTextComponent docComponent) {
		if (docComponent instanceof DocText || docComponent instanceof DocImageReference || docComponent instanceof DocList){
			components.add(docComponent);
		}else{
			throw new RuntimeException(docComponent.getClass().getName()+" not supported");
		}
	}

}
