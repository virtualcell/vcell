package cbit.vcell.mapping;

import org.vcell.util.Issue;

import cbit.vcell.mapping.SimContextTransformer.SimContextTransformation;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.ModelException;
import cbit.vcell.parser.ExpressionException;

public interface MathMapping {

	MathSymbolMapping getMathSymbolMapping() throws MappingException, MathException, MatrixException, ExpressionException, ModelException;

	MathDescription getMathDescription(MathMappingCallback callback) throws MappingException, MathException, MatrixException, ExpressionException, ModelException;

	MathDescription getMathDescription() throws MappingException, MathException, MatrixException, ExpressionException, ModelException;
	
	SimContextTransformation getTransformation();

	Issue[] getIssues();

}
