package cbit.vcell.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import org.vcell.util.Displayable;
import org.vcell.util.Matchable;

public interface ModelProcess extends BioModelEntityObject, Displayable, Matchable {

	String getName();

	boolean containsSearchText(String lowerCaseSearchText);

	Structure getStructure();
	
	ModelProcessDynamics getDynamics();
	
	Integer getNumParticipants();
	
	void setName(String inputValue) throws PropertyVetoException;

	void setStructure(Structure s);

	void removePropertyChangeListener(PropertyChangeListener listener);

	void addPropertyChangeListener(PropertyChangeListener listener);

}
