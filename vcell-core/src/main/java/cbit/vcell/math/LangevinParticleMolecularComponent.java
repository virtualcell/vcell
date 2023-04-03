package cbit.vcell.math;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Coordinate;
import org.vcell.util.Matchable;
import org.vcell.util.springsalad.Colors;
import org.vcell.util.springsalad.NamedColor;

import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class LangevinParticleMolecularComponent extends ParticleMolecularComponent {
	
	private double fieldRadius = 1.0;
	private double fieldDiffusionRate = 1.0;
	private Structure fieldLocation = null;		// feature or membrane
	private Coordinate fieldCoordinate = new Coordinate(0,0,0);	// double x,y,z; has distanceTo()
	private NamedColor fieldColor = Colors.RED;

	
	@SuppressWarnings("unused")
	private LangevinParticleMolecularComponent() {	// making it impossible to use the default constructor
		super(null, null);
	}
	public LangevinParticleMolecularComponent(String id, String name) {
		super(id, name);
	}


	@Override
	public boolean compareEqual(Matchable obj) {
		if (!(obj instanceof LangevinParticleMolecularComponent)) {
			return false;
		}
		LangevinParticleMolecularComponent other = (LangevinParticleMolecularComponent)obj;

		if(false) {			// TODO: compare everything that needs comparing
			return false;
		}
		return super.compareEqual(obj);
	}
	
	public String getVCML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("    "+VCML.ParticleMolecularComponent + " " + getName()+" { ");
		if (getComponentStateDefinitions().size()==0) {
			buffer.append(" }");
		} else {
			for (ParticleComponentStateDefinition state : getComponentStateDefinitions()) {
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
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				break;
			}			
			if (token.equalsIgnoreCase(VCML.ParticleComponentAllowableState)) {
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

}
