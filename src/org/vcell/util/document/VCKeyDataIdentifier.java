package org.vcell.util.document;

import cbit.vcell.simdata.SimulationData;

/**
 * identifier which {@link SimulationData} supports
 */
public interface VCKeyDataIdentifier extends VCDataIdentifier {
	
	/**
	 * @return simulation key
	 */
	public KeyValue getSimulationKey( );
	
	/*
	 * @return jobIndex, if application, or 0
	 */
	public int getJobIndex( );

}
