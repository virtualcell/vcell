package org.vcell.optimization.gui;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;

import org.vcell.util.gui.BoxPanel;


import cbit.plot.PlotPane;

public class ProfileDataPanel extends BoxPanel
{
	private ProfileDataPlotPanel profileDataPlotPanel;
	
	public ProfileDataPanel(ConfidenceIntervalPlotPanel plotPanel, String paramName) 
	{
		super("Profile Likelihood of " + paramName);
		setName("");
		profileDataPlotPanel = new ProfileDataPlotPanel(this, plotPanel, paramName);
        contentPane.setLayout(new BorderLayout());
        contentPane.add(profileDataPlotPanel, BorderLayout.CENTER);
	}
	
	public void setProfileDataPlotDetailsVisible(boolean bVisible)
	{
		profileDataPlotPanel.setDetail(bVisible);
	}
	
}
