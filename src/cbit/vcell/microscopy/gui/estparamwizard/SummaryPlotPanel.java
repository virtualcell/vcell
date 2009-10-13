package cbit.vcell.microscopy.gui.estparamwizard;

import javax.swing.BoxLayout;

import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;

public class SummaryPlotPanel extends BoxPanel
{
	private SubPlotPanel plotPanel;
	private FRAPWorkspace frapWorkspace = null;
	public SummaryPlotPanel() {
		super("Simulation Plots among Available Models under Selected ROIs");
			plotPanel = new SubPlotPanel(this);
	        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
	        contentPane.add(plotPanel);
	}
	
	public void setPlotData(DataSource[] argDataSources)
    {
		plotPanel.setPlotData(argDataSources);
    }
	
	public FRAPWorkspace getFrapWorkspace()
    {
    	return frapWorkspace;
    }
    
    public void setFrapWorkspace(FRAPWorkspace frapWorkspace)
	{
		this.frapWorkspace = frapWorkspace;
		plotPanel.setFrapWorkspace(frapWorkspace);
	}
}
