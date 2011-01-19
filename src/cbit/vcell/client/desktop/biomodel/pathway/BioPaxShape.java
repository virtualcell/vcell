package cbit.vcell.client.desktop.biomodel.pathway;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Entity;
import org.vcell.pathway.PathwayModel;

import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.MutableVisualState;
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

	@Override
	public Point getSeparatorDeepCount() {	
		return new Point(0,0);
	}

	public BioPaxObject getBioPaxObject() {
		return bioPaxObject;
	}

	protected boolean hasPCLink(){
		return false;
	}
	
	@Override
	public void refreshLabel() {
		String name = "no-name";
		if (bioPaxObject instanceof Entity){
			ArrayList<String> names = ((Entity)bioPaxObject).getName();
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

	@Override
	public void resize(Graphics2D g, Dimension newSize) {
		return;
	}

	public void truncateLabelName(boolean bTruncate) {

		bTruncateLabelName = bTruncate;
	}

}