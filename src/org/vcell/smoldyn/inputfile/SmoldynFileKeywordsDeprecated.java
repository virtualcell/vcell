package org.vcell.smoldyn.inputfile;




/**
 * Smoldyn keywords that seem to be unnecessary.  In some cases, they may be replaced by other statements, while in other cases,
 * their effects are completely unnecessary, undesirable, and easily discarded.  The arrangement into categories is the same as for
 * {@link SmoldynFileKeywords}.
 * 
 * @author mfenwick
 *
 */
public class SmoldynFileKeywordsDeprecated {

	public static enum ConfigStatement {
		define,
		define_global,
		undefine,
		ifdefine,
		ifundefine,
		ELSE,//else is reserved by Java
		endif,
	};
	
	public static enum Time {
		time_now,
	};
		
	public static enum Molecule {
		max_species,
		max_names,
		name,
		names,
	};	
	
	/* Reactions
	 * 
	 * --> reaction_rate <reaction name> <rate>
	 * 		## DON'T USE THIS   --  declare rate as part of reaction declaration instead
	 * --> reaction_probability <reaction name> <probability>
	 * 			//corresponds to unimolecular reaction rate
	 * --> reaction_production <reaction name> <rate>
	 * 			produce molecules in a 0th order reaction
	 * 		## DON'T USE THIS  --- declare rate with reaction declaration
	 * --> reaction_permit <reaction name> <state1> [<state2>]
	 * 		why is this here?  what purpose does it serve?
	 * --> reaction_forbid <reaction name> <state1> [<state2>]
	 * 		forbid reaction <reaction name> from occurring in certain states
	 * 		## is there any need for this?
	 */
	public static enum Reaction {
		reaction_rate,
		reaction_probability,
		reaction_production,
		reaction_permit,
		reaction_forbid,
		//for backwards compatibility:
		start_reaction,
		order,
		max_rxn,
		reactant,
		permit,
		rate,
		confspread_radius,
		rate_internal,
		probability,
		product,
		product_param,
		end_reaction,
	};

	public static enum Runtime {
		max_cmd,
	};
	
	public static enum Surface {
		new_surface,
		surface,
	};	
	
	public static enum Compartment {
		new_compartment,
		compartment,
	};
	
	public static enum Port {
		new_port,
		port,
	};
	
	public static enum SimulationControl {
		setgraphics,
		setgraphic_iter,
	}
}
