package org.jlibsedml.execution;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.util.List;

import org.jlibsedml.ArchiveComponents;
import org.jlibsedml.IModelContent;

public class ArchiveModelResolver implements IModelResolver {
  private ArchiveComponents ac;
  private String sedmlPath = "";
    public ArchiveModelResolver(ArchiveComponents ac) {
        this.ac=ac;
    }
    public String getModelXMLFor(URI modelURI) {
        List<IModelContent> children = ac.getModelFiles();
    	String modelStr = modelURI.toString();
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
