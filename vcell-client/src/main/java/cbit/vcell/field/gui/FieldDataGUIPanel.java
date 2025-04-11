/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.field.gui;

import cbit.vcell.client.*;
import cbit.vcell.client.TopLevelWindowManager.FDSimBioModelInfo;
import cbit.vcell.client.TopLevelWindowManager.FDSimMathModelInfo;
import cbit.vcell.client.TopLevelWindowManager.OpenModelInfoHolder;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.field.FieldDataFileConversion;
import cbit.vcell.field.gui.FieldDataGUIDataTransferObjects.*;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.VariableType;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.SimulationInfo;
import org.vcell.util.*;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.Version;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.exporter.FileFilters;

import javax.swing.*;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;
import java.util.Map.Entry;

public class FieldDataGUIPanel extends JPanel {

    private static final String FIELD_DATA_INFO = "Field Data Info";
    public static final String FIELD_NAME = "fieldName";
    public static final String USER_DEFINED_FDOS = "userDefinedFDOS";

    private final JPopupMenu jPopupMenu = new JPopupMenu();
    private static final int COPY_CSV = 0;
    private static final int COPY_NL = 1;
    private static final int COPY_CRNL = 2;
    private static final int COPY_SPACE = 3;


    private FieldDataWindowManager fieldDataWindowManager;
    private javax.swing.JButton fdDeleteButton = null;
    private javax.swing.JPanel ivjJPanel1 = null;
    private javax.swing.JPanel normalTopPanel = null;
    private javax.swing.JButton fdViewButton = null;
    private javax.swing.JTree ivjJTree1 = null;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private javax.swing.JScrollPane ivjJScrollPane1 = null;
    private javax.swing.JButton fdCopyReferenceButton = null;
    private JButton jButtonFindRefModel = null;
    private JPanel jPanel = null;
    private JButton createGeomButton = null;
    private JPanel jPanel1 = null;
    private JButton viewAnnotButton = null;
    private JButton fdCreateButton;

    private JComboBox<String> jComboBoxCreate;
    private static final String FROM_FILE = "from File";
    private static final String FROM_SIM = "from Simulation";


    class IvjEventHandler implements ActionListener, TreeExpansionListener, javax.swing.event.TreeSelectionListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == FieldDataGUIPanel.this.getJButtonFDDelete())
                fdDelete(e);
            if (e.getSource() == FieldDataGUIPanel.this.getJButtonFDCopyRef())
                fdCopyRef(e);
            if (e.getSource() == FieldDataGUIPanel.this.getJButtonFDView())
                viewFieldData(e);
            if (e.getSource() == FieldDataGUIPanel.this.getJButtonFDCreate())
                if (getCreateJComboBox().getSelectedItem().equals(FROM_SIM)) {
                    fdFromSim(e);
                } else if (getCreateJComboBox().getSelectedItem().equals(FROM_FILE)) {
                    fdFromFile();
                }
        }


        public void treeCollapsed(javax.swing.event.TreeExpansionEvent event) {
        }


        public void treeExpanded(javax.swing.event.TreeExpansionEvent event) {
            if (event.getSource() == FieldDataGUIPanel.this.getFieldDataTree())
                FieldDataGUIPanel.this.treeExpanded(event);
        }

        public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
            if (e.getSource() == FieldDataGUIPanel.this.getFieldDataTree())
                FieldDataGUIPanel.this.treeValueChanged(e);
        }

    }


    public FieldDataGUIPanel() {
        super();
        setName("FieldDataGUIPanel");
        setLayout(new GridBagLayout());
        setSize(676, 430);

        GridBagConstraints gbc_normalTopPanel = new GridBagConstraints();
        gbc_normalTopPanel.gridx = 0;
        gbc_normalTopPanel.gridy = 0;
        gbc_normalTopPanel.fill = GridBagConstraints.BOTH;
        gbc_normalTopPanel.insets = new Insets(4, 4, 4, 4);
        add(getNormalTopPanel(), gbc_normalTopPanel);

        GridBagConstraints constraintsJScrollPane1 = new GridBagConstraints();
        constraintsJScrollPane1.gridx = 0;
        constraintsJScrollPane1.gridy = 1;
        constraintsJScrollPane1.fill = GridBagConstraints.BOTH;
        constraintsJScrollPane1.weightx = 1.0;
        constraintsJScrollPane1.weighty = 1.0;
        constraintsJScrollPane1.insets = new Insets(4, 4, 4, 4);
        add(getMainScrollPanel(), constraintsJScrollPane1);
        getFieldDataTree().addTreeSelectionListener(ivjEventHandler);
        getFieldDataTree().addTreeExpansionListener(ivjEventHandler);
        getJButtonFDDelete().addActionListener(ivjEventHandler);
        getJButtonFDCopyRef().addActionListener(ivjEventHandler);
        getJButtonFDCreate().addActionListener(ivjEventHandler);
        getJButtonFDView().addActionListener(ivjEventHandler);
        this.fieldDataGUIPanel_Initialize();
    }


    private void copyMethod(int copyMode) {
        String delimiter = "";
        if (copyMode == COPY_NL) {
            delimiter = "\n";
        } else if (copyMode == COPY_CRNL) {
            delimiter = "\r\n";
        } else if (copyMode == COPY_CSV) {
            delimiter = ",";
        } else if (copyMode == COPY_SPACE) {
            delimiter = " ";
        }
        String copyString = "";
        javax.swing.tree.TreePath selPath = getFieldDataTree().getSelectionPath();
        if (selPath != null) {
            javax.swing.tree.DefaultMutableTreeNode lastPathComponent = (javax.swing.tree.DefaultMutableTreeNode) selPath.getLastPathComponent();
            if (lastPathComponent.equals(getFieldDataTree().getModel().getRoot())) {
                int childCount = lastPathComponent.getChildCount();
                for (int i = 0; i < childCount; i += 1) {
                    if (i != 0) {
                        copyString += delimiter;
                    }
                    copyString +=
                            ((FieldDataMainList) ((DefaultMutableTreeNode) lastPathComponent.getChildAt(i)).getUserObject()).externalDataIdentifier.getName();
                }
            } else if (lastPathComponent.getUserObject() instanceof FieldDataOriginList) {
                Origin origin = ((FieldDataOriginList) lastPathComponent.getUserObject()).origin;
                copyString = origin.getX() + delimiter + origin.getY() + delimiter + origin.getZ();
            } else if (lastPathComponent.getUserObject() instanceof FieldDataExtentList) {
                Extent extent = ((FieldDataExtentList) lastPathComponent.getUserObject()).extent;
                copyString = extent.getX() + delimiter + extent.getY() + delimiter + extent.getZ();
            } else if (lastPathComponent.getUserObject() instanceof FieldDataISizeList) {
                ISize isize = ((FieldDataISizeList) lastPathComponent.getUserObject()).isize;
                copyString = isize.getX() + delimiter + isize.getY() + delimiter + isize.getZ();
            } else if (lastPathComponent.getUserObject() instanceof FieldDataTimeList) {
                double[] times = ((FieldDataTimeList) lastPathComponent.getUserObject()).times;
                for (int i = 0; i < times.length; i += 1) {
                    if (i != 0) {
                        copyString += delimiter;
                    }
                    copyString += times[i] + "";
                }
            } else if (lastPathComponent.getUserObject() instanceof FieldDataMainList) {
                ExternalDataIdentifier extDataID =
                        ((FieldDataMainList) lastPathComponent.getUserObject()).externalDataIdentifier;
                copyString = extDataID.getName();
            } else if (lastPathComponent.getUserObject() instanceof FieldDataVarList) {
                DataIdentifier dataIdentifier =
                        ((FieldDataVarList) lastPathComponent.getUserObject()).dataIdentifier;
                copyString = dataIdentifier.getName();
            } else if (lastPathComponent.getUserObject() instanceof FieldDataVarMainList) {
                int childCount = lastPathComponent.getChildCount();
                for (int i = 0; i < childCount; i += 1) {
                    if (i != 0) {
                        copyString += delimiter;
                    }
                    copyString +=
                            ((FieldDataVarList) ((DefaultMutableTreeNode) lastPathComponent.getChildAt(i)).getUserObject()).dataIdentifier.getName();
                }
            }
            if (copyString.length() > 0) {
                VCellTransferable.sendToClipboard(copyString);
            }
        }
    }


    private void fieldDataGUIPanel_Initialize() {

        getFieldDataTree().getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);

        JMenuItem copyMenuItem = new JMenuItem("Copy");
        copyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                copyMethod(COPY_SPACE);
            }
        });
        JMenuItem copyCSVMenuItem = new JMenuItem("Copy w/ Commas");
        copyCSVMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                copyMethod(COPY_CSV);
            }
        });
        JMenuItem copyNewLineMenuItem = new JMenuItem("Copy w/ LF");
        copyNewLineMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                copyMethod(COPY_NL);
            }
        });
        JMenuItem copyReturnNewLineMenuItem = new JMenuItem("Copy w/ CRLF");
        copyReturnNewLineMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                copyMethod(COPY_CRNL);
            }
        });

    }

    public void updateJTree(final RequestManager clientRequestManager) {

        if (clientRequestManager == null) {
            DefaultMutableTreeNode emptyNode = new DefaultMutableTreeNode("No Info Available");
            getFieldDataTree().setModel(new DefaultTreeModel(emptyNode));
        } else {
            DefaultMutableTreeNode startupNode = new DefaultMutableTreeNode("Gathering Field Data Information... (Please wait)");
            getFieldDataTree().setModel(new DefaultTreeModel(startupNode));
            AsynchClientTask gatherInfo = new AsynchClientTask("gatherInfo", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

                @Override
                public void run(Hashtable<String, Object> hashTable) throws Exception {
                    try {
                        DocumentManager documentManager = clientRequestManager.getDocumentManager();
                        FieldDataDBOperationSpec fdos = FieldDataDBOperationSpec.createGetExtDataIDsSpec(documentManager.getUser());
                        FieldDataDBOperationResults fieldDataDBOperationResults = documentManager.fieldDataDBOperation(fdos);

                        ExternalDataIdentifier[] externalDataIdentifierArr = fieldDataDBOperationResults.extDataIDArr;
                        String[] extDataAnnotArr = fieldDataDBOperationResults.extDataAnnotArr;

                        TreeMap<ExternalDataIdentifier, String> sortedExtDataIDTreeMap = new TreeMap<ExternalDataIdentifier, String>(
                                new Comparator<ExternalDataIdentifier>() {
                                    public int compare(ExternalDataIdentifier o1, ExternalDataIdentifier o2) {
                                        return o1.getName().compareToIgnoreCase(o2.getName());
                                    }
                                }
                        );
                        for (int i = 0; i < externalDataIdentifierArr.length; i += 1) {
                            sortedExtDataIDTreeMap.put(externalDataIdentifierArr[i], extDataAnnotArr[i]);
                        }
                        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new InitializedRootNode(
                                FIELD_DATA_INFO + (externalDataIdentifierArr.length == 0 ? " (None Defined)" : "")));

                        Iterator<Entry<ExternalDataIdentifier, String>> sortIter = sortedExtDataIDTreeMap.entrySet().iterator();
                        while (sortIter.hasNext()) {
                            Entry<ExternalDataIdentifier, String> entry = sortIter.next();
                            DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode(new FieldDataMainList(entry.getKey(), entry.getValue()));
                            mainNode.add(new DefaultMutableTreeNode(new FieldDataVarMainList()));
                            rootNode.add(mainNode);
                        }
                        hashTable.put("rootNode", rootNode);
                    } catch (Exception e) {
                        DefaultMutableTreeNode errorNode = new DefaultMutableTreeNode("Error Getting Field Data Information");
                        hashTable.put("rootNode", errorNode);
                        throw e;
                    }
                }
            };
            AsynchClientTask updateTree = new AsynchClientTask("updateTree", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

                @Override
                public void run(Hashtable<String, Object> hashTable) throws Exception {
                    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) hashTable.get("rootNode");
                    getFieldDataTree().setModel(new DefaultTreeModel(rootNode));
                }
            };
            ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[]{gatherInfo, updateTree});
        }
    }


    /**
     * Return the JButtonFDCopyRef property value.
     *
     * @return javax.swing.JButton
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getJButtonFDCopyRef() {
        if (fdCopyReferenceButton == null) {
            fdCopyReferenceButton = new javax.swing.JButton();
            fdCopyReferenceButton.setName("JButtonFDCopyRef");
            fdCopyReferenceButton.setText("Copy Func");
            fdCopyReferenceButton.setEnabled(false);
        }
        return fdCopyReferenceButton;
    }


    private javax.swing.JButton getJButtonFDDelete() {
        if (fdDeleteButton == null) {
            fdDeleteButton = new javax.swing.JButton();
            fdDeleteButton.setName("JButtonFDDelete");
            fdDeleteButton.setText("Delete");
            fdDeleteButton.setEnabled(false);
        }
        return fdDeleteButton;
    }


    private JButton getJButtonFDCreate() {
        if (fdCreateButton == null) {
            fdCreateButton = new javax.swing.JButton();
            fdCreateButton.setName("JButtonFDFromCreate");
            fdCreateButton.setText("Create...");
        }
        return fdCreateButton;
    }



    //private static final String FROM_IMAGEJ = "from ImageJ";
    private JComboBox<String> getCreateJComboBox() {
        if (jComboBoxCreate == null) {
            jComboBoxCreate = new JComboBox(new Object[]{FROM_FILE, FROM_SIM/*,FROM_IMAGEJ*/});
        }
        return jComboBoxCreate;
    }

    private javax.swing.JButton getJButtonFDView() {
        if (fdViewButton == null) {
            fdViewButton = new javax.swing.JButton();
            fdViewButton.setName("JButtonFDView");
            fdViewButton.setText("View...");
            fdViewButton.setEnabled(false);
        }
        return fdViewButton;
    }


    private javax.swing.JPanel getJPanel1() {
        if (ivjJPanel1 == null) {
            org.vcell.util.gui.TitledBorderBean ivjLocalBorder;
            ivjLocalBorder = new org.vcell.util.gui.TitledBorderBean();
            ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
            ivjLocalBorder.setTitle("Create New Field Data");
            ivjJPanel1 = new javax.swing.JPanel();
            ivjJPanel1.setName("JPanel1");
            ivjJPanel1.setBorder(ivjLocalBorder);
            final java.awt.GridBagLayout gridBagLayout = new java.awt.GridBagLayout();
            gridBagLayout.rowHeights = new int[]{0, 0, 7};
            ivjJPanel1.setLayout(gridBagLayout);

            java.awt.GridBagConstraints constraintsJButtonFDFromFile = new java.awt.GridBagConstraints();
            constraintsJButtonFDFromFile.gridx = 1;
            constraintsJButtonFDFromFile.gridy = 1;
            constraintsJButtonFDFromFile.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJButtonFDFromFile.weightx = 1.0;
            constraintsJButtonFDFromFile.insets = new java.awt.Insets(4, 4, 4, 4);
            getJPanel1().add(getJButtonFDCreate(), constraintsJButtonFDFromFile);

            java.awt.GridBagConstraints constraintsJButtonFDFromSim = new java.awt.GridBagConstraints();
            constraintsJButtonFDFromSim.gridx = 2;
            constraintsJButtonFDFromSim.gridy = 1;
            constraintsJButtonFDFromSim.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJButtonFDFromSim.weightx = 1.0;
            constraintsJButtonFDFromSim.insets = new java.awt.Insets(4, 4, 4, 4);
            getJPanel1().add(getCreateJComboBox(), constraintsJButtonFDFromSim);
        }
        return ivjJPanel1;
    }


    private javax.swing.JPanel getNormalTopPanel() {
        if (normalTopPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 2;
            gridBagConstraints4.gridy = 1;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 2;
            gridBagConstraints21.gridy = 0;
            normalTopPanel = new javax.swing.JPanel();
            normalTopPanel.setName("normalTopPanel");
            normalTopPanel.setLayout(new java.awt.GridBagLayout());

            java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
            constraintsJPanel1.gridx = 0;
            constraintsJPanel1.gridy = 0;
            constraintsJPanel1.gridheight = 2;
            constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
            getNormalTopPanel().add(getJPanel1(), constraintsJPanel1);

            java.awt.GridBagConstraints constraintsJButtonFDDelete = new java.awt.GridBagConstraints();
            constraintsJButtonFDDelete.gridx = 1;
            constraintsJButtonFDDelete.gridy = 0;
            constraintsJButtonFDDelete.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJButtonFDDelete.weightx = 0.0;
            constraintsJButtonFDDelete.insets = new java.awt.Insets(4, 4, 4, 4);
            java.awt.GridBagConstraints constraintsJButtonFDView = new java.awt.GridBagConstraints();
            constraintsJButtonFDView.gridx = 1;
            constraintsJButtonFDView.gridy = 1;
            constraintsJButtonFDView.gridwidth = 1;
            constraintsJButtonFDView.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJButtonFDView.insets = new java.awt.Insets(4, 4, 4, 4);
            normalTopPanel.add(getJButtonFDDelete(), constraintsJButtonFDDelete);
            normalTopPanel.add(getJButtonFDView(), constraintsJButtonFDView);
            normalTopPanel.add(getJPanel(), gridBagConstraints21);
            normalTopPanel.add(getJPanel12(), gridBagConstraints4);
        }
        return normalTopPanel;
    }

    public static final int DISPLAY_NORMAL = 0;
    public static final int DISPLAY_DATASYMBOLS = 1;
    private int displayMode = DISPLAY_NORMAL;
    private JPanel dataSymbolsJPanel = null;
    private final JButton dsAnnotButton = new JButton();
    private final JButton dsViewButton = new JButton();
    private final JButton dsDataSymbolButton = new JButton();

    public int getDisplayMode() {
        return displayMode;
    }

    private void setDisplayMode(int newDisplayMode) {
        displayMode = newDisplayMode;
        if (newDisplayMode == DISPLAY_DATASYMBOLS) {
            for (int i = 0; i < getComponentCount(); i++) {
                if (getComponent(i) == getNormalTopPanel()) {
                    remove(getComponent(i));
                    break;
                } else if (getComponent(i) == dataSymbolsJPanel) {
                    return;
                }
            }
            if (dataSymbolsJPanel == null) {
                dataSymbolsJPanel = new JPanel();
                dataSymbolsJPanel.setName("dataSymbolsPanel");
                //View Button
                //JButton viewButton = new JButton();
                dsViewButton.setText("View...");
                dsViewButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        viewFieldData(e);
                    }
                });
                dsViewButton.setEnabled(false);
                dataSymbolsJPanel.add(dsViewButton);
                //view Annotation
                //JButton annotButton = new JButton();
                dsAnnotButton.setText("View Annot...");
                dsAnnotButton.addActionListener(viewAnnotAction);
                dsAnnotButton.setEnabled(false);
                dataSymbolsJPanel.add(dsAnnotButton);
                //datasymbol Callback button
                //JButton dataSymbolButton = new JButton();
                dsDataSymbolButton.setText("Create Data Symbol...");
                dsDataSymbolButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        fdCopyRef(e);
                    }
                });
                dsDataSymbolButton.setEnabled(false);
                dataSymbolsJPanel.add(dsDataSymbolButton);
            }
            java.awt.GridBagConstraints gbc_datasymbolsPanel = new java.awt.GridBagConstraints();
            gbc_datasymbolsPanel.gridx = 0;
            gbc_datasymbolsPanel.gridy = 0;
            gbc_datasymbolsPanel.fill = java.awt.GridBagConstraints.BOTH;
            gbc_datasymbolsPanel.insets = new java.awt.Insets(4, 4, 4, 4);
            add(dataSymbolsJPanel, gbc_datasymbolsPanel);

        } else {
            for (int i = 0; i < getComponentCount(); i++) {
                if (getComponent(i) == dataSymbolsJPanel) {
                    remove(getComponent(i));
                    break;
                } else if (getComponent(i) == getNormalTopPanel()) {
                    return;
                }
            }
            java.awt.GridBagConstraints gbc_normalTopPanel = new java.awt.GridBagConstraints();
            gbc_normalTopPanel.gridx = 0;
            gbc_normalTopPanel.gridy = 0;
            gbc_normalTopPanel.fill = java.awt.GridBagConstraints.BOTH;
            gbc_normalTopPanel.insets = new java.awt.Insets(4, 4, 4, 4);
            add(getNormalTopPanel(), gbc_normalTopPanel);
        }
    }

    private FieldDataWindowManager.DataSymbolCallBack dataSymbolCallBack = null;

    public void setCreateDataSymbolCallBack(FieldDataWindowManager.DataSymbolCallBack dataSymbolCallBack) {
        this.dataSymbolCallBack = dataSymbolCallBack;
        setDisplayMode((dataSymbolCallBack == null ? FieldDataGUIPanel.DISPLAY_NORMAL : FieldDataGUIPanel.DISPLAY_DATASYMBOLS));
    }


    private javax.swing.JScrollPane getMainScrollPanel() {
        if (ivjJScrollPane1 == null) {
            ivjJScrollPane1 = new javax.swing.JScrollPane();
            ivjJScrollPane1.setName("JScrollPane1");
            getMainScrollPanel().setViewportView(getFieldDataTree());
        }
        return ivjJScrollPane1;
    }

    private javax.swing.JTree getFieldDataTree() {
        if (ivjJTree1 == null) {
            try {
                ivjJTree1 = new javax.swing.JTree();
                ivjJTree1.setName("JTree1");
                ivjJTree1.setBounds(0, 0, 516, 346);
            } catch (java.lang.Throwable ivjExc) {
                ivjJTree1.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        maybeShowPopup(e);
                    }

                    public void mouseReleased(MouseEvent e) {
                        maybeShowPopup(e);
                    }

                    private void maybeShowPopup(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            jPopupMenu.show(e.getComponent(),
                                    e.getX(), e.getY());
                        }
                    }
                });
            }
        }
        return ivjJTree1;
    }


    public boolean isInitialized() {
        DefaultMutableTreeNode rootNode =
                (DefaultMutableTreeNode) getFieldDataTree().getModel().getRoot();
        if (rootNode == null) {
            return false;
        }
        return (rootNode.getUserObject() instanceof InitializedRootNode);
    }


    private void treeValueChanged(javax.swing.event.TreeSelectionEvent treeSelectionEvent) {
        getJButtonFDDelete().setEnabled(false);
        getJButtonFDView().setEnabled(false);
        dsViewButton.setEnabled(false);
        getJButtonFDCopyRef().setEnabled(false);
        dsDataSymbolButton.setEnabled(false);
        getJButtonFindRefModel().setEnabled(false);
        getJButtonViewAnnot().setEnabled(false);
        dsAnnotButton.setEnabled(false);
        getJButtonCreateGeom().setEnabled(false);
        javax.swing.tree.TreePath selPath = getFieldDataTree().getSelectionPath();
        if (selPath != null) {
            javax.swing.tree.DefaultMutableTreeNode lastPathComponent = (javax.swing.tree.DefaultMutableTreeNode) selPath.getLastPathComponent();
//		System.out.println("count="+selPath.getPathCount()+"  "+(lastPathComponent != null?lastPathComponent.toString():"null"));
            if (lastPathComponent.getUserObject() instanceof FieldDataMainList) {
                getJButtonFDDelete().setEnabled(true);
                getJButtonFDView().setEnabled(fieldDataWindowManager != null);
                dsViewButton.setEnabled(fieldDataWindowManager != null);
                getJButtonFindRefModel().setEnabled(true);
                getJButtonViewAnnot().setEnabled(true);
                dsAnnotButton.setEnabled(true);
            } else if (lastPathComponent.getUserObject() instanceof FieldDataVarList) {
                getJButtonFDCopyRef().setEnabled(true);
                dsDataSymbolButton.setEnabled(true);
                getJButtonCreateGeom().setEnabled(true);
            }
        }
    }

    private void fdFromSim(java.awt.event.ActionEvent actionEvent) {
        try {
            final RequestManager clientRequestManager = fieldDataWindowManager.getLocalRequestManager();

            final FieldDataWindowManager.OpenModelInfoHolder simInfoHolder = FieldDataWindowManager.selectOpenModelsFromDesktop(this, fieldDataWindowManager.getRequestManager(), true, "Select Simulation for Field Data", true);
            if (simInfoHolder == null) {
                PopupGenerator.showErrorDialog(this, "Please open a Bio or Math model containing the spatial (non-compartmental) simulation you wish to use to create a new Field Data");
                return;
            }

            String newFieldDataName = PopupGenerator.showInputDialog(this, "Field Data Name:", "");

            //Check that the sim is in a state that can be copied
            final SimulationInfo simInfo = simInfoHolder.getSimInfo();
            if (simInfo == null) {
                throw new Exception("Selected sim '" + simInfoHolder.getSimName() + "' has no simInfo (save your model and retry).");
            }
            SimulationStatus simStatus = clientRequestManager.getServerSimulationStatus(simInfo);
            if (simStatus != null && (simStatus.isRunning() || simStatus.isStartRequested())) {
                throw new Exception("Can't copy 'running' simulation data from sim '" + simInfo.getName() + "'");
            }


            AsynchClientTask[] taskArray = new AsynchClientTask[1];

            taskArray[0] = new AsynchClientTask("Saving Field Data From Simulation", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
                public void run(Hashtable<String, Object> hashTable) throws Exception {
                    KeyValue simReference = simInfo.getParentSimulationReference() != null ? simInfo.getParentSimulationReference() : simInfo.getSimulationVersion().getVersionKey();
                    clientRequestManager.getDocumentManager().fieldDataFromSimulation(simReference, simInfoHolder.getJobIndex(), newFieldDataName);
                    updateJTree(clientRequestManager);
                }
            };

            Hashtable<String, Object> hash = new Hashtable<String, Object>();
            ClientTaskDispatcher.dispatch(this, hash, taskArray, false);

        } catch (UserCancelException e) {
            return;
        } catch (Exception e) {
            PopupGenerator.showErrorDialog(this, "Error creating Field Data from simulation\n" + e.getMessage(), e);
        }
    }

    private void viewFieldData(java.awt.event.ActionEvent actionEvent) {
        if (fieldDataWindowManager == null) {
            System.out.println("No FieldDataViewManager available for View action");
            return;
        }
        javax.swing.tree.TreePath selPath = getFieldDataTree().getSelectionPath();
        if (selPath != null) {
            javax.swing.tree.DefaultMutableTreeNode lastPathComponent = (javax.swing.tree.DefaultMutableTreeNode) selPath.getLastPathComponent();
            if (lastPathComponent.getUserObject() instanceof FieldDataMainList) {
                ExternalDataIdentifier edi = ((FieldDataMainList) lastPathComponent.getUserObject()).externalDataIdentifier;
                fieldDataWindowManager.viewData(edi);
            }
        }
    }

    String IMAGE_FILE_KEY = "imageFile";

    // Add New Data is called from within this function.
    public void fdFromFile() {
        final RequestManager clientRequestManager = fieldDataWindowManager.getLocalRequestManager();
        AsynchClientTask[] addTasks = addNewExternalData(this, this, false);
        AsynchClientTask[] taskArray = new AsynchClientTask[2 + addTasks.length];
        System.arraycopy(addTasks, 0, taskArray, 2, addTasks.length); // add to the end

        taskArray[0] = new AsynchClientTask("select a file", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
            public void run(Hashtable<String, Object> hashTable) throws Exception {
                FieldDataFileOperationSpec argfdos = (FieldDataFileOperationSpec) hashTable.get("argfdos");
                if (argfdos == null && hashTable.get(IMAGE_FILE_KEY) == null) {
                    File imageFile = DatabaseWindowManager.showFileChooserDialog(
                            fieldDataWindowManager.getComponent(), FileFilters.FILE_FILTER_FIELDIMAGES,
                            clientRequestManager.getUserPreferences(), JFileChooser.FILES_AND_DIRECTORIES);
                    hashTable.put(IMAGE_FILE_KEY, imageFile);
                }
            }
        };
        taskArray[1] = new AsynchClientTask("Generate field data from file", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
            public void run(Hashtable<String, Object> hashTable) throws Exception {
                FieldDataFileOperationSpec generatedFieldData ;
                File imageFile = (File) hashTable.get(IMAGE_FILE_KEY);
                if (imageFile == null || imageFile.getName().contains(".vfrap")){
                    return;
                } else {
                    generatedFieldData = FieldDataFileConversion.analyzeMetaData(imageFile);
                }

                generatedFieldData.owner = clientRequestManager.getDocumentManager().getUser();
                generatedFieldData.opType = FieldDataFileOperationSpec.FDOS_ADD;
                hashTable.put("fdos", generatedFieldData);
                hashTable.put("file", imageFile);
                hashTable.put("initFDName", imageFile.getName());
            }
        };
        Hashtable<String, Object> hash = new Hashtable<>();
        ClientTaskDispatcher.dispatch(this, hash, taskArray, false, true, null);
    }

    private void fdDelete(java.awt.event.ActionEvent actionEvent) {

        final RequestManager clientRequestManager = fieldDataWindowManager.getLocalRequestManager();


        TreePath selPath = getFieldDataTree().getSelectionPath();
        final DefaultMutableTreeNode mainNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        final FieldDataMainList fieldDataMainList = (FieldDataMainList) mainNode.getUserObject();

        if (!fieldDataMainList.externalDataIdentifier.getOwner().equals(
                clientRequestManager.getDocumentManager().getUser())) {
            DialogUtils.showErrorDialog(this, "Delete failed: User " + clientRequestManager.getDocumentManager().getUser().getName() +
                    "does not own FieldData '" + fieldDataMainList.externalDataIdentifier.getName() + "'");
        }
        if (PopupGenerator.showComponentOKCancelDialog(
                this, new JLabel("Delete " + fieldDataMainList.externalDataIdentifier.getName() + "?"),
                "Confirm Delete") != JOptionPane.OK_OPTION) {
            return;

        }
        AsynchClientTask CheckRemoveFromDBTask = new AsynchClientTask("Check Field Data references in DB", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
            public void run(Hashtable<String, Object> hash) throws Exception {
                if (fieldDataWindowManager.findReferencingModels(fieldDataMainList.externalDataIdentifier, false)) {
                    throw new Exception("Cannot delete Field Data '" + fieldDataMainList.externalDataIdentifier.getName() +
                            "' because it is referenced in a Model(s) or Function(s) file.");
                }
            }
        };
        AsynchClientTask RemoveNodeTreeTask = new AsynchClientTask("Remove FieldData tree node", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
            public void run(Hashtable<String, Object> hash) throws Exception {
                ((DefaultTreeModel) getFieldDataTree().getModel()).removeNodeFromParent(mainNode);
                if (((DefaultMutableTreeNode) getFieldDataTree().getModel().getRoot()).getChildCount() == 0) {
                    updateJTree(clientRequestManager);
                }
            }
        };
        AsynchClientTask RemoveFromDiskAndDBTask = new AsynchClientTask("Remove Field Data from Disk and DB", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
            public void run(Hashtable<String, Object> hash) throws Exception {
                //Remove from Disk
                FieldDataMainList fieldDataMainList = (FieldDataMainList) mainNode.getUserObject();
                FieldDataFileOperationSpec fdos = FieldDataFileOperationSpec.createDeleteFieldDataFileOperationSpec(
                        fieldDataMainList.externalDataIdentifier);
                clientRequestManager.getDocumentManager().fieldDataFileOperation(fdos);
            }
        };
        //
        //Execute Field Data Info - JTree tasks
        //
        AsynchClientTask tasks[] = new AsynchClientTask[]{CheckRemoveFromDBTask, RemoveFromDiskAndDBTask, RemoveNodeTreeTask};
        Hashtable<String, Object> hash = new Hashtable<String, Object>();
        ClientTaskDispatcher.dispatch(this, hash, tasks, false);

    }

    private class SelectedTimes {
        private double[] times;
        private int selectedIndex;

        public SelectedTimes(double[] times, int selectedIndex) {
            super();
            this.times = times;
            this.selectedIndex = selectedIndex;
        }

        public double[] getTimes() {
            return times;
        }

        public int getSelectedIndex() {
            return selectedIndex;
        }

    }

    private SelectedTimes selectTimeFromNode(DefaultMutableTreeNode mainNode) {
        Enumeration<?> children = mainNode.children();
        double[] times = null;
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
            if (child.getUserObject() instanceof FieldDataTimeList) {
                times = ((FieldDataTimeList) child.getUserObject()).times;
                break;
            }
        }

        if (times != null && times.length > 1) {
            String[] timesStr = new String[times.length];
            for (int i = 0; i < times.length; i += 1) {
                timesStr[i] = times[i] + "";
            }
            JPanel jp = new JPanel();
            BoxLayout bl = new BoxLayout(jp, BoxLayout.X_AXIS);
            jp.setLayout(bl);
            JComboBox<String> jcBeg = new JComboBox(Arrays.asList(timesStr).toArray(new Object[0]));
            jp.add(jcBeg);

            if (PopupGenerator.showComponentOKCancelDialog(this, jp, "Select Field Data timepoint") == JOptionPane.OK_OPTION) {
                return new SelectedTimes(times, jcBeg.getSelectedIndex());
            }
            throw UserCancelException.CANCEL_GENERIC;
        }
        return new SelectedTimes(new double[]{0.0}, 0);
    }

    private void fdCopyRef(java.awt.event.ActionEvent actionEvent) {
        if (actionEvent.getSource() == getJButtonCreateGeom()) {
            DocumentWindow.showGeometryCreationWarning(FieldDataGUIPanel.this);
            return;
        }
        TreePath selPath = getFieldDataTree().getSelectionPath();
        final DefaultMutableTreeNode varNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        final DefaultMutableTreeNode mainNode = (DefaultMutableTreeNode) varNode.getParent().getParent();

        SelectedTimes selectedTimes = null;
        try {
            selectedTimes = selectTimeFromNode(mainNode);
        } catch (UserCancelException e) {
            return;
        }
        double selectedTime = selectedTimes.getTimes()[selectedTimes.getSelectedIndex()];

        if (actionEvent.getSource() == getJButtonFDCopyRef()) {
            String fieldFunctionReference =
                    SimulationData.createCanonicalFieldFunctionSyntax(
                            ((FieldDataMainList) mainNode.getUserObject()).externalDataIdentifier.getName(),
                            ((FieldDataVarList) varNode.getUserObject()).dataIdentifier.getName(),
                            selectedTime, ((FieldDataVarList) varNode.getUserObject()).dataIdentifier.getVariableType().getTypeName());

            VCellTransferable.sendToClipboard(fieldFunctionReference);
        } else if (actionEvent.getSource() == dsDataSymbolButton && dataSymbolCallBack != null) {
            dataSymbolCallBack.createDataSymbol(
                    ((FieldDataMainList) mainNode.getUserObject()).externalDataIdentifier,
                    ((FieldDataVarList) varNode.getUserObject()).dataIdentifier.getName(),
                    ((FieldDataVarList) varNode.getUserObject()).dataIdentifier.getVariableType(),
                    selectedTime);
        }

    }

    private void treeExpanded(final javax.swing.event.TreeExpansionEvent treeExpansionEvent) {
        if (fieldDataWindowManager == null) {
            return;
        }
        //
        //Determine if we need to get Info
        //
        javax.swing.tree.TreePath expPath = null;
        try {
            expPath = treeExpansionEvent.getPath();
            if (expPath != null) {
                DefaultMutableTreeNode mainNode = (javax.swing.tree.DefaultMutableTreeNode) expPath.getLastPathComponent();
//			if(mainNode.equals(getJTree1().getModel().getRoot())){
//				System.out.println("Root Node expanded");
//			}
                if (mainNode.getUserObject() instanceof FieldDataMainList) {
                    if (mainNode.getChildCount() > 1) {//Already populated
                        return;
                    }
                    refreshMainNode(mainNode);
                } else {
                    return;
                }
            } else {
                return;
            }
        } catch (Exception e) {
            PopupGenerator.showErrorDialog(this, "Error getting Field Data Info\n" + e.getMessage(), e);
            return;
        }

    }

    public void refreshExternalDataIdentifierNode(ExternalDataIdentifier refreshEDI) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getFieldDataTree().getModel().getRoot();
        if (root != null) {
            int childCount = root.getChildCount();
            for (int i = 0; i < childCount; i += 1) {
                DefaultMutableTreeNode mainNode = (DefaultMutableTreeNode) root.getChildAt(i);
                FieldDataMainList fieldDataMainList = (FieldDataMainList) mainNode.getUserObject();
                if (fieldDataMainList.externalDataIdentifier.equals(refreshEDI)) {
                    refreshMainNode(mainNode);
                    return;
                }
            }
        }
    }

    private void refreshMainNode(final DefaultMutableTreeNode mainNode) {
        final boolean isMainExpanded = getFieldDataTree().isExpanded(new TreePath(mainNode.getPath()));
        final boolean isVarExpanded = getFieldDataTree().isExpanded(
                new TreePath(((DefaultMutableTreeNode) mainNode.getLastChild()).getPath()));
        //Remove all children from Main node in a Tree safe way
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getFieldDataTree().getModel().getRoot();
        int mainNodeIndex =
                ((DefaultTreeModel) getFieldDataTree().getModel()).getIndexOfChild(root, mainNode);
        ((DefaultTreeModel) getFieldDataTree().getModel()).removeNodeFromParent(mainNode);
        mainNode.removeAllChildren();
        final DefaultMutableTreeNode varNode = new DefaultMutableTreeNode(new FieldDataVarMainList());
        mainNode.add(varNode);
        ((DefaultTreeModel) getFieldDataTree().getModel()).insertNodeInto(mainNode, root, mainNodeIndex);
        //
        //Create thread-safe tasks to get Field Data Info and update JTree
        //
        final String FDOR_INFO = "FDOR_INFO";
        final RequestManager clientRequestManager = fieldDataWindowManager.getLocalRequestManager();

        AsynchClientTask FieldDataInfoTask = new AsynchClientTask("Gather Field Data info", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
            public void run(Hashtable<String, Object> hash) throws Exception {
                FieldDataMainList fieldDataMainList = (FieldDataMainList) mainNode.getUserObject();
                final FieldDataFileOperationResults fieldDataFileOperationResults =
                        clientRequestManager.getDocumentManager().
                                fieldDataFileOperation(
                                        FieldDataFileOperationSpec.createInfoFieldDataFileOperationSpec(
                                                fieldDataMainList.externalDataIdentifier.getKey(),
                                                clientRequestManager.getDocumentManager().getUser(),
                                                FieldDataFileOperationSpec.JOBINDEX_DEFAULT)
                                );
                hash.put(FDOR_INFO, fieldDataFileOperationResults);
            }
        };

        AsynchClientTask FieldDataInfoTreeUpdate = new AsynchClientTask("Update Field Data GUI", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
            public void run(Hashtable<String, Object> hash) {
                try {
                    FieldDataFileOperationResults fieldDataFileOperationResults =
                            (FieldDataFileOperationResults) hash.get(FDOR_INFO);
                    Arrays.sort(fieldDataFileOperationResults.dataIdentifierArr,
                            new Comparator<DataIdentifier>() {
                                public int compare(DataIdentifier o1, DataIdentifier o2) {
                                    return o1.getName().compareToIgnoreCase(o2.getName());
                                }
                            }
                    );
                    FieldDataMainList fieldDataMainList = (FieldDataMainList) mainNode.getUserObject();
                    final DefaultMutableTreeNode isizeNode =
                            new DefaultMutableTreeNode(new FieldDataISizeList(fieldDataFileOperationResults.iSize));
                    final DefaultMutableTreeNode originNode =
                            new DefaultMutableTreeNode(new FieldDataOriginList(fieldDataFileOperationResults.origin));
                    final DefaultMutableTreeNode extentNode =
                            new DefaultMutableTreeNode(new FieldDataExtentList(fieldDataFileOperationResults.extent));
                    final DefaultMutableTreeNode timeNode =
                            new DefaultMutableTreeNode(new FieldDataTimeList(fieldDataFileOperationResults.times));
                    final DefaultMutableTreeNode idNode =
                            new DefaultMutableTreeNode(new FieldDataIDList(fieldDataMainList.externalDataIdentifier.getKey()));
                    ((DefaultTreeModel) getFieldDataTree().getModel()).insertNodeInto(isizeNode, mainNode, 0);
                    ((DefaultTreeModel) getFieldDataTree().getModel()).insertNodeInto(originNode, mainNode, 1);
                    ((DefaultTreeModel) getFieldDataTree().getModel()).insertNodeInto(extentNode, mainNode, 2);
                    ((DefaultTreeModel) getFieldDataTree().getModel()).insertNodeInto(timeNode, mainNode, 3);
                    ((DefaultTreeModel) getFieldDataTree().getModel()).insertNodeInto(idNode, mainNode, 4);
                    for (int i = 0; i < fieldDataFileOperationResults.dataIdentifierArr.length; i += 1) {
                        ((DefaultTreeModel) getFieldDataTree().getModel()).insertNodeInto(
                                new DefaultMutableTreeNode(
                                        new FieldDataVarList(fieldDataFileOperationResults.dataIdentifierArr[i])),
                                varNode, varNode.getChildCount());
                    }
                    if (isMainExpanded) {
                        getFieldDataTree().expandPath(new TreePath(mainNode.getPath()));
                    }
                    if (isVarExpanded) {
                        getFieldDataTree().expandPath(new TreePath(varNode.getPath()));
                    }
                } catch (Throwable e) {
                    hash.put(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR, e);
                }
            }
        };

        //
        //Execute Field Data Info - JTree tasks
        //
        AsynchClientTask tasks[] = new AsynchClientTask[]{FieldDataInfoTask, FieldDataInfoTreeUpdate};
        Hashtable<String, Object> hash = new Hashtable<String, Object>();
        ClientTaskDispatcher.dispatch(this, hash, tasks, false);
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            javax.swing.JFrame frame = new javax.swing.JFrame();
            FieldDataGUIPanel aFieldDataGUIPanel;
            aFieldDataGUIPanel = new FieldDataGUIPanel();
            frame.setContentPane(aFieldDataGUIPanel);
            frame.setSize(aFieldDataGUIPanel.getSize());
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }

                ;
            });
            frame.setVisible(true);
            java.awt.Insets insets = frame.getInsets();
            frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JPanel");
            exception.printStackTrace(System.out);
        }
    }

    public void setFieldDataWindowManager(FieldDataWindowManager fdwm) {
        fieldDataWindowManager = fdwm;
    }

    public void checkFieldDataName(String fieldDataName) throws Exception {
        if (fieldDataName == null || fieldDataName.length() == 0 ||
                !fieldDataName.equals(TokenMangler.fixTokenStrict(fieldDataName))) {
            throw new Exception("Field Data names can contain only letters,digits and underscores");
        }
        //Check to see if this name is already used
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) this.getFieldDataTree().getModel().getRoot();
        if (!rootNode.getUserObject().toString().startsWith(FIELD_DATA_INFO)) {
            updateJTree(fieldDataWindowManager.getLocalRequestManager());
            rootNode = (DefaultMutableTreeNode) this.getFieldDataTree().getModel().getRoot();
        }
        for (int i = 0; i < rootNode.getChildCount(); i += 1) {
            ExternalDataIdentifier extDataID =
                    ((FieldDataMainList) ((DefaultMutableTreeNode) rootNode.getChildAt(i)).getUserObject()).externalDataIdentifier;
            if (fieldDataName.equals(extDataID.getName())) {
                throw new Exception("New Field Data name " + fieldDataName + " already used.");
            }
        }
    }
    
    // creation and saving of file is separate act from saving file info into DB

    private static AsynchClientTask[] addNewExternalData(final Component requester, final FieldDataGUIPanel fieldDataGUIPanel, final boolean isFromSimulation) {

        final RequestManager clientRequestManager = fieldDataGUIPanel.fieldDataWindowManager.getLocalRequestManager();

        AsynchClientTask task1 = new AsynchClientTask("review field data", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

            @Override
            public void run(Hashtable<String, Object> hashTable) throws Exception {
                //Allow user to review/change info about fielddata
                FieldDataFileOperationSpec generatedFieldDataOpSpec = (FieldDataFileOperationSpec) hashTable.get("fdos");
                String initialExtDataName = (String) hashTable.get("initFDName");

                generatedFieldDataOpSpec.specEDI = null;

                FieldDataInfoPanel fieldDataInfoPanel = new FieldDataInfoPanel();
                fieldDataInfoPanel.setSimulationMode(isFromSimulation);
                fieldDataInfoPanel.initISize(generatedFieldDataOpSpec.isize);
                fieldDataInfoPanel.initIOrigin(generatedFieldDataOpSpec.origin);
                fieldDataInfoPanel.initIExtent(generatedFieldDataOpSpec.extent);
                fieldDataInfoPanel.initTimes(generatedFieldDataOpSpec.times);
                fieldDataInfoPanel.initNames(TokenMangler.fixTokenStrict(initialExtDataName), generatedFieldDataOpSpec.varNames);
                fieldDataInfoPanel.setAnnotation(generatedFieldDataOpSpec.annotation);

                FieldDataFileOperationSpec userDefinedFDOS = new FieldDataFileOperationSpec();
                while (true) {
                    int choice = PopupGenerator.showComponentOKCancelDialog(requester, fieldDataInfoPanel, "Create new field data");
                    if (choice == JOptionPane.OK_OPTION) {
                        //Check values
                        try {
                            userDefinedFDOS.extent = fieldDataInfoPanel.getExtent();
                        } catch (Exception e) {
                            PopupGenerator.showErrorDialog(requester, "Problem with Extent values. Please re-enter.\n" + e.getMessage() + "\nTry Again.", e);
                            continue;
                        }
                        try {
                            userDefinedFDOS.origin = fieldDataInfoPanel.getOrigin();
                        } catch (Exception e) {
                            PopupGenerator.showErrorDialog(requester, "Problem with Origin values. Please re-enter.\n" + e.getMessage() + "\nTry Again.", e);
                            continue;
                        }
                        try {
                            userDefinedFDOS.varNames = fieldDataInfoPanel.getVariableNames();
                        } catch (Exception e) {
                            PopupGenerator.showErrorDialog(requester, "Problem with Variable names. Please re-enter.\n" + e.getMessage() + "\nTry Again.", e);
                            continue;
                        }
                        userDefinedFDOS.annotation = fieldDataInfoPanel.getAnnotation();
                        userDefinedFDOS.times = fieldDataInfoPanel.getTimes();
                        try {
                            fieldDataGUIPanel.checkFieldDataName(fieldDataInfoPanel.getFieldName());
                        } catch (Exception e) {
                            PopupGenerator.showErrorDialog(requester, "Error saving Field Data Name to Database. Try again.\n" + e.getMessage(), e);
                            continue;
                        }
                        if (!isFromSimulation){
                            generatedFieldDataOpSpec.extent = userDefinedFDOS.extent;
                            generatedFieldDataOpSpec.origin = userDefinedFDOS.origin;
                            generatedFieldDataOpSpec.varNames = userDefinedFDOS.varNames;
                            generatedFieldDataOpSpec.times = userDefinedFDOS.times;
                        }
                        generatedFieldDataOpSpec.annotation = userDefinedFDOS.annotation;
                        hashTable.put(USER_DEFINED_FDOS, userDefinedFDOS);
                        hashTable.put(FIELD_NAME, fieldDataInfoPanel.getFieldName());
                        break;
                    } else {
                        throw UserCancelException.CANCEL_GENERIC;
                    }
                }
            }
        };

        AsynchClientTask task2 = new AsynchClientTask("saving field data", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
            @Override
            public void run(Hashtable<String, Object> hashTable) throws Exception {
                //Add to Server Disk
                //save Database
                String fieldName = (String) hashTable.get(FIELD_NAME);
                FieldDataFileOperationSpec generatedFieldDataOpSpec
                        = (FieldDataFileOperationSpec) hashTable.get("fdos");
                DocumentManager documentManager = clientRequestManager.getDocumentManager();
                File file = (File) hashTable.get("file");
                generatedFieldDataOpSpec.fieldDataName = fieldName;
                try {
                    //Add to Server Disk
                    FieldDataFileOperationResults results = documentManager.analyzeAndSaveFieldFromFile(file, generatedFieldDataOpSpec.fieldDataName, generatedFieldDataOpSpec.extent,
                            generatedFieldDataOpSpec.isize, generatedFieldDataOpSpec.varNames,
                            generatedFieldDataOpSpec.times, generatedFieldDataOpSpec.annotation, generatedFieldDataOpSpec.origin);
                    generatedFieldDataOpSpec.specEDI = results.externalDataIdentifier;
                } catch (Exception e) {
                    generatedFieldDataOpSpec.specEDI = null;
                    throw e;
                }
            }
        };

        AsynchClientTask task3 = new AsynchClientTask("refreshing field data", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
            @Override
            public void run(Hashtable<String, Object> hashTable) throws Exception {
                FieldDataFileOperationSpec fdos = (FieldDataFileOperationSpec) hashTable.get("fdos");

                DefaultMutableTreeNode root = ((DefaultMutableTreeNode) fieldDataGUIPanel.getFieldDataTree().getModel().getRoot());
                if (root.getChildCount() == 0) {
                    fieldDataGUIPanel.updateJTree(clientRequestManager);
                } else {
                    int alphabeticalIndex = -1;
                    for (int i = 0; i < root.getChildCount(); i += 1) {
                        if ((((FieldDataMainList) ((DefaultMutableTreeNode) root.getChildAt(i)).getUserObject())).externalDataIdentifier.getName().
                                compareToIgnoreCase(fdos.specEDI.getName()) > 0) {
                            alphabeticalIndex = i;
                            break;
                        }
                    }
                    if (alphabeticalIndex == -1) {
                        alphabeticalIndex = root.getChildCount();
                    }
                    DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode(new FieldDataMainList(fdos.specEDI, fdos.annotation));
                    mainNode.add(new DefaultMutableTreeNode(new FieldDataVarMainList()));
                    ((DefaultTreeModel) fieldDataGUIPanel.getFieldDataTree().getModel()).insertNodeInto(mainNode, root, alphabeticalIndex);
                }
            }
        };

        return new AsynchClientTask[]{task1, task2, task3};
    }

    /**
     * This method initializes jButtonFindRefModel
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonFindRefModel() {
        if (jButtonFindRefModel == null) {
            jButtonFindRefModel = new JButton();
            jButtonFindRefModel.setText("Model Refs...");
            jButtonFindRefModel.setEnabled(false);
            jButtonFindRefModel.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    TreePath selPath = getFieldDataTree().getSelectionPath();
                    DefaultMutableTreeNode mainNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                    if (mainNode.getUserObject() instanceof FieldDataMainList) {
                        final ExternalDataIdentifier extDataID = ((FieldDataMainList) mainNode.getUserObject()).externalDataIdentifier;

                        AsynchClientTask task1 = new AsynchClientTask("find model references", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

                            @Override
                            public void run(Hashtable<String, Object> hashTable) throws Exception {
                                fieldDataWindowManager.findReferencingModels(extDataID, true);
                            }
                        };
                        ClientTaskDispatcher.dispatch(FieldDataGUIPanel.this, new Hashtable<String, Object>(), new AsynchClientTask[]{task1}, false);
                    }
                }
            });
        }
        return jButtonFindRefModel;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(4, 4, 4, 4);
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new Insets(4, 4, 4, 4);
            gridBagConstraints2.gridy = 0;
            jPanel = new JPanel();
            jPanel.setLayout(new GridBagLayout());
            jPanel.add(getJButtonCreateGeom(), gridBagConstraints2);
            jPanel.add(getJButtonFindRefModel(), gridBagConstraints);
        }
        return jPanel;
    }

    /**
     * This method initializes jButtonCopyInfo
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonCreateGeom() {
        if (createGeomButton == null) {
            createGeomButton = new JButton();
            createGeomButton.setEnabled(false);
            createGeomButton.setText("Create Geom");
            createGeomButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        RequestManager clientRequestManager = fieldDataWindowManager.getLocalRequestManager();
                        javax.swing.tree.TreePath selPath = getFieldDataTree().getSelectionPath();
                        javax.swing.tree.DefaultMutableTreeNode lastPathComponent = (javax.swing.tree.DefaultMutableTreeNode) selPath.getLastPathComponent();
                        if (lastPathComponent.getUserObject() instanceof FieldDataVarList) {
                            DataIdentifier dataIdentifier =
                                    ((FieldDataVarList) lastPathComponent.getUserObject()).dataIdentifier;

                            TreePath ppPath = selPath.getParentPath().getParentPath();
                            javax.swing.tree.DefaultMutableTreeNode ppLastPathComp = (javax.swing.tree.DefaultMutableTreeNode) ppPath.getLastPathComponent();
                            if (ppLastPathComp.getUserObject() instanceof FieldDataMainList) {
                                ExternalDataIdentifier extDataID =
                                        ((FieldDataMainList) ppLastPathComp.getUserObject()).externalDataIdentifier;

                                final OpenModelInfoHolder openModelInfoHolder =
                                        FieldDataWindowManager.selectOpenModelsFromDesktop(FieldDataGUIPanel.this, fieldDataWindowManager.getRequestManager(), false, "Select BioModel or MathModel to receive new geometry", false);
                                if (openModelInfoHolder == null) {
                                    DialogUtils.showErrorDialog(FieldDataGUIPanel.this,
                                            "Before proceeding, please open a Biomodel application or Mathmodel you wish to apply a new Field Data Geometry to");
                                    return;
                                }

                                AsynchClientTask applyGeomTask = new AsynchClientTask("apply geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
                                    @Override
                                    public void run(Hashtable<String, Object> hashTable) throws Exception {
                                        Geometry newGeom = (Geometry) hashTable.get("doc");
                                        final String OK_OPTION = "Ok";
                                        if (openModelInfoHolder instanceof FDSimMathModelInfo) {
                                            Version version = ((FDSimMathModelInfo) openModelInfoHolder).getMathModelVersion();
                                            String modelName = (version == null ? "NoName" : version.getName());
                                            if (newGeom.getName() == null) {
                                                newGeom.setName(modelName + "_" + BeanUtils.generateDateTimeString());
                                            }
                                            String message = "Confirm Setting new FieldData derived geometry on MathModel '" + modelName + "'";
                                            if (DialogUtils.showWarningDialog(FieldDataGUIPanel.this, message, new String[]{OK_OPTION, "Cancel"}, OK_OPTION).equals(OK_OPTION)) {
                                                ((FDSimMathModelInfo) openModelInfoHolder).getMathDescription().setGeometry(newGeom);
                                            }
                                        } else if (openModelInfoHolder instanceof FDSimBioModelInfo) {
                                            Version version = ((FDSimBioModelInfo) openModelInfoHolder).getBioModelVersion();
                                            String modelName = (version == null ? "NoName" : version.getName());
                                            String simContextName = ((FDSimBioModelInfo) openModelInfoHolder).getSimulationContext().getName();
                                            if (newGeom.getName() == null) {
                                                newGeom.setName(modelName + "_" + simContextName + "_" + BeanUtils.generateDateTimeString());
                                            }
                                            String message = "Confirm Setting new FieldData derived geometry on BioModel '" + modelName + "' , Application '" + simContextName + "'";
                                            if (DialogUtils.showWarningDialog(FieldDataGUIPanel.this, message, new String[]{OK_OPTION, "Cancel"}, OK_OPTION).equals(OK_OPTION)) {
                                                ((FDSimBioModelInfo) openModelInfoHolder).getSimulationContext().setGeometry(newGeom);
                                            }
                                        }
                                    }
                                };
                                VCDocument.GeomFromFieldDataCreationInfo geomFromFieldDataCreationInfo =
                                        new VCDocument.GeomFromFieldDataCreationInfo(
                                                extDataID, dataIdentifier.getName());
                                AsynchClientTask[] createGeomTask = clientRequestManager.createNewGeometryTasks(fieldDataWindowManager,
                                        geomFromFieldDataCreationInfo,
                                        new AsynchClientTask[]{applyGeomTask},
                                        "Apply Geometry");

                                Hashtable<String, Object> hash = new Hashtable<String, Object>();
                                hash.put(ClientRequestManager.GUI_PARENT, fieldDataWindowManager.getComponent());
                                ClientTaskDispatcher.dispatch(FieldDataGUIPanel.this, hash,
                                        createGeomTask, false, false, null, true);

                            }

                        }
                    } catch (UserCancelException e1) {
                        //ignore
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        DialogUtils.showErrorDialog(FieldDataGUIPanel.this, e1.getMessage());
                    }
                }
            });
        }
        return createGeomButton;
    }

    /**
     * This method initializes jPanel1
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel12() {
        if (jPanel1 == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.insets = new Insets(4, 4, 4, 4);
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.gridy = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.NONE;
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.insets = new Insets(4, 4, 4, 4);
            jPanel1 = new JPanel();
            jPanel1.setLayout(new GridBagLayout());
            jPanel1.add(getJButtonFDCopyRef(), gridBagConstraints1);
            jPanel1.add(getJButtonViewAnnot(), gridBagConstraints3);
        }
        return jPanel1;
    }

    /**
     * This method initializes jButtonViewAnnot
     *
     * @return javax.swing.JButton
     */
    ActionListener viewAnnotAction = new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            javax.swing.tree.TreePath selPath = getFieldDataTree().getSelectionPath();
            if (selPath != null) {
                javax.swing.tree.DefaultMutableTreeNode lastPathComponent =
                        (javax.swing.tree.DefaultMutableTreeNode) selPath.getLastPathComponent();
                if (lastPathComponent.getUserObject() instanceof FieldDataMainList) {
                    PopupGenerator.showInfoDialog(FieldDataGUIPanel.this,
                            ((FieldDataMainList) (lastPathComponent.getUserObject())).extDataAnnot);
                }
            }
        }
    };

    private JButton getJButtonViewAnnot() {
        if (viewAnnotButton == null) {
            viewAnnotButton = new JButton();
            viewAnnotButton.setText("View Annot...");
            viewAnnotButton.setEnabled(false);
            viewAnnotButton.addActionListener(viewAnnotAction);
        }
        return viewAnnotButton;
    }

}
