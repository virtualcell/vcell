package cbit.vcell.export.gui;

import cbit.vcell.export.server.*;

import javax.swing.*;
import java.awt.*;

public class N5SettingsPanel extends javax.swing.JPanel implements ExportConstants, java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
    private javax.swing.JButton ivjJButtonOK = null;
    private javax.swing.JLabel ivjJLabelDataType = null;
    private javax.swing.JRadioButton ivjJRadioButtonParticles = null;
    private javax.swing.JRadioButton ivjJRadioButtonVariables = null;
    protected transient cbit.vcell.export.gui.ASCIISettingsPanelListener fieldASCIISettingsPanelListenerEventMulticaster = null;
    private javax.swing.JCheckBox ivjJCheckBoxSwitch = null;
    private javax.swing.JLabel ivjJLabelAdditional = null;
    private ExportConstants.DataType fieldExportDataType;
    private javax.swing.JButton ivjCancelJButton = null;
    private javax.swing.JPanel mainPanel = null;
    private ExportSpecs.SimulationSelector simulationSelector;

    private JRadioButton ivjRadioButtonBZIP = null;
    private JRadioButton ivjRadioButtonGZIP = null;
    private JRadioButton radioButtonRAW = null;
    private N5Specs.CompressionLevel compressionLevel = N5Specs.CompressionLevel.RAW;
    private JLabel dataSetNameLabel = null;
    private JTextField dataSetName = null;
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


    public void setSimulationSelector(ExportSpecs.SimulationSelector simulationSelector){
        this.simulationSelector = simulationSelector;

    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        try{
            if (e.getSource() == getJButtonOK())
                    this.fireJButtonOKAction_actionPerformed(new java.util.EventObject(this));
            if (e.getSource() == getCancelJButton())
                this.fireJButtonCancelAction_actionPerformed(new java.util.EventObject(this));

            if (e.getSource() == getJRadioButtonBZIP())
                compressionLevel = N5Specs.CompressionLevel.BZIP;
            if (e.getSource() == getJRadioButtonGZIP())
                compressionLevel = N5Specs.CompressionLevel.GZIP;
            if (e.getSource() == getRadioButtonRAW())
                compressionLevel = N5Specs.CompressionLevel.RAW;
        }
        catch (Throwable ivjExc){
            handleException(ivjExc);
        }

        // user code begin {2}
        // user code end
    }
    /**
     *
     * @param newListener cbit.vcell.export.ASCIISettingsPanelListener
     */
    public void addN5SettingsPanelListener(cbit.vcell.export.gui.ASCIISettingsPanelListener newListener) {
        fieldASCIISettingsPanelListenerEventMulticaster = cbit.vcell.export.gui.ASCIISettingsPanelListenerEventMulticaster.add(fieldASCIISettingsPanelListenerEventMulticaster, newListener);
        return;
    }

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
        String dataSetName = getJTextFieldDataSetName().getText();
        boolean textConditions = dataSetName.isEmpty() || !dataSetName.matches("^[a-zA-Z 0-9]*$"); //Regex expression only allows spaces, a-z, A-Z, and 0-9. If it has anything else there is no Regex match
        if (fieldASCIISettingsPanelListenerEventMulticaster == null || textConditions) {
            return;
        };
        fieldASCIISettingsPanelListenerEventMulticaster.JButtonOKAction_actionPerformed(newEvent);
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

    private javax.swing.JLabel getJLabelDatasetName() {
        if (dataSetNameLabel == null) {
            try {
                dataSetNameLabel = new javax.swing.JLabel();
                dataSetNameLabel.setName("JLabelDatasetName");
                dataSetNameLabel.setPreferredSize(new java.awt.Dimension(108, 27));
                dataSetNameLabel.setText("Dataset Name:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return dataSetNameLabel;
    }
    /**
     * Return the JPanel1 property value.
     * @return javax.swing.JPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getMainPanel() {
        if (mainPanel == null) {
            try {
                mainPanel = new javax.swing.JPanel();
                mainPanel.setName("MainPanel");
                mainPanel.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsJButtonOK = new java.awt.GridBagConstraints();
                constraintsJButtonOK.fill = GridBagConstraints.HORIZONTAL;
                constraintsJButtonOK.gridx = 0; constraintsJButtonOK.gridy = 0;
                mainPanel.add(getJButtonOK(), constraintsJButtonOK);

                java.awt.GridBagConstraints constraintsCancelJButton = new java.awt.GridBagConstraints();
                constraintsCancelJButton.fill = GridBagConstraints.HORIZONTAL;
                constraintsCancelJButton.gridx = 1; constraintsCancelJButton.gridy = 0;
                mainPanel.add(getCancelJButton(), constraintsCancelJButton);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return mainPanel;
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
            } catch (java.lang.Throwable ivjExc) {
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

    private JRadioButton getRadioButtonRAW(){
        if (radioButtonRAW == null) {
            try {
                radioButtonRAW = new javax.swing.JRadioButton();
                radioButtonRAW.setName("JRadioButtonRAW");
                radioButtonRAW.setSelected(true);
                radioButtonRAW.setText("RAW");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return radioButtonRAW;
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
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRadioButtonGZIP;
    }

    private javax.swing.JTextField getJTextFieldDataSetName() {
        if (dataSetName == null) {
            try {
                dataSetName = new javax.swing.JTextField();
                dataSetName.setName("JTextFieldDataSetName");
                dataSetName.setToolTipText("Only A-Z and 1-9");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return dataSetName;
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
        this.addPropertyChangeListener(this);
        getCancelJButton().addActionListener(this);

        getJRadioButtonGZIP().addActionListener(this);
        getJRadioButtonBZIP().addActionListener(this);
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
            constraintsJLabelDataType.insets = new Insets(10, 5, 5, 5);
            add(getJLabelCompressionType(), constraintsJLabelDataType);

            java.awt.GridBagConstraints constraintsJRadioButtonVariables = new java.awt.GridBagConstraints();
            constraintsJRadioButtonVariables.anchor = GridBagConstraints.WEST;
            constraintsJRadioButtonVariables.gridx = 0; constraintsJRadioButtonVariables.gridy = 1;
            constraintsJRadioButtonVariables.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJRadioButtonVariables.weightx = 1.0;
            constraintsJRadioButtonVariables.insets = new Insets(0, 5, 5, 5);
            add(getJRadioButtonBZIP(), constraintsJRadioButtonVariables);

            java.awt.GridBagConstraints constraintsJRadioButtonParticles = new java.awt.GridBagConstraints();
            constraintsJRadioButtonParticles.anchor = GridBagConstraints.WEST;
            constraintsJRadioButtonParticles.gridx = 0; constraintsJRadioButtonParticles.gridy = 2;
            constraintsJRadioButtonParticles.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJRadioButtonParticles.weightx = 1.0;
            constraintsJRadioButtonParticles.insets = new Insets(0, 5, 5, 5);
            add(getJRadioButtonGZIP(), constraintsJRadioButtonParticles);

            java.awt.GridBagConstraints constraintsJRadioButtonRAW = new java.awt.GridBagConstraints();
            constraintsJRadioButtonRAW.anchor = GridBagConstraints.WEST;
            constraintsJRadioButtonRAW.gridx = 0; constraintsJRadioButtonRAW.gridy = 3;
            constraintsJRadioButtonRAW.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJRadioButtonRAW.weightx = 1.0;
            constraintsJRadioButtonRAW.insets = new Insets(0, 5, 5, 5);
            add(getRadioButtonRAW(), constraintsJRadioButtonRAW);

            java.awt.GridBagConstraints constraintsJLabelDataSetName = new java.awt.GridBagConstraints();
            constraintsJLabelDataSetName.anchor = GridBagConstraints.WEST;
            constraintsJLabelDataSetName.gridx = 0; constraintsJLabelDataSetName.gridy = 4;
            constraintsJLabelDataSetName.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJLabelDataSetName.weightx = 1.0;
            constraintsJLabelDataSetName.insets = new Insets(0, 5, 5, 5);
            add(getJLabelDatasetName(), constraintsJLabelDataSetName);

            java.awt.GridBagConstraints constraintsJTextFieldDataSetName = new java.awt.GridBagConstraints();
            constraintsJTextFieldDataSetName.anchor = GridBagConstraints.WEST;
            constraintsJTextFieldDataSetName.gridx = 0; constraintsJTextFieldDataSetName.gridy = 5;
            constraintsJTextFieldDataSetName.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJTextFieldDataSetName.weightx = 1.0;
            constraintsJTextFieldDataSetName.insets = new Insets(0, 5, 5, 5);
            add(getJTextFieldDataSetName(), constraintsJTextFieldDataSetName);

            ButtonGroup compressionButtons = new ButtonGroup();
            compressionButtons.add(getJRadioButtonBZIP());
            compressionButtons.add(getJRadioButtonGZIP());
            compressionButtons.add(getRadioButtonRAW());


            java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
            constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 6;
            add(getMainPanel(), constraintsJPanel1);
            initConnections();

        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        // user code end
    }

    public static void main(String[] args) {
        N5SettingsPanel n5SettingsPanel = new N5SettingsPanel();
        n5SettingsPanel.initialize();
        JFrame jFrame = new JFrame();
        jFrame.add(n5SettingsPanel.getMainPanel());
        jFrame.setSize(400, 400);
        jFrame.setVisible(true);
        n5SettingsPanel.getMainPanel().show();
    }

    /**
     * Method to handle events for the ItemListener interface.
     * @param e java.awt.event.ItemEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void itemStateChanged(java.awt.event.ItemEvent e) {
        return;
    }

    /**
     * Method to handle events for the PropertyChangeListener interface.
     * @param evt java.beans.PropertyChangeEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        return;
    }

    /**
     * Method to handle events for the ChangeListener interface.
     * @param e javax.swing.event.ChangeEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void stateChanged(javax.swing.event.ChangeEvent e) {
        return;
    }

    public N5Specs getN5Specs(){
        ExportSpecs.SimNameSimDataID[] simDataID = simulationSelector == null ? null : simulationSelector.getSelectedSimDataInfo();
        int[] paramScanIndexes = simulationSelector == null ? null : simulationSelector.getselectedParamScanIndexes();
        String dataSetName = getJTextFieldDataSetName().getText();

        return new N5Specs(DataType.PDE_VARIABLE_DATA, ExportFormat.N5, compressionLevel, dataSetName);
    }

    private JLabel lblSeeVcellHelp;

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
