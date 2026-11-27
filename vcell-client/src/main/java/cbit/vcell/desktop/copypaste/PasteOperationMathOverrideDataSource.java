package cbit.vcell.desktop.copypaste;

import cbit.vcell.math.Constant;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ConstantArraySpec;
import cbit.vcell.solver.MathOverrides;

public class PasteOperationMathOverrideDataSource implements PasteOperationDataSource<String> {
	private static final String[] columnNames = new String[]{"Parameter Name", "Type of Paste", "Current Value", "Proposed Value"};
	protected final MathOverrides mathOverrides;
	private final String pastedConstantName;
	private final Object currentValue;
	private final Object proposedValue;
	private final String valueTypeConversion;

	public PasteOperationMathOverrideDataSource(MathOverrides mathOverrides, String pastedConstantName, Object currentValue, Object proposedValue) {
		this.mathOverrides = mathOverrides;
		this.pastedConstantName = pastedConstantName;
		this.currentValue = currentValue;
		this.proposedValue = proposedValue;
		this.valueTypeConversion = String.format("%s -> %s", currentValue.getClass().getSimpleName(), proposedValue.getClass().getSimpleName());
	}

	@Override
	public PasteOperationTableModelRow<String> createTableModelRow() {
		return new PasteOperationTableModelRow<>(this.pastedConstantName, this.valueTypeConversion,
				PasteOperationMathOverrideDataSource.getStringRepresentation(this.currentValue),
				PasteOperationMathOverrideDataSource.getStringRepresentation(this.proposedValue)
		);
	}

	@Override
	public boolean isProposedDifferentThanOriginal() {
		if (!this.proposedValue.getClass().equals(this.currentValue.getClass())) return true;

		if (this.proposedValue instanceof Expression propExpr && this.currentValue instanceof Expression currExpr){
			return !propExpr.infix().equals(currExpr.infix());
		}
		return this.proposedValue.toString().equals(this.currentValue.toString());
	}

	@Override
	public void performChange() throws ExpressionException {
		if (this.proposedValue instanceof Expression expression) {
			this.mathOverrides.putConstant(new Constant(this.pastedConstantName, expression));
		} else if (this.proposedValue instanceof ConstantArraySpec constantArraySpec) {
			this.mathOverrides.putConstantArraySpec(constantArraySpec);
		} else {
			throw new IllegalStateException(String.format("VCell does not know how to store the new value's type: `%s`", this.proposedValue.getClass().getSimpleName()));
		}
	}

	@Override
	public String[] getColumnNames() {
		return PasteOperationMathOverrideDataSource.columnNames;
	}

	private static String getStringRepresentation(Object value){
		return value instanceof Expression expr ? expr.infix() : value.toString();
	}
}
