package cbit.vcell.math.gui;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.parser.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.*;
import cbit.vcell.server.*;
/**
 * This type was created in VisualAge.
 */
public class MathDescEditorTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static MathDescription getMathDescExample_Stoch() throws Exception
{
	//create a subDomain
	CompartmentSubDomain sd = new CompartmentSubDomain("bistable",CompartmentSubDomain.NON_SPATIAL_PRIORITY);
	StochVolVariable v = new StochVolVariable("X");
	VarIniCondition varIni = new VarIniCondition(v,new Expression(5));
	//subDomain add variables
	sd.addVarIniCondition(varIni); 

	Action action1 = new Action(v,"inc", new Expression(1));
	Expression exp1 = null;
	try
	{
		exp1 = new Expression("1*5*X*(X-1)");
	}catch (ExpressionException e){}
	JumpProcess jp1 = new JumpProcess("R1", exp1); //create jumpPorcess1
	jp1.addAction(action1);

	Action action2 = new Action(v,"inc", new Expression(-1));
	Expression exp2 = null;
	try
	{
		exp2 = new Expression("10*X*(X-1)*(X-2)");
	}catch (ExpressionException e){}
	JumpProcess jp2 = new JumpProcess("R2", exp2); //create jumpProcess2
	jp2.addAction(action2);

	Action action3 = new Action(v,"inc", new Expression(1));
	Expression exp3 = null;
	try
	{
		exp3 = new Expression("1*5");
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
	
	

	// //end of the portion copy from getODEExampleWagner
	return  md;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		MathDescEditor aMathDescEditor;
		aMathDescEditor = new MathDescEditor();
		frame.setContentPane(aMathDescEditor);
		frame.setSize(aMathDescEditor.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});

//		aMathDescEditor.setMakeCanonicalVisibility(true);
//		aMathDescEditor.setFlattenVisibility(true);
//		aMathDescEditor.setApproxSensSolnButtonVisibility(true);
//		aMathDescEditor.setConstructedSolnButtonVisibility(true);
		frame.setVisible(true);
		//cbit.vcell.mapping.SimulationContext simContext = cbit.vcell.mapping.SimulationContextTest.getExample(0);
		//cbit.vcell.mapping.MathMapping mathMapping = new cbit.vcell.mapping.MathMapping(simContext);
		aMathDescEditor.setMathDescription(getMathDescExample_Stoch());

	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}