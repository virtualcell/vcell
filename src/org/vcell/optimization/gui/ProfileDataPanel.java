/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization.gui;

import java.awt.BorderLayout;

import org.vcell.util.gui.BoxPanel;

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
