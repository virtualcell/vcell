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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.vcell.util.gui.HyperLinkLabel;
/**
 * The panel contains a PlotPane which displays the profile likelihood data
 * and enables expanding and collapsing of the PlotPane.
 * @author Tracy LI 
 */
public class ProfileDataPlotPanel extends JPanel
{
    private JPanel parent;
    private JLabel profileLabel;
    private ConfidenceIntervalPlotPanel plotPanel;//put plotpane and confidence panel together
    private HyperLinkLabel hypDetail;

    public ProfileDataPlotPanel(JPanel parent, ConfidenceIntervalPlotPanel plotPanel, String paramName) 
    {
    	super();
    	this.parent = parent;
    	this.plotPanel = plotPanel;
        final GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0,0};
        setLayout(gridBagLayout);
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JLabel lblConfHeading = new JLabel("  ");
        lblConfHeading.setFont(new Font("Tahoma", Font.BOLD, 11));
        hypDetail = new HyperLinkLabel("Less Details", new HyperLinkListener(), 0);
        hypDetail.setHorizontalAlignment(JLabel.RIGHT);
        
        profileLabel = new JLabel(paramName);
        profileLabel.setOpaque(true);
        profileLabel.setBackground(new Color(166, 166, 255));
        profileLabel.setBorder(BorderFactory.createEmptyBorder(1,5,1,1));

        GridBagConstraints gc1 = new GridBagConstraints();
        gc1.gridx = 0;
        gc1.gridy = 0;
        gc1.weightx = 1.0;
        gc1.anchor = GridBagConstraints.WEST;
        gc1.fill = GridBagConstraints.HORIZONTAL;
        add(lblConfHeading, gc1);

        GridBagConstraints gc2 = new GridBagConstraints();
        gc2.fill = GridBagConstraints.HORIZONTAL;
        gc2.gridx = 1;
        gc2.gridy = 0;
        gc2.anchor = GridBagConstraints.EAST;
        add(hypDetail, gc2);

        GridBagConstraints gc3 = new GridBagConstraints();
        gc3.anchor = GridBagConstraints.WEST;
        gc3.gridx = 0;
        gc3.gridy = 1; 
        gc3.gridwidth = 2;
        gc3.weightx = 1.0;
        gc3.fill = GridBagConstraints.BOTH;
        add(profileLabel, gc3);
         //by default expand panel
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 1;
        gc.gridwidth = 2;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        add(plotPanel,gc);
//        if(paramName.equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE]))
//        {
        	setDetail(true);
//        }
//        else
//        {
//        	setDetail(false);
//        	hypDetail.setText("Details");
//        }
   }



    private class HyperLinkListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            boolean isDetail = hypDetail.getText().equals("Details");
            setDetail(isDetail);
            hypDetail.setText(isDetail? "Less Details" : "Details");
        }

    }

    public void setDetail(boolean isDetail) {
    	if (isDetail) {
            profileLabel.setVisible(false);
            if(plotPanel != null)
            {
            	plotPanel.setVisible(true);
            }
        }
        else {
            if (plotPanel != null) {
            	plotPanel.setVisible(false);
            }

            profileLabel.setVisible(true);
        }
        parent.repaint();
    }
    
    public void setProfileLable(String paramName)
    {
    	profileLabel.setText(paramName);
    }
}
