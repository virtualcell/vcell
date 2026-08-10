package cbit.vcell.math;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.CommentStringTokenizer;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionUtils;
import org.vcell.util.Compare;
import org.vcell.util.Coordinate;
import org.vcell.util.Matchable;
import org.vcell.util.springsalad.Colors;
import org.vcell.util.springsalad.NamedColor;


@SuppressWarnings("serial")
public class LangevinParticleMolecularComponent extends ParticleMolecularComponent {

	/** An {@link Expression}, like every scalar in a math description - see docs/architecture-layers.md P3. */
	private Expression fieldRadius = new Expression(1.0);
	private Expression fieldDiffusionRate = new Expression(1.0);
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
		return compareEqual(obj, false);
	}

	/**
	 * @param bIgnoreDisplayAttributes skip attributes that are persisted but never reach the solver, so
	 *        they cannot change what a simulation computes - currently just colour.
	 *        <p>
	 *        Used by the equivalence tier, where a difference clears
	 *        {@code SimulationVersion.parentSimulationReference} and hides the user's existing results;
	 *        a colour change must not cost them their results. The identical tier passes {@code false},
	 *        because the math still has to be saved or the edit is lost.
	 */
	public boolean compareEqual(Matchable obj, boolean bIgnoreDisplayAttributes) {
		// exact class, not instanceof: the superclass accepts any ParticleMolecularComponent, so an
		// instanceof test here would make comparison depend on which side it is called from.
		if (obj == null || !getClass().equals(obj.getClass())) {
			return false;
		}
		LangevinParticleMolecularComponent other = (LangevinParticleMolecularComponent)obj;

		// Exact comparison: this is the "identical" tier. Tolerance belongs to the equivalence tier,
		// as it does for expressions (ExpressionUtils.functionallyEquivalent).
		if(!Compare.isEqualOrNull(fieldRadius, other.fieldRadius, new ExpressionUtils.ExpressionEquivalencePredicate())) {
			return false;
		}
		if(!Compare.isEqualOrNull(fieldDiffusionRate, other.fieldDiffusionRate, new ExpressionUtils.ExpressionEquivalencePredicate())) {
			return false;
		}
		if(!Compare.isEqualOrNull(fieldLocation, other.fieldLocation)) {
			return false;
		}
		if(!Compare.isEqualOrNull(fieldCoordinate, other.fieldCoordinate)) {
			return false;
		}
		// Colour is part of the persisted math, so it is compared here to keep "identical" meaning
		// identical - without it a colour edit would not be saved. It does not reach the solver
		// input, so an equivalence tier may legitimately ignore it.
		if(!bIgnoreDisplayAttributes
				&& !Compare.isEqualOrNull(fieldColor == null ? null : fieldColor.getName(),
						other.fieldColor == null ? null : other.fieldColor.getName())) {
			return false;
		}
		return super.compareEqual(obj);
	}
	
	public String getVCML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("    "+VCML.ParticleMolecularComponent + " " + getName()+" { ");
		buffer.append("\n            "+VCML.ParticleComponentRadius + " " + fieldRadius.infix() + "");
		buffer.append("\n            "+VCML.ParticleComponentDiffusionRate + " " + fieldDiffusionRate.infix() + "");
		buffer.append("\n            "+VCML.ParticleComponentLocation + " " + fieldLocation + "");
		buffer.append("\n            "+VCML.ParticleComponentCoordinate + " " + fieldCoordinate + "");
		buffer.append("\n            "+VCML.ParticleComponentColor + " " + fieldColor + "");
		for (ParticleComponentStateDefinition state : getComponentStateDefinitions()) {
			String name = state.getName();
			buffer.append("\n            "+VCML.ParticleComponentAllowableState + " " + name + "");
		}
		buffer.append("\n        "+"}");
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
				try {
					setRadius(new Expression(token));
				} catch(ExpressionException e) {
					throw new MathFormatException("unparseable " + VCML.ParticleComponentRadius + " '" + token + "': " + e.getMessage());
				}
				continue;
			}
			if(token.equalsIgnoreCase(VCML.ParticleComponentDiffusionRate)) {
				token = tokens.nextToken();
				try {
					setDiffusionRate(new Expression(token));
				} catch(ExpressionException e) {
					throw new MathFormatException("unparseable " + VCML.ParticleComponentDiffusionRate + " '" + token + "': " + e.getMessage());
				}
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
	
	
	public Expression getRadius() {
		return fieldRadius;
	}
	public void setRadius(Expression fieldRadius) {
		this.fieldRadius = fieldRadius;
	}
	public Expression getDiffusionRate() {
		return fieldDiffusionRate;
	}
	public void setDiffusionRate(Expression fieldDiffusionRate) {
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
