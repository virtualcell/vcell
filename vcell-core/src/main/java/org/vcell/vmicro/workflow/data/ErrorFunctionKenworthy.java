package org.vcell.vmicro.workflow.data;

import cbit.vcell.math.RowColumnResultSet;

public class ErrorFunctionKenworthy extends ErrorFunctionInverseTimeL1 {

	public ErrorFunctionKenworthy(RowColumnResultSet reducedData) {
		super(getTimeOffset(reducedData));
	}

	private static double getTimeOffset(RowColumnResultSet reducedData) {
		//
		// computes integral of bleach data over time for normalizing the fitting residual
		//
		if (reducedData.getDataColumnCount()!=2){
			throw new RuntimeException("expected reduced data to have two columns, time and fluorescence, found "+reducedData.getDataColumnCount());
		}
		double timeOffset = 0;
		double lastTime = reducedData.getRow(0)[0];
		for (int r=0; r<reducedData.getRowCount(); r++){
			double[] row = reducedData.getRow(r);
			// 0th index is time
			double deltaT = row[0]-lastTime;
			lastTime = row[0];
			timeOffset += row[1]*deltaT;
		}
		return timeOffset;
	}

}
