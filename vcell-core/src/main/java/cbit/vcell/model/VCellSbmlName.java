package cbit.vcell.model;

import java.beans.PropertyVetoException;

public interface VCellSbmlName {
	public String getSbmlId();
	public void setSbmlId(String newString) throws PropertyVetoException;
	
	public String getSbmlName();
	public void setSbmlName(String newString) throws PropertyVetoException;
}
