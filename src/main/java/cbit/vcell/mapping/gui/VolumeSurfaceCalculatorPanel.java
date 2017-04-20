/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.StringTokenizer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.vcell.util.gui.DialogUtils;

public class VolumeSurfaceCalculatorPanel extends JPanel {
//	private JLabel volumeSurfaceCalculatorLabel = null; 
//	private JPanel volumeSurfaceCalculatorPanel = null;
	private VolSurfCalcCondensedPanel volumeSurfaceCalculatorPanel;
	private JCheckBox chckbxNewCheckBox;
	
//	private JComboBox shapeComboBox = null;
	
//	private JLabel attrLabel = null;
//	private JTextField attrTextField = null;
//	private JButton calculateButton = null;
//	private JTextField volumeTextField = null;
//	private JTextField surfaceTextField = null;
	
//	private JLabel volumeFormulaLabel = null;
//	private JLabel surfaceFormulaLabel = null; 

	private static final String SHAPE_SPHERE = "Sphere";
	private static final String SHAPE_HEMISPHERE = "Hemisphere";
	private static final String SHAPE_CYLINDER = "Cylinder";
	private static final String SHAPE_CONE = "Cone";
	private static final String SHAPE_CUBE = "Cube";
	private static final String SHAPE_BOX = "Box";
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private GridBagConstraints gbc_1;
	class IvjEventHandler implements /*MouseListener,*/ ActionListener, ItemListener {		
//		public void mouseClicked(MouseEvent e) {
//			if (e.getSource() == volumeSurfaceCalculatorLabel) {
//				if (volumeSurfaceCalculatorPanel.isVisible()) {
//					volumeSurfaceCalculatorLabel.setText("<html><font color=blue><u>Volume and Surface Calculator &gt;&gt;</u></font></html>");
//					volumeSurfaceCalculatorPanel.setVisible(false);
//				} else {
//					volumeSurfaceCalculatorLabel.setText("<html><font color=blue><u>Volume and Surface Calculator &lt;&lt;</u></font></html>");
//					volumeSurfaceCalculatorPanel.setVisible(true);
//					shapeComboBox.setSelectedIndex(0);
//				}
//			}
//		}
//		public void mouseEntered(MouseEvent e) {
//		}
//		public void mouseExited(MouseEvent e) {
//		}
//		public void mousePressed(MouseEvent e) {
//		}
//		public void mouseReleased(MouseEvent e) {
//		}
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == volumeSurfaceCalculatorPanel.getAttrTextField()){
				volumeSurfaceCalculatorPanel.getCalculateButton().doClick();
			}else if (e.getSource() == volumeSurfaceCalculatorPanel.getCalculateButton()) {
				Object selectedItem = volumeSurfaceCalculatorPanel.getShapeComboBox().getSelectedItem();
				String rtext = volumeSurfaceCalculatorPanel.getAttrTextField().getText();
				double v = 0, s = 0;
				try {
					if (selectedItem.equals(SHAPE_SPHERE)) {
						double r = Double.parseDouble(rtext);
						v = 4.0 * Math.PI * r * r * r / 3.0;
						s = 4.0 * Math.PI * r * r;
					} else if (selectedItem.equals(SHAPE_HEMISPHERE)) {
						double r = Double.parseDouble(rtext);
						v = 2.0 * Math.PI * r * r * r / 3.0;
						s = 3.0 * Math.PI * r * r;
					} else if (selectedItem.equals(SHAPE_CYLINDER)) {
						StringTokenizer st = new StringTokenizer(rtext," ,;");
						double r = Double.parseDouble(st.nextToken());
						double h = Double.parseDouble(st.nextToken());
						v = Math.PI * r * r * h;
						s = 2 * Math.PI * r * (r + h);
					} else if (selectedItem.equals(SHAPE_CONE)) {
						StringTokenizer st = new StringTokenizer(rtext," ,;");
						double r = Double.parseDouble(st.nextToken());
						double h = Double.parseDouble(st.nextToken());
						v = Math.PI * r * r * h / 3.0;
						s = Math.PI * r * (r + Math.sqrt(r * r + h * h));
					} else if (selectedItem.equals(SHAPE_CUBE)) {
						double a = Double.parseDouble(rtext);
						v = a * a * a;
						s = 6 * a * a;
					} else if (selectedItem.equals(SHAPE_BOX)) {
						StringTokenizer st = new StringTokenizer(rtext," ,;");
						double l = Double.parseDouble(st.nextToken());
						double w = Double.parseDouble(st.nextToken());
						double h = Double.parseDouble(st.nextToken());
						v = l * w * h;
						s = 2 * (l * w + l * h + w * h);
					}
				} catch (Exception ex) {
					DialogUtils.showErrorDialog(VolumeSurfaceCalculatorPanel.this, "Enter values "+volumeSurfaceCalculatorPanel.getAttrLabel().getText()+" for "+selectedItem+".  "+ex.getClass().getName(), ex);
				}
				volumeSurfaceCalculatorPanel.getVolumeTextField().setText(v + "");
				volumeSurfaceCalculatorPanel.getSurfaceTextField().setText(s + "");
			}
			
		}
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() != ItemEvent.SELECTED/* || shapeComboBox.getSelectedIndex() < 0*/) {
				return;
			}
			if (e.getSource() == volumeSurfaceCalculatorPanel.getShapeComboBox()) {
				volumeSurfaceCalculatorPanel.getVolumeTextField().setText(null);
				volumeSurfaceCalculatorPanel.getSurfaceTextField().setText(null);
				volumeSurfaceCalculatorPanel.getAttrTextField().setText(null);
				Object selectedItem = volumeSurfaceCalculatorPanel.getShapeComboBox().getSelectedItem();
				String piStr = "<font face=Symbol>&pi;</font>";
				if (selectedItem.equals(SHAPE_SPHERE)) {
					volumeSurfaceCalculatorPanel.getAttrLabel().setText("(r)");
					volumeSurfaceCalculatorPanel.getAttrTextField().setText("1");
					volumeSurfaceCalculatorPanel.getVolumeTextField().setToolTipText("(4/3)*pi*(r^3)");
					volumeSurfaceCalculatorPanel.getSurfaceTextField().setToolTipText("4*pi*(r^2)");
//					volumeFormulaLabel.setText("<html><i>4 " + piStr + " r <sup>3</sup> / 3</i></html>");//4 " + multChar + " " + piChar + " " + multChar + " r " + multChar + " r " + multChar + " r / 3");
//					surfaceFormulaLabel.setText("<html><i>4 " + piStr + " r <sup>2</sup></i></html");//4 " + multChar + " " + piChar + " " + multChar + " r " + multChar + " r");
				} else if (selectedItem.equals(SHAPE_HEMISPHERE)) {
					volumeSurfaceCalculatorPanel.getAttrLabel().setText("(r)");
					volumeSurfaceCalculatorPanel.getAttrTextField().setText("1");
					volumeSurfaceCalculatorPanel.getVolumeTextField().setToolTipText("(2/3)*pi*(r^3)");
					volumeSurfaceCalculatorPanel.getSurfaceTextField().setToolTipText("3*pi*(r^2)");
//					volumeFormulaLabel.setText("<html><i>2 " + piStr + " r <sup>3</sup> / 3</i></html>");//4 " + multChar + " " + piChar + " " + multChar + " r " + multChar + " r " + multChar + " r / 3");
//					surfaceFormulaLabel.setText("<html><i>3 " + piStr + " r <sup>2</sup></i></html");//4 " + multChar + " " + piChar + " " + multChar + " r " + multChar + " r");
				} else if (selectedItem.equals(SHAPE_CYLINDER)) {
					volumeSurfaceCalculatorPanel.getAttrLabel().setText("(r, h)");
					volumeSurfaceCalculatorPanel.getAttrTextField().setText("1,1");
					volumeSurfaceCalculatorPanel.getVolumeTextField().setToolTipText("pi*(r^2)*h");
					volumeSurfaceCalculatorPanel.getSurfaceTextField().setToolTipText("(2*pi*r^2) + (2*pi*r*h)");
//					volumeFormulaLabel.setText("<html><i>" + piStr + " r <sup>2</sup> h</i></html>");//piChar + " " + multChar + " r " + multChar + " r " + multChar + " h");
//					surfaceFormulaLabel.setText("<html><i>2 " + piStr + " r <sup>2</sup> + 2 " + piStr + " r h</i></html>");//"2 " + multChar + " " + piChar + " " + multChar + " r " + multChar + " r + 2 " + multChar + " " + piChar + " " + multChar + " r " + multChar + " h");
				} else if (selectedItem.equals(SHAPE_CONE)) {
					volumeSurfaceCalculatorPanel.getAttrLabel().setText("(r, h)");
					volumeSurfaceCalculatorPanel.getAttrTextField().setText("1,1");
					volumeSurfaceCalculatorPanel.getVolumeTextField().setToolTipText("(pi/3)*(r^2)*h");
					volumeSurfaceCalculatorPanel.getSurfaceTextField().setToolTipText("(pi*(r^2)) + (pi*r*sqrt((r^2) + (h^2)))");
//					volumeFormulaLabel.setText("<html><i>" + piStr +" r <sup>2</sup> h / 3<i></html>");//piChar + " " + multChar + " r " + multChar + " r " + multChar + " h / 3");
//					surfaceFormulaLabel.setText("<html><table cellspacing=0 cellpadding=0 border=0>" +
//							"<tr><td>&nbsp;</td><td>_________</td>" +
//							"</tr><tr><td><i>" + piStr + " r <sup>2</sup> + " + piStr +" r</i>&nbsp;&radic;</td><td><i>&nbsp;r <sup>2</sup> + h <sup>2</sup></i></td></tr></table></html>");//piChar + " " + multChar + " r " + multChar + " r + " + piChar + " " + multChar + " r " + multChar + " sqrt ( r " + multChar + " r + h " + multChar + " h )");
				} else if (selectedItem.equals(SHAPE_CUBE)) {
					volumeSurfaceCalculatorPanel.getAttrLabel().setText("(a)");
					volumeSurfaceCalculatorPanel.getAttrTextField().setText("1");
					volumeSurfaceCalculatorPanel.getVolumeTextField().setToolTipText("a^3");
					volumeSurfaceCalculatorPanel.getSurfaceTextField().setToolTipText("6*(a^2)");
//					volumeFormulaLabel.setText("<html><i> a <sup>3</sup> <i></html>");//piChar + " " + multChar + " r " + multChar + " r " + multChar + " h / 3");
//					surfaceFormulaLabel.setText("<html><i>6 a <sup>2</sup></i></html>");//piChar + " " + multChar + " r " + multChar + " r + " + piChar + " " + multChar + " r " + multChar + " sqrt ( r " + multChar + " r + h " + multChar + " h )");
				} else if (selectedItem.equals(SHAPE_BOX)) {
					volumeSurfaceCalculatorPanel.getAttrLabel().setText("(l, w, h)");
					volumeSurfaceCalculatorPanel.getAttrTextField().setText("1,1,1");
					volumeSurfaceCalculatorPanel.getVolumeTextField().setToolTipText("l*w*h");
					volumeSurfaceCalculatorPanel.getSurfaceTextField().setToolTipText("2*(l*w + l*h + w*h)");
//					volumeFormulaLabel.setText("<html><i>l w h</i></html>");
//					surfaceFormulaLabel.setText("<html><i>2 * ( l w + l h + w h )</i></html>");
				}
				volumeSurfaceCalculatorPanel.getAttrTextField().setToolTipText("Enter values "+volumeSurfaceCalculatorPanel.getAttrLabel().getText());
			} 			
		};
	};
	
	public VolumeSurfaceCalculatorPanel() {
		super();
		initialize();
	}

	private void initialize() {		

		volumeSurfaceCalculatorPanel = new VolSurfCalcCondensedPanel();
//		volumeSurfaceCalculatorPanel.setBorder(new JScrollPane().getBorder());			
//		volumeSurfaceCalculatorPanel.setLayout(new GridBagLayout());
//		
//		int gridy = 0;			
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		DefaultComboBoxModel dcb = (DefaultComboBoxModel<String>)volumeSurfaceCalculatorPanel.getShapeComboBox().getModel();
		dcb.addElement(SHAPE_SPHERE);
		dcb.addElement(SHAPE_HEMISPHERE);
		dcb.addElement(SHAPE_CYLINDER);
		dcb.addElement(SHAPE_CONE);
		dcb.addElement(SHAPE_CUBE);
		dcb.addElement(SHAPE_BOX);
		volumeSurfaceCalculatorPanel.getShapeComboBox().setSelectedIndex(-1);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0};
		setLayout(gridBagLayout);
		//		gbc.gridx = 0; 
		//		gbc.gridy = gridy;
		//		gbc.anchor = GridBagConstraints.LINE_END;
		//		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		//		volumeSurfaceCalculatorPanel.add(new JLabel("Shape"), gbc);
		//		
		//		gbc = new java.awt.GridBagConstraints();
		//		gbc.gridx = 1; 
		//		gbc.gridy = gridy;
		//		gbc.weightx = 0.5;
		//		gbc.fill = GridBagConstraints.HORIZONTAL;
		//		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		//		shapeComboBox = new JComboBox();
				
				//		shapeComboBox.setModel(dcb);
				//		volumeSurfaceCalculatorPanel.add(shapeComboBox, gbc);
				//
				//		gbc = new java.awt.GridBagConstraints();
				//		gbc.gridx = 2; 
				//		gbc.gridy = gridy;
				//		gbc.gridheight = 3;
				//		gbc.fill = GridBagConstraints.VERTICAL;
				//		JPanel panel = new JPanel();
				//		panel.setLayout(new GridBagLayout());
				//		GridBagConstraints gbc1 = new GridBagConstraints();
				//		gbc1.gridx = 0; 
				//		gbc1.gridy = 0;
				//		gbc1.weighty = 1.0;
				//		calculateButton = new JButton("Go >>");
				//		calculateButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
				//		panel.add(calculateButton, gbc1);
				//		volumeSurfaceCalculatorPanel.add(panel, gbc);
				//		
				//		gbc = new java.awt.GridBagConstraints();
				//		gbc.gridx = 3; 
				//		gbc.gridy = gridy;
				//		gbc.anchor = GridBagConstraints.LINE_END;
				//		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				//		volumeSurfaceCalculatorPanel.add(new JLabel("Volume"), gbc);
				//		
				//		gbc = new java.awt.GridBagConstraints();
				//		gbc.gridx = 4; 
				//		gbc.gridy = gridy;
				//		gbc.weightx = 0.5;
				//		gbc.fill = GridBagConstraints.HORIZONTAL;
				//		gbc.insets = new java.awt.Insets(4, 4, 0, 4);
				//		volumeTextField = new JTextField(20);
				//		volumeTextField.setEditable(false);
				//		volumeSurfaceCalculatorPanel.add(volumeTextField, gbc);
				//
				//		//
				//		gridy ++;
				//		volumeFormulaLabel = new JLabel();
				//		gbc = new java.awt.GridBagConstraints();
				//		gbc.gridx = 4; 
				//		gbc.gridy = gridy;
				//		gbc.weightx = 1.0; 
				//		gbc.anchor = GridBagConstraints.PAGE_START;
				//		gbc.fill = GridBagConstraints.HORIZONTAL;
				//		gbc.insets = new java.awt.Insets(0, 4, 4, 4);
				//		volumeSurfaceCalculatorPanel.add(volumeFormulaLabel, gbc);
				//		
				//		//
				//		gridy ++;
				//		attrLabel = new JLabel("r ");
				//		java.awt.Font font = attrLabel.getFont();
				//		attrLabel.setFont(font.deriveFont(Font.ITALIC + Font.BOLD));
				//		gbc = new java.awt.GridBagConstraints();
				//		gbc.gridx = 0; gbc.gridy = gridy;
				//		gbc.anchor = GridBagConstraints.LINE_END;
				//		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				//		volumeSurfaceCalculatorPanel.add(attrLabel, gbc);
				//		
				//		gbc = new java.awt.GridBagConstraints();
				//		gbc.gridx = 1; 
				//		gbc.gridy = gridy;
				//		gbc.weightx = 0.5;
				//		gbc.fill = GridBagConstraints.HORIZONTAL;
				//		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				//		attrTextField = new JTextField(20);
				//		volumeSurfaceCalculatorPanel.add(attrTextField, gbc);
				//		
				//		gbc = new java.awt.GridBagConstraints();
				//		gbc.gridx = 3; 
				//		gbc.gridy = gridy;
				//		gbc.anchor = GridBagConstraints.LINE_END;
				//		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				//		volumeSurfaceCalculatorPanel.add(new JLabel("Surface"), gbc);
				//		
				//		gbc = new java.awt.GridBagConstraints();
				//		gbc.gridx = 4; 
				//		gbc.gridy = gridy;
				//		gbc.weightx = 0.5;
				//		gbc.fill = GridBagConstraints.HORIZONTAL;
				//		gbc.insets = new java.awt.Insets(4, 4, 0, 4);
				//		surfaceTextField = new JTextField(20);
				//		surfaceTextField.setEditable(false);
				//		volumeSurfaceCalculatorPanel.add(surfaceTextField, gbc);
				//		
				//		//
				//		gridy ++;
				//		surfaceFormulaLabel = new JLabel();
				//		gbc = new java.awt.GridBagConstraints();
				//		gbc.gridx = 4; 
				//		gbc.gridy = gridy;
				//		gbc.weightx = 1.0;
				//		gbc.fill = GridBagConstraints.HORIZONTAL;
				//		gbc.insets = new java.awt.Insets(0, 4, 4, 4);
				//		volumeSurfaceCalculatorPanel.add(surfaceFormulaLabel, gbc);
				//		
				//		
				//		setLayout(new GridBagLayout());
				//		
				//		volumeSurfaceCalculatorLabel = new JLabel("<html><font color=blue><u>Volume and Surface Calculator &gt;&gt;</u></font></html>");
				//		volumeSurfaceCalculatorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
				//		gbc = new java.awt.GridBagConstraints();
				//		gbc.gridx = 0; 
				//		gbc.gridy = 0;
				//		gbc.weightx = 1.0;
				//		gbc.fill = GridBagConstraints.HORIZONTAL;
				//		gbc.anchor = GridBagConstraints.LINE_END;
				//		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				//		add(volumeSurfaceCalculatorLabel, gbc);
				//
						gbc_1 = new java.awt.GridBagConstraints();
						gbc_1.fill = GridBagConstraints.HORIZONTAL;
						gbc_1.weightx = 1.0;
						gbc_1.gridx = 1; 
						gbc_1.gridy = 0;
						gbc_1.insets = new java.awt.Insets(4, 4, 4, 4);
						
						chckbxNewCheckBox = new JCheckBox("Volume/Surface Calculator");
						chckbxNewCheckBox.setForeground(Color.BLUE);
						chckbxNewCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 9));
						chckbxNewCheckBox.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								volumeSurfaceCalculatorPanel.setVisible(chckbxNewCheckBox.isSelected());
							}
						});
						GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
						gbc_chckbxNewCheckBox.gridx = 0;
						gbc_chckbxNewCheckBox.gridy = 0;
						add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);
						GridBagConstraints gbc_volumeSurfaceCalculatorPanel = new GridBagConstraints();
						gbc_volumeSurfaceCalculatorPanel.insets = new Insets(0, 0, 0, 4);
						gbc_volumeSurfaceCalculatorPanel.fill = GridBagConstraints.HORIZONTAL;
						gbc_volumeSurfaceCalculatorPanel.weightx = 1.0;
						gbc_volumeSurfaceCalculatorPanel.gridx = 1;
						gbc_volumeSurfaceCalculatorPanel.gridy = 0;
						add(volumeSurfaceCalculatorPanel, gbc_volumeSurfaceCalculatorPanel);
						volumeSurfaceCalculatorPanel.setVisible(false);

//		
//		volumeSurfaceCalculatorPanel.setVisible(false);
//		shapeComboBox.setSelectedIndex(-1);
		initConnections();
	}
	
	private void initConnections() {
//		volumeSurfaceCalculatorLabel.addMouseListener(ivjEventHandler);
		volumeSurfaceCalculatorPanel.getCalculateButton().addActionListener(ivjEventHandler);
		volumeSurfaceCalculatorPanel.getAttrTextField().addActionListener(ivjEventHandler);
//		shapeComboBox.addItemListener(ivjEventHandler);
		volumeSurfaceCalculatorPanel.getShapeComboBox().addItemListener(ivjEventHandler);
		volumeSurfaceCalculatorPanel.getShapeComboBox().setSelectedIndex(0);
	}	
}
