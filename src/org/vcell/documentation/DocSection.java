package org.vcell.documentation;

public class DocSection extends DocTextComponent {

	@Override
	public void add(DocTextComponent docComponent) {
		if (docComponent instanceof DocImageReference || docComponent instanceof DocLink || docComponent instanceof DocList 
			|| docComponent instanceof DocParagraph || docComponent instanceof DocText || docComponent instanceof DocDefinitionReference){
			components.add(docComponent);
		}else{
			throw new RuntimeException("not supported");
		}
	}
	
}
