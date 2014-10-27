package cbit.vcell.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

public interface ModelProcess {

	String getName();

	boolean containsSearchText(String lowerCaseSearchText);

	Structure getStructure();
	
	ModelProcessDynamics getDynamics();

	void setName(String inputValue) throws PropertyVetoException;

	void setStructure(Structure s);

	void removePropertyChangeListener(PropertyChangeListener listener);

	void addPropertyChangeListener(PropertyChangeListener listener);

}
