package cbit.vcell.client.configuration;

import java.beans.PropertyChangeEvent;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.vcell.model.rbm.MolecularComponent;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderNode;
import cbit.vcell.desktop.BioModelNode;


public class ConfigurationOptionsTreeModel extends DefaultTreeModel
	implements java.beans.PropertyChangeListener, TreeExpansionListener, TreeSelectionListener {

	public static class ConfigurationOptionsTreeFolderNode {
		private ConfigurationOptionsTreeFolderClass folderClass;
		private boolean bBold;
		boolean bSupported = true;
		public ConfigurationOptionsTreeFolderNode(ConfigurationOptionsTreeFolderClass c) {
			this(c, false);
		}
		public ConfigurationOptionsTreeFolderNode(ConfigurationOptionsTreeFolderClass c, boolean bBold) {
			this.folderClass = c;
			this.bBold = bBold;
		}		
		public boolean isSupported() {
			return bSupported;
		}		
		public void setSupported(boolean bSupported) {
			this.bSupported = bSupported;
		}
		public final String getName() {
			return folderClass.title;
		}
		public final ConfigurationOptionsTreeFolderClass getFolderClass() {
			return folderClass;
		}
		public boolean isBold() {
			return bBold;
		}
		public String toString() {
			return getName();
		}
	}
	
	public enum ConfigurationOptionsTreeFolderClass {
		
		GENERAL_NODE("General Properties"),
		BIONETGEN_NODE("BioNetGen Preferences");
		
		private String title = null;
		ConfigurationOptionsTreeFolderClass(String n) {
			title = n;
		}
		public final String getTitle() {
			return title;
		}
	}

	// first Level
	private ConfigurationOptionsTreeFolderNode configurationChildFolderNodes[] = {
			new ConfigurationOptionsTreeFolderNode(ConfigurationOptionsTreeFolderClass.GENERAL_NODE, true),
			new ConfigurationOptionsTreeFolderNode(ConfigurationOptionsTreeFolderClass.BIONETGEN_NODE, true),
		};
	private ConfigurationModelNode generalNode = new ConfigurationModelNode(configurationChildFolderNodes[0], true);
	private ConfigurationModelNode bioNetGenNode = new ConfigurationModelNode(configurationChildFolderNodes[1], false);	// no leaves
	
	private ConfigurationModelNode rootNode = null;
	private JTree ownerTree = null;

	
	public ConfigurationOptionsTreeModel(JTree tree) {
		
		super(new ConfigurationModelNode("empty", true));
		
		this.rootNode = (ConfigurationModelNode)root;
		this.ownerTree = tree;
	}
	
	public void populateTree() {

		rootNode.setUserObject(null);
		rootNode.removeAllChildren();

		rootNode.add(generalNode);
		rootNode.add(bioNetGenNode);
		
		nodeStructureChanged(rootNode);
		GuiUtils.treeExpandAllRows(ownerTree);
	}


	@Override
	public void treeExpanded(TreeExpansionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
