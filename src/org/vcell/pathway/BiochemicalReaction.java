/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import java.util.ArrayList;

public interface BiochemicalReaction extends Conversion {

	public ArrayList<DeltaG> getDeltaG();

	public ArrayList<Double> getDeltaH();

	public ArrayList<Double> getDeltaS();

	public ArrayList<String> getECNumber();

	public ArrayList<KPrime> getkEQ();
	
	public void setDeltaG(ArrayList<DeltaG> deltaG);
	
	public void setDeltaH(ArrayList<Double> deltaH);
	
	public void setDeltaS(ArrayList<Double> deltaS);
	
	public void setECNumber(ArrayList<String> eCNumber);
	
	public void setkEQ(ArrayList<KPrime> kEQ);

}
