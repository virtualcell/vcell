package org.vcell.sbml;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.message.server.console.LoginChecker;
import cbit.vcell.message.server.console.LoginChecker.SiteInfo;
import cbit.vcell.resource.CurrentWorkingAsInstall;
import cbit.vcell.server.UserMetaDbServer;
import cbit.vcell.server.VCellBootstrap;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

/**
 * test fixture
 * @author GWeatherby
 */
public class SBMLExportTest {

	private String failMessage;

	SBMLExportTest( ) {
		failMessage = null;
	}

	public static void main(String[] args) {
		if (args.length != 4) {
			System.out.println("Usage: site username password");
			System.exit(99);
		}
		CurrentWorkingAsInstall.init();
		String site = args[0];
		String user = args[1];
		String password = args[2];
		String modelName = args[3];
		final long timeOut = TimeUnit.MINUTES.toMillis(1);
		final String timeOutString = Long.toString(timeOut);
		System.setProperty("sun.rmi.transport.tcp.responseTimeout",timeOutString); 

		SBMLExportTest sExpTest = new SBMLExportTest();
		sExpTest.testExport(site, user, password, modelName);
	}
	
	boolean valid(Object o, String msg) {
		if  (o != null) {
			return true;
		}
		failMessage = msg;
		return false;
	}

	void testExport(String site, String user, String password, String modelName) {
		VCellConnection vcc = getConnection(site, user, password);
		try {
			BioModel bm = getBiomodelByName(vcc, modelName); 
			if (valid(bm,"No BioModel named " + modelName)) {
				SimulationContext sc = findSpatialDeterministic(bm);
				if (valid(sc,"No spatial deterministic sim context")) {
					export(sc);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		displayFails();
	}
	
	void displayFails( ) {
		if (failMessage != null) {
			System.err.println(failMessage);
			failMessage = null;
		}
	}

	static SimulationContext findSpatialDeterministic(BioModel bm) {
		for (SimulationContext simContext : bm.getSimulationContexts()) {
			if (simContext.isStoch()) {
				continue;
			}
			if (simContext.getGeometry().getDimension() == 0) {
				continue;
			}
			return simContext;
		}
		return null;
	}

	void export(SimulationContext sc) throws XmlParseException, IOException  {
		BioModel bioModel = sc.getBioModel();
		String resultString = XmlHelper.exportSBML(bioModel, 3,1,0,true, sc, null);
		String niceName = bioModel.getName().replace(' ', '_') + ".sbml";
		File exportFile = new File(niceName);
		XmlUtil.writeXMLStringToFile(resultString, exportFile.getAbsolutePath(), true);
	}



	static VCellConnection getConnection(String siteName, String user, String password) {
		Collection<SiteInfo> sites = LoginChecker.getSiteInfos(siteName);
		for (SiteInfo si : sites) {
			try {
				String url = si.bootStrapUrl();
				VCellBootstrap vcellBootstrap = (VCellBootstrap) java.rmi.Naming.lookup(url);
				DigestedPassword dp = new UserLoginInfo.DigestedPassword(password);
				UserLoginInfo uli = new UserLoginInfo(user,dp);
				VCellConnection vcellConnection = vcellBootstrap.getVCellConnection(uli);
				return vcellConnection;
			} catch (Exception e) {
				System.err.println(e.getMessage());
				continue;
			}
		}
		throw new RuntimeException("No connection for site " + siteName);
	}

	static BioModel getBiomodelByName(VCellConnection vcn, String name) throws RemoteException, DataAccessException, XmlParseException {
		EscapedName eName = new EscapedName(name);
		UserMetaDbServer dataServer = vcn.getUserMetaDbServer();
		for (BioModelInfo bmi : dataServer.getBioModelInfos(false)) {
			String mName = bmi.getVersion().getName();
			if (eName.matches(mName)) {
				KeyValue bioModelKey = bmi.getVersion().getVersionKey();
				BigString bioModelXML = dataServer.getBioModelXML(bioModelKey);
				BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
				return bioModel;
			}
		}
		//if not found, list what's available
		for (BioModelInfo bmi : dataServer.getBioModelInfos(false)) {
			String mName = bmi.getVersion().getName();
			System.out.println(mName);
		}
		return null;
	}

	/*
	bioModel.refreshDependencies();
	 */
	private static class EscapedName {
		final String original;
		final String escaped;
		EscapedName(String original) {
			super();
			this.original = original;
			escaped = original.replace('_', ' ');
		}
		boolean matches(String candidate) {
			return candidate.equals(original) || candidate.equals(escaped);
		}



	}
}
