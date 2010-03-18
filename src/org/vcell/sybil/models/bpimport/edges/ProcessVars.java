package org.vcell.sybil.models.bpimport.edges;

/*   ProcessLink  --- by Oliver Ruebenacker, UCHC --- June 2008 to March 2009
 *   The link between a process and the stuff
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.sparql.core.Var;

public class ProcessVars {

	public static final Var PROCESS = Var.alloc("process");
	public static final Var PARTICIPANT = Var.alloc("participant");
	public static final Var SPECIES = Var.alloc("species");
	public static final Var ENTITY = Var.alloc("entity");
	public static final Var ENTITY_TYPE = Var.alloc("entityType");
	public static final Var SUBSTANCE = Var.alloc("substance");
	public static final Var LOCATION = Var.alloc("location");
	public static final Var STOICHIOMETRY = Var.alloc("stoichiometry");
	public static final Var STOICHCOEFF = Var.alloc("stoichcoeff");
	
	public static Set<Var> ALL = new HashSet<Var>();
	public static Map<String, Var> DIR = new HashMap<String, Var>();
	
	protected static void add(Var var) { 
		ALL.add(var);
		DIR.put(var.getName(), var); }
	
	static {
		add(PROCESS);
		add(PARTICIPANT);
		add(SPECIES);
		add(ENTITY);
		add(ENTITY_TYPE);
		add(SUBSTANCE);
		add(LOCATION);
		add(STOICHIOMETRY);
		add(STOICHCOEFF);
	}
	
}
