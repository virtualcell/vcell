package org.vcell.sbml;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

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

@Ignore
public class SBMLUnitTranslatorTest {

	public static File[] getBiomodelsCuratedSBMLFiles(){
		File[] sbmlFiles = new File("/Users/schaff/Documents/workspace-maven/BioModels_Database-r30_pub-sbml_files/curated/").listFiles();
		return sbmlFiles;
	}
	
	@Test
	public void testSBMLtoVCell() throws XMLStreamException, IOException, SbmlException {
		File[] sbmlFiles = getBiomodelsCuratedSBMLFiles();
//		File[] sbmlFiles = new File[] {
//				new File("/Users/schaff/Documents/workspace-maven/BioModels_Database-r30_pub-sbml_files/curated/BIOMD0000000001.xml"),
//				new File("/Users/schaff/Documents/workspace-maven/BioModels_Database-r30_pub-sbml_files/curated/BIOMD0000000101.xml"),
//			new File("/Users/schaff/Documents/workspace-maven/sbml-test-suite/cases/semantic/00001/00001-sbml-l3v1.xml")
//		};
		for (File sbmlFile : sbmlFiles){
			if (sbmlFile.getName().equals("BIOMD0000000539.xml")){
				System.err.println("skipping this model, seems like a bug in jsbml  RenderParser.processEndDocument() ... line 403 ... wrong constant for extension name");
				continue;
			}
			SBMLDocument doc = SBMLReader.read(sbmlFile);
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
			if (sbmlFile == sbmlFiles[0]){
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

}
