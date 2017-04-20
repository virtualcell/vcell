package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SeedSpecies;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.document.PropertyConstants;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.BondLocal;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.SpeciesPatternLocal;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.model.SpeciesContext;

class SpeciesPropertiesTreeModel extends RbmDefaultTreeModel implements PropertyChangeListener {
	private BioModelNode rootNode;
	private SpeciesContext speciesContext;
	private JTree ownerTree;
	private BioModel bioModel;
	private boolean bShowDetails = false;
	
	public SpeciesPropertiesTreeModel(JTree tree) {
		super(new BioModelNode("Species Pattern",true),true);
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
	
	public void populateTree() {
		if (speciesContext == null || bioModel == null) {
			return;
		}
		rootNode.setUserObject(speciesContext);
		rootNode.removeAllChildren();
		int count = 0;
		
		if (speciesContext.getSpeciesPattern() == null) {
			nodeStructureChanged(rootNode);
			return;
		}
		
		SpeciesPattern sp = speciesContext.getSpeciesPattern();
//		BioModelNode spNode = new BioModelNode(new SpeciesPatternLocal(sp, ++count));
		for (MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
			BioModelNode node = createMolecularTypePatternNode(mtp);
			rootNode.add(node);
//			spNode.add(node);
		}
//		rootNode.add(spNode);
		nodeStructureChanged(rootNode);
		GuiUtils.treeExpandAllRows(ownerTree);

		// we fire a "dummy" event because the species properties panel and the bio model editor species table model
		// will repaint the shape and respectively the table row for any speciesContext property change event
		speciesContext.firePropertyChange("entityChange", null, "bbb");
	}
	private BioModelNode createMolecularTypePatternNode(MolecularTypePattern molecularTypePattern) {
		MolecularType molecularType = molecularTypePattern.getMolecularType();
		BioModelNode node = new BioModelNode(molecularTypePattern, true);
		for (MolecularComponent mc : molecularType.getComponentList()) {
// Attention: we show all components even though the combination State Any + Bond Possible should be "invisible"
// uncomment the "if" to hide the Any + Possible combination
//			if (bShowDetails || molecularTypePattern.getMolecularComponentPattern(mc).isbVisible()) {
			BioModelNode n = createMolecularComponentPatternNode(molecularTypePattern.getMolecularComponentPattern(mc));
			if(n != null) {
				node.add(n);
//			}		
			}
		}
		return node;
	}
	private BioModelNode createMolecularComponentPatternNode(MolecularComponentPattern molecularComponentPattern) {
		MolecularComponent mc = molecularComponentPattern.getMolecularComponent();
		BioModelNode node = new BioModelNode(molecularComponentPattern, true);
//		ComponentStatePattern csp = molecularComponentPattern.getComponentStatePattern();
//		if(mc.getComponentStateDefinitions().size() > 0) {	// we don't show the state if nothing to choose from
//			StateLocal sl = new StateLocal(molecularComponentPattern);
//			BioModelNode ns = new BioModelNode(sl, false);
//			node.add(ns);
//		}
//		if(!molecularComponentPattern.getBondType().equals(BondType.None) || true) {	// we save space by not showing the Bond.None
//			BondLocal bl = new BondLocal(molecularComponentPattern);
//			BioModelNode nb = new BioModelNode(bl, false);
//			node.add(nb);
//		}
		return node;
	}
		
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_NAME)) {
			nodeChanged(rootNode);
//		} else if (evt.getSource() == seedSpecies && evt.getPropertyName().equals(SeedSpecies.PROPERTY_NAME_TYPE)){
//			nodeChanged(rootNode);
		} else if(evt.getPropertyName().equals("entityChange")) {
			nodeChanged(rootNode);
		} else {
			populateTree();
			
			Object source = evt.getSource();
			if (source == speciesContext) {
				if (evt.getPropertyName().equals(SeedSpecies.PROPERTY_NAME_SPECIES_PATTERN)) {
					SpeciesPattern oldValue = (SpeciesPattern) evt.getOldValue();
					if (oldValue != null) {
						RbmUtils.removePropertyChangeListener(oldValue, this);
					}
					SpeciesPattern newValue = (SpeciesPattern) evt.getNewValue();
					if (newValue != null) {
						// TODO
						RbmUtils.addPropertyChangeListener(newValue, this);
					}
				}
			} else if (source instanceof SpeciesPattern) {
				if (evt.getPropertyName().equals(SpeciesPattern.PROPERTY_NAME_MOLECULAR_TYPE_PATTERNS)) {
					List<MolecularTypePattern> oldValue = (List<MolecularTypePattern>) evt.getOldValue();
					if (oldValue != null) {
						for (MolecularTypePattern mtp : oldValue) {
							RbmUtils.removePropertyChangeListener(mtp, this);
						}
					}
					List<MolecularTypePattern> newValue = (List<MolecularTypePattern>) evt.getNewValue();
					if (newValue != null) {
						for (MolecularTypePattern mtp : newValue) {
							RbmUtils.addPropertyChangeListener(mtp, this);
						}
					}
				}
			} else if (source instanceof MolecularTypePattern) {
				if (evt.getPropertyName().equals(MolecularTypePattern.PROPERTY_NAME_COMPONENT_PATTERN_LIST)) {
					List<MolecularComponentPattern> oldValue = (List<MolecularComponentPattern>) evt.getOldValue();
					if (oldValue != null) {
						for (MolecularComponentPattern mcp : oldValue) {
							RbmUtils.removePropertyChangeListener(mcp, this);
						}
					}
					List<MolecularComponentPattern> newValue = (List<MolecularComponentPattern>) evt.getNewValue();
					if (newValue != null) {
						for (MolecularComponentPattern mcp : newValue) {
							RbmUtils.addPropertyChangeListener(mcp, this);
						}
					}
				}
			} else if (source instanceof MolecularComponentPattern) {
				if (evt.getSource().equals(MolecularComponentPattern.PROPERTY_NAME_COMPONENT_STATE)) {	// it's componentStatePattern
					ComponentStateDefinition oldValue = (ComponentStateDefinition) evt.getOldValue();
					if (oldValue != null) {
						oldValue.removePropertyChangeListener(this);
					}
					ComponentStateDefinition newValue = (ComponentStateDefinition) evt.getNewValue();
					if (newValue != null) {
						newValue.addPropertyChangeListener(this);
					}
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
		BioModelNode parentNode = (BioModelNode)selectedNode.getParent();
		Object userObject = selectedNode.getUserObject();
		try {
			if (newValue instanceof String) {
				String inputString = (String)newValue;
				if (inputString == null || inputString.length() == 0) {
					return;
				}
//				if (userObject instanceof SeedSpecies) {
//					((SeedSpecies) userObject).setSpeciesPattern(inputString);
//				}
			} else if (newValue instanceof MolecularComponentPattern) {
				MolecularComponentPattern newMcp = (MolecularComponentPattern) newValue;
				Object parentObject = parentNode == null ? null : parentNode.getUserObject();
				if (parentObject instanceof MolecularTypePattern) {
					MolecularTypePattern mtp = (MolecularTypePattern) parentObject;
					MolecularComponent mc = newMcp.getMolecularComponent();
					MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(mc);
					
					// TODO: what's correct here ?					
					mcp.setComponentStatePattern(newMcp.getComponentStatePattern());
					BondType bp = mcp.getBondType();
					BondType newbp = newMcp.getBondType();
					mcp.setBondType(newbp);
					// specified -> specified
					if (bp == BondType.Specified && newbp == BondType.Specified) {
						// bond didn't change 
					}  else if (bp == BondType.Specified && newbp != BondType.Specified) {
						// specified -> non specified
						// change the partner to possible
						mcp.getBond().molecularComponentPattern.setBondType(BondType.Possible);
						mcp.setBond(null);
					} else if (bp != BondType.Specified && newbp == BondType.Specified){
						// non specified -> specified
						int newBondId = newMcp.getBondId();
						mcp.setBondId(newBondId);
						mcp.setBond(newMcp.getBond());
						mcp.getBond().molecularComponentPattern.setBondId(newBondId);
						speciesContext.getSpeciesPattern().resolveBonds();
					} else {
					}				
				}
			}
		} catch (Exception ex) {
			DialogUtils.showErrorDialog(ownerTree, ex.getMessage());			
		}
	}
	
	public void setSpeciesContext(SpeciesContext newValue) {
		if (newValue == speciesContext) {
			return;
		}
		SpeciesContext oldValue = speciesContext;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(this);
			SpeciesPattern speciesPattern = oldValue.getSpeciesPattern();
			RbmUtils.removePropertyChangeListener(speciesPattern, this);
		}
		speciesContext = newValue;
		populateTree();
		if (newValue != null) {
			newValue.addPropertyChangeListener(this);
			SpeciesPattern speciesPattern = newValue.getSpeciesPattern();
			RbmUtils.addPropertyChangeListener(speciesPattern, this);
		}
		
	}
	
	public void setBioModel(BioModel bioModel) {
		this.bioModel = bioModel;
	}

	public final void setShowDetails(boolean bShowDetails) {
		this.bShowDetails = bShowDetails;
		populateTree();
	}
}
