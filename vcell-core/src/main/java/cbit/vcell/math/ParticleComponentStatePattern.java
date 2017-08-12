package cbit.vcell.math;

import java.io.Serializable;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;


public class ParticleComponentStatePattern implements Serializable, Matchable {
	public static final String ANY_STATE = "*";

	private final ParticleComponentStateDefinition particleComponentStateDefinition;
	private final boolean bAny;
		
	public ParticleComponentStatePattern() {
		this.bAny = true;
		this.particleComponentStateDefinition = null;
	}
	public ParticleComponentStatePattern(ParticleComponentStateDefinition particleComponentStateDefinition) {
		this.bAny = false;
		this.particleComponentStateDefinition = particleComponentStateDefinition;
	}
		
	public ParticleComponentStatePattern(ParticleComponentStatePattern pattern) {
		this.particleComponentStateDefinition = pattern.particleComponentStateDefinition;
		this.bAny = pattern.bAny;
	}
	public boolean isAny(){
		return bAny;
	}
	
	public ParticleComponentStateDefinition getParticleComponentStateDefinition() {
		return particleComponentStateDefinition;
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof ParticleComponentStatePattern){
			ParticleComponentStatePattern other = (ParticleComponentStatePattern)obj;
			if (!Compare.isEqual(particleComponentStateDefinition, other.particleComponentStateDefinition)){
				return false;
			}
			if (bAny != other.bAny) {
				return false;
			}
			return true;
		}
		return false;
	}
}
