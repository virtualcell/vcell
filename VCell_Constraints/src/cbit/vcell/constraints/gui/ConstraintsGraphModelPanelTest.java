package cbit.vcell.constraints.gui;
import cbit.vcell.constraints.ConstraintSolver;
/**
 * Insert the type's description here.
 * Creation date: (7/8/2003 3:32:30 PM)
 * @author: Jim Schaff
 */
public class ConstraintsGraphModelPanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ConstraintsGraphModelPanel aConstraintsGraphModelPanel;
		aConstraintsGraphModelPanel = new ConstraintsGraphModelPanel();
		frame.setContentPane(aConstraintsGraphModelPanel);
		frame.setSize(aConstraintsGraphModelPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		
		cbit.vcell.constraints.ConstraintContainerImpl constraintContainerImpl = cbit.vcell.constraints.ConstraintContainerImplTest.getMichaelisMentenExample();
		aConstraintsGraphModelPanel.setConstraintSolver(new ConstraintSolver(constraintContainerImpl));
		
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}