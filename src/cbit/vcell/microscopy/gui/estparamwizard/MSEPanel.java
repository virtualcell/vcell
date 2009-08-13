package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;

public class MSEPanel extends BoxPanel
{
	private MSETablePanel mseTablePanel;
	public MSEPanel() {
		super("Mean Square Error among Available Models under Selected ROIs");
			mseTablePanel = new MSETablePanel(this);
	        contentPane.setLayout(/*new BorderLayout()*/new BoxLayout(contentPane, BoxLayout.Y_AXIS));
	        contentPane.add(mseTablePanel/*, BorderLayout.CENTER*/);
	}
	
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			MSEPanel aPanel = new MSEPanel();
			frame.setContentPane(aPanel);
//			frame.pack();
			frame.setSize(900,800);
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
			
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}
}
