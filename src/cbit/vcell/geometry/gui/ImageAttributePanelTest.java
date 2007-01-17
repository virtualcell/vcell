package cbit.vcell.geometry.gui;

/**
 * Insert the type's description here.
 * Creation date: (6/19/2002 3:10:42 PM)
 * @author: Jim Schaff
 */
public class ImageAttributePanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ImageAttributePanel aCreateImagePanel;
		aCreateImagePanel = new ImageAttributePanel();
		frame.setContentPane(aCreateImagePanel);
		frame.setSize(aCreateImagePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aCreateImagePanel.setImage(cbit.vcell.geometry.GeometryTest.getImageExample3D().getGeometrySpec().getImage());
		//aCreateImagePanel.setImage(cbit.vcell.geometry.GeometryTest.getImageExample2D().getImage());
		
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
