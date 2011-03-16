package cbit.vcell.math;

import java.io.Serializable;
import java.util.ArrayList;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class ParticleProperties implements Serializable, Matchable {
	
	public abstract static class ParticleInitialCondition implements Serializable, Matchable {
		public abstract String getVCML(int dimension);
		abstract void bind(SymbolTable symbolTable) throws ExpressionBindingException;
	}
	
	public static class ParticleInitialConditionConcentration extends ParticleInitialCondition {
		Expression distribution;
		public ParticleInitialConditionConcentration(Expression dist) {
			distribution = dist;
		}
		public ParticleInitialConditionConcentration(CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {
			super();
			readVCML(tokens);
		}
		public boolean compareEqual(Matchable obj) {
			if (!(obj instanceof ParticleInitialConditionConcentration)) {
				return false;
			}
			
			ParticleInitialConditionConcentration pic = (ParticleInitialConditionConcentration) obj;		
			return Compare.isEqual(distribution, pic.distribution);
		}
		public String getVCML(int dimension) {
			StringBuilder buffer = new StringBuilder();
			buffer.append(VCML.ParticleInitialConcentration + " " + VCML.BeginBlock +"\n");
			buffer.append("\t\t\t"+VCML.ParticleDistribution + " " + distribution.infix() + ";\n");
			buffer.append("\t\t"+" "+VCML.EndBlock+"\n");
			return buffer.toString();	
		}
		private void readVCML(CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {			
			String token = tokens.nextToken();
			if (!token.equals(VCML.BeginBlock)){
				throw new MathFormatException("expecting "+VCML.BeginBlock+", found "+token);
			}
			distribution = new Expression(0);
			while(true){
				token = tokens.nextToken();
				if (token.equals(VCML.EndBlock)) {
					break;
				}
				if (token.equals(VCML.ParticleDistribution)) {
					distribution = new Expression(tokens.readToSemicolon());
				} else {
					throw new MathFormatException("unexpected identifier "+token);
				}
			}
		}
		void bind(SymbolTable symbolTable) throws ExpressionBindingException {		
			distribution.bindExpression(symbolTable);
		}
		public Expression getDistribution() {
			return distribution;
		}
	}
	public static class ParticleInitialConditionCount extends ParticleInitialCondition {
		Expression count;
		Expression locationX = null;
		Expression locationY = null;
		Expression locationZ = null;
		private static final String UNIFORM = "u";
		
		public ParticleInitialConditionCount(Expression count, Expression locationX,
				Expression locationY, Expression locationZ) {
			super();
			try {
				this.count = count == null ? new Expression(0) : count;
				this.locationX = locationX == null ? new Expression("u") : locationX;
				this.locationY = locationY == null ? new Expression("u") : locationY;
				this.locationZ = locationZ == null ? new Expression("u") : locationZ;
			} catch (ExpressionException e) {
				// ignore
//				e.printStackTrace();
			}
		}
		public ParticleInitialConditionCount(CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {
			super();
			readVCML(tokens);
		}
		private void readVCML(CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {			
			String token = tokens.nextToken();
			if (!token.equals(VCML.BeginBlock)){
				throw new MathFormatException("expecting "+VCML.BeginBlock+", found "+token);
			}
			count = new Expression(0);
			locationX = new Expression("u");
			locationY = new Expression("u");
			locationZ = new Expression("u");
			while(true){
				token = tokens.nextToken();
				if (token.equals(VCML.EndBlock)) {
					break;
				}
				if (token.equals(VCML.ParticleCount)) {
					count = new Expression(tokens.readToSemicolon());
				} else if (token.equals(VCML.ParticleLocationX)) {
					locationX = new Expression(tokens.readToSemicolon());
				} else if (token.equals(VCML.ParticleLocationY)) {
					locationY = new Expression(tokens.readToSemicolon());
				} else if (token.equals(VCML.ParticleLocationZ)) {
					locationZ = new Expression(tokens.readToSemicolon());
				} else {
					throw new MathFormatException("unexpected identifier "+token);
				}
			}
		}
		
		public String getVCML(int dimension) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(VCML.ParticleInitialCount + " " + VCML.BeginBlock +"\n");
			buffer.append("\t\t\t"+VCML.ParticleCount + " " + count.infix() + ";\n");
			buffer.append("\t\t\t"+VCML.ParticleLocationX + " " + locationX.infix() + ";\n");
			if (dimension > 1) {
				buffer.append("\t\t\t"+VCML.ParticleLocationY + " " + locationY.infix() + ";\n");
			}
			if (dimension > 2) {
				buffer.append("\t\t\t"+VCML.ParticleLocationZ + " " + locationZ.infix() + ";\n");
			}
			buffer.append("\t\t"+" "+VCML.EndBlock+"\n");
			return buffer.toString();	
		}
		public final Expression getCount() {
			return count;
		}
		public final Expression getLocationX() {
			return locationX;
		}
		public final Expression getLocationY() {
			return locationY;
		}
		public final Expression getLocationZ() {
			return locationZ;
		}
		public boolean isXUniform() {
			return locationX.infix().equals(UNIFORM);
		}
		public boolean isYUniform() {
			return locationY.infix().equals(UNIFORM);
		}
		public boolean isZUniform() {
			return locationZ.infix().equals(UNIFORM);
		}
		public boolean isUniform() {
			return isXUniform() && isYUniform() && isZUniform();
		}
		public boolean compareEqual(Matchable object) {
			if (!(object instanceof ParticleInitialConditionCount)) {
				return false;
			}
			
			ParticleInitialConditionCount pic = (ParticleInitialConditionCount) object;		
			return Compare.isEqual(count, pic.count) 
					&&	Compare.isEqualOrNull(locationX, pic.locationX) 
					&&	Compare.isEqualOrNull(locationY, pic.locationY) 
					&&	Compare.isEqualOrNull(locationZ, pic.locationZ);
		}
		void bind(SymbolTable symbolTable) throws ExpressionBindingException {		
			count.bindExpression(symbolTable);
			if (locationX != null && !isXUniform()) {
				locationX.bindExpression(symbolTable);
			}
			if (locationY != null && !isYUniform()) {
				locationY.bindExpression(symbolTable);
			}
			if (locationZ != null && !isZUniform()) {
				locationZ.bindExpression(symbolTable);
			}
		}	
	}
	
	private Variable var = null;
	private Expression diffExp = null;
	private ArrayList<ParticleInitialCondition> listOfParticleInitialConditions = new ArrayList<ParticleInitialCondition>();
	
	public ParticleProperties(Variable var, Expression diffExp, ArrayList<ParticleInitialCondition> initialConditions) {
		super();
		this.var = var;
		this.diffExp = diffExp;
		this.listOfParticleInitialConditions = initialConditions;
	}
	
	public ParticleProperties(MathDescription mathDesc, CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {
		super();
		readVCML(mathDesc, tokens);
	}
	
	private void readVCML(MathDescription mathDesc, CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {	
		String token = tokens.nextToken();
		String varname = token;
		var = mathDesc.getVariable(varname);
		
		token = tokens.nextToken();
		if (!token.equals(VCML.BeginBlock)){
			throw new MathFormatException("expecting "+VCML.BeginBlock+", found "+token);
		}
		while(true){
			token = tokens.nextToken();
			if (token.equals(VCML.EndBlock)) {
				break;
			}
			if (token.equals(VCML.ParticleDiffusion)) {
				diffExp = new Expression(tokens.readToSemicolon());
			} else if (token.equals(VCML.ParticleInitialCount) || token.equals(VCML.ParticleInitialCount_old)) {
				ParticleInitialConditionCount pic = new ParticleInitialConditionCount(tokens);
				listOfParticleInitialConditions.add(pic);
			} else if (token.equals(VCML.ParticleInitialConcentration)) {
				ParticleInitialConditionConcentration pic = new ParticleInitialConditionConcentration(tokens);
				listOfParticleInitialConditions.add(pic);
			} else {
				throw new MathFormatException("unexpected identifier "+token);
			}
		}
	}

	public Variable getVariable() {
		return var;
	}

	public String getVCML(int dimension) {		
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t" + VCML.ParticleProperties + " " + var.getName() + " " + VCML.BeginBlock +"\n");
		for (ParticleInitialCondition pic : listOfParticleInitialConditions){
			buffer.append("\t\t" + pic.getVCML(dimension)+"\n");
		}
		buffer.append("\t\t" + VCML.ParticleDiffusion + " " + (diffExp == null ?  "1.0" : diffExp.infix()) + ";\n");
		buffer.append("\t" + VCML.EndBlock + "\n");
		return buffer.toString();	
	}

	public final Expression getDiffusion() {
		return diffExp;
	}

	public final ArrayList<ParticleInitialCondition> getParticleInitialConditions() {
		return listOfParticleInitialConditions;
	}

	public boolean compareEqual(Matchable object) {
		if (!(object instanceof ParticleProperties)) {
			return false;
		}
		
		ParticleProperties pp = (ParticleProperties) object;
		if(!Compare.isEqual(var,pp.var)) {
			return false;
		}
		if(!Compare.isEqual(diffExp,pp.diffExp)) {
			return false;
		}
		if (listOfParticleInitialConditions.size() != pp.listOfParticleInitialConditions.size()) {
			return false;
		}
		for (ParticleInitialCondition pic : listOfParticleInitialConditions) {
			boolean bFound = false;
			for (ParticleInitialCondition pic2 : pp.listOfParticleInitialConditions) {
				if (Compare.isEqual(pic, pic2)) {
					bFound = true;
					break;
				}
				if (!bFound) {
					return false;
				}
			}
		}
				
		return true;
	}
	
	public void bind(SymbolTable symbolTable) throws ExpressionBindingException {
		diffExp.bindExpression(symbolTable);
		for (ParticleInitialCondition pic : listOfParticleInitialConditions) {
			pic.bind(symbolTable);
		}
	}
}
