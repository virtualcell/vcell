package org.jlibsedml.execution;

import java.net.URI;
import java.util.List;

import org.jlibsedml.ArchiveComponents;
import org.jlibsedml.IModelContent;

public class ArchiveModelResolver implements IModelResolver {
  private ArchiveComponents ac;
    public ArchiveModelResolver(ArchiveComponents ac) {
        this.ac=ac;
    }
    public String getModelXMLFor(URI modelURI) {
        String rc=null;
        List<IModelContent> children = ac.getModelFiles();
        for (IModelContent imc: children) {
            String modelElementStr = imc.getName(), modelStr = modelURI.toString();
            //if(imc.getName().endsWith(modelURI.toString())){
            if(modelElementStr.endsWith(modelStr)){
                rc = imc.getContents();
            }
        }
        return rc;
    }

}
