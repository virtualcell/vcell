/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import org.vcell.model.rbm.MolecularType;
import org.vcell.pathway.BioPaxObject;
import org.vcell.sybil.models.AnnotationQualifiers;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.models.miriam.MIRIAMRef.URNParseFailureException;
import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.Identifiable;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.VCMetaDataMiriamManager;
import cbit.vcell.biomodel.meta.VCMetaDataMiriamManager.VCMetaDataDataType;
import cbit.vcell.biomodel.meta.MiriamManager.DataType;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.desktop.BioModelCellRenderer;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.xml.gui.MiriamTreeModel;
import cbit.vcell.xml.gui.MiriamTreeModel.LinkNode;
/**
 * Superclass for all the Annotation panels
 * Creation date: (11/6/2018 3:44:00 PM)
 * @author: Dan Vasilescu
 */
@SuppressWarnings("serial")
public class AnnotationsPanel extends DocumentEditorSubPanel {
	private BioModel bioModel = null;
	private VCMetaData vcMetaData = null;
	MiriamTreeModel miriamTreeModel = null;
	
	private Identifiable selectedObject = null;
	
	public static final String ACTION_ADD ="Add...";
	public static final String ACTION_DELETE ="Delete";
	public static final String ACTION_REMOVE_TEXT = "Delete";
	public static final String ACTION_GOTO ="Go";
	public static final int COMBO_QUALIFIER_WIDTH = 140;
	public static final int MAX_DESCRIPTION_LENGTH = 145;
	public static final int MAX_URI_LENGTH = 130;
	
	private EventHandler eventHandler = new EventHandler();
	
	private JTree jTreeMIRIAM = null;

	private JPanel jPanelNewIdentifier = null;			// add new identity dialog
	private JPanel jPanelIdentifierManager = null;		// upper panel with the identity provider combobox, Navigate and Add buttons
	private JPanel jPanelLowerLeft = null;				// lower left panel with the  Delete buttons
	private JPanel jBottomPanel = null;
	private JSplitPane splitPane = null;
	private JScrollPane jScrollPane = null;			// will show the MIRIAM JTree
	
	private JComboBox jComboBoxURI = null;			// identity provider combo
	private DefaultComboBoxModel defaultComboBoxModelURI = null;
	private JComboBox jComboBoxQualifier = null;	// annotation qualifier combo (isDescribedBy, etc)
	private DefaultComboBoxModel defaultComboBoxModelQualifier = new DefaultComboBoxModel();
	
	private JTextField jTextFieldFormalID = null;	// immortal ID text
	private JButton jButtonAddRef = null;			// add a cross-reference
	private JButton jButtonDeleteRef = null;		// delete selected cross-reference
	private JButton jButtonRemoveText = null;		// remove text annotation

	private JTextPane annotationTextArea = null;
	private String emptyHtmlText = null;	// content of annotationTextArea after initialization with null
						// will contain an empty html object "<html><header></header><body><p style="margin-top: 0"></p></body></html>"
	private static String emptyHtmlText2 = "<html><header></header><body></body></html>";	// really empty
	

	private class EventHandler extends MouseAdapter implements ActionListener, FocusListener, PropertyChangeListener, KeyListener {
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == annotationTextArea) {
				changeTextAnnotation();
			}
		}
		@Override
		public void mouseExited(MouseEvent e) {
			super.mouseExited(e);
			if(e.getSource() == annotationTextArea) {
				changeTextAnnotation();
			}
		}
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() instanceof SpeciesContext
					|| evt.getSource() instanceof MolecularType
					|| evt.getSource() instanceof ReactionRule
					|| evt.getSource() instanceof ReactionStep
					|| evt.getSource() instanceof RbmObservable
					|| evt.getSource() instanceof BioModel
					|| evt.getSource() instanceof Structure
					|| evt.getSource() instanceof Model.ModelParameter
					|| evt.getSource() instanceof BioPaxObject
					|| evt.getSource() instanceof SimulationContext
					) {
				initializeComboBoxURI();
				updateInterface();
			}
		}
		@Override
		public void actionPerformed(ActionEvent evt) {
			if (evt.getSource() == getJButtonAddRef()) {
				addIdentifier();
			} else if(evt.getSource() == getJButtonDeleteRef()) {
				removeIdentifier();
			} else if(evt.getSource() == getJButtonRemoveText()) {
				removeText();
			}
		}
		@Override
		public void keyPressed(KeyEvent e) {
		}
		@Override
		public void keyReleased(KeyEvent e) {
			// too expensive to parse it at each keystroke
//			System.out.println("key released");
//			if (e.getSource() == annotationTextArea) {
//				String text = annotationTextArea.getText();
//				text = text.replaceAll("(\n\r|\n|\r)", "");
//				text = text.replaceAll(" ", "");
//				System.out.println(text);
//			}
		}
		@Override
		public void keyTyped(KeyEvent e) {
		}
	}
	
	public class ComboboxToolTipRenderer extends DefaultListCellRenderer {
		List<String> tooltips;
		@SuppressWarnings("rawtypes")
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component comp;
			if(value == null) {
				comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			} else if(value instanceof DataType) {
				DataType dt = (DataType)value;
				comp = super.getListCellRendererComponent(list,dt.getDataTypeName(),index,isSelected,cellHasFocus);
			} else if(value instanceof MIRIAMQualifier) {
				MIRIAMQualifier mc = (MIRIAMQualifier)value;
				comp = super.getListCellRendererComponent(list,mc.getDescription(),index,isSelected,cellHasFocus);
			} else {
				comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
			if (-1 < index && null != value && null != tooltips) {
				list.setToolTipText(tooltips.get(index));
			}
		return comp;
		}
		public void setTooltips(List<String> tooltips) {
			this.tooltips = tooltips;
		}
	}


public AnnotationsPanel() {
	super();
	initialize();
}

private void handleException(java.lang.Throwable exception) {
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}

private JPanel getJPanelNewIdentifier() {
	jPanelNewIdentifier = new JPanel();
	jPanelNewIdentifier.setLayout(new GridBagLayout());
//	jPanelNewIdentifier.setPreferredSize(new Dimension(725, 37));
//	jPanelNewIdentifier.setBorder(BorderFactory.createLineBorder(SystemColor.windowBorder, 2));
		
	VCMetaDataDataType mdt = (VCMetaDataDataType)getJComboBoxURI().getSelectedItem();
	int gridx = 0;
	int gridy = 0;
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.insets = new Insets(3, 15, 3, 0);		// top left bottom right
	gbc.gridx = gridx;
	gbc.gridy = gridy;
	gbc.anchor = GridBagConstraints.WEST;
	jPanelNewIdentifier.add(new JLabel("Provider: "), gbc);
	
	gridx++;
	gbc = new GridBagConstraints();
	gbc.insets = new Insets(3, 5, 3, 4);
	gbc.gridx = gridx;
	gbc.gridy = gridy;
	gbc.anchor = GridBagConstraints.WEST;
	jPanelNewIdentifier.add(new JLabel("<html><b>" + mdt.getDataTypeName() + "</b></html>"), gbc);

	// ------------------------------------- Qualifier combobox  -----------------------------
	
	gridx++;
	gbc = new GridBagConstraints();
	gbc.insets = new Insets(3, 15, 3, 0);		// top left bottom right
	gbc.gridx = gridx;
	gbc.gridy = gridy;
	jPanelNewIdentifier.add(new JLabel("Qualifier"), gbc);
	
	gridx++;
	gbc = new GridBagConstraints();
	gbc.insets = new Insets(3, 5, 3, 4);
	gbc.gridx = gridx;
	gbc.gridy = gridy;
	jPanelNewIdentifier.add(getJComboBoxQualifier(), gbc);

	// -------------------------------------------------------------------------------
	gridx++;
	gbc = new GridBagConstraints();
	gbc.insets = new Insets(3, 15, 3, 0);
	gbc.gridx = gridx;
	gbc.gridy = gridy;
	gbc.anchor = GridBagConstraints.WEST;
	jPanelNewIdentifier.add(new JLabel("Identifier ID"), gbc);
		
	gridx++;
	gbc = new GridBagConstraints();
	gbc.insets = new Insets(3, 5, 3, 4);
	gbc.gridx = gridx;
	gbc.gridy = gridy;
	gbc.anchor = GridBagConstraints.WEST;
	jPanelNewIdentifier.add(getJTextFieldFormalID(), gbc);
	getJTextFieldFormalID().setText("NewID");

	gridx++;
	gbc = new GridBagConstraints();
	gbc.insets = new Insets(3, 15, 3, 0);
	gbc.gridx = gridx;
	gbc.gridy = gridy;
	gbc.anchor = GridBagConstraints.WEST;
	jPanelNewIdentifier.add(new JLabel("<html>Example: <b>" + mdt.getExample() +"</b></html>"), gbc);
	
	gridx++;
	gbc = new GridBagConstraints();
	gbc.insets = new Insets(3, 15, 3, 0);
	gbc.gridx = gridx;
	gbc.gridy = gridy;
	gbc.weightx = 1.0;
	gbc.anchor = GridBagConstraints.EAST;
	jPanelNewIdentifier.add(new JLabel(), gbc);

	// ---------------------------------------------------------------------
	gridx = 0;
	gridy++;
	
	JLabel linkLabel = new JLabel();
	String s = "<html>" + "Navigate to" + "&nbsp;<font color=\"" + "blue" + "\"><a href=" + mdt.getDataTypeURL() + ">" + mdt.getDataTypeURL() + "</a></font>&nbsp;&nbsp;";
	s += "and find the identifier ID, then paste it in the box above." + "</html>";
	linkLabel.setToolTipText("Double-click to open link");
	linkLabel.setText(s);
	linkLabel.addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			if(e.getClickCount() == 2) {
				showBrowseToLink(mdt);
			}
		} 
	});
	
	gbc = new GridBagConstraints();
	gbc.insets = new Insets(3, 15, 3, 0);
	gbc.gridx = gridx;
	gbc.gridy = gridy;
	gbc.gridwidth = 8;
	gbc.anchor = GridBagConstraints.WEST;
	jPanelNewIdentifier.add(linkLabel, gbc);
	
	// ---------------------------------------------------------------------
	List <String> rows = new ArrayList<> ();
	String value = mdt.getDescription();
	StringTokenizer tokenizer = new StringTokenizer(value, " ");
	String row = "";
	while(tokenizer.hasMoreTokens()) {
		String word = tokenizer.nextToken();
		if((row.length() + word.length()) > MAX_DESCRIPTION_LENGTH) {
			rows.add(row);
			row = word + " ";
		} else {
			row += word + " ";
		}
	}
	if(!row.isEmpty()) {
		rows.add(row);
	}
	for(String currentRow : rows) {
		gridx = 0;
		gridy++;
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 15, 3, 0);
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = 8;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		jPanelNewIdentifier.add(new JLabel(currentRow), gbc);

	}
	return jPanelNewIdentifier;
}
private JPanel getJPanelLeftTitle() {
	JPanel jPanelLeftTitle = new JPanel();
	jPanelLeftTitle.setLayout(new GridBagLayout());
	jPanelLeftTitle.setPreferredSize(new Dimension(260, 37));
	jPanelLeftTitle.setBorder(BorderFactory.createLineBorder(SystemColor.windowBorder, 1));
	int gridx = 0;
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.insets = new Insets(3, 15, 3, 0);		// top left bottom right
	gbc.gridx = gridx;
	gbc.gridy = 0;
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.weightx = 1.0;
	jPanelLeftTitle.add(new JLabel("User defined cross-references."), gbc);
	return jPanelLeftTitle;
}
private JPanel getJPanelRightTitle() {
	JPanel jPanelRightTitle = new JPanel();
	jPanelRightTitle.setLayout(new GridBagLayout());
	jPanelRightTitle.setBorder(BorderFactory.createLineBorder(SystemColor.windowBorder, 1));
	
	int gridx = 0;
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.gridx = gridx;
	gbc.gridy = 0;
	gbc.insets = new Insets(6, 8, 0, 0);		// top left bottom right
	gbc.anchor = GridBagConstraints.NORTHWEST;
	jPanelRightTitle.add(new JLabel("Text Annotations"), gbc);
	
	gridx++;
	gbc = new java.awt.GridBagConstraints();
	gbc.gridx = gridx; 
	gbc.gridy = 0;
	gbc.weightx = 1.0;
	gbc.weighty = 1.0;
	gbc.fill = java.awt.GridBagConstraints.BOTH;
	jPanelRightTitle.add(new JLabel(), gbc);
	
	gridx++;
	gbc = new java.awt.GridBagConstraints();
	gbc.gridx = gridx; 
	gbc.gridy = 0;
	gbc.insets = new Insets(3, 0, 3, 3);
	gbc.anchor = GridBagConstraints.NORTHEAST;
	jPanelRightTitle.add(getJButtonRemoveText(), gbc);
	
	getJButtonRemoveText().addActionListener(eventHandler);
	return jPanelRightTitle;
}
private JPanel getJPanelIdentifierManager() {
	if (jPanelIdentifierManager == null) {
		jPanelIdentifierManager = new JPanel();
		jPanelIdentifierManager.setLayout(new GridBagLayout());
		jPanelIdentifierManager.setPreferredSize(new Dimension(260, 37));
		jPanelIdentifierManager.setBorder(BorderFactory.createLineBorder(SystemColor.windowBorder, 1));
		
		int gridx = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 15, 3, 0);		// top left bottom right
		gbc.gridx = gridx;
		gbc.gridy = 0;
		jPanelIdentifierManager.add(new JLabel("Provider"), gbc);
		
		gridx++;
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 5, 3, 4);
		gbc.gridx = gridx;
		gbc.gridy = 0;
		jPanelIdentifierManager.add(getJComboBoxURI(), gbc);
		
		gridx++;
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 5, 3, 5);
		gbc.gridx = gridx;
		gbc.gridy = 0;
		jPanelIdentifierManager.add(getJButtonAddRef(), gbc);

		gridx++;
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 0, 3, 0);
		gbc.gridx = gridx;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		jPanelIdentifierManager.add(new JLabel(""), gbc);
		
		getJButtonAddRef().addActionListener(eventHandler);
	}
	return jPanelIdentifierManager;
}
private JPanel getJPanelLowerLeft() {
	if (jPanelLowerLeft == null) {
		jPanelLowerLeft = new JPanel();
		jPanelLowerLeft.setLayout(new GridBagLayout());
		jPanelLowerLeft.setPreferredSize(new Dimension(300, 37));
		jPanelLowerLeft.setBorder(BorderFactory.createLineBorder(SystemColor.windowBorder, 1));
		
		int gridx = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 3, 3, 3);
		gbc.gridx = gridx;
		gbc.gridy = 0;
		jPanelLowerLeft.add(getJButtonDeleteRef(), gbc);
		
		gridx++;
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 0, 3, 0);
		gbc.gridx = gridx;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		jPanelLowerLeft.add(new JLabel(""), gbc);
		
		getJButtonDeleteRef().setEnabled(false);
		getJButtonDeleteRef().addActionListener(eventHandler);
	}
	return jPanelLowerLeft;
}

private JComboBox getJComboBoxQualifier() {
	if (jComboBoxQualifier == null) {
		jComboBoxQualifier = new JComboBox();
//		defaultComboBoxModelQualifier = new DefaultComboBoxModel();	// already allocated
		jComboBoxQualifier.setModel(defaultComboBoxModelQualifier);
		Dimension d = jComboBoxQualifier.getPreferredSize();
		jComboBoxQualifier.setMinimumSize(new Dimension(COMBO_QUALIFIER_WIDTH, d.height));
		jComboBoxQualifier.setPreferredSize(new Dimension(COMBO_QUALIFIER_WIDTH, d.height));
		ComboboxToolTipRenderer renderer = new ComboboxToolTipRenderer();
		jComboBoxQualifier.setRenderer(renderer);
	}
	return jComboBoxQualifier;
}
private JTextField getJTextFieldFormalID() {
	if (jTextFieldFormalID == null) {
		jTextFieldFormalID = new JTextField();
		jTextFieldFormalID.setText("NewID");
		Dimension d = jTextFieldFormalID.getPreferredSize();
		jTextFieldFormalID.setMinimumSize(new Dimension(100, d.height));
		jTextFieldFormalID.setPreferredSize(new Dimension(100, d.height));
	}
	return jTextFieldFormalID;
}

@SuppressWarnings({ "rawtypes", "unchecked" })
private JComboBox getJComboBoxURI() {
	if (jComboBoxURI == null) {
		jComboBoxURI = new JComboBox();
		defaultComboBoxModelURI = new DefaultComboBoxModel();
		jComboBoxURI.setModel(defaultComboBoxModelURI);
		Dimension d = jComboBoxURI.getPreferredSize();
		jComboBoxURI.setMinimumSize(new Dimension(MAX_URI_LENGTH, d.height));
		jComboBoxURI.setPreferredSize(new Dimension(MAX_URI_LENGTH, d.height));
		ComboboxToolTipRenderer renderer = new ComboboxToolTipRenderer();
		jComboBoxURI.setRenderer(renderer);
//		jComboBoxURI.setRenderer(new DefaultListCellRenderer() {
//			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//				if(value == null) {
//					return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//				} else {
//					DataType dt = (DataType)value;
//					Component comp = super.getListCellRendererComponent(list,dt.getDataTypeName(),index,isSelected,cellHasFocus);
//					return comp;
//				}
//			}
//		});
	}
	return jComboBoxURI;
}

private JPanel getBottomPanel() {
	if (jBottomPanel == null) {
		
		jBottomPanel = new JPanel();
		jBottomPanel.setLayout(new BorderLayout());
		jBottomPanel.add(getSplitPane(), BorderLayout.CENTER);
	}
	return jBottomPanel;

}
private JSplitPane getSplitPane() {
	if (splitPane == null) {
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridBagLayout());
		leftPanel.setBackground(Color.white);
		
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 0;
		gbc.insets = new Insets(3, 3, 1, 3);
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		leftPanel.add(getJPanelLeftTitle(), gbc);

		gridy++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 0;
		gbc.insets = new Insets(2, 3, 1, 3);
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		leftPanel.add(getJPanelIdentifierManager(), gbc);

		gridy++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(2, 3, 1, 3);
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		leftPanel.add(getJScrollPane(), gbc);
		
		gridy++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 0;
		gbc.insets = new Insets(2, 3, 3, 3);
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		leftPanel.add(getJPanelLowerLeft(), gbc);
		// -----------------------------------------------------------------------
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridBagLayout());
		rightPanel.setBackground(Color.white);

		gridy = 0;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(3, 3, 2, 2);
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		rightPanel.add(getJPanelRightTitle(), gbc);

		annotationTextArea = new JTextPane();
		annotationTextArea.setContentType("text/html");
		annotationTextArea.setEditable(false);
		annotationTextArea.addFocusListener(eventHandler);
		annotationTextArea.addMouseListener(eventHandler);
		JScrollPane annotationPanel = new JScrollPane(annotationTextArea);

		gridy++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(1, 3, 1, 2);
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		rightPanel.add(annotationPanel, gbc);
		// ----------------------------------------------------------------------------------
				
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setLeftComponent(leftPanel);
		splitPane.setRightComponent(rightPanel);
		splitPane.setResizeWeight(0.1);
		splitPane.setDividerLocation(0.3);
		
		annotationTextArea.addKeyListener(eventHandler);
	}
	return splitPane;
}
	
private JScrollPane getJScrollPane() {
	if (jScrollPane == null) {
		jScrollPane = new JScrollPane();
		jScrollPane.setViewportView(getJTreeMIRIAM());
	}
	return jScrollPane;
}
private JTree getJTreeMIRIAM() {
	if (jTreeMIRIAM == null) {
		try {
			DefaultTreeSelectionModel ivjLocalSelectionModel;
			ivjLocalSelectionModel = new DefaultTreeSelectionModel();
			ivjLocalSelectionModel.setSelectionMode(1);
			jTreeMIRIAM = new JTree();
			jTreeMIRIAM.setName("JTree1");
			jTreeMIRIAM.setToolTipText("");
			jTreeMIRIAM.setBounds(0, 0, 357, 405);
			jTreeMIRIAM.setMinimumSize(new java.awt.Dimension(100, 72));
			jTreeMIRIAM.setSelectionModel(ivjLocalSelectionModel);
			jTreeMIRIAM.setRowHeight(0);
			jTreeMIRIAM.setRootVisible(false);
			
			// Add cellRenderer
			DefaultTreeCellRenderer dtcr = new BioModelCellRenderer(null) {
				@Override
				public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
					// default for LinkNode is in BioModelCellRenderer.java
					JLabel component = (JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
					// TODO: here
					component.setIcon(null);
					return component;
				}
			};
			jTreeMIRIAM.setCellRenderer(dtcr);
			
			MouseListener mouseListener = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
//					TreePath closestMousePath =jTreeMIRIAM.getClosestPathForLocation(e.getPoint().x, e.getPoint().y);
//					showPopup(e, closestMousePath);
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					super.mouseReleased(e);
//					TreePath closestMousePath =jTreeMIRIAM.getClosestPathForLocation(e.getPoint().x, e.getPoint().y);
//					showPopup(e,closestMousePath);
				}

				public void mousePressed(MouseEvent e) {
					TreePath closestMousePath =jTreeMIRIAM.getClosestPathForLocation(e.getPoint().x, e.getPoint().y);
					jTreeMIRIAM.setSelectionPath(closestMousePath);
//					showPopup(e,closestMousePath);
					if(e.getClickCount() == 2) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)jTreeMIRIAM.getLastSelectedPathComponent();
						if (node instanceof LinkNode) {
							showBrowseToLink((LinkNode)node);
						}
					}
				} 
			};
			jTreeMIRIAM.addMouseListener(mouseListener);

		} catch (java.lang.Throwable ivjExc) {
			ivjExc.printStackTrace(System.out);
		}
	}
	return jTreeMIRIAM;
}
private void showBrowseToLink(LinkNode linkNode) {
	String link = linkNode.getLink();
	if (link != null) {
		DialogUtils.browserLauncher(jTreeMIRIAM, link, "failed to launch");
	}else{
		DialogUtils.showWarningDialog(this, "No Web-site link available");
	}
}
private void showBrowseToLink(VCMetaDataDataType md) {
	String link = md.getDataTypeURL();
	if (link != null) {
		DialogUtils.browserLauncher(getJComboBoxURI(), link, "Failed to launch");
	} else {
		DialogUtils.showWarningDialog(this, "No Web-site link available");
	}
}

private void initialize() {
	try {
		setLayout(new GridBagLayout());
		setBackground(Color.white);
		
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		add(getBottomPanel(), gbc);
		
		JTextPane tenpTextPane = new JTextPane();	// used just to initialize emptyText once
		tenpTextPane.setContentType("text/html");
		tenpTextPane.setText(null);
		emptyHtmlText = tenpTextPane.getText();
		
		initializeComboBoxQualifier();

        getJTreeMIRIAM().addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreePath tp = ((JTree)e.getSource()).getSelectionPath();
				if(tp == null) {
					getJButtonDeleteRef().setEnabled(false);
				} else {
					Object lastPathComponent = tp.getLastPathComponent();
					getJButtonDeleteRef().setEnabled(tp != null && lastPathComponent instanceof LinkNode);
				}
			}
		});

	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private void changeTextAnnotation() {
	try{
		if (bioModel == null || selectedObject == null) {
			return;
		}
		Identifiable entity = getIdentifiable(selectedObject);
		String textAreaStr = (annotationTextArea.getText() == null || annotationTextArea.getText().length()==0?null:annotationTextArea.getText());
		String oldText = bioModel.getVCMetaData().getFreeTextAnnotation(entity);

		if(textAreaStr == null || textAreaStr.isEmpty() || emptyHtmlText.equals(textAreaStr)) {	// no annotation now, the field is empty
			bioModel.getVCMetaData().deleteFreeTextAnnotation(entity);	// delete, if there's something previously saved
			if(selectedObject instanceof ReactionStep) {
				// we tell ReactionPropertiesPanel to refresh the annotation icon
				((ReactionStep) selectedObject).firePropertyChange("addIdentifier", false, true);
			}
		} else if(!Compare.isEqualOrNull(oldText,textAreaStr)) {		// some text annotation different from what's already saved
			bioModel.getVCMetaData().setFreeTextAnnotation(entity, textAreaStr);	// overwrite
			if(selectedObject instanceof ReactionStep) {
				// we tell ReactionPropertiesPanel to refresh the text annotation icon
				((ReactionStep) selectedObject).firePropertyChange("addIdentifier", false, true);
			}
		}
	} catch(Exception e) {
		e.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this,"Annotation Error\n"+e.getMessage(), e);
	}
}
private void removeText() {
	if (bioModel == null || selectedObject == null) {
		return;
	}
	Identifiable entity = getIdentifiable(selectedObject);
	annotationTextArea.setText(null);
	bioModel.getVCMetaData().deleteFreeTextAnnotation(entity);	// delete, if there's something previously saved
	if(selectedObject instanceof ReactionStep) {
		((ReactionStep) selectedObject).firePropertyChange("addIdentifier", false, true);
	}
}

public void setBioModel(BioModel newValue) {
	if (newValue == bioModel) {
		return;
	}
	bioModel = newValue;
	vcMetaData = bioModel.getVCMetaData();

	// set tree model on jTableMIRIAM here, since we have access to miriamDescrHeir here
	miriamTreeModel = new MiriamTreeModel(new DefaultMutableTreeNode("Object Annotations",true), vcMetaData, false);
	jTreeMIRIAM.setModel(miriamTreeModel);

	initializeComboBoxURI();
	getJTextFieldFormalID().setText("NewID");
	updateInterface();
}

private void updateInterface() {
	if (bioModel == null) {
		return;
	}
	Identifiable entity = getIdentifiable(selectedObject);
	if(selectedObject != null && entity != null) {
		getJComboBoxURI().setEnabled(true);
		getJTextFieldFormalID().setEnabled(true);
		getJButtonAddRef().setEnabled(true);
		getJButtonRemoveText().setEnabled(true);
		VCMetaDataDataType mdt = (VCMetaDataDataType)getJComboBoxURI().getSelectedItem();
		miriamTreeModel.createTree(entity);
		
		String freeText = bioModel.getVCMetaData().getFreeTextAnnotation(entity);
		if(freeText == null || freeText.isEmpty()) {
			annotationTextArea.setText(null);
		} else {
			annotationTextArea.setText(freeText);
		}
		annotationTextArea.setEditable(true);
		annotationTextArea.setCaretPosition(0);
	} else {
		getJComboBoxURI().setEnabled(false);
		getJTextFieldFormalID().setEnabled(false);
		getJButtonAddRef().setEnabled(false);
		getJButtonRemoveText().setEnabled(false);
		miriamTreeModel.createTree(null);

		annotationTextArea.setText(null);
		annotationTextArea.setEditable(false);
	}
}
public static Identifiable getIdentifiable(Identifiable selectedObject) {
	//
	// for SpeciesContext objects:
	//		MIRIAM tree needs the Species				!!!
	//		text Annotation needs the SpeciesContext
	//
	// !!!	use this only for the tree !!!
	//
	if(selectedObject == null) {
		return null;
	}
	if(selectedObject instanceof SpeciesContext) {
		return (Species)((SpeciesContext)selectedObject).getSpecies();
	} else if(selectedObject instanceof MolecularType) {
		return (MolecularType)selectedObject;
	} else if(selectedObject instanceof ReactionRule) {
		return (ReactionRule)selectedObject;
	} else if(selectedObject instanceof ReactionStep) {
		return (ReactionStep)selectedObject;
	} else if(selectedObject instanceof RbmObservable) {
		return (RbmObservable)selectedObject;
	} else if(selectedObject instanceof BioModel) {
		return (BioModel)selectedObject;
	} else if(selectedObject instanceof Structure) {
		return (Structure)selectedObject;
	} else if(selectedObject instanceof BioPaxObject) {
		return null;
	} else if(selectedObject instanceof Model.ModelParameter) {
		return (Model.ModelParameter)selectedObject;
	} else if(selectedObject instanceof SimulationContext) {
		return (SimulationContext)selectedObject;
//	} else if(selectedObject instanceof Simulation) {	// TODO: Simulation must implement Identifiable, Displayable
//		return (Simulation)selectedObject;
//	} else if(selectedObject instanceof Structure) {
//		return (Structure)selectedObject;
	} else {
		return null;
	}
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects != null && selectedObjects.length == 1) {
		if(selectedObjects[0] instanceof BioModelInfo || selectedObjects[0] instanceof MathModelInfo) {
			return;
		}
		if(selectedObjects[0] instanceof Identifiable && selectedObjects[0] instanceof Displayable) {
			selectedObject = (Identifiable)selectedObjects[0];
			System.out.println("class: " + selectedObject.getClass().getSimpleName() + ", selected object: " + ((Displayable)selectedObject).getDisplayName());
		} else {
			selectedObject = null;
			System.out.println("Unsupported or null entity");
		}
		initializeComboBoxURI();		// if selectedObject or entity are null we just empty the combobox
		getJButtonDeleteRef().setEnabled(false);
		getJTextFieldFormalID().setText("NewID");
		updateInterface();
	}
}
	
// -------------------------------------------------------------------------------------------------------

private void addIdentifier() {
	if(PopupGenerator.showComponentOKCancelDialog(AnnotationsPanel.this, getJPanelNewIdentifier(), "Define New Formal Identifier") != JOptionPane.OK_OPTION) {
		return;
	}
	MIRIAMQualifier qualifier = (MIRIAMQualifier)getJComboBoxQualifier().getSelectedItem();
	MiriamManager.DataType objectNamespace = (MiriamManager.DataType)getJComboBoxURI().getSelectedItem();
	String objectID = getJTextFieldFormalID().getText();
	if(objectID.compareTo("NewID") == 0) {
		return;
	}
	MiriamManager miriamManager = vcMetaData.getMiriamManager();
	HashSet<MiriamResource> miriamResources = new HashSet<MiriamResource>();
	try {
		Identifiable entity = getIdentifiable(selectedObject);
		MiriamResource mr = miriamManager.createMiriamResource(objectNamespace.getBaseURN()+":"+objectID);
		miriamResources.add(mr);
		miriamManager.addMiriamRefGroup(entity, qualifier, miriamResources);
//		System.out.println(vcMetaData.printRdfStatements());
		updateInterface();
		if(selectedObject instanceof ReactionStep) {
			// we tell ReactionPropertiesPanel to refresh the annotation icon
			((ReactionStep) selectedObject).firePropertyChange("addIdentifier", false, true);
		}
	} catch (Exception e) {
		e.printStackTrace();
		DialogUtils.showErrorDialog(this,"Add Identifier failed:\n"+e.getMessage(), e);
	}
}
private void removeIdentifier() {
	Object treeNode = jTreeMIRIAM.getLastSelectedPathComponent();
	if (treeNode instanceof LinkNode) {
		LinkNode linkNode = (LinkNode)treeNode;
		MiriamResource resourceToDelete = linkNode.getMiriamResource();
		Identifiable entity = getIdentifiable(selectedObject);
		//Map<MiriamRefGroup, MIRIAMQualifier> refGroupsToRemove = vcMetaData.getMiriamManager().getAllMiriamRefGroups(entity);
		MiriamManager mm = vcMetaData.getMiriamManager();
		Map<MiriamRefGroup,MIRIAMQualifier> refGroupsToRemove = mm.getMiriamTreeMap().get(entity);
		
		boolean found = false;
		for (MiriamRefGroup refGroup : refGroupsToRemove.keySet()){
			MIRIAMQualifier qualifier = refGroupsToRemove.get(refGroup);
			for (MiriamResource miriamResource : refGroup.getMiriamRefs()) {
				
				if(!isEqual(miriamResource, resourceToDelete)) {
					continue;
				}
				try {
					mm.remove2(entity, qualifier, refGroup);	// remove the ref group for this resource
					System.out.println(vcMetaData.printRdfStatements());

					found = true;
					break;
				} catch (URNParseFailureException e) {
					e.printStackTrace(System.out);
				}
			}
			if(found == true) {
				updateInterface();
				if(selectedObject instanceof ReactionStep) {
					((ReactionStep) selectedObject).firePropertyChange("addIdentifier", true, false);
				}
				break;
			}
		}
	}
}

private boolean isEqual(MiriamResource aThis, MiriamResource aThat) {
	if (aThis == aThat) {
		return true;
	}
	if (!(aThis instanceof MiriamResource)) {
		return false;
	}
	if (!(aThat instanceof MiriamResource)) {
		return false;
	}
	MiriamResource athis = (MiriamResource)aThis;
	MiriamResource athat = (MiriamResource)aThat;
	
	if (athis.getDataType() != null && athat.getDataType() != null && !athis.getDataType().equals(athat.getDataType())) {
		return false;
	}
	if (!Compare.isEqual(athis.getMiriamURN(), athat.getMiriamURN())) {
		return false;
	}
	if (!Compare.isEqual(athis.getIdentifier(), athat.getIdentifier())) {
		return false;
	}
	return true;
}

@SuppressWarnings("unchecked")
private void initializeComboBoxURI() {
	Identifiable entity = getIdentifiable(selectedObject);
	defaultComboBoxModelURI.removeAllElements();
	List<String> tooltips = new ArrayList<String> ();
	List<DataType> dataTypeList = new ArrayList<>();
	if(entity == null) {
		for (DataType dt : vcMetaData.getMiriamManager().getAllDataTypes().values()) {
			dataTypeList.add(dt);
		}
	} else {	
		for (DataType dt : VCMetaDataMiriamManager.getSpecificDataTypes(entity)) {
			dataTypeList.add(dt);
		}
	}
	Collections.sort(dataTypeList);
	for(DataType dt : dataTypeList) {
		tooltips.add(dt.getDescription());
		defaultComboBoxModelURI.addElement(dt);
	}
	((ComboboxToolTipRenderer)getJComboBoxURI().getRenderer()).setTooltips(tooltips);
}
private void initializeComboBoxQualifier() {
	//Set<MIRIAMQualifier> MIRIAM_all;
	//AnnotationQualifiers.MIRIAM_all;
	List<MIRIAMQualifier> qualifierList = new ArrayList<>();
	for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAM_all) {
		qualifierList.add(qualifier);
	}
	Collections.sort(qualifierList);
	for(MIRIAMQualifier qualifier : qualifierList) {
		defaultComboBoxModelQualifier.addElement(qualifier);
	}
}

private JButton getJButtonAddRef() {
	if (jButtonAddRef == null) {
		jButtonAddRef = new JButton();
		jButtonAddRef.setText(ACTION_ADD);
		jButtonAddRef.setToolTipText("Add a new reference with this provider");
	}
	return jButtonAddRef;
}
private JButton getJButtonDeleteRef() {
	if (jButtonDeleteRef == null) {
		jButtonDeleteRef = new JButton();
		jButtonDeleteRef.setText(ACTION_DELETE);
		jButtonDeleteRef.setToolTipText("Delete the selected cross-reference");

	}
	return jButtonDeleteRef;
}
private JButton getJButtonRemoveText() {
	if (jButtonRemoveText == null) {
		jButtonRemoveText = new JButton();
		jButtonRemoveText.setText(ACTION_REMOVE_TEXT);
		jButtonRemoveText.setToolTipText("Remove the entire text annotation");
	}
	return jButtonRemoveText;
}
	/*

Universal (goes in all types of annotations):
PubMed ID https://www.ncbi.nlm.nih.gov/pubmed
DOI: https://www.doi.org/

Model
BioModels Database https://www.ebi.ac.uk/biomodels/
Reactome: https://reactome.org/
Grey: NeuronDB: https://senselab.med.yale.edu/neurondb/
Grey: https://senselab.med.yale.edu/modeldb/
Grey: CellML Model Repository: https://models.cellml.org/cellml

Species/Molecules/Observables:
CheBI: https://www.ebi.ac.uk/chebi/
Kegg Compound: https://www.genome.jp/kegg/compound/
Uniprot: https://www.uniprot.org/
Enzyme Nomenclature: http://www.sbcs.qmul.ac.uk/iubmb/enzyme/
Gene Ontology: http://www.geneontology.org/
Reactome: https://reactome.org/

Reactions
Kegg Reaction: https://www.genome.jp/kegg/reaction/
Kegg Pathway: https://www.genome.jp/kegg/pathway.html
Reactome: https://reactome.org/
Grey: BRENDA: https://www.brenda-enzymes.org/
Grey: BIND  https://www.bindingdb.org/bind/index.jsp

Parameters (all grey for now)
Grey:BIND https://www.bindingdb.org/bind/index.jsp

Structures
Grey: BRENDA: https://www.brenda-enzymes.org/
Grey: Gene Ontology  http://geneontology.org/


DataType DataType_PIR 			"PIRSF"
DataType DataType_DOI 			"DOI"
DataType DataType_BIOMODELS 	"BioModels Database"
DataType DataType_Chebi 		"ChEBI"
DataType DataType_IntAct 		"IntAct"
DataType DataType_InterPro 		"InterPro"
DataType DataType_ECCODE 		"Enzyme Nomenclature"
DataType DataType_ENSEMBLE 		"Ensembl"
DataType DataType_GO 			"Gene Ontology"
DataType DataType_KEGGCOMPOUND 	"KEGG Compound"
DataType DataType_KEGGPATHWAY 	"KEGG Pathway"
DataType DataType_KEGGREACTION 	"KEGG Reaction"
DataType DataType_PUBMED 		"PubMed"
DataType DataType_TAXONOMY 		"Taxonomy"
DataType DataType_REACTOME 		"Reactome"
DataType DataType_UNIPROT 		"UniProt"
DataType DataType_ICD 			"ICD"




*/





}
