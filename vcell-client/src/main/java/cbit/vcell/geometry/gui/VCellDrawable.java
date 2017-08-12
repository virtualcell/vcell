/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;

import org.vcell.util.DrawPaneModel;

/**
 * Insert the type's description here.
 * Creation date: (8/3/00 11:00:48 AM)
 * @author: 
 */
public interface VCellDrawable {
/**
 * Insert the method's description here.
 * Creation date: (8/5/00 3:11:59 PM)
 * @return cbit.vcell.geometry.DrawPaneModel
 */
DrawPaneModel getDrawPaneModel();
/**
 * Insert the method's description here.
 * Creation date: (8/5/00 3:35:18 PM)
 * @return java.awt.Graphics
 */
java.awt.Graphics getGraphics();
/**
 * Insert the method's description here.
 * Creation date: (8/8/00 10:33:43 PM)
 * @return java.awt.Point
 * @param p java.awt.Point
 */
java.awt.Point getImagePoint(java.awt.Point p);
/**
 * Insert the method's description here.
 * Creation date: (8/8/00 10:33:43 PM)
 * @return java.awt.Point
 * @param p java.awt.Point
 */
java.awt.geom.Point2D.Double getImagePointUnitized(java.awt.Point p);
/**
 * Insert the method's description here.
 * Creation date: (8/5/00 3:34:50 PM)
 */
void repaint();
/**
 * Insert the method's description here.
 * Creation date: (8/3/00 11:01:40 AM)
 * @param dpm cbit.vcell.geometry.DrawPaneModel
 */
void setDrawPaneModel(DrawPaneModel dpm);
}
