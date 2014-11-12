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
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.Cacheable;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

@SuppressWarnings("serial")
public class SpeciesContext implements Cacheable, Matchable, SymbolTableEntry, VetoableChangeListener, BioModelEntityObject, IssueSource {
	private KeyValue key = null;

	private transient Model model = null;
	private Species species = null;
	private Structure structure = null;
	private String fieldName = null/*new String()*/;
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
	if (!Compare.isEqual(getSpecies(),sc.getSpecies())){
		return false;
	}
	if (!Compare.isEqual(getStructure(),sc.getStructure())){
		return false;
	}
	if (!Compare.isEqual(getName(),sc.getName())){
		return false;
	}
	if (!Compare.isEqual(speciesPattern,sc.getSpeciesPattern())){
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
	sb.append(")");
	return sb.toString();
}

public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {
	if (e.getSource()==this){
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
		}
	}
}

public void writeTokens(PrintWriter pw) {
	pw.println("\t"+VCMODL.Context+" "+getName()+" "+getSpecies().getCommonName()+" "+VCMODL.BeginBlock+" ");
	pw.println("\t"+VCMODL.EndBlock+" ");
}

public String getTypeLabel() {
	return "Species";
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
							String msg = "Molecular types " + mtThat.getName() + " and " + mtpThis.getMolecularType().getName() + " do not compare equal.";
							issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
						} else {
							String msg = "All components in the Species Type definition must be present. Missing:";
							boolean isInvalid = false;
							for(int i=0; i< mtpThis.getComponentPatternList().size(); i++) {
								MolecularComponentPattern mcp = mtpThis.getComponentPatternList().get(i);
								if(mcp.isImplied()) {
									if(isInvalid) {
										msg += ",";
									}
									isInvalid = true;
									msg += " " + mcp.getMolecularComponent().getName();
								}
							}
							if(isInvalid) {
								msg += ".";
								issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
							}
						}
					}
				}
			}
			// if the component has states, the component pattern must have ONE of those states
			for(MolecularTypePattern mtpThis : speciesPattern.getMolecularTypePatterns()) {
				checkComponentStateConsistency(issueContext, issueList, mtpThis);
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
			String msg = "Multiple components with the same name are not yet supported.";
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
		} else {	// found exactly 1 component
			MolecularComponent mcThat = mcThatList[0];
			List<ComponentStateDefinition> csdListThat = mcThat.getComponentStateDefinitions();
			if(csdListThat.size() == 0) {	// component has no states, we check if mcpThis has any states... it shouldn't
				if(cspThis == null) {
					continue;				// all is well
				}
				if(!cspThis.isAny() || (cspThis.getComponentStateDefinition() != null)) {
					String msg = "Component pattern " + mcNameThis + " is in an invalid State.";
					issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
				}
			} else {						// we check if mcpThis has any of these states... it should!
				if((cspThis == null) || cspThis.isAny() || (cspThis.getComponentStateDefinition() == null)) {
					String msg = "Component pattern " + mcNameThis + " must be in an explicit State.";
					issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
				} else {
					String csdNameThis = cspThis.getComponentStateDefinition().getName();
					if(csdNameThis.isEmpty() || (mcThat.getComponentStateDefinition(csdNameThis) == null) ) {
						String msg = "Invalid State " + csdNameThis + " for component pattern " + mcNameThis;
						issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
					}
				}
			}
		}
	}
}
// I think this should never fire issues, that's why I keep issue severity to error to force further investigation
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
	// any component still present in hashThis it means that it has been deleted in the species types definition and is now invalid
	for (String key : hashThat.keySet()) {
		String msg = "All components in the Species Type definition must be present. Missing: " + key + ".";
		issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_ERROR));
	}
	for (String key : hashThis.keySet()) {
		String msg = "Component " + key + " is no longer defined in Species Type and must be removed.";
		issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_ERROR));
	}
}

private void checkBondsSufficiency(IssueContext issueContext, List<Issue> issueList, SpeciesPattern sp) {
	if(sp.getMolecularTypePatterns().size() == 0) {
		return;		// is this even possible??
	}
	if(sp.getMolecularTypePatterns().size() == 1) {
		MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(0);
		if(mtp.getComponentPatternList().size() < 2) {
			return;		// not enough types / components to form at least one bond bonds
		}
	}
	int numberOfComponents = 0;
	for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
		numberOfComponents += mtp.getComponentPatternList().size();
	}
	if(numberOfComponents < 2) {
		return;		// not enough components to establish bonds
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
		issueList.add(new Issue(parent, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
	}
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
