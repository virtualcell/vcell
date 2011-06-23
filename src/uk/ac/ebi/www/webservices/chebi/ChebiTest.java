package uk.ac.ebi.www.webservices.chebi;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;



public class ChebiTest {
	
	public static void main(final String[] args)
	{
		
		ChebiWebServiceServiceLocator locator = new ChebiWebServiceServiceLocator();
		ChebiWebServicePortType portType = null;
		try {
			portType = locator.getChebiWebServicePort();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String ID = "58424";
		SearchCategory sc = SearchCategory.fromString(SearchCategory._value3);
		int maxResults = 200;
		StarsCategory starts = StarsCategory.value1;
		try {
			LiteEntity[] le = portType.getLiteEntity(ID, sc, maxResults, starts);
			System.out.println((le!=null)?le[0].getChebiAsciiName():"cannot find" + ID);
		} catch (ChebiWebServiceFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
