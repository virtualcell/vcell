package cbit.vcell.math;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.ExpressionException;

public class ComputeCentroidComponentEquation extends MeasureEquation {
	public enum CentroidComponent {
		X, Y, Z;
	}
	
	private final CentroidComponent component;

	public ComputeCentroidComponentEquation(Variable var, CentroidComponent component) {
		super(var);
		this.component = component;
	}
	
	final String getVCMLName(){
		switch (component) {
		case X:
			return VCML.ComputeCentroidX;
		case Y:
			return VCML.ComputeCentroidY;
		case Z:
			return VCML.ComputeCentroidZ;
		default:
			throw new RuntimeException("unexpected component "+component);
		}
	}
	
	public final CentroidComponent getComponent(){
		return this.component;
	}

	@Override
	public final void checkValid(MathDescription mathDesc, SubDomain subDomain) throws MathException,	ExpressionException {
		if (!(subDomain instanceof CompartmentSubDomain)){
			throw new MathException("expecting "+getVCMLName()+" to be defined within a "+VCML.CompartmentSubDomain);
		}
		if (!(getVariable() instanceof VolVariable)){
			throw new MathException("expecting "+getVCMLName()+" to be defined for a variable of type "+VCML.VolumeVariable);
		}
	}

	@Override
	public boolean compareEqual(Matchable object) {
		if (object instanceof ComputeCentroidComponentEquation){
			ComputeCentroidComponentEquation other = (ComputeCentroidComponentEquation)object;
			if (!compareEqual0(other)){
				return false;
			}
			if (!Compare.isEqual(this.component, other.component)){
				return false;
			}
			return true;
		}
		return false;
	}

}