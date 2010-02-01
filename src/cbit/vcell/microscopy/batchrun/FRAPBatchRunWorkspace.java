package cbit.vcell.microscopy.batchrun;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;

import org.vcell.util.Compare;

import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;

public class FRAPBatchRunWorkspace extends FRAPWorkspace
{
	//Properties that are used in VFRAP Batch Run
	public static final String PROPERTY_CHANGE_BATCHRUN_DISPLAY_IMG = "BATCHRUN_DISPLAY_IMG";
	public static final String PROPERTY_CHANGE_BATCHRUN_DISPLAY_PARAM = "BATCHRUN_DISPLAY_PARAM";
	
	private ArrayList<FRAPStudy> frapStudyList = null;
	private PropertyChangeSupport propertyChangeSupport;
	private FRAPSingleWorkspace workingSingleWorkspace = null;
	private Object displaySelection = null;
	
	public FRAPBatchRunWorkspace() 
	{
		//initialize the working workspace
		workingSingleWorkspace = new FRAPSingleWorkspace();
		FRAPStudy fStudy = new FRAPStudy();
		frapStudyList = new ArrayList<FRAPStudy>();
		workingSingleWorkspace.setFrapStudy(fStudy, true);
		
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener p) {
    	propertyChangeSupport.addPropertyChangeListener(p);
    }
  
    public void removePropertyChangeListener(PropertyChangeListener p) {
    	propertyChangeSupport.removePropertyChangeListener(p);
    }
    
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    	propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
	public FRAPSingleWorkspace getWorkingSingleWorkspace() {
		return workingSingleWorkspace;
	}
	
	public  ArrayList<FRAPStudy> getFrapStudyList() {
		return frapStudyList;
	}
	
	public FRAPStudy getFRAPStudy(String fileName) //file name(with full path info.)
	{
		for(int i=0; i<frapStudyList.size(); i++)
		{
			if(Compare.isEqual(frapStudyList.get(i).getXmlFilename(), fileName))
			{
				return frapStudyList.get(i);
			}
		}
		return null;
	}
	
	public void clearWorkingSingleWorkspace()
	{
		FRAPStudy newFrapStudy = new FRAPStudy();
		getWorkingSingleWorkspace().setFrapStudy(newFrapStudy, true, true);
		String oldString = "Before clear all";
		String newString = "After clear all";
		firePropertyChange(PROPERTY_CHANGE_BATCHRUN_DISPLAY_IMG, oldString, newString);
	}
	
	public void setWorkingFRAPStudy(FRAPStudy fStudy)
	{
		getWorkingSingleWorkspace().setFrapStudy(fStudy, true, true);
	}
	
	public void addFrapStudy(FRAPStudy newFrapStudy)
	{
		frapStudyList.add(newFrapStudy);
	}
	
	public FRAPStudy getWorkingFrapStudy() //the working FRAPStudy
	{
		return getWorkingSingleWorkspace().getWorkingFrapStudy();
	}
	
	public void updateDisplayForTreeSelection(Object selection)
	{
		String oldString = null;
		if (displaySelection instanceof File)
		{
			oldString =  ((File)displaySelection).getAbsolutePath();
		}
		else if(displaySelection instanceof String)
		{
			oldString = ((String)displaySelection);
		}
		displaySelection = selection;
		if(selection instanceof File)
		{
			String newString = ((File)selection).getAbsolutePath();
			FRAPStudy selectedFrapStudy = getFRAPStudy(newString);
			setWorkingFRAPStudy(selectedFrapStudy);
			firePropertyChange(PROPERTY_CHANGE_BATCHRUN_DISPLAY_IMG, oldString, newString);
		}
		else if(selection instanceof String)
		{
			firePropertyChange(PROPERTY_CHANGE_BATCHRUN_DISPLAY_PARAM, oldString, ((String)selection));
		}
	}
}
