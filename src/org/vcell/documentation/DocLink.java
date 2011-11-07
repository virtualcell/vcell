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

public class DocLink extends DocTextComponent {
	private String target;
	private String text;
	private static final String HTTP_TARGET_STR = "http:";
	
	public DocLink(String target, String text) {
		super();
		this.target = target;
		this.text = text;
	}
	
	public boolean isWebTarget(){
		if(target.toLowerCase().startsWith(HTTP_TARGET_STR)){
			return true;
		}
		else{
			return false;
		}
	}
	
	public String getTarget() {
		return target;
	}
	
	public String getText() {
		return text;
	}

	@Override
	public void add(DocTextComponent docComponent) {
		throw new RuntimeException("not supported");
	}
}
