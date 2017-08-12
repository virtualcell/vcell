/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * This toolbar contains all the short cut buttons including open, save , print,
 * crop, refresh, curve fitting, runing simulation, report and help etc.
 *
 * @author Tracy Li
 * Created in Jan 2008.
 * @version 1.0 
 */


@SuppressWarnings("serial")
public class ToolBar extends JToolBar {
    // the image of the shortcut button appearance
	public final static int BUT_NEW = 0;
	public final static int BUT_OPEN = 1;
	public final static int BUT_SAVE = 2;
//	public final static int BUT_PRINT = 2;
	public final static int BUT_HELP = 3;
	public final static int BUT_RUN = 4;
    private URL[] iconFiles = {getClass().getResource("/images/new.gif"),
    		                   getClass().getResource("/images/open.gif"),
							   getClass().getResource("/images/save.gif"),
//							   getClass().getResource("/images/printer.gif"),
							   getClass().getResource("/images/help.gif"),
							   getClass().getResource("/images/run.gif")};

    private String[] buttonLabels = {"Create new batchrun","Open File", "Save File", /*"Print",*/"Help","Run batch files"};
    //icon objects for initializing the image button
    private ImageIcon[] icons = new ImageIcon[iconFiles.length];
    private JButton[] buttons = new JButton[iconFiles.length];
    
    //constructor
    public ToolBar() {
        // add short cut bottons on toolbar
        for (int i = 0; i < buttonLabels.length; ++i) {
            icons[i] = new ImageIcon(iconFiles[i]);
            buttons[i] = new JButton(icons[i]);
            buttons[i].setMargin(new Insets(0, 0, 0, 0));
            buttons[i].setSize(100, 100);
            buttons[i].setToolTipText(buttonLabels[i]);
            if(i == BUT_HELP || i == BUT_RUN)
            {
            	addSeparator();
            }
            add(buttons[i]);            
            if(i == BUT_SAVE)
            {
            	buttons[i].setEnabled(false);
            }
        }
    }// end of constructor
    
    public JButton[] getButtons()
    {
    	return buttons;
    }
    
    //Find button's index in bar
    public int findIndex(JButton butt)
    {
        for(int i=0;i<buttons.length;i++)
        {
            if(butt==buttons[i])
            {
            	return i;
            }
        }
        return -1;
    }// end of method findIndex()
    
    public void setNewAndRunButtonVisible(boolean bVisible)
    {
    	if(buttons != null && buttons.length == buttonLabels.length)
    	{
    		buttons[BUT_NEW].setVisible(bVisible);
    		buttons[BUT_RUN].setVisible(bVisible);
    	}
    }
    
    public void addToolBarHandler(ActionListener th)
    {
    	for(int i=0;i<buttons.length;i++)
    	{
    		buttons[i].addActionListener(th);
    	}
    }// end of method addToolBarHandler()
    
} // End of class ToolBar
