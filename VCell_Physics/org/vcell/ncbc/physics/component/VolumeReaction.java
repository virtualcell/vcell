package org.vcell.ncbc.physics.component;

import org.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/6/2004 1:49:46 PM)
 * @author: Jim Schaff
 */
public class VolumeReaction extends Device {
	private String rateExpression = null;
	private String speciesList[] = null;
	private int stoichList[] = null;	

/**
 * VolumeReaction constructor comment.
 */
public VolumeReaction(String argName, Location argLocation, String argRateExpression, String argSpeciesList[], int argStoichList[]) {
	super(argName, argLocation);
	this.rateExpression = argRateExpression;
	this.speciesList = argSpeciesList;
	this.stoichList = argStoichList;

	if (stoichList.length != speciesList.length){
		throw new IllegalArgumentException("stoichList.length != speciesList.length");
	}

	addIdentifier(new Variable("reactRate",VCUnitDefinition.UNIT_uM_per_s));
	addEquation(new Equation("reactRate == "+argRateExpression));
	//
	// reaction rates
	//
	for (int i = 0; i < argSpeciesList.length; i++){
		if (argStoichList[i] == 0){
			addConnector(new ConcentrationConnector(this,speciesList[i],Port.ROLE_USES,Port.ROLE_NONE));
		}else{
			ConcentrationConnector concentrationConnector = new ConcentrationConnector(this,speciesList[i],Port.ROLE_USES,Port.ROLE_DEFINES);
			addConnector(concentrationConnector);
			ConcentrationRatePort concentrationRatePort = concentrationConnector.getConcentrationRatePort();
			addEquation(new Equation(speciesList[i]+" == "+concentrationConnector.getName()+"."+concentrationRatePort.getVariable()+" * "+stoichList[i]));
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