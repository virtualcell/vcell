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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.gui.DialogUtils;

@SuppressWarnings("serial")
public class DocumentWindowAboutBox extends JPanel {

	private static final String COPASI_WEB_URL = "http://www.copasi.org";
	private static final String SMOLDYN_WEB_URL = "http://www.smoldyn.org";
	private static final String BIONETGEN_WEB_URL = "http://bionetgen.org";
	private static final String NFSIM_WEB_URL = "http://emonet.biology.yale.edu/nfsim/";
	private static final String ACKNOWLEGE_PUB__WEB_URL = "http://vcell.org/vcell_models/how_submit_publication.html";
	private static final String VCELL_WEB_URL = "http://www.vcell.org";
	private JLabel appName = null;
	private JLabel copyright = null;
	private JLabel iconLabel = null;
	private JLabel version = null;
	private static String VERSION_NO = "";
	private static String BUILD_NO = "";
	private static String EDITION = "";
	private JLabel buildNumber = null;
	private JLabel jarch;

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

	public DocumentWindowAboutBox(String vers, String build) {
		setLayout(new GridBagLayout());

		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(0,0,4,4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(getIconLabel(), gbc);

		gbc = attributionConstraints(2,gridy);
		add(getAppName(), gbc);

		gbc = attributionConstraints(0,++gridy);
		add(getVersion(vers), gbc);

		gbc = attributionConstraints(0,++gridy);
		add(getJavaArch(),gbc);

		gbc = attributionConstraints(0,++gridy);
		add(getBuildNumber(build), gbc);

		gbc = attributionConstraints(0,++gridy);
		add(getCopyright(), gbc);

		gbc = attributionConstraints(10,++gridy);
		add(getCOPASIAttribution(), gbc);

		gbc = attributionConstraints(10,++gridy);
		add(getSmoldynAttribution(), gbc);

		gbc = attributionConstraints(10,++gridy);
		add(getBioNetGenAttribution(), gbc);

		gbc = attributionConstraints(10,++gridy);
		add(getNFsimAttribution(),gbc);

		gbc = attributionConstraints(10,++gridy);
		add(getAcknowledgePubAttribution(), gbc);

		gbc = attributionConstraints(10,++gridy);
		add(new JLabel("<html>Virtual Cell is Supported by NIH Grant P41 GM103313 from the<br/> National Institute for General Medical Sciences.</html>"), gbc);

		setFocusable(true);
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
						DialogUtils.browserLauncher(DocumentWindowAboutBox.this, VCELL_WEB_URL, "Failed to open VCell web page (" + VCELL_WEB_URL + ")");
					}
				});
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return appName;
	}

	private JLabel getBuildNumber(String build) {
		if (buildNumber == null) {
			try {
				buildNumber = new JLabel(build);
				buildNumber.setName("BuildNumber");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return buildNumber;
	}


	private JLabel getJavaArch() {
		if (jarch == null) {
			String desc = System.getProperty("java.version") + " " + System.getProperty("os.name")+ " " + System.getProperty("os.arch");
			jarch = new JLabel(desc);
		}
		return jarch;
	}

	private JLabel getCopyright() {
		if (copyright == null) {
			try {
				copyright = new JLabel();
				copyright.setName("Copyright");
				copyright.setText("(c) Copyright 1998-2016 UConn Health");
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
//				iconLabel.seth
				iconLabel.setIcon(new ImageIcon(getClass().getResource("/images/ccam_sm_colorgr.gif")));
				iconLabel.setText("");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return iconLabel;
	}

	private JLabel getCOPASIAttribution() {
		JLabel copasiText = new JLabel();
			try {

				copasiText.setName("COPASI");
				copasiText.setText("<html>Featuring <font color=blue><u>COPASI</u></font> parameter estimation technology&nbsp;&nbsp;</html>");
				copasiText.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						DialogUtils.browserLauncher(DocumentWindowAboutBox.this, COPASI_WEB_URL, "Failed to open COPASI webpage ("+COPASI_WEB_URL+")");
					}
				});
			} catch (Throwable throwable) {
				handleException(throwable);
			}

		return copasiText;
	}

	private JLabel getSmoldynAttribution() {
		JLabel smoldynText = new JLabel();
			try {

				smoldynText.setName("SMOLDYN");
				smoldynText.setText("<html>Featuring spatial stochastic simulation powered by <font color=blue><u>SMOLDYN</u></font></html>");
				smoldynText.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						DialogUtils.browserLauncher(DocumentWindowAboutBox.this, SMOLDYN_WEB_URL, "Failed to open SMOLDYN webpage ("+SMOLDYN_WEB_URL+")");
					}
				});
			} catch (Throwable throwable) {
				handleException(throwable);
			}

		return smoldynText;
	}


	private JLabel getBioNetGenAttribution() {
		JLabel bioNetGenText = new JLabel();
			try {

				bioNetGenText.setName("BioNetGen");
				bioNetGenText.setText("<html>Featuring rule-based simulation powered by <font color=blue><u>BioNetGen</u></font></html>");
				bioNetGenText.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						DialogUtils.browserLauncher(DocumentWindowAboutBox.this, BIONETGEN_WEB_URL, "Failed to open BioNetGen webpage ("+BIONETGEN_WEB_URL+")");
					}
				});
			} catch (Throwable throwable) {
				handleException(throwable);
			}

		return bioNetGenText;
	}

	private JLabel getNFsimAttribution() {
		JLabel bioNetGenText = new JLabel();
			try {

				bioNetGenText.setName("NFsim");
				bioNetGenText.setText("<html>Featuring network free stochastic simulation powered by <font color=blue><u>NFsim</u></font></html>");
				bioNetGenText.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						DialogUtils.browserLauncher(DocumentWindowAboutBox.this, NFSIM_WEB_URL, "Failed to open NFsim webpage ("+NFSIM_WEB_URL+")");
					}
				});
			} catch (Throwable throwable) {
				handleException(throwable);
			}

		return bioNetGenText;
	}
	private JLabel getAcknowledgePubAttribution() {
		JLabel ackPubJlabel = new JLabel();
			try {

				ackPubJlabel.setName("AcknowledgePub");
				ackPubJlabel.setText("<html>Use <font color=blue><u>this link</u></font> for details on how to acknowledge Virtual Cell in your publication<br>and how to share your published research through the VCell database. </html>");
				ackPubJlabel.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						DialogUtils.browserLauncher(DocumentWindowAboutBox.this, ACKNOWLEGE_PUB__WEB_URL, "Failed to open BioNetGen webpage ("+ACKNOWLEGE_PUB__WEB_URL+")");
					}
				});
			} catch (Throwable throwable) {
				handleException(throwable);
			}

		return ackPubJlabel;
	}

	private JLabel getVersion(String vers) {
		if (version == null) {
			try {
				version = new JLabel(vers);
				version.setName("Version");
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

	/**
	 * common constraints for elements
	 * @param topOffset top offset for {@link Insets}
	 * @param gridy {@link GridBagConstraints#gridy} value
	 * @return new object
	 */
	private GridBagConstraints attributionConstraints(int topOffset, int gridy) {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(topOffset,4,0,4);
			gbc.anchor = GridBagConstraints.LINE_START;
			return gbc;
	}
}