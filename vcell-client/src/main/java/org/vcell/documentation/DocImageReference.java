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

public class DocImageReference extends DocTextComponent {
	private String imageTarget;
	private boolean bInline;

	public DocImageReference(String imageTarget, boolean bInline) {
		super();
		this.imageTarget = imageTarget;
		this.bInline = bInline;
		DocumentCompiler.referencedImageFiles.add(imageTarget);
	}

	public String getImageTarget() {
		return imageTarget;
	}

	public boolean isInline(){
		return bInline;
	}
	
	@Override
	public void add(DocTextComponent docComponent) {
		throw new RuntimeException("children not supported");
	}
	
	
}
