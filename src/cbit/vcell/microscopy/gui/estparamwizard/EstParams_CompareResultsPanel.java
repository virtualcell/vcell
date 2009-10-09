package cbit.vcell.microscopy.gui.estparamwizard;


import javax.help.Map;
import javax.swing.*;

import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPWorkspace;

import java.awt.*;
import java.awt.List;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

/**
 * 
 */
public class EstParams_CompareResultsPanel extends JPanel {

    private AnalysisResultsPanel anaResultsPanel;
    private SummaryPlotPanel sumPlotPanel;
    private MSEPanel msePanel;
    private FitModelPanel radioButtonPanel;
    private JScrollPane scroPane;
    private JPanel innerPanel;//put in the scroPane
    private FRAPWorkspace frapWorkspace;
    
    public EstParams_CompareResultsPanel() 
    {
    	super();
    	setLayout(new BorderLayout());
    	innerPanel= new JPanel();
    	innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
    	innerPanel.add(getAnalysisResultsPanel());
    	innerPanel.add(getSummaryPlotPanel());
    	innerPanel.add(getMSEPanel());
    	innerPanel.add(getRadioButtonPanel());
    	
    	//make innerPanel scrollable
    	scroPane = new JScrollPane(innerPanel);
    	scroPane.setAutoscrolls(true);
    	scroPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	scroPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    	//add scrollpane to the panel
    	add(scroPane, BorderLayout.CENTER);
    }

    public FitModelPanel getRadioButtonPanel()
    {
    	if(radioButtonPanel == null)
    	{
    		radioButtonPanel = new FitModelPanel();
    	}
    	return radioButtonPanel;
    }
    
    public AnalysisResultsPanel getAnalysisResultsPanel()
    {
    	if(anaResultsPanel == null)
    	{
    		anaResultsPanel = new AnalysisResultsPanel();
    	}
    	return anaResultsPanel;
    }
    
    public SummaryPlotPanel getSummaryPlotPanel()
    {
    	if(sumPlotPanel == null)
    	{
    		sumPlotPanel = new SummaryPlotPanel();
    	}
    	return sumPlotPanel;
    }
    
    public MSEPanel getMSEPanel()
    {
    	if(msePanel == null)
    	{
    		msePanel = new MSEPanel();
    	}
    	return msePanel;
    }
    
    public FRAPWorkspace getFrapWorkspace()
    {
    	return frapWorkspace;
    }
    
    public void setFrapWorkspace(FRAPWorkspace frapWorkspace)
	{
		this.frapWorkspace = frapWorkspace;
		getAnalysisResultsPanel().setFrapWorkspace(frapWorkspace);
		getMSEPanel().setFrapWorkspace(frapWorkspace);
	}
    
    public void setBestModelRadioButton(int bestModel)
    {
    	getRadioButtonPanel().setBestModelRadioButton(bestModel);
    }
    
    public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			EstParams_CompareResultsPanel aPanel = new EstParams_CompareResultsPanel();
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
