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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.vcell.chombo.ChomboSolverSpec;
import org.vcell.chombo.RefinementRoi;
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
import org.vcell.pathway.persistence.PathwayProducerBiopax3;
import org.vcell.pathway.persistence.RDFXMLContext;
import org.vcell.relationship.RelationshipModel;
import org.vcell.relationship.persistence.RelationshipProducer;
import org.vcell.util.Commented;
import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.Hex;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.GroupAccessAll;
import org.vcell.util.document.GroupAccessSome;
import org.vcell.util.document.Version;
import org.vcell.util.document.Versionable;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCPixelClass;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.importer.MicroscopyXMLTags;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.IdentifiableProvider;
import cbit.vcell.biomodel.meta.xml.XMLMetaDataWriter;
import cbit.vcell.data.DataContext;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.FieldDataSymbol;
import cbit.vcell.dictionary.BoundCompound;
import cbit.vcell.dictionary.BoundEnzyme;
import cbit.vcell.dictionary.BoundProtein;
import cbit.vcell.dictionary.CompoundInfo;
import cbit.vcell.dictionary.EnzymeInfo;
import cbit.vcell.dictionary.EnzymeRef;
import cbit.vcell.dictionary.ProteinInfo;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.CSGHomogeneousTransformation;
import cbit.vcell.geometry.CSGNode;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.CSGPrimitive;
import cbit.vcell.geometry.CSGPseudoPrimitive;
import cbit.vcell.geometry.CSGRotation;
import cbit.vcell.geometry.CSGScale;
import cbit.vcell.geometry.CSGSetOperator;
import cbit.vcell.geometry.CSGTransformation;
import cbit.vcell.geometry.CSGTranslation;
import cbit.vcell.geometry.CompartmentSubVolume;
import cbit.vcell.geometry.ControlPointCurve;
import cbit.vcell.geometry.Curve;
import cbit.vcell.geometry.Filament;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
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
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.CurrentDensityClampStimulus;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.ElectricalStimulus.ElectricalStimulusParameterType;
import cbit.vcell.mapping.Electrode;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.MicroscopeMeasurement;
import cbit.vcell.mapping.MicroscopeMeasurement.ConvolutionKernel;
import cbit.vcell.mapping.MicroscopeMeasurement.GaussianConvolutionKernel;
import cbit.vcell.mapping.MicroscopeMeasurement.ProjectionZKernel;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.ParameterContext.ParameterRoleEnum;
import cbit.vcell.mapping.RateRule;
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.ReactionRuleSpec;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
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
import cbit.vcell.mapping.spatial.processes.SurfaceKinematics;
import cbit.vcell.mapping.spatial.processes.VolumeKinematics;
import cbit.vcell.math.Action;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.ComputeCentroidComponentEquation;
import cbit.vcell.math.ComputeMembraneMetricEquation;
import cbit.vcell.math.ComputeNormalComponentEquation;
import cbit.vcell.math.Constant;
import cbit.vcell.math.ConvolutionDataGenerator;
import cbit.vcell.math.ConvolutionDataGenerator.ConvolutionDataGeneratorKernel;
import cbit.vcell.math.ConvolutionDataGenerator.GaussianConvolutionDataGeneratorKernel;
import cbit.vcell.math.DataGenerator;
import cbit.vcell.math.Equation;
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
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneParticleVariable;
import cbit.vcell.math.MembraneRegionEquation;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.OutsideVariable;
import cbit.vcell.math.ParticleComponentStateDefinition;
import cbit.vcell.math.ParticleJumpProcess;
import cbit.vcell.math.ParticleMolecularComponent;
import cbit.vcell.math.ParticleMolecularComponentPattern;
import cbit.vcell.math.ParticleMolecularComponentPattern.ParticleBondType;
import cbit.vcell.math.ParticleMolecularType;
import cbit.vcell.math.ParticleMolecularTypePattern;
import cbit.vcell.math.ParticleObservable;
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
import cbit.vcell.math.PostProcessingBlock;
import cbit.vcell.math.ProjectionDataGenerator;
import cbit.vcell.math.RandomVariable;
import cbit.vcell.math.StochVolVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.SubDomain.BoundaryConditionSpec;
import cbit.vcell.math.UniformDistribution;
import cbit.vcell.math.VarIniCondition;
import cbit.vcell.math.VarIniPoissonExpectedCount;
import cbit.vcell.math.Variable;
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
import cbit.vcell.model.Macroscopic_IRRKinetics;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Microscopic_IRRKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ElectricalTopology;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.NernstKinetics;
import cbit.vcell.model.NodeReference;
import cbit.vcell.model.Product;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.RbmKineticLaw;
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
import cbit.vcell.modelopt.AnalysisTask;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.ParameterEstimationTaskXMLPersistence;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.NFsimSimulationOptions;
import cbit.vcell.solver.NonspatialStochHybridOptions;
import cbit.vcell.solver.NonspatialStochSimOptions;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SmoldynSimulationOptions;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.SundialsPdeSolverOptions;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.units.VCUnitDefinition;

/**
 * This class concentrates all the XML production code from Java objects.
 * Creation date: (2/14/2001 3:40:30 PM)
 * @author: Daniel Lucio
 */
public class Xmlproducer extends XmlBase{
	//This variable is used as a flag to determine if the keys should generated or not.
	//By the default, is set to false to not affect the current code and behavior of the VCell framework.
	private boolean printKeysFlag = false;

/**
 * this is the default constructor.
 */
public Xmlproducer(boolean printkeys) {
	super();
	this.printKeysFlag = printkeys;
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2006 9:25:06 AM)
 * @return Element
 * @param analysisTasks cbit.vcell.modelopt.AnalysisTask[]
 */
private Element getXML(AnalysisTask[] analysisTasks) {
	Element analysisTaskListElement = new Element(XMLTags.AnalysisTaskListTag);

	for (int i = 0; i < analysisTasks.length; i++){
		Element analysisTaskElement = null;
		if (analysisTasks[i] instanceof ParameterEstimationTask){
			analysisTaskElement = ParameterEstimationTaskXMLPersistence.getXML((ParameterEstimationTask)analysisTasks[i]);
		}else{
			throw new RuntimeException("XML persistence not supported for analysis type "+analysisTasks[i].getClass().getName());
		}
		analysisTaskListElement.addContent(analysisTaskElement);
	}
		
	return analysisTaskListElement;
}


/**
 * This methods returns a XML represnetation of a VCImage object.
 * Creation date: (3/1/2001 3:02:37 PM)
 * @return Element
 * @param param cbit.image.VCImage
 */
Element getXML(VCImage param) throws XmlParseException{
		Element image = new Element(XMLTags.ImageTag);

		//add atributes
		image.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
//		image.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(param.getDescription()));

		//Add annotation
		if (param.getDescription()!=null && param.getDescription().length()>0) {
			Element annotationElement = new Element(XMLTags.AnnotationTag);
			annotationElement.setText( mangle(param.getDescription()) );
			image.addContent(annotationElement);
		}

		//Add Imagedata subelement
		byte[] compressedPixels = null;
		try {
			compressedPixels = param.getPixelsCompressed();
		} catch (ImageException e) {
			e.printStackTrace();
			throw new XmlParseException("An ImageParseException occurred when tring to retrieving the compressed Pixels", e);
		}
		Element imagedata = new Element(XMLTags.ImageDataTag);
		
		//Get imagedata attributes
		imagedata.setAttribute(XMLTags.XAttrTag, String.valueOf(param.getNumX()));
		imagedata.setAttribute(XMLTags.YAttrTag, String.valueOf(param.getNumY()));
		imagedata.setAttribute(XMLTags.ZAttrTag, String.valueOf(param.getNumZ()));
		imagedata.setAttribute(XMLTags.CompressedSizeTag, String.valueOf(compressedPixels.length));
		//Get imagedata content
		imagedata.addContent(Hex.toString(compressedPixels)); //encode
		//Add imagedata to VCImage element
		image.addContent(imagedata);
		//Add PixelClass elements
		VCPixelClass pixelClasses[] = param.getPixelClasses();
		for (int i = 0; i < pixelClasses.length; i++){
			image.addContent( getXML(pixelClasses[i]) );
		}

		//Add Metadata information
		if (param.getVersion()!=null) {
			image.addContent( getXML(param.getVersion(), param) );
		}

		return image;
}


/**
 * This method returns a XML version from a PixelClass object.
 * Creation date: (5/2/2001 3:10:35 PM)
 * @return Element
 * @param param cbit.image.VCImageRegion
 */
private Element getXML(VCPixelClass param) {
	
	//Create PixelClass subelement
	Element pixelclass = new Element(XMLTags.PixelClassTag);
	//add attributes
	pixelclass.setAttribute( XMLTags.NameAttrTag, param.getPixelClassName() );
	pixelclass.setAttribute( XMLTags.ImagePixelValueTag, String.valueOf(param.getPixel()));

	//If keyFlag is on print the Keyvalue
	if (param.getKey() !=null && this.printKeysFlag) {
		pixelclass.setAttribute(XMLTags.KeyValueAttrTag, param.getKey().toString());
	}	
	
	return pixelclass;
}


/**
 * This method returns a XML representation of a Version java object.
 * Creation date: (3/13/2001 6:00:59 PM)
 * @return Element
 * @param param cbit.sql.Version
 */
private Element getXML(Version version, Versionable versionable) {
	return getXML(version, versionable.getName(), versionable.getDescription());
}


/**
 * This method returns a XML representation of a Version java object.
 * Creation date: (3/13/2001 6:00:59 PM)
 * @return Element
 * @param param cbit.sql.Version
 */
private Element getXML(Version version, String nameParam, String descriptionParam) {
	//** Dump the content to the 'Version' object **
	Element versionElement = new Element(XMLTags.VersionTag);
	
	//if empty just return null
	if (version == null || this.printKeysFlag == false) {
		//*** If the version is empty, then use the versionable ***
		//*Name
		versionElement.setAttribute(XMLTags.NameAttrTag, mangle(nameParam));
		//Specify if it comes from a versionable
		versionElement.setAttribute(XMLTags.FromVersionableTag, "true");
		//*Annotation
		if (descriptionParam!=null && descriptionParam.length()>0) {
			Element annotationElem = new Element(XMLTags.AnnotationTag);
			annotationElem.setText(mangle(descriptionParam));
			versionElement.addContent(annotationElem);
		}		
	} else {
		//** Dump the content of the 'Version' object **
		//*Name
		versionElement.setAttribute(XMLTags.NameAttrTag, mangle(version.getName()));
		//*Key
		versionElement.setAttribute(XMLTags.KeyValueAttrTag, version.getVersionKey().toString());
		//*Owner
		Element owner = new Element(XMLTags.OwnerTag);
		owner.setAttribute(XMLTags.NameAttrTag, mangle(version.getOwner().getName()));
		owner.setAttribute(XMLTags.IdentifierAttrTag, version.getOwner().getID().toString());
		versionElement.addContent(owner);
		//*Access
		versionElement.addContent(getXML( version.getGroupAccess() ));
		//*BranchPointRef
		if (version.getBranchPointRefKey()!=null) {
			versionElement.setAttribute(XMLTags.BranchPointRefTag, version.getBranchPointRefKey().toString());			
		}
		//*BranchID
		versionElement.setAttribute(XMLTags.BranchIdAttrTag, version.getBranchID().toString());
		//*Flag
		versionElement.setAttribute(XMLTags.FlagAttrTag, String.valueOf(version.getFlag().getIntValue()));		
		//*Date
		java.text.SimpleDateFormat newDateFormatter = new java.text.SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", java.util.Locale.US);
		if (version.getDate() != null) {
			versionElement.setAttribute(XMLTags.DateAttrTag, newDateFormatter.format(version.getDate()));
		}
		//Specify if it comes from a versionable
		versionElement.setAttribute(XMLTags.FromVersionableTag, "false");
		//*Annotation
		if (version.getAnnot()!=null && version.getAnnot().length()>0) {
			Element annotationElem = new Element(XMLTags.AnnotationTag);
			annotationElem.setText(mangle(version.getAnnot()));
			versionElement.addContent(annotationElem);
		}
		if (version instanceof org.vcell.util.document.SimulationVersion){
			org.vcell.util.document.SimulationVersion simVersion = (org.vcell.util.document.SimulationVersion)version;
			if (simVersion.getParentSimulationReference()!=null){
				Element parentSimRefElem = new Element(XMLTags.ParentSimRefTag);
				parentSimRefElem.setAttribute(XMLTags.KeyValueAttrTag, simVersion.getParentSimulationReference().toString());
				versionElement.addContent(parentSimRefElem);
			}
		}
	}
	
	return versionElement;
}


/**
 * This method returns a XML representation of a Extent object.
 * Creation date: (2/28/2001 5:51:36 PM)
 * @return Element
 * @param param Extent
 */
public Element getXML(Extent param) throws XmlParseException{

	Element extent = new Element(XMLTags.ExtentTag);
	//Add extent attributes
	extent.setAttribute(XMLTags.XAttrTag, String.valueOf(param.getX()));
	extent.setAttribute(XMLTags.YAttrTag, String.valueOf(param.getY()));
	extent.setAttribute(XMLTags.ZAttrTag, String.valueOf(param.getZ()));

	return extent;
}

public Element getXML(Origin param) throws XmlParseException{

	Element origin = new Element(XMLTags.OriginTag);
	//Add extent attributes
	origin.setAttribute(XMLTags.XAttrTag, String.valueOf(param.getX()));
	origin.setAttribute(XMLTags.YAttrTag, String.valueOf(param.getY()));
	origin.setAttribute(XMLTags.ZAttrTag, String.valueOf(param.getZ()));

	return origin;
}

/**
 * This method returns a XML representation for a Biomodel object.
 * Creation date: (2/14/2001 3:41:13 PM)
 * @return Element
 * @param param cbit.vcell.biomodel.BioModel
 */
@SuppressWarnings("deprecation")
public Element getXML(BioModel param) throws XmlParseException, ExpressionException {
	//Creation of BioModel Node
	Element biomodelnode = new Element(XMLTags.BioModelTag);
	String name = param.getName();

	//Add attributes
	biomodelnode.setAttribute(XMLTags.NameAttrTag, name);
	//biomodelnode.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(param.getDescription()));
	//Add annotation
	if (param.getDescription()!=null && param.getDescription().length()>0) {
		Element annotationElem = new Element(XMLTags.AnnotationTag);
		annotationElem.setText(mangle(param.getDescription()));
		biomodelnode.addContent(annotationElem);
	}
	
	//Get Model
	try {
		biomodelnode.addContent( getXML(param.getModel()) );
	} catch (XmlParseException e) {
		e.printStackTrace();
		throw new XmlParseException("An error occurred while processing the BioModel " + name, e);
	}
	//Get SimulationContexts
	if ( param.getSimulationContexts()!=null ){
		for (int index=0;index<param.getSimulationContexts().length;index++){
			biomodelnode.addContent( getXML(param.getSimulationContext(index),param) );
		}
	}
	//Add Database Metadata (Version) information
	if (param.getVersion() != null) {
		biomodelnode.addContent( getXML(param.getVersion(), param.getName(), param.getDescription()) );
	}
	//Add bioPAX and relationship information
	if (param.getPathwayModel() != null) {
		biomodelnode.addContent(getXML(param.getPathwayModel(), new RDFXMLContext()));
	}
	RelationshipModel rm = param.getRelationshipModel();
	if (rm != null) {
		biomodelnode.addContent(getXML(rm, param));
	}

	biomodelnode.addContent(XMLMetaDataWriter.getElement(param.getVCMetaData(), param));
	return biomodelnode;
}

private Element getXML(PathwayModel pathwayModel, RDFXMLContext context) {
	Element pathwayElement = new Element(XMLTags.PathwayModelTag);
	String biopaxVersion = "3.0";
	// create root element of rdf for BioPAX level 3
	Namespace rdf = Namespace.getNamespace("rdf", DefaultNameSpaces.RDF.uri);
	Element rootElement = new Element("RDF", rdf);
	rootElement.setAttribute("version", biopaxVersion);
	
	// get element from producer and add it to root element
	PathwayProducerBiopax3 xmlProducer = new PathwayProducerBiopax3(context);
	xmlProducer.getXML(pathwayModel, rootElement);	// here is work done

	pathwayElement.addContent(rootElement);
	return pathwayElement;
}
private Element getXML(RelationshipModel relationshipModel, IdentifiableProvider provider) {
	Element relationshipElement = new Element(XMLTags.RelationshipModelTag);
	String biopaxVersion = "3.0";	// we'll use biopax version here
	Element rootElement = new Element("RMNS");
	rootElement.setAttribute("version", biopaxVersion);
	
	RelationshipProducer xmlProducer = new RelationshipProducer();
	xmlProducer.getXML(relationshipModel, rootElement, provider);
	relationshipElement.addContent(rootElement);
	return relationshipElement;
}


/**
 * This method returns a XML representation for a DBFormalSpecies.
 * Creation date: (6/3/2003 4:50:15 PM)
 * @return Element
 * @dbformalSpecies param cbit.vcell.dictionary.DBFormalSpecies
 */
private Element getXML(DBFormalSpecies dbformalSpecies) throws XmlParseException {
	//create XML object
	Element dbSpeciesElement = new Element(XMLTags.DBFormalSpeciesTag);
	//add FormalSpeciesKey
	dbSpeciesElement.setAttribute(XMLTags.KeyValueAttrTag, dbformalSpecies.getDBFormalSpeciesKey().toString());
	
	if (dbformalSpecies instanceof cbit.vcell.dictionary.FormalCompound) {
		dbSpeciesElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.CompoundTypeTag);
	} else if (dbformalSpecies instanceof cbit.vcell.dictionary.FormalEnzyme) {
		dbSpeciesElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.EnzymeTypeTag);
	} else if (dbformalSpecies instanceof cbit.vcell.dictionary.FormalProtein) {
		dbSpeciesElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.ProteinTypeTag);
	} else {
		throw new XmlParseException("DBFormalSpecies type "+dbformalSpecies.getClass().getName()+" not supported yet!");
	}

	//add FormalSpeciesInfo
	dbSpeciesElement.addContent( getXML(dbformalSpecies.getFormalSpeciesInfo()) );
	
	return dbSpeciesElement;
}


/**
 * This method returns the XML representation of a DBSpecies.
 * Creation date: (6/3/2003 4:36:40 PM)
 * @return Element
 * @param dbSpecies cbit.vcell.dictionary.DBSpecies
 */
private Element getXML(DBSpecies dbSpecies) throws XmlParseException {
	//create xml node
	Element dbSpeciesElement = new Element(XMLTags.DBSpeciesTag);

	if (this.printKeysFlag){
		//add the DBSpecieKey (IT ALWAYS NEED THE KEY!)
		dbSpeciesElement.setAttribute(XMLTags.KeyValueAttrTag, dbSpecies.getDBSpeciesKey().toString());

	}
	//detect the type of DBSpecie
	if (dbSpecies instanceof BoundCompound) {
		//add type
		dbSpeciesElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.CompoundTypeTag);
		//add FormalCompound
		dbSpeciesElement.addContent(getXML(((BoundCompound)dbSpecies).getFormalCompound()));
	} else if (dbSpecies instanceof BoundEnzyme) {
		//add type
		dbSpeciesElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.EnzymeTypeTag);
		//add FormalEnzyme
		dbSpeciesElement.addContent(getXML(((BoundEnzyme)dbSpecies).getFormalEnzyme()));
	} else if (dbSpecies instanceof BoundProtein) {
		//add type
		dbSpeciesElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.ProteinTypeTag);
		//add FormalProtein
		dbSpeciesElement.addContent(getXML(((BoundProtein)dbSpecies).getProteinEnzyme()));
	} else {
		throw new XmlParseException("DBSpecies type "+dbSpecies.getClass().getName()+" not supported yet!");
	}
	return dbSpeciesElement;
}


/**
 * This method returns a XML representation for a FormalSpeciesInfo.
 * Creation date: (6/3/2003 5:14:09 PM)
 * @return Element
 * @param speciesInfo cbit.vcell.dictionary.FormalSpeciesInfo
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
private Element getXML(FormalSpeciesInfo speciesInfo) throws XmlParseException {
	//Create XML object
	Element speciesInfoElement = new Element(XMLTags.FormalSpeciesInfoTag);

	//add formalID
	speciesInfoElement.setAttribute(XMLTags.FormalIDTag, mangle(speciesInfo.getFormalID()));

	//add names
	String[] namesArray = speciesInfo.getNames();

	for (int i = 0; i < namesArray.length; i++){
		Element nameElement = new Element(XMLTags.NameTag);
		nameElement.addContent(mangle(namesArray[i]));
		speciesInfoElement.addContent(nameElement);
	}
	String temp;
	//add type plus extra parameters
	if (speciesInfo instanceof CompoundInfo) {
		CompoundInfo info = (CompoundInfo)speciesInfo;
		
		//add formula
		temp = info.getFormula();
		if (temp !=null) {
			speciesInfoElement.setAttribute(XMLTags.FormulaTag, mangle(temp));
		}
		
		//add casID
		temp = info.getCasID();
		if (temp != null) {
			speciesInfoElement.setAttribute(XMLTags.CasIDTag, mangle(temp));			
		}
		
		//add enzymes
		if (info.getEnzymes()!=null) {
			for (int i = 0; i < info.getEnzymes().length; i++){
				Element enzymeElement = new Element(XMLTags.EnzymeTag);
				EnzymeRef ref = info.getEnzymes()[i];
				//add ECNumber
				enzymeElement.setAttribute(XMLTags.ECNumberTag, mangle(ref.getEcNumber()));
				//add EnzymeType
				enzymeElement.setAttribute(XMLTags.TypeAttrTag, String.valueOf(ref.getEnzymeType()));
				//add the enzymeElement to the speciesInfoElement
				speciesInfoElement.addContent(enzymeElement);
			}
		}
		
		//add type
		speciesInfoElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.CompoundTypeTag);
	} else if (speciesInfo instanceof EnzymeInfo) {
		EnzymeInfo info = (EnzymeInfo)speciesInfo;
		
		//add reaction
		temp = info.getReaction();
		if (temp != null) {
			speciesInfoElement.setAttribute(XMLTags.ExpressionAttrTag, mangle(temp));
		}
		//add sysname
		temp = info.getSysname();
		if (temp != null) {
			speciesInfoElement.setAttribute(XMLTags.SysNameTag, mangle(temp));
		}
		//add argcasID
		temp = info.getCasID();
		if (temp != null) {
			speciesInfoElement.setAttribute(XMLTags.CasIDTag, mangle(temp));
		}
		//addtype
		speciesInfoElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.EnzymeTypeTag);
	} else if (speciesInfo instanceof ProteinInfo) {
		ProteinInfo info = (ProteinInfo)speciesInfo;
			
		//add Organism
		temp = info.getOrganism();
		if (temp != null) {
			speciesInfoElement.setAttribute(XMLTags.OrganismTag, mangle(temp));
		}
		//add accession
		temp = info.getAccession();
		if (temp != null) {
			speciesInfoElement.setAttribute(XMLTags.AccessionTag, mangle(temp));	
		}	
		//add KeyWords
		temp = info.getKeyWords();
		if (temp != null) {
			speciesInfoElement.setAttribute(XMLTags.KeywordsTag, mangle(temp));
		}
		//add description
		temp = info.getDescription();
		if (temp != null) {
			speciesInfoElement.setAttribute(XMLTags.DescriptionTag, mangle(temp));
		}	
		//add type
		speciesInfoElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.ProteinTypeTag);
	} else {
		throw new XmlParseException("FormalSpeciesInfo type "+speciesInfo.getClass().getName()+", not supported yet!");
	}

	return speciesInfoElement;
}


/**
 * This method returns a XML representation of a AnalyticSubVolume object.
 * Creation date: (3/1/2001 3:50:41 PM)
 * @return Element
 * @param param cbit.vcell.geometry.AnalyticSubVolume
 */
private Element getXML(AnalyticSubVolume param) {
	Element analytic = new Element(XMLTags.SubVolumeTag);

	//Add Attributes
	analytic.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	analytic.setAttribute(XMLTags.HandleAttrTag, String.valueOf(param.getHandle()));
	analytic.setAttribute(XMLTags.TypeAttrTag, XMLTags.AnalyticBasedTypeTag);

	//Create Analytic Expression subelement
	Element expression = new Element(XMLTags.AnalyticExpressionTag);
	//Add expression Content
	expression.addContent(mangleExpression(param.getExpression()));
	analytic.addContent(expression);

	//If keyFlag is on print the Keyvalue
	if (param.getKey() !=null && this.printKeysFlag) {
		analytic.setAttribute(XMLTags.KeyValueAttrTag, param.getKey().toString());
	}
		
	return analytic;
}

public Element getXML(CSGObject param) {
	Element csgObjectElement = new Element(XMLTags.SubVolumeTag);

	//Add Attributes
	csgObjectElement.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	csgObjectElement.setAttribute(XMLTags.HandleAttrTag, String.valueOf(param.getHandle()));
	csgObjectElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.CSGBasedTypeTag);

	//Create CSGNode subelement
	Element csgNodeRoot = getXML (param.getRoot());
	csgObjectElement.addContent(csgNodeRoot);

	//If keyFlag is on print the Keyvalue
	if (param.getKey() !=null && this.printKeysFlag) {
		csgObjectElement.setAttribute(XMLTags.KeyValueAttrTag, param.getKey().toString());
	}
		
	return csgObjectElement;
}


private Element getXML(CSGNode param) {
	Element csgNodeElement = null;
	if (param instanceof CSGPrimitive) {
		csgNodeElement = getXML((CSGPrimitive) param);
	} else if (param instanceof CSGPseudoPrimitive) {
		csgNodeElement = getXML((CSGPseudoPrimitive) param);
	}  else if (param instanceof CSGSetOperator) {
		csgNodeElement = getXML((CSGSetOperator) param);
	}  else if (param instanceof CSGTransformation) {
		csgNodeElement = getXML((CSGTransformation) param);
	}
	return csgNodeElement; 
}

private Element getXML(CSGPrimitive param) {
	Element csgPrimitiveElement = new Element(XMLTags.CSGPrimitiveTag);
	csgPrimitiveElement.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	csgPrimitiveElement.setAttribute(XMLTags.CSGPrimitiveTypeTag, mangle(param.getType().name()));
	return csgPrimitiveElement; 
}

private Element getXML(CSGPseudoPrimitive param) {
	//	Element csgPseudoPrimitiveElement = new Element(XMLTags.CSGPseudoPrimitiveTag);
	//	csgPseudoPrimitiveElement.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	//	csgPseudoPrimitiveElement.setAttribute(XMLTags.CSGObjectRefTag, mangle(param.getCsgObjectName()));
	//	// csgObject in CSGPseudoPrimitive
	//	Element csgObjectElement = getXML(param.getCsgObject());
	//	csgPseudoPrimitiveElement.addContent(csgObjectElement);
	//	return csgPseudoPrimitiveElement;
	
	throw new RuntimeException("Not implemented yet.");
}

private Element getXML(CSGSetOperator param) {
	Element csgSetOperatorElement = new Element(XMLTags.CSGSetOperatorTag);
	
	csgSetOperatorElement.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	csgSetOperatorElement.setAttribute(XMLTags.CSGSetOperatorTypeTag, mangle(param.getOpType().name()));
	
	ArrayList<CSGNode> setOpChildren = param.getChildren();
	Element csgNodeElement = null;
	for (CSGNode setOpNode : setOpChildren) {
		csgNodeElement = getXML(setOpNode);
		csgSetOperatorElement.addContent(csgNodeElement);
	}
	
	return csgSetOperatorElement; 
}

private Element getXML(CSGTransformation param) {
	Element csgTransformationElement = null;
	
	// defer it to subclasses to get attributes
	if (param instanceof CSGHomogeneousTransformation) {
		csgTransformationElement = getXML((CSGHomogeneousTransformation) param);
	} else if (param instanceof CSGRotation) {
		csgTransformationElement = getXML((CSGRotation) param);
	}  else if (param instanceof CSGScale) {
		csgTransformationElement = getXML((CSGScale) param);
	}  else if (param instanceof CSGTranslation) {
		csgTransformationElement = getXML((CSGTranslation) param);
	}
	
	// add child CSGNode on which the transformation is applied
	Element csgNodeElement = getXML(param.getChild());
	csgTransformationElement.addContent(csgNodeElement);

	return csgTransformationElement; 
}


private Element getXML(CSGHomogeneousTransformation param) {
//		Element csgHomoTransElement = new Element(XMLTags.CSGHomogeneousTransformationTag);
//		csgHomoTransElement.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
//		return csgHomoTransElement;
	
	throw new RuntimeException("Not implemented yet.");
}

private Element getXML(CSGRotation param) {
	Element csgRotationElement = new Element(XMLTags.CSGRotationTag);
	csgRotationElement.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	csgRotationElement.setAttribute(XMLTags.CSGRotationXTag, String.valueOf(param.getAxis().getX()));
	csgRotationElement.setAttribute(XMLTags.CSGRotationYTag, String.valueOf(param.getAxis().getY()));
	csgRotationElement.setAttribute(XMLTags.CSGRotationZTag, String.valueOf(param.getAxis().getZ()));
	csgRotationElement.setAttribute(XMLTags.CSGRotationAngleInRadiansTag, String.valueOf(param.getRotationRadians()));
	return csgRotationElement; 
}

private Element getXML(CSGScale param) {
	Element csgScaleElement = new Element(XMLTags.CSGScaleTag);
	csgScaleElement.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	csgScaleElement.setAttribute(XMLTags.CSGScaleXTag, String.valueOf(param.getScale().getX()));
	csgScaleElement.setAttribute(XMLTags.CSGScaleYTag, String.valueOf(param.getScale().getY()));
	csgScaleElement.setAttribute(XMLTags.CSGScaleZTag, String.valueOf(param.getScale().getZ()));
	return csgScaleElement; 
}

private Element getXML(CSGTranslation param) {
	Element csgTranslateElement = new Element(XMLTags.CSGTranslationTag);
	csgTranslateElement.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	csgTranslateElement.setAttribute(XMLTags.CSGTranslationXTag, String.valueOf(param.getTranslation().getX()));
	csgTranslateElement.setAttribute(XMLTags.CSGTranslationYTag, String.valueOf(param.getTranslation().getY()));
	csgTranslateElement.setAttribute(XMLTags.CSGTranslationZTag, String.valueOf(param.getTranslation().getZ()));
	return csgTranslateElement; 
}



/**
 * This method returns a XML representation of a CompartmentSubVolume.
 * Creation date: (3/1/2001 4:01:59 PM)
 * @return Element
 * @param param cbit.vcell.geometry.CompartmentSubVolume
 */
private Element getXML(CompartmentSubVolume param) {
	Element subvolume = new Element(XMLTags.SubVolumeTag);
	//Add Atributes
	subvolume.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	subvolume.setAttribute(XMLTags.HandleAttrTag, String.valueOf(param.getHandle()));
	subvolume.setAttribute(XMLTags.TypeAttrTag, XMLTags.CompartmentBasedTypeTag);

	//If keyFlag is on print the Keyvalue
	if (param.getKey() !=null && this.printKeysFlag) {
		subvolume.setAttribute(XMLTags.KeyValueAttrTag, param.getKey().toString());
	}
	
	return subvolume;
}


/**
 * This method retruns a XML ELement from a ControlPointCurve object.
 * Creation date: (5/22/2001 4:11:37 PM)
 * @return Element
 * @param param cbit.vcell.geometry.ControlPointCurve
 */
private Element getXML(ControlPointCurve param) {
	Element curve = new Element(XMLTags.CurveTag);

	//Add attributes
	String type = null;
	if (param instanceof Spline) {
		type = XMLTags.SplineTypeTag;
	} else if (param instanceof Line || param instanceof SampledCurve) {
		type = XMLTags.PolyLineTypeTag;
	}
	curve.setAttribute(XMLTags.TypeAttrTag, type);
	curve.setAttribute(XMLTags.ClosedAttrTag, String.valueOf(param.isClosed()));
	
	//Add coordinates
	Vector<Coordinate> vector = param.getControlPointsVector();
	Iterator<Coordinate> iterator = vector.iterator();
	while (iterator.hasNext()) {
		curve.addContent(getXML(iterator.next()) );
	}
	
	return curve;
}


/**
 * This method returns a XML Element from a Coordinate object.
 * Creation date: (5/22/2001 4:43:29 PM)
 * @return Element
 * @param param cbit.vcell.geometry.Coordinate
 */
public Element getXML(Coordinate param) {
	Element coord = new Element(XMLTags.CoordinateTag);

	//X
	coord.setAttribute(XMLTags.XAttrTag, String.valueOf(param.getX()));
	//Y
	coord.setAttribute(XMLTags.YAttrTag, String.valueOf(param.getY()));
	//Z
	coord.setAttribute(XMLTags.ZAttrTag, String.valueOf(param.getZ()));
	
	return coord;
}


/**
 * This method returns a XML Element from a Filament object.
 * Creation date: (5/22/2001 4:03:13 PM)
 * @return Element
 * @param param cbit.vcell.geometry.Filament
 */
private Element getXML(Filament param) {
	//--- create Element
	Element filament = new Element(XMLTags.FilamentTag);
	//Add atributes
	filament.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()) );

	//add curves
	Curve[] array = param.getCurves();
	for (int i = 0; i < array.length ; i++){
		filament.addContent( getXML((ControlPointCurve)array[i]) );
	}
	
	return filament;
}


/**
 * This method returns a XML representation of a Geometry object.
 * Creation date: (2/28/2001 5:51:36 PM)
 * @return Element
 * @param param cbit.vcell.geometry.Geometry
 */
public Element getXML(Geometry param) throws XmlParseException{
	Element geometry = new Element(XMLTags.GeometryTag);

	// Add attributes
	String name = param.getName();
	geometry.setAttribute(XMLTags.NameAttrTag, mangle(name));
	geometry.setAttribute(XMLTags.DimensionAttrTag, String.valueOf(param.getDimension()));
	//geometry.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(param.getDescription()));
	//add Annotation
	if (param.getDescription()!=null && param.getDescription().length()>0) {
		Element annotationElem = new Element(XMLTags.AnnotationTag);
		annotationElem.setText(mangle(param.getDescription()));
		geometry.addContent(annotationElem);
	}
	
	// add sub-elements
	//Create extent subelement
	geometry.addContent(getXML(param.getExtent()));
	//Add Origin subelement
	Element origin = new Element(XMLTags.OriginTag);
	//Add Origin attributes
	origin.setAttribute(XMLTags.XAttrTag, String.valueOf(param.getOrigin().getX()));
	origin.setAttribute(XMLTags.YAttrTag, String.valueOf(param.getOrigin().getY()));
	origin.setAttribute(XMLTags.ZAttrTag, String.valueOf(param.getOrigin().getZ()));
	geometry.addContent(origin);
	//Add Image	subelement if there is.
	if (param.getGeometrySpec().getImage()!=null){
		try {
			geometry.addContent( getXML(param.getGeometrySpec().getImage()) );
		} catch (XmlParseException e) {
			e.printStackTrace();
			throw new XmlParseException("A problem occurred when trying to get the Image for the geometry " + name, e);
		}
	}
	//Add subvolumes elements 
	for (int i=0;i<param.getGeometrySpec().getSubVolumes().length;i++){
		geometry.addContent( getXML(param.getGeometrySpec().getSubVolumes(i)) );
	}
	if(param.getDimension() > 0 &&
			param.getGeometrySurfaceDescription() != null &&
			param.getGeometrySurfaceDescription().getSurfaceClasses() != null){
		//Add SurfaceClass elements 
		for (int i=0;i<param.getGeometrySurfaceDescription().getSurfaceClasses().length;i++){
			geometry.addContent( getXML(param.getGeometrySurfaceDescription().getSurfaceClasses()[i]) );
		}
	}
	//Add Filaments
	if (param.getDimension() > 0)
	{
		Filament[] filarray = param.getGeometrySpec().getFilamentGroup().getFilaments();
		for (int i = 0; i < filarray.length; i++){
			geometry.addContent( getXML(filarray[i]) );
		}
	}
	//Add Surface descriptions, if any
	GeometrySurfaceDescription gsd = param.getGeometrySurfaceDescription();
	if (gsd != null) {
		geometry.addContent(getXML(gsd));		
	}
		
	//Add Metadata(version) if there is one
	if ( param.getVersion()!=null ) {
		geometry.addContent( getXML(param.getVersion(), param) );
	}
	
	return geometry;
}


/**
 * This method returns a XML representation of a ImageSubVolume object.
 * Creation date: (3/1/2001 4:06:20 PM)
 * @return Element
 * @param param cbit.vcell.geometry.ImageSubVolume
 */
private Element getXML(ImageSubVolume param) {
	Element subvolume = new Element(XMLTags.SubVolumeTag);

	//add atributes
	subvolume.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	subvolume.setAttribute(XMLTags.HandleAttrTag, String.valueOf(param.getHandle()));
	subvolume.setAttribute(XMLTags.TypeAttrTag, XMLTags.ImageBasedTypeTag);
	subvolume.setAttribute( XMLTags.ImagePixelValueTag, String.valueOf(param.getPixelClass().getPixel()));

	//If keyFlag is on print the Keyvalue
	if (param.getKey() !=null && this.printKeysFlag) {
		subvolume.setAttribute(XMLTags.KeyValueAttrTag, param.getKey().toString());
	}
	
	return subvolume;
}

private Element getXML(SurfaceClass param) {
	Element surfaceClassElement = new Element(XMLTags.SurfaceClassTag);

	//add atributes
	surfaceClassElement.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	SubVolume[] subvolArr = param.getAdjacentSubvolumes().toArray(new SubVolume[0]);
	Arrays.sort(subvolArr, new Comparator<SubVolume>() {

		public int compare(SubVolume o1, SubVolume o2) {
			return o1.getName().compareTo(o2.getName());
		}
	});
	
	if(subvolArr.length>0){
		surfaceClassElement.setAttribute(XMLTags.SubVolume1RefAttrTag, subvolArr[0].getName());
	}
	if(subvolArr.length>1){
		surfaceClassElement.setAttribute(XMLTags.SubVolume2RefAttrTag, subvolArr[1].getName());
	}

	//If keyFlag is on print the Keyvalue
	if (param.getKey() !=null && this.printKeysFlag) {
		surfaceClassElement.setAttribute(XMLTags.KeyValueAttrTag, param.getKey().toString());
	}
	
	return surfaceClassElement;
}


/**
 * This method returns a XML representation of a Subvolume object.
 * Creation date: (3/1/2001 3:27:08 PM)
 * @return Element
 * @param param cbit.vcell.geometry.SubVolume
 */
private Element getXML(SubVolume param) {
	if (param instanceof AnalyticSubVolume) {
		return getXML((AnalyticSubVolume)param);
	} else if (param instanceof CompartmentSubVolume) {
		return getXML( (CompartmentSubVolume)param );
	} else if (param instanceof ImageSubVolume) {
		return getXML( (ImageSubVolume)param );
	}  else if (param instanceof CSGObject) {
		return getXML( (CSGObject)param );
	}

	return null;
}


	private Element getXML(GeometrySurfaceDescription param) throws XmlParseException {

		Element gsd = new Element(XMLTags.SurfaceDescriptionTag);
		//add attributes
		ISize isize = param.getVolumeSampleSize();
		if (isize == null) {
			throw new XmlParseException("Unable to retrieve dimensions for surface descriptions for Geometry: " +
			                        param.getGeometry().getName());
		}
		gsd.setAttribute(XMLTags.NumSamplesXAttrTag, String.valueOf(isize.getX()));
		gsd.setAttribute(XMLTags.NumSamplesYAttrTag, String.valueOf(isize.getY()));
		gsd.setAttribute(XMLTags.NumSamplesZAttrTag, String.valueOf(isize.getZ()));
		Double coFrequency = param.getFilterCutoffFrequency();
		if (coFrequency == null) {
			throw new XmlParseException("Unable to retrieve cutoff frequency for surface descriptions for Geometry: " +
			                        param.getGeometry().getName());
		}
		double cutoffFrequency = coFrequency.doubleValue();
		gsd.setAttribute(XMLTags.CutoffFrequencyAttrTag, String.valueOf(cutoffFrequency));

		//add subelements
		GeometricRegion geomRegions [] = param.getGeometricRegions();
		if (geomRegions != null) {
			for (int i = 0; i < geomRegions.length; i++) {
				if (geomRegions[i] instanceof SurfaceGeometricRegion) {
					SurfaceGeometricRegion sgr = (SurfaceGeometricRegion)geomRegions[i];
					Element membraneRegion = new Element(XMLTags.MembraneRegionTag);
					membraneRegion.setAttribute(XMLTags.NameAttrTag, sgr.getName());
					GeometricRegion adjacents [] = sgr.getAdjacentGeometricRegions();
					if (adjacents == null || adjacents.length != 2) {
						throw new XmlParseException("Wrong number of adjacent regions for surface descriptions for location: " +
									          sgr.getName() + " in Geometry: " + param.getGeometry().getName());	
					}
					membraneRegion.setAttribute(XMLTags.VolumeRegion_1AttrTag, adjacents[0].getName());
					membraneRegion.setAttribute(XMLTags.VolumeRegion_2AttrTag, adjacents[1].getName());
					double size = sgr.getSize();
					if (size != -1) {
						membraneRegion.setAttribute(XMLTags.SizeAttrTag, String.valueOf(size));
						VCUnitDefinition unit = sgr.getSizeUnit();
						if (unit != null) {
							membraneRegion.setAttribute(XMLTags.VCUnitDefinitionAttrTag, unit.getSymbol());
						}
					}
					gsd.addContent(membraneRegion);
				} else if (geomRegions[i] instanceof VolumeGeometricRegion) {
					VolumeGeometricRegion vgr = (VolumeGeometricRegion)geomRegions[i];
					Element volumeRegion = new Element(XMLTags.VolumeRegionTag);
					volumeRegion.setAttribute(XMLTags.NameAttrTag, vgr.getName());
					volumeRegion.setAttribute(XMLTags.RegionIDAttrTag, String.valueOf(vgr.getRegionID()));
					volumeRegion.setAttribute(XMLTags.SubVolumeAttrTag, vgr.getSubVolume().getName());
					double size = vgr.getSize();
					if (size != -1) {
						volumeRegion.setAttribute(XMLTags.SizeAttrTag, String.valueOf(size));
						VCUnitDefinition unit = vgr.getSizeUnit();
						if (unit != null) {
							volumeRegion.setAttribute(XMLTags.VCUnitDefinitionAttrTag, unit.getSymbol());
						}
					}
					gsd.addContent(volumeRegion);
				}
			}
		}

		return gsd;
	}


/**
 * This method returns a XML representation of an Electrode.
 * Creation date: (6/6/2002 2:32:56 PM)
 * @return Element
 * @param param cbit.vcell.mapping.Electrode
 */
private Element getXML(ElectricalStimulus param) {
	String electricalStimulusType = null;

	if (param instanceof VoltageClampStimulus) {
		//process a VoltageClampStimulus object
		electricalStimulusType = XMLTags.VoltageClampTag;
	} else if (param instanceof CurrentDensityClampStimulus) {
		//Process a CurrentClampStimulus
		electricalStimulusType = XMLTags.CurrentDensityClampTag;
	}else if (param instanceof TotalCurrentClampStimulus) {
		//Process a CurrentClampStimulus
		electricalStimulusType = XMLTags.TotalCurrentClampTag;
	}

	Element electricalStimulus = new Element(XMLTags.ClampTag);
	
	// Need to add electrode ??
	Element electrode = getXML(param.getElectrode());
	electricalStimulus.addContent(electrode);
	
	//Add attributes
	electricalStimulus.setAttribute(XMLTags.TypeAttrTag, electricalStimulusType);
	
	//Add Kinetics Parameters
	LocalParameter parameters[] = param.getLocalParameters();
	for (int i=0;i<parameters.length;i++){
		LocalParameter parm = parameters[i];
		Element tempparameter = new Element(XMLTags.ParameterTag);
		//Get parameter attributes
		tempparameter.setAttribute(XMLTags.NameAttrTag, mangle(parm.getName()));
		tempparameter.setAttribute(XMLTags.ParamRoleAttrTag, ((ElectricalStimulusParameterType)parm.getRole()).roleDescription);
		VCUnitDefinition unit = parm.getUnitDefinition();
		if (unit != null) {
			tempparameter.setAttribute(XMLTags.VCUnitDefinitionAttrTag, unit.getSymbol());
		}
		tempparameter.addContent( mangleExpression(parm.getExpression()) );
		//Add the parameter to the general electricalstimulus object
		electricalStimulus.addContent(tempparameter);
	}

	return electricalStimulus;
}


/**
 * This method returns a XML representation of an Electrode.
 * Creation date: (6/6/2002 2:32:56 PM)
 * @return Element
 * @param param cbit.vcell.mapping.Electrode
 */
private Element getXML(Electrode param) {
	Element electrodeElem = new Element(XMLTags.ElectrodeTag);
	//add feature name
	electrodeElem.setAttribute( XMLTags.FeatureAttrTag, mangle(param.getFeature().getName()) );

	//add coordinate
	electrodeElem.addContent(getXML(param.getPosition()));
	
	return electrodeElem;
}


/**
 * This method returns a XML representation of a featureMapping object.
 * Creation date: (3/1/2001 8:16:57 PM)
 * @return Element
 * @param param cbit.vcell.mapping.FeatureMapping
 */
private Element getXML(FeatureMapping param) {
	//Allow null subvolumes
	//if (param.getSubVolume()==null) {	//5/92001
		//return null;
	//}
	
	Element feature = new Element(XMLTags.FeatureMappingTag);
	
	//Add atributes
	feature.setAttribute(XMLTags.FeatureAttrTag, mangle(param.getFeature().getName()));
	GeometryClass geometryClass = param.getGeometryClass();
	if (geometryClass != null) {
		feature.setAttribute(XMLTags.GeometryClassAttrTag, Xmlproducer.mangle(geometryClass.getName()));
		if (geometryClass instanceof SubVolume){
			feature.setAttribute(XMLTags.SubVolumeAttrTag, Xmlproducer.mangle(geometryClass.getName()));
		}
	}
	//Add size
	if(param.getSizeParameter().getExpression() != null)
		feature.setAttribute(XMLTags.SizeTag, mangleExpression(param.getSizeParameter().getExpression()));
	
	// Add volume/unit_Area and volume/unit_vol if they exist
	if(param.getVolumePerUnitAreaParameter().getExpression() != null) {
		feature.setAttribute(XMLTags.VolumePerUnitAreaTag, mangleExpression(param.getVolumePerUnitAreaParameter().getExpression()));
	}
	if(param.getVolumePerUnitVolumeParameter().getExpression() != null) {
		feature.setAttribute(XMLTags.VolumePerUnitVolumeTag, mangleExpression(param.getVolumePerUnitVolumeParameter().getExpression()));
	}
	
	// write BoundariesyConditions
	Element boundariestypes = new Element(XMLTags.BoundariesTypesTag);
	
	//Xm
	boundariestypes.setAttribute(XMLTags.BoundaryAttrValueXm, param.getBoundaryConditionTypeXm().boundaryTypeStringValue());
	//Xp
	boundariestypes.setAttribute(XMLTags.BoundaryAttrValueXp, param.getBoundaryConditionTypeXp().boundaryTypeStringValue());
	//Ym
	boundariestypes.setAttribute(XMLTags.BoundaryAttrValueYm, param.getBoundaryConditionTypeYm().boundaryTypeStringValue());
	//Yp
	boundariestypes.setAttribute(XMLTags.BoundaryAttrValueYp, param.getBoundaryConditionTypeYp().boundaryTypeStringValue());
	//Zm
	boundariestypes.setAttribute(XMLTags.BoundaryAttrValueZm, param.getBoundaryConditionTypeZm().boundaryTypeStringValue());
	//Zp
	boundariestypes.setAttribute(XMLTags.BoundaryAttrValueZp, param.getBoundaryConditionTypeZp().boundaryTypeStringValue());
	
	feature.addContent( boundariestypes ); //add boundaries to the feature

	return feature;
}


/**
 * This method returns a XML representation of a GeometryContext object.
 * Creation date: (3/1/2001 6:50:24 PM)
 * @return Element
 * @param param cbit.vcell.mapping.GeometryContext
 */
private Element getXML(GeometryContext param) {
	Element geometrycontent = new Element(XMLTags.GeometryContextTag);

	// write Structure Mappings, separate membrane from feature mappings.
	StructureMapping[] array = param.getStructureMappings();
	ArrayList<Element> memMap = new ArrayList<Element>();
	for (int i=0; i<array.length ; i++) {
		StructureMapping sm = (StructureMapping)array[i];
		//check for FeatureMappings
		//allow 'null' subvolumes for FeatureMapping. 
		//if (sm.getSubVolume()== null)	
			//continue;
		if (sm instanceof FeatureMapping){
			geometrycontent.addContent(getXML((FeatureMapping)sm));
		} else if (sm instanceof MembraneMapping){
			//try MembraneMappings
			memMap.add(getXML((MembraneMapping)sm));
		}
	}
	for (int i = 0; i < memMap.size(); i++)
		geometrycontent.addContent((Element)memMap.get(i));

	return geometrycontent;
}


/**
 * This method returns a XML representation of a MembraneMapping.
 * Creation date: (3/1/2001 8:48:14 PM)
 * @return Element
 * @param param cbit.vcell.mapping.MembraneMapping
 */
private Element getXML(MembraneMapping param) {
	//Allow 'null' subvolumes
	//if (param.getSubVolume() == null) {
		//return null;
	//}
	Element membrane = new Element(XMLTags.MembraneMappingTag);
	
	//Add atributes
	membrane.setAttribute(XMLTags.MembraneAttrTag, mangle(param.getMembrane().getName()));
	// write FluxCorrections
	
	//SurfaceToVolumeRatio if it exsits, amended Sept. 27th, 2007
	if(param.getSurfaceToVolumeParameter().getExpression() != null)
	{
		membrane.setAttribute(XMLTags.SurfaceToVolumeRatioTag, mangleExpression(param.getSurfaceToVolumeParameter().getExpression()) );
	}
	/*	Element surface = new Element(XMLTags.SurfaceToVolumeRatioTag);
	surface.addContent( this.mangleExpression(param.getSurfaceToVolumeExpression()) );
	membrane.addContent( surface );*/
	
	//VolumeFraction if it exsits, amended Sept. 27th, 2007
	if(param.getVolumeFractionParameter().getExpression() != null)
	{
		membrane.setAttribute(XMLTags.VolumeFractionTag, mangleExpression(param.getVolumeFractionParameter().getExpression()));
	}
	/*	Element volume = new Element(XMLTags.VolumeFractionTag);
	volume.addContent( this.mangleExpression(param.getVolumeFractionExpression()) );
	membrane.addContent( volume );*/
	//Add size
	if(param.getSizeParameter().getExpression() != null){
 		membrane.setAttribute(XMLTags.SizeTag, mangleExpression(param.getSizeParameter().getExpression()));
	}
	
	// Add area/unit_area and area/unit_vol if they exist
	if(param.getAreaPerUnitAreaParameter().getExpression() != null) {
		membrane.setAttribute(XMLTags.AreaPerUnitAreaTag, mangleExpression(param.getAreaPerUnitAreaParameter().getExpression()));
	}
	if(param.getAreaPerUnitVolumeParameter().getExpression() != null) {
		membrane.setAttribute(XMLTags.AreaPerUnitVolumeTag, mangleExpression(param.getAreaPerUnitVolumeParameter().getExpression()));
	}
	//Add the electrical properties
	membrane.setAttribute(XMLTags.CalculateVoltageTag, String.valueOf(param.getCalculateVoltage()));
	membrane.setAttribute(XMLTags.SpecificCapacitanceTag, mangleExpression(param.getSpecificCapacitanceParameter().getExpression()));
	membrane.setAttribute(XMLTags.InitialVoltageTag,mangleExpression(param.getInitialVoltageParameter().getExpression()));

	GeometryClass geometryClass = param.getGeometryClass();
	if (geometryClass != null) {
		membrane.setAttribute(XMLTags.GeometryClassAttrTag, mangle(geometryClass.getName()));
	}
	
	return membrane;
}


/**
 * This method returns a XML representation of a ReactionContext object.
 * Creation date: (3/1/2001 9:03:52 PM)
 * @return Element
 * @param param cbit.vcell.mapping.ReactionContext
 */
private Element getXML(ReactionContext param) {
	Element reactioncontext = new 	Element(XMLTags.ReactionContextTag);

	//Add SpeciesContextSpecs
	SpeciesContextSpec[] array = param.getSpeciesContextSpecs();
	for (int i =0; i<array.length ; i ++){
		reactioncontext.addContent( getXML(array[i]) );
	}
	//Add ReactionSpecs
	ReactionSpec[] reactionarray = param.getReactionSpecs();
	for (int i =0; i<reactionarray.length ; i ++){
		reactioncontext.addContent( getXML(reactionarray[i]) );
	}
	//Add ReactionRuleSpecs
	ReactionRuleSpec[] reactionRuleArray = param.getReactionRuleSpecs();
	if (reactionRuleArray.length>0){
		reactioncontext.addContent( getXML(reactionRuleArray) );
	}
	
	return reactioncontext;
}


/**
 * This method returns a XML representation of a ReactionSpec object.
 * Creation date: (4/26/2001 3:07:29 PM)
 * @return Element
 * @param param cbit.vcell.mapping.ReactionSpec
 */
private Element getXML(ReactionSpec param) {
	Element reactionSpec = new Element(XMLTags.ReactionSpecTag);

	//Add Atributes
	reactionSpec.setAttribute( XMLTags.ReactionStepRefAttrTag, mangle(param.getReactionStep().getName()) );
	reactionSpec.setAttribute( XMLTags.ReactionMappingAttrTag, mangle(param.getReactionMappingDescription()) );
	
	return reactionSpec;
}

//For rateRules in SimulationContext
public Element getXML(ReactionRuleSpec[] reactionRuleSpecs) {
	Element reactionRuleSpecsElement = new Element(XMLTags.ReactionRuleSpecsTag);
	for (ReactionRuleSpec reactionRuleSpec : reactionRuleSpecs){
		Element reactionRuleSpecElement = new Element(XMLTags.ReactionRuleSpecTag);
		reactionRuleSpecElement.setAttribute(XMLTags.ReactionRuleRefAttrTag, mangle(reactionRuleSpec.getReactionRule().getName()));
		reactionRuleSpecElement.setAttribute(XMLTags.ReactionRuleMappingAttrTag, mangle(reactionRuleSpec.getReactionRuleMapping().getDatabaseName()));

		reactionRuleSpecsElement.addContent(reactionRuleSpecElement);
	}

	return reactionRuleSpecsElement;
}

/**
 * This method returns a XML representation of a SimulationContext object.
 * Creation date: (2/22/2001 2:15:14 PM)
 * @return Element
 * @param param cbit.vcell.mapping.SimulationContext
 */
public Element getXML(SimulationContext param, BioModel bioModel) throws XmlParseException{
	SimulationContext.Application applicationType = param.getApplicationType();
	Element simulationcontext = new Element(XMLTags.SimulationSpecTag);

	//add attributes
	String name = mangle(param.getName());
	simulationcontext.setAttribute(XMLTags.NameAttrTag, name);
	//set isStoch, isUsingConcentration attributes
	if (applicationType == Application.NETWORK_STOCHASTIC)
	{
		simulationcontext.setAttribute(XMLTags.StochAttrTag, "true");
		setBooleanAttribute(simulationcontext, XMLTags.ConcentrationAttrTag, param.isUsingConcentration());
		// write out 'randomizeInitConditin' flag only if non-spatial stochastic simContext
		if(param.getGeometry().getDimension() == 0) {
			setBooleanAttribute(simulationcontext, XMLTags.RandomizeInitConditionTag,param.isRandomizeInitCondition());
		}
	}
	else
	{
		simulationcontext.setAttribute(XMLTags.StochAttrTag, "false");
		simulationcontext.setAttribute(XMLTags.ConcentrationAttrTag, "true");
	}
	final boolean ruleBased = param.getApplicationType() == SimulationContext.Application.RULE_BASED_STOCHASTIC; 
	setBooleanAttribute(simulationcontext,XMLTags.RuleBasedAttrTag, ruleBased); 
	setBooleanAttribute(simulationcontext,XMLTags.InsufficientIterationsTag,param.isInsufficientIterations());
	setBooleanAttribute(simulationcontext,XMLTags.InsufficientMaxMoleculesTag,param.isInsufficientMaxMolecules());
	NetworkConstraints constraints = param.getNetworkConstraints();
	if(constraints != null) {
		simulationcontext.addContent(getXML(constraints));
	}

	//simulationcontext.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(param.getDescription()));
	//add annotation
	if (param.getDescription()!=null && param.getDescription().length()>0) {
		Element annotationElem = new Element(XMLTags.AnnotationTag);
		annotationElem.setText(mangle(param.getDescription()));
		simulationcontext.addContent(annotationElem);
	}
	
	if ( param.getCharacteristicSize() != null) {
		simulationcontext.setAttribute(XMLTags.CharacteristicSizeTag,param.getCharacteristicSize().toString());
	}

	// write Geometry (or GeometryRef???)
	try {
		simulationcontext.addContent(getXML(param.getGeometryContext().getGeometry()));
	} catch (XmlParseException e) {
		e.printStackTrace();
		throw new XmlParseException("A problem occurred when trying to process the geometry for the simulationContext " + name, e);
	}
	// write GeometryContext (geometric mapping)
	simulationcontext.addContent(getXML(param.getGeometryContext()));
	// write ReactionContext (parameter/variable mapping)
	simulationcontext.addContent(getXML(param.getReactionContext()));

	//check if there is anything to write first for the electrical context
	if (param.getElectricalStimuli().length==1 || param.getGroundElectrode()!=null) {
		//create ElectricalContext
		Element electricalElement = new Element(XMLTags.ElectricalContextTag);

		//Write the electrical stimuli
		if (param.getElectricalStimuli().length==1) {
			//write clamp
			electricalElement.addContent(getXML(param.getElectricalStimuli(0)));

			//Element clampElem = new Element(XMLTags.ClampTag);
			//clampElem.addContent(getXML(param.getElectricalStimuli(0)));
			//if (param.getElectricalStimuli()[0] instanceof VoltageClampStimulus) {
				////this is a VOLTAGE clamp
				//clampElem.setAttribute(XMLTags.TypeAttrTag, XMLTags.VoltageClampTag);
				//clampElem.setAttribute( XMLTags.NameAttrTag, this.mangle((param.getElectricalStimuli()[0]).getName()) );
				//String tempExp = this.mangleExpression( ((VoltageClampStimulus)param.getElectricalStimuli()[0]).getVoltageParameter().getExpression() );
				//clampElem.setAttribute(XMLTags.ExpressionAttrTag, tempExp);
				////add probe-electrode
				//clampElem.addContent(getXML(param.getElectricalStimuli()[0].getElectrode()));
			//} else {
				////this is a CURRENT clamp
				//clampElem.setAttribute(XMLTags.TypeAttrTag, XMLTags.CurrentClampTag);
				//clampElem.setAttribute( XMLTags.NameAttrTag, this.mangle((param.getElectricalStimuli()[0]).getName()) );
				//String tempExp = this.mangleExpression( ((CurrentClampStimulus)param.getElectricalStimuli()[0]).getCurrentParameter().getExpression() );
				//clampElem.setAttribute(XMLTags.ExpressionAttrTag, tempExp);
				////add probe-electrode 
				//clampElem.addContent(getXML(param.getElectricalStimuli()[0].getElectrode()));			
			//}
			// electricalElement.addContent(clampElem);

			
			//
		} else if (param.getElectricalStimuli().length>1) {
			// **ONLY ONE ELECTRODE IS SUPPORTED RIGHT NOW!
			throw new IllegalArgumentException("More than one electrode is not supported!");
		}

		//Process the Ground electrode
		if (param.getGroundElectrode()!=null) {
			//write the ground electrode
			electricalElement.addContent(getXML(param.getGroundElectrode()));
		}
		
		simulationcontext.addContent(electricalElement);		
	}
	
	//Add Mathdescription (if present)
	if (param.getMathDescription() != null) {
		simulationcontext.addContent(getXML(param.getMathDescription()));
	}
	
	if (param.getOutputFunctionContext() != null) {
		ArrayList<AnnotatedFunction> outputFunctions = param.getOutputFunctionContext().getOutputFunctionsList();
		if(outputFunctions != null && outputFunctions.size() > 0) {
			// get output functions
			simulationcontext.addContent(getXML(outputFunctions));
		}
	}
	
	//Add Simulations to the simulationSpec
	if (bioModel!=null){
		cbit.vcell.solver.Simulation simulations[] = bioModel.getSimulations(param);
		for (int i=0;simulations!=null && i<simulations.length;i++){
			simulationcontext.addContent(getXML(simulations[i]));
		}
	}
	//Add AnalysisTasks
	if (param.getAnalysisTasks()!=null && param.getAnalysisTasks().length>0){
		//if length of analysisTasks is 1, it might be the default added analysis task
		//if the task is empty (no parameters set up, no reference data), we shall not save it
		if(param.getAnalysisTasks().length == 1)
		{
			AnalysisTask task = param.getAnalysisTasks()[0];
			if(task instanceof ParameterEstimationTask)
			{
				if(!((ParameterEstimationTask)task).isEmpty())
				{
					simulationcontext.addContent( getXML(param.getAnalysisTasks()) );
				}
			}
		}
		else // have more than one analysis task 
		{
			simulationcontext.addContent( getXML(param.getAnalysisTasks()) );
		}
	}
	
	// Add (Bio)events
	BioEvent[] bioEvents = param.getBioEvents();
	if (bioEvents!=null && bioEvents.length>0){
		simulationcontext.addContent( getXML(bioEvents) );
	}
	
	SimulationContextParameter[] appParams = param.getSimulationContextParameters();
	if (appParams!=null && appParams.length>0){
		simulationcontext.addContent( getXML(appParams));
	}
	
	SpatialObject[] spatialObjects = param.getSpatialObjects();
	if (spatialObjects!=null && spatialObjects.length>0){
		simulationcontext.addContent( getXML(spatialObjects));
	}
	
	SpatialProcess[] spatialProcesses = param.getSpatialProcesses();
	if (spatialProcesses!=null && spatialProcesses.length>0){
		simulationcontext.addContent( getXML(spatialProcesses));
	}
	
	// Add rate rules
	RateRule[] rateRules = param.getRateRules();
	if (param.getRateRules()!=null && rateRules.length>0){
		simulationcontext.addContent( getXML(rateRules) );
	}

	// Add Datacontext
	if (param.getDataContext()!=null && param.getDataContext().getDataSymbols().length>0){
		simulationcontext.addContent( getXML(param.getDataContext(), param.getModel().getUnitSystem()) );
	}
	
	//Add Metadata (if any)
	if ( param.getVersion() != null) {
		simulationcontext.addContent( getXML(param.getVersion(), param) );
	}
	
	// Add microscope measurements
	simulationcontext.addContent(getXML(param.getMicroscopeMeasurement()));

	return simulationcontext;
}

/**
 * set atttribute to "true" or "false" 
 * @param elem non null
 * @param tag name of attribute
 * @param exp to evaluate
 */
private void setBooleanAttribute(Element elem, String tag, boolean exp) {
	elem.setAttribute(tag, Boolean.toString(exp) );
}


public Element getXML(MicroscopeMeasurement microscopeMeasurement) {
	Element element = new Element(XMLTags.MicroscopeMeasurement);
	element.setAttribute(XMLTags.NameAttrTag, microscopeMeasurement.getName());
	
	ArrayList<SpeciesContext> speciesContextList = microscopeMeasurement.getFluorescentSpecies();
	for (SpeciesContext sc : speciesContextList) {
		Element e = new Element(XMLTags.FluorescenceSpecies);
		e.setAttribute(XMLTags.NameAttrTag, sc.getName());
		element.addContent(e);
	}
	ConvolutionKernel ck = microscopeMeasurement.getConvolutionKernel();
	Element kernelElement = new Element(XMLTags.ConvolutionKernel);
	if (ck instanceof ProjectionZKernel) {
		kernelElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.ConvolutionKernel_Type_ProjectionZKernel);
	} else if (ck instanceof GaussianConvolutionKernel){
		kernelElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.ConvolutionKernel_Type_GaussianConvolutionKernel);
		
		GaussianConvolutionKernel gck = (GaussianConvolutionKernel)ck;
		Element e = new Element(XMLTags.KernelGaussianSigmaXY);
		e.addContent(mangleExpression(gck.getSigmaXY_um()));
		kernelElement.addContent(e);
		
		e = new Element(XMLTags.KernelGaussianSigmaZ);
		e.addContent(mangleExpression(gck.getSigmaZ_um()));
		kernelElement.addContent(e);
	}
	element.addContent(kernelElement);
	return element;
}

private Element getXML(DataContext dataContext, ModelUnitSystem modelUnitSystem) {
	Element dataContextElement = new Element(XMLTags.DataContextTag);
	Element dataSymbolElement = null;
	
	for(int i=0; i<dataContext.getDataSymbols().length; i++) {
		DataSymbol ds = dataContext.getDataSymbols()[i];
		if (ds instanceof FieldDataSymbol){
			dataSymbolElement = getXML((FieldDataSymbol)ds, modelUnitSystem);
		}else{
			throw new RuntimeException("XML persistence not supported for analysis type "+ds.getClass().getName());
		}
		dataContextElement.addContent(dataSymbolElement);
	}
	return dataContextElement;
}


private Element getXML(FieldDataSymbol fds, ModelUnitSystem modelUnitSystem) {
	Element fieldDataSymbolElement = new Element(XMLTags.FieldDataSymbolTag);

	fieldDataSymbolElement.setAttribute(XMLTags.DataSymbolNameTag, fds.getName());
	fieldDataSymbolElement.setAttribute(XMLTags.DataSymbolTypeTag, fds.getDataSymbolType().getDatabaseName());
	// DataContext is runtime only
	fieldDataSymbolElement.setAttribute(XMLTags.VCUnitDefinitionAttrTag, fds.getUnitDefinition().getSymbol());

	Element dataSetIDElement = new Element(XMLTags.ExternalDataIdentifierTag);
	ExternalDataIdentifier edi = fds.getExternalDataIdentifier();
	dataSetIDElement.setAttribute(XMLTags.NameAttrTag, edi.getName());
	dataSetIDElement.setAttribute(XMLTags.KeyValueAttrTag, edi.getKey().toString());
	dataSetIDElement.setAttribute(MicroscopyXMLTags.OwnerNameAttrTag, edi.getOwner().getName());
	dataSetIDElement.setAttribute(XMLTags.OwnerKeyAttrTag, edi.getOwner().getID().toString());
	fieldDataSymbolElement.addContent(dataSetIDElement);

	fieldDataSymbolElement.setAttribute(XMLTags.FieldItemNameTag, fds.getFieldDataVarName());
	fieldDataSymbolElement.setAttribute(XMLTags.FieldItemTypeTag, fds.getFieldDataVarType());
	fieldDataSymbolElement.setAttribute(XMLTags.FieldItemTimeTag, Double.toString(fds.getFieldDataVarTime()));

	return fieldDataSymbolElement;
}


/**
 * This method returns a XML representation of a SpeciesContextSpec object.
 * Creation date: (3/1/2001 9:13:56 PM)
 * @return Element
 * @param param cbit.vcell.mapping.SpeciesContextSpec
 */
private Element getXML(SpeciesContextSpec param) {
	Element speciesContextSpecElement = new Element(XMLTags.SpeciesContextSpecTag);

	//Add Attributes
	speciesContextSpecElement.setAttribute(XMLTags.SpeciesContextRefAttrTag, mangle(param.getSpeciesContext().getName()));
	speciesContextSpecElement.setAttribute(XMLTags.ForceConstantAttrTag, String.valueOf(param.isConstant()));
	//speciesContextSpecElement.setAttribute(XMLTags.EnableDiffusionAttrTag, String.valueOf(param.isEnableDiffusing()));
	if (param.isWellMixed()!=null){
		speciesContextSpecElement.setAttribute(XMLTags.WellMixedAttrTag, String.valueOf(param.isWellMixed()));
	}
	if (param.isForceContinuous()!=null){
		speciesContextSpecElement.setAttribute(XMLTags.ForceContinuousAttrTag, String.valueOf(param.isForceContinuous()));
	}

	//Add initial
	Expression initCon = param.getInitialConcentrationParameter().getExpression();
	Expression initAmt = param.getInitialCountParameter().getExpression();
	if (initCon != null)
	{
		Element initial = new Element(XMLTags.InitialConcentrationTag);
		initial.addContent(mangleExpression(initCon));
		speciesContextSpecElement.addContent( initial );
	}
	else if(initAmt != null)
	{
		Element initial = new Element(XMLTags.InitialAmountTag);
		initial.addContent(mangleExpression(initAmt));
		speciesContextSpecElement.addContent( initial );
	}
	//Add diffusion
	cbit.vcell.parser.Expression diffRate = param.getDiffusionParameter().getExpression();
	if (diffRate!=null){
		Element diffusion = new Element(XMLTags.DiffusionTag);
		diffusion.addContent(mangleExpression(diffRate));
		speciesContextSpecElement.addContent(diffusion);
	}
	// write BoundaryConditions
	cbit.vcell.parser.Expression exp;
	Element boundaries = new Element(XMLTags.BoundariesTag);

	//XM
	exp = param.getBoundaryXmParameter().getExpression();
	if (exp!=null){
		boundaries.setAttribute(XMLTags.BoundaryAttrValueXm, mangleExpression(exp) );
	}
	//XP
	exp = param.getBoundaryXpParameter().getExpression();
	if (exp!=null){
		boundaries.setAttribute(XMLTags.BoundaryAttrValueXp, mangleExpression(exp) );
	}
	//YM
	exp = param.getBoundaryYmParameter().getExpression();
	if (exp!=null){
		boundaries.setAttribute(XMLTags.BoundaryAttrValueYm, mangleExpression(exp) );
	}
	//YP
	exp = param.getBoundaryYpParameter().getExpression();
	if (exp!=null){
		boundaries.setAttribute(XMLTags.BoundaryAttrValueYp, mangleExpression(exp) );
	}
	//ZM
	exp = param.getBoundaryZmParameter().getExpression();
	if (exp!=null){
		boundaries.setAttribute(XMLTags.BoundaryAttrValueZm, mangleExpression(exp) );
	}
	//ZP
	exp = param.getBoundaryZpParameter().getExpression();
	if (exp!=null){
		boundaries.setAttribute(XMLTags.BoundaryAttrValueZp, mangleExpression(exp) );
	}
	if (boundaries.getAttributes().size() >0) {
		speciesContextSpecElement.addContent( boundaries );
	}

	// Add Velocities Vx, Vy, Vz
	Element velocityElement = null;
	Expression velX = param.getVelocityXParameter().getExpression();
	if (velX != null) {
		velocityElement = new Element(XMLTags.VelocityTag);
		velocityElement.setAttribute(XMLTags.XAttrTag, mangleExpression(velX));
	}
	Expression velY = param.getVelocityYParameter().getExpression();
	if (velY != null) {
		if (velocityElement == null) {
			velocityElement = new Element(XMLTags.VelocityTag);
		}
		velocityElement.setAttribute(XMLTags.YAttrTag, mangleExpression(velY));
	}
	Expression velZ = param.getVelocityZParameter().getExpression();
	if (velZ != null) {
		if (velocityElement == null) {
			velocityElement = new Element(XMLTags.VelocityTag);
		}
		velocityElement.setAttribute(XMLTags.ZAttrTag, mangleExpression(velZ));
	}
	if (velocityElement != null) {
		speciesContextSpecElement.addContent(velocityElement);
	}
	
	return speciesContextSpecElement;
}


/**
 * Get a XML element for the action taken in stochasitc process.
 * Creation date: (7/21/2006 11:46:04 AM)
 * @return Element
 * @param param cbit.vcell.math.Action
 */
private Element getXML(Action param) 
{
	Element action = new Element(XMLTags.ActionTag);

	//Add atributes
	action.setAttribute(XMLTags.VarNameAttrTag, mangle(param.getVar().getName()));
	action.setAttribute(XMLTags.OperationAttrTag, mangle(param.getOperation()));
	if (param.getOperand() != null) {
		action.addContent( mangleExpression(param.getOperand()));
	}
	return action;
}


/**
 * This method returns a XML representation of a CompartmentSubDomain object.
 * Creation date: (3/2/2001 1:18:55 PM)
 * @return Element
 * @param param cbit.vcell.math.CompartmentSubDomain
 */
private Element getXML(CompartmentSubDomain param) throws XmlParseException{
	Element compartment = new Element(XMLTags.CompartmentSubDomainTag);

	compartment.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	//Add boundaryType subelements
	Element boundary;
	//Xm
	boundary = new Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueXm);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionXm().boundaryTypeStringValue());
	compartment.addContent(boundary);
	//Xp
	boundary = new Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueXp);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionXp().boundaryTypeStringValue());
	compartment.addContent(boundary);
	//Ym
	boundary = new Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueYm);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionYm().boundaryTypeStringValue());
	compartment.addContent(boundary);
	//Yp
	boundary = new Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueYp);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionYp().boundaryTypeStringValue());
	compartment.addContent(boundary);
	//Zm
	boundary = new Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueZm);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionZm().boundaryTypeStringValue());
	compartment.addContent(boundary);
	//Zp
	boundary = new Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueZp);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionZp().boundaryTypeStringValue());
	compartment.addContent(boundary);
	
	// add BoundaryConditionSpecs
	for (BoundaryConditionSpec bcs : param.getBoundaryconditionSpecs()) {
		compartment.addContent(getXML(bcs));
	}
	
	//Add Equations
	Enumeration<Equation> enum1 = param.getEquations();
	while (enum1.hasMoreElements()){
		Equation equ = enum1.nextElement();
		compartment.addContent( getXML(equ) );
	}
	//Add FastSystem
	if (param.getFastSystem()!=null){
		compartment.addContent( getXML(param.getFastSystem()) );
	}
	//Add Variable Initial Condition
	for (VarIniCondition varIni : param.getVarIniConditions()){
		compartment.addContent(getXML(varIni));
	}
	//Add JumpProcesses
	for (JumpProcess jp : param.getJumpProcesses()){
		compartment.addContent(getXML(jp));
	}
	for (ParticleJumpProcess pjp : param.getParticleJumpProcesses()){
		compartment.addContent(getXML(pjp));
	}
	for (ParticleProperties pp : param.getParticleProperties()){
		compartment.addContent(getXML(pp));
	}
	return compartment;
}

private org.jdom.Element getXML(ParticleProperties param) throws XmlParseException {
	org.jdom.Element particleProperties = new org.jdom.Element(XMLTags.ParticlePropertiesTag);

	particleProperties.setAttribute(XMLTags.NameAttrTag, mangle(param.getVariable().getName()));
	
	for (ParticleInitialCondition pic : param.getParticleInitialConditions()) {
		org.jdom.Element particleInitial = null;
		if (pic instanceof ParticleInitialConditionCount) {
			particleInitial = new org.jdom.Element(XMLTags.ParticleInitialCountTag);
			ParticleInitialConditionCount ppic = (ParticleInitialConditionCount)pic;
			Element e = new Element(XMLTags.ParticleCountTag);
			e.setText(mangleExpression(ppic.getCount()));
			particleInitial.addContent(e);
			
			if (ppic.getLocationX() != null) {
				e = new Element(XMLTags.ParticleLocationXTag);
				e.setText(mangleExpression(ppic.getLocationX()));
				particleInitial.addContent(e);
			}
			
			if (ppic.getLocationY() != null) {
				e = new Element(XMLTags.ParticleLocationYTag);
				e.setText(mangleExpression(ppic.getLocationY()));
				particleInitial.addContent(e);
			}
			
			if (ppic.getLocationZ() != null) {
				e = new Element(XMLTags.ParticleLocationZTag);
				e.setText(mangleExpression(ppic.getLocationZ()));
				particleInitial.addContent(e);				
			}			
		} else if (pic instanceof ParticleInitialConditionConcentration) {
			particleInitial = new org.jdom.Element(XMLTags.ParticleInitialConcentrationTag);
			ParticleInitialConditionConcentration ppic = (ParticleInitialConditionConcentration)pic;
			Element e = new Element(XMLTags.ParticleDistributionTag);
			e.setText(mangleExpression(ppic.getDistribution()));
			particleInitial.addContent(e);
		}
		particleProperties.addContent(particleInitial);
	}
	
	Element diff = new Element(XMLTags.ParticleDiffusionTag);
	diff.setText(mangleExpression(param.getDiffusion()));
	particleProperties.addContent(diff);

	if (param.getDriftX()!=null){
		Element driftX = new Element(XMLTags.ParticleDriftXTag);
		driftX.setText(mangleExpression(param.getDriftX()));
		particleProperties.addContent(driftX);
	}
	if (param.getDriftY()!=null){
		Element driftY = new Element(XMLTags.ParticleDriftYTag);
		driftY.setText(mangleExpression(param.getDriftY()));
		particleProperties.addContent(driftY);
	}
	if (param.getDriftZ()!=null){
		Element driftZ = new Element(XMLTags.ParticleDriftZTag);
		driftZ.setText(mangleExpression(param.getDriftZ()));
		particleProperties.addContent(driftZ);
	}
	return particleProperties;
}

private org.jdom.Element getXML(ParticleJumpProcess param) {
	org.jdom.Element particleJumpProcessElement = new org.jdom.Element(XMLTags.ParticleJumpProcessTag);
	//name
	particleJumpProcessElement.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	if (param.getProcessSymmetryFactor()!=null){
		particleJumpProcessElement.setAttribute(XMLTags.ProcessSymmetryFactorAttrTag, Double.toString(param.getProcessSymmetryFactor().getFactor()));
	}
	// Selected Particle
	for (ParticleVariable vpv : param.getParticleVariables()) {
		Element e = new Element(XMLTags.SelectedParticleTag);
		e.setAttribute(XMLTags.NameAttrTag, mangle(vpv.getName()));
		particleJumpProcessElement.addContent(e);
	}
	//probability rate
	Element prob = null;
	JumpProcessRateDefinition particleProbabilityRate = param.getParticleRateDefinition();
	if (particleProbabilityRate instanceof MacroscopicRateConstant) {
		prob = new Element(XMLTags.MacroscopicRateConstantTag);
		prob.addContent(mangleExpression(((MacroscopicRateConstant)particleProbabilityRate).getExpression()));
	}else if (particleProbabilityRate instanceof InteractionRadius) {
		prob = new Element(XMLTags.InteractionRadiusTag);
		prob.addContent(mangleExpression(((InteractionRadius)particleProbabilityRate).getExpression()));
	} 
	else {
		throw new RuntimeException("ParticleRateDefinition in XmlProducer not implemented");
	}
	particleJumpProcessElement.addContent(prob);
	
	//Actions
	for (Action action : param.getActions()) {
		particleJumpProcessElement.addContent(getXML(action));
	}

	return particleJumpProcessElement;
}

/**
 * This method returns a XML representation of a Constant object.
 * Creation date: (3/2/2001 11:46:46 AM)
 * @return Element
 * @param param cbit.vcell.math.Constant
 */
private Element getXML(Constant param) {
	Element constant = new Element(XMLTags.ConstantTag);

	//Add atributes
	constant.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	constant.addContent(mangleExpression(param.getExpression()) );

	return constant;
}


/**
 * This method returns a XML representation of a Equation object.
 * Creation date: (3/2/2001 1:56:34 PM)
 * @return Element
 * @param param cbit.vcell.math.Equation
 */
private Element getXML(Equation param) throws XmlParseException{
	if (param instanceof JumpCondition) {
		return getXML((JumpCondition)param);
	}
	else if (param instanceof MembraneRegionEquation) {
		return getXML((MembraneRegionEquation)param);
	}
	else if (param instanceof OdeEquation) {
		return getXML((OdeEquation)param);
	}
	else if (param instanceof PdeEquation) {
		return getXML((PdeEquation)param);
	}
	else if (param instanceof VolumeRegionEquation) {
		return getXML((VolumeRegionEquation)param);
	}
	else if (param instanceof ComputeMembraneMetricEquation){
		return getXML((ComputeMembraneMetricEquation)param);
	}
	else if (param instanceof ComputeNormalComponentEquation){
		return getXML((ComputeNormalComponentEquation)param);
	}
	else if (param instanceof ComputeCentroidComponentEquation){
		return getXML((ComputeCentroidComponentEquation)param);
	}
	else {
		throw new XmlParseException("Unknown equation type: " + param.getClass().getName());
	}
}

/**
 * This method returns a XML representation of a FastSystemImplicit object.
 * Creation date: (3/2/2001 4:05:28 PM)
 * @return Element
 * @param param cbit.vcell.math.FastSystemImplicit
 */
private Element getXML(FastSystem param) {
	Element fastsystem = new Element(XMLTags.FastSystemTag);

	//Add Fast Invariant subelements
	Element fastinvariant;
	Enumeration<FastInvariant> enum_fi = param.getFastInvariants();
	while (enum_fi.hasMoreElements()){
		fastinvariant = new Element(XMLTags.FastInvariantTag);
		FastInvariant fi = enum_fi.nextElement();
		fastinvariant.addContent(mangleExpression(fi.getFunction()));
		fastsystem.addContent(fastinvariant);
	}	
	//Add FastRate subelements
	Element fastrate;
	Enumeration<FastRate> enum_fr = param.getFastRates();
	while (enum_fr.hasMoreElements()){
		FastRate fr = (FastRate)enum_fr.nextElement();
		fastrate = new Element(XMLTags.FastRateTag);
		fastrate.addContent(mangleExpression(fr.getFunction()));
		fastsystem.addContent(fastrate);
	}
		
	return fastsystem;
}


/**
 * This method returns a XML representation of a FilamentRegionVariable object.
 * Creation date: (3/2/2001 11:46:46 AM)
 * @return Element
 * @param param cbit.vcell.math.FilamentRegionVariable
 */
private Element getXML(FilamentRegionVariable param) {
	Element filregvar = new Element(XMLTags.FilamentRegionVariableTag);

	//Add atributes
	filregvar.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	if (param.getDomain()!=null){
		filregvar.setAttribute(XMLTags.DomainAttrTag, mangle(param.getDomain().getName()));
	}

	return filregvar;
}


/**
 * This method returns a XML representation of a FilamentSubDomain object.
 * Creation date: (3/2/2001 5:49:07 PM)
 * @return Element
 * @param param cbit.vcell.math.FilamentSubDomain
 */
private Element getXML(FilamentSubDomain param) throws XmlParseException{
	Element filament = new Element(XMLTags.FilamentSubDomainTag);
	
	filament.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	filament.setAttribute(XMLTags.OutsideCompartmentTag, mangle(param.getOutsideCompartment().getName()));
	//Add equations
	Enumeration<Equation> enum1 = param.getEquations();
	while (enum1.hasMoreElements()){
		Equation equ = enum1.nextElement();
		filament.addContent( getXML(equ) );
	}
	//Add FastSytem
	if (param.getFastSystem()!=null){
		filament.addContent( getXML(param.getFastSystem()));
	}

	return filament;
}


/**
 * This method returns a XML representation of a FilamentVariable object.
 * Creation date: (3/2/2001 11:46:46 AM)
 * @return Element
 * @param param cbit.vcell.math.FilamentVariable
 */
private Element getXML(FilamentVariable param) {
	Element filvar = new Element(XMLTags.FilamentVariableTag);

	//Add atributes
	filvar.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	if (param.getDomain()!=null){
		filvar.setAttribute(XMLTags.DomainAttrTag, mangle(param.getDomain().getName()));
	}

	return filvar;
}


private Element getXML(PointVariable param) {
	Element pointvar = new Element(XMLTags.PointVariableTag);

	//Add atributes
	pointvar.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	if (param.getDomain()!=null){
		pointvar.setAttribute(XMLTags.DomainAttrTag, mangle(param.getDomain().getName()));
	}

	return pointvar;
}


/**
 * This method returns a XML declaration of a Function object.
 * Creation date: (3/2/2001 1:08:13 PM)
 * @return Element
 * @param param cbit.vcell.math.Function
 */
private Element getXML(Function param) {
	Element function = new Element(XMLTags.FunctionTag);

	//Add atributes
	function.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	if (param.getDomain()!=null){
		function.setAttribute(XMLTags.DomainAttrTag, mangle(param.getDomain().getName()));
	}
	function.addContent(mangleExpression(param.getExpression()) );

	return function;
}

private Element getXML(AnnotatedFunction param) {
	Element function = new Element(XMLTags.AnnotatedFunctionTag);

	//Add atributes
	function.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	if (param.getErrorString() != null) {
		function.setAttribute(XMLTags.ErrorStringTag, param.getErrorString());
	} else {
		function.setAttribute(XMLTags.ErrorStringTag, "");
	}
	if (param.getDomain()!=null){
		function.setAttribute(XMLTags.DomainAttrTag, mangle(param.getDomain().getName()));
	}
	function.setAttribute(XMLTags.FunctionTypeTag, param.getFunctionType().getTypeName());
	function.addContent(mangleExpression(param.getExpression()) );

	return function;
}

/**
 * This method returns a XML representation of a JumpCondition object.
 * Creation date: (3/2/2001 2:05:17 PM)
 * @return Element
 * @param param cbit.vcell.math.JumpCondition
 */
private Element getXML(JumpCondition param) {
	Element jump = new Element(XMLTags.JumpConditionTag);

	//add Atributes
	jump.setAttribute(XMLTags.NameAttrTag, mangle(param.getVariable().getName()));
	//add Influx subelement
	Element influx = new Element(XMLTags.InFluxTag);
	if (param.getInFluxExpression() != null) {
		influx.addContent(mangleExpression(param.getInFluxExpression()) );
	} else {
		influx.addContent("0.0");
	}
	jump.addContent(influx);
	//Add OutFlux subelement
	 Element outflux = new  Element(XMLTags.OutFluxTag);
	if (param.getOutFluxExpression() != null) {
		outflux.addContent(mangleExpression(param.getOutFluxExpression()));
	} else {
		outflux.addContent("0.0");
	}
	jump.addContent(outflux);

	return jump;
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2006 11:43:19 AM)
 * @return Element
 * @param param cbit.vcell.math.JumpProcess
 */
private Element getXML(JumpProcess param) 
{
	Element jump = new Element(XMLTags.JumpProcessTag);
	//name
	jump.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	//probability rate
	Element prob = new Element(XMLTags.ProbabilityRateTag);
	prob.addContent(mangleExpression(param.getProbabilityRate()) );
	jump.addContent(prob);
	//Actions
	Enumeration<Action> actions = Collections.enumeration(param.getActions());
	while (actions.hasMoreElements())
	{
		Action action = actions.nextElement();
		jump.addContent(getXML(action));
	}

	return jump;
}

/**
 * copy comments, if present, into XML representation
 * @param source if instanceof {@link Commented}, queried for comments
 * @param destination
 */
private void transcribeComments(Object source, Element destination) {
	if (source instanceof Commented) {
		Commented c= (Commented) source;
		String before = c.getBeforeComment();
		String after = c.getAfterComment();
		if (before != null) {
			destination.setAttribute(XMLTags.BEFORE_COMMENT_ATTR_TAG,before);
		}
		if (after != null) {
			destination.setAttribute(XMLTags.AFTER_COMMENT_ATTR_TAG,after);
		}
	}
}

/**
 * This method returns a XML representation of a MathDescription object.
 * Creation date: (3/2/2001 10:57:25 AM)
 * @return Element
 * @param mathdes cbit.vcell.math.MathDescription
 */
Element getXML(MathDescription mathdes) throws XmlParseException {
    Element math = new Element(XMLTags.MathDescriptionTag);

    //Add attributes
    math.setAttribute(XMLTags.NameAttrTag, mangle(mathdes.getName()));
    //math.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(mathdes.getDescription()));
    //Add annotation
    if (mathdes.getDescription()!=null && mathdes.getDescription().length()>0) {
    	Element annotationElem = new Element(XMLTags.AnnotationTag);
    	annotationElem.setText(mangle(mathdes.getDescription()));
    	math.addContent(annotationElem);
    }
    
    List <ParticleMolecularType> particleMolecularTypes = mathdes.getParticleMolecularTypes();
    for (ParticleMolecularType particleMolecularType : particleMolecularTypes) {
    	math.addContent(getXML(particleMolecularType));
    }
    
    //Add Constant subelements
    Enumeration<Variable> enum1 = mathdes.getVariables();
    //extra reordering added here, temporary
	/*java.util.Iterator k;
    try {
    	VariableHash varHash = new VariableHash();
    	while (enum1.hasMoreElements()) 
    	 	varHash.addVariable((Variable)enum1.nextElement());
    	Variable vars [] = varHash.getReorderedVariables();
    	k = new ArrayList(java.util.Arrays.asList(vars)).iterator();
    } catch (cbit.vcell.mapping.MappingException e) {
		e.printStackTrace();
		return null;
    }*/
    while (enum1.hasMoreElements()) {
        Variable var = enum1.nextElement();
        Element element = null;
        if (var instanceof Constant) 
		{
            element = getXML((Constant) var);
        }
        else if (var instanceof FilamentRegionVariable) {
            element = getXML((FilamentRegionVariable) var);
        }
        else if (var instanceof FilamentVariable) {
            element = getXML((FilamentVariable) var);
        }
        else if (var instanceof PointVariable) {
            element = getXML((PointVariable) var);
        }
        else if (var instanceof Function) {
            element = getXML((Function) var);
        }
        else if (var instanceof RandomVariable) {
            element = getXML((RandomVariable) var);
        }
        else if (var instanceof InsideVariable) {
	        //*** for internal use! Ignore it ***
	        continue;
        }
        else if (var instanceof MembraneRegionVariable) {
	        element = getXML((MembraneRegionVariable) var);
        }
        else if (var instanceof MemVariable) {
            element = getXML((MemVariable) var);
        }         
        else if (var instanceof OutsideVariable) {
	        //*** for internal use! Ignore it ****
	        continue;
        }
        else if (var instanceof VolumeRegionVariable) {
            element = getXML((VolumeRegionVariable) var);
        }
        else if (var instanceof VolVariable) {
            element = getXML((VolVariable) var);
        } 
        else if (var instanceof StochVolVariable) { //added for stochastic volumn variables
            element = getXML((StochVolVariable) var);
        } 
        else if (var instanceof ParticleVariable) {
        	element = getXML((ParticleVariable) var);
        }
        else if (var instanceof ParticleObservable) {
        	element = getXML((ParticleObservable) var);
        }
        else {
	        throw new XmlParseException("An unknown variable type "+var.getClass().getName()+" was found when parsing the mathdescription "+ mathdes.getName() +"!");
        }
        transcribeComments(var, element);
        math.addContent(element);
    }

    //this was moved to the simspec!
    /*	buffer.append("\n");
    	if (geometry != null){
    		buffer.append(geometry.getXML());
    	}	
    	buffer.append("\n");*/
    	
    //Add subdomains
    Enumeration<SubDomain> enum2 = mathdes.getSubDomains();
    while (enum2.hasMoreElements()) {
        SubDomain subDomain = enum2.nextElement();
        math.addContent(getXML(subDomain));
    }
    
    //Add Metadata (Version) if there is!
    if (mathdes.getVersion() != null) {
        math.addContent(getXML(mathdes.getVersion(), mathdes));
    }
    Iterator<Event> iter = mathdes.getEvents();
    while (iter.hasNext()) {
    	math.addContent(getXML(iter.next()));
    }

    PostProcessingBlock postProcessingBlock = mathdes.getPostProcessingBlock();
    if (postProcessingBlock.getNumDataGenerators() > 0) {    	
    	math.addContent(getXML(postProcessingBlock));
    }

    return math;
}

private Element getXML(PostProcessingBlock postProcessingBlock) {
	Element element = new Element(XMLTags.PostProcessingBlock);
    for (DataGenerator dataGenerator : postProcessingBlock.getDataGeneratorList()) {
    	if (dataGenerator instanceof ExplicitDataGenerator) {
	    	Element e = new Element(XMLTags.ExplicitDataGenerator);
	    	e.setAttribute(XMLTags.NameAttrTag, mangle(dataGenerator.getName()));
	    	if (dataGenerator.getDomain()!=null){
	    		e.setAttribute(XMLTags.DomainAttrTag, mangle(dataGenerator.getDomain().getName()));
	    	}
	    	e.addContent(mangleExpression(dataGenerator.getExpression()));
	    	element.addContent(e);
    	} else if (dataGenerator instanceof ProjectionDataGenerator) {
    		element.addContent(getXML((ProjectionDataGenerator)dataGenerator));
    	} else if (dataGenerator instanceof ConvolutionDataGenerator) {
    		element.addContent(getXML((ConvolutionDataGenerator)dataGenerator));
    	}
    } 
    return element;
}

private Element getXML(ConvolutionDataGenerator convolutionDataGenerator) {
	Element element = new Element(XMLTags.ConvolutionDataGenerator);
	element.setAttribute(XMLTags.NameAttrTag, mangle(convolutionDataGenerator.getName()));
	
	Element kernelElement = new Element(XMLTags.Kernel);
	ConvolutionDataGeneratorKernel kernel = convolutionDataGenerator.getKernel();
	if (kernel instanceof GaussianConvolutionDataGeneratorKernel) {
		kernelElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.KernelType_Gaussian);
	
		GaussianConvolutionDataGeneratorKernel gck = (GaussianConvolutionDataGeneratorKernel)kernel;
		Element e = new Element(XMLTags.KernelGaussianSigmaXY);
		e.addContent(mangleExpression(gck.getSigmaXY_um()));
		kernelElement.addContent(e);
		
		e = new Element(XMLTags.KernelGaussianSigmaZ);
		e.addContent(mangleExpression(gck.getSigmaZ_um()));
		kernelElement.addContent(e);
	}
	element.addContent(kernelElement);
	
	if (convolutionDataGenerator.getVolFunction()!=null){
		Element e = new Element(XMLTags.VolumeFunctionTag);
		e.addContent(mangleExpression(convolutionDataGenerator.getVolFunction()));
		element.addContent(e);
	}
	
	if (convolutionDataGenerator.getMemFunction()!=null){
		Element e = new Element(XMLTags.MembraneFunctionTag);
		e.addContent(mangleExpression(convolutionDataGenerator.getMemFunction()));
		element.addContent(e);
	}
	
	return element;
}

private Element getXML(ProjectionDataGenerator projectionDataGenerator) {
	Element element = new Element(XMLTags.ProjectionDataGenerator);
	element.setAttribute(XMLTags.NameAttrTag, mangle(projectionDataGenerator.getName()));
	if (projectionDataGenerator.getDomain()!=null){
		element.setAttribute(XMLTags.DomainAttrTag, mangle(projectionDataGenerator.getDomain().getName()));
	}
	
	Element e = new Element(XMLTags.ProjectionAxis);
	e.addContent(projectionDataGenerator.getAxis().name());
	element.addContent(e);
	
	e = new Element(XMLTags.ProjectionOperation);
	e.addContent(projectionDataGenerator.getOperation().name());
	element.addContent(e);
	
	e = new Element(XMLTags.FunctionTag);
	e.addContent(mangleExpression(projectionDataGenerator.getFunction()));
	element.addContent(e); 
	
	return element;
}

private Element getXML(RandomVariable var) {
	Element randomVariableElement = null;
	if (var instanceof VolumeRandomVariable) {
		randomVariableElement = new Element(XMLTags.VolumeRandomVariableTag);
	} else {
		randomVariableElement = new Element(XMLTags.MembraneRandomVariableTag);
	}

	randomVariableElement.setAttribute(XMLTags.NameAttrTag, mangle(var.getName()));
	
	Element seedElement = new Element(XMLTags.RandomVariableSeedTag);
	seedElement.addContent(mangleExpression(var.getSeed()));
	randomVariableElement.addContent(seedElement);
	
	if (var.getDomain()!=null){
		randomVariableElement.setAttribute(XMLTags.DomainAttrTag, mangle(var.getDomain().getName()));
	}

	Element distElement = null;
	if (var.getDistribution() instanceof UniformDistribution) {
		distElement = getXML((UniformDistribution)var.getDistribution());
	} else if (var.getDistribution() instanceof GaussianDistribution) {
		distElement = getXML((GaussianDistribution)var.getDistribution());
	}
	randomVariableElement.addContent(distElement);
	
	return randomVariableElement;
}
	

private Element getXML(UniformDistribution uniDist) {	
	Element element = new Element(XMLTags.UniformDistributionTag);
	
	Element loelement = new Element(XMLTags.UniformDistributionMinimumTag);
	loelement.addContent(mangleExpression(uniDist.getMinimum()));
	element.addContent(loelement);
	
	Element hielement = new Element(XMLTags.UniformDistributionMaximumTag);
	hielement.addContent(mangleExpression(uniDist.getMaximum()));
	element.addContent(hielement);		
	
	return element;
}

private Element getXML(GaussianDistribution gauDist) {
	Element element = new Element(XMLTags.GaussianDistributionTag);
	
	Element muelement = new Element(XMLTags.GaussianDistributionMeanTag);
	muelement.addContent(mangleExpression(gauDist.getMean()));
	element.addContent(muelement);
	
	Element sigmaelement = new Element(XMLTags.GaussianDistributionStandardDeviationTag);
	sigmaelement.addContent(mangleExpression(gauDist.getStandardDeviation()));
	element.addContent(sigmaelement);		

	return element;
}

/**
 * This method returns a XML representation of a MembraneRegionEquation object.
 * Creation date: (3/2/2001 2:05:17 PM)
 * @return Element
 * @param param cbit.vcell.math.MembraneRegionEquation
 */
private Element getXML(MembraneRegionEquation param) {
	Element memregeq = new Element(XMLTags.MembraneRegionEquationTag);

	//add name
	memregeq.setAttribute(XMLTags.NameAttrTag, mangle(param.getVariable().getName()));
	
	//add uniform rate
	Element tempElem = null;
	String tempString;
	
	if (param.getUniformRateExpression() != null){
		tempString = mangleExpression(param.getUniformRateExpression());
		//buffer.append("\t\t"+VCML.UniformRate+" "+getUniformRateExpression()+";\n");
	}else{
		tempString = "0.0";
		//buffer.append("\t\t"+VCML.UniformRate+" "+"0.0;\n");
	}
	tempElem = new Element(XMLTags.UniformRateTag);
	tempElem.setText(tempString);
	memregeq.addContent(tempElem);
	
	//add MembraneRate
	if (param.getMembraneRateExpression() != null){
		tempString = mangleExpression(param.getMembraneRateExpression());
//		buffer.append("\t\t"+VCML.MembraneRate+" "+getMembraneRateExpression()+";\n");
	}else{
		tempString = "0.0";
//		buffer.append("\t\t"+VCML.MembraneRate+" "+"0.0;\n");
	}
	tempElem = new Element(XMLTags.MembraneRateTag);
	tempElem.setText(tempString);
	memregeq.addContent(tempElem);
	
	//add initial
	if (param.getInitialExpression() != null){
		tempElem = new Element( XMLTags.InitialTag );
		tempElem.setText(mangleExpression(param.getInitialExpression()) );
		memregeq.addContent(tempElem);
//		buffer.append("\t\t"+VCML.Initial+"\t "+initialExp.infix()+";\n");
	}
	
	//add solutionType
	tempElem = new Element(XMLTags.SolutionTypeTag);
	switch (param.getSolutionType()){
		case MembraneRegionEquation.UNKNOWN_SOLUTION:{
			tempElem.setAttribute(XMLTags.TypeAttrTag, "unknown");	
			
			if (param.getInitialExpression() == null){
				tempElem.setText("0.0");
//				buffer.append("\t\t"+VCML.Initial+"\t "+"0.0;\n");
			}
			break;
		}
		case MembraneRegionEquation.EXACT_SOLUTION:{
			tempElem.setAttribute(XMLTags.TypeAttrTag, "exact");
			tempElem.setText(mangleExpression(param.getExactSolution()) );		
//			buffer.append("\t\t"+VCML.Exact+" "+exactExp.infix()+";\n");
			break;
		}
	}
	memregeq.addContent(tempElem);

	return memregeq;
}


/**
 * This method returns a XML representation of a MembraneRegionVariable object.
 * Creation date: (3/2/2001 1:03:35 PM)
 * @return Element
 * @param param cbit.vcell.math.MembraneRegionVariable
 */
private Element getXML(MembraneRegionVariable param) {
	Element memregvar = new Element(XMLTags.MembraneRegionVariableTag);

	//Add atributes
	memregvar.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	if (param.getDomain()!=null){
		memregvar.setAttribute(XMLTags.DomainAttrTag, mangle(param.getDomain().getName()));
	}

	return memregvar;
}


/**
 * This method returns a XML representation of a MembraneSubDomain object.
 * Creation date: (3/2/2001 5:40:17 PM)
 * @return Element
 * @param param cbit.vcell.math.MembraneSubDomain
 */
private Element getXML(MembraneSubDomain param) throws XmlParseException{
	Element membrane = new Element(XMLTags.MembraneSubDomainTag);
	
	//Add attributes
	membrane.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	membrane.setAttribute(XMLTags.InsideCompartmentTag, mangle(param.getInsideCompartment().getName()));
	membrane.setAttribute(XMLTags.OutsideCompartmentTag, mangle(param.getOutsideCompartment().getName()));
	
	//Add boundaryType subelements
	Element boundary;
	//Xm
	boundary = new Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueXm);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionXm().boundaryTypeStringValue());
	membrane.addContent(boundary);
	//Xp
	boundary = new Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueXp);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionXp().boundaryTypeStringValue());
	membrane.addContent(boundary);
	//Ym
	boundary = new Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueYm);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionYm().boundaryTypeStringValue());
	membrane.addContent(boundary);
	//Yp
	boundary = new Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueYp);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionYp().boundaryTypeStringValue());
	membrane.addContent(boundary);
	//Zm
	boundary = new Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueZm);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionZm().boundaryTypeStringValue());
	membrane.addContent(boundary);
	//Zp
	boundary = new Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueZp);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionZp().boundaryTypeStringValue());
	membrane.addContent(boundary);

	//Add Equation subelements
	Enumeration<Equation> enum1 = param.getEquations();
	while (enum1.hasMoreElements()){
		Equation equ = enum1.nextElement();
		membrane.addContent( getXML(equ) );
	}
	//Add JumConditions
	Enumeration<JumpCondition> enum2 = param.getJumpConditions();
	while (enum2.hasMoreElements()){
		JumpCondition jc = (JumpCondition)enum2.nextElement();
		membrane.addContent( getXML(jc) );
	}
	//Add FastSystem (if there is)
	if (param.getFastSystem()!=null){
		membrane.addContent( getXML(param.getFastSystem()) );
	}
	
	for (ParticleProperties pp : param.getParticleProperties()){
		membrane.addContent(getXML(pp));
	}
	for (ParticleJumpProcess pjp : param.getParticleJumpProcesses()){
		membrane.addContent(getXML(pjp));
	}
	
	Mutable<Element> velocity = new MutableObject<>();
	addVelocityMaybe(velocity, XMLTags.XAttrTag,param.getVelocityX()); 
	addVelocityMaybe(velocity, XMLTags.YAttrTag,param.getVelocityY()); 
	if (velocity.getValue() != null ) {
		membrane.addContent(velocity.getValue());
	}
	
	return membrane;
}

/**
 * add velocity element if it's not null
 * @param dest non null; lazily create container element when needed
 * @param tag to label with
 * @param exp if null, do nothing
 */
private void addVelocityMaybe(Mutable<Element> dest, String tag, Expression exp) {
	if (exp == null) {
		return;
	}
	
	Element velocity = dest.getValue(); //create Element if we need to
	if (velocity == null) {
		velocity = new Element(XMLTags.VelocityTag);
		dest.setValue(velocity);
	}
	Element component = new Element(tag);
	component.addContent(mangleExpression(exp));
	velocity.addContent(component);
}

private Element getXML(PointSubDomain param) throws XmlParseException{
	Element pointSubDomain = new Element(XMLTags.PointSubDomainTag);
	
	//Add attributes
	pointSubDomain.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	

	//Add Equation subelements
	Enumeration<Equation> enum1 = param.getEquations();
	while (enum1.hasMoreElements()){
		Equation equ = enum1.nextElement();
		pointSubDomain.addContent( getXML(equ) );
	}
	if (param.getPositionX()!=null){
		Element positionX = new Element(XMLTags.PositionXTag);
		positionX.addContent(param.getPositionX().infix());
		pointSubDomain.addContent(positionX);
	}
	if (param.getPositionY()!=null){
		Element positionY = new Element(XMLTags.PositionYTag);
		positionY.addContent(param.getPositionY().infix());
		pointSubDomain.addContent(positionY);
	}
	if (param.getPositionZ()!=null){
		Element positionZ = new Element(XMLTags.PositionZTag);
		positionZ.addContent(param.getPositionZ().infix());
		pointSubDomain.addContent(positionZ);
	}
	return pointSubDomain;
}


/**
 * This method returns a XML representation of a MemVariable object.
 * Creation date: (3/2/2001 1:03:35 PM)
 * @return Element
 * @param param cbit.vcell.math.MemVariable
 */
private Element getXML(MemVariable param) {
	Element memvariable = new Element(XMLTags.MembraneVariableTag);

	//Add atributes
	memvariable.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	if (param.getDomain()!=null){
		memvariable.setAttribute(XMLTags.DomainAttrTag, mangle(param.getDomain().getName()));
	}

	return memvariable;
}


/**
 * This method returns a XML representation of a OdeEquation object.
 * Creation date: (3/2/2001 2:52:14 PM)
 * @return Element
 * @param param cbit.vcell.math.OdeEquation
 */
private Element getXML(OdeEquation param) throws XmlParseException {
	Element ode = new Element(XMLTags.OdeEquationTag);

	//Add atribute
	ode.setAttribute(XMLTags.NameAttrTag, mangle(param.getVariable().getName()));
	//Add Rate subelement
	Element rate = new Element(XMLTags.RateTag);
	if (param.getRateExpression() != null) {
		rate.addContent(mangleExpression(param.getRateExpression()));
	} else {
		rate.addContent("0.0");
	}
	ode.addContent(rate);
	//Add Initial
	Element initial = new Element(XMLTags.InitialTag);
	if (param.getInitialExpression() != null) {
		initial.addContent(mangleExpression(param.getInitialExpression()));
		ode.addContent(initial);
	}
	
	//add solution expression
	switch (param.getSolutionType()) {
		case Equation.UNKNOWN_SOLUTION: {
			ode.setAttribute(XMLTags.SolutionTypeTag, XMLTags.UnknownTypeTag);
			
			if (param.getInitialExpression()==null) {
				initial.setText(mangleExpression(new cbit.vcell.parser.Expression(0.0)) );
				ode.addContent(initial);
			}
			
			break;		
		}
		case Equation.EXACT_SOLUTION: {
			ode.setAttribute(XMLTags.SolutionTypeTag, XMLTags.ExactTypeTag);			
			Element solution = new Element(XMLTags.SolutionExpressionTag);
			solution.setText(mangle(param.getExactSolution().infix()) );
			ode.addContent(solution);

			break;
		}
		default: {
			throw new XmlParseException("Unknown solution type:"+param.getSolutionType());
		}
	}


	return ode;
}


/**
 * This methos returns a XML representation of a PdeEquation object.
 * Creation date: (3/2/2001 3:21:20 PM)
 * @return Element
 * @param param cbit.vcell.math.PdeEquation
 */
private Element getXML(PdeEquation param) throws XmlParseException {
	Element pde = new Element(XMLTags.PdeEquationTag);

	//Add Atribute
	pde.setAttribute(XMLTags.NameAttrTag, mangle(param.getVariable().getName()));
	if (param.isSteady()) {
		pde.setAttribute(XMLTags.SteadyTag, "1");
	}
	//Add Boundary subelements
	Element boundaries = new Element(XMLTags.BoundariesTag);
	//Xm
	if (param.getBoundaryXm() != null) {
		boundaries.setAttribute(XMLTags.BoundaryAttrValueXm, mangleExpression(param.getBoundaryXm()));
	}
	//Xp
	if (param.getBoundaryXp() != null) {
		boundaries.setAttribute(XMLTags.BoundaryAttrValueXp, mangleExpression(param.getBoundaryXp()));
	}
	//Ym
	if (param.getBoundaryYm() != null) {
		boundaries.setAttribute(XMLTags.BoundaryAttrValueYm, mangleExpression(param.getBoundaryYm()));
	}
	//Yp
	if (param.getBoundaryYp() != null) {
		boundaries.setAttribute(XMLTags.BoundaryAttrValueYp, mangleExpression(param.getBoundaryYp()));
	}
	//Zm
	if (param.getBoundaryZm() != null) {
		boundaries.setAttribute(XMLTags.BoundaryAttrValueZm, mangleExpression(param.getBoundaryZm()));
	}
	//Zp
	if (param.getBoundaryZp() != null) {
		boundaries.setAttribute(XMLTags.BoundaryAttrValueZp, mangleExpression(param.getBoundaryZp()));
	}
	//If is not empty, add it to the pdeEquation
	if (boundaries.getAttributes().size() >0){
		pde.addContent(boundaries);
	}
	
	// add BoundaryConditionValue
	if (param.getBoundaryconditionValues().size() > 0) {
		for (BoundaryConditionValue bcv : param.getBoundaryconditionValues()) {
			Element bcValueElement = new Element(XMLTags.BoundaryConditionValueTag);
			bcValueElement.setAttribute(XMLTags.NameAttrTag, mangle(bcv.getSubdomainName()));
			bcValueElement.setAttribute(XMLTags.BoundaryValueExpressionTag, mangleExpression(bcv.getBoundaryConditionExpression()));
			pde.addContent(bcValueElement);
		}
	}
	
	//add Rate
	Element rate = new Element(XMLTags.RateTag);
	if (param.getRateExpression() != null) {
		rate.addContent(mangleExpression(param.getRateExpression()));
	} else {
		rate.addContent(mangleExpression(new cbit.vcell.parser.Expression(0.0)));
	}
	pde.addContent(rate);
	//Diffusion
	Element diffusion = new Element(XMLTags.DiffusionTag);
	if (param.getDiffusionExpression() != null) {
		diffusion.addContent(mangleExpression(param.getDiffusionExpression()));
	} else {
		diffusion.addContent(mangleExpression(new cbit.vcell.parser.Expression(0.0)) );
	}
	pde.addContent(diffusion);
	//Initial
	Element initial = new Element(XMLTags.InitialTag);
	if (param.getInitialExpression() != null) {
		initial.addContent(mangleExpression(param.getInitialExpression()));
		pde.addContent(initial);
	}

	//add solution expression
	switch (param.getSolutionType()) {
		case Equation.UNKNOWN_SOLUTION: {
			pde.setAttribute(XMLTags.SolutionTypeTag, XMLTags.UnknownTypeTag);
			
			if (param.getInitialExpression()==null) {
				initial.setText( mangleExpression(new cbit.vcell.parser.Expression(0.0)) );
				pde.addContent(initial);
			}
			
			break;		
		}
		case Equation.EXACT_SOLUTION: {
			pde.setAttribute(XMLTags.SolutionTypeTag, XMLTags.ExactTypeTag);			
			Element solution = new Element(XMLTags.SolutionExpressionTag);
			solution.setText( mangle(param.getExactSolution().infix()) );
			pde.addContent(solution);

			break;
		}
		default: {
			throw new XmlParseException("Unknown solution type:"+param.getSolutionType());
		}
	}
	{
		//add Velocity
		Element velocity = null;
		Expression velX = param.getVelocityX();
		if (velX != null) {
			velocity = new Element(XMLTags.VelocityTag);
			velocity.setAttribute(XMLTags.XAttrTag, mangleExpression(velX));
		}
		Expression velY = param.getVelocityY();
		if (velY != null) {
			if (velocity == null) {
				velocity = new Element(XMLTags.VelocityTag);
			}
			velocity.setAttribute(XMLTags.YAttrTag, mangleExpression(velY));
		}
		Expression velZ = param.getVelocityZ();
		if (velZ != null) {
			if (velocity == null) {
				velocity = new Element(XMLTags.VelocityTag);
			}
			velocity.setAttribute(XMLTags.ZAttrTag, mangleExpression(velZ));
		}
		if (velocity != null) {
			pde.addContent(velocity);
		}
	}
	
	{
		//add Grad
		Element grad = null;
		Expression gradX = param.getGradientX();
		if (gradX != null) {
			if (grad == null) {
				grad = new Element(XMLTags.GradientTag);
			}
			grad.setAttribute(XMLTags.XAttrTag, mangleExpression(gradX));
		}
		Expression gradY = param.getGradientY();
		if (gradY != null) {
			if (grad == null) {
				grad = new Element(XMLTags.GradientTag);
			}
			grad.setAttribute(XMLTags.YAttrTag, mangleExpression(gradY));
		}
		Expression gradZ = param.getGradientZ();
		if (gradZ != null) {
			if (grad == null) {
				grad = new Element(XMLTags.GradientTag);
			}
			grad.setAttribute(XMLTags.ZAttrTag, mangleExpression(gradZ));
		}
		if (grad != null) {
			pde.addContent(grad);
		}
	}
	
	return pde;
}


/**
 * Get a XML element for this stochastic volumn variable.
 * Creation date: (7/19/2006 5:53:15 PM)
 * @return Element
 * @param param cbit.vcell.math.StochVolVariable
 */
private Element getXML(StochVolVariable param) {
	Element stochVar = new Element(XMLTags.StochVolVariableTag);

	//Add atribute
	stochVar.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));

	return stochVar;
}

private Element getXML(ParticleComponentStateDefinition param) {
	Element e = new Element(XMLTags.ParticleMolecularTypeAllowableStateTag);
	e.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	return e;
}
private Element getXML(ParticleMolecularComponent param) {
	Element e = new Element(XMLTags.ParticleMolecularComponentPatternTag);
	e.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	for (ParticleComponentStateDefinition pp : param.getComponentStateDefinitions()){
		e.addContent(getXML(pp));
	}
	return e;
}
private Element getXML(ParticleMolecularType param) {
	Element e = new Element(XMLTags.ParticleMolecularTypeTag);
	e.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	for (ParticleMolecularComponent pp : param.getComponentList()){
		e.addContent(getXML(pp));
	}
	for(String anchor : param.getAnchorList()) {
		Element a = new Element(XMLTags.ParticleMolecularTypeAnchorTag);
		a.setAttribute(XMLTags.NameAttrTag, mangle(anchor));
		e.addContent(a);
	}
	return e;
}

private Element getXML(ParticleMolecularTypePattern param) {
	Element e = new Element(XMLTags.ParticleMolecularTypePatternTag);
	e.setAttribute(XMLTags.NameAttrTag, mangle(param.getMolecularType().getName()));
	if (param.getMatchLabel() != null){
		e.setAttribute(XMLTags.ParticleMolecularTypePatternMatchLabelAttrTag, mangle(param.getMatchLabel()));
	}
	for(ParticleMolecularComponentPattern cp : param.getMolecularComponentPatternList()) {
		e.addContent(getXML(cp));
	}
	return e;
}
private Element getXML(ParticleMolecularComponentPattern param) {
	Element e = new Element(XMLTags.ParticleMolecularComponentPatternTag);
	e.setAttribute(XMLTags.NameAttrTag, mangle(param.getMolecularComponent().getName()));
	String state = "";
	if(param.getComponentStatePattern().isAny()) {
		state = "*";
	} else {
		state = param.getComponentStatePattern().getParticleComponentStateDefinition().getName();
	}
	e.setAttribute(XMLTags.StateAttrTag, mangle(state));
	if(param.getBondType().equals(ParticleBondType.Specified)) {
		String s = Integer.toString(param.getBondId());
		e.setAttribute(XMLTags.BondAttrTag, s);
	} else {
		String s = param.getBondType().symbol;
		e.setAttribute(XMLTags.BondAttrTag, s);
	}
	return e;
}

private Element getXML(ParticleObservable param) {
	Element e = null;
	if(param instanceof VolumeParticleObservable) {
		e = new org.jdom.Element(XMLTags.VolumeParticleObservableTag);
	} else {
		e = new org.jdom.Element(XMLTags.ParticleObservableTag);	
	}
	e.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	if (param.getDomain()!=null){
		e.setAttribute(XMLTags.DomainAttrTag, mangle(param.getDomain().getName()));
	}
	e.setAttribute(XMLTags.ParticleMolecularTypePatternTag, mangle(param.getType().name()));
	e.setAttribute(XMLTags.ParticleObservableSequenceTypeAttrTag, mangle(param.getSequence().name()));
	if (param.getSequence() == Sequence.PolymerLengthEqual || param.getSequence() == Sequence.PolymerLengthGreater){
		e.setAttribute(XMLTags.ParticleObservableSequenceLengthAttrTag, param.getQuantity().toString());
	}
	e.addContent(getVolumeParticleSpeciesPatternList(param));
	return e;
}
private Element getVolumeParticleSpeciesPatternList(ParticleObservable param) {
	org.jdom.Element e = null;
	e = new org.jdom.Element(XMLTags.VolumeParticleSpeciesPatternsTag);
	for (ParticleSpeciesPattern pp : param.getParticleSpeciesPatterns()){
		org.jdom.Element e1 = new org.jdom.Element(XMLTags.VolumeParticleSpeciesPatternTag);
		e1.setAttribute(XMLTags.NameAttrTag, mangle(pp.getName()));
		e.addContent(e1);
	}
	return e;
}
private Element getXML(ParticleVariable param) {
	org.jdom.Element e = null;
	if(param instanceof VolumeParticleVariable) {
		e = new org.jdom.Element(XMLTags.VolumeParticleVariableTag);
	} else if(param instanceof VolumeParticleSpeciesPattern) {
		e = new org.jdom.Element(XMLTags.VolumeParticleSpeciesPatternTag);
		String locationName = ((VolumeParticleSpeciesPattern) param).getLocationName();
		if (locationName != null){
			e.setAttribute(XMLTags.LocationAttrTag, mangle(locationName));
		}
		for (ParticleMolecularTypePattern pp : ((VolumeParticleSpeciesPattern)param).getParticleMolecularTypePatterns()){
			e.addContent(getXML(pp));
		}
	} else if(param instanceof MembraneParticleVariable){
		e = new org.jdom.Element(XMLTags.MembraneParticleVariableTag);
	} else {
		System.out.println("Unexpected element" + param);
	}
	//Add atribute
	e.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	if (param.getDomain()!=null){
		e.setAttribute(XMLTags.DomainAttrTag, mangle(param.getDomain().getName()));
	}
	return e;
}

/**
 * This method returns a XML representation of a SubDomain object type.
 * Creation date: (3/2/2001 1:13:30 PM)
 * @return Element
 * @param param cbit.vcell.math.SubDomain
 */
private Element getXML(SubDomain param) throws XmlParseException{
	Element e = null;
	if (param instanceof CompartmentSubDomain) {
		e = getXML((CompartmentSubDomain)param);
	} else if (param instanceof FilamentSubDomain) {
		e = getXML((FilamentSubDomain)param);
	} else if (param instanceof MembraneSubDomain) {
		e = getXML((MembraneSubDomain)param);
	} else if (param instanceof PointSubDomain) {
		e = getXML((PointSubDomain)param);
	}
	transcribeComments(param, e);
	
	return e;
}


/**
 * Get a XML element for the initial condition of a stochastic variable.
 * Creation date: (7/21/2006 10:54:51 AM)
 * @return Element
 * @param param cbit.vcell.math.VarIniCondition
 */
private Element getXML(VarIniCondition param) 
{
	Element varIni = null;
	if(param instanceof VarIniPoissonExpectedCount)
	{
		varIni = new Element(XMLTags.VarIniPoissonExpectedCountTag);
	}
	else
	{
		varIni = new Element(XMLTags.VarIniCountTag);
	}
	//Add atribute
	varIni.setAttribute(XMLTags.NameAttrTag, mangle(param.getVar().getName()));
	varIni.addContent(mangleExpression(param.getIniVal()));
	return varIni;
}

private Element getXML(BoundaryConditionSpec param) 
{
	Element bcSpec = new Element(XMLTags.BoundaryConditionSpecTag);
	//Add atribute
	bcSpec.setAttribute(XMLTags.BoundarySubdomainNameTag, mangle(param.getBoundarySubdomainName()));
	bcSpec.setAttribute(XMLTags.BoundaryTypeTag, param.getBoundaryConditionType().boundaryTypeStringValue());
	return bcSpec;
}


/**
 * This method returns a XML representation of a VolumeRegionEquation object.
 * Creation date: (3/2/2001 2:05:17 PM)
 * @return Element
 * @param param cbit.vcell.math.VolumeRegionEquation
 */
private Element getXML(VolumeRegionEquation param) {
	Element memregeq = new Element(XMLTags.VolumeRegionEquationTag);
	Element tempElem = null;
	String tempString;
	
	//add name
	memregeq.setAttribute(XMLTags.NameAttrTag, mangle(param.getVariable().getName()));
	//
	//add UniformRate
	if (param.getUniformRateExpression() != null){
		tempString = mangleExpression(param.getUniformRateExpression());
		//buffer.append("\t\t"+VCML.UniformRate+" "+getUniformRateExpression()+";\n");
	}else{
		tempString = "0.0";
		//buffer.append("\t\t"+VCML.UniformRate+" "+"0.0;\n");
	}
	tempElem = new Element(XMLTags.UniformRateTag);
	tempElem.setText(tempString);
	memregeq.addContent(tempElem);

	//add VolumeRate
	if (param.getVolumeRateExpression() != null){
		tempString = mangleExpression(param.getVolumeRateExpression());
//		buffer.append("\t\t"+VCML.VolumeRate+" "+getVolumeRateExpression()+";\n");
	}else{
		tempString = "0.0";
//		buffer.append("\t\t"+VCML.VolumeRate+" "+"0.0;\n");
	}
	tempElem = new Element(XMLTags.VolumeRateTag);
	tempElem.setText(tempString);
	memregeq.addContent(tempElem);
	
	//add Initial
	if (param.getInitialExpression() != null){
		tempElem = new Element( XMLTags.InitialTag );
		tempElem.setText(mangleExpression(param.getInitialExpression()) );
		memregeq.addContent(tempElem);
//		buffer.append("\t\t"+VCML.Initial+"\t "+initialExp.infix()+";\n");
	}

	//add solutionType
	tempElem = new Element(XMLTags.SolutionTypeTag);
	switch (param.getSolutionType()){
		case MembraneRegionEquation.UNKNOWN_SOLUTION:{
			tempElem.setAttribute(XMLTags.TypeAttrTag, "unknown");	
			if (param.getInitialExpression() == null){
				tempElem.setText("0.0");
//				buffer.append("\t\t"+VCML.Initial+"\t "+"0.0;\n");
			}
			break;
		}
		case MembraneRegionEquation.EXACT_SOLUTION:{
			tempElem.setAttribute(XMLTags.TypeAttrTag, "exact");
			tempElem.setText(mangleExpression(param.getExactSolution()) );		
//			buffer.append("\t\t"+VCML.Exact+" "+exactExp.infix()+";\n");
			break;
		}
	}
	memregeq.addContent(tempElem);

	return memregeq;
}


private Element getXML(ComputeMembraneMetricEquation param) {
	Element computeMemMetric = new Element(XMLTags.ComputeMembraneMetricTag);
	switch (param.getComponent()){
	case directionToMembraneX: {
		computeMemMetric.setAttribute(XMLTags.ComputeMembraneMetricComponentAttrTag, XMLTags.ComputeMembraneMetricComponentAttrTagValue_directionX);
		break;
	}
	case directionToMembraneY: {
		computeMemMetric.setAttribute(XMLTags.ComputeMembraneMetricComponentAttrTag, XMLTags.ComputeMembraneMetricComponentAttrTagValue_directionY);
		break;
	}
	case directionToMembraneZ: {
		computeMemMetric.setAttribute(XMLTags.ComputeMembraneMetricComponentAttrTag, XMLTags.ComputeMembraneMetricComponentAttrTagValue_directionZ);
		break;
	}
	case distanceToMembrane: {
		computeMemMetric.setAttribute(XMLTags.ComputeMembraneMetricComponentAttrTag, XMLTags.ComputeMembraneMetricComponentAttrTagValue_distance);
		break;
	}
	default:{
		throw new RuntimeException("unexpected component "+param.getComponent()+" for element "+XMLTags.ComputeMembraneMetricTag);
	}
	}
	computeMemMetric.setAttribute(XMLTags.ComputeMembraneMetricTargetMembraneAttrTag,mangle(param.getTargetMembrane().getName()));
	computeMemMetric.setAttribute(XMLTags.NameAttrTag,mangle(param.getVariable().getName()));
	return computeMemMetric;
}


private Element getXML(ComputeNormalComponentEquation param) {
	Element computeNormal = new Element(XMLTags.ComputeNormalTag);
	switch (param.getComponent()){
	case X: {
		computeNormal.setAttribute(XMLTags.ComputeNormalComponentAttrTag, XMLTags.ComputeNormalComponentAttrTagValue_X);
		break;
	}
	case Y: {
		computeNormal.setAttribute(XMLTags.ComputeNormalComponentAttrTag, XMLTags.ComputeNormalComponentAttrTagValue_Y);
		break;
	}
	case Z: {
		computeNormal.setAttribute(XMLTags.ComputeNormalComponentAttrTag, XMLTags.ComputeNormalComponentAttrTagValue_Z);
		break;
	}
	default:{
		throw new RuntimeException("unexpected component "+param.getComponent()+" for element "+XMLTags.ComputeNormalTag);
	}
	}
	computeNormal.setAttribute(XMLTags.NameAttrTag,mangle(param.getVariable().getName()));
	return computeNormal;
}


private Element getXML(ComputeCentroidComponentEquation param) {
	Element computeCentroid = new Element(XMLTags.ComputeCentroidTag);
	switch (param.getComponent()){
	case X: {
		computeCentroid.setAttribute(XMLTags.ComputeCentroidComponentAttrTag, XMLTags.ComputeCentroidComponentAttrTagValue_X);
		break;
	}
	case Y: {
		computeCentroid.setAttribute(XMLTags.ComputeCentroidComponentAttrTag, XMLTags.ComputeCentroidComponentAttrTagValue_Y);
		break;
	}
	case Z: {
		computeCentroid.setAttribute(XMLTags.ComputeCentroidComponentAttrTag, XMLTags.ComputeCentroidComponentAttrTagValue_Z);
		break;
	}
	default:{
		throw new RuntimeException("unexpected component "+param.getComponent()+" for element "+XMLTags.ComputeMembraneMetricTag);
	}
	}
	computeCentroid.setAttribute(XMLTags.NameAttrTag,mangle(param.getVariable().getName()));
	return computeCentroid;
}


/**
 * This method returns a XML representation of a VolumeRegionVariable object.
 * Creation date: (3/2/2001 11:56:48 AM)
 * @return Element
 * @param param cbit.vcell.math.VolumeRegionVariable
 */
private Element getXML(VolumeRegionVariable param) {
	Element volregvar = new Element(XMLTags.VolumeRegionVariableTag);

	//Add atribute
	volregvar.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	if (param.getDomain()!=null){
		volregvar.setAttribute(XMLTags.DomainAttrTag, mangle(param.getDomain().getName()));
	}

	return volregvar;
}


/**
 * This method returns a XML representation of a VolVariable object.
 * Creation date: (3/2/2001 11:56:48 AM)
 * @return Element
 * @param param cbit.vcell.math.VolVariable
 */
private Element getXML(VolVariable param) {
	Element volvariable = new Element(XMLTags.VolumeVariableTag);

	//Add atribute
	volvariable.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	if (param.getDomain()!=null){
		volvariable.setAttribute(XMLTags.DomainAttrTag, mangle(param.getDomain().getName()));
	}

	return volvariable;
}


/**
 * This method returns the XML representation of a MathModel.
 * Creation date: (3/28/2001 12:27:28 PM)
 * @return Element
 * @param param cbit.vcell.mathmodel.MathModel
 */
public Element getXML(MathModel param) throws XmlParseException{
	Element mathmodel = new Element(XMLTags.MathModelTag);
	//Add Attributes
	String name = param.getName();
	mathmodel.setAttribute(XMLTags.NameAttrTag, mangle(name));
	//mathmodel.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(param.getDescription()));
	//add Annotation
	if (param.getDescription()!=null && param.getDescription().length()>0) {
		Element annotationElem = new Element(XMLTags.AnnotationTag);
		annotationElem.setText(mangle(param.getDescription()));
		mathmodel.addContent(annotationElem);
	}
	//Add Subelements
	//Add Geometry
	try {
		mathmodel.addContent( getXML(param.getMathDescription().getGeometry()) );
	} catch (XmlParseException e) {
		e.printStackTrace();
		throw new XmlParseException("A problem occurred when trying to process the geometry!", e);
	}
	//Add Mathdescription
	mathmodel.addContent( getXML(param.getMathDescription()) );
	
	// Add output functions
	if (param.getOutputFunctionContext() != null) {
		ArrayList<AnnotatedFunction> outputFunctions = param.getOutputFunctionContext().getOutputFunctionsList();
		if(outputFunctions != null && outputFunctions.size() > 0) {
			// get output functions
			mathmodel.addContent(getXML(outputFunctions));
		}
	}
	
	//Add Simulations
	cbit.vcell.solver.Simulation[] arraysim = param.getSimulations();
	if (arraysim != null) {
		for (int i=0 ; i< arraysim.length ; i++) {
			mathmodel.addContent( getXML(arraysim[i]) );
		}
	}
	//Add Metadata (if there is)
	if ( param.getVersion() != null ) {
		mathmodel.addContent( getXML(param.getVersion(), param.getName(), param.getDescription()) );
	}
	
//	MIRIAMHelper.addToSBMLAnnotation(mathmodel, param.getMIRIAMAnnotation(), true);
	return mathmodel;
}


/**
 * This method returns a XML representation of a Catalyst object.
 * Creation date: (2/26/2001 6:44:05 PM)
 * @return Element
 * @param param cbit.vcell.model.Catalyst
 */
private Element getXML(Catalyst param) {
	Element catalyst = new Element(XMLTags.CatalystTag);

	//Add attribute
	catalyst.setAttribute(XMLTags.SpeciesContextRefAttrTag, mangle(param.getSpeciesContext().getName()));

	//If keyFlag is on print the Keyvalue
	if (param.getKey() !=null && this.printKeysFlag) {
		catalyst.setAttribute(XMLTags.KeyValueAttrTag, param.getKey().toString());
	}
		
	return catalyst;
}


/**
 * This method returns a XML representation of a diagram object type.
 * Creation date: (2/27/2001 5:13:26 PM)
 * @return Element
 * @param param cbit.vcell.model.Diagram
 */
private Element getXML(Diagram param) {
	Element diagram = new Element(XMLTags.DiagramTag);

	//add attributes
	diagram.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	diagram.setAttribute(XMLTags.StructureAttrTag, mangle(param.getStructure().getName()));
	
	//Add NodeReferences subelements
	if (param.getNodeList().size()>0){
		List<NodeReference> children = param.getNodeList();
		for (int i=0 ; i<children.size() ;i++ ){
			NodeReference node = children.get(i);
			diagram.addContent( getXML(node) );
		}
	}
	
	return diagram;
}

/**
 * This method returns a XML representation of a Flux Reaction object.
 * Creation date: (2/26/2001 12:30:13 PM)
 * @return Element
 * @param param cbit.vcell.model.FluxReaction
 */
private Element getXML(FluxReaction param) throws XmlParseException {
	Element fluxreaction = new Element(XMLTags.FluxStepTag);
	//get Attributes
	String versionName = (param.getName() != null) ? mangle(param.getName()) : "unnamed_fluxReaction";
	fluxreaction.setAttribute(XMLTags.NameAttrTag, versionName);
	fluxreaction.setAttribute(XMLTags.StructureAttrTag, mangle(param.getStructure().getName()));
	fluxreaction.setAttribute(XMLTags.ReversibleAttrTag, mangle(Boolean.valueOf(param.isReversible()).toString()));
	
	if (param.getPhysicsOptions() == FluxReaction.PHYSICS_ELECTRICAL_ONLY){
		fluxreaction.setAttribute(XMLTags.FluxOptionAttrTag, XMLTags.FluxOptionElectricalOnly);
	}else if (param.getPhysicsOptions() == FluxReaction.PHYSICS_MOLECULAR_AND_ELECTRICAL){
		fluxreaction.setAttribute(XMLTags.FluxOptionAttrTag, XMLTags.FluxOptionMolecularAndElectrical);
	}else if (param.getPhysicsOptions() == FluxReaction.PHYSICS_MOLECULAR_ONLY){
		fluxreaction.setAttribute(XMLTags.FluxOptionAttrTag, XMLTags.FluxOptionMolecularOnly);
	}

	//If keyFlag is on print the Keyvalue
	if (param.getKey() !=null && this.printKeysFlag) {
		fluxreaction.setAttribute(XMLTags.KeyValueAttrTag, param.getKey().toString());
	}

	// Add subelements: Reactants/Products/Catalysts
	//separate the order of the reactants, products, and modifiers.
	ReactionParticipant rpArray[] = param.getReactionParticipants();
	ArrayList<Element> products = new ArrayList<Element>();
	ArrayList<Element> modifiers = new ArrayList<Element>();
	for (int i = 0; i < rpArray.length; i++){
		Element rp =  getXML(rpArray[i]);
		if (rp != null) {
			if (rpArray[i] instanceof Reactant)
				fluxreaction.addContent(rp);
			else if (rpArray[i] instanceof Product)
				products.add(rp);
			else if (rpArray[i] instanceof Catalyst)
				modifiers.add(rp);
		}
	}
	for (int i = 0; i < products.size(); i++)
		fluxreaction.addContent((Element)products.get(i));
	for (int i = 0; i < modifiers.size(); i++)
		fluxreaction.addContent((Element)modifiers.get(i));
	
	//Add Kinetics	
	fluxreaction.addContent( getXML(param.getKinetics()) );

	return fluxreaction;
}


/**
 * This method returns a XML representation of a Kinetics object type.
 * Creation date: (2/26/2001 7:31:43 PM)
 * @return Element
 * @param param cbit.vcell.model.Kinetics
 */
private Element getXML(Kinetics param) throws XmlParseException {

	String kineticsType = null;

	if (param instanceof GeneralKinetics) {
		//process a GeneralKinetics object
		kineticsType = XMLTags.KineticsTypeGeneralKinetics;
	} else if (param instanceof MassActionKinetics) {
		//Process a MassActionKinetics
		kineticsType = XMLTags.KineticsTypeMassAction;
	} else if (param instanceof NernstKinetics) {
		//Process a NernstKinetics
		kineticsType = XMLTags.KineticsTypeNernst;
	} else if (param instanceof GHKKinetics) {
		//Process a GHKKinetics
		kineticsType = XMLTags.KineticsTypeGHK;
	} else if (param instanceof GeneralCurrentKinetics) {
		//Process a GeneralCurrentKinetics
		kineticsType = XMLTags.KineticsTypeGeneralCurrentKinetics;
	} else if (param instanceof HMM_IRRKinetics) {
		//Process a HenriMichaelasMentenKinetics (irreversible)
		kineticsType = XMLTags.KineticsTypeHMM_Irr;
	} else if (param instanceof HMM_REVKinetics) {
		//Process a HenriMichaelasMentenKinetics (reversible)
		kineticsType = XMLTags.KineticsTypeHMM_Rev;
	} else if (param instanceof GeneralLumpedKinetics) {
		//Process a GeneralLumpedKinetics
		kineticsType = XMLTags.KineticsTypeGeneralLumped;
	} else if (param instanceof GeneralCurrentLumpedKinetics) {
		//Process a GeneralCurrentLumpedKinetics
		kineticsType = XMLTags.KineticsTypeGeneralCurrentLumped;
	} else if (param instanceof GeneralPermeabilityKinetics) {
		//Process a GeneralPermeabilityKinetics
		kineticsType = XMLTags.KineticsTypeGeneralPermeability;
	} else if (param instanceof Macroscopic_IRRKinetics) {
		//Process a Macroscopic_IRRKinetics
		kineticsType = XMLTags.KineticsTypeMacroscopic_Irr;
	}  else if (param instanceof Microscopic_IRRKinetics) {
		//Process a Microscopic_IRRKinetics
		kineticsType = XMLTags.KineticsTypeMicroscopic_Irr;
	} 
	Element kinetics = new Element(XMLTags.KineticsTag);
	//Add atributes
	kinetics.setAttribute(XMLTags.KineticsTypeAttrTag, kineticsType);
	//Add Kinetics Parameters
	Kinetics.KineticsParameter parameters[] = param.getKineticsParameters();
	for (int i=0;i<parameters.length;i++){
		Kinetics.KineticsParameter parm = parameters[i];
		Element tempparameter = new Element(XMLTags.ParameterTag);
		//Get parameter attributes
		tempparameter.setAttribute(XMLTags.NameAttrTag, mangle(parm.getName()));
		tempparameter.setAttribute(XMLTags.ParamRoleAttrTag, param.getDefaultParameterDesc(parm.getRole()));
		VCUnitDefinition unit = parm.getUnitDefinition();
		if (unit != null) {
			tempparameter.setAttribute(XMLTags.VCUnitDefinitionAttrTag, unit.getSymbol());
		}
		tempparameter.addContent( mangleExpression(parm.getExpression()) );
		//Add the parameter to the general kinetics object
		kinetics.addContent(tempparameter);
	}

	return kinetics;
}

private Element getXML(RbmKineticLaw param) {


	Element kinetics = new Element(XMLTags.KineticsTag);

	switch (param.getRateLawType()){
		case MassAction:{
			kinetics.setAttribute(XMLTags.KineticsTypeAttrTag, XMLTags.RbmKineticTypeMassAction);
			break;
		}
		case MichaelisMenten:{
			kinetics.setAttribute(XMLTags.KineticsTypeAttrTag, XMLTags.RbmKineticTypeMichaelisMenten);
			break;
		}
		case Saturable: {
			kinetics.setAttribute(XMLTags.KineticsTypeAttrTag, XMLTags.RbmKineticTypeSaturable);
			break;
		}
	}
	
	HashMap<ParameterRoleEnum, String> roleHash = new HashMap<ParameterRoleEnum, String>();
	roleHash.put(RbmKineticLawParameterType.MassActionForwardRate, XMLTags.RbmMassActionKfRole);
	roleHash.put(RbmKineticLawParameterType.MassActionReverseRate, XMLTags.RbmMassActionKrRole);
	roleHash.put(RbmKineticLawParameterType.MichaelisMentenKcat, XMLTags.RbmMichaelisMentenKcatRole);
	roleHash.put(RbmKineticLawParameterType.MichaelisMentenKm, XMLTags.RbmMichaelisMentenKmRole);
	roleHash.put(RbmKineticLawParameterType.SaturableVmax, XMLTags.RbmSaturableVmaxRole);
	roleHash.put(RbmKineticLawParameterType.SaturableKs, XMLTags.RbmSaturableKsRole);
	roleHash.put(RbmKineticLawParameterType.RuleRate, XMLTags.RbmRuleRateRole);
	roleHash.put(RbmKineticLawParameterType.UserDefined, XMLTags.RbmUserDefinedRole);

	//Add Kinetics Parameters
	LocalParameter parameters[] = param.getLocalParameters();
	for (int i=0;i<parameters.length;i++){
		LocalParameter parm = parameters[i];
		Element tempparameter = new Element(XMLTags.ParameterTag);
		//Get parameter attributes
		tempparameter.setAttribute(XMLTags.NameAttrTag, mangle(parm.getName()));
		tempparameter.setAttribute(XMLTags.ParamRoleAttrTag, roleHash.get(parm.getRole()));
		VCUnitDefinition unit = parm.getUnitDefinition();
		if (unit != null) {
			tempparameter.setAttribute(XMLTags.VCUnitDefinitionAttrTag, unit.getSymbol());
		}
		Expression expression = parm.getExpression();
		if (expression != null){
			tempparameter.addContent( mangleExpression(expression) );
		}
		//Add the parameter to the general kinetics object
		kinetics.addContent(tempparameter);
	}

	return kinetics;
}


public Element getXML(ArrayList<AnnotatedFunction> outputFunctions) {
	Element outputFunctionsElement = new Element(XMLTags.OutputFunctionsTag);
	for (AnnotatedFunction outputfunction : outputFunctions) {
		Element functionElement = getXML(outputfunction);
		outputFunctionsElement.addContent(functionElement);
	}

	return outputFunctionsElement;
}
public Element getXML(ModelParameter[] modelParams) {
	Element globalsElement = new Element(XMLTags.ModelParametersTag);
	for (int i = 0; i < modelParams.length; i++) {
		Element glParamElement = getXML(modelParams[i]);
		globalsElement.addContent(glParamElement);
	}

	return globalsElement;
}


private Element getXML(ModelParameter modelParam) {
	Element glParamElement = new Element(XMLTags.ParameterTag);
	//Get parameter attributes - name, role and unit definition
	glParamElement.setAttribute(XMLTags.NameAttrTag, mangle(modelParam.getName()));
	if (modelParam.getRole() == Model.ROLE_UserDefined) {
		glParamElement.setAttribute(XMLTags.ParamRoleAttrTag, Model.RoleDesc);
	} else {
		throw new RuntimeException("Unknown model parameter role/type");
	}
//	if (modelParam.getRole() == Model.ROLE_UserDefined) {
//		glParamElement.setAttribute(XMLTags.ParamRoleAttrTag, Model.RoleDescs[0]);
//	} else	if (modelParam.getRole() == Model.ROLE_VariableRate) {
//		glParamElement.setAttribute(XMLTags.ParamRoleAttrTag, Model.RoleDescs[1]);
//	} else {
//		throw new RuntimeException("Unknown model parameter role/type");
//	}
	VCUnitDefinition unit = modelParam.getUnitDefinition();
	if (unit != null) {
		glParamElement.setAttribute(XMLTags.VCUnitDefinitionAttrTag, unit.getSymbol());
	}
	// add expression as content
	glParamElement.addContent(mangleExpression(modelParam.getExpression()) );
	//add annotation (if there is any)
	if (modelParam.getModelParameterAnnotation() != null &&
			modelParam.getModelParameterAnnotation().length() > 0) {
		Element annotationElement = new Element(XMLTags.AnnotationTag);
		annotationElement.setText(mangle(modelParam.getModelParameterAnnotation()));
		glParamElement.addContent(annotationElement);
	}
	return glParamElement;
}

public Element getXML(SimulationContextParameter[] appParams) {
	Element appParamsElement = new Element(XMLTags.ApplicationParametersTag);
	for (int i = 0; i < appParams.length; i++) {
		Element appParamElement = getXML(appParams[i]);
		appParamsElement.addContent(appParamElement);
	}

	return appParamsElement;
}

private Element getXML(SimulationContextParameter appParam) {
	Element appParamElement = new Element(XMLTags.ParameterTag);
	//Get parameter attributes - name, role and unit definition
	appParamElement.setAttribute(XMLTags.NameAttrTag, mangle(appParam.getName()));
	if (appParam.getRole() == SimulationContext.ROLE_UserDefined) {
		appParamElement.setAttribute(XMLTags.ParamRoleAttrTag, SimulationContext.RoleDesc);
	} else {
		throw new RuntimeException("Unknown application parameter role/type");
	}
	VCUnitDefinition unit = appParam.getUnitDefinition();
	if (unit != null) {
		appParamElement.setAttribute(XMLTags.VCUnitDefinitionAttrTag, unit.getSymbol());
	}
	// add expression as content
	appParamElement.addContent(mangleExpression(appParam.getExpression()) );
	//add annotation (if there is any)
//	if (modelParam.getModelParameterAnnotation() != null &&
//			modelParam.getModelParameterAnnotation().length() > 0) {
//		Element annotationElement = new Element(XMLTags.AnnotationTag);
//		annotationElement.setText(mangle(modelParam.getModelParameterAnnotation()));
//		glParamElement.addContent(annotationElement);
//	}
	return appParamElement;
}
// ============================================================================================================
public Element getXML(RbmModelContainer rbmModelContainer) {
	Element rbmModelContainerElement = new Element(XMLTags.RbmModelContainerTag);
	List<MolecularType> molecularTypeList = rbmModelContainer.getMolecularTypeList();
	if(!molecularTypeList.isEmpty()) {
		Element molecularTypeListElement = new Element(XMLTags.RbmMolecularTypeListTag);
		for (MolecularType mt : molecularTypeList) {
			molecularTypeListElement.addContent(getXML(mt));
		}
		rbmModelContainerElement.addContent(molecularTypeListElement);
	}
	List<RbmObservable> observablesList = rbmModelContainer.getObservableList();
	if(!observablesList.isEmpty()) {
		Element observablesListElement = new Element(XMLTags.RbmObservableListTag);
		for (RbmObservable oo : observablesList) {
			observablesListElement.addContent(getXML(oo));
		}
		rbmModelContainerElement.addContent(observablesListElement);
	}
	List<ReactionRule> reactionList = rbmModelContainer.getReactionRuleList();
	if(!reactionList.isEmpty()) {
		Element reactionListElement = new Element(XMLTags.RbmReactionRuleListTag);
		for (ReactionRule rr : reactionList) {
			reactionListElement.addContent(getXML(rr));
		}
		rbmModelContainerElement.addContent(reactionListElement);
	}
	return rbmModelContainerElement;
}
// public because it's being called in simcontexttable to populate the app components element
public Element getXML(NetworkConstraints param) {
	Element e = new Element(XMLTags.RbmNetworkConstraintsTag);
	e.setAttribute(XMLTags.RbmMaxIterationTag, Integer.toString(param.getMaxIteration()));
	e.setAttribute(XMLTags.RbmMaxMoleculesPerSpeciesTag, Integer.toString(param.getMaxMoleculesPerSpecies()));
	Map<MolecularType, Integer> sm = param.getMaxStoichiometry();
	for (Map.Entry<MolecularType, Integer> m : sm.entrySet()) {
		MolecularType mt = m.getKey();
		Integer value = m.getValue();
		Element e1 = new Element(XMLTags.RbmMaxStoichiometryTag);
		e1.setAttribute(XMLTags.RbmMolecularTypeTag, mangle(mt.getName()));
		e1.setAttribute(XMLTags.RbmIntegerAttrTag, Integer.toString(value));
		e.addContent(e1);
	}
	return e;
}
private Element getXML(ReactionRule param) {
	Element e = new Element(XMLTags.RbmReactionRuleTag);
	e.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	e.setAttribute(XMLTags.StructureAttrTag, mangle(param.getStructure().getName()));
	e.setAttribute(XMLTags.RbmReactionRuleLabelTag, mangle(param.getName()));
	boolean reversible = param.isReversible();
	e.setAttribute(XMLTags.RbmReactionRuleReversibleTag, String.valueOf(reversible));
//	if (param.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.MassActionForwardRate)!=null){
//		e.setAttribute(XMLTags.RbmMassActionKfTag, mangleExpression(param.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.MassActionForwardRate)));
//	}
//	if (param.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.MassActionReverseRate)!=null){
//		e.setAttribute(XMLTags.RbmMassActionKrTag, mangleExpression(param.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.MassActionReverseRate)));
//	}
//	if (param.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.MichaelisMentenKcat)!=null){
//		e.setAttribute(XMLTags.RbmMichaelisMentenKcatTag, mangleExpression(param.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.MichaelisMentenKcat)));
//	}
//	if (param.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.MichaelisMentenKm)!=null){
//		e.setAttribute(XMLTags.RbmMichaelisMentenKmTag, mangleExpression(param.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.MichaelisMentenKm)));
//	}
//	if (param.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.SaturableKs)!=null){
//		e.setAttribute(XMLTags.RbmSaturableKsTag, mangleExpression(param.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.SaturableKs)));
//	}
//	if (param.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.SaturableVmax)!=null){
//		e.setAttribute(XMLTags.RbmSaturableVmaxTag, mangleExpression(param.getKineticLaw().getLocalParameterValue(RbmKineticLawParameterType.SaturableVmax)));
//	}
	List<ReactantPattern> reactantPatterns = param.getReactantPatterns();
	if(!reactantPatterns.isEmpty()) {
		Element reactantPatternsListElement = new Element(XMLTags.RbmReactantPatternsListTag);
		for (ReactantPattern rp : reactantPatterns) {
			reactantPatternsListElement.addContent(getXML(rp));
		}
		e.addContent(reactantPatternsListElement);
	}
	List<ProductPattern> productPatterns = param.getProductPatterns();
	if(!productPatterns.isEmpty()) {
		Element productPatternsListElement = new Element(XMLTags.RbmProductPatternsListTag);
		for (ProductPattern pp : productPatterns) {
			productPatternsListElement.addContent(getXML(pp));
		}
		e.addContent(productPatternsListElement);
	}
	// apparently the molecularTypeMapping list is not being used
	
	//Add kinetics		
	e.addContent( getXML(param.getKineticLaw()) );
	return e;
}
private Element getXML(ReactantPattern param) {
	Element e = new Element(XMLTags.RbmReactantPatternTag);
	e.setAttribute(XMLTags.StructureAttrTag, mangle(param.getStructure().getName()));
	e.addContent(getXML(param.getSpeciesPattern()));
	return e;
}
private Element getXML(ProductPattern param) {
	Element e = new Element(XMLTags.RbmProductPatternTag);
	e.setAttribute(XMLTags.StructureAttrTag, mangle(param.getStructure().getName()));
	e.addContent(getXML(param.getSpeciesPattern()));
	return e;
}

//private Element getXMLShort(SpeciesPattern param) {
//	Element e = new Element(XMLTags.RbmSpeciesPatternTag);
//	e.setAttribute(XMLTags.NameAttrTag, mangle(param.getId()));
//	return e;
//}
private Element getXML(RbmObservable param) {
	// if RbmObservableEmbedded we don't save the model or the structure
	// we know which they are once we use the XmlReader to recreate the object
	Element e = new Element(XMLTags.RbmObservableTag);
	e.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	RbmObservable.ObservableType ot = param.getType();
	e.setAttribute(XMLTags.RbmObservableTypeTag, mangle(ot.name()));
	
	String sequenceName = param.getSequence().name();
	e.setAttribute(XMLTags.RbmObservableSequenceAttrTag, sequenceName);
	e.setAttribute(XMLTags.RbmObservableLenEqualAttrTag, ""+param.getSequenceLength(RbmObservable.Sequence.PolymerLengthEqual));
	e.setAttribute(XMLTags.RbmObservableLenGreaterAttrTag, ""+param.getSequenceLength(RbmObservable.Sequence.PolymerLengthGreater));
	
	e.setAttribute(XMLTags.StructureAttrTag, mangle(param.getStructure().getName()));
//	SpeciesPattern sp = param.getSpeciesPattern(0);
//	Element e1 = new Element(XMLTags.RbmSpeciesPatternTag);
//	e1.setAttribute(XMLTags.NameAttrTag, mangle(sp.getId()));
//	e.addContent(getXML(sp));
	List<SpeciesPattern> spl = param.getSpeciesPatternList();
	for(SpeciesPattern sp : spl) {
		e.addContent(getXML(sp));
	}
	return e;
}
//private Element getXML(SeedSpecies param) {
//	Element e = new Element(XMLTags.RbmSeedSpeciesTag);
////	e.setAttribute(XMLTags.NameAttrTag, mangle(param.getId()));
//	e.setAttribute(XMLTags.RbmInitialConditionTag, mangleExpression(param.getInitialCondition()));
//	SpeciesPattern sp = param.getSpeciesPattern();
//	e.addContent(getXML(sp));
//	return e;	
//}
private Element getXML(SpeciesPattern param) {
	Element e = new Element(XMLTags.RbmSpeciesPatternTag);
//	e.setAttribute(XMLTags.NameAttrTag, mangle(param.getId()));
	for(MolecularTypePattern mp : param.getMolecularTypePatterns()) {
		e.addContent(getXML(mp));
	}
	return e;
}
private Element getXML(MolecularTypePattern param) {
	Element e = new Element(XMLTags.RbmMolecularTypePatternTag);
	if(param.hasExplicitParticipantMatch()) {
		e.setAttribute(XMLTags.RbmParticipantPatternMatchTag, mangle(param.getParticipantMatchLabel()));
	}
	e.setAttribute(XMLTags.RbmIndexAttrTag, Integer.toString(param.getIndex()));
	Element e1 = new Element(XMLTags.RbmMolecularTypeTag);
	MolecularType mt = param.getMolecularType();
	e1.setAttribute(XMLTags.NameAttrTag, mangle(mt.getName()));
	e.addContent(e1);
	for(MolecularComponentPattern cp : param.getComponentPatternList()) {
		e.addContent(getXML(cp));
	}
	return e;
}
private Element getXML(MolecularComponentPattern param) {
	Element e = new Element(XMLTags.RbmMolecularComponentPatternTag);
	//e.setAttribute(XMLTags.NameAttrTag, mangle(param.getId()));
	e.setAttribute(XMLTags.RbmMolecularComponentTag, mangle(param.getMolecularComponent().getName()));
	ComponentStatePattern cs = param.getComponentStatePattern();
	if(cs != null) {
		if(cs.isAny()) {
			e.setAttribute(XMLTags.RbmMolecularTypeAnyTag, String.valueOf(cs.isAny()));
		} else {
			if(cs.getComponentStateDefinition() != null) {
				e.setAttribute(XMLTags.RbmMolecularComponentStatePatternTag, mangle(cs.getComponentStateDefinition().getName()));
			} else {
				System.err.println(ComponentStateDefinition.typeName + " is missing!");
			}
		}
	}
	Bond b = param.getBond();
	if(b != null) {
		Element e1 = new Element(XMLTags.RbmBondTag);
		String test = mangle(b.molecularTypePattern.getMolecularType().getName());
		e1.setAttribute(XMLTags.RbmMolecularTypePatternTag, test);
		e1.setAttribute(XMLTags.RbmMolecularComponentPatternTag, mangle(b.molecularComponentPattern.getMolecularComponent().getName()));
		e.addContent(e1);
	}
	BondType bt = param.getBondType();
	if(bt == null) {
		return e;
	}
	if(bt.equals(BondType.Specified)) {
		String s = Integer.toString(param.getBondId());
		e.setAttribute(XMLTags.RbmBondTypeAttrTag, s);
	} else {
		String s = bt.symbol;
		e.setAttribute(XMLTags.RbmBondTypeAttrTag, s);
	}
	return e;
}
private Element getXML(MolecularType param) {
	Element e  = new Element(XMLTags.RbmMolecularTypeTag);
	e.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	
	boolean anchorAll = param.isAnchorAll();
	e.setAttribute(XMLTags.RbmMolecularTypeAnchorAllAttrTag, String.valueOf(anchorAll));
	for (Structure ss : param.getAnchors()){
		Element es = new Element(XMLTags.RbmMolecularTypeAnchorTag);
		es.setAttribute(XMLTags.StructureAttrTag, mangle(ss.getName()));
		e.addContent(es);
	}
	for (MolecularComponent pp : param.getComponentList()){
		e.addContent(getXML(pp));
	}
	return e;
}
private Element getXML(MolecularComponent param) {
	Element e = new Element(XMLTags.RbmMolecularComponentTag);
	e.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	String s = Integer.toString(param.getIndex());
	e.setAttribute(XMLTags.RbmIndexAttrTag, s);
	for (ComponentStateDefinition pp : param.getComponentStateDefinitions()){
		e.addContent(getXML(pp));
	}
	return e;
}
private Element getXML(ComponentStateDefinition param) {
	Element e = new Element(XMLTags.RbmMolecularTypeAllowableStateTag);
	e.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	return e;
}


/**
 * Outputs a XML version of a Model object
 * Creation date: (2/15/2001 11:39:27 AM)
 * @return Element
 * @param param cbit.vcell.model.Model
 */
private Element getXML(Model param) throws XmlParseException/*, cbit.vcell.parser.ExpressionException */{
	Element modelnode = new Element(XMLTags.ModelTag);
	String versionName = (param.getName()!=null)?mangle(param.getName()):"unnamed_model";

	//get Attributes
	modelnode.setAttribute(XMLTags.NameAttrTag, versionName);
	//modelnode.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(param.getDescription()));
	if (param.getDescription()!=null && param.getDescription().length()>0) {
		Element annotationElem = new Element(XMLTags.AnnotationTag);
		annotationElem.setText(mangle(param.getDescription()));
		modelnode.addContent(annotationElem);
	}

	// get global parameters
	ModelParameter[] modelGlobals = param.getModelParameters();
	if (modelGlobals != null && modelGlobals.length > 0) {
		modelnode.addContent(getXML(modelGlobals));
	}

	//Get Species
	Species[] array = param.getSpecies();
	for (int i=0 ; i<array.length ; i++){
		modelnode.addContent( getXML(array[i]) );
	}
	//Get Structures(Features and Membranes). Add them in an ordered fashion, but it does not matter who comes first.
	try {
		ArrayList<Element> list = new ArrayList<Element>();
		Structure[] structarray = param.getStructures();
		for (int i=0 ; i < structarray.length ; i++){
			Element structure = getXML(structarray[i], param);
			if (structarray[i] instanceof Feature)
				modelnode.addContent(structure);
			else
				list.add(structure);
		}
		for (int i = 0; i < list.size(); i++) {
			modelnode.addContent((Element)list.get(i));
		}
	} catch (XmlParseException e) {
		e.printStackTrace();
		throw new XmlParseException("An error occurred while procesing a Structure for the model " + versionName, e);
	}
	//Process SpeciesContexts
	SpeciesContext[] specarray = param.getSpeciesContexts();
	for (int i=0 ; i < specarray.length ; i++) {
		modelnode.addContent( getXML(specarray[i]) );
	}
	//Get reaction Steps(Simple Reactions and Fluxtep)
	ReactionStep[] reactarray = param.getReactionSteps();
	for (int i=0 ; i < reactarray.length ; i++ ){
		modelnode.addContent( getXML(reactarray[i]) );
	}
	
	// add the rbmModelContainer elements
	RbmModelContainer rbmModelContainer = param.getRbmModelContainer();
	if(rbmModelContainer != null && !rbmModelContainer.isEmpty()) {
		Element rbmModelContainerElement = getXML(rbmModelContainer);

		{	// for testing purposes only
			Document doc = new Document();
			Element clone = (Element)rbmModelContainerElement.clone();
			doc.setRootElement(clone);
			String xmlString = XmlUtil.xmlToString(doc, false);
			System.out.println(xmlString);
		}
		modelnode.addContent(rbmModelContainerElement);
	}
	
//	// Add rate rules
//	if (param.getRateRuleVariables()!=null && param.getRateRuleVariables().length>0){
//		modelnode.addContent( getXML(param.getRateRuleVariables()) );
//	}

	//Get Diagrams
	Diagram[] diagarray = param.getDiagrams();
	for (int i=0 ; i<diagarray.length ; i++){
		modelnode.addContent( getXML(diagarray[i]) );
	}
	//Add Metadata information
	if (param.getVersion()!= null) {
		modelnode.addContent( getXML(param.getVersion(), param) );
	}
	
	// add model UnitSystem
	ModelUnitSystem unitSystem = param.getUnitSystem();
	if (unitSystem != null) {
		modelnode.addContent(getXML(unitSystem));
	}
	return modelnode;
}


public Element getXML(ModelUnitSystem unitSystem) {
	Element unitSystemNode = new Element(XMLTags.ModelUnitSystemTag);
	unitSystemNode.setAttribute(XMLTags.VolumeSubstanceUnitTag, mangle(unitSystem.getVolumeSubstanceUnit().getSymbol()));
	unitSystemNode.setAttribute(XMLTags.MembraneSubstanceUnitTag, mangle(unitSystem.getMembraneSubstanceUnit().getSymbol()));
	unitSystemNode.setAttribute(XMLTags.LumpedReactionSubstanceUnitTag, mangle(unitSystem.getLumpedReactionSubstanceUnit().getSymbol()));
	unitSystemNode.setAttribute(XMLTags.VolumeUnitTag, mangle(unitSystem.getVolumeUnit().getSymbol()));
	unitSystemNode.setAttribute(XMLTags.AreaUnitTag, mangle(unitSystem.getAreaUnit().getSymbol()));
	unitSystemNode.setAttribute(XMLTags.LengthUnitTag, mangle(unitSystem.getLengthUnit().getSymbol()));
	unitSystemNode.setAttribute(XMLTags.TimeUnitTag, mangle(unitSystem.getTimeUnit().getSymbol()));
	
	return unitSystemNode;
}

/**
 * This method returns the XML representation of a NodeReference type object.
 * Creation date: (2/27/2001 5:32:32 PM)
 * @return Element
 * @param param cbit.vcell.model.NodeReference
 */
private Element getXML(NodeReference param) {
	switch (param.nodeType){
		case NodeReference.SIMPLE_REACTION_NODE:{
			Element simplereaction = new Element(XMLTags.SimpleReactionShapeTag);
			//add Attributes
			simplereaction.setAttribute(XMLTags.SimpleReactionRefAttrTag, mangle(param.getName()));
			simplereaction.setAttribute(XMLTags.LocationXAttrTag, String.valueOf(param.location.x));
			simplereaction.setAttribute(XMLTags.LocationYAttrTag, String.valueOf(param.location.y));

			return simplereaction;
		}
		case NodeReference.FLUX_REACTION_NODE:{
			Element fluxreaction = new Element(XMLTags.FluxReactionShapeTag);
			//add Attributes
			fluxreaction.setAttribute(XMLTags.FluxReactionRefAttrTag, mangle(param.getName()));
			fluxreaction.setAttribute(XMLTags.LocationXAttrTag, String.valueOf(param.location.x));
			fluxreaction.setAttribute(XMLTags.LocationYAttrTag, String.valueOf(param.location.y));

			return fluxreaction;
		}
		case NodeReference.SPECIES_CONTEXT_NODE:{
			Element speciecontext = new Element(XMLTags.SpeciesContextShapeTag);
			//add Attributes
			speciecontext.setAttribute(XMLTags.SpeciesContextRefAttrTag, mangle(param.getName()));
			speciecontext.setAttribute(XMLTags.LocationXAttrTag, String.valueOf(param.location.x));
			speciecontext.setAttribute(XMLTags.LocationYAttrTag, String.valueOf(param.location.y));

			return speciecontext;
		}
		case NodeReference.REACTION_RULE_NODE:{
			Element reactionRuleNode = new Element(XMLTags.ReactionRuleShapeTag);
			//add Attributes
			reactionRuleNode.setAttribute(XMLTags.ReactionRuleRef2AttrTag, mangle(param.getName()));
			reactionRuleNode.setAttribute(XMLTags.LocationXAttrTag, String.valueOf(param.location.x));
			reactionRuleNode.setAttribute(XMLTags.LocationYAttrTag, String.valueOf(param.location.y));

			return reactionRuleNode;
		}
		case NodeReference.RULE_PARTICIPANT_SIGNATURE_FULL_NODE: {
			Element ruleParticipant = new Element(XMLTags.RuleParticipantFullShapeTag);
			//add Attributes
			ruleParticipant.setAttribute(XMLTags.RuleParticipantRefAttrTag, mangle(param.getName()));
			ruleParticipant.setAttribute(XMLTags.LocationXAttrTag, String.valueOf(param.location.x));
			ruleParticipant.setAttribute(XMLTags.LocationYAttrTag, String.valueOf(param.location.y));

			return ruleParticipant;
		}
		case NodeReference.RULE_PARTICIPANT_SIGNATURE_SHORT_NODE: {
			Element ruleParticipant = new Element(XMLTags.RuleParticipantShortShapeTag);
			//add Attributes
			ruleParticipant.setAttribute(XMLTags.RuleParticipantRefAttrTag, mangle(param.getName()));
			ruleParticipant.setAttribute(XMLTags.LocationXAttrTag, String.valueOf(param.location.x));
			ruleParticipant.setAttribute(XMLTags.LocationYAttrTag, String.valueOf(param.location.y));

			return ruleParticipant;
		}
	}
	return null;
}


/**
 * this method returns a XML representation of a Product object.
 * Creation date: (2/27/2001 3:00:56 PM)
 * @return Element
 * @param param cbit.vcell.model.Product
 */
private Element getXML(Product param) {
	Element product = new Element(XMLTags.ProductTag);
	//Add attributes
	product.setAttribute(XMLTags.SpeciesContextRefAttrTag, mangle(param.getSpeciesContext().getName()));
	product.setAttribute(XMLTags.StoichiometryAttrTag, String.valueOf(param.getStoichiometry()));

	//If keyFlag is on print the Keyvalue
	if (param.getKey() !=null && this.printKeysFlag) {
		product.setAttribute(XMLTags.KeyValueAttrTag, param.getKey().toString());
	}
		
	return product;
}


/**
 * This method returns a XML representation of a Reactant object.
 * Creation date: (2/27/2001 2:54:47 PM)
 * @return Element
 * @param param cbit.vcell.model.Reactant
 */
private Element getXML(Reactant param) {
	Element reactant = new Element(XMLTags.ReactantTag);
	//Add attributes
	reactant.setAttribute(XMLTags.SpeciesContextRefAttrTag, mangle(param.getSpeciesContext().getName()));
	reactant.setAttribute(XMLTags.StoichiometryAttrTag, String.valueOf(param.getStoichiometry()));

	//If keyFlag is on print the Keyvalue
	if (param.getKey() !=null && this.printKeysFlag) {
		reactant.setAttribute(XMLTags.KeyValueAttrTag, param.getKey().toString());
	}
	
	return reactant;
}


/**
 * This method returns a XML representation of a ReactionParticipant object.
 * Creation date: (2/27/2001 2:41:45 PM)
 * @return Element
 * @param param cbit.vcell.model.ReactionParticipant
 */
private Element getXML(ReactionParticipant param) {
	if (param instanceof Reactant) {
		//Process a reactant
		return getXML((Reactant)param);
	} else if (param instanceof Product){
		//Process a Product
		return getXML((Product)param);
	} else if (param instanceof Catalyst){
		return getXML((Catalyst)param);
	}
	
	return null;
}


/**
 * This method returns a XML representation of a Reaction Step.
 * Creation date: (2/26/2001 12:12:55 PM)
 * @return Element
 * @param param cbit.vcell.model.ReactionStep
 */
private Element getXML(ReactionStep param) throws XmlParseException {
	Element rsElement = null;
	if (param instanceof FluxReaction) {
		rsElement = getXML((FluxReaction)param);
	} else if (param instanceof SimpleReaction) {
		rsElement = getXML((SimpleReaction)param);
	}
	return rsElement;
}


/**
 * This method returns the XML represntation of a Simple reaction object.
 * Creation date: (2/27/2001 2:27:28 PM)
 * @return Element
 * @param param cbit.vcell.model.SimpleReaction
 */
private Element getXML(SimpleReaction param) throws XmlParseException {
	Element simplereaction = new Element(XMLTags.SimpleReactionTag);
	//Add attribute
	String nameStr = (param.getName()!=null)?(mangle(param.getName())):"unnamed_SimpleReaction";
	simplereaction.setAttribute(XMLTags.StructureAttrTag, mangle(param.getStructure().getName()));
	simplereaction.setAttribute(XMLTags.NameAttrTag, nameStr);
	simplereaction.setAttribute(XMLTags.ReversibleAttrTag, mangle(Boolean.valueOf(param.isReversible()).toString()));

	if (param.getPhysicsOptions() == SimpleReaction.PHYSICS_ELECTRICAL_ONLY){
		simplereaction.setAttribute(XMLTags.FluxOptionAttrTag, XMLTags.FluxOptionElectricalOnly);
	}else if (param.getPhysicsOptions() == SimpleReaction.PHYSICS_MOLECULAR_AND_ELECTRICAL){
		simplereaction.setAttribute(XMLTags.FluxOptionAttrTag, XMLTags.FluxOptionMolecularAndElectrical);
	}else if (param.getPhysicsOptions() == SimpleReaction.PHYSICS_MOLECULAR_ONLY){
		simplereaction.setAttribute(XMLTags.FluxOptionAttrTag, XMLTags.FluxOptionMolecularOnly);
	}

	//If keyFlag is on print the Keyvalue
	if (param.getKey() !=null && this.printKeysFlag) {
		simplereaction.setAttribute(XMLTags.KeyValueAttrTag, param.getKey().toString());
	}
	

	// Add subelements: Reactants/Products/Catalysts
	//separate the order of the reactants, products, and modifiers.
	ReactionParticipant rpArray[] = param.getReactionParticipants();
	ArrayList<Element> products = new ArrayList<Element>();
	ArrayList<Element> modifiers = new ArrayList<Element>();
	for (int i = 0; i < rpArray.length; i++){
		Element rp =  getXML(rpArray[i]);
		if (rp != null) {
			if (rpArray[i] instanceof Reactant)
				simplereaction.addContent(rp);
			else if (rpArray[i] instanceof Product)
				products.add(rp);
			else if (rpArray[i] instanceof Catalyst)
				modifiers.add(rp);
		}
	}
	for (int i = 0; i < products.size(); i++)
		simplereaction.addContent((Element)products.get(i));
	for (int i = 0; i < modifiers.size(); i++)
		simplereaction.addContent((Element)modifiers.get(i));
	//Add kinetics		
	simplereaction.addContent( getXML(param.getKinetics()) );
	
	return simplereaction;
}


/**
 * This method returns a XML version of a Specie.
 * Creation date: (2/22/2001 2:37:45 PM)
 * @return Element
 * @param param cbit.vcell.model.Species
 */
private Element getXML(Species species) throws XmlParseException {
	Element speciesElement = new Element(XMLTags.SpeciesTag);

	//add name
	speciesElement.setAttribute(XMLTags.NameAttrTag, mangle(species.getCommonName()) );
	
	//add annotation (if there is any)
	if (species.getAnnotation()!=null && species.getAnnotation().length()!=0) {
		//speciesElement.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(species.getAnnotation()) );
		Element annotationElem = new Element(XMLTags.AnnotationTag);
		annotationElem.setText(mangle(species.getAnnotation()));
		speciesElement.addContent(annotationElem);
	}
	
	//add DBSpecies
	if (species.getDBSpecies()!=null) {
		speciesElement.addContent( getXML(species.getDBSpecies()) );
	}

	return speciesElement;
}



/**
 * This method returns a XML representation of a SpeciesContext object.
 * Creation date: (2/26/2001 11:26:37 AM)
 * @return Element
 * @param param cbit.vcell.model.SpeciesContext
 */
private Element getXML(SpeciesContext param) {
	Element speciecontext = new Element( XMLTags.SpeciesContextTag);
	//Add atributes
	speciecontext.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	speciecontext.setAttribute(XMLTags.SpeciesRefAttrTag, mangle(param.getSpecies().getCommonName()));
	speciecontext.setAttribute( XMLTags.StructureAttrTag, mangle(param.getStructure().getName()) );
	speciecontext.setAttribute( XMLTags.HasOverrideAttrTag, true + "" );

	//If keyFlag is on print the Keyvalue
	if (param.getKey() !=null && this.printKeysFlag) {
		speciecontext.setAttribute(XMLTags.KeyValueAttrTag, param.getKey().toString());
	}
	SpeciesPattern sp = param.getSpeciesPattern();
	if(sp != null) {
		speciecontext.addContent(getXML(sp));
	}
		
	return speciecontext;
}

/**
 * This method identifies if the structure as a parameter is a Feature or a Membrane, and then calls the respective getXML method.
 * Creation date: (2/22/2001 6:31:04 PM)
 * @return Element
 * @param param cbit.vcell.model.Structure
 * @param model cbit.vcell.model.Model
 */
private Element getXML(Structure structure, Model model) throws XmlParseException {
	Element structureElement = null;
	StructureTopology structTopology = model.getStructureTopology();
    if (structure instanceof Feature) {
        //This is a Feature
        structureElement = new Element(XMLTags.FeatureTag);
    } else if (structure instanceof Membrane) {
	    //process a Membrane
	    structureElement = new Element(XMLTags.MembraneTag);
	    //add specific attributes
		Feature insideFeature = structTopology.getInsideFeature((Membrane)structure);
		if (insideFeature != null) {
			structureElement.setAttribute(XMLTags.InsideFeatureTag, mangle(insideFeature.getName()));
		}
		Feature outsideFeature = structTopology.getOutsideFeature((Membrane)structure);
		if (outsideFeature != null) {
			structureElement.setAttribute(XMLTags.OutsideFeatureTag, mangle(outsideFeature.getName()));
		}
		// positive & negative features for electrical topology
		ElectricalTopology electricalTopology = model.getElectricalTopology();
		Feature positiveFeature = electricalTopology.getPositiveFeature((Membrane)structure);
		if (positiveFeature != null) {
			structureElement.setAttribute(XMLTags.PositiveFeatureTag, mangle(positiveFeature.getName()));
		}
		Feature negativeFeature = electricalTopology.getNegativeFeature((Membrane)structure);
		if (negativeFeature != null) {
			structureElement.setAttribute(XMLTags.NegativeFeatureTag, mangle(negativeFeature.getName()));
		}

		structureElement.setAttribute(XMLTags.MemVoltNameTag, mangle(((Membrane)structure).getMembraneVoltage().getName()));
    } else {
	    throw new XmlParseException("An unknown type of structure was found:"+structure.getClass().getName());
    }
    
    //add attributes
    structureElement.setAttribute(XMLTags.NameAttrTag, mangle(structure.getName()));
    
	//If the keyFlag is on, print Keys
	if (structure.getKey() != null && this.printKeysFlag) {
		structureElement.setAttribute(XMLTags.KeyValueAttrTag, structure.getKey().toString());
	}

	return structureElement;
}


/**
 * This method returns a XML version of a GroupAccess object.
 * Creation date: (5/23/2003 3:53:50 PM)
 * @return Element
 * @param groupAccess cbit.vcell.server.GroupAccess
 */
private Element getXML(GroupAccess groupAccess) {
	Element groupElement = new Element(XMLTags.GroupAccessTag);
	
	if (groupAccess instanceof GroupAccessAll) {
		//case: ALL
		groupElement.setAttribute(XMLTags.TypeAttrTag, GroupAccess.GROUPACCESS_ALL.toString());		
	} else if (groupAccess instanceof org.vcell.util.document.GroupAccessNone) {
		//case: NONE
		groupElement.setAttribute(XMLTags.TypeAttrTag, GroupAccess.GROUPACCESS_NONE.toString());		
	} else {
		//case: SOME
		//*groupid
		groupElement.setAttribute(XMLTags.TypeAttrTag, groupAccess.getGroupid().toString());
		//*hash
		groupElement.setAttribute(XMLTags.HashAttrTag, ((GroupAccessSome)groupAccess).getHash().toString());
		//*users+hidden value
		//get normal users
		org.vcell.util.document.User[] users = ((GroupAccessSome)groupAccess).getNormalGroupMembers();
		for (int i = 0; i < users.length; i++){
			Element userElement = new Element(XMLTags.UserTag);
			//add name
			userElement.setAttribute(XMLTags.NameAttrTag, mangle(users[i].getName()));
			//add key
			userElement.setAttribute(XMLTags.KeyValueAttrTag, users[i].getID().toString());
			//isHidden property
			userElement.setAttribute(XMLTags.HiddenTag, "false");
			//
			groupElement.addContent(userElement);
		}
		
		//get hidden users
		users = ((GroupAccessSome)groupAccess).getHiddenGroupMembers();
		
		if (users != null) {
			for (int i = 0; i < users.length; i++){
				Element userElement = new Element(XMLTags.UserTag);
				//add name
				userElement.setAttribute(XMLTags.NameAttrTag, mangle(users[i].getName()));
				//add key
				userElement.setAttribute(XMLTags.KeyValueAttrTag, users[i].getID().toString());
				//isHidden property
				userElement.setAttribute(XMLTags.HiddenTag, "true");
			}
		}
	}
	
	return groupElement;
}


/**
 * This methos returns a XML representation of a ErrorTolerance.
 * Creation date: (3/3/2001 12:02:33 AM)
 * @return Element
 * @param param cbit.vcell.solver.ErrorTolerance
 */
private Element getXML(ErrorTolerance param) {
	Element errortol = new Element(XMLTags.ErrorToleranceTag);

	//Add Atributes
	errortol.setAttribute(XMLTags.AbsolutErrorToleranceTag, String.valueOf(param.getAbsoluteErrorTolerance()));
	errortol.setAttribute(XMLTags.RelativeErrorToleranceTag, String.valueOf(param.getRelativeErrorTolerance()));
		
	return errortol;
}


/**
 * returns a XML representation of a MathOverrides.
 * Creation date: (3/3/2001 12:15:17 AM)
 * @return Element
 * @param param cbit.vcell.solver.MathOverrides
 */
private Element getXML(MathOverrides param) {
	Element overrides = new Element(XMLTags.MathOverridesTag);

	//Add Constant subelements
	//Get names
	String[] constantNames = param.getOverridenConstantNames();
	//get the expressions
	for (int i = 0; i < constantNames.length; i++){
		Element constant = new Element(XMLTags.ConstantTag);
		constant.setAttribute( XMLTags.NameAttrTag, mangle(constantNames[i]) );
		if (param.isScan(constantNames[i])) {
			cbit.vcell.solver.ConstantArraySpec cas = param.getConstantArraySpec(constantNames[i]);
			constant.setAttribute(XMLTags.ConstantArraySpec, Integer.toString(cas.getType()));
			constant.addContent(mangle(cas.toString()));
		} else {
			constant.addContent(mangleExpression(param.getActualExpression(constantNames[i], 0)) );
		}
		//and add it to the mathOverrides Element
		overrides.addContent( constant );
	}
		
	return overrides;
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/2001 12:24:59 AM)
 * @return Element
 * @param param cbit.vcell.mesh.MeshSpecification
 */
private Element getXML(MeshSpecification param) {
	Element meshspec = new Element(XMLTags.MeshSpecTag);

	Element size = new Element(XMLTags.SizeTag);
	//Add extent attributes
	ISize sampling = param.getSamplingSize();

	size.setAttribute(XMLTags.XAttrTag, String.valueOf(sampling.getX()));
	size.setAttribute(XMLTags.YAttrTag, String.valueOf(sampling.getY()));
	size.setAttribute(XMLTags.ZAttrTag, String.valueOf(sampling.getZ()));
	meshspec.addContent(size);

	return meshspec;
}


/**
 * This methos returns a XML representation of a ErrorTolerance.
 * Creation date: (3/3/2001 12:02:33 AM)
 * @return Element
 * @param param cbit.vcell.solver.ErrorTolerance
 */
private Element getXML(OutputTimeSpec param) {
	Element outputOptions = new Element(XMLTags.OutputOptionsTag);

	//Add Atributes
	if (param.isDefault()){
		DefaultOutputTimeSpec dots = (DefaultOutputTimeSpec)param;
		outputOptions.setAttribute(XMLTags.KeepEveryAttrTag, String.valueOf(dots.getKeepEvery()));
		outputOptions.setAttribute(XMLTags.KeepAtMostAttrTag, String.valueOf(dots.getKeepAtMost()));
	}else if (param.isExplicit()){
		ExplicitOutputTimeSpec eots = (ExplicitOutputTimeSpec)param;
		outputOptions.setAttribute(XMLTags.OutputTimesAttrTag, eots.toCommaSeperatedOneLineOfString());
	}else if (param.isUniform()){
		UniformOutputTimeSpec uots = (UniformOutputTimeSpec)param;
		outputOptions.setAttribute(XMLTags.OutputTimeStepAttrTag, String.valueOf(uots.getOutputTimeStep()));
	}
		
	return outputOptions;
}

/**
 * This method returns a XML representation of a stochSimOption object.
 * Creation date: (5/2/2007 09:47:20 AM)
 * @return Element
 * @param param cbit.vcell.solver.StochSimOption
 */
private Element getXML(NonspatialStochSimOptions stochOpts, NonspatialStochHybridOptions hybridOptions) {
	Element stochSimOptions = new Element(XMLTags.StochSimOptionsTag);
	if(stochOpts != null)
	{
		stochSimOptions.setAttribute(XMLTags.UseCustomSeedAttrTag, String.valueOf(stochOpts.isUseCustomSeed()));
		if(stochOpts.isUseCustomSeed()){
			stochSimOptions.setAttribute(XMLTags.CustomSeedAttrTag, String.valueOf(stochOpts.getCustomSeed()));
		}
		stochSimOptions.setAttribute(XMLTags.NumberOfTrialAttrTag, String.valueOf(stochOpts.getNumOfTrials()));
		if (hybridOptions != null) {
			stochSimOptions.setAttribute(XMLTags.HybridEpsilonAttrTag, String.valueOf(hybridOptions.getEpsilon()));
			stochSimOptions.setAttribute(XMLTags.HybridLambdaAttrTag, String.valueOf(hybridOptions.getLambda()));
			stochSimOptions.setAttribute(XMLTags.HybridMSRToleranceAttrTag, String.valueOf(hybridOptions.getMSRTolerance()));
			stochSimOptions.setAttribute(XMLTags.HybridSDEToleranceAttrTag, String.valueOf(hybridOptions.getSDETolerance()));
		}
	}
	return stochSimOptions;
}

/**
 * This method returns a XML representation of a Simulation object.
 * Creation date: (3/2/2001 10:42:35 PM)
 * @return Element
 * @param param cbit.vcell.solver.Simulation
 */
Element getXML(Simulation param) {
	Element simulationElement = new Element(XMLTags.SimulationTag);

	//Add Attributes
	String name = mangle(param.getName());
	simulationElement.setAttribute(XMLTags.NameAttrTag, name);
	//simulation.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(param.getDescription()));
	//Add annotation
	if (param.getDescription()!=null && param.getDescription().trim().length()>0) {
		Element annotationElem = new Element(XMLTags.AnnotationTag);
		annotationElem.setText(mangle(param.getDescription()));
		simulationElement.addContent(annotationElem);
	}
	//Add dataProcessingInstructions
	if (param.getDataProcessingInstructions()!=null){
		Element dataProcessingInstructionsElement = new Element(XMLTags.DataProcessingInstructionsTag);
		dataProcessingInstructionsElement.setAttribute(XMLTags.DataProcessingScriptNameAttrTag, param.getDataProcessingInstructions().getScriptName());
		dataProcessingInstructionsElement.setText(param.getDataProcessingInstructions().getScriptInput());
		simulationElement.addContent(dataProcessingInstructionsElement);
	}
	//Add SolverTaskDescription 
	simulationElement.addContent( getXML(param.getSolverTaskDescription()) );
	//Add Math Overrides
	simulationElement.addContent( getXML(param.getMathOverrides()) );
	//Add MeshSpecification (if there is )
	if (param.getMeshSpecification() != null) {
	simulationElement.addContent( getXML(param.getMeshSpecification()) );
	}
	//Add Metadata (Version) if there is one!
	if ( param.getVersion() != null ) {
		simulationElement.addContent( getXML(param.getVersion(), param) );
	}
	
	return simulationElement;
}


/**
 * This method returns a XML representation of a SolverTaskDescription object.
 * Creation date: (3/2/2001 10:59:55 PM)
 * @return Element
 * @param param cbit.vcell.solver.SolverTaskDescription
 */
private Element getXML(SolverTaskDescription param) {
	Element solvertask = new Element(XMLTags.SolverTaskDescriptionTag);

	//Add Attributes
	if (param.getTaskType() == SolverTaskDescription.TASK_UNSTEADY){
		solvertask.setAttribute(XMLTags.TaskTypeTag, XMLTags.UnsteadyTag);
	} else if (param.getTaskType() == SolverTaskDescription.TASK_STEADY){
		solvertask.setAttribute(XMLTags.TaskTypeTag, XMLTags.SteadyTag);		
	} else{
		throw new IllegalArgumentException("Unexpected task type:"+ param.getTaskType());
	}
	//solvertask.setAttribute(XMLTags.KeepEveryTag, String.valueOf(param.getKeepEvery()));
	//solvertask.setAttribute(XMLTags.KeepAtMostTag, String.valueOf(param.getKeepAtMost()));
	solvertask.setAttribute(XMLTags.UseSymbolicJacobianAttrTag, String.valueOf(param.getUseSymbolicJacobian()));
	//Add timeBounds
	solvertask.addContent( getXML(param.getTimeBounds()) );
	//Add timeStep
	solvertask.addContent( getXML(param.getTimeStep()) );
	//Add ErrorTolerence
	solvertask.addContent( getXML(param.getErrorTolerance()) );
	//Add Stochastic simulation Options, 5th Feb, 2007
	//Amended 2oth July, 2007. We need to distinguish hybrid and SSA options
	// Jan 8, 2016 (jim) let getXML(stochOpt) write out the correct stochastic options
	if (param.getStochOpt() != null) {
		solvertask.addContent( getXML(param.getStochOpt(), param.getStochHybridOpt()));
	}
	//Add OutputOptions
	solvertask.addContent(getXML(param.getOutputTimeSpec()));
	//Add sensitivityParameter
	if (param.getSensitivityParameter()!=null){
		solvertask.addContent( getXML(param.getSensitivityParameter()) );
	}
	//Add solver name
	solvertask.setAttribute(XMLTags.SolverNameTag, param.getSolverDescription().getDatabaseName());
	
	// Stop At Spatially Uniform
	ErrorTolerance stopAtSpatiallyUniformErrorTolerance = param.getStopAtSpatiallyUniformErrorTolerance();
	if (stopAtSpatiallyUniformErrorTolerance != null) {
		Element element = new Element(XMLTags.StopAtSpatiallyUniform);
		element.addContent(getXML(stopAtSpatiallyUniformErrorTolerance));
		solvertask.addContent(element);
	}
	
	boolean bRunParameterScanSerially = param.isSerialParameterScan();
	if (bRunParameterScanSerially) {
		solvertask.setAttribute(XMLTags.RunParameterScanSerially, String.valueOf(bRunParameterScanSerially));
	}
	
	SmoldynSimulationOptions smoldynSimulationOptions = param.getSmoldynSimulationOptions();
	if (smoldynSimulationOptions != null) {		
		solvertask.addContent(getXML(smoldynSimulationOptions));
	}
	NFsimSimulationOptions nfsimSimulationOptions = param.getNFSimSimulationOptions();
	if (nfsimSimulationOptions != null) {		
		solvertask.addContent(getXML(nfsimSimulationOptions));
	}
	SundialsPdeSolverOptions sundialsPdeSolverOptions = param.getSundialsPdeSolverOptions();
	if (sundialsPdeSolverOptions != null) {		
		solvertask.addContent(getXML(sundialsPdeSolverOptions));
	}
	ChomboSolverSpec chomboSolverSpec = param.getChomboSolverSpec();
	if (chomboSolverSpec != null) {
		Element chomboElement = getXML(chomboSolverSpec);
		solvertask.addContent(chomboElement);
	}
	
	Element numProcessors= new Element(XMLTags.NUM_PROCESSORS);
	numProcessors.setText(Integer.toString(param.getNumProcessors()));
	solvertask.addContent(numProcessors);
	return solvertask;
}
private Element getXML(NFsimSimulationOptions sso) {			// we know that sso is not null, no need to check again
	Element e = null;
	Element ssoe = new Element(XMLTags.NFSimSimulationOptions);
	if(sso.getObservableComputationOff() == true) {
		e = new Element(XMLTags.NFSimSimulationOptions_observableComputationOff);
		e.setText("true");		// we know it to be true, we don't save the default ("false") at all
		ssoe.addContent(e);
	}
	if (sso.getMoleculeDistance() != null) {
		e = new Element(XMLTags.NFSimSimulationOptions_moleculeDistance);
		e.setText(sso.getMoleculeDistance() + "");
		ssoe.addContent(e);			
	}
	if(sso.getAggregateBookkeeping() == true) {
		e = new Element(XMLTags.NFSimSimulationOptions_aggregateBookkeeping);
		e.setText("true");
		ssoe.addContent(e);
	}
	if (sso.getMaxMoleculesPerType() != null) {
		e = new Element(XMLTags.NFSimSimulationOptions_maxMoleculesPerType);
		e.setText(sso.getMaxMoleculesPerType() + "");
		ssoe.addContent(e);			
	}
	if (sso.getEquilibrateTime() != null) {
		e = new Element(XMLTags.NFSimSimulationOptions_equilibrateTime);
		e.setText(sso.getEquilibrateTime() + "");
		ssoe.addContent(e);			
	}
	if (sso.getRandomSeed() != null) {
		e = new Element(XMLTags.NFSimSimulationOptions_randomSeed);
		e.setText(sso.getRandomSeed() + "");
		ssoe.addContent(e);			
	}
	if(sso.getPreventIntraBonds() == true) {
		e = new Element(XMLTags.NFSimSimulationOptions_preventIntraBonds);
		e.setText("true");
		ssoe.addContent(e);
	}
	return ssoe;
}
private Element getXML(SmoldynSimulationOptions sso) {
	Element ssoElement = null;
	if (sso != null) {
		ssoElement = new Element(XMLTags.SmoldynSimulationOptions);
		
		Element element = new Element(XMLTags.SmoldynSimulationOptions_accuracy);
		element.setText(sso.getAccuracy() + "");
		ssoElement.addContent(element);
		
		if (sso.getRandomSeed() != null) {
			element = new Element(XMLTags.SmoldynSimulationOptions_randomSeed);
			element.setText(sso.getRandomSeed() + "");
			ssoElement.addContent(element);			
		}
		
		element = new Element(XMLTags.SmoldynSimulationOptions_high_res);
		element.setText(sso.isUseHighResolutionSample() + "");
		ssoElement.addContent(element);

		element = new Element(XMLTags.SmoldynSimulationOptions_saveParticleFiles);
		element.setText(sso.isSaveParticleLocations() + "");
		ssoElement.addContent(element);

		element = new Element(XMLTags.SmoldynSimulationOptions_gaussianTableSize);
		element.setText(sso.getGaussianTableSize() + "");
		ssoElement.addContent(element);			
	}
	return ssoElement;
}

private Element getXML(SundialsPdeSolverOptions sso) {
	Element ssoElement = null;
	if (sso != null) {
		ssoElement = new Element(XMLTags.SundialsSolverOptions);
		
		Element element = new Element(XMLTags.SundialsSolverOptions_maxOrderAdvection);
		element.setText(sso.getMaxOrderAdvection() + "");
		ssoElement.addContent(element);
	}
	return ssoElement;
}

/**
 * This method returns a XML representation of a timebounds object.
 * Creation date: (3/2/2001 11:39:20 PM)
 * @return Element
 * @param param cbit.vcell.solver.TimeBounds
 */
private Element getXML(TimeBounds param) {
	Element timebounds = new Element(XMLTags.TimeBoundTag);

	timebounds.setAttribute(XMLTags.StartTimeAttrTag, String.valueOf(param.getStartingTime()));
	timebounds.setAttribute(XMLTags.EndTimeAttrTag, String.valueOf(param.getEndingTime()));
		
	return timebounds;
}


/**
 * This method returns a XML representation of a timeStep object.
 * Creation date: (3/2/2001 11:53:17 PM)
 * @return Element
 * @param param cbit.vcell.solver.TimeStep
 */
private Element getXML(TimeStep param) {
	Element timestep = new Element(XMLTags.TimeStepTag);

	timestep.setAttribute(XMLTags.DefaultTimeAttrTag, String.valueOf(param.getDefaultTimeStep()));
	timestep.setAttribute(XMLTags.MinTimeAttrTag, String.valueOf(param.getMinimumTimeStep()));
	timestep.setAttribute(XMLTags.MaxTimeAttrTag, String.valueOf(param.getMaximumTimeStep()));
	
	return timestep;
}


private Element getXML(Event event) throws XmlParseException{
	Element eventElement = new Element(XMLTags.EventTag);
	eventElement.setAttribute(XMLTags.NameAttrTag, mangle(event.getName()));

	Element element = new Element(XMLTags.TriggerTag);
	element.addContent(mangleExpression(event.getTriggerExpression()));
	eventElement.addContent(element);

	Delay delay = event.getDelay();
	if (delay != null) {
		element = new Element(XMLTags.DelayTag);		
		element.setAttribute(XMLTags.UseValuesFromTriggerTimeAttrTag, delay.useValuesFromTriggerTime() + "");
		element.addContent(mangleExpression(delay.getDurationExpression()));
		eventElement.addContent(element);
	}
	Iterator<EventAssignment> iter = event.getEventAssignments();
	while (iter.hasNext()) {
		EventAssignment eventAssignment = iter.next();
		element = new Element(XMLTags.EventAssignmentTag);
		element.setAttribute(XMLTags.EventAssignmentVariableAttrTag, eventAssignment.getVariable().getName());
		element.addContent(mangleExpression(eventAssignment.getAssignmentExpression()));
		eventElement.addContent(element);
	}
	transcribeComments(event, eventElement);
	return eventElement;
}

//For events in SimulationContext - XML is very similar to math events
public Element getXML(BioEvent[] bioEvents) throws XmlParseException{
	Element bioEventsElement = new Element(XMLTags.BioEventsTag);
	for (int i = 0; i < bioEvents.length; i++) {
		Element eventElement = new Element(XMLTags.BioEventTag);
		eventElement.setAttribute(XMLTags.NameAttrTag, mangle(bioEvents[i].getName()));


		String triggerType = bioEvents[i].getTriggerType().getXmlName();
		
		//Add atributes
		eventElement.setAttribute(XMLTags.BioEventTriggerTypeAttrTag, triggerType);
		eventElement.setAttribute(XMLTags.UseValuesFromTriggerTimeAttrTag, Boolean.toString(bioEvents[i].getUseValuesFromTriggerTime()));
		
		//Add BioEvent Parameters
		LocalParameter parameters[] = bioEvents[i].getEventParameters();
		for (LocalParameter parm : parameters){
			if (parm.getExpression()!=null){
				Element tempparameter = new Element(XMLTags.ParameterTag);
				//Get parameter attributes
				tempparameter.setAttribute(XMLTags.NameAttrTag, mangle(parm.getName()));
				tempparameter.setAttribute(XMLTags.ParamRoleAttrTag, bioEvents[i].getParameterType(parm).getRoleXmlName());
				VCUnitDefinition unit = parm.getUnitDefinition();
				if (unit != null) {
					tempparameter.setAttribute(XMLTags.VCUnitDefinitionAttrTag, unit.getSymbol());
				}
				tempparameter.addContent( mangleExpression(parm.getExpression()) );
				//Add the parameter to the general kinetics object
				eventElement.addContent(tempparameter);
			}
		}

		ArrayList<BioEvent.EventAssignment> eventAssignmentsList = bioEvents[i].getEventAssignments();
		if(eventAssignmentsList != null){
			for (BioEvent.EventAssignment eventAssignment : eventAssignmentsList) {
				Element eventAssignmentElement = new Element(XMLTags.EventAssignmentTag);
				eventAssignmentElement.setAttribute(XMLTags.EventAssignmentVariableAttrTag, eventAssignment.getTarget().getName());
				eventAssignmentElement.addContent(mangleExpression(eventAssignment.getAssignmentExpression()));
				eventElement.addContent(eventAssignmentElement);
			}
		}
		bioEventsElement.addContent(eventElement);
	}

	System.out.println(XmlUtil.xmlToString(bioEventsElement));
	return bioEventsElement;
}


public Element getXML(SpatialObject[] spatialObjects) throws XmlParseException{
	Element spatialObjectsElement = new Element(XMLTags.SpatialObjectsTag);
	for (SpatialObject spatialObject : spatialObjects) {
		Element spatialObjectElement = new Element(XMLTags.SpatialObjectTag);
		spatialObjectElement.setAttribute(XMLTags.NameAttrTag, mangle(spatialObject.getName()));
		
		if (spatialObject instanceof PointObject){
			spatialObjectElement.setAttribute(XMLTags.SpatialObjectTypeAttrTag,XMLTags.SpatialObjectTypeAttrValue_Point);
		} else if (spatialObject instanceof SurfaceRegionObject){
			spatialObjectElement.setAttribute(XMLTags.SpatialObjectTypeAttrTag,XMLTags.SpatialObjectTypeAttrValue_Surface);
			spatialObjectElement.setAttribute(XMLTags.SpatialObjectSubVolumeInsideAttrTag,((SurfaceRegionObject) spatialObject).getInsideSubVolume().getName());
			spatialObjectElement.setAttribute(XMLTags.SpatialObjectRegionIdInsideAttrTag,((SurfaceRegionObject) spatialObject).getInsideRegionID().toString());
			spatialObjectElement.setAttribute(XMLTags.SpatialObjectSubVolumeOutsideAttrTag,((SurfaceRegionObject) spatialObject).getOutsideSubVolume().getName());
			spatialObjectElement.setAttribute(XMLTags.SpatialObjectRegionIdOutsideAttrTag,((SurfaceRegionObject) spatialObject).getOutsideRegionID().toString());
		} else if (spatialObject instanceof VolumeRegionObject){
			spatialObjectElement.setAttribute(XMLTags.SpatialObjectTypeAttrTag,XMLTags.SpatialObjectTypeAttrValue_Volume);
			spatialObjectElement.setAttribute(XMLTags.SpatialObjectSubVolumeAttrTag,((VolumeRegionObject) spatialObject).getSubVolume().getName());
			spatialObjectElement.setAttribute(XMLTags.SpatialObjectRegionIdAttrTag,((VolumeRegionObject) spatialObject).getRegionID().toString());
		} else {
			throw new RuntimeException("spatialObject type "+spatialObject.getClass().getSimpleName()+" not yet supported for persistence.");
		}
		
		Element quantityCategoryListElement = new Element(XMLTags.QuantityCategoryListTag);
		for (QuantityCategory quantityCategory : spatialObject.getQuantityCategories()){
			Element quantityCategoryElement = new Element(XMLTags.QuantityCategoryTag);
			quantityCategoryElement.setAttribute(XMLTags.QuantityCategoryNameAttrTag, quantityCategory.xmlName);
			quantityCategoryElement.setAttribute(XMLTags.QuantityCategoryEnabledAttrTag, Boolean.toString(spatialObject.isQuantityCategoryEnabled(quantityCategory)));
			quantityCategoryListElement.addContent(quantityCategoryElement);
		}
		spatialObjectElement.addContent(quantityCategoryListElement);
		spatialObjectsElement.addContent(spatialObjectElement);
	}

	System.out.println(XmlUtil.xmlToString(spatialObjectsElement));
	return spatialObjectsElement;
}


public Element getXML(SpatialProcess[] spatialProcesses) throws XmlParseException{
	Element spatialProcessesElement = new Element(XMLTags.SpatialProcessesTag);
	for (SpatialProcess spatialProcess : spatialProcesses) {
		Element spatialProcessElement = new Element(XMLTags.SpatialProcessTag);
		spatialProcessElement.setAttribute(XMLTags.NameAttrTag, mangle(spatialProcess.getName()));
		
		if (spatialProcess instanceof PointKinematics){
			spatialProcessElement.setAttribute(XMLTags.SpatialProcessTypeAttrTag,XMLTags.SpatialProcessTypeAttrValue_PointKinematics);
			spatialProcessElement.setAttribute(XMLTags.SpatialProcessPointObjectAttrTag,((PointKinematics) spatialProcess).getPointObject().getName());
		} else if (spatialProcess instanceof PointLocation){
			spatialProcessElement.setAttribute(XMLTags.SpatialProcessTypeAttrTag,XMLTags.SpatialProcessTypeAttrValue_PointLocation);
			spatialProcessElement.setAttribute(XMLTags.SpatialProcessPointObjectAttrTag,((PointLocation) spatialProcess).getPointObject().getName());
		} else if (spatialProcess instanceof SurfaceKinematics){
			spatialProcessElement.setAttribute(XMLTags.SpatialProcessTypeAttrTag,XMLTags.SpatialProcessTypeAttrValue_SurfaceKinematics);
			spatialProcessElement.setAttribute(XMLTags.SpatialProcessSurfaceObjectAttrTag,((SurfaceKinematics) spatialProcess).getSurfaceRegionObject().getName());
		} else if (spatialProcess instanceof VolumeKinematics){
			spatialProcessElement.setAttribute(XMLTags.SpatialProcessTypeAttrTag,XMLTags.SpatialProcessTypeAttrValue_VolumeKinematics);
			spatialProcessElement.setAttribute(XMLTags.SpatialProcessVolumeObjectAttrTag,((VolumeKinematics) spatialProcess).getVolumeRegionObject().getName());
		} else {
			throw new RuntimeException("spatialProcess type "+spatialProcess.getClass().getSimpleName()+" not yet supported for persistence.");
		}
		
		LocalParameter parameters[] = spatialProcess.getParameters();
		for (LocalParameter parm : parameters){
			if (parm.getExpression()!=null){
				Element tempparameter = new Element(XMLTags.ParameterTag);
				//Get parameter attributes
				tempparameter.setAttribute(XMLTags.NameAttrTag, mangle(parm.getName()));
				tempparameter.setAttribute(XMLTags.ParamRoleAttrTag, spatialProcess.getParameterType(parm).getRoleXmlName());
				VCUnitDefinition unit = parm.getUnitDefinition();
				if (unit != null) {
					tempparameter.setAttribute(XMLTags.VCUnitDefinitionAttrTag, unit.getSymbol());
				}
				tempparameter.addContent( mangleExpression(parm.getExpression()) );
				spatialProcessElement.addContent(tempparameter);
			}
		}
		spatialProcessesElement.addContent(spatialProcessElement);
	}

	System.out.println(XmlUtil.xmlToString(spatialProcessesElement));
	return spatialProcessesElement;
}


//For rateRules in SimulationContext
public Element getXML(RateRule[] rateRules) throws XmlParseException{
	Element rateRulesElement = new Element(XMLTags.RateRulesTag);
	for (int i = 0; i < rateRules.length; i++) {
		Element rateRuleElement = new Element(XMLTags.RateRuleTag);
		rateRuleElement.setAttribute(XMLTags.NameAttrTag, mangle(rateRules[i].getName()));
		rateRuleElement.setAttribute(XMLTags.RateRuleVariableAttrTag, rateRules[i].getRateRuleVar().getName());
		rateRuleElement.addContent(mangleExpression(rateRules[i].getRateRuleExpression()));

		rateRulesElement.addContent(rateRuleElement);
	}

	return rateRulesElement;
}

	private Element getXML(ChomboSolverSpec chomboSolverSpec) {
		Element chomboElement = new Element(XMLTags.ChomboSolverSpec);
		
		Element maxBoxSize = new Element(XMLTags.MaxBoxSizeTag);
		maxBoxSize.setText(chomboSolverSpec.getMaxBoxSize() + "");
		chomboElement.addContent(maxBoxSize);
		
		if (!chomboSolverSpec.isFinestViewLevel())
		{
			Element viewLevel = new Element(XMLTags.ViewLevelTag);
			viewLevel.setText(chomboSolverSpec.getViewLevel() + "");
			chomboElement.addContent(viewLevel);
		}
		
		Element fillRatio = new Element(XMLTags.FillRatioTag);
		fillRatio.setText(chomboSolverSpec.getFillRatio() + "");
		chomboElement.addContent(fillRatio);
		
		List<Integer> ratios = chomboSolverSpec.getRefineRatioList();
		if (ratios.size() > 0)
		{
			Element ratiosElement = new Element(XMLTags.RefineRatios);
			StringBuilder sb = new StringBuilder();
			for (Integer i: ratios)
			{
				sb.append("," + i);
			}
			ratiosElement.setText(sb.substring(1) + "");
			chomboElement.addContent(ratiosElement);
		}
	
		Element saveVCellOutput = new Element(XMLTags.SaveVCellOutput);
		saveVCellOutput.setText(chomboSolverSpec.isSaveVCellOutput() + "");
		chomboElement.addContent(saveVCellOutput);
		
		Element saveChomboOutput = new Element(XMLTags.SaveChomboOutput);
		saveChomboOutput.setText(chomboSolverSpec.isSaveChomboOutput() + "");
		chomboElement.addContent(saveChomboOutput);
		
		Element activateFeatureUnderDevelopment = new Element(XMLTags.ActivateFeatureUnderDevelopment);
		activateFeatureUnderDevelopment.setText(chomboSolverSpec.isActivateFeatureUnderDevelopment() + "");
		chomboElement.addContent(activateFeatureUnderDevelopment);
		
		Element smallVolfracThreshold = new Element(XMLTags.SmallVolfracThreshold);
		smallVolfracThreshold.setText(chomboSolverSpec.getSmallVolfracThreshold() + "");
		chomboElement.addContent(smallVolfracThreshold);
		
		Element timeBoundsTag = new Element(XMLTags.TimeBoundTag); 
		for (TimeInterval ti : chomboSolverSpec.getTimeIntervalList())
		{
			Element e = new Element(XMLTags.TimeIntervalTag);
			e.setAttribute(XMLTags.StartTimeAttrTag, String.valueOf(ti.getStartingTime()));
			e.setAttribute(XMLTags.EndTimeAttrTag, String.valueOf(ti.getEndingTime()));
			e.setAttribute(XMLTags.TimeStepAttrTag, String.valueOf(ti.getTimeStep()));
			e.setAttribute(XMLTags.OutputTimeStepAttrTag, String.valueOf(ti.getOutputTimeStep()));
			timeBoundsTag.addContent(e);
		}
		chomboElement.addContent(timeBoundsTag);
		
		Element meshRefinement = new Element(XMLTags.MeshRefinementTag);
		for (RefinementRoi roi : chomboSolverSpec.getRefinementRois()) {
			Element roiElement = new Element(XMLTags.RefinementRoiTag);
			roiElement.setAttribute(XMLTags.RefineRoiLevelAttrTag, String.valueOf(roi.getLevel()));
			roiElement.setAttribute(XMLTags.RefinementRoiTypeAttrTag, roi.getType().name());
			roiElement.setAttribute(XMLTags.TagsGrowAttrTag, String.valueOf(roi.getTagsGrow()));
			if (roi.getRoiExpression() != null)
			{
				Element expElement = new Element(XMLTags.ROIExpressionTag);
				expElement.setText(roi.getRoiExpression().infix() + ";");
				roiElement.addContent(expElement);
			}
			meshRefinement.addContent(roiElement);
		}
		chomboElement.addContent(meshRefinement);
		return chomboElement;
	}

/*
//For rateRuleVariables in model
public Element getXML(RateRuleVariable[] rateRuleVars) throws XmlParseException{
	Element rateRuleVarsElement = new Element(XMLTags.RateRuleVariablesTag);
	for (int i = 0; i < rateRuleVars.length; i++) {
		Element rateRuleElement = new Element(XMLTags.RateRuleVariableTag);
		rateRuleElement.setAttribute(XMLTags.NameAttrTag, mangle(rateRuleVars[i].getName()));
		if (rateRuleVars[i].getStructure() != null) {
			rateRuleElement.setAttribute(XMLTags.StructureAttrTag, mangle(rateRuleVars[i].getStructure().getName()));
		}
		rateRuleElement.setAttribute(XMLTags.ParamRoleAttrTag, rateRuleVars[i].getParameterRoleDesc(rateRuleVars[i].getParameterRole()));
		rateRuleElement.addContent(getXML(rateRuleVars[i].getParameter()));

		rateRuleVarsElement.addContent(rateRuleElement);
	}

	return rateRuleVarsElement;
}
*/
}
