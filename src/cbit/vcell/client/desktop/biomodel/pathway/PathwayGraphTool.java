package cbit.vcell.client.desktop.biomodel.pathway;

import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.vcell.graph.BioCartoonTool;

public class PathwayGraphTool extends BioCartoonTool {
	
	private PathwayGraphModel pathwayGraphModel;

	public void setPathwayGraphModel(PathwayGraphModel pathwayGraphModel) {
		this.pathwayGraphModel = pathwayGraphModel;
	}

	@Override
	public GraphModel getGraphModel() {
		return pathwayGraphModel;
	}

	@Override
	protected void menuAction(Shape shape, String menuAction) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shapeHasMenuAction(Shape shape, String menuAction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shapeHasMenuActionEnabled(Shape shape, String menuAction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateMode(Mode newMode) {
		// TODO Auto-generated method stub

	}

}
