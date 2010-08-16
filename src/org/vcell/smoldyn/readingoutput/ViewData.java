package org.vcell.smoldyn.readingoutput;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;


import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import cbit.plot.PlotPane;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;


/**
 * @author mfenwick
 *
 */
public class ViewData {

	public static void main(String [] args) {
		
		JFrame frame = new JFrame();
		frame.setVisible(true);
		frame.setBounds(15, 15, 500, 500);
		frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		
		JPanel jp = new JPanel();
		frame.add(jp);
		jp.setVisible(true);
		jp.setBounds(15, 15, 500, 500);
		jp.setBackground(new Color(250, 100, 250));
		
//		Plot2DDataPanel panel = new Plot2DDataPanel();
//		panel.setVisible(true);
//		jp.add(panel);
		
		double [] [] data = ReadLocations.getNumbersAsDoubles();
		PlotData whatever1 = new PlotData(data[0], data[2]);
		PlotData whatever2 = new PlotData(data[0], data[4]);
//		PlotData whatever3 = new PlotData(data[0], data[1]);
//		PlotData whatever0 = new PlotData(data[1]);
		//for(int i = 0; i < )
//		PlotData whatever2 = new PlotData(data[0], data[2]);
//		PlotData whatever3 = new PlotData(data[0], data[3]);
		PlotData [] stuff = {whatever1, whatever2};//, whatever2, whatever3};
		SymbolTableEntry i = new SymbolTableEntry() {
			
			/**@Override*/
			public boolean isConstant() throws ExpressionException {
				// TODO Auto-generated method stub
				return false;
			}
			
			/**@Override*/
			public VCUnitDefinition getUnitDefinition() {
				// TODO Auto-generated method stub
				return null;
			}
			
			/**@Override*/
			public NameScope getNameScope() {
				// TODO Auto-generated method stub
				return null;
			}
			
			/**@Override*/
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			/**@Override*/
			public int getIndex() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			/**@Override*/
			public Expression getExpression() throws ExpressionException {
				// TODO Auto-generated method stub
				return null;
			}
			
			/**@Override*/
			public double getConstantValue() throws ExpressionException {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		SymbolTableEntry [] darn = {i, i};
		String [] strings = {"foxes", "sheep"};
		Plot2D data1 = new Plot2D(darn, strings, stuff);
		
		
		PlotPane twodpanel = new PlotPane();
		twodpanel.setVisible(true);
		jp.add(twodpanel);
		
		twodpanel.setPlot2D(data1);
	}
}
