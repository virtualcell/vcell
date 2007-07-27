package cbit.vcell.solver.stoch;

import cbit.gui.DialogUtils;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.Action;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.JumpProcess;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.StochVolVariable;
import cbit.vcell.math.VarIniCondition;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;

/**
 * To test if the NetCDFWriter works properly.
 * Two examples are included in the class.
 * @author Tracy LI
 * Created in June, 2007
 */

public class NetCDFWriterTest {
		
		//manually create a bistable example.
		public static Simulation getExample1()
		{
			// create a subDomain
			try{
				CompartmentSubDomain sd = new CompartmentSubDomain("bistable",CompartmentSubDomain.NON_SPATIAL_PRIORITY);
				StochVolVariable v = new StochVolVariable("X");
				VarIniCondition varIni = new VarIniCondition(v,new Expression(5));
				//subDomain add variables
				sd.addVarIniCondition(varIni); 
		
				Action action1 = new Action(v,"inc", new Expression(1));
				Expression exp1 = null;
				try
				{
					exp1 = new Expression("2*5*X*(X-1)");
				}catch (ExpressionException e){}
				JumpProcess jp1 = new JumpProcess("R1", exp1); //create jumpPorcess1
				jp1.addAction(action1);
		
				Action action2 = new Action(v,"inc", new Expression(-1));
				Expression exp2 = null;
				try
				{
					exp2 = new Expression("1*X*(X-1)*(X-2)");
				}catch (ExpressionException e){}
				JumpProcess jp2 = new JumpProcess("R2", exp2); //create jumpProcess2
				jp2.addAction(action2);
		
				Action action3 = new Action(v,"inc", new Expression(1));
				Expression exp3 = null;
				try
				{
					exp3 = new Expression("0.05*5");
				}catch (ExpressionException e){}
				JumpProcess jp3 = new JumpProcess("R3", exp3); //create jumpProcess3
				jp3.addAction(action3);
		
				Action action4 = new Action(v,"inc", new Expression(-1));
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
				geo.getGeometrySpec().setExtent(new cbit.util.Extent(1.0, 1.0, 1.0));
		
				geo.getGeometrySpec().setOrigin(new cbit.util.Origin(0.0, 0.0, 0.0));
				md.setGeometry(geo);

				// variable
				md.addVariable(v);
				//	Constants
				Constant constant = new Constant("KMOLE",new Expression("0.0016611295681063123"));
				md.addVariable(constant);
				constant = new Constant("_N_pmol_",new Expression("6.02E11"));
				md.addVariable(constant);
							
				//create a simulation
				Simulation simInstance = new Simulation(md);
				SolverTaskDescription std = new SolverTaskDescription(simInstance);
				TimeBounds tb = new TimeBounds(0.0,10.0);
				std.setTimeBounds(tb);
				ErrorTolerance et = new ErrorTolerance(1e-9,1e-9);
				std.setErrorTolerance(et);
				UniformOutputTimeSpec outputTimeSpec = new UniformOutputTimeSpec(0.5);
				std.setOutputTimeSpec(outputTimeSpec);
				StochSimOptions stochOpt = new StochSimOptions(false, 13,1);
				std.setStochOpt(stochOpt);
				simInstance.setSolverTaskDescription(std);
				return simInstance;
			}catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}
		
		//manually create a HMM example.
		public static Simulation getExample2()
		{
			// create a subDomain
			try{
				CompartmentSubDomain sd = new CompartmentSubDomain("HMM",CompartmentSubDomain.NON_SPATIAL_PRIORITY);
				StochVolVariable v1 = new StochVolVariable("S");
				VarIniCondition varIni1 = new VarIniCondition(v1,new Expression(602));
				StochVolVariable v2 = new StochVolVariable("E");
				VarIniCondition varIni2 = new VarIniCondition(v2,new Expression(602));
				StochVolVariable v3 = new StochVolVariable("ES");
				VarIniCondition varIni3 = new VarIniCondition(v3,new Expression(0));
				StochVolVariable v4 = new StochVolVariable("P");
				VarIniCondition varIni4 = new VarIniCondition(v4,new Expression(0));
				//subDomain add variables
				sd.addVarIniCondition(varIni1);
				sd.addVarIniCondition(varIni2);
				sd.addVarIniCondition(varIni3);
				sd.addVarIniCondition(varIni4);
		
				Action action11 = new Action(v1,"inc", new Expression(-1));
				Action action12 = new Action(v2,"inc", new Expression(-1));
				Action action13 = new Action(v3,"inc", new Expression(1));
				Expression exp1 = null;
				try
				{
					exp1 = new Expression("0.02*S*E");
				}catch (ExpressionException e){}
				JumpProcess jp1 = new JumpProcess("R1", exp1); //create jumpPorcess1
				jp1.addAction(action11);
				jp1.addAction(action12);
				jp1.addAction(action13);
		
				Action action21 = new Action(v3,"inc", new Expression(-1));
				Action action22 = new Action(v1,"inc", new Expression(1));
				Action action23 = new Action(v2,"inc", new Expression(1));
				Expression exp2 = null;
				try
				{
					exp2 = new Expression("ES");
				}catch (ExpressionException e){}
				JumpProcess jp2 = new JumpProcess("R2", exp2); //create jumpProcess2
				jp2.addAction(action21);
				jp2.addAction(action22);
				jp2.addAction(action23);
		
				Action action31 = new Action(v3,"inc", new Expression(-1));
				Action action32 = new Action(v2,"inc", new Expression(1));
				Action action33 = new Action(v4,"inc", new Expression(1));
				Expression exp3 = null;
				try
				{
					exp3 = new Expression("ES");
				}catch (ExpressionException e){}
				JumpProcess jp3 = new JumpProcess("R3", exp3); //create jumpProcess3
				jp3.addAction(action31);
				jp3.addAction(action32);
				jp3.addAction(action33);
				
				//subDomain add jump processes
				sd.addJumpProcess(jp1);
				sd.addJumpProcess(jp2);
				sd.addJumpProcess(jp3);
				
				//create a mathDescription
				MathDescription md = new MathDescription("StochCompartmentModel");
				try
				{
					md.addSubDomain(sd);
				} catch (MathException e) {}
		
		
				// //copy from getODEExampleWagner---all are nonsense here , just for making MathDescriton valid
				Geometry geo = new Geometry("getOdeExampleWagner()",0);
				geo.getGeometrySpec().setExtent(new cbit.util.Extent(1.0, 1.0, 1.0));
		
				geo.getGeometrySpec().setOrigin(new cbit.util.Origin(0.0, 0.0, 0.0));
				md.setGeometry(geo);

				// variable
				md.addVariable(v1);
				md.addVariable(v2);
				md.addVariable(v3);
				md.addVariable(v4);
				
				//	Constants
				Constant constant = new Constant("KMOLE",new Expression("0.0016611295681063123"));
				md.addVariable(constant);
				constant = new Constant("_N_pmol_",new Expression("6.02E11"));
				md.addVariable(constant);
							
				//create a simulation
				Simulation simInstance = new Simulation(md);
				SolverTaskDescription std = new SolverTaskDescription(simInstance);
				TimeBounds tb = new TimeBounds(0.0, 5.0);
				std.setTimeBounds(tb);
				ErrorTolerance et = new ErrorTolerance(1e-9,1e-9);
				std.setErrorTolerance(et);
				UniformOutputTimeSpec outputTimeSpec = new UniformOutputTimeSpec(0.1);
				std.setOutputTimeSpec(outputTimeSpec);
				StochSimOptions stochOpt = new StochSimOptions(false, 13,1);
				std.setStochOpt(stochOpt);
				simInstance.setSolverTaskDescription(std);
				return simInstance;
			}catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}
		
		/**
		 * @param args
		 */
		public static void main(String[] args) {
			if(getExample2() != null)
			{
				NetCDFWriter ncWriter = new NetCDFWriter(getExample1(),"c:/test.nc");
				try{
					ncWriter.writeHybridInputFile();
				}catch (Exception e){
					e.printStackTrace();
					DialogUtils.showErrorDialog(e.getMessage());
				}
				
				System.out.println("The end of main function.");
			}
		}
}
