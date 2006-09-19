package cbit.vcell.solver.ode;
import cbit.vcell.parser.*;
import cbit.vcell.math.ReservedVariable;
/**
 * Insert the type's description here.
 * Creation date: (9/11/2003 11:02:57 AM)
 * @author: Anuradha Lakshminarayana
 */
public class ODESolverResultSetTest {
/**
 * ODESolverResultSetTest constructor comment.
 */
public ODESolverResultSetTest() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (9/11/2003 11:05:08 AM)
 * @return cbit.vcell.solver.ode.ODESolverResultSet
 * @param varNames java.lang.String[]
 * @param expressions cbit.vcell.parser.Expression[]
 */
public static ODESolverResultSet getExample(double times[], String[] varNames, cbit.vcell.parser.Expression[] expressions) throws ExpressionException {
	if (varNames.length!=expressions.length){
		throw new IllegalArgumentException("must have one variable name per expression");
	}
	
	SimpleSymbolTable symbolTable = new SimpleSymbolTable(new String[] { ReservedVariable.TIME.getName() });

	
	ODESolverResultSet r = new ODESolverResultSet();
	r.addDataColumn(new cbit.vcell.solver.ode.ODESolverResultSetColumnDescription ("t"));
	for (int i = 0; i < varNames.length; i++){
		r.addDataColumn(new cbit.vcell.solver.ode.ODESolverResultSetColumnDescription (varNames[i]));
		String symbols[] = expressions[i].getSymbols();
		if (symbols==null || symbols.length==0){
			// no symbols, ok
		}else if (symbols.length==1){
			// must be time only
			if (!symbols[0].equals(cbit.vcell.math.ReservedVariable.TIME.getName())){
				throw new IllegalArgumentException("can only be function of time");
			}
		}else{
			throw new IllegalArgumentException("can only be function of time");
		}
		expressions[i].bindExpression(symbolTable);
	}

	double values[] = new double[1];
	for (int i = 0; i < times.length; i++) {
		double data[] = new double[expressions.length+1];
		data[0] = times[i];
		values[0] = times[i];
		for (int j = 0; j < expressions.length; j++){
			data[1+j] = expressions[j].evaluateVector(values);
		}
		r.addRow(data);
	}
	return r;
}


/**
 * Insert the method's description here.
 * Creation date: (9/11/2003 11:03:48 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		final int numTimes = 100;
		double times[] = new double[numTimes];
		for (int i = 0; i < numTimes; i++){
			times[i] = ((double)i)/numTimes;
		}
		String names[] = { "v1", "v2", "v3" };
		Expression exps[] = { new Expression("cos(5*t)"), new Expression("sin(10*t)"), new Expression("cos(20*t)") };
		ODESolverResultSet r = getExample(times,names,exps);
		plot(r);
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/11/2003 11:23:18 AM)
 * @param rowColumnResultSet cbit.vcell.util.RowColumnResultSet
 */
public static void plot(cbit.vcell.solver.ode.ODESimData odeSimData) throws ExpressionException {
	//
	// create PlotDatas
	//
	ColumnDescription colDescs[] = odeSimData.getColumnDescriptions();
	String plotNames[] = new String[colDescs.length-1];
	cbit.plot.PlotData plots[] = new cbit.plot.PlotData[colDescs.length-1];
	double timeArray[] = odeSimData.extractColumn(odeSimData.findColumn("t"));

	// double timeArray[] = odeSimData.extractColumn(odeSimData.findColumn(cbit.vcell.math.ReservedVariable.TIME.getName()));
	for (int i = 1; i < colDescs.length; i++){
		plotNames[i-1] = colDescs[i].getDisplayName();
		// double varArray[] = odeSimData.extractColumn(odeSimData.findColumn(colDescs[i].getName()));
		double varArray[] = odeSimData.extractColumn(i);
		plots[i-1] = new cbit.plot.PlotData(timeArray,varArray);
	}
	System.out.println("size: rows="+odeSimData.getRowCount()+", columns="+odeSimData.getColumnDescriptionsCount());

	//
	// create Plot2DPanel and display plots.
	//
	javax.swing.JFrame frame = new javax.swing.JFrame();
	cbit.plot.Plot2DPanel aPlot2DPanel;
	aPlot2DPanel = new cbit.plot.Plot2DPanel();
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
	aPlot2DPanel.setPlot2D(new cbit.plot.Plot2D(plotNames,plots));
}


/**
 * Insert the method's description here.
 * Creation date: (9/11/2003 11:23:18 AM)
 * @param rowColumnResultSet cbit.vcell.util.RowColumnResultSet
 */
public static void plot(cbit.vcell.solver.ode.ODESolverResultSet odeSolverResultSet) throws ExpressionException {
	//
	// create PlotDatas
	//
	ColumnDescription colDescs[] = odeSolverResultSet.getColumnDescriptions();
	String plotNames[] = new String[colDescs.length-1];
	cbit.plot.PlotData plots[] = new cbit.plot.PlotData[colDescs.length-1];
	double timeArray[] = odeSolverResultSet.extractColumn(odeSolverResultSet.findColumn(cbit.vcell.math.ReservedVariable.TIME.getName()));
	for (int i = 1; i < colDescs.length; i++){
		plotNames[i-1] = colDescs[i].getName();
		double varArray[] = odeSolverResultSet.extractColumn(odeSolverResultSet.findColumn(colDescs[i].getName()));
		plots[i-1] = new cbit.plot.PlotData(timeArray,varArray);
	}
	System.out.println("size: rows="+odeSolverResultSet.getRowCount()+", columns="+odeSolverResultSet.getColumnDescriptionsCount());

	//
	// create Plot2DPanel and display plots.
	//
	javax.swing.JFrame frame = new javax.swing.JFrame();
	cbit.plot.Plot2DPanel aPlot2DPanel;
	aPlot2DPanel = new cbit.plot.Plot2DPanel();
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
	aPlot2DPanel.setPlot2D(new cbit.plot.Plot2D(plotNames,plots));
}
}