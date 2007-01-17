package cbit.gui.graph;
/**
 * Insert the type's description here.
 * Creation date: (7/8/2003 3:32:30 PM)
 * @author: Jim Schaff
 */
public class SimpleGraphModelPanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SimpleGraphModelPanel aSimpleGraphModelPanel;
		aSimpleGraphModelPanel = new SimpleGraphModelPanel();
		frame.setContentPane(aSimpleGraphModelPanel);
		frame.setSize(aSimpleGraphModelPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		
		cbit.vcell.constraints.ConstraintContainerImpl constraintContainerImpl = cbit.vcell.constraints.ConstraintContainerImplTest.getMichaelisMentenExample();
		cbit.util.graph.Graph constraintGraph = cbit.vcell.constraints.ConstraintContainerImplTest.getConstraintGraph(constraintContainerImpl);
		aSimpleGraphModelPanel.setGraph(constraintGraph);
		
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}