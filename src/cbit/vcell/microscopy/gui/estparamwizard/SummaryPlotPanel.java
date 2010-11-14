package cbit.vcell.microscopy.gui.estparamwizard;

import javax.swing.BoxLayout;

import org.vcell.util.gui.BoxPanel;

import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;

public class SummaryPlotPanel extends BoxPanel
{
	private SubPlotPanel plotPanel;
	private FRAPSingleWorkspace frapWorkspace = null;
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
	
	public FRAPSingleWorkspace getFrapWorkspace()
    {
    	return frapWorkspace;
    }
    
    public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace)
	{
		this.frapWorkspace = frapWorkspace;
		plotPanel.setFrapWorkspace(frapWorkspace);
	}
}
