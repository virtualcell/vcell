package cbit.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
/**
 * Insert the type's description here.
 * Creation date: (2/10/2001 3:13:43 AM)
 * @author: Ion Moraru
 */
public class EnhancedLabelUI extends BasicLabelUI {
/**
 * EnhancedLabelUI constructor comment.
 */
public EnhancedLabelUI() {
	super();
}
	protected String layoutCL(
		JLabel label,                  
		FontMetrics fontMetrics, 
		String text, 
		Icon icon, 
		Rectangle viewR, 
		Rectangle iconR, 
		Rectangle textR)
	{
		// so that we compute proper text clipping if rotated label
		if (label instanceof EnhancedJLabel) {
			EnhancedJLabel eLabel = (EnhancedJLabel)label;
			if (eLabel.getVertical()) {
				Rectangle2D viewR2 = (Rectangle2D)viewR;
				viewR2.setRect(viewR.x, viewR.y, viewR.height, viewR.width);
			}
		}
		//
		return SwingUtilities.layoutCompoundLabel(
			(JComponent) label,
			fontMetrics,
			text,
			icon,
			label.getVerticalAlignment(),
			label.getHorizontalAlignment(),
			label.getVerticalTextPosition(),
			label.getHorizontalTextPosition(),
			viewR,
			iconR,
			textR,
			label.getIconTextGap());
	}
}
