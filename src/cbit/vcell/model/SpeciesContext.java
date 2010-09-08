package cbit.vcell.model;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.PrintWriter;

import org.vcell.util.Cacheable;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;

import cbit.vcell.parser.*;
import cbit.vcell.units.VCUnitDefinition;

@SuppressWarnings("serial")
public class SpeciesContext implements Cacheable, Matchable, SymbolTableEntry, VetoableChangeListener, PropertyChangeListener {
	private KeyValue key = null;

	private transient Model model = null;
	private Species species = null;
	private Structure structure = null;
	private String fieldName = null/*new String()*/;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private boolean fieldHasOverride = false;

public SpeciesContext(KeyValue key, String name, Species species, Structure structure, boolean argHasOverride) {
	
	this.key = key;
	
	addVetoableChangeListener(this);
	addPropertyChangeListener(this);

	setHasOverride(argHasOverride);
	try {
		setName0(name);
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
	setSpecies(species);
	setStructure(structure);
}                  


public SpeciesContext(Species species, Structure structure) {
	this(null,createContextName(species,structure),species,structure,false);
}                  

public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}

public synchronized void addVetoableChangeListener(VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}

public boolean compareEqual(Matchable obj) {

	if (obj instanceof SpeciesContext){
		SpeciesContext sc = (SpeciesContext)obj;
		if (!Compare.isEqual(getSpecies(),sc.getSpecies())){
			return false;
		}
		if (!Compare.isEqual(getStructure(),sc.getStructure())){
			return false;
		}
		if (!Compare.isEqual(getName(),sc.getName())){
			return false;
		}
		if (!(getHasOverride() == sc.getHasOverride())){
			return false;
		}
		return true;
	}else{
		return false;
	}
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

public boolean getHasOverride() {
	return fieldHasOverride;
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
	if (structure instanceof Feature){
		return VCUnitDefinition.UNIT_uM;
	}else if (structure instanceof Membrane){
		return VCUnitDefinition.UNIT_molecules_per_um2;
	}else{
		System.out.println("SpeciesContext ... don't know what units are for SpeciesContext "+fieldName);
		return null;
	}
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

public void propertyChange(PropertyChangeEvent evt) {
	if (evt.getSource() == getSpecies() && evt.getPropertyName().equals("commonName")){
		try {
			// if not a user supplied SpeciesContext name, then propagate "cannonical" speciesContext name
			if (getHasOverride()==false){
				setName0(createContextName(getSpecies(), getStructure()));
			}
		}catch (PropertyVetoException e){
			e.printStackTrace(System.out);
		}
	}
	
	if (evt.getSource() == getStructure() && evt.getPropertyName().equals("name")){
		try {
			//
			// if not a user supplied SpeciesContext name, then propagate "cannonical" speciesContext name
			//
			if (getHasOverride()==false){
				setName0(createContextName(getSpecies(), getStructure()));
			}
		}catch (PropertyVetoException e){
			e.printStackTrace(System.out);
		}
	}
	if (evt.getSource() == this && evt.getPropertyName().equals("hasOverride")){
		try {
			//
			// if not a user supplied SpeciesContext name, then propagate "cannonical" speciesContext name
			//
			if (getHasOverride()==false){
				setName0(createContextName(getSpecies(), getStructure()));
			}
		}catch (PropertyVetoException e){
			e.printStackTrace(System.out);
		}
	}
}

public void refreshDependencies() {
	
	species.removePropertyChangeListener(this);
	species.addPropertyChangeListener(this);
	
	removePropertyChangeListener(this);
	addPropertyChangeListener(this);
	
	removeVetoableChangeListener(this);
	addVetoableChangeListener(this);
	
	structure.removePropertyChangeListener(this);
	structure.addPropertyChangeListener(this);
}

public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}

public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}

public void setHasOverride(boolean hasOverride) {
	boolean oldValue = fieldHasOverride;
	fieldHasOverride = hasOverride;
	firePropertyChange("hasOverride", new Boolean(oldValue), new Boolean(hasOverride));
}

public void setModel(Model argModel) {
	model = argModel;
}

public void setName(String name) throws PropertyVetoException {
	if (getHasOverride()==false){
		throw new PropertyVetoException("cannot override SpeciesContext.name, hasOverride is false",new PropertyChangeEvent(this,"name",getName(),name));
	}
	setName0(name);
}


private void setName0(String name) throws java.beans.PropertyVetoException {
	String oldValue = fieldName;
	fireVetoableChange("name", oldValue, name);
	fieldName = name;
	firePropertyChange("name", oldValue, name);
}

private void setSpecies(Species newSpecies){
	Species oldValue = this.species;
	if (oldValue != null){
		oldValue.removePropertyChangeListener(this);
	}
	this.species = newSpecies;
	if (this.species != null){
		this.species.addPropertyChangeListener(this);
	}
}

private void setStructure(Structure structure) {
	
	if(this.structure != null){
		this.structure.removePropertyChangeListener(this);
	}
	this.structure = structure;
	if(structure != null){
		this.structure.addPropertyChangeListener(this);
	}
}

public String toString() {
	StringBuffer sb = new StringBuffer();
	
	sb.append("SpeciesContext@"+Integer.toHexString(hashCode())+"(id="+getKey()+", name='"+getName()+"'");
	if (species != null){ sb.append(", species='"+species.getCommonName()+"'"); }
	if (structure != null){ sb.append(", structure='"+structure.getName()+"'"); }
	sb.append(", hasOverride="+getHasOverride()+")");
	
	return sb.toString();
}

public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {
	if (e.getSource()==this){
		if (e.getPropertyName().equals("diffusionRate")){
			double newValue = ((Double)e.getNewValue()).doubleValue();
			if (newValue<0.0){
				throw new PropertyVetoException("diffusionRate must be non-negative",e);
			}
		}else if (e.getPropertyName().equals("initialValue")){
			double newValue = ((Double)e.getNewValue()).doubleValue();
			if (newValue<0.0){
				throw new PropertyVetoException("initialValue must be non-negative",e);
			}
		}else if (e.getPropertyName().equals("name")){
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
}