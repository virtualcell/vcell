package cbit.vcell.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import cbit.vcell.client.configuration.BioNetGenConfigurationPanel;
import cbit.vcell.client.configuration.ComsolConfigurationPanel;
import cbit.vcell.client.configuration.ConfigurationModelNode;
import cbit.vcell.client.configuration.ConfigurationOptionsTreeModel;
import cbit.vcell.client.configuration.GeneralConfigurationPanel;
import cbit.vcell.client.configuration.ConfigurationOptionsTreeModel.ConfigurationOptionsTreeFolderNode;
import cbit.vcell.client.configuration.VisItConfigurationPanel;

public class VCellConfigurationPanel extends JPanel {
	
	private GeneralConfigurationPanel generalConfigurationPanel = null;
	private VisItConfigurationPanel visItConfigurationPanel = null;
	private ComsolConfigurationPanel comsolConfigurationPanel = null;
	private BioNetGenConfigurationPanel bioNetGenConfigurationPanel = null;
	private JTree configurationOptionsTree = null;
	private ConfigurationOptionsTreeModel configurationOptionsTreeModel = null;
	
	JSplitPane splitPane = null;
	
	public VCellConfigurationPanel() {
		super();
		initialize();
	}

	private void initialize() {

		generalConfigurationPanel = new GeneralConfigurationPanel();
		generalConfigurationPanel.setName("generalConfigurationPanel");
		visItConfigurationPanel = new VisItConfigurationPanel();
		visItConfigurationPanel.setName("visItConfigurationPanel");
		comsolConfigurationPanel = new ComsolConfigurationPanel();
		comsolConfigurationPanel.setName("comsolConfigurationPanel");
		bioNetGenConfigurationPanel = new BioNetGenConfigurationPanel();
		bioNetGenConfigurationPanel.setName("bioNetGenConfigurationPanel");
		
		configurationOptionsTree = new javax.swing.JTree();
		configurationOptionsTreeModel = new ConfigurationOptionsTreeModel(configurationOptionsTree);
		configurationOptionsTree.setModel(configurationOptionsTreeModel);
		configurationOptionsTree.setEditable(false);
		configurationOptionsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		configurationOptionsTree.setRootVisible(false);
		
		configurationOptionsTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			
		    @Override
		    public void valueChanged(TreeSelectionEvent e) {
		    	ConfigurationModelNode cmn = (ConfigurationModelNode) e.getNewLeadSelectionPath().getLastPathComponent();
		    	ConfigurationOptionsTreeFolderNode cotfn = (ConfigurationOptionsTreeFolderNode) cmn.getUserObject();
		    	
		    	switch(cotfn.getFolderClass()) {
		    	case GENERAL_NODE:
		    		splitPane.setRightComponent(generalConfigurationPanel);
		    		break;
		    	case VISIT_NODE:
		    		splitPane.setRightComponent(visItConfigurationPanel);
		    		break;
		    	case COMSOL_NODE:
		    		splitPane.setRightComponent(comsolConfigurationPanel);
		    		break;
		    	case BIONETGEN_NODE:
		    		splitPane.setRightComponent(bioNetGenConfigurationPanel);
		    		break;
		    	}
		    }
		});
		
		JScrollPane treePanel = new javax.swing.JScrollPane(configurationOptionsTree);
		Dimension dim = new Dimension(200, 400);
		treePanel.setPreferredSize(dim);
		treePanel.setMinimumSize(dim);
		treePanel.setMaximumSize(dim);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT) {
			private final int location = 200;
			{
				setDividerLocation(location);
				setResizeWeight(0);				// left component is fixed width
				setDividerSize(3);
				setOneTouchExpandable(false);		// no UI widget on the divider to quickly expand / collapse
			}
			@Override
			public int getDividerLocation() {
				return location;
			}
			@Override
			public int getLastDividerLocation() {
				return location;
			}
		};
		splitPane.setLeftComponent(treePanel);
		splitPane.setRightComponent(generalConfigurationPanel);
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);

//		configurationOptionsTree.addTreeSelectionListener(eventHandler);
//		configurationOptionsTree.addMouseListener(eventHandler);

		configurationOptionsTreeModel.populateTree();
	}

}
