package cbit.vcell.math;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.ExpressionException;

public class ComputeNormalComponentEquation extends MeasureEquation {
	public enum NormalComponent {
		X, Y, Z;
	}
	
	private final NormalComponent component;

	public ComputeNormalComponentEquation(Variable var, NormalComponent component) {
		super(var);
		this.component = component;
	}
	
	public final NormalComponent getComponent(){
		return this.component;
	}

	final String getVCMLName(){
		switch (component) {
		case X:
			return VCML.ComputeNormalX;
		case Y:
			return VCML.ComputeNormalY;
		case Z:
			return VCML.ComputeNormalZ;
		default:
			throw new RuntimeException("unexpected component "+component);
		}
	}
	
	@Override
	public final void checkValid(MathDescription mathDesc, SubDomain subDomain) throws MathException,	ExpressionException {
		if (!(subDomain instanceof MembraneSubDomain)){
			throw new MathException("expecting "+getVCMLName()+" to be defined within a "+VCML.MembraneSubDomain);
		}
		if (!(getVariable() instanceof MemVariable)){
			throw new MathException("expecting "+getVCMLName()+" to be defined for a variable of type "+VCML.MembraneVariable);
		}
	}

	@Override
	public boolean compareEqual(Matchable object) {
		if (object instanceof ComputeNormalComponentEquation){
			ComputeNormalComponentEquation other = (ComputeNormalComponentEquation)object;
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