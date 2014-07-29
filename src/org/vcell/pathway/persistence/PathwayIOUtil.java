/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.persistence;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.sbpax.schemas.BioPAX3;
import org.vcell.pathway.PathwayModel;
import org.vcell.util.ClientTaskStatusSupport;


public class PathwayIOUtil {

	public static PathwayModel extractPathwayFromJDOM(Document jdomDocument, RDFXMLContext context, 
			ClientTaskStatusSupport statusSupport) {
		PathwayModel pathwayModel = null;
		Element rootElement = jdomDocument.getRootElement();
		@SuppressWarnings("unchecked")
		List<Namespace> namespaces = rootElement.getAdditionalNamespaces();
		boolean itIsLevel3 = false;
		for(Namespace namespace : namespaces) {
			if(namespace != null && namespace.getURI() != null && namespace.getURI().equals(BioPAX3.ns.uri)) {
				itIsLevel3 = true;
				break;
			}
		}
		if(itIsLevel3) {
			PathwayReaderBiopax3 pathwayReader = new PathwayReaderBiopax3(context);
			pathwayModel = pathwayReader.parse(rootElement,true);
		} else {		// if it's not level3 we assume it to be level2
		// TODO: once biopax version3 becomes dominant change the code to use that as the default
			PathwayReader pathwayReader = new PathwayReader(context);
			pathwayModel = pathwayReader.parse(rootElement, statusSupport);
		}
		pathwayModel.reconcileReferences(statusSupport);
		pathwayModel.refreshParentMap();
		return pathwayModel;
	}
	
}
