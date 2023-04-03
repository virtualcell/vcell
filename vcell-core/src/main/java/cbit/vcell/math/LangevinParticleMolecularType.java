package cbit.vcell.math;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.Pair;

@SuppressWarnings("serial")
public class LangevinParticleMolecularType extends ParticleMolecularType {
	
	private Set<Pair<LangevinParticleMolecularComponent, LangevinParticleMolecularComponent>> internalLinkSpec = new LinkedHashSet<> ();
	
	
	public LangevinParticleMolecularType(String name) {
		super(name);
	}
	
	
	@Override
	public boolean compareEqual(Matchable obj) {
		if(!(obj instanceof ParticleMolecularType)) {
			return false;
		}
		ParticleMolecularType other = (ParticleMolecularType)obj;
		if (false) {
			return false;			// TODO: compare everything that needs comparing
		}
		return super.compareEqual(obj);
	}
	
	public String getVCML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(VCML.ParticleMolecularType + " " + getName() + " " + VCML.BeginBlock);
		if (getComponentList().size() == 0 && getAnchorList().size() == 0){
			buffer.append(" "+VCML.EndBlock+"\n");
		}else{
			for (ParticleMolecularComponent component : getComponentList()) {
				buffer.append("\n    "+component.getVCML());
			}
			for(String anchor : getAnchorList()) {
				buffer.append("\n        " + VCML.ParticleMolecularTypeAnchor + " " + anchor);
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
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				break;
			}			
			if (token.equalsIgnoreCase(VCML.ParticleMolecularComponent)) {
				token = tokens.nextToken();
				String molecularComponentName = token;
				String id = getName() + "_" + molecularComponentName;
				ParticleMolecularComponent particleMolecularComponent = new ParticleMolecularComponent(id, molecularComponentName);
				particleMolecularComponent.read(tokens);
				addMolecularComponent(particleMolecularComponent);
				continue;
			} else if(token.equalsIgnoreCase(VCML.ParticleMolecularTypeAnchor)) {
				token = tokens.nextToken();
				String anchor = token;
				getAnchorList().add(anchor);
				continue;
			}
			throw new MathFormatException("unexpected identifier "+token);
		}	
	}
	
}
