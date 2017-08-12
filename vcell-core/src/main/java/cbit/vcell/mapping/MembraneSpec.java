package cbit.vcell.mapping;

import java.io.Serializable;

import org.vcell.util.Matchable;

import cbit.vcell.model.Membrane;
import cbit.vcell.parser.Expression;

/**
 * model "Celerity" "swiftness of movement or action" of membrane. 
 * "Velocity" currently understood to be advection velocity  
 * @author GWeatherby
 *
 */
public class MembraneSpec implements Matchable, Serializable {
	
	private Membrane membrane; 
	private Expression  celerityX;
	private Expression  celerityY;
	/**
	 * @param m to set velocity (celerity) for
	 */
	public MembraneSpec(Membrane m) {
		membrane = m;
		celerityX = new Expression(0);
		celerityY = new Expression(0);
	}
	
	/**
	 * @return name of membrane
	 */
	public String getName() {
		return membrane.getName(); 
	}

	public Expression getCelerityX() {
		return celerityX;
	}
	
	public void setCelerityX(Expression celerityX) {
		this.celerityX = celerityX;
	}
	public Expression getCelerityY() {
		return celerityY;
	}
	public void setCelerityY(Expression celerityY) {
		this.celerityY = celerityY;
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof MembraneSpec) {
			return compareEqual( (MembraneSpec) obj);
		}
		return false;
	}
	
	/**
	 * overload if type is known
	 * @param rhs
	 * @return true if membrane + {@link #getCelerityX()} + {@link #getCelerityY()} the same
	 */
	public boolean compareEqual(MembraneSpec rhs) {
		return membrane.compareEqual(rhs.membrane)
				&& celerityX.compareEqual(rhs.celerityX)
				&& celerityY.compareEqual(rhs.celerityY);
	}
	

}
