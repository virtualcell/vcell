package cbit.vcell.numericstest;

import org.vcell.util.document.KeyValue;

public class ModelGeometryOPResults extends TestSuiteOPResults {

	KeyValue geometryKey;
	public ModelGeometryOPResults(KeyValue geometryKey) {
		super(null);
		this.geometryKey = geometryKey;
	}
	public KeyValue getGeometryKey(){
		return geometryKey;
	}
}
