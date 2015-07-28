/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.beans.PropertyVetoException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathSymbolTableFactory;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import cbit.xml.merge.XmlTreeDiff;
import cbit.xml.merge.XmlTreeDiff.DiffConfiguration;
import cbit.xml.merge.gui.TMLPanel;

public class MathDebuggerPanel extends JPanel {

	private JSplitPane jSplitPane = null;
	private JPanel math1Panel = null;
	private JPanel math2Panel = null;
	private JSplitPane jSplitPane1 = null;
	private TMLPanel treeMergePanel = null;
	private MathDescEditor mathDescEditor1 = null;
	private MathDescEditor mathDescEditor2 = null;
	private JPanel buttonPanel = null;
	private JButton compareTreeButton = null;
	private JButton testEquivalencyButton = null;
	private JEditorPane statusEditorPane = null;
	private JButton expandBothButton = null;
	private JScrollPane jScrollPane = null;
	private JPanel jPanel = null;
	private JButton paste = null;
	private JButton flattenButton = null;


	/**
	 * This method initializes 
	 * 
	 */
	public MathDebuggerPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.fill = GridBagConstraints.BOTH;
        gridBagConstraints3.weightx = 1.0D;
        gridBagConstraints3.weighty = 0.0D;
        gridBagConstraints3.gridy = 1;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = GridBagConstraints.BOTH;
        gridBagConstraints1.weighty = 1.0;
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 1.0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(1500, 388));
        this.add(getJSplitPane1(), gridBagConstraints1);
        this.add(getButtonPanel(), gridBagConstraints3);
			
	}

	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setResizeWeight(1.0D);
			jSplitPane.setDividerLocation(500);
			jSplitPane.setLeftComponent(getMath1Panel());
			jSplitPane.setRightComponent(getMath2Panel());
		}
		return jSplitPane;
	}

	public void setMathModel1(MathModel mathModel){
		getMathDescEditor1().setMathDescription(mathModel.getMathDescription());
	}
	
	public void setMathModel2(MathModel mathModel){
		getMathDescEditor2().setMathDescription(mathModel.getMathDescription());
	}
	
	public MathModel getMathModel1() throws PropertyVetoException {
		MathDescription mathDesc = mathDescEditor1.getMathDescription();
		if (mathDesc==null){
			return null;
		}else{
			MathModel mathModel = new MathModel(null);
			mathModel.setName("MATH 1");
			mathModel.setMathDescription(mathDesc);
			return mathModel;
		}
	}
	
	public MathModel getMathModel2() throws PropertyVetoException {
		MathDescription mathDesc = mathDescEditor2.getMathDescription();
		if (mathDesc==null){
			return null;
		}else{
			MathModel mathModel = new MathModel(null);
			mathModel.setName("MATH 2");
			mathModel.setMathDescription(mathDesc);
			return mathModel;
		}
	}
	
	/**
	 * This method initializes math1Panel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMath1Panel() {
		if (math1Panel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			math1Panel = new JPanel();
			math1Panel.setLayout(new GridBagLayout());
			math1Panel.add(getMathDescEditor1(), gridBagConstraints);
		}
		return math1Panel;
	}

	/**
	 * This method initializes math2Panel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMath2Panel() {
		if (math2Panel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.weighty = 1.0D;
			gridBagConstraints2.weightx = 1.0D;
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 0;
			math2Panel = new JPanel();
			math2Panel.setLayout(new GridBagLayout());
			math2Panel.add(getMathDescEditor2(), gridBagConstraints2);
		}
		return math2Panel;
	}

	/**
	 * This method initializes jSplitPane1	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane1() {
		if (jSplitPane1 == null) {
			jSplitPane1 = new JSplitPane();
			jSplitPane1.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			jSplitPane1.setLeftComponent(getJSplitPane());
			jSplitPane1.setRightComponent(getTMLPanel());
			jSplitPane1.setDividerLocation(1000);
		}
		return jSplitPane1;
	}

	/**
	 * This method initializes TMLPanel	
	 * 	
	 * @return cbit.xml.merge.TMLPanel	
	 */
	private TMLPanel getTMLPanel() {
		if (treeMergePanel == null) {
			treeMergePanel = new TMLPanel();
		}
		return treeMergePanel;
	}

	/**
	 * This method initializes mathDescEditor1	
	 * 	
	 * @return cbit.vcell.math.gui.MathDescEditor	
	 */
	private MathDescEditor getMathDescEditor1() {
		if (mathDescEditor1 == null) {
			mathDescEditor1 = new MathDescEditor();
			mathDescEditor1
					.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
						public void propertyChange(java.beans.PropertyChangeEvent e) {
							if ((e.getPropertyName().equals("mathDescription"))) {
								System.out.println("MATH1: propertyChange(mathDescription)"); // TODO Auto-generated property Event stub "mathDescription" 
							}
						}
					});
		}
		return mathDescEditor1;
	}

	/**
	 * This method initializes mathDescEditor2	
	 * 	
	 * @return cbit.vcell.math.gui.MathDescEditor	
	 */
	private MathDescEditor getMathDescEditor2() {
		if (mathDescEditor2 == null) {
			mathDescEditor2 = new MathDescEditor();
			mathDescEditor2
					.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
						public void propertyChange(java.beans.PropertyChangeEvent e) {
							if ((e.getPropertyName().equals("mathDescription"))) {
								System.out.println("MATH2: propertyChange(mathDescription)"); // TODO Auto-generated property Event stub "mathDescription" 
							}
						}
					});
		}
		return mathDescEditor2;
	}

	/**
	 * This method initializes buttonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 0;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.BOTH;
			gridBagConstraints8.gridy = 1;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.weighty = 1.0;
			gridBagConstraints8.gridwidth = 1;
			gridBagConstraints8.gridx = 0;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getJScrollPane(), gridBagConstraints8);
			buttonPanel.add(getJPanel(), gridBagConstraints6);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes compareTreeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCompareTreeButton() {
		if (compareTreeButton == null) {
			compareTreeButton = new JButton();
			compareTreeButton.setText("Compare tree");
			compareTreeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						compareTree();
					} catch (Exception e1) {
						e1.printStackTrace();
						DialogUtils.showErrorDialog(MathDebuggerPanel.this, e1.getMessage(), e1);
					}
				}
			});
		}
		return compareTreeButton;
	}
	
	private void compareTree() throws PropertyVetoException, XmlParseException{
		MathModel mathModel1 = getMathModel1();
		MathModel mathModel2 = getMathModel2();
		if (mathModel1!=null && mathModel2!=null){
			String math1XML = XmlHelper.mathModelToXML(mathModel1);
			String math2XML = XmlHelper.mathModelToXML(mathModel2);
			boolean ignoreVersion = true;
			XmlTreeDiff diffTree = cbit.vcell.xml.XmlHelper.compareMerge(math1XML, math2XML, DiffConfiguration.COMPARE_DOCS_OTHER, ignoreVersion);
			getTMLPanel().setXmlTreeDiff(diffTree);
		}else{
			DialogUtils.showErrorDialog(MathDebuggerPanel.this, "failed");
		}
	}

	/**
	 * This method initializes testEquivalencyButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getTestEquivalencyButton() {
		if (testEquivalencyButton == null) {
			testEquivalencyButton = new JButton();
			testEquivalencyButton.setText("Test equivalent");
			testEquivalencyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						compareEquivalent();
					} catch (Exception e1) {
						e1.printStackTrace();
						DialogUtils.showErrorDialog(MathDebuggerPanel.this, e1.getMessage(), e1);
					}
				}
			});
		}
		return testEquivalencyButton;
	}
	
	private void compareEquivalent() throws PropertyVetoException, XmlParseException {
		MathModel mathModel1 = getMathModel1();
		MathModel mathModel2 = getMathModel2();
		if (mathModel1!=null && mathModel2!=null){
			MathCompareResults mathCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(),mathModel1.getMathDescription(), mathModel2.getMathDescription());
			getStatusEditorPane().setText("equiv = "+mathCompareResults.isEquivalent()+"\n"+"reason = "+mathCompareResults.toDatabaseStatus());
		}else{
			DialogUtils.showErrorDialog(MathDebuggerPanel.this, "failed : at least one math description is null.");
		}
	}

	/**
	 * This method initializes statusEditorPane	
	 * 	
	 * @return javax.swing.JEditorPane	
	 */
	private JEditorPane getStatusEditorPane() {
		if (statusEditorPane == null) {
			statusEditorPane = new JEditorPane();
			statusEditorPane.setMinimumSize(new Dimension(33, 100));
			statusEditorPane.setPreferredSize(new Dimension(1500, 100));
			statusEditorPane.setMaximumSize(new Dimension(2147483647, 100));
		}
		return statusEditorPane;
	}

	/**
	 * This method initializes expandBothButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getExpandBothButton() {
		if (expandBothButton == null) {
			expandBothButton = new JButton();
			expandBothButton.setText("Expand both");
			expandBothButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						expandBoth();
					}catch (Exception e1){
						e1.printStackTrace(System.out);
						DialogUtils.showErrorDialog(MathDebuggerPanel.this, e1.getMessage(), e1);
					}
				}
			});
		}
		return expandBothButton;
	}
	
	private void addStatus(String message){
		getStatusEditorPane().setText(getStatusEditorPane().getText()+message);
	}
	
	private void setStatus(String message){
		getStatusEditorPane().setText(message);
	}
	
	private void expandBoth() throws PropertyVetoException, MappingException, MathException, ExpressionException {
		
		setStatus("Canonical Math for both mathDescriptions: \n");

		MathDescription[] canonicalMathDescs = MathUtilities.getCanonicalMathDescriptions(SimulationSymbolTable.createMathSymbolTableFactory(),getMathModel1().getMathDescription(), getMathModel2().getMathDescription());
		
		MathModel newMathModel = new MathModel(null);
		newMathModel.setName("Math1");
		newMathModel.setMathDescription(canonicalMathDescs[0]);
		setMathModel1(newMathModel);

		newMathModel = new MathModel(null);
		newMathModel.setName("Math2");
		newMathModel.setMathDescription(canonicalMathDescs[1]);
		setMathModel2(newMathModel);
		
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setPreferredSize(new Dimension(1503, 100));
			jScrollPane.setMinimumSize(new Dimension(22, 100));
			jScrollPane.setViewportView(getStatusEditorPane());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new FlowLayout());
			jPanel.add(getPaste(), null);
			jPanel.add(getFlattenButton(), null);
			jPanel.add(getCompareTreeButton(), null);
			jPanel.add(getTestEquivalencyButton(), null);
			jPanel.add(getExpandBothButton(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes paste	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPaste() {
		if (paste == null) {
			paste = new JButton();
			paste.setText("Paste");
			paste.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						paste();
					}catch (Exception e1){
						e1.printStackTrace(System.out);
						DialogUtils.showErrorDialog(MathDebuggerPanel.this, e1.getMessage(), e1);
					}
				}
			});
		}
		return paste;
	}
	
	private void paste() throws PropertyVetoException, MathException, XmlParseException{
		String copiedText = (String)VCellTransferable.getFromClipboard(DataFlavor.stringFlavor);
		if (!copiedText.contains("BEGINMATH:") && !copiedText.contains("ENDMATH")) {
			throw new RuntimeException("Make sure each math description is enclosed with 'BEGINMATH:' and 'ENDMATH' tags for the 'Paste' operation to work.");
		}
		String[] mathStrings = copiedText.split("BEGINMATH:");
		String mathString1 = null;
		String mathString2 = null;
		for (int i = 0; i < mathStrings.length; i++) {
			mathStrings[i] = mathStrings[i].trim();
			if (mathStrings[i].trim().startsWith("MathDescription {")){
				int endIndex = mathStrings[i].indexOf("ENDMATH");
				if (endIndex>0){
					if (mathString1 == null){
						mathString1 = mathStrings[i].substring(0, endIndex);
					}else if (mathString2 == null){
						mathString2 = mathStrings[i].substring(0, endIndex);
					}else{
						throw new RuntimeException("too many math Descriptions (expecting only two)");
					}
				}
			}
		}
		if (mathString1!=null){
			MathDescription mathDesc = MathDescription.fromEditor(getMathDescEditor1().getMathDescription(), mathString1);
			MathModel mathModel = new MathModel(null);
			mathModel.setName("Math1");
			mathModel.setMathDescription(mathDesc);
			setMathModel1(mathModel);
		}
		if (mathString2!=null){
			MathDescription mathDesc = MathDescription.fromEditor(getMathDescEditor2().getMathDescription(), mathString2);
			MathModel mathModel = new MathModel(null);
			mathModel.setName("Math2");
			mathModel.setMathDescription(mathDesc);
			setMathModel2(mathModel);
		}
		compareTree();
	}

	/**
	 * This method initializes flattenButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getFlattenButton() {
		if (flattenButton == null) {
			flattenButton = new JButton();
			flattenButton.setText("Flatten");
			flattenButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						flatten(SimulationSymbolTable.createMathSymbolTableFactory());
					}catch (Exception e1){
						e1.printStackTrace(System.out);
						DialogUtils.showErrorDialog(MathDebuggerPanel.this, e1.getMessage(), e1);
					}
				}
			});
		}
		return flattenButton;
	}
	
	private void flatten(MathSymbolTableFactory mathSymbolTableFactory) throws PropertyVetoException, MathException, ExpressionException, MappingException, XmlParseException{
		MathDescription math1 = getMathModel1().getMathDescription();
		MathDescription newMath1 = MathDescription.createCanonicalMathDescription(mathSymbolTableFactory,math1);
		MathModel newMathModel1 = new MathModel(null);
		newMathModel1.setName("Math1");
		newMathModel1.setMathDescription(newMath1);
		setMathModel1(newMathModel1);
		
		MathDescription math2 = getMathModel2().getMathDescription();
		MathDescription newMath2 = MathDescription.createCanonicalMathDescription(mathSymbolTableFactory,math2);
		MathModel newMathModel2 = new MathModel(null);
		newMathModel2.setName("Math2");
		newMathModel2.setMathDescription(newMath2);
		setMathModel2(newMathModel2);
		
		compareTree();
	}
	
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			MathDebuggerPanel aMathDebuggerPanel;
			aMathDebuggerPanel = new MathDebuggerPanel();
			frame.setContentPane(aMathDebuggerPanel);
			frame.setTitle("Math Descriptions Comparator");
			frame.setSize(aMathDebuggerPanel.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});

			MathModel mathModel1 = new MathModel(null);
			mathModel1.setName("math1");
			Geometry geometry1 = new Geometry("geo",0);
			MathDescription mathDesc1 = mathModel1.getMathDescription();
			mathDesc1.setGeometry(geometry1);
			mathDesc1.addSubDomain(new CompartmentSubDomain("Compartment",CompartmentSubDomain.NON_SPATIAL_PRIORITY));

			MathModel mathModel2 = new MathModel(null);
			mathModel2.setName("math2");
			Geometry geometry2 = new Geometry("geo",0);
			MathDescription mathDesc2 = mathModel2.getMathDescription();
			mathDesc2.setGeometry(geometry2);
			mathDesc2.addSubDomain(new CompartmentSubDomain("Compartment",CompartmentSubDomain.NON_SPATIAL_PRIORITY));

			aMathDebuggerPanel.setMathModel1(mathModel1);
			aMathDebuggerPanel.setMathModel2(mathModel2);
			
			frame.setSize(1500,800);
			frame.setVisible(true);

		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"
