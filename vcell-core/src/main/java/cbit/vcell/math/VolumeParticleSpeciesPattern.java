package cbit.vcell.math;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Matchable;

@SuppressWarnings("serial")
public class VolumeParticleSpeciesPattern extends ParticleSpeciesPattern {

	public VolumeParticleSpeciesPattern(String name, Domain domain, String locationName) {
		super(name, domain, locationName);
	}

	public VolumeParticleSpeciesPattern(VolumeParticleSpeciesPattern volumeParticleSpeciesPattern, String newName) {
		super(volumeParticleSpeciesPattern, newName);
	}

	@Override
	public String getVCML() {
		return getVCML(getQualifiedName());
	}

	public String getVCML(String name) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(VCML.VolumeParticleSpeciesPattern+"    "+name + " {\n");
		buffer.append("    "+VCML.SpeciesPatternLocation+"    "+getLocationName()+"\n");
		for (ParticleMolecularTypePattern pattern : getParticleMolecularTypePatterns()) {
			buffer.append(pattern.getVCML()+"\n");
		}
		buffer.append("}\n");
		return buffer.toString();
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

	public void read(MathDescription mathDescription, CommentStringTokenizer tokens) throws MathFormatException {
		String token = null;
		token = tokens.nextToken();
		if (!token.equalsIgnoreCase(VCML.BeginBlock)){
			throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
		}			
		while (tokens.hasMoreTokens()){
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)){
				break;
			}			
			if (token.equalsIgnoreCase(VCML.SpeciesPatternLocation)){
				token = tokens.nextToken();
				setLocationName(token);
				continue;
			}	
			if (token.equalsIgnoreCase(VCML.ParticleMolecularTypePattern)){
				token = tokens.nextToken();
				String molecularTypeName = token;
				ParticleMolecularType particleMolecularType = mathDescription.getParticleMolecularType(molecularTypeName);
				if (particleMolecularType!=null){
					ParticleMolecularTypePattern pattern = new ParticleMolecularTypePattern(particleMolecularType);
					pattern.read(tokens);
					addMolecularTypePattern(pattern);
				}else{
					throw new MathFormatException("failed to find "+VCML.ParticleMolecularType+" named "+molecularTypeName);
				}
				continue;
			}	
			throw new MathFormatException("unexpected identifier "+token);
		}	
			
	}
}
