package cbit.vcell.desktop.copypaste;

import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;

public interface PasteOperationDataSource<T> {

	PasteOperationTableModelRow<T> createTableModelRow();

	boolean isProposedDifferentThanOriginal();

	void performChange() throws ExpressionBindingException, ExpressionException;

	String[] getColumnNames();
}
