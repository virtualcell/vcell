package cbit.vcell.client.desktop.biomodel.pathway;

import java.awt.Point;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.PathwayEvent;
import org.vcell.pathway.PathwayListener;
import org.vcell.pathway.PathwayModel;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;

public class PathwayGraphModel extends GraphModel implements PathwayListener {
	private PathwayModel pathwayModel;

	public PathwayModel getPathwayModel() {
		return pathwayModel;
	}

	public void setPathwayModel(PathwayModel pathwayModel) {
		if (this.pathwayModel!=null){
			this.pathwayModel.removePathwayListener(this);
		}
		this.pathwayModel = pathwayModel;
		if (this.pathwayModel!=null){
			this.pathwayModel.addPathwayListener(this);
		}
		refreshAll();
	}

	@Override
	public void refreshAll() {
		if (pathwayModel == null){
			clearAllShapes();
			fireGraphChanged();
			return;
		}
		clearAllShapes();
		PathwayContainerShape pathwayContainerShape = new PathwayContainerShape(this,pathwayModel);
		for (BioPaxObject bpObject : pathwayModel.getBiopaxObjects()){
			BioPaxObjectShape bpObjectShape = new BioPaxObjectShape(bpObject, this);
			pathwayContainerShape.addChildShape(bpObjectShape);
		}
		addShape(pathwayContainerShape);
		fireGraphChanged();
	}

	public void pathwayChanged(PathwayEvent event) {
		refreshAll();
	}

}
