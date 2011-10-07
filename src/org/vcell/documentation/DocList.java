package org.vcell.documentation;

import java.util.ArrayList;

public class DocList extends DocTextComponent {

	@Override
	public void add(DocTextComponent docComponent) {
		if (docComponent instanceof DocListItem){
			components.add((DocListItem) docComponent);
		}else{
			throw new RuntimeException("not supported");
		}
	}

}
