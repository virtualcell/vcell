/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.xml;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.function.Consumer;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.Namespace;
import org.vcell.chombo.ChomboSolverSpec;
import org.vcell.chombo.RefinementRoi;
import org.vcell.chombo.RefinementRoi.RoiType;
import org.vcell.chombo.TimeInterval;
import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.NetworkConstraints;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.persistence.PathwayReaderBiopax3;
import org.vcell.pathway.persistence.RDFXMLContext;
import org.vcell.relationship.RelationshipModel;
import org.vcell.relationship.persistence.RelationshipReader;
import org.vcell.sbml.vcell.StructureSizeSolver;
import org.vcell.util.BeanUtils;
import org.vcell.util.Commented;
import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.GenericUtils;
import org.vcell.util.Hex;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.GroupAccessAll;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.GroupAccessSome;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
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
import cbit.vcell.biomodel.meta.xml.rdf.XMLRDF;
import cbit.vcell.data.DataContext;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.DataSymbol.DataSymbolType;
import cbit.vcell.data.FieldDataSymbol;
import cbit.vcell.dictionary.BoundCompound;
import cbit.vcell.dictionary.BoundEnzyme;
import cbit.vcell.dictionary.BoundProtein;
import cbit.vcell.dictionary.CompoundInfo;
import cbit.vcell.dictionary.EnzymeInfo;
import cbit.vcell.dictionary.EnzymeRef;
import cbit.vcell.dictionary.FormalCompound;
import cbit.vcell.dictionary.FormalEnzyme;
import cbit.vcell.dictionary.FormalProtein;
import cbit.vcell.dictionary.ProteinInfo;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.CSGHomogeneousTransformation;
import cbit.vcell.geometry.CSGNode;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.CSGPrimitive;
import cbit.vcell.geometry.CSGPrimitive.PrimitiveType;
import cbit.vcell.geometry.CSGPseudoPrimitive;
import cbit.vcell.geometry.CSGRotation;
import cbit.vcell.geometry.CSGScale;
import cbit.vcell.geometry.CSGSetOperator;
import cbit.vcell.geometry.CSGSetOperator.OperatorType;
import cbit.vcell.geometry.CSGTranslation;
import cbit.vcell.geometry.CompartmentSubVolume;
import cbit.vcell.geometry.ControlPointCurve;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.GeometryUnitSystem;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.Line;
import cbit.vcell.geometry.SampledCurve;
import cbit.vcell.geometry.Spline;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mapping.AssignmentRule;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.BioEvent.BioEventParameterType;
import cbit.vcell.mapping.BioEvent.TriggerType;
import cbit.vcell.mapping.CurrentDensityClampStimulus;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.Electrode;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.MicroscopeMeasurement;
import cbit.vcell.mapping.MicroscopeMeasurement.ConvolutionKernel;
import cbit.vcell.mapping.MicroscopeMeasurement.GaussianConvolutionKernel;
import cbit.vcell.mapping.MicroscopeMeasurement.ProjectionZKernel;
import cbit.vcell.mapping.ParameterContext;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.ParameterContext.ParameterRoleEnum;
import cbit.vcell.mapping.RateRule;
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.ReactionRuleSpec;
import cbit.vcell.mapping.ReactionRuleSpec.ReactionRuleMappingType;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.SimulationContextParameter;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.TotalCurrentClampStimulus;
import cbit.vcell.mapping.VoltageClampStimulus;
import cbit.vcell.mapping.spatial.PointObject;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityCategory;
import cbit.vcell.mapping.spatial.SurfaceRegionObject;
import cbit.vcell.mapping.spatial.VolumeRegionObject;
import cbit.vcell.mapping.spatial.processes.PointKinematics;
import cbit.vcell.mapping.spatial.processes.PointLocation;
import cbit.vcell.mapping.spatial.processes.SpatialProcess;
import cbit.vcell.mapping.spatial.processes.SpatialProcess.SpatialProcessParameterType;
import cbit.vcell.mapping.spatial.processes.SurfaceKinematics;
import cbit.vcell.mapping.spatial.processes.VolumeKinematics;
import cbit.vcell.math.Action;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.ComputeCentroidComponentEquation;
import cbit.vcell.math.ComputeCentroidComponentEquation.CentroidComponent;
import cbit.vcell.math.ComputeMembraneMetricEquation;
import cbit.vcell.math.ComputeMembraneMetricEquation.MembraneMetricComponent;
import cbit.vcell.math.ComputeNormalComponentEquation;
import cbit.vcell.math.ComputeNormalComponentEquation.NormalComponent;
import cbit.vcell.math.Constant;
import cbit.vcell.math.ConvolutionDataGenerator;
import cbit.vcell.math.ConvolutionDataGenerator.ConvolutionDataGeneratorKernel;
import cbit.vcell.math.ConvolutionDataGenerator.GaussianConvolutionDataGeneratorKernel;
import cbit.vcell.math.Distribution;
import cbit.vcell.math.Event;
import cbit.vcell.math.Event.Delay;
import cbit.vcell.math.Event.EventAssignment;
import cbit.vcell.math.ExplicitDataGenerator;
import cbit.vcell.math.FastInvariant;
import cbit.vcell.math.FastRate;
import cbit.vcell.math.FastSystem;
import cbit.vcell.math.FilamentRegionVariable;
import cbit.vcell.math.FilamentSubDomain;
import cbit.vcell.math.FilamentVariable;
import cbit.vcell.math.Function;
import cbit.vcell.math.GaussianDistribution;
import cbit.vcell.math.InsideVariable;
import cbit.vcell.math.InteractionRadius;
import cbit.vcell.math.JumpCondition;
import cbit.vcell.math.JumpProcess;
import cbit.vcell.math.JumpProcessRateDefinition;
import cbit.vcell.math.MacroscopicRateConstant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathFormatException;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneParticleVariable;
import cbit.vcell.math.MembraneRandomVariable;
import cbit.vcell.math.MembraneRegionEquation;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.OutsideVariable;
import cbit.vcell.math.ParticleComponentStateDefinition;
import cbit.vcell.math.ParticleComponentStatePattern;
import cbit.vcell.math.ParticleJumpProcess;
import cbit.vcell.math.ParticleJumpProcess.ProcessSymmetryFactor;
import cbit.vcell.math.ParticleMolecularComponent;
import cbit.vcell.math.ParticleMolecularComponentPattern;
import cbit.vcell.math.ParticleMolecularComponentPattern.ParticleBondType;
import cbit.vcell.math.ParticleMolecularType;
import cbit.vcell.math.ParticleMolecularTypePattern;
import cbit.vcell.math.ParticleObservable.ObservableType;
import cbit.vcell.math.ParticleObservable.Sequence;
import cbit.vcell.math.ParticleProperties;
import cbit.vcell.math.ParticleProperties.ParticleInitialCondition;
import cbit.vcell.math.ParticleProperties.ParticleInitialConditionConcentration;
import cbit.vcell.math.ParticleProperties.ParticleInitialConditionCount;
import cbit.vcell.math.ParticleSpeciesPattern;
import cbit.vcell.math.ParticleVariable;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.PdeEquation.BoundaryConditionValue;
import cbit.vcell.math.PointSubDomain;
import cbit.vcell.math.PointVariable;
import cbit.vcell.math.ProjectionDataGenerator;
import cbit.vcell.math.RandomVariable;
import cbit.vcell.math.StochVolVariable;
import cbit.vcell.math.SubDomain.BoundaryConditionSpec;
import cbit.vcell.math.UniformDistribution;
import cbit.vcell.math.VarIniCondition;
import cbit.vcell.math.VarIniCount;
import cbit.vcell.math.VarIniPoissonExpectedCount;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableHash;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.VolumeParticleObservable;
import cbit.vcell.math.VolumeParticleSpeciesPattern;
import cbit.vcell.math.VolumeParticleVariable;
import cbit.vcell.math.VolumeRandomVariable;
import cbit.vcell.math.VolumeRegionEquation;
import cbit.vcell.math.VolumeRegionVariable;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.DBFormalSpecies;
import cbit.vcell.model.DBSpecies;
import cbit.vcell.model.Diagram;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.FormalSpeciesInfo;
import cbit.vcell.model.GHKKinetics;
import cbit.vcell.model.GeneralCurrentKinetics;
import cbit.vcell.model.GeneralCurrentLumpedKinetics;
import cbit.vcell.model.GeneralKinetics;
import cbit.vcell.model.GeneralLumpedKinetics;
import cbit.vcell.model.GeneralPermeabilityKinetics;
import cbit.vcell.model.HMM_IRRKinetics;
import cbit.vcell.model.HMM_REVKinetics;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Macroscopic_IRRKinetics;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Microscopic_IRRKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.Model.ReservedSymbol;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.NernstKinetics;
import cbit.vcell.model.NodeReference;
import cbit.vcell.model.NodeReference.Mode;
import cbit.vcell.model.Product;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.RbmKineticLaw;
import cbit.vcell.model.RbmKineticLaw.RateLawType;
import cbit.vcell.model.RbmKineticLaw.RbmKineticLawParameterType;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.VCMODL;
import cbit.vcell.modelopt.AnalysisTask;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.ParameterEstimationTaskXMLPersistence;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.render.Vect3d;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.AnnotatedFunction.FunctionCategory;
import cbit.vcell.solver.ConstantArraySpec;
import cbit.vcell.solver.DataProcessingInstructions;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.NFsimSimulationOptions;
import cbit.vcell.solver.NonspatialStochHybridOptions;
import cbit.vcell.solver.NonspatialStochSimOptions;
import cbit.vcell.solver.OutputFunctionContext;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SmoldynSimulationOptions;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.SundialsPdeSolverOptions;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solvers.mb.MovingBoundarySolverOptions;
import cbit.vcell.solvers.mb.MovingBoundarySolverOptions.ExtrapolationMethod;
import cbit.vcell.solvers.mb.MovingBoundarySolverOptions.RedistributionMode;
import cbit.vcell.solvers.mb.MovingBoundarySolverOptions.RedistributionVersion;
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
    private ModelUnitSystem forcedModelUnitSystem = null;
	    	
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

public void setForcedModelUnitSystem(ModelUnitSystem newModelUnitSystem) {
	forcedModelUnitSystem = newModelUnitSystem;
}
    
/**
 * This method returns a Action object from a XML element.
 * Creation date: (7/24/2006 5:56:36 PM)
 * @return cbit.vcell.math.Action
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
private Action getAction(Element param, MathDescription md) throws XmlParseException, MathException, ExpressionException
{
	//retrieve values
	String operation = unMangle( param.getAttributeValue(XMLTags.OperationAttrTag) );
	String operand = param.getText();
	Expression exp = null;
	if (operand != null && operand.length() != 0) {
		exp = unMangleExpression(operand);
	}
	String name = unMangle( param.getAttributeValue(XMLTags.VarNameAttrTag) );
	
	Variable var = md.getVariable(name);
	if (var == null){
		throw new MathFormatException("variable "+name+" not defined");
	}	
	if (!(var instanceof StochVolVariable) && !(var instanceof ParticleVariable)){
		throw new MathFormatException("variable "+name+" not a Stochastic Volume Variable");
	}
	try {
		Action action = new Action(var, operation, exp);
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
private AnalyticSubVolume getAnalyticSubVolume(Element param) throws XmlParseException{
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
		throw new XmlParseException("An ExpressionException occured when creating the new AnalyticSubvolume " + name, e);
	}

	return  newsubvolume;
}

private VCellSoftwareVersion docVCellSoftwareVersion = null;

/**
 * This method returns a Biomodel object from a XML Element.
 * Creation date: (3/13/2001 12:35:00 PM)
 * @return cbit.vcell.biomodel.BioModel
 * @param param org.jdom.Element
 */
public BioModel getBioModel(Element param,VCellSoftwareVersion docVcellSoftwareVersion) throws XmlParseException{
	this.docVCellSoftwareVersion = docVcellSoftwareVersion;
//long l1 = System.currentTimeMillis();
	//Get metadata information Version (if available)
	Version version = getVersion(param.getChild(XMLTags.VersionTag, vcNamespace));
		
	//Create new biomodel
	BioModel biomodel = new BioModel( version );
	
	//Set name
	String name = unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	try {
		biomodel.setName( name );
//		String annotation = param.getAttributeValue(XMLTags.AnnotationAttrTag);

//		if (annotation!=null) {
//			biomodel.setDescription(unMangle(annotation));
//		}
		//get annotation
		String annotationText = param.getChildText(XMLTags.AnnotationTag, vcNamespace);
		if (annotationText!=null && annotationText.length()>0) {
			biomodel.setDescription(unMangle(annotationText));
		}
	} catch(java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(e);
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
	if(biomodel.getVersion() != null && biomodel.getVersion().getVersionKey() != null) {
		Long lpcBMKey = Long.valueOf(biomodel.getVersion().getVersionKey().toString());
		MathDescription.originalHasLowPrecisionConstants.remove(lpcBMKey);
	}
	while (iterator.hasNext()) {
//long l4 = System.currentTimeMillis();
		Element tempElement = iterator.next();
		SimulationContext simContext = getSimulationContext(tempElement, biomodel);
		try {
			biomodel.addSimulationContext( simContext );
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException("An error occurred while trying to add the SimContext "+ simContext.getName() +" to the BioModel Object!", e);
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
				throw new XmlParseException("A PropertyVetoException occurred when adding a Simulation entity to the BioModel " + name, e);
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
	Element pathwayElement = param.getChild(XMLTags.PathwayModelTag, vcNamespace);
	if (pathwayElement!=null){
		Element rdfElement = pathwayElement.getChild(XMLRDF.tagRDF, XMLRDF.nsRDF);
		if (rdfElement!=null){
			PathwayReaderBiopax3 pathwayReader = new PathwayReaderBiopax3(new RDFXMLContext());
			PathwayModel pathwayModel = pathwayReader.parse(rdfElement,false);
			pathwayModel.reconcileReferences(null);		// ??? is this needed ???
			// we keep as lvl 1 only the objects which we want to show in the diagram
			pathwayModel.filterDiagramObjects();
			biomodel.getPathwayModel().merge(pathwayModel);
		} else {
			throw new XmlParseException("expecting RDF element as child of pathwayModel within VCML document");
		}
	}
	
	Element relationshipElement = param.getChild(XMLTags.RelationshipModelTag, vcNamespace);
	if (relationshipElement!=null){
		Element rmnsElement = relationshipElement.getChild("RMNS", vcNamespace);
		if (rmnsElement!=null){
			RelationshipReader relationshipReader = new RelationshipReader();
			RelationshipModel relationshipModel = relationshipReader.parse(rmnsElement, biomodel);
			biomodel.getRelationshipModel().merge(relationshipModel);
		} else {
//			throw new XmlParseException("expecting RMNS element as child of pathwayModel within VCML document");
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
private Catalyst getCatalyst(Element param, ReactionStep reaction, Model model) throws XmlParseException {
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
private CompartmentSubDomain getCompartmentSubDomain(Element param, MathDescription mathDesc) throws XmlParseException {
	//get attributes
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	int priority = -1;
	String temp = param.getAttributeValue(XMLTags.PriorityAttrTag);
	if ( temp != null) {
		priority = Integer.parseInt( temp );
	}
	//--- create new CompartmentSubDomain ---
	CompartmentSubDomain subDomain = new CompartmentSubDomain(name, priority);
	transcribeComments(param, subDomain);

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
	
	//process BoundaryConditionSpecs
	iterator = param.getChildren( XMLTags.BoundaryConditionSpecTag, vcNamespace ).iterator();
	if(iterator != null) {
		while (iterator.hasNext()) {
			Element tempelement = (Element)iterator.next();
			try {
				subDomain.addBoundaryConditionSpec( getBoundaryConditionSpec(tempelement) );
			} catch (MathException e) {
				e.printStackTrace();
				throw new XmlParseException("A MathException was fired when adding a BoundaryConditionSpec to the compartmentSubDomain " + name, e);
			}
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
			throw new XmlParseException("A MathException was fired when adding an OdeEquation to the compartmentSubDomain " + name, e);
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
			throw new XmlParseException("A MathException was fired when adding an PdeEquation to the compartmentSubDomain " + name, e);
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
			throw new XmlParseException("A MathException was fired when adding a VolumeRegionEquation to the compartmentSubDomain " + name, e);
		}
	}

	//Process Variable initial conditions (added for stochastic algos)
	iterator = param.getChildren( XMLTags.VarIniCount_OldTag, vcNamespace ).iterator();
	if(iterator != null)
	{
		while (iterator.hasNext()) {
			Element tempelement = (Element)iterator.next();
			try {
				subDomain.addVarIniCondition( getVarIniCount(tempelement, mathDesc) );
			} catch (MathException e) {
				e.printStackTrace();
				throw new XmlParseException("A MathException was fired when adding a variable initial condition to the compartmentSubDomain " + name, e);
			} catch (ExpressionException e) {e.printStackTrace();}
		}
	}
	
	iterator = param.getChildren( XMLTags.VarIniCountTag, vcNamespace ).iterator();
	if(iterator != null)
	{
		while (iterator.hasNext()) {
			Element tempelement = (Element)iterator.next();
			try {
				subDomain.addVarIniCondition( getVarIniCount(tempelement, mathDesc) );
			} catch (MathException e) {
				e.printStackTrace();
				throw new XmlParseException("A MathException was fired when adding a variable initial condition to the compartmentSubDomain " + name, e);
			} catch (ExpressionException e) {e.printStackTrace();}
		}
	}
	
	iterator = param.getChildren( XMLTags.VarIniPoissonExpectedCountTag, vcNamespace ).iterator();
	if(iterator != null)
	{
		while (iterator.hasNext()) {
			Element tempelement = (Element)iterator.next();
			try {
				subDomain.addVarIniCondition( getVarIniPoissonExpectedCount(tempelement, mathDesc) );
			} catch (MathException e) {
				e.printStackTrace();
				throw new XmlParseException("A MathException was fired when adding a variable initial condition to the compartmentSubDomain " + name, e);
			} catch (ExpressionException e) {e.printStackTrace();}
		}
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
			throw new XmlParseException("A MathException was fired when adding a jump process to the compartmentSubDomain " + name, e);
		} 
	}
	
	iterator = param.getChildren(XMLTags.ParticleJumpProcessTag, vcNamespace).iterator();
	while (iterator.hasNext()) {
		Element tempelement = (Element)iterator.next();
		try {
			subDomain.addParticleJumpProcess(getParticleJumpProcess(tempelement, mathDesc) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding a jump process to the compartmentSubDomain " + name, e);
		} 
	}
	
	iterator = param.getChildren(XMLTags.ParticlePropertiesTag, vcNamespace).iterator();
	while (iterator.hasNext()) {
		Element tempelement = (Element)iterator.next();
		try {
			subDomain.addParticleProperties(getParticleProperties(tempelement, mathDesc));
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding a jump process to the compartmentSubDomain " + name, e);
		} 
	}
	
	//process ComputeCentroid "equations"
	iterator = param.getChildren( XMLTags.ComputeCentroidTag, vcNamespace ).iterator();
	while (iterator.hasNext()) {
		Element tempelement = (Element)iterator.next();

		try {
			subDomain.addEquation( getComputeCentroid(tempelement, mathDesc) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding an ComputeCentroid 'equation' to the compartmentSubDomain " + name, e);
		}
	}
	
	//process ComputeMembraneMetric "equations"
	iterator = param.getChildren( XMLTags.ComputeMembraneMetricTag, vcNamespace ).iterator();
	while (iterator.hasNext()) {
		Element tempelement = (Element)iterator.next();

		try {
			subDomain.addEquation( getComputeMembraneMetric(tempelement, mathDesc) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding an ComputeMembraneMetric 'equation' to the compartmentSubDomain " + name, e);
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
private CompartmentSubVolume getCompartmentSubVolume(Element param) throws XmlParseException{
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
		throw new XmlParseException("A propertyVetoException was fired when setting the name to the compartmentSubVolume " + name, e);
	}
	
	return  newcompartment;
}

private void transcribeComments(Element source, Object destination) {
	String before = source.getAttributeValue(XMLTags.BEFORE_COMMENT_ATTR_TAG);
	String after = source.getAttributeValue(XMLTags.AFTER_COMMENT_ATTR_TAG);
	if (before != null || after != null) {
		if (!(destination instanceof Commented)) {
			throw new UnsupportedOperationException("Can't add comments, " + destination.getClass().toString() + " does not implement " + Commented.class.toString() ); 
			
		}
		Commented c= (Commented) destination;
		c.setBeforeComment(before);
		c.setAfterComment(after);
	}
}

/**
 * This method returns a Constant object from a XML element.
 * Creation date: (5/16/2001 1:50:07 PM)
 * @return cbit.vcell.math.Constant
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
private Constant getConstant(Element param) throws XmlParseException {
	//retrieve values
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	Expression exp = unMangleExpression(param.getText());
	
	//-- create new constant object ---
	Constant newconstant = new Constant(name, exp);
	transcribeComments(param, newconstant);

	return newconstant;	
}


/**
 * This method returns a ControlPointcurve object from a XML element.
 * Creation date: (5/22/2001 5:20:39 PM)
 * @return cbit.vcell.geometry.ControlPointCurve
 * @param param org.jdom.Element
 */
private ControlPointCurve getControlPointCurve(Element param) {
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
private DBFormalSpecies getDBFormalSpecies(Element formalSpeciesElement) throws XmlParseException {
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
private DBSpecies getDBSpecies(Element dbSpeciesElement) throws XmlParseException {
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
private Diagram getDiagram(Element param, Model model) throws XmlParseException{
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
		List<NodeReference> nodeRefList = new ArrayList<>();
		for (int i=0 ; i<children.size() ; i++) {
			nodeRefList.add(getNodeReference(children.get(i)));
		}
		newdiagram.setNodeReferences(nodeRefList);
	}
	
	return newdiagram;
}


/**
 * This method process Electrical Stimulus, also called Clamps.
 * Creation date: (6/6/2002 4:46:18 PM)
 * @return cbit.vcell.mapping.ElectricalStimulus
 * @param param org.jdom.Element
 */
private ElectricalStimulus getElectricalStimulus(Element param, SimulationContext currentSimulationContext) throws XmlParseException{
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
		Model model = currentSimulationContext.getModel();
		addResevedSymbols(varHash, model);

		//
		// rename "special" parameters (those that are not "user defined")
		//
		for (Element xmlParam : list){
			String paramName = unMangle(xmlParam.getAttributeValue(XMLTags.NameAttrTag));
			String role = xmlParam.getAttributeValue(XMLTags.ParamRoleAttrTag);
			String paramExpStr = xmlParam.getText();
			Expression paramExp = unMangleExpression(paramExpStr);
			try {
				if (varHash.getVariable(paramName) == null){
					Domain domain = null;
					varHash.addVariable(new Function(paramName,paramExp,domain));
				} else {
					if (model.getReservedSymbolByName(paramName) != null) {
						varHash.removeVariable(paramName);
						Domain domain = null;
						varHash.addVariable(new Function(paramName, paramExp,domain));
					}
				}
			}catch (MathException e){
				e.printStackTrace(System.out);
				throw new XmlParseException("error reordering parameters according to dependencies:", e);
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
				Domain domain = null;
				varHash.addVariable(new Function(unresolvedSymbol,new Expression(0.0),domain));  // will turn into an UnresolvedParameter.
			}catch (MathException e){
				e.printStackTrace(System.out);
				throw new XmlParseException(e.getMessage());
			}
			clampStimulus.addUnresolvedParameter(unresolvedSymbol);
			unresolvedSymbol = varHash.getFirstUnresolvedSymbol();
		}
		
		Variable sortedVariables[] = varHash.getTopologicallyReorderedVariables();
		ModelUnitSystem modelUnitSystem = model.getUnitSystem();
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
					unit = modelUnitSystem.getInstance(symbol);
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
		throw new XmlParseException("Exception while setting parameters for simContext : " + currentSimulationContext.getName(), e);
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException("Exception while settings parameters for simContext : " + currentSimulationContext.getName(), e);
	} finally {
		clampStimulus.reading(false);
	}
	
	return clampStimulus;
}


private void readParameters(List<Element> parameterElements, ParameterContext parameterContext, HashMap<String,ParameterRoleEnum> roleHash, ParameterRoleEnum userDefinedRole, HashSet<String> xmlRolesTagsToIgnore, Model model) throws XmlParseException {

	String contextName = parameterContext.getNameScope().getName();
	try {
		//
		// prepopulate varHash with reserved symbols
		//
		VariableHash varHash = new VariableHash();
		addResevedSymbols(varHash, model);

		//
		// process each parameter:
		//    1) put the parameter into the varHash
		//    2) rename predefined parameters from the ParameterContext as necessary to avoid naming conflicts 
		//       and use the stored names for pre-defined parameters.
		//
		for (Element xmlParam : parameterElements){
			String parsedParamName = unMangle(xmlParam.getAttributeValue(XMLTags.NameAttrTag));
			String parsedRoleString = xmlParam.getAttributeValue(XMLTags.ParamRoleAttrTag);
			String parsedExpressionString = xmlParam.getText();

			//
			// should we skip this xml role tag? not used anymore.
			//
			if (xmlRolesTagsToIgnore.contains(parsedRoleString)){
				varHash.removeVariable(parsedParamName);
				continue;
			}			
			
			Expression paramExp = null;
			if (parsedExpressionString.trim().length()>0){
				paramExp = unMangleExpression(parsedExpressionString);
			}
			
			if (varHash.getVariable(parsedParamName) == null){
				Domain domain = null;
				varHash.addVariable(new Function(parsedParamName,paramExp,domain));
			} else {
				if (model.getReservedSymbolByName(parsedParamName) != null) {
					varHash.removeVariable(parsedParamName);
					Domain domain = null;
					varHash.addVariable(new Function(parsedParamName, paramExp,domain));
				}
			}

			//
			// get the parameter for this xml role string
			//
			ParameterRoleEnum paramRole = roleHash.get(parsedRoleString);
			if (paramRole == null){
				throw new XmlParseException("parameter '"+parsedParamName+"' has unexpected role '"+parsedRoleString+"' in '"+contextName+"'");
			}
			
			//
			// if parameter is not user-defined, then force the parameter with the same role to have the same name.
			//
			if (paramRole != userDefinedRole){
				LocalParameter paramWithSameRole = parameterContext.getLocalParameterFromRole(paramRole);
				if (paramWithSameRole == null){
					throw new XmlParseException("can't find parameter with role '"+parsedRoleString+"' in '"+contextName+"'");
				}
				//
				// "special" parameter with same role has a different name, rename 
				// 
				//
				if (!paramWithSameRole.getName().equals(parsedParamName)) {
					//
					// first rename other parameters with same name
					//
					LocalParameter paramWithSameNameButDifferentRole = parameterContext.getLocalParameterFromName(parsedParamName);
					if (paramWithSameNameButDifferentRole!=null){
						//
						// find available name
						//
						int n = 0;
						String newName = parsedParamName + "_" + n++;
						while (parameterContext.getEntry(newName) != null){
							newName = parsedParamName + "_" + n++;
						}
						parameterContext.renameLocalParameter(parsedParamName, newName);
					}
					//
					// then rename parameter with correct role
					//
					parameterContext.renameLocalParameter(paramWithSameRole.getName(),parsedParamName);
				}
			}
		}

		//
		// create unresolved parameters for all unresolved symbols
		//
		String unresolvedSymbol = varHash.getFirstUnresolvedSymbol();
		while (unresolvedSymbol!=null){
			try {
				Domain domain = null;
				varHash.addVariable(new Function(unresolvedSymbol,new Expression(0.0),domain));  // will turn into an UnresolvedParameter.
			}catch (MathException e){
				e.printStackTrace(System.out);
				throw new XmlParseException(e.getMessage());
			}
			parameterContext.addUnresolvedParameter(unresolvedSymbol);
			unresolvedSymbol = varHash.getFirstUnresolvedSymbol();
		}
		
		
		//
		// in topological order, add parameters to model (getting units also).
		// note that all pre-defined parameters already have the correct names
		// here we set expressions on pre-defined parameters and add user-defined parameters
		//
		Variable sortedVariables[] = varHash.getTopologicallyReorderedVariables();
		ModelUnitSystem modelUnitSystem = model.getUnitSystem();
		for (int i = sortedVariables.length-1; i >= 0 ; i--){
			if (sortedVariables[i] instanceof Function){
				Function paramFunction = (Function)sortedVariables[i];
				Element xmlParam = null;
				for (int j = 0; j < parameterElements.size(); j++){
					Element tempParam = (Element)parameterElements.get(j);
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
					unit = modelUnitSystem.getInstance(symbol);
				}
				LocalParameter tempParam = parameterContext.getLocalParameterFromName(paramFunction.getName());
				if (tempParam == null) {
					tempParam = parameterContext.addLocalParameter(paramFunction.getName(), new Expression(0.0), userDefinedRole, unit, userDefinedRole.getDescription());
					parameterContext.setParameterValue(tempParam, paramFunction.getExpression(), true);
				} else {
					if (tempParam.getExpression()!=null){ // if the expression is null, it should remain null.
						parameterContext.setParameterValue(tempParam, paramFunction.getExpression(), true);
					}
					tempParam.setUnitDefinition(unit);
				}
			}
		}
		
	} catch (PropertyVetoException | ExpressionException | MathException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException("Exception while setting parameters for '"+contextName+"': " + e.getMessage(), e);
	}
}


/**
 * This method returns an Electrode object from a XML representation.
 * Creation date: (6/6/2002 4:22:55 PM)
 * @return cbit.vcell.mapping.Electrode
 */
private Electrode getElectrode(org.jdom.Element elem, SimulationContext currentSimulationContext) {
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
private ErrorTolerance getErrorTolerance(Element param) {
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
private FastSystem getFastSystem(
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
                "A MathException was fired when adding the FastInvariant " + fastInvariant + ", to a FastSystem!"+" : ", e);
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
                "A MathException was fired when adding the FastRate " + fastRate + ", to a FastSystem!", e);
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
private Structure getFeature(Element param) throws XmlParseException {
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
				+ param.getAttributeValue(XMLTags.NameAttrTag), e);
	}

	return newfeature;
}


/**
 * This method retuns a FeatureMapping object from a XML representation.
 * Creation date: (5/7/2001 4:12:03 PM)
 * @return cbit.vcell.mapping.FeatureMapping
 * @param param org.jdom.Element
 */
private FeatureMapping getFeatureMapping(Element param, SimulationContext simulationContext) throws XmlParseException{
	//Retrieve attributes
	String featurename = unMangle( param.getAttributeValue(XMLTags.FeatureAttrTag) );
	String geometryClassName = param.getAttributeValue(XMLTags.SubVolumeAttrTag);
	if (geometryClassName != null){
		geometryClassName = unMangle(geometryClassName);
	}else{
		geometryClassName = param.getAttributeValue(XMLTags.GeometryClassAttrTag);
		if (geometryClassName != null){
			geometryClassName = unMangle(geometryClassName);
		}
	}
	
	Feature featureref = (Feature)simulationContext.getModel().getStructure(featurename);
	if (featureref == null) {
		throw new XmlParseException("The Feature "+ featurename + " could not be resolved!");
	}

	//*** Create new Feature Mapping ****
	FeatureMapping feamap = new FeatureMapping(featureref,simulationContext, simulationContext.getModel().getUnitSystem());

	//Set Size
	if(param.getAttributeValue(XMLTags.SizeTag) != null)
	{
		String size = unMangle( param.getAttributeValue(XMLTags.SizeTag) );
		try {
			feamap.getSizeParameter().setExpression(unMangleExpression(size));
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException("An expressionException was fired when setting the size Expression " + size + " to a featureMapping!", e);
		}	
	}else{
		try {
			feamap.getSizeParameter().setExpression(null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("unexpected exception while setting structure size: "+e.getMessage());
		}
	}
	
	//Set Volume/unit_area if it exists
	if(param.getAttributeValue(XMLTags.VolumePerUnitAreaTag) != null)
	{
		String volPerUnitArea = unMangle( param.getAttributeValue(XMLTags.VolumePerUnitAreaTag) );
		try {
			feamap.getVolumePerUnitAreaParameter().setExpression(unMangleExpression(volPerUnitArea));
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException("An expressionException was fired when setting the VolumePerUnitArea Expression " + volPerUnitArea + " to a featureMapping!", e);
		}	
	}

	//Set Volume/unitVol if it exists
	if(param.getAttributeValue(XMLTags.VolumePerUnitVolumeTag) != null)
	{
		String volPerUnitVol = unMangle( param.getAttributeValue(XMLTags.VolumePerUnitVolumeTag) );
		try {
			feamap.getVolumePerUnitVolumeParameter().setExpression(unMangleExpression(volPerUnitVol));
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException("An expressionException was fired when setting the size Expression " + volPerUnitVol + " to a featureMapping!", e);
		}	
	}

	if (geometryClassName != null) {
		GeometryClass[] geometryClasses = simulationContext.getGeometry().getGeometryClasses();
		for (int i = 0; i < geometryClasses.length; i++) {
			if (geometryClasses[i].getName().equals(geometryClassName)){
				try {
					feamap.setGeometryClass(geometryClasses[i]);
				} catch (PropertyVetoException e) {
					e.printStackTrace(System.out);
					throw new XmlParseException("A propertyVetoException was fired when trying to set the subvolume or surface " + geometryClassName + " to a MembraneMapping!", e);
				}
			}
		}
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
private FilamentRegionVariable getFilamentRegionVariable(Element param) {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String domainStr = unMangle( param.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}

	//-- create new FilamentRegionVariable object
	FilamentRegionVariable filRegVariable = new FilamentRegionVariable( name, domain );
	transcribeComments(param, filRegVariable);

	return filRegVariable;
}


/**
 * This method returns a FilamentSubDomain object from a XMl element.
 * Creation date: (5/18/2001 4:27:22 PM)
 * @return cbit.vcell.math.FilamentSubDomain
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
private FilamentSubDomain getFilamentSubDomain(Element param, MathDescription mathDesc) throws XmlParseException {
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
			e.printStackTrace(System.out);
			throw new XmlParseException("A MathException was fired when adding an OdeEquation to the FilamentSubDomain " + name, e);
		}
	}
	//Add the FastSytem
	filDomain.setFastSystem( getFastSystem(param.getChild(XMLTags.FastSystemTag, vcNamespace), mathDesc) );

	return filDomain;
}

private PointSubDomain getPointSubDomain(Element param, MathDescription mathDesc) throws XmlParseException {
	//get name
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	//*** create new pointSubDomain object ***
	PointSubDomain pointDomain = new PointSubDomain(name);
	
	//add OdeEquations
	Iterator<Element> iterator = param.getChildren(XMLTags.OdeEquationTag, vcNamespace).iterator();
	while ( iterator.hasNext() ){
		Element tempElement = (Element)iterator.next();
		try {
			pointDomain.addEquation( getOdeEquation(tempElement, mathDesc) );
		} catch (MathException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException("A MathException was fired when adding an OdeEquation to the FilamentSubDomain " + name, e);
		}
	}
	String temp = param.getChildText(XMLTags.PositionXTag, vcNamespace);
	if (temp!=null && temp.length()>0) {
		pointDomain.setPositionX(unMangleExpression(temp));
	}
	temp = param.getChildText(XMLTags.PositionYTag, vcNamespace);
	if (temp!=null && temp.length()>0) {
		pointDomain.setPositionY(unMangleExpression(temp));
	}
	temp = param.getChildText(XMLTags.PositionZTag, vcNamespace);
	if (temp!=null && temp.length()>0) {
		pointDomain.setPositionZ(unMangleExpression(temp));
	}
	

//	//Add the FastSytem
//	pointDomain.setFastSystem( getFastSystem(param.getChild(XMLTags.FastSystemTag, vcNamespace), mathDesc) );

	return pointDomain;
}


/**
 * This method returns a FilamentVariable object from a XML Element.
 * Creation date: (5/16/2001 2:56:34 PM)
 * @return cbit.vcell.math.FilamentVariable
 * @param param org.jdom.Element
 */
private FilamentVariable getFilamentVariable(Element param) {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String domainStr = unMangle( param.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}
	//-- create new filVariable object
	FilamentVariable filVariable = new FilamentVariable( name, domain );
	transcribeComments(param, filVariable);

	return filVariable;
}


/**
 * This method returns a FluxReaction object from a XML element.
 * Creation date: (3/16/2001 11:52:02 AM)
 * @return cbit.vcell.model.FluxReaction
 * @param param org.jdom.Element
 * @throws XmlParseException 
 * @throws PropertyVetoException 
 * @throws ModelException 
 * @throws Exception 
 */
private FluxReaction getFluxReaction( Element param, Model model) throws XmlParseException, PropertyVetoException {
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
		

    //-- Instantiate new FluxReaction --
	FluxReaction fluxreaction = null;
	String name = unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	
    String reversibleAttributeValue = param.getAttributeValue(XMLTags.ReversibleAttrTag);
    boolean bReversible = true;
    if (reversibleAttributeValue != null){
    	if (Boolean.TRUE.toString().equals(reversibleAttributeValue)){
    		bReversible = true;
    	} else if (Boolean.FALSE.toString().equals(reversibleAttributeValue)){
    		bReversible = false;
    	} else {
    		throw new RuntimeException("unexpected value "+reversibleAttributeValue+" for reversible flag for reaction "+name);
    	}
    }
    
	try {
		fluxreaction = new FluxReaction(model, structureref, key, name, bReversible);
		fluxreaction.setModel(model);
        if(param.getAttributeValue(XMLTags.SbmlNameAttrTag) != null) {
        	fluxreaction.setSbmlName(unMangle(param.getAttributeValue(XMLTags.SbmlNameAttrTag)));
        }

	} catch (Exception e) {
		e.printStackTrace();
		throw new XmlParseException( "An exception occurred while trying to create the FluxReaction " + name, e);
	}
	//resolve reference to the fluxCarrier
	if (param.getAttribute(XMLTags.FluxCarrierAttrTag)!= null) {	
		String speciesname = unMangle(param.getAttributeValue(XMLTags.FluxCarrierAttrTag));
		Species specieref = model.getSpecies(speciesname);
		if (specieref != null) {
			Feature insideFeature = model.getStructureTopology().getInsideFeature(structureref);
			try {
				if (insideFeature!=null){
					SpeciesContext insideSpeciesContext = model.getSpeciesContext(specieref, insideFeature);
					fluxreaction.addProduct(insideSpeciesContext, 1);
				}
				Feature outsideFeature = model.getStructureTopology().getOutsideFeature(structureref);
				if (outsideFeature!=null){
					SpeciesContext outsideSpeciesContext = model.getSpeciesContext(specieref, outsideFeature);
					fluxreaction.addReactant(outsideSpeciesContext, 1);
				}
			}catch (ModelException e){
				e.printStackTrace(System.out);
				throw new XmlParseException(e.getMessage());
			}
		}
	}
	//Annotation
//	String rsAnnotation = null;
//	String annotationText = param.getChildText(XMLTags.AnnotationTag, vcNamespace);
//	if (annotationText!=null && annotationText.length()>0) {
//		rsAnnotation = unMangle(annotationText);
//	}
//	fluxreaction.setAnnotation(rsAnnotation);
	
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
			throw new XmlParseException("A propertyVetoException was fired when setting the fluxOption to the flux reaction " + name, e);
		}
	}
	
	//Add Reactants, if any
	try {
		Iterator<Element> iterator = param.getChildren(XMLTags.ReactantTag, vcNamespace).iterator();
		while (iterator.hasNext()) {
			Element temp = iterator.next();
			//Add Reactant to this SimpleReaction
			fluxreaction.addReactionParticipant(getReactant(temp, fluxreaction, model));
		}
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("Error adding a reactant to the reaction "+ name+" : "+e.getMessage());
	}

	//Add Products, if any
	try {
		Iterator<Element> iterator = param.getChildren(XMLTags.ProductTag, vcNamespace).iterator();
		while (iterator.hasNext()) {
			Element temp = iterator.next();
			//Add Product to this simplereaction
			fluxreaction.addReactionParticipant(getProduct(temp, fluxreaction, model));
        }
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("Error adding a product to the reaction "+ name+" : "+e.getMessage());
	}

	//Add Catalyst(Modifiers) (if there are)
	Iterator<Element> iterator = param.getChildren(XMLTags.CatalystTag, vcNamespace).iterator();
	while (iterator.hasNext()) {
		Element temp = iterator.next();
		fluxreaction.addReactionParticipant( getCatalyst(temp, fluxreaction, model) );
	}
	//Add Kinetics
	fluxreaction.setKinetics(getKinetics(param.getChild(XMLTags.KineticsTag, vcNamespace), fluxreaction, model));
	
	//set the valence (for legacy support for "chargeCarrierValence" stored with reaction).
	String valenceString = null;
	try {
		valenceString = unMangle(param.getAttributeValue(XMLTags.FluxCarrierValenceAttrTag));
		if (valenceString!=null&&valenceString.length()>0){
			KineticsParameter chargeValenceParameter = fluxreaction.getKinetics().getChargeValenceParameter();
			if (chargeValenceParameter!=null){
				chargeValenceParameter.setExpression(new Expression(Integer.parseInt(unMangle(valenceString))));
			}
		}
	} catch (NumberFormatException e) {
		e.printStackTrace();
		throw new XmlParseException("A NumberFormatException was fired when setting the (integer) valence '"+valenceString+"' (integer) to the flux reaction " + name, e);
	}
	return fluxreaction;
}


/**
 * This method creates a FormalSpeciesInfo from a XML representation.
 * Creation date: (6/3/2003 9:11:26 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 * @param speciesInfoElement org.jdom.Element
 */
private FormalSpeciesInfo getFormalSpeciesInfo(Element speciesInfoElement) throws XmlParseException {
	//get formalID
	String formalID = unMangle(speciesInfoElement.getAttributeValue(XMLTags.FormalIDTag));
	//get names
	List<Element> namesList = speciesInfoElement.getChildren(XMLTags.NameTag, vcNamespace);
	String[] namesArray = new String[namesList.size()];
    int nameCounter = 0;
	for (Element nameElement : namesList){
		namesArray[nameCounter] = unMangle(nameElement.getText());
		nameCounter ++;
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
			int enzymeCounter = 0;
			for (Element enzymeElement : enzymelist){
				//get ECNumber
				String ecnumber = unMangle(enzymeElement.getAttributeValue(XMLTags.ECNumberTag));
				//get Enzymetype
				String enztypestr = enzymeElement.getAttributeValue(XMLTags.TypeAttrTag);
				char enzymetype = enztypestr.charAt(0);
				enzymeArray[enzymeCounter] = new EnzymeRef(ecnumber, enzymetype);
				enzymeCounter ++;
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
private Function getFunction(Element param) throws XmlParseException {
	//get attributes
	String name = unMangle( param.getAttributeValue( XMLTags.NameAttrTag) );

	
/** ---------------------------------------------------------------
 * ATTENTATION: this is a quick fix for a specific user to load his model
 * with a function name as "ATP/ADP".  This syntax is not allowed. 
-----------------------------------------------------------------------*/
if(name.equals("ATP/ADP"))
{
	name = "ATP_ADP_renamed";
	System.err.print("Applying species function name change ATP/ADP to ATP_ADP for a specific user (key=2288008)");
	Thread.dumpStack();
}
	
	
	String domainStr = unMangle( param.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}
	String temp = param.getText();
	
	Expression exp = unMangleExpression(temp);
	
	//-- create new Function --
	Function function = new Function(name, exp, domain);
	transcribeComments(param, function);

	return function;
}

private AnnotatedFunction getOutputFunction(Element param) throws XmlParseException {
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
	String domainStr = unMangle( param.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}
	AnnotatedFunction function = new AnnotatedFunction(name, exp, domain, errStr, funcType, FunctionCategory.OUTPUTFUNCTION);

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
			throw new XmlParseException(e);
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
		throw new XmlParseException("A PropertyVetoException occurred when setting the name " + name + " to a Geometry object!", e);
	}
	//Add the Extent
	try {
		newgeometry.getGeometrySpec().setExtent( newextent );
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException occurred while trying to set the Extent for the Geometry " + name, e);
	}
	//Add the Origin
	newgeometry.getGeometrySpec().setOrigin( getOrigin(param.getChild(XMLTags.OriginTag, vcNamespace)) );

	//Add the SubVolumes
	List<Element> children = param.getChildren(XMLTags.SubVolumeTag, vcNamespace);
	SubVolume[] newsubvolumes = new SubVolume[children.size()];
	int subvolumeCounter = 0;
	for (Element child : children) {
		newsubvolumes[subvolumeCounter] = getSubVolume(child);
		subvolumeCounter ++;
	}
	try {
		newgeometry.getGeometrySpec().setSubVolumes( newsubvolumes );
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException was generated when ading the subvolumes to the Geometry " + name, e);
	}
	if(newgeometry.getDimension()>0){
		//Add SurfaceClasses
		List<Element> surfaceClassChildren = param.getChildren(XMLTags.SurfaceClassTag, vcNamespace);
		SurfaceClass[] newSurfaceClassArr = new SurfaceClass[surfaceClassChildren.size()];
		int surfClassCounter = 0;
		for (Element surfClassChild : surfaceClassChildren) {
			newSurfaceClassArr[surfClassCounter] = getSurfaceClass(surfClassChild,newgeometry);
			surfClassCounter ++;
		}
		try {
			newgeometry.getGeometrySurfaceDescription().setSurfaceClasses(newSurfaceClassArr);
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
			throw new XmlParseException("A PropertyVetoException was generated when ading the subvolumes to the Geometry " + name, e);
		}
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
	
	try {
		newgeometry.precomputeAll(new GeometryThumbnailImageFactoryAWT(), false, false);
	} catch (GeometryException e) {
		e.printStackTrace(System.out);
	} catch (ImageException e) {
		e.printStackTrace(System.out);
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
	}
	return newgeometry;
}


    private GeometrySurfaceDescription getGeometrySurfaceDescription(Element param, Geometry geom) throws XmlParseException {
 
	    GeometrySurfaceDescription gsd = geom.getGeometrySurfaceDescription();
	    String cutoffStr = param.getAttributeValue(XMLTags.CutoffFrequencyAttrTag);
	    String xDim = param.getAttributeValue(XMLTags.NumSamplesXAttrTag);
	    String yDim = param.getAttributeValue(XMLTags.NumSamplesYAttrTag);
	    String zDim = param.getAttributeValue(XMLTags.NumSamplesZAttrTag);
		if (cutoffStr == null || xDim == null || yDim == null || zDim == null) {
			throw new XmlParseException("Attributes for element Surface Description not properly set, under geometry: " +
										((Element) param.getParent()).getAttributeValue(XMLTags.NameAttrTag));
		}
		try {
			ISize isize = new ISize(Integer.parseInt(xDim), Integer.parseInt(yDim), Integer.parseInt(zDim));
			gsd.setVolumeSampleSize(isize);
			gsd.setFilterCutoffFrequency(new Double(cutoffStr));

			//these lists are allowed to be empty.
		    ArrayList<Element> memRegions = new ArrayList<Element>(param.getChildren(XMLTags.MembraneRegionTag, vcNamespace));
		    ArrayList<Element> volRegions = new ArrayList<Element>(param.getChildren(XMLTags.VolumeRegionTag, vcNamespace));
			ArrayList<GeometricRegion> regions = new ArrayList<GeometricRegion>();
			GeometryUnitSystem geometryUnitSystem = geom.getUnitSystem();
			for (Element temp : volRegions) {
				String regionID = temp.getAttributeValue(XMLTags.RegionIDAttrTag);
				String name = temp.getAttributeValue(XMLTags.NameAttrTag);
				String subvolumeRef = temp.getAttributeValue(XMLTags.SubVolumeAttrTag);
				if (regionID == null || name == null || subvolumeRef == null) {
					throw new XmlParseException("Attributes for element Volume Region not properly set, under geometry: " +
											((Element) param.getParent()).getAttributeValue(XMLTags.NameAttrTag));
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
						unit = geometryUnitSystem.getInstance(unitSymbol);
					}
				}
				VolumeGeometricRegion vgr = new VolumeGeometricRegion(name, size, unit, subvolume, Integer.parseInt(regionID));
				regions.add(vgr);
			}
			for (Element temp : memRegions) {
				String volRegion_1 = temp.getAttributeValue(XMLTags.VolumeRegion_1AttrTag);
				String volRegion_2 = temp.getAttributeValue(XMLTags.VolumeRegion_2AttrTag);
				String name = temp.getAttributeValue(XMLTags.NameAttrTag);
				if (volRegion_1 == null || volRegion_2 == null || name == null) {
					throw new XmlParseException("Attributes for element Membrane Region not properly set, under geometry: " +
											((Element) param.getParent()).getAttributeValue(XMLTags.NameAttrTag));
				}
				VolumeGeometricRegion region1 = getAdjacentVolumeRegion(regions, volRegion_1);
				VolumeGeometricRegion region2 = getAdjacentVolumeRegion(regions, volRegion_2);
				if (region1 == null || region2 == null) {
					throw new XmlParseException("Element Membrane Region refernces invalid volume regions, under geometry: " +
											((Element) param.getParent()).getAttributeValue(XMLTags.NameAttrTag));
				}
				double size = -1;
				VCUnitDefinition unit = null;
				String sizeStr = temp.getAttributeValue(XMLTags.SizeAttrTag);
				if (sizeStr != null) {
					size = Double.parseDouble(sizeStr);
					String unitSymbol = temp.getAttributeValue(XMLTags.VCUnitDefinitionAttrTag);
					if (unitSymbol != null) {
						unit = geometryUnitSystem.getInstance(unitSymbol);
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
								((Element) param.getParent()).getAttributeValue(XMLTags.NameAttrTag));
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
private GroupAccess getGroupAccess(Element xmlGroup) {
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
		int counter = 0;
		for (Element userElement : userlist){
			String userid = unMangle(userElement.getAttributeValue(XMLTags.NameAttrTag));
			KeyValue key = new KeyValue(userElement.getAttributeValue(XMLTags.KeyValueAttrTag));
			boolean hidden = Boolean.valueOf(userElement.getAttributeValue(XMLTags.HiddenTag)).booleanValue();
			userArray[counter] = new User(userid, key);
			booleanArray[counter] = hidden;
			counter ++;
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
private ImageSubVolume getImageSubVolume(Element param) throws XmlParseException{
	//retrieve the attributes
	String name = unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	int handle = Integer.parseInt( param.getAttributeValue(XMLTags.HandleAttrTag) );
	int imagePixelValue = Integer.parseInt( param.getAttributeValue(XMLTags.ImagePixelValueTag) );
 
	//Get the PixelClass from image (image should be a sibling of this subVolume element)
	Element imageElement = ((Element) param.getParent()).getChild(XMLTags.ImageTag, vcNamespace);
	if (imageElement==null){
		throw new XmlParseException("image not found in geometry corresponding to ImageSubVolume");
	}
	
	List<Element> pixelClassList = imageElement.getChildren(XMLTags.PixelClassTag, vcNamespace);
	VCPixelClass pixelClass = null;
	for (Element pixelClassElement : pixelClassList){
		VCPixelClass pc = getPixelClass(pixelClassElement);
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
		throw new XmlParseException("A propertyVetoException was generated when setting the name " + name +" to an ImageSubvolume object!", e);
	}
	
	return  newsubvolume;
}


/**
 * This method returns an InsideVariable object from a XML Element
 * Creation date: (5/18/2001 6:14:42 PM)
 * @return cbit.vcell.math.InsideVariable
 * @param param org.jdom.Element
 */
private InsideVariable getInsideVariable(Element param) {
	//Get name
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	//get VolVariableRef
	String volvarName = unMangle( param.getAttributeValue(XMLTags.VolumeVariableAttrTag) );

	//*** create new InsideVariable ***
	InsideVariable variable = new InsideVariable(name , volvarName);
	transcribeComments(param, variable);
	
	return variable;
}


/**
 * This method returns a JumpCondition object from a XML Element.
 * Creation date: (5/18/2001 5:10:10 PM)
 * @return cbit.vcell.math.JumpCondition
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
private JumpCondition getJumpCondition(Element param, MathDescription mathDesc) throws XmlParseException {
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
private JumpProcess getJumpProcess(Element param, MathDescription md) throws XmlParseException 
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
			throw new XmlParseException("A MathException was fired when adding a new Action to the JumpProcess " + name, e);
		} catch (ExpressionException e) {e.printStackTrace();}
	}
	
	return jump;
}

private ParticleInitialConditionCount getParticleInitialConditionCount(Element param) {
    String temp = param.getChildText(XMLTags.ParticleCountTag, vcNamespace);
    Expression countExp = null;
    if (temp!=null && temp.length()>0) {
    	countExp = unMangleExpression(temp);        	
    }  
    temp = param.getChildText(XMLTags.ParticleLocationXTag, vcNamespace);
    Expression locXExp = null;
    if (temp!=null && temp.length()>0) {
    	locXExp = unMangleExpression(temp);        	
    }  
    temp = param.getChildText(XMLTags.ParticleLocationYTag, vcNamespace);
    Expression locYExp = null;
    if (temp!=null && temp.length()>0) {
    	locYExp = unMangleExpression(temp);        	
    }  
    temp = param.getChildText(XMLTags.ParticleLocationZTag, vcNamespace);
    Expression locZExp = null;
    if (temp!=null && temp.length()>0) {
    	locZExp = unMangleExpression(temp);        	
    }  
        
    return new ParticleInitialConditionCount(countExp, locXExp, locYExp, locZExp);
}

private ParticleProperties getParticleProperties(Element param, MathDescription mathDesc) throws XmlParseException {
    //Retrieve the variable reference
    String name = unMangle(param.getAttributeValue(XMLTags.NameAttrTag));    
    Variable varref = mathDesc.getVariable(name);    
    if (varref == null) {
    	throw new XmlParseException( "The variable " + name + " for a PdeEquation, could not be resolved!");
    }    
    
    ArrayList<ParticleInitialCondition> initialConditions = new ArrayList<ParticleInitialCondition>();
	Iterator<Element> iterator = param.getChildren(XMLTags.ParticleInitialCountTag, vcNamespace).iterator();
	while (iterator.hasNext() ) {
		Element tempelement = (Element)iterator.next();        
        initialConditions.add(getParticleInitialConditionCount(tempelement));
	}
	iterator = param.getChildren(XMLTags.ParticleInitialCountTag_old, vcNamespace).iterator();
	while (iterator.hasNext() ) {
		Element tempelement = (Element)iterator.next();		
		initialConditions.add(getParticleInitialConditionCount(tempelement));
	}
	iterator = param.getChildren(XMLTags.ParticleInitialConcentrationTag, vcNamespace).iterator();
	while (iterator.hasNext() ) {
		Element tempelement = (Element)iterator.next();
		
		String temp = tempelement.getChildText(XMLTags.ParticleDistributionTag, vcNamespace);
		Expression distExp = null;
		if (temp!=null && temp.length()>0) {
			distExp = unMangleExpression(temp);        	
		}	
		initialConditions.add(new ParticleInitialConditionConcentration(distExp));
	}

	String temp = param.getChildText(XMLTags.ParticleDiffusionTag, vcNamespace);
    Expression diffExp = null;
    if (temp!=null && temp.length()>0) {
    	diffExp = unMangleExpression(temp);        	
    } 

	String driftXString = param.getChildText(XMLTags.ParticleDriftXTag, vcNamespace);
    Expression driftXExp = null;
    if (driftXString!=null && driftXString.length()>0) {
    	driftXExp = unMangleExpression(driftXString);        	
    } 

	String driftYString = param.getChildText(XMLTags.ParticleDriftYTag, vcNamespace);
    Expression driftYExp = null;
    if (driftYString!=null && driftYString.length()>0) {
    	driftYExp = unMangleExpression(driftYString);        	
    } 

	String driftZString = param.getChildText(XMLTags.ParticleDriftZTag, vcNamespace);
    Expression driftZExp = null;
    if (driftZString!=null && driftZString.length()>0) {
    	driftZExp = unMangleExpression(driftZString);        	
    } 

    return new ParticleProperties(varref, diffExp, driftXExp, driftYExp, driftZExp, initialConditions);
}

private ParticleJumpProcess getParticleJumpProcess(Element param, MathDescription md) throws XmlParseException 
{
	//name
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	ProcessSymmetryFactor processSymmetryFactor = null;
	Attribute symmetryFactorAttr = param.getAttribute(XMLTags.ProcessSymmetryFactorAttrTag);
	if (symmetryFactorAttr != null){
		processSymmetryFactor = new ProcessSymmetryFactor(Double.parseDouble(symmetryFactorAttr.getValue()));
	}
	
	// selected particle
	List<ParticleVariable> varList = new ArrayList<ParticleVariable>();
	Iterator<Element> iterator = param.getChildren(XMLTags.SelectedParticleTag, vcNamespace).iterator();
	while (iterator.hasNext() ) {
		Element tempelement = (Element)iterator.next();
		String varname = unMangle(tempelement.getAttributeValue(XMLTags.NameAttrTag) );
		Variable var = md.getVariable(varname);
		if (!(var instanceof ParticleVariable)) {
			throw new XmlParseException("Not a ParticleVariable in ParticleJumpProcess.");
		}
		varList.add((ParticleVariable)var);
	}
	
	//probability rate
	JumpProcessRateDefinition jprd = null;
	//for old models
	Element pb = param.getChild(XMLTags.ParticleProbabilityRateTag, vcNamespace);
	if(pb != null)
	{
		Expression exp = unMangleExpression(pb.getText());
		jprd = new MacroscopicRateConstant(exp);
	}
	else //for new models
	{
		pb = param.getChild(XMLTags.MacroscopicRateConstantTag, vcNamespace);
		if(pb != null) //jump process rate defined by macroscopic rate constant
		{
			Expression exp = unMangleExpression(pb.getText());
			jprd = new MacroscopicRateConstant(exp);
		}
		else //jump process rate defined by binding radius
		{
			pb = param.getChild(XMLTags.InteractionRadiusTag, vcNamespace);
			if(pb != null)
			{
				Expression exp = unMangleExpression(pb.getText());
				jprd = new InteractionRadius(exp);
			}
		}
	}
	//add actions
	List<Action> actionList = new ArrayList<Action>();	
	iterator = param.getChildren(XMLTags.ActionTag, vcNamespace).iterator();
	while (iterator.hasNext() ) {
		Element tempelement = (Element)iterator.next();
		try {
			actionList.add(getAction(tempelement, md));
		} catch (MathException e) {			
			e.printStackTrace();
			throw new XmlParseException(e);
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException(e);
		}
	}
	
	ParticleJumpProcess jump = new ParticleJumpProcess(name, varList, jprd, actionList, processSymmetryFactor);
	
	return jump;
}

/**
 * This method returns a Kinetics object from a XML Element based on the value of the kinetics type attribute.
 * Creation date: (3/19/2001 4:42:04 PM)
 * @return cbit.vcell.model.Kinetics
 * @param param org.jdom.Element
 */
private Kinetics getKinetics(Element param, ReactionStep reaction, Model model) throws XmlParseException{
	VariableHash varHash = new VariableHash();
	addResevedSymbols(varHash, model);

	String type = param.getAttributeValue(XMLTags.KineticsTypeAttrTag);
	Kinetics newKinetics = null;
	try {
		if ( type.equalsIgnoreCase(XMLTags.KineticsTypeGeneralKinetics) ) {
			//create a general kinetics
			newKinetics = new GeneralKinetics(reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeGeneralCurrentKinetics) ) {
			//Create GeneralCurrentKinetics
			newKinetics = new GeneralCurrentKinetics(reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeMassAction) && reaction instanceof SimpleReaction) {
			//create a Mass Action kinetics
			newKinetics = new MassActionKinetics((SimpleReaction)reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeNernst) && reaction instanceof FluxReaction) {
			// create NernstKinetics
			newKinetics = new NernstKinetics((FluxReaction)reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeGHK) && reaction instanceof FluxReaction) {
			//create GHKKinetics
			newKinetics = new GHKKinetics((FluxReaction)reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeHMM_Irr) && reaction instanceof SimpleReaction) {
			//create HMM_IrrKinetics
			newKinetics = new HMM_IRRKinetics((SimpleReaction)reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeHMM_Rev) && reaction instanceof SimpleReaction) {
			//create HMM_RevKinetics
			newKinetics = new HMM_REVKinetics((SimpleReaction)reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeGeneralTotal_oldname) ) {
			//create GeneralTotalKinetics
			newKinetics = new GeneralLumpedKinetics(reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeGeneralLumped) ) {
			//create GeneralLumpedKinetics
			newKinetics = new GeneralLumpedKinetics(reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeGeneralCurrentLumped) ) {
			//create GeneralCurrentLumpedKinetics
			newKinetics = new GeneralCurrentLumpedKinetics(reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeGeneralPermeability) && reaction instanceof FluxReaction) {
			// create GeneralPermeabilityKinetics
			newKinetics = new GeneralPermeabilityKinetics((FluxReaction)reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeMacroscopic_Irr) && reaction instanceof SimpleReaction) {
			// create Macroscopic_IRRKinetics
			newKinetics = new Macroscopic_IRRKinetics((SimpleReaction)reaction);
		} else if ( type.equalsIgnoreCase(XMLTags.KineticsTypeMicroscopic_Irr) && reaction instanceof SimpleReaction) {
			// create Microscopic_IRRKinetics
			newKinetics = new Microscopic_IRRKinetics((SimpleReaction)reaction);
		}else {
			throw new XmlParseException("Unknown kinetics type: " + type);
		}
	}  catch (ExpressionException e) {
		e.printStackTrace();
		throw new XmlParseException("Error creating the kinetics for reaction: "+reaction.getName(), e);
	}
	
	try {
		newKinetics.reading(true);   // transaction begin flag ... yeah, this is a hack
		
		//Read all of the parameters
		List<Element> list = param.getChildren(XMLTags.ParameterTag, vcNamespace);

		// add constants that may be used in kinetics.
		// VariableHash varHash = getVariablesHash();
		ArrayList<String> reserved = new ArrayList<String>();
		
		ReservedSymbol[] reservedSymbols = reaction.getModel().getReservedSymbols();
		for (ReservedSymbol rs : reservedSymbols) {
			reserved.add(rs.getName());
		}

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
		} catch (MathException e){
			e.printStackTrace(System.out);
			throw new XmlParseException("error reordering parameters according to dependencies: ", e);
		}
		//
		// rename "special" parameters (those that are not "user defined")
		//
		for (Element xmlParam : list){
			String paramName = unMangle(xmlParam.getAttributeValue(XMLTags.NameAttrTag));
			String role = xmlParam.getAttributeValue(XMLTags.ParamRoleAttrTag);
			String paramExpStr = xmlParam.getText();
			Expression paramExp = unMangleExpression(paramExpStr);
			try {
				if (varHash.getVariable(paramName) == null){
					varHash.addVariable(new Function(paramName,paramExp,null));
				} else {
					if (reserved.contains(paramName)) {
						varHash.removeVariable(paramName);
						varHash.addVariable(new Function(paramName, paramExp,null));
					}
				}
			}catch (MathException e){
				e.printStackTrace(System.out);
				throw new XmlParseException("error reordering parameters according to dependencies: ", e);
			}
			Kinetics.KineticsParameter tempParam = null;
			if (!role.equals(XMLTags.ParamRoleUserDefinedTag)) {
				tempParam = newKinetics.getKineticsParameterFromRole(Kinetics.getParamRoleFromDefaultDesc(role));
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
			// hack from bringing in chargeValence parameters without breaking
			if (tempParam == null && Kinetics.getParamRoleFromDefaultDesc(role) == Kinetics.ROLE_ChargeValence){
				tempParam = newKinetics.getChargeValenceParameter();
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
				varHash.addVariable(new Function(unresolvedSymbol,new Expression(0.0),null));  // will turn into an UnresolvedParameter.
			}catch (MathException e){
				e.printStackTrace(System.out);
				throw new XmlParseException(e);
			}
			newKinetics.addUnresolvedParameter(unresolvedSymbol);
			unresolvedSymbol = varHash.getFirstUnresolvedSymbol();
		}
		
		Variable sortedVariables[] = varHash.getTopologicallyReorderedVariables();
		ModelUnitSystem modelUnitSystem = reaction.getModel().getUnitSystem();
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
					unit = modelUnitSystem.getInstance(symbol);
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
		throw new XmlParseException("Exception while setting parameters for Reaction : " + reaction.getName(), e);
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException("Exception while settings parameters for Reaction : " + reaction.getName(), e);
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
MathDescription getMathDescription(Element param, Geometry geometry) throws XmlParseException {
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

	try {
		mathdes.setGeometry(geometry);	//this step is needed!
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("a PropertyVetoException was fired when setting the Geometry to the Mathdescription in the simContext "+ name, e);
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
		throw new XmlParseException("A PropertyVetoException was fired when setting the name " + name + ", to a new MathDescription!", e);
	}
	
	VariableHash varHash = new VariableHash();
	
	//Retrieve Constant
	Iterator<Element> iterator = param.getChildren(XMLTags.ConstantTag, vcNamespace).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getConstant(tempelement));
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException(e);
		}
	}

	//Retrieve FilamentRegionVariables
	iterator = param.getChildren(XMLTags.FilamentRegionVariableTag, vcNamespace).iterator();
	while (iterator.hasNext()) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getFilamentRegionVariable(tempelement) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException(e);
		}
	}

	//Retrieve FilamentVariables
	iterator = param.getChildren(XMLTags.FilamentVariableTag, vcNamespace).iterator();
	while (iterator.hasNext()) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getFilamentVariable(tempelement) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException(e);
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
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException(e);
		}

	}
	
	//Retrieve MembraneVariable
	iterator = param.getChildren(XMLTags.MembraneVariableTag, vcNamespace).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getMemVariable(tempelement) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException(e);
		}

	}
	
	//Retrieve PointVariable
	iterator = param.getChildren(XMLTags.PointVariableTag, vcNamespace).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getPointVariable(tempelement) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException(e);
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
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException(e);
		}

	}
	
	//Retrieve VolumeVariable
	iterator = param.getChildren(XMLTags.VolumeVariableTag, vcNamespace).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getVolVariable(tempelement) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException(e);
		}
	}

	//Retrieve StochVolVariable
	iterator = param.getChildren(XMLTags.StochVolVariableTag, vcNamespace).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable( getStochVolVariable(tempelement) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException(e);
		}
	}
	
	//Retrieve all the Functions //This needs to be processed before all the variables are read!
	iterator = param.getChildren(XMLTags.FunctionTag, vcNamespace).iterator();
	while ( iterator.hasNext() ){
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable(getFunction(tempelement));
		}catch (MathException e){
			e.printStackTrace();
			throw new XmlParseException(e);
		}
	}

	iterator = param.getChildren(XMLTags.VolumeRandomVariableTag, vcNamespace).iterator();
	while ( iterator.hasNext() ){
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable(getRandomVariable(tempelement));
		}catch (MathException e){
			e.printStackTrace();
			throw new XmlParseException(e);
		}
	}
	iterator = param.getChildren(XMLTags.MembraneRandomVariableTag, vcNamespace).iterator();
	while ( iterator.hasNext() ){
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable(getRandomVariable(tempelement));
		}catch (MathException e){
			e.printStackTrace();
			throw new XmlParseException(e);
		}
	}
	iterator = param.getChildren(XMLTags.VolumeParticleVariableTag, vcNamespace).iterator();
	while ( iterator.hasNext() ){
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable(getVolumeParticalVariable(tempelement));
		}catch (MathException e){
			e.printStackTrace();
			throw new XmlParseException(e);
		}
	}
	iterator = param.getChildren(XMLTags.MembraneParticleVariableTag, vcNamespace).iterator();
	while ( iterator.hasNext() ){
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable(getMembraneParticalVariable(tempelement));
		}catch (MathException e){
			e.printStackTrace();
			throw new XmlParseException(e);
		}
	}
	
	// ParticleMolecularTypeTag       getParticleMolecularTypes
	// has to be done before VolumeParticleSpeciesPattern and VolumeParticleObservable
	iterator = param.getChildren(XMLTags.ParticleMolecularTypeTag, vcNamespace).iterator();
	while ( iterator.hasNext() ){
		tempelement = (Element)iterator.next();
		mathdes.addParticleMolecularType(getParticleMolecularType(tempelement));
	}
	// VolumeParticleSpeciesPatternTag
	iterator = param.getChildren(XMLTags.VolumeParticleSpeciesPatternTag, vcNamespace).iterator();
	while ( iterator.hasNext() ){
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable(getVolumeParticleSpeciesPattern(tempelement, mathdes));
		}catch (MathException e){
			e.printStackTrace();
			throw new XmlParseException(e);
		}
	}
	// VolumeParticleObservableTag    getParticleObservables
	iterator = param.getChildren(XMLTags.VolumeParticleObservableTag, vcNamespace).iterator();
	while ( iterator.hasNext() ){
		tempelement = (Element)iterator.next();
		try {
			varHash.addVariable(getVolumeParticleObservable(tempelement, varHash));
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException(e);
		}
	}
	
	//
	// add all variables at once
	//
	try {
		mathdes.setAllVariables(varHash.getAlphabeticallyOrderedVariables());
	} catch (MathException e) {
		e.printStackTrace();
		throw new XmlParseException("Error adding the Function variables to the MathDescription " + name, e);
	} catch (ExpressionBindingException e) {
		e.printStackTrace();
		throw new XmlParseException("Error adding the Function variables to the MathDescription " + name, e);
	}

	//Retrieve CompartmentsSubdomains
	iterator = param.getChildren(XMLTags.CompartmentSubDomainTag, vcNamespace).iterator();
	while ( iterator.hasNext() ) {
		tempelement = (Element)iterator.next();
		try {
			mathdes.addSubDomain( getCompartmentSubDomain(tempelement, mathdes) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("Error adding a new CompartmentSubDomain to the MathDescription " + name, e);
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
			throw new XmlParseException("Error adding a new MembraneSubDomain to the MathDescription " + name, e);
		}
	}
	
	//Retrieve the FilamentSubdomain (if any)
	tempelement = param.getChild(XMLTags.FilamentSubDomainTag, vcNamespace);
	if (tempelement != null) {
		try {
			mathdes.addSubDomain( getFilamentSubDomain(tempelement, mathdes) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("Error adding a new FilamentSubDomain to the MathDescription " + name, e);
		}
	}
	
	//Retrieve the PointSubdomain (if any)
	tempelement = param.getChild(XMLTags.PointSubDomainTag, vcNamespace);
	if (tempelement != null) {
		try {
			mathdes.addSubDomain( getPointSubDomain(tempelement, mathdes) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("Error adding a new PointSubDomain to the MathDescription " + name, e);
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
			throw new XmlParseException(e);
		}
	}
	iterator = param.getChildren(XMLTags.PostProcessingBlock, vcNamespace).iterator();
	while (iterator.hasNext()) {
		tempelement = (Element)iterator.next();
		getPostProcessingBlock(mathdes, tempelement);
	}
	return mathdes;
}

private void getPostProcessingBlock(MathDescription mathDesc, Element element) throws XmlParseException {
	Iterator<Element> iterator = element.getChildren(XMLTags.ExplicitDataGenerator, vcNamespace).iterator();
	while (iterator.hasNext()){
		Element tempelement = (Element)iterator.next();
		
		ExplicitDataGenerator explicitDataGenerator = getExplicitDataGenerator(tempelement);
		try {
			mathDesc.getPostProcessingBlock().addDataGenerator(explicitDataGenerator);
		} catch (MathException e) {
			throw new XmlParseException(e);
		}
	}
	iterator = element.getChildren(XMLTags.ProjectionDataGenerator, vcNamespace).iterator();
	while (iterator.hasNext()){
		Element tempelement = (Element)iterator.next();
		
		ProjectionDataGenerator projectionDataGenerator = getProjectionDataGenerator(tempelement);
		try {
			mathDesc.getPostProcessingBlock().addDataGenerator(projectionDataGenerator);
		} catch (MathException e) {
			throw new XmlParseException(e);
		}
	}
	iterator = element.getChildren(XMLTags.ConvolutionDataGenerator, vcNamespace).iterator();
	while (iterator.hasNext()){
		Element tempelement = (Element)iterator.next();
		
		ConvolutionDataGenerator convolutionDataGenerator = getConvolutionDataGenerator(tempelement);
		try {
			mathDesc.getPostProcessingBlock().addDataGenerator(convolutionDataGenerator);
		} catch (MathException e) {
			throw new XmlParseException(e);
		}
	}
}

private ExplicitDataGenerator getExplicitDataGenerator(Element element) {
	String name = unMangle( element.getAttributeValue( XMLTags.NameAttrTag) );
	String domainStr = unMangle( element.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}
	String temp = element.getText();
	
	Expression exp = unMangleExpression(temp);
	ExplicitDataGenerator explicitDataGenerator = new ExplicitDataGenerator(name, domain, exp);
	return explicitDataGenerator;
}

private ConvolutionDataGenerator getConvolutionDataGenerator(Element element) {
	String name = unMangle( element.getAttributeValue( XMLTags.NameAttrTag) );

	Expression volumeFunction = null;
	Element volumeFunctionElement = element.getChild(XMLTags.FunctionTag, vcNamespace);
	if (volumeFunctionElement == null){
		volumeFunctionElement = element.getChild(XMLTags.VolumeFunctionTag, vcNamespace);
	}
	if (volumeFunctionElement != null){
		String s = volumeFunctionElement.getText();	
		volumeFunction = unMangleExpression(s);
	}
	
	Expression membraneFunction = null;	
	Element membraneFunctionElement = element.getChild(XMLTags.MembraneFunctionTag, vcNamespace);
	if (membraneFunctionElement != null){
		String s = membraneFunctionElement.getText();	
		membraneFunction = unMangleExpression(s);
	}
	
	ConvolutionDataGeneratorKernel kernel = null;
	Element kernelElement = element.getChild(XMLTags.Kernel, vcNamespace);
	String kernelType = kernelElement.getAttributeValue(XMLTags.TypeAttrTag);
	if (kernelType.equals(XMLTags.KernelType_Gaussian)) {
		Element e0 = kernelElement.getChild(XMLTags.KernelGaussianSigmaXY, vcNamespace);
		String s = e0.getText();	
		Expression sigmaXY = unMangleExpression(s);
		
		e0 = kernelElement.getChild(XMLTags.KernelGaussianSigmaZ, vcNamespace);
		s = e0.getText();	
		Expression sigmaZ = unMangleExpression(s);
		
		kernel = new GaussianConvolutionDataGeneratorKernel(sigmaXY, sigmaZ);
	}
	
	ConvolutionDataGenerator cdg = new ConvolutionDataGenerator(name, kernel, volumeFunction, membraneFunction);
	return cdg;
}

private ProjectionDataGenerator getProjectionDataGenerator(Element element) {
	String name = unMangle( element.getAttributeValue( XMLTags.NameAttrTag) );
	String domainStr = unMangle( element.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}

	Element e = element.getChild(XMLTags.ProjectionAxis, vcNamespace);
	String axis = e.getText();
//	ProjectionDataGenerator.Axis axis = ProjectionDataGenerator.Axis.valueOf(s);
	
	e = element.getChild(XMLTags.ProjectionOperation, vcNamespace);
	String operation = e.getText();
//	ProjectionDataGenerator.Operation operation = ProjectionDataGenerator.Operation.valueOf(s);	
	
	e = element.getChild(XMLTags.FunctionTag, vcNamespace);
	String s = e.getText();	
	Expression exp = unMangleExpression(s);
	ProjectionDataGenerator projectionDataGenerator = new ProjectionDataGenerator(name, domain, axis, operation, exp);
	return projectionDataGenerator;
}

private RandomVariable getRandomVariable(Element param) throws XmlParseException {
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
	
	String domainStr = unMangle( param.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}

	RandomVariable var = null;
	if (param.getName().equals(XMLTags.VolumeRandomVariableTag)) {
		 var = new VolumeRandomVariable(name, seed, dist, domain);
	} else if (param.getName().equals(XMLTags.MembraneRandomVariableTag)) {
		var = new MembraneRandomVariable(name, seed, dist, domain);
	} else {
		throw new XmlParseException(param.getName() + " is not supported!");
	}
	transcribeComments(param, var);
	return var;
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
	transcribeComments(eventElement, event);
	return event;
}

public BioEvent[] getBioEvents(SimulationContext simContext, Element bioEventsElement) throws XmlParseException  {
	Iterator<Element> bioEventsIterator = bioEventsElement.getChildren(XMLTags.BioEventTag, vcNamespace).iterator();
	Vector<BioEvent> bioEventsVector = new Vector<BioEvent>();
	while (bioEventsIterator.hasNext()) {
		Element bEventElement = (Element) bioEventsIterator.next();

		BioEvent newBioEvent = null;
		String name = unMangle(bEventElement.getAttributeValue(XMLTags.NameAttrTag));
		Element triggerElement = bEventElement.getChild(XMLTags.TriggerTag, vcNamespace);
		if (triggerElement != null && triggerElement.getText().length()>0){
			//
			// read legacy VCell 5.3 style trigger and delay elements
			//
			// <Trigger>(t>3.0)</Trigger>
			// <Delay UseValuesFromTriggerTime="true">3.0</Delay>     [optional]
			// 
			Expression triggerExpression = unMangleExpression(triggerElement.getText());
			
			// read <Delay>
			Expression delayDurationExpression = null;
			boolean useValuesFromTriggerTime = true;
			Element delayElement = bEventElement.getChild(XMLTags.DelayTag, vcNamespace);
			if (delayElement != null) {
				useValuesFromTriggerTime = Boolean.valueOf(delayElement.getAttributeValue(XMLTags.UseValuesFromTriggerTimeAttrTag)).booleanValue();
				delayDurationExpression = unMangleExpression((delayElement.getText()));
			}
			
			newBioEvent = new BioEvent(name, TriggerType.GeneralTrigger, useValuesFromTriggerTime, simContext);
			try {
				newBioEvent.setParameterValue(BioEventParameterType.GeneralTriggerFunction, triggerExpression);
				if (delayDurationExpression != null){
					newBioEvent.setParameterValue(BioEventParameterType.TriggerDelay, delayDurationExpression);
				}
			} catch (ExpressionBindingException | PropertyVetoException e) {
				e.printStackTrace();
				throw new XmlParseException("failed to read trigger or delay expressions in bioEvent "+name+": "+e.getMessage(), e);
			}
			
		} else if (triggerElement != null && triggerElement.getText().length()==0){
			//
			// read legacy first-pass VCell 5.4 style trigger and delay elements
			//
			// <Trigger>
			//		<TriggerParameters triggerClass="TriggerGeneral">
			//			(t > 500.0)
			//      </TriggerParameters>
			// </Trigger>
			// <Delay UseValuesFromTriggerTime="true">3.0</Delay>     [optional]
			// 
			final String TriggerParametersTag = "TriggerParameters";
			final String TriggerClassAttrTag = "triggerClass";
			final String TriggerClassAttrValue_TriggerGeneral = "TriggerGeneral";
			
			Element triggerParametersElement = triggerElement.getChild(TriggerParametersTag, vcNamespace);
			
			Expression triggerExpression = null;
			
			String triggerClass = triggerParametersElement.getAttributeValue(TriggerClassAttrTag);
			if (triggerClass.equals(TriggerClassAttrValue_TriggerGeneral)){
				triggerExpression = unMangleExpression(triggerParametersElement.getText());
			}else{
				// not general trigger (just make it never happen, user will have to edit "t > -1")
				triggerExpression = Expression.relational(">", new Expression(simContext.getModel().getTIME(), simContext.getModel().getNameScope()),new Expression(-1.0));
			}
			
			// read <Delay>
			Expression delayDurationExpression = null;
			boolean useValuesFromTriggerTime = true;
			Element delayElement = bEventElement.getChild(XMLTags.DelayTag, vcNamespace);
			if (delayElement != null) {
				useValuesFromTriggerTime = Boolean.valueOf(delayElement.getAttributeValue(XMLTags.UseValuesFromTriggerTimeAttrTag)).booleanValue();
				delayDurationExpression = unMangleExpression((delayElement.getText()));
			}
			
			newBioEvent = new BioEvent(name, TriggerType.GeneralTrigger, useValuesFromTriggerTime, simContext);
			try {
				newBioEvent.setParameterValue(BioEventParameterType.GeneralTriggerFunction, triggerExpression);
				if (delayDurationExpression != null){
					newBioEvent.setParameterValue(BioEventParameterType.TriggerDelay, delayDurationExpression);
				}
			} catch (ExpressionBindingException | PropertyVetoException e) {
				e.printStackTrace();
				throw new XmlParseException("failed to read trigger or delay expressions in bioEvent "+name+": "+e.getMessage(), e);
			}
				
		}else{
			//
			// VCell 5.4 style bioevent parameters
			// 
			//
			TriggerType triggerType = TriggerType.fromXmlName(bEventElement.getAttributeValue(XMLTags.BioEventTriggerTypeAttrTag));
			boolean bUseValuesFromTriggerTime = Boolean.parseBoolean(bEventElement.getAttributeValue(XMLTags.UseValuesFromTriggerTimeAttrTag));

			newBioEvent = new BioEvent(name, triggerType, bUseValuesFromTriggerTime, simContext);
			
			Iterator<Element> paramElementIter = bEventElement.getChildren(XMLTags.ParameterTag, vcNamespace).iterator();
			ArrayList<LocalParameter> parameters = new ArrayList<LocalParameter>();
			
			boolean bHasGeneralTriggerParam = false;

			while (paramElementIter.hasNext()){
				Element paramElement = paramElementIter.next();

				//Get parameter attributes
				String paramName = paramElement.getAttributeValue(XMLTags.NameAttrTag);
				Expression exp = unMangleExpression(paramElement.getText());
				String roleStr = paramElement.getAttributeValue(XMLTags.ParamRoleAttrTag);
				BioEventParameterType parameterType = BioEventParameterType.fromRoleXmlName(roleStr);
				if (parameterType == BioEventParameterType.GeneralTriggerFunction){
					bHasGeneralTriggerParam = true;
				}
				VCUnitDefinition unit = simContext.getModel().getUnitSystem().getInstance_TBD();
				String unitSymbol = paramElement.getAttributeValue(XMLTags.VCUnitDefinitionAttrTag);
				if (unitSymbol != null) {
					unit = simContext.getModel().getUnitSystem().getInstance(unitSymbol);
				}
				
				parameters.add(newBioEvent.createNewParameter(paramName, parameterType, exp, unit));
			}
			if (!bHasGeneralTriggerParam){
				parameters.add(newBioEvent.createNewParameter(
						BioEventParameterType.GeneralTriggerFunction.getDefaultName(), 
						BioEventParameterType.GeneralTriggerFunction, 
						null, // computed as needed
						simContext.getModel().getUnitSystem().getInstance_DIMENSIONLESS()));
			}
			try {
				newBioEvent.setParameters(parameters.toArray(new LocalParameter[0]));
			} catch (PropertyVetoException | ExpressionBindingException e) {
				e.printStackTrace();
				throw new XmlParseException("failed to read parameters in bioEvent "+name+": "+e.getMessage(), e);
			}
		}
		
		ArrayList<BioEvent.EventAssignment> eventAssignmentList = new ArrayList<BioEvent.EventAssignment>();
		Iterator<Element> iter = bEventElement.getChildren(XMLTags.EventAssignmentTag, vcNamespace).iterator();
		while (iter.hasNext()) {
			Element eventAssignmentElement = iter.next();
			try {
				String varname = eventAssignmentElement.getAttributeValue(XMLTags.EventAssignmentVariableAttrTag);
				Expression assignExp = unMangleExpression(eventAssignmentElement.getText());
				SymbolTableEntry target = simContext.getEntry(varname);
				BioEvent.EventAssignment eventAssignment = newBioEvent.new EventAssignment(target, assignExp);
				eventAssignmentList.add(eventAssignment);
			} catch (ExpressionException e) {
				e.printStackTrace(System.out);
				throw new XmlParseException(e);
			}
		}
		try {
			newBioEvent.setEventAssignmentsList(eventAssignmentList);
		} catch (PropertyVetoException e1) {
			e1.printStackTrace(System.out);
			throw new XmlParseException(e1);
		}
		try {
			newBioEvent.bind();
		} catch (ExpressionBindingException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException(e);
		}
		bioEventsVector.add(newBioEvent);
	}
	
	return ((BioEvent[])BeanUtils.getArray(bioEventsVector, BioEvent.class));
}

public SpatialObject[] getSpatialObjects(SimulationContext simContext, Element spatialObjectsElement) throws XmlParseException  {
	Iterator<Element> spatialObjectElementIterator = spatialObjectsElement.getChildren(XMLTags.SpatialObjectTag, vcNamespace).iterator();
	ArrayList<SpatialObject> spatialObjectList = new ArrayList<SpatialObject>();
	while (spatialObjectElementIterator.hasNext()) {
		Element spatialObjectElement = (Element) spatialObjectElementIterator.next();

		SpatialObject spatialObject = null;
		String name = unMangle(spatialObjectElement.getAttributeValue(XMLTags.NameAttrTag));
		String type = unMangle(spatialObjectElement.getAttributeValue(XMLTags.SpatialObjectTypeAttrTag));
		if (type.equals(XMLTags.SpatialObjectTypeAttrValue_Point)){
			PointObject pointObject = new PointObject(name,simContext);
			spatialObject = pointObject;
		}else if (type.equals(XMLTags.SpatialObjectTypeAttrValue_Surface)){
			String insideSubvolumeName = unMangle(spatialObjectElement.getAttributeValue(XMLTags.SpatialObjectSubVolumeInsideAttrTag));
			String insideRegionIDString = spatialObjectElement.getAttributeValue(XMLTags.SpatialObjectRegionIdInsideAttrTag);
			String outsideSubvolumeName = unMangle(spatialObjectElement.getAttributeValue(XMLTags.SpatialObjectSubVolumeOutsideAttrTag));
			String outsideRegionIDString = spatialObjectElement.getAttributeValue(XMLTags.SpatialObjectRegionIdOutsideAttrTag);
			
			SubVolume insideSubvolume = null;
			if (insideSubvolumeName!=null){
				insideSubvolume = simContext.getGeometry().getGeometrySpec().getSubVolume(insideSubvolumeName);
			}
			Integer insideRegionID = null;
			if (insideRegionIDString!=null){
				insideRegionID = Integer.parseUnsignedInt(insideRegionIDString);
			}
			SubVolume outsideSubvolume = null;
			if (outsideSubvolumeName!=null){
				outsideSubvolume = simContext.getGeometry().getGeometrySpec().getSubVolume(outsideSubvolumeName);
			}
			Integer outsideRegionID = null;
			if (outsideRegionIDString!=null){
				outsideRegionID = Integer.parseUnsignedInt(outsideRegionIDString);
			}
			SurfaceRegionObject surfaceRegionObject = new SurfaceRegionObject(name,insideSubvolume,insideRegionID,outsideSubvolume,outsideRegionID,simContext);
			spatialObject = surfaceRegionObject;
		}else if (type.equals(XMLTags.SpatialObjectTypeAttrValue_Volume)){
			String subvolumeName = unMangle(spatialObjectElement.getAttributeValue(XMLTags.SpatialObjectSubVolumeAttrTag));
			String regionIDString = spatialObjectElement.getAttributeValue(XMLTags.SpatialObjectRegionIdAttrTag);
			
			SubVolume subvolume = null;
			if (subvolumeName!=null){
				subvolume = simContext.getGeometry().getGeometrySpec().getSubVolume(subvolumeName);
			}
			Integer regionID = null;
			if (regionIDString!=null){
				regionID = Integer.parseUnsignedInt(regionIDString);
			}
			VolumeRegionObject volumeRegionObject = new VolumeRegionObject(name,subvolume,regionID,simContext);
			spatialObject = volumeRegionObject;
		}
		
		// set Quantity enables
		Element quantityCategoryListElement = spatialObjectElement.getChild(XMLTags.QuantityCategoryListTag, vcNamespace);
		List<Element> quantityCategoryElements = quantityCategoryListElement.getChildren(XMLTags.QuantityCategoryTag, vcNamespace);
		for (Element quantityCategoryElement : quantityCategoryElements){
			String quantityCategoryName = unMangle(quantityCategoryElement.getAttributeValue(XMLTags.QuantityCategoryNameAttrTag));
			Boolean enabled = Boolean.parseBoolean(quantityCategoryElement.getAttributeValue(XMLTags.QuantityCategoryEnabledAttrTag));
			QuantityCategory category = QuantityCategory.fromXMLName(quantityCategoryName);
			spatialObject.setQuantityCategoryEnabled(category, enabled);
		}
		
		spatialObjectList.add(spatialObject);
	}
	return spatialObjectList.toArray(new SpatialObject[0]);
}

public SpatialProcess[] getSpatialProcesses(SimulationContext simContext, Element spatialProcessesElement) throws XmlParseException  {
	Iterator<Element> spatialProcessElementIterator = spatialProcessesElement.getChildren(XMLTags.SpatialProcessTag, vcNamespace).iterator();
	ArrayList<SpatialProcess> spatialProcessList = new ArrayList<SpatialProcess>();
	while (spatialProcessElementIterator.hasNext()) {
		Element spatialProcessElement = (Element) spatialProcessElementIterator.next();

		SpatialProcess spatialProcess = null;
		String name = unMangle(spatialProcessElement.getAttributeValue(XMLTags.NameAttrTag));
		String type = unMangle(spatialProcessElement.getAttributeValue(XMLTags.SpatialProcessTypeAttrTag));
		if (type.equals(XMLTags.SpatialProcessTypeAttrValue_PointKinematics)){
			PointKinematics pointKinematics = new PointKinematics(name,simContext);
			String pointObjectName = spatialProcessElement.getAttributeValue(XMLTags.SpatialProcessPointObjectAttrTag);
			PointObject pointObject = (PointObject)simContext.getSpatialObject(pointObjectName);
			pointKinematics.setPointObject(pointObject);
			spatialProcess = pointKinematics;
		}else if (type.equals(XMLTags.SpatialProcessTypeAttrValue_PointLocation)){
			PointLocation pointLocation = new PointLocation(name,simContext);
			String pointObjectName = spatialProcessElement.getAttributeValue(XMLTags.SpatialProcessPointObjectAttrTag);
			PointObject pointObject = (PointObject)simContext.getSpatialObject(pointObjectName);
			pointLocation.setPointObject(pointObject);
			spatialProcess = pointLocation;
		}else if (type.equals(XMLTags.SpatialProcessTypeAttrValue_SurfaceKinematics)){
			SurfaceKinematics surfaceKinematics = new SurfaceKinematics(name,simContext);
			String surfaceRegionObjectName = spatialProcessElement.getAttributeValue(XMLTags.SpatialProcessSurfaceObjectAttrTag);
			SurfaceRegionObject surfaceRegionObject = (SurfaceRegionObject)simContext.getSpatialObject(surfaceRegionObjectName);
			surfaceKinematics.setSurfaceRegionObject(surfaceRegionObject);
			spatialProcess = surfaceKinematics;
		}else if (type.equals(XMLTags.SpatialProcessTypeAttrValue_VolumeKinematics)){
			VolumeKinematics volumeKinematics = new VolumeKinematics(name,simContext);
			String volumeRegionObjectName = spatialProcessElement.getAttributeValue(XMLTags.SpatialProcessVolumeObjectAttrTag);
			VolumeRegionObject volumeRegionObject = (VolumeRegionObject)simContext.getSpatialObject(volumeRegionObjectName);
			volumeKinematics.setVolumeRegionObject(volumeRegionObject);
			spatialProcess = volumeKinematics;
		}
		
		// set parameters
		Iterator<Element> paramElementIter = spatialProcessElement.getChildren(XMLTags.ParameterTag, vcNamespace).iterator();
		ArrayList<LocalParameter> parameters = new ArrayList<LocalParameter>();
		
		while (paramElementIter.hasNext()){
			Element paramElement = paramElementIter.next();

			//Get parameter attributes
			String paramName = paramElement.getAttributeValue(XMLTags.NameAttrTag);
			Expression exp = unMangleExpression(paramElement.getText());
			String roleStr = paramElement.getAttributeValue(XMLTags.ParamRoleAttrTag);
			SpatialProcessParameterType parameterType = SpatialProcessParameterType.fromRoleXmlName(roleStr);
			VCUnitDefinition unit = simContext.getModel().getUnitSystem().getInstance_TBD();
			String unitSymbol = paramElement.getAttributeValue(XMLTags.VCUnitDefinitionAttrTag);
			if (unitSymbol != null) {
				unit = simContext.getModel().getUnitSystem().getInstance(unitSymbol);
			}
			parameters.add(spatialProcess.createNewParameter(paramName, parameterType, exp, unit));
		}
		try {
			spatialProcess.setParameters(parameters.toArray(new LocalParameter[0]));
		} catch (PropertyVetoException | ExpressionBindingException e) {
			e.printStackTrace();
			throw new XmlParseException("failed to read parameters in bioEvent "+name+": "+e.getMessage(), e);
		}
		
		spatialProcessList.add(spatialProcess);
	}
	return spatialProcessList.toArray(new SpatialProcess[0]);
}

public RateRule[] getRateRules(SimulationContext simContext, Element rateRulesElement) throws XmlParseException  {
	Iterator<Element> rateRulesIterator = rateRulesElement.getChildren(XMLTags.RateRuleTag, vcNamespace).iterator();
	Vector<RateRule> rateRulesVector = new Vector<RateRule>();
	while (rateRulesIterator.hasNext()) {
		Element rrElement = (Element) rateRulesIterator.next();

		RateRule newRateRule = null;
		try {
			String rrName = unMangle(rrElement.getAttributeValue(XMLTags.NameAttrTag));
			String varname = rrElement.getAttributeValue(XMLTags.RateRuleVariableAttrTag);
			SymbolTableEntry rrVar = simContext.getEntry(varname);
			Expression rrExp = unMangleExpression(rrElement.getText());
		    newRateRule = new RateRule(rrName, rrVar, rrExp, simContext);
			newRateRule.bind();
		} catch (ExpressionBindingException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException(e.getMessage());
		}
		if (newRateRule != null) {
			rateRulesVector.add(newRateRule);
		}
	}
	return ((RateRule[])BeanUtils.getArray(rateRulesVector, RateRule.class));
}
public AssignmentRule[] getAssignmentRules(SimulationContext simContext, Element assignmentRulesElement) throws XmlParseException  {
	Iterator<Element> assignmentRulesIterator = assignmentRulesElement.getChildren(XMLTags.AssignmentRuleTag, vcNamespace).iterator();
	Vector<AssignmentRule> assignmentRulesVector = new Vector<AssignmentRule>();
	while (assignmentRulesIterator.hasNext()) {
		Element rrElement = (Element) assignmentRulesIterator.next();

		AssignmentRule newAssignmentRule = null;
		try {
			String rrName = unMangle(rrElement.getAttributeValue(XMLTags.NameAttrTag));
			String varname = rrElement.getAttributeValue(XMLTags.AssignmentRuleVariableAttrTag);
			SymbolTableEntry rrVar = simContext.getEntry(varname);
			Expression rrExp = unMangleExpression(rrElement.getText());
		    newAssignmentRule = new AssignmentRule(rrName, rrVar, rrExp, simContext);
			newAssignmentRule.bind();
		} catch (ExpressionBindingException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException(e.getMessage());
		}
		if (newAssignmentRule != null) {
			assignmentRulesVector.add(newAssignmentRule);
		}
	}
	return ((AssignmentRule[])BeanUtils.getArray(assignmentRulesVector, AssignmentRule.class));
}


public ReactionRuleSpec[] getReactionRuleSpecs(SimulationContext simContext, Element reactionRuleSpecsElement) throws XmlParseException  {
	List<Element> reactionRulesSpecIterator = reactionRuleSpecsElement.getChildren(XMLTags.ReactionRuleSpecTag, vcNamespace);
	ArrayList<ReactionRuleSpec> reactionRuleSpecs = new ArrayList<ReactionRuleSpec>();
	for (Element rrElement : reactionRulesSpecIterator){
		String rrName = unMangle(rrElement.getAttributeValue(XMLTags.ReactionRuleRefAttrTag));
		String rrMappingString = rrElement.getAttributeValue(XMLTags.ReactionRuleMappingAttrTag);
		ReactionRuleMappingType rrMapping = ReactionRuleMappingType.fromDatabaseName(rrMappingString);
		ReactionRule reactionRule = simContext.getModel().getRbmModelContainer().getReactionRule(rrName);
	    ReactionRuleSpec reactionRuleSpec = new ReactionRuleSpec(reactionRule);
	    reactionRuleSpec.setReactionRuleMapping(rrMapping);
		reactionRuleSpecs.add(reactionRuleSpec);
	}
	
	return reactionRuleSpecs.toArray(new ReactionRuleSpec[0]);
}


/*
public RateRuleVariable[] getRateRuleVariables(Element rateRuleVarsElement, Model model) throws XmlParseException  {
	Iterator<Element> rateRuleVarsIterator = rateRuleVarsElement.getChildren(XMLTags.RateRuleVariableTag, vcNamespace).iterator();
	Vector<RateRuleVariable> rateRuleVarsVector = new Vector<RateRuleVariable>();
	while (rateRuleVarsIterator.hasNext()) {
		Element rrvElement = (Element) rateRuleVarsIterator.next();

		RateRuleVariable newRateRuleVar = null;
		try {
			String rrvName = unMangle(rrvElement.getAttributeValue(XMLTags.NameAttrTag));
		    String rrvStructureName = unMangle(rrvElement.getAttributeValue(XMLTags.StructureAttrTag));
		    // structure can be null
		    Structure rrvStructure = null;
		    if (rrvStructureName != null) {
		    	rrvStructure = (Structure) model.getStructure(rrvStructureName);
		    }
//		    if (structureref == null) {
//		    	throw new XmlParseException("The structure " + rrvStructureName + "could not be resolved!");
//		    }
		    String rrvRoleStr = rrvElement.getAttributeValue(XMLTags.ParamRoleAttrTag);
		    int rrvRole = RateRuleVariable.getParamRoleFromDesc(rrvRoleStr);
		    Element rrvParamElement = rrvElement.getChild(XMLTags.ParameterTag, vcNamespace);
		    ModelParameter rrvParameter = getModelParameter(rrvParamElement, model);
		    newRateRuleVar = new RateRuleVariable(rrvName, rrvStructure, rrvParameter, rrvRole);
			newRateRuleVar.bind();
		} catch (ExpressionBindingException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException(e.getMessage());
		}
		if (newRateRuleVar != null) {
			rateRuleVarsVector.add(newRateRuleVar);
		}
	}
	
	return ((RateRuleVariable[])BeanUtils.getArray(rateRuleVarsVector, RateRuleVariable.class));
}
*/

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
		throw new XmlParseException("An error occurred while trying to set the name " + param.getAttributeValue(XMLTags.NameAttrTag) + "to a MathModel!", e);
	}
	
	//set Geometry (if any)
	Element tempElem = param.getChild(XMLTags.GeometryTag, vcNamespace);
	Geometry tempGeometry = getGeometry(tempElem);
	
		

	//set MathDescription
	tempElem = param.getChild(XMLTags.MathDescriptionTag, vcNamespace);
	MathDescription mathDesc = getMathDescription(tempElem, tempGeometry);
	
	if ( tempElem != null) {
		mathmodel.setMathDescription( mathDesc );
	} else {
		throw new XmlParseException("MathDescription missing in this MathModel!");
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
			throw new XmlParseException(e);		
		}
	}

	//Set simulations contexts (if any)
	List<Element> childList = param.getChildren(XMLTags.SimulationTag, vcNamespace);
	Simulation[] simList = new Simulation[childList.size()];
	int simCounter = 0;
	for (Element simElement : childList){
		simList[simCounter] = getSimulation(simElement, mathDesc);
		simCounter ++;
	}
	try {
		mathmodel.setSimulations(simList);
	} catch(java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException occurred when adding the Simulations to the MathModel " + name, e);
	}

	return mathmodel;
}


/**
 * This method returns a MathOverrides object from a XML Element.
 * Creation date: (5/21/2001 3:05:17 PM)
 * @return cbit.vcell.solver.MathOverrides
 * @param param org.jdom.Element
 */
private MathOverrides getMathOverrides(Element param, Simulation simulation) throws XmlParseException{

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
		throw new XmlParseException("A ExpressionException was fired when adding a Constant to the MathOverrides", e);
	} catch (DataConversionException e2) {
		e2.printStackTrace();
		throw new XmlParseException("A DataConversionException occured when reading a ConstantArraySpec type", e2);
	}
	return mathOverrides;
}


/**
 * This method returns a Membrane object from a XML element.
 * Creation date: (4/4/2001 4:17:32 PM)
 * @return cbit.vcell.model.Membrane
 * @param param org.jdom.Element
 */
private Membrane getMembrane(Model model, Element param, List<Structure> featureList) throws XmlParseException {
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
			"An error occurred while trying to create the Membrane object " + name, e);
	}
	//set inside feature
	String infeaturename = unMangle(param.getAttributeValue(XMLTags.InsideFeatureTag));
	String outfeaturename = unMangle(param.getAttributeValue(XMLTags.OutsideFeatureTag));

	String posFeatureName = unMangle(param.getAttributeValue(XMLTags.PositiveFeatureTag));
	String negFeatureName = unMangle(param.getAttributeValue(XMLTags.NegativeFeatureTag));

	Feature infeatureref = null; 
	Feature outfeatureref = null;
	Feature posFeature = null;
	Feature negFeature = null;
	
	for (Structure s : featureList) {
		String sname = s.getName();
		if (sname.equals(infeaturename)) {		
			infeatureref = (Feature)s;
		} 
		if (sname.equals(outfeaturename)) {
			outfeatureref = (Feature)s;
		}
		if (sname.equals(posFeatureName)) {		
			posFeature = (Feature)s;
		} 
		if (sname.equals(negFeatureName)) {
			negFeature = (Feature)s;
		}
	}
	
	//set inside and outside features
	if (infeatureref != null) {
		model.getStructureTopology().setInsideFeature(newmembrane,infeatureref);
	}
	if (outfeatureref != null) {
		model.getStructureTopology().setOutsideFeature(newmembrane,outfeatureref);
	}
	//set positive & negative features
	if (posFeature != null) {
		model.getElectricalTopology().setPositiveFeature(newmembrane, posFeature);
	}
	if (negFeature != null) {
		model.getElectricalTopology().setNegativeFeature(newmembrane, negFeature);
	}
	//set MemVoltName
	if (param.getAttribute(XMLTags.MemVoltNameTag)==null) {
		throw new XmlParseException("Error reading membrane Voltage Name!");
	}
	String memvoltName = unMangle( param.getAttributeValue(XMLTags.MemVoltNameTag) );
	try {
		newmembrane.getMembraneVoltage().setName(memvoltName);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("Error setting the membrane Voltage Name", e);
	}

	return newmembrane;
}


/**
 * This method retuns a MembraneMapping object from a XML representation.
 * Creation date: (5/7/2001 4:12:03 PM)
 * @return cbit.vcell.mapping.MembraneMapping
 * @param param org.jdom.Element
 */
private MembraneMapping getMembraneMapping(Element param, SimulationContext simulationContext) throws XmlParseException{
	//Retrieve attributes
	String membranename = unMangle( param.getAttributeValue(XMLTags.MembraneAttrTag) );
	
	Membrane membraneref = (Membrane)simulationContext.getModel().getStructure(membranename);
	if (membraneref == null) {
		throw new XmlParseException("The Membrane "+ membranename + " could not be resolved!");
	}

	//*** Create new Membrane Mapping ****
	MembraneMapping memmap = new MembraneMapping(membraneref, simulationContext, simulationContext.getModel().getUnitSystem());

	//Set SurfacetoVolumeRatio when it exists, amended Sept. 27th, 2007
	if(param.getAttributeValue(XMLTags.SurfaceToVolumeRatioTag)!= null)
	{
		String ratio = unMangle( param.getAttributeValue(XMLTags.SurfaceToVolumeRatioTag) );
		try {
			memmap.getSurfaceToVolumeParameter().setExpression(unMangleExpression(ratio));
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException("An expressionException was fired when setting the SurfacetoVolumeRatio Expression " + ratio + " to a membraneMapping!", e);
		}
	}
	
	//Set VolumeFraction when it exists, amended Sept. 27th, 2007
	if(param.getAttributeValue(XMLTags.VolumeFractionTag) != null)
	{
		String fraction = unMangle( param.getAttributeValue(XMLTags.VolumeFractionTag) );
		try {
			memmap.getVolumeFractionParameter().setExpression(unMangleExpression(fraction));
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException("An expressionException was fired when setting the VolumeFraction Expression " + fraction + " to a membraneMapping!", e);
		}
	}
	
	//Set Area/unit_area if it exists, amended Sept. 27th, 2007
	if(param.getAttributeValue(XMLTags.AreaPerUnitAreaTag)!= null)
	{
		String ratio = unMangle( param.getAttributeValue(XMLTags.AreaPerUnitAreaTag) );
		try {
			memmap.getAreaPerUnitAreaParameter().setExpression(unMangleExpression(ratio));
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException("An expressionException was fired when setting the AreaPerUnitArea Expression " + ratio + " to a membraneMapping!", e);
		}
	}
	
	//Set SurfacetoVolumeRatio when it exists, amended Sept. 27th, 2007
	if(param.getAttributeValue(XMLTags.AreaPerUnitVolumeTag)!= null)
	{
		String ratio = unMangle( param.getAttributeValue(XMLTags.AreaPerUnitVolumeTag) );
		try {
			memmap.getAreaPerUnitVolumeParameter().setExpression(unMangleExpression(ratio));
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException("An expressionException was fired when setting the AreaPerUnitVolume Expression " + ratio + " to a membraneMapping!", e);
		}
	}

	//Set Size
	if(param.getAttributeValue(XMLTags.SizeTag) != null)
	{
		String size = unMangle( param.getAttributeValue(XMLTags.SizeTag) );
		try {
			memmap.getSizeParameter().setExpression(unMangleExpression(size));
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException("An expressionException was fired when setting the size Expression " + size + " to a membraneMapping!", e);
		}
	}else{
		try {
			memmap.getSizeParameter().setExpression(null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("unexpected exception while setting structure size", e);
		}
	}
	//** Set electrical properties **
	//set specific capacitance
	double specificCap = Double.parseDouble(param.getAttributeValue(XMLTags.SpecificCapacitanceTag));
	try {
		memmap.getSpecificCapacitanceParameter().setExpression(new Expression(specificCap));		
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException(e);
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
		e.printStackTrace(System.out);
		throw new XmlParseException(e);
	}
	
	String geometryClassName = param.getAttributeValue(XMLTags.GeometryClassAttrTag);
	if (geometryClassName != null){
		geometryClassName = unMangle(geometryClassName);
	}
	//Retrieve subvolumeref, allow subvolumes to be 'null'
	if (geometryClassName != null) {
		GeometryClass[] geometryClasses = simulationContext.getGeometry().getGeometryClasses();
		for (int i = 0; i < geometryClasses.length; i++) {
			if (geometryClasses[i].getName().equals(geometryClassName)){
				try {
					memmap.setGeometryClass(geometryClasses[i]);
				} catch (PropertyVetoException e) {
					e.printStackTrace();
					throw new XmlParseException("A propertyVetoException was fired when trying to set the subvolume or surface " + geometryClassName + " to a MembraneMapping!", e);
				}
			}
		}
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
private MembraneRegionEquation getMembraneRegionEquation(Element param, MathDescription mathDesc) throws XmlParseException {
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

private ComputeNormalComponentEquation getComputeNormal(Element param, MathDescription mathDesc) throws XmlParseException {
	//get attributes
	String varname = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	//find reference in the dictionnary
	//try a MembraneRegionVariable
	MemVariable varref = (MemVariable)mathDesc.getVariable(varname);
	if (varref == null) {
		throw new XmlParseException("The reference to the Membrane variable "+ varname+ " could not be resolved!");
	}

	NormalComponent normalComponent = null;
	String normalComponentString = param.getAttributeValue(XMLTags.ComputeNormalComponentAttrTag);
	if (normalComponentString.equals(XMLTags.ComputeNormalComponentAttrTagValue_X)){
		normalComponent = NormalComponent.X;
	}else if (normalComponentString.equals(XMLTags.ComputeNormalComponentAttrTagValue_Y)){
		normalComponent = NormalComponent.Y;
	}else if (normalComponentString.equals(XMLTags.ComputeNormalComponentAttrTagValue_Z)){
		normalComponent = NormalComponent.Z;
	}
	
	ComputeNormalComponentEquation computeNormal = new ComputeNormalComponentEquation(varref, normalComponent);
	return computeNormal;
}

private ComputeMembraneMetricEquation getComputeMembraneMetric(Element param, MathDescription mathDesc) throws XmlParseException {
	//get attributes
	String varname = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	//find reference in the dictionnary
	//try a MembraneRegionVariable
	VolVariable varref = (VolVariable)mathDesc.getVariable(varname);
	if (varref == null) {
		throw new XmlParseException("The reference to the Volume variable "+ varname+ " could not be resolved!");
	}

	MembraneMetricComponent normalComponent = null;
	String normalComponentString = param.getAttributeValue(XMLTags.ComputeMembraneMetricComponentAttrTag);
	if (normalComponentString.equals(XMLTags.ComputeMembraneMetricComponentAttrTagValue_directionX)){
		normalComponent = MembraneMetricComponent.directionToMembraneX;
	}else if (normalComponentString.equals(XMLTags.ComputeMembraneMetricComponentAttrTagValue_directionY)){
		normalComponent = MembraneMetricComponent.directionToMembraneY;
	}else if (normalComponentString.equals(XMLTags.ComputeMembraneMetricComponentAttrTagValue_directionZ)){
		normalComponent = MembraneMetricComponent.directionToMembraneZ;
	}else if (normalComponentString.equals(XMLTags.ComputeMembraneMetricComponentAttrTagValue_distance)){
		normalComponent = MembraneMetricComponent.distanceToMembrane;
	}
	
	ComputeMembraneMetricEquation computeMembraneMetric = new ComputeMembraneMetricEquation(varref, normalComponent);
	String membraneName = param.getAttributeValue(XMLTags.ComputeMembraneMetricTargetMembraneAttrTag);
	computeMembraneMetric.setTargetMembraneName(membraneName);
	return computeMembraneMetric;
}

private ComputeCentroidComponentEquation getComputeCentroid(Element param, MathDescription mathDesc) throws XmlParseException {
	//get attributes
	String varname = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	
	//find reference in the dictionnary
	//try a MembraneRegionVariable
	VolumeRegionVariable varref = (VolumeRegionVariable)mathDesc.getVariable(varname);
	if (varref == null) {
		throw new XmlParseException("The reference to the Volume Region variable "+ varname+ " could not be resolved!");
	}

	CentroidComponent normalComponent = null;
	String normalComponentString = param.getAttributeValue(XMLTags.ComputeCentroidComponentAttrTag);
	if (normalComponentString.equals(XMLTags.ComputeCentroidComponentAttrTagValue_X)){
		normalComponent = CentroidComponent.X;
	}else if (normalComponentString.equals(XMLTags.ComputeCentroidComponentAttrTagValue_Y)){
		normalComponent = CentroidComponent.Y;
	}else if (normalComponentString.equals(XMLTags.ComputeCentroidComponentAttrTagValue_Z)){
		normalComponent = CentroidComponent.Z;
	}
	
	ComputeCentroidComponentEquation centroid = new ComputeCentroidComponentEquation(varref, normalComponent);
	return centroid;
}

/**
 * This method returns a MembraneRegionVariable object from a XML Element.
 * Creation date: (5/16/2001 2:56:34 PM)
 * @return cbit.vcell.math.MembraneRegionVariable
 * @param param org.jdom.Element
 */
private MembraneRegionVariable getMembraneRegionVariable(Element param) {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String domainStr = unMangle( param.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}

	//-- create new MembraneRegionVariable object
	MembraneRegionVariable memRegVariable = new MembraneRegionVariable( name, domain );
	transcribeComments(param, memRegVariable);

	return memRegVariable;
}


/**
 * This method returns a MembraneSubDomain object from a XML Element.
 * Creation date: (5/18/2001 4:23:30 PM)
 * @return cbit.vcell.math.MembraneSubDomain
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
@SuppressWarnings("unchecked")
private MembraneSubDomain getMembraneSubDomain(Element param, MathDescription mathDesc) throws XmlParseException {

	// no need to do anything with the 'Name' attribute : constructor of MembraneSubDomain creates name from inside/outside compartmentSubDomains.
//	String msdName = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
//	if ( msdName != null) {
//	}

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
	SubVolume insideSubVolume = mathDesc.getGeometry().getGeometrySpec().getSubVolume(insideRef.getName());
	SubVolume outsideSubVolume = mathDesc.getGeometry().getGeometrySpec().getSubVolume(outsideRef.getName());
	SurfaceClass surfaceClass = mathDesc.getGeometry().getGeometrySurfaceDescription().getSurfaceClass(insideSubVolume, outsideSubVolume);
	
	MembraneSubDomain subDomain = new MembraneSubDomain(insideRef, outsideRef,surfaceClass.getName());
	transcribeComments(param,subDomain);

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
			throw new XmlParseException("A MathException was fired when adding an OdeEquation to a MembraneSubDomain!", e);
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
			throw new XmlParseException("A MathException was fired when adding an PdeEquation to the MembraneSubDomain " + name, e);
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
			throw new XmlParseException("A MathException was fired when adding a JumpCondition to a MembraneSubDomain!", e);
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
			throw new XmlParseException("A MathException was fired when adding a MembraneRegionEquation to a MEmbraneSubDomain!", e);
		}
	}
	
	iterator = param.getChildren(XMLTags.ParticleJumpProcessTag, vcNamespace).iterator();
	while (iterator.hasNext()) {
		Element tempelement = (Element)iterator.next();
		try {
			subDomain.addParticleJumpProcess(getParticleJumpProcess(tempelement, mathDesc) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding a jump process to the MembraneSubDomain " + name, e);
		} 
	}
	
	iterator = param.getChildren(XMLTags.ParticlePropertiesTag, vcNamespace).iterator();
	while (iterator.hasNext()) {
		Element tempelement = (Element)iterator.next();
		try {
			subDomain.addParticleProperties(getParticleProperties(tempelement, mathDesc));
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding a jump process to the MembraneSubDomain " + name, e);
		} 
	}
	
	//process ComputeNormal "equations"
	iterator = param.getChildren( XMLTags.ComputeNormalTag, vcNamespace ).iterator();
	while (iterator.hasNext()) {
		Element tempelement = (Element)iterator.next();

		try {
			subDomain.addEquation( getComputeNormal(tempelement, mathDesc) );
		} catch (MathException e) {
			e.printStackTrace();
			throw new XmlParseException("A MathException was fired when adding an ComputeNormal 'equation' to the MembraneSubDomain " + name, e);
		}
	}


	
	Element velElem = param.getChild(XMLTags.VelocityTag, vcNamespace);
	setMembraneSubdomainVelocity(velElem, XMLTags.XAttrTag,subDomain::setVelocityX); 
	setMembraneSubdomainVelocity(velElem, XMLTags.YAttrTag,subDomain::setVelocityY); 

	return subDomain;	
}

/**
 * MembraneSubDomain velocity 
 * @param vel could be null
 * @param tag 
 * @param dest
 * @throws XmlParseException
 */
private void setMembraneSubdomainVelocity(Element vel, String tag, Consumer<Expression> dest) throws XmlParseException { 
	Expression exp = null; 
	if (vel != null) {
		Element e = vel.getChild(tag, vcNamespace);
		if (e != null) {
			String expStr = e.getValue();
			try {
				exp = new Expression(expStr);
			} catch (ExpressionException ee) {
				throw new XmlParseException("Error parsing "  + expStr, ee);
			}
		}
	}
	dest.accept(exp);
}


/**
 * This method returns a MemVariable object from a XML element.
 * Creation date: (5/16/2001 3:17:18 PM)
 * @return cbit.vcell.math.MemVariable
 * @param param org.jdom.Element
 */
private MemVariable getMemVariable(Element param) {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String domainStr = unMangle( param.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}

	//Create new memVariable
	MemVariable memVariable = new MemVariable( name, domain );
	transcribeComments(param, memVariable);
	
	return memVariable;
}


private PointVariable getPointVariable(Element param) {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String domainStr = unMangle( param.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}

	//Create new memVariable
	PointVariable pointVariable = new PointVariable( name, domain );
	transcribeComments(param, pointVariable);
	
	return pointVariable;
}


/**
 * This method returns a MeshSpecification object from a XML Element.
 * Creation date: (5/22/2001 12:05:21 PM)
 * @return cbit.vcell.mesh.MeshSpecification
 * @param param org.jdom.Element
 */
private MeshSpecification getMeshSpecification(Element param, Geometry geometry) throws XmlParseException {
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
		throw new XmlParseException("A PropertyVetoException was fired when setting the ISize object to a new MeshSpecification", e);
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
	Model newmodel = null;
	Version version = getVersion(param.getChild(XMLTags.VersionTag, vcNamespace));

	// read in unit system
	// if forcedModelUnitSystem has been set, ues that (could be overriding unit system for SBML export)
	if (forcedModelUnitSystem != null) {
		newmodel = new Model(version, forcedModelUnitSystem);
	} else {
		Element unitSystemNode = param.getChild(XMLTags.ModelUnitSystemTag, vcNamespace);
		if (unitSystemNode != null) {
			ModelUnitSystem modelUnitSystem = getUnitSystem(unitSystemNode);
			newmodel = new Model(version, modelUnitSystem);
		} else {
			newmodel = new Model(version);
		}
	} 
	
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
		for (Element featureElement : children) {
			newstructures.add( getFeature(featureElement) );
		}
		//(Membrane)
		children = param.getChildren(XMLTags.MembraneTag, vcNamespace);
		for (Element memElement : children) {
			newstructures.add( getMembrane(newmodel, memElement, newstructures));
		}
		if (newstructures.size()>0) {
			Structure[] structarray = new Structure[newstructures.size()];
			newstructures.toArray(structarray);
			// Add all the retrieved structures
			newmodel.setStructures( structarray );			
		}

		// retrieve the RbmModelContainer, if present - must be done before we retrieve species context!
		Element element = param.getChild(XMLTags.RbmModelContainerTag, vcNamespace);
		if(element != null) {
			getRbmModelContainer(element, newmodel);
		} else {
			lg.info("RbmModelContainer is missing.");
		}

		//Add SpeciesContexts
		children = param.getChildren(XMLTags.SpeciesContextTag, vcNamespace);
		SpeciesContext[] newspeccon = new SpeciesContext[children.size()];
		int scCounter = 0;
		for (Element scElement : children) {
			newspeccon[scCounter] = getSpeciesContext(scElement, newmodel);
			scCounter ++;
		}
		newmodel.setSpeciesContexts(newspeccon);
		
		// Retrieve rateRules and add to model
//		Element rateRuleVarsElement = param.getChild(XMLTags.RateRuleVariablesTag, vcNamespace);
//		if(rateRuleVarsElement != null){
//			RateRuleVariable[] rateRuleVars = getRateRuleVariables(rateRuleVarsElement, newmodel);
//			newmodel.setRateRuleVariables(rateRuleVars);
//		}

		//Add Reaction steps (if available)
		
		//(Simplereaction)
		// Create a varHash with reserved symbols and global parameters, if any, to pass on to Kinetics
		// must create new hash for each reaction and flux, since each kinetics uses new variables hash
		iterator = param.getChildren(XMLTags.SimpleReactionTag, vcNamespace).iterator();
		ArrayList<ReactionStep> reactionStepList = new ArrayList<ReactionStep>();
		while (iterator.hasNext()) {
			org.jdom.Element temp = iterator.next();
			reactionStepList.add(getSimpleReaction(temp, newmodel));
		}
		//(fluxStep)
		iterator = param.getChildren(XMLTags.FluxStepTag, vcNamespace).iterator();
		while (iterator.hasNext()) {
			org.jdom.Element temp = iterator.next();
			reactionStepList.add(getFluxReaction(temp, newmodel));
		}
		newmodel.setReactionSteps(reactionStepList.toArray(new ReactionStep[reactionStepList.size()]));
		//Add Diagrams
		children = param.getChildren(XMLTags.DiagramTag, vcNamespace);
		if (children.size()>0) {
			Diagram[] newdiagrams = new Diagram[children.size()];
			int diagramCounter = 0;
			for (Element diagramElement : children) {
				newdiagrams[diagramCounter] = getDiagram(diagramElement, newmodel);
				diagramCounter ++;
			}
			reorderDiagramsInPlace_UponRead(docVCellSoftwareVersion, newdiagrams, newmodel.getStructureTopology());
//			if(docVCellSoftwareVersion != null && !docVCellSoftwareVersion.isValid() && docVCellSoftwareVersion.getMajorVersion()<=5 && docVCellSoftwareVersion.getMinorVersion() <=2){
//				//In Vcell 5.2 and previous we need to order diagrams topologically, in 5.3 and later the diagrams are displayed as they are ordered when read from document
//				final StructureTopology structureTopology = newmodel.getStructureTopology();
//				Arrays.sort(newdiagrams, new Comparator<Diagram>() {
//					@Override
//					public int compare(Diagram o1, Diagram o2) {
//						return getStructureLevel(o1.getStructure(), structureTopology) - getStructureLevel(o2.getStructure(), structureTopology);
//					}
//				});
//			}
			newmodel.setDiagrams(newdiagrams);
		}
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(e);
	} catch (ModelException e) {
		e.printStackTrace();
	}

	// model param expresions are not bound when they are read in, since they could be functions of each other or structures/speciesContexts.
	// Hence bind the model param exprs at the end, after reading all model level quantities.
	ModelParameter[] modelParameters = newmodel.getModelParameters();
	for (int i=0; modelParameters != null && i<modelParameters.length;i++){
		try {
			modelParameters[i].getExpression().bindExpression(newmodel);
		} catch (ExpressionBindingException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error binding global parameter '" + modelParameters[i].getName() + "' to model."  + e.getMessage());
		}
	}
	return newmodel;
}

@SuppressWarnings("unchecked")
public void getRbmModelContainer(Element param, Model newModel) throws ModelException, PropertyVetoException, XmlParseException {
	Element element = param.getChild(XMLTags.RbmMolecularTypeListTag, vcNamespace);
	if(element != null) {
		getRbmMolecularTypeList(element, newModel);
	}
	element = param.getChild(XMLTags.RbmObservableListTag, vcNamespace);
	if(element != null) {
		getRbmObservableList(element, newModel);
	}
	element = param.getChild(XMLTags.RbmReactionRuleListTag, vcNamespace);
	if(element != null) {
		getRbmReactionRuleList(element, newModel);
	}
	element = param.getChild(XMLTags.RbmNetworkConstraintsTag, vcNamespace);
	if(element != null) {
		getRbmNetworkConstraints(element, newModel);	// one network constraint element
	}
}
private void getRbmMolecularTypeList(Element param, Model newModel) {
	RbmModelContainer mc = newModel.getRbmModelContainer();
	List<MolecularType> mtl = mc.getMolecularTypeList();
	List<Element> children = param.getChildren(XMLTags.RbmMolecularTypeTag, vcNamespace);
	for (Element element : children) {
		MolecularType t = getRbmMolecularType(element, newModel);
		if(t != null) { mtl.add(t); }
	}
}
//private void getRbmSeedSpeciesList(Element param, Model newModel) {
//	RbmModelContainer mc = newModel.getRbmModelContainer();
//	List<SeedSpecies> ssl = mc.getSeedSpeciesList();
//	List<Element> children = param.getChildren(XMLTags.RbmSeedSpeciesTag, vcNamespace);
//	for (Element element : children) {
//		SeedSpecies s = getRbmSeedSpecies(element, newModel);
//		if(s != null) { ssl.add(s); }
//	}
//}
private void getRbmObservableList(Element param, Model newModel) throws ModelException, PropertyVetoException {
	RbmModelContainer mc = newModel.getRbmModelContainer();
	List<Element> children = new ArrayList<Element>();
	children = param.getChildren(XMLTags.RbmObservableTag, vcNamespace);
	for (Element element : children) {
		RbmObservable o = getRbmObservables(element, newModel);
		if(o != null) { mc.addObservable(o); }
	}
}
private void getRbmReactionRuleList(Element param, Model newModel) throws XmlParseException {
	RbmModelContainer mc = newModel.getRbmModelContainer();
	List<ReactionRule> rrl = mc.getReactionRuleList();
	List<Element> children = new ArrayList<Element>();
	children = param.getChildren(XMLTags.RbmReactionRuleTag, vcNamespace);
	for (Element element : children) {
		ReactionRule r = getRbmReactionRule(element, newModel);
		if(r != null) { rrl.add(r); }
	}
}
private MolecularType getRbmMolecularType(Element e, Model newModel) {
	String s = e.getAttributeValue(XMLTags.NameAttrTag);
	if(s == null || s.isEmpty()) {
		System.out.println("XMLReader: getRBMMolecularType: name is missing.");
		return null;
	}
	MolecularType mt = new MolecularType(s, newModel);
	
	final String attributeValue = e.getAttributeValue(XMLTags.RbmMolecularTypeAnchorAllAttrTag);
	if(attributeValue != null) {
		boolean anchorAll = Boolean.parseBoolean(attributeValue);
		mt.setAnchorAll(anchorAll);
	}
	List<Element> anchors = e.getChildren(XMLTags.RbmMolecularTypeAnchorTag, vcNamespace);
	for (Element element : anchors) {
		String anchor = element.getAttributeValue(XMLTags.StructureAttrTag);
		Structure structure = newModel.getStructure(anchor);
		if(structure == null) {
			System.out.println("XMLReader: getRbmMolecularType: anchor is missing from the structures list.");
		}
		if(structure != null) { mt.addAnchor(structure); }
	}
	List<Element> children = e.getChildren(XMLTags.RbmMolecularComponentTag, vcNamespace);
	for (Element element : children) {
		MolecularComponent mc = getRbmMolecularComponent(element, newModel);
		if(mc != null) { mt.addMolecularComponent(mc); }
	}
	return mt;
}
private MolecularComponent getRbmMolecularComponent(Element e, Model newModel) {
	String s = e.getAttributeValue(XMLTags.NameAttrTag);
	if(s == null || s.isEmpty()) {
		System.out.println("XMLReader: getRbmMolecularComponent: name is missing.");
		return null;
	}
	MolecularComponent mc = new MolecularComponent(s);
	s = e.getAttributeValue(XMLTags.RbmIndexAttrTag);
	if(s == null || s.isEmpty()) {
		System.out.println("XMLReader: getRbmMolecularComponent: index is missing.");
		return null;
	}
	int index = Integer.parseInt(s);
	mc.setIndex(index);
	List<Element> children = e.getChildren(XMLTags.RbmMolecularTypeAllowableStateTag, vcNamespace);
	for (Element element : children) {
		ComponentStateDefinition cs = getRbmComponentStateDefinition(element, newModel);
		if(cs != null) { mc.addComponentStateDefinition(cs); }
	}
	return mc;
}
private ComponentStateDefinition getRbmComponentStateDefinition(Element e, Model newModel) {
	String s = e.getAttributeValue(XMLTags.NameAttrTag);
	if(s == null || s.isEmpty()) {
		System.out.println("XMLReader: getRbmComponentState: name is missing.");
		return null;
	}
	ComponentStateDefinition cs = new ComponentStateDefinition(s);
//	s = e.getAttributeValue(XMLTags.RbmMolecularTypeAnyTag);
//	if(s!=null && !s.isEmpty()) {
//		boolean any = Boolean.parseBoolean(s);
//		cs.setAny(any);
//	}
	return cs;
}
//private SeedSpecies getRbmSeedSpecies(Element e, Model newModel) {
//	String s = e.getAttributeValue(XMLTags.RbmInitialConditionTag);
//	if(s == null || s.isEmpty()) {
//		System.out.println("XMLReader: getRbmSeedSpecies: initial condition is missing.");
//		return null;
//	}
//	Expression exp = unMangleExpression(s);
//	Element element = e.getChild(XMLTags.RbmSpeciesPatternTag, vcNamespace);
//	SpeciesPattern sp = getSpeciesPattern(element, newModel);
//	if(sp == null) {
//		System.out.println("XMLReader: getRbmSeedSpecies: SpeciesPattern is missing.");
//		return null;
//	}
//	SeedSpecies ss = new SeedSpecies(sp, exp);
//	return ss;
//}
private SpeciesPattern getSpeciesPattern(Element e, Model newModel) {
	
	SpeciesPattern sp = new SpeciesPattern();
	List<Element> children = e.getChildren(XMLTags.RbmMolecularTypePatternTag, vcNamespace);
	for (Element element : children) {
		MolecularTypePattern tp = getRbmMolecularTypePattern(element, newModel);
		if(tp != null) { sp.addMolecularTypePattern(tp); }
	}
	return sp;
}
private MolecularTypePattern getRbmMolecularTypePattern(Element e, Model newModel) {
	RbmModelContainer mc = newModel.getRbmModelContainer();
	Element e1 = e.getChild(XMLTags.RbmMolecularTypeTag, vcNamespace);
	String molecularTypeName = e1.getAttributeValue(XMLTags.NameAttrTag);
	MolecularType mt = mc.getMolecularType(molecularTypeName);
	if(mt == null) {
		System.out.println("XMLReader: getRbmMolecularTypePattern: encountered reference to non-existing MolecularType.");
		return null;
	}
	MolecularTypePattern tp = new MolecularTypePattern(mt, false);	// we insert the component patterns below
	String index = e.getAttributeValue(XMLTags.RbmIndexAttrTag);
	if(index!=null && !index.isEmpty()) {
		tp.setIndex(Integer.parseInt(index));
	}
	String match = e.getAttributeValue(XMLTags.RbmParticipantPatternMatchTag);
	if(match!=null && !match.isEmpty()) {
		tp.setParticipantMatchLabel(match);
	}
	List<MolecularComponentPattern> cpl = new ArrayList<MolecularComponentPattern>();
	List<Element> children = e.getChildren(XMLTags.RbmMolecularComponentPatternTag, vcNamespace);
	for (Element e2 : children) {
		MolecularComponentPattern cp = getRbmMolecularComponentPattern(e2, tp, mt, newModel);
		if(cp != null) { cpl.add(cp); }
	}
	tp.setComponentPatterns(cpl);
	return tp;
}
public static void reorderDiagramsInPlace_UponRead(VCellSoftwareVersion docVCellSoftwareVersion,final Diagram[] diagramArr,final StructureTopology structureTopology){
	if(docVCellSoftwareVersion != null && (docVCellSoftwareVersion.getMajorVersion()<5 || (docVCellSoftwareVersion.getMajorVersion()==5 && docVCellSoftwareVersion.getMinorVersion() <=2))){
		//In Vcell 5.2 and previous we need to order diagrams topologically, in 5.3 and later the diagrams are displayed as they are ordered when read from document
		Arrays.sort(diagramArr, new Comparator<Diagram>() {
			@Override
			public int compare(Diagram o1, Diagram o2) {
				return getStructureLevel(o1.getStructure(), structureTopology) - getStructureLevel(o2.getStructure(), structureTopology);
			}
		});
	}
}
private static Integer getStructureLevel(Structure s,StructureTopology structureTopology) {
	Structure s0 = s;
	int level = 0;
	while (s0 != null) {
		level += 1;
		s0 = structureTopology.getParentStructure(s0);
	}
	return level;
}
private MolecularComponentPattern getRbmMolecularComponentPattern(Element e, MolecularTypePattern mtp, MolecularType mt, Model newModel) {
	RbmModelContainer mc = newModel.getRbmModelContainer();
	String s = e.getAttributeValue(XMLTags.RbmMolecularComponentTag);
	if(s == null || s.isEmpty()) {
		System.out.println("XMLReader: getRbmMolecularComponentPattern: MolecularComponent name is missing.");
		return null;
	}
	MolecularComponent c = mt.getMolecularComponent(s);
	if(c == null) {
		System.out.println("XMLReader: getRbmMolecularComponentPattern: encountered reference " + s + " to non-existing MolecularComponent.");
		return null;
	}
	ComponentStatePattern csp = new ComponentStatePattern();
	MolecularComponentPattern mcp = new MolecularComponentPattern(c);
	s = e.getAttributeValue(XMLTags.RbmMolecularComponentStatePatternTag);
	if(s != null && !s.isEmpty()) {		// state may be missing, we set it only if is present
		ComponentStateDefinition cs = c.getComponentStateDefinition(s);
		if(cs == null) {
			System.out.println("XMLReader: getRbmMolecularComponentPattern: encountered reference " + s + " to non-existing MolecularComponentState.");
			return null;
		}
		csp = new ComponentStatePattern(cs);
		mcp.setComponentStatePattern(csp);
	}
//	s = e.getAttributeValue(XMLTags.RbmMolecularTypeAnyTag);
//	if(s!=null && !s.isEmpty()) {
//		boolean any = Boolean.parseBoolean(s);
//		csp.setAny(any);
//	}

	s = e.getAttributeValue(XMLTags.RbmBondTypeAttrTag);
	BondType bondType = BondType.fromSymbol(s);
	if (bondType == BondType.Specified){
		int bondId = Integer.parseInt(s);
		mcp.setBondId(bondId);
	}
	mcp.setBondType(bondType);
	// sanity check, we only read the names here and make sure they make sense
	Element bondElement = e.getChild(XMLTags.RbmBondTag, vcNamespace);
	if(bondElement != null) {
		String molecularTypeName = bondElement.getAttributeValue(XMLTags.RbmMolecularTypePatternTag);	// it's actually the name of the MolecularType inside this pattern
		String molecularComponentName = bondElement.getAttributeValue(XMLTags.RbmMolecularComponentPatternTag);
		if(molecularTypeName == null || molecularTypeName.isEmpty()) {
			System.out.println("XMLReader: getRbmMolecularComponentPattern: Bond Attribute molecularTypeName missing.");
			return mcp;
		}
		if(molecularComponentName == null || molecularComponentName.isEmpty()) {
			System.out.println("XMLReader: getRbmMolecularComponentPattern: Bond Attribute molecularComponentName missing.");
			return mcp;
		}
		Bond bond = new Bond();		// we'll have a bond here, it will be properly initialized during RbmObservable.resolveBonds() call  !!!
		mcp.setBond(bond);
	}
	return mcp;
}
private RbmObservable getRbmObservables(Element e, Model newModel) {
	String n = e.getAttributeValue(XMLTags.NameAttrTag);
	if(n == null || n.isEmpty()) {
		System.out.println("XMLReader: getRbmObservables: name is missing.");
		return null;
	}
	String t = e.getAttributeValue(XMLTags.RbmObservableTypeTag);
	if(t == null || t.isEmpty()) {
		System.out.println("XMLReader: getRbmObservables: type is missing.");
		return null;
	}
	RbmObservable.ObservableType ot = RbmObservable.ObservableType.Molecules;
	if(!t.equals(ot.name())) {
		ot = RbmObservable.ObservableType.Species;
	}
	Structure structure = null;
	String structureName = e.getAttributeValue(XMLTags.StructureAttrTag);
	if(structureName == null || structureName.isEmpty()) {	// the tag is missing
		if(newModel.getStructures().length == 1) {
			structure = newModel.getStructure(0);	// possible old single compartment model where we were not saving the structure for observable
		} else {
			throw new RuntimeException("XMLReader: structure missing for observable " + n);
		}
	} else {
		structure = newModel.getStructure(structureName);
	}
	RbmObservable o = new RbmObservable(newModel, n, structure, ot);
	
	RbmObservable.Sequence se = RbmObservable.Sequence.Multimolecular;			// Sequence
	String ses = e.getAttributeValue(XMLTags.RbmObservableSequenceAttrTag);
	if(ses != null && ses.equals(RbmObservable.Sequence.PolymerLengthEqual.name())) {
		se = RbmObservable.Sequence.PolymerLengthEqual;
	} else if(ses != null && ses.equals(RbmObservable.Sequence.PolymerLengthGreater.name())) {
		se = RbmObservable.Sequence.PolymerLengthGreater;
	}
	o.setSequence(se);
	
	String lens = e.getAttributeValue(XMLTags.RbmObservableLenEqualAttrTag);
	if(lens != null) {	// may be null for older models in which case the observable has default initial values
		int len = Integer.parseInt(lens);
		o.setSequenceLength(RbmObservable.Sequence.PolymerLengthEqual, len);
	}
	lens = e.getAttributeValue(XMLTags.RbmObservableLenGreaterAttrTag);
	if(lens != null) {
		int len = Integer.parseInt(lens);
		o.setSequenceLength(RbmObservable.Sequence.PolymerLengthGreater, len);
	}
	
//	Element element = e.getChild(XMLTags.RbmSpeciesPatternTag, vcNamespace);
//	SpeciesPattern sp = getSpeciesPattern(element, newModel);
	List<Element> children = e.getChildren(XMLTags.RbmSpeciesPatternTag, vcNamespace);
	for (Element e2 : children) {
		SpeciesPattern sp = getSpeciesPattern(e2, newModel);
		if(sp != null) { o.addSpeciesPattern(sp); }		// setSpeciesPattern() will call resolveBonds()
	}
	return o;
}
private ReactionRule getRbmReactionRule(Element reactionRuleElement, Model newModel) throws XmlParseException {
	String n = reactionRuleElement.getAttributeValue(XMLTags.NameAttrTag);
	if(n == null || n.isEmpty()) {
		System.out.println("XMLReader: getRbmReactionRule: name is missing.");
		return null;
	}
	try {
		boolean reversible = Boolean.valueOf(reactionRuleElement.getAttributeValue(XMLTags.RbmReactionRuleReversibleTag));
		String structureName = reactionRuleElement.getAttributeValue(XMLTags.StructureAttrTag, newModel.getStructures()[0].getName());	// get 1st structure if attribute missing
		Structure structure = newModel.getStructure(structureName);
		ReactionRule reactionRule = new ReactionRule(newModel, n, structure, reversible);
		String reactionRuleLabel = reactionRuleElement.getAttributeValue(XMLTags.RbmReactionRuleLabelTag);	// we ignore this, name and label are the same thing for now
	
		//
		// old style kinetics placed parameter values as attributes
		// look for attributes named ("MassActionKf","MassActionKr","MichaelisMentenKcat","MichaelisMentenKm","SaturableKs","SaturableVmax")
		String[] oldKineticsAttributes = new String[] {
				XMLTags.RbmMassActionKfAttrTag_DEPRECATED,
				XMLTags.RbmMassActionKrAttrTag_DEPRECATED,
				XMLTags.RbmMichaelisMentenKcatAttrTag_DEPRECATED,
				XMLTags.RbmMichaelisMentenKmAttrTag_DEPRECATED,
				XMLTags.RbmSaturableKsAttrTag_DEPRECATED,
				XMLTags.RbmSaturableVmaxAttrTag_DEPRECATED
		};
		boolean bOldKineticsFound = false;
		for (String oldKineticsAttribute : oldKineticsAttributes){
			if (reactionRuleElement.getAttribute(oldKineticsAttribute) != null){
				bOldKineticsFound = true;
			}
		}
	
		if (bOldKineticsFound){
			readOldRbmKineticsAttributes(reactionRuleElement,reactionRule);
		} else {
			Element kineticsElement = reactionRuleElement.getChild(XMLTags.KineticsTag, vcNamespace);
			if (kineticsElement != null){
				String kineticLawTypeString = kineticsElement.getAttributeValue(XMLTags.KineticsTypeAttrTag);
				RbmKineticLaw.RateLawType rateLawType = null;
				if (XMLTags.RbmKineticTypeMassAction.equals(kineticLawTypeString)){
					rateLawType = RateLawType.MassAction;
				}else if (XMLTags.RbmKineticTypeMichaelisMenten.equals(kineticLawTypeString)){
					rateLawType = RateLawType.MichaelisMenten;
				}else if (XMLTags.RbmKineticTypeSaturable.equals(kineticLawTypeString)){
					rateLawType = RateLawType.Saturable;
				}else{
					throw new RuntimeException("unexpected rate law type "+kineticLawTypeString);
				}
				reactionRule.setKineticLaw(new RbmKineticLaw(reactionRule, rateLawType));
				List<Element> parameterElements = kineticsElement.getChildren(XMLTags.ParameterTag, vcNamespace);
				HashMap<String,ParameterRoleEnum> roleHash = new HashMap<String, ParameterContext.ParameterRoleEnum>();
				roleHash.put(XMLTags.RbmMassActionKfRole,RbmKineticLawParameterType.MassActionForwardRate);
				roleHash.put(XMLTags.RbmMassActionKrRole,RbmKineticLawParameterType.MassActionReverseRate);
				roleHash.put(XMLTags.RbmMichaelisMentenVmaxRole,RbmKineticLawParameterType.MichaelisMentenVmax);
				roleHash.put(XMLTags.RbmMichaelisMentenKmRole,RbmKineticLawParameterType.MichaelisMentenKm);
				roleHash.put(XMLTags.RbmSaturableVmaxRole,RbmKineticLawParameterType.SaturableVmax);
				roleHash.put(XMLTags.RbmSaturableKsRole,RbmKineticLawParameterType.SaturableKs);
				roleHash.put(XMLTags.RbmUserDefinedRole,RbmKineticLawParameterType.UserDefined);
				HashSet<String> xmlRolesToIgnore = new HashSet<String>();
				xmlRolesToIgnore.add(XMLTags.RbmRuleRateRole);
				ParameterContext parameterContext = reactionRule.getKineticLaw().getParameterContext();
				readParameters(parameterElements, parameterContext, roleHash, RbmKineticLawParameterType.UserDefined, xmlRolesToIgnore, newModel);
			}
		}
		Element e1 = reactionRuleElement.getChild(XMLTags.RbmReactantPatternsListTag, vcNamespace);
		getRbmReactantPatternsList(e1, reactionRule, newModel);
		Element e2 = reactionRuleElement.getChild(XMLTags.RbmProductPatternsListTag, vcNamespace);
		getRbmProductPatternsList(e2, reactionRule, newModel);
		reactionRule.checkMatchConsistency();
		return reactionRule;	
	}catch (PropertyVetoException | ExpressionException ex){
		ex.printStackTrace(System.out);
		throw new RuntimeException("failed to parse kinetics for reaction rule '"+n+"': "+ex.getMessage(),ex);
	}
}

private void readOldRbmKineticsAttributes(Element reactionRuleElement, ReactionRule reactionRule) throws PropertyVetoException, ExpressionException{
	boolean reversible = reactionRule.isReversible();
	
	//
	// try Mass Action Kinetics attributes
	//
	{
	String massActionForwardRate = reactionRuleElement.getAttributeValue(XMLTags.RbmMassActionKfAttrTag_DEPRECATED);
	if(massActionForwardRate != null && !massActionForwardRate.isEmpty()) {
		reactionRule.setKineticLaw(new RbmKineticLaw(reactionRule, RateLawType.MassAction));
		Expression massActionKfExp = unMangleExpression(massActionForwardRate);
		LocalParameter forwardRateParameter = reactionRule.getKineticLaw().getLocalParameter(RbmKineticLawParameterType.MassActionForwardRate);
		reactionRule.getKineticLaw().setParameterValue(forwardRateParameter, massActionKfExp, true);
		if(reversible == true) {
			String massActionReverseRate = reactionRuleElement.getAttributeValue(XMLTags.RbmMassActionKrAttrTag_DEPRECATED);
			if(massActionReverseRate == null || massActionReverseRate.isEmpty()) {
				throw new RuntimeException("XMLReader: getRbmReactionRule: Mass Action: Reverse Rate is missing.");
			} else {
				Expression massActionKrExp = unMangleExpression(massActionReverseRate);
				LocalParameter reverseRateParameter = reactionRule.getKineticLaw().getLocalParameter(RbmKineticLawParameterType.MassActionReverseRate);
				reactionRule.getKineticLaw().setParameterValue(reverseRateParameter, massActionKrExp, true);
			}
		}
		return;
	}
	}

	//
	// try Michaelis Menten Kinetics attributes
	//
	{
	String MM_Kcat = reactionRuleElement.getAttributeValue(XMLTags.RbmMichaelisMentenKcatAttrTag_DEPRECATED);
	if(MM_Kcat != null && !MM_Kcat.isEmpty()) {
		reactionRule.setKineticLaw(new RbmKineticLaw(reactionRule, RateLawType.MichaelisMenten));
		Expression MM_Kcat_exp = unMangleExpression(MM_Kcat);
		LocalParameter kcatParameter = reactionRule.getKineticLaw().getLocalParameter(RbmKineticLawParameterType.MichaelisMentenVmax);
		reactionRule.getKineticLaw().setParameterValue(kcatParameter, MM_Kcat_exp, true);
		String MM_Km = reactionRuleElement.getAttributeValue(XMLTags.RbmMichaelisMentenKmAttrTag_DEPRECATED);
		if(MM_Km == null || MM_Km.isEmpty()) {
			System.out.println("XMLReader: getRbmReactionRule: MM_Km is missing.");
		} else {
			Expression MM_Km_exp = unMangleExpression(MM_Km);
			LocalParameter kmParameter = reactionRule.getKineticLaw().getLocalParameter(RbmKineticLawParameterType.MichaelisMentenKm);
			reactionRule.getKineticLaw().setParameterValue(kmParameter, MM_Km_exp, true);
		}
		return;
	}
	}

	//
	// try Saturable Kinetics attributes
	//
	{
	String Sat_Ks = reactionRuleElement.getAttributeValue(XMLTags.RbmSaturableKsAttrTag_DEPRECATED);
	if(Sat_Ks != null && !Sat_Ks.isEmpty()) {
		reactionRule.setKineticLaw(new RbmKineticLaw(reactionRule, RateLawType.Saturable));
		Expression Sat_Ks_exp = unMangleExpression(Sat_Ks);
		LocalParameter ksParameter = reactionRule.getKineticLaw().getLocalParameter(RbmKineticLawParameterType.SaturableKs);
		reactionRule.getKineticLaw().setParameterValue(ksParameter, Sat_Ks_exp, true);
		String Sat_Vmax = reactionRuleElement.getAttributeValue(XMLTags.RbmSaturableVmaxAttrTag_DEPRECATED);
		if(Sat_Vmax == null || Sat_Vmax.isEmpty()) {
			System.out.println("XMLReader: getRbmReactionRule: Sat_Vmax is missing.");
		} else {
			Expression Sat_Vmax_exp = unMangleExpression(Sat_Vmax);
			LocalParameter vmaxParameter = reactionRule.getKineticLaw().getLocalParameter(RbmKineticLawParameterType.SaturableVmax);
			reactionRule.getKineticLaw().setParameterValue(vmaxParameter, Sat_Vmax_exp, true);
		}
		return;
	}
	}

	throw new RuntimeException("Kinetic law unsupported or missing. Must be Mass Action, Michaelis Menten or Saturable.");
}
private void getRbmReactantPatternsList(Element e, ReactionRule r, Model newModel) {
	if (e != null ) {
		List<Element> rpChildren = e.getChildren(XMLTags.RbmReactantPatternTag, vcNamespace);
		for (Element rpElement : rpChildren) {
			Structure structure = null;
			String structureName = rpElement.getAttributeValue(XMLTags.StructureAttrTag);
			if(structureName == null || structureName.isEmpty()) {	// the tag is missing
				throw new RuntimeException("XMLReader: structure missing for reaction rule pattern.");
			} else {
				structure = newModel.getStructure(structureName);
			}
			Element spe = rpElement.getChild(XMLTags.RbmSpeciesPatternTag, vcNamespace);
			SpeciesPattern s = getSpeciesPattern(spe, newModel);
			if(s != null) { r.addReactant(new ReactantPattern(s, structure), false); }
		}
		// older models have the species pattern saved directly and using the structure or the rule
		List<Element> spChildren = e.getChildren(XMLTags.RbmSpeciesPatternTag, vcNamespace);
		for (Element element : spChildren) {
			SpeciesPattern s = getSpeciesPattern(element, newModel);
			if(s != null) { r.addReactant(new ReactantPattern(s, r.getStructure()), false); }
		}
	}
}
private void getRbmProductPatternsList(Element e, ReactionRule r, Model newModel) {
	if (e != null) {
		List<Element> ppChildren = e.getChildren(XMLTags.RbmProductPatternTag, vcNamespace);
		for (Element ppElement : ppChildren) {
			Structure structure = null;
			String structureName = ppElement.getAttributeValue(XMLTags.StructureAttrTag);
			if(structureName == null || structureName.isEmpty()) {	// the tag is missing
				throw new RuntimeException("XMLReader: structure missing for reaction rule pattern.");
			} else {
				structure = newModel.getStructure(structureName);
			}
			Element spe = ppElement.getChild(XMLTags.RbmSpeciesPatternTag, vcNamespace);
			SpeciesPattern s = getSpeciesPattern(spe, newModel);
			if(s != null) { r.addProduct(new ProductPattern(s, structure), false); }
		}
		// older models have the species pattern saved directly and using the structure or the rule
		List<Element> spChildren = GenericUtils.convert(e.getChildren(XMLTags.RbmSpeciesPatternTag, vcNamespace), Element.class);
		for (Element element : spChildren) {
			SpeciesPattern s = getSpeciesPattern(element, newModel);
			if(s != null) { r.addProduct(new ProductPattern(s, r.getStructure()), false); }
		}
	}
}

//
// Legacy NetworkConstraints was the single NetworkConstraints object formerly stored
// in the Model's RbmModelContainer.  The NetworkConstraints are now stored in each
// SimulationContext instead (inheriting the "global" NetworkConstraint) upon first
// load.
//
// We read the single BioModel network constraints from legacy Rule-based models
// (pre-release VCell 6.0 models only).  
//
// Warning: We will NOT preserve this default NetworkConstraints object for saved BioModels
// where the database cached XML document is lost and must be regenerated.  
// This rare condition for few models would have added complexity and been of limited value.
// 
//
private NetworkConstraints legacyNetworkConstraints = null;

private void getRbmNetworkConstraints(Element e, Model newModel) {
	RbmModelContainer mc = newModel.getRbmModelContainer();
	NetworkConstraints nc = new NetworkConstraints();
	this.legacyNetworkConstraints = nc;
	
	String s = e.getAttributeValue(XMLTags.RbmMaxIterationTag);
	if(s!=null && !s.isEmpty()) {
		int maxIteration = Integer.parseInt(s);
		nc.setMaxIteration(maxIteration);
	}
	s = e.getAttributeValue(XMLTags.RbmMaxMoleculesPerSpeciesTag);
	if(s!=null && !s.isEmpty()) {
		int maxMoleculesPerSpecies = Integer.parseInt(s);
		nc.setMaxMoleculesPerSpecies(maxMoleculesPerSpecies);
	}
	/* 
	 * there has never been a species limit or a reaction limit here, so we don't even try to read them
	 */
	List<Element> children = e.getChildren(XMLTags.RbmMaxStoichiometryTag, vcNamespace);
	for (Element element : children) {
		Integer i = 1;
		MolecularType mt = null;
		s = element.getAttributeValue(XMLTags.RbmIntegerAttrTag);
		if(s!=null && !s.isEmpty()) {
			i = Integer.valueOf(s);
		}
		s = element.getAttributeValue(XMLTags.RbmMolecularTypeTag);
		if(s!=null && !s.isEmpty()) {
			mt = mc.getMolecularType(s);
		}
		if(mt != null) {
			nc.setMaxStoichiometry(mt, i);
		}
	}
}

// --------------------------------------------------------------------

public ModelUnitSystem getUnitSystem(Element unitSystemNode) {

	//Read all the attributes
	String volSubsUnit = unMangle(unitSystemNode.getAttributeValue(XMLTags.VolumeSubstanceUnitTag));
	String memSubsUnit = unMangle(unitSystemNode.getAttributeValue(XMLTags.MembraneSubstanceUnitTag));
	String lumpedSubsUnit = unMangle(unitSystemNode.getAttributeValue(XMLTags.LumpedReactionSubstanceUnitTag));
	String volUnit = unMangle(unitSystemNode.getAttributeValue(XMLTags.VolumeUnitTag));
	String areaUnit = unMangle(unitSystemNode.getAttributeValue(XMLTags.AreaUnitTag));
	String lengthUnit = unMangle(unitSystemNode.getAttributeValue(XMLTags.LengthUnitTag));
	String timeUnit = unMangle(unitSystemNode.getAttributeValue(XMLTags.TimeUnitTag));
	
	return ModelUnitSystem.createVCModelUnitSystem(volSubsUnit, memSubsUnit, lumpedSubsUnit, volUnit, areaUnit, lengthUnit, timeUnit);
}

/**
 * This method returns a nodeReference onject from a XML representation.
 * Creation date: (4/24/2001 5:35:56 PM)
 * @return cbit.vcell.model.NodeReference
 * @param param org.jdom.Element
 */
private NodeReference getNodeReference(Element param) throws XmlParseException{
	String tempname = param.getName();
	NodeReference newNodeRef = null;
	
	int type = NodeReference.UNKNOWN_NODE;
	String name = null;
	//determine the type of nodereference to create
	if  ( tempname.equalsIgnoreCase(XMLTags.SpeciesContextShapeTag) ) {
		type = NodeReference.SPECIES_CONTEXT_NODE;
		name = unMangle(param.getAttributeValue( XMLTags.SpeciesContextRefAttrTag ));
	} else if  ( tempname.equalsIgnoreCase(XMLTags.SimpleReactionShapeTag) ) {
		type = NodeReference.SIMPLE_REACTION_NODE;
		name = unMangle(param.getAttributeValue( XMLTags.SimpleReactionRefAttrTag ));
	} else if  ( tempname.equalsIgnoreCase(XMLTags.FluxReactionShapeTag) ) {
		type = NodeReference.FLUX_REACTION_NODE;
		name = unMangle(param.getAttributeValue( XMLTags.FluxReactionRefAttrTag ));
	} else if  ( tempname.equalsIgnoreCase(XMLTags.ReactionRuleShapeTag) ) {
		type = NodeReference.REACTION_RULE_NODE;
		name = unMangle(param.getAttributeValue( XMLTags.ReactionRuleRef2AttrTag ));
	} else if  ( tempname.equalsIgnoreCase(XMLTags.RuleParticipantFullShapeTag) || tempname.equalsIgnoreCase(XMLTags.RuleParticipantShapeTag) ) {
		type = NodeReference.RULE_PARTICIPANT_SIGNATURE_FULL_NODE;
		name = unMangle(param.getAttributeValue( XMLTags.RuleParticipantRefAttrTag ));
	} else if  ( tempname.equalsIgnoreCase(XMLTags.RuleParticipantShortShapeTag) ) {
		type = NodeReference.RULE_PARTICIPANT_SIGNATURE_SHORT_NODE;
		name = unMangle(param.getAttributeValue( XMLTags.RuleParticipantRefAttrTag ));
	} else {
		throw new XmlParseException("An unknown type was found " + tempname+",when processing noderefence!");
	}
	String modeString = unMangle(param.getAttributeValue(XMLTags.NodeReferenceModeAttrTag));
	NodeReference.Mode mode = NodeReference.Mode.none;
	if(modeString != null) {
		mode = Mode.fromValue(modeString);
	}
	java.awt.Point location = new java.awt.Point( Integer.parseInt(param.getAttributeValue(XMLTags.LocationXAttrTag)), Integer.parseInt(param.getAttributeValue(XMLTags.LocationYAttrTag)) );
	newNodeRef = new NodeReference(mode, type, name, location);
	return newNodeRef;
}


/**
 * This method returns an OdeEquation from a XML Element.
 * Creation date: (5/17/2001 3:52:40 PM)
 * @return cbit.vcell.math.OdeEquation
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
private OdeEquation getOdeEquation(Element param, MathDescription mathDesc) throws XmlParseException {
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
private OutputTimeSpec getOutputTimeSpec(Element param) {
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
private OutsideVariable getOutsideVariable(Element param) {
	//Get name
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	//get VolVariableRef
	String volvarName = unMangle( param.getAttributeValue(XMLTags.VolumeVariableAttrTag) );

	//*** create new OutsideVariable ***
	OutsideVariable variable = new OutsideVariable(name , volvarName);
	transcribeComments(param, variable);
	
	return variable;
}


/**
 * This method returns a PdeEquation from a XML element.
 * Creation date: (4/26/2001 12:11:14 PM)
 * @return cbit.vcell.math.PdeEquation
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */

private PdeEquation getPdeEquation(Element param, MathDescription mathDesc) throws XmlParseException {
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

        //process BoundaryConditionValues
        {
	    	Iterator<Element> iterator = param.getChildren( XMLTags.BoundaryConditionValueTag, vcNamespace ).iterator();
	    	if(iterator != null) {
	    		while (iterator.hasNext()) {
	    			tempelement = (Element)iterator.next();
	    			try {
	    				pdeEquation.addBoundaryConditionValue( getBoundaryConditionValue(tempelement, pdeEquation) );
	    			} catch (MathException e) {
	    				e.printStackTrace();
	    				throw new XmlParseException("A MathException was fired when adding a BoundaryConditionValue to the compartmentSubDomain " + name, e);
	    			}
	    		}
	    	}
    	}

        {
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
        }
        {
        	//add Grad
        	Element gradElement = param.getChild(XMLTags.GradientTag, vcNamespace);
        	if (gradElement != null) {
        		String tempStr = null;
        		tempStr = gradElement.getAttributeValue(XMLTags.XAttrTag);
        		if (tempStr != null) {
        			pdeEquation.setGradientX(unMangleExpression(tempStr));    //all grad dimensions are optional.
        		}
        		tempStr = gradElement.getAttributeValue(XMLTags.YAttrTag);
        		if (tempStr != null) {
        			pdeEquation.setGradientY(unMangleExpression(tempStr));
        		}
        		tempStr = gradElement.getAttributeValue(XMLTags.ZAttrTag);
        		if (tempStr != null) {
        			pdeEquation.setGradientZ(unMangleExpression(tempStr));
        		}
        	}
        }        
    } catch (Exception e) {
        e.printStackTrace();
        throw new XmlParseException(e);
    }

    return pdeEquation;
}


/**
 * This method returns a VCImageRegion from a XML Representation.
 * Creation date: (5/2/2001 12:17:05 PM)
 * @return cbit.image.VCImageRegion
 * @param param org.jdom.Element
 */
private VCPixelClass getPixelClass(Element param) {
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
private Product getProduct(Element param, ReactionStep reaction, Model model) throws XmlParseException {
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
private Reactant getReactant(Element param, ReactionStep reaction, Model model) throws XmlParseException {
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
private ReactionSpec getReactionSpec(Element param, SimulationContext simulationContext) throws XmlParseException{
	ReactionSpec reactionspec = null;

	//retrieve the reactionstep reference
	String reactionstepname = unMangle( param.getAttributeValue(XMLTags.ReactionStepRefAttrTag) );
	ReactionStep reactionstepref = (ReactionStep)simulationContext.getModel().getReactionStep(reactionstepname);
	
	if (reactionstepref ==null) {
		throw new XmlParseException("The reference to the ReactionStep " + reactionstepname + ", could not be resolved!");
	}
	//Create the new SpeciesContextSpec
	reactionspec = new ReactionSpec(reactionstepref, simulationContext);

	//set the reactionMapping value
	String temp = param.getAttributeValue(XMLTags.ReactionMappingAttrTag);
	try {
		reactionspec.setReactionMapping( temp );
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException was fired when setting the reactionMapping value " + temp +", in a reactionSpec object!", e);
	}

	return reactionspec;
}


/**
 * This method returns a SimpleReaction object from a XML element.
 * Creation date: (3/16/2001 11:52:02 AM)
 * @return cbit.vcell.model.SimpleReaction
 * @param param org.jdom.Element
 */
private SimpleReaction getSimpleReaction(Element param, Model model) throws XmlParseException {
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
    
    String reversibleAttributeValue = param.getAttributeValue(XMLTags.ReversibleAttrTag);
    boolean bReversible = true;
    if (reversibleAttributeValue != null){
    	if (Boolean.TRUE.toString().equals(reversibleAttributeValue)){
    		bReversible = true;
    	} else if (Boolean.FALSE.toString().equals(reversibleAttributeValue)){
    		bReversible = false;
    	} else {
    		throw new RuntimeException("unexpected value "+reversibleAttributeValue+" for reversible flag for reaction "+name);
    	}
    }
    
    try {
        simplereaction = new SimpleReaction(model, structureref, key, name, bReversible);
        if(param.getAttributeValue(XMLTags.SbmlNameAttrTag) != null) {
        	simplereaction.setSbmlName(unMangle(param.getAttributeValue(XMLTags.SbmlNameAttrTag)));
        }
    } catch (java.beans.PropertyVetoException e) {
        e.printStackTrace();
        throw new XmlParseException("An error occurred while trying to create the simpleReaction " + name, e);
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
			throw new XmlParseException("A propertyVetoException was fired when setting the fluxOption to the flux reaction " + name, e);
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
		throw new XmlParseException("Error adding a reactant to the reaction "+ name, e);
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
		throw new XmlParseException("Error adding a product to the reaction "+ name+" : ", e);
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
		throw new XmlParseException("Error adding a catalyst to the reaction "+ name, e);
	}
 
	//Add Kinetics
	Element tempKinet = param.getChild(XMLTags.KineticsTag, vcNamespace);

	if (tempKinet!= null) {
		simplereaction.setKinetics(getKinetics(tempKinet, simplereaction, model));
	}

	//set the valence (for legacy support for "chargeCarrierValence" stored with reaction).
	String valenceString = null;
	try {
		valenceString = unMangle(param.getAttributeValue(XMLTags.FluxCarrierValenceAttrTag));
		if (valenceString!=null&&valenceString.length()>0){
			KineticsParameter chargeValenceParameter = simplereaction.getKinetics().getChargeValenceParameter();
			if (chargeValenceParameter!=null){
				chargeValenceParameter.setExpression(new Expression(Integer.parseInt(unMangle(valenceString))));
			}
		}
	} catch (NumberFormatException e) {
		e.printStackTrace();
		throw new XmlParseException("A NumberFormatException was fired when setting the (integer) valence '"+valenceString+"' (integer) to the reaction " + name, e);
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
Simulation getSimulation(Element param, MathDescription mathDesc) throws XmlParseException {
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
		throw new XmlParseException(e);
	}

	//Retrieve MathOverrides
		simulation.setMathOverrides( getMathOverrides( param.getChild(XMLTags.MathOverridesTag, vcNamespace), simulation) );

	//Retrieve SolverTaskDescription
	try {
		simulation.setSolverTaskDescription( getSolverTaskDescription(param.getChild(XMLTags.SolverTaskDescriptionTag, vcNamespace), simulation) );
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("A PropertyVetoException was fired when setting the SolverTaskDescroiption object to the Simulation object "+ name, e);
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
			throw new XmlParseException("A ProperyVetoException was fired when setting the MeshSpecification to a new Simulation!", e);
		}
	}
	
	return simulation;
}

//public because it's being called in simcontexttable to read from the app components element
public NetworkConstraints getAppNetworkConstraints(Element e, Model newModel) {
	RbmModelContainer mc = newModel.getRbmModelContainer();
	NetworkConstraints nc = new NetworkConstraints();
	String s = e.getAttributeValue(XMLTags.RbmMaxIterationTag);
	if(s!=null && !s.isEmpty()) {
		int maxIteration = Integer.parseInt(s);
		nc.setMaxIteration(maxIteration);
	}
	s = e.getAttributeValue(XMLTags.RbmMaxMoleculesPerSpeciesTag);
	if(s!=null && !s.isEmpty()) {
		int maxMoleculesPerSpecies = Integer.parseInt(s);
		nc.setMaxMoleculesPerSpecies(maxMoleculesPerSpecies);
	}
	s = e.getAttributeValue(XMLTags.RbmSpeciesLimitTag);
	if(s!=null && !s.isEmpty()) {
		int speciesLimit = Integer.parseInt(s);
		nc.setSpeciesLimit(speciesLimit);
	}
	s = e.getAttributeValue(XMLTags.RbmReactionsLimitTag);
	if(s!=null && !s.isEmpty()) {
		int reactionsLimit = Integer.parseInt(s);
		nc.setReactionsLimit(reactionsLimit);
	}
	List<Element> children = e.getChildren(XMLTags.RbmMaxStoichiometryTag, vcNamespace);
	for (Element element : children) {
		Integer i = 1;
		MolecularType mt = null;
		s = element.getAttributeValue(XMLTags.RbmIntegerAttrTag);
		if(s!=null && !s.isEmpty()) {
			i = Integer.valueOf(s);
		}
		s = element.getAttributeValue(XMLTags.RbmMolecularTypeTag);
		if(s!=null && !s.isEmpty()) {
			mt = mc.getMolecularType(s);
		}
		if(mt != null) {
			nc.setMaxStoichiometry(mt, i);
		}
	}
	return nc;
}
/**
 * This method returns a SimulationContext from a XML representation.
 * Creation date: (4/2/2001 3:19:01 PM)
 * @return cbit.vcell.mapping.SimulationContext
 * @param param org.jdom.Element
 */
private SimulationContext getSimulationContext(Element param, BioModel biomodel) throws XmlParseException{
	//get the attributes
	String name = unMangle(param.getAttributeValue(XMLTags.NameAttrTag)); //name
	boolean bStoch = false;
	boolean bRuleBased = false;
	boolean bUseConcentration = true;
	boolean bRandomizeInitCondition = false;
	boolean bInsufficientIterations = false;
	boolean bInsufficientMaxMolecules = false;
	boolean bMassConservationModelReduction = true;		// default is true for now
	
	NetworkConstraints nc = null;
	Element ncElement = param.getChild(XMLTags.RbmNetworkConstraintsTag, vcNamespace);
	if(ncElement != null) {
		nc = getAppNetworkConstraints(ncElement, biomodel.getModel());	// one network constraint element
	} else {
		if(legacyNetworkConstraints != null) {
			nc = legacyNetworkConstraints;
		}
	}

	if ((param.getAttributeValue(XMLTags.StochAttrTag)!= null) && (param.getAttributeValue(XMLTags.StochAttrTag).equals("true"))){
		bStoch = true;
	}
	if(bStoch)
	{
		// stochastic and using concentration vs amount
		if((param.getAttributeValue(XMLTags.ConcentrationAttrTag)!= null) && (param.getAttributeValue(XMLTags.ConcentrationAttrTag).equals("false"))) {
			bUseConcentration = false;
		}

		// stochastic and randomizing initial conditions or not (for non-spatial)
		if((param.getAttributeValue(XMLTags.RandomizeInitConditionTag)!= null) && (param.getAttributeValue(XMLTags.RandomizeInitConditionTag).equals("true"))) {
			bRandomizeInitCondition = true;
		}
	}
	if((param.getAttributeValue(XMLTags.MassConservationModelReductionTag)!= null) && (param.getAttributeValue(XMLTags.MassConservationModelReductionTag).equals("false"))) {
		bMassConservationModelReduction = false;
	}
	if((param.getAttributeValue(XMLTags.InsufficientIterationsTag)!= null) && (param.getAttributeValue(XMLTags.InsufficientIterationsTag).equals("true"))) {
		bInsufficientIterations = true;
	}
	if((param.getAttributeValue(XMLTags.InsufficientMaxMoleculesTag)!= null) && (param.getAttributeValue(XMLTags.InsufficientMaxMoleculesTag).equals("true"))) {
		bInsufficientMaxMolecules = true;
	}
	if ((param.getAttributeValue(XMLTags.RuleBasedAttrTag)!= null) && (param.getAttributeValue(XMLTags.RuleBasedAttrTag).equals("true"))){
		bRuleBased = true;
		if((param.getAttributeValue(XMLTags.ConcentrationAttrTag)!= null) && (param.getAttributeValue(XMLTags.ConcentrationAttrTag).equals("false"))) {
			bUseConcentration = false;
		}
		if((param.getAttributeValue(XMLTags.RandomizeInitConditionTag)!= null) && (param.getAttributeValue(XMLTags.RandomizeInitConditionTag).equals("true"))) {
			// we propagate the flag but we don't use it for now
			bRandomizeInitCondition = true;
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
			"A Problem occurred while retrieving the geometry for the simulationContext " + name, e);
	}
	
	//Retrieve MathDescription(if there is no MathDescription skip it)
	MathDescription newmathdesc = null;
	Element xmlMathDescription = param.getChild(XMLTags.MathDescriptionTag, vcNamespace);
	if (xmlMathDescription!=null) {
		newmathdesc = getMathDescription( xmlMathDescription, newgeometry );
		if(biomodel.getVersion() != null && biomodel.getVersion().getVersionKey() != null) {
			Long lpcBMKey = Long.valueOf(biomodel.getVersion().getVersionKey().toString());
			
	//		MathDescription.originalHasLowPrecisionConstants.remove(lpcBMKey);
			try {
				Enumeration<Constant> myenum = newmathdesc.getConstants();
				while(myenum.hasMoreElements()) {
					Constant nextElement = myenum.nextElement();
					String name2 = nextElement.getName();
					ReservedSymbol reservedSymbolByName = biomodel.getModel().getReservedSymbolByName(name2);
					if(reservedSymbolByName != null && nextElement.getExpression() != null && reservedSymbolByName.getExpression() != null) {
	//					System.out.println(name2);
						boolean equals = nextElement.getExpression().infix().equals(reservedSymbolByName.getExpression().infix());
	//					System.out.println("--"+" "+nextElement.getExpression().infix() +" "+reservedSymbolByName.getExpression().infix()+" "+equals);
						if(!equals) {
							TreeSet<String> treeSet = MathDescription.originalHasLowPrecisionConstants.get(lpcBMKey);
							if(treeSet == null) {
								treeSet = new TreeSet<>();
								MathDescription.originalHasLowPrecisionConstants.put(lpcBMKey,treeSet);
							}
							treeSet.add(newmathdesc.getVersion().getVersionKey().toString());
							break;
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//Retrieve Version (Metada)
	Version version = getVersion( param.getChild(XMLTags.VersionTag, vcNamespace) );

	//------ Create SimContext ------
	SimulationContext newsimcontext = null;
	
	try {
		newsimcontext = new SimulationContext(biomodel.getModel(), newgeometry, newmathdesc, version, bStoch, bRuleBased);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException("A propertyveto exception was generated when creating the new SimulationContext " + name, e);
	}
	
	//set attributes
	try {
		newsimcontext.setName(name);
		//Add annotation
		String annotation = param.getChildText(XMLTags.AnnotationTag, vcNamespace);
		if (annotation!=null/* && annotation.length()>0*/) {
			newsimcontext.setDescription(unMangle(annotation));
		}
		//set if using concentration
		newsimcontext.setUsingConcentration(bUseConcentration);
		// set mass conservation model reduction flag
		newsimcontext.setUsingMassConservationModelReduction(bMassConservationModelReduction);
		// set if randomizing init condition or not (for stochastic applications
		if (bStoch) {
			newsimcontext.setRandomizeInitConditions(bRandomizeInitCondition);
		}
		if(bInsufficientIterations) {
			newsimcontext.setInsufficientIterations(bInsufficientIterations);
		}
		if(bInsufficientMaxMolecules) {
			newsimcontext.setInsufficientMaxMolecules(bInsufficientMaxMolecules);
		}
		if(nc != null) {
			newsimcontext.setNetworkConstraints(nc);
		}
		 
	} catch(java.beans.PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException("Exception", e);
	} 
	
	String tempchar = param.getAttributeValue(XMLTags.CharacteristicSizeTag);
	if (tempchar!=null) {
		try {
			newsimcontext.setCharacteristicSize( Double.valueOf(tempchar) );
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException("A PropertyVetoException was fired when setting the CharacteristicSize "+ tempchar, e);
		}
	}
	
	// Retrieve DataContext
	Element dataContextElement = param.getChild(XMLTags.DataContextTag, vcNamespace);
	if (dataContextElement != null) {
		DataContext dataContext = newsimcontext.getDataContext();
		ArrayList<DataSymbol> dataSymbols = getDataSymbols(dataContextElement, dataContext, newsimcontext.getModel().getUnitSystem());
		for (int i = 0; i < dataSymbols.size(); i++) {
			dataContext.addDataSymbol(dataSymbols.get(i));
		}
	}
	
	// Retrieve spatialObjects and add to simContext
	Element spatialObjectsElement = param.getChild(XMLTags.SpatialObjectsTag, vcNamespace);
	if(spatialObjectsElement != null){
		SpatialObject[] spatialObjects = getSpatialObjects(newsimcontext, spatialObjectsElement);
		try {
			newsimcontext.setSpatialObjects(spatialObjects);
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error adding spatialObjects to simulationContext", e);
		}
	}

	// Retrieve application parameters and add to simContext
	Element appParamsElement = param.getChild(XMLTags.ApplicationParametersTag, vcNamespace);
	if(appParamsElement != null){
		SimulationContextParameter[] appParameters = getSimulationContextParams(appParamsElement, newsimcontext);
		try {
			newsimcontext.setSimulationContextParameters(appParameters);
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error adding application parameters to simulationContext", e);
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
		newsimcontext.getGeometryContext().refreshStructureMappings();
		newsimcontext.refreshSpatialObjects();
	} catch (MappingException e) {
		e.printStackTrace();
		throw new XmlParseException("A MappingException was fired when trying to set the StructureMappings array to the Geometrycontext of the SimContext "+ name, e);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new XmlParseException("A PopertyVetoException was fired when trying to set the StructureMappings array to the Geometrycontext of the SimContext "+ name, e);
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
		int rSpecCounter = 0;
		for (Element rsElement : children){
			reactionSpecs[rSpecCounter] = getReactionSpec(rsElement, newsimcontext);
			rSpecCounter ++;
		}
		try {
			newsimcontext.getReactionContext().setReactionSpecs(reactionSpecs);
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException("A PropertyVetoException occurred while setting the ReactionSpecs to the SimContext " + name, e);
		}
	}
	
	// Retrieve ReactionRuleSpecs
	Element reactionRuleSpecsElement = tempelement.getChild(XMLTags.ReactionRuleSpecsTag, vcNamespace);
	if (reactionRuleSpecsElement != null){
		ReactionRuleSpec[] reactionRuleSpecs = getReactionRuleSpecs(newsimcontext, reactionRuleSpecsElement);
		try {
			newsimcontext.getReactionContext().setReactionRuleSpecs(reactionRuleSpecs);
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException("A PropertyVetoException occurred while setting the ReactionRuleSpecs to the SimContext " + name,e);
		}
	}
	
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
			throw new XmlParseException(e);		
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
				throw new XmlParseException(e);
			}
		}
		
		//read ground electrode
		if (electElem.getChild(XMLTags.ElectrodeTag, vcNamespace)!=null) {
			Electrode groundElectrode = getElectrode(electElem.getChild(XMLTags.ElectrodeTag, vcNamespace), newsimcontext);
			
			try{
				newsimcontext.setGroundElectrode(groundElectrode);
			} catch (java.beans.PropertyVetoException e) {
				e.printStackTrace(System.out);
				throw new XmlParseException(e);
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
			throw new RuntimeException("Error adding events to simulationContext", e);
		}
	}

	// Retrieve spatialProcesses and add to simContext
	tempelement = param.getChild(XMLTags.SpatialProcessesTag, vcNamespace);
	if(tempelement != null){
		SpatialProcess[] spatialProcesses = getSpatialProcesses(newsimcontext, tempelement);
		try {
			newsimcontext.setSpatialProcesses(spatialProcesses);
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error adding spatialProcesses to simulationContext", e);
		}
	}

	// Retrieve rate rules and add to simContext
	tempelement = param.getChild(XMLTags.RateRulesTag, vcNamespace);
	if(tempelement != null){
		RateRule[] rateRules = getRateRules(newsimcontext, tempelement);
		try {
			newsimcontext.setRateRules(rateRules);
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error adding rate rules to simulationContext", e);
		}
	}
	tempelement = param.getChild(XMLTags.AssignmentRulesTag, vcNamespace);
	if(tempelement != null){
		AssignmentRule[] assignmentRules = getAssignmentRules(newsimcontext, tempelement);
		try {
			newsimcontext.setAssignmentRules(assignmentRules);
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error adding assignment rules to simulationContext", e);
		}
	}

	org.jdom.Element analysisTaskListElement = param.getChild(XMLTags.AnalysisTaskListTag, vcNamespace);
	if (analysisTaskListElement!=null){
		children = analysisTaskListElement.getChildren(XMLTags.ParameterEstimationTaskTag, vcNamespace);
		if (children.size()!=0) {
			Vector<ParameterEstimationTask> analysisTaskList = new Vector<ParameterEstimationTask>();
			for (Element parameterEstimationTaskElement : children){
				try {
					ParameterEstimationTask parameterEstimationTask = ParameterEstimationTaskXMLPersistence.getParameterEstimationTask(parameterEstimationTaskElement,newsimcontext);
					analysisTaskList.add(parameterEstimationTask);
				}catch (Exception e){
					e.printStackTrace(System.out);
					throw new XmlParseException("An Exception occurred when parsing AnalysisTasks of SimContext " + name, e);
				}
			}
			try {
				AnalysisTask[] analysisTasks = (AnalysisTask[])BeanUtils.getArray(analysisTaskList,AnalysisTask.class);
				newsimcontext.setAnalysisTasks(analysisTasks);
			} catch (java.beans.PropertyVetoException e) {
				e.printStackTrace(System.out);
				throw new XmlParseException("A PropertyVetoException occurred when setting the AnalysisTasks of the SimContext " + name, e);
			}
		}
	}
	
	// Microscope Measurement
	org.jdom.Element element = param.getChild(XMLTags.MicroscopeMeasurement, vcNamespace);
	if (element != null) {
		getMicroscopeMeasurement(element, newsimcontext);
	}
	
	
	for (GeometryClass gc : newsimcontext.getGeometry().getGeometryClasses()) {
		try {
			StructureSizeSolver.updateUnitStructureSizes(newsimcontext, gc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	newsimcontext.getGeometryContext().enforceHierarchicalBoundaryConditions(newsimcontext.getModel().getStructureTopology());
	
	return newsimcontext;
}

public void getMicroscopeMeasurement(Element element, SimulationContext simContext) {
	MicroscopeMeasurement microscopeMeasurement = simContext.getMicroscopeMeasurement();
	
	String name = element.getAttributeValue(XMLTags.NameAttrTag);
	microscopeMeasurement.setName(name);
		
	Element kernelElement = element.getChild(XMLTags.ConvolutionKernel, vcNamespace);
	String type = kernelElement.getAttributeValue(XMLTags.TypeAttrTag);
	ConvolutionKernel ck = null;
	if (type.equals(XMLTags.ConvolutionKernel_Type_ProjectionZKernel)) {
		ck = new ProjectionZKernel();
	} else if (type.equals(XMLTags.ConvolutionKernel_Type_GaussianConvolutionKernel)) {
		Element e = kernelElement.getChild(XMLTags.KernelGaussianSigmaXY, vcNamespace);
		String s = e.getText();	
		Expression sigmaXY = unMangleExpression(s);
		
		e = kernelElement.getChild(XMLTags.KernelGaussianSigmaZ, vcNamespace);
		s = e.getText();	
		Expression sigmaZ = unMangleExpression(s);
		
		ck = new GaussianConvolutionKernel(sigmaXY, sigmaZ);
	}
	microscopeMeasurement.setConvolutionKernel(ck);
	List<Element> children = element.getChildren(XMLTags.FluorescenceSpecies, vcNamespace);
	for (Element c : children) {
		String speciesName = c.getAttributeValue(XMLTags.NameAttrTag);
		SpeciesContext sc = simContext.getModel().getSpeciesContext(speciesName);
		microscopeMeasurement.addFluorescentSpecies(sc);
	}
}

private ArrayList<DataSymbol> getDataSymbols(Element dataContextElement, DataContext dataContext, ModelUnitSystem modelUnitSystem) {
	ArrayList<DataSymbol> dataSymbolsList = new ArrayList<DataSymbol>();
	// iterate over fieldDatasymbols. When other dataSymbol types are implemented, repeat this loop.
	Iterator dataSymbolsElementIter = dataContextElement.getChildren(XMLTags.FieldDataSymbolTag, vcNamespace).iterator();
	while (dataSymbolsElementIter.hasNext()) {
		Element dataSymbolElement = (Element)dataSymbolsElementIter.next();
		String dataSymbolName = unMangle(dataSymbolElement.getAttributeValue(XMLTags.DataSymbolNameTag));
		DataSymbolType dataSymbolType = DataSymbolType.fromDatabaseName(unMangle(dataSymbolElement.getAttributeValue(XMLTags.DataSymbolTypeTag)));
		String symbol = dataSymbolElement.getAttributeValue(XMLTags.VCUnitDefinitionAttrTag);
		VCUnitDefinition vcUnitDefinition = null;
		if (symbol != null) {
			vcUnitDefinition = modelUnitSystem.getInstance(symbol);
		}
		// ExternalDataIdentifier dataSetID in FieldDataSymbol
		Element dataSetIDElement = dataSymbolElement.getChild(XMLTags.ExternalDataIdentifierTag, vcNamespace);
		String name = unMangle(dataSetIDElement.getAttributeValue(XMLTags.NameAttrTag));
		String key = unMangle(dataSetIDElement.getAttributeValue(XMLTags.KeyValueAttrTag));
		String userID = unMangle(dataSetIDElement.getAttributeValue(XMLTags.OwnerNameAttrTag));
		String userKey = unMangle(dataSetIDElement.getAttributeValue(XMLTags.OwnerKeyAttrTag));
		User owner = new User(userID, new KeyValue(userKey));
		ExternalDataIdentifier edi = new ExternalDataIdentifier(new KeyValue(key), owner, name);
		// ---
		String fieldItemName = unMangle(dataSymbolElement.getAttributeValue(XMLTags.FieldItemNameTag));
		String fieldItemType = unMangle(dataSymbolElement.getAttributeValue(XMLTags.FieldItemTypeTag));
		double fieldItemTime = Double.parseDouble(unMangle(dataSymbolElement.getAttributeValue(XMLTags.FieldItemTimeTag)));
	
		FieldDataSymbol fds = new FieldDataSymbol(dataSymbolName, dataSymbolType, dataContext, vcUnitDefinition,
				edi, fieldItemName, fieldItemType, fieldItemTime);
		dataSymbolsList.add(fds);
	}
	// other while loops for other dataSymbol types; then return cumulative list
	// ...
	
	return dataSymbolsList;
}

/**
 * This method returns a Version object from an XML representation.
 * Creation date: (3/16/2001 3:41:24 PM)
 * @return cbit.sql.Version
 * @param param org.jdom.Element
 */
private SimulationVersion getSimulationVersion(Element xmlVersion) throws XmlParseException {
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
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BeanUtils.vcDateFormat, Locale.US);
			date = simpleDateFormat.parse(temp);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			throw new XmlParseException("Invalid date:"+temp, e);
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
private SolverTaskDescription getSolverTaskDescription(Element param, Simulation simulation) throws XmlParseException{
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
		if (sd == null){
			System.err.println("====================================== couldn't find solver description name ==========================================");
		}
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
		throw new XmlParseException("A PropertyVetoException was fired when setting the taskType: " + taskType, e);
	}
	int numProcessors = parseIntWithDefault(param, XMLTags.NUM_PROCESSORS, 1); 

	try {
		solverTaskDesc.setNumProcessors(numProcessors);
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
			if( simulation.getMathDescription().isNonSpatialStoch() && param.getChild(XMLTags.StochSimOptionsTag, vcNamespace) != null)
			{   //Amended July 22nd, 2007 to read either stochSimOptions or stochHybridOptions
				solverTaskDesc.setStochOpt(getStochSimOptions(param.getChild(XMLTags.StochSimOptionsTag, vcNamespace)));
				if(sd != null && !sd.equals(SolverDescription.StochGibson)){
					solverTaskDesc.setStochHybridOpt(getStochHybridOptions(param.getChild(XMLTags.StochSimOptionsTag, vcNamespace)));
				}
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
		String timeoutDisabledAttributeValue = param.getAttributeValue(XMLTags.TimeoutSimulationDisabled);
		if (timeoutDisabledAttributeValue != null) {
			solverTaskDesc.setTimeoutDisabled(new Boolean(timeoutDisabledAttributeValue).booleanValue());
		}
		String borderExtrapolationDisabled = param.getAttributeValue(XMLTags.BorderExtrapolationDisabled);
		if (borderExtrapolationDisabled != null) {
			solverTaskDesc.setBorderExtrapolationDisabled(new Boolean(borderExtrapolationDisabled).booleanValue());
		}

		Element nfsimSimulationOptionsElement = param.getChild(XMLTags.NFSimSimulationOptions, vcNamespace);
		if (nfsimSimulationOptionsElement != null) {
			NFsimSimulationOptions nfsimSimulationOptions = getNFSimSimulationOptions(nfsimSimulationOptionsElement);
			solverTaskDesc.setNFSimSimulationOptions(nfsimSimulationOptions);			
		}
		Element smoldySimulationOptionsElement = param.getChild(XMLTags.SmoldynSimulationOptions, vcNamespace);
		if (smoldySimulationOptionsElement != null) {
			SmoldynSimulationOptions smoldynSimulationOptions = getSmoldySimulationOptions(smoldySimulationOptionsElement);
			solverTaskDesc.setSmoldynSimulationOptions(smoldynSimulationOptions);			
		}
		Element sundialsPdeSolverOptionsElement = param.getChild(XMLTags.SundialsSolverOptions, vcNamespace);
		if (sundialsPdeSolverOptionsElement != null) {
			SundialsPdeSolverOptions sundialsPdeSolverOptions = getSundialsPdeSolverOptions(sundialsPdeSolverOptionsElement);
			solverTaskDesc.setSundialsPdeSolverOptions(sundialsPdeSolverOptions);			
		}
		Element chomboElement = param.getChild(XMLTags.ChomboSolverSpec, vcNamespace);		
		if (chomboElement != null) {
			ChomboSolverSpec chombo = getChomboSolverSpec(solverTaskDesc, chomboElement, simulation.getMathDescription().getGeometry().getDimension());
			solverTaskDesc.setChomboSolverSpec(chombo);
		}
		Element mbElement = param.getChild(XMLTags.MovingBoundarySolverOptionsTag, vcNamespace);		
		if (mbElement != null) {
			MovingBoundarySolverOptions mb = getMovingBoundarySolverOptions(solverTaskDesc, mbElement);
			solverTaskDesc.setMovingBoundarySolverOptions(mb);
		}
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException(e);
	}
		
	return solverTaskDesc;
}
private NFsimSimulationOptions getNFSimSimulationOptions(Element nfsimSimulationOptionsElement) throws XmlParseException {
	NFsimSimulationOptions so = new NFsimSimulationOptions();
	String temp = null;
	
	temp = nfsimSimulationOptionsElement.getChildText(XMLTags.NFSimSimulationOptions_observableComputationOff, vcNamespace);
	if(temp != null) {
		so.setObservableComputationOff(new Boolean(temp));
	}
	temp = nfsimSimulationOptionsElement.getChildText(XMLTags.NFSimSimulationOptions_moleculeDistance, vcNamespace);
	if(temp != null) {
		so.setMoleculeDistance(new Integer(temp));
	}
	temp = nfsimSimulationOptionsElement.getChildText(XMLTags.NFSimSimulationOptions_aggregateBookkeeping, vcNamespace);
	if(temp != null) {
		so.setAggregateBookkeeping(new Boolean(temp));
	}
	temp = nfsimSimulationOptionsElement.getChildText(XMLTags.NFSimSimulationOptions_maxMoleculesPerType, vcNamespace);
	if(temp != null) {
		so.setMaxMoleculesPerType(new Integer(temp));
	}
	temp = nfsimSimulationOptionsElement.getChildText(XMLTags.NFSimSimulationOptions_equilibrateTime, vcNamespace);
	if(temp != null) {
		so.setEquilibrateTime(new Integer(temp));
	}
	temp = nfsimSimulationOptionsElement.getChildText(XMLTags.NFSimSimulationOptions_randomSeed, vcNamespace);
	if(temp != null) {
		so.setRandomSeed(new Integer(temp));
	}
	temp = nfsimSimulationOptionsElement.getChildText(XMLTags.NFSimSimulationOptions_preventIntraBonds, vcNamespace);
	if(temp != null) {
		so.setPreventIntraBonds(new Boolean(temp));
	}
	temp = nfsimSimulationOptionsElement.getChildText(XMLTags.NFSimSimulationOptions_matchComplexes, vcNamespace);
	if(temp != null) {
		so.setMatchComplexes(new Boolean(temp));
	}
	temp = nfsimSimulationOptionsElement.getChildText(XMLTags.NFSimSimulationOptions_numOfTrials, vcNamespace);
	if(temp != null) {
		so.setNumOfTrials(new Integer(temp));
	}
	return so;
}

private SmoldynSimulationOptions getSmoldySimulationOptions(Element smoldySimulationOptionsElement) throws XmlParseException {
	
	SmoldynSimulationOptions sso = null;	
	if (smoldySimulationOptionsElement != null) {
		sso = new SmoldynSimulationOptions();
		String temp = smoldySimulationOptionsElement.getChildText(XMLTags.SmoldynSimulationOptions_accuracy, vcNamespace);
		if (temp != null) {
			sso.setAccuracy(Double.parseDouble(temp));
		}
		temp = smoldySimulationOptionsElement.getChildText(XMLTags.SmoldynSimulationOptions_randomSeed, vcNamespace);
		if (temp != null) {
			sso.setRandomSeed(new Integer(temp));
		}
		temp = smoldySimulationOptionsElement.getChildText(XMLTags.SmoldynSimulationOptions_gaussianTableSize, vcNamespace);
		if (temp != null) {
			try {
				sso.setGaussianTableSize(Integer.parseInt(temp));
			} catch (NumberFormatException e) {
				e.printStackTrace(System.out);
				throw new XmlParseException(e);
			} catch (PropertyVetoException e) {
				e.printStackTrace(System.out);
				throw new XmlParseException(e);
			}
		}
		temp = smoldySimulationOptionsElement.getChildText(XMLTags.SmoldynSimulationOptions_high_res, vcNamespace);
		if (temp != null) {
			sso.setUseHighResolutionSample(new Boolean(temp));
		}
		temp = smoldySimulationOptionsElement.getChildText(XMLTags.SmoldynSimulationOptions_saveParticleFiles, vcNamespace);
		if (temp != null) {
			sso.setSaveParticleLocations(new Boolean(temp));
		}
		temp = smoldySimulationOptionsElement.getChildText(XMLTags.SmoldynSimulationOptions_stepMultiplier, vcNamespace);
		if (temp != null) {
			sso.setSmoldynStepMultiplier(Integer.parseInt(temp));
		}
	}	
	return sso;
}

private SundialsPdeSolverOptions getSundialsPdeSolverOptions(Element sundialsPdeSolverOptionsElement) throws XmlParseException {
	
	SundialsPdeSolverOptions sundialsPdeSolverOptions = null;	
	if (sundialsPdeSolverOptionsElement != null) {		
		String temp = sundialsPdeSolverOptionsElement.getChildText(XMLTags.SundialsSolverOptions_maxOrderAdvection, vcNamespace);
		if (temp != null) {
			sundialsPdeSolverOptions = new SundialsPdeSolverOptions(Integer.parseInt(temp));
		}
	}	
	return sundialsPdeSolverOptions;
}

public ModelParameter[] getModelParams(Element globalParams, Model model) throws XmlParseException {
	Iterator<Element> globalsIterator = globalParams.getChildren(XMLTags.ParameterTag, vcNamespace).iterator();
	Vector<ModelParameter> modelParamsVector = new Vector<ModelParameter>();
	while (globalsIterator.hasNext()) {
		org.jdom.Element paramElement = (Element) globalsIterator.next();
		modelParamsVector.add(getModelParameter(paramElement, model));
	}
	return ((ModelParameter[])BeanUtils.getArray(modelParamsVector, ModelParameter.class));
}


public ModelParameter getModelParameter(Element paramElement, Model model) throws XmlParseException {
	//get its attributes : name, role and unit definition
	String glParamName = unMangle( paramElement.getAttributeValue(XMLTags.NameAttrTag) );
	String role = paramElement.getAttributeValue(XMLTags.ParamRoleAttrTag);
	ModelUnitSystem modelUnitSystem = model.getUnitSystem();
	int glParamRole = -1;
	if (role.equals(XMLTags.ParamRoleUserDefinedTag)) {
		glParamRole = Model.ROLE_UserDefined;
	} else {
		throw new RuntimeException("unknown type of model parameter (not user-defined)");
	}
//
//	int glParamRole = -1;
//	if (role.equals(XMLTags.ParamRoleUserDefinedTag)) {
//		glParamRole = Model.ROLE_UserDefined;
//	} else	if (role.equals(XMLTags.RoleVariableRateTag)) {
//		glParamRole = Model.ROLE_VariableRate;
//	} else {
//		throw new RuntimeException("unknown type of model parameter (not user-defined or variable rate)");
//	}
	String unitSymbol = paramElement.getAttributeValue(XMLTags.VCUnitDefinitionAttrTag);
	VCUnitDefinition glParamUnit = null;
	if (unitSymbol != null) {
		glParamUnit = modelUnitSystem.getInstance(unitSymbol);
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
	try {
		ModelParameter newGlParam = model.new ModelParameter(glParamName, glParamExp, glParamRole, glParamUnit);
		if(paramElement.getAttributeValue(XMLTags.SbmlNameAttrTag) != null) {
			String sbmlName = unMangle(paramElement.getAttributeValue(XMLTags.SbmlNameAttrTag));
			if(sbmlName != null && !sbmlName.isEmpty()) {
				newGlParam.setSbmlName(sbmlName);
			}
		}
		newGlParam.setModelParameterAnnotation(glParamAnnotation);
		return newGlParam;
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new XmlParseException("An error occurred while trying to create the ModelParameter " + glParamName, e);
	}
}

public SimulationContextParameter[] getSimulationContextParams(Element appParams, SimulationContext simContext) throws XmlParseException {
	Iterator<Element> appParamIterator = appParams.getChildren(XMLTags.ParameterTag, vcNamespace).iterator();
	ArrayList<SimulationContextParameter> appParamsList = new ArrayList<SimulationContextParameter>();
	while (appParamIterator.hasNext()) {
		org.jdom.Element paramElement = (Element) appParamIterator.next();
		appParamsList.add(getSimulationContextParameter(paramElement, simContext));
	}
	return appParamsList.toArray(new SimulationContextParameter[0]);
}


public SimulationContextParameter getSimulationContextParameter(Element paramElement, SimulationContext simContext) {
	//get its attributes : name, role and unit definition
	String appParamName = unMangle( paramElement.getAttributeValue(XMLTags.NameAttrTag) );
	String role = paramElement.getAttributeValue(XMLTags.ParamRoleAttrTag);
	ModelUnitSystem modelUnitSystem = simContext.getModel().getUnitSystem();
	int appParamRole = -1;
	if (role.equals(XMLTags.ParamRoleUserDefinedTag)) {
		appParamRole = SimulationContext.ROLE_UserDefined;
	} else {
		throw new RuntimeException("unknown type of application parameter (not user-defined)");
	}
	String unitSymbol = paramElement.getAttributeValue(XMLTags.VCUnitDefinitionAttrTag);
	VCUnitDefinition appParamUnit = null;
	if (unitSymbol != null) {
		appParamUnit = modelUnitSystem.getInstance(unitSymbol);
	}
	//get parameter contents : expression; annotation, if any.
	String appParamExpStr = paramElement.getText();
	Expression appParamExp = unMangleExpression(appParamExpStr);
//	String appParamAnnotation = null;
//	String annotationText = paramElement.getChildText(XMLTags.AnnotationTag, vcNamespace);
//	if (annotationText != null && annotationText.length() > 0) {
//		appParamAnnotation = unMangle(annotationText);
//	}
	
	//create new global parameter
	SimulationContextParameter newAppParam = simContext.new SimulationContextParameter(appParamName, appParamExp, appParamRole, appParamUnit);
//	newGlParam.setModelParameterAnnotation(appParamAnnotation);

	return newAppParam;
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
private Species getSpecies(Element param) throws XmlParseException {
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
			throw new XmlParseException(e);
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
private SpeciesContext getSpeciesContext(Element param, Model model) throws XmlParseException{
	//retrieve its information
	String name = unMangle(param.getAttributeValue(XMLTags.NameAttrTag));
	String sbmlName = unMangle(param.getAttributeValue(XMLTags.SbmlNameAttrTag));
	String hasOverrideString = param.getAttributeValue(XMLTags.HasOverrideAttrTag);
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
	
	SpeciesPattern sp = null;
	Element element = param.getChild(XMLTags.RbmSpeciesPatternTag, vcNamespace);
	if(element != null) {
		sp = getSpeciesPattern(element, model);
		sp.resolveBonds();
		if(sp == null) {
			throw new XmlParseException("XMLReader: getSpeciesContext: SpeciesPattern is missing.");
		}
	}
	//---try to create the speciesContext---
	SpeciesContext speciecontext = null;
	speciecontext = new SpeciesContext( key, name, specieref, structureref, sp);
	try {
		speciecontext.setSbmlName(sbmlName);
	} catch (PropertyVetoException e) {		// can't happen here, whatever we saved must have been correct
		// throw new XmlParseException("The SbmlName is invalid");
		e.printStackTrace();
	}
	return speciecontext;
}


/**
 * This method returns a SpeciesContextSpec object from a XML representation.
 * Creation date: (4/26/2001 4:14:01 PM)
 * @return cbit.vcell.mapping.SpeciesContextSpec
 * @param param org.jdom.Element
 */
private void getSpeciesContextSpecs(List<Element> scsChildren, ReactionContext rxnContext, Model model) throws XmlParseException{
	for (int i = 0; i < scsChildren.size(); i++) {
		Element scsElement = scsChildren.get(i); 
		SpeciesContextSpec specspec = null;
		//Get Atributes
		String speccontname = unMangle( scsElement.getAttributeValue(XMLTags.SpeciesContextRefAttrTag) );
		boolean constant = Boolean.valueOf(scsElement.getAttributeValue(XMLTags.ForceConstantAttrTag)).booleanValue();
		//boolean enabledif = Boolean.valueOf(scsElement.getAttributeValue(XMLTags.EnableDiffusionAttrTag)).booleanValue();
		String spatialStr = scsElement.getAttributeValue(XMLTags.SpatialAttrTag);
		Boolean spatial = null;
		if (spatialStr!=null){
			spatial = Boolean.valueOf(spatialStr);
		}
		
		String bWellMixedStr = scsElement.getAttributeValue(XMLTags.WellMixedAttrTag);
		Boolean bWellMixed = null;
		if (bWellMixedStr!=null){
			bWellMixed = Boolean.valueOf(bWellMixedStr);
		}
		
		String bForceContinuousStr = scsElement.getAttributeValue(XMLTags.ForceContinuousAttrTag);
		Boolean bForceContinuous = null;
		if (bForceContinuousStr!=null){
			bForceContinuous = Boolean.valueOf(bForceContinuousStr);
		}
		
		//Retrieve reference
		SpeciesContext specref = model.getSpeciesContext(speccontname);
		if (specref == null) {
			throw new XmlParseException("The SpeciesContext " + speccontname +" refrence could not be resolved!");
		}
		
		// get SpeciesContextSpec from reactionContext & specRef
	 	specspec = rxnContext.getSpeciesContextSpec(specref);
		//set attributes
		specspec.setConstant( constant);
//	try {
//		specspec.setEnableDiffusing( enabledif );
//	} catch (MappingException e) {
//		e.printStackTrace();
//		throw new XmlParseException("error setting the 'enableDiffusing' property of a SpeciesContext: "+e.getMessage());
//	}
		if (spatial!=null){
			specspec.setWellMixed(!spatial);
		}
		if (bWellMixed!=null){
			specspec.setWellMixed(bWellMixed);
		}
		if (bForceContinuous!=null){
			specspec.setForceContinuous(bForceContinuous);
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
			throw new XmlParseException("An expressionException was fired when setting the InitilaconditionExpression "+ temp + ", for a SpeciesContextSpec!", e);
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
				throw new XmlParseException("An ExpressionException was fired when setting the diffusionExpression " + temp + " to a SpeciesContextSpec!", e);
			}
		}
		
		//Get Boundaries if any
		Element tempElement = scsElement.getChild(XMLTags.BoundariesTag, vcNamespace);
		if (tempElement != null) {
			try {
				//Xm
				temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueXm);
				if (temp != null) {
					specspec.getBoundaryXmParameter().setExpression(unMangleExpression(temp));
				}
				//Xp
				temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueXp);
				if (temp != null) {
					specspec.getBoundaryXpParameter().setExpression(unMangleExpression(temp));
				}
				//Ym
				temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueYm);
				if (temp != null) {
					specspec.getBoundaryYmParameter().setExpression(unMangleExpression(temp));
				}
				//Yp
				temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueYp);
				if (temp != null) {
					specspec.getBoundaryYpParameter().setExpression(unMangleExpression(temp));
				}
				//Zm
				temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueZm);
				if (temp != null) {
					specspec.getBoundaryZmParameter().setExpression(unMangleExpression(temp));
				}
				//Zp
				temp = tempElement.getAttributeValue(XMLTags.BoundaryAttrValueZp);
				if (temp != null) {
					specspec.getBoundaryZpParameter().setExpression(unMangleExpression(temp));
				}
			} catch (ExpressionException e) {
				e.printStackTrace();
				throw new XmlParseException("An ExpressionException was fired when Setting the boundary Expression: " + unMangle(temp), e);
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
			} catch (ExpressionException e) {
				e.printStackTrace();
				throw new XmlParseException("Error setting Velocity parameter for '" + specspec.getSpeciesContext().getName(), e);
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
private NonspatialStochSimOptions getStochSimOptions(Element param) {
	//get attributes
	boolean isUseCustomSeed  = Boolean.parseBoolean( param.getAttributeValue(XMLTags.UseCustomSeedAttrTag) );
	int customSeed = 0;
	if(isUseCustomSeed)
		customSeed = Integer.parseInt( param.getAttributeValue(XMLTags.CustomSeedAttrTag) );
	int numOfTrials = Integer.parseInt( param.getAttributeValue(XMLTags.NumberOfTrialAttrTag) );
	boolean bHistogram = true;	// initialize for old style cases when XMLTags.IsHistogram may be missing
	if(numOfTrials == 1) {
		bHistogram = false;
	}
	String histAttr = param.getAttributeValue(XMLTags.IsHistogram);
	if(histAttr != null && !histAttr.isEmpty()) {
		bHistogram = Boolean.parseBoolean(histAttr);
	}

	return new NonspatialStochSimOptions(isUseCustomSeed, customSeed, numOfTrials, bHistogram);
}


/**
 * This method returns a TimeStep object from a XML Element.
 * Creation date: (5/22/2001 11:45:33 AM)
 * @return cbit.vcell.solver.TimeStep
 * @param param org.jdom.Element
 */
private NonspatialStochHybridOptions getStochHybridOptions(Element param) {
	// StochHybridOptions are immutable, so we grab the default values from the default constructor - and read the options which are stored in XML
	NonspatialStochHybridOptions defaultStochHybridOptions = new NonspatialStochHybridOptions();
	double epsilon = defaultStochHybridOptions.getEpsilon();
	double lambda = defaultStochHybridOptions.getLambda();
	double MSRTolerance = defaultStochHybridOptions.getMSRTolerance();
	double SDETDolerance = defaultStochHybridOptions.getSDETolerance();
	if (param.getAttributeValue(XMLTags.HybridEpsilonAttrTag) != null){
		epsilon = Double.parseDouble( param.getAttributeValue(XMLTags.HybridEpsilonAttrTag) );
	}
	if (param.getAttributeValue(XMLTags.HybridLambdaAttrTag) != null){
		lambda = Double.parseDouble( param.getAttributeValue(XMLTags.HybridLambdaAttrTag) );
	}
	if (param.getAttributeValue(XMLTags.HybridMSRToleranceAttrTag) !=null){
		MSRTolerance = Double.parseDouble( param.getAttributeValue(XMLTags.HybridMSRToleranceAttrTag) );
	}
	if (param.getAttributeValue(XMLTags.HybridSDEToleranceAttrTag) !=null){
		SDETDolerance = Double.parseDouble( param.getAttributeValue(XMLTags.HybridSDEToleranceAttrTag) );
	}
	//**** create a new StochHybridOptions object and return ****
	return new NonspatialStochHybridOptions(epsilon, lambda, MSRTolerance, SDETDolerance);
}


/**
 * This method returns a Stochasitc volumn variable from a XML element.
 * Creation date: (7/24/2006 5:05:51 PM)
 * @return cbit.vcell.math.StochVolVariable
 * @param param org.jdom.Element
 */
private StochVolVariable getStochVolVariable(Element param) 
{
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	//-- create new StochVolVariable object
	StochVolVariable stochVar = new StochVolVariable(name);
	transcribeComments(param, stochVar);

	return stochVar;
}

/**
 * This method returns a SubVolume element type from a XML representation.
 * Creation date: (4/26/2001 4:14:01 PM)
 * @return SubVolume
 * @param param org.jdom.Element
 */
private SubVolume getSubVolume(Element param) throws XmlParseException{
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
		} else if ( typeString.equalsIgnoreCase(XMLTags.CSGBasedTypeTag) ) {
			//Process Constructed Solid Geometry based
			newsubvolume = getCSGObject( param, null );
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

public CSGObject getCSGObject(Element param, KeyValue keyFromDB) throws XmlParseException {
	//retrieve the attributes
	String name = param.getAttributeValue(XMLTags.NameAttrTag);
	int handle = Integer.parseInt( param.getAttributeValue(XMLTags.HandleAttrTag) );
	
	//process the key
	KeyValue key = null;
	String temp = param.getAttributeValue(XMLTags.KeyValueAttrTag);
	
	if (temp!=null && temp.length()>0 && this.readKeysFlag) {
		key = new KeyValue( temp );
	}
	if (keyFromDB != null) {
		key = keyFromDB;
	}

	//Retrieve CSGObject CSGNode - CSGObject element should have one child (the root node of the CSGObject) 
	Object[] elements = param.getChildren().toArray();
	if (elements.length > 1) {
		throw new XmlParseException("CSGObject subvolume element cannot have more than one child element");
	}
	CSGNode csgRootNode = getCSGNode((Element)elements[0]);
	
	//Create the CSGObject
	CSGObject newCSGObjectSubvol =  new CSGObject(key, name, handle);
	newCSGObjectSubvol.setRoot(csgRootNode);

	return  newCSGObjectSubvol;
}

private CSGNode getCSGNode(Element param) throws XmlParseException {
	String nodeNameString = param.getName();
	CSGNode csgNode = null;
	
	if (nodeNameString!=null) {
		if ( nodeNameString.equalsIgnoreCase(XMLTags.CSGPrimitiveTag) ) {
			//Process CSGPrimitive 		
			csgNode = getCSGPrimitive( param );
		} else if ( nodeNameString.equalsIgnoreCase(XMLTags.CSGPseudoPrimitiveTag) ) {
			//Process CSGPseudoPrimitive
			csgNode = getCSGPseudoPrimitive( param );
		} else if ( nodeNameString.equalsIgnoreCase(XMLTags.CSGSetOperatorTag) ) {
			//Process CSGSetOperator
			csgNode = getCSGSetOperator( param );
		} else if ( nodeNameString.equalsIgnoreCase(XMLTags.CSGHomogeneousTransformationTag) ) {
			//Process CSGHomogeneousTransformation
			csgNode = getCSGHomogeneousTransformation( param );
		} else if ( nodeNameString.equalsIgnoreCase(XMLTags.CSGRotationTag) ) {
			//Process CSGRotation
			csgNode = getCSGRotation( param );
		} else if ( nodeNameString.equalsIgnoreCase(XMLTags.CSGScaleTag) ) {
			//Process CSGScale
			csgNode = getCSGScale( param );
		} else if ( nodeNameString.equalsIgnoreCase(XMLTags.CSGTranslationTag) ) {
			//Process CSGTranslation
			csgNode = getCSGTranslation( param );
		} else {
			//Throw an exception
			throw new XmlParseException("Parse Error! Unknown CSGNode type : "+nodeNameString);
		}

	} else {
		throw new XmlParseException("CSGNode : cannot be null");
	}
	
	return csgNode;
}


private CSGPrimitive getCSGPrimitive(Element param) {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String  primitiveTypeStr = unMangle(param.getAttributeValue(XMLTags.CSGPrimitiveTypeTag));
	
	//---Create the new CSGPrimitive object ---
	PrimitiveType type = CSGPrimitive.PrimitiveType.valueOf(primitiveTypeStr);
	CSGPrimitive csgPrimitive = new CSGPrimitive(name, type);

	return csgPrimitive;
}

private CSGPseudoPrimitive getCSGPseudoPrimitive(Element param) throws XmlParseException {
	throw new XmlParseException("CSGPseudoPrimitive not implemented yet.");
}


private CSGSetOperator getCSGSetOperator(Element param) throws XmlParseException {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String operatorTypeStr = unMangle(param.getAttributeValue(XMLTags.CSGSetOperatorTypeTag));
	
	//---Create the new CSGSetOperator object ---
	OperatorType type = CSGSetOperator.OperatorType.valueOf(operatorTypeStr);
	CSGSetOperator csgSetOperator = new CSGSetOperator(name, type);
	
	List<Element> children = param.getChildren();
	Iterator<Element> iterator = children.iterator();
	int count = 0;
	while (iterator.hasNext()) {
		Element tempElement = iterator.next();	
		CSGNode csgNode = getCSGNode(tempElement);
		csgSetOperator.addChild(csgNode);
	}

	return csgSetOperator;
}

private CSGHomogeneousTransformation getCSGHomogeneousTransformation(Element param) throws XmlParseException {
	throw new XmlParseException("CSGHomogeneousTransformation not implemented yet.");
}


private CSGRotation getCSGRotation(Element param) throws XmlParseException {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String rotateXStr = unMangle(param.getAttributeValue(XMLTags.CSGRotationXTag));
	String rotateYStr = unMangle(param.getAttributeValue(XMLTags.CSGRotationYTag));
	String rotateZStr = unMangle(param.getAttributeValue(XMLTags.CSGRotationZTag));
	String rotationAngleStr = unMangle(param.getAttributeValue(XMLTags.CSGRotationAngleInRadiansTag));
	Vect3d rotationAxis = new Vect3d(Double.parseDouble(rotateXStr), Double.parseDouble(rotateYStr), Double.parseDouble(rotateZStr));
	CSGRotation csgRotation = new CSGRotation(name, rotationAxis, Double.parseDouble(rotationAngleStr));
	
	//Retrieve CSGNode - CSGRotation element should have one child  
	Object[] elements = param.getChildren().toArray();
	if (elements.length > 1) {
		throw new XmlParseException("CSGRotation element cannot have more than one child element");
	}
	CSGNode csgChildNode = getCSGNode((Element)elements[0]);
	
	csgRotation.setChild(csgChildNode);
	return csgRotation;
}


private CSGScale getCSGScale(Element param) throws XmlParseException {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String scaleXStr = unMangle(param.getAttributeValue(XMLTags.CSGScaleXTag));
	String scaleYStr = unMangle(param.getAttributeValue(XMLTags.CSGScaleYTag));
	String scaleZStr = unMangle(param.getAttributeValue(XMLTags.CSGScaleZTag));
	Vect3d scaleAxis = new Vect3d(Double.parseDouble(scaleXStr), Double.parseDouble(scaleYStr), Double.parseDouble(scaleZStr));
	CSGScale csgScale = new CSGScale(name, scaleAxis);
	
	//Retrieve CSGNode - CSGScale element should have one child  
	Object[] elements = param.getChildren().toArray();
	if (elements.length > 1) {
		throw new XmlParseException("CSGScale element cannot have more than one child element");
	}
	CSGNode csgChildNode = getCSGNode((Element)elements[0]);
	
	csgScale.setChild(csgChildNode);
	return csgScale;
}

private CSGTranslation getCSGTranslation(Element param) throws XmlParseException {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String translateXStr = unMangle(param.getAttributeValue(XMLTags.CSGTranslationXTag));
	String translateYStr = unMangle(param.getAttributeValue(XMLTags.CSGTranslationYTag));
	String translateZStr = unMangle(param.getAttributeValue(XMLTags.CSGTranslationZTag));
	Vect3d translateAxis = new Vect3d(Double.parseDouble(translateXStr), Double.parseDouble(translateYStr), Double.parseDouble(translateZStr));
	CSGTranslation csgTranslation = new CSGTranslation(name, translateAxis);
	
	//Retrieve CSGNode - CSGScale element should have one child  
	Object[] elements = param.getChildren().toArray();
	if (elements.length > 1) {
		throw new XmlParseException("CSGScale element cannot have more than one child element");
	}
	CSGNode csgChildNode = getCSGNode((Element)elements[0]);
	
	csgTranslation.setChild(csgChildNode);
	return csgTranslation;
}



private SurfaceClass getSurfaceClass(Element param,Geometry geom) throws XmlParseException{
		
	Set<SubVolume> surfaceClassSubVolumeSet = new HashSet<SubVolume>();
	KeyValue surfaceClassKey = null;
	String surfaceClassName = null;
	
	surfaceClassName = unMangle( param.getAttributeValue(XMLTags.NameAttrTag));
	String surfaceClassKeyStr = param.getAttributeValue(XMLTags.KeyValueAttrTag);
	surfaceClassKey = (surfaceClassKeyStr==null?null:new KeyValue(surfaceClassKeyStr));
	String subVol1Ref = param.getAttributeValue(XMLTags.SubVolume1RefAttrTag);
	String subVol2Ref = param.getAttributeValue(XMLTags.SubVolume2RefAttrTag);
	if(subVol1Ref != null){
		SubVolume subVolume = geom.getGeometrySpec().getSubVolume(subVol1Ref);
		if(subVolume == null){
			throw new XmlParseException("SurfaceClass missing subvolume '"+subVol1Ref+"'");
		}
		surfaceClassSubVolumeSet.add(subVolume);
	}
	if(subVol2Ref != null){
		SubVolume subVolume = geom.getGeometrySpec().getSubVolume(subVol2Ref);
		if(subVolume == null){
			throw new XmlParseException("SurfaceClass missing subvolume '"+subVol2Ref+"'");
		}
		surfaceClassSubVolumeSet.add(subVolume);
	}
		
	return new SurfaceClass(surfaceClassSubVolumeSet, surfaceClassKey, surfaceClassName);
}
/**
 * This method returns a TimeBounds object from a XML Element.
 * Creation date: (5/22/2001 11:41:04 AM)
 * @return cbit.vcell.solver.TimeBounds
 * @param param org.jdom.Element
 */
private TimeBounds getTimeBounds(Element param) {
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
private TimeStep getTimeStep(Element param) {
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
private User getUser(Element param) {
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
private void addResevedSymbols(VariableHash varHash, Model model) throws XmlParseException {

	//
	// add constants that may be used in kinetics.
	//
	try {
		// add reserved symbols
		varHash.addVariable(new Constant(model.getPI_CONSTANT().getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(model.getFARADAY_CONSTANT().getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(model.getFARADAY_CONSTANT_NMOLE().getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(model.getGAS_CONSTANT().getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(model.getKMILLIVOLTS().getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(model.getN_PMOLE().getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(model.getKMOLE().getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(model.getTEMPERATURE().getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(model.getK_GHK().getName(), new Expression(0.0)));
		varHash.addVariable(new Constant(model.getTIME().getName(), new Expression(0.0)));
	} catch (MathException e){
		e.printStackTrace(System.out);
		throw new XmlParseException("error reordering parameters according to dependencies", e);
	}
}

/**
 * This method return a VarIniCondition object from a XML element.
 * Creation date: (7/24/2006 5:26:05 PM)
 * @return cbit.vcell.math.VarIniCondition
 * @param param org.jdom.Element
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
private VarIniCondition getVarIniCount(Element param, MathDescription md) throws XmlParseException, MathException, ExpressionException
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
		VarIniCondition varIni= new VarIniCount(var,exp);
		return varIni;		
	} catch (Exception e){e.printStackTrace();}
	
	return null;	
}


private BoundaryConditionSpec getBoundaryConditionSpec(Element param) throws XmlParseException, MathException {
	//retrieve values
	String boundarySubdomainName = unMangle( param.getAttributeValue(XMLTags.BoundarySubdomainNameTag) );
	String boundarySubdomainType = unMangle( param.getAttributeValue(XMLTags.BoundaryTypeTag) );

	if (boundarySubdomainName != null && boundarySubdomainType != null) {
		BoundaryConditionSpec bcs = new BoundaryConditionSpec(boundarySubdomainName, new BoundaryConditionType(boundarySubdomainType));
		return bcs;		
	}
	return null;
}


private BoundaryConditionValue getBoundaryConditionValue(Element param, PdeEquation pde) throws XmlParseException, MathException {
	//retrieve values
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	Expression valueExpr = unMangleExpression( param.getAttributeValue(XMLTags.BoundaryValueExpressionTag) );

	if (name != null && valueExpr != null) { 
		BoundaryConditionValue bcv = pde.new BoundaryConditionValue(name, valueExpr);
		return bcv;		
	}
	return null;
}

private VarIniCondition getVarIniPoissonExpectedCount(Element param, MathDescription md) throws XmlParseException, MathException, ExpressionException
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
		VarIniCondition varIni= new VarIniPoissonExpectedCount(var,exp);
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
VCImage getVCImage(Element param, Extent extent) throws XmlParseException{
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
		throw new XmlParseException("An imageException occurred while trying to create a VCImage!", e);
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
		throw new XmlParseException(e);
	}

	//get PixelClasses
	List<Element> pixelClassList = param.getChildren(XMLTags.PixelClassTag, vcNamespace);

	if (pixelClassList.size()!=0) {
		VCPixelClass[] pixelClassArray = new VCPixelClass[pixelClassList.size()];
		int pixelClassCounter = 0;
		for (Element pcElement : pixelClassList){
			pixelClassArray[pixelClassCounter] = getPixelClass(pcElement);
			pixelClassCounter ++;
		}
		try {
			newimage.setPixelClasses(pixelClassArray);
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new XmlParseException(e);
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
private Version getVersion(Element xmlVersion) throws XmlParseException {
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
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BeanUtils.vcDateFormat, Locale.US);
			date = simpleDateFormat.parse(temp);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			throw new XmlParseException("Invalid date:"+temp, e);
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
private VolumeRegionEquation getVolumeRegionEquation(Element param, MathDescription mathDesc) throws XmlParseException {
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
private VolumeRegionVariable getVolumeRegionVariable(Element param) {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String domainStr = unMangle( param.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}
	//-- create new VolumeRegionVariable object
	VolumeRegionVariable volRegVariable = new VolumeRegionVariable( name, domain );
	transcribeComments(param, volRegVariable);

	return volRegVariable;
}


/**
 * This method returns a VolVariable object from a XML Element.
 * Creation date: (5/16/2001 2:56:34 PM)
 * @return cbit.vcell.math.VolVariable
 * @param param org.jdom.Element
 */
private VolVariable getVolVariable(Element param) {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String domainStr = unMangle( param.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}

	//-- create new VolVariable object
	VolVariable volVariable = new VolVariable( name, domain );
	transcribeComments(param, volVariable);

	return volVariable;
}

private VolumeParticleObservable getVolumeParticleObservable(Element param, VariableHash varHash) throws XmlParseException {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String domainStr = unMangle( param.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}
	String molecularTypeString = unMangle( param.getAttributeValue(XMLTags.ParticleMolecularTypePatternTag) );
	ObservableType observableType = ObservableType.fromString(molecularTypeString);
	VolumeParticleObservable var = new VolumeParticleObservable( name, domain, observableType );
	String sequenceAttr = param.getAttributeValue(XMLTags.ParticleObservableSequenceTypeAttrTag);

	if (sequenceAttr != null){
		Sequence sequence = Sequence.fromString(sequenceAttr);
		String sequenceLength = param.getAttributeValue(XMLTags.ParticleObservableSequenceLengthAttrTag);
		var.setSequence(sequence);
		if(sequence != Sequence.Multimolecular) {
			var.setQuantity(Integer.parseInt(sequenceLength));
		}
	}else{
		var.setSequence(Sequence.Multimolecular);
	}
	
	Element volumeParticleSpeciesPatternsElement = param.getChild(XMLTags.VolumeParticleSpeciesPatternsTag, vcNamespace);
	if(volumeParticleSpeciesPatternsElement != null) {
		List<Element> volumeParticleSpeciesPatternList = volumeParticleSpeciesPatternsElement.getChildren(XMLTags.VolumeParticleSpeciesPatternTag, vcNamespace);
		for (Element volumeParticleSpeciesPattern : volumeParticleSpeciesPatternList) {
			String volumeParticleSpeciesPatternName = unMangle( volumeParticleSpeciesPattern.getAttributeValue(XMLTags.NameAttrTag));
			
			Variable v = varHash.getVariable(volumeParticleSpeciesPatternName);
			if(v == null) {
				throw new XmlParseException("failed to find VolumeParticleSpeciesPattern named " + volumeParticleSpeciesPatternName);
			}
			if(v instanceof ParticleSpeciesPattern) {
				var.addParticleSpeciesPattern((ParticleSpeciesPattern)v);
			} else {
				throw new XmlParseException("Variable " + volumeParticleSpeciesPatternName + " is not a ParticleSpeciesPattern");
			}
		}
	}
	return var;
}

private ParticleMolecularComponent getParticleMolecularComponent(String pmtName, Element param) {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag));
	ParticleMolecularComponent var = new ParticleMolecularComponent(pmtName + "_" + name, name);
	List<Element> componentStateList = param.getChildren(XMLTags.ParticleMolecularTypeAllowableStateTag, vcNamespace);
	for (Element componentState : componentStateList) {
		String componentStateName = unMangle( componentState.getAttributeValue(XMLTags.NameAttrTag));
		if(!componentStateName.equals("*")) {
			ParticleComponentStateDefinition p = var.getComponentStateDefinition(componentStateName);
			if(p == null) {
				p = new ParticleComponentStateDefinition(componentStateName);
				var.addComponentStateDefinition(p);
			}
		}
	}
	return var;
}
private ParticleMolecularType getParticleMolecularType(Element param) {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	ParticleMolecularType var = new ParticleMolecularType( name );
	
	List<Element> molecularComponentList = param.getChildren(XMLTags.ParticleMolecularComponentPatternTag, vcNamespace);
	for (Element molecularComponent : molecularComponentList) {
		ParticleMolecularComponent p = getParticleMolecularComponent(name, molecularComponent);
		var.addMolecularComponent(p);
	}
	List<Element> anchorList = param.getChildren(XMLTags.ParticleMolecularTypeAnchorTag, vcNamespace);
	for(Element anchorElement : anchorList) {
		String anchor = unMangle( anchorElement.getAttributeValue(XMLTags.NameAttrTag));
		var.addAnchor(anchor);
	}
	return var;
}

private ParticleMolecularComponentPattern getParticleMolecularComponentPattern(Element param, ParticleMolecularType particleMolecularType)  throws XmlParseException {
	String molecularComponentName = unMangle( param.getAttributeValue(XMLTags.NameAttrTag));
	ParticleMolecularComponent particleMolecularComponent = particleMolecularType.getMolecularComponent(molecularComponentName);
	if (particleMolecularComponent != null){
		ParticleMolecularComponentPattern var = new ParticleMolecularComponentPattern(particleMolecularComponent);
		ParticleComponentStatePattern pcsp = null;
		String componentStateName = unMangle( param.getAttributeValue(XMLTags.StateAttrTag));
		if(componentStateName.equals("*")) {
			pcsp = new ParticleComponentStatePattern();
		} else {
			//ParticleComponentStateDefinition pcsd = new ParticleComponentStateDefinition(componentStateName);		// bad??
			ParticleComponentStateDefinition pcsd = particleMolecularComponent.getComponentStateDefinition(componentStateName);
			if(pcsd == null) {
				throw new XmlParseException("failed to find ParticleComponentStateDefinition named " + molecularComponentName);
			}
			pcsp = new ParticleComponentStatePattern(pcsd);
		}
		var.setComponentStatePattern(pcsp);
		String bondString = unMangle( param.getAttributeValue(XMLTags.BondAttrTag));
		
		ParticleBondType bondType = ParticleBondType.fromSymbol(bondString);
		if (bondType == ParticleBondType.Specified){
			int bondId = Integer.parseInt(bondString);
			var.setBondId(bondId);
		}
		var.setBondType(bondType);
		return var;
	} else {
		throw new XmlParseException("failed to find ParticleMolecularComponent named " + molecularComponentName);
	}
}
private ParticleMolecularTypePattern getParticleMolecularTypePattern(Element param, MathDescription mathDescription) throws XmlParseException {
	String molecularTypeName = unMangle( param.getAttributeValue(XMLTags.NameAttrTag));
	String matchLabel = unMangle( param.getAttributeValue(XMLTags.ParticleMolecularTypePatternMatchLabelAttrTag));
	ParticleMolecularType particleMolecularType = mathDescription.getParticleMolecularType(molecularTypeName);
	if (particleMolecularType != null){
		ParticleMolecularTypePattern var = new ParticleMolecularTypePattern(particleMolecularType);
		if (matchLabel != null){
			var.setMatchLabel(matchLabel);
		}
		List<Element> componentPatternList = param.getChildren(XMLTags.ParticleMolecularComponentPatternTag, vcNamespace);
		for (Element componentPattern : componentPatternList) {
			ParticleMolecularComponentPattern p = getParticleMolecularComponentPattern(componentPattern, particleMolecularType);
			var.addMolecularComponentPattern(p);
		}
		return var;
	}else{
		throw new XmlParseException("failed to find ParticleMolecularType named " + molecularTypeName);
	}
}
private VolumeParticleSpeciesPattern getVolumeParticleSpeciesPattern(Element param, MathDescription mathdes) throws XmlParseException {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String domainStr = unMangle( param.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}
	String location = unMangle( param.getAttributeValue(XMLTags.LocationAttrTag) );
	VolumeParticleSpeciesPattern var = new VolumeParticleSpeciesPattern( name, domain, location);
	
	List<Element> molecularTypeList = param.getChildren(XMLTags.ParticleMolecularTypePatternTag, vcNamespace);
	for (Element molecularType : molecularTypeList) {
		ParticleMolecularTypePattern p = getParticleMolecularTypePattern(molecularType, mathdes);
		var.addMolecularTypePattern(p);
	}

//	Element meshRefineElement = param.getChild(XMLTags.ParticleMolecularTypePatternTag, vcNamespace);		
//	if (meshRefineElement != null) {
//		List<Element> levelElementList = meshRefineElement.getChildren(XMLTags.RefinementLevelTag, vcNamespace);
//		for (Element levelElement : levelElementList) {
	return var;
}

private VolumeParticleVariable getVolumeParticalVariable(Element param) {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String domainStr = unMangle( param.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}
	
	//-- create new VolVariable object
	VolumeParticleVariable var = new VolumeParticleVariable( name, domain );
	transcribeComments(param, var);
	
	return var;
}

private MembraneParticleVariable getMembraneParticalVariable(Element param) {
	String name = unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String domainStr = unMangle( param.getAttributeValue(XMLTags.DomainAttrTag) );
	Domain domain = null;
	if (domainStr!=null){
		domain = new Domain(domainStr);
	}
	
	//-- create new VolVariable object
	MembraneParticleVariable var = new MembraneParticleVariable( name, domain );
	transcribeComments(param, var);
	return var;
}

private interface Convert<T> {
	T parse(String in);
}
private	Convert<Integer> convertInt = new Convert<Integer>() {
			public Integer parse(String in) {
				return Integer.parseInt(in);
			}
		}; 
		
private Convert<Double> convertDouble = new Convert<Double>() {
			public Double parse(String in) {
				return Double.parseDouble(in);
			}
		}; 
		
private	Convert<Boolean> convertBoolean = new Convert<Boolean>() {
			public Boolean parse(String in)  {
				return Boolean.parseBoolean(in);
			}
		}; 

/**
 * parse parent for specified element. Return defaultValue if it's not present
 * @param parent
 * @param tagName
 * @param defaultValue
 * @param c converter to change string into T 
 * @return parsed value, if present, default otherwise
 */
	private <T> T parseWithDefault(Element parent, String tagName, T defaultValue, Convert<T> c) {
		Element child = parent.getChild(tagName,vcNamespace);
		if (child != null) {
			return c.parse(child.getText());
		}
		return defaultValue;
	}
	
	/**
	 * read integer XML
	 * @see #parseWithDefault(Element, String, Object, Convert) 
	 */
	private int parseIntWithDefault(Element parent, String tagName, int defaultValue) {
		return parseWithDefault(parent,tagName,defaultValue,convertInt);
	}
	
	/**
	 * read double XML
	 * @see #parseWithDefault(Element, String, Object, Convert) 
	 */
	private double parseDoubleWithDefault(Element parent, String tagName, double defaultValue) {
		return parseWithDefault(parent,tagName,defaultValue,convertDouble);
	}
	/**
	 * read boolean XML
	 * @see #parseWithDefault(Element, String, Object, Convert) 
	 */
	private boolean parseBooleanWithDefault(Element parent, String tagName, boolean defaultValue) {
		return parseWithDefault(parent,tagName,defaultValue,convertBoolean);
	}
	
	private ChomboSolverSpec getChomboSolverSpec(SolverTaskDescription solverTaskDesc, Element element, int dimension) throws XmlParseException {
		int maxBoxSize = parseIntWithDefault(element, XMLTags.MaxBoxSizeTag, ChomboSolverSpec.getDefaultMaxBoxSize(dimension));
		double fillRatio = parseDoubleWithDefault(element,XMLTags.FillRatioTag, ChomboSolverSpec.getDefaultFillRatio());
		boolean bSaveVCellOutput = parseBooleanWithDefault(element, XMLTags.SaveVCellOutput, true);
		boolean bSaveChomboOutput = parseBooleanWithDefault(element, XMLTags.SaveChomboOutput, false); 
		Element childElement = element.getChild(XMLTags.RefineRatios, vcNamespace);
		List<Integer> refineRatioList = null;
		if (childElement != null)
		{
			String text = childElement.getText();
			if (text != null && !text.isEmpty())
			{
				StringTokenizer st = new StringTokenizer(text, ",");
				if (st.hasMoreTokens())
				{
					refineRatioList = new ArrayList<Integer>();
					while (st.hasMoreElements())
					{
						String token = st.nextToken();
						if (token != null)
						{
							int n = Integer.parseInt(token);
							refineRatioList.add(n);
						}
					}
				}
			}
		}
		Integer viewLevel = null;
		try 
		{
			ChomboSolverSpec css = new ChomboSolverSpec(maxBoxSize, fillRatio, viewLevel, bSaveVCellOutput, bSaveChomboOutput, refineRatioList);
			double smallVolfracThreshold = parseDoubleWithDefault(element, XMLTags.SmallVolfracThreshold, 0);
			int blockFactor = parseIntWithDefault(element, XMLTags.BlockFactorTag, ChomboSolverSpec.DEFAULT_BLOCK_FACTOR);
			boolean bActivateFeatureUnderDevelopment = parseBooleanWithDefault(element, XMLTags.ActivateFeatureUnderDevelopment, false); 
			css.setSmallVolfracThreshold(smallVolfracThreshold);
			css.setActivateFeatureUnderDevelopment(bActivateFeatureUnderDevelopment);
			css.setBlockFactor(blockFactor);
			
			int tagsGrow = parseIntWithDefault(element, XMLTags.TagsGrowTag, ChomboSolverSpec.defaultTagsGrow);
			css.setTagsGrow(tagsGrow);
			
			Element timeBoundsElement = element.getChild(XMLTags.TimeBoundTag, vcNamespace);
			List<Element> timeIntervalElementList = null;
			boolean noTimeBounds = false;
			if (timeBoundsElement == null)
			{
				noTimeBounds = true;
			}
			else
			{
				timeIntervalElementList = timeBoundsElement.getChildren(XMLTags.TimeIntervalTag, vcNamespace);
				if (timeIntervalElementList.size() == 0)
				{
					noTimeBounds = true;
				}
			}
				
			if (noTimeBounds)
			{
				// old format
				double startTime = 0;
				double endTime = solverTaskDesc.getTimeBounds().getEndingTime();
				double timeStep = solverTaskDesc.getTimeStep().getDefaultTimeStep();
				double outputTimeStep = ((UniformOutputTimeSpec)solverTaskDesc.getOutputTimeSpec()).getOutputTimeStep();
				try
				{
					TimeInterval ti = new TimeInterval(startTime, endTime, timeStep, outputTimeStep);
					css.addTimeInterval(ti);
				} 
				catch (IllegalArgumentException ex)
				{
					css.addTimeInterval(TimeInterval.getDefaultTimeInterval());
				}
			}
			else
			{
				for (Element e: timeIntervalElementList)
				{
					String s = e.getAttributeValue(XMLTags.StartTimeAttrTag);
					double startTime = Double.valueOf(s);
					
					s = e.getAttributeValue(XMLTags.EndTimeAttrTag);
					double endTime = Double.valueOf(s);					
					
					s = e.getAttributeValue(XMLTags.TimeStepAttrTag);
					double timeStep = Double.valueOf(s);					
					
					s = e.getAttributeValue(XMLTags.OutputTimeStepAttrTag);
					double outputTimeStep = Double.valueOf(s);		
					TimeInterval ti = new TimeInterval(startTime, endTime, timeStep, outputTimeStep);
					css.addTimeInterval(ti);
				}
			}
			
			Element meshRefineElement = element.getChild(XMLTags.MeshRefinementTag, vcNamespace);		
			if (meshRefineElement != null) 
			{
				if (meshRefineElement.getChildren().size() != 0)
				{
					// in old model, if there is no refinement, set view level to finest
					// only set viewLevel when meshRefinement has children
					Element viewLevelChild = element.getChild(XMLTags.ViewLevelTag,vcNamespace);
					if (viewLevelChild != null)
					{
						viewLevel = parseIntWithDefault(element,XMLTags.ViewLevelTag, 0);
						css.setViewLevel(viewLevel);
					}
				}
				List<Element> levelElementList = meshRefineElement.getChildren(XMLTags.RefinementRoiTag, vcNamespace);
				for (Element levelElement : levelElementList) {
					String levelStr = levelElement.getAttributeValue(XMLTags.RefineRoiLevelAttrTag);
					int level = 1;
					if (levelStr != null)
					{
						level = Integer.parseInt(levelStr);
					}
					String type = levelElement.getAttributeValue(XMLTags.RefinementRoiTypeAttrTag);
					RoiType roiType = RoiType.Membrane;
					if (type != null)
					{
						try
						{
							roiType = RoiType.valueOf(type);
						}
						catch (Exception ex)
						{
							// ignore
						}
					}
					
					Element expElement = levelElement.getChild(XMLTags.ROIExpressionTag, vcNamespace);
					String roiExp = null;
					if (expElement != null)
					{
						roiExp = expElement.getText();
						RefinementRoi roi = new RefinementRoi(roiType, level, roiExp);
						css.addRefinementRoi(roi);
					}
				}
			}
			return css;
		} catch (ExpressionException e) {
			throw new XmlParseException(e);
		}
	}
	
	private MovingBoundarySolverOptions getMovingBoundarySolverOptions(SolverTaskDescription solverTaskDesc,
			Element mbElement) {
		double frontToNodeRatio = parseDoubleWithDefault(mbElement, XMLTags.FrontToNodeRatioTag,
				MovingBoundarySolverOptions.DEFAULT_FRONT_TO_NODE_RATIO);
		int redistributionFrequency = parseIntWithDefault(mbElement, XMLTags.RedistributionFrequencyTag,
				MovingBoundarySolverOptions.DEFAULT_REDISTRIBUTION_FREQUENCY);

		RedistributionMode redistributionMode = RedistributionMode.FULL_REDIST;
		Element child = mbElement.getChild(XMLTags.RedistributionModeTag, vcNamespace);
		if (child != null) {
			String text = child.getText();
			redistributionMode = RedistributionMode.valueOf(text);
		}
		RedistributionVersion redistributionVersion = RedistributionVersion.EQUI_BOND_REDISTRIBUTE;
		child = mbElement.getChild(XMLTags.RedistributionVersionTag, vcNamespace);
		if (child != null) {
			String text = child.getText();
			redistributionVersion = RedistributionVersion.valueOf(text);
		}
		ExtrapolationMethod extrapolationMethod = ExtrapolationMethod.NEAREST_NEIGHBOR;
		child = mbElement.getChild(XMLTags.ExtrapolationMethodTag, vcNamespace);
		if (child != null) {
			String text = child.getText();
			extrapolationMethod = ExtrapolationMethod.valueOf(text);
		}
		MovingBoundarySolverOptions mb = new MovingBoundarySolverOptions(frontToNodeRatio, redistributionMode, redistributionVersion, redistributionFrequency, extrapolationMethod);
		return mb;
	}
}
