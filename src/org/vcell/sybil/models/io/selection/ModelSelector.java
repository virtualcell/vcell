package org.vcell.sybil.models.io.selection;

/*   ModelSelector  --- by Oliver Ruebenacker, UCHC --- November 2009
 *   Extract statements of a model based on selected resources
 */

import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public interface ModelSelector {

	public Model createSelection(Model model, Set<Resource> selectedResources);	
}
