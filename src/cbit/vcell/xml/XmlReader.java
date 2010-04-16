package cbit.vcell.xml;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.Namespace;
import org.vcell.util.BeanUtils;
import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.Hex;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.GroupAccessAll;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.GroupAccessSome;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageCompressed;
import cbit.image.VCPixelClass;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.xml.XMLMetaData;
import cbit.vcell.biomodel.meta.xml.XMLMetaDataReader;
import cbit.vcell.dictionary.BoundCompound;
import cbit.vcell.dictionary.BoundEnzyme;
import cbit.vcell.dictionary.BoundProtein;
import cbit.vcell.dictionary.CompoundInfo;
import cbit.vcell.dictionary.DBFormalSpecies;
import cbit.vcell.dictionary.DBSpecies;
import cbit.vcell.dictionary.EnzymeInfo;
import cbit.vcell.dictionary.EnzymeRef;
import cbit.vcell.dictionary.FormalCompound;
import cbit.vcell.dictionary.FormalEnzyme;
import cbit.vcell.dictionary.FormalProtein;
import cbit.vcell.dictionary.FormalSpeciesInfo;
import cbit.vcell.dictionary.ProteinInfo;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.CompartmentSubVolume;
import cbit.vcell.geometry.ControlPointCurve;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.Line;
import cbit.vcell.geometry.SampledCurve;
import cbit.vcell.geometry.Spline;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.CurrentDensityClampStimulus;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.Electrode;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.TotalCurrentClampStimulus;
import cbit.vcell.mapping.VariableHash;
import cbit.vcell.mapping.VoltageClampStimulus;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.math.Action;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Distribution;
import cbit.vcell.math.Event;
import cbit.vcell.math.FastInvariant;
import cbit.vcell.math.FastRate;
import cbit.vcell.math.FastSystem;
import cbit.vcell.math.FilamentRegionVariable;
import cbit.vcell.math.FilamentSubDomain;
import cbit.vcell.math.FilamentVariable;
import cbit.vcell.math.Function;
import cbit.vcell.math.GaussianDistribution;
import cbit.vcell.math.InsideVariable;
import cbit.vcell.math.JumpCondition;
import cbit.vcell.math.JumpProcess;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathFormatException;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneRandomVariable;
import cbit.vcell.math.MembraneRegionEquation;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.OutputFunctionContext;
import cbit.vcell.math.OutsideVariable;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.RandomVariable;
import cbit.vcell.math.StochVolVariable;
import cbit.vcell.math.UniformDistribution;
import cbit.vcell.math.VarIniCondition;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.VolumeRandomVariable;
import cbit.vcell.math.VolumeRegionEquation;
import cbit.vcell.math.VolumeRegionVariable;
import cbit.vcell.math.AnnotatedFunction.FunctionCategory;
import cbit.vcell.math.Event.Delay;
import cbit.vcell.math.Event.EventAssignment;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Diagram;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.GHKKinetics;
import cbit.vcell.model.GeneralCurrentKinetics;
import cbit.vcell.model.GeneralCurrentLumpedKinetics;
import cbit.vcell.model.GeneralKinetics;
import cbit.vcell.model.GeneralLumpedKinetics;
import cbit.vcell.model.HMM_IRRKinetics;
import cbit.vcell.model.HMM_REVKinetics;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.NernstKinetics;
import cbit.vcell.model.NodeReference;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.VCMODL;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.modelopt.AnalysisTask;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.ParameterEstimationTaskXMLPersistence;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.ConstantArraySpec;
import cbit.vcell.solver.DataProcessingInstructions;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.stoch.StochHybridOptions;
import cbit.vcell.solver.stoch.StochSimOptions;
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
    private Namespace vcNamespace = Namespace.getNamespace(XMLTags.VCML_NS_BLANK);	// default - blank namespace
    	
/**
 * This constructor takes a parameter to specify if the KeyValue should be ignored
 * Creation date: (3/13/2001 12:16:30 PM)
 */
public XmlReader(boolean readKeys) {
	super();
	this.readKeysFlag = readKeys;
}

public XmlReader(boolean readKeys, Namespace argNS) {
	super();
	this.readKeysFlag = readKeys;
	this.vcNamespace = argNS;
}

/**
 * This method returns a Action object from a XML element.
 * Creation date: (7/24/2006 5:56:36 PM)
 * @return cbit.vcell.math.Action
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public Action getAction(Element param, MathDescription md) throws XmlParseException, MathException, ExpressionException
{
	//retrieve values
	String operation = unMangle( param.getAttributeValue(XMLTags.OperationAttrTag) );
	Expression exp = unMangleExpression(param.getText());
	String name = unMangle( param.getAttributeValue(XMLTags.VarNameAttrTag) );
	
	Variable var = md.getVariable(name);
	if (var == null){
		throw new MathFormatException("variable "+name+" not defined");
	}	
	if (!(var instanceof StochVolVariable)){
		throw new MathFormatException("variable "+name+" not a Stochastic Volume Variable");
	}
	try {
		Action action = new Action(var,operation,exp);
		return action;
	} catch (Exception e){e.printStackTrace();}
	
	return null;
}

	private VolumeGeometricRegion getAdjacentVolumeRegion(ArrayList<GeometricRegion> regions, String regionName) {
		for (int i = 0; i < regions.size(); i++) {
			GeometricRegion rvl = regions.get(i);
			if (rvl instanceof VolumeGeometricRegion && rvl.getName().equals(regionName)) {
				return (VolumeGeometricRegion)rvl;
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
	temp = param.getChildText(XMLTags.AnalyticExpressionTag, vcNamespace);
	if (temp == null) {
		throw new XmlParseException("A Problem occured while retrieving the analytic expression of the AnalyticSubvolume "+ name);
	}
	Expression newexpression = unMangleExpression(temp);
	
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
public BioModel getBioModel(Element param) throws XmlParseException{
//long l1 = System.currentTimeMillis();
	//Get metadata information Version (if available)
	Version version = getVersion(param.getChild(XMLTags.VersionTag, vcNamespace));
		
	//Create new biomodel
	BioModel biomodel = new BioModel( version );
	
	//Set name
	String name = unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	try {
		biomodel.setName( name );
		//String annotation = param.getAttributeValue(XMLTags.AnnotationAttrTag);

		//if (annotation!=null) {
			//biomodel.setDescription(unMangle(annotation));
		//}
		//get annotation
		String annotationText = param.getChildText(XMLTags.AnnotationTag, vcNamespace);
		if (annotationText!=null && annotationText.length()>0) {
			biomodel.setDescription(unMangle(annotationText));
		}
	} catch(java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(e.getMessage());
	}
//long l2 = System.currentTimeMillis();
//System.out.println("biomodel-------- "+((double)(l2-l1))/1000);
	
	//***Add biomodel to the dictionnary***
	//dictionnary.put(simcontext.getClass().getName()+":"+simcontext.getName(), simcontext);
	//Set model
	Model newmodel = getModel(param.getChild(XMLTags.ModelTag, vcNamespace));
	biomodel.setModel( newmodel );
	//Set simulation contexts
	java.util.List<Element> children = param.getChildren(XMLTags.SimulationSpecTag, vcNamespace);
	java.util.Iterator<Element> iterator = children.iterator();
//long l3 = System.currentTimeMillis();
//System.out.println("model-------- "+((double)(l3-l2))/1000);
	while (iterator.hasNext()) {
//long l4 = System.currentTimeMillis();
		Element tempElement = iterator.next();
		SimulationContext simContext = getSimulationContext(tempElement, biomodel);
		try {
			biomodel.addSimulationContext( simContext );
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException("An error occurred while trying to add the SimContext "+ simContext.getName() +" to the BioModel Object!"+" : "+e.getMessage());
		}
		//process the simulations within this Simspec
		Iterator<Element> simIterator = tempElement.getChildren(XMLTags.SimulationTag, vcNamespace).iterator();
//long l5 = System.currentTimeMillis();
//System.out.println("simcontext-------- "+((double)(l5-l4))/1000);
		while (simIterator.hasNext()) {
			try {
				biomodel.addSimulation(getSimulation((Element)simIterator.next(), simContext.getMathDescription()));
			} catch(java.beans.PropertyVetoException e) {
				e.printStackTrace();
				throw new XmlParseException("A PropertyVetoException occurred when adding a Simulation entity to the BioModel " + name+" : "+e.getMessage());
			}
		}
//long l6 = System.currentTimeMillis();
//System.out.println("sims-------- "+((double)(l6-l5))/1000);		
	}
	
	//	biomodel.getVCMetaData().setAnnotation(biomodel, param);
	//	biomodel.getVCMetaData().setNotes(biomodel, param);
	boolean bMetaDataPopulated = false;
	List<Element> elementsMetaData = param.getChildren(XMLMetaData.VCMETADATA_TAG, VCMetaData.nsVCML);
	if (elementsMetaData != null && elementsMetaData.size() > 0) {
		for(Element elementMetaData : elementsMetaData) {
			XMLMetaDataReader.readFromElement(biomodel.getVCMetaData(), biomodel, elementMetaData);		
		}
		bMetaDataPopulated = true;
	} else {
		// no metadata was found, populate vcMetaData from biomodel (mainly free text annotation for identifiables)
		if (!bMetaDataPopulated) {
			biomodel.populateVCMetadata(bMetaDataPopulated);
		}
	}
	
	return biomodel;
}


/**
 * This method returns a Catalyst object from a XML representation.
 * Creation date: (5/4/2001 2:22:56 PM)
 * @return cbit.vcell.model.Product
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public Catalyst getCatalyst(Element param, ReactionStep reaction, Model model) throws XmlParseException {
    //retrieve the key if there is one
    KeyValue key = null;
    String keystring = param.getAttributeValue(XMLTags.KeyValueAttrTag);
    if (keystring != null && keystring.length()>0 && this.readKeysFlag) {
        key = new KeyValue(keystring);
    }

    String speccontref = unMangle(param.getAttributeValue(XMLTags.SpeciesContextRefAttrTag));
    SpeciesContext speccont = model.getSpeciesContext(speccontref);
    if (speccont == null) {
        throw new XmlParseException(
            "The reference to the SpecieContext " + speccontref
                + " for a Catalyst could not be resolved!");
    }

    return new Catalyst(key, reaction, speccont);
}


/**
 * This method returns a CompartmentSubDomain objecy from a XML element.
 * Creation date: (5/17/2001 11:59:45 AM)
 * @return cbit.vcell.math.CompartmentSubDomain
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public CompartmentSubDomain getCompartmentSubDomain(Element param, MathDescription mathDesc) throws XmlParseException {
	//get attributes
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	int priority = -1;
	String temp = param.getAttributeValue(XMLTags.PriorityAttrTag);
	if ( temp != null) {
		priority = Integer.parseInt( temp );
	}
	//--- create new CompartmentSubDomain ---
	CompartmentSubDomain subDomain = new CompartmentSubDomain(name, priority);

	//Process BoundaryConditions
	Iterator<Element> iterator = param.getChildren(XMLTags.BoundaryTypeTag, vcNamespace).iterator();
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
	iterator = param.getChildren( XMLTags.OdeEquationTag, vcNamespace ).iterator();
	while (iterator.hasNext()) {
		Element tempelement = (Element)iterator.next();

		try {
			subDomain.addEquation( getOdeEquation(tempelement, mathDesc) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding an OdeEquation to the compartmentSubDomain " + name+" : "+e.getMessage());
		}
	}

	//process PdeEquations
	iterator = param.getChildren( XMLTags.PdeEquationTag, vcNamespace ).iterator();
	while (iterator.hasNext()) {
		Element tempelement = (Element)iterator.next();

		try {
			subDomain.addEquation( getPdeEquation(tempelement, mathDesc) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding an PdeEquation to the compartmentSubDomain " + name+" : "+e.getMessage());
		}
	}

	//Process VolumeRegionEquation
	iterator = param.getChildren( XMLTags.VolumeRegionEquationTag, vcNamespace ).iterator();
	while (iterator.hasNext()) {
		Element tempelement = (Element)iterator.next();

		try {
			subDomain.addEquation( getVolumeRegionEquation(tempelement, mathDesc) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding a VolumeRegionEquation to the compartmentSubDomain " + name+" : "+e.getMessage());
		}
	}

	//Process Variable initial conditions (added for stochastic algos)
	iterator = param.getChildren( XMLTags.VarIniConditionTag, vcNamespace ).iterator();
	while (iterator.hasNext()) {
		Element tempelement = (Element)iterator.next();
		try {
			subDomain.addVarIniCondition( getVarIniCondition(tempelement, mathDesc) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding a variable initial condition to the compartmentSubDomain " + name+" : "+e.getMessage());
		} catch (ExpressionException e) {e.printStackTrace();}
	}
	
	//	
	//Process JumpProcesses (added for stochastic algos)
	iterator = param.getChildren( XMLTags.JumpProcessTag, vcNamespace ).iterator();
	while (iterator.hasNext()) {
		Element tempelement = (Element)iterator.next();
		try {
			subDomain.addJumpProcess( getJumpProcess(tempelement, mathDesc) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding a jump process to the compartmentSubDomain " + name+" : "+e.getMessage());
		} 
	}
	
	//Process the FastSystem (if thre is)
	Element tempelement = param.getChild(XMLTags.FastSystemTag, vcNamespace);
	if ( tempelement != null){
		subDomain.setFastSystem( getFastSystem(tempelement, mathDesc));
	}
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
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public Constant getConstant(Element param) throws XmlParseException {
	//retrieve values
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
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
	List<Element> coordList = param.getChildren();

	//Upon de type, decide which Curve type to create
	if (type.equalsIgnoreCase(XMLTags.PolyLineTypeTag)) {
		if ( coordList.size()==2) { //I have a Line
			Coordinate begin = getCoordinate(coordList.get(0));
			Coordinate end = getCoordinate(coordList.get(1));
			// ****create new Line ****
			curve = new Line(begin, end);
		} else {
			//If it it is not a Line, then it is a SampledCurve
			Coordinate[] coords = new Coordinate[coordList.size()];
			for (int i = 0; i < coordList.size(); i++){
				coords[i] = getCoordinate(coordList.get(i) );
			}
			//****create new SampledCurve ****
			 curve = new SampledCurve(coords);
		}
	} else if (type.equalsIgnoreCase(XMLTags.SplineTypeTag)) {
		Coordinate[] coords = new Coordinate[coordList.size()];
		for (int i = 0; i < coordList.size(); i++){
			coords[i] = getCoordinate(coordList.get(i));
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
public DBFormalSpecies getDBFormalSpecies(Element formalSpeciesElement) throws XmlParseException {
	//read key
	String keystring = formalSpeciesElement.getAttributeValue(XMLTags.KeyValueAttrTag);
	KeyValue key = new KeyValue(keystring);
	//read type
	String typestring = formalSpeciesElement.getAttributeValue(XMLTags.TypeAttrTag);
	//read the FormalSpeciesInfo
	Element speciesInfoElement = formalSpeciesElement.getChild(XMLTags.FormalSpeciesInfoTag, vcNamespace);

	//create the DBFormalSpecies upon the type
	DBFormalSpecies formalSpecies = null;
	
	if (typestring.equalsIgnoreCase(XMLTags.CompoundTypeTag)) {
		formalSpecies = new FormalCompound(key, (CompoundInfo)getFormalSpeciesInfo(speciesInfoElement));
	} else if (typestring.equalsIgnoreCase(XMLTags.EnzymeTypeTag)) {
		formalSpecies = new FormalEnzyme(key, (EnzymeInfo)getFormalSpeciesInfo(speciesInfoElement));
	} else if (typestring.equalsIgnoreCase(XMLTags.ProteinTypeTag)) {
		formalSpecies = new FormalProtein(key, (ProteinInfo)getFormalSpeciesInfo(speciesInfoElement));
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
public DBSpecies getDBSpecies(Element dbSpeciesElement) throws XmlParseException {
	//Read the key
	String keystring = dbSpeciesElement.getAttributeValue(XMLTags.KeyValueAttrTag);
	KeyValue key = new KeyValue(keystring);
	DBSpecies dbSpecies = null;

	//read the type
	String type = dbSpeciesElement.getAttributeValue(XMLTags.TypeAttrTag);
	//Read the DBFormalSpecies
	org.jdom.Element formalSpeciesElement = dbSpeciesElement.getChild(XMLTags.DBFormalSpeciesTag, vcNamespace);

	if (type.equalsIgnoreCase(XMLTags.CompoundTypeTag)) {
		//Create a BoundCompound
		dbSpecies = new BoundCompound(key, (FormalCompound)getDBFormalSpecies(formalSpeciesElement));
	} else if (type.equalsIgnoreCase(XMLTags.EnzymeTypeTag)) {
		//Create a BoundEnzyme
		dbSpecies = new BoundEnzyme(key, (FormalEnzyme)getDBFormalSpecies(formalSpeciesElement));
	} else if (type.equalsIgnoreCase(XMLTags.ProteinTypeTag)) {
		//Create a BoundProtein
		dbSpecies = new BoundProtein(key, (FormalProtein)getDBFormalSpecies(formalSpeciesElement));
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
public Diagram getDiagram(Element param, Model model) throws XmlParseException{
	//get Attibutes
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String structureName = unMangle( param.getAttributeValue(XMLTags.StructureAttrTag) );
	Structure structureref = model.getStructure(structureName);
	if (structureref == null) {
		throw new XmlParseException("The structure " + structureName + "could not be resolved!");
	}
	//try to create the new Diagram
	Diagram newdiagram = new Diagram(structureref, name);
	//Add Nodereferences (Shapes)
	List<Element> children = param.getChildren();
	if ( children.size()>0 ) {
		NodeReference[] arraynoderef = new NodeReference[children.size()];
		for (int i=0 ; i<children.size() ; i++) {
			arraynoderef[i] = getNodeReference(children.get(i) );
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
	// String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	//get Electrode
	Electrode electrode = getElectrode(param.getChild(XMLTags.ElectrodeTag, vcNamespace), currentSimulationContext);
	
	if (param.getAttributeValue(XMLTags.TypeAttrTag).equalsIgnoreCase(XMLTags.VoltageClampTag)) {
		//is a voltage clamp
		clampStimulus = new VoltageClampStimulus(electrode, "voltClampElectrode", new Expression(0.0), currentSimulationContext);
	} else if (param.getAttributeValue(XMLTags.TypeAttrTag).equalsIgnoreCase(XMLTags.CurrentDensityClampTag) ||
				param.getAttributeValue(XMLTags.TypeAttrTag).equalsIgnoreCase(XMLTags.CurrentDensityClampTag_oldName)) {
		//is a current density clamp
		clampStimulus = new CurrentDensityClampStimulus(electrode, "currDensityClampElectrode", new Expression(0.0), currentSimulationContext);
	} else if (param.getAttributeValue(XMLTags.TypeAttrTag).equalsIgnoreCase(XMLTags.TotalCurrentClampTag)) {
		//is a "total" current clamp
		clampStimulus = new TotalCurrentClampStimulus(electrode, "totalCurrClampElectrode", new Expression(0.0), currentSimulationContext);
	}
	
	try {
		clampStimulus.reading(true);   // transaction begin flag ... yeah, this is a hack
		
		//Read all of the parameters
		List<Element> list = param.getChildren(XMLTags.ParameterTag, vcNamespace);

		// add constants that may be used in the electrical stimulus.
		VariableHash varHash = new VariableHash();
		addResevedSymbols(varHash);
		ArrayList<String> reserved = getReservedVars();

		//
		// rename "special" parameters (those that are not "user defined")
		//
		for (int i = 0; i < list.size() ; i++){
			Element xmlParam = list.get(i);
			String paramName = unMangle(xmlParam.getAttributeValue(XMLTags.NameAttrTag));
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
			LocalParameter tempParam = null;
			if (!role.equals(XMLTags.ParamRoleUserDefinedTag)) {
				if (role.equals(XMLTags.ParamRoleTotalCurrentTag)){
					if (clampStimulus instanceof TotalCurrentClampStimulus){
						tempParam = ((TotalCurrentClampStimulus)clampStimulus).getCurrentParameter();
					} else {
						varHash.removeVariable(paramName);
						continue;
					}
				}else if (role.equals(XMLTags.ParamRoleTotalCurrentDensityTag) || role.equals(XMLTags.ParamRoleTotalCurrentDensityOldNameTag)){
					if (clampStimulus instanceof CurrentDensityClampStimulus){
						tempParam = ((CurrentDensityClampStimulus)clampStimulus).getCurrentDensityParameter();
					} else {
						varHash.removeVariable(paramName);
						continue;
					}
				}else if (role.equals(XMLTags.ParamRolePotentialDifferenceTag)){
					if (clampStimulus instanceof VoltageClampStimulus){
						tempParam = ((VoltageClampStimulus)clampStimulus).getVoltageParameter();
					}else{
						varHash.removeVariable(paramName);
						continue;
					}
				}
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
				LocalParameter multNameParam = clampStimulus.getLocalParameter(paramName);
				int n = 0;
				while (multNameParam != null) {
					String tempName = paramName + "_" + n++;
					clampStimulus.renameParameter(paramName, tempName);
					multNameParam = clampStimulus.getLocalParameter(tempName);
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
		
		Variable sortedVariables[] = varHash.getTopologicallyReorderedVariables();
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
				LocalParameter tempParam = clampStimulus.getLocalParameter(paramFunction.getName());
				if (tempParam == null) {
					clampStimulus.addUserDefinedParameter(paramFunction.getName(), paramFunction.getExpression(), unit);
				} else {
					if (tempParam.getExpression()!=null){ // if the expression is null, it should remain null.
						clampStimulus.setParameterValue(tempParam, paramFunction.getExpression());
					}
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
	String featureName = unMangle(elem.getAttributeValue(XMLTags.FeatureAttrTag));
	Feature feature = (Feature)currentSimulationContext.getModel().getStructure(featureName);
	//retrieve position
	Coordinate position = getCoordinate(elem.getChild(XMLTags.CoordinateTag, vcNamespace));
	
	Electrode newElect = new Electrode(feature, position);
	
	return newElect;
}


/**
 * This method returns a ErrorTolerance object from a XML Element.
 * Creation date: (5/22/2001 11:50:07 AM)
 * @return cbit.vcell.solver.ErrorTolerance
 * @param param org.jdom.Element
 */
public ErrorTolerance getErrorTolerance(Element param) {
	//getAttributes
	double absolut = Double.parseDouble( param.getAttributeValue(XMLTags.AbsolutErrorToleranceTag) );
	double relative = Double.parseDouble( param.getAttributeValue(XMLTags.RelativeErrorToleranceTag) );
	
	//*** create new ErrorTolerance object ****
	ErrorTolerance errorTol = new ErrorTolerance(absolut, relative);
	
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
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public FastSystem getFastSystem(
    Element param,
    MathDescription mathDesc)
    throws XmlParseException {
    //Create a new FastSystem
    FastSystem fastSystem = new FastSystem(mathDesc);

    //Process the FastInvariants
    Iterator<Element> iterator = param.getChildren(XMLTags.FastInvariantTag, vcNamespace).iterator();
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
    iterator = param.getChildren(XMLTags.FastRateTag, vcNamespace).iterator();
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
public Structure getFeature(Element param) throws XmlParseException {
	Feature newfeature = null;
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

	//retrieve the key if there is one
	KeyValue key = null;
	String keystring = param.getAttributeValue(XMLTags.KeyValueAttrTag);
	
	if ( keystring!=null && keystring.length()>0 && this.readKeysFlag) {
		key = new KeyValue( keystring );
	}
	
	//---Create the new feature---
	try {
		newfeature = new Feature( key, name );
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(
			"An error occurred while creating the feature "
				+ param.getAttributeValue(XMLTags.NameAttrTag)+" : "+e.getMessage());
	}

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
	String featurename = unMangle( param.getAttributeValue(XMLTags.FeatureAttrTag) );
	String subvolumename = param.getAttributeValue(XMLTags.SubVolumeAttrTag);
	if (subvolumename != null)
		subvolumename = unMangle(subvolumename);
	boolean resolved = Boolean.valueOf(param.getAttributeValue(XMLTags.ResolvedAttrTag)).booleanValue();
	
	Feature featureref = (Feature)simulationContext.getModel().getStructure(featurename);
	if (featureref == null) {
		throw new XmlParseException("The Feature "+ featurename + " could not be resolved!");
	}

	//*** Create new Feature Mapping ****
	FeatureMapping feamap = new FeatureMapping(featureref,simulationContext);

	//Set Size
	if(param.getAttributeValue(XMLTags.SizeTag) != null)
	{
		String size = unMangle( param.getAttributeValue(XMLTags.SizeTag) );
		try {
			feamap.getSizeParameter().setExpression(new Expression(size));
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("An expressionException was fired when setting the size Expression " + size + " to a featureMapping!"+" : "+e.getMessage());
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
		}	
	}
	//Retrieve subvolumeref, allow subvolumes to be 'null'
	if (subvolumename != null) {
		SubVolume subvolumeref = simulationContext.getGeometry().getGeometrySpec().getSubVolume(subvolumename);
		if (subvolumeref == null) {
			throw new XmlParseException("The SubVolume "+ subvolumename + " could not be resolved!");
		}
		//Set attributes to the featuremapping
		try {
			feamap.setSubVolume( subvolumeref );
		}catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException("A propertyVetoException was fired when trying to set the subvolume " + subvolumename + " to a FeatureMapping!"+" : "+e.getMessage());
		}
	}
	try {
		feamap.setResolved( resolved );
	}catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A propertyVetoException was fired when setting to a FeatureMapping if it is resolved!"+" : "+e.getMessage());
	}
	//Set Boundary conditions
	Element tempElement = param.getChild(XMLTags.BoundariesTypesTag, vcNamespace);
	
	//Xm
	String temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueXm);
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
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

	//-- create new FilamentRegionVariable object
	FilamentRegionVariable filRegVariable = new FilamentRegionVariable( name );

	return filRegVariable;
}


/**
 * This method returns a FilamentSubDomain object from a XMl element.
 * Creation date: (5/18/2001 4:27:22 PM)
 * @return cbit.vcell.math.FilamentSubDomain
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public FilamentSubDomain getFilamentSubDomain(Element param, MathDescription mathDesc) throws XmlParseException {
	//get name
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	//get outside Compartment ref
	String outsideName = unMangle( param.getAttributeValue(XMLTags.OutsideCompartmentTag) );
	CompartmentSubDomain outsideRef = (CompartmentSubDomain)mathDesc.getCompartmentSubDomain(outsideName);
	if (outsideRef == null) {
		throw new XmlParseException("The reference to the CompartmentSubDomain "+ outsideName + ", could not be resolved!");
	}
	//*** create new filamentSubDomain object ***
	FilamentSubDomain filDomain = new FilamentSubDomain(name, outsideRef);
	
	//add OdeEquations
	Iterator<Element> iterator = param.getChildren(XMLTags.OdeEquationTag, vcNamespace).iterator();
	while ( iterator.hasNext() ){
		Element tempElement = (Element)iterator.next();
		try {
			filDomain.addEquation( getOdeEquation(tempElement, mathDesc) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding an OdeEquation to the FilamentSubDomain " + name+" : "+e.getMessage());
		}
	}
	//Add the FastSytem
	filDomain.setFastSystem( getFastSystem(param.getChild(XMLTags.FastSystemTag, vcNamespace), mathDesc) );

	return filDomain;
}


/**
 * This method returns a FilamentVariable object from a XML Element.
 * Creation date: (5/16/2001 2:56:34 PM)
 * @return cbit.vcell.math.FilamentVariable
 * @param param org.jdom.Element
 */
public FilamentVariable getFilamentVariable(Element param) {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

	//-- create new filVariable object
	FilamentVariable filVariable = new FilamentVariable( name );

	return filVariable;
}


/**
 * This method returns a FluxReaction object from a XML element.
 * Creation date: (3/16/2001 11:52:02 AM)
 * @return cbit.vcell.model.FluxReaction
 * @param param org.jdom.Element
 */
public FluxReaction getFluxReaction( Element param, Model model, VariableHash varsHash) throws XmlParseException, java.beans.PropertyVetoException {
	//retrieve the key if there is one
	KeyValue key = null;
	String keystring = param.getAttributeValue(XMLTags.KeyValueAttrTag);
	
	if ( keystring != null && keystring.length()>0 && this.readKeysFlag ) {
		key = new KeyValue( keystring );
	}

	//resolve reference to the Membrane
	String structureName = unMangle(param.getAttributeValue(XMLTags.StructureAttrTag));
	Membrane structureref = (Membrane) model.getStructure(structureName);

	if (structureref == null) {
		throw new XmlParseException(
			"The membrane " + structureName + " could not be resolved in the dictionnary!");
	}
	//resolve reference to the fluxCarrier
	Species specieref = null;
	if (param.getAttribute(XMLTags.FluxCarrierAttrTag)!= null) {	
		String speciesname = unMangle(param.getAttributeValue(XMLTags.FluxCarrierAttrTag));
		specieref = model.getSpecies(speciesname);
		
		if (specieref == null) {
			throw new XmlParseException("The Species " + speciesname + " could not be resolved in the dictionnary!");
		}
	}
	
	//-- Instantiate new FluxReaction --
	FluxReaction fluxreaction = null;
	String name = unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	try {
		fluxreaction = new FluxReaction(structureref, specieref, model, key, name);
		fluxreaction.setModel(model);
	} catch (Exception e) {
		e.printStackTrace();
		throw new XmlParseException( "An exception occurred while trying to create the FluxReaction " + name+" : "+e.getMessage());
	}
	//Annotation
//	String rsAnnotation = null;
//	String annotationText = param.getChildText(XMLTags.AnnotationTag, vcNamespace);
//	if (annotationText!=null && annotationText.length()>0) {
//		rsAnnotation = unMangle(annotationText);
//	}
//	fluxreaction.setAnnotation(rsAnnotation);
	
	//set the valence
	String valenceString = null;
	try {
		valenceString = unMangle(param.getAttributeValue(XMLTags.FluxCarrierValenceAttrTag));
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
				fluxreaction.setPhysicsOptions(FluxReaction.PHYSICS_ELECTRICAL_ONLY);
			}else if (fluxOptionString.equals(XMLTags.FluxOptionMolecularAndElectrical)){
				fluxreaction.setPhysicsOptions(FluxReaction.PHYSICS_MOLECULAR_AND_ELECTRICAL);
			}else if (fluxOptionString.equals(XMLTags.FluxOptionMolecularOnly)){
				fluxreaction.setPhysicsOptions(FluxReaction.PHYSICS_MOLECULAR_ONLY);
			} 
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new XmlParseException("A propertyVetoException was fired when setting the fluxOption to the flux reaction " + name+" : "+e.getMessage());
		}
	}
	//Add Catalyst(Modifiers) (if there are)
	Iterator<Element> iterator = param.getChildren(XMLTags.CatalystTag, vcNamespace).iterator();
	while (iterator.hasNext()) {
		Element temp = iterator.next();
		fluxreaction.addReactionParticipant( getCatalyst(temp, fluxreaction, model) );
	}
	//Add Kinetics
	fluxreaction.setKinetics(getKinetics(param.getChild(XMLTags.KineticsTag, vcNamespace), fluxreaction, varsHash));
	
	return fluxreaction;
}


/**
 * This method creates a FormalSpeciesInfo from a XML representation.
 * Creation date: (6/3/2003 9:11:26 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 * @param speciesInfoElement org.jdom.Element
 */
public FormalSpeciesInfo getFormalSpeciesInfo(Element speciesInfoElement) throws XmlParseException {
	//get formalID
	String formalID = unMangle(speciesInfoElement.getAttributeValue(XMLTags.FormalIDTag));
	//get names
	List<Element> namesList = speciesInfoElement.getChildren(XMLTags.NameTag, vcNamespace);
	String[] namesArray = new String[namesList.size()];

	for (int i = 0; i < namesList.size(); i++){
		Element nameElement = (Element)namesList.get(i);
		namesArray[i] = unMangle(nameElement.getText());
	}
	String tempstring;
	//get type
	String type = speciesInfoElement.getAttributeValue(XMLTags.TypeAttrTag);
	FormalSpeciesInfo formalSpeciesInfo = null;

	if (type.equalsIgnoreCase(XMLTags.CompoundTypeTag)) {
		//get formula
		String formula = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.FormulaTag);
		if (tempstring!=null) {
			formula = unMangle(tempstring);
		}
		
		//get CASID
		String casid = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.CasIDTag);
		if (tempstring!=null) {
			casid = unMangle(tempstring);
		}
		
		//get Enzymes
		List<Element> enzymelist = speciesInfoElement.getChildren(XMLTags.EnzymeTag, vcNamespace);
		EnzymeRef[] enzymeArray = null;
		
		if (enzymelist!=null && enzymelist.size()>0) {
			enzymeArray = new EnzymeRef[enzymelist.size()];
			
			for (int i = 0; i < enzymelist.size(); i++){
				Element enzymeElement = (Element)enzymelist.get(i);
				//get ECNumber
				String ecnumber = unMangle(enzymeElement.getAttributeValue(XMLTags.ECNumberTag));
				//get Enzymetype
				String enztypestr = enzymeElement.getAttributeValue(XMLTags.TypeAttrTag);
				char enzymetype = enztypestr.charAt(0);
				enzymeArray[i] = new EnzymeRef(ecnumber, enzymetype);
			}
		}

		//create new CompoundInfo
		formalSpeciesInfo = new CompoundInfo(formalID, namesArray, formula, casid, enzymeArray);
	} else if (type.equalsIgnoreCase(XMLTags.EnzymeTypeTag)) {
		//get reaction
		String reaction = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.ExpressionAttrTag);
		if (tempstring != null) {
			reaction = unMangle(tempstring);
		}
		//get sysname
		String sysname = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.SysNameTag);
		if (tempstring != null) {
			sysname = unMangle(tempstring);
		}
		//get argcasID
		String casid = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.CasIDTag);
		if (tempstring != null) {
			casid = unMangle(tempstring);
		}
		//create new EnzymeInfo
		formalSpeciesInfo = new EnzymeInfo(formalID, namesArray, reaction, sysname, casid);
	} else if (type.equalsIgnoreCase(XMLTags.ProteinTypeTag)) {
		//get organism
		String organism = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.OrganismTag);
		if (tempstring != null) {
			organism = unMangle(tempstring);
		}
		//get accession
		String accession = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.AccessionTag);
		if (tempstring != null) {
			accession = unMangle(tempstring);
		}
		//get keywords
		String keywords = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.KeywordsTag);
		if (tempstring != null) {
			keywords = unMangle(tempstring);
		}
		//get description
		String description = null;
		tempstring = speciesInfoElement.getAttributeValue(XMLTags.DescriptionTag);
		if (tempstring != null) {
			description = unMangle(tempstring);
		}
		//create new ProteinInfo
		formalSpeciesInfo = new ProteinInfo(formalID, namesArray, organism, accession, keywords, description);
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
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public Function getFunction(Element param) throws XmlParseException {
	//get attributes
	String name = unMangle( param.getAttributeValue( XMLTags.NameAttrTag) );
	String temp = param.getText();
	
	Expression exp = unMangleExpression(temp);
	
	//-- create new Function --
	Function function = new Function(name, exp);

	return function;
}

public AnnotatedFunction getOutputFunction(Element param) throws XmlParseException {
	//get attributes
	String name = unMangle( param.getAttributeValue( XMLTags.NameAttrTag) );
	String temp = param.getText();
	Expression exp = unMangleExpression(temp);
	String errStr = unMangle( param.getAttributeValue( XMLTags.ErrorStringTag) );
	
	VariableType funcType = VariableType.UNKNOWN;
	String funcTypeAttr = param.getAttributeValue(XMLTags.FunctionTypeTag);
	if (funcTypeAttr != null) {
		String funcTypeStr = unMangle( funcTypeAttr );
		funcType = VariableType.getVariableTypeFromVariableTypeName(funcTypeStr);
	}

	//-- create new AnnotatedFunction --
	AnnotatedFunction function = new AnnotatedFunction(name, exp, errStr, funcType, FunctionCategory.OUTPUTFUNCTION);

	return function;
}

/**
 * This method returns a Geometry object from a XML representation.
 * Creation date: (4/26/2001 12:12:18 PM)
 * @return cbit.vcell.geometry.Geometry
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public Geometry getGeometry(Element param) throws XmlParseException {
	//Get the Extent object
	Extent newextent = getExtent(param.getChild(XMLTags.ExtentTag, vcNamespace));
	//Get VCimage information
	VCImage newimage = null;	
	if ( param.getChild(XMLTags.ImageTag, vcNamespace)!=null ) {
		try {
			newimage = getVCImage( param.getChild(XMLTags.ImageTag, vcNamespace), newextent );
		} catch (Throwable e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}
	
	//Get attributes
	String name = unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	int newdimension = Integer.parseInt(param.getAttributeValue(XMLTags.DimensionAttrTag));
	//Get Version
	Version version = getVersion(param.getChild(XMLTags.VersionTag, vcNamespace));
	
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
			//newgeometry.setDescription( unMangle(annotation) );
		//}
		//Add annotation
		String annotation = param.getChildText(XMLTags.AnnotationTag, vcNamespace);
		if ( annotation!=null && annotation.length()>0 ) {
			newgeometry.setDescription(unMangle(annotation));
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
	newgeometry.getGeometrySpec().setOrigin( getOrigin(param.getChild(XMLTags.OriginTag, vcNamespace)) );

	//Add the SubVolumes
	List<Element> children = param.getChildren(XMLTags.SubVolumeTag, vcNamespace);
	SubVolume[] newsubvolumes = new SubVolume[children.size()];
	for (int i=0 ; i<children.size() ; i++) {
		newsubvolumes[i] = getSubVolume(children.get(i));
	}
	try {
		newgeometry.getGeometrySpec().setSubVolumes( newsubvolumes );
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException was generated when ading the subvolumes to the Geometry " + name+" : "+e.getMessage());
	}
	//read Filaments (if any)
	Iterator<Element> iterator = param.getChildren(XMLTags.FilamentTag, vcNamespace).iterator();
	while (iterator.hasNext()) {
		Element tempElement = iterator.next();

		String filname = unMangle( tempElement.getAttributeValue(XMLTags.NameAttrTag));
		Iterator<Element> curveiterator = tempElement.getChildren().iterator();
		while (curveiterator.hasNext()) {
			ControlPointCurve curve = getControlPointCurve(curveiterator.next());
			newgeometry.getGeometrySpec().getFilamentGroup().addCurve(filname, curve);
		}
	}
	//read Surface description (if any)
	Element sd = param.getChild(XMLTags.SurfaceDescriptionTag, vcNamespace);
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
		    ArrayList<Element> memRegions = new ArrayList<Element>(param.getChildren(XMLTags.MembraneRegionTag, vcNamespace));
		    ArrayList<Element> volRegions = new ArrayList<Element>(param.getChildren(XMLTags.VolumeRegionTag, vcNamespace));
			ArrayList<GeometricRegion> regions = new ArrayList<GeometricRegion>();
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
				temp = memRegions.get(i);
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
		List<Element> userlist = xmlGroup.getChildren(XMLTags.UserTag, vcNamespace);
		User[] userArray = new User[userlist.size()];
		boolean[] booleanArray = new boolean[userlist.size()];
		
		for (int i = 0; i < userlist.size(); i++){
			Element userElement = userlist.get(i);
			String userid = unMangle(userElement.getAttributeValue(XMLTags.NameAttrTag));
			KeyValue key = new KeyValue(userElement.getAttributeValue(XMLTags.KeyValueAttrTag));
			boolean hidden = Boolean.valueOf(userElement.getAttributeValue(XMLTags.HiddenTag)).booleanValue();
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
	String name = unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	int handle = Integer.parseInt( param.getAttributeValue(XMLTags.HandleAttrTag) );
	int imagePixelValue = Integer.parseInt( param.getAttributeValue(XMLTags.ImagePixelValueTag) );
 
	//Get the PixelClass from image (image should be a sibling of this subVolume element)
	Element imageElement = param.getParent().getChild(XMLTags.ImageTag, vcNamespace);
	if (imageElement==null){
		throw new XmlParseException("image not found in geometry corresponding to ImageSubVolume");
	}
	
	List<Element> pixelClassList = imageElement.getChildren(XMLTags.PixelClassTag, vcNamespace);
	VCPixelClass pixelClass = null;
	for (int i = 0; i < pixelClassList.size(); i++){
		VCPixelClass pc = getPixelClass(pixelClassList.get(i));
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
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	//get VolVariableRef
	String volvarName = unMangle( param.getAttributeValue(XMLTags.VolumeVariableAttrTag) );

	//*** create new InsideVariable ***
	InsideVariable variable = new InsideVariable(name , volvarName);
	
	return variable;
}


/**
 * This method returns a JumpCondition object from a XML Element.
 * Creation date: (5/18/2001 5:10:10 PM)
 * @return cbit.vcell.math.JumpCondition
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public JumpCondition getJumpCondition(Element param, MathDescription mathDesc) throws XmlParseException {
	//get VolVariable ref
	String varname = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	Variable var = mathDesc.getVariable(varname);
	if ( var == null ) {
		throw new XmlParseException("The reference to the Variable " + varname + ", could not be resolved!");
	}
	
	JumpCondition jumpCondition = null;
	if (var instanceof VolVariable) {
		jumpCondition = new JumpCondition((VolVariable)var);
	} else if (var instanceof VolumeRegionVariable) {
		jumpCondition = new JumpCondition((VolumeRegionVariable)var);
	} else {
		throw new XmlParseException("unexpected variable type for jump condition");
	}

	//process InFlux
	String temp = param.getChildText(XMLTags.InFluxTag, vcNamespace);
	Expression exp = unMangleExpression( temp );
	jumpCondition.setInFlux(exp);
	

	//process OutFlux
	temp = param.getChildText(XMLTags.OutFluxTag, vcNamespace);
	exp = unMangleExpression( temp );
	jumpCondition.setOutFlux( exp );

	return jumpCondition;
}


/**
 * The method returns a JumpProcess object from a XML element.
 * Creation date: (7/24/2006 6:28:42 PM)
 * @return cbit.vcell.math.JumpProcess
 * @param param org.jdom.Element
 * @param md cbit.vcell.math.MathDescription
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public JumpProcess getJumpProcess(Element param, MathDescription md) throws XmlParseException 
{
	//name
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	//probability rate
	Element pb = param.getChild(XMLTags.ProbabilityRateTag, vcNamespace);
	Expression exp = unMangleExpression(pb.getText());

	JumpProcess jump = new JumpProcess(name,exp);
	//add actions
	Iterator<Element> iterator = param.getChildren(XMLTags.ActionTag, vcNamespace).iterator();
	while ( iterator.hasNext() ) {
		Element tempelement = (Element)iterator.next();
		try {
			jump.addAction(getAction(tempelement, md));
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding a new Action to the JumpProcess " + name+" : "+e.getMessage());
		} catch (ExpressionException e) {e.printStackTrace();}
	}
	
	return jump;
}

/**
 * This method returns a Kinetics object from a XML Element based on the value of the kinetics type attribute.
 * Creation date: (3/19/2001 4:42:04 PM)
 * @return cbit.vcell.model.Kinetics
 * @param param org.jdom.Element
 */
public Kinetics getKinetics(Element param, ReactionStep reaction, VariableHash varHash) throws XmlParseException{

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
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeGeneralTotal_oldname) ) {
			//create GeneralTotalKinetics
			newKinetics = new GeneralLumpedKinetics(reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeGeneralLumped) ) {
			//create GeneralLumpedKinetics
			newKinetics = new GeneralLumpedKinetics(reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeGeneralCurrentLumped) ) {
			//create GeneralCurrentLumpedKinetics
			newKinetics = new GeneralCurrentLumpedKinetics(reaction);
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
		List<Element> list = param.getChildren(XMLTags.ParameterTag, vcNamespace);

		// add constants that may be used in kinetics.
		// VariableHash varHash = getVariablesHash();
		ArrayList<String> reserved = getReservedVars();

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
			String paramName = unMangle(xmlParam.getAttributeValue(XMLTags.NameAttrTag));
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
			// hack for bringing in General Total kinetics without breaking.
			if (tempParam == null && newKinetics instanceof GeneralLumpedKinetics) {
				if (role.equals(Kinetics.GTK_AssumedCompartmentSize_oldname) || role.equals(Kinetics.GTK_ReactionRate_oldname) || role.equals(Kinetics.GTK_CurrentDensity_oldname)) {
					continue;
				} else if (role.equals(VCMODL.TotalRate_oldname)) {
					tempParam = newKinetics.getKineticsParameterFromRole(Kinetics.ROLE_LumpedReactionRate);
				}
			}
					
			if (tempParam == null) {
				throw new XmlParseException("parameter with role '"+role+"' not found in kinetics type '"+type+"'");
			} 
			//
			// custom name for "special" parameter
			//
			if (!tempParam.getName().equals(paramName)) {
				Kinetics.KineticsParameter multNameParam = newKinetics.getKineticsParameter(paramName);
				int n = 0;
				while (multNameParam != null) {
					String tempName = paramName + "_" + n++;
					newKinetics.renameParameter(paramName, tempName);
					multNameParam = newKinetics.getKineticsParameter(tempName);
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
		
		Variable sortedVariables[] = varHash.getTopologicallyReorderedVariables();
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
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public MathDescription getMathDescription(Element param) throws XmlParseException {
	MathDescription mathdes = null;
	Element tempelement;

	//Retrieve Metadata(Version)
	Version version = getVersion(param.getChild(XMLTags.VersionTag, vcNamespace));

	//Retrieve attributes
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

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
			//mathdes.setDescription(unMangle(annotation));
		//}
		//add Annotation
		String annotationText  = param.getChildText(XMLTags.AnnotationTag, vcNamespace);
		if (annotationText!=null && annotationText.length()>0) {
			mathdes.setDescription(unMangle(annotationText));
		}
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException was fired when setting the name " + name + ", to a new MathDescription!"+" : "+e.getMessage());
	}
	
	VariableHash varHash = new VariableHash();
	
	//Retrieve Constant
	Iterator<Element> iterator = param.getChildren(XMLTags.ConstantTag, vcNamespace).iterator();
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
	iterator = param.getChildren(XMLTags.FilamentRegionVariableTag, vcNamespace).iterator();
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
	iterator = param.getChildren(XMLTags.FilamentVariableTag, vcNamespace).iterator();
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
	iterator = param.getChildren(XMLTags.MembraneRegionVariableTag, vcNamespace).iterator();
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
	iterator = param.getChildren(XMLTags.MembraneVariableTag, vcNamespace).iterator();
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
	iterator = param.getChildren(XMLTags.VolumeRegionVariableTag, vcNamespace).iterator();
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
	iterator = param.getChildren(XMLTags.VolumeVariableTag, vcNamespace).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getVolVariable(tempelement) );
		} catch (MappingException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}

	//Retrieve StochVolVariable
	iterator = param.getChildren(XMLTags.StochVolVariableTag, vcNamespace).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getStochVolVariable(tempelement) );
		} catch (MappingException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}
	
	//Retrieve all the Functions //This needs to be processed before all the variables are read!
	iterator = param.getChildren(XMLTags.FunctionTag, vcNamespace).iterator();
	while ( iterator.hasNext() ){
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable(getFunction(tempelement));
		}catch (MappingException e){
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}

	iterator = param.getChildren(XMLTags.VolumeRandomVariableTag, vcNamespace).iterator();
	while ( iterator.hasNext() ){
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable(getRandomVariable(tempelement));
		}catch (MappingException e){
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}
	iterator = param.getChildren(XMLTags.MembraneRandomVariableTag, vcNamespace).iterator();
	while ( iterator.hasNext() ){
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable(getRandomVariable(tempelement));
		}catch (MappingException e){
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}
	
	//
	// add all variables at once
	//
	try {
		mathdes.setAllVariables(varHash.getAlphabeticallyOrderedVariables());
	} catch (MappingException e) {
		e.printStackTrace();
		throw new XmlParseException("Error adding the Function variables to the MathDescription " + name+" : "+e.getMessage());
	} catch (MathException e) {
		e.printStackTrace();
		throw new XmlParseException("Error adding the Function variables to the MathDescription " + name+" : "+e.getMessage());
	} catch (ExpressionBindingException e) {
		e.printStackTrace();
		throw new XmlParseException("Error adding the Function variables to the MathDescription " + name+" : "+e.getMessage());
	}

	//Retrieve CompartmentsSubdomains
	iterator = param.getChildren(XMLTags.CompartmentSubDomainTag, vcNamespace).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			mathdes.addSubDomain( getCompartmentSubDomain(tempelement, mathdes) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("Error adding a new CompartmentSubDomain to the MathDescription " + name+" : "+e.getMessage());
		}
	}

	//Retrieve MembraneSubdomains
	iterator = param.getChildren(XMLTags.MembraneSubDomainTag, vcNamespace).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			mathdes.addSubDomain( getMembraneSubDomain(tempelement, mathdes) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("Error adding a new MembraneSubDomain to the MathDescription " + name+" : "+e.getMessage());
		}
	}
	
	//Retrieve the FilamentSubdomain (if any)
	tempelement = param.getChild(XMLTags.FilamentSubDomainTag, vcNamespace);
	if (tempelement != null) {
		try {
			mathdes.addSubDomain( getFilamentSubDomain(tempelement, mathdes) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("Error adding a new FilamentSubDomain to the MathDescription " + name+" : "+e.getMessage());
		}
	}
	
	iterator = param.getChildren(XMLTags.EventTag, vcNamespace).iterator();
	while (iterator.hasNext()) {
		tempelement = (Element)iterator.next();
		Event event = getEvent(mathdes, tempelement);
		try {
			mathdes.addEvent(event);
		} catch (MathException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException(e.getMessage());
		}
	}
	return mathdes;
}

public RandomVariable getRandomVariable(Element param) throws XmlParseException {
	//get attributes
	String name = unMangle(param.getAttributeValue( XMLTags.NameAttrTag));
	Element element = param.getChild(XMLTags.RandomVariableSeedTag, vcNamespace);
	Expression seed = null;
	if (element != null) {
		seed = unMangleExpression(element.getText());
	}
	Distribution dist = null;
	element = param.getChild(XMLTags.UniformDistributionTag, vcNamespace);
	if (element != null) {	
		dist = getUniformDistribution(element);
	}	
	element = param.getChild(XMLTags.GaussianDistributionTag, vcNamespace);
	if (element != null) {	
		dist = getGaussianDistribution(element);
	}
	
	if (param.getName().equals(XMLTags.VolumeRandomVariableTag)) {
		return new VolumeRandomVariable(name, seed, dist);
	} else if (param.getName().equals(XMLTags.MembraneRandomVariableTag)) {
		return new MembraneRandomVariable(name, seed, dist);
	} else {
		throw new XmlParseException(param.getName() + " is not supported!");
	}
}

private GaussianDistribution getGaussianDistribution(Element distElement) {
	Element element = distElement.getChild(XMLTags.GaussianDistributionMeanTag, vcNamespace);
	Expression mu = unMangleExpression(element.getText());
	
	element = distElement.getChild(XMLTags.GaussianDistributionStandardDeviationTag, vcNamespace);
	Expression sigma = unMangleExpression(element.getText());

	return new GaussianDistribution(mu, sigma);
}

private UniformDistribution getUniformDistribution(Element distElement) {
	Element element = distElement.getChild(XMLTags.UniformDistributionMinimumTag, vcNamespace);
	Expression low = unMangleExpression(element.getText());
	
	element = distElement.getChild(XMLTags.UniformDistributionMaximumTag, vcNamespace);
	Expression high = unMangleExpression(element.getText());

	return new UniformDistribution(low, high);
}

private Event getEvent(MathDescription mathdesc, Element eventElement) throws XmlParseException  {

	String name = unMangle(eventElement.getAttributeValue(XMLTags.NameAttrTag));
	Element element = eventElement.getChild(XMLTags.TriggerTag, vcNamespace);
	Expression triggerExp = unMangleExpression(element.getText());
	
	element = eventElement.getChild(XMLTags.DelayTag, vcNamespace);
	Delay delay = null;
	if (element != null) {
		boolean useValuesFromTriggerTime = Boolean.valueOf(element.getAttributeValue(XMLTags.UseValuesFromTriggerTimeAttrTag)).booleanValue();
		Expression durationExp = unMangleExpression(element.getText());
		delay = new Delay(useValuesFromTriggerTime, durationExp);
	}
	
	ArrayList<EventAssignment> eventAssignmentList = new ArrayList<EventAssignment>();
	Iterator<Element> iter = eventElement.getChildren(XMLTags.EventAssignmentTag, vcNamespace).iterator();
	while (iter.hasNext()) {
		element = iter.next();
		String varname = element.getAttributeValue(XMLTags.EventAssignmentVariableAttrTag);
		Expression assignExp = unMangleExpression(element.getText());
		Variable var = mathdesc.getVariable(varname);
		EventAssignment eventAssignment = new EventAssignment(var, assignExp);
		eventAssignmentList.add(eventAssignment);
	}
	
	Event event = new Event(name, triggerExp, delay, eventAssignmentList);
	return event;
}

public BioEvent[] getBioEvents(SimulationContext simContext, Element bioEventsElement) throws XmlParseException  {
	Iterator<Element> bioEventsIterator = bioEventsElement.getChildren(XMLTags.BioEventTag, vcNamespace).iterator();
	Vector<BioEvent> bioEventsVector = new Vector<BioEvent>();
	while (bioEventsIterator.hasNext()) {
		Element bEventElement = (Element) bioEventsIterator.next();

		String name = unMangle(bEventElement.getAttributeValue(XMLTags.NameAttrTag));
		Element element = bEventElement.getChild(XMLTags.TriggerTag, vcNamespace);
		Expression triggerExp = unMangleExpression(element.getText());
		
		element = bEventElement.getChild(XMLTags.DelayTag, vcNamespace);
		BioEvent.Delay delay = null;
		BioEvent newBioEvent = new BioEvent(name, triggerExp, delay, null, simContext);
		if (element != null) {
			boolean useValuesFromTriggerTime = Boolean.valueOf(element.getAttributeValue(XMLTags.UseValuesFromTriggerTimeAttrTag)).booleanValue();
			Expression durationExp = unMangleExpression((element.getText()));
			try {
				delay = newBioEvent.new Delay(useValuesFromTriggerTime, durationExp);
				newBioEvent.setDelay(delay);
			} catch (ExpressionBindingException e) {
				e.printStackTrace(System.out);
				throw new XmlParseException(e.getMessage());
			}
		}
		
		ArrayList<BioEvent.EventAssignment> eventAssignmentList = new ArrayList<BioEvent.EventAssignment>();
		Iterator<Element> iter = bEventElement.getChildren(XMLTags.EventAssignmentTag, vcNamespace).iterator();
		while (iter.hasNext()) {
			element = iter.next();
			try {
				String varname = element.getAttributeValue(XMLTags.EventAssignmentVariableAttrTag);
				Expression assignExp = unMangleExpression(element.getText());
				SymbolTableEntry target = simContext.getEntry(varname);
				BioEvent.EventAssignment eventAssignment = newBioEvent.new EventAssignment(target, assignExp);
				eventAssignmentList.add(eventAssignment);
			} catch (ExpressionException e) {
				e.printStackTrace(System.out);
				throw new XmlParseException(e.getMessage());
			}
		}
		try {
			newBioEvent.setEventAssignmentsList(eventAssignmentList);
		} catch (PropertyVetoException e1) {
			e1.printStackTrace(System.out);
			throw new XmlParseException(e1.getMessage());
		}
		try {
			newBioEvent.bind();
		} catch (ExpressionBindingException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException(e.getMessage());
		}
		bioEventsVector.add(newBioEvent);
	}
	
	return ((BioEvent[])BeanUtils.getArray(bioEventsVector, BioEvent.class));
}

/**
 * This method returns a MathModel object from a XML Element.
 * Creation date: (3/13/2001 12:35:00 PM)
 * @return cbit.vcell.mathmodel.MathModel
 * @param param org.jdom.Element
 */
public MathModel getMathModel(Element param) throws XmlParseException{
	//Create it
	//set Metadata (version), if any
	Version versionObject = getVersion(param.getChild(XMLTags.VersionTag, vcNamespace));
	MathModel mathmodel = new MathModel(versionObject);
	
	//Set attributes
	String name = unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	try {
		mathmodel.setName( name );
		//String annotation = param.getAttributeValue(XMLTags.AnnotationAttrTag);

		//if (annotation!=null) {
			//mathmodel.setDescription(unMangle(annotation));
		//}
		//Add annotation
		String annotationText = param.getChildText(XMLTags.AnnotationTag, vcNamespace);
		if (annotationText!=null && annotationText.length()>0) {
			mathmodel.setDescription(unMangle(annotationText));
		}
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("An error occurred while trying to set the name " + param.getAttributeValue(XMLTags.NameAttrTag) + "to a MathModel!"+" : "+e.getMessage());
	}

	//set MathDescription
	Element tempElem = param.getChild(XMLTags.MathDescriptionTag, vcNamespace);
	MathDescription mathDesc = getMathDescription(tempElem);
	
	if ( tempElem != null) {
		mathmodel.setMathDescription( mathDesc );
	} else {
		throw new XmlParseException("MathDescription missing in this MathModel!");
	}

	//set Geometry (if any)
	tempElem = param.getChild(XMLTags.GeometryTag, vcNamespace);
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
		
	// set output functions (outputfunctionContext)
	Element outputFunctionsElement = param.getChild(XMLTags.OutputFunctionsTag, vcNamespace);
	if (outputFunctionsElement != null) {
		ArrayList<AnnotatedFunction> outputFunctions = getOutputFunctions(outputFunctionsElement); 
		try {
			// construct OutputFnContext from mathmodel and add output functions that were read in from XML.
			OutputFunctionContext outputFnContext = mathmodel.getOutputFunctionContext();
			for (AnnotatedFunction outputFunction : outputFunctions) {
				outputFnContext.addOutputFunction(outputFunction);
			}
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException(e.getMessage());		
		}
	}

	//Set simulations contexts (if any)
	List<Element> childList = param.getChildren(XMLTags.SimulationTag, vcNamespace);
	Simulation[] simList = new Simulation[childList.size()];
	
	for (int i = 0; i < childList.size(); i++){
		simList[i] = getSimulation(childList.get(i), mathDesc);
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
public MathOverrides getMathOverrides(Element param, Simulation simulation) throws XmlParseException{

	MathOverrides mathOverrides = null;
	try {
		//Get the constants
		Object[] elements = param.getChildren().toArray();
		Vector<ConstantArraySpec> v1 = new Vector<ConstantArraySpec>();
		Vector<Constant> v2 = new Vector<Constant>();
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
		mathOverrides = new MathOverrides(simulation, constants, specs);
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
public Membrane getMembrane(Element param, List<Structure> featureList) throws XmlParseException {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	Membrane newmembrane = null;

	//retrieve the key if there is one
	KeyValue key = null;
	String stringkey = param.getAttributeValue(XMLTags.KeyValueAttrTag);
	
	if ( stringkey!=null && stringkey.length()>0 && this.readKeysFlag) {
		key = new KeyValue( stringkey );
	}
	
	//try to create new Membrane named "name"
	try {
		newmembrane = new Membrane(key, name);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(
			"An error occurred while trying to create the Membrane object " + name+" : "+e.getMessage());
	}
	//set inside feature
	String infeaturename = unMangle(param.getAttributeValue(XMLTags.InsideFeatureTag));
	String outfeaturename = unMangle(param.getAttributeValue(XMLTags.OutsideFeatureTag));
	Feature infeatureref = null, outfeatureref = null;
	
	for (Structure s : featureList) {
		String sname = s.getName();
		if (sname.equals(infeaturename)) {		
			infeatureref = (Feature)s;
		} else if (sname.equals(outfeaturename)) {
			outfeatureref = (Feature)s;
		}
		if (infeatureref != null && outfeatureref != null) {
			break;
		}
	}
	if (infeatureref ==null) {
		throw new XmlParseException("The inside feature " + infeaturename + "could not be resolved!");
	}
	if (outfeatureref ==null) {
		throw new XmlParseException("The outside feature " + outfeaturename + "could not be resolved!");
	}
	//set inside and outside feature
	newmembrane.setInsideFeature(infeatureref);
	newmembrane.setOutsideFeature(outfeatureref);
	//set MemVoltName
	if (param.getAttribute(XMLTags.MemVoltNameTag)==null) {
		throw new XmlParseException("Error reading membrane Voltage Name!");
	}
	String memvoltName = unMangle( param.getAttributeValue(XMLTags.MemVoltNameTag) );
	try {
		newmembrane.getMembraneVoltage().setName(memvoltName);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("Error setting the membrane Voltage Name:\n"+e.getMessage());
	}

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
	String membranename = unMangle( param.getAttributeValue(XMLTags.MembraneAttrTag) );
	
	Membrane membraneref = (Membrane)simulationContext.getModel().getStructure(membranename);
	if (membraneref == null) {
		throw new XmlParseException("The Membrane "+ membranename + " could not be resolved!");
	}

	//*** Create new Membrane Mapping ****
	MembraneMapping memmap = new MembraneMapping(membraneref, simulationContext);

	//Set SurfacetoVolumeRatio when it exists, amended Sept. 27th, 2007
	if(param.getAttributeValue(XMLTags.SurfaceToVolumeRatioTag)!= null)
	{
		String ratio = unMangle( param.getAttributeValue(XMLTags.SurfaceToVolumeRatioTag) );
		try {
			memmap.getSurfaceToVolumeParameter().setExpression(new Expression(ratio));
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("An expressionException was fired when setting the SurfacetoVolumeRatio Expression " + ratio + " to a membraneMapping!"+" : "+e.getMessage());
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}
	
	//Set VolumeFraction when it exists, amended Sept. 27th, 2007
	if(param.getAttributeValue(XMLTags.VolumeFractionTag) != null)
	{
		String fraction = unMangle( param.getAttributeValue(XMLTags.VolumeFractionTag) );
		try {
			memmap.getVolumeFractionParameter().setExpression(new Expression(fraction));
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("An expressionException was fired when setting the VolumeFraction Expression " + fraction + " to a membraneMapping!"+" : "+e.getMessage());
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}
	
	//Set Size
	if(param.getAttributeValue(XMLTags.SizeTag) != null)
	{
		String size = unMangle( param.getAttributeValue(XMLTags.SizeTag) );
		try {
			memmap.getSizeParameter().setExpression(new Expression(size));
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("An expressionException was fired when setting the size Expression " + size + " to a membraneMapping!"+" : "+e.getMessage());
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
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
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public MembraneRegionEquation getMembraneRegionEquation(Element param, MathDescription mathDesc) throws XmlParseException {
	//get attributes
	String varname = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	//find reference in the dictionnary
	//try a MembraneRegionVariable
	MembraneRegionVariable varref = (MembraneRegionVariable)mathDesc.getVariable(varname);
	if (varref == null) {
		throw new XmlParseException("The reference to the MembraneRegion variable "+ varname+ " could not be resolved!");
	}

	//get Initial condition
	String temp = param.getChildText(XMLTags.InitialTag, vcNamespace);
	Expression exp;
	exp = unMangleExpression( temp );
	// ** Create the Equation **
	MembraneRegionEquation memRegEq = new MembraneRegionEquation(varref,exp);

	//set the Uniform Rate
	temp = param.getChildText(XMLTags.UniformRateTag, vcNamespace);
	exp = unMangleExpression( temp );
	memRegEq.setUniformRateExpression(exp);

	//set the Membrane Rate
	temp = param.getChildText(XMLTags.MembraneRateTag, vcNamespace);
	exp = unMangleExpression( temp );
	memRegEq.setMembraneRateExpression(exp);
	
	//get ExactSolution (if any)
/*	temp = param.getChildText(XMLTags.ExactTag);
	if (temp !=null) {
		try {
			Expression expression = new Expression( unMangle( temp) );
			odeEquation.setExactSolution( expression);			
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("An ExpressionException was fired when creating the expression: "+ unMangle(temp)+" : "+e.getMessage());
		}
	}
	//get ConstructedSolution (if any)
	temp = param.getChildText(XMLTags.ConstructedTag);
	if (temp != null) {
		try {
			Expression expression = new Expression(unMangle(temp));
			odeEquation.setConstructedSolution( expression );
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("An ExpressionException was fired when creating the expression: "+ unMangle(temp)+" : "+e.getMessage());
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
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

	//-- create new MembraneRegionVariable object
	MembraneRegionVariable memRegVariable = new MembraneRegionVariable( name );

	return memRegVariable;
}


/**
 * This method returns a MembraneSubDomain object from a XML Element.
 * Creation date: (5/18/2001 4:23:30 PM)
 * @return cbit.vcell.math.MembraneSubDomain
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public MembraneSubDomain getMembraneSubDomain(Element param, MathDescription mathDesc) throws XmlParseException {
	//get compartmentSubDomain references
	//inside
	String name = unMangle( param.getAttributeValue(XMLTags.InsideCompartmentTag) );
	CompartmentSubDomain insideRef = (CompartmentSubDomain)mathDesc.getCompartmentSubDomain(name);
	if  (insideRef == null) {
		throw new XmlParseException("The reference to the inside CompartmentSubDomain "+ name+ ", could not be resolved!" );
	}
	//outside
	name = unMangle( param.getAttributeValue(XMLTags.OutsideCompartmentTag) );
	CompartmentSubDomain outsideRef = (CompartmentSubDomain)mathDesc.getCompartmentSubDomain(name);
	if  (outsideRef == null) {
		throw new XmlParseException("The reference to the outside CompartmentSubDomain "+ name+ ", could not be resolved!" );
	}
	//*** create new Membrane SubDomain ***
	MembraneSubDomain subDomain = new MembraneSubDomain(insideRef, outsideRef);

	//Process BoundaryConditions
	Iterator<Element> iterator = param.getChildren(XMLTags.BoundaryTypeTag, vcNamespace).iterator();
	while (iterator.hasNext()){
		Element tempelement = (Element)iterator.next();

		//create BoundaryConditionType
		String temp = tempelement.getAttributeValue(XMLTags.BoundaryTypeAttrTag);
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
	iterator = param.getChildren(XMLTags.OdeEquationTag, vcNamespace).iterator();
	while (iterator.hasNext()) {
		Element tempElement = (Element)iterator.next();
		OdeEquation odeEquation = getOdeEquation(tempElement, mathDesc);
		
		try {
			subDomain.addEquation( odeEquation );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding an OdeEquation to a MEmbraneSubDomain!"+" : "+e.getMessage());
		}
	}

	//process PdeEquations
	iterator = param.getChildren( XMLTags.PdeEquationTag, vcNamespace ).iterator();
	while (iterator.hasNext()) {
		Element tempElement = (Element)iterator.next();

		try {
			subDomain.addEquation( getPdeEquation(tempElement, mathDesc) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding an PdeEquation to the compartmentSubDomain " + name+" : "+e.getMessage());
		}
	}

	//Add JumpConditions
	iterator = param.getChildren(XMLTags.JumpConditionTag, vcNamespace).iterator();
	while (iterator.hasNext()) {
		Element tempElement = (Element)iterator.next();
		try {
			subDomain.addJumpCondition( getJumpCondition(tempElement, mathDesc) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding a JumpCondition to a MembraneSubDomain!"+" : "+e.getMessage());
		}
	}

	//Add the FastSystem (if any)
	Element tempElement = param.getChild(XMLTags.FastSystemTag, vcNamespace);
	if (tempElement != null) {
		subDomain.setFastSystem( getFastSystem(tempElement, mathDesc) );
	}

	//add MembraneRegionEquation
	iterator = param.getChildren(XMLTags.MembraneRegionEquationTag, vcNamespace).iterator();
	while (iterator.hasNext()) {
		tempElement = (Element)iterator.next();
		try {
			subDomain.addEquation( getMembraneRegionEquation(tempElement, mathDesc) );
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
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

	//Create new memVariable
	MemVariable memVariable = new MemVariable( name );
	
	return memVariable;
}


/**
 * This method returns a MeshSpecification object from a XML Element.
 * Creation date: (5/22/2001 12:05:21 PM)
 * @return cbit.vcell.mesh.MeshSpecification
 * @param param org.jdom.Element
 */
public MeshSpecification getMeshSpecification(Element param, Geometry geometry) throws XmlParseException {
	//*** create new MeshSpecification ***
	MeshSpecification meshSpec = new MeshSpecification(geometry);
	
	//get ISize
	Element size = param.getChild(XMLTags.SizeTag, vcNamespace);
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
public Model getModel(Element param) throws XmlParseException {
	if (param==null) {
		throw new XmlParseException("Invalid 'NULL' XML 'model' element arrived!");
	}
	//Get version, if any	
	Version version = getVersion(param.getChild(XMLTags.VersionTag, vcNamespace));
	Model newmodel = new Model(version);
	
	try {
		//Set attributes
		newmodel.setName( unMangle(param.getAttributeValue(XMLTags.NameAttrTag)) );
		//Add annotation
		String annotationText = param.getChildText(XMLTags.AnnotationTag, vcNamespace);
		if (annotationText!=null && annotationText.length()>0) {
			newmodel.setDescription(unMangle(annotationText));
		}

		// Add global parameters
		Element globalParamsElement = param.getChild(XMLTags.ModelParametersTag, vcNamespace);
		if (globalParamsElement != null) {
			ModelParameter[] modelParams = getModelParams(globalParamsElement, newmodel); 
			// add global/model param to model - done inside getModelParam by passing newModel
			newmodel.setModelParameters(modelParams);
		}
		
		//Add Species (Compounds)
		Iterator<Element> iterator = param.getChildren(XMLTags.SpeciesTag, vcNamespace).iterator();
		ArrayList<Species> speciesList = new ArrayList<Species>();
		while (iterator.hasNext()) {
			org.jdom.Element temp = (Element) iterator.next();
			speciesList.add(getSpecies(temp));
		}
		newmodel.setSpecies(speciesList.toArray(new Species[speciesList.size()]));
		//Add Structures
		LinkedList<Structure> newstructures = new LinkedList<Structure>();
		//(features)
		List<Element> children = param.getChildren(XMLTags.FeatureTag, vcNamespace);
		for (int i = 0; i < children.size(); i++) {
			newstructures.add( getFeature(children.get(i)) );
		}
		//(Membrane)
		children = param.getChildren(XMLTags.MembraneTag, vcNamespace);
		for (int i = 0; i < children.size(); i++) {
			newstructures.add( getMembrane(children.get(i), newstructures));
		}
		if (newstructures.size()>0) {
			Structure[] structarray = new Structure[newstructures.size()];
			newstructures.toArray(structarray);
			// Add all the retrieved structures
			newmodel.setStructures( structarray );			
		}

		//Add SpeciesContexts
		children = param.getChildren(XMLTags.SpeciesContextTag, vcNamespace);
		SpeciesContext[] newspeccon = new SpeciesContext[children.size()];
		for (int i=0 ; i < children.size() ; i++) {
			newspeccon[i] = getSpeciesContext(children.get(i), newmodel);
		}
		newmodel.setSpeciesContexts(newspeccon);
		//Add Reaction steps (if available)
		
		//(Simplereaction)
		// Create a varHash with reserved symbols and global parameters, if any, to pass on to Kinetics
		// must create new hash for each reaction and flux, since each kinetics uses new variables hash
		VariableHash varHash;
		iterator = param.getChildren(XMLTags.SimpleReactionTag, vcNamespace).iterator();
		ArrayList<ReactionStep> reactionStepList = new ArrayList<ReactionStep>();
		while (iterator.hasNext()) {
			varHash = new VariableHash();
			addResevedSymbols(varHash);
			org.jdom.Element temp = iterator.next();
			reactionStepList.add(getSimpleReaction(temp, newmodel, varHash));
		}
		//(fluxStep)
		iterator = param.getChildren(XMLTags.FluxStepTag, vcNamespace).iterator();
		while (iterator.hasNext()) {
			varHash = new VariableHash();
			addResevedSymbols(varHash);
			org.jdom.Element temp = iterator.next();
			reactionStepList.add(getFluxReaction(temp, newmodel, varHash));
		}
		newmodel.setReactionSteps(reactionStepList.toArray(new ReactionStep[reactionStepList.size()]));
		//Add Diagrams
		children = param.getChildren(XMLTags.DiagramTag, vcNamespace);
		if (children.size()>0) {
			Diagram[] newdiagrams = new Diagram[children.size()];
			for (int i = 0; i < children.size(); i++) {
				newdiagrams[i] = getDiagram(children.get(i), newmodel);
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
		String name = unMangle(param.getAttributeValue( XMLTags.SpeciesContextRefAttrTag ));
		java.awt.Point location = new java.awt.Point( Integer.parseInt(param.getAttributeValue(XMLTags.LocationXAttrTag)), Integer.parseInt(param.getAttributeValue(XMLTags.LocationYAttrTag)) );

		newnoderef = new NodeReference(type, name, location);
	} else if  ( tempname.equalsIgnoreCase(XMLTags.SimpleReactionShapeTag) ) {
		int type = NodeReference.SIMPLE_REACTION_NODE;
		String name = unMangle(param.getAttributeValue( XMLTags.SimpleReactionRefAttrTag ));
		java.awt.Point location = new java.awt.Point( Integer.parseInt(param.getAttributeValue(XMLTags.LocationXAttrTag)), Integer.parseInt(param.getAttributeValue(XMLTags.LocationYAttrTag)) );

		newnoderef = new NodeReference(type, name, location);
	} else if  ( tempname.equalsIgnoreCase(XMLTags.FluxReactionShapeTag) ) {
		int type = NodeReference.FLUX_REACTION_NODE;
		String name = unMangle(param.getAttributeValue( XMLTags.FluxReactionRefAttrTag ));
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
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public OdeEquation getOdeEquation(Element param, MathDescription mathDesc) throws XmlParseException {
	//get attributes
	String varname = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	//find reference in the dictionnary
	//try as a VolVariable
	Variable varref = mathDesc.getVariable(varname);
	
	//Make sure that the reference is not empty!!!
	if (varref == null) {
		throw new XmlParseException("The reference to the variable "+ varname+ " in a OdeEquation could not be resolved!");
	}	
	
	//get Initial condition
	String temp = param.getChildText(XMLTags.InitialTag, vcNamespace);
	Expression initialexp = null;
	
	if (temp!=null && temp.length()>0) {
		initialexp = unMangleExpression(temp);
	}
	
	//Get Rate condition
	temp = param.getChildText(XMLTags.RateTag, vcNamespace);
	Expression rateexp = null;
	if (temp!=null && temp.length()>0) {
		rateexp = unMangleExpression((temp));
	}
	
	//--- Create the OdeEquation object ---
	OdeEquation odeEquation = new OdeEquation(varref, initialexp, rateexp);

	//add specific solutions expressions
	String solType = param.getAttributeValue(XMLTags.SolutionTypeTag);

	if (solType.equalsIgnoreCase(XMLTags.ExactTypeTag)) {
		String solutionExp = param.getChildText(XMLTags.SolutionExpressionTag, vcNamespace);
		
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
public OutputTimeSpec getOutputTimeSpec(Element param) {
	if (param != null) {
		//get attributes
		if (param.getAttributeValue(XMLTags.KeepEveryAttrTag) != null) {
			int keepEvery = Integer.parseInt(param.getAttributeValue(XMLTags.KeepEveryAttrTag));
			int keepAtMost = Integer.parseInt(param.getAttributeValue(XMLTags.KeepAtMostAttrTag));
			return new DefaultOutputTimeSpec(keepEvery, keepAtMost);		
		} else if (param.getAttributeValue(XMLTags.OutputTimeStepAttrTag) != null) {
			double outputStep = Double.parseDouble(param.getAttributeValue(XMLTags.OutputTimeStepAttrTag));
			return new UniformOutputTimeSpec(outputStep);		
		} else if (param.getAttributeValue(XMLTags.OutputTimesAttrTag) != null) {
			String line = param.getAttributeValue(XMLTags.OutputTimesAttrTag);
			return ExplicitOutputTimeSpec.fromString(line);		
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
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	//get VolVariableRef
	String volvarName = unMangle( param.getAttributeValue(XMLTags.VolumeVariableAttrTag) );

	//*** create new OutsideVariable ***
	OutsideVariable variable = new OutsideVariable(name , volvarName);
	
	return variable;
}


/**
 * This method returns a PdeEquation from a XML element.
 * Creation date: (4/26/2001 12:11:14 PM)
 * @return cbit.vcell.math.PdeEquation
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */

public PdeEquation getPdeEquation(Element param, MathDescription mathDesc) throws XmlParseException {
    //Retrieve the variable reference
    String name = unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
    boolean bSteady = false;
    String bSteadyAttr = param.getAttributeValue(XMLTags.SteadyTag);
    if (bSteadyAttr != null && bSteadyAttr.equals("1")) {
    	bSteady = true;
    }
    Variable varref = mathDesc.getVariable(name);    
    if (varref == null) {
    	throw new XmlParseException( "The variable " + name + " for a PdeEquation, could not be resolved!");
    }
    PdeEquation pdeEquation = null;
    
    try {
        //Retrieve the initial expression
        String temp = param.getChildText(XMLTags.InitialTag, vcNamespace);
        Expression initialExp = null;
        if (temp!=null && temp.length()>0) {
	        initialExp = unMangleExpression(temp);        	
        }

        //Retrieve the Rate Expression
        temp = param.getChildText(XMLTags.RateTag, vcNamespace);
        Expression rateExp = null;
        if (temp!=null && temp.length()>0) {
        	rateExp = unMangleExpression(temp);
        }

        //Retrieve the diffusion rate expression
        temp = param.getChildText(XMLTags.DiffusionTag, vcNamespace);
        Expression difExp = null;
        if (temp!=null && temp.length()>0) {
        	difExp = unMangleExpression(temp);
        }
        
        //*** Create new PdeEquation object ****
        pdeEquation =  new PdeEquation(varref, bSteady, initialExp, rateExp, difExp);
        //***** *****

		//add specific solutions expressions
		String solType = param.getAttributeValue(XMLTags.SolutionTypeTag);

		if (solType.equalsIgnoreCase(XMLTags.ExactTypeTag)) {
			String solutionExp = param.getChildText(XMLTags.SolutionExpressionTag, vcNamespace);
			
			if (solutionExp!=null && solutionExp.length()>0) {
				Expression expression = unMangleExpression(solutionExp);			
				pdeEquation.setExactSolution( expression );	
			}
		}
        
        //Retrieve Boudaries (if any)
        Element tempelement = param.getChild(XMLTags.BoundariesTag, vcNamespace);
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
        Element velocityE = param.getChild(XMLTags.VelocityTag, vcNamespace);
        if (velocityE != null) {
	        String tempStr = null;
	        boolean dummyVel = true;
	        tempStr = velocityE.getAttributeValue(XMLTags.XAttrTag);
	        if (tempStr != null) {
	       		pdeEquation.setVelocityX(unMangleExpression(tempStr));                  //all velocity dimensions are optional.
				if (dummyVel) {
					dummyVel = false;
				}
	       	}
	        tempStr = velocityE.getAttributeValue(XMLTags.YAttrTag);
	        if (tempStr != null) {
				pdeEquation.setVelocityY(unMangleExpression(tempStr));
				if (dummyVel) {
					dummyVel = false;
				}
	        }
	        tempStr = velocityE.getAttributeValue(XMLTags.ZAttrTag);
	        if (tempStr != null) {
				pdeEquation.setVelocityZ(unMangleExpression(tempStr));
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
	String pixelClassName = unMangle(param.getAttributeValue( XMLTags.NameAttrTag));
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
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public Product getProduct(Element param, SimpleReaction reaction, Model model) throws XmlParseException {
    //retrieve the key if there is one
    KeyValue key = null;
    String keystring = param.getAttributeValue(XMLTags.KeyValueAttrTag);
    
    if (keystring != null && keystring.length()>0 && this.readKeysFlag) {
        key = new KeyValue(keystring);
    }

    String speccontref = unMangle(param.getAttributeValue(XMLTags.SpeciesContextRefAttrTag));
    SpeciesContext speccont = model.getSpeciesContext(speccontref);
    if (speccont == null) {
        throw new XmlParseException(
            "The reference to the SpecieContext "
                + speccontref
                + " for a Product could not be resolved!");
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
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public Reactant getReactant(Element param, SimpleReaction reaction, Model model) throws XmlParseException {
    //retrieve the key if there is one
    String keystring = param.getAttributeValue(XMLTags.KeyValueAttrTag);
    KeyValue key = null;

    if (keystring != null && keystring.length()>0 && this.readKeysFlag) {
        key = new KeyValue(keystring);
    }

    String speccontref = unMangle(param.getAttributeValue(XMLTags.SpeciesContextRefAttrTag));
    SpeciesContext speccont = model.getSpeciesContext(speccontref);
    if (speccont == null) {
        throw new XmlParseException(
            "The reference to the SpecieContext "
                + speccontref
                + " for a SimpleReaction could not be resolved!");
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
public ReactionSpec getReactionSpec(Element param, Model model) throws XmlParseException{
	ReactionSpec reactionspec = null;

	//retrieve the reactionstep reference
	String reactionstepname = unMangle( param.getAttributeValue(XMLTags.ReactionStepRefAttrTag) );
	ReactionStep reactionstepref = (ReactionStep)model.getReactionStep(reactionstepname);
	
	if (reactionstepref ==null) {
		throw new XmlParseException("The reference to the ReactionStep " + reactionstepname + ", could not be resolved!");
	}
	//Create the new SpeciesContextSpec
	reactionspec = new ReactionSpec(reactionstepref);

	//set the reactionMapping value
	String temp = param.getAttributeValue(XMLTags.ReactionMappingAttrTag);
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
private ArrayList<String> getReservedVars() {
	
	ArrayList<String> reservedVars = new ArrayList<String>();
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
public SimpleReaction getSimpleReaction(Element param, Model model, VariableHash varsHash) throws XmlParseException {
    //resolve reference to the  structure that it belongs to.
    String structureName = unMangle(param.getAttributeValue(XMLTags.StructureAttrTag));
    Structure structureref = (Structure) model.getStructure(structureName);
    
    if (structureref == null) {
    	throw new XmlParseException("The structure " + structureName + "could not be resolved!");
    }

    //try to get keValue information
    String keystring = param.getAttributeValue(XMLTags.KeyValueAttrTag);
    KeyValue key = null;
    
    if (keystring!=null && keystring.length()>0 && this.readKeysFlag) {
    	key = new KeyValue(keystring);
    }
        
    //---Instantiate a new Simplereaction---
    SimpleReaction simplereaction = null;
    String name = unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
    
    try {
        simplereaction = new SimpleReaction(structureref, key, name);
        simplereaction.setModel(model);
    } catch (java.beans.PropertyVetoException e) {
        e.printStackTrace();
        throw new XmlParseException("An error occurred while trying to create the simpleReaction " + name+" : "+e.getMessage());
    }
	//Annotation
//	String rsAnnotation = null;
//	String annotationText = param.getChildText(XMLTags.AnnotationTag, vcNamespace);
//	if (annotationText!=null && annotationText.length()>0) {
//		rsAnnotation = unMangle(annotationText);
//	}
//	simplereaction.setAnnotation(rsAnnotation);
	
	//set the fluxOption
	String fluxOptionString = null;
	fluxOptionString = param.getAttributeValue(XMLTags.FluxOptionAttrTag);
	
	if (fluxOptionString!=null&&fluxOptionString.length()>0){
		try {
			if (fluxOptionString.equals(XMLTags.FluxOptionElectricalOnly)){
				simplereaction.setPhysicsOptions(SimpleReaction.PHYSICS_ELECTRICAL_ONLY);
			}else if (fluxOptionString.equals(XMLTags.FluxOptionMolecularAndElectrical)){
				simplereaction.setPhysicsOptions(SimpleReaction.PHYSICS_MOLECULAR_AND_ELECTRICAL);
			}else if (fluxOptionString.equals(XMLTags.FluxOptionMolecularOnly)){
				simplereaction.setPhysicsOptions(SimpleReaction.PHYSICS_MOLECULAR_ONLY);
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
		Iterator<Element> iterator = param.getChildren(XMLTags.ReactantTag, vcNamespace).iterator();

		while (iterator.hasNext()) {
			Element temp = iterator.next();

			//Add Reactant to this SimpleReaction
			simplereaction.addReactionParticipant(getReactant(temp, simplereaction, model));
		}
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("Error adding a reactant to the reaction "+ name+" : "+e.getMessage());
	}

	//Add Products
	try {
		Iterator<Element> iterator = param.getChildren(XMLTags.ProductTag, vcNamespace).iterator();
		
		while (iterator.hasNext()) {
			Element temp = iterator.next();
			
			//Add Product to this simplereaction
			simplereaction.addReactionParticipant(getProduct(temp, simplereaction, model));
        }
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("Error adding a product to the reaction "+ name+" : "+e.getMessage());
	}

	//Add Catalyst(Modifiers)
	try {
		Iterator<Element> iterator = param.getChildren(XMLTags.CatalystTag, vcNamespace).iterator();

		while (iterator.hasNext()) {
			Element temp = iterator.next();
			simplereaction.addReactionParticipant(getCatalyst(temp, simplereaction, model));
		}
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("Error adding a catalyst to the reaction "+ name+" : "+e.getMessage());
	}
 
	//Add Kinetics
	Element tempKinet = param.getChild(XMLTags.KineticsTag, vcNamespace);

	if (tempKinet!= null) {
		simplereaction.setKinetics(getKinetics(tempKinet, simplereaction, varsHash));
	}

    return simplereaction;
}


/**
 * This method returns a Simulation object from a XML element.
 * Creation date: (4/26/2001 12:14:30 PM)
 * @return cbit.vcell.solver.Simulation
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public Simulation getSimulation(Element param, MathDescription mathDesc) throws XmlParseException {
	//retrive metadata (if any)
	SimulationVersion simulationVersion = getSimulationVersion(param.getChild(XMLTags.VersionTag, vcNamespace));
	
	//create new simulation
	Simulation simulation = null;
		
	if (simulationVersion!=null) {
		simulation = new Simulation(simulationVersion, mathDesc);
	} else {
		simulation = new Simulation(mathDesc);
	}
	
	//set attributes
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	try {
		simulation.setName(name);
		//String annotation = param.getAttributeValue(XMLTags.AnnotationAttrTag);

		//if (annotation!=null) {
			//simulation.setDescription(unMangle(annotation));
		//}
		//Add Annotation
		String annotationText = param.getChildText(XMLTags.AnnotationTag, vcNamespace);
		if (annotationText!=null && annotationText.length()>0) {
			simulation.setDescription(unMangle(annotationText));
		}
	}catch (java.beans.PropertyVetoException e) {
		throw new XmlParseException(e.getMessage());
	}

	//Retrieve MathOverrides
		simulation.setMathOverrides( getMathOverrides( param.getChild(XMLTags.MathOverridesTag, vcNamespace), simulation) );

	//Retrieve SolverTaskDescription
	try {
		simulation.setSolverTaskDescription( getSolverTaskDescription(param.getChild(XMLTags.SolverTaskDescriptionTag, vcNamespace), simulation) );
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException was fired when setting the SolverTaskDescroiption object to the Simulation object "+ name+" : "+e.getMessage());
	}
	
	Element dataProcessingInstructionsElement = param.getChild(XMLTags.DataProcessingInstructionsTag, vcNamespace);
	if (dataProcessingInstructionsElement!=null){
		String scriptName = dataProcessingInstructionsElement.getAttributeValue(XMLTags.DataProcessingScriptNameAttrTag);
		String scriptInput = dataProcessingInstructionsElement.getText();
		simulation.setDataProcessingInstructions(new DataProcessingInstructions(scriptName,scriptInput));
	}

	//Retrieve MeshEspecification (if any)
	Element tempElement = param.getChild(XMLTags.MeshSpecTag, vcNamespace);
	
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
public SimulationContext getSimulationContext(Element param, BioModel biomodel) throws XmlParseException{
	//get the attributes
	String name = unMangle(param.getAttributeValue(XMLTags.NameAttrTag)); //name
	boolean bStoch = false;
	boolean bUseConcentration = true;
	if ((param.getAttributeValue(XMLTags.StochAttrTag)!= null) && (param.getAttributeValue(XMLTags.StochAttrTag).equals("true")))
		bStoch = true;
	if(bStoch)
	{
		if((param.getAttributeValue(XMLTags.ConcentrationAttrTag)!= null) && (param.getAttributeValue(XMLTags.ConcentrationAttrTag).equals("false")))
		{
			bUseConcentration = false;
		}
	}
	//Retrieve Geometry
	Geometry newgeometry = null;
	try {
		newgeometry = getGeometry( param.getChild(XMLTags.GeometryTag, vcNamespace));
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
	Element xmlMathDescription = param.getChild(XMLTags.MathDescriptionTag, vcNamespace);
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
	Version version = getVersion( param.getChild(XMLTags.VersionTag, vcNamespace) );

	//------ Create SimContext ------
	SimulationContext newsimcontext = null;
	
	try {
		newsimcontext = new SimulationContext(biomodel.getModel(), newgeometry, newmathdesc, version, bStoch);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException("A propertyveto exception was generated when creating the new SimulationContext " + name+" : "+e.getMessage());
	}
	
	//set attributes
	try {
		newsimcontext.setName(name);
		//Add annotation
		String annotation = param.getChildText(XMLTags.AnnotationTag, vcNamespace);
		if (annotation!=null && annotation.length()>0) {
			newsimcontext.setDescription(unMangle(annotation));
		}
		//set if using concentration
		newsimcontext.setUsingConcentration(bUseConcentration);
		 
	} catch(java.beans.PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException("Exception : "+e.getMessage());
	} 
	
	String tempchar = param.getAttributeValue(XMLTags.CharacteristicSizeTag);
	if (tempchar!=null) {
		try {
			newsimcontext.setCharacteristicSize( Double.valueOf(tempchar) );
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException("A PropertyVetoException was fired when setting the CharacteristicSize "+ tempchar+" : "+e.getMessage());
		}
	}
	//
	//-Process the GeometryContext-
	//
	Element tempelement =  param.getChild(XMLTags.GeometryContextTag, vcNamespace);
	LinkedList<StructureMapping> maplist = new LinkedList<StructureMapping>();
	//Retrieve FeatureMappings
	Iterator<Element> iterator = tempelement.getChildren(XMLTags.FeatureMappingTag, vcNamespace).iterator();
	while ( iterator.hasNext() ) {
		maplist.add( getFeatureMapping((Element)(iterator.next()), newsimcontext) );
	}
	//Retrieve MembraneMappings
	iterator = tempelement.getChildren(XMLTags.MembraneMappingTag, vcNamespace).iterator();
	while (iterator.hasNext()) {
		maplist.add( getMembraneMapping((Element)(iterator.next()), newsimcontext) );
	}
	// Add these mappings to the internal geometryContext of this simcontext
	StructureMapping[] structarray = new StructureMapping[maplist.size()];
	maplist.toArray(structarray);
	try {
		newsimcontext.getGeometryContext().setStructureMappings(structarray);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException("A PopertyVetoException was fired when trying to set the StructureMappings array to the Geometrycontext of the SimContext "+ name+" : "+e.getMessage());
	}
	//
	//-Process the ReactionContext-
	//
	tempelement =  param.getChild(XMLTags.ReactionContextTag, vcNamespace);
	// Retrieve ReactionSpecs
	List<Element> children = tempelement.getChildren(XMLTags.ReactionSpecTag, vcNamespace);
	if (children.size()!=0) {
		if (children.size()!= biomodel.getModel().getReactionSteps().length) {
			throw new XmlParseException("The number of reactions is not consistent.\n"+"Model reactions="+biomodel.getModel().getReactionSteps().length+", Reaction specs="+children.size());
		}
		//*NOTE: Importing a model from other languages does not generates reaction specs.
		// A more robust code will read the reactions in the source file and replace the ones created by the default by the VirtualCell framework.
		ReactionSpec reactionSpecs[] = new ReactionSpec[children.size()];
		for (int i=0;i<children.size();i++){
			reactionSpecs[i] = getReactionSpec(children.get(i), biomodel.getModel());
		}
		try {
			newsimcontext.getReactionContext().setReactionSpecs(reactionSpecs);
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException("A PropertyVetoException occurred while setting the ReactionSpecs to the SimContext " + name+" : "+e.getMessage());
		}
	}
	
	// Retrieve SpeciesContextSpecs
	children = tempelement.getChildren(XMLTags.SpeciesContextSpecTag, vcNamespace);
	getSpeciesContextSpecs(children, newsimcontext.getReactionContext(), biomodel.getModel());

	// Retrieve output functions
	Element outputFunctionsElement = param.getChild(XMLTags.OutputFunctionsTag, vcNamespace);
	if (outputFunctionsElement != null) {
		ArrayList<AnnotatedFunction> outputFunctions = getOutputFunctions(outputFunctionsElement); 
		try {
			// construct OutputFnContext from mathDesc in newSimContext and add output functions that were read in from XML.
			OutputFunctionContext outputFnContext = newsimcontext.getOutputFunctionContext();
			for (AnnotatedFunction outputFunction : outputFunctions) {
				outputFnContext.addOutputFunction(outputFunction);
			}
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException(e.getMessage());		
		}
	}

	//Retrieve Electrical context
	org.jdom.Element electElem = param.getChild(XMLTags.ElectricalContextTag, vcNamespace);
	//this information is optional!
	if (electElem != null) {
		if (electElem.getChild(XMLTags.ClampTag, vcNamespace)!=null) {
			//read clamp
			ElectricalStimulus[] electArray = new ElectricalStimulus[1];
			electArray[0] = getElectricalStimulus(electElem.getChild(XMLTags.ClampTag, vcNamespace), newsimcontext);
			
			try {
				newsimcontext.setElectricalStimuli(electArray);
			} catch (java.beans.PropertyVetoException e) {
				e.printStackTrace(System.out);
				throw new XmlParseException(e.getMessage());
			}
		}
		
		//read ground electrode
		if (electElem.getChild(XMLTags.ElectrodeTag, vcNamespace)!=null) {
			Electrode groundElectrode = getElectrode(electElem.getChild(XMLTags.ElectrodeTag, vcNamespace), newsimcontext);
			
			try{
				newsimcontext.setGroundElectrode(groundElectrode);
			} catch (java.beans.PropertyVetoException e) {
				e.printStackTrace(System.out);
				throw new XmlParseException(e.getMessage());
			}
		}
	}	

	// Retrieve (bio)events and add to simContext
	tempelement = param.getChild(XMLTags.BioEventsTag, vcNamespace);
	if(tempelement != null){
		BioEvent[] bioEvents = getBioEvents(newsimcontext, tempelement);
		try {
			newsimcontext.setBioEvents(bioEvents);
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error adding events to simulationContext : " + e.getMessage());
		}
	}

	org.jdom.Element analysisTaskListElement = param.getChild(XMLTags.AnalysisTaskListTag, vcNamespace);
	if (analysisTaskListElement!=null){
		children = analysisTaskListElement.getChildren(XMLTags.ParameterEstimationTaskTag, vcNamespace);
		if (children.size()!=0) {
			Vector<ParameterEstimationTask> analysisTaskList = new Vector<ParameterEstimationTask>();
			for (int i = 0; i < children.size(); i++){
				Element parameterEstimationTaskElement = (Element)children.get(i);
				try {
					ParameterEstimationTask parameterEstimationTask = ParameterEstimationTaskXMLPersistence.getParameterEstimationTask(parameterEstimationTaskElement,newsimcontext);
					analysisTaskList.add(parameterEstimationTask);
				}catch (Exception e){
					e.printStackTrace(System.out);
					throw new XmlParseException("An Exception occurred when parsing AnalysisTasks of SimContext " + name+" : "+e.getMessage());
				}
			}
			try {
				AnalysisTask[] analysisTasks = (AnalysisTask[])BeanUtils.getArray(analysisTaskList,AnalysisTask.class);
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
	String name = unMangle(xmlVersion.getAttributeValue(XMLTags.NameAttrTag));
	//*key
	String temp = xmlVersion.getAttributeValue(XMLTags.KeyValueAttrTag);
	KeyValue key = new KeyValue( temp );
	//*owner
	Element tempElement = xmlVersion.getChild(XMLTags.OwnerTag, vcNamespace);
	User owner = new User(unMangle(tempElement.getAttributeValue(XMLTags.NameAttrTag)), new KeyValue(tempElement.getAttributeValue(XMLTags.IdentifierAttrTag)));	
	//*access
	GroupAccess groupAccess = getGroupAccess(xmlVersion.getChild(XMLTags.GroupAccessTag, vcNamespace));
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
	tempElement = xmlVersion.getChild(XMLTags.ParentSimRefTag, vcNamespace);
	if (tempElement!=null){
		parentSimRefKey = new KeyValue(tempElement.getAttributeValue(XMLTags.KeyValueAttrTag));
	}

	//*Annotation
	String annotation = null;
	String annotationText = xmlVersion.getChildText(XMLTags.AnnotationTag, vcNamespace);
	if (annotationText!= null && annotationText.length()>0) {
		annotation = unMangle(annotationText);
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
public SolverTaskDescription getSolverTaskDescription(Element param, Simulation simulation) throws XmlParseException{
	//*** create new SolverTaskDescription ***
	SolverTaskDescription solverTaskDesc = new SolverTaskDescription(simulation);
	//Added July 22nd, 2007, used as condition for stochSimOptions or stochHybridOprtions
	SolverDescription sd = null;
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
	Element sensparamElement = param.getChild(XMLTags.ConstantTag, vcNamespace);
	Constant sensitivityparam = null;
	
	if (sensparamElement!=null) {
		sensitivityparam = getConstant(sensparamElement);
	}	

	//set Attributes
	try {
		//set solver
		sd = SolverDescription.fromDatabaseName(solverName);
		solverTaskDesc.setSolverDescription(sd);
		
		if ( taskType.equalsIgnoreCase(XMLTags.UnsteadyTag) ) {
			solverTaskDesc.setTaskType(SolverTaskDescription.TASK_UNSTEADY );
		} else if (taskType.equalsIgnoreCase( XMLTags.SteadyTag)) {
			solverTaskDesc.setTaskType(SolverTaskDescription.TASK_STEADY );
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
		solverTaskDesc.setTimeBounds( getTimeBounds(param.getChild(XMLTags.TimeBoundTag, vcNamespace)) );
		//get TimeStep
		solverTaskDesc.setTimeStep( getTimeStep(param.getChild(XMLTags.TimeStepTag, vcNamespace)) );
		//get ErrorTolerance
		solverTaskDesc.setErrorTolerance( getErrorTolerance(param.getChild(XMLTags.ErrorToleranceTag, vcNamespace)) );
		//get StochSimOptions
		if(simulation != null && simulation.getMathDescription()!= null)
		{
			if( simulation.getMathDescription().isStoch() && param.getChild(XMLTags.StochSimOptionsTag, vcNamespace) != null)
			{   //Amended July 22nd, 2007 to read either stochSimOptions or stochHybridOptions
				if(sd != null && sd.equals(SolverDescription.StochGibson))
					solverTaskDesc.setStochOpt(getStochSimOptions(param.getChild(XMLTags.StochSimOptionsTag, vcNamespace),false));
				else 
					solverTaskDesc.setStochOpt(getStochSimOptions(param.getChild(XMLTags.StochSimOptionsTag, vcNamespace),true));
			}
		}
		//get OutputOptions
		if (keepEvery != -1) {
			solverTaskDesc.setOutputTimeSpec(new DefaultOutputTimeSpec(keepEvery,keepAtMost));
		}
		OutputTimeSpec ots = getOutputTimeSpec(param.getChild(XMLTags.OutputOptionsTag, vcNamespace));
		if (ots != null) {
			solverTaskDesc.setOutputTimeSpec(getOutputTimeSpec(param.getChild(XMLTags.OutputOptionsTag, vcNamespace)));
		}
		//set SensitivityParameter
		solverTaskDesc.setSensitivityParameter(sensitivityparam);
		
		// set StopAtSpatiallyUniform
		Element stopSpatiallyElement = param.getChild(XMLTags.StopAtSpatiallyUniform, vcNamespace);
		if (stopSpatiallyElement != null) {
			Element errTolElement = stopSpatiallyElement.getChild(XMLTags.ErrorToleranceTag, vcNamespace);
			if (errTolElement != null) {
				solverTaskDesc.setStopAtSpatiallyUniformErrorTolerance(getErrorTolerance(errTolElement));
			}
		}
		
		String runParameterScanSeriallyAttributeValue = param.getAttributeValue(XMLTags.RunParameterScanSerially);
		if (runParameterScanSeriallyAttributeValue != null) {
			solverTaskDesc.setSerialParameterScan(new Boolean(runParameterScanSeriallyAttributeValue).booleanValue());
		}
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(e.getMessage());
	}
		
	return solverTaskDesc;
}

public ModelParameter[] getModelParams(Element globalParams, Model model) throws XmlParseException {
	Iterator<Element> globalsIterator = globalParams.getChildren(XMLTags.ParameterTag, vcNamespace).iterator();
	Vector<ModelParameter> modelParamsVector = new Vector<ModelParameter>();
	while (globalsIterator.hasNext()) {
		org.jdom.Element paramElement = (Element) globalsIterator.next();
		//get its attributes : name, role and unit definition
		String glParamName = unMangle( paramElement.getAttributeValue(XMLTags.NameAttrTag) );
		String role = paramElement.getAttributeValue(XMLTags.ParamRoleAttrTag);
		int glParamRole = -1;
		if (role.equals(XMLTags.ParamRoleUserDefinedTag)) {
			glParamRole = Model.ROLE_UserDefined;
		} else {
			throw new RuntimeException("unknown type of model parameter (not user-defined)");
		}
		String unitSymbol = paramElement.getAttributeValue(XMLTags.VCUnitDefinitionAttrTag);
		VCUnitDefinition glParamUnit = null;
		if (unitSymbol != null) {
			glParamUnit = VCUnitDefinition.getInstance(unitSymbol);
		}
		//get parameter contents : expression; annotation, if any.
		String glParamExpStr = paramElement.getText();
		Expression glParamExp = unMangleExpression(glParamExpStr);
		String glParamAnnotation = null;
		String annotationText = paramElement.getChildText(XMLTags.AnnotationTag, vcNamespace);
		if (annotationText != null && annotationText.length() > 0) {
			glParamAnnotation = unMangle(annotationText);
		}
		
		//create new global parameter
		ModelParameter newGlParam = model.new ModelParameter(glParamName, glParamExp, glParamRole, glParamUnit);
		newGlParam.setModelParameterAnnotation(glParamAnnotation);
		modelParamsVector.add(newGlParam);
	}
	
	return ((ModelParameter[])BeanUtils.getArray(modelParamsVector, ModelParameter.class));
}

public ArrayList<AnnotatedFunction> getOutputFunctions(Element outputFunctionsElement) throws XmlParseException {
	Iterator<Element> outputFnsIterator = outputFunctionsElement.getChildren(XMLTags.AnnotatedFunctionTag, vcNamespace).iterator();
	ArrayList<AnnotatedFunction> outputFunctions = new ArrayList<AnnotatedFunction>();
	while (outputFnsIterator.hasNext()) {
		org.jdom.Element observableElement = (Element) outputFnsIterator.next();
		AnnotatedFunction func = getOutputFunction(observableElement);
		outputFunctions.add(func);
	}
	return (outputFunctions);
}

/**
 * This method creates a Specie (Compound) object from an XML Element.
 * Creation date: (3/15/2001 12:57:43 PM)
 * @return cbit.vcell.model.Species
 * @param param org.jdom.Element
 */
public Species getSpecies(Element param) throws XmlParseException {
	//get its data
	String specieName = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String specieAnnotation = null;

	//the Annotation paramater can be optional
	//String temp = param.getAttributeValue(XMLTags.AnnotationAttrTag);
	//if (temp!=null && temp.length()!=0) {
		//specieAnnotation = unMangle(temp);
	//}
	String annotationText = param.getChildText(XMLTags.AnnotationTag, vcNamespace);
	if (annotationText!=null && annotationText.length()>0) {
		specieAnnotation = unMangle(annotationText);
	}
	//create new Specie
	Species newspecie = new Species( specieName, specieAnnotation);
	
	//Try to read the DBSpecie data
	Element dbspecieElement = param.getChild(XMLTags.DBSpeciesTag, vcNamespace);
	
	if (dbspecieElement!=null && this.readKeysFlag) {
		//read the data
		try {
			newspecie.setDBSpecies(getDBSpecies(dbspecieElement));
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}
	
	return newspecie;
}


/**
 * This method returns a Speciecontext object from a XML Element.
 * Creation date: (4/16/2001 6:32:23 PM)
 * @return cbit.vcell.model.SpeciesContext
 * @param param org.jdom.Element
 */
public SpeciesContext getSpeciesContext(Element param, Model model) throws XmlParseException{
	//retrieve its information
	String name = unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	String hasOverrideString = param.getAttributeValue(XMLTags.HasOverrideAttrTag);
	boolean bHasOverride = true; // default (safety) is name has been overridden
	if (hasOverrideString!=null && hasOverrideString.length()>0){
		bHasOverride = Boolean.valueOf(hasOverrideString).booleanValue();
	}
	String speciesName = unMangle(param.getAttributeValue(XMLTags.SpeciesRefAttrTag));
	Species specieref= (Species)model.getSpecies(speciesName);	
	if (specieref == null) {
		throw new XmlParseException("The Species " + speciesName + "could not be resolved!");
	}
	
	String structureName = unMangle(param.getAttributeValue(XMLTags.StructureAttrTag));
	Structure structureref = (Structure)model.getStructure(structureName);	
	if (structureref == null) {
		//the structure coul not be retrieved, so throw an exception!
		throw new XmlParseException("The Structure " + structureName + "could not be resolved!");
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
	
	return speciecontext;
}


/**
 * This method returns a SpeciesContextSpec object from a XML representation.
 * Creation date: (4/26/2001 4:14:01 PM)
 * @return cbit.vcell.mapping.SpeciesContextSpec
 * @param param org.jdom.Element
 */
public void getSpeciesContextSpecs(List<Element> scsChildren, ReactionContext rxnContext, Model model) throws XmlParseException{
	for (int i = 0; i < scsChildren.size(); i++) {
		Element scsElement = scsChildren.get(i); 
		SpeciesContextSpec specspec = null;
		//Get Atributes
		String speccontname = unMangle( scsElement.getAttributeValue(XMLTags.SpeciesContextRefAttrTag) );
		boolean constant = Boolean.valueOf(scsElement.getAttributeValue(XMLTags.ForceConstantAttrTag)).booleanValue();
		boolean enabledif = Boolean.valueOf(scsElement.getAttributeValue(XMLTags.EnableDiffusionAttrTag)).booleanValue();
		
		//Retrieve reference
		SpeciesContext specref = model.getSpeciesContext(speccontname);
		if (specref == null) {
			throw new XmlParseException("The SpeciesContext " + speccontname +" refrence could not be resolved!");
		}
		
		// get SpeciesContextSpec from reactionContext & specRef
	 	specspec = rxnContext.getSpeciesContextSpec(specref);
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
		String tempCon = scsElement.getChildText(XMLTags.InitialConcentrationTag, vcNamespace);
		String tempAmt = scsElement.getChildText(XMLTags.InitialAmountTag, vcNamespace);
		String temp = scsElement.getChildText(XMLTags.InitialTag, vcNamespace);
		try {
			if(temp != null)//old model
			{
				Expression expression = unMangleExpression(temp);
				specspec.getInitialConcentrationParameter().setExpression(expression);
				specspec.getInitialCountParameter().setExpression(null);
			}	
			else //new model
			{
				if(tempCon != null)//use concentration as initial condition
				{
					Expression expression = unMangleExpression(tempCon);
					specspec.getInitialConcentrationParameter().setExpression(expression);
					specspec.getInitialCountParameter().setExpression(null);
				}
				else if(tempAmt != null)//use number of particles as initial condition
				{
					Expression expression = unMangleExpression(tempAmt);
					specspec.getInitialCountParameter().setExpression(expression);
					specspec.getInitialConcentrationParameter().setExpression(null);
				}
				else
				{
					throw new XmlParseException("Unrecognizable initial condition when parsing VCML file.");
				}
			}
		
	//		Expression expression = unMangleExpression(temp);
	//		specspec.getInitialConditionParameter().setExpression(expression);
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("An expressionException was fired when setting the InitilaconditionExpression "+ temp + ", for a SpeciesContextSpec!"+" : "+e.getMessage());
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
		//diffusion (if there is no diffusion information skip it)
		Element xmlDiffusionElement = scsElement.getChild(XMLTags.DiffusionTag, vcNamespace);
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
		Element tempElement = scsElement.getChild(XMLTags.BoundariesTag, vcNamespace);
		if (tempElement != null) {
			try {
				//Xm
				temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueXm);
				if (temp != null) {
					specspec.getBoundaryXmParameter().setExpression(new Expression(unMangle(temp)));
				}
				//Xp
				temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueXp);
				if (temp != null) {
					specspec.getBoundaryXpParameter().setExpression(new Expression(unMangle(temp)));
				}
				//Ym
				temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueYm);
				if (temp != null) {
					specspec.getBoundaryYmParameter().setExpression(new Expression(unMangle(temp)));
				}
				//Yp
				temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueYp);
				if (temp != null) {
					specspec.getBoundaryYpParameter().setExpression(new Expression(unMangle(temp)));
				}
				//Zm
				temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueZm);
				if (temp != null) {
					specspec.getBoundaryZmParameter().setExpression(new Expression(unMangle(temp)));
				}
				//Zp
				temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueZp);
				if (temp != null) {
					specspec.getBoundaryZpParameter().setExpression(new Expression(unMangle(temp)));
				}
			} catch (ExpressionException e) {
				e.printStackTrace();
				throw new XmlParseException("An ExpressionException was fired when Setting the boundary Expression: " + unMangle(temp)+" : "+e.getMessage());
			} catch (java.beans.PropertyVetoException e) {
				e.printStackTrace();
				throw new XmlParseException(e.getMessage());
			}
		}
		
	    // Get Velocities if any
	    Element velocityE = scsElement.getChild(XMLTags.VelocityTag, vcNamespace);
	    if (velocityE != null) {
	        String tempStr = null;
	        boolean dummyVel = true;
	        try {
				tempStr = velocityE.getAttributeValue(XMLTags.XAttrTag);
				if (tempStr != null) {
					specspec.getVelocityXParameter().setExpression(unMangleExpression(tempStr));       //all velocity dimensions are optional.
					if (dummyVel) {
						dummyVel = false;
					}
				}
				tempStr = velocityE.getAttributeValue(XMLTags.YAttrTag);
				if (tempStr != null) {
					specspec.getVelocityYParameter().setExpression(unMangleExpression(tempStr));
					if (dummyVel) {
						dummyVel = false;
					}
				}
				tempStr = velocityE.getAttributeValue(XMLTags.ZAttrTag);
				if (tempStr != null) {
					specspec.getVelocityZParameter().setExpression(unMangleExpression(tempStr));
					if (dummyVel) {
						dummyVel = false;
					}
				}
			} catch (PropertyVetoException e) {
				e.printStackTrace();
				throw new XmlParseException("Error setting Velocity parameter for '" + specspec.getSpeciesContext().getName() + "'.\n" + e.getMessage());
			} catch (ExpressionException e) {
				e.printStackTrace();
				throw new XmlParseException("Error setting Velocity parameter for '" + specspec.getSpeciesContext().getName() + "'.\n" + e.getMessage());
			}
	        if (dummyVel) {
	        	throw new XmlParseException("Void Velocity element found under PDE for: " + specspec.getSpeciesContext().getName());
	    	} 
	    }
	}
}

/**
 * This method returns a TimeStep object from a XML Element.
 * Creation date: (5/22/2001 11:45:33 AM)
 * @return cbit.vcell.solver.TimeStep
 * @param param org.jdom.Element
 */
public StochSimOptions getStochSimOptions(Element param, boolean isHybrid) {
	//get attributes
	boolean isUseCustomSeed  = Boolean.parseBoolean( param.getAttributeValue(XMLTags.UseCustomSeedAttrTag) );
	int customSeed = 0;
	if(isUseCustomSeed)
		customSeed = Integer.parseInt( param.getAttributeValue(XMLTags.CustomSeedAttrTag) );
	int numOfTrials = Integer.parseInt( param.getAttributeValue(XMLTags.NumberOfTrialAttrTag) );
	// Amended July 22nd,2007 to add StochHybridOptions
	if(isHybrid)
	{
		if(param.getAttributeValue(XMLTags.HybridEpsilonAttrTag) != null &&
		   param.getAttributeValue(XMLTags.HybridLambdaAttrTag) != null &&
		   param.getAttributeValue(XMLTags.HybridMSRToleranceAttrTag) !=null &&
		   param.getAttributeValue(XMLTags.HybridSDEToleranceAttrTag) !=null )
		{
			double epsilon = Double.parseDouble( param.getAttributeValue(XMLTags.HybridEpsilonAttrTag) );
			double lambda = Double.parseDouble( param.getAttributeValue(XMLTags.HybridLambdaAttrTag) );
			double MSRTolerance = Double.parseDouble( param.getAttributeValue(XMLTags.HybridMSRToleranceAttrTag) );
			double sDETDolerance = Double.parseDouble( param.getAttributeValue(XMLTags.HybridSDEToleranceAttrTag) );
			//**** create a new StochHybridOptions object and return ****
			return new StochHybridOptions(isUseCustomSeed, customSeed, numOfTrials, epsilon, lambda, MSRTolerance, sDETDolerance);
		}
	}
	//**** create new StochSimOptions object and return ****
	return new StochSimOptions(isUseCustomSeed, customSeed, numOfTrials);
}


/**
 * This method returns a Stochasitc volumn variable from a XML element.
 * Creation date: (7/24/2006 5:05:51 PM)
 * @return cbit.vcell.math.StochVolVariable
 * @param param org.jdom.Element
 */
public StochVolVariable getStochVolVariable(Element param) 
{
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	//-- create new StochVolVariable object
	StochVolVariable stochVar = new StochVolVariable(name);

	return stochVar;
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
public TimeBounds getTimeBounds(Element param) {
	//get Attributes
	double start = Double.parseDouble( param.getAttributeValue(XMLTags.StartTimeAttrTag) );
	double end = Double.parseDouble( param.getAttributeValue(XMLTags.EndTimeAttrTag) );

	//*** create new TimeBounds object ****
	TimeBounds timeBounds = new TimeBounds(start, end);
	
	return timeBounds;
}


/**
 * This method returns a TimeStep object from a XML Element.
 * Creation date: (5/22/2001 11:45:33 AM)
 * @return cbit.vcell.solver.TimeStep
 * @param param org.jdom.Element
 */
public TimeStep getTimeStep(Element param) {
	//get attributes
	double min = Double.parseDouble( param.getAttributeValue(XMLTags.MinTimeAttrTag) );
	double def = Double.parseDouble( param.getAttributeValue(XMLTags.DefaultTimeAttrTag) );
	double max = Double.parseDouble( param.getAttributeValue(XMLTags.MaxTimeAttrTag) );

	//**** create new TimeStep object ****
	TimeStep timeStep = new TimeStep(min, def, max);
	
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
private void addResevedSymbols(VariableHash varHash) throws XmlParseException {

	//
	// add constants that may be used in kinetics.
	//
	try {
		// add reserved symbols
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
}

/**
 * This method return a VarIniCondition object from a XML element.
 * Creation date: (7/24/2006 5:26:05 PM)
 * @return cbit.vcell.math.VarIniCondition
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public VarIniCondition getVarIniCondition(Element param, MathDescription md) throws XmlParseException, MathException, ExpressionException
{
	//retrieve values
	Expression exp = unMangleExpression(param.getText());
	
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	Variable var = md.getVariable(name);
	if (var == null){
		throw new MathFormatException("variable "+name+" not defined");
	}	
	if (!(var instanceof StochVolVariable)){
		throw new MathFormatException("variable "+name+" not a Stochastic Volume Variable");
	}
	try {
		VarIniCondition varIni= new VarIniCondition(var,exp);
		return varIni;		
	} catch (Exception e){e.printStackTrace();}
	
	return null;	
}


/**
 * This method returns a VCIMage object from a XML representation.
 * Creation date: (3/16/2001 3:41:24 PM)
 * @return VCImage
 * @param param org.jdom.Element
 */
public VCImage getVCImage(Element param, Extent extent) throws XmlParseException{
	//try to get metadata(version)
	Version version = getVersion(param.getChild(XMLTags.VersionTag, vcNamespace));
	
	//get the attributes
	Element tempelement= param.getChild(XMLTags.ImageDataTag, vcNamespace);
	int aNumX = Integer.parseInt( tempelement.getAttributeValue(XMLTags.XAttrTag) );
	int aNumY = Integer.parseInt( tempelement.getAttributeValue(XMLTags.YAttrTag) );
	int aNumZ = Integer.parseInt( tempelement.getAttributeValue(XMLTags.ZAttrTag) );

	//getpixels
	String temp = tempelement.getText();
	byte[] data = Hex.toBytes(temp); //decode

	//create the VCImage object
	VCImageCompressed newimage = null;	
	try { 
		newimage = new VCImageCompressed( version, data, extent, aNumX, aNumY, aNumZ);
	} catch(ImageException e) {
		e.printStackTrace();
		throw new XmlParseException("An imageException occurred while trying to create a VCImage!"+" : "+e.getMessage());
	}
	//set attributes
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	try {
		newimage.setName(name);
		//String annotation = param.getAttributeValue(XMLTags.AnnotationAttrTag);
		//if (annotation!=null) {
			//newimage.setDescription(unMangle(annotation));
		//}

		//read the annotation
		String annotation = param.getChildText(XMLTags.AnnotationTag, vcNamespace);
		if (annotation!=null && annotation.length()>0) {
			newimage.setDescription(unMangle(annotation));
		}

	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException(e.getMessage());
	}

	//get PixelClasses
	List<Element> pixelClassList = param.getChildren(XMLTags.PixelClassTag, vcNamespace);

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
	String name = unMangle(xmlVersion.getAttributeValue(XMLTags.NameAttrTag));
	//*key
	String temp = xmlVersion.getAttributeValue(XMLTags.KeyValueAttrTag);
	KeyValue key = new KeyValue( temp );
	//*owner
	Element tempElement = xmlVersion.getChild(XMLTags.OwnerTag, vcNamespace);
	User owner = new User(unMangle(tempElement.getAttributeValue(XMLTags.NameAttrTag)), new KeyValue(tempElement.getAttributeValue(XMLTags.IdentifierAttrTag)));	
	//*access
	GroupAccess groupAccess = getGroupAccess(xmlVersion.getChild(XMLTags.GroupAccessTag, vcNamespace));
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
	String annotationText = xmlVersion.getChildText(XMLTags.AnnotationTag, vcNamespace);
	if (annotationText!= null && annotationText.length()>0) {
		annotation = unMangle(annotationText);
	}

	//Create and return the version object
	return new Version(key, name, owner, groupAccess, branchpointref, branchId, date, flag, annotation);
}


/**
 * This method returns a VolumeRegionEquation from a XML Element.
 * Creation date: (5/17/2001 3:52:40 PM)
 * @return cbit.vcell.math.VolumeRegionEquation
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public VolumeRegionEquation getVolumeRegionEquation(Element param, MathDescription mathDesc) throws XmlParseException {
	//get attributes
	String varname = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	//try a VolumeRegionVariable
	VolumeRegionVariable varref = (VolumeRegionVariable)mathDesc.getVariable(varname);
	if (varref == null) {
		throw new XmlParseException("The reference to the VolumeRegion variable "+ varname+ " could not be resolved!");
	}

	//get Initial condition
	String temp = param.getChildText(XMLTags.InitialTag, vcNamespace);
	Expression exp = unMangleExpression( temp );
	// ** Create the Equation **
	VolumeRegionEquation volRegEq = new VolumeRegionEquation(varref,exp);

	//set the Uniform Rate
	temp = param.getChildText(XMLTags.UniformRateTag, vcNamespace);
	exp = unMangleExpression( temp );
	volRegEq.setUniformRateExpression(exp);

	//Set the Volume Rate
	temp = param.getChildText(XMLTags.VolumeRateTag, vcNamespace);
	exp = unMangleExpression( temp );
	volRegEq.setVolumeRateExpression(exp);
	
	//get ExactSolution (if any)
/*	temp = param.getChildText(XMLTags.ExactTag);
	if (temp !=null) {
		try {
			Expression expression = new Expression( unMangle( temp) );
			odeEquation.setExactSolution( expression);			
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("An ExpressionException was fired when creating the expression: "+ unMangle(temp)+" : "+e.getMessage());
		}
	}
	//get ConstructedSolution (if any)
	temp = param.getChildText(XMLTags.ConstructedTag);
	if (temp != null) {
		try {
			Expression expression = new Expression(unMangle(temp));
			odeEquation.setConstructedSolution( expression );
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("An ExpressionException was fired when creating the expression: "+ unMangle(temp) +" : "+e.getMessage());
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
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

	//-- create new VolumeRegionVariable object
	VolumeRegionVariable volRegVariable = new VolumeRegionVariable( name );

	return volRegVariable;
}


/**
 * This method returns a VolVariable object from a XML Element.
 * Creation date: (5/16/2001 2:56:34 PM)
 * @return cbit.vcell.math.VolVariable
 * @param param org.jdom.Element
 */
public VolVariable getVolVariable(Element param) {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );

	//-- create new VolVariable object
	VolVariable volVariable = new VolVariable( name );

	return volVariable;
}

}