package org.vcell;

import net.imagej.Dataset;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;

/**
 * Created by kevingaffney on 6/26/17.
 */
public class MainModel {

    private Project project;
    private ArrayList<ChangeListener> changeListeners;

    public MainModel() {
        this.changeListeners = new ArrayList<>();
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
        notifyChangeListeners();
    }

    public void setProjectTitle(String title) {
        this.project.setTitle(title);
        notifyChangeListeners();
    }

    public void addData(Dataset data) {
        project.getData().add(data);
        notifyChangeListeners();
    }

    public void addGeometry(Dataset geometry) {
        project.getGeometry().add(geometry);
        notifyChangeListeners();
    }

    public void addResult(Dataset result) {
        project.getResults().add(result);
        notifyChangeListeners();
    }

    public boolean delete(Dataset dataset) {
        boolean success = project.getData().remove(dataset)
                || project.getGeometry().remove(dataset)
                || project.getResults().remove(dataset);
        notifyChangeListeners();
        return success;
    }

    public void addChangeListener(ChangeListener e) {
        this.changeListeners.add(e);
    }

    public void notifyChangeListeners() {
        ChangeEvent changeEvent = new ChangeEvent(this);
        for (ChangeListener listener : changeListeners) {
            listener.stateChanged(changeEvent);
        }
    }
}
