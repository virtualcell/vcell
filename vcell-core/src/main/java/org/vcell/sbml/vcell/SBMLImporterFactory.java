package org.vcell.sbml.vcell;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import org.sbml.jsbml.Model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SBMLImporterFactory {

    //TODO: Change this to `@Nonnull` annotation on `sbmlModel`
    public static SBMLImporter getSBMLImporter(Model sbmlModel, VCLogger argVCLogger, boolean shouldValidateSBML) throws VCLoggerException{
        if (sbmlModel == null) throw new NullPointerException("Model must not be null");
        return new SBMLModelObjectImporter(sbmlModel, argVCLogger, shouldValidateSBML);
    }

    public static SBMLImporter getSBMLImporter(String argSbmlFileName, VCLogger argVCLogger, boolean shouldValidateSBML) throws IOException, VCLoggerException{
        if (argSbmlFileName == null) throw new NullPointerException("Expected non-null SBML model");
        File sbmlFile = new File(argSbmlFileName);
        return SBMLImporterFactory.getSBMLImporter(sbmlFile, argVCLogger, shouldValidateSBML);
    }

    public static SBMLImporter getSBMLImporter(InputStream sbmlInputStream, VCLogger argVCLogger, boolean shouldValidateSBML) throws VCLoggerException{
        if (sbmlInputStream == null) throw new NullPointerException("Expected non-null SBML model");
        return new SBMLStreamImporter(sbmlInputStream, argVCLogger, shouldValidateSBML);
    }

    public static SBMLImporter getSBMLImporter(File sbmlModelFile, VCLogger argVCLogger, boolean shouldValidateSBML) throws IOException, VCLoggerException{
        if (sbmlModelFile == null) throw new NullPointerException("Expected non-null SBML model");
        if (!sbmlModelFile.exists() || sbmlModelFile.isDirectory()) throw new IOException("File is not an appropriate type");
        return new SBMLFileImporter(sbmlModelFile, argVCLogger, shouldValidateSBML);
    }
}
