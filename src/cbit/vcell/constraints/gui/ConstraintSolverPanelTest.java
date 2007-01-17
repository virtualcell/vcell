package cbit.vcell.constraints.gui;
/**
 * Insert the type's description here.
 * Creation date: (9/25/2003 11:34:47 AM)
 * @author: Jim Schaff
 */
public class ConstraintSolverPanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ConstraintSolverPanel aConstraintSolverPanel;
		aConstraintSolverPanel = new ConstraintSolverPanel();
		frame.setContentPane(aConstraintSolverPanel);
		frame.setSize(aConstraintSolverPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		
		cbit.vcell.constraints.ConstraintContainerImpl constraintContainerImpl = cbit.vcell.constraints.ConstraintContainerImplTest.getMichaelisMentenExample();
		aConstraintSolverPanel.setConstraintContainerImpl(constraintContainerImpl);
		
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}