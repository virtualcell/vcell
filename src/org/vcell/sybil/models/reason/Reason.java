package org.vcell.sybil.models.reason;

/*   Reason  --- by Oliver Ruebenacker, UCHC --- November 2007 to February 2009
 *   Creates inference models out of models and schemas
 */

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;

public class Reason {

	public static abstract class Choice {
		public abstract Model infModel(Model data, Model schema);
	}
	
	public static abstract class ReasonerChoice extends Choice {

		public abstract Reasoner reasoner();
		
		@Override
		public InfModel infModel(Model data, Model schema) {
			return ModelFactory.createInfModel(reasoner(), schema, data);
		}

	}
	
}
