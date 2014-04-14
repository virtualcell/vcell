package cbit.vcell.model;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.vcell.util.document.BioModelInfo;

import cbit.vcell.biomodel.BioModel;

public interface VCMultiBioVisitor extends Iterable<BioModel >{
	public boolean filterBioModel(BioModelInfo bioModelInfo);
	public void setBioModel(BioModel bioModel, PrintWriter printWriter);

}
