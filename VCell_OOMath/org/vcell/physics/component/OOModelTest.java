package org.vcell.physics.component;
import org.vcell.expression.ExpressionFactory;

/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 11:33:57 PM)
 * @author: Jim Schaff
 */
public class OOModelTest {
	/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:34:20 PM)
 * @return ncbc.physics2.component.Model
 */
public static OOModel getExample() {

	//
	//
	//                 R1
	//         c1____/\/\/\____c2
	//         |                |
	//         |                |
	//         |                |
	//         |              _____
	//       [ Vs ]           _____ C1
	//         |                |
	//         |                |
	//         |                |
	//         |________________|
	//                  |
	//                  O gnd
	//                  |
	//               ______
	//                ____   
	//                 __
	//
	//
	//
	OOModel oOModel = new OOModel();
	VoltageSource vs = new VoltageSource("VS",10);
	Resistor r = new Resistor("R1",1);
	Capacitor c = new Capacitor("C1",1);
	Ground gnd = new Ground("gnd");
	Connection c1 = new Connection(new Connector[] { vs.getConnectors(0), r.getConnectors(1) });
	Connection c2 = new Connection(new Connector[] { r.getConnectors(0), c.getConnectors(1) });
	Connection c3 = new Connection(new Connector[] { vs.getConnectors(1), c.getConnectors(0), gnd.getConnectors(0) });

	oOModel.addModelComponent(vs);
	oOModel.addModelComponent(r);
	oOModel.addModelComponent(c);
	oOModel.addModelComponent(gnd);
	oOModel.addConnection(c1);
	oOModel.addConnection(c2);
	oOModel.addConnection(c3);
	
	return oOModel;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:34:20 PM)
 * @return ncbc.physics2.component.Model
 */
public static OOModel getExample2() {

	//
	//
	//                 R1           L1
	//         c1____/\/\/\__c2___UUUUU___c3
	//         |                          |
	//         |                          |
	//         |                          |
	//         |                        _____
	//       [ Vs ]                     _____ C1
	//         |                          |
	//         |                          |
	//         |                          |
	//         |________c4________________|
	//                  |
	//                  O gnd
	//                  |
	//               ______
	//                ____   
	//                 __
	//
	//
	//
	OOModel oOModel = new OOModel();
	VoltageSource vs = new VoltageSource("VS",10);
	Resistor R = new Resistor("R1",1);
	Capacitor C = new Capacitor("C1",1);
	Inductor L = new Inductor("L1",1);
	Ground gnd = new Ground("gnd");
	Connection c1 = new Connection(new Connector[] { vs.getConnectors(0), R.getConnectors(1) });
	Connection c2 = new Connection(new Connector[] { R.getConnectors(0), L.getConnectors(1) });
	Connection c3 = new Connection(new Connector[] { L.getConnectors(0), C.getConnectors(1) });
	Connection c4 = new Connection(new Connector[] { vs.getConnectors(1), C.getConnectors(0), gnd.getConnectors(0) });

	oOModel.addModelComponent(vs);
	oOModel.addModelComponent(R);
	oOModel.addModelComponent(L);
	oOModel.addModelComponent(C);
	oOModel.addModelComponent(gnd);
	oOModel.addConnection(c1);
	oOModel.addConnection(c2);
	oOModel.addConnection(c3);
	oOModel.addConnection(c4);
	
	return oOModel;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:34:20 PM)
 * @return ncbc.physics2.component.Model
 */
public static OOModel getExample3() {

	//
	//
	//                     
	//          ___________________c1_____
	//         |                   |      |
	//         |                   |      |
	//         |                   |      |
	//         |                   \    _____
	//       [ Vs ]             R1 /    _____ C1
	//         |                   \      |
	//         |                   /      |
	//         |                   |      |
	//         |________c2_________|______|
	//                  |
	//                  O gnd
	//                  |
	//               ______
	//                ____   
	//                 __
	//
	//
	//
	OOModel oOModel = new OOModel();
	VoltageSource vs = new VoltageSource("VS",10);
	Resistor R = new Resistor("R1",1);
	Capacitor C = new Capacitor("C1",1);
	Ground gnd = new Ground("gnd");
	Connection c1 = new Connection(new Connector[] { vs.getConnectors(1), R.getConnectors(1), C.getConnectors(1) });
	Connection c2 = new Connection(new Connector[] { vs.getConnectors(0), R.getConnectors(0), C.getConnectors(0), gnd.getConnectors(0) });

	oOModel.addModelComponent(vs);
	oOModel.addModelComponent(R);
	oOModel.addModelComponent(C);
	oOModel.addModelComponent(gnd);
	oOModel.addConnection(c1);
	oOModel.addConnection(c2);
	
	return oOModel;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:34:20 PM)
 * @return ncbc.physics2.component.Model
 */
public static OOModel getExample4() throws org.vcell.expression.ExpressionException {

	//
	//
	//                     
	//        c1   ____________               
	//     A ===> |            |      
	//            |    R1      |      c3       __________
	//        c2  |            | ===> C  ===> |          |  c5
	//     B ===> |____________|              |    R2    | ===> E
	//                                    c4  |          |
	//                                D  ===> |          |
	//                                        |__________|
	//
	//
	//
	OOModel oOModel = new OOModel();
	MassActionReaction R1 = new MassActionReaction("R1",new String[] { "r1","r2","p1" }, new int[] { -1, -1, 1 }, false);
	MassActionReaction R2 = new MassActionReaction("R2",new String[] { "r1","r2","p1" }, new int[] { -1, -1, 1 }, true);
	Species A = new Species("A");
	Species B = new Species("B");
	Species C = new Species("C");
	Species D = new Species("D");
	Species E = new Species("E");
	Connection c1 = new Connection(new Connector[] { R1.getConnectors(0), A.getConnectors(0) });
	Connection c2 = new Connection(new Connector[] { R1.getConnectors(1), B.getConnectors(0) });
	Connection c3 = new Connection(new Connector[] { R1.getConnectors(2), C.getConnectors(0), R2.getConnectors(0) });
	Connection c4 = new Connection(new Connector[] { R2.getConnectors(1), D.getConnectors(0) });
	Connection c5 = new Connection(new Connector[] { R2.getConnectors(2), E.getConnectors(0) });

	oOModel.addModelComponent(R1);
	oOModel.addModelComponent(R2);
	oOModel.addModelComponent(A);
	oOModel.addModelComponent(B);
	oOModel.addModelComponent(C);
	oOModel.addModelComponent(D);
	oOModel.addModelComponent(E);
	oOModel.addConnection(c1);
	oOModel.addConnection(c2);
	oOModel.addConnection(c3);
	oOModel.addConnection(c4);
	oOModel.addConnection(c5);
	
	return oOModel;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:34:20 PM)
 * @return ncbc.physics2.component.Model
 */
public static OOModel getExample5() {

	//
	//
	//                     
	//          ___________________c1_____________
	//         |                   |              |
	//         |                   |              |+
	//         |                   |+            C_
	//         |                   \             C_
	//         |                R1 /             C_  L1
	//         |                   \             C_
	//         |                   /              |
	//         |+                  |              |
	//       [ Vs ]                |      R2      |
	//         |                 c3|____/\/\/\____|
	//         |                   |          +   |
	//         |                  +|              |
	//         |                   \              |
	//         |                R3 /              |
	//         |                   \              |
	//         |                   /              |
	//         |                   |              |
	//         |________c2_________|______________|
	//                  |
	//                  | gnd
	//                  |
	//               ______
	//                ____   
	//                 __
	//
	//
	//
	OOModel oOModel = new OOModel();
	VoltageSource vs = new VoltageSource("VS",10);
	Resistor R1 = new Resistor("R1",1);
	Resistor R2 = new Resistor("R2",1);
	Resistor R3 = new Resistor("R3",1);
	Inductor L1 = new Inductor("L1",1);
	Ground gnd = new Ground("gnd");
	Connection c1 = new Connection(new Connector[] { vs.getConnectors(1), R1.getConnectors(1), L1.getConnectors(1) });
	Connection c2 = new Connection(new Connector[] { vs.getConnectors(0), R3.getConnectors(0), R2.getConnectors(1), L1.getConnectors(0), gnd.getConnectors(0) });
	Connection c3 = new Connection(new Connector[] { R3.getConnectors(1), R2.getConnectors(0), R1.getConnectors(0) });

	oOModel.addModelComponent(vs);
	oOModel.addModelComponent(R1);
	oOModel.addModelComponent(R2);
	oOModel.addModelComponent(R3);
	oOModel.addModelComponent(L1);
	oOModel.addModelComponent(gnd);
	oOModel.addConnection(c1);
	oOModel.addConnection(c2);
	oOModel.addConnection(c3);
	
	return oOModel;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:34:20 PM)
 * @return ncbc.physics2.component.Model
 */
public static OOModel getExampleTriangle_a_b() throws org.vcell.expression.ExpressionException {

	//
	//
	//                     
	//                               |
	//                              b|
	//                               |
	//                      a   ----------   area
	//                  -------| triangle |-------
	//                         |__________|
	//                               |
	//                              h|
	//                               |
	//
	//
	OOModel oOModel = new OOModel();

	ModelComponent triangle = new ModelComponent("triangle");
	Variable tri_a = new Variable("a");
	Variable tri_b = new Variable("b");
	Variable tri_h = new Variable("h");
	Variable tri_area = new Variable("area");
	triangle.addSymbol(tri_a);
	triangle.addSymbol(tri_b);
	triangle.addSymbol(tri_h);
	triangle.addSymbol(tri_area);
	triangle.addEquation(ExpressionFactory.createExpression("a*a+b*b-h*h"));
	triangle.addEquation(ExpressionFactory.createExpression("area - a*b/2"));
	triangle.setConnectors(new Connector[] { 
		new Connector(triangle,"pin_a",tri_a,null),
		new Connector(triangle,"pin_b",tri_b,null),
		new Connector(triangle,"pin_h",tri_h,null),
		new Connector(triangle,"pin_area",tri_area,null) } );

	PointSource source_a = new PointSource("source_a",3);
	PointSource source_b = new PointSource("source_b",4);
	
	Connection c1 = new Connection(new Connector[] { source_a.getConnectors(0), triangle.getConnectors(0) });
	Connection c2 = new Connection(new Connector[] { source_b.getConnectors(0), triangle.getConnectors(1) });

	oOModel.addModelComponent(triangle);
	oOModel.addModelComponent(source_a);
	oOModel.addModelComponent(source_b);
	oOModel.addConnection(c1);
	oOModel.addConnection(c2);
	
	return oOModel;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:34:20 PM)
 * @return ncbc.physics2.component.Model
 */
public static OOModel getExampleTriangle_h_a() throws org.vcell.expression.ExpressionException {

	//
	//
	//                     
	//                               |
	//                              b|
	//                               |
	//                      a   ----------   area
	//                  -------| triangle |-------
	//                         |__________|
	//                               |
	//                              h|
	//                               |
	//
	//
	OOModel oOModel = new OOModel();

	ModelComponent triangle = new ModelComponent("triangle");
	Variable tri_a = new Variable("a");
	Variable tri_b = new Variable("b");
	Variable tri_h = new Variable("h");
	Variable tri_area = new Variable("area");
	triangle.addSymbol(tri_a);
	triangle.addSymbol(tri_b);
	triangle.addSymbol(tri_h);
	triangle.addSymbol(tri_area);
	triangle.addEquation(ExpressionFactory.createExpression("a*a+b*b-h*h"));
	triangle.addEquation(ExpressionFactory.createExpression("area - a*b/2"));
	triangle.setConnectors(new Connector[] { 
		new Connector(triangle,"pin_a",tri_a,null),
		new Connector(triangle,"pin_b",tri_b,null),
		new Connector(triangle,"pin_h",tri_h,null),
		new Connector(triangle,"pin_area",tri_area,null) } );

	PointSource source_h = new PointSource("source_h",5);
	PointSource source_area = new PointSource("source_area",6);
	
	Connection c1 = new Connection(new Connector[] { source_h.getConnectors(0), triangle.getConnectors(2) });
	Connection c2 = new Connection(new Connector[] { source_area.getConnectors(0), triangle.getConnectors(3) });

	oOModel.addModelComponent(triangle);
	oOModel.addModelComponent(source_h);
	oOModel.addModelComponent(source_area);
	oOModel.addConnection(c1);
	oOModel.addConnection(c2);
	
	return oOModel;
}

/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		//Model model = ModelReader.parse(null);
		//Model model = getExampleTriangle_h_a();
		OOModel oOModel = getExample3();
		System.out.println(ModelReader.print(oOModel));

	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}
}