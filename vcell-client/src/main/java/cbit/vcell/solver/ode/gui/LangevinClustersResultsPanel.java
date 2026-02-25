package cbit.vcell.solver.ode.gui;

import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;

public class LangevinClustersResultsPanel extends DocumentEditorSubPanel {

    private final ODEDataViewer owner;

    public LangevinClustersResultsPanel(ODEDataViewer odeDataViewer) {
        this.owner = odeDataViewer;
    }


    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {

    }
}
