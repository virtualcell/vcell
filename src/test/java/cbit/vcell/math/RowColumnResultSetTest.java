/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;

import org.junit.Test;

import cbit.vcell.parser.Expression;
/**
 * Insert the type's description here.
 * Creation date: (6/25/2001 3:05:25 PM)
 * @author: John Wagner
 */
public class RowColumnResultSetTest {

	@Test
public void test() {
	try {
		java.util.Random random = new java.util.Random();
		RowColumnResultSet r = new RowColumnResultSet();
		r.addDataColumn(new ODESolverResultSetColumnDescription ("t"));
		r.addDataColumn(new ODESolverResultSetColumnDescription ("x"));
		r.addDataColumn(new ODESolverResultSetColumnDescription ("y"));
		int N = 500;
		double w = 80.0/N;
		int SAMPLING = 150;    // 50;
		double t[] = new double[N];
		double x[] = new double[N];
		double y[] = new double[N];
		for (int i = 0; i < N; i++) {
			t[i] = ((double)i)*w;
			x[i] = 1e-5*Math.sin(t[i]);
			y[i] = 1e-5*Math.exp(-t[i]*10);
			r.addRow(new double[] {t[i], x[i], y[i]});
		}
		r.addFunctionColumn(new FunctionColumnDescription(new Expression("1e-5*pow(sin(.3*t), 2)"),"newFunc", null, "newFunc", false));
		long startTime = System.currentTimeMillis();
		r.trimRows(SAMPLING);
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		System.out.println("Elapsed Time: " + elapsedTime);
		double t2[] = r.extractColumn(0);
		double x2[] = r.extractColumn(1);
		double y2[] = r.extractColumn(2);
		double f2[] = r.extractColumn(3);
	System.out.println("size: rows="+r.getRowCount()+", columns="+r.getColumnDescriptionsCount());
		
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			cbit.plot.gui.Plot2DPanel aPlot2DPanel;
			aPlot2DPanel = new cbit.plot.gui.Plot2DPanel();
			frame.setContentPane(aPlot2DPanel);
			frame.setSize(aPlot2DPanel.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.show();
			java.awt.Insets insets = frame.getInsets();
			frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
			frame.setVisible(true);
			aPlot2DPanel.setPlot2D(new cbit.plot.Plot2D(null,null,new String[] {"plot1","plot2","plot3","plot4", "plot5"},new cbit.plot.PlotData[] { new cbit.plot.PlotData(t,x), new cbit.plot.PlotData(t2,x2), new cbit.plot.PlotData(t,y), new cbit.plot.PlotData(t2,y2), new cbit.plot.PlotData(t2,f2) }));
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}
