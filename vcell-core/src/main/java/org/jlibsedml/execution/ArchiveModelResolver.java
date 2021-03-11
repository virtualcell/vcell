package org.jlibsedml.execution;

import java.io.File;
import java.net.URI;
import java.util.List;

import cbit.vcell.resource.OperatingSystemInfo;
import org.jlibsedml.ArchiveComponents;
import org.jlibsedml.IModelContent;

public class ArchiveModelResolver implements IModelResolver {
  private ArchiveComponents ac;
    public ArchiveModelResolver(ArchiveComponents ac) {
        this.ac=ac;
    }
    public String getModelXMLFor(URI modelURI) {
        String rc = null;
        // Considering the import for nested SED-ML document
        String regExp = null;
        List<IModelContent> children = ac.getModelFiles();
        for (IModelContent imc: children) {
            if (OperatingSystemInfo.getInstance().isWindows()) {
                regExp = "\\\\";
            } else {
                regExp = "/";
            }
            String[] splitURI = modelURI.toString().split(regExp);
            String filename = splitURI[splitURI.length-1];
            if(imc.getName().equals(filename)){
                rc= imc.getContents();
            }
        }
        return rc;
    }

}
