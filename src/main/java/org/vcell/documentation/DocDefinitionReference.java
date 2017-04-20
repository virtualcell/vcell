/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.documentation;

public class DocDefinitionReference extends DocTextComponent {
	private String defTarget;
	private String text;

	public DocDefinitionReference(String defTarget, String text) {
		super();
		this.defTarget = defTarget;
		this.text = text;
	}

	public String getDefinitionTarget() {
		return defTarget;
	}

	public String getText() {
		return text;
	}
	
	@Override
	public void add(DocTextComponent docComponent) {
		throw new RuntimeException("children not supported");
	}
	
	
}
