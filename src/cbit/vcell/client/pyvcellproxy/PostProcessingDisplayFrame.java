package cbit.vcell.client.pyvcellproxy;

import cbit.vcell.simdata.DataOperation;
import cbit.vcell.simdata.NewClientPDEDataContext;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.DataOperationResults.DataProcessingOutputInfo;

public class PostProcessingDisplayFrame {

	
	
	
	private PDEDataContext pdeDataContext;
	private DataProcessingOutputInfo dataProcessingOutputInfo;
	
	
	
	
	private void read(NewClientPDEDataContext pdeDataContext0) throws Exception {
		this.pdeDataContext = pdeDataContext0;
		dataProcessingOutputInfo = null;
		try {
			dataProcessingOutputInfo = (DataProcessingOutputInfo)pdeDataContext.doDataOperation(new DataOperation.DataProcessingOutputInfoOP(pdeDataContext.getVCDataIdentifier(),true,((NewClientPDEDataContext)pdeDataContext0).getDataManager().getOutputContext()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Data Processing Output Error - '"+e.getMessage()+"'  (Note: Data Processing Output is generated automatically when running VCell 5.2 or later simulations)");
		}
	}	
	
}
