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

import javax.swing.BoxLayout;

import org.vcell.util.gui.BoxPanel;

import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.modelopt.DataSource;

@SuppressWarnings("serial")
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
