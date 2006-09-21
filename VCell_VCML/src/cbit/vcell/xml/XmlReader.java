package cbit.vcell.xml;
import org.jdom.DataConversionException;
import cbit.vcell.solver.ConstantArraySpec;
import org.jdom.Attribute;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mapping.*;
import cbit.image.*;
import cbit.vcell.model.*;
import cbit.vcell.math.*;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.geometry.*;
import java.util.*;
import org.jdom.Element;
import org.vcell.model.Activator;
import org.vcell.model.analysis.IAnalysisTask;
import org.vcell.model.analysis.IAnalysisTaskFactory;

import cbit.util.*;
import cbit.util.xml.XmlParseException;
import cbit.vcell.units.VCUnitDefinition;
/**
 * This class implements the translation of XML data into Java Vcell objects..
 * Creation date: (7/17/2000 12:22:50 PM)
 * @author: 
 */
 
public class XmlReader extends XmlBase{
	
	//The following parameter specifies if the keys should be read.
	//By default the value is FALSE to not affect of the current software.
	private boolean readKeysFlag = false;
    private XMLDict dictionary = new XMLDict();
    	
/**
 * This constructor takes a parameter to specify if the KeyValue should be ignored
 * Creation date: (3/13/2001 12:16:30 PM)
 */
public XmlReader(boolean readKeys) {
	super();
	this.readKeysFlag = readKeys;
}


/**
 * Insert the method's description here.
 * Creation date: (4/28/2003 4:46:11 PM)
 * @return boolean
 * @param expString java.lang.String
 */
private boolean expressionIsSingleIdentifier(String expString) throws ExpressionException {
	Expression exp = new Expression(expString);
	String[] symbols = exp.getSymbols();
	if (symbols != null && symbols.length==1){
		if (exp.compareEqual(new Expression(symbols[0]))){
			return true;
		}
	}
	return false;
}


	private VolumeGeometricRegion getAdjacentVolumeRegion(ArrayList regions, String regionName) {
		for (int i = 0; i < regions.size(); i++) {
			VolumeGeometricRegion rvl = (VolumeGeometricRegion)regions.get(i);
			if (rvl.getName().equals(regionName)) {
				return rvl;
			}
		}

		return null;
    }


/**
 * This method returns an AnalyticSubVolume object from a XML representation.
 * Creation date: (5/1/2001 5:26:17 PM)
 * @return cbit.vcell.geometry.AnalyticSubVolume
 * @param param org.jdom.Element
 */
public AnalyticSubVolume getAnalyticSubVolume(Element param) throws XmlParseException{
	//retrieve the attributes
	String name = param.getAttributeValue(XMLTags.NameAttrTag);
	int handle = Integer.parseInt( param.getAttributeValue(XMLTags.HandleAttrTag) );
	
	//process the key
	KeyValue key = null;
	String temp = param.getAttributeValue(XMLTags.KeyValueAttrTag);
	
	if (temp!=null && temp.length()>0 && this.readKeysFlag) {
		key = new KeyValue( temp );
	}

	//Retrieve the expression
	temp = param.getChildText(XMLTags.AnalyticExpressionTag);
	if (temp == null) {
		throw new XmlParseException("A Problem occured while retrieving the analytic expression of the AnalyticSubvolume "+ name);
	}
	Expression newexpression = this.unMangleExpression(temp);
	
	//Create the AnalyticCompartment
	AnalyticSubVolume newsubvolume = null;
	try {
		newsubvolume =  new AnalyticSubVolume(key, name, newexpression, handle);
	} catch (ExpressionException e) {
		e.printStackTrace();
		throw new XmlParseException("An ExpressionException occured when creating the new AnalyticSubvolume " + name+" : "+e.getMessage());
	}

	return  newsubvolume;
}


/**
 * This method returns a Biomodel object from a XML Element.
 * Creation date: (3/13/2001 12:35:00 PM)
 * @return cbit.vcell.biomodel.BioModel
 * @param param org.jdom.Element
 */
public cbit.vcell.biomodel.BioModel getBioModel(Element param) throws XmlParseException{
long l1 = System.currentTimeMillis();
	//Get metadata information Version (if available)
	Version version = getVersion(param.getChild(XMLTags.VersionTag));
		
	//Create new biomodel
	cbit.vcell.biomodel.BioModel biomodel = new cbit.vcell.biomodel.BioModel( version );
	
	//Set name
	String name = this.unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	try {
		biomodel.setName( name );
		//String annotation = param.getAttributeValue(XMLTags.AnnotationAttrTag);

		//if (annotation!=null) {
			//biomodel.setDescription(this.unMangle(annotation));
		//}
		//get annotation
		String annotationText = param.getChildText(XMLTags.AnnotationTag);
		if (annotationText!=null && annotationText.length()>0) {
			biomodel.setDescription(this.unMangle(annotationText));
		}
	} catch(java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(e.getMessage());
	}
long l2 = System.currentTimeMillis();
System.out.println("biomodel-------- "+((double)(l2-l1))/1000);
	
	//***Add biomodel to the dictionnary***
	//dictionnary.put(simcontext.getClass().getName()+":"+simcontext.getName(), simcontext);
	//Set model
	cbit.vcell.model.Model newmodel = getModel(param.getChild(XMLTags.ModelTag));
	biomodel.setModel( newmodel );
	//Set simulation contexts
	java.util.List children = param.getChildren(XMLTags.SimulationSpecTag);
	java.util.Iterator iterator = children.iterator();
long l3 = System.currentTimeMillis();
System.out.println("model-------- "+((double)(l3-l2))/1000);
	while (iterator.hasNext()) {
long l4 = System.currentTimeMillis();
		Element tempElement = (Element)iterator.next();
		cbit.vcell.mapping.SimulationContext simContext = getSimulationContext(tempElement, biomodel);
		try {
			biomodel.addSimulationContext( simContext );
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException("An error occurred while trying to add the SimContext "+ simContext.getName() +" to the BioModel Object!"+" : "+e.getMessage());
		}
		//process the simulations within this Simspec
		Iterator simIterator = tempElement.getChildren(XMLTags.SimulationTag).iterator();
long l5 = System.currentTimeMillis();
System.out.println("simcontext-------- "+((double)(l5-l4))/1000);
		while (simIterator.hasNext()) {
			try {
				biomodel.addSimulation(getSimulation((Element)simIterator.next(), simContext.getMathDescription()));
			} catch(java.beans.PropertyVetoException e) {
				e.printStackTrace();
				throw new XmlParseException("A PropertyVetoException occurred when adding a Simulation entity to the BioModel " + name+" : "+e.getMessage());
			}
		}
long l6 = System.currentTimeMillis();
System.out.println("sims-------- "+((double)(l6-l5))/1000);		
	}
	return biomodel;
}


/**
 * This method returns a Catalyst object from a XML representation.
 * Creation date: (5/4/2001 2:22:56 PM)
 * @return cbit.vcell.model.Product
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public Catalyst getCatalyst(Element param, ReactionStep reaction) throws XmlParseException {
    //retrieve the key if there is one
    KeyValue key = null;
    String keystring = param.getAttributeValue(XMLTags.KeyValueAttrTag);
    if (keystring != null && keystring.length()>0 && this.readKeysFlag) {
        key = new KeyValue(keystring);
    }

    String speccontref = this.unMangle(param.getAttributeValue(XMLTags.SpeciesContextRefAttrTag));
    //Resolve reference to the SpeciesContext
    String tempname = "cbit.vcell.model.SpeciesContext:" + speccontref;
    Element re = XMLDict.getResolvedElement(param, XMLTags.SpeciesContextTag, XMLTags.NameAttrTag, speccontref);
    SpeciesContext speccont = (SpeciesContext) dictionary.get(re, tempname);
    if (speccont == null) {
        throw new XmlParseException(
            "The reference to the SpecieContext "
                + tempname
                + " for a Catalyst could not be resolved in the dictionnary!");
    }

    return new Catalyst(key, reaction, speccont);
}


/**
 * This method returns a CompartmentSubDomain objecy from a XML element.
 * Creation date: (5/17/2001 11:59:45 AM)
 * @return cbit.vcell.math.CompartmentSubDomain
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public CompartmentSubDomain getCompartmentSubDomain(Element param, MathDescription mathDesc) throws XmlParseException {
	//get attributes
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	int priority = -1;
	String temp = param.getAttributeValue(XMLTags.PriorityAttrTag);
	if ( temp != null) {
		priority = Integer.parseInt( temp );
	}
	//--- create new CompartmentSubDomain ---
	CompartmentSubDomain subDomain = new CompartmentSubDomain(name, priority);

	//Process BoundaryConditions
	Iterator iterator = param.getChildren(XMLTags.BoundaryTypeTag).iterator();
	while (iterator.hasNext()){
		Element tempelement = (Element)iterator.next();

		//create BoundaryConditionType
		temp = tempelement.getAttributeValue(XMLTags.BoundaryTypeAttrTag);
		BoundaryConditionType bType = new BoundaryConditionType(temp);
		
		//check where it goes
		//Process Xm
		if (tempelement.getAttributeValue(XMLTags.BoundaryAttrTag).equalsIgnoreCase(XMLTags.BoundaryAttrValueXm)) {
			subDomain.setBoundaryConditionXm(bType);
		} else if (tempelement.getAttributeValue(XMLTags.BoundaryAttrTag).equalsIgnoreCase(XMLTags.BoundaryAttrValueXp)) {
			//Process Xp
			subDomain.setBoundaryConditionXp(bType);
		} else  if (tempelement.getAttributeValue(XMLTags.BoundaryAttrTag).equalsIgnoreCase(XMLTags.BoundaryAttrValueYm)) {
			//Process Ym
			subDomain.setBoundaryConditionYm(bType);
		} else if (tempelement.getAttributeValue(XMLTags.BoundaryAttrTag).equalsIgnoreCase(XMLTags.BoundaryAttrValueYp)) {
			//Process Yp
			subDomain.setBoundaryConditionYp(bType);
		} else if (tempelement.getAttributeValue(XMLTags.BoundaryAttrTag).equalsIgnoreCase(XMLTags.BoundaryAttrValueZm)) {
			//Process Zm
			subDomain.setBoundaryConditionZm(bType);
		} else if (tempelement.getAttributeValue(XMLTags.BoundaryAttrTag).equalsIgnoreCase(XMLTags.BoundaryAttrValueZp)) {
			//Process Zp
			subDomain.setBoundaryConditionZp(bType);
		} else {
			// If not indentified throw an exception!!
			throw new XmlParseException("Unknown BoundaryConditionType: " + tempelement.getAttributeValue(XMLTags.BoundaryAttrTag));
		}
	}

	//process OdeEquations
	iterator = param.getChildren( XMLTags.OdeEquationTag ).iterator();
	while (iterator.hasNext()) {
		Element tempelement = (Element)iterator.next();

		try {
			subDomain.addEquation( getOdeEquation(tempelement) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding an OdeEquation to the compartmentSubDomain " + name+" : "+e.getMessage());
		}
	}

	//process PdeEquations
	iterator = param.getChildren( XMLTags.PdeEquationTag ).iterator();
	while (iterator.hasNext()) {
		Element tempelement = (Element)iterator.next();

		try {
			subDomain.addEquation( getPdeEquation(tempelement) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding an PdeEquation to the compartmentSubDomain " + name+" : "+e.getMessage());
		}
	}

	//Process VolumeRegionEquation
	iterator = param.getChildren( XMLTags.VolumeRegionEquationTag ).iterator();
	while (iterator.hasNext()) {
		Element tempelement = (Element)iterator.next();

		try {
			subDomain.addEquation( getVolumeRegionEquation(tempelement) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding a VolumeRegionEquation to the compartmentSubDomain " + name+" : "+e.getMessage());
		}
	}

	//Process the FastSystem (if thre is)
	Element tempelement = param.getChild(XMLTags.FastSystemTag);
	if ( tempelement != null){
		subDomain.setFastSystem( getFastSystemImplicit(tempelement, mathDesc));
	}

	//****** Add the compartmentSubDomain to the dictionnary ****
	temp = subDomain.getClass().getName() + ":" + subDomain.getName();
	this.dictionary.put(param, temp, subDomain);
	//****** 	*******
	return subDomain;
}


/**
 * This method returns a CompartmentSubVolume object from a XML representation.
 * Creation date: (5/1/2001 5:26:17 PM)
 * @return cbit.vcell.geometry.CompartmentSubVolume
 * @param param org.jdom.Element
 */
public CompartmentSubVolume getCompartmentSubVolume(Element param) throws XmlParseException{
	//retrieve the attributes
	String name = param.getAttributeValue(XMLTags.NameAttrTag);
	int handle = Integer.parseInt( param.getAttributeValue(XMLTags.HandleAttrTag) );
	
	//process the key
	KeyValue key = null;
	String temp = param.getAttributeValue(XMLTags.KeyValueAttrTag);
	if (temp!=null && temp.length()>0 && this.readKeysFlag) {
		key = new KeyValue( temp );
	}
	
	//Create the CompartmentVolume
	CompartmentSubVolume newcompartment = new CompartmentSubVolume(key, handle);
	
	//set the name
	try {
		newcompartment.setName(name);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A propertyVetoException was fired when setting the name to the compartmentSubVolume " + name+" : "+e.getMessage());
	}
	
	return  newcompartment;
}


/**
 * This method returns a Constant object from a XML element.
 * Creation date: (5/16/2001 1:50:07 PM)
 * @return cbit.vcell.math.Constant
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public Constant getConstant(Element param) throws XmlParseException {
	//retrieve values
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	Expression exp = unMangleExpression(param.getText());
	
	//-- create new constant object ---
	Constant newconstant = new Constant(name, exp);

	return newconstant;	
}


/**
 * This method returns a ControlPointcurve object from a XML element.
 * Creation date: (5/22/2001 5:20:39 PM)
 * @return cbit.vcell.geometry.ControlPointCurve
 * @param param org.jdom.Element
 */
public ControlPointCurve getControlPointCurve(Element param) {
	ControlPointCurve curve = null;
	//get Attributes
	String type = param.getAttributeValue(XMLTags.TypeAttrTag);
	boolean closed = Boolean.valueOf(param.getAttributeValue(XMLTags.ClosedAttrTag)).booleanValue();
	List coordList = param.getChildren();

	//Upon de type, decide which Curve type to create
	if (type.equalsIgnoreCase(XMLTags.PolyLineTypeTag)) {
		if ( coordList.size()==2) { //I have a Line
			Coordinate begin = getCoordinate((Element)coordList.get(0));
			Coordinate end = getCoordinate((Element)coordList.get(1));
			// ****create new Line ****
			curve = new Line(begin, end);
		} else {
			//If it it is not a Line, then it is a SampledCurve
			Coordinate[] coords = new Coordinate[coordList.size()];
			for (int i = 0; i < coordList.size(); i++){
				coords[i] = getCoordinate( (Element)coordList.get(i) );
			}
			//****create new SampledCurve ****
			 curve = new SampledCurve(coords);
		}
	} else if (type.equalsIgnoreCase(XMLTags.SplineTypeTag)) {
		Coordinate[] coords = new Coordinate[coordList.size()];
		for (int i = 0; i < coordList.size(); i++){
			coords[i] = getCoordinate( (Element)coordList.get(i) );
		}
		//****create new Spline ****
		 curve = new Spline(coords);
	}
	
	//set Atributes
	curve.setClosed( closed );
	
	return curve;
}


/**
 * This method returns a Coordinate object from a XML Element.
 * Creation date: (5/22/2001 5:53:05 PM)
 * @return cbit.vcell.geometry.Coordinate
 * @param param org.jdom.Element
 */
public Coordinate getCoordinate(Element param) {
	//get attributes
	double x = Double.parseDouble( param.getAttributeValue(XMLTags.XAttrTag) );
	double y = Double.parseDouble( param.getAttributeValue(XMLTags.YAttrTag) );
	double z = Double.parseDouble( param.getAttributeValue(XMLTags.ZAttrTag) );

	//**** create coordinate ***
	Coordinate coord = new Coordinate(x, y, z);
	
	return coord;
}


/**
 * This method returns a DBFormalSpecies from a XML representation.
 * Creation date: (6/3/2003 8:46:44 PM)
 * @return cbit.vcell.dictionary.DBFormalSpecies
 * @param formalSpeciesElement org.jdom.Element
 */
public cbit.vcell.dictionary.DBFormalSpecies getDBFormalSpecies(Element formalSpeciesElement) throws XmlParseException {
	//read key
	String keystring = formalSpeciesElement.getAttributeValue(XMLTags.KeyValueAttrTag);
	KeyValue key = new KeyValue(keystring);
	//read type
	String typestring = formalSpeciesElement.getAttributeValue(XMLTags.TypeAttrTag);
	//read the FormalSpeciesInfo
	Element speciesInfoElement = formalSpeciesElement.getChild(XMLTags.FormalSpeciesInfoTag);

	//create the DBFormalSpecies upon the type
	cbit.vcell.dictionary.DBFormalSpecies formalSpecies = null;
	
	if (typestring.equalsIgnoreCase(XMLTags.CompoundTypeTag)) {
		formalSpecies = new cbit.vcell.dictionary.FormalCompound(key, (cbit.vcell.dictionary.CompoundInfo)getFormalSpeciesInfo(speciesInfoElement));
	} else if (typestring.equalsIgnoreCase(XMLTags.EnzymeTypeTag)) {
		formalSpecies = new cbit.vcell.dictionary.FormalEnzyme(key, (cbit.vcell.dictionary.EnzymeInfo)getFormalSpeciesInfo(speciesInfoElement));
	} else if (typestring.equalsIgnoreCase(XMLTags.ProteinTypeTag)) {
		formalSpecies = new cbit.vcell.dictionary.FormalProtein(key, (cbit.vcell.dictionary.ProteinInfo)getFormalSpeciesInfo(speciesInfoElement));
	} else {
		throw new XmlParseException("DBFormalSpecies type:"+typestring+", not supported yet!");
	}
	
	return formalSpecies;
}


/**
 * This method reads a DBSpecies from a XML representation.
 * Creation date: (6/3/2003 8:20:54 PM)
 * @return cbit.vcell.dictionary.DBSpecies
 * @param dbSpeciesElement org.jdom.Element
 */
public cbit.vcell.dictionary.DBSpecies getDBSpecies(Element dbSpeciesElement) throws XmlParseException {
	//Read the key
	String keystring = dbSpeciesElement.getAttributeValue(XMLTags.KeyValueAttrTag);
	KeyValue key = new KeyValue(keystring);
	cbit.vcell.dictionary.DBSpecies dbSpecies = null;

	//read the type
	String type = dbSpeciesElement.getAttributeValue(XMLTags.TypeAttrTag);
	//Read the DBFormalSpecies
	org.jdom.Element formalSpeciesElement = dbSpeciesElement.getChild(XMLTags.DBFormalSpeciesTag);

	if (type.equalsIgnoreCase(XMLTags.CompoundTypeTag)) {
		//Create a BoundCompound
		dbSpecies = new cbit.vcell.dictionary.BoundCompound(key, (cbit.vcell.dictionary.FormalCompound)getDBFormalSpecies(formalSpeciesElement));
	} else if (type.equalsIgnoreCase(XMLTags.EnzymeTypeTag)) {
		//Create a BoundEnzyme
		dbSpecies = new cbit.vcell.dictionary.BoundEnzyme(key, (cbit.vcell.dictionary.FormalEnzyme)getDBFormalSpecies(formalSpeciesElement));
	} else if (type.equalsIgnoreCase(XMLTags.ProteinTypeTag)) {
		//Create a BoundProtein
		dbSpecies = new cbit.vcell.dictionary.BoundProtein(key, (cbit.vcell.dictionary.FormalProtein)getDBFormalSpecies(formalSpeciesElement));
	} else {
		throw new XmlParseException("DBSpecies type: "+type+", not supported yet!");
	}

	return dbSpecies;
}


/**
 * This method returns a Diagram object from a XML element.
 * Creation date: (4/4/2001 4:20:52 PM)
 * @return cbit.vcell.model.Diagram
 * @param param org.jdom.Element
 */
public Diagram getDiagram(Element param) throws XmlParseException{
	//get Attibutes
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String tempname = this.unMangle( param.getAttributeValue(XMLTags.StructureAttrTag) );

	//Try to retrieve the reference to the structure as a Feature
	Element re = XMLDict.getResolvedElement(param, XMLTags.FeatureTag, XMLTags.NameAttrTag, tempname);
	Structure structureref = (Structure)this.dictionary.get(re, "cbit.vcell.model.Feature:" + tempname);
	if (structureref == null) {
		//if it is not a Feature, try with membrane
		re = XMLDict.getResolvedElement(param, XMLTags.MembraneTag, XMLTags.NameAttrTag, tempname);
		structureref =(Structure) dictionary.get(re, "cbit.vcell.model.Membrane:" + tempname);
	} 
	if (structureref == null) {
		throw new XmlParseException("The structure " + tempname + "could not be resolved in the dictionnary!");
	}
	//try to create the new Diagram
	Diagram newdiagram = new Diagram(structureref, name);
	//Add Nodereferences (Shapes)
	List children = param.getChildren();
	if ( children.size()>0 ) {
		NodeReference[] arraynoderef = new NodeReference[children.size()];
		for (int i=0 ; i<children.size() ; i++) {
			arraynoderef[i] = getNodeReference( (Element)children.get(i) );
		}
		newdiagram.setNodeReferences( arraynoderef );
	}
	
	return newdiagram;
}


/**
 * This method process Electrical Stimulus, also called Clamps.
 * Creation date: (6/6/2002 4:46:18 PM)
 * @return cbit.vcell.mapping.ElectricalStimulus
 * @param param org.jdom.Element
 */
public ElectricalStimulus getElectricalStimulus(Element param, SimulationContext currentSimulationContext) throws XmlParseException{
	ElectricalStimulus clampStimulus = null;
	
	//get name
	// String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	//get Electrode
	Electrode electrode = getElectrode(param.getChild(XMLTags.ElectrodeTag), currentSimulationContext);
	
	if (param.getAttributeValue(XMLTags.TypeAttrTag).equalsIgnoreCase(XMLTags.VoltageClampTag)) {
		//is a voltage clamp
		clampStimulus = new VoltageClampStimulus(electrode, "voltClampElectrode", new Expression(0.0), currentSimulationContext);
	} else {
		//is a current clamp
		clampStimulus = new CurrentClampStimulus(electrode, "currClampElectrode", new Expression(0.0), currentSimulationContext);
	}
	
	try {
		clampStimulus.reading(true);   // transaction begin flag ... yeah, this is a hack
		
		//Read all of the parameters
		List list = param.getChildren(XMLTags.ParameterTag);

		// add constants that may be used in the electrical stimulus.
		VariableHash varHash = getVariablesHash();
		ArrayList reserved = getReservedVars();

		//
		// rename "special" parameters (those that are not "user defined")
		//
		for (int i = 0; i < list.size() ; i++){
			Element xmlParam = (Element)list.get(i);
			String paramName = this.unMangle(xmlParam.getAttributeValue(XMLTags.NameAttrTag));
			String role = xmlParam.getAttributeValue(XMLTags.ParamRoleAttrTag);
			String paramExpStr = xmlParam.getText();
			Expression paramExp = unMangleExpression(paramExpStr);
			try {
				if (varHash.getVariable(paramName) == null){
					varHash.addVariable(new Function(paramName,paramExp));
				} else {
					if (reserved.contains(paramName)) {
						varHash.removeVariable(paramName);
						varHash.addVariable(new Function(paramName, paramExp));
					}
				}
			}catch (MappingException e){
				e.printStackTrace(System.out);
				throw new XmlParseException("error reordering parameters according to dependencies: "+e.getMessage());
			}
			ElectricalStimulus.ElectricalStimulusParameter tempParam = null;
			if (!role.equals(XMLTags.ParamRoleUserDefinedTag)) {
				tempParam = clampStimulus.getElectricalStimulusParameterFromRole(ElectricalStimulus.getParamRoleFromDesc(role));
			}else{
				continue;
			}
			if (tempParam == null) {
				throw new XmlParseException("parameter with role '"+role+"' not found in electricalstimulus");
			} 
			//
			// custom name for "special" parameter
			//
			if (!tempParam.getName().equals(paramName)) {
				ElectricalStimulus.ElectricalStimulusParameter multNameParam = (ElectricalStimulus.ElectricalStimulusParameter)clampStimulus.getElectricalStimulusParameter(paramName);
				int n = 0;
				while (multNameParam != null) {
					String tempName = paramName + "_" + n++;
					clampStimulus.renameParameter(paramName, tempName);
					multNameParam = (ElectricalStimulus.ElectricalStimulusParameter)clampStimulus.getParameter(tempName);
				}
				clampStimulus.renameParameter(tempParam.getName(), paramName);
			}
		}
		//
		// create unresolved parameters for all unresolved symbols
		//
		String unresolvedSymbol = varHash.getFirstUnresolvedSymbol();
		while (unresolvedSymbol!=null){
			try {
				varHash.addVariable(new Function(unresolvedSymbol,new Expression(0.0)));  // will turn into an UnresolvedParameter.
			}catch (MappingException e){
				e.printStackTrace(System.out);
				throw new XmlParseException(e.getMessage());
			}
			clampStimulus.addUnresolvedParameter(unresolvedSymbol);
			unresolvedSymbol = varHash.getFirstUnresolvedSymbol();
		}
		
		Variable sortedVariables[] = varHash.getReorderedVariables();
		for (int i = sortedVariables.length-1; i >= 0 ; i--){
			if (sortedVariables[i] instanceof Function){
				Function paramFunction = (Function)sortedVariables[i];
				Element xmlParam = null;
				for (int j = 0; j < list.size(); j++){
					Element tempParam = (Element)list.get(j);
					if (paramFunction.getName().equals(unMangle(tempParam.getAttributeValue(XMLTags.NameAttrTag)))){
						xmlParam = tempParam;
						break;
					}
				}
				if (xmlParam==null){
					continue; // must have been an unresolved parameter
				}
				String symbol = xmlParam.getAttributeValue(XMLTags.VCUnitDefinitionAttrTag);
				VCUnitDefinition unit = null;
				if (symbol != null) {
					unit = VCUnitDefinition.getInstance(symbol);
				}
				ElectricalStimulus.ElectricalStimulusParameter tempParam = clampStimulus.getElectricalStimulusParameter(paramFunction.getName());
				if (tempParam == null) {
					clampStimulus.addUserDefinedKineticsParameter(paramFunction.getName(), paramFunction.getExpression(), unit);
				} else {
					clampStimulus.setParameterValue(tempParam, paramFunction.getExpression());
					tempParam.setUnitDefinition(unit);
				}
			}
		}

		
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException("Exception: "+e.getMessage()+" while setting parameters for simContext : " + currentSimulationContext.getName());
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException("Exception: "+e.getMessage()+" while settings parameters for simContext : " + currentSimulationContext.getName());
	} finally {
		clampStimulus.reading(false);
	}
	
	return clampStimulus;
}


/**
 * This method returns an Electrode object from a XML representation.
 * Creation date: (6/6/2002 4:22:55 PM)
 * @return cbit.vcell.mapping.Electrode
 */
public Electrode getElectrode(org.jdom.Element elem, SimulationContext currentSimulationContext) {
	//retrieve feature
	String featureName = this.unMangle(elem.getAttributeValue(XMLTags.FeatureAttrTag));
	Feature feature = (Feature)currentSimulationContext.getModel().getStructure(featureName);
	//retrieve position
	cbit.util.Coordinate position = getCoordinate(elem.getChild(XMLTags.CoordinateTag));
	
	Electrode newElect = new Electrode(feature, position);
	
	return newElect;
}


/**
 * This method returns a ErrorTolerance object from a XML Element.
 * Creation date: (5/22/2001 11:50:07 AM)
 * @return cbit.vcell.solver.ErrorTolerance
 * @param param org.jdom.Element
 */
public cbit.vcell.solver.ErrorTolerance getErrorTolerance(Element param) {
	//getAttributes
	double absolut = Double.parseDouble( param.getAttributeValue(XMLTags.AbsolutErrorToleranceTag) );
	double relative = Double.parseDouble( param.getAttributeValue(XMLTags.RelativeErrorToleranceTag) );
	
	//*** create new ErrorTolerance object ****
	cbit.vcell.solver.ErrorTolerance errorTol = new cbit.vcell.solver.ErrorTolerance(absolut, relative);
	
	return errorTol;
}


public Extent getExtent(Element parsed) {
	double x = Double.parseDouble( parsed.getAttributeValue(XMLTags.XAttrTag) );
	double y = Double.parseDouble( parsed.getAttributeValue(XMLTags.YAttrTag) );
	double z = Double.parseDouble( parsed.getAttributeValue(XMLTags.ZAttrTag) );
	Extent extent = new Extent(x,y,z);

	return extent;
}


/**
 * This method returns a FastSystemImplicit from a XML Element.
 * Creation date: (5/18/2001 2:38:56 PM)
 * @return cbit.vcell.math.FastSystemImplicit
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public FastSystemImplicit getFastSystemImplicit(
    Element param,
    MathDescription mathDesc)
    throws XmlParseException {
    //Create a new FastSystem
    FastSystemImplicit fastSystem = new FastSystemImplicit(mathDesc);

    //Process the FastInvariants
    Iterator iterator = param.getChildren(XMLTags.FastInvariantTag).iterator();
    FastInvariant fastInvariant = null;
    while (iterator.hasNext()) {
	    Element tempElement = (Element)iterator.next();
        String temp = tempElement.getText();

        try {
            Expression newExp = unMangleExpression(temp);
            fastInvariant = new FastInvariant(newExp);
            fastSystem.addFastInvariant(fastInvariant);
        } catch (MathException e) {
            e.printStackTrace();
            throw new XmlParseException(
                "A MathException was fired when adding the FastInvariant " + fastInvariant + ", to a FastSystem!"+" : "+e.getMessage());
        } 
    }
    //Process the FastRate
    iterator = param.getChildren(XMLTags.FastRateTag).iterator();
    FastRate fastRate = null;
    
    while (iterator.hasNext()) {
	    Element tempElement = (Element)iterator.next();
        String temp = tempElement.getText();

        try {
            Expression newExp = unMangleExpression(temp);
            fastRate = new FastRate(newExp);
            fastSystem.addFastRate(fastRate);
        } catch (MathException e) {
            e.printStackTrace();
            throw new XmlParseException(
                "A MathException was fired when adding the FastRate " + fastRate + ", to a FastSystem!"+" : "+e.getMessage());
        } 
    }

    return fastSystem;
}


/**
 * This method returns a Feature object (Structure) from a XML representation.
 * Creation date: (3/15/2001 6:12:36 PM)
 * @return cbit.vcell.model.Structure
 * @param param org.jdom.Element
 */
public cbit.vcell.model.Structure getFeature(Element param) throws XmlParseException {
	cbit.vcell.model.Feature newfeature = null;
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

	//retrieve the key if there is one
	KeyValue key = null;
	String keystring = param.getAttributeValue(XMLTags.KeyValueAttrTag);
	
	if ( keystring!=null && keystring.length()>0 && this.readKeysFlag) {
		key = new KeyValue( keystring );
	}
	
	//---Create the new feature---
	try {
		newfeature = new cbit.vcell.model.Feature( key, name );
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(
			"An error occurred while creating the feature "
				+ param.getAttributeValue(XMLTags.NameAttrTag)+" : "+e.getMessage());
	}
	
	//*** Add Feture to the dictionnary ****
	this.dictionary.put(param, newfeature.getClass().getName() + ":" + name, newfeature);

	return newfeature;
}


/**
 * This method retuns a FeatureMapping object from a XML representation.
 * Creation date: (5/7/2001 4:12:03 PM)
 * @return cbit.vcell.mapping.FeatureMapping
 * @param param org.jdom.Element
 */
public FeatureMapping getFeatureMapping(Element param, SimulationContext simulationContext) throws XmlParseException{
	//Retrieve attributes
	String featurename = this.unMangle( param.getAttributeValue(XMLTags.FeatureAttrTag) );
	String subvolumename = param.getAttributeValue(XMLTags.SubVolumeAttrTag);
	if (subvolumename != null)
		subvolumename = this.unMangle(subvolumename);
	boolean resolved = Boolean.valueOf(param.getAttributeValue(XMLTags.ResolvedAttrTag)).booleanValue();
	
	//Retrieve feature reference
	String temp = "cbit.vcell.model.Feature:" +featurename;
	Element re = XMLDict.getResolvedElement(param, XMLTags.FeatureTag, XMLTags.NameAttrTag, featurename);
	Feature featureref = (Feature)this.dictionary.get(re, temp );
	if (featureref == null) {
		throw new XmlParseException("The Feature "+ temp + " could not be resolved in the dictionnary!");
	}

	//*** Create new Feature Mapping ****
	FeatureMapping feamap = new FeatureMapping(featureref,simulationContext);

	//Retrieve subvolumeref, allow subvolumes to be 'null'
	if (subvolumename != null) {
		temp = "cbit.vcell.geometry.SubVolume:" + subvolumename;
		re = XMLDict.getResolvedElement(param, XMLTags.SubVolumeTag, XMLTags.NameAttrTag, subvolumename);
		SubVolume subvolumeref = (SubVolume)this.dictionary.get(re, temp);
		if (subvolumeref == null) {
			throw new XmlParseException("The SubVolume "+ temp + " could not be resolved in the dictionnary!");
		}
		//Set attributes to the featuremapping
		try {
			feamap.setSubVolume( subvolumeref );
		}catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException("A propertyVetoException was fired when trying to set the subvolume " + temp + " to a FeatureMapping!"+" : "+e.getMessage());
		}
	}
	try {
		feamap.setResolved( resolved );
	}catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A propertyVetoException was fired when setting to a FeatureMapping if it is resolved!"+" : "+e.getMessage());
	}
	//Set Boundary conditions
	Element tempElement = param.getChild(XMLTags.BoundariesTypesTag);
	
	//Xm
	temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueXm);
	BoundaryConditionType bct = new BoundaryConditionType(temp);
	feamap.setBoundaryConditionTypeXm(bct);
	//Xp
	temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueXp);
	bct = new BoundaryConditionType(temp);
	feamap.setBoundaryConditionTypeXp(bct);
	//Ym
	temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueYm);
	bct = new BoundaryConditionType(temp);
	feamap.setBoundaryConditionTypeYm(bct);
	//Yp
	temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueYp);
	bct = new BoundaryConditionType(temp);
	feamap.setBoundaryConditionTypeYp(bct);
	//Zm
	temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueZm);
	bct = new BoundaryConditionType(temp);
	feamap.setBoundaryConditionTypeZm(bct);
	//Zp
	temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueZp);
	bct = new BoundaryConditionType(temp);
	feamap.setBoundaryConditionTypeZp(bct);
	
	return feamap;
}


/**
 * This method returns a FilamentRegionVariable object from a XML Element.
 * Creation date: (5/16/2001 2:56:34 PM)
 * @return cbit.vcell.math.FilamentRegionVariable
 * @param param org.jdom.Element
 */
public FilamentRegionVariable getFilamentRegionVariable(Element param) {
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

	//-- create new FilamentRegionVariable object
	FilamentRegionVariable filRegVariable = new FilamentRegionVariable( name );

	//***add it to the dictionnary ***
	String temp = filRegVariable.getClass().getName()+":"+name;
	this.dictionary.put(param, temp, filRegVariable);

	return filRegVariable;
}


/**
 * This method returns a FilamentSubDomain object from a XMl element.
 * Creation date: (5/18/2001 4:27:22 PM)
 * @return cbit.vcell.math.FilamentSubDomain
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public FilamentSubDomain getFilamentSubDomain(Element param, MathDescription mathDesc) throws XmlParseException {
	//get name
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	//get outside Compartment ref
	String outsideName = this.unMangle( param.getAttributeValue(XMLTags.OutsideCompartmentTag) );
	String temp = "cbit.vcell.math.CompartmentSubDomain:" + outsideName;
	Element re = XMLDict.getResolvedElement(param, XMLTags.CompartmentSubDomainTag, XMLTags.NameAttrTag, outsideName);
	CompartmentSubDomain outsideRef = (CompartmentSubDomain)this.dictionary.get(re, temp);
	if (outsideRef == null) {
		throw new XmlParseException("The reference to the CompartmentSubDomain "+ outsideName + ", could not be resolved in the dictionnary!");
	}
	//*** create new filamentSubDomain object ***
	FilamentSubDomain filDomain = new FilamentSubDomain(name, outsideRef);
	
	//add OdeEquations
	Iterator iterator = param.getChildren(XMLTags.OdeEquationTag).iterator();
	while ( iterator.hasNext() ){
		Element tempElement = (Element)iterator.next();
		try {
			filDomain.addEquation( getOdeEquation(tempElement) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding an OdeEquation to the FilamentSubDomain " + name+" : "+e.getMessage());
		}
	}
	//Add the FastSytem
	filDomain.setFastSystem( getFastSystemImplicit(param.getChild(XMLTags.FastSystemTag), mathDesc) );

	return filDomain;
}


/**
 * This method returns a FilamentVariable object from a XML Element.
 * Creation date: (5/16/2001 2:56:34 PM)
 * @return cbit.vcell.math.FilamentVariable
 * @param param org.jdom.Element
 */
public FilamentVariable getFilamentVariable(Element param) {
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

	//-- create new filVariable object
	FilamentVariable filVariable = new FilamentVariable( name );

	//***add it to the dictionnary ***
	String temp = filVariable.getClass().getName()+":"+name;
	this.dictionary.put(param, temp, filVariable);

	return filVariable;
}


/**
 * This method returns a FluxReaction object from a XML element.
 * Creation date: (3/16/2001 11:52:02 AM)
 * @return cbit.vcell.model.FluxReaction
 * @param param org.jdom.Element
 */
public cbit.vcell.model.FluxReaction getFluxReaction( Element param, Model model) throws XmlParseException, java.beans.PropertyVetoException {
	//retrieve the key if there is one
	KeyValue key = null;
	String keystring = param.getAttributeValue(XMLTags.KeyValueAttrTag);
	
	if ( keystring != null && keystring.length()>0 && this.readKeysFlag ) {
		key = new KeyValue( keystring );
	}

	//resolve reference to the Membrane
	String tempname = "cbit.vcell.model.Membrane:" + this.unMangle(param.getAttributeValue(XMLTags.StructureAttrTag));
	Element re = XMLDict.getResolvedElement(param, XMLTags.MembraneTag, XMLTags.NameAttrTag, 
		                                    this.unMangle(param.getAttributeValue(XMLTags.StructureAttrTag)));
	cbit.vcell.model.Membrane structureref = (Membrane) this.dictionary.get(re, tempname);

	if (structureref == null) {
		throw new XmlParseException(
			"The membrane " + tempname + " could not be resolved in the dictionnary!");
	}
	//resolve reference to the fluxCarrier
	cbit.vcell.model.Species specieref = null;

	if (param.getAttribute(XMLTags.FluxCarrierAttrTag)!= null) {	
		tempname = "cbit.vcell.model.Species:" + this.unMangle(param.getAttributeValue(XMLTags.FluxCarrierAttrTag));
		re = XMLDict.getResolvedElement(param, XMLTags.SpeciesTag, XMLTags.NameAttrTag, 
										this.unMangle(param.getAttributeValue(XMLTags.FluxCarrierAttrTag)));
		specieref = (Species) this.dictionary.get(re, tempname);
		
		if (specieref == null) {
			throw new XmlParseException("The Species " + tempname + " could not be resolved in the dictionnary!");
		}
	}
	
	//-- Instantiate new FluxReaction --
	cbit.vcell.model.FluxReaction fluxreaction = null;
	String name = this.unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	try {
		fluxreaction = new cbit.vcell.model.FluxReaction(structureref, specieref, model, key, name);
	} catch (Exception e) {
		e.printStackTrace();
		throw new XmlParseException( "An exception occurred while trying to create the FluxReaction " + name+" : "+e.getMessage());
	}
	//set the valence
	String valenceString = null;
	try {
		valenceString = this.unMangle(param.getAttributeValue(XMLTags.FluxCarrierValenceAttrTag));
		if (valenceString!=null&&valenceString.length()>0){
			try {
				fluxreaction.getChargeCarrierValence().setExpression(new Expression(Integer.parseInt(unMangle(valenceString))));
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				throw new XmlParseException("A propertyVetoException was fired when setting the valence to the flux reaction " + name+" : "+e.getMessage());
			}
		}
	} catch (NumberFormatException e) {
		e.printStackTrace();
		throw new XmlParseException("A NumberFormatException was fired when setting the (integer) valence '"+valenceString+"' (integer) to the flux reaction " + name+" : "+e.getMessage());
	}
	//set the fluxOption
	String fluxOptionString = null;
	fluxOptionString = param.getAttributeValue(XMLTags.FluxOptionAttrTag);
	if (fluxOptionString!=null&&fluxOptionString.length()>0){
		try {
			if (fluxOptionString.equals(XMLTags.FluxOptionElectricalOnly)){
				fluxreaction.setPhysicsOptions(ReactionStep.PHYSICS_ELECTRICAL_ONLY);
			}else if (fluxOptionString.equals(XMLTags.FluxOptionMolecularAndElectrical)){
				fluxreaction.setPhysicsOptions(ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL);
			}else if (fluxOptionString.equals(XMLTags.FluxOptionMolecularOnly)){
				fluxreaction.setPhysicsOptions(ReactionStep.PHYSICS_MOLECULAR_ONLY);
			} 
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new XmlParseException("A propertyVetoException was fired when setting the fluxOption to the flux reaction " + name+" : "+e.getMessage());
		}
	}
	//Add Catalyst(Modifiers) (if there are)
	Iterator iterator = param.getChildren(XMLTags.CatalystTag).iterator();
	while (iterator.hasNext()) {
		Element temp = (Element) iterator.next();

		fluxreaction.addReactionParticipant( getCatalyst(temp, fluxreaction) );
	}
	//Add Kinetics
	fluxreaction.setKinetics(getKinetics(param.getChild(XMLTags.KineticsTag), fluxreaction));

	//*** Add FluxReaction to he dictionnary ***
	String temp = fluxreaction.getClass().getName() + ":"+ fluxreaction.getName();
	this.dictionary.put(param, temp, fluxreaction);
	//*****	*****	*****
	
	return fluxreaction;
}


/**
 * This method creates a FormalSpeciesInfo from a XML representation.
 * Creation date: (6/3/2003 9:11:26 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 * @param speciesInfoElement org.jdom.Element
 */
public cbit.vcell.dictionary.FormalSpeciesInfo getFormalSpeciesInfo(Element speciesInfoElement) throws XmlParseException {
	//get formalID
	String formalID = this.unMangle(speciesInfoElement.getAttributeValue(XMLTags.FormalIDTag));
	//get names
	List namesList = speciesInfoElement.getChildren(XMLTags.NameTag);
	String[] namesArray = new String[namesList.size()];

	for (int i = 0; i < namesList.size(); i++){
		Element nameElement = (Element)namesList.get(i);
		namesArray[i] = this.unMangle(nameElement.getText());
	}
	String tempstring;
	//get type
	String type = speciesInfoElement.getAttributeValue(XMLTags.TypeAttrTag);
	cbit.vcell.dictionary.FormalSpeciesInfo formalSpeciesInfo = null;

	if (type.equalsIgnoreCase(XMLTags.CompoundTypeTag)) {
		//get formula
		String formula = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.FormulaTag);
		if (tempstring!=null) {
			formula = this.unMangle(tempstring);
		}
		
		//get CASID
		String casid = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.CasIDTag);
		if (tempstring!=null) {
			casid = this.unMangle(tempstring);
		}
		
		//get Enzymes
		List enzymelist = speciesInfoElement.getChildren(XMLTags.EnzymeTag);
		cbit.vcell.dictionary.EnzymeRef[] enzymeArray = null;
		
		if (enzymelist!=null && enzymelist.size()>0) {
			enzymeArray = new cbit.vcell.dictionary.EnzymeRef[enzymelist.size()];
			
			for (int i = 0; i < enzymelist.size(); i++){
				Element enzymeElement = (Element)enzymelist.get(i);
				//get ECNumber
				String ecnumber = this.unMangle(enzymeElement.getAttributeValue(XMLTags.ECNumberTag));
				//get Enzymetype
				String enztypestr = enzymeElement.getAttributeValue(XMLTags.TypeAttrTag);
				char enzymetype = enztypestr.charAt(0);
				enzymeArray[i] = new cbit.vcell.dictionary.EnzymeRef(ecnumber, enzymetype);
			}
		}

		//create new CompoundInfo
		formalSpeciesInfo = new cbit.vcell.dictionary.CompoundInfo(formalID, namesArray, formula, casid, enzymeArray);
	} else if (type.equalsIgnoreCase(XMLTags.EnzymeTypeTag)) {
		//get reaction
		String reaction = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.ExpressionAttrTag);
		if (tempstring != null) {
			reaction = this.unMangle(tempstring);
		}
		//get sysname
		String sysname = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.SysNameTag);
		if (tempstring != null) {
			sysname = this.unMangle(tempstring);
		}
		//get argcasID
		String casid = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.CasIDTag);
		if (tempstring != null) {
			casid = this.unMangle(tempstring);
		}
		//create new EnzymeInfo
		formalSpeciesInfo = new cbit.vcell.dictionary.EnzymeInfo(formalID, namesArray, reaction, sysname, casid);
	} else if (type.equalsIgnoreCase(XMLTags.ProteinTypeTag)) {
		//get organism
		String organism = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.OrganismTag);
		if (tempstring != null) {
			organism = this.unMangle(tempstring);
		}
		//get accession
		String accession = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.AccessionTag);
		if (tempstring != null) {
			accession = this.unMangle(tempstring);
		}
		//get keywords
		String keywords = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.KeywordsTag);
		if (tempstring != null) {
			keywords = this.unMangle(tempstring);
		}
		//get description
		String description = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.DescriptionTag);
		if (tempstring != null) {
			description = this.unMangle(tempstring);
		}
		//create new ProteinInfo
		formalSpeciesInfo = new cbit.vcell.dictionary.ProteinInfo(formalID, namesArray, organism, accession, keywords, description);
	} else {
		throw new XmlParseException("FormalSpeciesInfo type "+type+", not supported yet!");
	}

	return formalSpeciesInfo;
}


/**
 * This method returns a Function variable object from a XML Element.
 * Creation date: (5/16/2001 3:45:21 PM)
 * @return cbit.vcell.math.Function
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public Function getFunction(Element param) throws XmlParseException {
	//get attributes
	String name = this.unMangle( param.getAttributeValue( XMLTags.NameAttrTag) );
	String temp = param.getText();
	
	Expression exp = unMangleExpression(temp);
	
	//-- create new Function --
	Function function = new Function(name, exp);

	return function;
}


/**
 * This method returns a Geometry object from a XML representation.
 * Creation date: (4/26/2001 12:12:18 PM)
 * @return cbit.vcell.geometry.Geometry
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public Geometry getGeometry(Element param) throws XmlParseException {
	//Get the Extent object
	Extent newextent = getExtent(param.getChild(XMLTags.ExtentTag));
	//Get VCimage information
	VCImage newimage = null;	
	if ( param.getChild(XMLTags.ImageTag)!=null ) {
		try {
			newimage = getVCImage( param.getChild(XMLTags.ImageTag), newextent );
		} catch (Throwable e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}
	
	//Get attributes
	String name = this.unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	int newdimension = Integer.parseInt(param.getAttributeValue(XMLTags.DimensionAttrTag));
	//Get Version
	Version version = getVersion(param.getChild(XMLTags.VersionTag));
	
	//Try to construct the geometry upon four different cases
	Geometry newgeometry = null;
	if ( version!=null && newimage!=null ) {
		newgeometry = new Geometry( version, newimage);
	} else if (version!=null) {
		newgeometry = new Geometry( version, newdimension );
	} else if (newimage!=null) {
		newgeometry = new Geometry(name, newimage);
	} else {
		newgeometry = new Geometry(name, newdimension);
	}
	
	//set attributes
	try {
		if (!newgeometry.getName().equalsIgnoreCase(name)) {
			newgeometry.setName(name);			
		}
		
		//String annotation = param.getAttributeValue(XMLTags.AnnotationAttrTag);
		
		//if (annotation!=null) {
			//newgeometry.setDescription( this.unMangle(annotation) );
		//}
		//Add annotation
		String annotation = param.getChildText(XMLTags.AnnotationTag);
		if ( annotation!=null && annotation.length()>0 ) {
			newgeometry.setDescription(this.unMangle(annotation));
		}
	} catch ( java.beans.PropertyVetoException e ) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException occurred when setting the name " + name + " to a Geometry object!" +" : "+e.getMessage());
	}
	//Add the Extent
	try {
		newgeometry.getGeometrySpec().setExtent( newextent );
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException occurred while trying to set the Extent for the Geometry " + name+" : "+e.getMessage());
	}
	//Add the Origin
	newgeometry.getGeometrySpec().setOrigin( getOrigin(param.getChild(XMLTags.OriginTag)) );

	//Add the SubVolumes
	List children = param.getChildren(XMLTags.SubVolumeTag);
	SubVolume[] newsubvolumes = new SubVolume[children.size()];
	for (int i=0 ; i<children.size() ; i++) {
		newsubvolumes[i] = getSubVolume((Element)children.get(i));
	}
	try {
		newgeometry.getGeometrySpec().setSubVolumes( newsubvolumes );
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException was generated when ading the subvolumes to the Geometry " + name+" : "+e.getMessage());
	}
	//read Filaments (if any)
	Iterator iterator = param.getChildren(XMLTags.FilamentTag).iterator();
	while (iterator.hasNext()) {
		Element tempElement = (Element)iterator.next();

		String filname = this.unMangle( tempElement.getAttributeValue(XMLTags.NameAttrTag));
		Iterator curveiterator = tempElement.getChildren().iterator();
		while (curveiterator.hasNext()) {
			ControlPointCurve curve = getControlPointCurve((Element)curveiterator.next());
			newgeometry.getGeometrySpec().getFilamentGroup().addCurve(filname, curve);
		}
	}
	//read Surface description (if any)
	Element sd = param.getChild(XMLTags.SurfaceDescriptionTag);
	if (sd != null) {
		GeometrySurfaceDescription dummy = getGeometrySurfaceDescription(sd, newgeometry);
	}
	
	return newgeometry;
}


    public GeometrySurfaceDescription getGeometrySurfaceDescription(Element param, Geometry geom) throws XmlParseException {
 
	    GeometrySurfaceDescription gsd = geom.getGeometrySurfaceDescription();
	    String cutoffStr = param.getAttributeValue(XMLTags.CutoffFrequencyAttrTag);
	    String xDim = param.getAttributeValue(XMLTags.NumSamplesXAttrTag);
	    String yDim = param.getAttributeValue(XMLTags.NumSamplesYAttrTag);
	    String zDim = param.getAttributeValue(XMLTags.NumSamplesZAttrTag);
		if (cutoffStr == null || xDim == null || yDim == null || zDim == null) {
			throw new XmlParseException("Attributes for element Surface Description not properly set, under geometry: " +
										param.getParent().getAttributeValue(XMLTags.NameAttrTag));
		}
		try {
			ISize isize = new ISize(Integer.parseInt(xDim), Integer.parseInt(yDim), Integer.parseInt(zDim));
			gsd.setVolumeSampleSize(isize);
			gsd.setFilterCutoffFrequency(new Double(cutoffStr));

			//these lists are allowed to be empty.
		    ArrayList memRegions = new ArrayList(param.getChildren(XMLTags.MembraneRegionTag));
		    ArrayList volRegions = new ArrayList(param.getChildren(XMLTags.VolumeRegionTag));
			ArrayList regions = new ArrayList();
			Element temp;
			for (int i = 0; i < volRegions.size(); i++) {
				temp = (Element)volRegions.get(i);
				String regionID = temp.getAttributeValue(XMLTags.RegionIDAttrTag);
				String name = temp.getAttributeValue(XMLTags.NameAttrTag);
				String subvolumeRef = temp.getAttributeValue(XMLTags.SubVolumeAttrTag);
				if (regionID == null || name == null || subvolumeRef == null) {
					throw new XmlParseException("Attributes for element Volume Region not properly set, under geometry: " +
											param.getParent().getAttributeValue(XMLTags.NameAttrTag));
				}
				SubVolume subvolume = geom.getGeometrySpec().getSubVolume(subvolumeRef);
				if (subvolume == null) {
					throw new XmlParseException("The subvolume "+ subvolumeRef + " could not be resolved.");
				} 
				double size = -1;
				VCUnitDefinition unit = null;
				String sizeStr = temp.getAttributeValue(XMLTags.SizeAttrTag);
				if (sizeStr != null) {
					size = Double.parseDouble(sizeStr);
					String unitSymbol = temp.getAttributeValue(XMLTags.VCUnitDefinitionAttrTag);
					if (unitSymbol != null) {
						unit = VCUnitDefinition.getInstance(unitSymbol);
					}
				}
				VolumeGeometricRegion vgr = new VolumeGeometricRegion(name, size, unit, subvolume, Integer.parseInt(regionID));
				regions.add(vgr);
			}
			for (int i = 0; i < memRegions.size(); i++) {
				temp = (Element)memRegions.get(i);
				String volRegion_1 = temp.getAttributeValue(XMLTags.VolumeRegion_1AttrTag);
				String volRegion_2 = temp.getAttributeValue(XMLTags.VolumeRegion_2AttrTag);
				String name = temp.getAttributeValue(XMLTags.NameAttrTag);
				if (volRegion_1 == null || volRegion_2 == null || name == null) {
					throw new XmlParseException("Attributes for element Membrane Region not properly set, under geometry: " +
											param.getParent().getAttributeValue(XMLTags.NameAttrTag));
				}
				VolumeGeometricRegion region1 = getAdjacentVolumeRegion(regions, volRegion_1);
				VolumeGeometricRegion region2 = getAdjacentVolumeRegion(regions, volRegion_2);
				if (region1 == null || region2 == null) {
					throw new XmlParseException("Element Membrane Region refernces invalid volume regions, under geometry: " +
											param.getParent().getAttributeValue(XMLTags.NameAttrTag));
				}
				double size = -1;
				VCUnitDefinition unit = null;
				String sizeStr = temp.getAttributeValue(XMLTags.SizeAttrTag);
				if (sizeStr != null) {
					size = Double.parseDouble(sizeStr);
					String unitSymbol = temp.getAttributeValue(XMLTags.VCUnitDefinitionAttrTag);
					if (unitSymbol != null) {
						unit = VCUnitDefinition.getInstance(unitSymbol);
					}
				}
				SurfaceGeometricRegion rsl = new SurfaceGeometricRegion(name, size, unit);
				rsl.addAdjacentGeometricRegion(region1);
				region1.addAdjacentGeometricRegion(rsl);
				rsl.addAdjacentGeometricRegion(region2);
				region2.addAdjacentGeometricRegion(rsl);
				regions.add(rsl);
			}
			if (regions.size() > 0) {
		    	gsd.setGeometricRegions((GeometricRegion [])regions.toArray(new GeometricRegion[regions.size()]));
			}
		} catch (Exception e) {
			System.err.println("Unable to read geometry surface description from XML, for geometry: " +
								param.getParent().getAttributeValue(XMLTags.NameAttrTag));
			e.printStackTrace();
		}
	    
		return gsd;	    
    }


/**
 * This method returns a GroupAccess object from an XML format.
 * Creation date: (5/23/2003 7:27:10 PM)
 * @return cbit.vcell.server.GroupAccess
 * @param xmlGroup org.jdom.Element
 */
public GroupAccess getGroupAccess(Element xmlGroup) {
	//guess the type of group
	String temp = xmlGroup.getAttributeValue(XMLTags.TypeAttrTag);
	java.math.BigDecimal type = new java.math.BigDecimal(temp);

	if (type.equals(GroupAccess.GROUPACCESS_ALL) ) {
		//Type ALL
		return new GroupAccessAll();
	} else if (type.equals(GroupAccess.GROUPACCESS_NONE)) {
		//Type NONE
		return new GroupAccessNone();
	} else {
		//Type SOME
		//Read attributes
		//*groupid
		temp = xmlGroup.getAttributeValue(XMLTags.TypeAttrTag);
		java.math.BigDecimal groupid = new java.math.BigDecimal(temp);
		//*hash
		temp = xmlGroup.getAttributeValue(XMLTags.HashAttrTag);
		java.math.BigDecimal hashcode = new java.math.BigDecimal(temp);
		//*users
		List userlist = xmlGroup.getChildren(XMLTags.UserTag);
		User[] userArray = new User[userlist.size()];
		boolean[] booleanArray = new boolean[userlist.size()];
		
		for (int i = 0; i < userlist.size(); i++){
			String userid = this.unMangle(((Element)userlist.get(i)).getAttributeValue(XMLTags.NameAttrTag));
			KeyValue key = new KeyValue(((Element)userlist.get(i)).getAttributeValue(XMLTags.KeyValueAttrTag));
			boolean hidden = Boolean.valueOf(((Element)userlist.get(i)).getAttributeValue(XMLTags.HiddenTag)).booleanValue();
			userArray[i] = new User(userid, key);
			booleanArray[i] = hidden;
		}
		//create and return the GroupAccess
		return new GroupAccessSome(groupid, hashcode, userArray, booleanArray);
	}
}


/**
 * This method returns an ImageSubVolume object from a XML representation.
 * Creation date: (5/1/2001 5:26:17 PM)
 * @return cbit.vcell.geometry.ImageSubVolume
 * @param param org.jdom.Element
 */
public ImageSubVolume getImageSubVolume(Element param) throws XmlParseException{
	//retrieve the attributes
	String name = this.unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	int handle = Integer.parseInt( param.getAttributeValue(XMLTags.HandleAttrTag) );
	int imagePixelValue = Integer.parseInt( param.getAttributeValue(XMLTags.ImagePixelValueTag) );
 
	//Get the PixelClass from image (image should be a sibling of this subVolume element)
	Element imageElement = param.getParent().getChild(XMLTags.ImageTag);
	if (imageElement==null){
		throw new XmlParseException("image not found in geometry corresponding to ImageSubVolume");
	}
	
	List pixelClassList = imageElement.getChildren(XMLTags.PixelClassTag);
	VCPixelClass pixelClass = null;
	for (int i = 0; i < pixelClassList.size(); i++){
		VCPixelClass pc = getPixelClass((Element)pixelClassList.get(i));
		if (pc.getPixel() == imagePixelValue){
			pixelClass = pc;
		}
	} 
	if (pixelClass==null){
		throw new XmlParseException("image pixelclass(pixel="+imagePixelValue+") not found while creating ImageSubVolume "+name);
	}

	//retrieve the key if there is one
	KeyValue key = null;
	String stringkey = param.getAttributeValue(XMLTags.KeyValueAttrTag);
	
	if ( stringkey!=null && stringkey.length()>0 && this.readKeysFlag ) {
		key = new KeyValue( stringkey );
	}
		
	//Create the new Image SubVolume
	ImageSubVolume newsubvolume = new ImageSubVolume(key, pixelClass, handle);
	//set name
	try {
		newsubvolume.setName(name);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A propertyVetoException was generated when setting the name " + name +" to an ImageSubvolume object!"+" : "+e.getMessage());
	}
	
	return  newsubvolume;
}


/**
 * This method returns an InsideVariable object from a XML Element
 * Creation date: (5/18/2001 6:14:42 PM)
 * @return cbit.vcell.math.InsideVariable
 * @param param org.jdom.Element
 */
public InsideVariable getInsideVariable(Element param) {
	//Get name
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	//get VolVariableRef
	String volvarName = this.unMangle( param.getAttributeValue(XMLTags.VolumeVariableAttrTag) );

	//*** create new InsideVariable ***
	InsideVariable variable = new InsideVariable(name , volvarName);
	
	return variable;
}


/**
 * This method returns a JumpCondition object from a XML Element.
 * Creation date: (5/18/2001 5:10:10 PM)
 * @return cbit.vcell.math.JumpCondition
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public JumpCondition getJumpCondition(Element param) throws XmlParseException {
	//get VolVariable ref
	String varname = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	Element re = XMLDict.getResolvedElement(param, XMLTags.VolumeVariableTag, XMLTags.NameAttrTag, varname);
	String temp = "cbit.vcell.math.VolVariable:" + varname;
	
	VolVariable volVar = (VolVariable)this.dictionary.get(re, temp);
	if ( volVar == null ) {
		throw new XmlParseException("The reference to the VolVariable " + varname + ", could not be resolved in the dictionnary!");
	}
	JumpCondition jumpCondition = new JumpCondition(volVar);

	//process InFlux
	temp = param.getChildText(XMLTags.InFluxTag);
	Expression exp = unMangleExpression( temp );
	jumpCondition.setInFlux(exp);
	

	//process OutFlux
	temp = param.getChildText(XMLTags.OutFluxTag);
	exp = unMangleExpression( temp );
	jumpCondition.setOutFlux( exp );

	return jumpCondition;
}


/**
 * This method returns a Kinetics object from a XML Element based on the value of the kinetics type attribute.
 * Creation date: (3/19/2001 4:42:04 PM)
 * @return cbit.vcell.model.Kinetics
 * @param param org.jdom.Element
 */
public cbit.vcell.model.Kinetics getKinetics(Element param, ReactionStep reaction) throws XmlParseException{

	String type = param.getAttributeValue(XMLTags.KineticsTypeAttrTag);
	Kinetics newKinetics = null;
	try {
		if ( type.equalsIgnoreCase(XMLTags.KineticsTypeGeneralKinetics) ) {
			//create a general kinetics
			newKinetics = new GeneralKinetics(reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeGeneralCurrentKinetics) ) {
			//Create GeneralCurrentKinetics
			newKinetics = new GeneralCurrentKinetics(reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeMassAction) ) {
			//create a Mass Action kinetics
			newKinetics = new MassActionKinetics(reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeNernst) ) {
			// create NernstKinetics
			newKinetics = new NernstKinetics(reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeGHK) ) {
			//create GHKKinetics
			newKinetics = new GHKKinetics(reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeHMM_Irr) ) {
			//create HMM_IrrKinetics
			newKinetics = new HMM_IRRKinetics(reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeHMM_Rev) ) {
			//create HMM_RevKinetics
			newKinetics = new HMM_REVKinetics(reaction);
		} else {
			throw new XmlParseException("Unknown kinetics type: " + type);
		}
	}  catch (ExpressionException e) {
		e.printStackTrace();
		throw new XmlParseException("Error creating the kinetics for reaction: "+reaction.getName()+" : "+e.getMessage());
	}
	
	try {
		newKinetics.reading(true);   // transaction begin flag ... yeah, this is a hack
		
		//Read all of the parameters
		List list = param.getChildren(XMLTags.ParameterTag);

		// add constants that may be used in kinetics.
		VariableHash varHash = getVariablesHash();
		ArrayList reserved = getReservedVars();

		try {
			if (reaction.getStructure() instanceof Membrane){
				Membrane membrane = (Membrane)reaction.getStructure();
				varHash.addVariable(new Constant(membrane.getMembraneVoltage().getName(),new Expression(0.0)));
				reserved.add(membrane.getMembraneVoltage().getName());
			}
			//
			// add Reactants, Products, and Catalysts (ReactionParticipants)
			//
			ReactionParticipant rp [] = reaction.getReactionParticipants();
			for (int i = 0; i < rp.length; i++){
				varHash.addVariable(new Constant(rp[i].getName(), new Expression(0.0)));			
			}
		} catch (MappingException e){
			e.printStackTrace(System.out);
			throw new XmlParseException("error reordering parameters according to dependencies: "+e.getMessage());
		}
		//
		// rename "special" parameters (those that are not "user defined")
		//
		for (int i = 0; i < list.size() ; i++){
			Element xmlParam = (Element)list.get(i);
			String paramName = this.unMangle(xmlParam.getAttributeValue(XMLTags.NameAttrTag));
			String role = xmlParam.getAttributeValue(XMLTags.ParamRoleAttrTag);
			String paramExpStr = xmlParam.getText();
			Expression paramExp = unMangleExpression(paramExpStr);
			try {
				if (varHash.getVariable(paramName) == null){
					varHash.addVariable(new Function(paramName,paramExp));
				} else {
					if (reserved.contains(paramName)) {
						varHash.removeVariable(paramName);
						varHash.addVariable(new Function(paramName, paramExp));
					}
				}
			}catch (MappingException e){
				e.printStackTrace(System.out);
				throw new XmlParseException("error reordering parameters according to dependencies: "+e.getMessage());
			}
			Kinetics.KineticsParameter tempParam = null;
			if (!role.equals(XMLTags.ParamRoleUserDefinedTag)) {
				tempParam = newKinetics.getKineticsParameterFromRole(Kinetics.getParamRoleFromDesc(role));
			}else{
				continue;
			}
			if (tempParam == null) {
				throw new XmlParseException("parameter with role '"+role+"' not found in kinetics type '"+type+"'");
			} 
			//
			// custom name for "special" parameter
			//
			if (!tempParam.getName().equals(paramName)) {
				Kinetics.KineticsParameter multNameParam = (Kinetics.KineticsParameter)newKinetics.getParameter(paramName);
				int n = 0;
				while (multNameParam != null) {
					String tempName = paramName + "_" + n++;
					newKinetics.renameParameter(paramName, tempName);
					multNameParam = (Kinetics.KineticsParameter)newKinetics.getParameter(tempName);
				}
				newKinetics.renameParameter(tempParam.getName(), paramName);
			}
		}
		//
		// create unresolved parameters for all unresolved symbols
		//
		String unresolvedSymbol = varHash.getFirstUnresolvedSymbol();
		while (unresolvedSymbol!=null){
			try {
				varHash.addVariable(new Function(unresolvedSymbol,new Expression(0.0)));  // will turn into an UnresolvedParameter.
			}catch (MappingException e){
				e.printStackTrace(System.out);
				throw new XmlParseException(e.getMessage());
			}
			newKinetics.addUnresolvedParameter(unresolvedSymbol);
			unresolvedSymbol = varHash.getFirstUnresolvedSymbol();
		}
		
		Variable sortedVariables[] = varHash.getReorderedVariables();
		for (int i = sortedVariables.length-1; i >= 0 ; i--){
			if (sortedVariables[i] instanceof Function){
				Function paramFunction = (Function)sortedVariables[i];
				Element xmlParam = null;
				for (int j = 0; j < list.size(); j++){
					Element tempParam = (Element)list.get(j);
					if (paramFunction.getName().equals(unMangle(tempParam.getAttributeValue(XMLTags.NameAttrTag)))){
						xmlParam = tempParam;
						break;
					}
				}
				if (xmlParam==null){
					
					continue; // must have been an unresolved parameter
				}
				String symbol = xmlParam.getAttributeValue(XMLTags.VCUnitDefinitionAttrTag);
				VCUnitDefinition unit = null;
				if (symbol != null) {
					unit = VCUnitDefinition.getInstance(symbol);
				}
				Kinetics.KineticsParameter tempParam = newKinetics.getKineticsParameter(paramFunction.getName());
				if (tempParam == null) {
					newKinetics.addUserDefinedKineticsParameter(paramFunction.getName(), paramFunction.getExpression(), unit);
				} else {
					newKinetics.setParameterValue(tempParam, paramFunction.getExpression());
					tempParam.setUnitDefinition(unit);
				}
			}
		}
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException("Exception: "+e.getMessage()+" while setting parameters for Reaction : " + reaction.getName());
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException("Exception: "+e.getMessage()+" while settings parameters for Reaction : " + reaction.getName());
	} finally {
		newKinetics.reading(false);
	}
	
	return newKinetics;
}


/**
 * This method returns a MathDescription from a XML element.
 * Creation date: (4/26/2001 12:11:14 PM)
 * @return cbit.vcell.math.MathDescription
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public MathDescription getMathDescription(Element param) throws XmlParseException {
	MathDescription mathdes = null;
	Element tempelement;

	//Retrieve Metadata(Version)
	Version version = getVersion(param.getChild(XMLTags.VersionTag));

	//Retrieve attributes
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

	//Create new MathDescription
	if (version != null) {
		mathdes = new MathDescription( version );
	} else {
		mathdes = new MathDescription( name );		
	}

	//set attributes 
	try {
		mathdes.setName( name );
		//String annotation = param.getAttributeValue(XMLTags.AnnotationAttrTag);
		
		//if (annotation!=null) {
			//mathdes.setDescription(this.unMangle(annotation));
		//}
		//add Annotation
		String annotationText  = param.getChildText(XMLTags.AnnotationTag);
		if (annotationText!=null && annotationText.length()>0) {
			mathdes.setDescription(this.unMangle(annotationText));
		}
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException was fired when setting the name " + name + ", to a new MathDescription!"+" : "+e.getMessage());
	}
	
	VariableHash varHash = new VariableHash();
	
	//Retrieve Constant
	Iterator iterator = param.getChildren(XMLTags.ConstantTag).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getConstant(tempelement));
		} catch (MappingException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}

	//Retrieve FilamentRegionVariables
	iterator = param.getChildren(XMLTags.FilamentRegionVariableTag).iterator();
	while (iterator.hasNext()) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getFilamentRegionVariable(tempelement) );
		} catch (MappingException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}

	//Retrieve FilamentVariables
	iterator = param.getChildren(XMLTags.FilamentVariableTag).iterator();
	while (iterator.hasNext()) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getFilamentVariable(tempelement) );
		} catch (MappingException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}
	
	//retrieve InsideVariables
	//**** This variables are for internal USE ******
	
	//Retrieve MembraneRegionVariable
	iterator = param.getChildren(XMLTags.MembraneRegionVariableTag).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getMembraneRegionVariable(tempelement) );
		} catch (MappingException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}

	}
	
	//Retrieve MembraneVariable
	iterator = param.getChildren(XMLTags.MembraneVariableTag).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getMemVariable(tempelement) );
		} catch (MappingException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}

	}
	//retrieve OutsideVariables
	//**** This variables are for internal USE ******

	//Retrieve Volume Region variable
	iterator = param.getChildren(XMLTags.VolumeRegionVariableTag).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getVolumeRegionVariable(tempelement) );
		} catch (MappingException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}

	}
	
	//Retrieve VolumeVariable
	iterator = param.getChildren(XMLTags.VolumeVariableTag).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getVolVariable(tempelement) );
		} catch (MappingException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}

	//Retrieve all the Functions //This needs to be processed before all the variables are read!
	iterator = param.getChildren(XMLTags.FunctionTag).iterator();
	while ( iterator.hasNext() ){
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable(getFunction(tempelement));
		}catch (MappingException e){
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}

	//
	// add all variables at once
	//
	try {
		mathdes.setAllVariables(varHash.getReorderedVariables());
	} catch (MappingException e) {
		e.printStackTrace();
		throw new XmlParseException("A MappingException was fired when adding the Function variables to the MathDescription " + name+" : "+e.getMessage());
	} catch (MathException e) {
		e.printStackTrace();
		throw new XmlParseException("A MathException was fired when adding the Function variables to the MathDescription " + name+" : "+e.getMessage());
	} catch (cbit.vcell.parser.ExpressionBindingException e) {
		e.printStackTrace();
		throw new XmlParseException("A ExpressionBindingException was fired when adding the Function variables to the MathDescription " + name+" : "+e.getMessage());
	}

	//Retrieve CompartmentsSubdomains
	iterator = param.getChildren(XMLTags.CompartmentSubDomainTag).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			mathdes.addSubDomain( getCompartmentSubDomain(tempelement, mathdes) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding a new CompartmentSubDomain to the MathDescription " + name+" : "+e.getMessage());
		}
	}

	//Retrieve MembraneSubdomains
	iterator = param.getChildren(XMLTags.MembraneSubDomainTag).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			mathdes.addSubDomain( getMembraneSubDomain(tempelement, mathdes) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding a new MembraneSubDomain to the MathDescription " + name+" : "+e.getMessage());
		}
	}
	
	//Retrieve the FilamentSubdomain (if any)
	tempelement = param.getChild(XMLTags.FilamentSubDomainTag);
	if (tempelement != null) {
		try {
			mathdes.addSubDomain( getFilamentSubDomain(tempelement, mathdes) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding a new FilamentSubDomain to the MathDescription " + name+" : "+e.getMessage());
		}
	}
	
	return mathdes;
}


/**
 * This method returns a MathModel object from a XML Element.
 * Creation date: (3/13/2001 12:35:00 PM)
 * @return cbit.vcell.mathmodel.MathModel
 * @param param org.jdom.Element
 */
public cbit.vcell.mathmodel.MathModel getMathModel(Element param) throws XmlParseException{
	//Create it
	//set Metadata (version), if any
	Version versionObject = getVersion(param.getChild(XMLTags.VersionTag));
	cbit.vcell.mathmodel.MathModel mathmodel = new cbit.vcell.mathmodel.MathModel(versionObject);
	
	//Set attributes
	String name = this.unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	try {
		mathmodel.setName( name );
		//String annotation = param.getAttributeValue(XMLTags.AnnotationAttrTag);

		//if (annotation!=null) {
			//mathmodel.setDescription(this.unMangle(annotation));
		//}
		//Add annotation
		String annotationText = param.getChildText(XMLTags.AnnotationTag);
		if (annotationText!=null && annotationText.length()>0) {
			mathmodel.setDescription(this.unMangle(annotationText));
		}
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("An error occurred while trying to set the name " + param.getAttributeValue(XMLTags.NameAttrTag) + "to a MathModel!"+" : "+e.getMessage());
	}

	//set MathDescription
	Element tempElem = param.getChild(XMLTags.MathDescriptionTag);
	MathDescription mathDesc = getMathDescription(tempElem);
	
	if ( tempElem != null) {
		mathmodel.setMathDescription( mathDesc );
	} else {
		throw new XmlParseException("MathDescription missing in this MathModel!");
	}

	//set Geometry (if any)
	tempElem = param.getChild(XMLTags.GeometryTag);
	if ( tempElem != null) {
		try {
			mathDesc.setGeometry( getGeometry(tempElem) );
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	} else {
		throw new XmlParseException("It needs to be a Geometry within a MathModel!");
	}
		
	//Set simulations contexts (if any)
	List childList = param.getChildren(XMLTags.SimulationTag);
	cbit.vcell.solver.Simulation[] simList = new cbit.vcell.solver.Simulation[childList.size()];
	
	for (int i = 0; i < childList.size(); i++){
		simList[i] = getSimulation((Element)childList.get(i), mathDesc);
	}
	try {
		mathmodel.setSimulations(simList);
	} catch(java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException occurred when adding the Simulations to the MathModel " + name+" : "+e.getMessage());
	}

	return mathmodel;
}


/**
 * This method returns a MathOverrides object from a XML Element.
 * Creation date: (5/21/2001 3:05:17 PM)
 * @return cbit.vcell.solver.MathOverrides
 * @param param org.jdom.Element
 */
public cbit.vcell.solver.MathOverrides getMathOverrides(Element param, cbit.vcell.solver.Simulation simulation) throws XmlParseException{

	cbit.vcell.solver.MathOverrides mathOverrides = null;
	try {
		//Get the constants
		Object[] elements = param.getChildren().toArray();
		Vector v1 = new Vector();
		Vector v2 = new Vector();
		for (int i = 0; i < elements.length; i++){
			Element e = (Element)elements[i];
			Attribute array = e.getAttribute(XMLTags.ConstantArraySpec);
			if (array != null) {
				// collect scan overrides
				String name = e.getAttributeValue(XMLTags.NameAttrTag);
				int type = array.getIntValue();
				v1.add(ConstantArraySpec.createFromString(name, e.getText(), type));
			} else {
				// collect regular overrides
				v2.add(getConstant(e));
			}
		}
		Constant[] constants = (Constant[])BeanUtils.getArray(v2, Constant.class);
		ConstantArraySpec[] specs = (ConstantArraySpec[])BeanUtils.getArray(v1, ConstantArraySpec.class);
		//create new MathOverrides object
		mathOverrides = new cbit.vcell.solver.MathOverrides(simulation, constants, specs);
	} catch (ExpressionException e) {
		e.printStackTrace();
		throw new XmlParseException("A ExpressionException was fired when adding a Constant to the MathOverrides"+" : "+e.getMessage());
	} catch (DataConversionException e2) {
		e2.printStackTrace();
		throw new XmlParseException("A DataConversionException occured when reading a ConstantArraySpec type"+" : "+e2.getMessage());
	}
	return mathOverrides;
}


/**
 * This method returns a Membrane object from a XML element.
 * Creation date: (4/4/2001 4:17:32 PM)
 * @return cbit.vcell.model.Membrane
 * @param param org.jdom.Element
 */
public Membrane getMembrane(Element param) throws XmlParseException {
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	cbit.vcell.model.Membrane newmembrane = null;

	//retrieve the key if there is one
	KeyValue key = null;
	String stringkey = param.getAttributeValue(XMLTags.KeyValueAttrTag);
	
	if ( stringkey!=null && stringkey.length()>0 && this.readKeysFlag) {
		key = new KeyValue( stringkey );
	}
	
	//try to create new Membrane named "name"
	try {
		newmembrane = new cbit.vcell.model.Membrane(key, name);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(
			"An error occurred while trying to create the Membrane object " + name+" : "+e.getMessage());
	}
	//set inside feature
	String featurename = "cbit.vcell.model.Feature:" + this.unMangle(param.getAttributeValue(XMLTags.InsideFeatureTag));
	Element re = XMLDict.getResolvedElement(param, XMLTags.FeatureTag, XMLTags.NameAttrTag, 
		                                    this.unMangle(param.getAttributeValue(XMLTags.InsideFeatureTag)));
	Feature featureref = (Feature) this.dictionary.get(re, featurename );
	if (featureref ==null) {
		throw new XmlParseException("The feature " + featurename + "could not be retrieved from the dictionnary!");
	}
	newmembrane.setInsideFeature(featureref);
	//set outside feature
	featurename = "cbit.vcell.model.Feature:" + this.unMangle(param.getAttributeValue(XMLTags.OutsideFeatureTag));
	re = XMLDict.getResolvedElement(param, XMLTags.FeatureTag, XMLTags.NameAttrTag, 
		                                    this.unMangle(param.getAttributeValue(XMLTags.OutsideFeatureTag)));
	featureref = (Feature)this.dictionary.get(re, featurename );
	if (featureref ==null) {
		throw new XmlParseException("The feature " + featurename + "could not be retrieved from the dictionnary!");
	}
	newmembrane.setOutsideFeature(featureref);
	//set MemVoltName
	if (param.getAttribute(XMLTags.MemVoltNameTag)==null) {
		throw new XmlParseException("Error reading membrane Voltage Name!");
	}
	String memvoltName = this.unMangle( param.getAttributeValue(XMLTags.MemVoltNameTag) );
	try {
		newmembrane.getMembraneVoltage().setName(memvoltName);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("Error setting the membrane Voltage Name:\n"+e.getMessage());
	}
	
	//*** Add Membrane to the Dictionnary ***
	this.dictionary.put(param, newmembrane.getClass().getName()+":"+name, newmembrane);

	return newmembrane;
}


/**
 * This method retuns a MembraneMapping object from a XML representation.
 * Creation date: (5/7/2001 4:12:03 PM)
 * @return cbit.vcell.mapping.MembraneMapping
 * @param param org.jdom.Element
 */
public MembraneMapping getMembraneMapping(Element param, SimulationContext simulationContext) throws XmlParseException{
	//Retrieve attributes
	String membranename = this.unMangle( param.getAttributeValue(XMLTags.MembraneAttrTag) );
	
	//Retrieve membrane reference
	String temp = "cbit.vcell.model.Membrane:" +membranename;
	Element re = XMLDict.getResolvedElement(param, XMLTags.MembraneTag, XMLTags.NameAttrTag, membranename);
	Membrane membraneref = (Membrane)this.dictionary.get(re, temp);
	if (membraneref == null) {
		throw new XmlParseException("The Membrane "+ temp + " could not be resolved in the dictionnary!");
	}

	//*** Create new Feature Mapping ****
	MembraneMapping memmap = new MembraneMapping(membraneref, simulationContext);

	//Set SurfacetoVolumeRatio
	String ratio = this.unMangle( param.getAttributeValue(XMLTags.SurfaceToVolumeRatioTag) );
	try {
		memmap.getSurfaceToVolumeParameter().setExpression(new Expression(ratio));
	} catch (ExpressionException e) {
		e.printStackTrace();
		throw new XmlParseException("An expressionException was fired when setting the SurfacetoVolumeRatio Expression " + ratio + " to a membraneMapping!"+" : "+e.getMessage());
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(e.getMessage());
	}

	//Set VolumeFraction
	String fraction = this.unMangle( param.getAttributeValue(XMLTags.VolumeFractionTag) );
	try {
		memmap.getVolumeFractionParameter().setExpression(new Expression(fraction));
	} catch (ExpressionException e) {
		e.printStackTrace();
		throw new XmlParseException("An expressionException was fired when setting the VolumeFraction Expression " + fraction + " to a membraneMapping!"+" : "+e.getMessage());
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(e.getMessage());
	}
	//** Set electrical properties **
	//set specific capacitance
	double specificCap = Double.parseDouble(param.getAttributeValue(XMLTags.SpecificCapacitanceTag));
	try {
		memmap.getSpecificCapacitanceParameter().setExpression(new Expression(specificCap));		
	} catch (ExpressionException e) {
		e.printStackTrace();
		throw new XmlParseException(e.getMessage());
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(e.getMessage());
	}
	
	//set flag calculate voltage
	boolean calculateVolt = (Boolean.valueOf(param.getAttributeValue(XMLTags.CalculateVoltageTag))).booleanValue();
	memmap.setCalculateVoltage(calculateVolt);

	//set initial Voltage
	String initialVoltString = param.getAttributeValue(XMLTags.InitialVoltageTag);
	try {
		Expression initialExpr = unMangleExpression(initialVoltString);
		memmap.getInitialVoltageParameter().setExpression(initialExpr);
	} catch (ExpressionException e) {
		e.printStackTrace();
		throw new XmlParseException(e.getMessage());
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(e.getMessage());
	}
	
	return memmap;
}


/**
 * This method returns a MembraneRegionEquation from a XML Element.
 * Creation date: (5/17/2001 3:52:40 PM)
 * @return cbit.vcell.math.MembraneRegionEquation
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public MembraneRegionEquation getMembraneRegionEquation(Element param) throws XmlParseException {
	//get attributes
	String varname = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	//find reference in the dictionnary
	//try a MembraneRegionVariable
	String temp = "cbit.vcell.math.MembraneRegionVariable:"+ varname;
	Element re = XMLDict.getResolvedElement(param, XMLTags.MembraneRegionVariableTag, XMLTags.NameAttrTag, varname);
	MembraneRegionVariable varref = (MembraneRegionVariable)this.dictionary.get(re, temp);
	if (temp == null) {
		throw new XmlParseException("The reference to the MembraneRegion variable "+ varname+ " could not be resolved in the dictionnary!");
	}

	//get Initial condition
	temp = param.getChildText(XMLTags.InitialTag);
	Expression exp;
	exp = unMangleExpression( temp );
	// ** Create the Equation **
	MembraneRegionEquation memRegEq = new MembraneRegionEquation(varref,exp);

	//set the Uniform Rate
	temp = param.getChildText(XMLTags.UniformRateTag);
	exp = unMangleExpression( temp );
	memRegEq.setUniformRateExpression(exp);

	//set the Membrane Rate
	temp = param.getChildText(XMLTags.MembraneRateTag);
	exp = unMangleExpression( temp );
	memRegEq.setMembraneRateExpression(exp);
	
	//get ExactSolution (if any)
/*	temp = param.getChildText(XMLTags.ExactTag);
	if (temp !=null) {
		try {
			Expression expression = new Expression( this.unMangle( temp) );
			odeEquation.setExactSolution( expression);			
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("An ExpressionException was fired when creating the expression: "+ this.unMangle(temp)+" : "+e.getMessage());
		}
	}
	//get ConstructedSolution (if any)
	temp = param.getChildText(XMLTags.ConstructedTag);
	if (temp != null) {
		try {
			Expression expression = new Expression(this.unMangle(temp));
			odeEquation.setConstructedSolution( expression );
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("An ExpressionException was fired when creating the expression: "+ this.unMangle(temp)+" : "+e.getMessage());
		}
	}*/
	
 	return memRegEq;	
}


/**
 * This method returns a MembraneRegionVariable object from a XML Element.
 * Creation date: (5/16/2001 2:56:34 PM)
 * @return cbit.vcell.math.MembraneRegionVariable
 * @param param org.jdom.Element
 */
public MembraneRegionVariable getMembraneRegionVariable(Element param) {
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

	//-- create new MembraneRegionVariable object
	MembraneRegionVariable memRegVariable = new MembraneRegionVariable( name );

	//***add it to the dictionnary ***
	String temp = memRegVariable.getClass().getName()+":"+name;
	this.dictionary.put(param, temp, memRegVariable);

	return memRegVariable;
}


/**
 * This method returns a MembraneSubDomain object from a XML Element.
 * Creation date: (5/18/2001 4:23:30 PM)
 * @return cbit.vcell.math.MembraneSubDomain
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public MembraneSubDomain getMembraneSubDomain(Element param, MathDescription mathDesc) throws XmlParseException {
	//get compartmentSubDomain references
	//inside
	String name = this.unMangle( param.getAttributeValue(XMLTags.InsideCompartmentTag) );
	Element re = XMLDict.getResolvedElement(param, XMLTags.CompartmentSubDomainTag, XMLTags.NameAttrTag, name);
	String temp = "cbit.vcell.math.CompartmentSubDomain:" + name;
	CompartmentSubDomain insideRef = (CompartmentSubDomain)this.dictionary.get(re, temp);
	if  (insideRef == null) {
		throw new XmlParseException("The reference to the inside CompartmentSubDomain "+ name+ ", could not be resolved in the dictionnary!" );
	}
	//outside
	name = this.unMangle( param.getAttributeValue(XMLTags.OutsideCompartmentTag) );
	re = XMLDict.getResolvedElement(param, XMLTags.CompartmentSubDomainTag, XMLTags.NameAttrTag, name);
	temp = "cbit.vcell.math.CompartmentSubDomain:" + name;
	CompartmentSubDomain outsideRef = (CompartmentSubDomain)this.dictionary.get(re, temp);
	if  (outsideRef == null) {
		throw new XmlParseException("The reference to the outside CompartmentSubDomain "+ name+ ", could not be resolved in the dictionnary!" );
	}
	//*** create new Membrane SubDomain ***
	MembraneSubDomain subDomain = new MembraneSubDomain(insideRef, outsideRef);

	//Process BoundaryConditions
	Iterator iterator = param.getChildren(XMLTags.BoundaryTypeTag).iterator();
	while (iterator.hasNext()){
		Element tempelement = (Element)iterator.next();

		//create BoundaryConditionType
		temp = tempelement.getAttributeValue(XMLTags.BoundaryTypeAttrTag);
		BoundaryConditionType bType = new BoundaryConditionType(temp);
		
		//check where it goes
		//Process Xm
		if (tempelement.getAttributeValue(XMLTags.BoundaryAttrTag).equalsIgnoreCase(XMLTags.BoundaryAttrValueXm)) {
			subDomain.setBoundaryConditionXm(bType);
		} else if (tempelement.getAttributeValue(XMLTags.BoundaryAttrTag).equalsIgnoreCase(XMLTags.BoundaryAttrValueXp)) {
			//Process Xp
			subDomain.setBoundaryConditionXp(bType);
		} else  if (tempelement.getAttributeValue(XMLTags.BoundaryAttrTag).equalsIgnoreCase(XMLTags.BoundaryAttrValueYm)) {
			//Process Ym
			subDomain.setBoundaryConditionYm(bType);
		} else if (tempelement.getAttributeValue(XMLTags.BoundaryAttrTag).equalsIgnoreCase(XMLTags.BoundaryAttrValueYp)) {
			//Process Yp
			subDomain.setBoundaryConditionYp(bType);
		} else if (tempelement.getAttributeValue(XMLTags.BoundaryAttrTag).equalsIgnoreCase(XMLTags.BoundaryAttrValueZm)) {
			//Process Zm
			subDomain.setBoundaryConditionZm(bType);
		} else if (tempelement.getAttributeValue(XMLTags.BoundaryAttrTag).equalsIgnoreCase(XMLTags.BoundaryAttrValueZp)) {
			//Process Zp
			subDomain.setBoundaryConditionZp(bType);
		} else {
			// If not indentified throw an exception!!
			throw new XmlParseException("Unknown BoundaryConditionType: " + tempelement.getAttributeValue(XMLTags.BoundaryAttrTag));
		}
	}

	//Add OdeEquations
	iterator = param.getChildren(XMLTags.OdeEquationTag).iterator();
	while (iterator.hasNext()) {
		Element tempElement = (Element)iterator.next();
		OdeEquation odeEquation = getOdeEquation(tempElement);
		
		try {
			subDomain.addEquation( odeEquation );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding an OdeEquation to a MEmbraneSubDomain!"+" : "+e.getMessage());
		}
	}

	//process PdeEquations
	iterator = param.getChildren( XMLTags.PdeEquationTag ).iterator();
	while (iterator.hasNext()) {
		Element tempElement = (Element)iterator.next();

		try {
			subDomain.addEquation( getPdeEquation(tempElement) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding an PdeEquation to the compartmentSubDomain " + name+" : "+e.getMessage());
		}
	}

	//Add JumpConditions
	iterator = param.getChildren(XMLTags.JumpConditionTag).iterator();
	while (iterator.hasNext()) {
		Element tempElement = (Element)iterator.next();
		try {
			subDomain.addJumpCondition( getJumpCondition(tempElement) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding a JumpCondition to a MembraneSubDomain!"+" : "+e.getMessage());
		}
	}

	//Add the FastSystem (if any)
	Element tempElement = param.getChild(XMLTags.FastSystemTag);
	if (tempElement != null) {
		subDomain.setFastSystem( getFastSystemImplicit(tempElement, mathDesc) );
	}

	//add MembraneRegionEquation
	iterator = param.getChildren(XMLTags.MembraneRegionEquationTag).iterator();
	while (iterator.hasNext()) {
		tempElement = (Element)iterator.next();
		try {
			subDomain.addEquation( getMembraneRegionEquation(tempElement) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding a MembraneRegionEquation to a MEmbraneSubDomain!"+" : "+e.getMessage());
		}
	}

	return subDomain;	
}


/**
 * This method returns a MemVariable object from a XML element.
 * Creation date: (5/16/2001 3:17:18 PM)
 * @return cbit.vcell.math.MemVariable
 * @param param org.jdom.Element
 */
public MemVariable getMemVariable(Element param) {
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

	//Create new memVariable
	MemVariable memVariable = new MemVariable( name );

	//***Add it to the dictionnary ***
	String temp = memVariable.getClass().getName() + ":" + name;
	this.dictionary.put(param, temp, memVariable)	;
	
	return memVariable;
}


/**
 * This method returns a MeshSpecification object from a XML Element.
 * Creation date: (5/22/2001 12:05:21 PM)
 * @return cbit.vcell.mesh.MeshSpecification
 * @param param org.jdom.Element
 */
public cbit.vcell.solver.MeshSpecification getMeshSpecification(Element param, Geometry geometry) throws XmlParseException {
	//*** create new MeshSpecification ***
	cbit.vcell.solver.MeshSpecification meshSpec = new cbit.vcell.solver.MeshSpecification(geometry);
	
	//get ISize
	Element size = param.getChild(XMLTags.SizeTag);
	int x = Integer.parseInt( size.getAttributeValue(XMLTags.XAttrTag));
	int y = Integer.parseInt( size.getAttributeValue(XMLTags.YAttrTag));
	int z = Integer.parseInt( size.getAttributeValue(XMLTags.ZAttrTag));

	ISize newsize = new ISize(x, y, z);

	//set ISize
	try {
		meshSpec.setSamplingSize( newsize);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException was fired when setting the ISize object to a new MeshSpecification"+" : "+e.getMessage());
	}
	
	return meshSpec;
}


/**
 * This method creates a Model object from a XML element.
 * Creation date: (3/14/2001 6:14:37 PM)
 * @return cbit.vcell.model.Model
 * @param param org.jdom.Element
 */
public cbit.vcell.model.Model getModel(Element param) throws XmlParseException {
	if (param==null) {
		throw new XmlParseException("Invalid 'NULL' XML 'model' element arrived!");
	}
	//Get version, if any	
	Version version = getVersion(param.getChild(XMLTags.VersionTag));
	cbit.vcell.model.Model newmodel = newmodel = new cbit.vcell.model.Model(version);
	
	try {
		//Set attributes
		newmodel.setName( this.unMangle(param.getAttributeValue(XMLTags.NameAttrTag)) );
		//String annotation = param.getAttributeValue(XMLTags.AnnotationAttrTag);

		//if (annotation!=null) {
			//newmodel.setDescription(this.unMangle(annotation));
		//}
		//Add annotation
		String annotationText = param.getChildText(XMLTags.AnnotationTag);
		if (annotationText!=null && annotationText.length()>0) {
			newmodel.setDescription(this.unMangle(annotationText));
		}

		//***Add Model to the dictionnary***
		dictionary.put(param, 
			newmodel.getClass().getName() + ":" + newmodel.getName(),
			newmodel);
		//Add Species (Compounds)
		Iterator iterator = param.getChildren(XMLTags.SpeciesTag).iterator();
		while (iterator.hasNext()) {
			org.jdom.Element temp = (Element) iterator.next();
			newmodel.addSpecies(getSpecies(temp));
		}
		//Add Structures
		LinkedList newstructures = new LinkedList();
		//(features)
		List children = param.getChildren(XMLTags.FeatureTag);
		for (int i = 0; i < children.size(); i++) {
			newstructures.add( getFeature((Element) children.get(i)) );
		}
		//(Membrane)
		children = param.getChildren(XMLTags.MembraneTag);
		for (int i = 0; i < children.size(); i++) {
			newstructures.add( getMembrane((Element) children.get(i)) );
		}
		if (newstructures.size()>0) {
			Structure[] structarray = new Structure[newstructures.size()];
			newstructures.toArray(structarray);
			// Add all the retrieved structures
			newmodel.setStructures( structarray );			
		}

		//Add SpeciesContexts
		children = param.getChildren(XMLTags.SpeciesContextTag);
		SpeciesContext[] newspeccon = new SpeciesContext[children.size()];
		for (int i=0 ; i < children.size() ; i++) {
			newspeccon[i] = getSpeciesContext( (Element)children.get(i));
		}
		newmodel.setSpeciesContexts(newspeccon);
		//Add Reaction steps (if available)
		//(Simplereaction)
		iterator = param.getChildren(XMLTags.SimpleReactionTag).iterator();
		while (iterator.hasNext()) {
			org.jdom.Element temp = (Element) iterator.next();
			newmodel.addReactionStep(getSimpleReaction(temp));
		}
		//(fluxStep)
		iterator = param.getChildren(XMLTags.FluxStepTag).iterator();
		while (iterator.hasNext()) {
			org.jdom.Element temp = (Element) iterator.next();
			newmodel.addReactionStep(getFluxReaction(temp, newmodel));
		}
		//Add Diagrams
		children = param.getChildren(XMLTags.DiagramTag);
		if (children.size()>0) {
			Diagram[] newdiagrams = new Diagram[children.size()];
			for (int i = 0; i < children.size(); i++) {
				newdiagrams[i] = getDiagram((Element) children.get(i));
			}
			newmodel.setDiagrams(newdiagrams);
		}
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(e.getMessage());
	}

	return newmodel;
}


/**
 * This method returns a nodeReference onject from a XML representation.
 * Creation date: (4/24/2001 5:35:56 PM)
 * @return cbit.vcell.model.NodeReference
 * @param param org.jdom.Element
 */
public NodeReference getNodeReference(Element param) throws XmlParseException{
	String tempname = param.getName();
	NodeReference newnoderef = null;
	
	//determine the type of nodereference to create
	if  ( tempname.equalsIgnoreCase(XMLTags.SpeciesContextShapeTag) ) {
		int type = NodeReference.SPECIES_CONTEXT_NODE;
		String name = this.unMangle(param.getAttributeValue( XMLTags.SpeciesContextRefAttrTag ));
		java.awt.Point location = new java.awt.Point( Integer.parseInt(param.getAttributeValue(XMLTags.LocationXAttrTag)), Integer.parseInt(param.getAttributeValue(XMLTags.LocationYAttrTag)) );

		newnoderef = new NodeReference(type, name, location);
	} else if  ( tempname.equalsIgnoreCase(XMLTags.SimpleReactionShapeTag) ) {
		int type = NodeReference.SIMPLE_REACTION_NODE;
		String name = this.unMangle(param.getAttributeValue( XMLTags.SimpleReactionRefAttrTag ));
		java.awt.Point location = new java.awt.Point( Integer.parseInt(param.getAttributeValue(XMLTags.LocationXAttrTag)), Integer.parseInt(param.getAttributeValue(XMLTags.LocationYAttrTag)) );

		newnoderef = new NodeReference(type, name, location);
	} else if  ( tempname.equalsIgnoreCase(XMLTags.FluxReactionShapeTag) ) {
		int type = NodeReference.FLUX_REACTION_NODE;
		String name = this.unMangle(param.getAttributeValue( XMLTags.FluxReactionRefAttrTag ));
		java.awt.Point location = new java.awt.Point( Integer.parseInt(param.getAttributeValue(XMLTags.LocationXAttrTag)), Integer.parseInt(param.getAttributeValue(XMLTags.LocationYAttrTag)) );

		newnoderef = new NodeReference(type, name, location);
	} else {
		throw new XmlParseException("An unknown type was found " + tempname+",when processing noderefence!");
	}
	
	return newnoderef;
}


/**
 * This method returns an OdeEquation from a XML Element.
 * Creation date: (5/17/2001 3:52:40 PM)
 * @return cbit.vcell.math.OdeEquation
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public OdeEquation getOdeEquation(Element param) throws XmlParseException {
	//get attributes
	String varname = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	//find reference in the dictionnary
	//try as a VolVariable
	Variable varref = null;
	String temp = "cbit.vcell.math.VolVariable:"+ varname;
	Element re = XMLDict.getResolvedElement(param, XMLTags.VolumeVariableTag, XMLTags.NameAttrTag, varname);
	Object entry = this.dictionary.get(re, temp);

	if (entry!=null) {
		varref = (VolVariable)entry;
	} else {
		//try to get a MembraneVariable
	    re = XMLDict.getResolvedElement(param, XMLTags.MembraneVariableTag, XMLTags.NameAttrTag, varname);
		temp = "cbit.vcell.math.MemVariable:"+ varname;
		entry = this.dictionary.get(re, temp);
		
		if (entry!=null) {
			varref = (MemVariable)entry;
		}
	}
	
	//Make sure that the reference is not empty!!!
	if (varref == null) {
		throw new XmlParseException("The reference to the variable "+ varname+ " in a OdeEquation could not be resolved in the dictionnary!");
	}	
	
	//get Initial condition
	temp = param.getChildText(XMLTags.InitialTag);
	Expression initialexp = null;
	
	if (temp!=null && temp.length()>0) {
		initialexp = unMangleExpression(temp);
	}
	
	//Get Rate condition
	temp = param.getChildText(XMLTags.RateTag);
	Expression rateexp = null;
	if (temp!=null && temp.length()>0) {
		rateexp = unMangleExpression((temp));
	}
	
	//--- Create the OdeEquation object ---
	OdeEquation odeEquation = new OdeEquation(varref, initialexp, rateexp);

	//add specific solutions expressions
	String solType = param.getAttributeValue(XMLTags.SolutionTypeTag);

	if (solType.equalsIgnoreCase(XMLTags.ExactTypeTag)) {
		String solutionExp = param.getChildText(XMLTags.SolutionExpressionTag);
		
		if (solutionExp!=null && solutionExp.length()>0) {
			Expression expression = unMangleExpression(solutionExp);			
			odeEquation.setExactSolution( expression );	
		}
	}

 	return odeEquation;	
}


public Origin getOrigin(Element parsed){
	double x = Double.parseDouble( parsed.getAttributeValue(XMLTags.XAttrTag) );
	double y = Double.parseDouble( parsed.getAttributeValue(XMLTags.YAttrTag) );
	double z = Double.parseDouble( parsed.getAttributeValue(XMLTags.ZAttrTag) );
	Origin origin = new Origin(x,y,z);

	return origin;
}


/**
 * This method returns a TimeStep object from a XML Element.
 * Creation date: (5/22/2001 11:45:33 AM)
 * @return cbit.vcell.solver.TimeStep
 * @param param org.jdom.Element
 */
public cbit.vcell.solver.OutputTimeSpec getOutputTimeSpec(Element param) {
	if (param != null) {
		//get attributes
		if (param.getAttributeValue(XMLTags.KeepEveryAttrTag) != null) {
			int keepEvery = Integer.parseInt(param.getAttributeValue(XMLTags.KeepEveryAttrTag));
			int keepAtMost = Integer.parseInt(param.getAttributeValue(XMLTags.KeepAtMostAttrTag));
			return new cbit.vcell.solver.DefaultOutputTimeSpec(keepEvery, keepAtMost);		
		} else if (param.getAttributeValue(XMLTags.OutputTimeStepAttrTag) != null) {
			double outputStep = Double.parseDouble(param.getAttributeValue(XMLTags.OutputTimeStepAttrTag));
			return new cbit.vcell.solver.UniformOutputTimeSpec(outputStep);		
		} else if (param.getAttributeValue(XMLTags.OutputTimesAttrTag) != null) {
			String line = param.getAttributeValue(XMLTags.OutputTimesAttrTag);
			return cbit.vcell.solver.ExplicitOutputTimeSpec.fromString(line);		
		}
	}
	return null;
}


/**
 * This method returns an OutsideVariable object from a XML Element
 * Creation date: (5/18/2001 6:14:42 PM)
 * @return cbit.vcell.math.InsideVariable
 * @param param org.jdom.Element
 */
public OutsideVariable getOutsideVariable(Element param) {
	//Get name
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	//get VolVariableRef
	String volvarName = this.unMangle( param.getAttributeValue(XMLTags.VolumeVariableAttrTag) );

	//*** create new OutsideVariable ***
	OutsideVariable variable = new OutsideVariable(name , volvarName);
	
	return variable;
}


/**
 * This method returns a PdeEquation from a XML element.
 * Creation date: (4/26/2001 12:11:14 PM)
 * @return cbit.vcell.math.PdeEquation
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */

public PdeEquation getPdeEquation(Element param) throws XmlParseException {
    //Retrieve the variable reference
    String name = this.unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
    //try to get a MemVariable from the dictionnary
    String temp = "cbit.vcell.math.MemVariable:" + name;
    Element re = XMLDict.getResolvedElement(param, XMLTags.MembraneVariableTag, XMLTags.NameAttrTag, name);
    Variable varref = (MemVariable) this.dictionary.get(re, temp);
    
    if (varref == null) {
        //try to get a VolVariable
        re = XMLDict.getResolvedElement(param, XMLTags.VolumeVariableTag, XMLTags.NameAttrTag, name);
        temp = "cbit.vcell.math.VolVariable:" + name;
        varref = (VolVariable) this.dictionary.get(re, temp);
        if (varref == null) {
            //if not founded throw an exception
            throw new XmlParseException( "The variable " + name
                    + " for a PdeEquation, could not be resolved in the dictionnary!");
        }
    }
    PdeEquation pdeEquation = null;
    
    try {
        //Retrieve the initial expression
        temp = param.getChildText(XMLTags.InitialTag);
        Expression initialExp = null;
        if (temp!=null && temp.length()>0) {
	        initialExp = unMangleExpression(temp);        	
        }

        //Retrieve the Rate Expression
        temp = param.getChildText(XMLTags.RateTag);
        Expression rateExp = null;
        if (temp!=null && temp.length()>0) {
        	rateExp = unMangleExpression(temp);
        }

        //Retrieve the diffusion rate expression
        temp = param.getChildText(XMLTags.DiffusionTag);
        Expression difExp = null;
        if (temp!=null && temp.length()>0) {
        	difExp = unMangleExpression(temp);
        }
        
        //*** Create new PdeEquation object ****
        pdeEquation =  new PdeEquation(varref, initialExp, rateExp, difExp);
        //***** *****

		//add specific solutions expressions
		String solType = param.getAttributeValue(XMLTags.SolutionTypeTag);

		if (solType.equalsIgnoreCase(XMLTags.ExactTypeTag)) {
			String solutionExp = param.getChildText(XMLTags.SolutionExpressionTag);
			
			if (solutionExp!=null && solutionExp.length()>0) {
				Expression expression = unMangleExpression(solutionExp);			
				pdeEquation.setExactSolution( expression );	
			}
		}
        
        //Retrieve Boudaries (if any)
        Element tempelement = param.getChild(XMLTags.BoundariesTag);
        if (tempelement != null) {
            Expression newexp = null;
            //Xm
            temp = tempelement.getAttributeValue(XMLTags.BoundaryAttrValueXm);
            if (temp != null) {
                newexp = unMangleExpression(temp);
                pdeEquation.setBoundaryXm(newexp);
            }
            //Xp
            temp = tempelement.getAttributeValue(XMLTags.BoundaryAttrValueXp);
            if (temp != null) {
                newexp = unMangleExpression(temp);
                pdeEquation.setBoundaryXp(newexp);
            }
            //Ym
            temp = tempelement.getAttributeValue(XMLTags.BoundaryAttrValueYm);
            if (temp != null) {
                newexp = unMangleExpression(temp);
                pdeEquation.setBoundaryYm(newexp);
            }
            //Yp
            temp = tempelement.getAttributeValue(XMLTags.BoundaryAttrValueYp);
            if (temp != null) {
                newexp = unMangleExpression(temp);
                pdeEquation.setBoundaryYp(newexp);
            }
            //Zm
            temp = tempelement.getAttributeValue(XMLTags.BoundaryAttrValueZm);
            if (temp != null) {
                newexp = unMangleExpression(temp);
                pdeEquation.setBoundaryZm(newexp);
            }
            //Zp
            temp = tempelement.getAttributeValue(XMLTags.BoundaryAttrValueZp);
            if (temp != null) {
                newexp = unMangleExpression(temp);
                pdeEquation.setBoundaryZp(newexp);
            }
        }
        //add Velocity
        Element velocityE = param.getChild(XMLTags.VelocityTag);
        if (velocityE != null) {
	        String tempStr = null;
	        boolean dummyVel = true;
	        tempStr = velocityE.getAttributeValue(XMLTags.XAttrTag);
	        if (tempStr != null) {
	       		pdeEquation.setVelocityX(new Expression(tempStr));                  //all velocity dimensions are optional.
				if (dummyVel) {
					dummyVel = false;
				}
	       	}
	        tempStr = velocityE.getAttributeValue(XMLTags.YAttrTag);
	        if (tempStr != null) {
				pdeEquation.setVelocityY(new Expression(tempStr));
				if (dummyVel) {
					dummyVel = false;
				}
	        }
	        tempStr = velocityE.getAttributeValue(XMLTags.ZAttrTag);
	        if (tempStr != null) {
				pdeEquation.setVelocityZ(new Expression(tempStr));
				if (dummyVel) {
					dummyVel = false;
				}
	        }
	        if (dummyVel) {
	        	throw new XmlParseException("Void Velocity element found under PDE for: " + name);
        	} 
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        throw new XmlParseException(e.getMessage());
    }

    return pdeEquation;
}


/**
 * This method returns a VCImageRegion from a XML Representation.
 * Creation date: (5/2/2001 12:17:05 PM)
 * @return cbit.image.VCImageRegion
 * @param param org.jdom.Element
 */
public VCPixelClass getPixelClass(Element param) {
	//Read attributes
	String pixelClassName = this.unMangle(param.getAttributeValue( XMLTags.NameAttrTag));
	int pixelvalue = Integer.parseInt( param.getAttributeValue(XMLTags.ImagePixelValueTag) );

	//retrieve the key if there is one
	KeyValue key = null;
	String stringkey = param.getAttributeValue(XMLTags.KeyValueAttrTag);
	
	if ( stringkey!=null && stringkey.length()>0 && this.readKeysFlag ) {
		key = new KeyValue( stringkey );
	}
		
	return new VCPixelClass(key, pixelClassName, pixelvalue);
}


/**
 * This method returns a Product object from a XML representation.
 * Creation date: (5/4/2001 2:22:56 PM)
 * @return cbit.vcell.model.Product
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public Product getProduct(Element param, SimpleReaction reaction) throws XmlParseException {
    //retrieve the key if there is one
    KeyValue key = null;
    String keystring = param.getAttributeValue(XMLTags.KeyValueAttrTag);
    
    if (keystring != null && keystring.length()>0 && this.readKeysFlag) {
        key = new KeyValue(keystring);
    }

    String speccontref = this.unMangle(param.getAttributeValue(XMLTags.SpeciesContextRefAttrTag));
    //Resolve reference to the SpeciesContext
    Element re = XMLDict.getResolvedElement(param, XMLTags.SpeciesContextTag, XMLTags.NameAttrTag, speccontref);
    String tempname = "cbit.vcell.model.SpeciesContext:" + speccontref;
    SpeciesContext speccont = (SpeciesContext) dictionary.get(re, tempname);
    if (speccont == null) {
        throw new XmlParseException(
            "The reference to the SpecieContext "
                + tempname
                + " for a Product could not be resolved in the dictionnary!");
    }
    //Retrieve Stoichiometry
    int stoch = 1;
    org.jdom.Attribute tempAttrib = param.getAttribute(XMLTags.StoichiometryAttrTag);
    if (tempAttrib != null) {
    	 String temp = tempAttrib.getValue();
    	 if (temp.length()>0) {
    	 	stoch = Integer.parseInt(temp);
    	 }
    }
    //int stoch = Integer.parseInt(param.getAttributeValue(XMLTags.StoichiometryAttrTag));

    return new Product(key, reaction, speccont, stoch);
}


/**
 * This method returns a Reactant object from a XML representation.
 * Creation date: (5/4/2001 2:22:56 PM)
 * @return cbit.vcell.model.Reactant
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public Reactant getReactant(Element param, SimpleReaction reaction) throws XmlParseException {
    //retrieve the key if there is one
    String keystring = param.getAttributeValue(XMLTags.KeyValueAttrTag);
    KeyValue key = null;

    if (keystring != null && keystring.length()>0 && this.readKeysFlag) {
        key = new KeyValue(keystring);
    }

    String speccontref =
        this.unMangle(param.getAttributeValue(XMLTags.SpeciesContextRefAttrTag));
    //Resolve reference to the SpeciesContext
    Element re = XMLDict.getResolvedElement(param, XMLTags.SpeciesContextTag, XMLTags.NameAttrTag, speccontref);
    String tempname = "cbit.vcell.model.SpeciesContext:" + speccontref;
    SpeciesContext speccont = (SpeciesContext) dictionary.get(re, tempname);
    if (speccont == null) {
        throw new XmlParseException(
            "The reference to the SpecieContext "
                + tempname
                + " for a SimpleReaction could not be resolved in the dictionnary!");
    }
    //Retrieve Stoichiometry
    int stoch = 1;
    org.jdom.Attribute tempArg = param.getAttribute(XMLTags.StoichiometryAttrTag);
    if (tempArg!= null) {
	    String tempValue = tempArg.getValue();
	    if (tempValue.length()>0)
    	stoch = Integer.parseInt(tempValue);
    	//param.getAttributeValue(XMLTags.StoichiometryAttrTag));
    }

    //return new Reactant(newkey, reaction, speccont, stoch);
    return new Reactant(key, reaction, speccont, stoch);
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2001 4:13:26 PM)
 * @return cbit.vcell.mapping.ReactionSpec
 * @param param org.jdom.Element
 */
public ReactionSpec getReactionSpec(Element param) throws XmlParseException{
	ReactionSpec reactionspec = null;

	//retrieve the reactionstep reference
	String reactionstepname = this.unMangle( param.getAttributeValue(XMLTags.ReactionStepRefAttrTag) );
	//try to recover a fluxReaction
	Element re = XMLDict.getResolvedElement(param, XMLTags.FluxStepTag, XMLTags.NameAttrTag, reactionstepname);
	String temp = "cbit.vcell.model.FluxReaction:" + reactionstepname;
	ReactionStep reactionstepref = (ReactionStep)this.dictionary.get(re, temp);
	
	if (reactionstepref ==null) {
		//try to recover a SimpleReaction
		re = XMLDict.getResolvedElement(param, XMLTags.SimpleReactionTag, XMLTags.NameAttrTag, reactionstepname);
		temp = "cbit.vcell.model.SimpleReaction:" + reactionstepname;
		reactionstepref = (ReactionStep)this.dictionary.get(re, temp);
		
		if (reactionstepref ==null) {
			throw new XmlParseException("The reference to the ReactionStep " + reactionstepname + ", could not be resolved in the dictionnary!");
		}
	}
	//Create the new SpeciesContextSpec
	reactionspec = new ReactionSpec(reactionstepref);

	//set the reactionMapping value
	temp = param.getAttributeValue(XMLTags.ReactionMappingAttrTag);
	try {
		reactionspec.setReactionMapping( temp );
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException was fired when setting the reactionMapping value " + temp +", in a reactionSpec object!"+" : "+e.getMessage());
	}
	
	return reactionspec;
}


/**
 * This method returns a Kinetics object from a XML Element based on the value of the kinetics type attribute.
 * Creation date: (3/19/2001 4:42:04 PM)
 * @return cbit.vcell.model.Kinetics
 * @param param org.jdom.Element
 */
private ArrayList getReservedVars() {
	
	ArrayList reservedVars = new ArrayList();
	//
	// add constants that may be used in kinetics.
	//
	reservedVars.add(ReservedSymbol.FARADAY_CONSTANT.getName());
	reservedVars.add(ReservedSymbol.FARADAY_CONSTANT_NMOLE.getName());
	reservedVars.add(ReservedSymbol.GAS_CONSTANT.getName());
	reservedVars.add(ReservedSymbol.KMILLIVOLTS.getName());
	reservedVars.add(ReservedSymbol.KMOLE.getName());
	reservedVars.add(ReservedSymbol.N_PMOLE.getName());
	reservedVars.add(ReservedSymbol.TEMPERATURE.getName());
	reservedVars.add(ReservedSymbol.K_GHK.getName());
	reservedVars.add(ReservedSymbol.TIME.getName());
	
	return reservedVars;
}


/**
 * This method returns a SimpleReaction object from a XML element.
 * Creation date: (3/16/2001 11:52:02 AM)
 * @return cbit.vcell.model.SimpleReaction
 * @param param org.jdom.Element
 */
public cbit.vcell.model.SimpleReaction getSimpleReaction(Element param) throws XmlParseException {
    //resolve reference to the  structure that it belongs to.
    String tempname = "cbit.vcell.model.Feature:" + this.unMangle(param.getAttributeValue(XMLTags.StructureAttrTag));
    Element re = XMLDict.getResolvedElement(param, XMLTags.FeatureTag, XMLTags.NameAttrTag, 
	    									this.unMangle(param.getAttributeValue(XMLTags.StructureAttrTag)));
    cbit.vcell.model.Structure structureref = (Structure) dictionary.get(re, tempname);
    
    if (structureref == null) {
        //if it is not a Feature, try with membrane
        re = XMLDict.getResolvedElement(param, XMLTags.MembraneTag, XMLTags.NameAttrTag, 
	    									this.unMangle(param.getAttributeValue(XMLTags.StructureAttrTag)));
        tempname = "cbit.vcell.model.Membrane:" + this.unMangle(param.getAttributeValue(XMLTags.StructureAttrTag));
        structureref = (Structure) dictionary.get(re, tempname);
    } else if (structureref == null) {
            throw new XmlParseException("The structure " + tempname + "could not be resolved in the dictionnary!");
    }

    //try to get keValue information
    String keystring = param.getAttributeValue(XMLTags.KeyValueAttrTag);
    KeyValue key = null;
    
    if (keystring!=null && keystring.length()>0 && this.readKeysFlag) {
    	key = new KeyValue(keystring);
    }
        
    //---Instantiate a new Simplereaction---
    cbit.vcell.model.SimpleReaction simplereaction = null;
    String name = this.unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
    
    try {
        simplereaction = new cbit.vcell.model.SimpleReaction(structureref, key, name);
    } catch (java.beans.PropertyVetoException e) {
        e.printStackTrace();
        throw new XmlParseException("An error occurred while trying to create the simpleReaction " + name+" : "+e.getMessage());
    }
    
	//set the fluxOption
	String fluxOptionString = null;
	fluxOptionString = param.getAttributeValue(XMLTags.FluxOptionAttrTag);
	
	if (fluxOptionString!=null&&fluxOptionString.length()>0){
		try {
			if (fluxOptionString.equals(XMLTags.FluxOptionElectricalOnly)){
				simplereaction.setPhysicsOptions(simplereaction.PHYSICS_ELECTRICAL_ONLY);
			}else if (fluxOptionString.equals(XMLTags.FluxOptionMolecularAndElectrical)){
				simplereaction.setPhysicsOptions(simplereaction.PHYSICS_MOLECULAR_AND_ELECTRICAL);
			}else if (fluxOptionString.equals(XMLTags.FluxOptionMolecularOnly)){
				simplereaction.setPhysicsOptions(simplereaction.PHYSICS_MOLECULAR_ONLY);
			} 
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new XmlParseException("A propertyVetoException was fired when setting the fluxOption to the flux reaction " + name+" : "+e.getMessage());
		}
	}
	//set the fluxcarrier
	String fluxValue = param.getAttributeValue(XMLTags.FluxCarrierValenceAttrTag);

	if (fluxValue!=null && fluxValue.length()!=0) {
		int carrierValue = Integer.parseInt(fluxValue);

		try {
			simplereaction.getChargeCarrierValence().setExpression(new Expression((double)carrierValue));
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException("Veto error when setting tha ChargeVarrierValence "+ carrierValue + "to reaction "+name+" : "+e.getMessage());
		}
	}

	//Add Reactants
	try {
		Iterator iterator = param.getChildren(XMLTags.ReactantTag).iterator();

		while (iterator.hasNext()) {
			Element temp = (Element) iterator.next();

			//Add Reactant to this SimpleReaction
			simplereaction.addReactionParticipant(getReactant(temp, simplereaction));
		}
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("Error adding a reactant to the reaction "+ name+" : "+e.getMessage());
	}

	//Add Products
	try {
		Iterator iterator = param.getChildren(XMLTags.ProductTag).iterator();
		
		while (iterator.hasNext()) {
			Element temp = (Element) iterator.next();
			
			//Add Product to this simplereaction
			simplereaction.addReactionParticipant(getProduct(temp, simplereaction));
        }
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("Error adding a product to the reaction "+ name+" : "+e.getMessage());
	}

	//Add Catalyst(Modifiers)
	try {
		Iterator iterator = param.getChildren(XMLTags.CatalystTag).iterator();

		while (iterator.hasNext()) {
			Element temp = (Element)iterator.next();
			simplereaction.addReactionParticipant(getCatalyst(temp, simplereaction));
		}
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("Error adding a catalyst to the reaction "+ name+" : "+e.getMessage());
	}
 
	//Add Kinetics
	Element tempKinet = param.getChild(XMLTags.KineticsTag);

	if (tempKinet!= null) {
		simplereaction.setKinetics(getKinetics(tempKinet, simplereaction));
	}

    //**** Add the SimpleReaction to the dictionnary ****
    String temp = simplereaction.getClass().getName() + ":" + simplereaction.getName();
    this.dictionary.put(param, temp, simplereaction);

    return simplereaction;
}


/**
 * This method returns a Simulation object from a XML element.
 * Creation date: (4/26/2001 12:14:30 PM)
 * @return cbit.vcell.solver.Simulation
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public cbit.vcell.solver.Simulation getSimulation(Element param, MathDescription mathDesc) throws XmlParseException {
	//retrive metadata (if any)
	SimulationVersion simulationVersion = getSimulationVersion(param.getChild(XMLTags.VersionTag));
	
	//create new simulation
	cbit.vcell.solver.Simulation simulation = null;
		
	if (simulationVersion!=null) {
		simulation = new cbit.vcell.solver.Simulation(simulationVersion, mathDesc);
	} else {
		simulation = new cbit.vcell.solver.Simulation(mathDesc);
	}
	
	//set attributes
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	try {
		simulation.setName(name);
		//String annotation = param.getAttributeValue(XMLTags.AnnotationAttrTag);

		//if (annotation!=null) {
			//simulation.setDescription(this.unMangle(annotation));
		//}
		//Add Annotation
		String annotationText = param.getChildText(XMLTags.AnnotationTag);
		if (annotationText!=null && annotationText.length()>0) {
			simulation.setDescription(this.unMangle(annotationText));
		}
	}catch (java.beans.PropertyVetoException e) {
		throw new XmlParseException(e.getMessage());
	}

	//Retrieve MathOverrides
		simulation.setMathOverrides( getMathOverrides( param.getChild(XMLTags.MathOverridesTag), simulation) );

	//Retrieve SolverTaskDescription
	try {
		simulation.setSolverTaskDescription( getSolverTaskDescription(param.getChild(XMLTags.SolverTaskDescriptionTag), simulation) );
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException was fired when setting the SolverTaskDescroiption object to the Simulation object "+ name+" : "+e.getMessage());
	}

	//Retrieve MeshEspecification (if any)
	Element tempElement = param.getChild(XMLTags.MeshSpecTag);
	
	if (tempElement != null) {
		try {
			simulation.setMeshSpecification( getMeshSpecification(tempElement, mathDesc.getGeometry()) );
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException("A ProperyVetoException was fired when setting the MeshSpecification to a new Simulation!"+" : "+e.getMessage());
		}
	}
	
	return simulation;
}


/**
 * This method returns a SimulationContext from a XML representation.
 * Creation date: (4/2/2001 3:19:01 PM)
 * @return cbit.vcell.mapping.SimulationContext
 * @param param org.jdom.Element
 */
public cbit.vcell.mapping.SimulationContext getSimulationContext(Element param, cbit.vcell.biomodel.BioModel biomodel) throws XmlParseException{
	//get the name
	String name = this.unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	//Retrieve Geometry
	Geometry newgeometry = null;
	try {
		newgeometry = getGeometry( param.getChild(XMLTags.GeometryTag));
	} catch (Throwable e) {
		e.printStackTrace();
		String stackTrace = null;
		try{
			java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
			java.io.PrintStream ps = new java.io.PrintStream(bos);
			e.printStackTrace(ps);
			ps.flush();
			bos.flush();
			stackTrace = new String(bos.toByteArray());
			ps.close();
			bos.close();
		}catch(Exception e2){
			//do Nothing
		}
		throw new XmlParseException(
			"A Problem occurred while retrieving the geometry for the simulationContext " + name+"\n"+
			e.getClass().getName()+"\n"+e.getMessage()+"\n"+
			stackTrace);
	}
	
	//Retrieve MathDescription(if there is no MathDescription skip it)
	MathDescription newmathdesc = null;
	Element xmlMathDescription = param.getChild(XMLTags.MathDescriptionTag);
	if (xmlMathDescription!=null) {
		newmathdesc = getMathDescription( xmlMathDescription );
		
		/////// postprocess //////////
		try {
			newmathdesc.setGeometry(newgeometry);	//this step is needed!
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException("a PropertyVetoException was fired when setting the Geometry to the Mathdescription in the simContext "+ name+" : "+e.getMessage());
		}
	}
	
	//Retrieve Version (Metada)
	Version version = getVersion( param.getChild(XMLTags.VersionTag) );

	//------ Create SimContext ------
	SimulationContext newsimcontext = null;
	
	try {
		newsimcontext = new SimulationContext(biomodel.getModel(), newgeometry, newmathdesc, version);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A propertyveto exception was generated when creating the new SimulationContext " + name+" : "+e.getMessage());
	}
	
	//set attributes
	try {
		newsimcontext.setName(name);
		//String annotation = param.getAttributeValue(XMLTags.AnnotationAttrTag);
		
		//if (annotation!=null) {
			//newsimcontext.setDescription(this.unMangle(annotation));
		//}
		//Add annotation
		String annotation = param.getChildText(XMLTags.AnnotationTag);
		if (annotation!=null && annotation.length()>0) {
			newsimcontext.setDescription(this.unMangle(annotation));
		}
	} catch(java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A Propertyveto exception was generated when seting the name to the simulationContext " + name+" : "+e.getMessage());
	}
	String tempchar = param.getAttributeValue(XMLTags.CharacteristicSizeTag);
	if (tempchar!=null) {
		try {
			newsimcontext.setCharacteristicSize( Double.valueOf(tempchar) );
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException("A PropertyVetoException was fired when setting the CharacteristicSize "+ tempchar+" : "+e.getMessage());
		}
	}
	//
	//-Process the GeometryContext-
	//
	Element tempelement =  param.getChild(XMLTags.GeometryContextTag);
	LinkedList maplist = new LinkedList();
	//Retrieve FeatureMappings
	Iterator iterator = tempelement.getChildren(XMLTags.FeatureMappingTag).iterator();
	while ( iterator.hasNext() ) {
		maplist.add( getFeatureMapping((Element)(iterator.next()), newsimcontext) );
	}
	//Retrieve MembraneMappings
	iterator = tempelement.getChildren(XMLTags.MembraneMappingTag).iterator();
	while (iterator.hasNext()) {
		maplist.add( getMembraneMapping((Element)(iterator.next()), newsimcontext) );
	}
	// Add these mappings to the internal geometryContext of this simcontext
	StructureMapping[] structarray = new StructureMapping[maplist.size()];
	maplist.toArray(structarray);
	try {
		newsimcontext.getGeometryContext().setStructureMappings(structarray);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PopertyVetoException was fired when trying to set the StructureMappings array to the Geometrycontext of the SimContext "+ name+" : "+e.getMessage());
	}
	//
	//-Process the ReactionContext-
	//
	tempelement =  param.getChild(XMLTags.ReactionContextTag);
	// Retrieve ReactionSpecs
	List children = tempelement.getChildren(XMLTags.ReactionSpecTag);
	if (children.size()!=0) {
		if (children.size()!= biomodel.getModel().getReactionSteps().length) {
			throw new XmlParseException("The number of reactions is not consistant.\n"+"Model reactions="+biomodel.getModel().getReactionSteps().length+", Reaction specs="+children.size());
		}
		//*NOTE: Importing a model from other languages does not generates reaction specs.
		// A more robust code will read the reactions in the source file and replace the ones created by the default by the VirtualCell framework.
		ReactionSpec reactionSpecs[] = new ReactionSpec[children.size()];
		for (int i=0;i<children.size();i++){
			reactionSpecs[i] = getReactionSpec((Element)children.get(i));
		}
		try {
			newsimcontext.getReactionContext().setReactionSpecs(reactionSpecs);
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException("A PropertyVetoException occurred while setting the ReactionSpecs to the SimContext " + name+" : "+e.getMessage());
		}
	}
	// Retrieve SpeciesContextSpecs
	children = tempelement.getChildren(XMLTags.SpeciesContextSpecTag);
	SpeciesContextSpec specContSpecs[] = new SpeciesContextSpec[children.size()];
	for (int i=0;i<children.size();i++){
		specContSpecs[i] = getSpeciesContextSpec( (Element)children.get(i) );
	}
	try {
		newsimcontext.getReactionContext().setSpeciesContextSpecs( specContSpecs );
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException occurred when setting the SpeciesContextSpecs of the SimContext " + name+" : "+e.getMessage());
	}
	//Retrieve Electrical context
	org.jdom.Element electElem = param.getChild(XMLTags.ElectricalContextTag);
	
	//this information is optional!
	if (electElem != null) {
		if (electElem.getChild(XMLTags.ClampTag)!=null) {
			//read clamp
			ElectricalStimulus[] electArray = new ElectricalStimulus[1];
			electArray[0] = getElectricalStimulus(electElem.getChild(XMLTags.ClampTag), newsimcontext);
			
			try {
				newsimcontext.setElectricalStimuli(electArray);
			} catch (java.beans.PropertyVetoException e) {
				e.printStackTrace();
				throw new XmlParseException(e.getMessage());
			}
		}
		
		//read ground electrode
		if (electElem.getChild(XMLTags.ElectrodeTag)!=null) {
			Electrode groundElectrode = getElectrode(electElem.getChild(XMLTags.ElectrodeTag), newsimcontext);
			
			try{
				newsimcontext.setGroundElectrode(groundElectrode);
			} catch (java.beans.PropertyVetoException e) {
				e.printStackTrace();
				throw new XmlParseException(e.getMessage());
			}
		}
	}	

	org.jdom.Element analysisTaskListElement = param.getChild(XMLTags.AnalysisTaskListTag);
	if (analysisTaskListElement!=null){
		children = analysisTaskListElement.getChildren(); // XMLTags.ParameterEstimationTaskTag);
		if (children.size()!=0) {
			Vector analysisTaskList = new Vector();
			for (int i = 0; i < children.size(); i++){
				Element analysisTaskElement = (Element)children.get(i);
				try {
					IAnalysisTaskFactory[] analysisTaskFactories = Activator.getDefault().getAnalysisTaskFactories(analysisTaskElement.getName());
					if (analysisTaskFactories!=null){
						if (analysisTaskFactories.length > 1){
							System.out.println("XmlReader.getSimulationContext(): multiple plugins availlable for analysis task type "+analysisTaskElement.getName()+", taking first one");
						}
						IAnalysisTask analysisTask = analysisTaskFactories[0].create(analysisTaskElement, newsimcontext);
						analysisTaskList.add(analysisTask);
					}
				}catch (Exception e){
					e.printStackTrace(System.out);
					throw new XmlParseException("An Exception occurred when parsing AnalysisTasks of SimContext " + name+" : "+e.getMessage());
				}
			}
			try {
				IAnalysisTask[] analysisTasks = (IAnalysisTask[])BeanUtils.getArray(analysisTaskList,IAnalysisTask.class);
				newsimcontext.setAnalysisTasks(analysisTasks);
			} catch (java.beans.PropertyVetoException e) {
				e.printStackTrace(System.out);
				throw new XmlParseException("A PropertyVetoException occurred when setting the AnalysisTasks of the SimContext " + name+" : "+e.getMessage());
			}
		}
	}

	//Retrieve Simulations and gave it to the BioModel object
	//****** All thsi was moved to the getSimulationContext. Because the Simcontext needs to be added to
	//****** the Biomodel before the Simulations.
	/*
	iterator = param.getChildren(XMLTags.SimulationTag).iterator();
	while (iterator.hasNext()) {
		try {
			biomodel.addSimulation( getSimulation((Element)iterator.next(), newmathdesc) );
			;
		} catch(java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException("A PropertyVetoException occurred when adding a Simulation entity to the BioModel in the SimContext " + name+" : "+e.getMessage());
		}
	}*/
	
	return newsimcontext;
}


/**
 * This method returns a Version object from an XML representation.
 * Creation date: (3/16/2001 3:41:24 PM)
 * @return cbit.sql.Version
 * @param param org.jdom.Element
 */
public SimulationVersion getSimulationVersion(Element xmlVersion) throws XmlParseException {
	if (xmlVersion == null) {
		return null;
	}
	
	//determine if it should be processed using the 'fromVersionable'
	if (xmlVersion.getAttributeValue(XMLTags.FromVersionableTag)==null || Boolean.valueOf(xmlVersion.getAttributeValue(XMLTags.FromVersionableTag)).booleanValue() || this.readKeysFlag == false) {
		//this came from a versionable object, so skip! Or it should not explicitly import the information inside the Version 
		return null;		
	}
	
	//Read all the attributes
	//*name
	String name = this.unMangle(xmlVersion.getAttributeValue(XMLTags.NameAttrTag));
	//*key
	String temp = xmlVersion.getAttributeValue(XMLTags.KeyValueAttrTag);
	KeyValue key = new KeyValue( temp );
	//*owner
	Element tempElement = xmlVersion.getChild(XMLTags.OwnerTag);
	User owner = new User(this.unMangle(tempElement.getAttributeValue(XMLTags.NameAttrTag)), new KeyValue(tempElement.getAttributeValue(XMLTags.IdentifierAttrTag)));	
	//*access
	GroupAccess groupAccess = getGroupAccess(xmlVersion.getChild(XMLTags.GroupAccessTag));
	//*Branchpointref
	temp = xmlVersion.getAttributeValue(XMLTags.BranchPointRefTag);
	KeyValue branchpointref = null;
	
	if (temp!=null) {
		branchpointref = new KeyValue(temp);
	}
	
	//*BranchID
	java.math.BigDecimal branchId = new java.math.BigDecimal(xmlVersion.getAttributeValue(XMLTags.BranchIdAttrTag));	
	//*Flag
	temp = xmlVersion.getAttributeValue(XMLTags.FlagAttrTag);
	VersionFlag flag = VersionFlag.fromInt(Integer.parseInt(temp));
	//*Date
	java.util.Date date = null;	
	temp = xmlVersion.getAttributeValue(XMLTags.DateAttrTag);
	
	if ( temp != null ) {
		java.text.SimpleDateFormat newDateFormatter = new java.text.SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", java.util.Locale.US);
		
		try {
			date = newDateFormatter.parse(temp);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			throw new XmlParseException("Invalid date:"+temp+" : "+e.getMessage());
		}
	}
	
	//*DataSimulationRef
	KeyValue parentSimRefKey = null;
	tempElement = xmlVersion.getChild(XMLTags.ParentSimRefTag);
	if (tempElement!=null){
		parentSimRefKey = new KeyValue(tempElement.getAttributeValue(XMLTags.KeyValueAttrTag));
	}

	//*Annotation
	String annotation = null;
	String annotationText = xmlVersion.getChildText(XMLTags.AnnotationTag);
	if (annotationText!= null && annotationText.length()>0) {
		annotation = this.unMangle(annotationText);
	}

	//Create and return the version object
	return new SimulationVersion(key, name, owner, groupAccess, branchpointref, branchId, date, flag, annotation, parentSimRefKey);
}


/**
 * This method returns a SolverTaskDescription Object from a XML Element.
 * Creation date: (5/22/2001 10:51:23 AM)
 * @return cbit.vcell.solver.SolverTaskDescription
 * @param param org.jdom.Element
 * @param simulation cbit.vcell.solver.Simulation
 */
public cbit.vcell.solver.SolverTaskDescription getSolverTaskDescription(Element param, cbit.vcell.solver.Simulation simulation) throws XmlParseException{
	//*** create new SolverTaskDescription ***
	cbit.vcell.solver.SolverTaskDescription solverTaskDesc = new cbit.vcell.solver.SolverTaskDescription(simulation);
	
	//Retrieve attributes
	String taskType = param.getAttributeValue(XMLTags.TaskTypeTag);
	int keepEvery = -1;
	int keepAtMost = -1;
	if (param.getAttributeValue(XMLTags.KeepEveryTag) != null) {
		keepEvery = Integer.parseInt( param.getAttributeValue(XMLTags.KeepEveryTag) );
		keepAtMost= Integer.parseInt( param.getAttributeValue(XMLTags.KeepAtMostTag) );
	}
	boolean useSymJacob = new Boolean(param.getAttributeValue(XMLTags.UseSymbolicJacobianAttrTag)).booleanValue();
	String solverName = param.getAttributeValue(XMLTags.SolverNameTag);
	//get sentivity parameter
	Element sensparamElement = param.getChild(XMLTags.ConstantTag);
	Constant sensitivityparam = null;
	
	if (sensparamElement!=null) {
		sensitivityparam = getConstant(sensparamElement);
	}

	//set Attributes
	try {
		//set solver
		solverTaskDesc.setSolverDescription(cbit.vcell.solver.SolverDescription.fromName(solverName));
		
		if ( taskType.equalsIgnoreCase(XMLTags.UnsteadyTag) ) {
			solverTaskDesc.setTaskType(solverTaskDesc.TASK_UNSTEADY );
		} else if (taskType.equalsIgnoreCase( XMLTags.SteadyTag)) {
			solverTaskDesc.setTaskType(solverTaskDesc.TASK_STEADY );
		} else {
			throw new XmlParseException("Unexpected task type: " + taskType);
		}
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException was fired when setting the taskType: " + taskType + ": "+e.getMessage());
	}

	try {
		solverTaskDesc.setUseSymbolicJacobian(useSymJacob);	
		//get TimeBound
		solverTaskDesc.setTimeBounds( getTimeBounds(param.getChild(XMLTags.TimeBoundTag)) );
		//get TimeStep
		solverTaskDesc.setTimeStep( getTimeStep(param.getChild(XMLTags.TimeStepTag)) );
		//get ErrorTolerance
		solverTaskDesc.setErrorTolerance( getErrorTolerance(param.getChild(XMLTags.ErrorToleranceTag)) );
		//get OutputOptions
		if (keepEvery != -1) {
			solverTaskDesc.setOutputTimeSpec(new cbit.vcell.solver.DefaultOutputTimeSpec(keepEvery,keepAtMost));
		}
		cbit.vcell.solver.OutputTimeSpec ots = getOutputTimeSpec(param.getChild(XMLTags.OutputOptionsTag));
		if (ots != null) {
			solverTaskDesc.setOutputTimeSpec(getOutputTimeSpec(param.getChild(XMLTags.OutputOptionsTag)));
		}
		//set SensitivityParameter
		solverTaskDesc.setSensitivityParameter(sensitivityparam);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(e.getMessage());
	}
		
	return solverTaskDesc;
}


/**
 * This method creates a Specie (Compound) object from an XML Element.
 * Creation date: (3/15/2001 12:57:43 PM)
 * @return cbit.vcell.model.Species
 * @param param org.jdom.Element
 */
public cbit.vcell.model.Species getSpecies(Element param) throws XmlParseException {
	//get its data
	String specieName = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String specieAnnotation = null;

	//the Annotation paramater can be optional
	//String temp = param.getAttributeValue(XMLTags.AnnotationAttrTag);
	//if (temp!=null && temp.length()!=0) {
		//specieAnnotation = this.unMangle(temp);
	//}
	String annotationText = param.getChildText(XMLTags.AnnotationTag);
	if (annotationText!=null && annotationText.length()>0) {
		specieAnnotation = this.unMangle(annotationText);
	}

	//create new Specie
	cbit.vcell.model.Species newspecie = new cbit.vcell.model.Species( specieName, specieAnnotation);

	//Try to read the DBSpecie data
	Element dbspecieElement = param.getChild(XMLTags.DBSpeciesTag);
	
	if (dbspecieElement!=null && this.readKeysFlag) {
		//read the data
		try {
			newspecie.setDBSpecies(getDBSpecies(dbspecieElement));
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}

	//***Add the Species to the dictionnary ****
	String hashname = newspecie.getClass().getName() + ":" + specieName;
	this.dictionary.put(param, hashname, newspecie);

	return newspecie;
}


/**
 * This method returns a Speciecontext object from a XML Element.
 * Creation date: (4/16/2001 6:32:23 PM)
 * @return cbit.vcell.model.SpeciesContext
 * @param param org.jdom.Element
 */
public SpeciesContext getSpeciesContext(Element param) throws XmlParseException{
	//retrieve its information
	String name = this.unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	String hasOverrideString = param.getAttributeValue(XMLTags.HasOverrideAttrTag);
	boolean bHasOverride = true; // default (safety) is name has been overridden
	if (hasOverrideString!=null && hasOverrideString.length()>0){
		bHasOverride = Boolean.valueOf(hasOverrideString).booleanValue();
	}
	String tempname = "cbit.vcell.model.Species:" + this.unMangle(param.getAttributeValue(XMLTags.SpeciesRefAttrTag));
	Element re = XMLDict.getResolvedElement(param, XMLTags.SpeciesTag, XMLTags.NameAttrTag, 
											this.unMangle(param.getAttributeValue(XMLTags.SpeciesRefAttrTag)));
	Species specieref= (Species)this.dictionary.get(re, tempname);
	
	if (specieref == null) {
		throw new XmlParseException("The Species " + tempname + "could not be retrieved from the dictionnary!");
	}
	//Try to get the structure as a Feature
	re = XMLDict.getResolvedElement(param, XMLTags.FeatureTag, XMLTags.NameAttrTag, 
									this.unMangle(param.getAttributeValue(XMLTags.StructureAttrTag)));
	tempname = "cbit.vcell.model.Feature:" + this.unMangle(param.getAttributeValue(XMLTags.StructureAttrTag));
	Structure structureref = (Structure)this.dictionary.get(re, tempname);
	
	if (structureref == null) {
		//Try with Membranes
		re = XMLDict.getResolvedElement(param, XMLTags.MembraneTag, XMLTags.NameAttrTag, 
									this.unMangle(param.getAttributeValue(XMLTags.StructureAttrTag)));
		tempname = "cbit.vcell.model.Membrane:" + this.unMangle(param.getAttributeValue(XMLTags.StructureAttrTag));
		structureref = (Structure)this.dictionary.get(re, tempname);
	}/*else*/
	if (structureref == null) {
		//the structure coul not be retrieved, so throw an exception!
		throw new XmlParseException("The Structure " + tempname + "could not be retrieved from the dictionnary!");
	}

	//Try to read KeyValue data
	String keystring = param.getAttributeValue(XMLTags.KeyValueAttrTag);
	KeyValue key = null;

	if (keystring!=null && keystring.length()>0 && this.readKeysFlag) {
		key = new KeyValue(keystring);
	}

	//---try to create the speciesContext---
	SpeciesContext speciecontext = null;
	speciecontext = new SpeciesContext( key, name, specieref, structureref, bHasOverride);
	//*** Add this speciesContext to the dictionnary ***
	tempname = speciecontext.getClass().getName() + ":"+ name;
	this.dictionary.put(param, tempname,speciecontext);
	
	return speciecontext;
}


/**
 * This method returns a SpeciesContextSpec object from a XML representation.
 * Creation date: (4/26/2001 4:14:01 PM)
 * @return cbit.vcell.mapping.SpeciesContextSpec
 * @param param org.jdom.Element
 */
public SpeciesContextSpec getSpeciesContextSpec(Element param) throws XmlParseException{
	SpeciesContextSpec specspec = null;
	//Get Atributes
	String speccontname = this.unMangle( param.getAttributeValue(XMLTags.SpeciesContextRefAttrTag) );
	boolean constant = Boolean.valueOf(param.getAttributeValue(XMLTags.ForceConstantAttrTag)).booleanValue();
	boolean enabledif = Boolean.valueOf(param.getAttributeValue(XMLTags.EnableDiffusionAttrTag)).booleanValue();
	
	//Retrieve reference
	Element re = XMLDict.getResolvedElement(param, XMLTags.SpeciesContextTag, XMLTags.NameAttrTag, speccontname);
	String temp = "cbit.vcell.model.SpeciesContext:"+speccontname;
	SpeciesContext specref = (SpeciesContext)this.dictionary.get(re, temp);
	if (specref == null) {
		throw new XmlParseException("The SpeciesContext " + temp +" refrence could not be resolved in the dictionnary!");
	}
	
	//*** Create new SpeciesContextSpec ****
	specspec = new SpeciesContextSpec(specref, null);     //refreshDependencies() will re-connect SimulationContext
	//set attributes
	specspec.setConstant( constant);
	try {
		specspec.setEnableDiffusing( enabledif );
	} catch (MappingException e) {
		e.printStackTrace();
		throw new XmlParseException("error setting the 'enableDiffusing' property of a SpeciesContext: "+e.getMessage());
	}
	//set expressions
	//Initial
	temp = param.getChildText(XMLTags.InitialTag);
	try {
		Expression expression = unMangleExpression(temp);
		specspec.getInitialConditionParameter().setExpression(expression);
	} catch (ExpressionException e) {
		e.printStackTrace();
		throw new XmlParseException("An expressionException was fired when setting the InitilaconditionExpression "+ temp + ", for a SpeciesContextSpec!"+" : "+e.getMessage());
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(e.getMessage());
	}
	//diffusion (if there is no diffusion information skip it)
	Element xmlDiffusionElement = param.getChild(XMLTags.DiffusionTag);
	if (xmlDiffusionElement != null) {
		temp = xmlDiffusionElement.getText();
		try {
			Expression expression = unMangleExpression(temp);
			specspec.getDiffusionParameter().setExpression(expression);
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("An ExpressionException was fired when setting the diffusionExpression " + temp + " to a SpeciesContextSpec!"+" : "+e.getMessage());
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}
	
	//Get Boundaries if any
	Element tempElement = param.getChild(XMLTags.BoundariesTag);
	if (tempElement != null) {
		cbit.vcell.parser.SymbolTable simbolTable= new ReservedSymbolTable(true);
		try {
			//Xm
			temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueXm);
			if (temp != null) {
				specspec.getBoundaryXmParameter().setExpression(new Expression(this.unMangle(temp)));
			}
			//Xp
			temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueXp);
			if (temp != null) {
				specspec.getBoundaryXpParameter().setExpression(new Expression(this.unMangle(temp)));
			}
			//Ym
			temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueYm);
			if (temp != null) {
				specspec.getBoundaryYmParameter().setExpression(new Expression(this.unMangle(temp)));
			}
			//Yp
			temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueYp);
			if (temp != null) {
				specspec.getBoundaryYpParameter().setExpression(new Expression(this.unMangle(temp)));
			}
			//Zm
			temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueZm);
			if (temp != null) {
				specspec.getBoundaryZmParameter().setExpression(new Expression(this.unMangle(temp)));
			}
			//Zp
			temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueZp);
			if (temp != null) {
				specspec.getBoundaryZpParameter().setExpression(new Expression(this.unMangle(temp)));
			}
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("An ExpressionException was fired when Setting the boundary Expression: " + this.unMangle(temp)+" : "+e.getMessage());
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}
	
	return specspec;
}


/**
 * This method returns a SubVolume element type from a XML representation.
 * Creation date: (4/26/2001 4:14:01 PM)
 * @return SubVolume
 * @param param org.jdom.Element
 */
public SubVolume getSubVolume(Element param) throws XmlParseException{
	String typeString = param.getAttributeValue(XMLTags.TypeAttrTag);
	SubVolume newsubvolume = null;
	
	if (typeString!=null) {
		//process the subvolume upon the 'type'
		if ( typeString.equalsIgnoreCase(XMLTags.CompartmentBasedTypeTag) ) {
			//Process compartmental based		
			newsubvolume = getCompartmentSubVolume( param );
		} else if ( typeString.equalsIgnoreCase(XMLTags.AnalyticBasedTypeTag) ) {
			//Process Analytic based
			newsubvolume = getAnalyticSubVolume( param );
		} else if ( typeString.equalsIgnoreCase(XMLTags.ImageBasedTypeTag) ) {
			//Process Image based
			newsubvolume = getImageSubVolume( param );
		} else {
			//Throw an exception
			throw new XmlParseException("Parse Error! Unknown Subvolume type:"+typeString);
		}

		//*******Add subVolume to the dictionnary ************
		this.dictionary.put(param, "cbit.vcell.geometry.SubVolume:"+newsubvolume.getName(), newsubvolume);
		//*****************************************************
	} else {
		System.out.println("Invalid VCML format! Error in <Subvolume name=\""+ param.getAttributeValue(XMLTags.NameAttrTag)+"\"/>");
		System.out.println("Valid format is:");
		System.out.println("<SubVolume Name=\"??\" Handle=\"?\" ImagePixelValue=\"?\"/>");
		throw new XmlParseException("Invalid VCML syntax in <Subvolume name=\""+ param.getAttributeValue(XMLTags.NameAttrTag)+"\"/>");
	}
	
	return newsubvolume;
}


/**
 * This method returns a TimeBounds object from a XML Element.
 * Creation date: (5/22/2001 11:41:04 AM)
 * @return cbit.vcell.solver.TimeBounds
 * @param param org.jdom.Element
 */
public cbit.vcell.solver.TimeBounds getTimeBounds(Element param) {
	//get Attributes
	double start = Double.parseDouble( param.getAttributeValue(XMLTags.StartTimeAttrTag) );
	double end = Double.parseDouble( param.getAttributeValue(XMLTags.EndTimeAttrTag) );

	//*** create new TimeBounds object ****
	cbit.vcell.solver.TimeBounds timeBounds = new cbit.vcell.solver.TimeBounds(start, end);
	
	return timeBounds;
}


/**
 * This method returns a TimeStep object from a XML Element.
 * Creation date: (5/22/2001 11:45:33 AM)
 * @return cbit.vcell.solver.TimeStep
 * @param param org.jdom.Element
 */
public cbit.vcell.solver.TimeStep getTimeStep(Element param) {
	//get attributes
	double min = Double.parseDouble( param.getAttributeValue(XMLTags.MinTimeAttrTag) );
	double def = Double.parseDouble( param.getAttributeValue(XMLTags.DefaultTimeAttrTag) );
	double max = Double.parseDouble( param.getAttributeValue(XMLTags.MaxTimeAttrTag) );

	//**** create new TimeStep object ****
	cbit.vcell.solver.TimeStep timeStep = new cbit.vcell.solver.TimeStep(min, def, max);
	
	return timeStep;
}


/**
 * This methos returns a User object from a XML Element.
 * Creation date: (3/16/2001 3:52:30 PM)
 * @return cbit.vcell.server.User
 * @param param org.jdom.Element
 */
public User getUser(Element param) {
	User newuser =
		new User(
			param.getAttributeValue( XMLTags.NameAttrTag),
			new KeyValue(param.getAttributeValue(XMLTags.IdentifierAttrTag)) );

	return newuser;
}


/**
 * This method returns a Kinetics object from a XML Element based on the value of the kinetics type attribute.
 * Creation date: (3/19/2001 4:42:04 PM)
 * @return cbit.vcell.model.Kinetics
 * @param param org.jdom.Element
 */
private VariableHash getVariablesHash() throws XmlParseException {

	VariableHash varHash = new VariableHash();
	//
	// add constants that may be used in kinetics.
	//
	try {
		varHash.addVariable(new Constant(ReservedSymbol.FARADAY_CONSTANT.getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(ReservedSymbol.FARADAY_CONSTANT_NMOLE.getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(ReservedSymbol.GAS_CONSTANT.getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(ReservedSymbol.KMILLIVOLTS.getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(ReservedSymbol.KMOLE.getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(ReservedSymbol.N_PMOLE.getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(ReservedSymbol.TEMPERATURE.getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(ReservedSymbol.K_GHK.getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(ReservedSymbol.TIME.getName(), new Expression(0.0)));
	} catch (MappingException e){
		e.printStackTrace(System.out);
		throw new XmlParseException("error reordering parameters according to dependencies: "+e.getMessage());
	}

	return varHash;
}


/**
 * This method returns a VCIMage object from a XML representation.
 * Creation date: (3/16/2001 3:41:24 PM)
 * @return VCImage
 * @param param org.jdom.Element
 */
public VCImage getVCImage(Element param, Extent extent) throws XmlParseException{
	//try to get metadata(version)
	Version version = getVersion(param.getChild(XMLTags.VersionTag));
	
	//get the attributes
	Element tempelement= param.getChild(XMLTags.ImageDataTag);
	int aNumX = Integer.parseInt( tempelement.getAttributeValue(XMLTags.XAttrTag) );
	int aNumY = Integer.parseInt( tempelement.getAttributeValue(XMLTags.YAttrTag) );
	int aNumZ = Integer.parseInt( tempelement.getAttributeValue(XMLTags.ZAttrTag) );

	//getpixels
	String temp = tempelement.getText();
	byte[] data = cbit.util.Hex.toBytes(temp); //decode

	//create the VCImage object
	VCImageCompressed newimage = null;	
	try { 
		newimage = new cbit.image.VCImageCompressed( version, data, extent, aNumX, aNumY, aNumZ);
	} catch(ImageException e) {
		e.printStackTrace();
		throw new XmlParseException("An imageException occurred while trying to create a VCImage!"+" : "+e.getMessage());
	}
	//set attributes
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	try {
		newimage.setName(name);
		//String annotation = param.getAttributeValue(XMLTags.AnnotationAttrTag);
		//if (annotation!=null) {
			//newimage.setDescription(this.unMangle(annotation));
		//}

		//read the annotation
		String annotation = param.getChildText(XMLTags.AnnotationTag);
		if (annotation!=null && annotation.length()>0) {
			newimage.setDescription(this.unMangle(annotation));
		}

	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException(e.getMessage());
	}

	//get PixelClasses
	List pixelClassList = param.getChildren(XMLTags.PixelClassTag);

	if (pixelClassList.size()!=0) {
		VCPixelClass[] pixelClassArray = new VCPixelClass[pixelClassList.size()];
		
		for (int i = 0; i < pixelClassList.size(); i++){
			pixelClassArray[i] = getPixelClass((Element)pixelClassList.get(i));
		}
		try {
			newimage.setPixelClasses(pixelClassArray);
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new XmlParseException(e.getMessage());
		}
	} else {//Invalid format
		System.out.println("Format Error! No <PixelClass> references found inside <Image name=\""+name+"\">!");
		System.out.println("Valid format for images is:");
		System.out.println("<Image Name=\"??\">");
		System.out.println(" <ImageData Z=\"?\" Y=\"?\" Z=\"?\" CompressedSize=\"?\">");
		System.out.println("     ......Image content...");
		System.out.println(" </ImageData>");
		System.out.println(" <PixelClass Name=\"?\" ImagePixelValue=\"?\"/>");
		System.out.println("  ...");
		System.out.println(" <PixelClass Name=\"?\" ImagePixelValue=\"?\"/>");
		System.out.println(" <Version Name=\"?\"/>");		
		System.out.println("</Image>");
		throw new XmlParseException("Invalid VCML format error!\nNo <PixelClass> references found inside <Image name=\""+name+"\">!");
	}
		
	return newimage;
}


/**
 * This method returns a Version object from an XML representation.
 * Creation date: (3/16/2001 3:41:24 PM)
 * @return cbit.sql.Version
 * @param param org.jdom.Element
 */
public Version getVersion(Element xmlVersion) throws XmlParseException {
	if (xmlVersion == null) {
		return null;
	}
	
	//determine if it should be processed using the 'fromVersionable'
	if (xmlVersion.getAttributeValue(XMLTags.FromVersionableTag)==null || Boolean.valueOf(xmlVersion.getAttributeValue(XMLTags.FromVersionableTag)).booleanValue() || this.readKeysFlag == false) {
		//this came from a versionable object, so skip! Or it should not explicitly import the information inside the Version 
		return null;		
	}
	
	//Read all the attributes
	//*name
	String name = this.unMangle(xmlVersion.getAttributeValue(XMLTags.NameAttrTag));
	//*key
	String temp = xmlVersion.getAttributeValue(XMLTags.KeyValueAttrTag);
	KeyValue key = new KeyValue( temp );
	//*owner
	Element tempElement = xmlVersion.getChild(XMLTags.OwnerTag);
	User owner = new User(this.unMangle(tempElement.getAttributeValue(XMLTags.NameAttrTag)), new KeyValue(tempElement.getAttributeValue(XMLTags.IdentifierAttrTag)));	
	//*access
	GroupAccess groupAccess = getGroupAccess(xmlVersion.getChild(XMLTags.GroupAccessTag));
	//*Branchpointref
	temp = xmlVersion.getAttributeValue(XMLTags.BranchPointRefTag);
	KeyValue branchpointref = null;
	
	if (temp!=null) {
		branchpointref = new KeyValue(temp);
	}
	
	//*BranchID
	java.math.BigDecimal branchId = new java.math.BigDecimal(xmlVersion.getAttributeValue(XMLTags.BranchIdAttrTag));	
	//*Flag
	temp = xmlVersion.getAttributeValue(XMLTags.FlagAttrTag);
	VersionFlag flag = VersionFlag.fromInt(Integer.parseInt(temp));
	//*Date
	java.util.Date date = null;	
	temp = xmlVersion.getAttributeValue(XMLTags.DateAttrTag);
	
	if ( temp != null ) {
		java.text.SimpleDateFormat newDateFormatter = new java.text.SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", java.util.Locale.US);
		
		try {
			date = newDateFormatter.parse(temp);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			throw new XmlParseException("Invalid date:"+temp+" : "+e.getMessage());
		}
	}
	
	//*Annotation
	String annotation = null;
	String annotationText = xmlVersion.getChildText(XMLTags.AnnotationTag);
	if (annotationText!= null && annotationText.length()>0) {
		annotation = this.unMangle(annotationText);
	}

	//Create and return the version object
	return new Version(key, name, owner, groupAccess, branchpointref, branchId, date, flag, annotation);
}


/**
 * This method returns a VolumeRegionEquation from a XML Element.
 * Creation date: (5/17/2001 3:52:40 PM)
 * @return cbit.vcell.math.VolumeRegionEquation
 * @param param org.jdom.Element
 * @exception cbit.util.xml.XmlParseException The exception description.
 */
public VolumeRegionEquation getVolumeRegionEquation(Element param) throws XmlParseException {
	//get attributes
	String varname = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	//find reference in the dictionnary
	//try a VolumeRegionVariable
	Element re = XMLDict.getResolvedElement(param, XMLTags.VolumeRegionVariableTag, XMLTags.NameAttrTag, varname);
	String temp = "cbit.vcell.math.VolumeRegionVariable:"+ varname;
	VolumeRegionVariable varref = (VolumeRegionVariable)this.dictionary.get(re, temp);
	if (temp == null) {
		throw new XmlParseException("The reference to the VolumeRegion variable "+ varname+ " could not be resolved in the dictionnary!");
	}

	//get Initial condition
	temp = param.getChildText(XMLTags.InitialTag);
	Expression exp = unMangleExpression( temp );
	// ** Create the Equation **
	VolumeRegionEquation volRegEq = new VolumeRegionEquation(varref,exp);

	//set the Uniform Rate
	temp = param.getChildText(XMLTags.UniformRateTag);
	exp = unMangleExpression( temp );
	volRegEq.setUniformRateExpression(exp);

	//Set the Volume Rate
	temp = param.getChildText(XMLTags.VolumeRateTag);
	exp = unMangleExpression( temp );
	volRegEq.setVolumeRateExpression(exp);

	//set the Membrane Rate
	temp = param.getChildText(XMLTags.MembraneRateTag);
	exp = unMangleExpression( temp );
	volRegEq.setMembraneRateExpression(exp);
	
	//get ExactSolution (if any)
/*	temp = param.getChildText(XMLTags.ExactTag);
	if (temp !=null) {
		try {
			Expression expression = new Expression( this.unMangle( temp) );
			odeEquation.setExactSolution( expression);			
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("An ExpressionException was fired when creating the expression: "+ this.unMangle(temp)+" : "+e.getMessage());
		}
	}
	//get ConstructedSolution (if any)
	temp = param.getChildText(XMLTags.ConstructedTag);
	if (temp != null) {
		try {
			Expression expression = new Expression(this.unMangle(temp));
			odeEquation.setConstructedSolution( expression );
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("An ExpressionException was fired when creating the expression: "+ this.unMangle(temp) +" : "+e.getMessage());
		}
	}*/
	
 	return volRegEq;	
}


/**
 * This method returns a VolumeRegionVariable object from a XML Element.
 * Creation date: (5/16/2001 2:56:34 PM)
 * @return cbit.vcell.math.VolumeRegionVariable
 * @param param org.jdom.Element
 */
public VolumeRegionVariable getVolumeRegionVariable(Element param) {
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

	//-- create new VolumeRegionVariable object
	VolumeRegionVariable volRegVariable = new VolumeRegionVariable( name );

	//***add it to the dictionnary ***
	String temp = volRegVariable.getClass().getName()+":"+name;
	this.dictionary.put(param, temp, volRegVariable);

	return volRegVariable;
}


/**
 * This method returns a VolVariable object from a XML Element.
 * Creation date: (5/16/2001 2:56:34 PM)
 * @return cbit.vcell.math.VolVariable
 * @param param org.jdom.Element
 */
public VolVariable getVolVariable(Element param) {
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

	//-- create new VolVariable object
	VolVariable volVariable = new VolVariable( name );

	//***add it to the dictionnary ***
	String temp = volVariable.getClass().getName()+":"+name;
	this.dictionary.put(param, temp, volVariable);

	return volVariable;
}
}