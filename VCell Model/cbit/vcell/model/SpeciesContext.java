package cbit.vcell.model;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.beans.*;
import java.util.*;
import cbit.vcell.parser.*;
import cbit.vcell.parser.Expression;
import cbit.vcell.model.*;
import cbit.util.Cacheable;
import cbit.util.Compare;
import cbit.util.KeyValue;
import cbit.util.Matchable;

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


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
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


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:04:03 PM)
 * @return java.lang.String
 * @param species cbit.vcell.model.Species
 * @param structure cbit.vcell.model.Structure
 */
private final static String createContextName(Species species, Structure structure) {
	return cbit.util.TokenMangler.fixTokenStrict(species.getCommonName()+"_"+structure.getName());
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public void fromTokens(cbit.vcell.math.CommentStringTokenizer tokens) throws Exception {
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


/**
 * This method was created in VisualAge.
 * @return double
 */
public double getConstantValue() throws ExpressionException {
	throw new ExpressionException(getName()+" is not constant");
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Expression
 * @exception java.lang.Exception The exception description.
 */
public Expression getExpression() {
	return null;
}


/**
 * Gets the hasOverride property (boolean) value.
 * @return The hasOverride property value.
 * @see #setHasOverride
 */
public boolean getHasOverride() {
	return fieldHasOverride;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getIndex() {
	return -1;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
public KeyValue getKey() {
	return key;
}


/**
 * Gets the name property (java.lang.String) value.
 * @return The name property value.
 * @see #setName
 */
public String getName() {
	return fieldName;
}


/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 10:30:36 AM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	if (model != null){
		return model.getNameScope();
	}else{
		return null;
	}
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Species
 */
public Species getSpecies() {
	return species;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Structure
 */
public Structure getStructure() {
	return structure;
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/00 10:19:02 PM)
 * @return java.lang.String
 */
public static String getTerm() {
	return "SpeciesContext";
}


/**
 * Insert the method's description here.
 * Creation date: (3/31/2004 3:06:22 PM)
 * @return cbit.vcell.units.VCUnitDefinition
 */
public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
	if (structure instanceof Feature){
		return cbit.vcell.units.VCUnitDefinition.UNIT_uM;
	}else if (structure instanceof Membrane){
		return cbit.vcell.units.VCUnitDefinition.UNIT_molecules_per_um2;
	}else{
		System.out.println("SpeciesContext ... don't know what units are for SpeciesContext "+fieldName);
		return null;
	}
}


/**
 * Accessor for the vetoPropertyChange field.
 */
protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isConstant() {
	return false;
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == getSpecies() && evt.getPropertyName().equals("commonName")){
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


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:16:46 PM)
 */
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

/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}


/**
 * Sets the hasOverride property (boolean) value.
 * @param hasOverride The new value for the property.
 * @see #getHasOverride
 */
public void setHasOverride(boolean hasOverride) {
	boolean oldValue = fieldHasOverride;
	fieldHasOverride = hasOverride;
	firePropertyChange("hasOverride", new Boolean(oldValue), new Boolean(hasOverride));
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 10:25:43 AM)
 * @param model cbit.vcell.model.Model
 */
public void setModel(Model argModel) {
	model = argModel;
}


/**
 * Sets the name property (java.lang.String) value.
 * @param name The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getName
 */
public void setName(String name) throws java.beans.PropertyVetoException {
	if (getHasOverride()==false){
		throw new PropertyVetoException("cannot override SpeciesContext.name, hasOverride is false",new PropertyChangeEvent(this,"name",getName(),name));
	}
	setName0(name);
}


/**
 * Sets the name property (java.lang.String) value.
 * @param name The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getName
 */
private void setName0(String name) throws java.beans.PropertyVetoException {
	String oldValue = fieldName;
	fireVetoableChange("name", oldValue, name);
	fieldName = name;
	firePropertyChange("name", oldValue, name);
}


/**
 * This method was created in VisualAge.
 * @param species cbit.vcell.model.Species
 */
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


/**
 * This method was created in VisualAge.
 * @param structure cbit.vcell.model.Structure
 */
private void setStructure(Structure structure) {
	
	if(this.structure != null){
		this.structure.removePropertyChangeListener(this);
	}
	this.structure = structure;
	if(structure != null){
		this.structure.addPropertyChangeListener(this);
	}
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String toString() {
	StringBuffer sb = new StringBuffer();
	
	sb.append("SpeciesContext@"+Integer.toHexString(hashCode())+"(id="+getKey()+", name='"+getName()+"'");
	if (species != null){ sb.append(", species='"+species.getCommonName()+"'"); }
	if (structure != null){ sb.append(", structure='"+structure.getName()+"'"); }
	sb.append(", hasOverride="+getHasOverride()+")");
	
	return sb.toString();
}


/**
 * This method was created in VisualAge.
 * @param e java.beans.PropertyChangeEvent
 */
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


/**
 * This method was created by a SmartGuide.
 * @param ps java.io.PrintStream
 * @exception java.lang.Exception The exception description.
 */
public void writeTokens(java.io.PrintWriter pw) {
	pw.println("\t"+VCMODL.Context+" "+getName()+" "+getSpecies().getCommonName()+" "+VCMODL.BeginBlock+" ");
	
	pw.println("\t"+VCMODL.EndBlock+" ");
}
}