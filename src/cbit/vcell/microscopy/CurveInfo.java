package cbit.vcell.microscopy;

import cbit.vcell.microscopy.FRAPStudy.AnalysisParameters;

public class CurveInfo {
	AnalysisParameters analysisParameters;
	String roiName = null;

	public CurveInfo(AnalysisParameters analysisParameters,String roiName){
		this.analysisParameters = analysisParameters;
		this.roiName = roiName;
	}

	public boolean equals(Object obj){
		if (!(obj instanceof CurveInfo)){
			return false;
		}
		CurveInfo ci = (CurveInfo)obj;
		if(analysisParameters == null && ci.analysisParameters == null){
			return ci.roiName.equals(roiName);
		}
		if(ci.analysisParameters != null && analysisParameters != null){
			return
			ci.analysisParameters.equals(analysisParameters) &&
			ci.roiName.equals(roiName);		
		}
		return false;
	}
	
	public int hashCode(){
		return (analysisParameters != null?analysisParameters.hashCode():0)+roiName.hashCode();
	}
	public boolean isExperimentInfo(){
		return analysisParameters == null;
	}
	public AnalysisParameters getAnalysisParameters(){
		return analysisParameters;
	}
	public String getROIName(){
		return roiName;
	}
}
