package cbit.vcell.client.desktop.biomodel;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public abstract class DocumentEditorTreeModel extends DefaultTreeModel
	implements java.beans.PropertyChangeListener/*, TreeExpansionListener, TreeSelectionListener*/ {

	public static class DocumentEditorTreeFolderNode {
		private DocumentEditorTreeFolderClass folderClass;
		private String name;
		private boolean bFirstLevel;
		boolean bSupported = true;
		
		public DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass c, String name) {
			this(c, name, false);
		}
		public DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass c, String name, boolean bFirstLevel) {
			this.folderClass = c;
			this.name = name;
			this.bFirstLevel = bFirstLevel;
		}		
		public boolean isSupported() {
			return bSupported;
		}		
		public void setSupported(boolean bSupported) {
			this.bSupported = bSupported;
		}
		public final String getName() {
			return name;
		}
		public final DocumentEditorTreeFolderClass getFolderClass() {
			return folderClass;
		}
		public boolean isFirstLevel() {
			return bFirstLevel;
		}
	}
	
	public enum DocumentEditorTreeFolderClass {
		PATHWAY_NODE,
		MODEL_NODE,	
		APPLICATTIONS_NODE,	
		SCRIPTING_NODE,

		REACTIONS_NODE,
		STRUCTURES_NODE,
		SPECIES_NODE,
		GLOBAL_PARAMETER_NODE,
		
		MATHEMATICS_NODE,
		ANALYSIS_NODE,
		
		GEOMETRY_NODE,
		STRUCTURE_MAPPING_NODE,
		INITIAL_CONDITIONS_NODE,		
		APP_REACTIONS_NODE,
		EVENTS_NODE,
		ELECTRICAL_MAPPING_NODE,
		DATA_SYMBOLS_NODE,
		
		SIMULATIONS_NODE,
		OUTPUT_FUNCTIONS_NODE,
		
		MICROSCOPE_MEASUREMENT_NODE,
		
		
		MATH_ANNOTATION_NODE,
		MATH_VCML_NODE,
		MATH_GEOMETRY_NODE,
		MATH_SIMULATIONS_NODE,
		MATH_OUTPUT_FUNCTIONS_NODE,
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

//	public void treeCollapsed(TreeExpansionEvent e) {
////		if (e.getSource() == ownerTree) {
////			TreePath path = e.getPath();
////			BioModelNode lastComp = (BioModelNode) path.getLastPathComponent();
////			expandedObject.remove(lastComp);
////		}
//	}
//	
//	public void treeExpanded(TreeExpansionEvent e) {
//		if (e.getSource() == ownerTree) {
//			TreePath path = e.getPath();
//			BioModelNode lastComp = (BioModelNode) path.getLastPathComponent();
//			if (lastComp.isNodeDescendant(selectedBioModelNode)) {
//				TreePath selectpath = new TreePath(selectedBioModelNode.getPath());
//				if (!ownerTree.isPathSelected(selectpath)) {
//					ownerTree.setSelectionPath(selectpath);
//					ownerTree.scrollPathToVisible(selectpath);
//				}
//			}
//		}
//	}
	
	protected void restoreTreeSelection() {
		if (bPopulatingRoot) {
			return;
		}
		if (selectedBioModelNode != null) {
			while (true) {
				if (rootNode.isNodeDescendant(selectedBioModelNode)) {
					break;
				}
				selectedBioModelNode = (BioModelNode) selectedBioModelNode.getParent();
				if (selectedBioModelNode == null) {
					selectedBioModelNode = getDefaultSelectionNode();
				}
			}
			TreePath path = new TreePath(selectedBioModelNode.getPath());
			boolean bSelectedAlready = ownerTree.isPathSelected(path); 
			if (!bSelectedAlready) {
				ownerTree.setSelectionPath(path);
				ownerTree.scrollPathToVisible(path);
			}
		}
	}
	
	protected abstract BioModelNode getDefaultSelectionNode();
	
	private void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects != null && selectedObjects.length > 0) {
			ArrayList<TreePath> newPathList = new ArrayList<TreePath>();
			for (Object object : selectedObjects) {
				BioModelNode node = rootNode.findNodeByUserObject(object);
				if (node != null) {
					newPathList.add(new TreePath(node.getPath()));
				}
			}
			if (newPathList.size() > 0) {
				ownerTree.setSelectionPaths(newPathList.toArray(new TreePath[0]));
				TreePath path = newPathList.get(0);
				selectedBioModelNode = (BioModelNode) path.getLastPathComponent();
				ownerTree.scrollPathToVisible(path);
			}
		} else {
			restoreTreeSelection();
		}
//		boolean bAllSimulationContext = true;
//		if (selectedObjects != null && selectedObjects.length > 0) {
//			ArrayList<TreePath> newPathList = new ArrayList<TreePath>();
//			for (Object object : selectedObjects) {
//				BioModelNode node = rootNode.findNodeByUserObject(object);
//				if (node != null) {
//					if (!(node.getUserObject() instanceof SimulationContext)) {
//						bAllSimulationContext = false;
//					}
//					newPathList.add(new TreePath(node.getPath()));
//				}
//			}
//			if (newPathList.size() > 0) {
//				TreePath path = newPathList.get(0);
//				selectedBioModelNode = (BioModelNode) path.getLastPathComponent();
//				if (!bAllSimulationContext) {
//					ownerTree.setSelectionPaths(newPathList.toArray(new TreePath[0]));
//					ownerTree.scrollPathToVisible(path);
//				}
//			}
//		} else {
//			restoreTreeSelection();
//		}
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
	
//	public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
//		if (e.getSource() == ownerTree) {
//			try {
//				Object node = ownerTree.getLastSelectedPathComponent();;
//				if (node != null && (node instanceof BioModelNode)) {
//					selectedBioModelNode = (BioModelNode) node;
//				}				
//			} catch (Exception ex){
//				ex.printStackTrace(System.out);
//			}
//		}
//	}
	
	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
		if (selectionManager != null) {
			selectionManager.removePropertyChangeListener(this);
			selectionManager.addPropertyChangeListener(this);
		}
	}
}
