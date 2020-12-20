package org.jlibsedml.validation;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jlibsedml.SEDMLTags;
import org.jlibsedml.SedMLError;
import org.jlibsedml.Simulation;
import org.jlibsedml.modelsupport.KisaoOntology;

/**
 * Validates that the supplied KISAO id is a valid KISAO identifier.
 * @author radams
 *
 */
public class KisaoIDValidator  extends AbstractDocumentValidator implements ISedMLValidator{
 
	 private List<Simulation> sims;
	 KisaoIDValidator(List<Simulation> simulations, Document doc) {
		 super(doc);
		this.sims=simulations;
	}

	/**
	 * Validates the syntax of the KiSAO identifier for uniform time course descriptions.
	 * @see org.jlibsedml.validation.ISedMLValidator#validate()
	 */
	public List<SedMLError> validate()  {
		List<SedMLError> errs = new ArrayList<SedMLError>();
		for (Simulation sim: sims){
			String kisaoID = sim.getAlgorithm().getKisaoID();
			if(KisaoOntology.getInstance().getTermById(kisaoID) == null){
				 int line = getLineNumberOfError(SEDMLTags.SIMUL_UTC_KIND,sim);
				errs.add(new SedMLError(line, " The supplied KisaoID [" + kisaoID +"] for simulation [" + sim.getId() +"]  is not a recognized KISAO identifier.\n" +
						     " Identifiers should be the format 'KISAO:0000001' ", SedMLError.ERROR_SEVERITY.WARNING));
			}
		}
		
		return errs;
		
	}

	

}
