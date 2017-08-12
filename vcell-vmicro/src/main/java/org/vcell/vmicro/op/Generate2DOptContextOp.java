package org.vcell.vmicro.op;

import org.vcell.vmicro.workflow.data.ErrorFunction;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.vmicro.workflow.data.OptModel;

import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.parser.ExpressionException;

public class Generate2DOptContextOp {
	
	public OptContext generate2DOptContext(OptModel optModel, RowColumnResultSet normExpDataset, RowColumnResultSet measurementErrorDataset, ErrorFunction errorFunction) throws ExpressionException {
		
		double[] normExpTimePoints = normExpDataset.extractColumn(0);

		int numRois = normExpDataset.getDataColumnCount()-1;
		int numNormExpTimes = normExpDataset.getRowCount();
		double[][] normExpData = new double[numRois][numNormExpTimes];
		for (int roi=0; roi<numRois; roi++){
			double[] roiData = normExpDataset.extractColumn(roi+1);
			for (int t=0; t<numNormExpTimes; t++){
				normExpData[roi][t] = roiData[t];
			}
		}
		
		double[][] measurementErrors = new double[numRois][numNormExpTimes];
		for (int roi=0; roi<numRois; roi++){
			double[] roiData = measurementErrorDataset.extractColumn(roi+1);
			for (int t=0; t<numNormExpTimes; t++){
				measurementErrors[roi][t] = roiData[t];
			}
		}
		
		return new OptContext(optModel,normExpData,normExpTimePoints,measurementErrors,errorFunction);
	}
	
}
