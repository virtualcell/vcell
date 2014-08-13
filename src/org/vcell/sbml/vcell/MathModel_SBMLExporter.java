/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sbml.vcell;
    
import java.io.File;
import java.util.Enumeration;

import org.sbml.libsbml.ASTNode;
import org.sbml.libsbml.AdjacentDomains;
import org.sbml.libsbml.AnalyticGeometry;
import org.sbml.libsbml.AnalyticVolume;
import org.sbml.libsbml.AssignmentRule;
import org.sbml.libsbml.BoundaryMax;
import org.sbml.libsbml.BoundaryMin;
import org.sbml.libsbml.Compartment;
import org.sbml.libsbml.CompartmentMapping;
import org.sbml.libsbml.CoordinateComponent;
import org.sbml.libsbml.Domain;
import org.sbml.libsbml.DomainType;
import org.sbml.libsbml.ImageData;
import org.sbml.libsbml.InitialAssignment;
import org.sbml.libsbml.InteriorPoint;
import org.sbml.libsbml.OStringStream;
import org.sbml.libsbml.RateRule;
import org.sbml.libsbml.RequiredElementsSBasePlugin;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLError;
import org.sbml.libsbml.SBMLWriter;
import org.sbml.libsbml.SBase;
import org.sbml.libsbml.SBasePlugin;
import org.sbml.libsbml.SampledField;
import org.sbml.libsbml.SampledFieldGeometry;
import org.sbml.libsbml.SampledVolume;
import org.sbml.libsbml.SpatialCompartmentPlugin;
import org.sbml.libsbml.SpatialModelPlugin;
import org.sbml.libsbml.SpatialParameterPlugin;
import org.sbml.libsbml.SpatialSymbolReference;
import org.sbml.libsbml.libsbml;
import org.vcell.sbml.SBMLUtils;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.TokenMangler;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.math.Equation;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.Variable;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionMathMLPrinter;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.xml.XMLSource;
/**
 * Insert the type's description here.
 * Creation date: (4/11/2006 11:35:34 AM)
 * @author: Jim Schaff
 */
public class MathModel_SBMLExporter {
	static
	{
		NativeLib.SBML.load( );
	}

/**
 * Insert the method's description here.
 * Creation date: (4/11/2006 11:38:26 AM)
 * @return org.sbml.libsbml.Model
 * @param mathModel cbit.vcell.mathmodel.MathModel
 */
public static String getSBMLString(cbit.vcell.mathmodel.MathModel mathModel, long level, long version) throws cbit.vcell.parser.ExpressionException, java.io.IOException{

	if (mathModel.getMathDescription().isSpatial()){
		throw new RuntimeException("spatial models export to SBML not supported");
	}

	if (mathModel.getMathDescription().hasFastSystems()){
		throw new RuntimeException("math models with fast systems cannot be exported to SBML");
	}

	if (mathModel.getMathDescription().isNonSpatialStoch()){
		throw new RuntimeException("stochastic math models cannot be exported to SBML");
	}

	if (!mathModel.getMathDescription().isValid()){
		throw new RuntimeException("math model has an invalid Math Description, cannot export to SBML");
	}

	String dummyID = "ID_0";
	String compartmentId = "compartment";
	SBMLDocument sbmlDocument = new SBMLDocument(level, version);
	org.sbml.libsbml.Model sbmlModel = sbmlDocument.createModel();
	sbmlModel.setId("MathModel_"+TokenMangler.mangleToSName(mathModel.getName()));
	org.sbml.libsbml.Compartment compartment = sbmlModel.createCompartment();
	compartment.setId(compartmentId);
	
//  ------ For spatial SBML when implemented ----- 
//	if (vcMathModel.getMathDescription().isSpatial()){
//		// for spatial model, compartment(s) created in addGeometry(), based on number of subVolumes/surfaceClasses.
//		addGeometry();
//	} else {
//		// for non-spatial mathmodel, only 1 compartment; create it here.
//		String compartmentId = "compartment";
//		org.sbml.libsbml.Compartment compartment = sbmlModel.createCompartment();
//		compartment.setId(compartmentId);
//	}
	

	MathDescription mathDesc = mathModel.getMathDescription();
	Enumeration<Variable> enumVars = mathDesc.getVariables();
	
	while (enumVars.hasMoreElements()){
		Variable vcVar = (Variable)enumVars.nextElement();
		//
		// Variables map to species
		// Constants (that are numeric) map to parameters with value
		// Constants and Functions that are not numeric map to Parameters with assignment rules
		// ODEs map to rate rules.
		//
		if (vcVar instanceof cbit.vcell.math.VolVariable){
			//
			// skip for now, define later when defining ODEEquations.
			//
//			org.sbml.libsbml.Species species = model.createSpecies();
//			species.setId(vcVar.getName());
//			species.setCompartment(compartmentId);
		}else if (vcVar instanceof cbit.vcell.math.Constant && ((cbit.vcell.math.Constant)vcVar).getExpression().isNumeric()){
			org.sbml.libsbml.Parameter param = sbmlModel.createParameter();
			param.setId(TokenMangler.mangleToSName(vcVar.getName()));
			param.setConstant(true);
			param.setValue(vcVar.getExpression().evaluateConstant());
		}else if (vcVar instanceof cbit.vcell.math.Constant || vcVar instanceof cbit.vcell.math.Function) {
			org.sbml.libsbml.Parameter param = sbmlModel.createParameter();
			param.setId(TokenMangler.mangleToSName(vcVar.getName()));
			param.setConstant(false);
			//
			// Function or Constant with expressions - create assignment rule and add to model.
			//
			ASTNode mathNode = getFormulaFromExpression(vcVar.getExpression());
			AssignmentRule assignmentRule = sbmlModel.createAssignmentRule();
			dummyID = TokenMangler.getNextEnumeratedToken(dummyID);
			assignmentRule.setId(dummyID);
			assignmentRule.setVariable(TokenMangler.mangleToSName(vcVar.getName()));
			
			assignmentRule.setMath(mathNode);
			// Create a parameter for this function/non-numeric constant, set its value to be 'not-constant', 
			// add to model.
		}
	}
	
	cbit.vcell.math.CompartmentSubDomain subDomain = (cbit.vcell.math.CompartmentSubDomain)mathDesc.getSubDomains().nextElement();
//	System.out.println(model.toSBML());
	Enumeration<Equation> enumEqu = subDomain.getEquations();
	
	while (enumEqu.hasMoreElements()){
		cbit.vcell.math.Equation equ = (cbit.vcell.math.Equation)enumEqu.nextElement();
		if (equ instanceof cbit.vcell.math.OdeEquation){
			// For ODE equations, add the ode variable as a parameter, add rate as a rate rule and init condition as an initial assignment rule.
			org.sbml.libsbml.Parameter param = sbmlModel.createParameter();
			param.setId(TokenMangler.mangleToSName(equ.getVariable().getName()));
			param.setConstant(false);
			
			// try to obtain the constant to which the init expression evaluates.
			RateRule rateRule = sbmlModel.createRateRule();
			rateRule.setVariable(TokenMangler.mangleToSName(equ.getVariable().getName()));
			rateRule.setMath(getFormulaFromExpression(equ.getRateExpression()));

			InitialAssignment initialAssignment = sbmlModel.createInitialAssignment();
			dummyID = TokenMangler.getNextEnumeratedToken(dummyID);
			initialAssignment.setId(dummyID);
			initialAssignment.setMath(getFormulaFromExpression(equ.getInitialExpression()));
			initialAssignment.setSymbol(TokenMangler.mangleToSName(equ.getVariable().getName()));
		 }else{
		 	throw new RuntimeException("equation type "+equ.getClass().getName()+" not supported");
		 }
	}
	
	//validate the sbml document
	sbmlDocument.checkInternalConsistency();
	long internalErrCount = sbmlDocument.getNumErrors();
	if(internalErrCount>0)
	{
		StringBuffer sbmlErrbuf = new StringBuffer();
		for(long i=0; i<internalErrCount; i++)
		{
			SBMLError sbmlErr = sbmlDocument.getError(i);
			if (sbmlErr.isError() || sbmlErr.isFatal()){
				sbmlErrbuf.append(sbmlErr.getCategoryAsString() + " :: " + sbmlErr.getSeverityAsString() + " :: " + sbmlErr.getMessage()+"\n");
			}
		}
		if (sbmlErrbuf.length()>0){
			throw new RuntimeException("SBML Internal consistency checks failed: \n"+sbmlErrbuf.toString());
		}
	}
	sbmlDocument.setConsistencyChecks(libsbml.LIBSBML_CAT_GENERAL_CONSISTENCY, true);
	sbmlDocument.setConsistencyChecks(libsbml.LIBSBML_CAT_IDENTIFIER_CONSISTENCY, true);
	sbmlDocument.setConsistencyChecks(libsbml.LIBSBML_CAT_UNITS_CONSISTENCY, false);
	sbmlDocument.setConsistencyChecks(libsbml.LIBSBML_CAT_MATHML_CONSISTENCY, true);
	sbmlDocument.setConsistencyChecks(libsbml.LIBSBML_CAT_SBO_CONSISTENCY, false);
	sbmlDocument.setConsistencyChecks(libsbml.LIBSBML_CAT_OVERDETERMINED_MODEL, true);
	sbmlDocument.setConsistencyChecks(libsbml.LIBSBML_CAT_MODELING_PRACTICE, false);
	
	sbmlDocument.checkConsistency();
	long errCount = sbmlDocument.getNumErrors();
	if(errCount>0)
	{
		StringBuffer sbmlErrbuf = new StringBuffer();
		for(long i=0; i<errCount; i++)
		{
			SBMLError sbmlErr = sbmlDocument.getError(i);
			if (sbmlErr.isError() || sbmlErr.isFatal()){
				sbmlErrbuf.append(sbmlErr.getCategoryAsString() + " :: " + sbmlErr.getSeverityAsString() + " :: " + sbmlErr.getMessage()+"\n");
			}
		}
		if (sbmlErrbuf.length() > 0){
			throw new RuntimeException("SBML validation failed: \n"+sbmlErrbuf.toString());
		}
	}
	//end of validation

	//start writing
	SBMLWriter sbmlWriter = new SBMLWriter();
	String sbmlStr = sbmlWriter.writeSBMLToString(sbmlDocument);

	// Error check - use libSBML's document.printError to print to outputstream
	System.out.println("\n\nSBML Export Error Report");
	OStringStream oStrStream = new OStringStream();
	sbmlDocument.printErrors(oStrStream);
	System.out.println(oStrStream.str());

	// cleanup
	sbmlModel.delete();
	sbmlDocument.delete();
	sbmlWriter.delete();	

	return sbmlStr;
}

/**
 * 	getFormulaFromExpression : 
 *  Expression infix strings are not handled gracefully by libSBML, esp when ligical or inequality operators are used.
 *  This method 
 *		converts the expression into MathML using ExpressionMathMLPrinter;
 *		converts that into libSBMl-readable formula using libSBML utilties.
 *		returns the new formula string.
 *  
 */
public static ASTNode getFormulaFromExpression(Expression expression) { 
	// Convert expression into MathML string
	String expMathMLStr = null;

	try {
		//mangling the identifiers in the expression to make them proper SBase ids.
		Expression mangledExpression = new Expression(expression);
		String[] symbols = mangledExpression.getSymbols();
		if(symbols != null)
		{
			for (String symbol : mangledExpression.getSymbols()){
				String mangledSymbol = TokenMangler.mangleToSName(symbol);
				if (!mangledSymbol.equals(symbol)){
					mangledExpression.substituteInPlace(new Expression(symbol), new Expression(mangledSymbol));
				}
			}
		}
		expMathMLStr = cbit.vcell.parser.ExpressionMathMLPrinter.getMathML(mangledExpression, false);
	} catch (java.io.IOException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Error converting expression to MathML string :" + e.getMessage());
	} catch (cbit.vcell.parser.ExpressionException e1) {
		e1.printStackTrace(System.out);
		throw new RuntimeException("Error converting expression to MathML string :" + e1.getMessage());
	}
	
	// Use libSBMl routines to convert MathML string to MathML document and a libSBML-readable formula string

	ASTNode mathNode = libsbml.readMathMLFromString(expMathMLStr);
	return mathNode.deepCopy();
}

private void addGeometry(org.sbml.libsbml.Model sbmlModel, MathModel vcMathModel) {

	SBasePlugin plugin = sbmlModel.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
    SpatialModelPlugin mplugin = (SpatialModelPlugin)plugin;

    // Creates a geometry object via SpatialModelPlugin object.
	org.sbml.libsbml.Geometry sbmlGeometry = mplugin.getGeometry();
	sbmlGeometry.setCoordinateSystem("Cartesian");

	Geometry vcGeometry = vcMathModel.getGeometry();
	//
	// list of CoordinateComponents : 1 if geometry is 1-d, 2 if geometry is 2-d, 3 if geometry is 3-d
	//
	int dimension = vcGeometry.getDimension();
	Extent vcExtent = vcGeometry.getExtent();
	Origin vcOrigin = vcGeometry.getOrigin();
	// add x coordinate component
	CoordinateComponent xComp = sbmlGeometry.createCoordinateComponent();
	xComp.setSpatialId(ReservedVariable.X.getName());
	xComp.setComponentType("cartesianX");
//	xComp.setSbmlUnit(ReservedVariable.X.getUnitDefinition().getSymbol());
	xComp.setIndex(0);
	BoundaryMin minX = xComp.createBoundaryMin();
	minX.setSpatialId("Xmin");
	minX.setValue(vcOrigin.getX());
	BoundaryMax maxX = xComp.createBoundaryMax();
	maxX.setSpatialId("Xmax");
	maxX.setValue(vcOrigin.getX() + (vcExtent.getX()));
	createParamForSpatialElement(xComp, xComp.getSpatialId(), sbmlModel);
	// add y coordinate component
	if (dimension == 2 || dimension == 3) {
		CoordinateComponent yComp = sbmlGeometry.createCoordinateComponent();
		yComp.setSpatialId(ReservedVariable.Y.getName());
		yComp.setComponentType("cartesianY");
//		yComp.setSbmlUnit(ReservedVariable.Y.getUnitDefinition().getSymbol());
		yComp.setIndex(1);
		BoundaryMin minY = yComp.createBoundaryMin();
		minY.setSpatialId("Ymin");
		minY.setValue(vcOrigin.getY());
		BoundaryMax maxY = yComp.createBoundaryMax();
		maxY.setSpatialId("Ymax");
		maxY.setValue(vcOrigin.getY() + (vcExtent.getY()));
		createParamForSpatialElement(yComp, yComp.getSpatialId(), sbmlModel);
	}
	// add z coordinate component
	if (dimension == 3) {
		CoordinateComponent zComp = sbmlGeometry.createCoordinateComponent();
		zComp.setSpatialId(ReservedVariable.Z.getName());
		zComp.setComponentType("cartesianZ");
//		zComp.setSbmlUnit(ReservedVariable.Z.getUnitDefinition().getSymbol());
		zComp.setIndex(2);
		BoundaryMin minZ = zComp.createBoundaryMin();
		minZ.setSpatialId("Zmin");
		minZ.setValue(vcOrigin.getZ());
		BoundaryMax maxZ = zComp.createBoundaryMax();
		maxZ.setSpatialId("Zmax");
		maxZ.setValue(vcOrigin.getZ() + (vcExtent.getZ()));
		createParamForSpatialElement(zComp, zComp.getSpatialId(), sbmlModel);
	}

	//
	// list of domain types : subvolumes and surface classes from VC
	// Also create compartments - one compartment for each geometryClass. set id and spatialDimension based on type of geometryClass.
	//
	boolean bAnalyticGeom = false;
	boolean bImageGeom = false;
	GeometryClass[] vcGeomClasses = vcGeometry.getGeometryClasses();
	int numVCGeomClasses = vcGeomClasses.length;
	for (int i = 0; i < numVCGeomClasses; i++) {
	    DomainType domainType = sbmlGeometry.createDomainType();
	    domainType.setSpatialId(vcGeomClasses[i].getName());
	    if (vcGeomClasses[i] instanceof SubVolume) {
	    	if (((SubVolume)vcGeomClasses[i]) instanceof AnalyticSubVolume) {
	    		bAnalyticGeom = true;
	    	} else if (((SubVolume)vcGeomClasses[i]) instanceof ImageSubVolume) {
	    		bImageGeom = true;
	    	}
	    	domainType.setSpatialDimensions(3);
	    } else if (vcGeomClasses[i] instanceof SurfaceClass) {
	    	domainType.setSpatialDimensions(2);
	    }
	}
	
	//
	// list of domains, adjacent domains : from VC geometricRegions
	//
	GeometrySurfaceDescription vcGSD = vcGeometry.getGeometrySurfaceDescription();
	if (vcGSD.getRegionImage() == null) {
		try {
			vcGSD.updateAll();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Unable to generate region images for geometry");
		}
	}
	GeometricRegion[] vcGeometricRegions = vcGSD.getGeometricRegions();
	ISize sampleSize = vcGSD.getVolumeSampleSize();
	int numX = sampleSize.getX();
	int numY = sampleSize.getY();
	int numZ = sampleSize.getZ();
	double ox = vcOrigin.getX();
	double oy = vcOrigin.getY();
	double oz = vcOrigin.getZ();
	RegionInfo[] regionInfos = vcGSD.getRegionImage().getRegionInfos();
	Compartment compartment = null;

	for (int i = 0; i < vcGeometricRegions.length; i++) {
		// domains
		Domain domain = sbmlGeometry.createDomain();
		domain.setSpatialId(vcGeometricRegions[i].getName());
	    compartment = sbmlModel.createCompartment();
	    compartment.setId("compartment" + i);
		if (vcGeometricRegions[i] instanceof VolumeGeometricRegion) {
			domain.setDomainType(((VolumeGeometricRegion)vcGeometricRegions[i]).getSubVolume().getName());
//			domain.setImplicit(false);
	    	compartment.setSpatialDimensions(3);
			InteriorPoint interiorPt = domain.createInteriorPoint();
			int regionID = ((VolumeGeometricRegion)vcGeometricRegions[i]).getRegionID();
			boolean bFound = false;
			int regInfoIndx = 0;
			for (int j = 0; j < regionInfos.length; j++) {
				regInfoIndx = j;
				if (regionInfos[j].getRegionIndex() == regionID) {
					int volIndx = 0;
					for (int z = 0; z < numZ && !bFound; z++) {
						for (int y = 0; y < numY && !bFound; y++) {
							for (int x = 0; x < numX && !bFound; x++) {
								if (regionInfos[j].isIndexInRegion(volIndx)) {
									bFound = true;
									double unit_z = (numZ>1)?((double)z)/(numZ-1):0.5;
									double coordZ = oz + vcExtent.getZ() * unit_z;
									double unit_y = (numY>1)?((double)y)/(numY-1):0.5;
									double coordY = oy + vcExtent.getY() * unit_y;
									double unit_x = (numX>1)?((double)x)/(numX-1):0.5;
									double coordX = ox + vcExtent.getX() * unit_x;
									interiorPt.setCoord1(coordX);
									interiorPt.setCoord2(coordY);
									interiorPt.setCoord3(coordZ);
								}
								volIndx++;
							}	// end - for x
						}	// end - for y
					}	// end - for z
				}	// end if 
			}	// end for regionInfos
			if (!bFound) {
				throw new RuntimeException("Unable to find interior point for region '" + regionInfos[regInfoIndx].toString());
			}
		} else if (vcGeometricRegions[i] instanceof SurfaceGeometricRegion) {
			SurfaceGeometricRegion vcSurfaceGeomReg = (SurfaceGeometricRegion)vcGeometricRegions[i];
			GeometricRegion geomRegion0 = vcSurfaceGeomReg.getAdjacentGeometricRegions()[0];
			GeometricRegion geomRegion1 = vcSurfaceGeomReg.getAdjacentGeometricRegions()[1];
			SurfaceClass surfaceClass = vcGSD.getSurfaceClass(((VolumeGeometricRegion)geomRegion0).getSubVolume(), ((VolumeGeometricRegion)geomRegion1).getSubVolume());
			domain.setDomainType(surfaceClass.getName());
//			domain.setImplicit(true);
	    	compartment.setSpatialDimensions(2);

			// adjacent domains : 2 adjacent domain objects for each surfaceClass in VC.
			// adjacent domain 1
			AdjacentDomains adjDomain = sbmlGeometry.createAdjacentDomains();
			adjDomain.setSpatialId(TokenMangler.mangleToSName(vcSurfaceGeomReg.getName()+"_"+geomRegion0.getName()));
			adjDomain.setDomain1(vcSurfaceGeomReg.getName());
			adjDomain.setDomain2(geomRegion0.getName());
			// adjacent domain 2
			adjDomain = sbmlGeometry.createAdjacentDomains();
			adjDomain.setSpatialId(TokenMangler.mangleToSName(vcSurfaceGeomReg.getName()+"_"+geomRegion1.getName()));
			adjDomain.setDomain1(vcSurfaceGeomReg.getName());
			adjDomain.setDomain2(geomRegion1.getName());
		}
		//
		// Mathmodel does not have structureMapping, hence creating compartmentMapping while creating domains.
		// @TODO : how to assign unitSize for compartmentMapping?
		//
		plugin = compartment.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		SpatialCompartmentPlugin cplugin = (SpatialCompartmentPlugin)plugin;
		CompartmentMapping compMapping = cplugin.getCompartmentMapping();
		String compMappingId = TokenMangler.mangleToSName(domain.getDomainType() + "_" + compartment.getId());
		compMapping.setSpatialId(compMappingId);
		compMapping.setCompartment(TokenMangler.mangleToSName(compartment.getId()));
		compMapping.setDomainType(TokenMangler.mangleToSName(domain.getDomainType()));
//		try {
//			compMapping.setUnitSize(1.0);
//		} catch (ExpressionException e) {
//			e.printStackTrace(System.out);
//			throw new RuntimeException("Unable to create compartment mapping for structureMapping '" + compMapping.getSpatialId() +"' : " + e.getMessage());
//		}
	}

	AnalyticGeometry sbmlAnalyticGeom = null;
	SampledFieldGeometry sbmlSFGeom = null;
	// If subvolumes in geometry are analytic, should GeometryDefinition be analyticGeometry? What if VC geom has
	// both image and analytic subvolumes?? == not handled in SBML at this time.
	if (bAnalyticGeom && !bImageGeom) {
		sbmlAnalyticGeom = sbmlGeometry.createAnalyticGeometry();
		sbmlAnalyticGeom.setSpatialId(TokenMangler.mangleToSName(vcGeometry.getName()));
	} else if (bImageGeom && !bAnalyticGeom){
		// assuming image based geometry if not analytic geometry
		sbmlSFGeom = sbmlGeometry.createSampledFieldGeometry();
		sbmlSFGeom.setSpatialId(TokenMangler.mangleToSName(vcGeometry.getName()));
	} else if (bAnalyticGeom && bImageGeom) {
		throw new RuntimeException("Export to SBML of a combination of Image-based and Analytic geometries is not supported yet.");
	} else if (!bAnalyticGeom && !bImageGeom) {
		throw new RuntimeException("Unknown geometry type.");
	}
	
	//
	// list of analytic volumes to analyticGeometry (geometricDefinition) : 
	// 
	for (int i = 0; i < vcGeomClasses.length; i++) {
		if (vcGeomClasses[i] instanceof AnalyticSubVolume) {
			// add analytiVols to sbmlAnalyticGeometry
			if (sbmlAnalyticGeom != null) {
				AnalyticVolume analyticVol = sbmlAnalyticGeom.createAnalyticVolume();
				analyticVol.setSpatialId(vcGeomClasses[i].getName());
				analyticVol.setDomainType(vcGeomClasses[i].getName());
				analyticVol.setFunctionType("layered");
				analyticVol.setOrdinal(i);
				Expression expr = ((AnalyticSubVolume)vcGeomClasses[i]).getExpression();
				try {
					String mathMLStr = ExpressionMathMLPrinter.getMathML(expr, true);
					ASTNode mathMLNode = libsbml.readMathMLFromString(mathMLStr);
					analyticVol.setMath(mathMLNode);
				} catch (Exception e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Error converting VC subvolume expression to mathML" + e.getMessage());
				}
			} else {
				throw new RuntimeException("SBML AnalyticGeometry is null.");
			}
		} else if (vcGeomClasses[i] instanceof ImageSubVolume) {
			// add sampledVols to sbmlSFGeometry
			if (sbmlSFGeom != null) {
				SampledVolume sampledVol = sbmlSFGeom.createSampledVolume();
				sampledVol.setSpatialId(vcGeomClasses[i].getName());
				sampledVol.setDomainType(vcGeomClasses[i].getName());
				sampledVol.setSampledValue(((ImageSubVolume)vcGeomClasses[i]).getPixelValue());
			} else {
				throw new RuntimeException("SBML SampledFieldGeometry is null.");
			}
		}
	}
	
	if (sbmlSFGeom != null) {
		// add sampledField to sampledFieldGeometry
		SampledField sampledField = sbmlSFGeom.createSampledField();
		VCImage vcImage = vcGeometry.getGeometrySpec().getImage();
		sampledField.setSpatialId(vcImage.getName());
		sampledField.setNumSamples1(vcImage.getNumX());
		sampledField.setNumSamples2(vcImage.getNumY());
		sampledField.setNumSamples3(vcImage.getNumZ());
		sampledField.setDataType("integer");
		sampledField.setInterpolationType("0-order interpolation");
		// add image from vcGeometrySpec to sampledField.
		try {
			ImageData imageData = sampledField.createImageData();
			byte[] imagePixelsBytes = vcImage.getPixelsCompressed();
			int[] imagePixelsInt = new int[imagePixelsBytes.length];
			for (int i = 0; i < imagePixelsBytes.length; i++) {
				imagePixelsInt[i] = (int)imagePixelsBytes[i];
			}
			imageData.setSamples(imagePixelsInt, imagePixelsInt.length);
		} catch (ImageException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Unable to export image from VCell to SBML : " + e.getMessage());
		}
	}
}

/**
 * This method is used to create a parameter for geometry elements that have references in SBML core. For example, 
 * CoordinateComponent: for vcell there will be 3 coordinate components (x,y,z) for 3-d application (2 for 2-d and so forth).
 * Create a parameter for each coordinate component. Whenever x/y/z is used in any expression in the model (init condn, 
 * boundary condition, etc.) this parameter is to be used.  
 * @param sBaseElement
 */
private void createParamForSpatialElement(SBase sBaseElement, String spatialId, org.sbml.libsbml.Model sbmlModel) {
	org.sbml.libsbml.Parameter p = sbmlModel.createParameter();
	if (sBaseElement instanceof CoordinateComponent) {
		CoordinateComponent cc = (CoordinateComponent)sBaseElement;
		// coordComponent with index = 1 represents X-axis, hence set param id as 'x'
		if (cc.getIndex() == 0) {
			p.setId("x");
		} else if (cc.getIndex() == 1) {
			p.setId("y");
		} else if (cc.getIndex() == 2) {
			p.setId("z");
		}
	} else {
		p.setId(spatialId);
	}
	p.setValue(0.0);
	// since p is a parameter from 'spatial' package, need to set the
	// requiredElements attributes on parameter 
	SBasePlugin plugin = p.getPlugin(SBMLUtils.SBML_REQUIREDELEMENTS_NS_PREFIX);
	RequiredElementsSBasePlugin reqPlugin = (RequiredElementsSBasePlugin)plugin;
	reqPlugin.setMathOverridden(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
	reqPlugin.setCoreHasAlternateMath(false);
	// now need to create a SpatialSymbolReference for parameter
	plugin = p.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
	SpatialParameterPlugin spPlugin = (SpatialParameterPlugin)plugin;
	SpatialSymbolReference spSymRef = spPlugin.getSpatialSymbolReference();
	spSymRef.setSpatialId(spatialId);
	spSymRef.setType(sBaseElement.getElementName());
	
}


/**
 * Insert the method's description here.
 * Creation date: (4/11/2006 12:46:12 PM)
 */
public static void main(String[] args) {
	try {
		if (args.length!=2){
			System.out.println("Usage: MathModel_SBMLExporter inputVCMLFile outputSBMLFile");
			System.exit(0);
		}
		String inputVCMLFileName = args[0];
		String outputSBMLFileName = args[1];
		XMLSource vcmlSource = new XMLSource(new File(inputVCMLFileName));
		cbit.vcell.mathmodel.MathModel mathModel = cbit.vcell.xml.XmlHelper.XMLToMathModel(vcmlSource);
		String sbmlString = getSBMLString(mathModel, 2, 3);
//		org.sbml.libsbml.SBMLWriter sbmlWriter = new org.sbml.libsbml.SBMLWriter();
//		sbmlWriter.setProgramName("Virtual Cell");
//		String vcellVersion = PropertyLoader.getProperty(PropertyLoader.vcellSoftwareVersion, "unknown");
//		sbmlWriter.setProgramVersion(vcellVersion);
//		String sbmlString = sbmlWriter.writeToString(sbmlDoc);
		java.io.FileWriter fileWriter = new java.io.FileWriter(new java.io.File(outputSBMLFileName));
		fileWriter.write(sbmlString);
		fileWriter.flush();
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}
}