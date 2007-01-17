package cbit.vcell.constraints.gui;
import cbit.vcell.constraints.ConstraintContainerImpl;
import cbit.vcell.constraints.ConstraintContainerImplTest;
import cbit.vcell.constraints.ConstraintSolver;
/**
 * Insert the type's description here.
 * Creation date: (5/14/2003 3:34:36 PM)
 * @author: Jim Schaff
 */
public class ConstraintPanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ConstraintPanel aConstraintPanel;
		aConstraintPanel = new ConstraintPanel();
		frame.setContentPane(aConstraintPanel);
		frame.setSize(aConstraintPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});

		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);

		ConstraintContainerImpl constraintContainerImpl1 = ConstraintContainerImplTest.getExample();
		ConstraintContainerImpl constraintContainerImpl2 = ConstraintContainerImplTest.getMichaelisMentenExample();
		System.out.println("setting constraintSolver1");
		aConstraintPanel.setConstraintSolver(new ConstraintSolver(constraintContainerImpl1));
		Thread.sleep(10000);
		System.out.println("setting constraintSolver2");
		aConstraintPanel.setConstraintSolver(new ConstraintSolver(constraintContainerImpl2));
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}