package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.Entity;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.ShapeSpaceManager;

public class BioPaxConversionShape extends BioPaxShape {
	private static final int RADIUS = 5;
	public static final int DIAMETER = 2*RADIUS;
	private Color darkerBackground = null;
	private Area icon = null;

	private static final int SCS_LABEL_WIDTHPARM = 3;
	private static final String SCS_LABEL_TRUCATED = "...";
	private String smallLabel = null;
	protected Dimension smallLabelSize = new Dimension();
	protected Point smallLabelPos = new Point(0,0);

	private boolean bTruncateLabelName = true;

	public BioPaxConversionShape(Conversion conversion, GraphModel graphModel) {
		super(conversion, graphModel);
		defaultBG = Color.yellow;
		defaultFGselect = Color.black;
		backgroundColor = defaultBG;
		darkerBackground = backgroundColor.darker().darker();
	}
	
	public Conversion getConversion() {
		return (Conversion) getModelObject();
	}
	
	@Override
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();
		setLabelSize(fm.stringWidth(getLabel()), fm.getMaxAscent() + fm.getMaxDescent());
		smallLabelSize.width = (smallLabel != null ? fm.stringWidth(smallLabel) : getLabelSize().width);
		smallLabelSize.height = getLabelSize().height;
		getSpaceManager().setSizePreferred(DIAMETER, DIAMETER);
		return getSpaceManager().getSizePreferred();
	}

	@Override
	public void refreshLayoutSelf() {
		int centerX = getSpaceManager().getSize().width/2;
		labelPos.x = centerX - getLabelSize().width/2; 
		labelPos.y = 0;
		smallLabelPos.x = centerX - smallLabelSize.width/2;
		smallLabelPos.y = getLabelPos().y;
	}

	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY ) {
		int circleDiameter = DIAMETER;
		int shapeHeight = getSpaceManager().getSize().height;
		int shapeWidth = getSpaceManager().getSize().width;
		int offsetX = (shapeWidth-circleDiameter) / 2;
		int offsetY = (shapeHeight-circleDiameter) / 2;
		Graphics2D g2D = g;
		if (icon == null) {
			icon = new Area();
			icon.add(new Area(new Ellipse2D.Double(offsetX, offsetY,circleDiameter,circleDiameter)));
			//icon.add(new Area(new RoundRectangle2D.Double(offsetX, offsetY,circleDiameter,circleDiameter,circleDiameter/2,circleDiameter/2)));
		}
		Area movedIcon = icon.createTransformedArea(
			AffineTransform.getTranslateInstance(absPosX, absPosY));

		g.setColor((!hasPCLink() && !isSelected()?darkerBackground:backgroundColor));
		g2D.fill(movedIcon);
		g.setColor(forgroundColor);
		g2D.draw(movedIcon);
		
		// draw label
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

	public void truncateLabelName(boolean bTruncate) {

		bTruncateLabelName = bTruncate;
	}

	@Override
	public final boolean isInside(Point p) {
		if (getRadius(p) < 1.0) {
			return true;
		} else {
			return false;
		}
	}

	final double getRadius(Point pick) {
		ShapeSpaceManager spaceManager = getSpaceManager();
		int centerX = spaceManager.getRelX() + spaceManager.getSize().width / 2;
		int centerY = spaceManager.getRelY() + spaceManager.getSize().height / 2;
		double radiusX = pick.x - centerX;
		double radiusY = pick.y - centerY;
		double b = spaceManager.getSize().height / 2;
		double a = spaceManager.getSize().width / 2;
		double radius = radiusX * radiusX / (a * a) + radiusY * radiusY
				/ (b * b);

		return radius;
	}
	
}