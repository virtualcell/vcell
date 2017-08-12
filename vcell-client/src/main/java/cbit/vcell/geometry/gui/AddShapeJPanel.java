/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.math.ReservedVariable;

@SuppressWarnings("serial")
public class AddShapeJPanel extends JPanel {
		
	private final JTextField manualTextField;
	public static String PROPCHANGE_VALID_ANALYTIC = "PROPCHANGE_VALID_ANALYTIC";
	public static final String MANUAL_SELECT = "Manual";
	public static final String CIRCLE_SELECT = "Circle";
	public static final String SPHERE_SELECT = "Sphere";
	public static final String RECTANGLE_SELECT = "Rectangle";
	public static final String BOX_SELECT = "Box";
	public static final String CYLINDER_SELECT = "Cylinder";
	public static final String ELLIPSE_SELECT = "Ellipse";
	public static final String ELLIPSOID_SELECT = "Ellipsoid";

	private final JTextField boxUCTextField = new JTextField();
	private final JTextField boxLCTextField = new JTextField();
	private final JTextField circleRadiusTextField = new JTextField();
	private final JTextField circleCenterTextField = new JTextField();
	private final JComboBox<String> comboBox = new JComboBox<>();
	private final JPanel cardPanel = new JPanel();
	private final CardLayout cardLayout = new CardLayout();
	private final JPanel circlePanel = new JPanel();
	private final JPanel boxPanel = new JPanel();
	private final JLabel analyticExprLabel = new JLabel();
	private final JButton copyExpressionTextButton = new JButton();
	private final JTextField axisRadiiTextField;
	private final JTextField ellipseCenterTextField;
	private final JPanel ellipsePanel = new JPanel();
	private final JTextField cylLengthTextField;
	private final JTextField cylRadiusTextField;
	private final JTextField cylStartPointTextField;
	private final JPanel cylinderPanel = new JPanel();
	private final JPanel manualPanel = new JPanel();

	private final JLabel circleCenterLabel = new JLabel();
	private final JLabel boxLCLabel = new JLabel();
	private final JLabel boxUCLabel = new JLabel();
	private final JLabel ellipseCenterLabel = new JLabel();
	private final JLabel ellipseAxisRadiiLabel = new JLabel();

	private final JRadioButton cylXRadioButton = new JRadioButton();
	private final JRadioButton cylYRadioButton = new JRadioButton();
	private final JRadioButton cylZRadioButton = new JRadioButton();
	private ButtonGroup cylAxisButtonGroup = new ButtonGroup();
	
	private class ComboRadioListener implements ActionListener {
		boolean active = true;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (active) {
				execute( );
			}
		}
			
		/**
		 * set panel / label based on selection
		 */
		private void execute( ) {
			//we expect comboBox and getSelectedItem to be non-null based on
			//program logic
			try {
				Object si = comboBox.getSelectedItem();
				JPanel panel = null;

				if(si.equals(MANUAL_SELECT)){
					panel = manualPanel;
				}else if(si.equals(CIRCLE_SELECT) || si.equals(SPHERE_SELECT)){
					panel = circlePanel;
				}else if(si.equals(RECTANGLE_SELECT) || si.equals(BOX_SELECT)){
					panel = boxPanel;
				}else if(si.equals(ELLIPSE_SELECT) || si.equals(ELLIPSOID_SELECT)){
					panel = ellipsePanel; 
				}else if(comboBox.getSelectedItem().equals(CYLINDER_SELECT)){
					panel = cylinderPanel; 
				}
				if (panel != null) {
					cardLayout.show(cardPanel, panel.getName());
				}
				setAnalyticExprLabel();
			} catch (NullPointerException npe) {
				npe.printStackTrace();
			}
		}

		/**
		 * disable (for setting items)
		 */
		void freeze( ) {
			active = false;
		}
		/**
		 * reenable (after settting items)
		 */
		void thaw( ) {
			active = true;
			execute( );
		}
	}
	
	private ComboRadioListener shapeTypeComboBoxActionListener = new ComboRadioListener();
	
	public AddShapeJPanel(int nDimensions) {
		super();
		setLayout(new GridBagLayout());

		final JPanel panel = new JPanel();
		final GridBagLayout gridBagLayout_3 = new GridBagLayout();
		gridBagLayout_3.columnWidths = new int[] {7};
		panel.setLayout(gridBagLayout_3);
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.weighty = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		add(panel, gridBagConstraints);
		
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(0, 0, 10, 0);
		gridBagConstraints_2.ipadx = 50;
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.weightx = 1;
		gridBagConstraints_2.gridx = 1;
		gridBagConstraints_2.gridy = 0;

		final JLabel selectShapeTypeLabel = new JLabel();
		selectShapeTypeLabel.setText("Select Subdomain Shape:");
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.insets = new Insets(0, 0, 0, 5);
		gridBagConstraints_15.gridy = 0;
		gridBagConstraints_15.gridx = 0;
		panel.add(selectShapeTypeLabel, gridBagConstraints_15);
		panel.add(comboBox, gridBagConstraints_2);

	
		cardPanel.setLayout(cardLayout);
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		gridBagConstraints_1.weighty = 1;
		gridBagConstraints_1.weightx = 1;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 0;
		add(cardPanel, gridBagConstraints_1);

		
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,0,7,7};

		final GridBagLayout gridBagLayout_6 = new GridBagLayout();
		gridBagLayout_6.rowHeights = new int[] {0,7};
		manualPanel.setLayout(gridBagLayout_6);
		manualPanel.setName("manualPanel");
		cardPanel.add(manualPanel, manualPanel.getName());

		final JLabel enterAnalyticExpressionLabel = new JLabel();
		enterAnalyticExpressionLabel.setText("Enter an analytic expression defining a shape");
		final GridBagConstraints gridBagConstraints_28 = new GridBagConstraints();
		gridBagConstraints_28.gridy = 0;
		gridBagConstraints_28.gridx = 0;
		manualPanel.add(enterAnalyticExpressionLabel, gridBagConstraints_28);

		manualTextField = new JTextField();
		final GridBagConstraints gridBagConstraints_29 = new GridBagConstraints();
		gridBagConstraints_29.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_29.weightx = 1;
		gridBagConstraints_29.gridy = 1;
		gridBagConstraints_29.gridx = 0;
		manualPanel.add(manualTextField, gridBagConstraints_29);
		circlePanel.setLayout(gridBagLayout);
		circlePanel.setName("circlePanel");
		cardPanel.add(circlePanel, circlePanel.getName());

		circleCenterLabel.setText("Center Point (x,y,z)");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.gridx = 0;
		circlePanel.add(circleCenterLabel, gridBagConstraints_4);

		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.weightx = 1;
		gridBagConstraints_3.gridy = 1;
		gridBagConstraints_3.gridx = 0;
		circlePanel.add(circleCenterTextField, gridBagConstraints_3);

		final JLabel radiusLabel = new JLabel();
		radiusLabel.setText("Radius");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.gridy = 2;
		gridBagConstraints_5.gridx = 0;
		circlePanel.add(radiusLabel, gridBagConstraints_5);

		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.weightx = 1;
		gridBagConstraints_6.gridy = 3;
		gridBagConstraints_6.gridx = 0;
		circlePanel.add(circleRadiusTextField, gridBagConstraints_6);

		
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.rowHeights = new int[] {0,7,7,7};
		boxPanel.setLayout(gridBagLayout_1);
		boxPanel.setName("boxPanel");
		cardPanel.add(boxPanel, boxPanel.getName());

		boxLCLabel.setText("Lower Corner Coordinate (x,y,z)");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.gridy = 0;
		gridBagConstraints_7.gridx = 0;
		boxPanel.add(boxLCLabel, gridBagConstraints_7);

		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_8.weightx = 1;
		gridBagConstraints_8.gridy = 1;
		gridBagConstraints_8.gridx = 0;
		boxPanel.add(boxLCTextField, gridBagConstraints_8);

		boxUCLabel.setText("Upper Corner Coordinate (x,y,z)");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.gridy = 2;
		gridBagConstraints_9.gridx = 0;
		boxPanel.add(boxUCLabel, gridBagConstraints_9);

		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_10.weightx = 1;
		gridBagConstraints_10.gridy = 3;
		gridBagConstraints_10.gridx = 0;
		boxPanel.add(boxUCTextField, gridBagConstraints_10);

		final GridBagLayout gridBagLayout_4 = new GridBagLayout();
		gridBagLayout_4.rowHeights = new int[] {0,7,7,7};
		ellipsePanel.setLayout(gridBagLayout_4);
		ellipsePanel.setName("ellipsePanel");
		cardPanel.add(ellipsePanel, ellipsePanel.getName());

		ellipseCenterLabel.setText("Center Coordinate (x,y,z)");
		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.gridy = 0;
		gridBagConstraints_16.gridx = 0;
		ellipsePanel.add(ellipseCenterLabel, gridBagConstraints_16);

		ellipseCenterTextField = new JTextField();
		final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_17.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_17.weightx = 1;
		gridBagConstraints_17.gridy = 1;
		gridBagConstraints_17.gridx = 0;
		ellipsePanel.add(ellipseCenterTextField, gridBagConstraints_17);

		ellipseAxisRadiiLabel.setText("Axis Radii (x,y,z)");
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.gridy = 2;
		gridBagConstraints_18.gridx = 0;
		ellipsePanel.add(ellipseAxisRadiiLabel, gridBagConstraints_18);

		axisRadiiTextField = new JTextField();
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_19.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_19.weightx = 1;
		gridBagConstraints_19.gridy = 3;
		gridBagConstraints_19.gridx = 0;
		ellipsePanel.add(axisRadiiTextField, gridBagConstraints_19);

		final GridBagLayout gridBagLayout_5 = new GridBagLayout();
		gridBagLayout_5.columnWidths = new int[] {0,7};
		gridBagLayout_5.rowHeights = new int[] {0,7,7,7};
		cylinderPanel.setLayout(gridBagLayout_5);
		cylinderPanel.setName("cylinderPanel");
		cardPanel.add(cylinderPanel, cylinderPanel.getName());

		final JLabel cylStartPointLabel = new JLabel();
		cylStartPointLabel.setText("Start Point (x,y,z)");
		final GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
		gridBagConstraints_20.weightx = 1;
		gridBagConstraints_20.gridy = 0;
		gridBagConstraints_20.gridx = 0;
		cylinderPanel.add(cylStartPointLabel, gridBagConstraints_20);

		final JLabel cylRadiusLabel = new JLabel();
		cylRadiusLabel.setText("Radius");
		final GridBagConstraints gridBagConstraints_22 = new GridBagConstraints();
		gridBagConstraints_22.weightx = 1;
		gridBagConstraints_22.gridy = 0;
		gridBagConstraints_22.gridx = 1;
		cylinderPanel.add(cylRadiusLabel, gridBagConstraints_22);

		cylStartPointTextField = new JTextField();
		final GridBagConstraints gridBagConstraints_23 = new GridBagConstraints();
		gridBagConstraints_23.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_23.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_23.weightx = 1;
		gridBagConstraints_23.gridy = 1;
		gridBagConstraints_23.gridx = 0;
		cylinderPanel.add(cylStartPointTextField, gridBagConstraints_23);

		cylRadiusTextField = new JTextField();
		final GridBagConstraints gridBagConstraints_24 = new GridBagConstraints();
		gridBagConstraints_24.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_24.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_24.weightx = 1;
		gridBagConstraints_24.gridy = 1;
		gridBagConstraints_24.gridx = 1;
		cylinderPanel.add(cylRadiusTextField, gridBagConstraints_24);

		final JLabel axixLabel = new JLabel();
		axixLabel.setText("Axis");
		final GridBagConstraints gridBagConstraints_21 = new GridBagConstraints();
		gridBagConstraints_21.gridy = 2;
		gridBagConstraints_21.gridx = 0;
		cylinderPanel.add(axixLabel, gridBagConstraints_21);

		final JLabel cylLengthLabel = new JLabel();
		cylLengthLabel.setText("Length");
		final GridBagConstraints gridBagConstraints_25 = new GridBagConstraints();
		gridBagConstraints_25.gridy = 2;
		gridBagConstraints_25.gridx = 1;
		cylinderPanel.add(cylLengthLabel, gridBagConstraints_25);

		final JPanel panel_1 = new JPanel();
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		final GridBagConstraints gridBagConstraints_26 = new GridBagConstraints();
		gridBagConstraints_26.weightx = 0;
		gridBagConstraints_26.gridy = 3;
		gridBagConstraints_26.gridx = 0;
		cylinderPanel.add(panel_1, gridBagConstraints_26);

		cylXRadioButton.setSelected(true);
		cylXRadioButton.setText("X");
		panel_1.add(cylXRadioButton);

		cylYRadioButton.setText("Y");
		panel_1.add(cylYRadioButton);

		cylZRadioButton.setText("Z");
		panel_1.add(cylZRadioButton);

		cylLengthTextField = new JTextField();
		final GridBagConstraints gridBagConstraints_27 = new GridBagConstraints();
		gridBagConstraints_27.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_27.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_27.weightx = 1;
		gridBagConstraints_27.gridy = 3;
		gridBagConstraints_27.gridx = 1;
		cylinderPanel.add(cylLengthTextField, gridBagConstraints_27);
		

		final JPanel exprPanel = new JPanel();
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.rowHeights = new int[] {0,7,7};
		exprPanel.setLayout(gridBagLayout_2);
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.weighty = 1;
		gridBagConstraints_11.fill = GridBagConstraints.BOTH;
		gridBagConstraints_11.weightx = 1;
		gridBagConstraints_11.gridy = 2;
		gridBagConstraints_11.gridx = 0;
		add(exprPanel, gridBagConstraints_11);

		final JLabel label6 = new JLabel();
		label6.setText("Analytic Expression");
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(10, 0, 0, 0);
		gridBagConstraints_12.gridy = 0;
		gridBagConstraints_12.gridx = 0;
		exprPanel.add(label6, gridBagConstraints_12);

		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_13.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_13.weightx = 1;
		gridBagConstraints_13.gridy = 1;
		gridBagConstraints_13.gridx = 0;
		exprPanel.add(analyticExprLabel, gridBagConstraints_13);
		analyticExprLabel.setText(" ");
		analyticExprLabel.setHorizontalAlignment(SwingConstants.CENTER);
		analyticExprLabel.setBorder(new LineBorder(Color.black, 1, false));

		copyExpressionTextButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				VCellTransferable.sendToClipboard(analyticExprLabel.getText());
			}
		});
		copyExpressionTextButton.setText("Copy Expression");
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.gridy = 2;
		gridBagConstraints_14.gridx = 0;
		exprPanel.add(copyExpressionTextButton, gridBagConstraints_14);
		
		init();
		setDimension(nDimensions);
	}
	
	private final static String XY = "(x,y)";
	private final static String XYZ = "(x,y,z)";
	private final static String CENTERPOINT = "Center Point";
	private final static String LOWERCORNERPOINT = "Lower Corner Point";
	private final static String UPPERCORNERPOINT = "Upper Corner Point";
	private final static String AXISRADII = "Axis Radii";
	private final static String XYZERO = "0,0";
	private final static String XYZZERO = "0,0,0";
	private final static String XYONE = "1,1";
	private final static String XYZONE = "1,1,1";
		
	//dialog used to be modal and reused
	private void setDimension(int dimension){
		populateShapeTypeComboBox(dimension);
		if (dimension > 1){
			circleCenterLabel.setText(CENTERPOINT+" "+(dimension == 2?XY:XYZ));
			boxLCLabel.setText(LOWERCORNERPOINT+" "+(dimension == 2?XY:XYZ));
			boxUCLabel.setText(UPPERCORNERPOINT+" "+(dimension == 2?XY:XYZ));
			ellipseCenterLabel.setText(CENTERPOINT+" "+(dimension == 2?XY:XYZ));
			ellipseAxisRadiiLabel.setText(AXISRADII+" "+(dimension == 2?XY:XYZ));
			
			circleCenterTextField.setText((dimension == 2?XYZERO:XYZZERO));
			circleRadiusTextField.setText("1.0");
			boxLCTextField.setText((dimension == 2?XYZERO:XYZZERO));
			boxUCTextField.setText((dimension == 2?XYONE:XYZONE));
			ellipseCenterTextField.setText((dimension == 2?XYZERO:XYZZERO));
			axisRadiiTextField.setText((dimension == 2?XYONE:XYZONE));
			cylStartPointTextField.setText((dimension == 2?XYZERO:XYZZERO));
			cylRadiusTextField.setText("1.0");
			cylLengthTextField.setText("1.0");
		}
		else {
			cardLayout.show(cardPanel, manualPanel.getName());
		}
		manualTextField.setText("1.0");
	}
	
	private void populateShapeTypeComboBox(int dimension){
		shapeTypeComboBoxActionListener.freeze();
		try {
			comboBox.removeAllItems();
			comboBox.insertItemAt(MANUAL_SELECT, 0);
			if(dimension == 2){
				comboBox.insertItemAt(CIRCLE_SELECT, 1);
				comboBox.insertItemAt(RECTANGLE_SELECT, 2);
				comboBox.insertItemAt(ELLIPSE_SELECT, 3);
			}else if(dimension == 3){
				comboBox.insertItemAt(SPHERE_SELECT, 1);
				comboBox.insertItemAt(BOX_SELECT, 2);
				comboBox.insertItemAt(ELLIPSOID_SELECT, 3);
				comboBox.insertItemAt(CYLINDER_SELECT, 4);			
			}
			comboBox.setSelectedIndex(0);
		}
		finally {
			shapeTypeComboBoxActionListener.thaw();
		}
	}

	private void setAnalyticExprLabel(){
		try{
			analyticExprLabel.setText(getCurrentAnalyticExpression());
			if(analyticExprLabel.getText() != null && analyticExprLabel.getText().length() > 0){
				copyExpressionTextButton.setEnabled(true);
			}else{
				copyExpressionTextButton.setEnabled(false);
			}
			firePropertyChange(PROPCHANGE_VALID_ANALYTIC, null, new Boolean(true));
		}catch(Exception e2){
			analyticExprLabel.setText(" ");
			copyExpressionTextButton.setEnabled(false);
			firePropertyChange(PROPCHANGE_VALID_ANALYTIC, null, new Boolean(false));
			//ignore
		}
	}
	
	private void init(){
		cylAxisButtonGroup.add(cylXRadioButton);
		cylAxisButtonGroup.add(cylYRadioButton);
		cylAxisButtonGroup.add(cylZRadioButton);
		cylXRadioButton.addActionListener(shapeTypeComboBoxActionListener);
		cylYRadioButton.addActionListener(shapeTypeComboBoxActionListener);
		cylZRadioButton.addActionListener(shapeTypeComboBoxActionListener);
		comboBox.addActionListener(shapeTypeComboBoxActionListener);
		
		copyExpressionTextButton.setEnabled(false);
		
		populateShapeTypeComboBox(1);
		
		UndoableEditListener undoableEditListener = 
			new UndoableEditListener(){
				public void undoableEditHappened(UndoableEditEvent e) {
					setAnalyticExprLabel();
				}
			};
		circleCenterTextField.getDocument().addUndoableEditListener(undoableEditListener);
		circleRadiusTextField.getDocument().addUndoableEditListener(undoableEditListener);
		boxLCTextField.getDocument().addUndoableEditListener(undoableEditListener);
		boxUCTextField.getDocument().addUndoableEditListener(undoableEditListener);
		ellipseCenterTextField.getDocument().addUndoableEditListener(undoableEditListener);
		axisRadiiTextField.getDocument().addUndoableEditListener(undoableEditListener);
		cylStartPointTextField.getDocument().addUndoableEditListener(undoableEditListener);
		cylRadiusTextField.getDocument().addUndoableEditListener(undoableEditListener);
		cylLengthTextField.getDocument().addUndoableEditListener(undoableEditListener);
		manualTextField.getDocument().addUndoableEditListener(undoableEditListener);
		
		setAnalyticExprLabel();
	}

	public void setDefaultCenter(Double x,Double y,Double z){
		circleCenterTextField.setText((x != null?x:"")+(y != null?","+y:"")+(z != null?","+z:""));
	}
	
	public String getCurrentAnalyticExpression(){
		String result = null;
		if(comboBox.getSelectedItem().equals(AddShapeJPanel.MANUAL_SELECT)){
			return manualTextField.getText();
		} else {
			String X = ReservedVariable.X.getName();
			String Y = ReservedVariable.Y.getName();
			String Z = ReservedVariable.Z.getName();
			if(comboBox.getSelectedItem().equals(AddShapeJPanel.CIRCLE_SELECT) ||comboBox.getSelectedItem().equals(AddShapeJPanel.SPHERE_SELECT)){
				double radius = Double.parseDouble(circleRadiusTextField.getText());
				double xp = 0;
				double yp = 0;
				double zp = 0;
				StringTokenizer st = new StringTokenizer(circleCenterTextField.getText(),",");
				xp = Double.parseDouble(st.nextToken());
				
				if(st.hasMoreTokens()){
					yp = Double.parseDouble(st.nextToken());
					if(st.hasMoreTokens()){
						zp = Double.parseDouble(st.nextToken());
						result = 
							(xp == 0 ? X + "^2" : "("+X+"-"+xp +")^2") + " + " +
							(yp == 0 ? Y + "^2" : "("+Y+"-"+yp +")^2") + " + " + 
							(zp == 0 ? Z + "^2" : "("+Z+"-"+zp +")^2") +
							" < "+radius+"^2";
					}else{
						result = 
							(xp == 0 ? X + "^2" : "("+X+"-"+xp +")^2") + " + " +
							(yp == 0 ? Y + "^2" : "("+Y+"-"+yp +")^2") + 
							" < "+radius+"^2";
					}
				}else{
					result = (xp == 0 ? X + "^2" : "("+X+"-"+xp +")^2") + " < "+radius+"^2";
				}
			}else if(comboBox.getSelectedItem().equals(AddShapeJPanel.RECTANGLE_SELECT) || comboBox.getSelectedItem().equals(AddShapeJPanel.BOX_SELECT)){
				double xl = 0;
				double yl = 0;
				double zl = 0;
				double xh = 0;
				double yh = 0;
				double zh = 0;
				StringTokenizer stl = new StringTokenizer(boxLCTextField.getText(),",");
				StringTokenizer sth = new StringTokenizer(boxUCTextField.getText(),",");
				xl = Double.parseDouble(stl.nextToken());
				xh = Double.parseDouble(sth.nextToken());
				if(xl > xh){double temp = xl;xl=xh;xh=temp;}
				if(stl.hasMoreTokens()){
					yl = Double.parseDouble(stl.nextToken());
					yh = Double.parseDouble(sth.nextToken());
					if(yl > yh){double temp = yl;yl=yh;yh=temp;}
					if(stl.hasMoreTokens()){
						zl = Double.parseDouble(stl.nextToken());
						zh = Double.parseDouble(sth.nextToken());
						if(zl > zh){double temp = zl;zl=zh;zh=temp;}
						result = 
							X+">="+xl+" && "+
							X+"<="+xh+" && "+
							Y+">="+yl+" && "+
							Y+"<="+yh+" && "+
							Z+">="+zl+" && "+
							Z+"<="+zh;
					}else{
						result = 
							X+">="+xl+" && "+
							X+"<="+xh+" && "+
							Y+">="+yl+" && "+
							Y+"<="+yh;
					}
				}else{
					result = 
						X+">="+xl+" && "+
						X+"<="+xh;
				}

			}else if(comboBox.getSelectedItem().equals(AddShapeJPanel.ELLIPSE_SELECT) || comboBox.getSelectedItem().equals(AddShapeJPanel.ELLIPSOID_SELECT)){
				double xl = 0;
				double yl = 0;
				double zl = 0;
				double xh = 0;
				double yh = 0;
				double zh = 0;
				StringTokenizer stl = new StringTokenizer(ellipseCenterTextField.getText(),",");
				StringTokenizer sth = new StringTokenizer(axisRadiiTextField.getText(),",");
				xl = Double.parseDouble(stl.nextToken());
				xh = Double.parseDouble(sth.nextToken());
				if(stl.hasMoreTokens()){
					yl = Double.parseDouble(stl.nextToken());
					yh = Double.parseDouble(sth.nextToken());
					if(stl.hasMoreTokens()){
						zl = Double.parseDouble(stl.nextToken());
						zh = Double.parseDouble(sth.nextToken());
						result = 
							(xl == 0 ? X + "^2" : "("+X+"-"+xl+")^2") + "/"+xh+"^2"+" + "+
							(yl == 0 ? Y + "^2" : "("+Y+"-"+yl+")^2") + "/"+yh+"^2"+" + "+
							(zl == 0 ? Z + "^2" : "("+Z+"-"+zl+")^2") + "/"+zh+"^2"+
							" <= 1";
					}else{
						result = 
							(xl == 0 ? X + "^2" : "("+X+"-"+xl+")^2") + "/"+xh+"^2"+" + "+
							(yl == 0 ? Y + "^2" : "("+Y+"-"+yl+")^2") + "/"+yh+"^2"+
							" <=1 ";
					}
				}else{
					result = 
						(xl == 0 ? X + "^2" : "("+X+"-"+xl+")^2") + "/"+xh+"^2"+
						" <=1 ";
				}

			}else if(comboBox.getSelectedItem().equals(AddShapeJPanel.CYLINDER_SELECT)){
				double radius = Double.parseDouble(cylRadiusTextField.getText());
				double length = Double.parseDouble(cylLengthTextField.getText());
				StringTokenizer st = new StringTokenizer(cylStartPointTextField.getText(),",");
				double xp = Double.parseDouble(st.nextToken());
				double yp = Double.parseDouble(st.nextToken());
				double zp = Double.parseDouble(st.nextToken());
				if(cylXRadioButton.isSelected()){
					result = X+">="+xp+" && "+X+"<="+(xp+length)+" && "+
					(yp == 0 ? Y + "^2" : "("+Y+"-"+yp+")^2") + " + " +
					(zp == 0 ? Z + "^2" : "("+Z+"-"+zp+")^2") + " < "+radius+"^2";
				}else if(cylYRadioButton.isSelected()){
					result = Y+">="+yp+" && "+Y+"<="+(yp+length)+" && "+
					(xp == 0 ? X + "^2" : "("+X+"-"+xp+")^2") + " + " +
					(zp == 0 ? Z + "^2" : "("+Z+"-"+zp+")^2") + " < "+radius+"^2";
				}else if(cylZRadioButton.isSelected()){
					result = Z+">="+zp+" && "+Z+"<="+(zp+length)+" && "+
					(xp == 0 ? X + "^2" : "("+X+"-"+xp+")^2") + " + " +
					(yp == 0 ? Y + "^2" : "("+Y+"-"+yp+")^2") + " < "+radius+"^2";
				}
			}
		}
		return result;
	}
}
