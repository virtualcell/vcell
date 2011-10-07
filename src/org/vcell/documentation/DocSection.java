package org.vcell.documentation;

import java.util.ArrayList;

public class DocSection extends DocTextComponent {

	@Override
	public void add(DocTextComponent docComponent) {
		if (docComponent instanceof DocImageReference || docComponent instanceof DocLink || docComponent instanceof DocList || docComponent instanceof DocParagraph || docComponent instanceof DocText){
			components.add(docComponent);
		}else{
			throw new RuntimeException("not supported");
		}
	}
	
}
