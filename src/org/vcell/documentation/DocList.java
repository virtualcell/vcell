package org.vcell.documentation;

public class DocList extends DocTextComponent {

	@Override
	public void add(DocTextComponent docComponent) {
		if (docComponent instanceof DocText 
				|| docComponent instanceof DocLink
				|| docComponent instanceof DocListItem
				|| docComponent instanceof DocImageReference
				|| docComponent instanceof DocParagraph){
			components.add(docComponent);
		}else{
			throw new RuntimeException("not supported");
		}			
	}
}
