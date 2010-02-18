package org.vcell.sybil.models;

/*   SchemaChooser  --- by Oliver Ruebenacker, UCHC --- February to June 2009
 *   Finds schemas for a given model
 */

import java.util.Set;

import org.vcell.sybil.rdf.schemas.BioPAX2;
import org.vcell.sybil.rdf.schemas.SBPAX;
import org.vcell.sybil.util.sets.SetOfOne;
import org.vcell.sybil.util.sets.SetOfTwo;

import com.hp.hpl.jena.rdf.model.Model;

public class SchemaFinder {

	public static final Set<Model> biopaxOnly = new SetOfOne<Model>(BioPAX2.schema);
	public static final Set<Model> biopaxAndSbpax = new SetOfTwo<Model>(BioPAX2.schema, SBPAX.schema);
	
	public static final Model biopaxAndSbpaxUnion = BioPAX2.schema.union(SBPAX.schema);
	
	public static Set<Model> schemasBare(Model model) { return biopaxOnly; }
	public static Set<Model> schemasExtended(Model model) { return biopaxAndSbpax; }
	
	public static Model schemaUnionBare(Model model) { return BioPAX2.schema; }
	public static Model schemaUnionExtended(Model model) { return biopaxAndSbpaxUnion; }

	
}
