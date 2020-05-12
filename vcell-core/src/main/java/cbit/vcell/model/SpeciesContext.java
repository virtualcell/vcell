/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.vcell.model.bngl.ParseException;
import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.Cacheable;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;
import org.vcell.util.Pair;
import org.vcell.util.document.KeyValue;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.units.VCUnitDefinition;

@SuppressWarnings("serial")
public class SpeciesContext implements Cacheable, Matchable, EditableSymbolTableEntry, VetoableChangeListener, BioModelEntityObject,
	IssueSource, Displayable, VCellSbmlName
{
	private KeyValue key = null;

	private transient Model model = null;
	private Species species = null;
	private Structure structure = null;
	private String fieldName = null/*new String()*/;
	private String sbmlId = null;
	private String sbmlName = null;
	private SpeciesPattern speciesPattern = null;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;

	private String speciesPatternString = null;

	public static final String PROPERTY_NAME_SPECIES_PATTERN = "speciesPattern";
	
	// store SBML unit for speciesContext from SBML species.
//	private transient VCUnitDefinition sbmlSpeciesUnit = null;

	
public SpeciesContext(KeyValue key, String name, Species species, Structure structure, SpeciesPattern speciesPattern) {
	this.key = key;
	addVetoableChangeListener(this);
	try {
		setName(name);
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
	this.species = species;
	setStructure(structure);
	this.speciesPattern = speciesPattern;
}            
public SpeciesContext(Species species, Structure structure, SpeciesPattern speciesPattern) {
	this(null,createContextName(species,structure),species,structure,speciesPattern);
}
public SpeciesContext(KeyValue key, String name, Species species, Structure structure) {
	this(key,name,species,structure,null);
}
public SpeciesContext(Species species, Structure structure) {
	this(null,createContextName(species,structure),species,structure,null);
}                  

public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}

public synchronized void addVetoableChangeListener(VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}

public boolean compareEqual(Matchable aThat) {
	if (this == aThat) {
		return true;
	}
	if (!(aThat instanceof SpeciesContext)) {
		return false;
	}
	SpeciesContext sc = (SpeciesContext)aThat;
	if (!Compare.isEqual(getSpecies(),sc.getSpecies())) {
		return false;
	}
	if (!Compare.isEqual(getStructure(),sc.getStructure())) {
		return false;
	}
	if (!Compare.isEqual(getName(),sc.getName())) {
		return false;
	}
	if (!Compare.isEqualOrNull(getSbmlName(),sc.getSbmlName())) {
		return false;
	}
	if (!Compare.isEqualOrNull(getSbmlId(),sc.getSbmlId())) {
		return false;
	}
	if (!Compare.isEqual(speciesPattern,sc.getSpeciesPattern())) {
		return false;
	}
	return true;
}

private final static String createContextName(Species species, Structure structure) {
	return org.vcell.util.TokenMangler.fixTokenStrict(species.getCommonName()+"_"+structure.getName());
}

public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}

public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}

public void fromTokens(CommentStringTokenizer tokens) throws Exception {
	String token = null;
	tokens.nextToken();  // read "{"
	while (tokens.hasMoreTokens()){
		token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCMODL.EndBlock)){
			break;
		}
		throw new Exception("SpeciesContext.fromTokens(), unexpected identifier "+token);
	}	
}

public double getConstantValue() throws ExpressionException {
	throw new ExpressionException(getName()+" is not constant");
}

public Expression getExpression() {
	return null;
}

public int getIndex() {
	return -1;
}

public KeyValue getKey() {
	return key;
}

public String getName() {
	return fieldName;
}

public String getSbmlId() {
	return sbmlId;
}
public String getSbmlName() {
	return sbmlName;
}

public NameScope getNameScope() {
	if (model != null){
		return model.getNameScope();
	}else{
		return null;
	}
}

protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}

public Species getSpecies() {
	return species;
}

public Structure getStructure() {
	return structure;
}

public static String getTerm() {
	return "SpeciesContext";
}

public VCUnitDefinition getUnitDefinition() {
	if (model != null) {
		return model.getUnitSystem().getConcentrationUnit(structure);
	} 
	return null;
}

protected VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
}

public boolean isConstant() {
	return false;
}

public void refreshDependencies() {
	
	removeVetoableChangeListener(this);
	addVetoableChangeListener(this);
}

public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}

public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}

public void setModel(Model argModel) {
	model = argModel;
}

public void setName(String name) throws PropertyVetoException {
	String oldValue = fieldName;
	fireVetoableChange("name", oldValue, name);
	fieldName = name;
	firePropertyChange("name", oldValue, name);
}


public static String fixSbmlName(String newString) {
	if(newString == null || newString.isEmpty()) {
		return null;
	}
	StringBuilder sb = new StringBuilder(newString);
	for (int i=0; i<sb.length(); i++){
		if (sb.charAt(i) == '\'') {
			sb.setCharAt(i,'_');
		}
		if (sb.charAt(i) == '\"') {
			sb.setCharAt(i,'_');
		}
	}
	String newValue = sb.toString();
	return newValue;
}
public void setSbmlName(String newString) throws PropertyVetoException {
	String oldValue = this.sbmlName;
	String newValue = fixSbmlName(newString);
	
	fireVetoableChange("sbmlName", oldValue, newValue);
	this.sbmlName = newValue;
	firePropertyChange("sbmlName", oldValue, newValue);
}
// this is setable only through SBML import, no need to check on it
public void setSbmlId(String newString) throws PropertyVetoException {
//	String oldValue = this.sbmlId;
//	fireVetoableChange("sbmlId", oldValue, newString);
	this.sbmlId = newString;
//	firePropertyChange("sbmlId", oldValue, newString);
}

public void setStructure(Structure structure) {
	Structure oldValue = this.structure;
	this.structure = structure;
	firePropertyChange("structuure", oldValue, structure);
}

public String toString() {
	StringBuffer sb = new StringBuffer();
	
	sb.append("SpeciesContext@"+Integer.toHexString(hashCode())+"(id="+getKey()+", name='"+getName()+"'");
	if (species != null){ sb.append(", species='"+species.getCommonName()+"'"); }
	if (structure != null){ sb.append(", structure='"+structure.getName()+"'"); }
	if (speciesPattern != null){sb.append(", speciesPatterns='"+speciesPattern.getMolecularTypePatterns().size()); }
	if (sbmlId != null){ sb.append(", sbmlId='"+sbmlId+"'"); }
	if (sbmlName != null){ sb.append(", sbmlName='"+sbmlName+"'"); }
	sb.append(")");
	return sb.toString();
}

public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {
	if (e.getSource()==this) {
		if (e.getPropertyName().equals("name")){
			String newName = (String)e.getNewValue();
			if (newName == null){
				throw new PropertyVetoException("species context name is null",e);
			}
			if (newName.length()<1){
				throw new PropertyVetoException("species context name is zero length",e);
			}
			if (!Character.isJavaIdentifierStart(newName.charAt(0))){
				throw new PropertyVetoException("species context name '"+newName+"' can't start with a '"+newName.charAt(0)+"'",e);
			}
			for (int i=1;i<newName.length();i++){
				if (!Character.isJavaIdentifierPart(newName.charAt(i))){
					throw new PropertyVetoException("species context name '"+newName+"' can't include a '"+newName.charAt(i)+"'",e);
				}
			}	
		} else if(e.getPropertyName().equals("sbmlName")) {
			String newName = (String)e.getNewValue();
			if(newName == null) {
				return;
			}
			// sbmlName may be null but it cannot contain illegal characters
//			if (!Character.isJavaIdentifierStart(newName.charAt(0))){
//				throw new PropertyVetoException("species context sbmlName '"+newName+"' can't start with a '"+newName.charAt(0)+"'", e);
//			}
//			for (int i=1;i<newName.length();i++){
//				if (!Character.isJavaIdentifierPart(newName.charAt(i))){
//					if(newName.charAt(i) != ' ') {
//						throw new PropertyVetoException("species context sbmlName '"+newName+"' can't include a '"+newName.charAt(i)+"'", e);
//					}
//				}
//			}	
		} else if(e.getPropertyName().equals("sbmlId")) {
			;
		}
	}
}

public String getTypeLabel() {
	return "Species";
}

// duplicate is only used for seed species (that have species pattern)
public static SpeciesContext duplicate(SpeciesContext oldSpecies, Structure s, Model m) throws ExpressionBindingException, PropertyVetoException {
	String newName = SpeciesContext.deriveSpeciesName(oldSpecies, m);
	Species newSpecies = new Species(newName, null);
	SpeciesContext newSpeciesContext = new SpeciesContext(null, newName, newSpecies, s);
	// we don't export BNGL models to SBML, so we have no reason to enforce the unique Id rule
	newSpeciesContext.setSbmlId(oldSpecies.getSbmlId());		// should be null
	newSpeciesContext.setSbmlName(oldSpecies.getSbmlName());
	SpeciesPattern newsp = new SpeciesPattern(m, oldSpecies.getSpeciesPattern());
	newSpeciesContext.setSpeciesPattern(newsp);
	m.addSpecies(newSpecies);
	m.addSpeciesContext(newSpeciesContext);
	return newSpeciesContext;
}
public static String deriveSpeciesName(SpeciesContext sc, Model m) {
	int count=0;
	String baseName = sc.getName() + "_";
	String newName = "";
	while (true) {
		newName = baseName + count;
		if (m.getSpecies(newName) == null && m.getSpeciesContext(newName) == null){
			break;
		}
		count++;
	}
	return newName;
}

public final SpeciesPattern getSpeciesPattern() {
	return speciesPattern;
}
public void parseSpeciesPatternString(Model model) {
	if(speciesPatternString.equalsIgnoreCase("null")) {
		System.out.println("Species Pattern String is 'NULL'.");
		return;
	}
if(speciesPatternString != null) {
		try {
			if(speciesPattern != null) {
				System.out.println("Species pattern already exists: " + speciesPattern.toString());
				return;
			}
			SpeciesPattern sp = RbmUtils.parseSpeciesPattern(speciesPatternString, model);
			setSpeciesPattern(sp);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException("Bad format for repository species pattern string: " + e.getMessage());
		}
	}
}

public void setSpeciesPattern(SpeciesPattern newValue) {
	if(this.speciesPattern == null && newValue == null) {
		return;
	}
	SpeciesPattern oldValue = speciesPattern;
	this.speciesPattern = newValue;
	if(newValue != null) {
		this.speciesPattern.resolveBonds();
	} else {
		// can happen when you delete the last molecular type pattern of a species pattern (in the species pattern tree property of a species context)
		System.out.println("Species pattern is null.");
	}
	firePropertyChange(PROPERTY_NAME_SPECIES_PATTERN, oldValue, newValue);
}
public void setSpeciesPatternString(String speciesPatternString) {
	this.speciesPatternString  = speciesPatternString;
}
public String getSpeciesPatternString() {
	return this.speciesPatternString;
}

public boolean hasSpeciesPattern() {
	return speciesPattern == null ? false : true;
}

public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
	issueContext = issueContext.newChildContext(ContextType.SpeciesContext, this);
	if(species == null) {
		issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Species is null", Issue.SEVERITY_WARNING));
	} else {
		if(speciesPattern != null) {
			checkBondsSufficiency(issueContext, issueList,speciesPattern);
			speciesPattern.checkSpeciesPattern(issueContext, issueList);
			speciesPattern.gatherIssues(issueContext, issueList);
			for(MolecularType mtThat : model.getRbmModelContainer().getMolecularTypeList()) {
				for(MolecularTypePattern mtpThis : speciesPattern.getMolecularTypePatterns()) {
					if(mtThat.getName().equals(mtpThis.getMolecularType().getName())) {
						
						checkMolecularTypeConsistency(issueContext, issueList, mtThat, mtpThis);	// this should never fire issues!!!
						
						if(!mtThat.compareEqual(mtpThis.getMolecularType())) {
							String msg = MolecularType.typeName + "s " + mtThat.getName() + " and " + mtpThis.getMolecularType().getName() + " do not compare equal.";
							issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
						} else {
// TODO: components cannot be missing, see if this test is really needed
// TODO: the isImplied test is wrong here
//							String msg = "All components in the " + mtThat.getDisplayType() + " definition must be present. Missing:";
//							boolean isInvalid = false;
//							for(int i=0; i< mtpThis.getComponentPatternList().size(); i++) {
//								MolecularComponentPattern mcp = mtpThis.getComponentPatternList().get(i);
//								if(mcp.isImplied()) {
//									if(isInvalid) {
//										msg += ",";
//									}
//									isInvalid = true;
//									msg += " " + mcp.getMolecularComponent().getName();
//								}
//							}
//							if(isInvalid) {
//								msg += ".";
//								issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
//							}
						}
					}
				}
			}
			// if the component has states, the component pattern must have ONE of those states
			for(MolecularTypePattern mtpThis : speciesPattern.getMolecularTypePatterns()) {
				checkComponentStateConsistency(issueContext, issueList, mtpThis);
			}
			// the bond must not be ambiguous - seed species must be either unbound or specified (explicit) bond
			for(MolecularTypePattern mtpThis : speciesPattern.getMolecularTypePatterns()) {
				for(MolecularComponentPattern mcpThis : mtpThis.getComponentPatternList()) {
					if(mcpThis.getBondType() == BondType.Possible || mcpThis.getBondType() == BondType.Exists) {
						String msg = "The Bond of " + mcpThis.getMolecularComponent().getDisplayName() + " must be 'Unbound' or explicit.";
						issueList.add(new Issue(this, mcpThis, issueContext, IssueCategory.Identifiers, msg, null, Issue.Severity.ERROR));
					}
				}
			}
			// the structure must be in the list of anchors, if anchors are being used
			for (MolecularTypePattern mtp : speciesPattern.getMolecularTypePatterns()) {
				MolecularType mt = mtp.getMolecularType();
				if(mt.isAnchorAll()) {
					continue;	// no restrictions for this molecular type
				}
				if(!mt.getAnchors().contains(structure)) {
					String message = "The Structure " + structure.getName() + " is not allowed for the Molecule " + mt.getDisplayName();
					issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, message, Issue.Severity.ERROR));
				}
			}
			// check for reserved name 'trash'
			for (MolecularTypePattern mtp : speciesPattern.getMolecularTypePatterns()) {
				String name = mtp.getMolecularType().getDisplayName().toLowerCase();
				if(name.equals("trash")) {
					String message = "'Trash' is a reserved NFSim keyword and it cannot be used as a seed species.";
					issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, message, Issue.Severity.WARNING));
				}
			}
		}
		if(sbmlName == null) {
			;
		} else {
			if(sbmlName.isEmpty()) {
				String message = "SbmlName cannot be an empty string.";
				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, message, Issue.Severity.ERROR));
			} else {
//				String message = null;
//				if (!Character.isJavaIdentifierStart(sbmlName.charAt(0))){
//					message = "SbmlName '"+sbmlName+"' can't start with a '"+sbmlName.charAt(0)+"'.";
//					issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, message, Issue.Severity.ERROR));
//				}
//				for (int i=1;i<sbmlName.length();i++){
//					if (!Character.isJavaIdentifierPart(sbmlName.charAt(i))){
//						message = "species context sbmlName '"+sbmlName+"' can't include a '"+sbmlName.charAt(i)+"'.";
//						issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, message, Issue.Severity.ERROR));
//					}
//				}	
			}
		}
		if(sbmlId == null) {
			;		// that's okay
		} else {
			if(sbmlId.isEmpty()) {
				String message = "SbmlId cannot be an empty string.";
				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, message, Issue.Severity.ERROR));
			}
		}
	}
}
public void checkComponentStateConsistency(IssueContext issueContext, List<Issue> issueList, MolecularTypePattern mtpThis) {
	issueContext = issueContext.newChildContext(ContextType.SpeciesContext, this);
	MolecularType mtThat = mtpThis.getMolecularType();
	for(MolecularComponentPattern mcpThis : mtpThis.getComponentPatternList()) {
		ComponentStatePattern cspThis = mcpThis.getComponentStatePattern();
		String mcNameThis = mcpThis.getMolecularComponent().getName();
		MolecularComponent[] mcThatList = mtThat.getMolecularComponents(mcNameThis);
		if(mcThatList.length == 0) {
			System.out.println("we already fired an issue about component missing");
			continue;	// nothing to do here, we already fired an issue about component missing 
		} else if(mcThatList.length > 1) {
			String msg = "Multiple " + MolecularComponent.typeName + "s with the same name are not yet supported.";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_ERROR));
		} else {	// found exactly 1 component
			MolecularComponent mcThat = mcThatList[0];
			List<ComponentStateDefinition> csdListThat = mcThat.getComponentStateDefinitions();
			if(csdListThat.size() == 0) {	// component has no states, we check if mcpThis has any states... it shouldn't
				if(cspThis == null) {
					continue;				// all is well
				}
				if(!cspThis.isAny() || (cspThis.getComponentStateDefinition() != null)) {
					String msg = MolecularComponentPattern.typeName + " " + mcNameThis + " is in an invalid State.";
					issueList.add(new Issue(this, mcpThis, issueContext, IssueCategory.Identifiers, msg, null, Issue.SEVERITY_WARNING));
				}
			} else {						// we check if mcpThis has any of these states... it should!
				if((cspThis == null) || cspThis.isAny() || (cspThis.getComponentStateDefinition() == null)) {
					String msg = MolecularComponentPattern.typeName + " " + mcNameThis + " must be in an explicit State.";
					issueList.add(new Issue(this, mcpThis, issueContext, IssueCategory.Identifiers, msg, null, Issue.Severity.ERROR));
				} else {
					String csdNameThis = cspThis.getComponentStateDefinition().getName();
					if(csdNameThis.isEmpty() || (mcThat.getComponentStateDefinition(csdNameThis) == null) ) {
						String msg = "Invalid State " + csdNameThis + " for " + MolecularComponentPattern.typeName + " " + mcNameThis;
						issueList.add(new Issue(this, mcpThis, issueContext, IssueCategory.Identifiers, msg, null, Issue.SEVERITY_WARNING));
					}
				}
			}
		}
	}
}
// I think this should never fire issues, that's why I keep issue severity to error to force further investigation
// TODO: oct 30, 2015: hasn't fired in 9 months, we replace the issues with exception just to protect ourselves against regressions
private void checkMolecularTypeConsistency(IssueContext issueContext, List<Issue> issueList, MolecularType mtThat, MolecularTypePattern mtpThis) {
	issueContext = issueContext.newChildContext(ContextType.SpeciesContext, this);
	Map<String, MolecularComponent> hashThat = new HashMap<String, MolecularComponent>();
	Map<String, MolecularComponent> hashThis = new HashMap<String, MolecularComponent>();
	for(MolecularComponent mcThat : mtThat.getComponentList()) {
		hashThat.put(mcThat.getName(), mcThat);
	}
	for(MolecularComponentPattern mcpThis : mtpThis.getComponentPatternList()) {
		hashThis.put(mcpThis.getMolecularComponent().getName(), mcpThis.getMolecularComponent());
	}
	Iterator<Entry<String, MolecularComponent>> it = hashThat.entrySet().iterator();
	while (it.hasNext()) {
		String key = ((Map.Entry<String, MolecularComponent>)it.next()).getKey();
		if(hashThis.containsKey(key)) {
			it.remove();
			hashThis.remove(key);
		}
	}
	// if any component still present in hashThat it means that it's missing in the species pattern of this species context
	// any component still present in hashThis it means that it has been deleted in the molecular types definition and is now invalid
	for (String key : hashThat.keySet()) {
		String msg = "All components in the " + mtThat.getDisplayType() + " definition must be present. Missing: " + key + ".";
		throw new RuntimeException(msg);
//		issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_ERROR));
	}
	for (String key : hashThis.keySet()) {
		String msg = "Component " + key + " is no longer defined for the " + mtThat.getDisplayType() + " and must be removed.";
		throw new RuntimeException(msg);
//		issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_ERROR));
	}
}

private void checkBondsSufficiency(IssueContext issueContext, List<Issue> issueList, SpeciesPattern sp) {

	if(sp.getMolecularTypePatterns().size() < 2) {
		return;
	}
	int numberOfMolecularTypeCandidates = 0;
	for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
		if(mtp.getComponentPatternList().size() > 0) {
			numberOfMolecularTypeCandidates++;
		}
	}
	if(numberOfMolecularTypeCandidates < 2) {
		return;		// we need at least 2 molecular types with at least 1 component each
	}
	
	boolean atLeastOneBad = false;
	for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
		boolean bondSpecifiedExists = false;
		for(MolecularComponentPattern mcp : mtp.getComponentPatternList()) {
			if (mcp.getBondType() == BondType.Specified) {
				Bond b = mcp.getBond();
				if(b != null) {
					bondSpecifiedExists = true;
					break;
				}
			}
		}
		if(!bondSpecifiedExists) {
			atLeastOneBad = true;
		}
	}
	if(atLeastOneBad) {
		String msg = "Each Molecular Pattern of the Species Pattern " + sp.toString() + " needs at least one explicit Bond.\n";
		IssueSource parent = issueContext.getContextObject();
		issueList.add(new Issue(parent, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
	}
}
public void findComponentUsage(MolecularType mt, MolecularComponent mc, Map<String, Pair<Displayable, SpeciesPattern>> usedHere) {
	if(!hasSpeciesPattern()) {
		return;
	}
	SpeciesPattern sp = getSpeciesPattern();
	for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
		if(mtp.getMolecularType() == mt) {
			List<MolecularComponentPattern> componentPatterns = mtp.getComponentPatternList();
			for (MolecularComponentPattern mcp : componentPatterns) {
				if(mcp.getMolecularComponent() == mc) {		// here all components are always in use
					if(mcp.getBond() != null) {				// we only care about the components with a bond
						String key = sp.getDisplayName();
						key = getDisplayType() + getDisplayName() + key;
						usedHere.put(key, new Pair<Displayable, SpeciesPattern>(this, sp));
					}
				}
			}
		}
	}
}
public void findStateUsage(MolecularType mt, MolecularComponent mc, ComponentStateDefinition csd,
		Map<String, Pair<Displayable, SpeciesPattern>> usedHere) {
	if(!hasSpeciesPattern()) {
		return;
	}
	SpeciesPattern sp = getSpeciesPattern();
	for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
		if(mtp.getMolecularType() == mt) {
			List<MolecularComponentPattern> componentPatterns = mtp.getComponentPatternList();
			for (MolecularComponentPattern mcp : componentPatterns) {
				if(mcp.getMolecularComponent() == mc) {		// here some state is always in use if available
					ComponentStatePattern csp = mcp.getComponentStatePattern();
					if(csp == null) {
						System.out.println("This component " + mc.getName() + " should have had some State specified.");
						continue;
					}
					if((csp.getComponentStateDefinition() == csd) && (mcp.getBond() != null)) {		// we only care if there's a bond
						String key = sp.getDisplayName();
						key = getDisplayType() + getDisplayName() + key;
						usedHere.put(key, new Pair<Displayable, SpeciesPattern>(this, sp));
					}
				}
			}
		}
	}
}

public boolean deleteComponentFromPatterns(MolecularType mt, MolecularComponent mc) {
	if(!hasSpeciesPattern()) {
		return true;
	}
	SpeciesPattern sp = getSpeciesPattern();
	for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
		if(mtp.getMolecularType() == mt) {
			List<MolecularComponentPattern> componentPatterns = mtp.getComponentPatternList();
			for (Iterator<MolecularComponentPattern> iterator = componentPatterns.iterator(); iterator.hasNext();) {
				MolecularComponentPattern mcp = iterator.next();
				if (mcp.getMolecularComponent() == mc) {
					iterator.remove();
				}
			}					
		}
	}
	sp.resolveBonds();
	return true;
}
public boolean deleteStateFromPatterns(MolecularType mt, MolecularComponent mc, ComponentStateDefinition csd) {
	if(!hasSpeciesPattern()) {
		return true;
	}
	SpeciesPattern sp = getSpeciesPattern();
	for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
		if(mtp.getMolecularType() == mt) {
			List<MolecularComponentPattern> componentPatterns = mtp.getComponentPatternList();
			for(MolecularComponentPattern mcp : componentPatterns) {
				if (!(mcp.getMolecularComponent() == mc)) {
					continue;
				}
				ComponentStatePattern csp = mcp.getComponentStatePattern();
				if(csp == null) {
					continue;
				}
				if(csp.isAny()) {
					if(mc.getComponentStateDefinitions().size() == 1) {
						mcp.setComponentStatePattern(null);
					}
					continue;
				}
				if(csp.getComponentStateDefinition() == csd) {
					if(mc.getComponentStateDefinitions().size() == 1) {
						// we are about to delete the last possible state, so we set the ComponentStatePattern to null
						mcp.setComponentStatePattern(null);
					} else {
						csp = new ComponentStatePattern();		// set to Any (may result in an Issue being raised)
						mcp.setComponentStatePattern(csp);
					}
				}
			}
		}
	}
	return true;
}

public static final String typeName = "Species";
@Override
public final String getDisplayName() {
	return getName();
}
@Override
public final String getDisplayType() {
	return typeName;
}
@Override
public boolean isExpressionEditable() {
	return false;
}
@Override
public boolean isUnitEditable() {
	return false;
}
@Override
public boolean isNameEditable() {
	return true;
}
@Override
public void setExpression(Expression expression) throws ExpressionBindingException {
	throw new RuntimeException("cannot set expression on a species context");
}
@Override
public void setUnitDefinition(VCUnitDefinition unit) throws PropertyVetoException {
	throw new RuntimeException("cannot set unit on a species context");
}
@Override
public String getDescription() {
	return "Species '"+getName()+"' defined in compartment '"+getStructure().getName()+"'";
}
@Override
public void setDescription(String description) throws PropertyVetoException {
	throw new RuntimeException("cannot set description on a species context");
}
@Override
public boolean isDescriptionEditable() {
	return false;
}


/*
public VCUnitDefinition getSbmlSpeciesUnit() {
	return sbmlSpeciesUnit;
}
public void setSbmlSpeciesUnit(VCUnitDefinition sbmlSpeciesUnit) {
	this.sbmlSpeciesUnit = sbmlSpeciesUnit;
}
*/

}
