package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.Version;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.JLabelLikeTextField;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.xml.gui.MiriamTreeModel;
import cbit.vcell.xml.gui.MiriamTreeModel.LinkNode;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2003 2:07:01 PM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class BioModelPropertiesPanel extends JPanel {
	
	private BioModel bioModel = null;
	private EventHandler eventHandler = new EventHandler();
	private JLabelLikeTextField nameLabel, ownerLabel, lastModifiedLabel, permissionLabel;
	private JButton changePermissionButton;
	private BioModelWindowManager bioModelWindowManager;
//	private JTree applicationsTree = null;
//	private DefaultTreeModel applicationTreeModel = null;
//	private BioModelNode applicationTreeRootNode = null;
	private JPanel webLinksPanel = null;

	private class EventHandler implements ActionListener, DatabaseListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == changePermissionButton) {
				changePermissions();
			}			
		}
		public void databaseDelete(DatabaseEvent event) {			
		}
		public void databaseInsert(DatabaseEvent event) {
		}
		public void databaseRefresh(DatabaseEvent event) {
		}
		public void databaseUpdate(DatabaseEvent event) {
			updateInterface();			
		}
	}

/**
 * EditSpeciesDialog constructor comment.
 */
public BioModelPropertiesPanel() {
	super();
	initialize();
}

public void changePermissions() {
	if (bioModel == null || bioModel.getVersion() == null) {
		return;
	}
	bioModelWindowManager.getRequestManager().accessPermissions(this, bioModel);	
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
 * Initialize the class.
 */
private void initialize() {
	try {		
		nameLabel = new JLabelLikeTextField();
		ownerLabel = new JLabelLikeTextField();
		lastModifiedLabel = new JLabelLikeTextField();
		permissionLabel = new JLabelLikeTextField();
		changePermissionButton = new JButton("Change Permissions...");
		changePermissionButton.setEnabled(false);
//		applicationsTree = new JTree();
//		applicationTreeModel = new DefaultTreeModel(new BioModelNode("Applications", true), true);
//		applicationTreeRootNode = (BioModelNode) applicationTreeModel.getRoot();
//		applicationsTree.setModel(applicationTreeModel);
//		applicationsTree.setCellRenderer(new BioModelCellRenderer(null));
//		applicationsTree.setRootVisible(false);
		webLinksPanel = new JPanel();
		webLinksPanel.setBackground(Color.white);
		
		setLayout(new GridBagLayout());
		setBackground(Color.white);
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;		
		gbc.insets = new Insets(10, 4, 4, 4);
		JLabel label = new JLabel("Saved BioModel Info");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		add(label, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(10, 10, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel("BioModel Name:");
		add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(nameLabel, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel("Owner:");
		add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(ownerLabel, gbc);

		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel("Last Modified:");
		add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(lastModifiedLabel, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;		
		label = new JLabel("Web Links:");
		add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(webLinksPanel, gbc);				
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel("Permissions:");
		add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;
		add(permissionLabel, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
//		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(changePermissionButton, gbc);

//		gridy ++;
//		gbc = new java.awt.GridBagConstraints();
//		gbc.gridx = 0; 
//		gbc.gridy = gridy;
//		gbc.insets = new Insets(4, 4, 4, 4);
//		gbc.anchor = GridBagConstraints.FIRST_LINE_END;		
//		label = new JLabel("Applications:");
//		add(label, gbc);
//		
//		gbc = new java.awt.GridBagConstraints();
//		gbc.gridx = 1; 
//		gbc.gridy = gridy;
//		gbc.fill = java.awt.GridBagConstraints.BOTH;
//		gbc.insets = new Insets(4, 4, 4, 4);
//		gbc.anchor = GridBagConstraints.LINE_START;	
//		gbc.weighty = 1.0;
//		gbc.insets = new Insets(4, 4, 20, 10);
//		add(new JScrollPane(applicationsTree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), gbc);

		changePermissionButton.addActionListener(eventHandler);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		BioModelPropertiesPanel aEditSpeciesPanel = new BioModelPropertiesPanel();
		frame.add(aEditSpeciesPanel);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.pack();
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of cbit.gui.JInternalFrameEnhanced");
		exception.printStackTrace(System.out);
	}
}

/**
 * Sets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @param speciesContext The new value for the property.
 * @see #getSpeciesContext
 */
public void setBioModel(BioModel newValue) {
	if (newValue == bioModel) {
		return;
	}
	bioModel = newValue;
	updateInterface();
}

public void setBioModelWindowManager(BioModelWindowManager bioModelWindowManager) {
	this.bioModelWindowManager = bioModelWindowManager;
	bioModelWindowManager.getRequestManager().getDocumentManager().addDatabaseListener(eventHandler);
	updateInterface();
}
/**
 * Comment
 */
private void updateInterface() {
	if (bioModel == null || bioModelWindowManager == null) {
		return;
	}
	nameLabel.setText(bioModel.getName());
	
	Version version = bioModel.getVersion();
	if (version != null) {
		ownerLabel.setText(version.getOwner().getName());
		lastModifiedLabel.setText(version.getDate().toString());
		try {
			BioModelInfo bioModelInfo = bioModelWindowManager.getRequestManager().getDocumentManager().getBioModelInfo(version.getVersionKey());
			permissionLabel.setText(bioModelInfo.getVersion().getGroupAccess().getDescription());
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		changePermissionButton.setEnabled(true);
	}
	webLinksPanel.removeAll();
	webLinksPanel.setLayout(new GridLayout(0,1));
	Set<MiriamRefGroup> resources = new HashSet<MiriamRefGroup>();
	Set<MiriamRefGroup> isDescribedByAnnotation = bioModel.getVCMetaData().getMiriamManager().getMiriamRefGroups(bioModel, MIRIAMQualifier.MODEL_isDescribedBy);
	Set<MiriamRefGroup> isAnnotation = bioModel.getVCMetaData().getMiriamManager().getMiriamRefGroups(bioModel, MIRIAMQualifier.MODEL_is);
	resources.addAll(isDescribedByAnnotation);
	resources.addAll(isAnnotation);
	for (MiriamRefGroup refGroup : resources){
		for (MiriamResource miriamResources : refGroup.getMiriamRefs()){
			LinkNode linkNode = new MiriamTreeModel.LinkNode(MIRIAMQualifier.MODEL_isDescribedBy, miriamResources);
			final String link = linkNode.getLink();
			String labelText = miriamResources.getDataType() == null ? "" : miriamResources.getDataType().getDataTypeName();
			String toolTip = null;
			if (link != null) {
				toolTip = "double-click to open link " + link;
				labelText = "<html><b>"+ labelText + "</b>&nbsp;" + "<font color=blue><a href=" + link + ">" + link + "</a></font></html>";
			}
			JLabel label = new JLabel(labelText);
			label.addMouseListener(new MouseListener() {				
				public void mouseReleased(MouseEvent e) {					
				}				
				public void mousePressed(MouseEvent e) {					
				}				
				public void mouseExited(MouseEvent e) {
				}				
				public void mouseEntered(MouseEvent e) {
				}				
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						DialogUtils.browserLauncher(BioModelPropertiesPanel.this, link, "failed to open " + link, false);
					}
				}
			});
			label.setToolTipText(toolTip);
			webLinksPanel.add(label);
		}
	}	
	
//	applicationTreeRootNode.removeAllChildren();
//	SimulationContext[] simulationContexts = bioModel.getSimulationContexts();
//	if (simulationContexts != null) {
//		for (int i = 0; i < bioModel.getNumSimulationContexts(); i ++) {
//			SimulationContext simContext = bioModel.getSimulationContext(i);
//			BioModelNode simContextNode = new BioModelNode(simContext, true);
//						
//			String typeInfo = simContext.getMathType();			
//			BioModelNode appTypeNode = new BioModelNode(typeInfo,false);
//			appTypeNode.setRenderHint("type","AppType");
//			simContextNode.add(appTypeNode);
//			
//			Geometry geometry = simContext.getGeometry();
//			BioModelNode geometryNode = new BioModelNode(geometry, false);
//			simContextNode.add(geometryNode);
//			
//			for (Simulation simulation : simContext.getSimulations()) {
//				BioModelNode simNode = new BioModelNode(simulation, false);
//				simContextNode.add(simNode);
//			}
//			applicationTreeRootNode.add(simContextNode);			
//		}
//	}
//	applicationTreeModel.nodeStructureChanged(applicationTreeRootNode);
//	GuiUtils.treeExpandAll(applicationsTree, applicationTreeRootNode, true);
}

}
