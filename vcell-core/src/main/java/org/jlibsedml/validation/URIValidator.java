package org.jlibsedml.validation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jlibsedml.Model;
import org.jlibsedml.SedMLError;
import org.jlibsedml.SedMLError.ERROR_SEVERITY;
/**
 * Validates that URIs in the 'source' attribute of a model can be parsed into URI objects.
 * This method generates a SED-ML error if the attempt at creation of a {@link URI} object fails.
 * @author radams
 *
 */
public class URIValidator implements ISedMLValidator {
 
	 private List<Model> models;
	 public final static String ErrMessageRoot="Could not convert model source to URI";
	 URIValidator(List<Model> simulations) {
		this.models=simulations;
	}

	/**
	 * @see ISedMLValidator
	 */
	public List<SedMLError> validate()  {
		List<SedMLError> errs = new ArrayList<SedMLError>();
		for (Model model: models){
			
				try {
					URI uri = new URI(model.getSource());
				} catch (URISyntaxException e) {
					errs.add(new SedMLError(0,"ErrMessageRoot[" + model.getSource() +"]", ERROR_SEVERITY.WARNING ));
				}
			
			
		}
		
		return errs;
		
	}

}
