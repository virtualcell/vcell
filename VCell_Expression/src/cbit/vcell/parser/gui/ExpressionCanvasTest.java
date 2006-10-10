package cbit.vcell.parser.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

import cbit.vcell.parser.*;
/**
 * This type was created in VisualAge.
 */
public class ExpressionCanvasTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ExpressionCanvas aExpressionCanvas;
		aExpressionCanvas = new ExpressionCanvas();
		frame.setContentPane(aExpressionCanvas);
		frame.setSize(aExpressionCanvas.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		IExpression expList[] = new IExpression[2];
		IExpression exp = ExpressionFactory.createExpression("((1/2) * (pow(hello/thereagain,2)*there - 5))/4/2;");
		IExpression labelExp = ExpressionFactory.createExpression("d/dt;");
		expList[0] = ExpressionFactory.assign(labelExp,exp);
		System.out.println(expList[0]);
		IExpression exp2 = ExpressionFactory.createExpression("8 * ((x > -5) && (x != 5) && (y >= -5) && (y < 5))");
		IExpression labelExp2 = ExpressionFactory.createExpression("d/dt;");
		expList[1] = ExpressionFactory.assign(labelExp2,exp2);
		System.out.println(expList[1]);
		aExpressionCanvas.setExpressions(expList);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.ReactionCanvas");
		exception.printStackTrace(System.out);
	}
}
}
