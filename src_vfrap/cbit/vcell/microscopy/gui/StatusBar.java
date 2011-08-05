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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EtchedBorder;

/**
 * The StatusBar is located at the bottom of VirtualFrapMainFrame. It has the following
 * functions: 1.show messages at the left side of the status bar. 2. show
 * coordinates of the mouse location at the centre of the statuas bar if 
 * needed. 3. show progress at the right side of the status bar.
 *
 * @author Tracy Li
 * @version 1.0
 * Created in Jan 2008.
 */

@SuppressWarnings("serial")
public class StatusBar extends JPanel implements Runnable 
{
  // instance variables
  protected JLabel msg = new JLabel();
  public JProgressBar progress = new JProgressBar();
  protected String statusText;
//protected JLabel coordinates = new JLabel("[X:     ]"+" "+"[Y:     ]");
  
  // constructor
  public StatusBar() {
    progress.setMinimum(0);
    progress.setMaximum(100);
    progress.setMinimumSize(new Dimension(100, 20));
    progress.setSize(new Dimension(100, 20));
    progress.setStringPainted(true);
    //set to false, because it is not ready to display progress yet. remove this when progress can be properly displayed
    progress.setVisible(false);
    JPanel proPanel=new JPanel();
    proPanel.setLayout(new GridLayout(1,2));
    proPanel.add(new JLabel(" "));
    proPanel.add(progress);

    msg.setMinimumSize(new Dimension(300, 20));
    msg.setSize(new Dimension(300, 20));
    msg.setForeground(Color.black);

    setLayout(new GridLayout(1,3));

    setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    add(msg);
//  add(coordsPanel);
    add(proPanel);
  }

  public void showStatus(String s) {
    msg.setText(s);
    paintImmediately(msg.getBounds());
  }

  public void showProgress(int percent)
  {
      if (progress.getMaximum() != 100)
          progress.setMaximum(100);    
      progress.setValue(percent);
      progress.setString(percent + " %");
  }
    
  public void incProgress(int delataPercent) {
    progress.setValue(progress.getValue() + delataPercent);
  }

  public synchronized void doFakeProgress(String s, int work) {
    statusText = s;
    progress.setMaximum(work);
    progress.setValue(0);
    Thread t = new Thread(this);
    t.start();
  }

  public synchronized void run() {
    int work = progress.getMaximum();
    for (int i = 0; i < 100; i++) {
      progress.setValue(i);
      repaint();
      try { wait(10*work); }
      catch (Exception ex) { }
    }
    repaint();
    try { wait(500); }
    catch (Exception ex) { }
    progress.setValue(0);
    repaint();
  }

} // end of class StatusBar
