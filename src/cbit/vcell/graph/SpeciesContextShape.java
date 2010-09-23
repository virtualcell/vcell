package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.GraphModelPreferences;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.MutableVisualState;
import cbit.vcell.biomodel.meta.MiriamManager;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.model.InUseException;
import cbit.vcell.model.Model;
import cbit.vcell.model.SpeciesContext;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Map;

import org.vcell.sybil.models.miriam.MIRIAMQualifier;

public class SpeciesContextShape extends ElipseShape {
	SpeciesContext speciesContext = null;
	private static final int RADIUS = 8;
	public static final int DIAMETER = 2*RADIUS;
	private static final int SMALL_DIAMETER = DIAMETER-1;
	private Color darkerBackground = null;

	private static final int SCS_LABEL_WIDTHPARM = 3;
	private static final String SCS_LABEL_TRUCATED = "...";
	private String smallLabel = null;
	protected Dimension smallLabelSize = new Dimension();
	protected Point smallLabelPos = new Point(0,0);

	private boolean bTruncateLabelName = true;

	public SpeciesContextShape(SpeciesContext speciesContext, GraphModel graphModel) {
		super(graphModel);
		this.speciesContext = speciesContext;
		defaultBG = java.awt.Color.green;
		defaultFGselect = java.awt.Color.black;
		backgroundColor = defaultBG;
		darkerBackground = backgroundColor.darker().darker();
	}

	@Override
	public VisualState createVisualState() { 
		return new MutableVisualState(this, VisualState.PaintLayer.NODE); 
	}

	protected void delete() throws Exception, InUseException {
		Model model = ((ModelCartoon)graphModel).getModel();
		model.removeSpeciesContext(getSpeciesContext());
	}

	@Override
	public Object getModelObject() {
		return speciesContext;
	}

	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();
		setLabelSize(fm.stringWidth(getLabel()), fm.getMaxAscent() + fm.getMaxDescent());
		smallLabelSize.width = (smallLabel != null ? fm.stringWidth(smallLabel) : getLabelSize().width);
		smallLabelSize.height = getLabelSize().height;
		getSpaceManager().setSizePreferred(DIAMETER, DIAMETER);
		return getSpaceManager().getSizePreferred();
	}

	@Override
	public Point getSeparatorDeepCount() {	
		return new Point(0,0);
	}

	public SpeciesContext getSpeciesContext() {
		return speciesContext;
	}

	@Override
	public void refreshLayout() {
		int centerX = getSpaceManager().getSize().width/2;
		labelPos.x = centerX - getLabelSize().width/2; 
		labelPos.y = 0;
		smallLabelPos.x = centerX - smallLabelSize.width/2;
		smallLabelPos.y = getLabelPos().y;
	}

	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY ) {
		boolean isBound = false;
		SpeciesContext sc = (SpeciesContext)getModelObject();
		boolean bHasPCLink = false;
		if (graphModel instanceof ModelCartoon) {
			ModelCartoon mc = (ModelCartoon)graphModel;
			// check if species has Pathway Commons link by querying VCMetadata : if it does, need to change color of speciesContext.
			try {
				MiriamManager miriamManager = mc.getModel().getVcMetaData().getMiriamManager();
				Map<MiriamRefGroup,MIRIAMQualifier> miriamRefGroups = miriamManager.getAllMiriamRefGroups(sc.getSpecies());
				if (miriamRefGroups!=null && miriamRefGroups.size()>0){
					bHasPCLink = true;
				}
			}catch (Exception e){
				e.printStackTrace(System.out);
			}
		}
		if(sc.getSpecies().getDBSpecies() != null || bHasPCLink){
			isBound = true;
		}
		// draw elipse
		g.setColor((!isBound && !isSelected()?darkerBackground:backgroundColor));
		g.fillOval(absPosX + 1, absPosY + 1 + getLabelPos().y, SMALL_DIAMETER, SMALL_DIAMETER);
		g.setColor(forgroundColor);
		g.drawOval(absPosX, absPosY + getLabelPos().y, DIAMETER, DIAMETER);
		// draw label
		g.setColor(forgroundColor);
		if (getLabel()!=null && getLabel().length()>0){
			if(isSelected()){//clear background and outline to make selected label stand out
				drawRaisedOutline(
						getLabelPos().x + absPosX - 5, 
						getLabelPos().y + absPosY - getLabelSize().height + 3,
						getLabelSize().width + 10, getLabelSize().height, g, 
						Color.white, forgroundColor, Color.gray);
			}
			g.setColor(forgroundColor);
			g.drawString(
					(isSelected() || smallLabel == null ? getLabel():smallLabel),
					(isSelected() || smallLabel == null ? getLabelPos().x : smallLabelPos.x) + 
					absPosX, getLabelPos().y + absPosY);
		}
	}

	@Override
	public void refreshLabel() {
		switch (GraphModelPreferences.getInstance().getSpeciesContextDisplayName()) {
		case GraphModelPreferences.DISPLAY_COMMON_NAME: {
			setLabel(getSpeciesContext().getSpecies().getCommonName());
			break;
		}
		case GraphModelPreferences.DISPLAY_CONTEXT_NAME: {
			setLabel(getSpeciesContext().getName());
			break;
		}
		}

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