package org.jlibsedml.execution;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystems;
import java.util.List;

import org.jlibsedml.ArchiveComponents;
import org.jlibsedml.IModelContent;

public class ArchiveModelResolver implements IModelResolver {
    public final static String SPACE_URI_ESCAPE_SEQUENCE = "%20";

  private ArchiveComponents ac;
  private String sedmlPath = "";
    public ArchiveModelResolver(ArchiveComponents ac) {
        this.ac=ac;
    }
    public String getModelXMLFor(URI modelURI) {
        List<IModelContent> children = ac.getModelFiles();
        // VCML sources exported by VCell append an application XPath after '#'.
        // Resolve the containing document here: ZIP entry names do not include
        // URI fragments. Keep the caller's URI intact for application selection.
        String modelStr = modelURI.toString();
        int fragmentIndex = modelStr.indexOf('#');
        if (fragmentIndex >= 0) {
            modelStr = modelStr.substring(0, fragmentIndex);
        }
        // Strip before decoding: an encoded %23 belongs to the filename, not
        // the fragment. Retain the existing space decoding behavior otherwise.
        modelStr = modelStr.replace(SPACE_URI_ESCAPE_SEQUENCE, " ");
    	// try direct match first
        for (IModelContent imc: children) {
            String modelElementStr = imc.getName();
            if(modelElementStr.equals(modelStr)){
                return imc.getContents();
            }
        }
        // try relative path to sedml
        File sedLocation = new File(sedmlPath);
        String modelLocation = new File(sedLocation, modelStr).toString();
		modelStr = FileSystems.getDefault().getPath(modelLocation).normalize().toString();
        for (IModelContent imc: children) {
            String modelElementStr = FileSystems.getDefault().getPath(imc.getName()).normalize().toString();
            if(modelElementStr.equals(modelStr)){
                return imc.getContents();
            }
        }
        // couldn't resolve
        return null;
    }
	public void setSedmlPath(String sedmlPath) {
		this.sedmlPath = sedmlPath;
	}

}
