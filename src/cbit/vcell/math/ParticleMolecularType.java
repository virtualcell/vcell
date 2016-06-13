package cbit.vcell.math;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

@SuppressWarnings("serial")
public class ParticleMolecularType implements Serializable, Matchable {
	
	private String name;	
	private List<ParticleMolecularComponent> componentList = new ArrayList<ParticleMolecularComponent>();
	
	public ParticleMolecularType(String name) {
		this.name = name;
	}
	
	public void addMolecularComponent(ParticleMolecularComponent molecularComponent) {
		if (!componentList.contains(molecularComponent)) {
			List<ParticleMolecularComponent> newValue = new ArrayList<ParticleMolecularComponent>(componentList);
			newValue.add(molecularComponent);
			setComponentList(newValue);
		}
	}
	
	public void insertMolecularComponent(int position, ParticleMolecularComponent molecularComponent) {
		if (!componentList.contains(molecularComponent)) {
			List<ParticleMolecularComponent> newValue = new ArrayList<ParticleMolecularComponent>(componentList);
			newValue.add(position, molecularComponent);
			setComponentList(newValue);
		}
	}
	
	public ParticleMolecularComponent createMolecularComponent() {
		int count=0;
		String name = null;
		while (true) {
			name = "component" + count;	
			if (getMolecularComponent(name) == null) {
				break;
			}	
			count++;
		}
		return new ParticleMolecularComponent(getName() + "_" + name, name);
	}
	
	public void removeMolecularComponent(ParticleMolecularComponent molecularComponent) {
		if (componentList.contains(molecularComponent)) {
			List<ParticleMolecularComponent> newValue = new ArrayList<ParticleMolecularComponent>(componentList);
			newValue.remove(molecularComponent);
			setComponentList(newValue);
		}
	}

	public ParticleMolecularComponent getMolecularComponent(String componentName) {
		for (ParticleMolecularComponent mc : componentList)  {
			if (mc.getName().equals(componentName)) {
				return mc;
			}
		}
		return null;
	}
	
	public final String getName() {
		return name;
	}

	public void setName(String newValue) throws PropertyVetoException {
		name = newValue;
	}

	public final List<ParticleMolecularComponent> getComponentList() {
		return componentList;
	}

	public final void setComponentList(List<ParticleMolecularComponent> newValue) {
		componentList = newValue;
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof ParticleMolecularType){
			ParticleMolecularType other = (ParticleMolecularType)obj;
			if (!Compare.isEqual(componentList, other.componentList)){
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
		buffer.append(VCML.ParticleMolecularType + " " + name + " " + VCML.BeginBlock);
		if (componentList.size()==0){
			buffer.append(" "+VCML.EndBlock+"\n");
		}else{
			for (ParticleMolecularComponent component : componentList){
				buffer.append("\n    "+component.getVCML());
			}
			buffer.append("\n"+VCML.EndBlock+"\n");
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
			if (token.equalsIgnoreCase(VCML.ParticleMolecularComponent)){
				token = tokens.nextToken();
				String molecularComponentName = token;
				String id = getName() + "_" + molecularComponentName;
				ParticleMolecularComponent particleMolecularComponent = new ParticleMolecularComponent(id, molecularComponentName);
				particleMolecularComponent.read(tokens);
				addMolecularComponent(particleMolecularComponent);
				continue;
			}
			throw new MathFormatException("unexpected identifier "+token);
		}	
	}
	
}
