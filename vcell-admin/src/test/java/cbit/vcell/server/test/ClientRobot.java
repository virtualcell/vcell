package cbit.vcell.server.test;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.io.File;

import org.vcell.util.document.BioModelInfo;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
/**
 * Insert the type's description here.
 * Creation date: (3/8/01 3:00:56 PM)
 * @author: Jim Schaff
 */
public class ClientRobot extends cbit.vcell.client.test.ClientTester implements Runnable {
	private String host = null;
	private String userid = null;
	private String password = null;
	private MixedSessionLog log = null;
	private File bioModelFile = null;
	private cbit.vcell.client.server.ClientServerManager managerManager = null;
	private String name = null;

/**
 * ClientRobot constructor comment.
 */
public ClientRobot(String argName, String argHost, String argUserid, String argPassword, File bioModelFile, MixedSessionLog argSessionLog) {
	this.name = argName;
	this.host = argHost;
	this.userid = argUserid;
	this.password = argPassword;
	this.log = argSessionLog;
	this.bioModelFile = bioModelFile;
}


/**
 * Insert the method's description here.
 * Creation date: (3/9/01 9:24:59 AM)
 * @return java.lang.String
 */
public String getActivityLog() {
	return log.getLocalSessionLog().getLog();
}


/**
 * Insert the method's description here.
 * Creation date: (3/8/01 3:18:01 PM)
 * @return cbit.vcell.desktop.controls.Workspace
 */
private cbit.vcell.client.server.ClientServerManager getManagerManager() {
	return managerManager;
}


/**
 * Insert the method's description here.
 * Creation date: (3/8/01 5:52:26 PM)
 * @return java.lang.String
 */
public String getName() {
	return name;
}


/**
 * Insert the method's description here.
 * Creation date: (3/8/01 3:04:15 PM)
 */
public void run() {
	try {
		String args[] = { host, userid, password };
		setManagerManager(cbit.vcell.client.test.ClientTester.mainInit(args,"ClientRobot",null));
		for (int i=0;i<10;i++){
			log.print("Robot "+getName()+ "starting loop : "+i);
			BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelFile));
			String newName = "bioModel_"+getName()+"_"+Math.random();
			log.print("Saving bioModel \""+newName+"\"");
			getManagerManager().getDocumentManager().saveAsNew(bioModel,newName,null);
			BioModelInfo bioModelInfos[] = getManagerManager().getDocumentManager().getBioModelInfos();
			if (bioModelInfos!=null && bioModelInfos.length>0){
				for (int j=0;j<bioModelInfos.length;j++){
					log.print("bioModelInfo["+j+"] = "+bioModelInfos[j]);
				}
			}
		}
	}catch (Throwable e){
		log.exception(e);
	}finally{
		log.alert("Robot "+getName()+" exiting");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/8/01 3:18:01 PM)
 * @return cbit.vcell.desktop.controls.Workspace
 */
private void setManagerManager(cbit.vcell.client.server.ClientServerManager argManagerManager) {
	
	this.managerManager = argManagerManager;
	
	DocumentManager documentManager = null;
	if (managerManager!=null){
		documentManager = managerManager.getDocumentManager();
	}
	
}
}