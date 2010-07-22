package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;

import cbit.plot.PlotPane;
import cbit.vcell.microscopy.FRAPSingleWorkspace;

public class ProfileDataPanel extends BoxPanel
{
	private ProfileDataPlotPanel profileDataPlotPanel;
	FRAPSingleWorkspace frapWorkspace = null; 
	
	public ProfileDataPanel(PlotPane plotPane, String paramName) 
	{
		super("Profile Likelihood of " + paramName);
		setName("");
		profileDataPlotPanel = new ProfileDataPlotPanel(this, plotPane, paramName);
        contentPane.setLayout(new BorderLayout());
        contentPane.add(profileDataPlotPanel, BorderLayout.CENTER);
	}
	
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			AnalysisResultsPanel aPanel = new AnalysisResultsPanel();
			frame.setContentPane(aPanel);
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
