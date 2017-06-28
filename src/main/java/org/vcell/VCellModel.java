package org.vcell;

import net.imagej.Data;
import net.imagej.Dataset;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;

/**
 * Created by kevingaffney on 6/26/17.
 */
public class VCellModel {

    private VCellProject project;
    private ArrayList<ChangeListener> changeListeners;

    public VCellModel() {
        this.changeListeners = new ArrayList<>();
    }

    public VCellProject getVCellProject() {
        return project;
    }

    public void setVCellProject(VCellProject project) {
        this.project = project;
        notifyChangeListeners();
    }

    public void addData(Dataset data) {
        project.getExperimentalDatasets().add(data);
        notifyChangeListeners();
    }

    public void addGeometry(Dataset geometry) {
        project.getGeometryDefinitions().add(geometry);
        notifyChangeListeners();
    }

    public void addResult(Dataset result) {
        project.getvCellResults().add(result);
        notifyChangeListeners();
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
