package org.vcell.ncbc.physics.component;

import cbit.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/6/2004 1:49:46 PM)
 * @author: Jim Schaff
 */
public class MembraneReaction extends Device {
	private String rateExpression = null;
	private String volSpecies[] = null;
	private int volStoichs[] = null;	
	private String memSpecies[] = null;
	private int memStoichs[] = null;	

/**
 * VolumeReaction constructor comment.
 */
public MembraneReaction(String argName, Location argLocation, String argRateExpression, String argMemSpecies[], int argMemStoichs[], String argVolSpecies[], int argVolStoichs[]) {
	super(argName, argLocation);
	this.rateExpression = argRateExpression;
	this.volSpecies = argVolSpecies;
	this.volStoichs = argVolStoichs;
	this.memSpecies = argMemSpecies;
	this.memStoichs = argMemStoichs;

	if (volStoichs.length != volSpecies.length){
		throw new IllegalArgumentException("volume stoichiometry list length != volume species list length");
	}
	if (memStoichs.length != memSpecies.length){
		throw new IllegalArgumentException("membrane stoichiometry list length != membrane species list length");
	}

	addIdentifier(new Variable("reactRate",VCUnitDefinition.UNIT_molecules_per_um2_per_s));
	addEquation(new Equation("reactRate == "+argRateExpression));
	//
	// membrane reaction rates
	//
	for (int i = 0; i < memSpecies.length; i++){
		if (memStoichs[i] == 0){
			addConnector(new SpeciesSurfaceDensityConnector(this,memSpecies[i],Port.ROLE_USES,Port.ROLE_NONE));
		}else{
			SpeciesSurfaceDensityConnector surfaceDensityConnector = new SpeciesSurfaceDensityConnector(this,memSpecies[i],Port.ROLE_USES,Port.ROLE_DEFINES);
			addConnector(surfaceDensityConnector);
			SpeciesSurfaceDensityRatePort surfaceDensityRatePort = surfaceDensityConnector.getSpeciesSurfaceDensityRatePort();
			addEquation(new Equation(memSpecies[i]+" == "+surfaceDensityConnector.getName()+"."+surfaceDensityRatePort.getVariable()+" * "+memStoichs[i]));
		}			
	}

	//
	// volume reaction rates
	//
	for (int i = 0; i < volSpecies.length; i++){
		if (volStoichs[i] == 0){
			addConnector(new ConcentrationConnector(this,volSpecies[i],Port.ROLE_USES,Port.ROLE_NONE));
		}else{
			ConcentrationConnector concentrationConnector = new ConcentrationConnector(this,volSpecies[i],Port.ROLE_USES,Port.ROLE_DEFINES);
			addConnector(concentrationConnector);
			ConcentrationRatePort concentrationRatePort = concentrationConnector.getConcentrationRatePort();
			addEquation(new Equation(volSpecies[i]+" == "+concentrationConnector.getName()+"."+concentrationRatePort.getVariable()+" * "+volStoichs[i]));
		}			
	}

}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 3:53:56 PM)
 * @return java.lang.String
 */
public java.lang.String getRateExpression() {
	return rateExpression;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 3:53:56 PM)
 * @param newRateExpression java.lang.String
 */
public void setRateExpression(java.lang.String newRateExpression) {
	rateExpression = newRateExpression;
}
}