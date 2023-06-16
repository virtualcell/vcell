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
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.Version;

import cbit.vcell.biomodel.BioModel;

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

   			//System.out.println(ret);
   		} catch (RDFHandlerException e) {
			logger.error("failed to create metadata");
		}
   		return ret;
   	}
	
    public static String getMetadata(String vcmlName, BioModel bioModel, File diagram, BioModelInfo bioModelInfo) {
    	String ret = "";
        String ns = DefaultNameSpaces.EX.uri;

		Graph graph = new HashGraph();
		Graph schema = new HashGraph();

        if(bioModelInfo == null) {								// perhaps it's not public, in which case is not in the map
        	ret = XmlRdfUtil.getMetadata(vcmlName);
        	return ret;
        }
        PublicationInfo[] publicationInfos = bioModelInfo.getPublicationInfos();
        if(publicationInfos == null || publicationInfos.length == 0) {				// we may not have PublicationInfo
        	ret = XmlRdfUtil.getMetadata(vcmlName);
    		return ret;
        }
        
        PublicationInfo publicationInfo = publicationInfos[0];
        String bioModelName = bioModel.getName();
        Version version = bioModelInfo.getVersion();
        String[] creators = publicationInfo.getAuthors();
        String citation = publicationInfo.getCitation();
        String doi = publicationInfo.getDoi();
        Date pubDate = publicationInfo.getPubDate();
        String pubmedid = publicationInfo.getPubmedid();
        String sTitle = publicationInfo.getTitle();
        String url = publicationInfo.getUrl();
        List<String> contributors = new ArrayList<>();
        contributors.add("Dan Vasilescu");
        contributors.add("Michael Blinov");
        contributors.add("Ion Moraru");
        
		String description = "http://omex-library.org/" + vcmlName + ".omex";	// "http://omex-library.org/biomodel_12345678.omex";
		URI descriptionURI = ValueFactoryImpl.getInstance().createURI(description);
		Literal descTitle = OntUtil.createTypedString(schema, sTitle);
		graph.add(descriptionURI, RDF.TYPE, PubMet.Description);		// <rdf:Description rdf:about='http://omex-library.org/Monkeyflower_pigmentation_v2.omex'>
		graph.add(descriptionURI, PubMet.Title, descTitle);
		
		try {
			Map<String, String> nsMap = DefaultNameSpaces.defaultMap.convertToMap();
			ret = SesameRioUtil.writeRDFToString(graph, nsMap, RDFFormat.RDFXML);
//			SesameRioUtil.writeRDFToStream(System.out, graph, nsMap, RDFFormat.RDFXML);
		} catch (RDFHandlerException e) {
			logger.error(e.getMessage(), e);
		}
		
		String end = "\n\n" + ret.substring(ret.indexOf(PubMet.EndDescription0));
		ret = ret.substring(0, ret.indexOf(PubMet.EndDescription0));

		// https://vcellapi-beta.cam.uchc.edu:8080/biomodel/200301683/diagram
		// <collex:thumbnail rdf:resource="http://omex-library.org/Monkeyflower_pigmentation_v2.omex/Figure1.png"/>
		if(diagram.exists()) {
			ret += PubMet.StartDiagram;
			ret += description;
			ret += "/diagram.png";
			ret += PubMet.EndDiagram;
		}
		
		ret += PubMet.CommentTaxon;
		
		ret += PubMet.CommentOther;
		ret += PubMet.StartIs;
		ret += PubMet.StartDescription;
		ret += PubMet.StartIdentifier;
		ret += PubMet.ResourceIdentifier;
		String isLabel = "vcell:" + version.getVersionKey();
		ret += isLabel;
		ret += PubMet.EndIdentifier;
		ret += PubMet.StartLabel;
		ret += isLabel;
		ret += PubMet.EndLabel;
		ret += PubMet.EndDescription;
		ret += PubMet.EndIs;

		ret += PubMet.StartIsDescribedBy;
		ret += PubMet.StartDescription;
		ret += PubMet.StartIdentifier;
		ret += PubMet.ResourceIdentifier;
		String pubmed = "pubmed:" + pubmedid;
		ret += pubmed;
		ret += PubMet.EndIdentifier;
		ret += PubMet.StartLabel;
		ret += pubmed;
		ret += PubMet.EndLabel;
		ret += PubMet.EndDescription;
		ret += PubMet.EndIsDescribedBy;

		ret += PubMet.CommentCreator;
		for(String creator : creators) {
			ret += PubMet.StartCreator;
			ret += PubMet.StartDescription;
			ret += PubMet.StartName;
			ret += creator;
			ret += PubMet.EndName;
			ret += PubMet.StartLabel;
			ret += creator;
			ret += PubMet.EndLabel;
			ret += PubMet.EndDescription;
			ret += PubMet.EndCreator;
		}
		
		ret += PubMet.CommentContributor;
		for(String contributor : contributors) {
			ret += PubMet.StartContributor;
			ret += PubMet.StartDescription;
			ret += PubMet.StartName;
			ret += contributor;
			ret += PubMet.EndName;
			ret += PubMet.StartLabel;
			ret += contributor;
			ret += PubMet.EndLabel;
			ret += PubMet.EndDescription;
			ret += PubMet.EndContributor;
		}

		ret += PubMet.CommentCitations;
		ret += PubMet.StartIsDescribedBy;
		ret += PubMet.StartDescription;
		ret += PubMet.StartIdentifier;
		ret += PubMet.ResourceIdentifier;
		String sdoi = "doi:" + doi;
		ret += sdoi;
		ret += PubMet.EndIdentifier;
		ret += PubMet.StartLabel;
		ret += citation;
		ret += PubMet.EndLabel;
		ret += PubMet.EndDescription;
		ret += PubMet.EndIsDescribedBy;
		
//		ret += PubMet.CommentLicense;
//		ret += PubMet.StartLicense;
//		ret += PubMet.StartDescription;
//		ret += PubMet.StartIdentifier;
//		ret += PubMet.ResourceIdentifier;
//		String lic = "spdx:" + "CC0-1.0";
//		ret += lic;
//		ret += PubMet.EndIdentifier;
//		ret += PubMet.StartLabel;
//		ret += "CC0-1.0";
//		ret += PubMet.EndLabel;
//		ret += PubMet.EndDescription;
//		ret+= PubMet.EndLicense;
		
		String sPubDate = new SimpleDateFormat("yyyy-MM-dd").format(pubDate);
		ret += PubMet.CommentCreated;
		ret += PubMet.StartCreated;
		ret += PubMet.StartDescription;
		ret += PubMet.PrefixCreated;
		ret += new SimpleDateFormat("yyyy-MM-dd").format(pubDate);
		ret += PubMet.SuffixCreated;
		ret += PubMet.EndDescription;
		ret += PubMet.EndCreated;
		
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
		
		ret += end;
		logger.trace(ret);
		return(ret);
    }

}
