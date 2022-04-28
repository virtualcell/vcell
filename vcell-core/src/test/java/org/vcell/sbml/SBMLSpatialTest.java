package org.vcell.sbml;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
//import org.junit.Assert;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.UnitDefinition;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.SBMLExporter.VCellSBMLDoc;
import org.vcell.sbml.vcell.SBMLImporter;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

public class SBMLSpatialTest {

	//@Before
	public void setUp() throws Exception {
	}

	//@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void test() throws Exception {
		//BioModel bioModel1 = BioModelTest.getExampleWithImage();
		URL vcmlURL = SBMLSpatialTest.class.getResource("Solver_Suite_6_2.vcml");
		File vcmlFile = new File(vcmlURL.toURI());
		BioModel bioModel1 = XmlHelper.XMLToBioModel(new XMLSource(vcmlFile));
		bioModel1.refreshDependencies();
		//for (int i = 0; i<bioModel1.getNumSimulationContexts(); i++){
		for (int i=5;i==5;i++){
			SimulationContext sc1 = bioModel1.getSimulationContext(i);
			if (sc1.getApplicationType()!=Application.NETWORK_DETERMINISTIC){
				System.err.println(sc1.getName()+" is not a network determistic application");
				continue;
			}
			boolean isSpatial = sc1.getGeometry().getDimension()>0;
			sc1.refreshMathDescription(null, NetworkGenerationRequirements.ComputeFullNoTimeout);
			String sbmlString = XmlHelper.exportSBML(sc1.getBioModel(), 3, 1, 0, isSpatial, sc1, null);
			File tempFile = File.createTempFile("sbmlSpatialTest_SBML_", ".sbml.xml");
			FileUtils.write(tempFile, sbmlString);
			System.out.println(tempFile);
			
			try {
				VCLogger argVCLogger = new TLogger();
				SBMLImporter importer = new SBMLImporter(tempFile.getAbsolutePath(), argVCLogger, isSpatial);
				BioModel bioModel2 = importer.getBioModel();
				File tempFile2 = File.createTempFile("sbmlSpatialTest_Biomodel_", ".vcml.xml");
				FileUtils.write(tempFile2, XmlHelper.bioModelToXML(bioModel2));
				System.out.println(tempFile2);
				
				//if (true) { throw new RuntimeException("stop"); }
				
				bioModel2.refreshDependencies();
				SimulationContext sc2 = bioModel2.getSimulationContext(0);
	//			sc2.refreshMathDescription(null, NetworkGenerationRequirements.ComputeFullNoTimeout);
				sc2.setMathDescription(sc2.createNewMathMapping(null, NetworkGenerationRequirements.ComputeFullNoTimeout).getMathDescription());
				if (!sc1.getMathDescription().isValid()){
					throw new RuntimeException("sc1.math is not valid");
				}
				if (!sc2.getMathDescription().isValid()){
					throw new RuntimeException("sc2.math is not valid");
				}
				MathCompareResults mathCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(),sc1.getMathDescription(), sc2.getMathDescription());
				if (!mathCompareResults.isEquivalent()){
					System.out.println("MATH DESCRIPTION 1 <UNCHANGED>");
					System.out.println(sc1.getMathDescription().getVCML_database());
					System.out.println("MATH DESCRIPTION 2 <UNCHANGED>");
					System.out.println(sc2.getMathDescription().getVCML_database());

//					if (mathCompareResults.decision  == Decision.MathDifferent_SUBDOMAINS_DONT_MATCH){
//						BioModel bioModel1_copy = XmlHelper.XMLToBioModel(new XMLSource(vcmlFile));
//						bioModel1_copy.refreshDependencies();
//						SimulationContext sc1_copy = bioModel1_copy.getSimulationContext(i);
//						VCImage image = sc1_copy.getGeometry().getGeometrySpec().getImage();
//						if (image!=null){
//							ArrayList<VCPixelClass> pcList = new ArrayList<VCPixelClass>();
//							for (VCPixelClass pc : image.getPixelClasses()){
//								pcList.add(new VCPixelClass(pc.getKey(),SBMLExporter.DOMAIN_TYPE_PREFIX+pc.getPixelClassName(),pc.getPixel()));
//							}
//							image.setPixelClasses(pcList.toArray(new VCPixelClass[0]));
//						}
//						for (GeometryClass gc : sc1_copy.getGeometry().getGeometryClasses()){
//							System.out.println("name before "+gc.getName());
//							gc.setName(SBMLExporter.DOMAIN_TYPE_PREFIX+gc.getName());
//							System.out.println("name after "+gc.getName());
//						}
//						sc1_copy.checkValidity();
//						bioModel1_copy.refreshDependencies();
//						sc1_copy.getGeometry().precomputeAll(new GeometryThumbnailImageFactoryAWT(), true, true);
//						sc1_copy.setMathDescription(sc1_copy.createNewMathMapping(null, NetworkGenerationRequirements.ComputeFullNoTimeout).getMathDescription());
//						MathCompareResults mathCompareResults_renamedSubdomains = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(),sc1_copy.getMathDescription(), sc2.getMathDescription());
//						if (!mathCompareResults_renamedSubdomains.isEquivalent()){
//							System.out.println("MATH DESCRIPTION 1 <RENAMED>");
//							System.out.println(sc1_copy.getMathDescription().getVCML_database());
//							Assert.fail(mathCompareResults_renamedSubdomains.decision+" "+mathCompareResults_renamedSubdomains.details);
//						}
//					}else{
						System.err.println(mathCompareResults.decision+" "+mathCompareResults.details);
//					}
				}else{
					System.out.println("MATHS WERE EQUIVALENT");
				}
			}finally{
				tempFile.delete();
			}
		} // loop over determinstic applications
		System.out.println("done");
	}
	
	public static void main(String args[]){
		try {
			new SBMLSpatialTest().test();
		}catch (Throwable e){
			e.printStackTrace(System.out);
		}
	}

}
