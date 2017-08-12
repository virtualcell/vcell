/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math.validation.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.VCellClient;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.math.gui.MathDebuggerPanel;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modeldb.MathVerifier;
import cbit.vcell.modeldb.MathVerifier.MathGenerationResults;

public class MathGenerationDebugger extends JPanel {
	private JTextField simContextTextField;
	private MathVerifier mathVerifier;
	private MathDebuggerPanel mathDebuggerPanel;
	private JTextPane mathGenTextPane;
	private MathGenerationResults mathGenerationResults;
	private String vcellUserID;
	private String vcellUserPassword;
	
	public MathGenerationDebugger() {
		setLayout(new BorderLayout(0, 0));
		
		mathDebuggerPanel = new MathDebuggerPanel();
		add(mathDebuggerPanel, BorderLayout.CENTER);
		
		JPanel mathGenerationPanel = new JPanel();
		mathGenerationPanel.setPreferredSize(new Dimension(10, 100));
		mathGenerationPanel.setMinimumSize(new Dimension(10, 100));
		add(mathGenerationPanel, BorderLayout.SOUTH);
		GridBagLayout gbl_mathGenerationPanel = new GridBagLayout();
		mathGenerationPanel.setLayout(gbl_mathGenerationPanel);
		
		JPanel mathGenControlPanel = new JPanel();
		GridBagConstraints gbc_mathGenControlPanel = new GridBagConstraints();
		gbc_mathGenControlPanel.insets = new Insets(0, 0, 5, 0);
		gbc_mathGenControlPanel.gridx = 0;
		gbc_mathGenControlPanel.gridy = 0;
		mathGenerationPanel.add(mathGenControlPanel, gbc_mathGenControlPanel);
		mathGenControlPanel.setLayout(new GridLayout(0, 6, 0, 0));
		
		JLabel lblSimcontextid = new JLabel("simContextID");
		mathGenControlPanel.add(lblSimcontextid);
		
		simContextTextField = new JTextField();
		mathGenControlPanel.add(simContextTextField);
		simContextTextField.setColumns(20);
		
		JButton testFromDBButton = new JButton("test from DB");
		testFromDBButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateMath();
			}
		});
		mathGenControlPanel.add(testFromDBButton);
		
		JButton compareMathButton_latest = new JButton("compare math (latest)");
		compareMathButton_latest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				compareMath_latest();
			}
		});
		mathGenControlPanel.add(compareMathButton_latest);
		
		JButton compareMathButton_4_8 = new JButton("compare math (4.8)");
		compareMathButton_4_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				compareMath_4_8();
			}
		});
		mathGenControlPanel.add(compareMathButton_4_8);
		
		JButton openBiomodelButton = new JButton("open BioModel");
		openBiomodelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showBioModel();
			}
		});
		mathGenControlPanel.add(openBiomodelButton);
		
		mathGenTextPane = new JTextPane();
		GridBagConstraints gbc_mathGenTextPane = new GridBagConstraints();
		gbc_mathGenTextPane.weightx = 1.0;
		gbc_mathGenTextPane.weighty = 1.0;
		gbc_mathGenTextPane.fill = GridBagConstraints.BOTH;
		gbc_mathGenTextPane.gridx = 0;
		gbc_mathGenTextPane.gridy = 1;
		mathGenerationPanel.add(mathGenTextPane, gbc_mathGenTextPane);
	}
	
	private void generateMath(){
		try {
			getMathGenTextPane().setText("testing...");
			KeyValue simContextKey = new KeyValue(getSimContextTextField().getText());
			
			mathGenerationResults = mathVerifier.testMathGeneration(simContextKey);
			
			getMathGenTextPane().setText("VCell new: "+mathGenerationResults.mathCompareResults_latest.toDatabaseStatus()+"\n"+
					"VCell 4.8: "+mathGenerationResults.mathCompareResults_4_8.toDatabaseStatus()+"\n");
		} catch (Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, e.getMessage(), e);
		}
	}
	
	private void compareMath_latest(){
		try {
			MathModel mathModel1 = new MathModel(null);
			MathModel mathModel2 = new MathModel(null);
			mathModel1.setMathDescription(mathGenerationResults.mathDesc_original);
			mathModel2.setMathDescription(mathGenerationResults.mathDesc_latest);
			
			getMathDebuggerPanel().setMathModel1(mathModel1);
			getMathDebuggerPanel().setMathModel2(mathModel2);
			
		} catch (Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, e.getMessage(), e);
		}
	}
	
	private void compareMath_4_8(){
		try {
			MathModel mathModel1 = new MathModel(null);
			MathModel mathModel2 = new MathModel(null);
			mathModel1.setMathDescription(mathGenerationResults.mathDesc_original);
			mathModel2.setMathDescription(mathGenerationResults.mathDesc_4_8);
			
			getMathDebuggerPanel().setMathModel1(mathModel1);
			getMathDebuggerPanel().setMathModel2(mathModel2);
			
		} catch (Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, e.getMessage(), e);
		}
	}
	
	private void showBioModel(){
		try {
			new Thread() {
				public void run(){
					ClientServerInfo clientServerInfo = ClientServerInfo.createLocalServerInfo(vcellUserID,new UserLoginInfo.DigestedPassword(vcellUserPassword));
					VCellClient.startClient(mathGenerationResults.bioModelFromDB, clientServerInfo);
				}
			}.start();
			
		}catch (Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, e.getMessage(), e);
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        if (args.length != 6) {
            System.out.println("Usage: host databaseSID schemaUser schemaUserPassword vcellUserID vcellUserPassword");
            System.exit(0);
        }
        String host = args[0];
        String db = args[1];
        String connectURL = "jdbc:oracle:thin:@" + host + ":1521:" + db;
        String dbSchemaUser = args[2];
        String dbPassword = args[3];
        String vcellUserID = args[4];
        String vcellUserPassword = args[5];
         //

        int ok =
            javax.swing.JOptionPane.showConfirmDialog(
                new JFrame(),
                "Will run MathGeneratorDebugger with settings: "
                    + "\nconnectURL="
                    + connectURL
                    + "\nDBSchema="
                    + dbSchemaUser
                    + "\npassword="
                    + dbPassword,
                "Confirm",
                javax.swing.JOptionPane.OK_CANCEL_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE);
        if (ok != javax.swing.JOptionPane.OK_OPTION) {
            throw new RuntimeException("Aborted by user");
        }
        
        try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			MathGenerationDebugger aMathGenerationDebugger = new MathGenerationDebugger();
			frame.setContentPane(aMathGenerationDebugger);
			frame.setTitle("Math Generation Debugger");
			frame.setSize(aMathGenerationDebugger.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
		
			aMathGenerationDebugger.mathVerifier = MathVerifier.createMathVerifier(host, db, dbSchemaUser, dbPassword);
			aMathGenerationDebugger.vcellUserID = vcellUserID;
			aMathGenerationDebugger.vcellUserPassword = vcellUserPassword;
			
			frame.setSize(1500,800);
			frame.setVisible(true);

	 	} catch (Throwable e) {
		    e.printStackTrace(System.out);
		}
	}

	protected JTextField getSimContextTextField() {
		return simContextTextField;
	}
	protected MathDebuggerPanel getMathDebuggerPanel() {
		return mathDebuggerPanel;
	}
	protected JTextPane getMathGenTextPane() {
		return mathGenTextPane;
	}
}
