package org.vcell.documentation;

import java.util.ArrayList;

public abstract class DocTextComponent {
	ArrayList<DocTextComponent> components = new ArrayList<DocTextComponent>();

	public abstract void add(DocTextComponent docComponent);

	public ArrayList<DocTextComponent> getComponents(){
		return components;
	}
}
