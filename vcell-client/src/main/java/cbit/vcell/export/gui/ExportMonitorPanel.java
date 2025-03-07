/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gui;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.resource.PropertyLoader;
import org.vcell.util.gui.DefaultScrollTableActionManager;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Optional;
/**
 * Insert the type's description here.
 * Creation date: (4/4/2001 2:38:28 AM)
 * @author: Ion Moraru
 */
public class ExportMonitorPanel extends JPanel {
	private ScrollTable ivjScrollPaneTable = null;
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private ExportMonitorTableModel ivjExportMonitorTableModel1 = null;
	private boolean fieldHasJobs = false;
	private javax.swing.JMenuItem ivjJMenuItemCopyLocation = null;

	private JButton copyButton = null;
	private JButton helpButton = null;
	private JButton imagejButton = null;

	class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, ListSelectionListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ExportMonitorPanel.this.getScrollPaneTable() && (evt.getPropertyName().equals("model")))
				connPtoP1SetTarget();
			if (evt.getSource() == ExportMonitorPanel.this.getExportMonitorTableModel1() && (evt.getPropertyName().equals("minRowHeight")))
				connPtoP2SetTarget();
		};

		@Override
		public void actionPerformed(ActionEvent e) {
//			if (e.getSource() == ExportMonitorPanel.this.getJMenuItemCopyLocation()) {
//				JMenuItemCopyLocation_ActionPerformed(e);
//			} else
			if(e.getSource() == getCopyButton()) {
				CopyButton_ActionPerformed();
//			} else if(e.getSource() == getImagejButton()) {
//				ImagejButton_ActionPerformed();
			} else if(e.getSource() == getHelpButton()) {
				HelpButton_ActionPerformed();
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) {
				return;
			}
			if(e.getSource() == getScrollPaneTable().getSelectionModel()) {
				int row = getScrollPaneTable().getSelectedRow();
				getCopyButton().setEnabled(row == -1 ? false : true);
				//getImagejButton().setEnabled(row == -1 ? false : true);
			}
		}

	};

/**
 * ExportMonitorPanel constructor comment.
 */
public ExportMonitorPanel() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2001 12:06:18 PM)
 * @param resultSetID java.lang.String
 * @param event cbit.rmi.event.ExportEvent
 */
public void addExportEvent(ExportEvent event, String resultSetID) {
	setHasJobs(true);
	ExportMonitorTableModel etm = (ExportMonitorTableModel)getScrollPaneTable().getModel();
	int r = etm.addExportEvent(resultSetID, event);
	Enumeration<TableColumn> en = getScrollPaneTable().getColumnModel().getColumns();
	int c = 0;
	while (en.hasMoreElements()) {
		TableColumn column = (TableColumn)en.nextElement();
		column.setPreferredWidth(Math.max(column.getPreferredWidth(), (int)getScrollPaneTable().getCellRenderer(r, c).getTableCellRendererComponent(getScrollPaneTable(), getScrollPaneTable().getValueAt(r, c), false, false, r, c).getPreferredSize().getWidth()));
		c++;
	}
	getScrollPaneTable().setPreferredScrollableViewportSize(getScrollPaneTable().getPreferredSize());
}

/**
 * connPtoP1SetSource:  (ScrollPaneTable.model <--> ExportMonitorTableModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getExportMonitorTableModel1() != null)) {
				getScrollPaneTable().setModel(getExportMonitorTableModel1());
			}
			getScrollPaneTable().createDefaultColumnsFromModel();
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetTarget:  (ScrollPaneTable.model <--> ExportMonitorTableModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setExportMonitorTableModel1((cbit.vcell.export.gui.ExportMonitorTableModel)getScrollPaneTable().getModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetTarget:  (ExportMonitorTableModel1.minRowHeight <--> ScrollPaneTable.rowHeight)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if ((getExportMonitorTableModel1() != null)) {
			getScrollPaneTable().setRowHeight(getExportMonitorTableModel1().getMinRowHeight());
		}
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Return the ExportMonitorTableModel1 property value.
 * @return cbit.vcell.export.ExportMonitorTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ExportMonitorTableModel getExportMonitorTableModel1() {
	// user code begin {1}
	// user code end
	return ivjExportMonitorTableModel1;
}
/**
 * Gets the hasJobs property (boolean) value.
 * @return The hasJobs property value.
 * @see #setHasJobs
 */
public boolean getHasJobs() {
	return fieldHasJobs;
}

/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ScrollTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new ScrollTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			ivjScrollPaneTable.setModel(new ExportMonitorTableModel());
			ivjScrollPaneTable.setToolTipText("Select a row and press the 'Copy Link' button to copy the file location to Clipboard");
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			ivjScrollPaneTable.setScrollTableActionManager(new DefaultScrollTableActionManager(getScrollPaneTable()) {
				@Override
				protected void constructPopupMenu() {
					// uncomment to enable "Copy to clipboard" popup menu
//					if(popupMenu == null) {
//						//super.constructPopupMenu();
//						popupMenu = new JPopupMenu();
//						popupLabel = new javax.swing.JLabel();
//						popupLabel.setText(" Popup Menu");
//						popupMenu.insert(getJMenuItemCopyLocation(), 0);
//					}
//					popupMenu.show(ivjScrollPaneTable, ownerTable.getX(), ownerTable.getY());
				}

			});
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getScrollPaneTable().addPropertyChangeListener(ivjEventHandler);
	ListSelectionModel lsm = getScrollPaneTable().getSelectionModel();
	if(lsm instanceof DefaultListSelectionModel) {
		DefaultListSelectionModel dlsm = (DefaultListSelectionModel)lsm;
		dlsm.addListSelectionListener(ivjEventHandler);
	}

	getJMenuItemCopyLocation().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ExportMonitorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(638, 241);

		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 7;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(getScrollPaneTable().getEnclosingScrollPane(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.insets = new Insets(1, 6, 7, 4);
        add(getCopyButton(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.insets = new Insets(1, 6, 7, 4);
		add(getHelpButton(), gbc);

//		gbc = new java.awt.GridBagConstraints();
//		gbc.gridx = 2;
//		gbc.gridy = 2;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.anchor = GridBagConstraints.NORTHEAST;
//		gbc.insets = new Insets(1, 6, 7, 4);
//		add(getImagejButton(), gbc);

		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new javax.swing.JFrame();
		ExportMonitorPanel aExportMonitorPanel;
		aExportMonitorPanel = new ExportMonitorPanel();
		frame.setContentPane(aExportMonitorPanel);
		frame.setSize(aExportMonitorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Set the ExportMonitorTableModel1 to a new value.
 * @param newValue cbit.vcell.export.ExportMonitorTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setExportMonitorTableModel1(ExportMonitorTableModel newValue) {
	if (ivjExportMonitorTableModel1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjExportMonitorTableModel1 != null) {
				ivjExportMonitorTableModel1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjExportMonitorTableModel1 = newValue;

			/* Listen for events from the new object */
			if (ivjExportMonitorTableModel1 != null) {
				ivjExportMonitorTableModel1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connPtoP2SetTarget();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

	private JMenuItem getJMenuItemCopyLocation() {
		if (ivjJMenuItemCopyLocation == null) {
			try {
				ivjJMenuItemCopyLocation = new javax.swing.JMenuItem();
				ivjJMenuItemCopyLocation.setName("JMenuItemCopyLocation");
				ivjJMenuItemCopyLocation.setText("Copy Location");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJMenuItemCopyLocation;
	}
	private void JMenuItemCopyLocation_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
//		int[] rows = getScrollPaneTable().getSelectedRows();
//		String str = (String)getScrollPaneTable().getModel().getValueAt(rows[0], 4);
//		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//		StringSelection stringSelection = new StringSelection(str);
//		clipboard.setContents(stringSelection, null);
	}

	private JButton getCopyButton() {
		if(copyButton == null) {
			copyButton = new JButton("Copy Link");
			copyButton.setName("CopyButton");
			copyButton.setToolTipText("Select a table row and press this button to copy the file location to Clipboard");
			copyButton.addActionListener(ivjEventHandler);
			copyButton.setEnabled(false);
		}
		return copyButton;
	}
	private JButton getHelpButton() {
		if(helpButton == null) {
			helpButton = new JButton("Help");
			helpButton.setName("HelpButton");
			helpButton.setToolTipText("ImageJ N5 format export help");
			helpButton.addActionListener(ivjEventHandler);
			helpButton.setEnabled(true);
		}
		return helpButton;
	}
	private JButton getImagejButton() {
		if(imagejButton == null) {
			imagejButton = new JButton("Launch Imagej");
			imagejButton.setName("ImagejButton");
			imagejButton.addActionListener(ivjEventHandler);
			String command = PropertyLoader.getProperty(PropertyLoader.imageJ, "");
			if(command == null || command.isEmpty()) {
				imagejButton.setEnabled(false);
			} else {
				File imageJExe = new File(command);
				if(imageJExe.exists() && !imageJExe.isDirectory() && imageJExe.canExecute()) {
					imagejButton.setEnabled(true);
				} else {
					imagejButton.setEnabled(false);
				}
			}
		}
		return imagejButton;
	}

	private void CopyButton_ActionPerformed() {
		int[] rows = getScrollPaneTable().getSelectedRows();
		String str = (String)getScrollPaneTable().getModel().getValueAt(rows[0], 3);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection stringSelection = new StringSelection(str);
		clipboard.setContents(stringSelection, null);
	}
	private void HelpButton_ActionPerformed() {
		String strFiji = "The simulation results of spatial applications may be exported to ImageJ for further ";
		strFiji += "processing using the compatible N5 format.<br>";
		strFiji += "Use the 'Copy Link' button above to copy the exported data location to the clipboard.<br>";
		strFiji += "The Fiji ImageJ application can be downloaded from the ";
		strFiji += "<a href='https://imagej.net/software/fiji/'>Fiji ImageJ website</a>";
		JEditorPane jepFiji = new JEditorPane("text/html", strFiji);
		jepFiji.setEditable(false);
		jepFiji.setOpaque(false);
		jepFiji.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent hle) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
					URL url = hle.getURL();
					System.out.println(url);
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(url.toURI());
						} catch(IOException | URISyntaxException e) {
							System.out.println(e.getMessage());
						}
					}
				}
			}
		});
		String strPlugin = "Use the VCell plugin in ImageJ to download the exported file for further processing.<br>";
		strPlugin += "Click <a href='https://sites.imagej.net/VCell-Simulations-Result-Viewer/'>here</a> to copy the ";
		strPlugin += "VCell plugin to the Clipboard";

		JEditorPane jepPlugin = new JEditorPane("text/html", strPlugin);
		jepPlugin.setEditable(false);
		jepPlugin.setOpaque(false);
		jepPlugin.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent hle) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
					URL url = hle.getURL();
					System.out.println(url);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					StringSelection stringSelection = new StringSelection(url.toString());
					clipboard.setContents(stringSelection, null);
				}
			}
		});

		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(1, 1, 5, 1);
		p.add(jepFiji, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(1, 1, 1, 1);
		p.add(jepPlugin, gbc);

		DialogUtils.showInfoDialog(ExportMonitorPanel.this, p, "ImageJ Export Help");

	}
	private void ImagejButton_ActionPerformed() {
		try {
//			String command = Paths.get(System.getenv("windir"), "system32", "tree.com /A").toString();
			String command = PropertyLoader.getProperty(PropertyLoader.imageJ, "");
			if(command == null || command.isEmpty()) {
				System.out.println("Property 'vcell.imageJ' not set");
				return;
			}
			ProcessHandle.allProcesses().forEach(process -> {
				Optional<String> proc = process.info().command();
				System.out.println(proc);
				if(proc.toString().toLowerCase().contains("imagej")) {
					System.out.println(proc);
					//DialogUtils.showInfoDialog(ExportMonitorPanel.this, "Information", "ImageJ already running.");
				}
			});
			Process p = Runtime.getRuntime().exec(command);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
/**
 * Sets the hasJobs property (boolean) value.
 * @param hasJobs The new value for the property.
 * @see #getHasJobs
 */
private void setHasJobs(boolean hasJobs) {
	boolean oldValue = fieldHasJobs;
	fieldHasJobs = hasJobs;
	firePropertyChange("hasJobs", oldValue, hasJobs);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC4FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBBEBF49467F5EA02D3CECB9A9263D4F6541858A4D1925946B129B9B6BF308F7141C1C9310AEB6A0A93B5F09A12C3DBB521AD66B876E8A5EC898B030DF065E3E28B9B0CB0C216108C32G23478A34E8A5AD2B556A0192B45A9D6D0E34BB331A9DA1713053FB3F476CE819519AEAD7675CB37B5D6FF55F5F3D5F0C04323796EEABF01489C241B6017D9DAF90044AFAC1F8D53D73E50EEBB818D3A2187E76
	813CA2BC98AA02E7BB744D9D4CA959A57CE7FFB1340720DD846D7F015F3B052D7D99C761074DBEBA745D5A11D1B37BBEF96DFC1F97C93BF72C941EC85CB0607019F590770F0E15B37C9C0667D060GDCA50E19BB36176172E1CCB1601AGFC15F4FC841E1400DB9A9B5838212FE68A1BFF22B9F660BA26B5B9E43339796CD9A1ECADC8BF04FB5DCF6844B915509E86C0668B0507065E0367C94FF8682340082CD7DF8AC811C835B49DD3B53D31310DBCBBD5C556D54DA3AAD28444779E2E774CD8B119AF88393056
	C202DB9341BD7A61F78CE06EFB9CAF44903F81FEBF8BD0F61873F56A60C9C6F77F558705B5C723C5BBED782ABACC79719DCE701596DD67F81D6820629C4AFABE74AD81D88930892034AA27248A608D227B03AE6433F9C4EFD523D1D5E90E45DAA2C12DD38DCA91785D5888BD8C6715822A9694842A5F87472AAC68198D683C9F5F70BCAA134CBA2E6BAC6F3DC26E83DFF73CE8A1135C4C2B8EE7951545D0D5C2A6F46F67EC773ED6B55396E76E5DA71877AE5A718F055BAC762E587AE461FC364EA2535E39D5286B
	76A86AFA857CAEE03220F89F41D79CC96029DDBECDEC865B8D5017F1045BC67F4B1C96F751DD422E5A3F3614C37A23739CC2B525213CBA19961D581D331A6ED9E25072E784BFEC42D3DADEA0F8ECDF033EBCGEADF075C68274E7741EFG26816683AC86488648FB1F5BD84C757E2734B10F2869AD13F2A4A8E9821DFB685CEB701426F5CDF4A9F1DDD48212DF54E4F1A4A24585B66668B91441480CF947096D87E00F3312A2E922AE2B8A2CAEC6A5DD5262F4CC5339D7F00CA611DE1F9C15E003E8CC606B361F5B
	8D4F089857FBE3C1D11792A3E83F679C4A6412680795E18178CD65626FC4F989B5B9A565G15B5DC8E2B48FA9D1286712231512D2A6313B19FF2A2CC57201C770CE36CB8893FFDB5DC465EAE44AD03769AE39DC7472B26F51CF2G4997354B5C473A08CDED04714535606B355CF67E2D1F5B0EF347BDC2551337D95A4E82306321DAEAB3FAED328F8D1235113E8C68DBD64B63FDDE245F46370AEBE77A9652D5D5EB6DEF33470EE7EC77581654AEE4F3EEE48FAA6F655D48DFAE503785207CA81779290E8AEA43AD
	E2DC62D65B2D0141C9410486A81DD7E23B18DC8B99EDA5868FFFCE7AEAE05DFE00B4004C23DC0625317E24F9BC865D889F334B70G59032F7F62E73C8715D51D9B4FAB69122468CEA9A6A9C17891E7FD0DEE5C0B797328EA4CE9A2789878FFABB8DC5C401FF1230F0A3A2E49A3133A54A3693A2C04927DDDE42F7864C8AC91BA78DE3DEE64D11D827FB7468F3A51F7822A22C8C4E9469456BFC4FCD70AC8D1A03AD51DD4F40A9F69447CE6DC3A4CE247E8678B827AFEE49263904D99960BAFA18F1A9C85D340D0A6
	D45421DFD70D171BE4349A740D394FE6F89657F15F1CFA077B667EAB5F9316DF0CB92C4E1874EFFF45912E233EF32DAE611B14263EAEEB5A7FC0E2884347A92DFF3F9765E015479412B9274365497DA4D41128DDCB475CF798F3BF27B4AACEC6F4525B8AC91297952EC9DA1B9246E89D2CB5FC6B998F521781F4AF86D8D947FD6B9F3B301F47B366D42DEAA4C2D584FD7575A847BB23E5CC8EF9B0F7CB9D15A33633C97F9E254CA42BEB7534FFE9BD5F6731EE743D38B4B1A981E1C8EF07214F1FF63F1A5467E73A
	257DCFF623EEA326183D66835C7361984FD54BBE207BD54153899086D88EB0F48C477D77849FD7F10C76B79C23B1548D4FB781CF47BD2271F1192CFFB99B3F160D2BBC0E63EE0D9BB9B260B11E949F2771389C1E558237495F9EC2BD12BCF6CC3CA4CED7472740FF9A9B4F91ED531C756B645C41717AF11E1BBE2A202E32203D82A077B83753A7F6705CF441F82610376349C23B1CFB73F116FFB418720F7B268A29AD34FAF14FFE688BB7F07DF8BC667331B99ED783B2B91A198E1E77186D18620A4EEF0767282C
	45F597585DB457577673C8AF1E5D8C4DF2194722BC5F3F867BAEE960797E6BA4C779D479FE428FD8FE982863B219FF655B4270EDF7B8DE321149A6A60B528F93B249BA72C1A125618B7DB3E988CD4AF03C27F0AFD6F738952EC36707DCD79F1FB16BEA594E0FED693A466849B0513562D77FD5C8E5360164FF38F6AE7417B56614D07F6F18C03C8CC2114508FCC52ABCC4631DBFE40EF7EE989F83D8G10594865FD545DC17CCDE77CF136AA5DE8265591D189D5131360045BE43755BA0A217ABAE11CB864E7F2C8
	1B60755772C6AE07FB5EECE234E45FFB17D0AC5E62302A27667DAA6458524878BFE13259B56B9C79GD49695DAA9DBBF8CE3961E6032F8AD0CFCC509D570987A838D4F8CBDAC47A99D3FE9B653317445F916F4043F1B6ED84B7697CEA66898DE3F4E212F67F47C5A204383E3661E64F41C0FA3BEAAABDEF52ADD12C3E11D622F929AB40EB47CE1D91C2F339C5658E22C730E0E340BDA68A85335BE1317EB831C17346CA5C2796EAFDB7202D0F30A7250FFAA41CB66AF56B91681501C649B6349F5D96629C4FE386C
	547539A22DD70E29C87B6AD334DEA9B470CD841FE742D3DED7AA1C57FC6873FD44F36C52DE34572550DE8330892068F4CE49FEG67E99E1B3EF63ECEC8D1433894A8AD94B152B3A3996135049777057C01355DDE349712BF88BCB74B0E5EDCCD7D748D4F23FCCF78713C9C869E2F81ACG488248B6F8FE42DB150AE7DE0BB1E6A9BD6795ABFAB61DE6B65DF43DBEA9BDCF75A1BDE550DF83508EB08C10564469F923A7258E5A26ADF5C0697AF5AC192E05C934ACCBEA53F9DB2E1BD71D544ECB1AC7E35C58D51B33
	1744AF9AAB27D4F32C5C52C4EB3F7D4D109FG501876D721E2C60B27191F4F7B487D40DCEC83ACED66B544AD93C86B6A66CFDFDFD1599F9940FCE2834C93DAA007G28876887508152DB388E641E93A9F500093FCFEDA649DBEBF2264DE47A2E164AE62DF5D11FF25E52161973A8FF0B86113F555037892028B52724824089B08CB027157377F0CFCBAA7E1AF9B182BC721F573349684D3FC9BE1FCBB9AF2355EA5E23711B5BEFF54AF9CBDA2D647A76904AB59B7AB682884E1C123DGCDGC3GE9CEAE571D3E14
	F2B52EE7E617AB3DAB79A149D953E17DACG0A7BCA94F12B213D51497D61240A67D6E99BEF1FA1ED4CC7E87BE70A5D1D420DG2EEF0527FB7C72B359235086CF653B27573E665E5BEBDDF33F5DEBDDF3D7763EC271300679C60E7757744EDE13B7745A5764177D76F426792D697C125F1A4EF97E5969DC603723135ACB18644C7BC1F775ED5C06FE2AF09BEAEA63367B73BEFB1A7F254F1A66CDFD56B47F26EFF61A377659517CFBE2830F3F36134C0F4725B88934CED197D95FAF75BD02455DC697F81540F352FD
	93A5ECCC1B2EF6E2EDCE4F2AD09B2EE1F1D67584B4284431DA9478BB0E7BD4FE9E855B78F9742DD1D25BC3FB89C03661EF3706B02EEC0076968B9FA9BDE3175B5DC64E370AB3B407ABB0705FA478AA931E66F687A33C2EEB003E0CB3DC1F99249E904E426F33DC1F47876C7579510035BE1D83D8674655C9ADA0A56B2CE3205C68CB84AC17AEC5D174F40C0F4C57A121162E537985B21F76594D8F8E585985556B9FB42ED70F2EFAEA0D356F9D346775C703563CBEBCE84F6B230329F9DDB5E84FEB4EE0EADE7FE9
	906B5491B9A8EB6C860872F8B0EE667128417BB37117B8FED4D32340BDDD27EA481E773AA1EB5EBF9C32673DE9A8B56F2EA1FB5EBDC329F99F9832667D71F13CEF0C081755C9A85E6621BD8BF9463DE285349BG06817433B40724FEFD7F942EB78AA7AB18CBCFCC8A8861F61C7B1DA81F3B8646EF84A886FCA5C0FBBB2F816BC8FDEF0AG6C5E6FCF8A2985C1F80B5B71DD70F8GDD0C2E3BC1616BEE06F6318B5684E887883BF8CCD82272DCE3AE60B2DCB3E340E217DDCC5893A26F50DC49755EDB843F4615DC
	6FFDF71457FB9B1006F3BCA6FC1F6453D950DEE9601EA7B83C134BB5F01F487CAC90BAB86D5F40721E1D6B0BCC3F0F583E1B78AC0065AE1F6B9ED4378FCC303A3F76E4874A60F1525E8F3F29CC0B0DB81B8638259D5C4EEA07B18FB44E0FEAF1CAEFECBC2B494196B164A6D6CB4F8ECF24184D4F6F60E747A7E44DCAB71C39G7DEEFEEFB37DAE3F3F4A7A4EDD421A926B3B46BC5F470E181B5A4C82F7620EE1C1F41D036E199D65FB2E067E62CE44ED0D16A5713E3E0056F5343FEA46DDB2465EC302397F0FB37A
	115E5F95197B674F685FE61ACFF9DBF50073766C2A45C264C7F3EC3FDD70F432F7F16CB9FF436D85694FDFA9246B7C5D8573B7903B6E08B88AEC5649E27317F6A6E4C46533DD6572595449655320A64BE75F8C7E4754E479FC4BE16E4FD01365733D99727D191AAC1F93ECFD6AFB2B88CD7B3B40EF3B38BFBE9DA35F2EC0FB0E017BE0027BE8FA977751B748B7AF9B0D76815246BBF45A3EE7546CC7B86E33775547E66CE1173B7F5FFC7511907E96A7F555AB4565AB64CE9AFC301B480A44B314FB913469F9F1A4
	4CE3F3DAB7FDFF128E4F2CEE763E02446F5BE4BEE685605762B800F28F1CAF9EFEAE7C1C1C8B5DA3E3F0C2D587B03DE55F2DC8E6DB2C396AF6945A58E23F074ABC4C1E4F6E39033DDFFEB9F2F3F7E0FBEFF2DE6F8D4F23E70E073CC7C877A4672783843F48131C1F4EB16266E268ABBB4F6F9EC3924AFB9A5A8BG32GD6GECG903CBCDF7DE1GBF3E1A35F67629B1B7A4BA91D3C9C179FB62066F8629AE9FF9114727A58FFDDFD87AA5276D3B03B2AF55E1BDFBD62E3F3DG1F143F2D2348DF907A52G9681AC
	83D88D305160EFEB005DB33685E53CDD9183E172064738E4142529849A13983A766F43C8A94B4D02876069974718F4F0A9D563AB1C03A3095A2BCF8C993A5F295E28EC287D7FC2456F66AEC91AAE874488CD0FDAC44D23C66440E566FF3FA3718BE37E33DE6EB77F91C33A432AA6DFD1957D7A195CF7F71018F68A5BBDD0CF751077BB934ED9686236360D511886F396G6CE16D4C1E04BDD2DA34A8664609540E3E53E4F406701BB073EB97FAE3D95D5C6B6B3E68ED6BF18D36DDECF63BAF36F63BFBBB3BFA384D
	AC1CB27BFF4376AFD83E43A67E5EC3EDE517AF617F54CFFA43BCE7EB073EB49F3F837D1BB0BF23167AF85E300A0C4D07F6F1EFCEC9C5AF9F7B304C5F05B57472736C37F272F97655996751013164738C77CA74770DA51FE7B3677F7BF872F94667D3DB6D12493DD3AF3779B941376D6C95102D605BBDD06AA9A1EE2B7E28E0586DC5536F3F78FF48BB6F7EDC77E05F08C976355A14E4DD2BFDACD95F31FCD11A7DFE6AAFA57B7B292FC966EFC6286EC4227BB9203B05GABG5682ECB2F4D9B10A3A24B74349312B
	AD9842DA913FDC53797E05A1FBDE3707D2FF6F36BB046F3CC7D4DDD72338BE473F9EC2FD69EA0CA0A96E4D90F96F076F7C68D0065FCF720308B4CA50546FFF922B97127CBE91A02A7DEDDE1F2B3559FD3127555B6DF637B4FBAFB677DC6CEAEBF336B9697C7D9347E6195F5E6DF58DF6F7792CD7900437489A60CF827F4BBFED0523B5676B247622F1671FCB79D9357787F78B4E271C0EF7846B38D36CE76FA27D099CF86DC4857BA6E187299366C3DF161F6B764E7061D97C542329B1B886AED3AC79E204794C05
	F02A33A55BEFF58FAF271C7726250F5E15F2DEB250FC6534CC3E9574A767B16D843F591F1C47BC9166F90C5017D3926C63B975F2593C7FCA4067764D1ED7D379FAE53C570833D8475EB10D597BC7ECAC35FFDCA65FE0CBE0573E4BB1F62F7249986E230E0E46A5438F3FB8364B4039629EB5AE63058B3585170267EDBE70D05A1FD3D283508EB08420834C8548GD88C3092A08FE0B3C071C0CE49DE00AA00A6G5FG3723BFC68E4DE2C72CD0A601C293E58C950C46D723A8ABA316D193637ECEEAD8637B8B7229
	8CBDB568FD70725DBBC8FFC4D624D67AA52CE1AFED32E07C6EB27DFEAA7C791FA7791F6B9E34AEFC0A64GE110BF1E539417CBF0E98C473E0DD36CEDAD2824363528E29D0B634A7531F8DA310E451FA866D84CECD341F3A884F651EF0C1BEB60868C5C17491A086B1303FA58405FCA68C2BC7F600362EFA7F4CD91DCBF43FD53049BE0383B957497E09952D5BEB7135C7B0D4A1188EDE711B132826EE34863FEC2239C93CDF47F2D01C33AE90EAA3EEB7AF60810CE71FB27F7A4704637AA747FD47AC7AF41B36BDF
	05ED3A30AF65E707F1CD58492F222ADD04483CCBF50D056B6FAEF488C5AE61852AB417D00412F7895B08E2DCC2A1A8ED9ABF82CF7A23E74D21BF19F35CAD3784ECF35C6CC19A7F728693E78D0DAD7712DCF66F852829AF707870DC6808E0DF3F74C445C884AA0671E6A50835CC4BE5DDCA64B38F04F17DB89F43BAE95FC3E124FB4CB467FF81D0CB8788AF9844C8D492GGD0B4GGD0CB818294G94G88G88GC4FBB0B6AF9844C8D492GGD0B4GG8CGGGGGGGGGGGGGGGGGE2F5E9EC
	E4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0E92GGGG
**end of data**/
}
}
