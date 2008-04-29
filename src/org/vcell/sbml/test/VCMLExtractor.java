package org.vcell.sbml.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JPanel;

import org.sbml.libsbml.OFStream;
import org.sbml.libsbml.OStream;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.XMLOutputStream;
import org.vcell.sbml.vcell.MathModel_SBMLExporter;

import cbit.sql.VersionableType;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.MathModelWindowManager;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.CommentStringTokenizer;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;
import cbit.vcell.modeldb.VersionableTypeVersion;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

public class VCMLExtractor implements VCDatabaseVisitor {
	int count = 0;
	final int MAX = 10;
	File directory = null;

	public boolean filterBioModel(BioModelInfo bioModelInfo) {
		//
		// only biomodels that have non-spatial applications.
		//
		if (bioModelInfo.getBioModelChildSummary()==null){
			return false;
		}
		int[] dims = bioModelInfo.getBioModelChildSummary().getGeometryDimensions();
		for (int i = 0; i < dims.length; i++) {
			if (dims[i] == 0){
				if (count++ < MAX){
					return true;
				}
			}
		}
		return false;
	}

	public void visitBioModel(BioModel bioModel, PrintStream p) {
		try {
			//
			// write out entire BioModel as a VCML file
			//
//			p.println("visiting bio-model <"+bioModel.getName()+","+bioModel.getVersion().getVersionKey()+">------------------------------");
//			String biomodelXML = XmlHelper.bioModelToXML(bioModel);
//			File biomodelFile = new File(directory,"BIOMODEL_"+bioModel.getVersion().getVersionKey().toString()+".vcml");
//			XmlUtil.writeXMLString(biomodelXML, biomodelFile.getAbsolutePath());

			//
			// get first non-spatial Application and write it out as a MathModel in VCML.
			//
			SimulationContext[] simContexts = bioModel.getSimulationContexts();
			SimulationContext simContext = null;
			for (int i=0;i<simContexts.length; i++){
				if (simContexts[i].getGeometry().getDimension()==0){
					simContext = simContexts[i];
					break;
				}
			}
			if (simContext==null){
				throw new RuntimeException("couldn't find a non-spatial application");
			}
			p.println("visiting bio-model <"+bioModel.getName()+","+bioModel.getVersion().getVersionKey()+">---------<"+"selected application "+simContext.getName()+">---------------------");
			MathModel newMathModel = new MathModel(null);
			MathDescription mathDesc = simContext.getMathDescription();
			newMathModel.setName(simContext.getName());
			newMathModel.setMathDescription(mathDesc);
			
			File biomodelFile = new File(directory,"BIOMODEL_Math_"+bioModel.getVersion().getVersionKey().toString()+".vcml");
			XmlUtil.writeXMLString(XmlHelper.mathModelToXML(newMathModel), biomodelFile.getAbsolutePath());
			SBMLDocument sbmlDocument = MathModel_SBMLExporter.getSBML(newMathModel);
			sbmlDocument.write(new XMLOutputStream(new OFStream(new File(directory,"BIOMODEL_Math_"+bioModel.getVersion().getVersionKey()+".sbml").getAbsolutePath())));
		} catch (Exception e1) {
			e1.printStackTrace(p);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		VCMLExtractor visitor = new VCMLExtractor();
		File baseDirectory = new File("c:\\temp\\biomodels\\math_vcml\\");
		if (!baseDirectory.exists()){
			baseDirectory.mkdirs();
		}
		visitor.directory = new File(baseDirectory,args[0]);
		if (!visitor.directory.exists()){
			visitor.directory.mkdirs();
		}
		boolean bAbortOnDataAccessException = false;
		try{
			VCDatabaseScanner.scanBioModels(args, visitor, bAbortOnDataAccessException);
		}catch(Exception e){e.printStackTrace(System.err);}
	}

	//
	// required for implementation of interface ... not used.
	//
	
	public boolean filterGeometry(GeometryInfo geometryInfo) {
		return false;
	}

	public void visitGeometry(Geometry geometry, PrintStream arg_p) {
	}


}
