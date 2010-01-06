package cbit.vcell.microscopy.batchrun;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import org.vcell.util.Compare;

import cbit.vcell.microscopy.FRAPStudy;

public class FRAPBatchRunWorkspace 
{
	private ArrayList<FRAPStudy> frapStudyList = null;
	private PropertyChangeSupport propertyChangeSupport;
	
	public FRAPBatchRunWorkspace()
	{
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public  ArrayList<FRAPStudy> getFrapStudyList() {
		return frapStudyList;
	}
	
	public FRAPStudy getFRAPStudy(String fStudyName)
	{
		for(int i=0; i<frapStudyList.size(); i++)
		{
			if(Compare.isEqual(frapStudyList.get(i).getName(), fStudyName))
			{
				return frapStudyList.get(i);
			}
		}
		return null;
	}
	
	public void addFrapStudy(FRAPStudy newFrapStudy)
	{
		frapStudyList.add(newFrapStudy);
	}
}
