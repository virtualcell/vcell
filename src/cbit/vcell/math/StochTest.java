package cbit.vcell.math;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.vcell.util.Extent;
import org.vcell.util.Origin;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.stoch.StochFileWriter;

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
	StochVolVariable v = new StochVolVariable("X");
	VarIniCondition varIni = new VarIniCondition(v,new Expression(5));
	//subDomain add variables
	sd.addVarIniCondition(varIni); 

	Action action1 = new Action(v,Action.ACTION_INC, new Expression(1));
	Expression exp1 = null;
	try
	{
		exp1 = new Expression("1*5*X*(X-1)");
	}catch (ExpressionException e){}
	JumpProcess jp1 = new JumpProcess("R1", exp1); //create jumpPorcess1
	jp1.addAction(action1);

	Action action2 = new Action(v,Action.ACTION_INC, new Expression(-1));
	Expression exp2 = null;
	try
	{
		exp2 = new Expression("10*X*(X-1)*(X-2)");
	}catch (ExpressionException e){}
	JumpProcess jp2 = new JumpProcess("R2", exp2); //create jumpProcess2
	jp2.addAction(action2);

	Action action3 = new Action(v,Action.ACTION_INC, new Expression(1));
	Expression exp3 = null;
	try
	{
		exp3 = new Expression("1*5");
	}catch (ExpressionException e){}
	JumpProcess jp3 = new JumpProcess("R3", exp3); //create jumpProcess3
	jp3.addAction(action3);

	Action action4 = new Action(v,Action.ACTION_INC, new Expression(-1));
	Expression exp4 = null;
	try
	{
		exp4 = new Expression("1*X");
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
	geo.getGeometrySpec().setExtent(new Extent(1.0, 1.0, 1.0));

	geo.getGeometrySpec().setOrigin(new Origin(0.0, 0.0, 0.0));
	md.setGeometry(geo);

	//	Constants
	Constant constant = new Constant("LambdaBeta",new Expression("6;"));
	md.addVariable(constant);
	constant = new Constant("vL",new Expression("5e-4;"));
	md.addVariable(constant);
	constant = new Constant("I",new Expression("0.12;"));
	md.addVariable(constant);
	constant = new Constant("dI",new Expression("0.025;"));
	md.addVariable(constant);
	constant = new Constant("dact",new Expression("1.2;"));
	md.addVariable(constant);
	constant = new Constant("Cer",new Expression("10;"));
	md.addVariable(constant);
	constant = new Constant("vP",new Expression("0.1;"));
	md.addVariable(constant);
	constant = new Constant("kP",new Expression("0.4;"));
	md.addVariable(constant);
	constant = new Constant("dinh",new Expression("1.5;"));
	md.addVariable(constant);
	constant = new Constant("tau0",new Expression("4.0;"));
	md.addVariable(constant);
	constant = new Constant("C",new Expression("4.0;"));
	md.addVariable(constant);
	constant = new Constant("h",new Expression("4.0;"));
	md.addVariable(constant);
	md.addVariable(v);
	
	Expression rateExpression = new Expression("LambdaBeta*("+
																"(vL+pow(I*C*h/((I+dI)*(C+dact)),3))*(Cer-C)"+
																"-vP*(C*C/(C*C+kP*kP)));");
	Expression initialExpression = new Expression("3;");
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
	
	try
	{
		FileWriter fw = new FileWriter("C:/gibson_deploy/gibson_deploy/testInput.txt",false);
		PrintWriter pw = new PrintWriter(fw);
		StochFileWriter sfw = new StochFileWriter(pw, new SimulationJob(simInstance, 0, null), false);
		try
		{
			sfw.write();
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