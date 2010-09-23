package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.gui.graph.*;
import cbit.gui.graph.Shape;
import cbit.vcell.model.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class FeatureShape extends StructureShape {

	private Area currentOval;

	public FeatureShape (Feature feature, Model model, GraphModel graphModel) {
		super(feature, model, graphModel);
	}

	private Dimension calculateBox(int scsCount,int childSCMaxWidth,int childSCMaxHeight) {

		if(scsCount == 0){
			return new Dimension(0,0);
		}
		int squareBoxX = (int)Math.ceil(Math.sqrt(scsCount));
		int squareBoxY = squareBoxX - (int)(((squareBoxX*squareBoxX)%scsCount)/(double)squareBoxX);
		int width = (squareBoxX * (childSCMaxWidth+ 0/*defaultSpacingX*/));// + defaultSpacingX;
		int height = (squareBoxY * (childSCMaxHeight+ 30/*defaultSpacingY*/));// + defaultSpacingY;
		return new Dimension(width,height);
	}

	@Override
	public Point getAttachmentLocation(int attachmentType) {
		return new Point(getSpaceManager().getSize().width/2, getLabelSize().height*5/4);
	}

	public Feature getFeature() {
		return (Feature)getStructure();
	}

	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		return getPreferedSize_Testing(g);
	}

	public Dimension getPreferedSize_Testing(Graphics2D g) {

		Font origfont = g.getFont();
		g.setFont(getLabelFont(g));
		try{
			FontMetrics fm = g.getFontMetrics();
			setLabelSize(fm.stringWidth(getLabel()), fm.getMaxAscent() + fm.getMaxDescent());
			labelPos.x = getSpaceManager().getSize().width/2 - fm.stringWidth(getLabel())/2; 
			labelPos.y = 5 + fm.getMaxAscent();

			if (childShapeList.size() > 0){
				int structCount = 0;
				int childStructureTotalHeight = getLabelSize().height;
				int childStructureMaxWidth = getLabelSize().width;
				for (int i=0;i<childShapeList.size();i++){
					Shape child = childShapeList.get(i);
					if(child instanceof StructureShape){
						structCount+= 1;
						Dimension childDim = child.getPreferedSize(g);
						childStructureTotalHeight += childDim.height + defaultSpacingY;
						childStructureMaxWidth = Math.max(childStructureMaxWidth,childDim.width);
					}
				}
				int scsCount = 0;
				//int childSCTotalHeight = 0;
				int childSCMaxWidth = 0;
				int childSCMaxHeight = 0;
				for (int i=0;i<childShapeList.size();i++){
					Shape child = childShapeList.get(i);
					if(child instanceof SpeciesContextShape){
						scsCount+= 1;
						Dimension childDim = child.getPreferedSize(g);
						childSCMaxHeight = Math.max(childSCMaxHeight,childDim.height +((SpeciesContextShape)child).smallLabelSize.height);
						//childSCTotalHeight += childDim.height +((SpeciesContextShape)child).smallLabelSize.height;
						childSCMaxWidth = Math.max(childSCMaxWidth,childDim.width);
						childSCMaxWidth = Math.max(childSCMaxWidth,((SpeciesContextShape)child).smallLabelSize.width);
					}
				}

				Dimension box = calculateBox(scsCount,childSCMaxWidth,childSCMaxHeight);
				if(structCount == 0) {
					getSpaceManager().setSizePreferred((box.width + defaultSpacingX*2), (box.height + defaultSpacingY*2));
				}else{
					getSpaceManager().setSizePreferred((childStructureMaxWidth + box.width + defaultSpacingX*2), (Math.max(childStructureTotalHeight,box.height) + defaultSpacingY*2));
				}
			} else {
				getSpaceManager().setSizePreferred((getLabelSize().width + defaultSpacingX*2), 
						(getLabelSize().height + defaultSpacingY*2));
			}	
			return getSpaceManager().getSizePreferred();
		} finally {
			g.setFont(origfont);
		}
	}

	@Override
	public Point getSeparatorDeepCount() {
		int selfCountX = 1;
		if (countChildren()>0){
			selfCountX++;
		}		
		int selfCountY = 1;
		// column1 is speciesContextShapes
		Point column1 = new Point();
		int scShapeCount = 0;
		for (int i=0;i<countChildren();i++){
			Shape child = childShapeList.get(i);
			if (child instanceof SpeciesContextShape){
				scShapeCount++;
			}
		}	
		column1.x = (scShapeCount>0)?1:0;
		column1.y = (scShapeCount>0)?scShapeCount/2:0;	
		// column2 is StructureShapes
		Point column2 = new Point();
		for (int i=0;i<countChildren();i++){
			Shape child = childShapeList.get(i);
			if (child instanceof StructureShape){
				Point childCount = child.getSeparatorDeepCount();
				column2.x += childCount.x;
				column2.y += childCount.y;
			}	
		}
		return new Point(selfCountX+column1.x+column2.x,selfCountY+Math.max(column1.y,column2.y));
	}

	@Override
	public void refreshLayout() throws LayoutException {
		// calculate total height and max width of speciesContext (column1)
		int scCount = 0;
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			if (child instanceof SpeciesContextShape){
				scCount++;
			}	
		}
		// calculate total height and max width of Membranes (column2)
		int memCount = 0;
		int memHeight = 0;
		int memWidth = 0;
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			if (child instanceof StructureShape){
				memHeight += child.getSpaceManager().getSize().height;
				memWidth = Math.max(memWidth, child.getSpaceManager().getSize().width);
				memCount++;
			}	
		}
		// position label
		int centerX = getSpaceManager().getSize().width/2;
		int currentY = getLabelSize().height;
		labelPos.x = centerX - getLabelSize().width/2; 
		labelPos.y = currentY;
		currentY += getLabelSize().height;
		int totalSpacingY;
		int spacingY;
		int extraSpacingY;
		int currentX = getSpaceManager().getSize().width/2 - memWidth/2;
		int spacingX = defaultSpacingX;
		// position column2 (membranes)
		totalSpacingY = (getSpaceManager().getSize().height - currentY) - memHeight;
		if(LayoutException.bActivated) {
			if (totalSpacingY<0){
				throw new LayoutException("unable to fit children within container");
			}				
		}
		spacingY = totalSpacingY/(memCount+1);
		extraSpacingY = totalSpacingY%(memCount+1);
		centerX = currentX + memWidth/2;
		// position children (and label)
		int col2Y = currentY + spacingY + extraSpacingY;
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			if (child instanceof StructureShape){
				child.getSpaceManager().setRelPos(centerX - child.getSpaceManager().getSize().width/2, col2Y);
				col2Y += child.getSpaceManager().getSize().height + spacingY;
			}	
		}
		if (col2Y != getSpaceManager().getSize().height){
			throw new RuntimeException("layout for column2 incorrect ("+getLabel()+"), currentY="+currentY+", screenSize.height="+getSpaceManager().getSize().height);
		}
		if (memCount>0){
			currentX += spacingX + memWidth;
		}	
		if(scCount > 0){
			//The following code attempts to position SpeciesContextShapes so that their
			//ovals and labels do not overlap other structures
			//Calculate Mask of valid area where SpeciesContextShape may render
			currentOval = new Area(new Ellipse2D.Double(
					getSpaceManager().getRelX(), getSpaceManager().getRelY(),
					getSpaceManager().getSize().width, getSpaceManager().getSize().height));
			for (int i=0;i<childShapeList.size();i++){
				Shape child = childShapeList.get(i);
				if (child instanceof StructureShape){
					Area childArea =
						new Area(new Ellipse2D.Double(
								child.getSpaceManager().getRelX() + getSpaceManager().getRelX(),
								child.getSpaceManager().getRelY() + getSpaceManager().getRelY(),
								child.getSpaceManager().getSize().width, child.getSpaceManager().getSize().height));
					currentOval.subtract(childArea);
				}	
			}
			//
			//Position SpeciesContextShapes so they and their labels don't overlap other structures.
			//If fail, try a few more times with reduced spacing between SpeciesContextShapes to see
			//if we can fit
			//	
			final int TOPOFFSET = 20;
			boolean bLayoutFailed = false;
			int layoutRetryCount = 0;
			final int MAX_LAYOUT_RETRY = 3;

			int boxSpacingX = 50;
			final int X_RETRY_SPACE_REDUCTION = 10;
			int boxSpacingY = 30;
			final int Y_RETRY_SPACE_REDUCTION = 5;

			final int XEND = currentOval.getBounds().x + currentOval.getBounds().width;
			final int YEND = currentOval.getBounds().y + currentOval.getBounds().height;
			while(true) {
				bLayoutFailed = false;
				int xCount = 0;
				int boxX = 0;
				int boxY = 0;
				for (int i=0;i<childShapeList.size();i++){
					if (childShapeList.get(i) instanceof SpeciesContextShape){
						SpeciesContextShape child = (SpeciesContextShape) childShapeList.get(i);
						final int XSTEP = child.getSpaceManager().getSize().width + boxSpacingX;
						final int YSTEP = child.getSpaceManager().getSize().height + boxSpacingY;
						while ((layoutRetryCount <= MAX_LAYOUT_RETRY
						// If last try, don't check labels for overlap
								&& (boxY <= TOPOFFSET// away from structure label
								|| !currentOval.contains(
										// SCS label not overlap other structures
										boxX + child.smallLabelPos.x
											+ currentOval.getBounds().getX(), boxY
											+ child.smallLabelPos.y
											+ currentOval.getBounds().getY()
											- child.smallLabelSize.height,
										child.smallLabelSize.width,
										child.smallLabelSize.height)))
								|| !currentOval.contains(
										// SCS oval not overlap other structures
										boxX + currentOval.getBounds().getX(),
										boxY + currentOval.getBounds().getY(),
										SpeciesContextShape.DIAMETER,
										SpeciesContextShape.DIAMETER)) {
							if (boxY < YEND) {
								boxY += YSTEP;
							} else {
								boxY = (xCount % 2 == 0 ? TOPOFFSET : TOPOFFSET
										+ SpeciesContextShape.DIAMETER);
								boxX += XSTEP;
								xCount += 1;
							}
							if (boxX > XEND && boxY > YEND) {
								bLayoutFailed = true;
								break;
							}
						}
						if (!bLayoutFailed) {
							child.getSpaceManager().setRelPos(boxX, boxY);
							boxY += YSTEP;
						} else {
							break;
						}
					}
				}
				if(bLayoutFailed && layoutRetryCount <= MAX_LAYOUT_RETRY){
					boxSpacingX-=X_RETRY_SPACE_REDUCTION;
					boxSpacingY-=Y_RETRY_SPACE_REDUCTION;
					layoutRetryCount+= 1;
				}else{
					break;
				}
			}
		}

	}

}