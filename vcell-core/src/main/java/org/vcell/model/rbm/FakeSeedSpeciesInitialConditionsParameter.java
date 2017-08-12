package org.vcell.model.rbm;


public class FakeSeedSpeciesInitialConditionsParameter {
	
	private static final String uniqueIdRoot = "unique_id_used_as_fake_parameter_";
	
	public final String speciesContextName;
	public final String fakeParameterName;
	
	public FakeSeedSpeciesInitialConditionsParameter(String speciesContextName) {
		this.speciesContextName = speciesContextName;
		this.fakeParameterName = uniqueIdRoot+speciesContextName;
	}
	
	@Override
	public boolean equals(Object obj){
		if (!(obj instanceof FakeSeedSpeciesInitialConditionsParameter)){
			return false;
		}
		FakeSeedSpeciesInitialConditionsParameter other = (FakeSeedSpeciesInitialConditionsParameter)obj;
		return fakeParameterName.equals(other.fakeParameterName);
	}
	
	@Override
	public int hashCode(){
		return fakeParameterName.hashCode();
	}
	
	@Override
	public String toString(){
		return fakeParameterName;
	}

	public static FakeSeedSpeciesInitialConditionsParameter fromString(String key) {
		if (key.startsWith(uniqueIdRoot)){
			String scName = key.replace(uniqueIdRoot, "");
			return new FakeSeedSpeciesInitialConditionsParameter(scName);
		}
		return null;
	}
	
}
