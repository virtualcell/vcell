package cbit.vcell.simdata;

import java.io.Serializable;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCDataIdentifier;

public class DataSetMetadata implements Serializable {
	public final VCDataIdentifier vcDataIdentifier;
	public final String[] varNames;

	public DataSetMetadata(VCDataIdentifier vcdataID, DataSetTimeSeries dataSetTimeSeries) throws DataAccessException {
		this.vcDataIdentifier = vcdataID;
		varNames = dataSetTimeSeries.getVarNames();
	}
	

	public String[] getVarNames() {
		return varNames;
	}
	
	public KeyValue getSimKey(){
		return new KeyValue(vcDataIdentifier.getID().split("_")[1]);
	}
	
}
