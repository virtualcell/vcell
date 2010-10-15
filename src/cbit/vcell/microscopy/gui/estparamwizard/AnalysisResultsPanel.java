package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;

import cbit.vcell.microscopy.FRAPSingleWorkspace;

public class AnalysisResultsPanel extends BoxPanel
{
	private AnalysisResultsTablePanel anaResultsTablePanel;
	FRAPSingleWorkspace frapWorkspace = null; 
	
	public AnalysisResultsPanel() 
	{
		super("Analysis Parameters among Models");
		setName("");
		anaResultsTablePanel = new AnalysisResultsTablePanel(this);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(anaResultsTablePanel);
	}
	
	public AnalysisResultsTablePanel getAnaResultsTablePanel() {
		return anaResultsTablePanel;
	}

	public FRAPSingleWorkspace getFrapWorkspace()
    {
    	return frapWorkspace;
    }
    
    public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace)
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

	public int getSelectedConfidenceIndex() {
		return anaResultsTablePanel.getSelectedConfidenceIndex();
	}
}
