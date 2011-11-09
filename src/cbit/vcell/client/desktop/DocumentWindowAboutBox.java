/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.KeySequenceListener;
import org.vcell.util.gui.VCellIcons;

@SuppressWarnings("serial")
public class DocumentWindowAboutBox extends JDialog {

	private static final String COPASI_WEB_URL = "http://www.copasi.org";
	private static final String VCELL_WEB_URL = "http://www.vcell.org";
	private JLabel appName = null;
	private JPanel buttonPane = null;
	private JLabel copyright = null;
	private EventHandler eventHandler = new EventHandler();
	private JLabel iconLabel = null;
	private JPanel iconPane = null;
	private JPanel dialogContentPane = null;
	private JButton okButton = null;
	private JPanel textPane = null;
	private JLabel version = null;
	private static String VERSION_NO = "";
	private static String BUILD_NO = "";
	private static String EDITION = "";
	private JLabel buildNumber = null;

	public static void parseVCellVersion() {
		try {
			VCellSoftwareVersion vcellSoftwareVersion = VCellSoftwareVersion.fromSystemProperty();
			EDITION = vcellSoftwareVersion.getSite().name().toUpperCase();
			VERSION_NO = vcellSoftwareVersion.getVersionNumber();
			BUILD_NO = vcellSoftwareVersion.getBuildNumber();
		} catch (Exception exc) {
			System.out.println("Failed to parse vcell.softwareVersion: " + exc.getMessage());
			exc.printStackTrace(System.out);
		}
	}
	
	public static String getVERSION_NO() {
		return VERSION_NO;
	}

	public static String getBUILD_NO() {
		return BUILD_NO;
	}

	public static String getEDITION() {
		return EDITION;
	}
	
	private class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == DocumentWindowAboutBox.this.getOkButton())
				try {
					DocumentWindowAboutBox.this.dispose();
				} catch (Throwable throwable) {
					handleException(throwable);
				}
		};
	};

	public static class HierarchyPrinter extends KeySequenceListener {
		
		public String getSequence() { return "hierarchy"; }
		
		public void sequenceTyped() {
			for(Frame frame : Frame.getFrames()) {
				printHierarchy(frame, "");
			}
		}
		
		public void printHierarchy(Component component, String indentation) {
			System.out.println(indentation + component);
			if(component instanceof Container) {
				for(Component child : ((Container) component).getComponents()) {
					printHierarchy(child, indentation + "  ");
				}
				
			}
		}
		
	}
	
	public DocumentWindowAboutBox() {
		super();
		initialize();
		setFocusable(true);
		addKeyListener(new HierarchyPrinter());
	}

	private JLabel getAppName() {
		if (appName == null) {
			try {
				appName = new JLabel();
				appName.setName("AppName");
				appName.setText("<html><u>Virtual Cell</u></html>");
				appName.setForeground(Color.blue);
				appName.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						DialogUtils.browserLauncher(DocumentWindowAboutBox.this, VCELL_WEB_URL, "Failed to open VCell web page (" + VCELL_WEB_URL + ")", false);
					}
				});
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return appName;
	}

	public JLabel getBuildNumber() {
		if (buildNumber == null) {
			try {
				buildNumber = new JLabel();
				buildNumber.setName("BuildNumber");
				buildNumber.setText("");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return buildNumber;
	}

	private JPanel getButtonPane() {
		if (buttonPane == null) {
			try {
				buttonPane = new JPanel();
				buttonPane.setName("ButtonPane");
				buttonPane.setLayout(new FlowLayout());
				getButtonPane().add(getOkButton(), getOkButton().getName());
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return buttonPane;
	}

	private JLabel getCopyright() {
		if (copyright == null) {
			try {
				copyright = new JLabel();
				copyright.setName("Copyright");
				copyright.setText("(c) Copyright 1998-2011 UCHC");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return copyright;
	}

	private JLabel getIconLabel() {
		if (iconLabel == null) {
			try {
				iconLabel = new JLabel();
				iconLabel.setName("IconLabel");
				iconLabel.setIcon(new ImageIcon(getClass().getResource("/images/ccam_sm_colorgr.gif")));
				iconLabel.setText("");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return iconLabel;
	}

	private JPanel getIconPane() {
		if (iconPane == null) {
			try {
				iconPane = new JPanel();
				iconPane.setName("IconPane");
				iconPane.setLayout(new FlowLayout());
				getIconPane().add(getIconLabel(), getIconLabel().getName());
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return iconPane;
	}

	private JPanel getJDialogContentPane() {
		if (dialogContentPane == null) {
			try {
				dialogContentPane = new JPanel();
				dialogContentPane.setName("JDialogContentPane");
				dialogContentPane.setLayout(new BorderLayout());
				getJDialogContentPane().add(getButtonPane(), "South");
				getJDialogContentPane().add(getTextPane(), "Center");
				getJDialogContentPane().add(getIconPane(), "West");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return dialogContentPane;
	}

	private JButton getOkButton() {
		if (okButton == null) {
			try {
				okButton = new JButton();
				okButton.setName("OkButton");
				okButton.setText("OK");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return okButton;
	}

	private JPanel getTextPane() {
		if (textPane == null) {
			try {
				textPane = new JPanel();
				textPane.setName("TextPane");
				textPane.setLayout(getTextPaneGridLayout());
				getTextPane().add(Box.createRigidArea(new Dimension(5,10)));
				getTextPane().add(getAppName(), getAppName().getName());
				getTextPane().add(getVersion(), getVersion().getName());
				getTextPane().add(getBuildNumber(), getBuildNumber().getName());
				getTextPane().add(getCopyright(), getCopyright().getName());
				//getTextPane().add(getUserName(), getUserName().getName());
				getTextPane().add(Box.createRigidArea(new Dimension(5,10)));
				getTextPane().add(getCOPASIAttribution(),getCOPASIAttribution().getName());
				getTextPane().add(Box.createRigidArea(new Dimension(5,10)));
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return textPane;
	}

	private GridLayout getTextPaneGridLayout() {
		GridLayout textPaneGridLayout = null;
		try {
			/* Create part */
			textPaneGridLayout = new GridLayout(0, 1);
		} catch (Throwable throwable) {
			handleException(throwable);
		};
		return textPaneGridLayout;
	}
	
	private JLabel getCOPASIAttribution() {
		JLabel copasiText = new JLabel();
			try {
				
				copasiText.setName("COPASI");
				copasiText.setText("<html>Featuring <font color=blue><u>COPASI</u></font> parameter estimation technology&nbsp;&nbsp;</html>");
				copasiText.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						DialogUtils.browserLauncher(DocumentWindowAboutBox.this, COPASI_WEB_URL, "Failed to open COPASI webpage ("+COPASI_WEB_URL+")", false);
					}
				});
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		
		return copasiText;
	}
	
	public JLabel getVersion() {
		if (version == null) {
			try {
				version = new JLabel();
				version.setName("Version");
				version.setText("Version 4.0");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return version;
	}

	private void handleException(Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}

	private void initialize() {
		try {
			setName("DocumentWindowAboutBox");
			setIconImage(VCellIcons.getJFrameImageIcon());
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setResizable(false);
			setTitle("DocumentWindowAboutBox");
			setContentPane(getJDialogContentPane());
			getOkButton().addActionListener(eventHandler);
			pack();
		} catch (Throwable throwable) {
			handleException(throwable);
		}
	}

	public static void main(String[] args) {
		try {
			DocumentWindowAboutBox aDocumentWindowAboutBox;
			aDocumentWindowAboutBox = new DocumentWindowAboutBox();
			aDocumentWindowAboutBox.setModal(true);
			aDocumentWindowAboutBox.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				};
			});
			aDocumentWindowAboutBox.setVisible(true);
			Insets insets = aDocumentWindowAboutBox.getInsets();
			aDocumentWindowAboutBox.setSize(aDocumentWindowAboutBox.getWidth() + insets.left + insets.right, aDocumentWindowAboutBox.getHeight() + insets.top + insets.bottom);
			aDocumentWindowAboutBox.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of JDialog");
			exception.printStackTrace(System.out);
		}
	}
}