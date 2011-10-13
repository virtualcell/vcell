package org.vcell.documentation;

public class DocListItem extends DocTextComponent {

	@Override
	public void add(DocTextComponent docComponent) {
		if (docComponent instanceof DocText 
				|| docComponent instanceof DocLink
				|| docComponent instanceof DocImageReference
				|| docComponent instanceof DocParagraph){
			components.add(docComponent);
		}
	}
}
