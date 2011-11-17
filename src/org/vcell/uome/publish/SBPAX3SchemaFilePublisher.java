package org.vcell.uome.publish;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.openrdf.model.Graph;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.sbpax.schemas.SBPAX3;
import org.sbpax.schemas.UOMECore;
import org.sbpax.schemas.UOMEList;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.sbpax.util.SesameRioUtil;

public class SBPAX3SchemaFilePublisher {

	public static final String DIR = "";
	
	public static void writeFile(Graph graph, String fileName, RDFFormat format) 
	throws FileNotFoundException, RDFHandlerException {
		FileOutputStream os = new FileOutputStream(fileName);
		SesameRioUtil.writeRDFToStream(os, graph, 
				DefaultNameSpaces.defaultMap.convertToMap(), format);
	}

	public static void writeDefaultFiles(Graph graph, String baseFileName) 
	throws FileNotFoundException, RDFHandlerException {
		writeFile(graph, baseFileName + ".owl", RDFFormat.RDFXML);
		writeFile(graph, baseFileName + ".n3", RDFFormat.N3);
	}
	
	public static void main(String[] args) 
	throws FileNotFoundException, RDFHandlerException {
		SesameRioUtil.setWriteRDFXMLPretty(true);
		writeDefaultFiles(SBPAX3.schema, DIR + "sbpax3");
		writeDefaultFiles(UOMECore.schema, DIR + "core");
		writeDefaultFiles(UOMEList.schema, DIR + "list");
	}

}
