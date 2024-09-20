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

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager;
import cbit.vcell.biomodel.meta.MiriamManager.DataType;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.VCMetaDataMiriamManager;
import cbit.vcell.biomodel.meta.VCMetaDataMiriamManager.VCMetaDataDataType;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.annotations.AddAnnotationsPanel;
import cbit.vcell.client.desktop.biomodel.annotations.SearchElement;
import cbit.vcell.desktop.BioModelCellRenderer;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.*;
import cbit.vcell.xml.gui.MiriamTreeModel;
import cbit.vcell.xml.gui.MiriamTreeModel.LinkNode;
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

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.List;
import java.util.*;

/**
 * Superclass for all the Annotation panels
 * Creation date: (11/6/2018 3:44:00 PM)
 *
 * @author: Dan Vasilescu
 */
@SuppressWarnings("serial")
public class AnnotationsPanel extends DocumentEditorSubPanel {
    private BioModel bioModel = null;
    private VCMetaData vcMetaData = null;
    MiriamTreeModel miriamTreeModel = null;

    private Identifiable selectedObject = null;

    public static final String ACTION_ADD = "Add...";
    public static final String ACTION_SEARCH = "Search...";
    public static final String ACTION_DELETE = "Delete";
    public static final String ACTION_REMOVE_TEXT = "Delete";
    public static final String ACTION_GOTO = "Go";
    public static final int COMBO_QUALIFIER_WIDTH = 200;
    public static final int MAX_DESCRIPTION_LENGTH = 145;
    public static final int MAX_URI_LENGTH = 130;

    private final EventHandler eventHandler = new EventHandler();

    private JTree jTreeMIRIAM = null;

    private JPanel jPanelNewIdentifier = null;            // add new identity dialog
    private JPanel jPanelIdentifierManager = null;        // upper panel with the identity provider combobox, Navigate and Add buttons
    private JPanel jPanelLowerLeft = null;                // lower left panel with the  Delete buttons
    private JPanel jBottomPanel = null;
    private JSplitPane splitPane = null;
    private JScrollPane jScrollPane = null;            // will show the MIRIAM JTree

    private JComboBox jComboBoxURI = null;            // identity provider combo
    private DefaultComboBoxModel defaultComboBoxModelURI = null;
    private JComboBox jComboBoxQualifier = null;    // annotation qualifier combo (isDescribedBy, etc)
    private final DefaultComboBoxModel defaultComboBoxModelQualifier = new DefaultComboBoxModel();

    private JTextField jTextFieldFormalID = null;    // immortal ID text
    private JButton jButtonAddRef = null;            // add a known cross-reference
    private JButton jButtonSearchRef = null;        // search a database add a cross-reference
    private JButton jButtonDeleteRef = null;        // delete selected cross-reference
    private JButton jButtonRemoveText = null;        // remove text annotation

    JTextArea annotationTextArea = null;
    private String emptyHtmlText = null;    // content of annotationTextArea after initialization with null
    // will contain an empty html object "<html><header></header><body><p style="margin-top: 0"></p></body></html>"
    private static final String emptyHtmlText2 = "<html><header></header><body></body></html>";    // really empty

    private AddAnnotationsPanel addAnnotationsPanel; //new window to add annotations from online DBs.


    private class EventHandler extends MouseAdapter implements ActionListener, FocusListener, PropertyChangeListener, KeyListener {
        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            if (e.getSource() == AnnotationsPanel.this.annotationTextArea) {
                AnnotationsPanel.this.changeTextAnnotation();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            if (e.getSource() == AnnotationsPanel.this.annotationTextArea) {
                AnnotationsPanel.this.changeTextAnnotation();
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
//					|| evt.getSource() instanceof Simulation			// TODO: check why is not saving
//					|| evt.getSource() instanceof SpeciesContextSpec	// TODO: can't use species name for spec too, need some hack
//					|| evt.getSource() instanceof ReactionSpec
            ) {
                AnnotationsPanel.this.initializeComboBoxURI();
                AnnotationsPanel.this.updateInterface();
            }
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == AnnotationsPanel.this.getJButtonAddRef()) {
                AnnotationsPanel.this.addIdentifier();
            } else if (evt.getSource() == AnnotationsPanel.this.getJButtonSearchRef()) {
                AnnotationsPanel.this.initializeAddAnnotationsPanel();
            } else if (AnnotationsPanel.this.addAnnotationsPanel != null && evt.getSource() == AnnotationsPanel.this.addAnnotationsPanel.getOkButton()) {
                AnnotationsPanel.this.annotationTextArea.setText("");
            } else if (evt.getSource() == AnnotationsPanel.this.getJButtonDeleteRef()) {
                AnnotationsPanel.this.removeIdentifier();
            } else if (evt.getSource() == AnnotationsPanel.this.getJButtonRemoveText()) {
                AnnotationsPanel.this.removeText();
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

    public static class ComboboxToolTipRenderer extends DefaultListCellRenderer {
        List<String> tooltips;

        @SuppressWarnings("rawtypes")
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component comp;
            if (value == null) {
                comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            } else if (value instanceof DataType dt) {
                comp = super.getListCellRendererComponent(list, dt.getDataTypeName(), index, isSelected, cellHasFocus);
            } else if (value instanceof MIRIAMQualifier mc) {
                comp = super.getListCellRendererComponent(list, mc.getDescription(), index, isSelected, cellHasFocus);
            } else {
                comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
            if (-1 < index && null != value && null != this.tooltips) {
                list.setToolTipText(this.tooltips.get(index));
            }
            return comp;
        }

        public void setTooltips(List<String> tooltips) {
            this.tooltips = tooltips;
        }
    }


    public AnnotationsPanel() {
        super();
        this.initialize();
    }

    private void handleException(java.lang.Throwable exception) {
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    private JPanel getJPanelNewIdentifier() {
        this.jPanelNewIdentifier = new JPanel();
        this.jPanelNewIdentifier.setLayout(new GridBagLayout());
//	jPanelNewIdentifier.setPreferredSize(new Dimension(725, 37));
//	jPanelNewIdentifier.setBorder(BorderFactory.createLineBorder(SystemColor.windowBorder, 2));

        VCMetaDataDataType mdt = (VCMetaDataDataType) this.getJComboBoxURI().getSelectedItem();
        int gridx = 0;
        int gridy = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 15, 3, 0);        // top left bottom right
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.anchor = GridBagConstraints.WEST;
        this.jPanelNewIdentifier.add(new JLabel("Provider: "), gbc);

        gridx++;
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 4);
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.anchor = GridBagConstraints.WEST;
        this.jPanelNewIdentifier.add(new JLabel("<html><b>" + mdt.getDataTypeName() + "</b></html>"), gbc);

        // ------------------------------------- Qualifier combobox  -----------------------------

        gridx++;
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 15, 3, 0);        // top left bottom right
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        this.jPanelNewIdentifier.add(new JLabel("Qualifier"), gbc);

        gridx++;
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 4);
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        this.jPanelNewIdentifier.add(this.getJComboBoxQualifier(), gbc);

        // -------------------------------------------------------------------------------
        gridx++;
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 15, 3, 0);
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.anchor = GridBagConstraints.WEST;
        this.jPanelNewIdentifier.add(new JLabel("Identifier ID"), gbc);

        gridx++;
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 4);
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.anchor = GridBagConstraints.WEST;
        this.jPanelNewIdentifier.add(this.getJTextFieldFormalID(), gbc);
        this.getJTextFieldFormalID().setText("NewID");

        gridx++;
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 15, 3, 0);
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.anchor = GridBagConstraints.WEST;
        this.jPanelNewIdentifier.add(new JLabel("<html>Example: <b>" + mdt.getExample() + "</b></html>"), gbc);

        gridx++;
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 15, 3, 0);
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        this.jPanelNewIdentifier.add(new JLabel(), gbc);

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
                if (e.getClickCount() == 2) {
                    AnnotationsPanel.this.showBrowseToLink(mdt);
                }
            }
        });

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 15, 3, 0);
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = 8;
        gbc.anchor = GridBagConstraints.WEST;
        this.jPanelNewIdentifier.add(linkLabel, gbc);

        // ---------------------------------------------------------------------
        List<String> rows = new ArrayList<>();
        String value = mdt.getDescription();
        StringTokenizer tokenizer = new StringTokenizer(value, " ");
        String row = "";
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken();
            if ((row.length() + word.length()) > MAX_DESCRIPTION_LENGTH) {
                rows.add(row);
                row = word + " ";
            } else {
                row += word + " ";
            }
        }
        if (!row.isEmpty()) {
            rows.add(row);
        }
        for (String currentRow : rows) {
            gridx = 0;
            gridy++;
            gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 15, 3, 0);
            gbc.gridx = gridx;
            gbc.gridy = gridy;
            gbc.gridwidth = 8;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            this.jPanelNewIdentifier.add(new JLabel(currentRow), gbc);

        }
        return this.jPanelNewIdentifier;
    }

    private JPanel getJPanelLeftTitle() {
        JPanel jPanelLeftTitle = new JPanel();
        jPanelLeftTitle.setLayout(new GridBagLayout());
        jPanelLeftTitle.setPreferredSize(new Dimension(260, 37));
        jPanelLeftTitle.setBorder(BorderFactory.createLineBorder(SystemColor.windowBorder, 1));
        int gridx = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 15, 3, 0);        // top left bottom right
        gbc.gridx = gridx;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        jPanelLeftTitle.add(new JLabel("User defined cross-references."), gbc);

        gridx++;
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 6, 3, 8);
        gbc.gridx = gridx;
        gbc.gridy = 0;
        jPanelLeftTitle.add(this.getJButtonSearchRef(), gbc);
        this.getJButtonSearchRef().addActionListener(this.eventHandler);

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
        gbc.insets = new Insets(6, 8, 0, 0);        // top left bottom right
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
        jPanelRightTitle.add(this.getJButtonRemoveText(), gbc);

        this.getJButtonRemoveText().addActionListener(this.eventHandler);
        return jPanelRightTitle;
    }

    private JPanel getJPanelIdentifierManager() {
        if (this.jPanelIdentifierManager == null) {
            this.jPanelIdentifierManager = new JPanel();
            this.jPanelIdentifierManager.setLayout(new GridBagLayout());
            this.jPanelIdentifierManager.setPreferredSize(new Dimension(260, 300)); //37
            this.jPanelIdentifierManager.setBorder(BorderFactory.createLineBorder(SystemColor.windowBorder, 1));

            int gridx = 0;
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 15, 3, 0);        // top left bottom right
            gbc.gridx = gridx;
            gbc.gridy = 0;
            this.jPanelIdentifierManager.add(new JLabel("Provider"), gbc);

            gridx++;
            gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 5, 3, 4);
            gbc.gridx = gridx;
            gbc.gridy = 0;
            this.jPanelIdentifierManager.add(this.getJComboBoxURI(), gbc);

            gridx++;
            gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 5, 3, 5);
            gbc.gridx = gridx;
            gbc.gridy = 0;
            this.jPanelIdentifierManager.add(this.getJButtonSearchRef(), gbc);

            gridx++;
            gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 5, 3, 5);
            gbc.gridx = gridx;
            gbc.gridy = 0;
            this.jPanelIdentifierManager.add(this.getJButtonAddRef(), gbc);

            gridx++;
            gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 0, 3, 0);
            gbc.gridx = gridx;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            this.jPanelIdentifierManager.add(new JLabel(""), gbc);

            this.getJButtonAddRef().addActionListener(this.eventHandler);
            this.getJButtonSearchRef().addActionListener(this.eventHandler);
        }
        return this.jPanelIdentifierManager;
    }

    private JPanel getJPanelLowerLeft() {
        if (this.jPanelLowerLeft == null) {
            this.jPanelLowerLeft = new JPanel();
            this.jPanelLowerLeft.setLayout(new GridBagLayout());
            this.jPanelLowerLeft.setPreferredSize(new Dimension(300, 37));
            this.jPanelLowerLeft.setBorder(BorderFactory.createLineBorder(SystemColor.windowBorder, 1));

            int gridx = 0;
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 3, 3, 3);
            gbc.gridx = gridx;
            gbc.gridy = 0;
            this.jPanelLowerLeft.add(this.getJButtonDeleteRef(), gbc);

            gridx++;
            gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 0, 3, 0);
            gbc.gridx = gridx;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            this.jPanelLowerLeft.add(new JLabel(""), gbc);

            this.getJButtonDeleteRef().setEnabled(false);
            this.getJButtonDeleteRef().addActionListener(this.eventHandler);
        }
        return this.jPanelLowerLeft;
    }

    public JComboBox getJComboBoxQualifier() {
        if (this.jComboBoxQualifier == null) {
            this.jComboBoxQualifier = new JComboBox();
//		defaultComboBoxModelQualifier = new DefaultComboBoxModel();	// already allocated
            this.jComboBoxQualifier.setModel(this.defaultComboBoxModelQualifier);
            Dimension d = this.jComboBoxQualifier.getPreferredSize();
            this.jComboBoxQualifier.setMinimumSize(new Dimension(COMBO_QUALIFIER_WIDTH, d.height));
            this.jComboBoxQualifier.setPreferredSize(new Dimension(COMBO_QUALIFIER_WIDTH, d.height));
            ComboboxToolTipRenderer renderer = new ComboboxToolTipRenderer();
            this.jComboBoxQualifier.setRenderer(renderer);
        }
        return this.jComboBoxQualifier;
    }

    private JTextField getJTextFieldFormalID() {
        if (this.jTextFieldFormalID == null) {
            this.jTextFieldFormalID = new JTextField();
            this.jTextFieldFormalID.setText("NewID");
            Dimension d = this.jTextFieldFormalID.getPreferredSize();
            this.jTextFieldFormalID.setMinimumSize(new Dimension(100, d.height));
            this.jTextFieldFormalID.setPreferredSize(new Dimension(100, d.height));
        }
        return this.jTextFieldFormalID;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public JComboBox getJComboBoxURI() {
        if (this.jComboBoxURI == null) {
            this.jComboBoxURI = new JComboBox();
            this.defaultComboBoxModelURI = new DefaultComboBoxModel();
            this.jComboBoxURI.setModel(this.defaultComboBoxModelURI);
            Dimension d = this.jComboBoxURI.getPreferredSize();
            this.jComboBoxURI.setMinimumSize(new Dimension(MAX_URI_LENGTH, d.height));
            this.jComboBoxURI.setPreferredSize(new Dimension(MAX_URI_LENGTH, d.height));
            ComboboxToolTipRenderer renderer = new ComboboxToolTipRenderer();
            this.jComboBoxURI.setRenderer(renderer);
            this.jComboBoxURI.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    VCMetaDataDataType mdt = (VCMetaDataDataType) AnnotationsPanel.this.jComboBoxURI.getSelectedItem();
                    System.out.println("aici");
                    AnnotationsPanel.this.getJButtonSearchRef().setEnabled(mdt != null && mdt.isSearchable());
                    // aici

                }
            });
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
        return this.jComboBoxURI;
    }

    private JPanel getBottomPanel() {
        if (this.jBottomPanel == null) {
            this.jBottomPanel = new JPanel();
            this.jBottomPanel.setLayout(new BorderLayout());
            this.jBottomPanel.add(this.getSplitPane(), BorderLayout.CENTER);
        }
        return this.jBottomPanel;

    }

    private JSplitPane getSplitPane() {
        if (this.splitPane == null) {

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
            leftPanel.add(this.getJPanelLeftTitle(), gbc);

            gridy++;
            gbc = new java.awt.GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = gridy;
            gbc.weightx = 1.0;
            gbc.weighty = 0;
            gbc.insets = new Insets(2, 3, 1, 3);
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            leftPanel.add(this.getJPanelIdentifierManager(), gbc);

            gridy++;
            gbc = new java.awt.GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = gridy;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.insets = new Insets(2, 3, 1, 3);
            gbc.fill = java.awt.GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.SOUTHWEST;
            leftPanel.add(this.getJScrollPane(), gbc);

            gridy++;
            gbc = new java.awt.GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = gridy;
            gbc.weightx = 1.0;
            gbc.weighty = 0;
            gbc.insets = new Insets(2, 3, 3, 3);
            gbc.fill = java.awt.GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.SOUTHWEST;
            leftPanel.add(this.getJPanelLowerLeft(), gbc);
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
            rightPanel.add(this.getJPanelRightTitle(), gbc);

            this.annotationTextArea = new JTextArea();
//		annotationTextArea.setContentType("text/html");
            this.annotationTextArea.setEditable(false);
            this.annotationTextArea.addFocusListener(this.eventHandler);
            this.annotationTextArea.addMouseListener(this.eventHandler);
            this.annotationTextArea.setLineWrap(true);
            this.annotationTextArea.setWrapStyleWord(true);
            this.annotationTextArea.setCaretPosition(0);
            JScrollPane annotationPanel = new JScrollPane(this.annotationTextArea);

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

            this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            this.splitPane.setOneTouchExpandable(true);
            this.splitPane.setLeftComponent(leftPanel);
            this.splitPane.setRightComponent(rightPanel);
            this.splitPane.setResizeWeight(0.1);
            this.splitPane.setDividerLocation(0.3);

            this.annotationTextArea.addKeyListener(this.eventHandler);
        }
        return this.splitPane;
    }

    private JScrollPane getJScrollPane() {
        if (this.jScrollPane == null) {
            this.jScrollPane = new JScrollPane();
            this.jScrollPane.setViewportView(this.getJTreeMIRIAM());
        }
        return this.jScrollPane;
    }

    private JTree getJTreeMIRIAM() {
        if (this.jTreeMIRIAM == null) {
            try {
                DefaultTreeSelectionModel ivjLocalSelectionModel;
                ivjLocalSelectionModel = new DefaultTreeSelectionModel();
                ivjLocalSelectionModel.setSelectionMode(1);
                this.jTreeMIRIAM = new JTree();
                this.jTreeMIRIAM.setName("JTree1");
                this.jTreeMIRIAM.setToolTipText("");
                this.jTreeMIRIAM.setBounds(0, 0, 357, 405);
                this.jTreeMIRIAM.setMinimumSize(new java.awt.Dimension(100, 72));
                this.jTreeMIRIAM.setSelectionModel(ivjLocalSelectionModel);
                this.jTreeMIRIAM.setRowHeight(0);
                this.jTreeMIRIAM.setRootVisible(false);

                // Add cellRenderer
                DefaultTreeCellRenderer dtcr = new BioModelCellRenderer(null) {
                    @Override
                    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                        // default for LinkNode is in BioModelCellRenderer.java
                        JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                        // TODO: here
                        component.setIcon(null);
                        return component;
                    }
                };
                this.jTreeMIRIAM.setCellRenderer(dtcr);

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
                        TreePath closestMousePath = AnnotationsPanel.this.jTreeMIRIAM.getClosestPathForLocation(e.getPoint().x, e.getPoint().y);
                        AnnotationsPanel.this.jTreeMIRIAM.setSelectionPath(closestMousePath);
//					showPopup(e,closestMousePath);
                        if (e.getClickCount() == 2) {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) AnnotationsPanel.this.jTreeMIRIAM.getLastSelectedPathComponent();
                            if (node instanceof LinkNode) {
                                AnnotationsPanel.this.showBrowseToLink((LinkNode) node);
                            }
                        }
                    }
                };
                this.jTreeMIRIAM.addMouseListener(mouseListener);

            } catch (java.lang.Throwable ivjExc) {
                ivjExc.printStackTrace(System.out);
            }
        }
        return this.jTreeMIRIAM;
    }

    private void showBrowseToLink(LinkNode linkNode) {
        String link = linkNode.getLink();
        if (link != null) {
            DialogUtils.browserLauncher(this.jTreeMIRIAM, link, "failed to launch");
        } else {
            DialogUtils.showWarningDialog(this, "No Web-site link available");
        }
    }

    public void showBrowseToLink(VCMetaDataDataType md) {
        String link = md.getDataTypeURL();
        if (link != null) {
            DialogUtils.browserLauncher(this.getJComboBoxURI(), link, "Failed to launch");
        } else {
            DialogUtils.showWarningDialog(this, "No Web-site link available");
        }
    }

    private void initialize() {
        try {
            this.setLayout(new GridBagLayout());
            this.setBackground(Color.white);

            int gridy = 0;
            GridBagConstraints gbc = new java.awt.GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = gridy;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.fill = java.awt.GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.SOUTHWEST;
            this.add(this.getBottomPanel(), gbc);

            JTextPane tenpTextPane = new JTextPane();    // used just to initialize emptyText once
            tenpTextPane.setContentType("text/html");
            tenpTextPane.setText(null);
            this.emptyHtmlText = tenpTextPane.getText();

            this.initializeComboBoxQualifier();
            this.initializeComboBoxURI();

            JTree miriamTree = this.getJTreeMIRIAM();
            miriamTree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    TreePath tp = ((JTree) e.getSource()).getSelectionPath();
                    if (tp == null) {
                        AnnotationsPanel.this.getJButtonDeleteRef().setEnabled(false);
                    } else {
                        Object lastPathComponent = tp.getLastPathComponent();
                        AnnotationsPanel.this.getJButtonDeleteRef().setEnabled(tp != null && lastPathComponent instanceof LinkNode);
                    }
                }
            });

        } catch (java.lang.Throwable ivjExc) {
            this.handleException(ivjExc);
        }
    }

    public void addToAnnotationTextArea(String annotDescription) {
//		annotationTextArea.setText("Text can be set from addToAnnotation method");
        String existingTest = this.annotationTextArea.getText();
        if (this.annotationTextArea.getText().length() != 0) {
            this.annotationTextArea.setText(existingTest + "\n" + annotDescription);
        } else {
            this.annotationTextArea.setText(annotDescription);
        }

//		String existingText = "";
//		if (annotationTextArea != null || annotationTextArea.getText().length() != 0) {
//			existingText = annotationTextArea.getText();
//		}
//		annotationTextArea.setText("\n" + existingText + annotDescription);
        this.changeTextAnnotation();
    }

    private void changeTextAnnotation() {
        try {
            if (this.bioModel == null || this.selectedObject == null) {
                return;
            }
            Identifiable entity = getIdentifiable(this.selectedObject);
            String textAreaStr = (this.annotationTextArea.getText() == null || this.annotationTextArea.getText().length() == 0 ? null : this.annotationTextArea.getText());
            String oldText = this.bioModel.getVCMetaData().getFreeTextAnnotation(entity);

            if (textAreaStr == null || textAreaStr.isEmpty() || this.emptyHtmlText.equals(textAreaStr)) {    // no annotation now, the field is empty
                this.bioModel.getVCMetaData().deleteFreeTextAnnotation(entity);    // delete, if there's something previously saved
                if (this.selectedObject instanceof ReactionStep) {
                    // we tell ReactionPropertiesPanel to refresh the annotation icon
                    ((ReactionStep) this.selectedObject).firePropertyChange("addIdentifier", false, true);
                }
            } else if (!Compare.isEqualOrNull(oldText, textAreaStr)) {        // some text annotation different from what's already saved
                this.bioModel.getVCMetaData().setFreeTextAnnotation(entity, textAreaStr);    // overwrite
                if (this.selectedObject instanceof ReactionStep) {
                    // we tell ReactionPropertiesPanel to refresh the text annotation icon
                    ((ReactionStep) this.selectedObject).firePropertyChange("addIdentifier", false, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            PopupGenerator.showErrorDialog(this, "Annotation Error\n" + e.getMessage(), e);
        }
    }

    private void removeText() {
        if (this.bioModel == null || this.selectedObject == null) {
            return;
        }
        Identifiable entity = getIdentifiable(this.selectedObject);
        this.annotationTextArea.setText(null);
        this.bioModel.getVCMetaData().deleteFreeTextAnnotation(entity);    // delete, if there's something previously saved
        if (this.selectedObject instanceof ReactionStep) {
            ((ReactionStep) this.selectedObject).firePropertyChange("addIdentifier", false, true);
        }
    }

    public void setBioModel(BioModel newValue) {
        if (newValue == this.bioModel) {
            return;
        }
        this.bioModel = newValue;
        this.vcMetaData = this.bioModel.getVCMetaData();

        // set tree model on jTableMIRIAM here, since we have access to miriamDescrHeir here
        this.miriamTreeModel = new MiriamTreeModel(new DefaultMutableTreeNode("Object Annotations", true), this.vcMetaData, false);
        this.jTreeMIRIAM.setModel(this.miriamTreeModel);

        this.initializeComboBoxURI();
        this.getJTextFieldFormalID().setText("NewID");
        this.updateInterface();
    }

    private void updateInterface() {
        if (this.bioModel == null) {
            return;
        }
        Identifiable entity = getIdentifiable(this.selectedObject);
        if (this.selectedObject != null && entity != null) {
            this.getJComboBoxURI().setEnabled(true);
            this.getJTextFieldFormalID().setEnabled(true);
            this.getJButtonAddRef().setEnabled(true);
            this.getJButtonRemoveText().setEnabled(true);
            VCMetaDataDataType mdt = (VCMetaDataDataType) this.getJComboBoxURI().getSelectedItem();
            this.getJButtonSearchRef().setEnabled(mdt != null && mdt.isSearchable());
            this.miriamTreeModel.createTree(entity);

            String freeText = this.bioModel.getVCMetaData().getFreeTextAnnotation(entity);
            if (freeText == null || freeText.isEmpty()) {
                this.annotationTextArea.setText(null);
            } else {
                this.annotationTextArea.setText(freeText);
            }
            this.annotationTextArea.setEditable(true);
            this.annotationTextArea.setCaretPosition(0);
        } else {
            this.getJComboBoxURI().setEnabled(false);
            this.getJTextFieldFormalID().setEnabled(false);
            this.getJButtonAddRef().setEnabled(false);
            this.getJButtonSearchRef().setEnabled(false);
            this.getJButtonRemoveText().setEnabled(false);
            this.miriamTreeModel.createTree(null);

            this.annotationTextArea.setText(null);
            this.annotationTextArea.setEditable(false);
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
        if (selectedObject == null) {
            return null;
        }
        if (selectedObject instanceof SpeciesContext) {
            return ((SpeciesContext) selectedObject).getSpecies();
        } else if (selectedObject instanceof MolecularType) {
            return selectedObject;
        } else if (selectedObject instanceof ReactionRule) {
            return selectedObject;
        } else if (selectedObject instanceof ReactionStep) {
            return selectedObject;
        } else if (selectedObject instanceof RbmObservable) {
            return selectedObject;
        } else if (selectedObject instanceof BioModel) {
            return selectedObject;
        } else if (selectedObject instanceof Structure) {
            return selectedObject;
        } else if (selectedObject instanceof BioPaxObject) {
            return null;
        } else if (selectedObject instanceof Model.ModelParameter) {
            return selectedObject;
        } else if (selectedObject instanceof SimulationContext) {
            return selectedObject;
//	} else if(selectedObject instanceof Simulation) {
//		return (Simulation)selectedObject;
//	} else if(selectedObject instanceof SpeciesContextSpec) {
//		return (SpeciesContextSpec)selectedObject;
//	} else if(selectedObject instanceof ReactionSpec) {
//		return (ReactionSpec)selectedObject;
        } else {
            return null;
        }
    }

    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {
        if (selectedObjects != null && selectedObjects.length == 1) {
            if (selectedObjects[0] instanceof BioModelInfo || selectedObjects[0] instanceof MathModelInfo) {
                return;
            }
            if (selectedObjects[0] instanceof Identifiable && selectedObjects[0] instanceof Displayable) {
                this.selectedObject = (Identifiable) selectedObjects[0];
                System.out.println("AnnotationsPanel: class: " + this.selectedObject.getClass().getSimpleName() + ", selected object: " + ((Displayable) this.selectedObject).getDisplayName());
            } else {
                this.selectedObject = null;
                System.out.println("Unsupported or null entity");
            }
            this.initializeComboBoxURI();        // if selectedObject or entity are null we just empty the combobox
            this.getJButtonDeleteRef().setEnabled(false);
            this.getJTextFieldFormalID().setText("NewID");
            this.updateInterface();
        }
    }

    // -------------------------------------------------------------------------------------------------------
    private void initializeAddAnnotationsPanel() {
        //generate addAnnotationPanel with URI and Qualifier ComboBox
        if (this.addAnnotationsPanel == null) {
            this.addAnnotationsPanel = new AddAnnotationsPanel(this, this.getJComboBoxURI(), this.getJComboBoxQualifier());
        } else {
            this.addAnnotationsPanel.dispose();
            this.addAnnotationsPanel = new AddAnnotationsPanel(this, this.getJComboBoxURI(), this.getJComboBoxQualifier());
        }
    }

    private void addIdentifier() {                // ----- used for add by direct editing / typing
        if (PopupGenerator.showComponentOKCancelDialog(AnnotationsPanel.this, this.getJPanelNewIdentifier(), "Define New Formal Identifier") != JOptionPane.OK_OPTION) {
            return;
        }
        MIRIAMQualifier qualifier = (MIRIAMQualifier) this.getJComboBoxQualifier().getSelectedItem();
        MiriamManager.DataType objectNamespace = (MiriamManager.DataType) this.getJComboBoxURI().getSelectedItem();
        String objectID = this.getJTextFieldFormalID().getText();
        if (objectID.compareTo("NewID") == 0) {
            return;
        }
        MiriamManager miriamManager = this.vcMetaData.getMiriamManager();
        HashSet<MiriamResource> miriamResources = new HashSet<MiriamResource>();
        try {
            Identifiable entity = getIdentifiable(this.selectedObject);
            MiriamResource mr = miriamManager.createMiriamResource(objectNamespace.getBaseURN() + ":" + objectID);
            miriamResources.add(mr);
            miriamManager.addMiriamRefGroup(entity, qualifier, miriamResources);
//		System.out.println(vcMetaData.printRdfStatements());
            this.updateInterface();
            if (this.selectedObject instanceof ReactionStep) {
                // we tell ReactionPropertiesPanel to refresh the annotation icon
                ((ReactionStep) this.selectedObject).firePropertyChange("addIdentifier", false, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showErrorDialog(this, "Add Identifier failed:\n" + e.getMessage(), e);
        }
    }

    public void addIdentifier(SearchElement searchElement) {    // ----- used for search
        MIRIAMQualifier qualifier = (MIRIAMQualifier) this.getJComboBoxQualifier().getSelectedItem();

        MiriamManager.DataType objectNamespace = (MiriamManager.DataType) this.getJComboBoxURI().getSelectedItem();

//		String objectID = getJTextFieldFormalID().getText();
        URI uri = searchElement.getDbLink();
        String dataTypeName = objectNamespace.getDataTypeName();
        String objectID = this.getId(uri, dataTypeName);
//		if(objectID.compareTo("NewID") == 0) {
//			return;
//		}
        MiriamManager miriamManager = this.vcMetaData.getMiriamManager();
        HashSet<MiriamResource> miriamResources = new HashSet<>();
        try {
            Identifiable entity = getIdentifiable(this.selectedObject);
            MiriamResource mr = miriamManager.createMiriamResource(objectNamespace.getBaseURN() + ":" + objectID);
            miriamResources.add(mr);
            miriamManager.addMiriamRefGroup(entity, qualifier, miriamResources);
//		System.out.println(vcMetaData.printRdfStatements());
            this.updateInterface();
            if (this.selectedObject instanceof ReactionStep) {
                // we tell ReactionPropertiesPanel to refresh the annotation icon
                ((ReactionStep) this.selectedObject).firePropertyChange("addIdentifier", false, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showErrorDialog(this, "Add Identifier failed:\n" + e.getMessage(), e);
        }
    }

    private String getId(URI uri, String dataTypeName) {
        String id = "IDError";

        if (uri.toString().contains("BTO"))
            id = uri.getPath().replace("/obo/BTO_", "");
        else if (uri.toString().contains("CHEBI"))
            id = uri.getPath().replace("/obo/CHEBI_", "");
        else if (uri.toString().contains("CL"))
            id = uri.getPath().replace("/obo/CL_", "");
        else if (uri.toString().contains("EFO"))
            id = uri.getPath().replace("/efo/EFO_", "");
        else if (uri.toString().contains("GO"))
            id = uri.getPath().replace("/obo/GO_", "");
        else if (uri.toString().contains("ncit"))
            id = uri.toString().replace("https://ncit.nci.nih.gov/ncitbrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus&ns=ncit&code=", "");
        else if (uri.toString().contains("PATO"))
            id = uri.getPath().replace("/obo/PATO_", "");
        else if (uri.toString().contains("PR"))
            id = uri.getPath().replace("/obo/PR_", "");
        else if (uri.toString().contains("uniprot"))
            id = uri.getPath().replace("/uniprot/", "");
        else if (uri.toString().contains("SBO/")) {
            id = uri.getPath().replace("/SBO/", "");
            id = id.replace("SBO_", "SBO:");
        }

        return id;
    }

    private void removeIdentifier() {
        Object treeNode = this.jTreeMIRIAM.getLastSelectedPathComponent();
        if (treeNode instanceof LinkNode linkNode) {
            MiriamResource resourceToDelete = linkNode.getMiriamResource();
            Identifiable entity = getIdentifiable(this.selectedObject);
            //Map<MiriamRefGroup, MIRIAMQualifier> refGroupsToRemove = vcMetaData.getMiriamManager().getAllMiriamRefGroups(entity);
            MiriamManager mm = this.vcMetaData.getMiriamManager();
            Map<MiriamRefGroup, MIRIAMQualifier> refGroupsToRemove = mm.getMiriamTreeMap().get(entity);

            boolean found = false;
            for (MiriamRefGroup refGroup : refGroupsToRemove.keySet()) {
                MIRIAMQualifier qualifier = refGroupsToRemove.get(refGroup);
                for (MiriamResource miriamResource : refGroup.getMiriamRefs()) {

                    if (!this.isEqual(miriamResource, resourceToDelete)) {
                        continue;
                    }
                    try {
                        mm.remove2(entity, qualifier, refGroup);    // remove the ref group for this resource
                        System.out.println(this.vcMetaData.printRdfStatements());

                        found = true;
                        break;
                    } catch (URNParseFailureException e) {
                        e.printStackTrace(System.out);
                    }
                }
                if (found) {
                    this.updateInterface();
                    if (this.selectedObject instanceof ReactionStep) {
                        ((ReactionStep) this.selectedObject).firePropertyChange("addIdentifier", true, false);
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
        MiriamResource athis = aThis;
        MiriamResource athat = aThat;

        if (athis.getDataType() != null && athat.getDataType() != null && !athis.getDataType().equals(athat.getDataType())) {
            return false;
        }
        if (!Compare.isEqual(athis.getMiriamURN(), athat.getMiriamURN())) {
            return false;
        }
        return Compare.isEqual(athis.getIdentifier(), athat.getIdentifier());
    }

    @SuppressWarnings("unchecked")
    private void initializeComboBoxURI() {
//		jComboBoxURI = getJComboBoxURI();
        if (this.selectedObject == null && this.vcMetaData == null) {
            return;
        }
        Identifiable entity = getIdentifiable(this.selectedObject);
        this.defaultComboBoxModelURI.removeAllElements();
        List<String> tooltips = new ArrayList<>();
        List<DataType> dataTypeList = new ArrayList<>();
        if (entity == null) {
            if (this.bioModel == null || this.vcMetaData == null) {
                return;        // called way too early, will be again called later
            }
            for (DataType dt : this.vcMetaData.getMiriamManager().getAllDataTypes().values()) {
                dataTypeList.add(dt);
            }
        } else {
            for (DataType dt : VCMetaDataMiriamManager.getSpecificDataTypes(entity)) {
                dataTypeList.add(dt);
            }
        }
        Collections.sort(dataTypeList);
        for (DataType dt : dataTypeList) {
            tooltips.add(dt.getDescription());
            this.defaultComboBoxModelURI.addElement(dt);
        }
        ((ComboboxToolTipRenderer) this.getJComboBoxURI().getRenderer()).setTooltips(tooltips);
    }

    private void initializeComboBoxQualifier() {
        //Set<MIRIAMQualifier> MIRIAM_all;
        //AnnotationQualifiers.MIRIAM_all;
        List<MIRIAMQualifier> qualifierList = new ArrayList<>();
        for (MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAM_all) {
            qualifierList.add(qualifier);
        }
        Collections.sort(qualifierList);
        for (MIRIAMQualifier qualifier : qualifierList) {
            this.defaultComboBoxModelQualifier.addElement(qualifier);
        }
    }

    private JButton getJButtonAddRef() {
        if (this.jButtonAddRef == null) {
            this.jButtonAddRef = new JButton();
            this.jButtonAddRef.setText(ACTION_ADD);
            this.jButtonAddRef.setToolTipText("Add a reference using a known keyword in a provider database");
        }
        return this.jButtonAddRef;
    }

    private JButton getJButtonSearchRef() {
        if (this.jButtonSearchRef == null) {
            this.jButtonSearchRef = new JButton();
            this.jButtonSearchRef.setText(ACTION_SEARCH);
            this.jButtonSearchRef.setToolTipText("Search provider database for a reference by keyword");
        }
        return this.jButtonSearchRef;
    }

    private JButton getJButtonDeleteRef() {
        if (this.jButtonDeleteRef == null) {
            this.jButtonDeleteRef = new JButton();
            this.jButtonDeleteRef.setText(ACTION_DELETE);
            this.jButtonDeleteRef.setToolTipText("Delete the selected cross-reference");

        }
        return this.jButtonDeleteRef;
    }

    private JButton getJButtonRemoveText() {
        if (this.jButtonRemoveText == null) {
            this.jButtonRemoveText = new JButton();
            this.jButtonRemoveText.setText(ACTION_REMOVE_TEXT);
            this.jButtonRemoveText.setToolTipText("Remove the entire text annotation");
        }
        return this.jButtonRemoveText;
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
