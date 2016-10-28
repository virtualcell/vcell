package cbit.vcell.simdata;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;

public class VtkManager {
	
	private final OutputContext outputContext;
	private final VCDataManager vcDataManager;
	private final VCDataIdentifier vcDataIdentifier;

	public VtkManager(OutputContext outputContext, VCDataManager vcDataManager, VCDataIdentifier vcDataID) {
		this.outputContext = outputContext;
		this.vcDataManager = vcDataManager;
		this.vcDataIdentifier = vcDataID;
	}

	public VtuVarInfo[] getVtuVarInfos() throws DataAccessException {
		return vcDataManager.getVtuVarInfos(outputContext, vcDataIdentifier);
	}

	public double[] getVtuMeshData(VtuVarInfo vtuVarInfo, double time) throws DataAccessException {
		return vcDataManager.getVtuMeshData(outputContext, vcDataIdentifier, vtuVarInfo, time);
	}

	public VtuFileContainer getEmptyVtuMeshFiles(int timeIndex) throws DataAccessException {
		return vcDataManager.getEmptyVtuMeshFiles(vcDataIdentifier, timeIndex);
	}

	public double[] getDataSetTimes() throws DataAccessException {
		return vcDataManager.getDataSetTimes(vcDataIdentifier);
	}

}
