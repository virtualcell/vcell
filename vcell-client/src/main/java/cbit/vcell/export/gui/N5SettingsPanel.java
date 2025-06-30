package cbit.vcell.export.gui;

import cbit.vcell.export.server.*;

import javax.swing.*;
import java.awt.*;

public class N5SettingsPanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
    private javax.swing.JButton ivjJButtonOK = null;
    private javax.swing.JLabel ivjJLabelDataType = null;
    private javax.swing.JRadioButton ivjJRadioButtonParticles = null;
    private javax.swing.JRadioButton ivjJRadioButtonVariables = null;
    protected transient cbit.vcell.export.gui.ASCIISettingsPanelListener fieldASCIISettingsPanelListenerEventMulticaster = null;
    private javax.swing.JButton ivjCancelJButton = null;
    private javax.swing.JPanel mainPanel = null;
    private ExportSpecs.SimulationSelector simulationSelector;

    private JLabel dataSetNameLabel = null;
    private JTextField dataSetName = null;


    public N5SettingsPanel() {
        super();
        initialize();
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
        }
        catch (Throwable ivjExc){
            handleException(ivjExc);
        }
    }

    public void addN5SettingsPanelListener(cbit.vcell.export.gui.ASCIISettingsPanelListener newListener) {
        fieldASCIISettingsPanelListenerEventMulticaster = cbit.vcell.export.gui.ASCIISettingsPanelListenerEventMulticaster.add(fieldASCIISettingsPanelListenerEventMulticaster, newListener);
        return;
    }

    protected void fireJButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
        if (fieldASCIISettingsPanelListenerEventMulticaster == null) {
            return;
        };
        fieldASCIISettingsPanelListenerEventMulticaster.JButtonCancelAction_actionPerformed(newEvent);
    }

    protected void fireJButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
        String dataSetName = getJTextFieldDataSetName().getText();

        boolean allowedCharacters = dataSetName.matches("^[a-zA-Z0-9 \\[\\]():,_-]*$");
        boolean onlySpaces = dataSetName.matches("^\\s*$");
        boolean notAllowedName = dataSetName.isEmpty() || onlySpaces || !allowedCharacters || dataSetName.length() > 100;
        if (fieldASCIISettingsPanelListenerEventMulticaster == null || notAllowedName) {
            JOptionPane.showMessageDialog(null, "N5 File Name contains characters not allowed. A-Z, 0-9, [] , () , : , _ ,- are allowed.", "Naming Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        };
        fieldASCIISettingsPanelListenerEventMulticaster.JButtonOKAction_actionPerformed(newEvent);
    }


    private javax.swing.JButton getCancelJButton() {
        if (ivjCancelJButton == null) {
            try {
                ivjCancelJButton = new javax.swing.JButton();
                ivjCancelJButton.setName("CancelJButton");
                ivjCancelJButton.setText("Cancel");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjCancelJButton;
    }


    private javax.swing.JButton getJButtonOK() {
        if (ivjJButtonOK == null) {
            try {
                ivjJButtonOK = new javax.swing.JButton();
                ivjJButtonOK.setName("JButtonOK");
                ivjJButtonOK.setFont(new java.awt.Font("dialog", 1, 12));
                ivjJButtonOK.setText("OK");
                ivjJButtonOK.setMaximumSize(new java.awt.Dimension(100, 50));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJButtonOK;
    }


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
                dataSetNameLabel.setText("N5 Dataset Name:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return dataSetNameLabel;
    }

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
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return mainPanel;
    }

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

    private javax.swing.JRadioButton getJRadioButtonVariables() {
        if (ivjJRadioButtonVariables == null) {
            try {
                ivjJRadioButtonVariables = new javax.swing.JRadioButton();
                ivjJRadioButtonVariables.setName("JRadioButtonVariables");
                ivjJRadioButtonVariables.setSelected(true);
                ivjJRadioButtonVariables.setText("Variable values");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJRadioButtonVariables;
    }

    private javax.swing.JTextField getJTextFieldDataSetName() {
        if (dataSetName == null) {
            try {
                dataSetName = new javax.swing.JTextField();
                dataSetName.setName("JTextFieldDataSetName");
                dataSetName.setToolTipText("Only A-Z, 1-9, _, [], (), :, -");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return dataSetName;
    }

    private void handleException(Throwable exception) {

        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    private void initConnections() throws java.lang.Exception {
        getJButtonOK().addActionListener(this);
        getJRadioButtonVariables().addItemListener(this);
        this.addPropertyChangeListener(this);
        getCancelJButton().addActionListener(this);
    }

    private void initialize() {
        try {
            setName("N5SettingsPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(235, 403);

            java.awt.GridBagConstraints constraintsJLabelDataSetName = new java.awt.GridBagConstraints();
            constraintsJLabelDataSetName.gridx = 0; constraintsJLabelDataSetName.gridy = 0;
            constraintsJLabelDataSetName.fill = java.awt.GridBagConstraints.HORIZONTAL;
            add(getJLabelDatasetName(), constraintsJLabelDataSetName);

            java.awt.GridBagConstraints constraintsJTextFieldDataSetName = new java.awt.GridBagConstraints();
            constraintsJTextFieldDataSetName.gridx = 0; constraintsJTextFieldDataSetName.gridy = 1;
            constraintsJTextFieldDataSetName.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJTextFieldDataSetName.weightx = 1.0;
            add(getJTextFieldDataSetName(), constraintsJTextFieldDataSetName);


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
        jFrame.setSize(235, 403);
        jFrame.setVisible(true);
        n5SettingsPanel.getMainPanel().show();
    }


    public void itemStateChanged(java.awt.event.ItemEvent e) {
        return;
    }

    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        return;
    }

    public void stateChanged(javax.swing.event.ChangeEvent e) {
        return;
    }

    public N5Specs getN5Specs(){
        ExportSpecs.SimNameSimDataID[] simDataID = simulationSelector == null ? null : simulationSelector.getSelectedSimDataInfo();
        int[] paramScanIndexes = simulationSelector == null ? null : simulationSelector.getselectedParamScanIndexes();
        String dataSetName = getJTextFieldDataSetName().getText();

        return new N5Specs(ExportSpecss.ExportableDataType.PDE_VARIABLE_DATA, ExportFormat.N5, N5Specs.CompressionLevel.GZIP, dataSetName);
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
