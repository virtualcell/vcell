/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package uk.ac.ebi.www.ws.services.WSDbfetch;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import uk.ac.ebi.jdbfetch.exceptions.DbfConnException;
import uk.ac.ebi.jdbfetch.exceptions.DbfException;
import uk.ac.ebi.jdbfetch.exceptions.DbfNoEntryFoundException;
import uk.ac.ebi.jdbfetch.exceptions.DbfParamsException;

public class DBFetchTest {
	public static void main(final String[] args)
	{
		WSDBFetchServerServiceLocator providerLocator = new WSDBFetchServerServiceLocator();
		try {
			WSDBFetchServer server = providerLocator.getWSDbfetch();
			String db = "uniprotkb";
			String fetchResultStr = server.fetchData(db+":WAP_RAT", "uniprotxml", "default");
			
			String[] dbFormats = server.getDbFormats(db);
			//for Debug purpose to print the database's formats and styles that are used in FetchData method
//			for(String dbFormat : dbFormats)
//			{
//				String[] styles = server.getFormatStyles(db, dbFormat);
//				
//				for(String style : styles){
//					System.out.println(db+" format: "+ dbFormat + "  style: " +style + "   ");
//				}
//			}
			
			System.out.println(fetchResultStr);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		}
		
	}

}
