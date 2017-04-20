package cbit.vcell.math;

import java.io.Serializable;

import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;


@SuppressWarnings("serial")
public class ParticleMolecularComponentPattern implements Serializable, Matchable {
	
	private ParticleMolecularComponent molecularComponent;
	private ParticleComponentStatePattern componentStatePattern;

	public enum ParticleBondType {
		Specified(""), // numbers
		Exists("+"),    // "+"
		Possible("?"),  // "?"
		None("-");  	   //
		
		public String symbol;
		ParticleBondType(String s) {
			this.symbol = s;
		}
		
		public static ParticleBondType fromSymbol(String symbol) throws NumberFormatException {
			for (ParticleBondType bondType : values()){
				if (bondType.symbol.equals(symbol)){
					return bondType;
				}
			}
			int bondInt = Integer.parseInt(symbol);
			return Specified;
		}
	}
	
	private int bondId = -1;                // used in BNGL for mapping notation (e.g. 1, 2, 3)
	private ParticleBondType bondType = ParticleBondType.Possible;
	
	public ParticleMolecularComponentPattern(ParticleMolecularComponent molecularComponent) {
		super();
		this.molecularComponent = molecularComponent;
	}
	
	public ParticleMolecularComponentPattern(ParticleMolecularComponentPattern pattern) {
		this.molecularComponent = pattern.molecularComponent;
		this.componentStatePattern = new ParticleComponentStatePattern(pattern.getComponentStatePattern());
		this.bondId = pattern.getBondId();
		this.bondType = pattern.getBondType();
	}

	public  boolean isFullyDefined(){
		return !componentStatePattern.isAny();
	}

	public final ParticleMolecularComponent getMolecularComponent() {
		return molecularComponent;
	}

	public final ParticleComponentStatePattern getComponentStatePattern() {
		return componentStatePattern;
	}

	public final void setComponentStatePattern(ParticleComponentStatePattern newValue) {
		if(componentStatePattern == newValue) {
			return;
		}
		this.componentStatePattern = newValue;
	}

	public final ParticleBondType getBondType() {
		return bondType;
	}

	public final int getBondId() {
		return bondId;
	}
	
	public final void setBondType(ParticleBondType newValue) {
		this.bondType = newValue;		
	}

	public final void setBondId(int newValue) {
		this.bondId = newValue;
	}
	
	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof ParticleMolecularComponentPattern){
			ParticleMolecularComponentPattern other = (ParticleMolecularComponentPattern)obj;
			if (!Compare.isEqual(bondId, other.bondId)){
				return false;
			}
			if (!Compare.isEqual(bondType, other.bondType)){
				return false;
			}
			if (!Compare.isEqual(componentStatePattern,other.componentStatePattern)){
				return false;
			}
			if (!Compare.isEqual(molecularComponent,other.molecularComponent)){
				return false;
			}
			return true;
		}
		return false;
	}
	
	public String getVCML(){
		String bondString = null;
		if (bondType == ParticleBondType.Specified){
			bondString = Integer.toString(bondId);
		}else{
			bondString = bondType.symbol;
		}
		if (componentStatePattern!=null){
			if(componentStatePattern.isAny()) {
				return "    "+VCML.ParticleMolecularComponentPattern + " " + molecularComponent.getName() + " " + VCML.ParticleComponentStatePattern + " " + ParticleComponentStatePattern.ANY_STATE + " " + VCML.ParticleComponentBondPattern + " " + bondString;
			} else {
				return "    "+VCML.ParticleMolecularComponentPattern + " " + molecularComponent.getName() + " " + VCML.ParticleComponentStatePattern + " " + componentStatePattern.getParticleComponentStateDefinition().getName() + " " + VCML.ParticleComponentBondPattern + " " + bondString;
			}
		}else{
			return "    "+VCML.ParticleMolecularComponentPattern + " " + molecularComponent.getName() + " " + VCML.ParticleComponentStatePattern + " " + ParticleComponentStatePattern.ANY_STATE + " " + VCML.ParticleComponentBondPattern + " " + bondString;
		}
	}

	public void read(CommentStringTokenizer tokens) throws MathFormatException {
		String token = tokens.nextToken();
		if (!token.equalsIgnoreCase(VCML.ParticleComponentStatePattern)){
			throw new MathFormatException("unexpected token "+token+" expecting "+VCML.ParticleComponentStatePattern);
		}
		String componentStatePatternString = tokens.nextToken();
		if(!componentStatePatternString.equals("*")) {
			ParticleComponentStateDefinition pcsd = molecularComponent.getComponentStateDefinition(componentStatePatternString);
			if(pcsd == null) {
				pcsd = new ParticleComponentStateDefinition(componentStatePatternString);
				molecularComponent.addComponentStateDefinition(pcsd);
			}
			// TODO: is this needed here or only when pcsd is created (above)?
			componentStatePattern = new ParticleComponentStatePattern(pcsd);
		} else {
			componentStatePattern = new ParticleComponentStatePattern();
		}
		
		token = tokens.nextToken();
		if (!token.equalsIgnoreCase(VCML.ParticleComponentBondPattern)){
			throw new MathFormatException("unexpected token "+token+" expecting "+VCML.ParticleComponentBondPattern);
		}
		String bondString = tokens.nextToken();
		bondType = ParticleBondType.fromSymbol(bondString);
		if (bondType == ParticleBondType.Specified){
			bondId = Integer.parseInt(bondString);
		}
	}
}
