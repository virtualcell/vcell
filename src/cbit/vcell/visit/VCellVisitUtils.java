/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.visit;

import java.io.*;
import java.util.ArrayList;

import com.jcraft.jsch.*;
import javax.swing.JDialog;
import javax.swing.filechooser.*;
import cbit.vcell.resource.ResourceUtil;

import org.vcell.util.Executable;
import org.vcell.util.ExecutableStatus;
import org.vcell.util.gui.DialogUtils;
import cbit.vcell.client.data.PDEDataViewer;

import java.security.*;
import java.security.spec.*;

public class VCellVisitUtils {
	
	
private static boolean bSucceeded = false;

private static PublicKey getUserPublicKey(String filename) throws Exception {
	File f = new File(filename);
	FileInputStream fileInputStream = new FileInputStream(f);
	DataInputStream dataInputStream = new DataInputStream(fileInputStream);
	byte[] keyBytes = new byte[(int)f.length()];
	dataInputStream.readFully(keyBytes);
	dataInputStream.close();
	
	X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    PublicKey pk = keyFactory.generatePublic(spec);
    return pk;
}

private static String getUserPublicKeyString(String filename) throws Exception {
	File f = new File(filename);
	FileInputStream fileInputStream = new FileInputStream(f);
	DataInputStream dataInputStream = new DataInputStream(fileInputStream);
	byte[] keyBytes = new byte[(int)f.length()];
	dataInputStream.readFully(keyBytes);
	dataInputStream.close();
	
	return(new String(keyBytes));
	
}

public static boolean checkVisitResourcePrerequisites(PDEDataViewer thisPDEDataViewer, String expectedVisitDirectoryName){
	
	
	
	//Check whether Visit installation exists
	
	File visitDirectory=new File(ResourceUtil.getVcellHome(),expectedVisitDirectoryName);
	if (visitDirectory.exists()){
		//Now check to see if the user has existing ssh keys
		File sshPublicKeyFile = null;
		if (ResourceUtil.osname=="windows"){
		
			sshPublicKeyFile=new File(new File(visitDirectory,".ssh"),"id_rsa.pub");
		}

		else {
			    //TODO: handle case where user's key has a nonstandard filename 
				sshPublicKeyFile=new File(new File(ResourceUtil.getUserHomeDir(),".ssh"),"id_rsa.pub");
		}
		if (sshPublicKeyFile.exists())
			return(true);
		else
		{
			//Handle creation of key pair
		}
	}
	else
	{
		//Handle installation of Visit, *then* handle checking and if necessary installing keypair
		
		String message = "There does not appear to be an appropriate version of VisIt present.  Would you like VisIt installed?\n";
		message=message+"VisIt will be installed at location "+ ResourceUtil.getVcellHome()+File.pathSeparator+expectedVisitDirectoryName+ " (and must reside at that location)";
	
		
		String proceedMessage = DialogUtils.showOKCancelWarningDialog(thisPDEDataViewer, "Visit Not Installed", message);
		
		if (!(proceedMessage=="OK")){
			
			//Show message dialog explaining they can try again if they want
			
			return(false);
			
		}
		
		if (ResourceUtil.osname == "windows"){
			bSucceeded=installVisitWindows();
		}
		else {
			bSucceeded=installVisitUnix();
		}
		
		if (!bSucceeded) {
			DialogUtils.showErrorDialog(thisPDEDataViewer, "VisIt installation failed!");
			return false;
		}
		else {
			if (VCellVisitUtils.checkVisitResourcePrerequisites(thisPDEDataViewer, expectedVisitDirectoryName)){
				return true;
			}
			else{
				return false;
			}
		}
	
	}
	
	return(false);
}

private static boolean installVisitUnix(){
	
	
	
//	try {
//		ArrayList<String> args = new ArrayList<String>();
//		args.add(wget);//location of visit
//		args.add("-movie");
//		args.add("-sessionfile");
//		args.add(localSessionFile);
//		args.add("-format");
//		args.add("mpeg");
//		args.add("-output");
//		args.add(fileLocation);
//		//args.add("/eboyce-local/"+currentLogFile.substring(0, currentLogFile.length() - 4));
//		Executable executable = new Executable(args.toArray(new String[0]));
//		executable.start();
//		while (!executable.getStatus().equals(ExecutableStatus.COMPLETE) && !executable.getStatus().equals(ExecutableStatus.STOPPED)){
//			Thread.sleep(1000);
//			System.out.println("waiting");
//			
//		}
//        //TODO: if error, should show error message to users.
//		System.out.println("done : status = " + executable.getStatus());
//	} catch (Exception e) {
//		e.printStackTrace();
//		throw e;
//	}
	
	
	
	
	return false;
}

private static boolean installVisitWindows(){
	
	return false;
}

private static boolean installKeyPairUnix(){
	
	return false;
}

private static boolean installKeyPairWindows(){
	
	return false;
}

public static void main(String[] args){
	
	try {
		System.out.println(getUserPublicKeyString("/home/VCELL/eboyce/.ssh/id_rsa.pub"));
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	
}

}

