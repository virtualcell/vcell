package cbit.vcell.desktop.copypaste;

import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import org.vcell.util.Compare;

public class PasteOperationScspDataSource implements PasteOperationDataSource<Expression> {
	private final SpeciesContext context;
	private final SpeciesContextSpecParameter targetContainer;
	private final Expression proposedChange;

	public PasteOperationScspDataSource(SpeciesContext context, SpeciesContextSpecParameter targetContainer, Expression proposedChange){
		this.context = context;
		this.targetContainer = targetContainer;
		this.proposedChange = proposedChange;
	}


	@Override
	public PasteOperationTableModelRow<Expression> createTableModelRow() {
		return new PasteOperationTableModelRow<>(
				this.context.getName(),
				this.targetContainer.getName(),
				this.targetContainer.getExpression(),
				this.proposedChange
		);
	}

	@Override
	public boolean isProposedChangeRedundant() {
		return Compare.isEqualOrNull(this.targetContainer.getExpression().infix(), this.proposedChange.infix());
	}

	@Override
	public void performChange() throws ExpressionBindingException {
		this.targetContainer.setExpression(this.proposedChange);
	}
}
