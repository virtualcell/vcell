package cbit.vcell.server;

public class BioModelLink extends SimulationDocumentLink {
	public final static String bmid = "bioModelKey";
	public final static String bmbranch = "bioModelBranchId";
	public final static String bmname = "bioModelName";
	public final static String scid = "simContextKey";
	public final static String scbranch = "simContextBranchId";
	public final static String scname = "simContextName";
	
	public String bioModelKey;
	public String bioModelBranchId;
	public final String bioModelName;
	public String simContextKey;
	public String simContextBranchId;
	public final String simContextName;
	
	public BioModelLink(String bioModelKey, String bioModelBranchId, String bioModelName, String simContextKey, String simContextBranchId, String simContextName) {
		this.bioModelKey = bioModelKey;
		this.bioModelBranchId = bioModelBranchId;
		this.bioModelName = bioModelName;
		this.simContextKey = simContextKey;
		this.simContextBranchId = simContextBranchId;
		this.simContextName = simContextName;
	}

	public String getBioModelKey() {
		return bioModelKey;
	}

	public String getBioModelBranchId() {
		return bioModelBranchId;
	}

	public String getBioModelName() {
		return bioModelName;
	}

	public String getSimContextKey() {
		return simContextKey;
	}

	public String getSimContextBranchId() {
		return simContextBranchId;
	}

	public String getSimContextName() {
		return simContextName;
	}
	
	public void clearZeroPadding(){
		while (bioModelKey.startsWith("0") && bioModelKey.length()>1){
			bioModelKey = bioModelKey.substring(1);
		}
		while (bioModelBranchId.startsWith("0") && bioModelBranchId.length()>1){
			bioModelBranchId = bioModelBranchId.substring(1);
		}
		while (simContextKey.startsWith("0") && simContextKey.length()>1){
			simContextKey = simContextKey.substring(1);
		}
		while (simContextBranchId.startsWith("0") && simContextBranchId.length()>1){
			simContextBranchId = simContextBranchId.substring(1);
		}
	}
	
}