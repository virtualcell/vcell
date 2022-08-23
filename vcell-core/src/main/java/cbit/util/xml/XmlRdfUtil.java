/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.util.xml;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.sbpax.impl.HashGraph;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.sbpax.schemas.util.OntUtil;
import org.sbpax.util.SesameRioUtil;
import org.vcell.sedml.PubMet;
import org.vcell.util.BeanUtils;
import org.vcell.util.ClientTaskStatusSupport;

/**
 * General Xml Rdf utility methods.
 */
public class XmlRdfUtil {
	private static final Logger logger = LogManager.getLogger(XmlRdfUtil.class);

	
    public static String getMetadata(String vcmlName) {
    	String ret = "";
        String ns = DefaultNameSpaces.EX.uri;

		Graph graph = new HashGraph();
		Graph schema = new HashGraph();

       	String description = "http://omex-library.org/" + vcmlName + ".omex";	// make an empty rdf file
		URI descriptionURI = ValueFactoryImpl.getInstance().createURI(description);
		graph.add(descriptionURI, RDF.TYPE, PubMet.Description);		// <rdf:Description rdf:about='http://omex-library.org/Monkeyflower_pigmentation_v2.omex'>
		
		Literal descTitle = OntUtil.createTypedString(schema, "Untitled");
		graph.add(descriptionURI, PubMet.Title, descTitle);
   		try {
   			Map<String, String> nsMap = DefaultNameSpaces.defaultMap.convertToMap();
   			ret = SesameRioUtil.writeRDFToString(graph, nsMap, RDFFormat.RDFXML);
//   			ret = ret.replaceFirst(".omex\"/", ".omex\"");
   			
//   			int index = ret.indexOf(PubMet.EndRdf);
   			int index = ret.indexOf(PubMet.EndDescription0);
   			String end = ret.substring(index);
   			ret = ret.substring(0, ret.indexOf(end));

   			Calendar calendar = Calendar.getInstance();
   			Date today = calendar.getTime();
   			ret += PubMet.CommentModified;
   			ret += PubMet.StartModified;
   			ret += PubMet.StartDescription;
   			ret += PubMet.PrefixModified;
   			ret += new SimpleDateFormat("yyyy-MM-dd").format(today);
   			ret += PubMet.SuffixModified;
   			ret += PubMet.EndDescription;
   			ret += PubMet.EndModified;
   			ret += "\n\n";
   			ret += PubMet.EndDescription0;
   			ret += "\n\n";
   			ret += PubMet.EndRdf;

   			System.out.println(ret);
   		} catch (RDFHandlerException e) {
			logger.error("failed to create metadata");
		}
   		return ret;
   	}
	
	
}
