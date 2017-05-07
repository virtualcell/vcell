package cbit.vcell.server;

public class MathModelLink extends SimulationDocumentLink {
	public final static String mmid = "mathModelKey";
	public final static String mmbranch = "mathModelBranchId";
	public final static String mmname = "mathModelName";
	
	public String mathModelKey;
	public String mathModelBranchId;
	public final String mathModelName;
	
	public MathModelLink(String mathModelKey, String mathModelBranchId, String mathModelName) {
		this.mathModelKey = mathModelKey;
		this.mathModelBranchId = mathModelBranchId;
		this.mathModelName = mathModelName;
	}

	public String getMathModelKey() {
		return mathModelKey;
	}

	public String getMathModelBranchId() {
		return mathModelBranchId;
	}

	public String getMathModelName() {
		return mathModelName;
	}
	
	public void clearZeroPadding(){
		while (mathModelKey.startsWith("0") && mathModelKey.length()>1){
			mathModelKey = mathModelKey.substring(1);
		}
		while (mathModelBranchId.startsWith("0") && mathModelBranchId.length()>1){
			mathModelBranchId = mathModelBranchId.substring(1);
		}
	}
	
}