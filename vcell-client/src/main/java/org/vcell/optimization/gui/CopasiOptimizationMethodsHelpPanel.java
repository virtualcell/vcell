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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.vcell.optimization.CopasiOptimizationMethodsDescription;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.HyperLinkLabel;

import cbit.vcell.opt.CopasiOptimizationMethod.CopasiOptimizationMethodType;


/**
 * This the simple help panel to show information about COPASI optimization methods.
 * The panel is splited into left and right panels. The left side panel show the contents
 * of all the COPASI optimiztion methods and right side panel shows the method description
 * and the input parameters according to the selection made on left side. 
 * 
 * @author Tracy LI
 * Created in Sept. 2011
 * @version 1.0
 */
@SuppressWarnings("serial")
public class CopasiOptimizationMethodsHelpPanel extends JPanel
{
	private JPanel messagePanel = null;
	private JPanel contentPanel = null;
	private JTextPane infoTextPane = null;
	private HyperLinkLabel[] methodLabels = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private class InternalEventHandler implements ActionListener, PropertyChangeListener, HyperlinkListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() instanceof HyperLinkLabel)
			{
				String methodStr = ((HyperLinkLabel)(e.getSource())).getText();
				refreshSolverInfo(methodStr);
			}
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() instanceof HyperLinkLabel)
			{
				String methodStr = ((HyperLinkLabel)evt.getSource()).getText();
				refreshSolverInfo(methodStr);
			}
		}
		public void hyperlinkUpdate(HyperlinkEvent e) 
		{
				HyperlinkEvent.EventType type = e.getEventType();
				if(type == HyperlinkEvent.EventType.ACTIVATED)
				{
					URL url = e.getURL();
					if (url != null) {
						String urlString = url.toExternalForm();
						DialogUtils.browserLauncher(CopasiOptimizationMethodsHelpPanel.this,urlString,null);
					}
				}
		}
	}
	
	public CopasiOptimizationMethodsHelpPanel() {
		super();
		initialize();
	}

	private void initialize() 
	{
		setLayout(new BorderLayout());
		getHyperLinkLabels();
		JScrollPane scrollContent = new JScrollPane(getContentPanel());
		JScrollPane scrollMessage = new JScrollPane(getMessagePanel());
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollContent, scrollMessage);
		split.setDividerLocation(170);
		split.setDividerSize(2);
		this.add(split, BorderLayout.CENTER);
	}

	private void getHyperLinkLabels() 
	{
		CopasiOptimizationMethodType[] methods = CopasiOptimizationMethodType.values();
		methodLabels = new HyperLinkLabel[methods.length];
		for(int i=0; i<methods.length; i++)
		{
			methodLabels[i] = new HyperLinkLabel(methods[i].getDisplayName(), eventHandler, 0);
		}
	}

	private JPanel getContentPanel()
	{
		if(contentPanel == null)
		{
			contentPanel = new JPanel(new GridBagLayout());
			contentPanel.setBackground(Color.white);
			int gridy = 0;
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = gridy;
			gbc.insets = new java.awt.Insets(0, 4, 10, 4);
			gbc.anchor = GridBagConstraints.CENTER;
			JLabel copasiOptLabel = new JLabel("COPASI Optimizatin Methods");
			copasiOptLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
			contentPanel.add(new JLabel("COPASI Optimizatin Methods"), gbc);

			for(int i=0; i<CopasiOptimizationMethodType.values().length; i++)
			{
				gridy ++;
				gbc = new GridBagConstraints();
				gbc.gridx = 0; 
				gbc.gridy = gridy;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.anchor = GridBagConstraints.LINE_START;
				contentPanel.add(methodLabels[i], gbc);
			}
		}
		return contentPanel;
	}
	
	private JPanel getMessagePanel()
	{
		if(messagePanel == null)
		{
			messagePanel = new JPanel(new BorderLayout());
			infoTextPane = new JTextPane();
			infoTextPane.setContentType("text/html");
			infoTextPane.addHyperlinkListener(eventHandler);
			
			infoTextPane.setCaretPosition(0);
			infoTextPane.setEditable(false);
			infoTextPane.setFont(infoTextPane.getFont().deriveFont(Font.BOLD));
			infoTextPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
			infoTextPane.setPreferredSize(new Dimension(520,400));
			
			messagePanel.add(infoTextPane, BorderLayout.CENTER);
			
			
		}
		return messagePanel;
	}
	
	public void refreshSolverInfo(CopasiOptimizationMethodType methodType)
	{
		updateHyperLinkLabel(methodType.getDisplayName());
		String methodInfo = CopasiOptimizationMethodsDescription.copasiMethodToHTMLInfoMap.get(methodType);
		infoTextPane.setText(methodInfo);
		infoTextPane.setCaretPosition(0);
	}

	public void refreshSolverInfo(String displayName)
	{
		
		if(displayName.equals("Evolutionary Programming"))
		{
			refreshSolverInfo(CopasiOptimizationMethodType.EvolutionaryProgram);
		}
		else if(displayName.equals("Evolution Strategy (SRES)"))
		{
			refreshSolverInfo(CopasiOptimizationMethodType.SRES);
		}
		else if(displayName.equals("Genetic Algorithm"))
		{
			refreshSolverInfo(CopasiOptimizationMethodType.GeneticAlgorithm);
		}
		else if(displayName.equals("Genetic Algorithm SR"))
		{
			refreshSolverInfo(CopasiOptimizationMethodType.GeneticAlgorithmSR);
		}
		else if(displayName.equals("Hooke & Jeeves"))
		{
			refreshSolverInfo(CopasiOptimizationMethodType.HookeJeeves);
		}
		else if(displayName.equals("Levenberg - Marquardt"))
		{
			refreshSolverInfo(CopasiOptimizationMethodType.LevenbergMarquardt);
		}
		else if(displayName.equals("Nelder - Mead"))
		{
			refreshSolverInfo(CopasiOptimizationMethodType.NelderMead);
		}
		else if(displayName.equals("Particle Swarm"))
		{
			refreshSolverInfo(CopasiOptimizationMethodType.ParticleSwarm);
		}
		else if(displayName.equals("Random Search"))
		{
			refreshSolverInfo(CopasiOptimizationMethodType.RandomSearch);
		}
		else if(displayName.equals("Simulated Annealing"))
		{
			refreshSolverInfo(CopasiOptimizationMethodType.SimulatedAnnealing);
		}
		else if(displayName.equals("Steepest Descent"))
		{
			refreshSolverInfo(CopasiOptimizationMethodType.SteepestDescent);
		}
		else if(displayName.equals("Praxis"))
		{
			refreshSolverInfo(CopasiOptimizationMethodType.Praxis);
		}
		else if(displayName.equals("Truncated Newton"))
		{
			refreshSolverInfo(CopasiOptimizationMethodType.TruncatedNewton);
		}
	}
	
	private void updateHyperLinkLabel(String methodStr) 
	{
		CopasiOptimizationMethodType[] methods = CopasiOptimizationMethodType.values();
		for(int i=0; i<methods.length; i++)
		{
			if(methodStr.equals(methods[i].getDisplayName()))
			{
				methodLabels[i].setColor(Color.red);
			}
			else
			{
				methodLabels[i].setColor(Color.blue);
			}
		}
	}
	
	public static void main(String[] argc)
	{
		
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			CopasiOptimizationMethodsHelpPanel aPanel = new CopasiOptimizationMethodsHelpPanel();
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
