package cbit.vcell.solver.test;
import cbit.vcell.simulation.ErrorTolerance;
import cbit.vcell.simulation.Simulation;
import cbit.vcell.simulation.SolverTaskDescription;
import cbit.vcell.simulation.TimeBounds;
import cbit.vcell.math.Action;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.JumpProcess;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.VarIniCondition;
import cbit.vcell.math.VolVariable;
import cbit.vcell.solver.stoch.*;
import cbit.vcell.geometry.*;
import java.io.*;

import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

/**
 * Test program for Stochastic simulation.
 * Data are filled into a simulation instance and the input file
 * will be generated from it.
 * Creation date: (6/29/2006 3:56:08 PM)
 * @author: Tracy LI
 */
public class StochTest 
{
	static Simulation simInstance = null;

/**
 * StochTest constructor comment.
 */
public StochTest() {
	super();
}


/**
 * Manually create a simulation.
 * Creation date: (6/30/2006 8:41:04 AM)
 */
public void getSimulationModel() throws Exception 
{
	//create a subDomain
	CompartmentSubDomain sd = new CompartmentSubDomain("bistable",CompartmentSubDomain.NON_SPATIAL_PRIORITY);
	VolVariable v = new VolVariable("X");//TODO: what kind of variable should use?
	VarIniCondition varIni = new VarIniCondition(v,ExpressionFactory.createExpression(5));
	//subDomain add variables
	sd.addVarIniCondition(varIni); 

	Action action1 = new Action(v,"inc", ExpressionFactory.createExpression(1));
	IExpression exp1 = null;
	try
	{
		exp1 = ExpressionFactory.createExpression("1*5*X*(X-1)");
	}catch (ExpressionException e){}
	JumpProcess jp1 = new JumpProcess("R1", exp1); //create jumpPorcess1
	jp1.addAction(action1);

	Action action2 = new Action(v,"inc", ExpressionFactory.createExpression(-1));
	IExpression exp2 = null;
	try
	{
		exp2 = ExpressionFactory.createExpression("10*X*(X-1)*(X-2)");
	}catch (ExpressionException e){}
	JumpProcess jp2 = new JumpProcess("R2", exp2); //create jumpProcess2
	jp2.addAction(action2);

	Action action3 = new Action(v,"inc", ExpressionFactory.createExpression(1));
	IExpression exp3 = null;
	try
	{
		exp3 = ExpressionFactory.createExpression("1*5");
	}catch (ExpressionException e){}
	JumpProcess jp3 = new JumpProcess("R3", exp3); //create jumpProcess3
	jp3.addAction(action3);

	Action action4 = new Action(v,"inc", ExpressionFactory.createExpression(-1));
	IExpression exp4 = null;
	try
	{
		exp4 = ExpressionFactory.createExpression("1*X");
	}catch (ExpressionException e){}	
	JumpProcess jp4 = new JumpProcess("R4", exp4); //create jumpProcess4
	jp4.addAction(action4);

	//subDomain add jump processes
	sd.addJumpProcess(jp1);
	sd.addJumpProcess(jp2);
	sd.addJumpProcess(jp3);
	sd.addJumpProcess(jp4);

	//create a mathDescription
	MathDescription md = new MathDescription("StochCompartmentModel");
	try
	{
		md.addSubDomain(sd);
	} catch (MathException e) {}


	// //copy from getODEExampleWagner---all are nonsense here , just for making MathDescriton valid
	Geometry geo = new Geometry("getOdeExampleWagner()",0);
	geo.getGeometrySpec().setExtent(new org.vcell.util.Extent(1.0, 1.0, 1.0));

	geo.getGeometrySpec().setOrigin(new org.vcell.util.Origin(0.0, 0.0, 0.0));
	md.setGeometry(geo);

	//	Constants
	Constant constant = new Constant("LambdaBeta",ExpressionFactory.createExpression("6;"));
	md.addVariable(constant);
	constant = new Constant("vL",ExpressionFactory.createExpression("5e-4;"));
	md.addVariable(constant);
	constant = new Constant("I",ExpressionFactory.createExpression("0.12;"));
	md.addVariable(constant);
	constant = new Constant("dI",ExpressionFactory.createExpression("0.025;"));
	md.addVariable(constant);
	constant = new Constant("dact",ExpressionFactory.createExpression("1.2;"));
	md.addVariable(constant);
	constant = new Constant("Cer",ExpressionFactory.createExpression("10;"));
	md.addVariable(constant);
	constant = new Constant("vP",ExpressionFactory.createExpression("0.1;"));
	md.addVariable(constant);
	constant = new Constant("kP",ExpressionFactory.createExpression("0.4;"));
	md.addVariable(constant);
	constant = new Constant("dinh",ExpressionFactory.createExpression("1.5;"));
	md.addVariable(constant);
	constant = new Constant("tau0",ExpressionFactory.createExpression("4.0;"));
	md.addVariable(constant);
	constant = new Constant("C",ExpressionFactory.createExpression("4.0;"));
	md.addVariable(constant);
	constant = new Constant("h",ExpressionFactory.createExpression("4.0;"));
	md.addVariable(constant);
	md.addVariable(v);
	
	IExpression rateExpression = ExpressionFactory.createExpression("LambdaBeta*("+
																"(vL+pow(I*C*h/((I+dI)*(C+dact)),3))*(Cer-C)"+
																"-vP*(C*C/(C*C+kP*kP)));");
	IExpression initialExpression = ExpressionFactory.createExpression("3;");
	OdeEquation equ = new OdeEquation(v,initialExpression, rateExpression);
	sd.addEquation(equ);

	// //end of the portion copy from getODEExampleWagner


	//create a simulation
	simInstance = new Simulation(md);
	SolverTaskDescription std = new SolverTaskDescription(simInstance);
	TimeBounds tb = new TimeBounds(0.0,20.0);
	try
	{
		std.setTimeBounds(tb);
	}catch (java.beans.PropertyVetoException e){}
	ErrorTolerance et = new ErrorTolerance(1e-6,1e-6);
	try
	{
		std.setErrorTolerance(et);
	}catch (java.beans.PropertyVetoException e){}
}


/**
 * Test stochastic simulation.
 * Creation date: (6/29/2006 4:05:21 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{
	StochTest st = new StochTest();
	try{
		st.getSimulationModel();
	}catch (Exception e) {}
	cbit.vcell.solver.stoch.StochFileWriter sfw = new StochFileWriter(simInstance);
	try
	{
		FileWriter fw = new FileWriter("C:/gibson_deploy/gibson_deploy/testInput.txt",false);
		PrintWriter pw = new PrintWriter(fw);
		try
		{
			sfw.writeStochInputFile(pw);
		}catch (java.lang.Exception e){}
		pw.close();
	}catch (IOException e){e.printStackTrace();}
	//call command line to run the simulation
	try{
		Runtime.getRuntime().exec("c:/gibson_deploy/gibson_deploy/VCellStoch.exe -m gibson -f C:/gibson_deploy/gibson_deploy/testInput.txt");
//    	Executable exe = new Executable("c:/gibson_deploy/gibson_deploy/VCellStoch.exe -m gibson -f C:/gibson_deploy/gibson_deploy/testInput.txt");
//    	exe.start();
//    	String output = exe.getStdoutString();
//		String err = exe.getStderrString();
//		System.out.println("-----------------------------------");
//    	System.out.println(output);
//		System.out.println("-----------------------------------");
//    	System.out.println(err);
	}catch (Exception e){
		e.printStackTrace();
	}
}
}