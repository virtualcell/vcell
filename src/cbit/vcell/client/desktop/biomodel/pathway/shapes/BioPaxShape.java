package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Entity;
import org.vcell.pathway.PathwayModel;

import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.MutableVisualState;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;
import cbit.vcell.model.InUseException;

public abstract class BioPaxShape extends Shape {
	BioPaxObject bioPaxObject = null;
	private static final int SCS_LABEL_WIDTHPARM = 3;
	private static final String SCS_LABEL_TRUCATED = "...";
	protected String smallLabel = null;
	protected Dimension smallLabelSize = new Dimension();
	protected Point smallLabelPos = new Point(0,0);

	private boolean bTruncateLabelName = true;

	public BioPaxShape(BioPaxObject bioPaxObject, GraphModel graphModel) {
		super(graphModel);
		this.bioPaxObject = bioPaxObject;
		defaultBG = java.awt.Color.pink;
		defaultFGselect = java.awt.Color.black;
		backgroundColor = defaultBG;
		backgroundColor.darker().darker();
	}

	@Override
	public VisualState createVisualState() { 
		return new MutableVisualState(this, VisualState.PaintLayer.NODE); 
	}

	protected void delete() throws Exception, InUseException {
		PathwayModel pathwayModel = ((PathwayGraphModel)graphModel).getPathwayModel();
		pathwayModel.getBiopaxObjects().remove(getBioPaxObject());
	}

	@Override
	public Object getModelObject() {
		return bioPaxObject;
	}

	public BioPaxObject getBioPaxObject() {
		return bioPaxObject;
	}

	protected boolean hasPCLink(){
		return false;
	}
	
	@Override
	public void refreshLabel() {
		String name = "[" + bioPaxObject.getID() + "]";
		if (bioPaxObject instanceof Entity){
			Entity entity = (Entity)bioPaxObject;
			ArrayList<String> names = entity.getName();
			if (names.size()>0){
				name = names.get(0);
			} 
		}
		setLabel(name);

		smallLabel = getLabel();
		if(bTruncateLabelName && getLabel().length() > (2*SCS_LABEL_WIDTHPARM + SCS_LABEL_TRUCATED.length())){
			smallLabel =
				getLabel().substring(0,SCS_LABEL_WIDTHPARM)+
				SCS_LABEL_TRUCATED+
				getLabel().substring(getLabel().length()-SCS_LABEL_WIDTHPARM);
		}
	}

	public void truncateLabelName(boolean bTruncate) {

		bTruncateLabelName = bTruncate;
	}

}