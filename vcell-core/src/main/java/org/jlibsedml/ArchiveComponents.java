package org.jlibsedml;

import java.util.List;

/** Encapsulates the contents of a MIASE archive file. <p/>
 * An archive file is a self contained, zipped archive containing a SED-ML file, and one or more XML based
 *  model files which are referred to by the SED-ML document.
 * @author Richard Adams
 *
 */
public class ArchiveComponents {

	private List<IModelContent> modelFiles;
	private List<SEDMLDocument> sedmlDocs;
	
	/**
	 * 
	 * @param modelFiles a non-null, but possible empty list of <code>IModelContent</code> objects.
	 * @param sedmlDoc A {@link SEDMLDocument}
	 * @throws IllegalArgumentException if either parameter is null.
	 */
	public ArchiveComponents(List<IModelContent> modelFiles, List<SEDMLDocument> sedmlDocs) {
		super();
		if(modelFiles ==null || sedmlDocs==null){
			throw new IllegalArgumentException();
		}
		this.modelFiles = modelFiles;
		this.sedmlDocs = sedmlDocs;
	}
	
	/**
	 * Gets the list of model files held by this archive. Modifying this List <em>WILL</em> modify the contents of the underlying List held by this object.
	 * @return a <code>List</code> of model files, not null but may be empty.
	 */
	public List<IModelContent> getModelFiles() {
		return modelFiles;
	}
	
	/**
	 * Removes a model content from this object.
	 * @param toRemove A Non-null {@link IModelContent} object.
	 * @return <code>true</code> if <code>toRemove</code> was removed.
	 */
	public boolean removeModelContent (IModelContent toRemove){
		return modelFiles.remove(toRemove);
	}
	
	/**
	 * Adds a model file to the list of files to be included in the archive.
	 * @param toAdd
	 * @return <code>true</code> if model file added.
	 */
	public boolean addModelContent (IModelContent toAdd){
		return modelFiles.add(toAdd);
	}
	
    /**
     * Gets the SED-ML document stored in this archive.
     * @return A {@link SEDMLDocument}, which will not be null.
     */
	public List<SEDMLDocument> getSedmlDocuments() {
		return sedmlDocs;
	}
	
	@Override
	public String toString() {
		return "ArchiveComponents [modelFiles=" + modelFiles + ", sedmlDocs="
				+ sedmlDocs + "]";
	}
}
