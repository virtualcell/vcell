package cbit.vcell.math;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.math.ParticleMolecularComponentPattern.ParticleBondType;

@SuppressWarnings("serial")
public abstract class ParticleSpeciesPattern extends ParticleVariable {

	private String locationName;
	private List<ParticleMolecularTypePattern> molecularTypePatterns = new ArrayList<ParticleMolecularTypePattern>();
	
	public ParticleSpeciesPattern(String name, Domain domain, String locationName) {
		super(name, domain);
		this.locationName = locationName;
	}
	
	public ParticleSpeciesPattern(ParticleSpeciesPattern particleSpeciesPattern, String newName) {
		super(newName, particleSpeciesPattern.getDomain());
		this.locationName = particleSpeciesPattern.getLocationName();
		for (ParticleMolecularTypePattern pattern : particleSpeciesPattern.molecularTypePatterns){
			molecularTypePatterns.add(new ParticleMolecularTypePattern(pattern));
		}
	}

	public String getLocationName(){
		return this.locationName;
	}
	
	void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	
	public void addMolecularTypePattern(ParticleMolecularTypePattern molecularTypePattern) {
		List<ParticleMolecularTypePattern> newValue = new ArrayList<ParticleMolecularTypePattern>(molecularTypePatterns);
		newValue.add(molecularTypePattern);
		setParticleMolecularTypePatterns(newValue);
	}
	
	public void removeMolecularTypePattern(ParticleMolecularTypePattern molecularTypePattern) {
		List<ParticleMolecularTypePattern> newValue = new ArrayList<ParticleMolecularTypePattern>(molecularTypePatterns);
		newValue.remove(molecularTypePattern);
		setParticleMolecularTypePatterns(newValue);
	}

	public final List<ParticleMolecularTypePattern> getParticleMolecularTypePatterns() {
		return molecularTypePatterns;
	}

	public final void setParticleMolecularTypePatterns(List<ParticleMolecularTypePattern> newValue) {
		this.molecularTypePatterns = newValue;		
	}
	
	public int nextBondId() {		
		int N = 128;
		while (true) {
			BitSet bs = new BitSet(N);
			for (ParticleMolecularTypePattern mtp : molecularTypePatterns) {
				for (ParticleMolecularComponentPattern mcp : mtp.getMolecularComponentPatternList()) {
					if (mcp.getBondType() == ParticleBondType.Specified) {
						bs.set(mcp.getBondId());
					}
				}
			}
			for (int i = 1; i < N; ++ i) {
				if (!bs.get(i)) {
					return i;
				}
			}
			N *= 2;
		}
	}
		
	@Override
	public boolean compareEqual(Matchable obj, boolean bIgnoreMissingDomain) {
		if (obj instanceof ParticleSpeciesPattern){
			ParticleSpeciesPattern other = (ParticleSpeciesPattern)obj;
			if (!compareEqual0(other, bIgnoreMissingDomain)){
				return false;
			}
			if (!Compare.isEqual(molecularTypePatterns, other.molecularTypePatterns)){
				return false;
			}
			if (!Compare.isEqual(locationName, other.locationName)){
				return false;
			}
			return true;
		}
		return false;
	}

	public boolean isConcrete() {
		for (ParticleMolecularTypePattern molecularTypePattern : molecularTypePatterns) {
			for (ParticleMolecularComponentPattern particleMolecularComponentPattern : molecularTypePattern.getMolecularComponentPatternList()){
				if (particleMolecularComponentPattern.getComponentStatePattern().isAny()){
					return false;
				}
				if (particleMolecularComponentPattern.getBondType() != ParticleBondType.Specified){
					return false;
				}
			}
		}
		return true;
	}
	
}
