package org.vcell.sybil.models.io;

/*   FileMan  --- by Oliver Ruebenacker, UCHC --- April 2007 to March 2010
 *   Stores the model and performs input and output operations
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.vcell.sybil.rdf.JenaIOUtil;
import org.vcell.sybil.util.text.StringUtil;

import cbit.vcell.biomodel.BioModel;

import com.hp.hpl.jena.rdf.model.Model;

import org.vcell.sybil.models.ontology.Evaluator;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.factories.SBBoxFactory;
import org.vcell.sybil.models.systemproperty.SystemPropertyRDFFormat;
import org.vcell.sybil.models.views.SBWorkView;

public class FileManager {

	protected String label = "[new file]";
	protected File file;
	protected Evaluator evaluator;
	// TODO remove this field
	protected SBBox box;
	protected SBWorkView view;
	// TODO remove this field
	protected BioModel bioModel;

	protected FileEvent.Listener.FireSet listeners = new FileEvent.Listener.FireSet();

	public FileManager(BioModel bioModel) { 
		this.bioModel = bioModel; 
		if(bioModel != null) { box = bioModel.getVCMetaData().getSBbox(); }
		else { box = SBBoxFactory.create(); }
		view = new SBWorkView(box, bioModel);
		evaluator = new Evaluator(view);
	} 
	
	public FileEvent.Listener.FireSet listeners() { return listeners; }
	
	public FileEvent createFileEvent(FileEvent.Action action) { 
		return new FileEvent(this, action); 
	}
	
	public String label() { return label; }
	public void setLabel(String label) { this.label = label; }
	
	public File file() { return file; }
	public void setFile(File file) { this.file = file; }
	public void setBox(SBBox box) { this.box = box; }
	
	public SBWorkView view() { return view; }
	public SBBox box() { return box; }
	public BioModel bioModel() { return bioModel; }
	
	public Evaluator evaluator() { return evaluator; }
	
	public FileEvent newFile() {
		box().getRdf().removeAll();
		return createFileEvent(FileEvent.FileNew);
	}
	
	public FileEvent inheritedFile() {
		return createFileEvent(FileEvent.FileInherited);
	}
	
		
	public FileEvent openFile(File file, String label) throws IOException { 
		if (file == null) {
			throw new IllegalArgumentException( "No file name was provided");
		} else {
			String text = StringUtil.textFromFile(file);
			JenaIOUtil.modelFromText(box().getRdf(), text);
			setFile(file);
			if(label != null) { setLabel(label); }
			return createFileEvent(FileEvent.FileOpen);
		}
	}
		
	public FileEvent importData(String textNew, Model model, String label) { 
		if(label != null) { setLabel(label); }
		// rdf().removeAll();
		box().getRdf().add(model);
		return createFileEvent(FileEvent.FileImport);
	}

	public FileEvent saveFile() throws Exception {
		if(file() == null) throw new Exception("No file selected to save to.");
		if(box().getRdf() == null) throw new Exception("No model to save.");
		FileOutputStream out = new FileOutputStream(file());
		box().getRdf().write(out, SystemPropertyRDFFormat.rdfFormat().toString());
		return createFileEvent(FileEvent.FileSave);		
	}

	public FileEvent saveFileAs(File file) throws FileNotFoundException {
		FileOutputStream out = new FileOutputStream(file);
		box().getRdf().write(out, SystemPropertyRDFFormat.rdfFormat().toString());
		setFile(file);
		return createFileEvent(FileEvent.FileSaveAs);
	}

}
