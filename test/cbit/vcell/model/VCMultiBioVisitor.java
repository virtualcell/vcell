package cbit.vcell.model;

import java.io.PrintWriter;

import org.vcell.util.document.BioModelInfo;

import cbit.vcell.biomodel.BioModel;

public interface VCMultiBioVisitor extends Iterable<BioModel >{
	/**
	 * first pass filter -- evaluate model on information available in Info summary object
	 * @param bioModelInfo
	 * @return true if model should be processs further
	 */
	public boolean filterBioModel(BioModelInfo bioModelInfo);
	
	/**
	 * second pass filter -- evaluate model on information available in model itself 
	 * @param bioModelInfo
	 * @return true if model should be process further
	 */
	public boolean filterBioModel(BioModel bioModel);
	/**
	 * set biomodel for additional processing
	 * @param bioModel
	 * @param printWriter
	 */
	public void setBioModel(BioModel bioModel, PrintWriter printWriter);

}
