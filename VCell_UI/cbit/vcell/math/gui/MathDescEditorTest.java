package cbit.vcell.math.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.*;
/**
 * This type was created in VisualAge.
 */
public class MathDescEditorTest {
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

		aMathDescEditor.setMakeCanonicalVisibility(true);
		aMathDescEditor.setFlattenVisibility(true);
		aMathDescEditor.setApproxSensSolnButtonVisibility(true);
		aMathDescEditor.setConstructedSolnButtonVisibility(true);
		frame.setVisible(true);
		//cbit.vcell.mapping.SimulationContext simContext = cbit.vcell.mapping.SimulationContextTest.getExample(0);
		//cbit.vcell.mapping.MathMapping mathMapping = new cbit.vcell.mapping.MathMapping(simContext);
		aMathDescEditor.setMathDescription(getMathDescExample_Stoch());

	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static MathDescription getMathDescExample_Stoch() throws Exception
{
	//create a subDomain
	CompartmentSubDomain sd = new CompartmentSubDomain("bistable",CompartmentSubDomain.NON_SPATIAL_PRIORITY);
	StochVolVariable v = new StochVolVariable("X");
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
	geo.getGeometrySpec().setExtent(new cbit.util.Extent(1.0, 1.0, 1.0));

	geo.getGeometrySpec().setOrigin(new cbit.util.Origin(0.0, 0.0, 0.0));
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
	
	

	// //end of the portion copy from getODEExampleWagner
	return  md;
}

}
