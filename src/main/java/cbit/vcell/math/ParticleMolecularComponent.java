package cbit.vcell.math;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

@SuppressWarnings("serial")
public class ParticleMolecularComponent implements Serializable, Matchable {
	
	private String id;
	private String name;                                  // binding site or phosphosite, motif, extracdomain (e.g. tyrosine 77) 
	private List<ParticleComponentStateDefinition> componentStateDefinitions = new ArrayList<ParticleComponentStateDefinition>();    // allowable states (e.g. Phosphorylated, Unphosphorylated)
	
//	public static class ParticleComponentState implements Serializable, Matchable {
//		private String name;   // e.g. Phosphorated, ...
//		public static final String ANY_STATE = "*";
//		
//		public ParticleComponentState(String name) {
//			this.name = name;
//		}
//		
//		public boolean isAny(){
//			return name.equals(ANY_STATE);
//		}
//
//		public final String getName() {
//			return name;
//		}
//		
//		@Override
//		public boolean compareEqual(Matchable obj) {
//			if (obj instanceof ParticleComponentState){
//				ParticleComponentState other = (ParticleComponentState)obj;
//				if (!Compare.isEqual(name, other.name)){
//					return false;
//				}
//				return true;
//			}
//			return false;
//		}
//
//		public String getVCML() {
//			return name;
//		}
//		
//	}
	
	@SuppressWarnings("unused")
	private ParticleMolecularComponent() {	// making it impossible to use the default constructor
		this(null, null);
	}
	public ParticleMolecularComponent(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public final String getName() {
		return name;
	}
	public void addComponentStateDefinition(ParticleComponentStateDefinition componentStateDefinition) {
		if (!componentStateDefinitions.contains(componentStateDefinition)) {
			List<ParticleComponentStateDefinition> newValue = new ArrayList<ParticleComponentStateDefinition>(componentStateDefinitions);
			newValue.add(componentStateDefinition);
			setComponentStateDefinitions(newValue);
		}
	}
	
	public void deleteComponentStateDefinition(ParticleComponentStateDefinition componentStateDefinition) {
		if (componentStateDefinitions.contains(componentStateDefinition)) {
			List<ParticleComponentStateDefinition> newValue = new ArrayList<ParticleComponentStateDefinition>(componentStateDefinitions);
			newValue.remove(componentStateDefinition);
			setComponentStateDefinitions(newValue);
		}
	}
	
	public ParticleComponentStateDefinition createComponentStateDefinition() {
		int count=0;
		String name = null;
		while (true) {
			name = "state" + count;	
			if (getComponentStateDefinition(name) == null) {
				break;
			}	
			count++;
		}
		return new ParticleComponentStateDefinition(name);
	}
	
	public ParticleComponentStateDefinition getComponentStateDefinition(String componentName) {
		for (ParticleComponentStateDefinition cs : componentStateDefinitions)  {
			if (cs.getName().equals(componentName)) {
				return cs;
			}
		}
		return null;
	}
	
	public final List<ParticleComponentStateDefinition> getComponentStateDefinitions() {
		return componentStateDefinitions;
	}
	public void setName(String newValue) throws PropertyVetoException {
		name = newValue;
	}
	
	public final void setComponentStateDefinitions(List<ParticleComponentStateDefinition> newValue) {
		componentStateDefinitions = newValue;
	}
	
	public String getId() {
		return id;
//		System.err.println("getId() not correct for MolecularComponent");
//		return "MolecularComponent_" + hashCode();
	}
	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof ParticleMolecularComponent){
			ParticleMolecularComponent other = (ParticleMolecularComponent)obj;
			if (!Compare.isEqual(componentStateDefinitions, other.componentStateDefinitions)){
				return false;
			}
			if (!Compare.isEqual(name, other.name)){
				return false;
			}
			return true;
		}
		return false;
	}
	
	public String getVCML(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("    "+VCML.ParticleMolecularComponent + " " + getName()+" { ");
		if (getComponentStateDefinitions().size()==0){
			buffer.append(" }");
		}else{
			for (ParticleComponentStateDefinition state : getComponentStateDefinitions()){
				String name = state.getName();
				buffer.append("\n            "+VCML.ParticleComponentAllowableState + " " + name + "");
			}
			buffer.append("\n        "+"}");
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
			if (token.equalsIgnoreCase(VCML.ParticleComponentAllowableState)){
				token = tokens.nextToken();
				String componentName = token;
				if(!componentName.equals("*")) {
					ParticleComponentStateDefinition pcsd = getComponentStateDefinition(componentName);
					if(pcsd == null) {
						pcsd = new ParticleComponentStateDefinition(componentName);
						addComponentStateDefinition(pcsd);
					}
				}
				continue;
			}	
			throw new MathFormatException("unexpected identifier "+token);
		}	
	}
//	public String getId(ParticleMolecularTypePattern particleMolecularTypePattern) {
//		return particleMolecularTypePattern.getMolecularType().getName()+"_"+name;
//	}
}
