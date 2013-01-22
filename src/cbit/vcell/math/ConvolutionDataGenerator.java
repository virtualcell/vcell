package cbit.vcell.math;

import java.io.Serializable;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class ConvolutionDataGenerator extends DataGenerator {
	public abstract static class ConvolutionDataGeneratorKernel implements Matchable, Serializable {
		abstract void bind(SymbolTable symbolTable) throws ExpressionBindingException;
		abstract String getVCML();
	}
	public static class GaussianConvolutionDataGeneratorKernel extends ConvolutionDataGeneratorKernel {
		private Expression sigmaXY_um;
		private Expression sigmaZ_um; 
		
		public GaussianConvolutionDataGeneratorKernel(Expression sigmaXY_um, Expression sigmaZ_um) {
			this.sigmaXY_um = sigmaXY_um;
			this.sigmaZ_um = sigmaZ_um;
		}
		public GaussianConvolutionDataGeneratorKernel(CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {
			read(tokens);
		}
		private void read(CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {
			String token = tokens.nextToken();
			if (!token.equalsIgnoreCase(VCML.BeginBlock)){
				throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
			}
			while (tokens.hasMoreTokens()){
				token = tokens.nextToken();
				if (token.equalsIgnoreCase(VCML.EndBlock)){
					break;
				}
				if (token.equalsIgnoreCase(VCML.KernelGaussianSigmaXY)) {
					sigmaXY_um = new Expression(tokens.readToSemicolon());			
				} else if (token.equalsIgnoreCase(VCML.KernelGaussianSigmaZ)) {
					sigmaZ_um = new Expression(tokens.readToSemicolon());				
				} else {
					throw new MathFormatException("unexpected identifier "+token);
				}

			}	
		}
		public void bind(SymbolTable symbolTable) throws ExpressionBindingException {
			sigmaXY_um.bindExpression(symbolTable);
			sigmaZ_um.bindExpression(symbolTable);
		}
		public boolean compareEqual(Matchable obj) {
			if (!(obj instanceof GaussianConvolutionDataGeneratorKernel)) {
				return false;
			}
			GaussianConvolutionDataGeneratorKernel gck = (GaussianConvolutionDataGeneratorKernel)obj;
			if (!Compare.isEqualOrNull(sigmaXY_um, gck.sigmaXY_um)) {
				return false;
			}
			if (!Compare.isEqualOrNull(sigmaZ_um, gck.sigmaZ_um)) {
				return false;
			}
			return true;
		}
		public final Expression getSigmaXY_um() {
			return sigmaXY_um;
		}
		public final Expression getSigmaZ_um() {
			return sigmaZ_um;
		}
		@Override
		String getVCML() {
			return VCML.Kernel + "  " + VCML.KernelGaussian + " " + VCML.BeginBlock + "\n"
					+ "\t\t\t" + VCML.KernelGaussianSigmaXY + " " + sigmaXY_um.infix() + ";\n"
					+ "\t\t\t" + VCML.KernelGaussianSigmaZ + " " + sigmaZ_um.infix() + ";\n"
					+ "\t\t" + VCML.EndBlock + "\n";
		}
	}
	
	private ConvolutionDataGeneratorKernel kernel;
	private Expression volFunction;
	private Expression memFunction;
	public ConvolutionDataGenerator(String name, ConvolutionDataGeneratorKernel kernel, Expression volFunc, Expression memFunc) {
		super(name, null);
		this.kernel = kernel;
		if (volFunc == null || memFunc == null){
			throw new RuntimeException("both volume and membrane functions must be defined for ConvolutionDataGenerator");
		}
		volFunction = volFunc;
		memFunction = memFunc;
	}
	public ConvolutionDataGenerator(String argName, CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {
		super(argName, null);
		read(tokens);
	}
	private void read(CommentStringTokenizer tokens) throws MathFormatException, ExpressionException {
		String token = tokens.nextToken();
		if (!token.equalsIgnoreCase(VCML.BeginBlock)){
			throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
		}
		while (tokens.hasMoreTokens()){
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)){
				break;
			}
			if (token.equalsIgnoreCase(VCML.Kernel)) {
				token = tokens.nextToken();
				if (token.equalsIgnoreCase(VCML.KernelGaussian)) {
					kernel = new GaussianConvolutionDataGeneratorKernel(tokens); 
				}				
			} else if (token.equalsIgnoreCase(VCML.Function)||token.equalsIgnoreCase(VCML.VolFunction)) {
				volFunction = new Expression(tokens.readToSemicolon());
			} else if (token.equalsIgnoreCase(VCML.MemFunction)) {
				memFunction = new Expression(tokens.readToSemicolon());
			} 
			else {
				throw new MathFormatException("unexpected identifier "+token);
			}

		}	
	}

	@Override
	public void bind(SymbolTable symbolTable) throws ExpressionBindingException {
		kernel.bind(symbolTable);
		if (volFunction!=null){
			volFunction.bindExpression(symbolTable);
		}
		if (memFunction!=null){
			memFunction.bindExpression(symbolTable);
		}
	}
	
	public Expression getVolFunction() {
		return volFunction;
	}

	public Expression getMemFunction() {
		return memFunction;
	}
	
	public ConvolutionDataGeneratorKernel getKernel() {
		return kernel;
	}

	@Override
	public boolean compareEqual(Matchable object, boolean bIgnoreMissingDomain) {
		if (!(object instanceof ConvolutionDataGenerator)){
			return false;
		}
		if (!compareEqual0(object,bIgnoreMissingDomain)){
			return false;
		}
		ConvolutionDataGenerator cdg = (ConvolutionDataGenerator)object;
		if (!Compare.isEqualOrNull(kernel, cdg.kernel)){
			return false;
		}
		if (!Compare.isEqualOrNull(volFunction, cdg.volFunction)){
			return false;
		}
		if (!Compare.isEqualOrNull(memFunction, cdg.memFunction)){
			return false;
		}
		return true;
	}

	@Override
	public String getVCML() throws MathException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(VCML.ConvolutionDataGenerator + "  " + getQualifiedName() + " " + VCML.BeginBlock + "\n");
		buffer.append("\t\t" + kernel.getVCML() + "\n");
		buffer.append("\t\t" + VCML.VolFunction + "\t" + volFunction.infix()+";\n");
		buffer.append("\t\t" + VCML.MemFunction + "\t" + memFunction.infix()+";\n");
		buffer.append("\t" + VCML.EndBlock + "\n");
		return buffer.toString();
	}

}
