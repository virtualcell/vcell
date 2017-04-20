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

public class DocParagraph extends DocTextComponent {

	@Override
	public void add(DocTextComponent docComponent) {
		if (docComponent instanceof DocText || docComponent instanceof DocImageReference || docComponent instanceof DocList 
			|| docComponent instanceof DocLink || docComponent instanceof DocDefinitionReference)
		{
			components.add(docComponent);
		}else{
			throw new RuntimeException(docComponent.getClass().getName()+" not supported");
		}
	}

}
