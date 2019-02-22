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
import org.vcell.util.document.KeyValue;
/**
 * Insert the type's description here.
 * Creation date: (4/23/2003 1:42:35 PM)
 * @author: Frank Morgan
 */
public class ReactionDescription implements java.io.Serializable, org.vcell.util.Cacheable{
	
	public enum ReactionType {
		REACTTYPE_FLUX_REVERSIBLE("flux"),
		REACTTYPE_SIMPLE_REVERSIBLE("simple"),
		REACTTYPE_FLUX_IRREVERSIBLE("flux_ir"),
		REACTTYPE_SIMPLE_IRREVERSIBLE("simple_ir"),
		UNKNOWN_REACTIONTYPE("unknown");
		
		public final String databaseName;
		
		ReactionType(String databaseName){
			this.databaseName = databaseName;
		}
		
		public static ReactionType fromDatabaseName(String dbName){
			for (ReactionType rt : values()){
				if (rt.databaseName.equals(dbName)){
					return rt;
				}
			}
			return null;
		}
	}
	
	private String reactionName = null;
	//
	//cbit.vcell.modeldb.ReactStepTable.id
	//will be null for Dictionary reactions
	private org.vcell.util.document.KeyValue vcellRXID = null;
	private KeyValue bioModelID = null;
	private KeyValue structRef = null;
	//
	private java.util.Vector reactionElements = new java.util.Vector();// SpeciesDescritpion
	private java.util.Vector reSpeciesContextNames = null;// String (Original SpeciesContext Names from Database)
	private java.util.Vector reStructNames = null;// String (Original Struct Names from Database)
	private java.util.Vector stoich = new java.util.Vector(); // Integer
	private java.util.Vector type = new java.util.Vector();// Character
	private java.util.Vector resolvedSC = new java.util.Vector();//SpeciesContexts
	//
	private ReactionType reactionType = null;
	//
	//
	public static final char RX_ELEMENT_PRODUCT = 'P';  // EnzymeReactionTable.REACANT_TYPE_PRODUCT;
	public static final char RX_ELEMENT_REACTANT = 'R'; // EnzymeReactionTable.REACANT_TYPE_REACTANT;
	public static final char RX_ELEMENT_FLUX = 'F';
	public static final char RX_ELEMENT_CATALYST = 'C';

	static {
		System.out.println("cbit.vcell.dictionary.ReactionDescription: DB constants should be abstracted away");
	}

public ReactionDescription(String argReactionName){
	this(argReactionName,ReactionType.UNKNOWN_REACTIONTYPE,null,null,null);
}


public ReactionDescription(String argReactionName,ReactionType argReactionType,org.vcell.util.document.KeyValue argVCellRXID,KeyValue argBioModelID,KeyValue structRef){
	
	if(argReactionName == null){
		throw new IllegalArgumentException("ReactionName cannot be null");
	}
	this.reactionName = argReactionName;
	this.vcellRXID = argVCellRXID;
	this.reactionType = argReactionType;
	this.bioModelID = argBioModelID;
	this.structRef = structRef;
}


		public void addReactionElement(SpeciesDescription argReactElement,int argStoich,char argType){
			//This method for adding Dictionary Reactions (they have no original structures)
			addReactionElement(argReactElement,null,null,argStoich,argType);
		}


public void addReactionElement(SpeciesDescription argReactElement,String originalSpeciesContextName,String originalStructName,int argStoich,char argType){
	if(argType != RX_ELEMENT_REACTANT &&
		argType != RX_ELEMENT_PRODUCT &&
		argType != RX_ELEMENT_FLUX &&
		argType != RX_ELEMENT_CATALYST){
		throw new IllegalArgumentException("Unknown dictionary Reaction Element");
	}
	if(argType == RX_ELEMENT_PRODUCT || argType == RX_ELEMENT_REACTANT){
		if(argStoich < 1){
			throw new IllegalArgumentException("Stoichiometry can't be less than 1 for reactants or products");
		}
//		if(!getReactionType().equals(UNKNOWN_REACTIONTYPE) 
//				&& !getReactionType().equals(cbit.vcell.modeldb.DatabaseConstants.REACTTYPE_SIMPLE_REVERSIBLE) 
//				&& !getReactionType().equals(cbit.vcell.modeldb.DatabaseConstants.REACTTYPE_SIMPLE_IRREVERSIBLE)){
//			throw new IllegalArgumentException("Illegal Attempt to add non-flux to "+getReactionType()+ " reaction type");
//		}	
	}
	if(argType == RX_ELEMENT_CATALYST && argStoich != 0){
		throw new IllegalArgumentException("Stoichiometry must be 0 for Catalysts");	
	}
	if(argType == RX_ELEMENT_FLUX && argStoich != 1){
		if(argStoich != 1){
			throw new IllegalArgumentException("Stoichiometry must be 1 for Fluxes");
		}
		if(getReactionType() != ReactionType.UNKNOWN_REACTIONTYPE 
				&& getReactionType() != ReactionType.REACTTYPE_FLUX_REVERSIBLE
				&& getReactionType() != ReactionType.REACTTYPE_FLUX_IRREVERSIBLE){
			throw new IllegalArgumentException("Illegal Attempt to add flux to "+getReactionType()+ " reaction type");
		}	
	}
	if(argReactElement == null){
		throw new IllegalArgumentException("Dictionary ReactionElement Can't be null");				
	}
	boolean bTypesOK = true;
	//boolean bSecondFlux = false;
	for(int i=0;i<reactionElements.size();i+= 1){
		char currentType = ((Character)type.get(i)).charValue();
		if(argType == RX_ELEMENT_FLUX){
			if(	currentType == RX_ELEMENT_PRODUCT || 
				currentType == RX_ELEMENT_REACTANT){
					bTypesOK=false;
			}
		}else if(argType == RX_ELEMENT_PRODUCT  || argType == RX_ELEMENT_REACTANT){
			if(currentType == RX_ELEMENT_FLUX){
				bTypesOK=false;
			}
			if(argType == currentType && argReactElement.equals(reactionElements.get(i))){
				throw new RuntimeException("duplicate reaction element");
			}
		}
		if(!bTypesOK){
			throw new IllegalArgumentException("Can't mix flux types and reactant-product types");	
		}
	}
	if(originalSpeciesContextName == null){
		if(reSpeciesContextNames != null){
			throw new RuntimeException("Original SpeciesConext names must all be null or all not-null");
		}
	}else if(reactionElements.size() != 0 && reSpeciesContextNames == null){
		throw new RuntimeException("Original SpeciesConext names must all be null or all not-null");
	}
	if(reactionElements.contains(argReactElement)){
		throw new RuntimeException("duplicate reaction element");
	}
	//Sort the reactionelements by preferredName into type groups, put flux at top
	int sortedIndex = 0;
	if(argType != RX_ELEMENT_FLUX){
		boolean bFoundMyStart = false;
		for(int i=0;i < reactionElements.size();i+= 1){
			String currentPreferreName = ((SpeciesDescription)reactionElements.get(i)).getPreferredName();
			char currentType = ((Character)type.get(i)).charValue();
			if(currentType == argType){
				bFoundMyStart = true;
			}else if(bFoundMyStart){//Past the type group
				break;
			}
			if(bFoundMyStart && argReactElement.getPreferredName().compareToIgnoreCase(currentPreferreName) < 0){
				break;
			}
			sortedIndex+= 1;
			
		}
	}
	//
	reactionElements.add(sortedIndex,argReactElement);
	stoich.add(sortedIndex,new Integer(argStoich));
	type.add(sortedIndex,new Character(argType));
	resolvedSC.add(sortedIndex,null);
	if(originalSpeciesContextName != null){
		if(reSpeciesContextNames == null){
			reSpeciesContextNames = new java.util.Vector();
		}
		reSpeciesContextNames.add(sortedIndex,originalSpeciesContextName);
		if(reStructNames == null){
			reStructNames = new java.util.Vector();
		}
		reStructNames.add(sortedIndex,originalStructName);
	}
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/2003 12:52:34 PM)
 * @return boolean
 * @param dbfs cbit.vcell.dictionary.SpeciesDescription
 */
public boolean containsRXElement(SpeciesDescription spd) {
	return reactionElements.contains(spd);
}


public int elementCount(){
	return reactionElements.size();
}


/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 12:36:12 PM)
 * @return cbit.vcell.dictionary.SpeciesDescription[]
 */
public SpeciesDescription[] getCatalysts() {
	java.util.Vector catalysts = new java.util.Vector();
	for(int i=0;i<reactionElements.size();i+= 1){
		int currentType = ((Character)type.get(i)).charValue();
		if(currentType == RX_ELEMENT_CATALYST){
			catalysts.add(reactionElements.get(i));
		}
	}

	if(catalysts.size() != 0){
		SpeciesDescription[] results = new SpeciesDescription[catalysts.size()];
		catalysts.copyInto(results);
		return results;
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (8/23/2003 1:55:10 PM)
 * @return int
 * @param dbfs cbit.vcell.dictionary.SpeciesDescription
 */
public int getDBSDIndex(SpeciesDescription spd) {
	for(int i=0;i<reactionElements.size();i+= 1){
		if(reactionElements.get(i).equals(spd)){
			return i;
		}
	}
	throw new RuntimeException("couldn't find "+spd.toString());
}


public SpeciesDescription getFluxCarrier(){
	for(int i=0;i < reactionElements.size();i+= 1){
		if(isFlux(i)){
			return (SpeciesDescription)(reactionElements.get(i));
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (8/23/2003 1:06:12 PM)
 * @return int
 */
public int getFluxIndexInside() {
	//second flux is inside flux
	//
	//
	if(!isFluxReaction()){
		throw new RuntimeException("method not applicable for non-flux reaction");
	}
	return 1;
}


/**
 * Insert the method's description here.
 * Creation date: (8/23/2003 1:06:12 PM)
 * @return int
 */
public int getFluxIndexOutside() {
	//first flux is outside flux
	//
	//
	if(!isFluxReaction()){
		throw new RuntimeException("method not applicable for non-flux reaction");
	}
	return 0;
}


/**
 * Insert the method's description here.
 * Creation date: (8/8/2003 5:25:30 PM)
 * @return cbit.vcell.dictionary.SpeciesDescription
 * @param species cbit.vcell.model.Species
 */
public SpeciesDescription getMappedDBSD(SpeciesContext sc) {
	
	for(int i=0;i < resolvedSC.size();i+= 1){
		if(sc.compareEqual((SpeciesContext)resolvedSC.get(i))){
			return (SpeciesDescription)reactionElements.get(i);
		}
	}
	return null;
}


public String getOrigSpeciesContextName(int index){
	if(reSpeciesContextNames == null){
		throw new RuntimeException("Reaction has no Orig Struct Names");
	}
	return ((String)reSpeciesContextNames.get(index));
}
public String getOrigStructName(int index){
	if(reStructNames == null){
		throw new RuntimeException("Reaction has no Orig Struct Names");
	}
	return ((String)reStructNames.get(index));
}


public SpeciesDescription getReactionElement(int index){
	return (SpeciesDescription)reactionElements.get(index);
}


public String getReactionName(){
	return reactionName;
}


public ReactionType getReactionType(){
	return reactionType;
}


/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 7:17:16 PM)
 * @return cbit.vcell.model.Structure
 * @param dbfs cbit.vcell.dictionary.SpeciesDescription
 */
public SpeciesContext getResolved(int index) {

	return (SpeciesContext)resolvedSC.get(index);
}


/**
 * Insert the method's description here.
 * Creation date: (8/8/2003 5:25:30 PM)
 * @return cbit.vcell.dictionary.SpeciesDescription
 * @param species cbit.vcell.model.Species
 */
public int getResolvedIndex(SpeciesContext resolveToSC) {

	for(int i=0;i < resolvedSC.size();i+= 1){
		if(resolvedSC.get(i).equals(resolveToSC)){
			return i;
		}
	}
	return -1;
}


		public int getStoich(int index){
			return ((Integer)stoich.get(index)).intValue();
		}


		public char getType(int index){
			return ((Character)type.get(index)).charValue();
		}


public org.vcell.util.document.KeyValue getVCellRXID(){
	return vcellRXID;
}

public org.vcell.util.document.KeyValue getVCellBioModelID(){
	return bioModelID;
}
public org.vcell.util.document.KeyValue getVCellStructRef(){
	return structRef;
}

/**
 * Insert the method's description here.
 * Creation date: (8/19/2003 1:53:20 PM)
 * @return boolean
 */
public boolean hasOrigStructNames() {
	return reSpeciesContextNames != null;
}


/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 3:41:13 PM)
 * @return boolean
 * @param i int
 */
public boolean isCatalyst(int i) {
	return (getType(i) == RX_ELEMENT_CATALYST);
}


/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 6:55:07 PM)
 * @return boolean
 */
public boolean isCompletelyResolved() {

	for(int i = 0;i < resolvedSC.size();i+= 1){
		if(resolvedSC.get(i) == null){
			return false;
		}
	}
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 3:41:13 PM)
 * @return boolean
 * @param i int
 */
public boolean isFlux(int i) {
	return (getType(i) == RX_ELEMENT_FLUX);
}


/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 3:41:13 PM)
 * @return boolean
 * @param i int
 */
public boolean isFluxReaction() {
	for(int i=0;i<type.size();i+= 1){
		char currentType = ((Character)type.get(i)).charValue();
		if(currentType == RX_ELEMENT_FLUX){
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 3:41:13 PM)
 * @return boolean
 * @param i int
 */
public boolean isProduct(int i) {
	return (getType(i) == RX_ELEMENT_PRODUCT);
}


/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 3:41:13 PM)
 * @return boolean
 * @param i int
 */
public boolean isReactant(int i) {
	return (getType(i) == RX_ELEMENT_REACTANT);
}


/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 6:50:39 PM)
 * @param dbfs cbit.vcell.dictionary.SpeciesDescription
 * @param struct cbit.vcell.model.Structure
 */
public void resolve(int index,SpeciesContext resolveToSC) {

	for(int i=0;i<resolvedSC.size();i+= 1){
		if(	i != index &&
			resolvedSC.elementAt(i) != null &&
			//Must be done this way (instead of SpeciesContext ==) because soemtimes we are assigning
			//a NEW SpeciesContext that DOES NOT EXIST in the Model yet but the Species and Structure DO EXIST
			((cbit.vcell.model.SpeciesContext)resolvedSC.elementAt(i)).getSpecies() == resolveToSC.getSpecies() &&
			((cbit.vcell.model.SpeciesContext)resolvedSC.elementAt(i)).getStructure() == resolveToSC.getStructure()){
				throw new IllegalArgumentException(
					"Cannot assign RX Element '"+getReactionElement(index).getPreferredName()+"'"+
					(isFluxReaction() && isFlux(index)?(getFluxIndexInside() == index?" (Inside)":" (Outside)"):"")+
					" to SpeciesContext '"+resolveToSC.getName()+"'"+"\n"+
					" because SpeciesContext '"+resolveToSC.getName()+"'"+
					" is already assigned to \n"+
					" RX Element '"+getReactionElement(i).getPreferredName()+
					(isFluxReaction() && isFlux(i)?(getFluxIndexInside() == i?" (Inside)":" (Outside)"):"")+"'");
			}
	}
	resolvedSC.set(index,resolveToSC);
	
}


/**
 * Insert the method's description here.
 * Creation date: (8/24/2003 4:55:35 PM)
 */
public void swapFluxSCNames() {
	String scName = (String)reSpeciesContextNames.remove(1);
	reSpeciesContextNames.insertElementAt(scName,0);
}


/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 2:06:47 PM)
 * @return java.lang.String
 */
public cbit.vcell.model.ReactionCanvasDisplaySpec toReactionCanvasDisplaySpec() {

	
	StringBuffer left = new StringBuffer();
	StringBuffer center = new StringBuffer();
	StringBuffer right = new StringBuffer();
	
	int rCount = 0;
	for(int i = 0;i < elementCount();i+= 1){
		if(getType(i) == RX_ELEMENT_REACTANT || (getType(i) == RX_ELEMENT_FLUX && i == getFluxIndexOutside())){
			if(rCount > 0){
				left.append(" + ");
			}
			left.append((getStoich(i) > 1?getStoich(i)+"":"") +" " + reSpeciesContextNames.get(i)/*getReactionElement(i).getPreferredName()*/);
			left.append((isFluxReaction() && i == getFluxIndexOutside()?" (Outside)":""));
			rCount+= 1;
		}
	}
	//
	boolean isFluxreaction = false;
	for(int i = 0;i < elementCount();i+= 1){
		if(getType(i) == RX_ELEMENT_FLUX){
			isFluxreaction = true; break;
		}
	}	
	//rx.append((isFluxreaction?" (=":" <="));
	rCount = 0;
	for(int i = 0;i < elementCount();i+= 1){
		if(getType(i) == RX_ELEMENT_CATALYST){
			if(rCount > 0){
				center.append(" , ");
			}
			center.append(reSpeciesContextNames.get(i)/*getReactionElement(i).getPreferredName()*/);
			rCount+= 1;
		}
	}
	//rx.append((isFluxreaction?"=) ":"=> "));
	//
	rCount=0;
	for(int i = 0;i < elementCount();i+= 1){
		if(getType(i) == RX_ELEMENT_PRODUCT || (getType(i) == RX_ELEMENT_FLUX && i == getFluxIndexInside())){
			if(rCount > 0){
				right.append(" + ");
			}
			right.append((getStoich(i) > 1?getStoich(i)+"":"") +" " + reSpeciesContextNames.get(i)/*getReactionElement(i).getPreferredName()*/);
			right.append((isFluxReaction() && i == getFluxIndexInside()?" (Inside)":""));
			rCount+= 1;
		}
	}

	cbit.vcell.model.ReactionCanvasDisplaySpec rcds =
		new cbit.vcell.model.ReactionCanvasDisplaySpec(left.toString(),right.toString(),center.toString(),null,cbit.vcell.model.ReactionCanvasDisplaySpec.ARROW_RIGHT);
	return rcds;
}


/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 2:06:47 PM)
 * @return java.lang.String
 */
public String toString() {
	StringBuffer rx = new StringBuffer();
	int rCount = 0;
	for(int i = 0;i < elementCount();i+= 1){
		if(getType(i) == RX_ELEMENT_REACTANT || (getType(i) == RX_ELEMENT_FLUX && i == getFluxIndexOutside())){
			if(rCount > 0){
				rx.append(" + ");
			}
			rx.append((getStoich(i) > 1?getStoich(i)+"":"") +" " + getReactionElement(i).getPreferredName());
			rCount+= 1;
		}
	}
	//
	boolean isFluxreaction = false;
	for(int i = 0;i < elementCount();i+= 1){
		if(getType(i) == RX_ELEMENT_FLUX){
			isFluxreaction = true; break;
		}
	}	
	rx.append((isFluxreaction?" (=":" <="));
	rCount = 0;
	for(int i = 0;i < elementCount();i+= 1){
		if(getType(i) == RX_ELEMENT_CATALYST){
			if(rCount > 0){
				rx.append(" , ");
			}
			rx.append(getReactionElement(i).getPreferredName());
			rCount+= 1;
		}
	}
	rx.append((isFluxreaction?"=) ":"=> "));
	//
	rCount=0;
	for(int i = 0;i < elementCount();i+= 1){
		if(getType(i) == RX_ELEMENT_PRODUCT || (getType(i) == RX_ELEMENT_FLUX && i == getFluxIndexInside())){
			if(rCount > 0){
				rx.append(" + ");
			}
			rx.append((getStoich(i) > 1?getStoich(i)+"":"") +" " + getReactionElement(i).getPreferredName());
			rCount+= 1;
		}
	}
	
	return rx.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 6:50:39 PM)
 * @param dbfs cbit.vcell.dictionary.SpeciesDescription
 * @param struct cbit.vcell.model.Structure
 */
public void unResolve(int index) {

	resolvedSC.set(index,null);
	
}
}
