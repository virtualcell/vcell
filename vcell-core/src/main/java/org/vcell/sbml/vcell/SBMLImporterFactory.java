package org.vcell.sbml.vcell;

import cbit.util.xml.VCLogger;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;

public class SBMLImporterFactory {

    //TODO: Change this to `@Nonnull` annotation on `sbmlModel`
    public static SBMLImporter getSBMLImporter(Model sbmlModel, VCLogger argVCLogger, boolean shouldValidateSBML){
        if (sbmlModel == null) throw new NullPointerException("Model must not be null");
        return new SBMLImporter(sbmlModel, argVCLogger, shouldValidateSBML);
    }

    public static SBMLImporter getSBMLImporter(String argSbmlFileName, VCLogger argVCLogger, boolean shouldValidateSBML){
        if (argSbmlFileName == null) throw new NullPointerException("Expected non-null SBML model");
    }

    public static SBMLImporter getSBMLImporter(InputStream sbmlInputStream, VCLogger argVCLogger, boolean shouldValidateSBML){

    }

    public static SBMLImporter getSBMLImporter(File sbmlModelFile, VCLogger argVCLogger, boolean shouldValidateSBML){

    }

    /*
     *if(sbmlFileName == null && sbmlModel == null && sbmlInputStream == null){
            throw new IllegalStateException("Expected non-null SBML model");
        }

        final SBMLDocument document;
        if(sbmlFileName != null){
            document = readSbmlDocument(new File(sbmlFileName));
            sbmlModel = document.getModel();
        } else if(sbmlInputStream != null){
            document = readSbmlDocument(this.sbmlInputStream);
            sbmlModel = document.getModel();
        } else { // sbmlModel != null
            document = sbmlModel.getSBMLDocument();
        }

        //
        // validate SBML model before import
        //
        if(this.bValidateSBML){
            validateSBMLDocument(document, vcLogger);
        }
        validateSBMLPackages(document, localIssueList, issueContext);
     */
}
