package cbit.vcell.client.configuration;

import java.beans.PropertyChangeEvent;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;

import org.vcell.util.gui.GuiUtils;


public class ConfigurationOptionsTreeModel extends DefaultTreeModel
	implements java.beans.PropertyChangeListener, TreeSelectionListener {

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
		PYTHON_NODE("Python Properties"),
		COMSOL_NODE("Comsol Properties"),
		BIONETGEN_NODE("BioNetGen Preferences");
		
		private String title = null;
		ConfigurationOptionsTreeFolderClass(String n) {
			title = n;
		}
		public final String getTitle() {
			return title;
		}
	}

	public ConfigurationOptionsTreeFolderNode configurationChildFolderNodes[] = {
			new ConfigurationOptionsTreeFolderNode(ConfigurationOptionsTreeFolderClass.GENERAL_NODE, true),
			new ConfigurationOptionsTreeFolderNode(ConfigurationOptionsTreeFolderClass.PYTHON_NODE, true),
			new ConfigurationOptionsTreeFolderNode(ConfigurationOptionsTreeFolderClass.COMSOL_NODE, true),
			new ConfigurationOptionsTreeFolderNode(ConfigurationOptionsTreeFolderClass.BIONETGEN_NODE, true),
		};
	private ConfigurationModelNode generalNode = new ConfigurationModelNode(configurationChildFolderNodes[0], true);
	private ConfigurationModelNode comsolNode = new ConfigurationModelNode(configurationChildFolderNodes[2], false);
	private ConfigurationModelNode bioNetGenNode = new ConfigurationModelNode(configurationChildFolderNodes[3], false);	// no leaves
	
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
		rootNode.add(comsolNode);
		generalNode.add(bioNetGenNode);
		
		nodeStructureChanged(rootNode);
		GuiUtils.treeExpandAllRows(ownerTree);
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
	}
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		
	}

}
