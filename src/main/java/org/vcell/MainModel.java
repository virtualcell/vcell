package org.vcell;

import net.imagej.Dataset;
import net.imagej.axis.AxisType;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevingaffney on 6/26/17.
 */
public class MainModel {

    private Project project;
    private ArrayList<ChangeListener> dataChangeListeners;
    private ArrayList<ChangeListener> displayChangeListeners;

    public MainModel() {
        this.dataChangeListeners = new ArrayList<>();
        this.displayChangeListeners = new ArrayList<>();
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
        notifyChangeListeners(dataChangeListeners);
    }

    public void setProjectTitle(String title) {
        this.project.setTitle(title);
        notifyChangeListeners(dataChangeListeners);
    }

    public void addData(Dataset data) {
        project.getData().add(data);
        notifyChangeListeners(dataChangeListeners);
    }

    public void addGeometry(Dataset geometry) {
        project.getGeometry().add(geometry);
        notifyChangeListeners(dataChangeListeners);
    }
    
    public void addModel(VCellModel model) {
    	project.getModels().add(model);
    	notifyChangeListeners(dataChangeListeners);
    }

    public void addResult(Dataset result) {
        project.getResults().add(result);
        notifyChangeListeners(dataChangeListeners);
    }

    public boolean delete(Dataset dataset) {
        boolean success = project.getData().remove(dataset)
                || project.getGeometry().remove(dataset)
                || project.getResults().remove(dataset);
        notifyChangeListeners(dataChangeListeners);
        return success;
    }
    
    public void changeAxes(Dataset dataset, List<AxisType> axisTypes) {
		if (axisTypes.size() != dataset.numDimensions()) {
			System.err.println("Number of dimensions do not match.");
			return;
		}
		for (int i = 0; i < axisTypes.size(); i++) {
			dataset.axis(i).setType(axisTypes.get(i));
		}
		notifyChangeListeners(displayChangeListeners);
    }

    public void addDataChangeListener(ChangeListener l) {
        this.dataChangeListeners.add(l);
    }
    
    public void addDisplayChangeListener(ChangeListener l) {
    	this.displayChangeListeners.add(l);
    }
    
    private void notifyChangeListeners(List<ChangeListener> changeListeners) {
        ChangeEvent changeEvent = new ChangeEvent(this);
        for (ChangeListener listener : changeListeners) {
            listener.stateChanged(changeEvent);
        }
    }
}
