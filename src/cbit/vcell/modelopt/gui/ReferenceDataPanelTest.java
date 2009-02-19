package cbit.vcell.modelopt.gui;
/**
 * Insert the type's description here.
 * Creation date: (8/23/2005 5:13:11 PM)
 * @author: Jim Schaff
 */
public class ReferenceDataPanelTest {
/**
 * ReferenceDataPanelTest constructor comment.
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ReferenceDataPanel aReferenceDataPanel;
		aReferenceDataPanel = new ReferenceDataPanel();
		frame.setContentPane(aReferenceDataPanel);
		frame.setSize(aReferenceDataPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);

		String dataString = "SimpleReferenceData { 3 2 t Ca_er 1 1 0 0.0 0.1 1 0.2 3 }";
		cbit.vcell.opt.SimpleReferenceData refData = cbit.vcell.opt.SimpleReferenceData.fromVCML(new cbit.util.CommentStringTokenizer(dataString));
		aReferenceDataPanel.setReferenceData(refData);
		System.out.println("setting 1");
		Thread.sleep(3000);
		aReferenceDataPanel.setReferenceData(null);
		System.out.println("setting 2");
		Thread.sleep(3000);
		aReferenceDataPanel.setReferenceData(refData);
		System.out.println("setting 3");
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}