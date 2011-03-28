package org.vcell.util.gui;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollPane;

public class ViewPortStabilizer {

	protected final JScrollPane scrollPane;
	protected double x, y;
	
	public ViewPortStabilizer(JScrollPane scrollPane) { this.scrollPane = scrollPane; }
	
	public void saveViewPortPosition() {
		BoundedRangeModel xModel = scrollPane.getHorizontalScrollBar().getModel();
		double centerX = xModel.getValue() + xModel.getExtent() / 2;
		x = (centerX - xModel.getMinimum()) / (xModel.getMaximum() - xModel.getMinimum());
		BoundedRangeModel yModel = scrollPane.getVerticalScrollBar().getModel();
		double centerY = yModel.getValue() + yModel.getExtent() / 2;
		y = (centerY - yModel.getMinimum()) / (yModel.getMaximum() - yModel.getMinimum());
	}
	
	public void restoreViewPortPosition() {
		BoundedRangeModel xModel = scrollPane.getHorizontalScrollBar().getModel();
		double centerX = xModel.getMinimum() + x*(xModel.getMaximum() - xModel.getMinimum());
		xModel.setValue((int)(centerX - xModel.getExtent()/2));
		BoundedRangeModel yModel = scrollPane.getVerticalScrollBar().getModel();
		double centerY = yModel.getMinimum() + y*(yModel.getMaximum() - yModel.getMinimum());
		yModel.setValue((int)(centerY - yModel.getExtent()/2));
	}
	
}
