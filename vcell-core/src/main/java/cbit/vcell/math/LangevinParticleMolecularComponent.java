package cbit.vcell.math;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Coordinate;
import org.vcell.util.Matchable;
import org.vcell.util.springsalad.Colors;
import org.vcell.util.springsalad.IOHelp;
import org.vcell.util.springsalad.NamedColor;

import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class LangevinParticleMolecularComponent extends ParticleMolecularComponent {
	
	private double fieldRadius = 1.0;
	private double fieldDiffusionRate = 1.0;
	private String fieldLocation = null;		// feature or membrane name, identical to subdomain name
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
			buffer.append("\n            "+VCML.ParticleComponentRadius + " " + fieldRadius + "");
			buffer.append("\n            "+VCML.ParticleComponentDiffusionRate + " " + fieldDiffusionRate + "");
			buffer.append("\n            "+VCML.ParticleComponentLocation + " " + fieldLocation + "");
			buffer.append("\n            "+VCML.ParticleComponentCoordinate + " " + fieldCoordinate + "");
			buffer.append("\n            "+VCML.ParticleComponentColor + " " + fieldColor + "");
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
			if(token.equalsIgnoreCase(VCML.ParticleComponentRadius)) {
				token = tokens.nextToken();
				Double radius = Double.parseDouble(token);
				setRadius(radius);
				continue;
			}
			if(token.equalsIgnoreCase(VCML.ParticleComponentDiffusionRate)) {
				token = tokens.nextToken();
				Double dr = Double.parseDouble(token);
				setDiffusionRate(dr);
				continue;
			}
			if(token.equalsIgnoreCase(VCML.ParticleComponentLocation)) {
				token = tokens.nextToken();
				String loc = token;
				setLocation(loc);
				continue;
			}
			if(token.equalsIgnoreCase(VCML.ParticleComponentCoordinate)) {
				token = tokens.nextToken();
				String x = token;
				x = x.substring(x.indexOf("=")+1, x.length());
				token = tokens.nextToken();
				String y = token;
				y = y.substring(y.indexOf("=")+1, y.length());
				token = tokens.nextToken();
				String z = token;
				z = z.substring(z.indexOf("=")+1, z.length());
				Coordinate coordinate = new Coordinate(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z));
				setCoordinate(coordinate);
				continue;
			}
			if(token.equalsIgnoreCase(VCML.ParticleComponentColor)) {
				token = tokens.nextToken();
				NamedColor color = Colors.getColorByName(token);
				setColor(color);
				continue;
			}
			throw new MathFormatException("unexpected identifier "+token);
		}	
	}
	
	public void writeType(StringBuilder sb) {
		sb.append("TYPE: Name \"" + getName() + "\"");
		sb.append(" Radius " + IOHelp.DF[5].format(getRadius()) + " D " + IOHelp.DF[3].format(getDiffusionRate()) + " Color " + getColor().getName());
		sb.append(" STATES ");
		for (ParticleComponentStateDefinition state : getComponentStateDefinitions()) {
			sb.append("\"" + state.getName() + "\"" + " ");
		}
		sb.append("\n");
	}
	public void writeSite(StringBuilder sb, int index, String initialState) {
		sb.append("SITE " + index + 
				" : " + getLocation() + " : Initial State '" + initialState + "'");
		sb.append("\n");
		sb.append("          ");
		this.writeType(sb);	// ex: TYPE: Name "Type2" Radius 1.00000 D 1.000 Color LIME STATES "State0" "State1" 
		sb.append("          " + "x " + IOHelp.DF[5].format(getCoordinate().getX()) + " y " + 
				IOHelp.DF[5].format(getCoordinate().getY()) + " z " + 
				IOHelp.DF[5].format(getCoordinate().getZ()) + " ");		// ex: x 4.00000 y 4.00000 z 20.00000
		sb.append("\n");
	}
	
	public double getRadius() {
		return fieldRadius;
	}
	public void setRadius(double fieldRadius) {
		this.fieldRadius = fieldRadius;
	}
	public double getDiffusionRate() {
		return fieldDiffusionRate;
	}
	public void setDiffusionRate(double fieldDiffusionRate) {
		this.fieldDiffusionRate = fieldDiffusionRate;
	}
	public String getLocation() {
		return fieldLocation;
	}
	public void setLocation(String fieldLocation) {
		this.fieldLocation = fieldLocation;
	}
	public Coordinate getCoordinate() {
		return fieldCoordinate;
	}
	public void setCoordinate(Coordinate fieldCoordinate) {
		this.fieldCoordinate = fieldCoordinate;
	}
	public NamedColor getColor() {
		return fieldColor;
	}
	public void setColor(NamedColor fieldColor) {
		this.fieldColor = fieldColor;
	}

}
