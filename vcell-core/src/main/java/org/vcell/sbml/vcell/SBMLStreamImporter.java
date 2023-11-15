package org.vcell.sbml.vcell;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import org.sbml.jsbml.SBMLDocument;

import java.io.InputStream;

public class SBMLStreamImporter extends AbstractSBMLImporter {
    public SBMLStreamImporter(InputStream inputStream, VCLogger argVCLogger, boolean shouldValidateSBML) throws VCLoggerException {
        super(argVCLogger, shouldValidateSBML);
        SBMLDocument document = this.readSbmlDocument(inputStream);
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
