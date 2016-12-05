package cbit.vcell.math;

import java.io.Serializable;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.ExpressionException;

public class ComputeMembraneMetricEquation extends MeasureEquation {
	public enum MembraneMetricComponent {
		directionToMembraneX, 
		directionToMembraneY,
		directionToMembraneZ,
		distanceToMembrane;
	}
	public static class MembraneSubDomainReference implements Serializable, Matchable {
		private String membraneSubDomainName;
		private MembraneSubDomain membraneSubDomain;
		
		public MembraneSubDomainReference(String membraneSubDomainName){
			this.membraneSubDomainName = membraneSubDomainName;
		}
		public MembraneSubDomainReference(MembraneSubDomain membraneSubDomain){
			this.membraneSubDomain = membraneSubDomain;
		}
		public MembraneSubDomain getMembraneSubDomain(){
			return this.membraneSubDomain;
		}
		public void bind(MathDescription mathDesc){
			if (membraneSubDomain == null && membraneSubDomainName != null){
				membraneSubDomain = (MembraneSubDomain)mathDesc.getSubDomain(membraneSubDomainName);
			}
		}
		
		@Override
		public boolean compareEqual(Matchable obj) {
			if (obj instanceof MembraneSubDomainReference){
				MembraneSubDomainReference other = (MembraneSubDomainReference)obj;
				if (!Compare.isEqualOrNull(membraneSubDomainName, other.membraneSubDomainName)){
					return false;
				}
				String memName = (membraneSubDomain!=null)?membraneSubDomain.getName():null;
				String otherMemName = (other.membraneSubDomain!=null)?other.membraneSubDomain.getName():null;
				if (!Compare.isEqualOrNull(memName, otherMemName)){
					return false;
				}
				return true;
			}
			return false;
		}
	}
	
	private final MembraneMetricComponent component;
	private MembraneSubDomainReference targetMembrane;

	public ComputeMembraneMetricEquation(Variable var, MembraneMetricComponent component) {
		super(var);
		this.component = component;
	}
	
	public final MembraneMetricComponent getComponent(){
		return this.component;
	}

	public final MembraneSubDomain getTargetMembrane(){
		return this.targetMembrane.getMembraneSubDomain();
	}
	
	@Override
	public void refreshDependencies(MathDescription mathDesc) {
		targetMembrane.bind(mathDesc);
	}

	@Override
	public void read(CommentStringTokenizer tokens, MathDescription mathDesc) throws MathFormatException {
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
			if (token.equalsIgnoreCase(VCML.MembraneSubDomain)){
				token = tokens.nextToken();
				SubDomain targetSubDomain = mathDesc.getSubDomain(token);
				if (targetSubDomain instanceof MembraneSubDomain){
					targetMembrane = new MembraneSubDomainReference((MembraneSubDomain)targetSubDomain);
				}else{
					targetMembrane = new MembraneSubDomainReference(token);
				}
				continue;
			}
			throw new MathFormatException("unexpected identifier "+token);
		}
		if (targetMembrane==null){
			throw new MathFormatException(VCML.MembraneSubDomain+" property not defined for "+getVCMLName(), tokens.lineIndex());
		}
	}
	
	@Override
	public String getVCML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t"+getVCMLName()+" "+getVariable().getName()+" { \n");
		buffer.append("\t\t"+VCML.MembraneSubDomain+" \t "+getTargetMembrane().getName()+" \n");
		buffer.append("\t}\n");
		return buffer.toString();		
	}


	final String getVCMLName(){
		switch (component) {
		case directionToMembraneX:
			return VCML.ComputeDirectionToMembraneX;
		case directionToMembraneY:
			return VCML.ComputeDirectionToMembraneY;
		case directionToMembraneZ:
			return VCML.ComputeDirectionToMembraneZ;
		case distanceToMembrane:
			return VCML.ComputeDistanceToMembrane;
		default:
			throw new RuntimeException("unexpected component "+component);
		}
	}
	
	@Override
	public final void checkValid(MathDescription mathDesc, SubDomain subDomain) throws MathException,	ExpressionException {
		targetMembrane.bind(mathDesc);
		if (!(subDomain instanceof CompartmentSubDomain)){
			throw new MathException("expecting "+getVCMLName()+" to be defined within a "+VCML.CompartmentSubDomain);
		}
		if (!(getTargetMembrane() instanceof MembraneSubDomain)){
			throw new MathException("expecting "+getVCMLName()+" to be targeted to a 'Membrane' subdomain of type "+VCML.MembraneSubDomain);
		}
		if (!(getVariable() instanceof VolVariable)){
			throw new MathException("expecting "+getVCMLName()+" to be defined for a variable of type "+VCML.VolumeVariable);
		}
	}

	@Override
	public boolean compareEqual(Matchable object) {
		if (object instanceof ComputeMembraneMetricEquation){
			ComputeMembraneMetricEquation other = (ComputeMembraneMetricEquation)object;
			if (!compareEqual0(other)){
				return false;
			}
			if (!Compare.isEqual(this.component, other.component)){
				return false;
			}
			if (!Compare.isEqual(this.targetMembrane,  other.targetMembrane)){
				return false;
			}
			return true;
		}
		return false;
	}

	public void setTargetMembraneName(String membraneSubDomainName) {
		this.targetMembrane = new MembraneSubDomainReference(membraneSubDomainName);
	}

}