package org.vcell.sbml.vcell;

import java.io.File;
import java.io.FileWriter;

import org.sbml.libsbml.ASTNode;
import org.sbml.libsbml.AdjacentDomains;
import org.sbml.libsbml.AdvectionCoefficient;
import org.sbml.libsbml.AnalyticGeometry;
import org.sbml.libsbml.AnalyticVolume;
import org.sbml.libsbml.AssignmentRule;
import org.sbml.libsbml.BoundaryCondition;
import org.sbml.libsbml.BoundaryMax;
import org.sbml.libsbml.BoundaryMin;
import org.sbml.libsbml.Compartment;
import org.sbml.libsbml.CompartmentMapping;
import org.sbml.libsbml.CoordinateComponent;
import org.sbml.libsbml.DiffusionCoefficient;
import org.sbml.libsbml.Domain;
import org.sbml.libsbml.DomainType;
import org.sbml.libsbml.Geometry;
import org.sbml.libsbml.GeometryDefinition;
import org.sbml.libsbml.ImageData;
import org.sbml.libsbml.InteriorPoint;
import org.sbml.libsbml.Model;
import org.sbml.libsbml.Parameter;
import org.sbml.libsbml.RequiredElementsPkgNamespaces;
import org.sbml.libsbml.RequiredElementsSBasePlugin;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLDocumentPlugin;
import org.sbml.libsbml.SBMLReader;
import org.sbml.libsbml.SBMLWriter;
import org.sbml.libsbml.SampledField;
import org.sbml.libsbml.SampledFieldGeometry;
import org.sbml.libsbml.SampledVolume;
import org.sbml.libsbml.SpatialCompartmentPlugin;
import org.sbml.libsbml.SpatialModelPlugin;
import org.sbml.libsbml.SpatialParameterPlugin;
import org.sbml.libsbml.SpatialSpeciesRxnPlugin;
import org.sbml.libsbml.SpatialSymbolReference;
import org.sbml.libsbml.Species;
import org.sbml.libsbml.libsbml;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.VCDocument;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Structure;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

public class SBMLSpatialTest {

public static void	main(String argv[])
{
	ResourceUtil.loadlibSbmlLibray();
	
//	File sbmlFile = testVCSBMLSpatialExporter(new File("C:\\\\Temp\\\\CaDiffusion_Tutorial.vcml"));
//	testVCSBMLSpatialImporter(sbmlFile);
	
 	 writeSpatialSBML();
 	 readSpatialSBML();

	//	writeLayoutSBML();
}

private static void writeSpatialSBML() {
  /*	
  // SBMLNamespaces of SBML Level 3 Version 1 with Spatial Version 1
  SBMLNamespaces sbmlns = new SBMLNamespaces(3,1,"spatial",1);
  // SpatialPkgNamespaces spatialns(3,1,1);
   * 
   */
	
  // SBMLNamespaces of SBML Level 3 Version 1 with 'req' Version 1
  // then add 'spatial' package namespace.
  RequiredElementsPkgNamespaces sbmlns = new RequiredElementsPkgNamespaces(3,1,1);
  sbmlns.addPkgNamespace("spatial",1);

  // create the L3V1 document with spatial package
  SBMLDocument document = new SBMLDocument(sbmlns);	
  
  // set 'required' attribute on document for 'spatial' and 'req' packages to 'T'??
	SBMLDocumentPlugin dplugin = (SBMLDocumentPlugin)document.getPlugin("req");
	dplugin.setRequired(true);
	dplugin = (SBMLDocumentPlugin)document.getPlugin("spatial");
	dplugin.setRequired(true);


  // create the Model 
  Model model = document.createModel();
  model.setId("trial_spatial");
  model.setName("trial_spatial");

  // create the Compartments
  Compartment compartment = model.createCompartment();
  compartment.setId("cytosol");
  compartment.setConstant(true);

  // create the Species
  Species species1 = model.createSpecies();
  species1.setId("ATPc");
  species1.setCompartment("cytosol");
  species1.setInitialConcentration(0);
  species1.setHasOnlySubstanceUnits(false);
  species1.setBoundaryCondition(false);
  species1.setConstant(false);
  SpatialSpeciesRxnPlugin srplugin = (SpatialSpeciesRxnPlugin)species1.getPlugin("spatial");
  srplugin.setIsSpatial(true);
  
  // param for species1 diff coeff
  Parameter param = model.createParameter();
  param.setId("paramDC");
  param.setValue(0);
  param.setConstant(false);
  RequiredElementsSBasePlugin reqPlugin = (RequiredElementsSBasePlugin)param.getPlugin("req");
  reqPlugin.setMathOverridden("spatial");
  reqPlugin.setCoreHasAlternateMath(true);
  SpatialParameterPlugin spplugin = (SpatialParameterPlugin)param.getPlugin("spatial");
  DiffusionCoefficient diffCoeff = spplugin.getDiffusionCoefficient();
  diffCoeff.setVariable(species1.getId());
  diffCoeff.setCoordinateIndex(0);

  // param for species1 adv coeff
  param = model.createParameter();
  param.setId("paramAC");
  param.setValue(0);
  param.setConstant(false);
  reqPlugin = (RequiredElementsSBasePlugin)param.getPlugin("req");
  reqPlugin.setMathOverridden("spatial");
  reqPlugin.setCoreHasAlternateMath(true);
  spplugin = (SpatialParameterPlugin)param.getPlugin("spatial");
  AdvectionCoefficient advCoeff = spplugin.getAdvectionCoefficient();
  advCoeff.setVariable(species1.getId());
  advCoeff.setCoordinateIndex(0);
  
  // param for species1 boundary conditions
  param = model.createParameter();
  param.setId("paramBC");
  param.setValue(0);
  param.setConstant(false);
  reqPlugin = (RequiredElementsSBasePlugin)param.getPlugin("req");
  reqPlugin.setMathOverridden("spatial");
  reqPlugin.setCoreHasAlternateMath(true);
  spplugin = (SpatialParameterPlugin)param.getPlugin("spatial");
  BoundaryCondition bc = spplugin.getBoundaryCondition();
  bc.setVariable(species1.getId());
  bc.setCoordinateBoundary("Xmin");
  bc.setType("value");
  
  
  Species species2 = model.createSpecies();
  species2.setId("ATPc");
  species2.setCompartment("cytosol");
  species2.setInitialConcentration(1);
  species2.setHasOnlySubstanceUnits(false);
  species2.setBoundaryCondition(false);
  species2.setConstant(false);
  
  // create a general parameter
  param = model.createParameter();
  param.setId("k_1");
  param.setValue(0.24);
  param.setConstant(true);

  // create an assignment rule
  AssignmentRule assignRule = model.createAssignmentRule();
  assignRule.setVariable(species1.getId());
  assignRule.setFormula("species2+k_1");

  //
  // Get a SpatialModelPlugin object plugged in the model object.
  //
  // The type of the returned value of SBase::getPlugin() function is 
  // SBasePlugin*, and thus the value needs to be casted for the 
  // corresponding derived class.
  //
  /*
  SBasePlugin plugin = model.getPlugin("spatial");
  SpatialModelPlugin mplugin = null;
  if (plugin instanceof SpatialModelPlugin) {
	  mplugin = (SpatialModelPlugin)plugin;
  } */
  SpatialModelPlugin mplugin = (SpatialModelPlugin)model.getPlugin("spatial");

  //
  // Creates a geometry object via SpatialModelPlugin object.
  //
  Geometry geometry = mplugin.getGeometry();
  geometry.setCoordinateSystem("XYZ");

  CoordinateComponent coordComp = geometry.createCoordinateComponent();
  coordComp.setSpatialId("x");
  coordComp.setComponentType("cartesian");
  coordComp.setSbmlUnit("umeter");
  coordComp.setIndex(1);
  BoundaryMin minX = coordComp.createBoundaryMin();
  minX.setSpatialId("minX");
  minX.setValue(0.0);
  BoundaryMax maxX = coordComp.createBoundaryMax();
  maxX.setSpatialId("maxX");
  maxX.setValue(10.0);
  
  // create a parameter in core package for each coord component
  Parameter paramX = model.createParameter();
  paramX.setId("x");
  reqPlugin = (RequiredElementsSBasePlugin)paramX.getPlugin("req");
  reqPlugin.setMathOverridden("spatial");
  reqPlugin.setCoreHasAlternateMath(true);
  spplugin = (SpatialParameterPlugin)paramX.getPlugin("spatial");
  SpatialSymbolReference spSymRef = spplugin.getSpatialSymbolReference();
  spSymRef.setSpatialId(coordComp.getSpatialId());
  spSymRef.setType(coordComp.getElementName());

  // add domainType
  DomainType domainType = geometry.createDomainType();
  domainType.setSpatialId("dtype1");
  domainType.setSpatialDimensions(3);

  // create a compartmentMapping plugin for each compartment in model
  reqPlugin = (RequiredElementsSBasePlugin)compartment.getPlugin("req");
  reqPlugin.setMathOverridden("spatial");
  reqPlugin.setCoreHasAlternateMath(true);
  SpatialCompartmentPlugin cplugin = (SpatialCompartmentPlugin)compartment.getPlugin("spatial");
  CompartmentMapping compMapping = cplugin.getCompartmentMapping();
  compMapping.setSpatialId("compMap1");
  compMapping.setCompartment(compartment.getId());
  compMapping.setDomainType(domainType.getSpatialId());
  compMapping.setUnitSize(1.0);

  // add domain(s)
  Domain domain = geometry.createDomain();
  domain.setSpatialId("domain1");
  domain.setDomainType("dtype1");
  domain.setImplicit(false);
  domain.setShapeId("circle");
  InteriorPoint intPt = domain.createInteriorPoint();
  intPt.setCoord1(1.0);

  domain = geometry.createDomain();
  domain.setSpatialId("domain2");
  domain.setDomainType("dtype1");
  domain.setImplicit(false);
  domain.setShapeId("square");
  intPt = domain.createInteriorPoint();
  intPt.setCoord1(2.0);

  // add adjacentDomains
  AdjacentDomains adjDomain = geometry.createAdjacentDomains();
  adjDomain.setSpatialId("adjDomain1");
  adjDomain.setDomain1("domain1");
  adjDomain.setDomain2("domain2");
  
  // add analytic geometry
  AnalyticGeometry analyticGeom = geometry.createAnalyticGeometry();
  analyticGeom.setSpatialId("analyticGeom1");
  AnalyticVolume analyticVol = analyticGeom.createAnalyticVolume();
  analyticVol.setSpatialId("analyticVol1");
  analyticVol.setDomainType("dtype1");
  analyticVol.setFunctionType("squareFn");
  analyticVol.setOrdinal(1);
  String mathFormulaStr = "x*x-1";
  ASTNode mathMLNode = libsbml.parseFormula(mathFormulaStr);
  analyticVol.setMath(mathMLNode);
  
  // add sampledfieldGeometry
  SampledFieldGeometry sfg = geometry.createSampledFieldGeometry();
  sfg.setSpatialId("sampledFieldGeom1");
  SampledField sampledField = sfg.createSampledField();
  sampledField.setSpatialId("sampledField1");
  sampledField.setNumSamples1(4);
  sampledField.setNumSamples2(4);
  sampledField.setNumSamples3(2);
  sampledField.setDataType("double");
  sampledField.setInterpolationType("linear");
  sampledField.setEncoding("encoding1");
  
  int samples[] = {
          // z=0
          0,0,0,0,
          0,1,1,0,
			 0,1,1,0,
			 0,0,0,0,
			 // z=1
			 0,0,0,0,
			 0,1,1,0,
			 0,1,1,0,
			 0,0,0,0
  };
  ImageData id = sampledField.createImageData();
  id.setSamples(samples, samples.length);
  
  SampledVolume sampledVol = sfg.createSampledVolume();
  sampledVol.setSpatialId("sv_1");
  sampledVol.setDomainType(domainType.getSpatialId());
  sampledVol.setSampledValue(128.0);
  sampledVol.setMinValue(0.0);
  sampledVol.setMaxValue(255.0);
  
/* ======= Alternative to above 3 lines of code =====  
  String mathMLStr = null;
  try {
	  Expression expr = new Expression("x*x-1");
	  ExpressionMathMLPrinter.getMathML(expr, true);
	} catch (Exception e) {
		e.printStackTrace(System.out);
	}
  ASTNode mathMLNode = libsbml.readMathMLFromString(mathMLStr);
*/  
  
  SBMLWriter sbmlWriter = new SBMLWriter();
  boolean bSuccess = sbmlWriter.writeSBML(document, "C:\\Temp\\spatial_example_J.xml");

}

private static void readSpatialSBML() {
	SBMLReader reader = new SBMLReader();
	SBMLDocument document = reader.readSBML("C:\\Temp\\spatial_example_J.xml");
  
	// model 
	Model model = document.getModel();
	
	// Compartment(s)
	Compartment c;
	RequiredElementsSBasePlugin reqPlugin = null;
	SpatialCompartmentPlugin cplugin = null;
	for (int i = 0; i < model.getNumCompartments(); i++) {
		c = model.getCompartment(i);
		System.out.println("Compartment" + i + " : " + c.getId());
		reqPlugin = (RequiredElementsSBasePlugin)c.getPlugin("req");
		if (!reqPlugin.getMathOverridden().equals("")) {
			System.out.print("\t req:MO = " + reqPlugin.getMathOverridden() + "\t req:CoreAltMath = " + reqPlugin.getCoreHasAlternateMath());
		}
		cplugin = (SpatialCompartmentPlugin)c.getPlugin("spatial");
		CompartmentMapping compMapping = cplugin.getCompartmentMapping();
		if (compMapping.isSetSpatialId()) {
			System.out.print("\t cm:id = " + compMapping.getSpatialId() + "\t cm:c = " + compMapping.getCompartment() +
							 "\t cm:dt = " + compMapping.getDomainType() + "\t cm:u = " + compMapping.getUnitSize());
		}		
		System.out.println("\n");
	}

	// species
	Species sp;
	SpatialSpeciesRxnPlugin srplugin = null;
	for (int i = 0; i < model.getNumSpecies(); i++) {
		sp = model.getSpecies(i);
		System.out.println("Species" + i + " : " + sp.getId());
		reqPlugin = (RequiredElementsSBasePlugin)sp.getPlugin("req");
		if (!reqPlugin.getMathOverridden().equals("")) {
			System.out.print("\t req:MO = " + reqPlugin.getMathOverridden() + "\t req:CoreAltMath = " + reqPlugin.getCoreHasAlternateMath());
		}
		srplugin = (SpatialSpeciesRxnPlugin)sp.getPlugin("spatial");
		if (srplugin.getIsSpatial()) {
			System.out.print("\t isSpatial = " + srplugin.getIsSpatial());
		}
		System.out.println("\n");
	}

	// parameters
	SpatialParameterPlugin spplugin = null;
	for (int i = 0; i < model.getNumParameters(); i++) {
		Parameter p = model.getParameter(i);
		System.out.print("Parameter" + i + " : " + p.getId());
		reqPlugin = (RequiredElementsSBasePlugin)p.getPlugin("req");
		if (!reqPlugin.getMathOverridden().equals("")) {
			System.out.print("\t req:MO = " + reqPlugin.getMathOverridden() + "\t req:CoreAltMath = " + reqPlugin.getCoreHasAlternateMath());
		}
		spplugin = (SpatialParameterPlugin)p.getPlugin("spatial");
		SpatialSymbolReference spSymRef = spplugin.getSpatialSymbolReference();
		if (spSymRef.isSetSpatialId()) {
			System.out.print("\t ssr:id = " + spSymRef.getSpatialId() + "\t ssr:type = " + spSymRef.getType());
		}
		DiffusionCoefficient diffCoeff = spplugin.getDiffusionCoefficient();
		if (diffCoeff.isSetVariable()) {
			System.out.print("\t dc:var = " + diffCoeff.getVariable() + "\t dc:indx = " + diffCoeff.getCoordinateIndex());
		}
		AdvectionCoefficient advCoeff = spplugin.getAdvectionCoefficient();
		if (advCoeff.isSetVariable()) {
			System.out.print("\t ac:var = " + advCoeff.getVariable() + "\t ac:indx = " + advCoeff.getCoordinateIndex());
		}
		BoundaryCondition bc = spplugin.getBoundaryCondition();
		if (bc.isSetVariable()) {
			System.out.print("\t bc:var = " + bc.getVariable() + "\t bc:coordBoundary = " + bc.getCoordinateBoundary() + "\t bc:Type = " + bc.getType());
		}
		System.out.println("\n");
	}
	
	//
	// Get a SpatialModelPlugin object plugged in the model object.
	//
	// The type of the returned value of SBase::getPlugin() function is 
	// SBasePlugin*, and thus the value needs to be cast for the 
	// corresponding derived class.
	//
	SpatialModelPlugin mplugin = (SpatialModelPlugin)model.getPlugin("spatial");
	System.out.println("URI : " + mplugin.getURI() + "\t; Prefix : " + mplugin.getPrefix());

	// get a Geometry object via SpatialModelPlugin object.
	Geometry geometry = mplugin.getGeometry();
	System.out.println("Geom coordSystem : " + geometry.getCoordinateSystem());
    
	// get a CoordComponent object via the Geometry object.	
	CoordinateComponent coordComp = geometry.getCoordinateComponent(0);
	System.out.println("CoordComp spatialId: " + coordComp.getSpatialId() + ";  compType: " + coordComp.getComponentType() +
			";  sbmlUnit: " + coordComp.getSbmlUnit() + ";  index: " + coordComp.getIndex());
	BoundaryMin minX = coordComp.getBoundaryMin();
	System.out.println("Min name: " + minX.getSpatialId() + "; Min value : " + minX.getValue());
	BoundaryMax maxX = coordComp.getBoundaryMax();
	System.out.println("Max name: " + maxX.getSpatialId() + "; Max value : " + maxX.getValue());

	// get a DomainType object via the Geometry object.	
	DomainType domainType = geometry.getDomainType(0);
	System.out.println("DomainType spatialId: " + domainType.getSpatialId() + ";  spDim: " + domainType.getSpatialDimensions());
	
	// get a Domain object via the Geometry object.	
	Domain domain = geometry.getDomain(0);
	System.out.println("Domain spatialId: " + domain.getSpatialId() + ";  implicit: " + domain.getImplicit() +
			";  domainType: " + domain.getDomainType() + ";  ShapeId: " + domain.getShapeId());
	
	// get an interior Point object via the Domain object.	
	InteriorPoint intPt = domain.getInteriorPoint(0);
	System.out.println("Point coord1: " + intPt.getCoord1());

	domain = geometry.getDomain(1);
	System.out.println("Domain spatialId: " + domain.getSpatialId() + ";  implicit: " + domain.getImplicit() +
			";  domainType: " + domain.getDomainType() + ";  ShapeId: " + domain.getShapeId());

	// get an interior Point object via the Domain object.	
	intPt = domain.getInteriorPoint(0);
	System.out.println("Point coord1: " + intPt.getCoord1());

	// get an AdjacentDomains object via the Geometry object.	
	AdjacentDomains adjDomains = geometry.getAdjacentDomains(0);
	System.out.println("AdjDomain spatialId: " + adjDomains.getSpatialId() + ";  domain1: " + adjDomains.getDomain1() +
			";  domain2: " + adjDomains.getDomain2());
	
	// get an AnalyticGeometry object via the Geometry object.
	GeometryDefinition gd;
	for (int i = 0; i < geometry.getNumGeometryDefinitions(); i++) {
		gd = geometry.getGeometryDefinition(i);

		if (gd instanceof AnalyticGeometry) {
			AnalyticGeometry ag = (AnalyticGeometry)gd;
			System.out.println("AnalyticGeom spatialId: " + ag.getSpatialId());
			AnalyticVolume av = ag.getAnalyticVolume(0);
			System.out.println("AnalyticVol spatialId: " + av.getSpatialId() + ";  domainType: " + av.getDomainType() +
					";  FuncType: " + av.getFunctionType() + ";  ordinal: " + av.getOrdinal());
			String mathFormulaStr = libsbml.formulaToString(av.getMath());
			System.out.println("AnalyticVol Math : " + mathFormulaStr);
		}
		if (gd instanceof SampledFieldGeometry) {
			SampledFieldGeometry sfGeom = (SampledFieldGeometry)gd;
			System.out.println("SampledFieldGeom spatialId: " + sfGeom.getSpatialId());
			
			// sampledField from sfGeom
			SampledField sf = sfGeom.getSampledField();
			System.out.println("SampledField spatialId: " + sf.getSpatialId());
			System.out.println("SampledField dataType: " + sf.getDataType());
			System.out.println("SampledField interpolation: " + sf.getInterpolationType());
			System.out.println("SampledField encoding: " + sf.getEncoding());
			System.out.println("SampledField numSamples1: " + sf.getNumSamples1());
			System.out.println("SampledField numSamples2: " + sf.getNumSamples2());
			System.out.println("SampledField numSamples3: " + sf.getNumSamples3());

			ImageData id = sf.getImageData();
			int[] samples = new int[(int) id.getSamplesLength()];
			id.getSamples(samples);
			System.out.println("ImageData samples[0]: " + samples[0]);
			System.out.println("ImageData samplesLen: " + id.getSamplesLength());

			// sampledVolVol from sfGeom.
			SampledVolume sv = sfGeom.getSampledVolume(0);
			System.out.println("SampledVol spatialId: " + sv.getSpatialId());
			System.out.println("SampledVol domainType: " + sv.getDomainType());
			System.out.println("SampledVol sampledVal: " + sv.getSampledValue());
			System.out.println("SampledVol min: " + sv.getMinValue());
			System.out.println("SampledVol max: " + sv.getMaxValue());
		}

	}
	
	document.delete();
}

/*
private static void writeLayoutSBML() {
	  // SBMLNamespaces of SBML Level 3 Version 1 with Spatial Version 1
	  SBMLNamespaces sbmlns = new SBMLNamespaces(3,1,"layout",1);
	  // SpatialPkgNamespaces spatialns(3,1,1);

	  // create the L3V1 document with spatial package
	  SBMLDocument document = new SBMLDocument(sbmlns);	

	  // create the Model 
	  Model model = document.createModel();
	  model.setId("trial_layout");
	  model.setName("trial_layout");

	  // create the Compartments

	  Compartment compartment = model.createCompartment();
	  compartment.setId("cytosol");
	  compartment.setConstant(true);

	  // create the Species

	  Species species = model.createSpecies();
	  species.setId("ATPc");
	  species.setCompartment("cytosol");
	  species.setInitialConcentration(1);
	  species.setHasOnlySubstanceUnits(false);
	  species.setBoundaryCondition(false);
	  species.setConstant(false);

	  //
	  // Get a LayoutModelPlugin object plugged in the model object.
	  //
	  // The type of the returned value of SBase::getPlugin() function is 
	  // SBasePlugin*, and thus the value needs to be casted for the 
	  // corresponding derived class.
	  //
	  int numPlugins = model.getNumPlugins();
	  SBasePlugin pluginL = model.getPlugin("layout");
//	  SBasePlugin pluginS = model.getPlugin("spatial");
//	  SBasePlugin pluginG = model.getPlugin("groups");
//	  String pkgname = pluginL.getPackageName();
//	  String uri = pluginL.getURI();
//	  String pre = pluginL.getPrefix();
//	  long pkgver = pluginL.getPackageVersion();
	  LayoutModelPlugin mplugin = null;
	  if (pluginL instanceof SpatialModelPlugin) {
		  System.out.println("YAY! I am a spatial Model plug in!!!");
		  mplugin = (LayoutModelPlugin)pluginL;
	  }
}*/

private static File testVCSBMLSpatialExporter(File vcmlFile) {
	try {
		XMLSource vcmlSrc = new XMLSource(vcmlFile);
		BioModel bioModel = XmlHelper.XMLToBioModel(vcmlSrc);
		// simulate non-spatial applications (that are not stochastic).
		SimulationContext[] simContexts = bioModel.getSimulationContexts();
		for (SimulationContext simContext : simContexts){
			if (simContext.getGeometry().getDimension()>0){
				Structure topStructure = simContext.getModel().getTopFeature();
				StructureMapping.StructureMappingParameter sizeParm = simContext.getGeometryContext().getStructureMapping(topStructure).getSizeParameter();
				if (sizeParm==null || sizeParm.getExpression()==null || sizeParm.getExpression().isZero()){
					StructureSizeSolver.updateAbsoluteStructureSizes(simContext, topStructure, 1000.0, VCUnitDefinition.UNIT_um3);
				}
				SBMLSpatialExporter sbmlSpatialExporter = new SBMLSpatialExporter(bioModel);
				sbmlSpatialExporter.setSelectedSimContext(simContext);
				String sbmlText = sbmlSpatialExporter.getSBMLFile();
				String filePath = vcmlFile.getPath().substring(0, vcmlFile.getPath().lastIndexOf("\\")+1);
				String sbmlFileName = TokenMangler.mangleToSName(bioModel.getName());
				File sbmlFile = new File(filePath + sbmlFileName + ".xml");
				FileWriter fileWriter = new FileWriter(sbmlFile);
				fileWriter.write(sbmlText);
				fileWriter.flush();
				fileWriter.close();
				return sbmlFile;
			}
		}
	} catch (Exception e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Unable to export spatial application to SBML : " + e.getMessage());
	}
	return null;
}

private static void testVCSBMLSpatialImporter(File sbmlFile) {
	if (sbmlFile == null) {
		throw new RuntimeException("Error importing from SBML : SBML file is null.");
	}
    VCDocument vcDoc = null;
    cbit.util.xml.VCLogger vcLogger = new cbit.util.xml.VCLogger() {
        private StringBuffer buffer = new StringBuffer();
        public void sendMessage(int messageLevel, int messageType) {
            String message = cbit.util.xml.VCLogger.getDefaultMessage(messageType);
            sendMessage(messageLevel, messageType, message);	
        }
        public void sendMessage(int messageLevel, int messageType, String message) {
            System.out.println("LOGGER: msgLevel="+messageLevel+", msgType="+messageType+", "+message);
            if (messageLevel==VCLogger.HIGH_PRIORITY){
            	throw new RuntimeException("SBML Import Error: "+message);
            }
        }
        public void sendAllMessages() {
        }
        public boolean hasMessages() {
            return false;
        }
    };

	SBMLSpatialImporter sbmlSpImporter = new SBMLSpatialImporter(sbmlFile.getAbsolutePath(), vcLogger);
	vcDoc = sbmlSpImporter.getBioModel();
	vcDoc.refreshDependencies();
	String vcmlStr = null;
	try {
		vcmlStr = XmlHelper.bioModelToXML((BioModel)vcDoc);
		String filePath = sbmlFile.getPath().substring(0, sbmlFile.getPath().lastIndexOf("\\")+1);
		String vcmlFileName = TokenMangler.mangleToSName(vcDoc.getName());
		File vcmlFile = new File(filePath + vcmlFileName + ".vcml");
		FileWriter fileWriter = new FileWriter(vcmlFile);
		fileWriter.write(vcmlStr);
		fileWriter.flush();
		fileWriter.close();
	} catch (Exception e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Unable to convert import biomodel into VCML string or write to VCML file : " + e.getMessage());
	}

}
}
