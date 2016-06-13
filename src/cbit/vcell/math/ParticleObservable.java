package cbit.vcell.math;

import java.util.ArrayList;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

@SuppressWarnings("serial")
public class ParticleObservable extends Variable {

    public enum ObservableType {
    	Molecules("Molecules"), Species("Species");
        private String text;
        ObservableType(String text) {
            this.text = text;
        }
        public String getText() {
            return this.text;
        }
        public static ObservableType fromString(String text) {
            if (text != null) {
                for (ObservableType b : ObservableType.values()) {
                    if (text.equalsIgnoreCase(b.text)) {
                        return b;
                    }
                }
            }
        return null;
        }
    }
	
	private String name;
	private ArrayList<ParticleSpeciesPattern> particleSpeciesPatternList = new ArrayList<ParticleSpeciesPattern>(); 
	private ObservableType type;
	
	public ParticleObservable(String name, Domain domain, ObservableType t) {
		super(name,domain);
		this.name = name;
		this.type = t;
	}
	
	public final String getName() {
		return name;
	}
	
	public void setName(String newValue) {
		name = newValue;
	}
	
	public final ObservableType getType() {
		return type;
	}
	
	protected void setType(ObservableType newValue) {
		type = newValue;
	}
	
	public final ArrayList<ParticleSpeciesPattern> getParticleSpeciesPatterns() {
		return particleSpeciesPatternList;
	}
	
	public void setParticleSpeciesPattern(ArrayList<ParticleSpeciesPattern> newValue) {
		particleSpeciesPatternList = newValue;
	}
	public void addParticleSpeciesPattern(ParticleSpeciesPattern particleSpeciesPattern) {
		if (!particleSpeciesPatternList.contains(particleSpeciesPattern)){
			particleSpeciesPatternList.add(particleSpeciesPattern);
		}
	}

	@Override
	public boolean compareEqual0(Matchable object, boolean bIgnoreMissingDomains) {
		if (object instanceof ParticleObservable){
			ParticleObservable other = (ParticleObservable)object;
			if (!Compare.isEqual(name, other.name)){
				return false;
			}
			if (!Compare.isEqual(particleSpeciesPatternList,other.particleSpeciesPatternList)){
				return false;
			}
			if (type != other.type){
				return false;
			}
			return true;
		}
		return false;
	}
	@Override
	public boolean compareEqual(Matchable object, boolean bIgnoreMissingDomains) {
		if (object instanceof VolumeParticleSpeciesPattern){
			if (!compareEqual0(object, bIgnoreMissingDomains)){
				return false;
			}
			return true;
		}
		return false;
	}
	@Override
	public String getVCML() throws MathException {
		return getVCML(getQualifiedName());
	}
	public String getVCML(String name) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(VCML.VolumeParticleObservable + "    " + name + " {\n");
		buffer.append("    " + VCML.ParticleMolecularType + "    " + type.name() + "\n");
		buffer.append("    " + VCML.VolumeParticleSpeciesPatterns + " {\n    ");
		for (ParticleSpeciesPattern pattern : getParticleSpeciesPatterns()) {
			buffer.append("    " + pattern.getName());
		}
		buffer.append("\n    }\n");
		buffer.append("}\n");
		return buffer.toString();
	}
}
