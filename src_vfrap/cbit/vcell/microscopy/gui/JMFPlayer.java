/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import org.vcell.util.FileUtils;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;

/**
 * To play a movie with Java Media Framework. Used in VFrap to show the
 * QuickTime movie of both exp and sim data.
 */
@SuppressWarnings("serial")
public class JMFPlayer extends JPanel implements ControllerListener {

	/** The player object */
	Player thePlayer = null;
	
	private ChildWindow childWindow = null;

	/** The visual component (if any) */
	Component visualComponent = null;

	/** The default control component (if any) */
	Component controlComponent = null;

	// /** The name of this instance's media file. */
	// String mediaName;

	/** The URL representing this media file. */
	URL theURL;

	JButton saveButton2 = null;

	private JPanel playerPanel;

	/** Construct the player object and the GUI. */
	public JMFPlayer(String mediaName, String fileStr) {
		super();
		setLayout(new BorderLayout());

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		final File movieFile = new File(fileStr);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		//buttonPanel.setBackground(new Color(184, 184, 190));

		JButton saveButton = new JButton("Save Movie As...");
		//saveButton.setBackground(new Color(184, 184, 190));
		//saveButton.setBorderPainted(false);
		GridBagConstraints buttonConstraints = new GridBagConstraints();
		buttonConstraints.gridy = 0;
		buttonConstraints.gridx = 0;
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save(movieFile);
			}
		});
	
		buttonPanel.add(saveButton, buttonConstraints);
		infoPanel.add(buttonPanel);

		
		Label expDataRangeLabel = new Label("Top: Experimental Data.  Value range [0.01,1.1]");
		infoPanel.add(expDataRangeLabel);
		
		Label simDataRangeLabel = new Label("Bottom: Simulation Data.  Value range [0.01,1.1]");
		infoPanel.add(simDataRangeLabel);
		
		JSeparator separator = new JSeparator();
		infoPanel.add(separator);
		//infoPanel.setBackground(new Color(184, 184, 190));

		add(infoPanel, BorderLayout.NORTH);

		playerPanel = new JPanel();
		
		playerPanel.setLayout(new BorderLayout());
		try {
			theURL = new URL(getClass().getResource("."), mediaName);
			thePlayer = Manager.createPlayer(theURL);
			thePlayer.addControllerListener(this);
		} catch (MalformedURLException e) {
			System.err.println("JMF URL creation error: " + e);
		} catch (Exception e) {
			System.err.println("JMF Player creation error: " + e);
			return;
		}
		add(playerPanel, BorderLayout.CENTER);
		
		// Start the player: this will notify ControllerListener.
		thePlayer.start(); // start playing
	}
	
	public void setChildWindow(ChildWindow childWindow){
		this.childWindow = childWindow;
	}

	private void save(final File movieFile){
		int choice = VirtualFrapLoader.saveMovieFileChooser.showSaveDialog(this);
		File outputFile = null;
		if (choice == JFileChooser.APPROVE_OPTION) {
			String outputFileName = VirtualFrapLoader.saveMovieFileChooser
					.getSelectedFile().getPath();
			outputFile = new File(outputFileName);
			if (!VirtualFrapLoader.filter_qt.accept(outputFile)) {
				if (outputFile.getName().indexOf(".") == -1) {
					outputFile = new File(outputFile.getParentFile(),
							outputFile.getName() + "."
									+ VirtualFrapLoader.QT_EXTENSION);
				} else {
					DialogUtils.showErrorDialog(this,
							"Quick Time movie must have an extension of ."
									+ VirtualFrapLoader.QT_EXTENSION);
					return;
				}
			}
			// copy saved movie file to user specified path.
			if(outputFile != null)
			{
				final File output = outputFile;
				AsynchClientTask saveTask = new AsynchClientTask("Saving movie to " + output.getAbsolutePath(), AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
	    		{
	    			public void run(Hashtable<String, Object> hashTable) throws Exception
	    			{
	    				FileUtils.copyFile(movieFile, output);
	    			}
	    		};
	    		ClientTaskDispatcher.dispatch(JMFPlayer.this, new Hashtable<String, Object>(), new AsynchClientTask[]{saveTask},true, false, true,null,true);
			}
		} else {
			throw UserCancelException.CANCEL_GENERIC;
		}
	}
	
	/** Called to stop the audio, as from a Stop button or menuitem */
	public void stop() {
		if (thePlayer == null)
			return;
		thePlayer.stop(); // stop playing!
		thePlayer.deallocate(); // free system resources
	}

	/** Called when we are really finished (as from an Exit button). */
	public void destroy() {
		if (thePlayer == null)
			return;
		thePlayer.close();
	}

	/** Called by JMF when the Player has something to tell us about. */
	public synchronized void controllerUpdate(ControllerEvent event) {
		if (event instanceof RealizeCompleteEvent) {
			if ((visualComponent = thePlayer.getVisualComponent()) != null)
				playerPanel.add(BorderLayout.CENTER, visualComponent);
			if ((controlComponent = thePlayer.getControlPanelComponent()) != null)
				playerPanel.add(BorderLayout.SOUTH, controlComponent);
			// re-size the main window
			if (childWindow != null) {
				try {

					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							childWindow.pack();
							childWindow.toFront();
						}
					});
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				childWindow.setTitle("VFRAP Movie");
			}
		}
	}

	public static void main(String[] argv) {
		// JFrame f = new JFrame("JMF Player Test");
		// Container frameCP = f.getContentPane();
		// JMFPlayer p = new JMFPlayer(f,
		// argv.length == 0 ? "file:///C:/VirtualMicroscopy/test.mov"
		// : argv[0]);
		// // p.setSize(350, 500);
		// frameCP.add(BorderLayout.CENTER, p);
		// // f.setSize(360, 550);
		// f.setVisible(true);
		// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		VirtualFrapLoader.saveMovieFileChooser = new JFileChooser();
		VirtualFrapLoader.saveMovieFileChooser
				.addChoosableFileFilter(VirtualFrapLoader.filter_qt);
		VirtualFrapLoader.saveMovieFileChooser
				.setAcceptAllFileFilterUsed(false);
		// VirtualFrapLoader.saveMovieFileChooser.setCurrentDirectory(new
		// File(localWorkspcae.getDefaultWorkspaceDirectory()));
		JDialog dialog = new JDialog();
		dialog.setTitle("VFRAP Movie");
		dialog.setSize(500,500);
		/*showMovieInDialog(dialog, "file:///C:/VirtualMicroscopy/test.mov",
				"C:/VirtualMicroscopy/test.mov");*/
	}
}
