/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.relationship;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.jdom.Element;
import org.jdom.Namespace;
import org.vcell.util.DataAccessException;

import cbit.util.xml.XmlUtil;

import uk.ac.ebi.jdbfetch.exceptions.DbfConnException;
import uk.ac.ebi.jdbfetch.exceptions.DbfException;
import uk.ac.ebi.jdbfetch.exceptions.DbfNoEntryFoundException;
import uk.ac.ebi.jdbfetch.exceptions.DbfParamsException;
import uk.ac.ebi.www.webservices.chebi.ChebiWebServiceFault;
import uk.ac.ebi.www.webservices.chebi.ChebiWebServicePortType;
import uk.ac.ebi.www.webservices.chebi.ChebiWebServiceServiceLocator;
import uk.ac.ebi.www.webservices.chebi.LiteEntity;
import uk.ac.ebi.www.webservices.chebi.SearchCategory;
import uk.ac.ebi.www.webservices.chebi.StarsCategory;
import uk.ac.ebi.www.ws.services.WSDbfetch.InputException;
import uk.ac.ebi.www.ws.services.WSDbfetch.WSDBFetchServer;
import uk.ac.ebi.www.ws.services.WSDbfetch.WSDBFetchServerServiceLocator;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;


/**
 * The class tries to use DBFetch or Chebi services to get reference 
 * from difference databases based on molecule's database ID.
 * @author: Tracy LI
 * Copyright 2011 University of Connecticut Health Center
 */


public class DataBaseReferenceReader {
	
	public static final String UniProt_RootNodeTag = "uniprot";
	public static final String UniProt_EntryTag	= "entry";
	public static final String Uniprot_NameTag = "name";
	public static final String UniprotDBName = "uniprotkb";
	public static final String InterproDBName = "interpro";
	public static final String Interpro_EntryTag	= "interpro";
	public static final String Interpro_NameTag = "name";
	private static final String format = "uniprotxml";
	private static final String style = "default";
	private static final String GO_URL_prefix = "http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:";
	private static final String GO_URL_surfix = "&format=oboxml";
	
	/**
	 * return a modlecule's reference Name by uniprot as default database.
	 * @param moleculeID: String, the molecule's ID in the uniprot. 
	 * from uniprot database, using uniproxml as format and default as style.
	 * @return String, the molecule's reference name in uniprot
	 */
	public static String getMoleculeDataBaseReference(String moleculeID)
	              throws ServiceException, DbfConnException, DbfNoEntryFoundException, DbfParamsException, DbfException, InputException, RemoteException
	{
		return getReferenceFromUniProt(moleculeID, format, style);
	}
	
	public static String getMoleculeDataBaseReference(String dbName, String moleculeID)
		throws ServiceException, DbfConnException, DbfNoEntryFoundException, DbfParamsException, DbfException, InputException, RemoteException, DataAccessException
	{
		if(dbName.equals(InterproDBName)){
			String name = getMoleculeDataBaseReference(dbName, moleculeID, "interproxml", style);
			if(name != null)
				return name;
		}
		return null;
	}
	
	/**
	 * return a molecule's reference name based on its ID from selected database
	 * @param dbName: String, the database name.
	 * @param moleculeID: String, the molecule's ID in the database
	 * @param dbFormat: output format, etc. xml, txt.
	 * @param dbStyle: default.
	 * @return String, the molecule's reference name in specific database.
	 */
	public static String getMoleculeDataBaseReference(String dbName,  String moleculeID, String dbFormat, String dbStyle) 
	              throws DataAccessException, ServiceException, DbfConnException, DbfNoEntryFoundException, DbfParamsException, DbfException, InputException, RemoteException
	
	{
		if(dbName.equals(UniprotDBName))
		{
			return getReferenceFromUniProt(moleculeID, dbFormat, dbStyle);
		}else if(dbName.equals(InterproDBName)){
			return getReferenceFromInterpro(InterproDBName, moleculeID, "interproxml", dbStyle);
		}
		/*...add other databases here ...*/
		else
		{
			throw new DataAccessException("Can not find such database: " + dbName);
		}
		
	}

	private static String getXMLFromUniProt(String moleculeID, String dbFormat, String dbStyle) 
	               throws ServiceException, DbfConnException, DbfNoEntryFoundException, DbfParamsException, DbfException, InputException, RemoteException 
	{
		WSDBFetchServerServiceLocator providerLocator = new WSDBFetchServerServiceLocator();
		WSDBFetchServer server = providerLocator.getWSDbfetch();
		String fetchResultStr = server.fetchData(UniprotDBName + ":" + moleculeID, dbFormat, dbStyle);
								
		return fetchResultStr;
		
	}
	private static String getXMLFromInterpro(String dbName, String moleculeID, String dbFormat, String dbStyle) 
    	throws ServiceException, DbfConnException, DbfNoEntryFoundException, DbfParamsException, DbfException, InputException, RemoteException 
	{
		WSDBFetchServerServiceLocator providerLocator = new WSDBFetchServerServiceLocator();
		WSDBFetchServer server = providerLocator.getWSDbfetch();
		String fetchResultStr = server.fetchBatch(dbName, moleculeID, dbFormat, dbStyle);
		return fetchResultStr;
	
	}
	
	private static String getReferenceFromUniProt(String moleculeID, String dbFormat, String dbStyle)
	               throws ServiceException, DbfConnException, DbfNoEntryFoundException, DbfParamsException, DbfException, InputException, RemoteException
	{
		String referenceName = null;
		
		String xmlStr = getXMLFromUniProt(moleculeID, dbFormat, dbStyle);
		//get root
		Element uniprotRoot = XmlUtil.stringToXML(xmlStr, null).getRootElement();
		Namespace uniprotNameSpace = uniprotRoot.getNamespace();
		//get entry
		Element entryElement = uniprotRoot.getChild(UniProt_EntryTag, uniprotNameSpace);
		//get name
		if(entryElement == null) {
			return null;	// not a big deal if we cannot get the uniprot name
		}
		Element nameElement = entryElement.getChild(Uniprot_NameTag, uniprotNameSpace);
		if(nameElement == null) {
			return null;
		}
		referenceName = nameElement.getText();
		
		return referenceName;
		
	}
	private static String getReferenceFromInterpro(String dbName, String moleculeID, String dbFormat, String dbStyle)
    throws ServiceException, DbfConnException, DbfNoEntryFoundException, DbfParamsException, DbfException, InputException, RemoteException
	{
		String referenceName = null;
		
		String xmlStr = getXMLFromInterpro(dbName, moleculeID, dbFormat, dbStyle);
		//get root
		Element root = XmlUtil.stringToXML(xmlStr, null).getRootElement();
		Namespace nameSpace = root.getNamespace();
		//get entry
		Element entryElement = root.getChild(Interpro_EntryTag, nameSpace);
		//get name
		if(entryElement == null) {
			return null;	// not a big deal if we cannot get the interpro name
		}
		Element nameElement = entryElement.getChild(Interpro_NameTag, nameSpace);
		if(nameElement == null) {
			return null;
		}
		referenceName = nameElement.getText();
		
		return referenceName;
	
	}

	public static String getChEBIName(String chebiId){
		String name = null;
		SearchCategory sc = SearchCategory.fromString(SearchCategory._value3);
		int maxResults = 200;
		StarsCategory starts = StarsCategory.value1;
		ChebiWebServiceServiceLocator locator = new ChebiWebServiceServiceLocator();
		ChebiWebServicePortType portType = null;
		try {
			portType = locator.getChebiWebServicePort();
			LiteEntity[] le = portType.getLiteEntity(chebiId, sc, maxResults, starts);
			if(le!=null)
				name = le[0].getChebiAsciiName();
//			System.out.println((le!=null)?le[0].getChebiAsciiName():"cannot find" + chebiId);
		} catch (ChebiWebServiceFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}
	
	public static String getGOTerm(String goId){
		String name = null;
		// URL a GO Term in OBO xml format
        URL u;
		try {
			String url_str = GO_URL_prefix + goId + GO_URL_surfix;
			u = new URL(url_str);
	        // Connect
	        HttpURLConnection urlConnection;
			urlConnection = (HttpURLConnection) u.openConnection();
	        // Parse an XML document from the connection
	        InputStream inputStream;
			inputStream = urlConnection.getInputStream();
			Document xml;
			xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
			inputStream.close();

			// XPath is here used to locate parts of an XML document
			XPath xpath=XPathFactory.newInstance().newXPath();

			//Locate the term name and print it out
			name = xpath.compile("/obo/term/name").evaluate(xml);
//			System.out.println("Term name:"+xpath.compile("/obo/term/name").evaluate(xml));
		
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}
	
	public static void main(final String[] args)
	{
		System.out.println("GO term is ... " + getGOTerm("GO:0006814"));
		try {
			String name = DataBaseReferenceReader.getMoleculeDataBaseReference("p00533");
			System.out.println("Name is ....." + name);
		} catch (DbfConnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbfNoEntryFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbfParamsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
