package org.vcell.sbml.vcell;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import org.sbml.jsbml.SBMLDocument;

import java.io.File;

public class SBMLFileImporter extends AbstractSBMLImporter {
    public SBMLFileImporter(File sbmlFile, VCLogger argVCLogger, boolean shouldValidateSBML) throws VCLoggerException {
        super(argVCLogger, shouldValidateSBML);
        SBMLDocument document = this.readSbmlDocument(sbmlFile);
        this.sbmlModel = document.getModel();
        //
        // validate SBML model before import
        //
        if(this.shouldValidateSBML){
            validateSBMLDocument(document, this.vcLogger);
        }
        validateSBMLPackages(document, this.localIssueList, this.issueContext);
        this.sbmlModel = document.getModel();
    }
}
