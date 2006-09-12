package cbit.vcell.model.render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import cbit.vcell.dictionary.ReactionCanvasDisplaySpec;


public class ReactionRenderer {

	/**
	 * This method was created by a SmartGuide.
	 * @param g java.awt.Graphics
	 */
public static Dimension paint(ReactionCanvasDisplaySpec reactionCanvasDisplaySpec, Image offScreenImage, int width, int height, int fontSize, Color backgroundColor) {

	java.awt.Graphics g = offScreenImage.getGraphics();
	g.setClip(0,0,width,height);
	g.setFont(new Font("SansSerif", Font.BOLD, fontSize));
	g.setColor(backgroundColor);
  	g.fillRect(0,0,width,height);

  	if (reactionCanvasDisplaySpec==null){
  		throw new RuntimeException("reactionCanvasDisplaySpec was null");
  	}

	g.setColor(java.awt.Color.black);
	Dimension newBounds = new Dimension(0,0);
	java.awt.FontMetrics fm = g.getFontMetrics();
	int bottomWidth =  (reactionCanvasDisplaySpec.getBottomText()!=null)?fm.stringWidth(reactionCanvasDisplaySpec.getBottomText()):0;
	int topWidth =  (reactionCanvasDisplaySpec.getTopText()!=null)?fm.stringWidth(reactionCanvasDisplaySpec.getTopText()):0;
	int leftWidth = (reactionCanvasDisplaySpec.getLeftText()!=null)?fm.stringWidth(reactionCanvasDisplaySpec.getLeftText()):0;
	int productWidth =  (reactionCanvasDisplaySpec.getRightText()!=null)?fm.stringWidth(reactionCanvasDisplaySpec.getRightText()):0;
	int bottomHeight =  (reactionCanvasDisplaySpec.getBottomText()!=null)?fm.getHeight():0;
	int topHeight =  (reactionCanvasDisplaySpec.getTopText()!=null)?fm.getHeight():0;
	int leftHeight = (reactionCanvasDisplaySpec.getLeftText()!=null)?fm.getHeight():0;
	int rightHeight =  (reactionCanvasDisplaySpec.getRightText()!=null)?fm.getHeight():0;
	int rateWidth = Math.max(bottomWidth,topWidth) + 30;
	int totHeight = Math.max(leftHeight, rightHeight);
	totHeight += topHeight + bottomHeight + 20;
	int totWidth = leftWidth + rateWidth + productWidth + 40;
	newBounds.width = totWidth;
	newBounds.height = totHeight;
	java.awt.Rectangle rect = g.getClipBounds();
	int posy = rect.y + rect.height/2 + leftHeight/2;
	int posx = rect.x + rect.width/2 - totWidth / 2 + 10;
	if (reactionCanvasDisplaySpec.getLeftText()!=null){
		g.drawString(reactionCanvasDisplaySpec.getLeftText(), posx, posy); posx += leftWidth + 10;
	}	
	if (reactionCanvasDisplaySpec.getArrowType()==ReactionCanvasDisplaySpec.ARROW_RIGHT){
		g.drawLine(posx,             posy-fm.getHeight()/3,   posx+rateWidth,   posy-fm.getHeight()/3  );
		g.drawLine(posx+rateWidth,   posy-fm.getHeight()/3,   posx+rateWidth-5, posy-fm.getHeight()/3-5);
		g.drawLine(posx+rateWidth,   posy-fm.getHeight()/3,   posx+rateWidth-5, posy-fm.getHeight()/3+5);
	} else if (reactionCanvasDisplaySpec.getArrowType()==ReactionCanvasDisplaySpec.ARROW_BOTH){
		g.drawLine(posx,             posy-fm.getHeight()/3-2, posx+rateWidth,   posy-fm.getHeight()/3-2);
		g.drawLine(posx,             posy-fm.getHeight()/3+2, posx+rateWidth,   posy-fm.getHeight()/3+2);
		g.drawLine(posx+rateWidth,   posy-fm.getHeight()/3-2, posx+rateWidth-5, posy-fm.getHeight()/3-7);
		g.drawLine(posx+1,           posy-fm.getHeight()/3+2, posx+6,           posy-fm.getHeight()/3+7);
	}	 
	posx += rateWidth + 10;
	if (reactionCanvasDisplaySpec.getRightText()!=null){
		g.drawString(reactionCanvasDisplaySpec.getRightText(), posx, posy);
	}	
	posy -= leftHeight;
	posx -= rateWidth/2 + 10 + topWidth/2;
	if (reactionCanvasDisplaySpec.getTopText()!=null){
		g.drawString(reactionCanvasDisplaySpec.getTopText(), posx, posy);
	}	
	posy += leftHeight + bottomHeight;
	posx += topWidth/2 - bottomWidth/2;
	if (reactionCanvasDisplaySpec.getBottomText()!=null){
		g.drawString(reactionCanvasDisplaySpec.getBottomText(), posx, posy);
	}
	return newBounds;
}

}
