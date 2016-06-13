package cbit.vcell.math;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

@SuppressWarnings("serial")
public class ParticleMolecularTypePattern implements Serializable, Matchable {
	private ParticleMolecularType molecularType;
	private String matchLabel = MolecularTypePattern.TRIVIAL_MATCH;
	private ArrayList<ParticleMolecularComponentPattern> componentPatternList = new ArrayList<ParticleMolecularComponentPattern>();

	public ParticleMolecularTypePattern(ParticleMolecularType molecularType) {
		this.molecularType = molecularType;
//		for (ParticleMolecularComponent mc : this.molecularType.getComponentList()) {
//			componentPatternList.add(new ParticleMolecularComponentPattern(mc));
//		}
	}
		
	public ParticleMolecularTypePattern(ParticleMolecularTypePattern particleMolecularTypePattern) {
		this.molecularType = particleMolecularTypePattern.molecularType;
		this.matchLabel = particleMolecularTypePattern.matchLabel;
		for (ParticleMolecularComponentPattern pattern : particleMolecularTypePattern.componentPatternList){
			componentPatternList.add(new ParticleMolecularComponentPattern(pattern));
		}
	}

	public ParticleMolecularComponentPattern getMolecularComponentPattern(ParticleMolecularComponent mc) {
		for (ParticleMolecularComponentPattern mcp : componentPatternList) {
			if (mcp.getMolecularComponent() == mc) {
				return mcp;
			}
		}
		throw new RuntimeException("All components are added in the constructor, so here it can never be null");
	}
	
	public void removeMolecularComponentPattern(ParticleMolecularComponentPattern molecularComponentPattern) {
		ArrayList<ParticleMolecularComponentPattern> newValue = new ArrayList<ParticleMolecularComponentPattern>(componentPatternList);
		newValue.remove(molecularComponentPattern);
		setComponentPatterns(newValue);
	}
	
	boolean isFullyDefined(){
		for (ParticleMolecularComponentPattern patterns : componentPatternList){
			if (!patterns.isFullyDefined()){
				return false;
			}
		}
		return true;
	}

	public final ParticleMolecularType getMolecularType() {
		return molecularType;
	}

	public final List<ParticleMolecularComponentPattern> getMolecularComponentPatternList() {
		return componentPatternList;
	}
	
	public void addMolecularComponentPattern(ParticleMolecularComponentPattern componentPattern){
		if (!componentPatternList.contains(componentPattern)){
			componentPatternList.add(componentPattern);
		}
	}

	public void insertMolecularComponentPattern(int position, ParticleMolecularComponentPattern componentPattern){
		if (!componentPatternList.contains(componentPattern)){
			componentPatternList.add(position,componentPattern);
		}
	}

	public final void setComponentPatterns(ArrayList<ParticleMolecularComponentPattern> newValue) {
		this.componentPatternList = newValue;
	}
	
	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof ParticleMolecularTypePattern){
			ParticleMolecularTypePattern other = (ParticleMolecularTypePattern)obj;
			if (!Compare.isEqual(componentPatternList, other.componentPatternList)){
				return false;
			}
			if (!Compare.isEqual(molecularType, other.molecularType)){
				return false;
			}
			if (!Compare.isEqualOrNull(matchLabel, other.matchLabel)){
				return false;
			}
			return true;
		}
		return false;
	}

	public String getVCML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("    "+VCML.ParticleMolecularTypePattern + " " + molecularType.getName() + " {");
		if (this.matchLabel!=null){
			buffer.append("\n    "+"    "+VCML.ParticleMolecularTypePatternMatchLabel+" "+matchLabel+"\n    ");
		}
		if (componentPatternList.size() == 0){
			buffer.append(" }");
		}else{
			for (ParticleMolecularComponentPattern pattern : componentPatternList) {
				buffer.append("\n        "+pattern.getVCML());
			}
			buffer.append("\n    }");
		}
		return buffer.toString();
	}

	public void read(CommentStringTokenizer tokens) throws MathFormatException {
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
			if (token.equalsIgnoreCase(VCML.ParticleMolecularTypePatternMatchLabel)){
				token = tokens.nextToken();
				setMatchLabel(token);
				continue;
			}	
			if (token.equalsIgnoreCase(VCML.ParticleMolecularComponentPattern)){
				token = tokens.nextToken();
//return VCML.ParticleMolecularComponentPattern + " " + molecularComponent.getName() + " " + VCML.ParticleComponentStatePattern + " " + componentState.getVCML() + " " + VCML.ParticleComponentBondPattern + " " + bondType.getVCML(bondId)+"\n";
				String molecularComponentName = token;
				ParticleMolecularComponent molecularComponent = molecularType.getMolecularComponent(molecularComponentName);
				ParticleMolecularComponentPattern particleMolecularComponentPattern = new ParticleMolecularComponentPattern(molecularComponent);
				particleMolecularComponentPattern.read(tokens);
				addMolecularComponentPattern(particleMolecularComponentPattern);
				continue;
			}	
			throw new MathFormatException("unexpected identifier "+token);
		}	
			
	}

	public void setMatchLabel(String matchLabel) {
		if(MolecularTypePattern.TRIVIAL_MATCH.equals(matchLabel)) {
			this.matchLabel = matchLabel;
			return;
		}
		try {
			Integer.parseInt(matchLabel);
			this.matchLabel = matchLabel;
			return;
		} catch(NumberFormatException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public String getMatchLabel(){
		return matchLabel;
	}
	
	public boolean hasExplicitParticipantMatch() {
		if(MolecularTypePattern.TRIVIAL_MATCH.equals(matchLabel)) {
			return false;
		} else {
			return true;
		}
	}
	
}
