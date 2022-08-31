package org.vcell.sbml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.UnitDefinition;
import org.vcell.sbml.vcell.SBMLUnitTranslator;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;

public class SBMLUnitTranslatorTest {

	public static File[] getBiomodelsCuratedSBMLFiles(){
		File[] sbmlFiles = new File("/Users/schaff/Documents/workspace-maven/BioModels_Database-r30_pub-sbml_files/curated/").listFiles();
		return sbmlFiles;
	}

	final String sbml_prefix =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<sbml xmlns=\"http://www.sbml.org/sbml/level3/version1/core\" level=\"3\" version=\"1\">\n" +
			"  <model metaid=\"model\" id=\"model\" name=\"model\">\n" +
			"     <listOfUnitDefinitions>\n";


	final String sbml_suffix =
			"     </listOfUnitDefinitions>" +
			"  </model>" +
			"</sbml>";


	String sbmlUnitDefinition_um =
					sbml_prefix +
					"        <unitDefinition id=\"unitid\">\n" +
					"            <listOfUnits>\n" +
					"                <unit kind=\"metre\" exponent=\"1\" scale=\"-6\" multiplier=\"1\"/>\n" +
					"            </listOfUnits>\n" +
					"        </unitDefinition>\n" +
					sbml_suffix;

	String sbmlUnitDefinition_um2 =
					sbml_prefix +
					"        <unitDefinition id=\"unitid\">\n" +
					"            <listOfUnits>\n" +
					"                <unit kind=\"metre\" exponent=\"2\" scale=\"-6\" multiplier=\"1\"/>\n" +
					"            </listOfUnits>\n" +
					"        </unitDefinition>\n" +
					sbml_suffix;

	String sbmlUnitDefinition_um2_per_second =
					sbml_prefix +
					"        <unitDefinition id=\"unitid\">\n" +
					"            <listOfUnits>\n" +
					"                <unit kind=\"metre\" exponent=\"2\" scale=\"-6\" multiplier=\"1\"/>\n" +
					"                <unit kind=\"second\" exponent=\"-1\" scale=\"0\" multiplier=\"1\"/>\n" +
					"            </listOfUnits>\n" +
					"        </unitDefinition>\n" +
					sbml_suffix;

	String getSbmlUnitDefinition_KMOLE =
					sbml_prefix +
					"      <unitDefinition id=\"unitid\">\n" +
					"        <listOfUnits>\n" +
					"          <unit kind=\"dimensionless\" exponent=\"1\" scale=\"0\" multiplier=\"1e-21\"/>\n" +
					"          <unit kind=\"item\" exponent=\"-1\" scale=\"0\" multiplier=\"1\"/>\n" +
					"          <unit kind=\"mole\" exponent=\"1\" scale=\"0\" multiplier=\"1\"/>\n" +
					"        </listOfUnits>\n" +
					"      </unitDefinition>\n" +
					sbml_suffix;

	@Test
	public void testImportSBMLUnit_um() throws XMLStreamException {
		final VCUnitSystem vcUnitSystem = new BioModel(null).getModel().getUnitSystem();
		VCUnitDefinition expectedUnit = vcUnitSystem.getInstance("um");

		UnitDefinition sbmlUnit_um = new SBMLReader().readSBMLFromString(sbmlUnitDefinition_um).getModel().getListOfUnitDefinitions().get("unitid");
		VCUnitDefinition vcUnit_um = SBMLUnitTranslator.getVCUnitDefinition(sbmlUnit_um, vcUnitSystem);

		Assert.assertEquals(vcUnit_um.getSymbol(), expectedUnit.getSymbol());
	}

	@Test
	public void testImportSBMLUnit_um2() throws XMLStreamException {
		final VCUnitSystem vcUnitSystem = new BioModel(null).getModel().getUnitSystem();
		VCUnitDefinition expectedUnit = vcUnitSystem.getInstance("um2");

		UnitDefinition sbmlUnit_um2 = new SBMLReader().readSBMLFromString(sbmlUnitDefinition_um2).getModel().getListOfUnitDefinitions().get("unitid");
		VCUnitDefinition vcUnit_um2 = SBMLUnitTranslator.getVCUnitDefinition(sbmlUnit_um2, vcUnitSystem);

		Assert.assertEquals(vcUnit_um2.getSymbol(), expectedUnit.getSymbol());
	}

	@Test
	public void testImportSBMLUnit_um2_per_second() throws XMLStreamException {
		final VCUnitSystem vcUnitSystem = new BioModel(null).getModel().getUnitSystem();
		VCUnitDefinition expectedUnit = vcUnitSystem.getInstance("um2.s-1");

		UnitDefinition sbmlUnit_um2_per_s = new SBMLReader().readSBMLFromString(sbmlUnitDefinition_um2_per_second).getModel().getListOfUnitDefinitions().get("unitid");
		VCUnitDefinition vcUnit_um2_per_s = SBMLUnitTranslator.getVCUnitDefinition(sbmlUnit_um2_per_s, vcUnitSystem);

		Assert.assertEquals(vcUnit_um2_per_s.getSymbol(), expectedUnit.getSymbol());
	}

	@Test
	public void testImportSBMLUnit_KMOLE() throws XMLStreamException {
		final VCUnitSystem vcUnitSystem = new BioModel(null).getModel().getUnitSystem();
		VCUnitDefinition expectedUnit = new BioModel(null).getModel().getReservedSymbolByName("KMOLE").getUnitDefinition();

		UnitDefinition sbmlUnit = new SBMLReader().readSBMLFromString(getSbmlUnitDefinition_KMOLE).getModel().getListOfUnitDefinitions().get("unitid");
		VCUnitDefinition vcUnit = SBMLUnitTranslator.getVCUnitDefinition(sbmlUnit, vcUnitSystem);

		Assert.assertTrue("expected=["+expectedUnit.getSymbol()+"], parsed=["+vcUnit.getSymbol()+"] are not equivalent", expectedUnit.isEquivalent(vcUnit));
	}

	@Test
	public void testKMOLE_Derivation() {
		final VCUnitSystem vcUnitSystem = new BioModel(null).getModel().getUnitSystem();
		cbit.vcell.model.Model.ReservedSymbol KMOLE = new BioModel(null).getModel().getReservedSymbolByName("KMOLE");

		VCUnitDefinition vcFluxUnits = vcUnitSystem.getInstance("uM.um.s-1");
		VCUnitDefinition vcMemReactUnits = vcUnitSystem.getInstance("molecules.um-2.s-1");
		Assert.assertFalse("flux and membrane units are incompatible because moles/molecules are different", vcFluxUnits.isCompatible(vcMemReactUnits));
		System.out.println("["+vcFluxUnits.getSymbol()+"] / ["+vcMemReactUnits.getSymbol()+"] = ["+vcFluxUnits.divideBy(vcMemReactUnits).getSymbol()+"]");
//		VCUnitDefinition KMOLE_expected_units = vcFluxUnits.divideBy(vcMemReactUnits);
//		Assert.assertTrue("KMOLE units should be equivalent to flux/membrane units", );
//
//		VCUnitDefinition fluxToMembraneReactUnits = vcFluxUnits.convertTo(1, vcMemReactUnits);
//		VCUnitDefinition fluxToMembraneReactUnits = vcFluxUnits.convertTo(1, vcMemReactUnits);
//		UnitDefinition sbmlUnit = new SBMLReader().readSBMLFromString(getSbmlUnitDefinition_KMOLE).getModel().getListOfUnitDefinitions().get("unitid");
//		VCUnitDefinition vcUnit = SBMLUnitTranslator.getVCUnitDefinition(sbmlUnit, vcUnitSystem);
//
//		Assert.assertTrue("expected=["+expectedUnit.getSymbol()+"], parsed=["+vcUnit.getSymbol()+"] are not equivalent", expectedUnit.isEquivalent(vcUnit));
	}

	@Ignore
	@Test
	public void testSBMLtoVCell() throws XMLStreamException, IOException, SbmlException {
		int[] biomodelsIds = SbmlTestSuiteFiles.getBiomodelsModels();
//		File[] sbmlFiles = new File[] {
//				new File("/Users/schaff/Documents/workspace-maven/BioModels_Database-r30_pub-sbml_files/curated/BIOMD0000000001.xml"),
//				new File("/Users/schaff/Documents/workspace-maven/BioModels_Database-r30_pub-sbml_files/curated/BIOMD0000000101.xml"),
//			new File("/Users/schaff/Documents/workspace-maven/sbml-test-suite/cases/semantic/00001/00001-sbml-l3v1.xml")
//		};
		for (int biomodelId : biomodelsIds){
			SBMLDocument doc = SBMLReader.read(SbmlTestSuiteFiles.getSbmlTestCase(biomodelId));
			BioModel bioModel = new BioModel(null);
			VCUnitSystem unitSystem = bioModel.getModel().getUnitSystem();
			Model sbmlModel = doc.getModel();
	
			ListOf<UnitDefinition> listOfUnitDefinitions = sbmlModel.getListOfUnitDefinitions();
			for (UnitDefinition sbmlUnitDef : listOfUnitDefinitions){
				VCUnitDefinition vcUnit = SBMLUnitTranslator.getVCUnitDefinition(sbmlUnitDef, unitSystem);
				UnitDefinition new_sbmlUnitDef = SBMLUnitTranslator.getSBMLUnitDefinition(vcUnit, 3, 1, unitSystem);
				VCUnitDefinition new_vcUnit = SBMLUnitTranslator.getVCUnitDefinition(new_sbmlUnitDef, unitSystem);
				if (!vcUnit.getSymbol().equals(new_vcUnit.getSymbol())){
					System.err.println("orig vcUnit '"+vcUnit.getSymbol()+"' doesn't match new vcUnit '"+new_vcUnit.getSymbol()+"'");
				}
	//			System.out.println("sbmlUnit = "+sbmlUnitDef.toString()+", vcUnit = "+vcUnit.getSymbol());
				System.out.println("sbmlUnit("+sbmlUnitDef.getClass().getName()+", builtin="+sbmlUnitDef.isVariantOfSubstance()+") = "+sbmlUnitDef.toString()+", id="+sbmlUnitDef.getId()+",  name="+sbmlUnitDef.getName()+",   vcUnit = "+vcUnit.getSymbol());
				if (sbmlUnitDef.getNumUnits()>1){
					System.out.println("vcUnit = "+vcUnit.getSymbol());
					for (Unit unit : sbmlUnitDef.getListOfUnits()){
						try {
							//VCUnitDefinition vcUnit = unitSystem.getInstance(unit.getKind().getName());
							System.out.println("    vcUnit = "+unit);
						}catch (Exception e){
							e.printStackTrace();
						}
					}
					System.out.println("found bigger unit, "+sbmlUnitDef);
				}
			}
				System.out.println("sbml length unit = "+sbmlModel.getLengthUnitsInstance()+", idref="+sbmlModel.getLengthUnits());
				System.out.println("sbml area unit = "+sbmlModel.getAreaUnitsInstance()+", idref="+sbmlModel.getAreaUnits());
				System.out.println("sbml volume unit = "+sbmlModel.getVolumeUnitsInstance()+", idref="+sbmlModel.getVolumeUnits());
				System.out.println("sbml time unit = "+sbmlModel.getTimeUnitsInstance()+", idref="+sbmlModel.getTimeUnits());
				System.out.println("sbml extent unit = "+sbmlModel.getExtentUnitsInstance()+", idref="+sbmlModel.getExtentUnits());
				System.out.println("sbml substance unit = "+sbmlModel.getSubstanceUnitsInstance()+", idref="+sbmlModel.getSubstanceUnits());
				for (UnitDefinition sbmlUnitDef : sbmlModel.getListOfPredefinedUnitDefinitions()){
					if (sbmlUnitDef.getNumUnits()==1 && sbmlUnitDef.getUnit(0).isAvogadro()){
						continue;
					}
					if (sbmlUnitDef.getNumUnits()==1 && sbmlUnitDef.getUnit(0).isKatal()){
						continue;
					}
					VCUnitDefinition vcUnit = SBMLUnitTranslator.getVCUnitDefinition(sbmlUnitDef, unitSystem);
		//			System.out.println("sbmlUnit = "+sbmlUnitDef.toString()+", vcUnit = "+vcUnit.getSymbol());
					System.out.println("sbmlUnit("+sbmlUnitDef.getClass().getName()+", builtin="+sbmlUnitDef.isVariantOfSubstance()+") = "+sbmlUnitDef.toString()+", id="+sbmlUnitDef.getId()+",  name="+sbmlUnitDef.getName()+",   vcUnit = "+vcUnit.getSymbol());
		//			for (Unit unit : sbmlUnitDef.getListOfUnits()){
		//				try {
		//					VCUnitDefinition vcUnit = unitSystem.getInstance(unit.getKind().getName());
		//					System.out.println("    vcUnit = "+vcUnit.getSymbol());
		//				}catch (Exception e){
		//					e.printStackTrace();
		//				}
		//			}
				}
		}
	}

}
