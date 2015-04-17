/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.inversepde.gui;


import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFrame;

import org.vcell.inversepde.InverseProblem;
import org.vcell.inversepde.services.InversePDERequestManager;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.server.ClientServerManager;

public class InverseProblemPanelTest {
	public static void main(String[] args){
		try {
			File directory = null;
			if (args.length!=4){
				throw new RuntimeException("usage: InverseProblemPanelTest [-local | host] userid password workingDirectory");
			}
			File file = new File(args[3]);
			if (file.exists() && !file.isDirectory()){
				throw new RuntimeException("workingDirectory '"+args[3]+"' is not a directory");
			}else if (!file.exists()){
				file.mkdirs();
			}else{
				directory = file;
			}
			
			
			InverseProblem inverseProblem = new InverseProblem();
			
			JFrame frame = new JFrame("inverse problem");
			InverseProblemPanel ipPanel = new InverseProblemPanel();
			frame.add(ipPanel);
			frame.addWindowListener(new WindowListener(){
				public void windowActivated(WindowEvent e) {
				}
				public void windowClosed(WindowEvent e) {
					System.exit(0);
				}
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
				public void windowDeactivated(WindowEvent e) {
				}
				public void windowDeiconified(WindowEvent e) {
				}
				public void windowIconified(WindowEvent e) {
				}
				public void windowOpened(WindowEvent e) {
				}
			});
			frame.setSize(800,900);
			ClientServerManager clientServerManager = mainInit(args, "InverseProblemPanelTest");
//			InversePDERequestManager inversePDERequestManager = new InversePDERequestManager(clientServerManager, directory);
			InversePDERequestManager inversePDERequestManager = null;
			ipPanel.setInversePDERequestManager(inversePDERequestManager );
			ipPanel.setInverseProblem(inverseProblem);
			System.setOut(new TextAreaPrintStream(ipPanel.getTextArea()));
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	public static cbit.vcell.client.server.ClientServerManager mainInit(String args[], String programName) throws Exception {
		ClientServerInfo csInfo = null;
		if (args.length >= 3) {
			if (args[0].equalsIgnoreCase("-local")) {
				csInfo = ClientServerInfo.createLocalServerInfo(args[1], new DigestedPassword(args[2]));
			} else {
				csInfo = ClientServerInfo.createRemoteServerInfo(new String[] {args[0]}, args[1], new DigestedPassword(args[2]));
			}
		}else{
			System.err.println("usage: " + programName + " -local userid password");
			//System.err.println("usage: " + programName + " -jms userid password");
			System.err.println("usage: " + programName +" host userid password");
			throw new Exception("cannot connect");
		}
		ClientServerManager clientServerManager = new ClientServerManager();
		clientServerManager.connect(null, csInfo);
		
		return clientServerManager;
	}

}
