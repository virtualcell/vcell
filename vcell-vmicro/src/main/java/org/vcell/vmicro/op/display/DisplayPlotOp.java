package org.vcell.vmicro.op.display;

import java.awt.event.WindowListener;

import javax.swing.JFrame;

import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import cbit.plot.gui.PlotPane;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.parser.ExpressionException;

public class DisplayPlotOp {
	
	public void displayPlot(RowColumnResultSet rowColumnResultSet, String title, WindowListener listener) throws ExpressionException {
		JFrame frame = new javax.swing.JFrame();
		PlotPane aPlotPane;
		aPlotPane = new PlotPane();
		frame.setContentPane(aPlotPane);
		frame.setSize(aPlotPane.getSize());
		if (listener!=null){
			frame.addWindowListener(listener);
		}
		frame.setTitle(title);
		frame.setVisible(true);
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);

		
		int dataColumnCount = rowColumnResultSet.getDataColumnCount();
		PlotData[] plotDatas = new PlotData[dataColumnCount-1];
		String[] labels = new String[dataColumnCount-1];
		double[] time = rowColumnResultSet.extractColumn(0);
		
		for (int i=0; i<dataColumnCount-1; i++){
			double[] yArray = rowColumnResultSet.extractColumn(i+1);
			plotDatas[i] = new PlotData(time, yArray);
			labels[i] = rowColumnResultSet.getColumnDescriptions(i+1).getName();
		}

		Plot2D plot2D = new Plot2D(null,null,labels,plotDatas);		
		
		aPlotPane.setPlot2D(plot2D);
	}

	

}
