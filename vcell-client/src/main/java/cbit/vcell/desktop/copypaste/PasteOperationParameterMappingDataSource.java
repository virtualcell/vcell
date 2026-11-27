package cbit.vcell.desktop.copypaste;

import cbit.vcell.modelopt.ParameterMappingSpec;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import org.vcell.util.Compare;

public class PasteOperationParameterMappingDataSource implements PasteOperationDataSource<Expression> {
	private static final String[] columnNames = new String[]{"Parameter Scope", "Parameter Name", "Current Value", "Proposed Value"};
	private final ParameterMappingSpec targetSpec;
	private final Expression expressionToApply;

	public PasteOperationParameterMappingDataSource(ParameterMappingSpec targetSpec, Expression expressionToApply) {
		this.targetSpec = targetSpec;
		this.expressionToApply = expressionToApply;
	}

	@Override
	public PasteOperationTableModelRow<Expression> createTableModelRow() {
		return new PasteOperationTableModelRow<>(
				this.targetSpec.getModelParameter().getNameScope().getName(),
				this.targetSpec.getModelParameter().getName(),
				new Expression(this.targetSpec.getCurrent()),
				this.expressionToApply
		);
	}

	@Override
	public boolean isProposedDifferentThanOriginal() {
		return !Compare.isEqualOrNull(new Expression(this.targetSpec.getCurrent()), this.expressionToApply);
	}

	@Override
	public void performChange() throws ExpressionException {
		this.targetSpec.setCurrent(this.expressionToApply.evaluateConstant());
	}

	@Override
	public String[] getColumnNames() {
		return PasteOperationParameterMappingDataSource.columnNames;
	}
}
