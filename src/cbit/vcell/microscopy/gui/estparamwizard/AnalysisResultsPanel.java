package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;

import cbit.vcell.microscopy.FRAPWorkspace;

public class AnalysisResultsPanel extends BoxPanel
{
	private AnalysisResultsTablePanel anaResultsTablePanel;
	FRAPWorkspace frapWorkspace = null; 
	
	public AnalysisResultsPanel() 
	{
		super("Analysis Parameters among Models");
		setName("");
		anaResultsTablePanel = new AnalysisResultsTablePanel(this);
        contentPane.setLayout(/*new BorderLayout()*/new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(anaResultsTablePanel/*, BorderLayout.CENTER*/);
	}
	
	public FRAPWorkspace getFrapWorkspace()
    {
    	return frapWorkspace;
    }
    
    public void setFrapWorkspace(FRAPWorkspace frapWorkspace)
	{
		this.frapWorkspace = frapWorkspace;
		anaResultsTablePanel.setFrapWorkspace(frapWorkspace);
	}
	
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			AnalysisResultsPanel aPanel = new AnalysisResultsPanel();
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
