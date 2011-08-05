/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.estparamwizard;


import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.modelopt.gui.DataSource;

/**
 * 
 */
@SuppressWarnings("serial")
public class EstParams_CompareResultsPanel extends JPanel implements PropertyChangeListener{

    private AnalysisResultsPanel anaResultsPanel;
    private SummaryPlotPanel sumPlotPanel;
    private MSEPanel msePanel;
    private FitModelPanel radioButtonPanel;
    private JScrollPane scrollPane;
    private JPanel innerPanel;//put in the scroPane
    private FRAPSingleWorkspace frapWorkspace;
    
    public EstParams_CompareResultsPanel() 
    {
    	super();
    	setLayout(new BorderLayout());
    	innerPanel= new JPanel();
    	innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
    	innerPanel.add(getAnalysisResultsPanel());
    	getAnalysisResultsPanel().getAnaResultsTablePanel().addPropertyChangeListener(this);
    	innerPanel.add(getSummaryPlotPanel());
    	innerPanel.add(getMSEPanel());
    	innerPanel.add(getRadioButtonPanel());
    	
    	//make innerPanel scrollable
    	scrollPane = new JScrollPane(innerPanel);
    	scrollPane.setAutoscrolls(true);
    	scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    	//add scrollpane to the panel
    	add(scrollPane, BorderLayout.CENTER);
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
    
    public void setPlotData(DataSource[] argDataSources)
    {
		sumPlotPanel.setPlotData(argDataSources);
    }
    
    public FRAPSingleWorkspace getFrapWorkspace()
    {
    	return frapWorkspace;
    }
    
    public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace)
	{
		this.frapWorkspace = frapWorkspace;
		getAnalysisResultsPanel().setFrapWorkspace(frapWorkspace);
		getMSEPanel().setFrapWorkspace(frapWorkspace);
		getSummaryPlotPanel().setFrapWorkspace(frapWorkspace);
	}
    
    public void setBestModelRadioButton(int bestModel)
    {
    	getRadioButtonPanel().setBestModelRadioButton(bestModel);
    }
    
    public void disableAllRadioButtons()
    {
    	getRadioButtonPanel().disableAllRadioButtons();
    }
    
    public int getSelectedConfidenceIndex()
    {
    	return getAnalysisResultsPanel().getSelectedConfidenceIndex();
    }
    
    public void enableRadioButton(int modelIdx)
	{
    	getRadioButtonPanel().enableRadioButton(modelIdx);
	}
    
	public void propertyChange(PropertyChangeEvent evt)
	{
		if(evt.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_BEST_MODEL_WITH_SIGNIFICANCE) && evt.getNewValue() != null)
		{
			int bestModelIdx = ((Integer)evt.getNewValue()).intValue();
			setBestModelRadioButton(bestModelIdx);
		}
	}
	
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			EstParams_CompareResultsPanel aPanel = new EstParams_CompareResultsPanel();
			frame.setContentPane(aPanel);
	//				frame.pack();
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
