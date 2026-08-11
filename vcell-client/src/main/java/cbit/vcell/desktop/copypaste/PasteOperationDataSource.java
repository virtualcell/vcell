package cbit.vcell.desktop.copypaste;

import cbit.vcell.parser.ExpressionBindingException;

public interface PasteOperationDataSource<T> {

	PasteOperationTableModelRow<T> createTableModelRow();

	boolean isProposedChangeRedundant();

	void performChange() throws ExpressionBindingException;
}
