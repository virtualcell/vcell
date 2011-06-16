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
import uk.ac.ebi.www.ws.services.WSDbfetch.InputException;
import uk.ac.ebi.www.ws.services.WSDbfetch.WSDBFetchServer;
import uk.ac.ebi.www.ws.services.WSDbfetch.WSDBFetchServerServiceLocator;
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
	private static final String dataBaseName = UniprotDBName;
	private static final String format = "uniprotxml";
	private static final String style = "default";
	
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
		Element nameElement = entryElement.getChild(Uniprot_NameTag, uniprotNameSpace);
		referenceName = nameElement.getText();
		
		return referenceName;
		
	}

	public static void main(final String[] args)
	{
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
