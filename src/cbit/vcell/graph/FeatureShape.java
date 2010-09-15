package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.gui.graph.*;
import cbit.gui.graph.Shape;
import cbit.vcell.model.*;
import java.awt.*;

public class FeatureShape extends StructureShape {

	private java.awt.geom.Area currentOval;

	public FeatureShape (cbit.vcell.model.Feature feature, cbit.vcell.model.Model model, GraphModel graphModel) {
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


	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Point
	 */
	@Override
	public Point getAttachmentLocation(int attachmentType) {
		return new Point(shapeSize.width/2, labelSize.height*5/4);
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return cbit.vcell.model.Feature
	 */
	public Feature getFeature() {
		return (Feature)getStructure();
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 * @param g java.awt.Graphics
	 */
	@Override
	public Dimension getPreferedSize(java.awt.Graphics2D g) {

		return getPreferedSize_Testing(g);

		//java.awt.FontMetrics fm = g.getFontMetrics();
		//labelSize.height = fm.getMaxAscent() + fm.getMaxDescent();
		//labelSize.width = fm.stringWidth(getLabel());

		//labelPos.x = screenSize.width/2 - fm.stringWidth(getLabel())/2;
		//labelPos.y = 5 + fm.getMaxAscent();

		//if (childShapeList.size()>0){
		//int totalHeight = labelSize.height;
		//int maxWidth = labelSize.width;
		//for (int i=0;i<childShapeList.size();i++){
		//Shape child = (Shape)childShapeList.elementAt(i);
		//Dimension childDim = child.getPreferedSize(g);
		//totalHeight += childDim.height + defaultSpacingY;
		//maxWidth = Math.max(maxWidth,childDim.width);
		//}
		//preferedSize.width = maxWidth + defaultSpacingX*2;
		//preferedSize.height = totalHeight + defaultSpacingY*2;
		//}else{
		//preferedSize.width = labelSize.width + defaultSpacingX*2;
		//preferedSize.height = labelSize.height + defaultSpacingY*2;
		//}	
		//return preferedSize;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 * @param g java.awt.Graphics
	 */
	public Dimension getPreferedSize_Testing(Graphics2D g) {

		Font origfont = g.getFont();
		g.setFont(getLabelFont(g));
		try{
			java.awt.FontMetrics fm = g.getFontMetrics();
			labelSize.height = fm.getMaxAscent() + fm.getMaxDescent();
			labelSize.width = fm.stringWidth(getLabel());

			labelPos.x = shapeSize.width/2 - fm.stringWidth(getLabel())/2;
			labelPos.y = 5 + fm.getMaxAscent();

			if (childShapeList.size()>0){
				int structCount = 0;
				int childStructureTotalHeight = labelSize.height;
				int childStructureMaxWidth = labelSize.width;
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
				if(structCount == 0){
					preferredSize.width = box.width +defaultSpacingX*2;
					preferredSize.height = box.height+defaultSpacingY*2;
				}else{
					preferredSize.width = childStructureMaxWidth + box.width + defaultSpacingX*2;
					preferredSize.height = Math.max(childStructureTotalHeight,box.height) + defaultSpacingY*2;
				}
			}else{
				preferredSize.width = labelSize.width + defaultSpacingX*2;
				preferredSize.height = labelSize.height + defaultSpacingY*2;
			}	
			return preferredSize;
		}finally{
			g.setFont(origfont);
		}
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 */
	@Override
	public Point getSeparatorDeepCount() {
		int selfCountX = 1;
		if (countChildren()>0){
			selfCountX++;
		}		
		int selfCountY = 1;

		//
		// column1 is speciesContextShapes
		//
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

		//
		// column2 is StructureShapes
		//
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


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 * @param g java.awt.Graphics
	 */
	@Override
	public void refreshLayout() throws LayoutException {




		//////
		////// calculate total height and max width of speciesContext (column1)
		//////
		int scCount = 0;
		//int scHeight = 0;
		//int scWidth = 0;
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			if (child instanceof SpeciesContextShape){
				//scHeight += child.screenSize.height;
				//scWidth = Math.max(scWidth,child.screenSize.width);
				scCount++;
			}	
		}

		//
		// calculate total height and max width of Membranes (column2)
		//
		int memCount = 0;
		int memHeight = 0;
		int memWidth = 0;
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			if (child instanceof StructureShape){
				memHeight += child.shapeSize.height;
				memWidth = Math.max(memWidth,child.shapeSize.width);
				memCount++;
			}	
		}

		//
		// position label
		//
		int centerX = shapeSize.width/2;
		int currentY = labelSize.height;
		labelPos.x = centerX - labelSize.width/2;
		labelPos.y = currentY;
		currentY += labelSize.height;

		////
		//// find current centerX
		////
		//int numColumns=0;
		//if (memCount>0) numColumns++;
		//if (scCount>0) numColumns++;

		//int totalSpacingX = screenSize.width - (scWidth+memWidth);
		//if (totalSpacingX<0){
		//throw new LayoutException("unable to fit children within container (width)");
		//}	
		//int spacingX = totalSpacingX/(numColumns+2);
		//int extraSpacingX = totalSpacingX%(numColumns+2);
		//int currentX = 2*spacingX + extraSpacingX;

		//////
		////// position column1 (speciesContext)
		//////
		//int totalSpacingY = (screenSize.height-currentY) - scHeight;
		//if (totalSpacingY<0){
		//throw new LayoutException("unable to fit children within container (height)");
		//}		
		//int spacingY = totalSpacingY/(scCount+2);
		//int extraSpacingY = totalSpacingY%(scCount+2);
		//centerX = currentX + scWidth/2;
		////
		//// position children (and label)
		////
		//int col1Y = currentY + spacingY + extraSpacingY;
		//for (int i=0;i<childShapeList.size();i++){
		//Shape child = (Shape)childShapeList.elementAt(i);
		//if (child instanceof SpeciesContextShape){
		//Dimension childDim = child.screenSize;
		//child.screenPos.x = centerX - child.screenSize.width/2;
		//child.screenPos.y = col1Y;
		//col1Y += child.screenSize.height + spacingY;
		//}	
		//}
		//col1Y += spacingY;
		//if (col1Y != screenSize.height){
		//throw new RuntimeException("layout for column1 incorrect, currentY="+col1Y+", screenSize.height="+screenSize.height);
		//}	
		//if (scCount>0){
		//currentX += spacingX + scWidth;
		//}



		//
		//
		//
		int totalSpacingY;
		int spacingY;
		int extraSpacingY;
		int currentX = shapeSize.width/2 - memWidth/2;
		int spacingX = defaultSpacingX;
		//
		// position column2 (membranes)
		//
		totalSpacingY = (shapeSize.height-currentY) - memHeight;
		if(LayoutException.bActivated) {
			if (totalSpacingY<0){
				throw new LayoutException("unable to fit children within container");
			}				
		}
		spacingY = totalSpacingY/(memCount+1);
		extraSpacingY = totalSpacingY%(memCount+1);
		centerX = currentX + memWidth/2;
		//
		// position children (and label)
		//
		int col2Y = currentY + spacingY + extraSpacingY;
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			if (child instanceof StructureShape){
				// Dimension childDim = child.screenSize;
				child.getSpaceManager().setRelPos(centerX - child.shapeSize.width/2, col2Y);
				col2Y += child.shapeSize.height + spacingY;
			}	
		}
		if (col2Y != shapeSize.height){
			throw new RuntimeException("layout for column2 incorrect ("+getLabel()+"), currentY="+currentY+", screenSize.height="+shapeSize.height);
		}
		if (memCount>0){
			currentX += spacingX + memWidth;
		}	
		if (currentX != shapeSize.width){
			//throw new RuntimeException("layout for column widths incorrect ("+getLabel()+"), currentX="+currentX+", screenSize.width="+screenSize.width);
		}


		if(scCount > 0){
			//
			//The following code attempts to position SpeciesContextShapes so that their
			//ovals and labels do not overlap other structures
			//
			//Calculate Mask of valid area where SpeciesContextShape may render
			//
			currentOval = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(
					getSpaceManager().getRelX(), getSpaceManager().getRelY(),
					shapeSize.width,shapeSize.height));
			for (int i=0;i<childShapeList.size();i++){
				Shape child = childShapeList.get(i);
				if (child instanceof StructureShape){
					java.awt.geom.Area childArea =
						new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(
								child.getSpaceManager().getRelX() + getSpaceManager().getRelX(),
								child.getSpaceManager().getRelY() + getSpaceManager().getRelY(),
								child.shapeSize.width,child.shapeSize.height));
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
			//		java.awt.geom.Rectangle2D featureLabelBounds =
			//			new java.awt.geom.Rectangle2D.Double(labelPos.x-10,labelPos.y-20,labelSize.width+20,labelSize.height+40);
			while(true){
				bLayoutFailed = false;
				int xCount = 0;
				int boxX = 0;
				int boxY = 0;
				for (int i=0;i<childShapeList.size();i++){
					if (childShapeList.get(i) instanceof SpeciesContextShape){
						SpeciesContextShape child = (SpeciesContextShape) childShapeList.get(i);
						final int XSTEP = child.shapeSize.width + boxSpacingX;
						final int YSTEP = child.shapeSize.height + boxSpacingY;
						while(
								(layoutRetryCount <= MAX_LAYOUT_RETRY//If last try, don't check labels for overlap
										&&
										(
												boxY <= TOPOFFSET// away from structure label
												||
												!currentOval.contains(//SCS label not overlap other structures
														boxX+child.smallLabelPos.x+currentOval.getBounds().getX(),
														boxY+child.smallLabelPos.y+currentOval.getBounds().getY()-child.smallLabelSize.height,
														child.smallLabelSize.width,child.smallLabelSize.height)
										)
								)
								||
								!currentOval.contains(//SCS oval not overlap other structures
										boxX+currentOval.getBounds().getX(),boxY+currentOval.getBounds().getY(),
										SpeciesContextShape.DIAMETER,SpeciesContextShape.DIAMETER)
						){

							if(boxY < YEND){
								boxY+= YSTEP;
							}else{
								boxY = (xCount%2 == 0?TOPOFFSET:TOPOFFSET+SpeciesContextShape.DIAMETER);
								boxX+= XSTEP;
								xCount+= 1;
							}
							if(boxX > XEND && boxY > YEND){
								bLayoutFailed = true;
								break;
							}
						}
						if(!bLayoutFailed){
							child.getSpaceManager().setRelPos(boxX, boxY);
							boxY+= YSTEP;
						}else{
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