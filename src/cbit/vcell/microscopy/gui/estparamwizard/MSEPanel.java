package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;

import cbit.vcell.microscopy.FRAPWorkspace;

public class MSEPanel extends BoxPanel
{
	private MSETablePanel mseTablePanel;
	private FRAPWorkspace frapWorkspace = null;
	public MSEPanel() {
		super("Squared Error among Available Models under Selected ROIs");
			mseTablePanel = new MSETablePanel(this);
	        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
	        contentPane.add(mseTablePanel);
	}
	
	public FRAPWorkspace getFrapWorkspace()
    {
    	return frapWorkspace;
    }
    
    public void setFrapWorkspace(FRAPWorkspace frapWorkspace)
	{
		this.frapWorkspace = frapWorkspace;
		mseTablePanel.setFrapWorkspace(frapWorkspace);
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
