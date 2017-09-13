package cbit.vcell.client;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.configuration.BioNetGenConfigurationPanel;
import cbit.vcell.client.configuration.ComsolConfigurationPanel;
import cbit.vcell.client.configuration.ConfigurationModelNode;
import cbit.vcell.client.configuration.ConfigurationOptionsTreeModel;
import cbit.vcell.client.configuration.ConfigurationOptionsTreeModel.ConfigurationOptionsTreeFolderNode;
import cbit.vcell.client.configuration.GeneralConfigurationPanel;
//import cbit.vcell.client.configuration.PythonConfigurationPanel;
//import cbit.vcell.client.configuration.PythonConfigurationPanel2;
import cbit.vcell.client.configuration.PythonConfigurationPanel3;
import cbit.vcell.client.configuration.VisItConfigurationPanel;

public class VCellConfigurationPanel extends JPanel {
	
	private GeneralConfigurationPanel generalConfigurationPanel = null;
	private PythonConfigurationPanel3 pythonConfigurationPanel = null;
	private VisItConfigurationPanel visItConfigurationPanel = null;
	private ComsolConfigurationPanel comsolConfigurationPanel = null;
	private BioNetGenConfigurationPanel bioNetGenConfigurationPanel = null;
	private JTree configurationOptionsTree = null;
	private ConfigurationOptionsTreeModel configurationOptionsTreeModel = null;
	
	JSplitPane splitPane = null;
	
	enum ActionButtons {
//		Ok,
		Close
	}
	ActionButtons buttonPushed = ActionButtons.Close;
//	private JButton okButton;
	private JButton closeButton;
	
	private TopLevelWindowManager owner;
	private ChildWindow parentChildWindow;

	
	public VCellConfigurationPanel(TopLevelWindowManager owner) {
		super();
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		generalConfigurationPanel = new GeneralConfigurationPanel();
		generalConfigurationPanel.setName("generalConfigurationPanel");
		pythonConfigurationPanel = new PythonConfigurationPanel3();
		pythonConfigurationPanel.setName("pythonConfigurationPanel");
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
		    	case PYTHON_NODE:
		    		splitPane.setRightComponent(pythonConfigurationPanel);
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
//		Border margin = new EmptyBorder(5,3,1,1);
//		treePanel.setBorder(margin);
		
		Dimension dim = new Dimension(200, 400);
		treePanel.setPreferredSize(dim);		// code that contributes to prevent resizing horizontally
		treePanel.setMinimumSize(dim);
		treePanel.setMaximumSize(dim);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT) {
			private final int location = 200;	// all code below to make it impossible to move the divider
			{									// so that the left component cannot be resized
				setDividerLocation(location);
				setResizeWeight(0);				// left component is fixed width
				setDividerSize(0);				// this also makes resizing impossible
				setOneTouchExpandable(false);	// no UI widget on the divider to quickly expand / collapse
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
		
		// ---------------------------------------------------------------------------
		JPanel okCancelPanel = new JPanel();
		okCancelPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;		// fake cell used for filling all the vertical empty space
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		okCancelPanel.add(new JLabel(""), gbc);

//		gbc = new GridBagConstraints();		
//		gbc.gridx = 1;
//		gbc.gridy = 0;
//		gbc.insets = new Insets(0, 0, 4, 2);				//  top, left, bottom, right 
//		okCancelPanel.add(getOkButton(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 2, 4, 4);
		okCancelPanel.add(getCloseButton(), gbc);
		
		// --------------------------------------------------------------------------------
		
		setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(1, 1, 1, 1);
		add(splitPane, gbc);

		gbc = new GridBagConstraints();		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(1, 1, 1, 1);
		add(okCancelPanel, gbc);

		

//		configurationOptionsTree.addTreeSelectionListener(eventHandler);
//		configurationOptionsTree.addMouseListener(eventHandler);

		configurationOptionsTreeModel.populateTree();
	}
	
//	private JButton getOkButton() {
//		if (okButton == null) {
//			okButton = new javax.swing.JButton("Ok");
//			okButton.setName("ApplyButton");
//			okButton.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					buttonPushed = ActionButtons.Ok;
////					try {
////
////					} catch (NumberFormatException ex) {
////						DialogUtils.showErrorDialog(parentChildWindow.getParent(), "Wrong number format: " + ex.getMessage());
////						return;
////					}
//					parentChildWindow.close();
//				}
//			});
//		}
//		return okButton;
//	}
	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new javax.swing.JButton("Close");
			closeButton.setName("CloseButton");
			closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonPushed = ActionButtons.Close;
					parentChildWindow.close();
				}
			});
		}
		return closeButton;
	}
	public ActionButtons getButtonPushed() {
		return buttonPushed;
	}
	
	public void setChildWindow(ChildWindow childWindow) {
		this.parentChildWindow = childWindow;
	}

}
