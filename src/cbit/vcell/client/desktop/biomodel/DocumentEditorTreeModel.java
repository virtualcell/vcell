package cbit.vcell.client.desktop.biomodel;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.solver.Simulation;

@SuppressWarnings("serial")
public abstract class DocumentEditorTreeModel extends DefaultTreeModel
	implements java.beans.PropertyChangeListener, TreeExpansionListener/*, TreeSelectionListener*/ {

	public static class DocumentEditorTreeFolderNode {
		private DocumentEditorTreeFolderClass folderClass;
		private boolean bFirstLevel;
		boolean bSupported = true;
		
		public DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass c) {
			this(c, false);
		}
		public DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass c, boolean bFirstLevel) {
			this.folderClass = c;
			this.bFirstLevel = bFirstLevel;
		}		
		public boolean isSupported() {
			return bSupported;
		}		
		public void setSupported(boolean bSupported) {
			this.bSupported = bSupported;
		}
		public final String getName() {
			return folderClass.name;
		}
		public final DocumentEditorTreeFolderClass getFolderClass() {
			return folderClass;
		}
		public boolean isFirstLevel() {
			return bFirstLevel;
		}
	}
	
	public enum DocumentEditorTreeFolderClass {
//		MODELINFO_NODE("Saved BioModel Info"),
		PATHWAY_NODE("Pathway"),
		MODEL_NODE("Biological Model"),	
		APPLICATTIONS_NODE("Applications"),	
		SCRIPTING_NODE("Scripting"),

		REACTIONS_NODE("Reactions"),
		STRUCTURES_NODE("Structures"),
		SPECIES_NODE("Species"),
		GLOBAL_PARAMETER_NODE("Global Parameters"),
		
		SPECIFICATIONS_NODE("Specifications"),
		MATHEMATICS_NODE("Generated Math"),
		
		GEOMETRY_NODE("Geometry"),
		STRUCTURE_MAPPING_NODE("Structure Mapping"),
		INITIAL_CONDITIONS_NODE("Initial Conditions"),
		APP_REACTIONS_NODE("Reactions"),
		EVENTS_NODE("Events"),
		ELECTRICAL_MAPPING_NODE("Electrical"),
		DATA_SYMBOLS_NODE("Data Symbols"),
		MICROSCOPE_MEASUREMENT_NODE("Microscope Measurements"),
		
		SIMULATIONS_NODE("Simulations"),
		OUTPUT_FUNCTIONS_NODE("Output Functions"),
		ANALYSIS_NODE("Parameter Estimation"),
		
		MATH_ANNOTATION_NODE("Annotation"),
		MATH_VCML_NODE("VCML Editor"),
		MATH_GEOMETRY_NODE("Geometry"),
		MATH_SIMULATIONS_NODE("Simulations"),
		MATH_OUTPUT_FUNCTIONS_NODE("Output Functions");
		
		private String name = null;
		DocumentEditorTreeFolderClass(String n) {
			name = n;
		}
	}
	
	protected boolean bPopulatingRoot = false;
	protected BioModelNode rootNode = null;
	protected JTree ownerTree = null;
	private transient java.beans.PropertyChangeSupport propertyChange;
	protected BioModelNode selectedBioModelNode = null;
	protected SelectionManager selectionManager = null;
			
	public DocumentEditorTreeModel(JTree tree) {
		super(new BioModelNode("empty",true),true);
		rootNode = (BioModelNode)root;
		this.ownerTree = tree;
	}	
	
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		try {
			Object source = evt.getSource();			
			if (source == selectionManager) {
				Object[] selectedObject = ((SelectionManager)source).getSelectedObjects();
				onSelectedObjectsChange(selectedObject);
			} 
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	public void treeCollapsed(TreeExpansionEvent e) {
	}
	
	public void treeExpanded(TreeExpansionEvent e) {
		if (e.getSource() == ownerTree) {
			onSelectedObjectsChange(selectionManager.getSelectedObjects());
		}
	}
	
	protected void restoreTreeSelection() {
		if (bPopulatingRoot) {
			return;
		}
		if (selectedBioModelNode == null || !rootNode.isNodeDescendant(selectedBioModelNode)) {
			selectedBioModelNode = getDefaultSelectionNode();
		}
		
		TreePath path = new TreePath(selectedBioModelNode.getPath());
		if (ownerTree.isPathSelected(path)) {
			return;
		}
//			while (true) {
//				if (rootNode.isNodeDescendant(selectedBioModelNode)) {
//					break;
//				}
//				selectedBioModelNode = (BioModelNode) selectedBioModelNode.getParent();
//				if (selectedBioModelNode == null) {
//					selectedBioModelNode = getDefaultSelectionNode();
//				}
//			}
		ownerTree.setSelectionPath(path);
		ownerTree.scrollPathToVisible(path);				
	}
	
	protected abstract BioModelNode getDefaultSelectionNode();
	
	private void onSelectedObjectsChange(Object[] selectedObjects) {
//		if (selectedObjects != null && selectedObjects.length > 0) {
//			ArrayList<TreePath> newPathList = new ArrayList<TreePath>();
//			for (Object object : selectedObjects) {
//				BioModelNode node = rootNode.findNodeByUserObject(object);
//				if (node != null) {
//					newPathList.add(new TreePath(node.getPath()));
//				}
//			}
//			if (newPathList.size() > 0) {
//				ownerTree.setSelectionPaths(newPathList.toArray(new TreePath[0]));
//				TreePath path = newPathList.get(0);
//				selectedBioModelNode = (BioModelNode) path.getLastPathComponent();
//				ownerTree.scrollPathToVisible(path);
//			}
//		} else {
//			restoreTreeSelection();
//		}
		boolean bAllSimulationContext = true;
		if (selectedObjects != null && selectedObjects.length > 0) {
			ArrayList<TreePath> newPathList = new ArrayList<TreePath>();
			for (Object object : selectedObjects) {
				BioModelNode node = rootNode.findNodeByUserObject(object);
				if (node != null) {
					TreePath path = new TreePath(node.getPath());
					if (ownerTree.isVisible(path)) {
						if (!(node.getUserObject() instanceof SimulationContext)) {
							bAllSimulationContext = false;
						}
						newPathList.add(path);
					}
				}
			}
			if (newPathList.size() > 0) {
				TreePath path = newPathList.get(0);
				if (bAllSimulationContext) {
					selectedBioModelNode = (BioModelNode) path.getParentPath().getLastPathComponent();
				} else {
					selectedBioModelNode = (BioModelNode) path.getLastPathComponent();
					ownerTree.setSelectionPaths(newPathList.toArray(new TreePath[0]));
					ownerTree.scrollPathToVisible(path);
				}
			}
		} else {
			restoreTreeSelection();
		}
	}

	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}
	
	private java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}
	
	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
		if (selectionManager != null) {
			selectionManager.removePropertyChangeListener(this);
			selectionManager.addPropertyChangeListener(this);
		}
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		if (!(newValue instanceof String)) {
			return;
		}
		String newName = (String)newValue;
		try {
			if (newName == null || newName.length() == 0) {
				return;
			}
			Object obj = path.getLastPathComponent();
			if (obj == null || !(obj instanceof BioModelNode)) {
				return;
			}
			BioModelNode selectedNode = (BioModelNode) obj;
			Object userObject = selectedNode.getUserObject();
			if (userObject instanceof ReactionStep) {
				((ReactionStep) userObject).setName(newName);
			} else if (userObject instanceof Structure) {
				Structure structure = (Structure) userObject;
				structure.setName(newName);
				structure.getStructureSize().setName(Structure.getDefaultStructureSizeName(newName));
				if (structure instanceof Membrane) {
					((Membrane)structure).getMembraneVoltage().setName(Membrane.getDefaultMembraneVoltageName(newName));
				}
			} else if (userObject instanceof SpeciesContext) {
				((SpeciesContext) userObject).setName(newName);
			} else if (userObject instanceof ModelParameter) {
				((ModelParameter) userObject).setName(newName);
			} else if (userObject instanceof SimulationContext) {
				((SimulationContext) userObject).setName(newName);
			} else if (userObject instanceof Simulation) {
				((Simulation) userObject).setName(newName);
			} else if (userObject instanceof BioEvent) {
				((BioEvent) userObject).setName(newName);
			}
		} catch (Exception ex) {
			DialogUtils.showErrorDialog(ownerTree, ex.getMessage());			
		}
	}
}
