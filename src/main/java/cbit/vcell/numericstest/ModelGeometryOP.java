package cbit.vcell.numericstest;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VCDocumentInfo;

public class ModelGeometryOP extends TestSuiteOP {

	private VCDocumentInfo  vcDocumentInfo;
	private String applicationName;
	public ModelGeometryOP(BioModelInfo bioModelInfo,String applicationName) {
		super(null);
		this.vcDocumentInfo = bioModelInfo;
		this.applicationName = applicationName;
	}
	public ModelGeometryOP(MathModelInfo mathModelInfo) {
		super(null);
		this.vcDocumentInfo = mathModelInfo;
	}
	public VCDocumentInfo getVCDocumentInfo(){
		return vcDocumentInfo;
	}
	public String getBioModelApplicationName(){
		if(!(vcDocumentInfo instanceof BioModelInfo)){
			throw new IllegalArgumentException("Call this method only ofr BioModelInfo");
		}
		return applicationName;
	}
}
