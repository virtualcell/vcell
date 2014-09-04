package org.vcell.model.rbm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.model.Model.ModelParameter;

public class ReactionRule extends RbmElement implements PropertyChangeListener {
	public static final String PROPERTY_NAME_REACTANT_PATTERNS = "reactantPatterns";
	public static final String PROPERTY_NAME_PRODUCT_PATTERNS = "productPatterns";
	public static final String PROPERTY_NAME_FORWARD_RATE = "forwardRate";
	public static final String PROPERTY_NAME_REVERSE_RATE = "reverseRate";
	public static final String PROPERTY_NAME_REACTANT_WARNING = "reactantWarning";
	public static final String PROPERTY_NAME_PRODUCT_WARNING = "productWarning";
	
	private boolean bReversible;
	private String comments;
	private List<SpeciesPattern> reactantPatterns = new ArrayList<SpeciesPattern>();
	private List<SpeciesPattern> productPatterns = new ArrayList<SpeciesPattern>();
	public RbmParameter forwardRate;
	public RbmParameter reverseRate;
	private List<MolecularTypeMapping> molecularTypeMappings = new ArrayList<MolecularTypeMapping>();
	private String reactantWarning = null;
	private String productWarning = null;
	
	public enum ReactionRuleParticipantType {
		Reactant,
		Product,
	}
	/**
	 * allowable changes from reactant patterns to product patterns:
	 * 
	 * change in a component state
	 * removal of an internal bond within an existing species pattern
	 * adding an internal bond within an existing or new species pattern
	 * removal of a molecule of a molecular type
	 * adding a fully defined molecule
	 */

	ReactionRule(boolean reversible) {
		super();	
		this.bReversible = reversible;
	}
	
	public void addReactant(SpeciesPattern reactant) {
		List<SpeciesPattern> newValue = new ArrayList<SpeciesPattern>(reactantPatterns);
		newValue.add(reactant);
		setReactantPatterns(newValue);		
	}
	public void removeReactant(SpeciesPattern reactant) {
		if (reactantPatterns.contains(reactant)) {
			List<SpeciesPattern> newValue = new ArrayList<SpeciesPattern>(reactantPatterns);
			newValue.remove(reactant);
			setReactantPatterns(newValue);	
		}
	}
	
	public void setReactantPatterns(List<SpeciesPattern> newValue) {
		List<SpeciesPattern> oldValue = reactantPatterns;
		if (oldValue != null) {
			for (SpeciesPattern sp : oldValue) {
				sp.removePropertyChangeListener(this);
			}
		}
		reactantPatterns = newValue;
		if (newValue != null) {
			for (SpeciesPattern sp : newValue) {
				sp.addPropertyChangeListener(this);				
			}
			resolveBonds(ReactionRuleParticipantType.Reactant);
		}
		firePropertyChange(PROPERTY_NAME_REACTANT_PATTERNS, oldValue, newValue);
		checkReactantPatterns();
		checkProductPatterns();
	}
	
	public void checkReactantPatterns() {
		int cnt = 0;
		StringBuilder warning = new StringBuilder();
		for (SpeciesPattern sp : reactantPatterns) {
			++ cnt;
			if (sp.getMolecularTypePatterns().size() == 0) {
				warning.append("Reactant " + cnt + " does not have any moleculues.\n");
			}
		}
		setReactantWarning(warning.length() == 0 ? null : warning.toString());
	}
	
	public void checkProductPatterns() {
		int reactantCnt = 0;
		for (SpeciesPattern sp : reactantPatterns) {
			reactantCnt += sp.getMolecularTypePatterns().size();
		}
		int productCnt = 0;
		int cnt = 0;
		StringBuilder warning = new StringBuilder();
		for (SpeciesPattern sp : productPatterns) {
			productCnt += sp.getMolecularTypePatterns().size();
			++ cnt;
			if (sp.getMolecularTypePatterns().size() == 0) {
				warning.append("Product " + cnt + " does not have any moleculues.\n");
			}
		}
		if(productCnt != reactantCnt) {
			warning.append("the number of molecules in products does not match the number of molecules in reactants.");
		}
		setProductWarning(warning.length() == 0 ? null : warning.toString());
	}
	
	public void addProduct(SpeciesPattern product) {
		List<SpeciesPattern> newValue = new ArrayList<SpeciesPattern>(productPatterns);
		newValue.add(product);
		setProductPatterns(newValue);		
	}
	
	public void removeProduct(SpeciesPattern product) {
		if (productPatterns.contains(product)) {
			List<SpeciesPattern> newValue = new ArrayList<SpeciesPattern>(productPatterns);
			newValue.remove(product);
			setProductPatterns(newValue);
		}
	}	
	public void setProductPatterns(List<SpeciesPattern> newValue) {
		List<SpeciesPattern> oldValue = productPatterns;
		if (oldValue != null) {
			for (SpeciesPattern sp : oldValue) {
				sp.removePropertyChangeListener(this);
			}
		}
		productPatterns = newValue;
		if (newValue != null) {
			for (SpeciesPattern sp : newValue) {
				sp.addPropertyChangeListener(this);
			}
			resolveBonds(ReactionRuleParticipantType.Product);
		}
		firePropertyChange(PROPERTY_NAME_PRODUCT_PATTERNS, oldValue, newValue);
		checkProductPatterns();
	}
	public final List<SpeciesPattern> getReactantPatterns() {
		return reactantPatterns;
	}
	public final List<SpeciesPattern> getProductPatterns() {
		return productPatterns;
	}
	
	public final RbmParameter getForwardRate() {
		return forwardRate;
	}
	
	public final void setForwardRate(RbmParameter newValue) {
		RbmParameter oldValue = forwardRate;
		this.forwardRate = newValue;
		firePropertyChange(PROPERTY_NAME_FORWARD_RATE, oldValue, newValue);
	}

	public final RbmParameter getReverseRate() {
		return reverseRate;
	}

	public final void setReverseRate(RbmParameter newValue) {
		RbmParameter oldValue = reverseRate;
		this.reverseRate = newValue;
		firePropertyChange(PROPERTY_NAME_REVERSE_RATE, oldValue, newValue);
	}
	public final boolean isbReversible() {
		return bReversible;
	}
	public final void setReversible(boolean bReversible) {
		this.bReversible = bReversible;
	}
		
	private void resolveBonds(ReactionRuleParticipantType type) {
		HashMap<MolecularType, Integer> moleculeIndexMap = new HashMap<MolecularType, Integer>();
		List<SpeciesPattern> patterns = type == ReactionRuleParticipantType.Reactant ? reactantPatterns : productPatterns;
		for (SpeciesPattern speciesPattern : patterns) {
			List<MolecularTypePattern> molecularTypePatterns = speciesPattern.getMolecularTypePatterns();
			for (MolecularTypePattern mtp : molecularTypePatterns) {
				Integer index = moleculeIndexMap.get(mtp.getMolecularType());
				if (index == null) {
					index = 1;					
				} else {
					++ index;
				}
				mtp.setIndex(index);
				moleculeIndexMap.put(mtp.getMolecularType(), index);
			}
			speciesPattern.resolveBonds();
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof SpeciesPattern && evt.getPropertyName().equals(SpeciesPattern.PROPERTY_NAME_MOLECULAR_TYPE_PATTERNS)) {
			for (SpeciesPattern speciesPattern : reactantPatterns) {
				if (speciesPattern == evt.getSource()) {
					resolveBonds(ReactionRuleParticipantType.Reactant);
					checkReactantPatterns();
					checkProductPatterns();
					return;
				}
			}
			for (SpeciesPattern speciesPattern : productPatterns) {
				if (speciesPattern == evt.getSource()) {
					resolveBonds(ReactionRuleParticipantType.Product);
					checkProductPatterns();
					return;
				}
			}
		}		
	}
	
	private void setReactantWarning(String newValue) {
		if (reactantWarning == newValue) {
			return;
		}
		String oldValue = reactantWarning;
		reactantWarning = newValue;
		firePropertyChange(PROPERTY_NAME_REACTANT_WARNING, oldValue, newValue);
	}
	
	private void setProductWarning(String newValue) {
		if (productWarning == newValue) {
			return;
		}
		String oldValue = productWarning;
		productWarning = newValue;
		firePropertyChange(PROPERTY_NAME_PRODUCT_WARNING, oldValue, newValue);
	}
	
	public List<MolecularTypePattern> getMissingMoleculesInProducts() {
		List<MolecularTypePattern> patterns = new ArrayList<MolecularTypePattern>();
		for (SpeciesPattern r : reactantPatterns) {
			for (MolecularTypePattern rmtp : r.getMolecularTypePatterns()) {
				boolean bFound = false;
				for (SpeciesPattern p : productPatterns) {
					for (MolecularTypePattern pmtp : p.getMolecularTypePatterns()) {
						if (rmtp.getMolecularType() == pmtp.getMolecularType() && rmtp.getIndex() == pmtp.getIndex()) {
							bFound = true;
							break;
						}
					}
					if (bFound) {
						break;
					}
				}
				if (!bFound) {
					patterns.add(rmtp);
				}
			}
		}
		return patterns;
	}
}
