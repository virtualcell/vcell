package cbit.vcell.mapping;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;

public class ResolvedFlux {
	private SpeciesContext speciesContext=null;
	private Expression flux = new Expression(0.0);
	/**
	 * ResolvedFlux constructor comment.
	 */
	ResolvedFlux(SpeciesContext arg_speciesContext) {
		this.speciesContext = arg_speciesContext;
	}
	/**
	 * This method was created in VisualAge.
	 * @return cbit.vcell.model.Species
	 */
	public SpeciesContext getSpeciesContext() {
		return speciesContext;
	}
	public Expression getFlux() {
		return flux;
	}
	public void setFlux(Expression flux) {
		this.flux = flux;
	}
	}
