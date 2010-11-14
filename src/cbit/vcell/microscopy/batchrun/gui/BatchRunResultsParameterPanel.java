package cbit.vcell.microscopy.batchrun.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BoxLayout;

import org.vcell.util.gui.BoxPanel;

import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;

public class BatchRunResultsParameterPanel extends BoxPanel
{
	private BatchRunResultsParamTablePanel paramTablePanel;
	FRAPBatchRunWorkspace batchRunWorkspace = null; 
	
	public BatchRunResultsParameterPanel() 
	{
		super("Analysis Parameters for Each FRAP Document");
		setName("");
		paramTablePanel = new BatchRunResultsParamTablePanel(this);
        contentPane.setLayout(new BorderLayout());
        contentPane.add(paramTablePanel);
	}
	
	public FRAPBatchRunWorkspace getBatchRunWorkspace()
    {
    	return batchRunWorkspace;
    }
    
    public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace)
	{
		this.batchRunWorkspace = batchRunWorkspace;
		paramTablePanel.setBatchRunWorkspace(batchRunWorkspace);
	}
	
    public void updateTableData()
    {
    	paramTablePanel.updateTableData();
    }
    
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			BatchRunResultsParameterPanel aPanel = new BatchRunResultsParameterPanel();
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
