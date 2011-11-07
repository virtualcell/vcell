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

public class DocText extends DocTextComponent {
	private String text;
	private boolean bBold;
	
	public DocText(String text, boolean argBold){
		this.text = text;
		this.bBold = argBold;
	}

	public String getText() {
		return text;
	}
	
	public boolean getBold(){
		return bBold;
	}

	@Override
	public void add(DocTextComponent docComponent) {
		throw new RuntimeException("children not supported");	
	}
	
}
