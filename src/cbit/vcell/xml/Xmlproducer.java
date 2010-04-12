/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

package cbit.vcell.xml;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;
import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import cbit.vcell.biomodel.meta.xml.XMLMetaDataWriter;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.CompartmentSubVolume;
import cbit.vcell.geometry.ControlPointCurve;
import cbit.vcell.geometry.Curve;
import cbit.vcell.geometry.Filament;
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
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.TotalCurrentClampStimulus;
import cbit.vcell.mapping.VoltageClampStimulus;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.math.Action;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Equation;
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
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneRegionEquation;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.OutsideVariable;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.RandomVariable;
import cbit.vcell.math.StochVolVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.UniformDistribution;
import cbit.vcell.math.VarIniCondition;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.VolumeRandomVariable;
import cbit.vcell.math.VolumeRegionEquation;
import cbit.vcell.math.VolumeRegionVariable;
import cbit.vcell.math.Event.Delay;
import cbit.vcell.math.Event.EventAssignment;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Diagram;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Flux;
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
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.stoch.StochHybridOptions;
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
 * @return org.jdom.Element
 * @param analysisTasks cbit.vcell.modelopt.AnalysisTask[]
 */
public Element getXML(cbit.vcell.modelopt.AnalysisTask[] analysisTasks) {
	org.jdom.Element analysisTaskListElement = new org.jdom.Element(XMLTags.AnalysisTaskListTag);

	for (int i = 0; i < analysisTasks.length; i++){
		Element analysisTaskElement = null;
		if (analysisTasks[i] instanceof cbit.vcell.modelopt.ParameterEstimationTask){
			analysisTaskElement = cbit.vcell.modelopt.ParameterEstimationTaskXMLPersistence.getXML((cbit.vcell.modelopt.ParameterEstimationTask)analysisTasks[i]);
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
 * @return org.jdom.Element
 * @param param cbit.image.VCImage
 */
public org.jdom.Element getXML(cbit.image.VCImage param) throws XmlParseException{
		org.jdom.Element image = new org.jdom.Element(XMLTags.ImageTag);

		//add atributes
		image.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
//		image.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(param.getDescription()));

		//Add annotation
		if (param.getDescription()!=null && param.getDescription().length()>0) {
			org.jdom.Element annotationElement = new org.jdom.Element(XMLTags.AnnotationTag);
			annotationElement.setText( mangle(param.getDescription()) );
			image.addContent(annotationElement);
		}

		//Add Imagedata subelement
		byte[] compressedPixels = null;
		try {
			compressedPixels = param.getPixelsCompressed();
		} catch (cbit.image.ImageException e) {
			e.printStackTrace();
			throw new XmlParseException("An ImageParseException occurred when tring to retrieving the compressed Pixels"+" : "+e.getMessage());
		}
		org.jdom.Element imagedata = new org.jdom.Element(XMLTags.ImageDataTag);
		
		//Get imagedata attributes
		imagedata.setAttribute(XMLTags.XAttrTag, String.valueOf(param.getNumX()));
		imagedata.setAttribute(XMLTags.YAttrTag, String.valueOf(param.getNumY()));
		imagedata.setAttribute(XMLTags.ZAttrTag, String.valueOf(param.getNumZ()));
		imagedata.setAttribute(XMLTags.CompressedSizeTag, String.valueOf(compressedPixels.length));
		//Get imagedata content
		imagedata.addContent(org.vcell.util.Hex.toString(compressedPixels)); //encode
		//Add imagedata to VCImage element
		image.addContent(imagedata);
		//Add PixelClass elements
		cbit.image.VCPixelClass pixelClasses[] = param.getPixelClasses();
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
 * @return org.jdom.Element
 * @param param cbit.image.VCImageRegion
 */
public org.jdom.Element getXML(cbit.image.VCPixelClass param) {
	
	//Create PixelClass subelement
	org.jdom.Element pixelclass = new org.jdom.Element(XMLTags.PixelClassTag);
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
 * @return org.jdom.Element
 * @param param cbit.sql.Version
 */
public org.jdom.Element getXML(org.vcell.util.document.Version version, org.vcell.util.document.Versionable versionable) {
	return getXML(version, versionable.getName(), versionable.getDescription());
}


/**
 * This method returns a XML representation of a Version java object.
 * Creation date: (3/13/2001 6:00:59 PM)
 * @return org.jdom.Element
 * @param param cbit.sql.Version
 */
public org.jdom.Element getXML(org.vcell.util.document.Version version, String nameParam, String descriptionParam) {
	//** Dump the content to the 'Version' object **
	org.jdom.Element versionElement = new org.jdom.Element(XMLTags.VersionTag);
	
	//if empty just return null
	if (version == null || this.printKeysFlag == false) {
		//*** If the version is empty, then use the versionable ***
		//*Name
		versionElement.setAttribute(XMLTags.NameAttrTag, mangle(nameParam));
		//Specify if it comes from a versionable
		versionElement.setAttribute(XMLTags.FromVersionableTag, "true");
		//*Annotation
		if (descriptionParam!=null && descriptionParam.length()>0) {
			org.jdom.Element annotationElem = new org.jdom.Element(XMLTags.AnnotationTag);
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
		org.jdom.Element owner = new org.jdom.Element(XMLTags.OwnerTag);
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
			org.jdom.Element annotationElem = new org.jdom.Element(XMLTags.AnnotationTag);
			annotationElem.setText(mangle(version.getAnnot()));
			versionElement.addContent(annotationElem);
		}
		if (version instanceof org.vcell.util.document.SimulationVersion){
			org.vcell.util.document.SimulationVersion simVersion = (org.vcell.util.document.SimulationVersion)version;
			if (simVersion.getParentSimulationReference()!=null){
				org.jdom.Element parentSimRefElem = new org.jdom.Element(XMLTags.ParentSimRefTag);
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
 * @return org.jdom.Element
 * @param param Extent
 */
public org.jdom.Element getXML(Extent param) throws XmlParseException{

	org.jdom.Element extent = new org.jdom.Element(XMLTags.ExtentTag);
	//Add extent attributes
	extent.setAttribute(XMLTags.XAttrTag, String.valueOf(param.getX()));
	extent.setAttribute(XMLTags.YAttrTag, String.valueOf(param.getY()));
	extent.setAttribute(XMLTags.ZAttrTag, String.valueOf(param.getZ()));

	return extent;
}

public org.jdom.Element getXML(Origin param) throws XmlParseException{

	org.jdom.Element origin = new org.jdom.Element(XMLTags.OriginTag);
	//Add extent attributes
	origin.setAttribute(XMLTags.XAttrTag, String.valueOf(param.getX()));
	origin.setAttribute(XMLTags.YAttrTag, String.valueOf(param.getY()));
	origin.setAttribute(XMLTags.ZAttrTag, String.valueOf(param.getZ()));

	return origin;
}

/**
 * This method returns a XML representation for a Biomodel object.
 * Creation date: (2/14/2001 3:41:13 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.biomodel.BioModel
 */
public org.jdom.Element getXML(cbit.vcell.biomodel.BioModel param) throws XmlParseException, cbit.vcell.parser.ExpressionException {
	//Creation of BioModel Node
	org.jdom.Element biomodelnode = new org.jdom.Element(XMLTags.BioModelTag);
	String name = param.getName();

	//Add attributes
	biomodelnode.setAttribute(XMLTags.NameAttrTag, name);
	//biomodelnode.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(param.getDescription()));
	//Add annotation
	if (param.getDescription()!=null && param.getDescription().length()>0) {
		org.jdom.Element annotationElem = new org.jdom.Element(XMLTags.AnnotationTag);
		annotationElem.setText(mangle(param.getDescription()));
		biomodelnode.addContent(annotationElem);
	}
	
	//Get Model
	try {
		biomodelnode.addContent( getXML(param.getModel()) );
	} catch (XmlParseException e) {
		e.printStackTrace();
		throw new XmlParseException("An error occurred while processing the BioModel " + name+" : "+e.getMessage());
	}
	//Get SimulationContexts
	if ( param.getSimulationContexts()!=null ){
		for (int index=0;index<param.getSimulationContexts().length;index++){
			biomodelnode.addContent( getXML(param.getSimulationContexts(index),param) );
		}
	}
	//Add Database Metadata (Version) information
	if (param.getVersion() != null) {
		biomodelnode.addContent( getXML(param.getVersion(), param.getName(), param.getDescription()) );
	}

	biomodelnode.addContent(XMLMetaDataWriter.getElement(param.getVCMetaData(), param));
	return biomodelnode;
}


/**
 * This method returns a XML representation for a DBFormalSpecies.
 * Creation date: (6/3/2003 4:50:15 PM)
 * @return org.jdom.Element
 * @dbformalSpecies param cbit.vcell.dictionary.DBFormalSpecies
 */
public org.jdom.Element getXML(cbit.vcell.dictionary.DBFormalSpecies dbformalSpecies) throws XmlParseException {
	//create XML object
	org.jdom.Element dbSpeciesElement = new org.jdom.Element(XMLTags.DBFormalSpeciesTag);
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
 * @return org.jdom.Element
 * @param dbSpecies cbit.vcell.dictionary.DBSpecies
 */
public org.jdom.Element getXML(cbit.vcell.dictionary.DBSpecies dbSpecies) throws XmlParseException {
	//create xml node
	org.jdom.Element dbSpeciesElement = new org.jdom.Element(XMLTags.DBSpeciesTag);

	if (this.printKeysFlag){
		//add the DBSpecieKey (IT ALWAYS NEED THE KEY!)
		dbSpeciesElement.setAttribute(XMLTags.KeyValueAttrTag, dbSpecies.getDBSpeciesKey().toString());

	}
	//detect the type of DBSpecie
	if (dbSpecies instanceof cbit.vcell.dictionary.BoundCompound) {
		//add type
		dbSpeciesElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.CompoundTypeTag);
		//add FormalCompound
		dbSpeciesElement.addContent(getXML(((cbit.vcell.dictionary.BoundCompound)dbSpecies).getFormalCompound()));
	} else if (dbSpecies instanceof cbit.vcell.dictionary.BoundEnzyme) {
		//add type
		dbSpeciesElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.EnzymeTypeTag);
		//add FormalEnzyme
		dbSpeciesElement.addContent(getXML(((cbit.vcell.dictionary.BoundEnzyme)dbSpecies).getFormalEnzyme()));
	} else if (dbSpecies instanceof cbit.vcell.dictionary.BoundProtein) {
		//add type
		dbSpeciesElement.setAttribute(XMLTags.TypeAttrTag, XMLTags.ProteinTypeTag);
		//add FormalProtein
		dbSpeciesElement.addContent(getXML(((cbit.vcell.dictionary.BoundProtein)dbSpecies).getProteinEnzyme()));
	} else {
		throw new XmlParseException("DBSpecies type "+dbSpecies.getClass().getName()+" not supported yet!");
	}
	return dbSpeciesElement;
}


/**
 * This method returns a XML representation for a FormalSpeciesInfo.
 * Creation date: (6/3/2003 5:14:09 PM)
 * @return org.jdom.Element
 * @param speciesInfo cbit.vcell.dictionary.FormalSpeciesInfo
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public org.jdom.Element getXML(cbit.vcell.dictionary.FormalSpeciesInfo speciesInfo) throws XmlParseException {
	//Create XML object
	org.jdom.Element speciesInfoElement = new org.jdom.Element(XMLTags.FormalSpeciesInfoTag);

	//add formalID
	speciesInfoElement.setAttribute(XMLTags.FormalIDTag, mangle(speciesInfo.getFormalID()));

	//add names
	String[] namesArray = speciesInfo.getNames();

	for (int i = 0; i < namesArray.length; i++){
		org.jdom.Element nameElement = new org.jdom.Element(XMLTags.NameTag);
		nameElement.addContent(mangle(namesArray[i]));
		speciesInfoElement.addContent(nameElement);
	}
	String temp;
	//add type plus extra parameters
	if (speciesInfo instanceof cbit.vcell.dictionary.CompoundInfo) {
		cbit.vcell.dictionary.CompoundInfo info = (cbit.vcell.dictionary.CompoundInfo)speciesInfo;
		
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
				org.jdom.Element enzymeElement = new org.jdom.Element(XMLTags.EnzymeTag);
				cbit.vcell.dictionary.EnzymeRef ref = info.getEnzymes()[i];
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
	} else if (speciesInfo instanceof cbit.vcell.dictionary.EnzymeInfo) {
		cbit.vcell.dictionary.EnzymeInfo info = (cbit.vcell.dictionary.EnzymeInfo)speciesInfo;
		
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
	} else if (speciesInfo instanceof cbit.vcell.dictionary.ProteinInfo) {
		cbit.vcell.dictionary.ProteinInfo info = (cbit.vcell.dictionary.ProteinInfo)speciesInfo;
			
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
 * @return org.jdom.Element
 * @param param cbit.vcell.geometry.AnalyticSubVolume
 */
public org.jdom.Element getXML(AnalyticSubVolume param) {
	org.jdom.Element analytic = new org.jdom.Element(XMLTags.SubVolumeTag);

	//Add Attributes
	analytic.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	analytic.setAttribute(XMLTags.HandleAttrTag, String.valueOf(param.getHandle()));
	analytic.setAttribute(XMLTags.TypeAttrTag, XMLTags.AnalyticBasedTypeTag);

	//Create Analytic Expression subelement
	org.jdom.Element expression = new org.jdom.Element(XMLTags.AnalyticExpressionTag);
	//Add expression Content
	expression.addContent(mangleExpression(param.getExpression()));
	analytic.addContent(expression);

	//If keyFlag is on print the Keyvalue
	if (param.getKey() !=null && this.printKeysFlag) {
		analytic.setAttribute(XMLTags.KeyValueAttrTag, param.getKey().toString());
	}
		
	return analytic;
}


/**
 * This method returns a XML representation of a CompartmentSubVolume.
 * Creation date: (3/1/2001 4:01:59 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.geometry.CompartmentSubVolume
 */
public org.jdom.Element getXML(cbit.vcell.geometry.CompartmentSubVolume param) {
	org.jdom.Element subvolume = new org.jdom.Element(XMLTags.SubVolumeTag);
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
 * @return org.jdom.Element
 * @param param cbit.vcell.geometry.ControlPointCurve
 */
public org.jdom.Element getXML(ControlPointCurve param) {
	org.jdom.Element curve = new org.jdom.Element(XMLTags.CurveTag);

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
 * @return org.jdom.Element
 * @param param cbit.vcell.geometry.Coordinate
 */
public org.jdom.Element getXML(Coordinate param) {
	org.jdom.Element coord = new org.jdom.Element(XMLTags.CoordinateTag);

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
 * @return org.jdom.Element
 * @param param cbit.vcell.geometry.Filament
 */
public org.jdom.Element getXML(Filament param) {
	//--- create Element
	org.jdom.Element filament = new org.jdom.Element(XMLTags.FilamentTag);
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
 * @return org.jdom.Element
 * @param param cbit.vcell.geometry.Geometry
 */
public org.jdom.Element getXML(cbit.vcell.geometry.Geometry param) throws XmlParseException{
	org.jdom.Element geometry = new org.jdom.Element(XMLTags.GeometryTag);

	// Add attributes
	String name = param.getName();
	geometry.setAttribute(XMLTags.NameAttrTag, mangle(name));
	geometry.setAttribute(XMLTags.DimensionAttrTag, String.valueOf(param.getDimension()));
	//geometry.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(param.getDescription()));
	//add Annotation
	if (param.getDescription()!=null && param.getDescription().length()>0) {
		org.jdom.Element annotationElem = new org.jdom.Element(XMLTags.AnnotationTag);
		annotationElem.setText(mangle(param.getDescription()));
		geometry.addContent(annotationElem);
	}
	
	// add sub-elements
	//Create extent subelement
	geometry.addContent(getXML(param.getExtent()));
	//Add Origin subelement
	org.jdom.Element origin = new org.jdom.Element(XMLTags.OriginTag);
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
			throw new XmlParseException("A problem occurred when trying to get the Image for the geometry " + name+" : "+e.getMessage());
		}
	}
	//Add subvolumes elements 
	for (int i=0;i<param.getGeometrySpec().getSubVolumes().length;i++){
		geometry.addContent( getXML(param.getGeometrySpec().getSubVolumes(i)) );
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
 * @return org.jdom.Element
 * @param param cbit.vcell.geometry.ImageSubVolume
 */
public org.jdom.Element getXML(ImageSubVolume param) {
	org.jdom.Element subvolume = new org.jdom.Element(XMLTags.SubVolumeTag);

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


/**
 * This method returns a XML representation of a Subvolume object.
 * Creation date: (3/1/2001 3:27:08 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.geometry.SubVolume
 */
public org.jdom.Element getXML(cbit.vcell.geometry.SubVolume param) {
	if (param instanceof AnalyticSubVolume) {
		return getXML((AnalyticSubVolume)param);
	} else if (param instanceof CompartmentSubVolume) {
		return getXML( (CompartmentSubVolume)param );
	} else if (param instanceof ImageSubVolume) {
		return getXML( (ImageSubVolume)param );
	}

	return null;
}


	public Element getXML(GeometrySurfaceDescription param) throws XmlParseException {

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
 * @return org.jdom.Element
 * @param param cbit.vcell.mapping.Electrode
 */
public org.jdom.Element getXML(ElectricalStimulus param) {
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

	org.jdom.Element electricalStimulus = new org.jdom.Element(XMLTags.ClampTag);
	
	// Need to add electrode ??
	Element electrode = getXML(param.getElectrode());
	electricalStimulus.addContent(electrode);
	
	//Add atributes
	electricalStimulus.setAttribute(XMLTags.TypeAttrTag, electricalStimulusType);
	
	//Add Kinetics Parameters
	LocalParameter parameters[] = param.getLocalParameters();
	for (int i=0;i<parameters.length;i++){
		LocalParameter parm = parameters[i];
		org.jdom.Element tempparameter = new org.jdom.Element(XMLTags.ParameterTag);
		//Get parameter attributes
		tempparameter.setAttribute(XMLTags.NameAttrTag, mangle(parm.getName()));
		tempparameter.setAttribute(XMLTags.ParamRoleAttrTag, ElectricalStimulus.RoleDescs[parm.getRole()]);
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
 * @return org.jdom.Element
 * @param param cbit.vcell.mapping.Electrode
 */
public org.jdom.Element getXML(Electrode param) {
	org.jdom.Element electrodeElem = new org.jdom.Element(XMLTags.ElectrodeTag);
	//add feature name
	electrodeElem.setAttribute( XMLTags.FeatureAttrTag, mangle(param.getFeature().getName()) );

	//add coordinate
	electrodeElem.addContent(getXML(param.getPosition()));
	
	return electrodeElem;
}


/**
 * This method returns a XML representation of a featureMapping object.
 * Creation date: (3/1/2001 8:16:57 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.mapping.FeatureMapping
 */
public org.jdom.Element getXML(FeatureMapping param) {
	//Allow null subvolumes
	//if (param.getSubVolume()==null) {	//5/92001
		//return null;
	//}
	
	org.jdom.Element feature = new org.jdom.Element(XMLTags.FeatureMappingTag);
	
	//Add atributes
	feature.setAttribute(XMLTags.FeatureAttrTag, mangle(param.getFeature().getName()));
	SubVolume subvol = param.getSubVolume();
	if (subvol != null) {
		feature.setAttribute(XMLTags.SubVolumeAttrTag, mangle(subvol.getName()));
	}
	feature.setAttribute(XMLTags.ResolvedAttrTag, String.valueOf(param.getResolved()));
	//Add size
	if(param.getSizeParameter().getExpression() != null)
		feature.setAttribute(XMLTags.SizeTag, mangleExpression(param.getSizeParameter().getExpression()));
	
	// write BoundariesyConditions
	org.jdom.Element boundariestypes = new org.jdom.Element(XMLTags.BoundariesTypesTag);
	
	//Xm
	boundariestypes.setAttribute(XMLTags.BoundaryAttrValueXm, param.getBoundaryConditionTypeXm().toString());
	//Xp
	boundariestypes.setAttribute(XMLTags.BoundaryAttrValueXp, param.getBoundaryConditionTypeXp().toString());
	//Ym
	boundariestypes.setAttribute(XMLTags.BoundaryAttrValueYm, param.getBoundaryConditionTypeYm().toString());
	//Yp
	boundariestypes.setAttribute(XMLTags.BoundaryAttrValueYp, param.getBoundaryConditionTypeYp().toString());
	//Zm
	boundariestypes.setAttribute(XMLTags.BoundaryAttrValueZm, param.getBoundaryConditionTypeZm().toString());
	//Zp
	boundariestypes.setAttribute(XMLTags.BoundaryAttrValueZp, param.getBoundaryConditionTypeZp().toString());
	
	feature.addContent( boundariestypes ); //add boundaries to the feature

	return feature;
}


/**
 * This method returns a XML representation of a GeometryContext object.
 * Creation date: (3/1/2001 6:50:24 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.mapping.GeometryContext
 */
public org.jdom.Element getXML(cbit.vcell.mapping.GeometryContext param) {
	org.jdom.Element geometrycontent = new org.jdom.Element(XMLTags.GeometryContextTag);

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
 * @return org.jdom.Element
 * @param param cbit.vcell.mapping.MembraneMapping
 */
public org.jdom.Element getXML(MembraneMapping param) {
	//Allow 'null' subvolumes
	//if (param.getSubVolume() == null) {
		//return null;
	//}
	org.jdom.Element membrane = new org.jdom.Element(XMLTags.MembraneMappingTag);
	
	//Add atributes
	membrane.setAttribute(XMLTags.MembraneAttrTag, mangle(param.getMembrane().getName()));
	// write FluxCorrections
	
	//SurfaceToVolumeRatio if it exsits, amended Sept. 27th, 2007
	if(param.getSurfaceToVolumeParameter().getExpression() != null)
	{
		membrane.setAttribute(XMLTags.SurfaceToVolumeRatioTag, mangleExpression(param.getSurfaceToVolumeParameter().getExpression()) );
	}
/*	org.jdom.Element surface = new org.jdom.Element(XMLTags.SurfaceToVolumeRatioTag);
	surface.addContent( this.mangleExpression(param.getSurfaceToVolumeExpression()) );
	membrane.addContent( surface );*/
	
	//VolumeFraction if it exsits, amended Sept. 27th, 2007
	if(param.getVolumeFractionParameter().getExpression() != null)
	{
		membrane.setAttribute(XMLTags.VolumeFractionTag, mangleExpression(param.getVolumeFractionParameter().getExpression()));
	}
/*	org.jdom.Element volume = new org.jdom.Element(XMLTags.VolumeFractionTag);
	volume.addContent( this.mangleExpression(param.getVolumeFractionExpression()) );
	membrane.addContent( volume );*/
	//Add size
	if(param.getSizeParameter().getExpression() != null)
 		membrane.setAttribute(XMLTags.SizeTag, mangleExpression(param.getSizeParameter().getExpression()));
	//Add the electrical properties
	membrane.setAttribute(XMLTags.CalculateVoltageTag, String.valueOf(param.getCalculateVoltage()));
	membrane.setAttribute(XMLTags.SpecificCapacitanceTag, mangleExpression(param.getSpecificCapacitanceParameter().getExpression()));
	membrane.setAttribute(XMLTags.InitialVoltageTag,mangleExpression(param.getInitialVoltageParameter().getExpression()));

	return membrane;
}


/**
 * This method returns a XML representation of a ReactionContext object.
 * Creation date: (3/1/2001 9:03:52 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.mapping.ReactionContext
 */
public org.jdom.Element getXML(ReactionContext param) {
	org.jdom.Element reactioncontext = new 	org.jdom.Element(XMLTags.ReactionContextTag);

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

	return reactioncontext;
}


/**
 * This method returns a XML representation of a ReactionSpec object.
 * Creation date: (4/26/2001 3:07:29 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.mapping.ReactionSpec
 */
public org.jdom.Element getXML(ReactionSpec param) {
	org.jdom.Element reactionSpec = new org.jdom.Element(XMLTags.ReactionSpecTag);

	//Add Atributes
	reactionSpec.setAttribute( XMLTags.ReactionStepRefAttrTag, mangle(param.getReactionStep().getName()) );
	reactionSpec.setAttribute( XMLTags.ReactionMappingAttrTag, mangle(param.getReactionMappingDescription()) );
	
	return reactionSpec;
}


/**
 * This method returns a XML representation of a SimulationContext object.
 * Creation date: (2/22/2001 2:15:14 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.mapping.SimulationContext
 */
public org.jdom.Element getXML(cbit.vcell.mapping.SimulationContext param, cbit.vcell.biomodel.BioModel bioModel) throws XmlParseException{
	org.jdom.Element simulationcontext = new org.jdom.Element(XMLTags.SimulationSpecTag);

	//add attributes
	String name = mangle(param.getName());
	simulationcontext.setAttribute(XMLTags.NameAttrTag, name);
	//set isStoch, isUsingConcentration attributes
	if (param.isStoch())
	{
		simulationcontext.setAttribute(XMLTags.StochAttrTag, "true");
		if(param.isUsingConcentration())
		{
			simulationcontext.setAttribute(XMLTags.ConcentrationAttrTag, "true");
		}	
		else
		{
			simulationcontext.setAttribute(XMLTags.ConcentrationAttrTag, "false");
		}
	}
	else
	{
		simulationcontext.setAttribute(XMLTags.StochAttrTag, "false");
		simulationcontext.setAttribute(XMLTags.ConcentrationAttrTag, "true");
	}
	//simulationcontext.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(param.getDescription()));
	//add annotation
	if (param.getDescription()!=null && param.getDescription().length()>0) {
		org.jdom.Element annotationElem = new org.jdom.Element(XMLTags.AnnotationTag);
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
		throw new XmlParseException("A problem occurred when trying to process the geometry for the simulationContext " + name+" : "+e.getMessage());
	}
	// write GeometryContext (geometric mapping)
	simulationcontext.addContent(getXML(param.getGeometryContext()));
	// write ReactionContext (parameter/variable mapping)
	simulationcontext.addContent(getXML(param.getReactionContext()));

	//chech if there is anything to write first for the electricla context
	if (param.getElectricalStimuli().length==1 || param.getGroundElectrode()!=null) {
		//create ElectricalContext
		org.jdom.Element electricalElement = new org.jdom.Element(XMLTags.ElectricalContextTag);

		//Write the electrical stimuli
		if (param.getElectricalStimuli().length==1) {
			//write clamp
			electricalElement.addContent(getXML(param.getElectricalStimuli(0)));

			//org.jdom.Element clampElem = new org.jdom.Element(XMLTags.ClampTag);
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
		if(outputFunctions != null) {
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
		simulationcontext.addContent( getXML(param.getAnalysisTasks()) );
	}
	
	// Add (Bio)events
	if (param.getBioEvents()!=null && param.getBioEvents().length>0){
		simulationcontext.addContent( getXML(param.getBioEvents()) );
	}
	
	//Add Metadata (if any)
	if ( param.getVersion() != null) {
		simulationcontext.addContent( getXML(param.getVersion(), param) );
	}
		
	return simulationcontext;
}


/**
 * This method returns a XML representation of a SpeciesContextSpec object.
 * Creation date: (3/1/2001 9:13:56 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.mapping.SpeciesContextSpec
 */
public org.jdom.Element getXML(SpeciesContextSpec param) {
	org.jdom.Element speciesContextSpecElement = new org.jdom.Element(XMLTags.SpeciesContextSpecTag);

	//Add Attributes
	speciesContextSpecElement.setAttribute(XMLTags.SpeciesContextRefAttrTag, mangle(param.getSpeciesContext().getName()));
	speciesContextSpecElement.setAttribute(XMLTags.ForceConstantAttrTag, String.valueOf(param.isConstant()));
	speciesContextSpecElement.setAttribute(XMLTags.EnableDiffusionAttrTag, String.valueOf(param.isEnableDiffusing()));

	//Add initial
	Expression initCon = param.getInitialConcentrationParameter().getExpression();
	Expression initAmt = param.getInitialCountParameter().getExpression();
	if (initCon != null)
	{
		org.jdom.Element initial = new org.jdom.Element(XMLTags.InitialConcentrationTag);
		initial.addContent(mangleExpression(initCon));
		speciesContextSpecElement.addContent( initial );
	}
	else if(initAmt != null)
	{
		org.jdom.Element initial = new org.jdom.Element(XMLTags.InitialAmountTag);
		initial.addContent(mangleExpression(initAmt));
		speciesContextSpecElement.addContent( initial );
	}
	//Add diffusion
	cbit.vcell.parser.Expression diffRate = param.getDiffusionParameter().getExpression();
	if (diffRate!=null){
		org.jdom.Element diffusion = new org.jdom.Element(XMLTags.DiffusionTag);
		diffusion.addContent(mangleExpression(diffRate));
		speciesContextSpecElement.addContent(diffusion);
	}
	// write BoundaryConditions
	cbit.vcell.parser.Expression exp;
	org.jdom.Element boundaries = new org.jdom.Element(XMLTags.BoundariesTag);

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
 * @return org.jdom.Element
 * @param param cbit.vcell.math.Action
 */
public Element getXML(Action param) 
{
	org.jdom.Element action = new org.jdom.Element(XMLTags.ActionTag);

	//Add atributes
	action.setAttribute(XMLTags.VarNameAttrTag, mangle(param.getVar().getName()));
	action.setAttribute(XMLTags.OperationAttrTag, mangle(param.getOperation()));
	action.addContent( mangleExpression(param.getOperand()));
	return action;
}


/**
 * This method returns a XML representation of a CompartmentSubDomain object.
 * Creation date: (3/2/2001 1:18:55 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.CompartmentSubDomain
 */
public org.jdom.Element getXML(cbit.vcell.math.CompartmentSubDomain param) throws XmlParseException{
	org.jdom.Element compartment = new org.jdom.Element(XMLTags.CompartmentSubDomainTag);

	compartment.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	if (param.getPriority() != -1){
			compartment.setAttribute(XMLTags.PriorityAttrTag, String.valueOf(param.getPriority()));
	}
	//Add boundatyType subelements
	org.jdom.Element boundary;
	//Xm
	boundary = new org.jdom.Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueXm);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionXm().toString());
	compartment.addContent(boundary);
	//Xp
	boundary = new org.jdom.Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueXp);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionXp().toString());
	compartment.addContent(boundary);
	//Ym
	boundary = new org.jdom.Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueYm);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionYm().toString());
	compartment.addContent(boundary);
	//Yp
	boundary = new org.jdom.Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueYp);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionYp().toString());
	compartment.addContent(boundary);
	//Zm
	boundary = new org.jdom.Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueZm);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionZm().toString());
	compartment.addContent(boundary);
	//Zp
	boundary = new org.jdom.Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueZp);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionZp().toString());
	compartment.addContent(boundary);
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
	Enumeration<VarIniCondition> varInis = param.getVarIniConditions().elements();
	while (varInis.hasMoreElements())
	{
		VarIniCondition varIni = varInis.nextElement();
		compartment.addContent(getXML(varIni));
	}
	//Add JumpProcesses
	Enumeration<JumpProcess> jumps = param.getJumpProcesses().elements();
	while (jumps.hasMoreElements())
	{
		JumpProcess jp = jumps.nextElement();
		compartment.addContent(getXML(jp));
	}
	return compartment;
}


/**
 * This method returns a XML representation of a Constant object.
 * Creation date: (3/2/2001 11:46:46 AM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.Constant
 */
public org.jdom.Element getXML(Constant param) {
	org.jdom.Element constant = new org.jdom.Element(XMLTags.ConstantTag);

	//Add atributes
	constant.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	constant.addContent(mangleExpression(param.getExpression()) );

	return constant;
}


/**
 * This method returns a XML representation of a Equation object.
 * Creation date: (3/2/2001 1:56:34 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.Equation
 */
public org.jdom.Element getXML(Equation param) throws XmlParseException{
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
	else {
		throw new XmlParseException("Unknown equation type: " + param.getClass().getName());
	}
}

/**
 * This method returns a XML representation of a FastSystemImplicit object.
 * Creation date: (3/2/2001 4:05:28 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.FastSystemImplicit
 */
public org.jdom.Element getXML(FastSystem param) {
	org.jdom.Element fastsystem = new org.jdom.Element(XMLTags.FastSystemTag);

	//Add Fast Invariant subelements
	org.jdom.Element fastinvariant;
	Enumeration<FastInvariant> enum_fi = param.getFastInvariants();
	while (enum_fi.hasMoreElements()){
		fastinvariant = new org.jdom.Element(XMLTags.FastInvariantTag);
		FastInvariant fi = enum_fi.nextElement();
		fastinvariant.addContent(mangleExpression(fi.getFunction()));
		fastsystem.addContent(fastinvariant);
	}	
	//Add FastRate subelements
	org.jdom.Element fastrate;
	Enumeration<FastRate> enum_fr = param.getFastRates();
	while (enum_fr.hasMoreElements()){
		FastRate fr = (FastRate)enum_fr.nextElement();
		fastrate = new org.jdom.Element(XMLTags.FastRateTag);
		fastrate.addContent(mangleExpression(fr.getFunction()));
		fastsystem.addContent(fastrate);
	}
		
	return fastsystem;
}


/**
 * This method returns a XML representation of a FilamentRegionVariable object.
 * Creation date: (3/2/2001 11:46:46 AM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.FilamentRegionVariable
 */
public org.jdom.Element getXML(FilamentRegionVariable param) {
	org.jdom.Element filregvar = new org.jdom.Element(XMLTags.FilamentRegionVariableTag);

	//Add atributes
	filregvar.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));

	return filregvar;
}


/**
 * This method returns a XML representation of a FilamentSubDomain object.
 * Creation date: (3/2/2001 5:49:07 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.FilamentSubDomain
 */
public org.jdom.Element getXML(FilamentSubDomain param) throws XmlParseException{
	org.jdom.Element filament = new org.jdom.Element(XMLTags.FilamentSubDomainTag);
	
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
 * @return org.jdom.Element
 * @param param cbit.vcell.math.FilamentVariable
 */
public org.jdom.Element getXML(FilamentVariable param) {
	org.jdom.Element filvar = new org.jdom.Element(XMLTags.FilamentVariableTag);

	//Add atributes
	filvar.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));

	return filvar;
}


/**
 * This method returns a XML declaration of a Function object.
 * Creation date: (3/2/2001 1:08:13 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.Function
 */
public org.jdom.Element getXML(Function param) {
	org.jdom.Element function = new org.jdom.Element(XMLTags.FunctionTag);

	//Add atributes
	function.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	function.addContent(mangleExpression(param.getExpression()) );

	return function;
}

public org.jdom.Element getXML(AnnotatedFunction param) {
	org.jdom.Element function = new org.jdom.Element(XMLTags.AnnotatedFunctionTag);

	//Add atributes
	function.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	if (param.getErrorString() != null) {
		function.setAttribute(XMLTags.ErrorStringTag, param.getErrorString());
	} else {
		function.setAttribute(XMLTags.ErrorStringTag, "");
	}
	function.setAttribute(XMLTags.FunctionTypeTag, param.getFunctionType().getTypeName());
	function.setAttribute(XMLTags.UserDefinedTag, Boolean.toString(param.isUserDefined()));
	function.addContent(mangleExpression(param.getExpression()) );

	return function;
}

/**
 * This method returns a XML representation of a JumpCondition object.
 * Creation date: (3/2/2001 2:05:17 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.JumpCondition
 */
public org.jdom.Element getXML(JumpCondition param) {
	org.jdom.Element jump = new org.jdom.Element(XMLTags.JumpConditionTag);

	//add Atributes
	jump.setAttribute(XMLTags.NameAttrTag, mangle(param.getVariable().getName()));
	//add Influx subelement
	org.jdom.Element influx = new org.jdom.Element(XMLTags.InFluxTag);
	if (param.getInFluxExpression() != null) {
		influx.addContent(mangleExpression(param.getInFluxExpression()) );
	} else {
		influx.addContent("0.0");
	}
	jump.addContent(influx);
	//Add OutFlux subelement
	 org.jdom.Element outflux = new  org.jdom.Element(XMLTags.OutFluxTag);
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
 * @return org.jdom.Element
 * @param param cbit.vcell.math.JumpProcess
 */
public Element getXML(JumpProcess param) 
{
	org.jdom.Element jump = new org.jdom.Element(XMLTags.JumpProcessTag);
	//name
	jump.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	//probability rate
	org.jdom.Element prob = new org.jdom.Element(XMLTags.ProbabilityRateTag);
	prob.addContent(mangleExpression(param.getProbabilityRate()) );
	jump.addContent(prob);
	//Actions
	Enumeration<Action> actions = param.getActions().elements();
	while (actions.hasMoreElements())
	{
		Action action = actions.nextElement();
		jump.addContent(getXML(action));
	}

	return jump;
}


/**
 * This methos returns a XML representation of a MathDescription object.
 * Creation date: (3/2/2001 10:57:25 AM)
 * @return org.jdom.Element
 * @param mathdes cbit.vcell.math.MathDescription
 */
public org.jdom.Element getXML(MathDescription mathdes) throws XmlParseException {
    org.jdom.Element math = new org.jdom.Element(XMLTags.MathDescriptionTag);

    //Add atributes
    math.setAttribute(XMLTags.NameAttrTag, mangle(mathdes.getName()));
    //math.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(mathdes.getDescription()));
    //Add annotation
    if (mathdes.getDescription()!=null && mathdes.getDescription().length()>0) {
    	org.jdom.Element annotationElem = new org.jdom.Element(XMLTags.AnnotationTag);
    	annotationElem.setText(mangle(mathdes.getDescription()));
    	math.addContent(annotationElem);
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
    //while (k.hasNext()) {
	    //Variable var = (Variable)k.next();
	    
        if (var instanceof Constant) {
            math.addContent(getXML((Constant) var));
        }
        else if (var instanceof FilamentRegionVariable) {
            math.addContent(getXML((FilamentRegionVariable) var));
        }
        else if (var instanceof FilamentVariable) {
            math.addContent(getXML((FilamentVariable) var));
        }
        else if (var instanceof Function) {
            math.addContent(getXML((Function) var));
        }
        else if (var instanceof RandomVariable) {
            math.addContent(getXML((RandomVariable) var));
        }
        else if (var instanceof InsideVariable) {
	        //*** for internal use! Ignore it ***
	        continue;
        }
        else if (var instanceof MembraneRegionVariable) {
	        math.addContent(getXML((MembraneRegionVariable) var));
        }
        else if (var instanceof MemVariable) {
            math.addContent(getXML((MemVariable) var));
        }         
        else if (var instanceof OutsideVariable) {
	        //*** for internal use! Ignore it ****
	        continue;
        }
        else if (var instanceof VolumeRegionVariable) {
            math.addContent(getXML((VolumeRegionVariable) var));
        }
        else if (var instanceof VolVariable) {
            math.addContent(getXML((VolVariable) var));
        }
        else if (var instanceof StochVolVariable) { //added for stochastic volumn variables
            math.addContent(getXML((StochVolVariable) var));
        }
        else {
	        throw new XmlParseException("An unknown variable type "+var.getClass().getName()+" was found when parsing the mathdescription "+ mathdes.getName() +"!");
        }
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

    return math;
}


private Element getXML(RandomVariable var) {
	org.jdom.Element randomVariableElement = null;
	if (var instanceof VolumeRandomVariable) {
		randomVariableElement = new org.jdom.Element(XMLTags.VolumeRandomVariableTag);
	} else {
		randomVariableElement = new org.jdom.Element(XMLTags.MembraneRandomVariableTag);
	}

	randomVariableElement.setAttribute(XMLTags.NameAttrTag, mangle(var.getName()));
	
	Element seedElement = new Element(XMLTags.RandomVariableSeedTag);
	seedElement.addContent(mangleExpression(var.getSeed()));
	randomVariableElement.addContent(seedElement);
	
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
 * @return org.jdom.Element
 * @param param cbit.vcell.math.MembraneRegionEquation
 */
public org.jdom.Element getXML(MembraneRegionEquation param) {
	org.jdom.Element memregeq = new org.jdom.Element(XMLTags.MembraneRegionEquationTag);

	//add name
	memregeq.setAttribute(XMLTags.NameAttrTag, mangle(param.getVariable().getName()));
	
	//add uniform rate
	org.jdom.Element tempElem = null;
	String tempString;
	
	if (param.getUniformRateExpression() != null){
		tempString = mangleExpression(param.getUniformRateExpression());
		//buffer.append("\t\t"+VCML.UniformRate+" "+getUniformRateExpression()+";\n");
	}else{
		tempString = "0.0";
		//buffer.append("\t\t"+VCML.UniformRate+" "+"0.0;\n");
	}
	tempElem = new org.jdom.Element(XMLTags.UniformRateTag);
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
	tempElem = new org.jdom.Element(XMLTags.MembraneRateTag);
	tempElem.setText(tempString);
	memregeq.addContent(tempElem);
	
	//add initial
	if (param.getInitialExpression() != null){
		tempElem = new org.jdom.Element( XMLTags.InitialTag );
		tempElem.setText(mangleExpression(param.getInitialExpression()) );
		memregeq.addContent(tempElem);
//		buffer.append("\t\t"+VCML.Initial+"\t "+initialExp.infix()+";\n");
	}
	
	//add solutionType
	tempElem = new org.jdom.Element(XMLTags.SolutionTypeTag);
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
 * @return org.jdom.Element
 * @param param cbit.vcell.math.MembraneRegionVariable
 */
public org.jdom.Element getXML(MembraneRegionVariable param) {
	org.jdom.Element memregvar = new org.jdom.Element(XMLTags.MembraneRegionVariableTag);

	//Add atributes
	memregvar.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));

	return memregvar;
}


/**
 * This method returns a XML representation of a MembraneSubDomain object.
 * Creation date: (3/2/2001 5:40:17 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.MembraneSubDomain
 */
public org.jdom.Element getXML(MembraneSubDomain param) throws XmlParseException{
	org.jdom.Element membrane = new org.jdom.Element(XMLTags.MembraneSubDomainTag);
	
	//Add attributes
	membrane.setAttribute(XMLTags.InsideCompartmentTag, mangle(param.getInsideCompartment().getName()));
	membrane.setAttribute(XMLTags.OutsideCompartmentTag, mangle(param.getOutsideCompartment().getName()));
	
	//Add boundatyType subelements
	org.jdom.Element boundary;
	//Xm
	boundary = new org.jdom.Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueXm);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionXm().toString());
	membrane.addContent(boundary);
	//Xp
	boundary = new org.jdom.Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueXp);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionXp().toString());
	membrane.addContent(boundary);
	//Ym
	boundary = new org.jdom.Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueYm);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionYm().toString());
	membrane.addContent(boundary);
	//Yp
	boundary = new org.jdom.Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueYp);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionYp().toString());
	membrane.addContent(boundary);
	//Zm
	boundary = new org.jdom.Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueZm);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionZm().toString());
	membrane.addContent(boundary);
	//Zp
	boundary = new org.jdom.Element(XMLTags.BoundaryTypeTag);
	boundary.setAttribute(XMLTags.BoundaryAttrTag, XMLTags.BoundaryAttrValueZp);
	boundary.setAttribute(XMLTags.BoundaryTypeAttrTag, param.getBoundaryConditionZp().toString());
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
	
	return membrane;
}


/**
 * This method returns a XML representation of a MemVariable object.
 * Creation date: (3/2/2001 1:03:35 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.MemVariable
 */
public org.jdom.Element getXML(MemVariable param) {
	org.jdom.Element memvariable = new org.jdom.Element(XMLTags.MembraneVariableTag);

	//Add atributes
	memvariable.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));

	return memvariable;
}


/**
 * This method returns a XML representation of a OdeEquation object.
 * Creation date: (3/2/2001 2:52:14 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.OdeEquation
 */
public org.jdom.Element getXML(OdeEquation param) throws XmlParseException {
	org.jdom.Element ode = new org.jdom.Element(XMLTags.OdeEquationTag);

	//Add atribute
	ode.setAttribute(XMLTags.NameAttrTag, mangle(param.getVariable().getName()));
	//Add Rate subelement
	org.jdom.Element rate = new org.jdom.Element(XMLTags.RateTag);
	if (param.getRateExpression() != null) {
		rate.addContent(mangleExpression(param.getRateExpression()));
	} else {
		rate.addContent("0.0");
	}
	ode.addContent(rate);
	//Add Initial
	org.jdom.Element initial = new org.jdom.Element(XMLTags.InitialTag);
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
			org.jdom.Element solution = new org.jdom.Element(XMLTags.SolutionExpressionTag);
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
 * @return org.jdom.Element
 * @param param cbit.vcell.math.PdeEquation
 */
public org.jdom.Element getXML(PdeEquation param) throws XmlParseException {
	org.jdom.Element pde = new org.jdom.Element(XMLTags.PdeEquationTag);

	//Add Atribute
	pde.setAttribute(XMLTags.NameAttrTag, mangle(param.getVariable().getName()));
	if (param.isSteady()) {
		pde.setAttribute(XMLTags.SteadyTag, "1");
	}
	//Add Boundary subelements
	org.jdom.Element boundaries = new org.jdom.Element(XMLTags.BoundariesTag);
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
	//add Rate
	org.jdom.Element rate = new org.jdom.Element(XMLTags.RateTag);
	if (param.getRateExpression() != null) {
		rate.addContent(mangleExpression(param.getRateExpression()));
	} else {
		rate.addContent(mangleExpression(new cbit.vcell.parser.Expression(0.0)));
	}
	pde.addContent(rate);
	//Diffusion
	org.jdom.Element diffusion = new org.jdom.Element(XMLTags.DiffusionTag);
	if (param.getDiffusionExpression() != null) {
		diffusion.addContent(mangleExpression(param.getDiffusionExpression()));
	} else {
		diffusion.addContent(mangleExpression(new cbit.vcell.parser.Expression(0.0)) );
	}
	pde.addContent(diffusion);
	//Initial
	org.jdom.Element initial = new org.jdom.Element(XMLTags.InitialTag);
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
			org.jdom.Element solution = new org.jdom.Element(XMLTags.SolutionExpressionTag);
			solution.setText( mangle(param.getExactSolution().infix()) );
			pde.addContent(solution);

			break;
		}
		default: {
			throw new XmlParseException("Unknown solution type:"+param.getSolutionType());
		}
	}

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

	return pde;
}


/**
 * Get a XML element for this stochastic volumn variable.
 * Creation date: (7/19/2006 5:53:15 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.StochVolVariable
 */
public Element getXML(StochVolVariable param) {
	org.jdom.Element stochVar = new org.jdom.Element(XMLTags.StochVolVariableTag);

	//Add atribute
	stochVar.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));

	return stochVar;
}

/**
 * This method returns a XML representation of a SubDomain object type.
 * Creation date: (3/2/2001 1:13:30 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.SubDomain
 */
public org.jdom.Element getXML(SubDomain param) throws XmlParseException{
	if (param instanceof CompartmentSubDomain) {
		return getXML((CompartmentSubDomain)param);
	} else if (param instanceof FilamentSubDomain) {
		return getXML((FilamentSubDomain)param);
	} else if (param instanceof MembraneSubDomain) {
		return getXML((MembraneSubDomain)param);
	}
	
	return null;
}


/**
 * Get a XML element for the initial condition of a stochastic variable.
 * Creation date: (7/21/2006 10:54:51 AM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.VarIniCondition
 */
public Element getXML(VarIniCondition param) 
{
	org.jdom.Element varIni = new org.jdom.Element(XMLTags.VarIniConditionTag);

	//Add atribute
	varIni.setAttribute(XMLTags.NameAttrTag, mangle(param.getVar().getName()));
	varIni.addContent(mangleExpression(param.getIniVal()));
	return varIni;
}

/**
 * This method returns a XML representation of a VolumeRegionEquation object.
 * Creation date: (3/2/2001 2:05:17 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.VolumeRegionEquation
 */
public org.jdom.Element getXML(VolumeRegionEquation param) {
	org.jdom.Element memregeq = new org.jdom.Element(XMLTags.VolumeRegionEquationTag);
	org.jdom.Element tempElem = null;
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
	tempElem = new org.jdom.Element(XMLTags.UniformRateTag);
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
	tempElem = new org.jdom.Element(XMLTags.VolumeRateTag);
	tempElem.setText(tempString);
	memregeq.addContent(tempElem);
	
	//add Initial
	if (param.getInitialExpression() != null){
		tempElem = new org.jdom.Element( XMLTags.InitialTag );
		tempElem.setText(mangleExpression(param.getInitialExpression()) );
		memregeq.addContent(tempElem);
//		buffer.append("\t\t"+VCML.Initial+"\t "+initialExp.infix()+";\n");
	}

	//add solutionType
	tempElem = new org.jdom.Element(XMLTags.SolutionTypeTag);
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
 * This method returns a XML representation of a VolumeRegionVariable object.
 * Creation date: (3/2/2001 11:56:48 AM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.VolumeRegionVariable
 */
public org.jdom.Element getXML(VolumeRegionVariable param) {
	org.jdom.Element volregvar = new org.jdom.Element(XMLTags.VolumeRegionVariableTag);

	//Add atribute
	volregvar.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));

	return volregvar;
}


/**
 * This method returns a XML representation of a VolVariable object.
 * Creation date: (3/2/2001 11:56:48 AM)
 * @return org.jdom.Element
 * @param param cbit.vcell.math.VolVariable
 */
public org.jdom.Element getXML(VolVariable param) {
	org.jdom.Element volvariable = new org.jdom.Element(XMLTags.VolumeVariableTag);

	//Add atribute
	volvariable.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));

	return volvariable;
}


/**
 * This method returns the XML representation of a MathModel.
 * Creation date: (3/28/2001 12:27:28 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.mathmodel.MathModel
 */
public org.jdom.Element getXML(cbit.vcell.mathmodel.MathModel param) throws XmlParseException{
	org.jdom.Element mathmodel = new org.jdom.Element(XMLTags.MathModelTag);
	//Add Attributes
	String name = param.getName();
	mathmodel.setAttribute(XMLTags.NameAttrTag, mangle(name));
	//mathmodel.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(param.getDescription()));
	//add Annotation
	if (param.getDescription()!=null && param.getDescription().length()>0) {
		org.jdom.Element annotationElem = new org.jdom.Element(XMLTags.AnnotationTag);
		annotationElem.setText(mangle(param.getDescription()));
		mathmodel.addContent(annotationElem);
	}
	//Add Subelements
	//Add Geometry
	try {
		mathmodel.addContent( getXML(param.getMathDescription().getGeometry()) );
	} catch (XmlParseException e) {
		e.printStackTrace();
		throw new XmlParseException("A problem occurred when trying to process the geometry!"+" : "+e.getMessage());
	}
	//Add Mathdescription
	mathmodel.addContent( getXML(param.getMathDescription()) );
	
	// Add output functions
	if (param.getOutputFunctionContext() != null) {
		ArrayList<AnnotatedFunction> outputFunctions = param.getOutputFunctionContext().getOutputFunctionsList();
		if(outputFunctions != null) {
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
 * @return org.jdom.Element
 * @param param cbit.vcell.model.Catalyst
 */
public org.jdom.Element getXML(Catalyst param) {
	org.jdom.Element catalyst = new org.jdom.Element(XMLTags.CatalystTag);

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
 * @return org.jdom.Element
 * @param param cbit.vcell.model.Diagram
 */
public org.jdom.Element getXML(Diagram param) {
	org.jdom.Element diagram = new org.jdom.Element(XMLTags.DiagramTag);

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
 * This method returns a XML version of a Feature.
 * Creation date: (2/22/2001 7:05:40 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.model.Feature
 * @param model cbit.vcell.model.Model
 * @deprecated This methos is no longer in use. Functionnality moved to the get-Structures.
 */
public org.jdom.Element getXML(Feature param/*, Model model*/) {
	org.jdom.Element feature = new org.jdom.Element(XMLTags.FeatureTag);
	
	//Get parameters
	feature.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	/******* not any more in use***** Species contexts moved to the Model level ***
	//Get SpeciesContexts
	SpeciesContext[] array = model.getSpeciesContexts(param);
	for (int i=0 ; i< array.length ; i++) {
		feature.addContent( getXML((SpeciesContext)array[i]) );
	}*********************/
	
	return feature;
}


/**
 * This method returns a XML representation of a Flux Reaction object.
 * Creation date: (2/26/2001 12:30:13 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.model.FluxReaction
 */
public org.jdom.Element getXML(FluxReaction param) throws XmlParseException {
	org.jdom.Element fluxreaction = new org.jdom.Element(XMLTags.FluxStepTag);
	//get Attributes
	String versionName = (param.getName() != null) ? mangle(param.getName()) : "unnamed_fluxReaction";
	fluxreaction.setAttribute(XMLTags.NameAttrTag, versionName);
	fluxreaction.setAttribute(XMLTags.StructureAttrTag, mangle(param.getStructure().getName()));
	
	if (param.getFluxCarrier() != null) {
		fluxreaction.setAttribute(XMLTags.FluxCarrierAttrTag, mangle(param.getFluxCarrier().getCommonName()));		
	}
	Expression tempExp = null;
	int valence;
	try {
		tempExp = param.getChargeCarrierValence().getExpression();
		double d = (int)tempExp.evaluateConstant();
		if ((int)d != d) {
			throw new XmlParseException("Invalid value for charge valence: " + d + " for reaction: " + param.getName());
		}
		valence = (int)d;
	} catch (ExpressionException e) {
		e.printStackTrace();
		throw new XmlParseException("Invalid value for the charge valence: " + 
									(tempExp == null ? "null": tempExp.infix()) + " for reaction: " + param.getName()+" : "+e.getMessage());
	}
	fluxreaction.setAttribute(XMLTags.FluxCarrierValenceAttrTag, String.valueOf(valence));
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

	//
	// write Catalysts
	//
	ReactionParticipant rpArray[] = param.getReactionParticipants();
	for (int i = 0; i < rpArray.length; i++){
		if (rpArray[i] instanceof Catalyst) {
			fluxreaction.addContent( getXML((Catalyst)rpArray[i]) );
		} else if (rpArray[i] instanceof Flux) {
	        //ignore a Flux object
		}/* else {
				throw new Error("expecting catalyst, found type " + rp.getClass());
			}*/
	}
	//Add Kinetics	
	fluxreaction.addContent( getXML(param.getKinetics()) );

	return fluxreaction;
}


/**
 * This method returns a XML representation of a Kinetics object type.
 * Creation date: (2/26/2001 7:31:43 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.model.Kinetics
 */
public org.jdom.Element getXML(Kinetics param) throws XmlParseException {

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
	} 
	org.jdom.Element kinetics = new org.jdom.Element(XMLTags.KineticsTag);
	//Add atributes
	kinetics.setAttribute(XMLTags.KineticsTypeAttrTag, kineticsType);
	//Add Kinetics Parameters
	Kinetics.KineticsParameter parameters[] = param.getKineticsParameters();
	for (int i=0;i<parameters.length;i++){
		Kinetics.KineticsParameter parm = parameters[i];
		org.jdom.Element tempparameter = new org.jdom.Element(XMLTags.ParameterTag);
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


/**
 * This method returns a XMl representation of a Membrane object.
 * Creation date: (2/26/2001 11:56:13 AM)
 * @return org.jdom.Element
 * @param param cbit.vcell.model.Membrane
 * @param model cbit.vcell.model.Model
 * @deprecated This method is no longer in use.
 */
public org.jdom.Element getXML(Membrane param/*, Model model*/) {
	org.jdom.Element membrane = new org.jdom.Element(XMLTags.MembraneTag);
	//Add Atributes
	membrane.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	membrane.setAttribute(XMLTags.InsideFeatureTag, mangle(param.getInsideFeature().getName()));
	membrane.setAttribute(XMLTags.OutsideFeatureTag, mangle(param.getOutsideFeature().getName()));
	//*****not any more in use***** speciesContexts moved to Model level********
	//Add SpeciesContexts			
	/*SpeciesContext[] array = model.getSpeciesContexts(param);
	for (int i=0 ; i<array.length ; i++){
		membrane.addContent( getXML(array[i]) );
	}**************/

	return membrane;
}

public org.jdom.Element getXML(ArrayList<AnnotatedFunction> outputFunctions) {
	Element outputFunctionsElement = new Element(XMLTags.OutputFunctionsTag);
	for (AnnotatedFunction outputfunction : outputFunctions) {
		Element functionElement = getXML(outputfunction);
		outputFunctionsElement.addContent(functionElement);
	}

	return outputFunctionsElement;
}
public org.jdom.Element getXML(ModelParameter[] modelParams) {
	Element globalsElement = new Element(XMLTags.ModelParametersTag);
	for (int i = 0; i < modelParams.length; i++) {
		Element glParamElement = new Element(XMLTags.ParameterTag);
		//Get parameter attributes - name, role and unit definition
		glParamElement.setAttribute(XMLTags.NameAttrTag, mangle(modelParams[i].getName()));
		if (modelParams[i].getRole() == Model.ROLE_UserDefined) {
			glParamElement.setAttribute(XMLTags.ParamRoleAttrTag, Model.RoleDesc);
		} else {
			throw new RuntimeException("Unknown model parameter role/type");
		}
		VCUnitDefinition unit = modelParams[i].getUnitDefinition();
		if (unit != null) {
			glParamElement.setAttribute(XMLTags.VCUnitDefinitionAttrTag, unit.getSymbol());
		}
		// add expression as content
		glParamElement.addContent(mangleExpression(modelParams[i].getExpression()) );
		//add annotation (if there is any)
		if (modelParams[i].getModelParameterAnnotation() != null &&
				modelParams[i].getModelParameterAnnotation().length() > 0) {
			Element annotationElement = new Element(XMLTags.AnnotationTag);
			annotationElement.setText(mangle(modelParams[i].getModelParameterAnnotation()));
			glParamElement.addContent(annotationElement);
		}
		globalsElement.addContent(glParamElement);
	}

	return globalsElement;
}


/**
 * Outputs a XML version of a Model object
 * Creation date: (2/15/2001 11:39:27 AM)
 * @return org.jdom.Element
 * @param param cbit.vcell.model.Model
 */
public org.jdom.Element getXML(cbit.vcell.model.Model param) throws XmlParseException/*, cbit.vcell.parser.ExpressionException */{
	org.jdom.Element modelnode = new org.jdom.Element(XMLTags.ModelTag);
	String versionName = (param.getName()!=null)?mangle(param.getName()):"unnamed_model";

	//get Attributes
	modelnode.setAttribute(XMLTags.NameAttrTag, versionName);
	//modelnode.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(param.getDescription()));
	if (param.getDescription()!=null && param.getDescription().length()>0) {
		org.jdom.Element annotationElem = new org.jdom.Element(XMLTags.AnnotationTag);
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
			Element structure = getXML(structarray[i]);
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
		throw new XmlParseException("An error occurred while procesing a Structure for the model " + versionName+" : "+e.getMessage());
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
	//Get Diagrams
	Diagram[] diagarray = param.getDiagrams();
	for (int i=0 ; i<diagarray.length ; i++){
		modelnode.addContent( getXML(diagarray[i]) );
	}
	//Add Metadata information
	if (param.getVersion()!= null) {
		modelnode.addContent( getXML(param.getVersion(), param) );
	}
	
	return modelnode;
}


/**
 * This method returns the XML representation of a NodeReference type object.
 * Creation date: (2/27/2001 5:32:32 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.model.NodeReference
 */
public org.jdom.Element getXML(NodeReference param) {
	switch (param.nodeType){
		case NodeReference.SIMPLE_REACTION_NODE:{
			org.jdom.Element simplereaction = new org.jdom.Element(XMLTags.SimpleReactionShapeTag);
			//add Attributes
			simplereaction.setAttribute(XMLTags.SimpleReactionRefAttrTag, mangle(param.getName()));
			simplereaction.setAttribute(XMLTags.LocationXAttrTag, String.valueOf(param.location.x));
			simplereaction.setAttribute(XMLTags.LocationYAttrTag, String.valueOf(param.location.y));

			return simplereaction;
		}
		case NodeReference.FLUX_REACTION_NODE:{
			org.jdom.Element fluxreaction = new org.jdom.Element(XMLTags.FluxReactionShapeTag);
			//add Attributes
			fluxreaction.setAttribute(XMLTags.FluxReactionRefAttrTag, mangle(param.getName()));
			fluxreaction.setAttribute(XMLTags.LocationXAttrTag, String.valueOf(param.location.x));
			fluxreaction.setAttribute(XMLTags.LocationYAttrTag, String.valueOf(param.location.y));

			return fluxreaction;
		}
		case NodeReference.SPECIES_CONTEXT_NODE:{
			org.jdom.Element speciecontext = new org.jdom.Element(XMLTags.SpeciesContextShapeTag);
			//add Attributes
			speciecontext.setAttribute(XMLTags.SpeciesContextRefAttrTag, mangle(param.getName()));
			speciecontext.setAttribute(XMLTags.LocationXAttrTag, String.valueOf(param.location.x));
			speciecontext.setAttribute(XMLTags.LocationYAttrTag, String.valueOf(param.location.y));

			return speciecontext;
		}
	}
	
	return null;
}


/**
 * this method returns a XML representation of a Product object.
 * Creation date: (2/27/2001 3:00:56 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.model.Product
 */
public org.jdom.Element getXML(Product param) {
	org.jdom.Element product = new org.jdom.Element(XMLTags.ProductTag);
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
 * @return org.jdom.Element
 * @param param cbit.vcell.model.Reactant
 */
public org.jdom.Element getXML(Reactant param) {
	org.jdom.Element reactant = new org.jdom.Element(XMLTags.ReactantTag);
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
 * @return org.jdom.Element
 * @param param cbit.vcell.model.ReactionParticipant
 */
public org.jdom.Element getXML(ReactionParticipant param) {
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
 * @return org.jdom.Element
 * @param param cbit.vcell.model.ReactionStep
 */
public org.jdom.Element getXML(ReactionStep param) throws XmlParseException {
	org.jdom.Element rsElement = null;
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
 * @return org.jdom.Element
 * @param param cbit.vcell.model.SimpleReaction
 */
public org.jdom.Element getXML(SimpleReaction param) throws XmlParseException {
	org.jdom.Element simplereaction = new org.jdom.Element(XMLTags.SimpleReactionTag);
	//Add attribute
	String nameStr = (param.getName()!=null)?(mangle(param.getName())):"unnamed_SimpleReaction";
	simplereaction.setAttribute(XMLTags.StructureAttrTag, mangle(param.getStructure().getName()));
	simplereaction.setAttribute(XMLTags.NameAttrTag, nameStr);
	Expression tempExp = null;
	int valence;
	try {
		tempExp = param.getChargeCarrierValence().getExpression();
		double d = (int)tempExp.evaluateConstant();
		if ((int)d != d) {
			throw new XmlParseException("Invalid value for charge valence: " + d + " for reaction: " + param.getName());
		}
		valence = (int)d;
	} catch (ExpressionException e) {
		e.printStackTrace();
		throw new XmlParseException("Invalid value for the charge valence: " + 
									(tempExp == null ? "null": tempExp.infix()) + " for reaction: " + param.getName()+" : "+e.getMessage());
	}
	simplereaction.setAttribute(XMLTags.FluxCarrierValenceAttrTag, String.valueOf(valence));
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
 * @return org.jdom.Element
 * @param param cbit.vcell.model.Species
 */
public org.jdom.Element getXML(Species species) throws XmlParseException {
	org.jdom.Element speciesElement = new org.jdom.Element(XMLTags.SpeciesTag);

	//add name
	speciesElement.setAttribute(XMLTags.NameAttrTag, mangle(species.getCommonName()) );
	
	//add annotation (if there is any)
	if (species.getAnnotation()!=null && species.getAnnotation().length()!=0) {
		//speciesElement.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(species.getAnnotation()) );
		org.jdom.Element annotationElem = new org.jdom.Element(XMLTags.AnnotationTag);
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
 * @return org.jdom.Element
 * @param param cbit.vcell.model.SpeciesContext
 */
public org.jdom.Element getXML(SpeciesContext param) {
	org.jdom.Element speciecontext = new org.jdom.Element( XMLTags.SpeciesContextTag);
	//Add atributes
	speciecontext.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
	speciecontext.setAttribute(XMLTags.SpeciesRefAttrTag, mangle(param.getSpecies().getCommonName()));
	speciecontext.setAttribute( XMLTags.StructureAttrTag, mangle(param.getStructure().getName()) );
	speciecontext.setAttribute( XMLTags.HasOverrideAttrTag, String.valueOf(param.getHasOverride()) );

	//If keyFlag is on print the Keyvalue
	if (param.getKey() !=null && this.printKeysFlag) {
		speciecontext.setAttribute(XMLTags.KeyValueAttrTag, param.getKey().toString());
	}
		
	return speciecontext;
}


/**
 * This method identifies if the structure as a parameter is a Feature or a Membrane, and then calls the respective getXML method.
 * Creation date: (2/22/2001 6:31:04 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.model.Structure
 * @param model cbit.vcell.model.Model
 */
public org.jdom.Element getXML(Structure structure) throws XmlParseException {
	org.jdom.Element structureElement = null;
	
    if (structure instanceof Feature) {
        //This is a Feature
        structureElement = new org.jdom.Element(XMLTags.FeatureTag);
    } else if (structure instanceof Membrane) {
	    //process a Membrane
	    structureElement = new org.jdom.Element(XMLTags.MembraneTag);
	    //add specific attributes
	    structureElement.setAttribute(XMLTags.InsideFeatureTag, mangle(((Membrane)structure).getInsideFeature().getName()));
		structureElement.setAttribute(XMLTags.OutsideFeatureTag, mangle(((Membrane)structure).getOutsideFeature().getName()));
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
 * @return org.jdom.Element
 * @param groupAccess cbit.vcell.server.GroupAccess
 */
public org.jdom.Element getXML(org.vcell.util.document.GroupAccess groupAccess) {
	org.jdom.Element groupElement = new org.jdom.Element(XMLTags.GroupAccessTag);
	
	if (groupAccess instanceof org.vcell.util.document.GroupAccessAll) {
		//case: ALL
		groupElement.setAttribute(XMLTags.TypeAttrTag, org.vcell.util.document.GroupAccess.GROUPACCESS_ALL.toString());		
	} else if (groupAccess instanceof org.vcell.util.document.GroupAccessNone) {
		//case: NONE
		groupElement.setAttribute(XMLTags.TypeAttrTag, org.vcell.util.document.GroupAccess.GROUPACCESS_NONE.toString());		
	} else {
		//case: SOME
		//*groupid
		groupElement.setAttribute(XMLTags.TypeAttrTag, groupAccess.getGroupid().toString());
		//*hash
		groupElement.setAttribute(XMLTags.HashAttrTag, ((org.vcell.util.document.GroupAccessSome)groupAccess).getHash().toString());
		//*users+hidden value
		//get normal users
		org.vcell.util.document.User[] users = ((org.vcell.util.document.GroupAccessSome)groupAccess).getNormalGroupMembers();
		for (int i = 0; i < users.length; i++){
			org.jdom.Element userElement = new org.jdom.Element(XMLTags.UserTag);
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
		users = ((org.vcell.util.document.GroupAccessSome)groupAccess).getHiddenGroupMembers();
		
		if (users != null) {
			for (int i = 0; i < users.length; i++){
				org.jdom.Element userElement = new org.jdom.Element(XMLTags.UserTag);
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
 * @return org.jdom.Element
 * @param param cbit.vcell.solver.ErrorTolerance
 */
public org.jdom.Element getXML(cbit.vcell.solver.ErrorTolerance param) {
	org.jdom.Element errortol = new org.jdom.Element(XMLTags.ErrorToleranceTag);

	//Add Atributes
	errortol.setAttribute(XMLTags.AbsolutErrorToleranceTag, String.valueOf(param.getAbsoluteErrorTolerance()));
	errortol.setAttribute(XMLTags.RelativeErrorToleranceTag, String.valueOf(param.getRelativeErrorTolerance()));
		
	return errortol;
}


/**
 * returns a XML representation of a MathOverrides.
 * Creation date: (3/3/2001 12:15:17 AM)
 * @return org.jdom.Element
 * @param param cbit.vcell.solver.MathOverrides
 */
public org.jdom.Element getXML(cbit.vcell.solver.MathOverrides param) {
	org.jdom.Element overrides = new org.jdom.Element(XMLTags.MathOverridesTag);

	//Add Constant subelements
	//Get names
	String[] constantNames = param.getOverridenConstantNames();
	//get the expressions
	for (int i = 0; i < constantNames.length; i++){
		org.jdom.Element constant = new org.jdom.Element(XMLTags.ConstantTag);
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
 * @return org.jdom.Element
 * @param param cbit.vcell.mesh.MeshSpecification
 */
public org.jdom.Element getXML(cbit.vcell.solver.MeshSpecification param) {
	org.jdom.Element meshspec = new org.jdom.Element(XMLTags.MeshSpecTag);

	org.jdom.Element size = new org.jdom.Element(XMLTags.SizeTag);
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
 * @return org.jdom.Element
 * @param param cbit.vcell.solver.ErrorTolerance
 */
public org.jdom.Element getXML(cbit.vcell.solver.OutputTimeSpec param) {
	org.jdom.Element outputOptions = new org.jdom.Element(XMLTags.OutputOptionsTag);

	//Add Atributes
	if (param.isDefault()){
		cbit.vcell.solver.DefaultOutputTimeSpec dots = (cbit.vcell.solver.DefaultOutputTimeSpec)param;
		outputOptions.setAttribute(cbit.vcell.xml.XMLTags.KeepEveryAttrTag, String.valueOf(dots.getKeepEvery()));
		outputOptions.setAttribute(cbit.vcell.xml.XMLTags.KeepAtMostAttrTag, String.valueOf(dots.getKeepAtMost()));
	}else if (param.isExplicit()){
		cbit.vcell.solver.ExplicitOutputTimeSpec eots = (cbit.vcell.solver.ExplicitOutputTimeSpec)param;
		outputOptions.setAttribute(cbit.vcell.xml.XMLTags.OutputTimesAttrTag, eots.toCommaSeperatedOneLineOfString());
	}else if (param.isUniform()){
		cbit.vcell.solver.UniformOutputTimeSpec uots = (cbit.vcell.solver.UniformOutputTimeSpec)param;
		outputOptions.setAttribute(cbit.vcell.xml.XMLTags.OutputTimeStepAttrTag, String.valueOf(uots.getOutputTimeStep()));
	}
		
	return outputOptions;
}

/**
 * This method returns a XML representation of a stochSimOption object.
 * Creation date: (5/2/2007 09:47:20 AM)
 * @return org.jdom.Element
 * @param param cbit.vcell.solver.StochSimOption
 */
public org.jdom.Element getXML(cbit.vcell.solver.stoch.StochSimOptions param, boolean isHybrid) {
	org.jdom.Element stochSimOptions = new org.jdom.Element(XMLTags.StochSimOptionsTag);
	if(param != null)
	{
		stochSimOptions.setAttribute(XMLTags.UseCustomSeedAttrTag, String.valueOf(param.isUseCustomSeed()));
		if(param.isUseCustomSeed())
			stochSimOptions.setAttribute(XMLTags.CustomSeedAttrTag, String.valueOf(param.getCustomSeed()));
		stochSimOptions.setAttribute(XMLTags.NumberOfTrialAttrTag, String.valueOf(param.getNumOfTrials()));
		if(isHybrid)
		{
			stochSimOptions.setAttribute(XMLTags.HybridEpsilonAttrTag, String.valueOf(((StochHybridOptions)param).getEpsilon()));
			stochSimOptions.setAttribute(XMLTags.HybridLambdaAttrTag, String.valueOf(((StochHybridOptions)param).getLambda()));
			stochSimOptions.setAttribute(XMLTags.HybridMSRToleranceAttrTag, String.valueOf(((StochHybridOptions)param).getMSRTolerance()));
			stochSimOptions.setAttribute(XMLTags.HybridSDEToleranceAttrTag, String.valueOf(((StochHybridOptions)param).getSDETolerance()));
		}
	}
	return stochSimOptions;
}

/**
 * This method returns a XML representation of a Simulation object.
 * Creation date: (3/2/2001 10:42:35 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.solver.Simulation
 */
public org.jdom.Element getXML(cbit.vcell.solver.Simulation param) {
	org.jdom.Element simulationElement = new org.jdom.Element(XMLTags.SimulationTag);

	//Add Atributes
	String name = mangle(param.getName());
	simulationElement.setAttribute(XMLTags.NameAttrTag, name);
	//simulation.setAttribute(XMLTags.AnnotationAttrTag, this.mangle(param.getDescription()));
	//Add annotation
	if (param.getDescription()!=null && param.getDescription().trim().length()>0) {
		org.jdom.Element annotationElem = new org.jdom.Element(XMLTags.AnnotationTag);
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
 * @return org.jdom.Element
 * @param param cbit.vcell.solver.SolverTaskDescription
 */
public org.jdom.Element getXML(SolverTaskDescription param) {
	org.jdom.Element solvertask = new org.jdom.Element(XMLTags.SolverTaskDescriptionTag);

	//Add Atributes
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
	if(param.getStochOpt() != null)
	{
		if(param.getSolverDescription().equals(SolverDescription.StochGibson))
			solvertask.addContent( getXML(param.getStochOpt(),false));
		else solvertask.addContent(getXML(param.getStochOpt(),true));
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
	
	return solvertask;
}


/**
 * This method returns a XML representation of a timebounds object.
 * Creation date: (3/2/2001 11:39:20 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.solver.TimeBounds
 */
public org.jdom.Element getXML(TimeBounds param) {
	org.jdom.Element timebounds = new org.jdom.Element(XMLTags.TimeBoundTag);

	timebounds.setAttribute(XMLTags.StartTimeAttrTag, String.valueOf(param.getStartingTime()));
	timebounds.setAttribute(XMLTags.EndTimeAttrTag, String.valueOf(param.getEndingTime()));
		
	return timebounds;
}


/**
 * This method returns a XML representation of a timeStep object.
 * Creation date: (3/2/2001 11:53:17 PM)
 * @return org.jdom.Element
 * @param param cbit.vcell.solver.TimeStep
 */
public org.jdom.Element getXML(TimeStep param) {
	org.jdom.Element timestep = new org.jdom.Element(XMLTags.TimeStepTag);

	timestep.setAttribute(XMLTags.DefaultTimeAttrTag, String.valueOf(param.getDefaultTimeStep()));
	timestep.setAttribute(XMLTags.MinTimeAttrTag, String.valueOf(param.getMinimumTimeStep()));
	timestep.setAttribute(XMLTags.MaxTimeAttrTag, String.valueOf(param.getMaximumTimeStep()));
	
	return timestep;
}


public org.jdom.Element getXML(Event event) throws XmlParseException{
	org.jdom.Element eventElement = new org.jdom.Element(XMLTags.EventTag);
	eventElement.setAttribute(XMLTags.NameAttrTag, mangle(event.getName()));

	Element element = new org.jdom.Element(XMLTags.TriggerTag);
	element.addContent(mangleExpression(event.getTriggerExpression()));
	eventElement.addContent(element);

	Delay delay = event.getDelay();
	if (delay != null) {
		element = new org.jdom.Element(XMLTags.DelayTag);		
		element.setAttribute(XMLTags.UseValuesFromTriggerTimeAttrTag, delay.useValuesFromTriggerTime() + "");
		element.addContent(mangleExpression(delay.getDurationExpression()));
		eventElement.addContent(element);
	}
	Iterator<EventAssignment> iter = event.getEventAssignments();
	while (iter.hasNext()) {
		EventAssignment eventAssignment = iter.next();
		element = new org.jdom.Element(XMLTags.EventAssignmentTag);
		element.setAttribute(XMLTags.EventAssignmentVariableAttrTag, eventAssignment.getVariable().getName());
		element.addContent(mangleExpression(eventAssignment.getAssignmentExpression()));
		eventElement.addContent(element);
	}

	return eventElement;
}

// For events in SimulationContext - XML is very similar to math events
public org.jdom.Element getXML(BioEvent[] bioEvents) throws XmlParseException{
	Element bioEventsElement = new Element(XMLTags.BioEventsTag);
	for (int i = 0; i < bioEvents.length; i++) {
		org.jdom.Element eventElement = new org.jdom.Element(XMLTags.BioEventTag);
		eventElement.setAttribute(XMLTags.NameAttrTag, mangle(bioEvents[i].getName()));

		Element element = new org.jdom.Element(XMLTags.TriggerTag);
		element.addContent(mangleExpression(bioEvents[i].getTriggerExpression()));
		eventElement.addContent(element);

		BioEvent.Delay delay = bioEvents[i].getDelay();
		if (delay != null) {
			element = new org.jdom.Element(XMLTags.DelayTag);		
			element.setAttribute(XMLTags.UseValuesFromTriggerTimeAttrTag, delay.useValuesFromTriggerTime() + "");
			element.addContent(mangleExpression(delay.getDurationExpression()));
			eventElement.addContent(element);
		}
		ArrayList<BioEvent.EventAssignment> eventAssignmentsList = bioEvents[i].getEventAssignments();
		for (BioEvent.EventAssignment eventAssignment : eventAssignmentsList) {
			element = new org.jdom.Element(XMLTags.EventAssignmentTag);
			element.setAttribute(XMLTags.EventAssignmentVariableAttrTag, eventAssignment.getTarget().getName());
			element.addContent(mangleExpression(eventAssignment.getAssignmentExpression()));
			eventElement.addContent(element);
		}
		bioEventsElement.addContent(eventElement);
	}

	return bioEventsElement;
}



}