package cbit.vcell.client.data;

import java.util.ArrayList;
import java.util.List;

import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.solver.AnnotatedFunction;

public interface DataIdentifierFilter{
	boolean accept(String filterSetName, List<AnnotatedFunction> functions, DataIdentifier dataidentifier);
	ArrayList<DataIdentifier> accept(String filterSetName, List<AnnotatedFunction> functions, DataIdentifier[] dataidentifiers);
	String[] getFilterSetNames();
	String getDefaultFilterName();
	boolean isAcceptAll(String filterSetName);
	void setPostProcessingMode(boolean bPostProcessingMode);
	boolean isPostProcessingMode();
}