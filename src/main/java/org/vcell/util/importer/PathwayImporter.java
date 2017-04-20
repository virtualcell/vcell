package org.vcell.util.importer;

import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.persistence.PathwayIOUtil;
import org.vcell.pathway.persistence.RDFXMLContext;
import org.vcell.util.ClientTaskStatusSupport;

import cbit.util.xml.XmlUtil;

public class PathwayImporter extends DataImporter {
	
	protected PathwayModel pathwayModel = null;

	public void reset() {
		super.reset();
		pathwayModel = null;
	}
	
	public PathwayModel extractPathwayModelFromPreviouslyRead(ClientTaskStatusSupport statusSupport) {
		String data = getPreviouslyReadData();
		if(data != null) {
			org.jdom.Document jdomDocument = XmlUtil.stringToXML(data, null);
			pathwayModel = PathwayIOUtil.extractPathwayFromJDOM(jdomDocument, new RDFXMLContext(), statusSupport);
			return pathwayModel;			
		}
		return null;
	}
	
	public PathwayModel getPreviouslyExtractedPathwayModel() { return pathwayModel; }

}
