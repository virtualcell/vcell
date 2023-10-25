package cbit.vcell.export.gui;

import cbit.vcell.export.server.ExportConstants;

import javax.swing.*;
import java.awt.*;

public class N5SettingsPanel extends javax.swing.JPanel implements ExportConstants, java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
    private javax.swing.JButton ivjJButtonOK = null;
    private javax.swing.ButtonGroup ivjButtonGroup1 = null;
    private javax.swing.JLabel ivjJLabelDataType = null;
    private javax.swing.JRadioButton ivjJRadioButtonParticles = null;
    private javax.swing.JRadioButton ivjJRadioButtonVariables = null;
    protected transient cbit.vcell.export.gui.ASCIISettingsPanelListener fieldASCIISettingsPanelListenerEventMulticaster = null;
    private javax.swing.JCheckBox ivjJCheckBoxSwitch = null;
    private javax.swing.JLabel ivjJLabelAdditional = null;
    private boolean fieldSwitchRowsColumns = false;
    private int fieldSimDataType = 0;
    private ExportConstants.DataType fieldExportDataType;
    private javax.swing.JButton ivjCancelJButton = null;
    private javax.swing.JPanel ivjJPanel1 = null;


    private JRadioButton ivjRadioButtonBZIP = null;
    private JRadioButton ivjRadioButtonGZIP = null;
    /**
     * Constructor
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public N5SettingsPanel() {
        super();
        initialize();
    }
    /**
     * MovieSettingsPanel constructor comment.
     * @param layout java.awt.LayoutManager
     */
    public N5SettingsPanel(java.awt.LayoutManager layout) {
        super(layout);
    }
    /**
     * MovieSettingsPanel constructor comment.
     * @param layout java.awt.LayoutManager
     * @param isDoubleBuffered boolean
     */
    public N5SettingsPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }
    /**
     * MovieSettingsPanel constructor comment.
     * @param isDoubleBuffered boolean
     */
    public N5SettingsPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }
    /**
     * Method to handle events for the ActionListener interface.
     * @param e java.awt.event.ActionEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        // user code begin {1}
        // user code end
        if (e.getSource() == getJButtonOK())
            connEtoC1(e);
        if (e.getSource() == getCancelJButton())
            try {
                // user code begin {1}
                // user code end
                this.fireJButtonCancelAction_actionPerformed(new java.util.EventObject(this));
                // user code begin {2}
                // user code end
            } catch (Throwable ivjExc) {
                // user code begin {3}
                // user code end
                handleException(ivjExc);
            }
        // user code begin {2}
        // user code end
    }
    /**
     *
     * @param newListener cbit.vcell.export.ASCIISettingsPanelListener
     */
    public void addASCIISettingsPanelListener(cbit.vcell.export.gui.ASCIISettingsPanelListener newListener) {
        fieldASCIISettingsPanelListenerEventMulticaster = cbit.vcell.export.gui.ASCIISettingsPanelListenerEventMulticaster.add(fieldASCIISettingsPanelListenerEventMulticaster, newListener);
        return;
    }
    /**
     * connEtoC1:  (JButtonOK.action.actionPerformed(java.awt.event.ActionEvent) --> ASCIISettingsPanel.fireJButtonOKAction_actionPerformed(Ljava.util.EventObject;)V)
     * @param arg1 java.awt.event.ActionEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.fireJButtonOKAction_actionPerformed(new java.util.EventObject(this));
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }
    /* WARNING: THIS METHOD WILL BE REGENERATED. */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */

    /* WARNING: THIS METHOD WILL BE REGENERATED. */

    /**
     * Method to support listener events.
     * @param newEvent java.util.EventObject
     */
    protected void fireJButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
        if (fieldASCIISettingsPanelListenerEventMulticaster == null) {
            return;
        };
        fieldASCIISettingsPanelListenerEventMulticaster.JButtonCancelAction_actionPerformed(newEvent);
    }
    /**
     * Method to support listener events.
     * @param newEvent java.util.EventObject
     */
    protected void fireJButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
        if (fieldASCIISettingsPanelListenerEventMulticaster == null) {
            return;
        };
        fieldASCIISettingsPanelListenerEventMulticaster.JButtonOKAction_actionPerformed(newEvent);
    }

    /**
     * Return the ButtonGroup1 property value.
     * @return javax.swing.ButtonGroup
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.ButtonGroup getButtonGroup1() {
        if (ivjButtonGroup1 == null) {
            try {
                ivjButtonGroup1 = new javax.swing.ButtonGroup();
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjButtonGroup1;
    }
    /**
     * Return the CancelJButton property value.
     * @return javax.swing.JButton
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getCancelJButton() {
        if (ivjCancelJButton == null) {
            try {
                ivjCancelJButton = new javax.swing.JButton();
                ivjCancelJButton.setName("CancelJButton");
                ivjCancelJButton.setText("Cancel");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCancelJButton;
    }

    /**
     * Return the JButtonOK property value.
     * @return javax.swing.JButton
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getJButtonOK() {
        if (ivjJButtonOK == null) {
            try {
                ivjJButtonOK = new javax.swing.JButton();
                ivjJButtonOK.setName("JButtonOK");
                ivjJButtonOK.setFont(new java.awt.Font("dialog", 1, 12));
                ivjJButtonOK.setText("OK");
                ivjJButtonOK.setMaximumSize(new java.awt.Dimension(100, 50));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJButtonOK;
    }
    /**
     * Return the JCheckBoxSwitch property value.
     * @return javax.swing.JCheckBox
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JCheckBox getJCheckBoxSwitch() {
        if (ivjJCheckBoxSwitch == null) {
            try {
                ivjJCheckBoxSwitch = new javax.swing.JCheckBox();
                ivjJCheckBoxSwitch.setName("JCheckBoxSwitch");
                ivjJCheckBoxSwitch.setText("switch rows/columns");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxSwitch;
    }
    /**
     * Return the JLabelAdditional property value.
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabelAdditional() {
        if (ivjJLabelAdditional == null) {
            try {
                ivjJLabelAdditional = new javax.swing.JLabel();
                ivjJLabelAdditional.setName("JLabelAdditional");
                ivjJLabelAdditional.setPreferredSize(new java.awt.Dimension(108, 27));
                ivjJLabelAdditional.setText("Additional formatting:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelAdditional;
    }
    /**
     * Return the JLabelDataFormat property value.
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabelCompressionType() {
        if (ivjJLabelDataType == null) {
            try {
                ivjJLabelDataType = new javax.swing.JLabel();
                ivjJLabelDataType.setName("JLabelCompressionType");
                ivjJLabelDataType.setPreferredSize(new java.awt.Dimension(108, 27));
                ivjJLabelDataType.setText("Compression Type:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelDataType;
    }
    /**
     * Return the JPanel1 property value.
     * @return javax.swing.JPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJPanel1() {
        if (ivjJPanel1 == null) {
            try {
                ivjJPanel1 = new javax.swing.JPanel();
                ivjJPanel1.setName("JPanel1");
                ivjJPanel1.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsJButtonOK = new java.awt.GridBagConstraints();
                constraintsJButtonOK.fill = GridBagConstraints.HORIZONTAL;
                constraintsJButtonOK.gridx = 0; constraintsJButtonOK.gridy = 0;
                getJPanel1().add(getJButtonOK(), constraintsJButtonOK);

                java.awt.GridBagConstraints constraintsCancelJButton = new java.awt.GridBagConstraints();
                constraintsCancelJButton.fill = GridBagConstraints.HORIZONTAL;
                constraintsCancelJButton.gridx = 1; constraintsCancelJButton.gridy = 0;
                getJPanel1().add(getCancelJButton(), constraintsCancelJButton);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJPanel1;
    }
    /**
     * Return the JRadioButtonCompressed property value.
     * @return javax.swing.JRadioButton
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JRadioButton getJRadioButtonParticles() {
        if (ivjJRadioButtonParticles == null) {
            try {
                ivjJRadioButtonParticles = new javax.swing.JRadioButton();
                ivjJRadioButtonParticles.setName("JRadioButtonParticles");
                ivjJRadioButtonParticles.setText("Particle data");
                ivjJRadioButtonParticles.setEnabled(false);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJRadioButtonParticles;
    }
    /**
     * Return the JRadioButtonUncompressed property value.
     * @return javax.swing.JRadioButton
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JRadioButton getJRadioButtonVariables() {
        if (ivjJRadioButtonVariables == null) {
            try {
                ivjJRadioButtonVariables = new javax.swing.JRadioButton();
                ivjJRadioButtonVariables.setName("JRadioButtonVariables");
                ivjJRadioButtonVariables.setSelected(true);
                ivjJRadioButtonVariables.setText("Variable values");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJRadioButtonVariables;
    }

    /**
     * Return the JRadioButtonUncompressed property value.
     * @return javax.swing.JRadioButton
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JRadioButton getJRadioButtonBZIP() {
        if (ivjRadioButtonBZIP == null) {
            try {
                ivjRadioButtonBZIP = new javax.swing.JRadioButton();
                ivjRadioButtonBZIP.setName("JRadioButtonBZIP");
                ivjRadioButtonBZIP.setSelected(true);
                ivjRadioButtonBZIP.setText("BZIP");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjRadioButtonBZIP;
    }

    /**
     * Return the JRadioButtonUncompressed property value.
     * @return javax.swing.JRadioButton
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JRadioButton getJRadioButtonGZIP() {
        if (ivjRadioButtonGZIP == null) {
            try {
                ivjRadioButtonGZIP = new javax.swing.JRadioButton();
                ivjRadioButtonGZIP.setName("JRadioButtonGZIP");
                ivjRadioButtonGZIP.setSelected(true);
                ivjRadioButtonGZIP.setText("GZIP");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjRadioButtonGZIP;
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }
    /**
     * Initializes connections
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initConnections() throws java.lang.Exception {
        // user code begin {1}
        // user code end
        getJButtonOK().addActionListener(this);
        getJRadioButtonVariables().addItemListener(this);
        getJCheckBoxSwitch().addChangeListener(this);
        this.addPropertyChangeListener(this);
        getCancelJButton().addActionListener(this);
    }
    /**
     * Initialize the class.
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("N5SettingsPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(235, 403);

            java.awt.GridBagConstraints constraintsJLabelDataType = new java.awt.GridBagConstraints();
            constraintsJLabelDataType.gridx = 0; constraintsJLabelDataType.gridy = 0;
            constraintsJLabelDataType.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJLabelDataType.weightx = 1.0;
            constraintsJLabelDataType.insets = new Insets(10, 5, 5, 0);
            add(getJLabelCompressionType(), constraintsJLabelDataType);

            java.awt.GridBagConstraints constraintsJRadioButtonVariables = new java.awt.GridBagConstraints();
            constraintsJRadioButtonVariables.anchor = GridBagConstraints.WEST;
            constraintsJRadioButtonVariables.gridx = 0; constraintsJRadioButtonVariables.gridy = 1;
            constraintsJRadioButtonVariables.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJRadioButtonVariables.weightx = 1.0;
            constraintsJRadioButtonVariables.insets = new Insets(0, 5, 5, 0);
            add(getJRadioButtonBZIP(), constraintsJRadioButtonVariables);

            java.awt.GridBagConstraints constraintsJRadioButtonParticles = new java.awt.GridBagConstraints();
            constraintsJRadioButtonParticles.anchor = GridBagConstraints.WEST;
            constraintsJRadioButtonParticles.gridx = 0; constraintsJRadioButtonParticles.gridy = 2;
            constraintsJRadioButtonParticles.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJRadioButtonParticles.weightx = 1.0;
            constraintsJRadioButtonParticles.insets = new Insets(0, 5, 5, 0);
            add(getJRadioButtonGZIP(), constraintsJRadioButtonParticles);

//            java.awt.GridBagConstraints constraintsJLabelAdditional = new java.awt.GridBagConstraints();
//            constraintsJLabelAdditional.gridx = 0; constraintsJLabelAdditional.gridy = 3;
//            constraintsJLabelAdditional.fill = java.awt.GridBagConstraints.HORIZONTAL;
//            constraintsJLabelAdditional.weightx = 1.0;
//            constraintsJLabelAdditional.insets = new Insets(0, 5, 5, 0);
//            add(getJLabelAdditional(), constraintsJLabelAdditional);
//            GridBagConstraints gbc_ivjJCheckBoxHDF5 = new GridBagConstraints();
//            gbc_ivjJCheckBoxHDF5.anchor = GridBagConstraints.WEST;
//            gbc_ivjJCheckBoxHDF5.insets = new Insets(0, 5, 5, 0);
//            gbc_ivjJCheckBoxHDF5.gridx = 0;
//            gbc_ivjJCheckBoxHDF5.gridy = 4;
//            add(getIvjJCheckBoxHDF5(), gbc_ivjJCheckBoxHDF5);
//
//            java.awt.GridBagConstraints constraintsJCheckBoxSwitch = new java.awt.GridBagConstraints();
//            constraintsJCheckBoxSwitch.gridx = 0; constraintsJCheckBoxSwitch.gridy = 5;
//            constraintsJCheckBoxSwitch.fill = java.awt.GridBagConstraints.HORIZONTAL;
//            constraintsJCheckBoxSwitch.insets = new Insets(0, 5, 5, 0);
//            add(getJCheckBoxSwitch(), constraintsJCheckBoxSwitch);
//
//            GridBagConstraints gbc_timeSimVarChkBox = new GridBagConstraints();
//            gbc_timeSimVarChkBox.anchor = GridBagConstraints.WEST;
//            gbc_timeSimVarChkBox.insets = new Insets(0, 5, 5, 0);
//            gbc_timeSimVarChkBox.gridx = 0;
//            gbc_timeSimVarChkBox.gridy = 8;
//            add(getTimeSimVarChkBox(), gbc_timeSimVarChkBox);




            java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
            constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 12;
            add(getJPanel1(), constraintsJPanel1);
            initConnections();
            try {
                // user code begin {1}
                // user code end
                getButtonGroup1().add(getJRadioButtonVariables());
                // user code begin {2}
                // user code end
            } catch (Throwable ivjExc) {
                // user code begin {3}
                // user code end
                handleException(ivjExc);
            }
            try {
                // user code begin {1}
                // user code end
                getButtonGroup1().add(getJRadioButtonParticles());
                // user code begin {2}
                // user code end
            } catch (Throwable ivjExc) {
                // user code begin {3}
                // user code end
                handleException(ivjExc);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        // user code end
    }

    public static void main(String[] args) {
        N5SettingsPanel n5SettingsPanel = new N5SettingsPanel();
        n5SettingsPanel.initialize();
    }

    /**
     * Method to handle events for the ItemListener interface.
     * @param e java.awt.event.ItemEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void itemStateChanged(java.awt.event.ItemEvent e) {
        // user code begin {1}
        // user code end
        if (e.getSource() == getJRadioButtonVariables())
            try {
                // user code begin {1}
                // user code end
                this.updateExportDataType();
                // user code begin {2}
                // user code end
            } catch (Throwable ivjExc) {
                // user code begin {3}
                // user code end
                handleException(ivjExc);
            }
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the PropertyChangeListener interface.
     * @param evt java.beans.PropertyChangeEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        // user code begin {1}
        // user code end
        if (evt.getSource() == this && (evt.getPropertyName().equals("simDataType")))
            try {
                // user code begin {1}
                // user code end
    //            this.updateChoices(this.getSimDataType());
                // user code begin {2}
                // user code end
            } catch (Throwable ivjExc) {
                // user code begin {3}
                // user code end
                handleException(ivjExc);
            }
        // user code begin {2}
        // user code end
    }
///**
// *
// * @param newListener cbit.vcell.export.ASCIISettingsPanelListener
// */
//public void removeASCIISettingsPanelListener(cbit.vcell.export.gui.ASCIISettingsPanelListener newListener) {
//	fieldASCIISettingsPanelListenerEventMulticaster = cbit.vcell.export.gui.ASCIISettingsPanelListenerEventMulticaster.remove(fieldASCIISettingsPanelListenerEventMulticaster, newListener);
//	return;
//}
    /**
     * Sets the exportDataType property (int) value.
     * @param odeVariableData The new value for the property.
     * @see
     */
    private void setExportDataType(ExportConstants.DataType odeVariableData) {
        ExportConstants.DataType oldValue = fieldExportDataType;
        fieldExportDataType = odeVariableData;
        firePropertyChange("exportDataType", oldValue, odeVariableData);
    }

    private boolean isCSVExport = false;
    public void setIsCSVExport(boolean isCSVExport){
        this.isCSVExport = isCSVExport;
    }
    /**
     * Method to handle events for the ChangeListener interface.
     * @param e javax.swing.event.ChangeEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void stateChanged(javax.swing.event.ChangeEvent e) {
        // user code begin {1}
        // user code end
        if (e.getSource() == getJCheckBoxSwitch())
            return;
//            connPtoP1SetTarget();
        // user code begin {2}
        // user code end
    }
    /**
     * Comment
     */
    private void updateChoices(int dataType) {
        switch (dataType) {
            case ODE_SIMULATION:
                getJRadioButtonParticles().setEnabled(false);
                getJRadioButtonVariables().setSelected(true);
                setExportDataType(ExportConstants.DataType.ODE_VARIABLE_DATA);
                break;
            case PDE_SIMULATION_NO_PARTICLES:
                getJRadioButtonParticles().setEnabled(false);
                getJRadioButtonVariables().setSelected(true);
                setExportDataType(ExportConstants.DataType.PDE_VARIABLE_DATA);
                break;
            case PDE_SIMULATION_WITH_PARTICLES:
                getJRadioButtonParticles().setEnabled(true);
                getJRadioButtonParticles().setSelected(true);
                setExportDataType(ExportConstants.DataType.PDE_PARTICLE_DATA);
                break;
        }
        return;
    }
    /**
     * Comment
     */
    private void updateExportDataType() {
        if (getJRadioButtonVariables().isSelected()) setExportDataType(ExportConstants.DataType.PDE_VARIABLE_DATA);
        if (getJRadioButtonParticles().isSelected()) setExportDataType(ExportConstants.DataType.PDE_PARTICLE_DATA);
        return;
    }


    private JButton simSelectorButton;
    private JCheckBox chckbxExportMultiParamScan;
    private JButton paramScanSelectorButton;
    private JCheckBox timeSimVarChkBox;
    private JCheckBox ivjJCheckBoxHDF5;
    private JLabel lblSeeVcellHelp;

    private JCheckBox getTimeSimVarChkBox() {
        if (timeSimVarChkBox == null) {
            timeSimVarChkBox = new JCheckBox("CSV Time-Sim-Var Layout");
            timeSimVarChkBox.setEnabled(false);
        }
        return timeSimVarChkBox;
    }
    public JCheckBox getIvjJCheckBoxHDF5() {
        if (ivjJCheckBoxHDF5 == null) {
            ivjJCheckBoxHDF5 = new JCheckBox("HDF5 Format");
        }
        return ivjJCheckBoxHDF5;
    }
    private JLabel getLblSeeVcellHelp() {
        if (lblSeeVcellHelp == null) {
            lblSeeVcellHelp = new JLabel("Data set layout is xyzt");		// was "See VCell Help (Biomodel Applications/Simulations/Simulation Results/Exporting Spatial Simulation Results)"
            lblSeeVcellHelp.setMinimumSize(new Dimension(300, 15));
            lblSeeVcellHelp.setMaximumSize(new Dimension(300, 15));
            lblSeeVcellHelp.setPreferredSize(new Dimension(300, 15));
            lblSeeVcellHelp.setToolTipText("See VCell Help (Biomodel Applications/Simulations/Simulation Results/Exporting Spatial Simulation Results)");
        }
        return lblSeeVcellHelp;
    }
}
