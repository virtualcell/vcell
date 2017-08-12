package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.document.PropertyConstants;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionRule.ReactionRuleParticipantType;
import cbit.vcell.model.ReactionRuleParticipant;


public class ReactionRulePropertiesTreeModel extends RbmDefaultTreeModel implements PropertyChangeListener {

	private BioModelNode rootNode;
	private ReactionRule reactionRule;
	private JTree ownerTree;
	private BioModel bioModel;
	private ReactionRuleParticipantType participantType;
	private final boolean bShowDetails = true;
	
	public ReactionRulePropertiesTreeModel(JTree tree, ReactionRuleParticipantType participantType) {
		super(new BioModelNode("Reaction Rule",true),true);
		rootNode = (BioModelNode)root;
		ownerTree = tree;
		this.participantType = participantType;
	}
	
	public TreePath findObjectPath(BioModelNode startNode, Object object) {
		if (startNode == null) {
			startNode = rootNode;
		}
		Object userObject = startNode.getUserObject();
		if (userObject == object || userObject instanceof ReactionRuleParticipant && ((ReactionRuleParticipant)userObject).getSpeciesPattern() == object) {
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
		if (reactionRule == null) {
			// this may be legit, for example when there's a plain reaction rather than a reaction rule
			System.out.println("ReactionRulePropertiesTreeModel: reactionRule is null.");
			return;
		}
		if (bioModel == null) {
			System.out.println("ReactionRulePropertiesTreeModel: bioModel is null.");
			return;
		}
		rootNode.setUserObject(reactionRule);
		rootNode.removeAllChildren();
		int count = 0;
		List<? extends ReactionRuleParticipant> patterns = participantType == ReactionRuleParticipantType.Reactant ? reactionRule.getReactantPatterns() : reactionRule.getProductPatterns();
		for (ReactionRuleParticipant rrp : patterns) {
			BioModelNode rrNode = new BioModelNode(new ReactionRuleParticipantLocal(participantType, rrp, ++ count));
			for (MolecularTypePattern mtp : rrp.getSpeciesPattern().getMolecularTypePatterns()) {
				BioModelNode node = createMolecularTypePatternNode(mtp);
				rrNode.add(node);
			}
			rootNode.add(rrNode);
		}
		nodeStructureChanged(rootNode);
//		GuiUtils.treeExpandAll(ownerTree, rootNode, true);
		GuiUtils.treeExpandAllRows(ownerTree);

		// we fire a dummy event because the species properties panel and the bio model editor species table model
		// will repaint the shape and respectively the table row for any speciesContext property change event
		reactionRule.firePropertyChange("entityChange", null, "bbb");
	}
	private BioModelNode createMolecularTypePatternNode(MolecularTypePattern molecularTypePattern) {
		MolecularType molecularType = molecularTypePattern.getMolecularType();
		BioModelNode node = new BioModelNode(molecularTypePattern, true);
		
		if(molecularTypePattern.hasExplicitParticipantMatch()) {
			ParticipantMatchLabelLocal pmll = new ParticipantMatchLabelLocal(molecularTypePattern.getParticipantMatchLabel());
			BioModelNode nm = new BioModelNode(pmll, true);
			node.add(nm);
		}
		for (MolecularComponent mc : molecularType.getComponentList()) {
			// dead code, we don't show state and bond
			if (bShowDetails || molecularTypePattern.getMolecularComponentPattern(mc).isbVisible()) {
				BioModelNode n = createMolecularComponentPatternNode(molecularTypePattern.getMolecularComponentPattern(mc));
				if(n != null) {
					node.add(n);
				}
			}
		}
		return node;
	}
	private BioModelNode createMolecularComponentPatternNode(MolecularComponentPattern molecularComponentPattern) {
		MolecularComponent mc = molecularComponentPattern.getMolecularComponent();
		BioModelNode node = new BioModelNode(molecularComponentPattern, true);
		ComponentStatePattern csp = molecularComponentPattern.getComponentStatePattern();
//		if(mc.getComponentStateDefinitions().size() > 0) {	// we don't show the state if nothing to choose from
//			StateLocal sl = new StateLocal(molecularComponentPattern);
//			BioModelNode ns = new BioModelNode(sl, false);
//			node.add(ns);
//		}
//		if(!molecularComponentPattern.getBondType().equals(BondType.None) || bShowDetails) {	// we save space by not showing the Bond.None
//			BondLocal bl = new BondLocal(molecularComponentPattern);
//			BioModelNode nb = new BioModelNode(bl, false);
//			node.add(nb);
//		}
		return node;
	}


	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_NAME)) {
			nodeChanged(rootNode);
		} else if(evt.getPropertyName().equals("entityChange")) {
			nodeChanged(rootNode);
		} else if (evt.getSource() == reactionRule || evt.getSource() instanceof SpeciesPattern || evt.getSource() instanceof MolecularTypePattern) {
			populateTree();
			
			Object source = evt.getSource();
			if (source == reactionRule) {
				if (participantType == ReactionRuleParticipantType.Reactant && evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_REACTANT_PATTERNS)) {
					List<ReactantPattern> oldValue = (List<ReactantPattern>) evt.getOldValue();
					if (oldValue != null) {
						for(ReactantPattern sp : oldValue) {
							RbmUtils.removePropertyChangeListener(sp.getSpeciesPattern(), this);
						}
					}
					List<ReactantPattern> newValue = (List<ReactantPattern>) evt.getNewValue();
					if (newValue != null) {
						for(ReactantPattern sp : newValue) {
							RbmUtils.addPropertyChangeListener(sp.getSpeciesPattern(), this);
						}
					}
				} else if (participantType == ReactionRuleParticipantType.Product && evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_PRODUCT_PATTERNS)) {
					List<ProductPattern> oldValue = (List<ProductPattern>) evt.getOldValue();
					if (oldValue != null) {
						for(ProductPattern sp : oldValue) {
							RbmUtils.removePropertyChangeListener(sp.getSpeciesPattern(), this);
						}
					}
					List<ProductPattern> newValue = (List<ProductPattern>) evt.getNewValue();
					if (newValue != null) {
						for(ProductPattern sp : newValue) {
							RbmUtils.addPropertyChangeListener(sp.getSpeciesPattern(), this);
						}
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
				if (evt.getSource().equals(MolecularComponentPattern.PROPERTY_NAME_COMPONENT_STATE)) {
					ComponentStatePattern oldValue = (ComponentStatePattern) evt.getOldValue();
					if (oldValue != null) {
						oldValue.removePropertyChangeListener(this);
					}
					ComponentStatePattern newValue = (ComponentStatePattern) evt.getNewValue();
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
				if (userObject instanceof ReactionRule) {				//TODO: untested!!!
					((ReactionRule) userObject).setName(inputString);
				}
			} else if (newValue instanceof MolecularComponentPattern) {
				MolecularComponentPattern newMcp = (MolecularComponentPattern) newValue;
				Object parentObject = parentNode == null ? null : parentNode.getUserObject();
				if (parentObject instanceof MolecularTypePattern) {
					MolecularTypePattern mtp = (MolecularTypePattern) parentObject;
					MolecularComponent mc = newMcp.getMolecularComponent();
					MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(mc);
											
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
						for(ReactantPattern rp : reactionRule.getReactantPatterns()) {
							rp.getSpeciesPattern().resolveBonds();
						}
						for(ProductPattern pp : reactionRule.getProductPatterns()) {
							pp.getSpeciesPattern().resolveBonds();
						}
					} else {
					}				
				}
			}
		} catch (Exception ex) {
			DialogUtils.showErrorDialog(ownerTree, ex.getMessage());			
		}
	}
	
	public void setReactionRule(ReactionRule newValue) {
		if (newValue == reactionRule) {
			return;
		}
		ReactionRule oldValue = reactionRule;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(this);
			List<? extends ReactionRuleParticipant> patterns = participantType == ReactionRuleParticipantType.Reactant ? oldValue.getReactantPatterns() : oldValue.getProductPatterns();
			for (ReactionRuleParticipant rrp : patterns) {
				RbmUtils.removePropertyChangeListener(rrp.getSpeciesPattern(), this);
			}
		}
		reactionRule = newValue;
		populateTree();
		if (newValue != null) {
			newValue.addPropertyChangeListener(this);
			List<? extends ReactionRuleParticipant> patterns = participantType == ReactionRuleParticipantType.Reactant ? newValue.getReactantPatterns() : newValue.getProductPatterns();
			for (ReactionRuleParticipant rrp : patterns) {
				RbmUtils.addPropertyChangeListener(rrp.getSpeciesPattern(), this);
			}
		}
	}
	
	public ReactionRuleParticipantType getParticipantType() {
		return participantType;
	}

	public void setBioModel(BioModel bioModel) {
		this.bioModel = bioModel;
	}
	
}