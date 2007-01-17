package cbit.vcell.client.bionetgen;

/**
 * Insert the type's description here.
 * Creation date: (3/9/2006 11:32:10 AM)
 * @author: Anuradha Lakshminarayana
 */
public class BNGOutputPanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		BNGOutputPanel aBNGOutputPanel;
		aBNGOutputPanel = new BNGOutputPanel();
		frame.setContentPane(aBNGOutputPanel);
		frame.setSize(aBNGOutputPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);

		cbit.vcell.client.server.ClientServerManager csManager = cbit.vcell.client.test.ClientTester.mainInit(args, "BNGOutputPanelTest");
		
		aBNGOutputPanel.setBngService(csManager.getBNGSerivce());
		
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}
}
