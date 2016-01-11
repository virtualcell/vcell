package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularType;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.PropertyConstants;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.Displayable;

import cbit.vcell.desktop.BioModelNode;

class MolecularTypeTreeModel extends RbmDefaultTreeModel implements PropertyChangeListener {
	private BioModelNode rootNode;
	private MolecularType molecularType;
	private JTree ownerTree;
	
	public MolecularTypeTreeModel(JTree tree) {
		super(new BioModelNode(MolecularType.typeName, true), true);
		rootNode = (BioModelNode)root;
		ownerTree = tree;
	}
	
	public TreePath findObjectPath(BioModelNode startNode, Object object) {
		if (startNode == null) {
			startNode = rootNode;
		}
		Object userObject = startNode.getUserObject();
		if (userObject == object) {
			return new TreePath(startNode.getPath());
		}
		for (int i = 0; i < startNode.getChildCount(); i ++) {
			BioModelNode childNode = (BioModelNode) startNode.getChildAt(i);
			TreePath path = findObjectPath(childNode, object);
			if (path != null) {
				return path;
			}
		}
		return null;
	}
	
	private BioModelNode createMolecularComponentNode(MolecularComponent molecularComponent) {
		BioModelNode node = new BioModelNode(molecularComponent, true);
		for (ComponentStateDefinition componentState : molecularComponent.getComponentStateDefinitions()) {
			BioModelNode n = new BioModelNode(componentState, false);
			node.add(n);
		}
		return node;
	}
	
	public void populateTree() {
		if (molecularType == null) {
			return;
		}
		rootNode.setUserObject(molecularType);
		rootNode.removeAllChildren();
		for (MolecularComponent molecularComponent: molecularType.getComponentList()) {
			BioModelNode node = createMolecularComponentNode(molecularComponent);
			rootNode.add(node);
		}
		
		nodeStructureChanged(rootNode);
		GuiUtils.treeExpandAllRows(ownerTree);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_NAME)) {
			nodeChanged(rootNode);
		} else if (evt.getPropertyName().equals("entityChange")) {
			nodeChanged(rootNode);
		} else {
			populateTree();
			
			Object object = evt.getSource();
			if (object instanceof MolecularType) {
				List<MolecularComponent> oldValue = (List<MolecularComponent>) evt.getOldValue();
				for (MolecularComponent molecularComponent : oldValue) {
					molecularComponent.removePropertyChangeListener(this);
					for (ComponentStateDefinition componentState : molecularComponent.getComponentStateDefinitions()) {
						componentState.removePropertyChangeListener(this);
					}
				}
				List<MolecularComponent> newValue = (List<MolecularComponent>) evt.getNewValue();
				for (MolecularComponent molecularComponent : newValue) {
					molecularComponent.addPropertyChangeListener(this);
					for (ComponentStateDefinition componentState : molecularComponent.getComponentStateDefinitions()) {
						componentState.addPropertyChangeListener(this);
					}
				}
			} else if (object instanceof MolecularComponent){
				List<ComponentStateDefinition> oldValue = (List<ComponentStateDefinition>) evt.getOldValue();
				for (ComponentStateDefinition componentState : oldValue) {
					componentState.removePropertyChangeListener(this);
				}
				List<ComponentStateDefinition> newValue = (List<ComponentStateDefinition>) evt.getNewValue();
				for (ComponentStateDefinition componentState : newValue) {
					componentState.addPropertyChangeListener(this);
				}
			}
		}
	}
	
	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		Object obj = path.getLastPathComponent();
		if (obj == null || !(obj instanceof BioModelNode)) {
			return;
		}
		BioModelNode selectedNode = (BioModelNode) obj;
		Object userObject = selectedNode.getUserObject();
		try {
			if (newValue instanceof String) {
				String inputString = (String)newValue;
				if (inputString == null || inputString.length() == 0) {
					return;
				}
				
				if (userObject instanceof MolecularType) {
					String mangled = TokenMangler.fixTokenStrict(inputString);
					if(!mangled.equals(inputString)) {
						String errMsg = ((Displayable)userObject).getDisplayType() + " '" + inputString + "' not legal identifier, try '" + mangled + "'";
						throw new RuntimeException(errMsg);
					}
					((MolecularType) userObject).setName(inputString);
				} else if (userObject instanceof MolecularComponent) {
					String mangled = TokenMangler.fixTokenStrict(inputString);
					if(!mangled.equals(inputString)) {
						String errMsg = ((Displayable)userObject).getDisplayType() + " '" + inputString + "' not legal identifier, try '" + mangled + "'";
						throw new RuntimeException(errMsg);
					}
					((MolecularComponent) userObject).setName(inputString);
				} else if (userObject instanceof ComponentStateDefinition) {
					if(inputString.matches("[A-Z_a-z0-9]+")) {
						((ComponentStateDefinition) userObject).setName(inputString);
					}
				}
			} 
		} catch (Exception ex) {
			DialogUtils.showErrorDialog(ownerTree, ex.getMessage());			
		}
	}
	
	public void setMolecularType(MolecularType newValue) {
		if (newValue == molecularType) {
			return;
		}
		MolecularType oldValue = molecularType;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(this);
			for (MolecularComponent molecularComponent : oldValue.getComponentList()) {
				molecularComponent.removePropertyChangeListener(this);
				for (ComponentStateDefinition componentState : molecularComponent.getComponentStateDefinitions()) {
					componentState.removePropertyChangeListener(this);
				}
			}
		}
		molecularType = newValue;
		populateTree();
		if (newValue != null) {
			newValue.addPropertyChangeListener(this);
			for (MolecularComponent molecularComponent : newValue.getComponentList()) {
				molecularComponent.addPropertyChangeListener(this);
				for (ComponentStateDefinition componentState : molecularComponent.getComponentStateDefinitions()) {
					componentState.addPropertyChangeListener(this);
				}
			}
		}
		
	}
}