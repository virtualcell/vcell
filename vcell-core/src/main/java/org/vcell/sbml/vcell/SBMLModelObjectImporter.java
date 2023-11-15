package org.vcell.sbml.vcell;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;

public class SBMLModelObjectImporter extends AbstractSBMLImporter{
    public SBMLModelObjectImporter(Model sbmlModel, VCLogger argVCLogger, boolean shouldValidateSBML) throws VCLoggerException {
        super(argVCLogger, shouldValidateSBML);
        this.sbmlModel = sbmlModel;
        SBMLDocument document = sbmlModel.getSBMLDocument();

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
