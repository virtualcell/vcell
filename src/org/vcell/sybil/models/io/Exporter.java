/**
 * 
 */
package org.vcell.sybil.models.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.sbml.libsbml.SBMLDocument;
import org.vcell.sybil.models.systemproperty.SystemPropertyRDFFormat;
import org.vcell.sybil.rdf.JenaIOUtil;
import org.vcell.sybil.util.sbml.LibSBMLUtil;
import org.vcell.sybil.util.sbml.LibSBMLUtil.LibSBMLException;

import com.hp.hpl.jena.rdf.model.Model;

public interface Exporter {
	
	@SuppressWarnings("serial")
	public static class ExporterException extends Exception {
		public ExporterException(Throwable t) { super(t); }
	}
	
	public String label();
	public void writeToFile(File file) throws ExporterException;
	public String convertToString() throws ExporterException;
	
	
	public static class ExporterSBML implements Exporter { 
		protected SBMLDocument sbmlDoc;
		public ExporterSBML(SBMLDocument sbmlDoc) { this.sbmlDoc = sbmlDoc; }
		public String label() { return "SBML"; }

		public void writeToFile(File file) throws ExporterException {
			try { LibSBMLUtil.writeSBML(sbmlDoc, file.getAbsolutePath()); } 
			catch (LibSBMLException e) { throw new ExporterException(e); } 				
		}
		public String convertToString() throws ExporterException {
			String string = null;
			try { string = LibSBMLUtil.convertSBMLToString(sbmlDoc); } 
			catch (LibSBMLException e) { throw new ExporterException(e); } 				
			return string;
		}
		
	};
	
	public static class ExporterSBPAX implements Exporter { 
		protected Model model;
		public ExporterSBPAX(Model model) { this.model = model; }
		public String label() { return "SBPAX"; }
		
		public void writeToFile(File file) throws ExporterException {
			try { 
				FileWriter out = new FileWriter(file);
				JenaIOUtil.writeModel(model, out, SystemPropertyRDFFormat.rdfFormat());
			} 
			catch (FileNotFoundException e) { throw new ExporterException(e); } 
			catch (IOException e) { throw new ExporterException(e); }
		}
		
		public String convertToString() {
			return JenaIOUtil.textFromModel(model, SystemPropertyRDFFormat.rdfFormat());
		} 
		
	}; 
	
}