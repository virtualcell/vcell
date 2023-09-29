package cbit.vcell.math;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
		if(!(obj instanceof LangevinParticleMolecularType)) {
			return false;
		}
		LangevinParticleMolecularType other = (LangevinParticleMolecularType)obj;
		if (false) {
			return false;			// TODO: compare everything that needs comparing
		}
		return super.compareEqual(obj);
	}
	
	public String getVCML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(VCML.ParticleMolecularType + " " + getName() + " " + VCML.IsLangevinParticleMolecularType + " " + VCML.BeginBlock);
		if (getComponentList().size() == 0 && getAnchorList().size() == 0) {
			buffer.append(" "+VCML.EndBlock+"\n");
		} else {
			if(internalLinkSpec.size() > 0) {
				buffer.append("\n        " + VCML.Links + "  " + VCML.BeginBlock);
				for(Pair<LangevinParticleMolecularComponent, LangevinParticleMolecularComponent> pair : internalLinkSpec) {
					buffer.append("\n            " + pair.one.getName() + VCML.LinkSeparator + pair.two.getName());
				}
				buffer.append("\n        " + VCML.EndBlock);
			}
			for (ParticleMolecularComponent component : getComponentList()) {
				if(!(component instanceof LangevinParticleMolecularComponent)) {
					throw new RuntimeException("LangevinParticleMolecularType: Site instance must be LangevinParticleMolecularComponent");
				}
				LangevinParticleMolecularComponent langevinComponent = (LangevinParticleMolecularComponent)component;
				buffer.append("\n    "+langevinComponent.getVCML());
			}
			for(String anchor : getAnchorList()) {
				buffer.append("\n        " + VCML.ParticleMolecularTypeAnchor + " " + anchor);
			}
			buffer.append("\n"+VCML.EndBlock+"\n");
		}
		return buffer.toString();
	}

	public void read(CommentStringTokenizer tokens) throws MathFormatException {
		Set<Pair<String, String>> linksAsStringsSet = new LinkedHashSet<> ();
		Map<String, LangevinParticleMolecularComponent> lpmcMap = new LinkedHashMap<> ();
		String token = null;
//		token = tokens.nextToken();
//		if (!token.equalsIgnoreCase(VCML.BeginBlock)){
//			throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
//		}
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				break;
			}
			if (token.equalsIgnoreCase(VCML.Links)) {
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)){
					throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
				}
				while (tokens.hasMoreTokens()) {
					token = tokens.nextToken();
					if (token.equalsIgnoreCase(VCML.EndBlock)) {
						token = tokens.nextToken();	// if we have links, we must have components
						break;
					}
					String one = token;
					token = tokens.nextToken();		// the " :: "
					token = tokens.nextToken();
					String two = token;
					Pair<String, String> pair = new Pair<> (one, two);
					linksAsStringsSet.add(pair);	// at this point we may not have yet the LangevinParticleMolecularComponent
				}
			}
			if (token.equalsIgnoreCase(VCML.ParticleMolecularComponent)) {
				token = tokens.nextToken();
				String molecularComponentName = token;
				String id = getName() + "_" + molecularComponentName;
				LangevinParticleMolecularComponent particleMolecularComponent = new LangevinParticleMolecularComponent(id, molecularComponentName);
				particleMolecularComponent.read(tokens);
				addMolecularComponent(particleMolecularComponent);
				lpmcMap.put(molecularComponentName, particleMolecularComponent);
				continue;
			} else if(token.equalsIgnoreCase(VCML.ParticleMolecularTypeAnchor)) {
				token = tokens.nextToken();
				String anchor = token;
				getAnchorList().add(anchor);
				continue;
			}
			throw new MathFormatException("unexpected identifier "+token);
		}
		for(Pair<String, String> pair : linksAsStringsSet) {
			LangevinParticleMolecularComponent one = lpmcMap.get(pair.one);
			LangevinParticleMolecularComponent two = lpmcMap.get(pair.two);
			Pair<LangevinParticleMolecularComponent, LangevinParticleMolecularComponent> link = new Pair<> (one, two);
			internalLinkSpec.add(link);
		}
	}

	public Set<Pair<LangevinParticleMolecularComponent, LangevinParticleMolecularComponent>> getInternalLinkSpec() {
		return internalLinkSpec;
	}
	public void setInternalLinkSpec(Set<Pair<LangevinParticleMolecularComponent, LangevinParticleMolecularComponent>> internalLinkSpec) {
		this.internalLinkSpec = internalLinkSpec;
	}
	
}
