/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.batchrun.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

/**
 * A Panel shows the information of the running status.
 * It can be turned on and off.
 * @author Tracy Li 
 * @version 1.0
 */
@SuppressWarnings("serial")
public class JobStatusPanel extends JPanel implements ActionListener
{
	public static final String STATUSPANEL_PROPERTY_CHANGE = "STATUSPANEL_PROPERTY";
    public URL[] iconFiles = {getClass().getResource("/images/close.gif"),
    						  getClass().getResource("/images/clear.gif")};
    
    public String[] buttonLabels = {"close", "clear"};
    //icon objects for initializ the image button
    private ImageIcon[] icons = new ImageIcon[iconFiles.length];
    private JButton closeButton, clearButton;

    private JPanel statusPanel = null;
    private JScrollPane statusPane = null;
    private JToolBar toolbar = null;
    private BatchRunDisplayPanel parent = null;

    public JobStatusPanel(BatchRunDisplayPanel arg_parent)
    {
        super();
        parent = arg_parent;
        
        setLayout(new BorderLayout());
        add(getToolBar(),BorderLayout.WEST);
        add(getStatusPane(),BorderLayout.CENTER);
    } //constructor
    
    public JToolBar getToolBar()
    {
    	if(toolbar == null)
    	{
	    	toolbar=new JToolBar(JToolBar.VERTICAL);
	        //System.out.println(buttonLabels.length);
	        for (int i = 0; i < buttonLabels.length; ++i)
	        {
	            icons[i]=new ImageIcon(iconFiles[i]);
	        }
	        closeButton=new JButton(icons[0]);
	        closeButton.setMargin(new Insets(0, 0, 0, 0));
	        closeButton.setToolTipText(buttonLabels[0]);
	        closeButton.setBorderPainted(false);
	        closeButton.addActionListener(this);
	        clearButton=new JButton(icons[1]);
	        clearButton.setMargin(new Insets(0, 0, 0, 0));
	        clearButton.setToolTipText(buttonLabels[1]);
	        clearButton.setBorderPainted(false);
	        clearButton.addActionListener(this);
	       
	        toolbar.add(closeButton);
	        toolbar.add(clearButton);
	        toolbar.setFloatable(false);
    	}
    	return toolbar;
       
    }//end of method setupToolBar()

    public JScrollPane getStatusPane()
    {
    	if(statusPane ==  null)
    	{
    		statusPanel=new JPanel();
    		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
    		statusPanel.setBackground(Color.white);
            statusPane=new JScrollPane(statusPanel);
    	}
    	return statusPane;
    }
    
    public void appendMessage(JPanel msgPanel)
    {
    	msgPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusPanel.add(msgPanel);
        SwingUtilities.invokeLater(new Runnable() {
        	public void run() {
        		statusPane.getVerticalScrollBar().setValue(statusPane.getVerticalScrollBar().getMaximum());
        	}
        });
        statusPanel.updateUI();
    }

    public void clearMessage()
    {
        statusPanel.removeAll();
        statusPanel.updateUI();
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
  	    if (source == closeButton)
	    {
  	    	Boolean wasVisible = new Boolean(true);
  	    	Boolean isVisible = new Boolean(false);
            parent.hideJobStatusPanel();
            firePropertyChange(STATUSPANEL_PROPERTY_CHANGE, wasVisible, isVisible );
        }
        else if(source == clearButton)
        {
        	clearMessage();
        }
    }
}//end of Class JobStatusPanel

