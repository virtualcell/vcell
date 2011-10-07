package org.vcell.documentation;

import java.util.ArrayList;

public class DocListItem extends DocTextComponent {

	@Override
	public void add(DocTextComponent docComponent) {
		if (docComponent instanceof DocText || docComponent instanceof DocLink){
			components.add(docComponent);
		}
	}
}
