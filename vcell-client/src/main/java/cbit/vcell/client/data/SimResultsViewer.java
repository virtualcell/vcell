/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.data;

import cbit.rmi.event.DataJobEvent;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.ClientTaskDispatcher.BlockingTimer;
import cbit.vcell.export.gui.ExportMonitorPanel;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.Constant;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.ode.ODESolverResultSet;
import org.vcell.util.ArrayUtils;
import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.VCDataIdentifier;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * Insert the type's description here.
 * Creation date: (10/17/2005 11:22:58 PM)
 *
 * @author: Ion Moraru
 */
public class SimResultsViewer extends DataViewer {
    private Simulation simulation = null;
    private DataViewer mainViewer = null;
    private JPanel paramChoicesPanel = null;
    private ODEDataViewer odeDataViewer = null;
    private PDEDataViewer pdeDataViewer = null;
    private final boolean isODEData;
    private final Hashtable<String, JTable> choicesHash = new Hashtable<>();
    private DataManager dataManager = null;

    public SimResultsViewer(Simulation simulation, DataManager arg_dataManager) throws DataAccessException {
        super();
        this.setSimulation(simulation);
        // If Langevin, ignore spatial so that it can be displayed as an ODE model
        if (simulation.getSolverTaskDescription().getSolverDescription().isLangevinSolver()) {
            this.isODEData = true;
        } else {
            this.isODEData = !simulation.isSpatial();
        }
        this.dataManager = arg_dataManager;
        this.initialize();
    }


    /**
     * Insert the method's description here.
     * Creation date: (6/11/2004 2:33:44 PM)
     *
     * @return javax.swing.JPanel
     * @throws DataAccessException
     */
    private DataViewer createODEDataViewer() throws DataAccessException {
        this.odeDataViewer = new ODEDataViewer();
        this.odeDataViewer.setSimulation(this.getSimulation());
        ODESolverResultSet odesrs = ((ODEDataManager) this.dataManager).getODESolverResultSet();
        this.odeDataViewer.setOdeSolverResultSet(odesrs);
        this.odeDataViewer.setNFSimMolecularConfigurations(((ODEDataManager) this.dataManager).getNFSimMolecularConfigurations());
        this.odeDataViewer.setVcDataIdentifier(this.dataManager.getVCDataIdentifier());
        if (this.getSimulation() != null) {
            String ownerName = generateHDF5DescrOwner(this.getSimulation());
            this.odeDataViewer.setHDF5DescriptionText(ownerName + ":" + this.simulation.getName());
        }
//	odeDataViewer.setXVarName(odeDataViewer.getODESolverPlotSpecificationPanel1().getXAxisComboBox_frm().getSelectedItem().toString());
        return this.odeDataViewer;
    }


    public static String generateHDF5DescrOwner(Simulation simulation) {
        String ownerName = "";
        if (simulation.getSimulationOwner() != null) {
            if (simulation.getSimulationOwner() instanceof SimulationContext sc) {
                BioModel bm = sc.getBioModel();
                ownerName = sc.getBioModel().getName();
                if (bm.getVersion() != null) {
                    ownerName += " (" + bm.getVersion().getDate() + ")";
                } else {
                    ownerName += " (unversioned)";
                }
                ownerName += ":" + sc.getName();
            } else {//MathModel
                MathModel mm = (MathModel) simulation.getSimulationOwner();
                ownerName = mm.getName();
                if (mm.getVersion() != null) {
                    ownerName += mm.getVersion().getDate();
                } else {
                    ownerName += " (unversioned)";
                }
            }
        }
        return ownerName;
    }


    /**
     * Insert the method's description here.
     * Creation date: (6/11/2004 2:33:44 PM)
     *
     * @return javax.swing.JPanel
     */
    private DataViewer createPDEDataViewer() throws DataAccessException {
        this.pdeDataViewer = new PDEDataViewer();
        this.pdeDataViewer.setSimulation(this.getSimulation());
        this.pdeDataViewer.setPdeDataContext(((PDEDataManager) this.dataManager).getPDEDataContext());
        return this.pdeDataViewer;
    }

    public void dataJobMessage(DataJobEvent dje) {
        this.getMainViewer().dataJobMessage(dje);
    }

    /**
     * Method generated to support the promotion of the exportMonitorPanel attribute.
     *
     * @return cbit.vcell.export.ExportMonitorPanel
     */
    public ExportMonitorPanel getExportMonitorPanel() {
        return this.getMainViewer().getExportMonitorPanel();
    }


    /**
     * Insert the method's description here.
     * Creation date: (10/17/2005 11:36:17 PM)
     *
     * @return cbit.vcell.client.data.DataViewer
     */
    public DataViewer getMainViewer() {
        return this.mainViewer;
    }


    /**
     * Insert the method's description here.
     * Creation date: (10/17/2005 11:36:17 PM)
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getParamChoicesPanel() {
        return this.paramChoicesPanel;
    }

    /**
     * Insert the method's description here.
     * Creation date: (10/17/2005 11:36:17 PM)
     *
     * @return cbit.vcell.solver.Simulation
     */
    private Simulation getSimulation() {
        return this.simulation;
    }

    private final JLabel label = new JLabel("<html><b>Choose Parameter Values</b></html>");
    private int localScanProgress;

    public void setLocalScanProgress(int localScanProgress) {
        this.localScanProgress = localScanProgress;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SimResultsViewer.this.label.setText("<html><b>Choose Parameter Values progress:(" + (localScanProgress) + "/" + SimResultsViewer.this.getSimulation().getScanCount() + ")</b></html>");
            }
        });
    }

    public int getLocalScanProgress() {
        return this.localScanProgress;
    }

    /**
     * Insert the method's description here.
     * Creation date: (10/17/2005 11:37:52 PM)
     *
     * @throws org.vcell.util.DataAccessException The exception description.
     */
    private void initialize() throws DataAccessException {

        // create main viewer for jobIndex 0 and wire it up
        if (this.isODEData) {
            this.setMainViewer(this.createODEDataViewer());
        } else {
            this.setMainViewer(this.createPDEDataViewer());
        }
        java.beans.PropertyChangeListener pcl = new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if (evt.getSource() == SimResultsViewer.this && (evt.getPropertyName().equals("dataViewerManager"))) {
                    try {
                        SimResultsViewer.this.getMainViewer().setDataViewerManager(SimResultsViewer.this.getDataViewerManager());
                    } catch (java.beans.PropertyVetoException exc) {
                        exc.printStackTrace();
                    }
                }
                if (evt.getSource() == SimResultsViewer.this && (evt.getPropertyName().equals("simulationModelInfo"))) {
                    SimResultsViewer.this.getMainViewer().setSimulationModelInfo(SimResultsViewer.this.getSimulationModelInfo());
                }
            }
        };
        this.addPropertyChangeListener(pcl);


        // if necessarry, create parameter choices panel and wire it up
        if (this.getSimulation().getScanCount() > 1) {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout(5, 0));
            panel.setBorder(BorderFactory.createEtchedBorder());
            this.label.setHorizontalAlignment(SwingConstants.CENTER);
            this.label.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 2));
            panel.add(this.label, BorderLayout.NORTH);

            String[] scanParams = this.getSimulation().getMathOverrides().getScannedConstantNames();
            Arrays.sort(scanParams);
            JPanel tablePanel = new JPanel();
            tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));
            for (int i = 0; i < scanParams.length; i++) {
                Constant[] scanConstants = this.getSimulation().getMathOverrides().getConstantArraySpec(scanParams[i]).getConstants();
                String[][] values = new String[scanConstants.length][1];
                for (int j = 0; j < scanConstants.length; j++) {
                    values[j][0] = scanConstants[j].getExpression().infix();
                }
                class ScanChoicesTableModel extends javax.swing.table.AbstractTableModel {
                    final String[] columnNames;
                    final Object[][] rowData;

                    ScanChoicesTableModel(Object[][] argData, String[] argNames) {
                        this.columnNames = argNames;
                        this.rowData = argData;
                    }

                    public String getColumnName(int column) {
                        return this.columnNames[column];
                    }

                    public int getRowCount() {
                        return this.rowData.length;
                    }

                    public int getColumnCount() {
                        return this.columnNames.length;
                    }

                    public Object getValueAt(int row, int col) {
                        return this.rowData[row][col];
                    }

                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }

                    public void setValueAt(Object value, int row, int col) {
                        this.rowData[row][col] = value;
                        this.fireTableCellUpdated(row, col);
                    }
                }
                ScanChoicesTableModel tm = new ScanChoicesTableModel(values, new String[]{scanParams[i]});
                final JTable table = new JTable(tm);
                this.choicesHash.put(scanParams[i], table);
                table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                table.getSelectionModel().setSelectionInterval(0, 0);
                final ListSelectionListener[] nextListSelectionListener = new ListSelectionListener[1];
                nextListSelectionListener[0] = new javax.swing.event.ListSelectionListener() {
                    public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                        if (!e.getValueIsAdjusting()) {
                            DefaultListSelectionModel list = (DefaultListSelectionModel) e.getSource();
                            int selected = list.getAnchorSelectionIndex();
                            final int previous = (selected == e.getFirstIndex() ? e.getLastIndex() : e.getFirstIndex());
                            ListReset listReset = new ListReset() {
                                @Override
                                public void reset(VCDataIdentifier myVcDataIdentifier) {
                                    if (myVcDataIdentifier instanceof VCSimulationDataIdentifier) {
                                        int paramScanIndex = ((VCSimulationDataIdentifier) myVcDataIdentifier).getJobIndex();
                                        table.getSelectionModel().removeListSelectionListener(nextListSelectionListener[0]);
                                        try {
                                            table.setRowSelectionInterval(paramScanIndex, paramScanIndex);
                                        } finally {
                                            table.getSelectionModel().addListSelectionListener(nextListSelectionListener[0]);
                                        }
                                    } else {
                                        table.setRowSelectionInterval(previous, previous);
                                    }
                                }
                            };
                            SimResultsViewer.this.updateScanParamChoices("SimResultsViewer set paramScan index=" + SimResultsViewer.this.getSelectedParamScanJobIndex(), listReset);
                        }
                    }
                };
                table.getSelectionModel().addListSelectionListener(nextListSelectionListener[0]);
                JScrollPane scr = new JScrollPane(table);
                JPanel p = new JPanel();
                scr.setPreferredSize(new java.awt.Dimension(100, Math.min(150, table.getPreferredSize().height + table.getTableHeader().getPreferredSize().height + 5)));
                p.setLayout(new java.awt.BorderLayout());
                p.add(scr, java.awt.BorderLayout.CENTER);
                p.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                tablePanel.add(p);
            }
            panel.add(tablePanel, BorderLayout.CENTER);

            if (this.isODEData) {
                JPanel buttonPanel = new JPanel(new FlowLayout());
                JButton button = new JButton("Time Plot with Multiple Parameter Value Sets");
                buttonPanel.add(button);
                panel.add(buttonPanel, BorderLayout.SOUTH);

                button.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        if (SimResultsViewer.this.getSimulation().getMathDescription().isNonSpatialStoch()) {
//						DialogUtils.showInfoDialog(SimResultsViewer.this,"Information", "Time Plot with Multiple Parameter Value Sets\nnot yet implemented for non-spatial stochastic");
//						return;
                        }
                        SimResultsViewer.this.mainViewer.showTimePlotMultipleScans(SimResultsViewer.this.dataManager);
                    }
                });
            } else {
                this.pdeDataViewer.setSimNameSimDataID(new ExportSpecs.SimNameSimDataID(this.getSimulation().getName(), this.getSimulation().getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), ExportSpecs.getParamScanInfo(this.getSimulation(), this.getSelectedParamScanJobIndex())));
            }

            this.setParamChoicesPanel(panel);

        }

        // put things together
        this.setLayout(new java.awt.BorderLayout());
        this.add(this.getMainViewer(), java.awt.BorderLayout.CENTER);
        if (this.getSimulation().getScanCount() > 1) {
            this.add(this.getParamChoicesPanel(), java.awt.BorderLayout.SOUTH);
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/11/2004 2:43:49 PM)
     *
     * @throws org.vcell.util.DataAccessException The exception description.
     */
    public void refreshData() throws DataAccessException {
        if (this.isODEData) {
            this.updateScanParamChoices("SimResultsViewer refreshData (ODE)...", null); // this takes care of all logic to get the fresh data
        } else {
            this.pdeDataViewer.getPdeDataContext().refreshTimes();
        }
    }

    public void refreshFunctions() throws DataAccessException {
        if (this.isODEData) {
            this.updateScanParamChoices("SimResultsViewer refreshFunctions (ODE)...", null);
        } else {
            // no other reliable way until the PDE context/viewer/manager/dataset furball will be cleaned up...
            this.updateScanParamChoices("SimResultsViewer refreshData (PDE)...", null);
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (10/17/2005 11:36:17 PM)
     *
     * @param newMainViewer cbit.vcell.client.data.DataViewer
     */
    private void setMainViewer(DataViewer newMainViewer) {
        this.mainViewer = newMainViewer;
    }


    /**
     * Insert the method's description here.
     * Creation date: (10/17/2005 11:36:17 PM)
     *
     * @param newParamChoicesPanel javax.swing.JPanel
     */
    private void setParamChoicesPanel(javax.swing.JPanel newParamChoicesPanel) {
        this.paramChoicesPanel = newParamChoicesPanel;
    }


    /**
     * Insert the method's description here.
     * Creation date: (10/17/2005 11:36:17 PM)
     *
     * @param newSimulation cbit.vcell.solver.Simulation
     */
    private void setSimulation(Simulation newSimulation) {
        this.simulation = newSimulation;
    }


    private int getSelectedParamScanJobIndex() {
        // figure out what job data we are looking for
        final String[] scanConstantNames = this.getSimulation().getMathOverrides().getScannedConstantNames();
        java.util.Arrays.sort(scanConstantNames);
        int[] indices = new int[scanConstantNames.length];
        int[] bounds = new int[scanConstantNames.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = this.choicesHash.get(scanConstantNames[i]).getSelectedRow();
            bounds[i] = this.getSimulation().getMathOverrides().getConstantArraySpec(scanConstantNames[i]).getNumValues() - 1;
        }
        int selectedJobIndex = -1;
        try {
            selectedJobIndex = BeanUtils.parameterScanCoordinateToJobIndex(indices, bounds);
        } catch (RuntimeException exc) {
            exc.printStackTrace();
        }
        return selectedJobIndex;
    }

    /**
     * Insert the method's description here.
     * Creation date: (10/18/2005 12:44:06 AM)
     */
    interface ListReset {
        void reset(VCDataIdentifier vcDataIdentifier);
    }

    private BlockingTimer paramScanChoiceTimer;

    private void updateScanParamChoices(final String message, ListReset listReset) {
        if ((this.paramScanChoiceTimer = ClientTaskDispatcher.getBlockingTimer(this, null, null, this.paramScanChoiceTimer, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e2) {
                SimResultsViewer.this.updateScanParamChoices(message, listReset);
            }
        }, message)) != null) {
            return;
        }

        int selectedJobIndex = this.getSelectedParamScanJobIndex();
        // update viewer
        if (selectedJobIndex == -1) {
            if (this.isODEData) {
                if (listReset != null && this.odeDataViewer != null && this.odeDataViewer.getVcDataIdentifier() != null) {
                    listReset.reset(this.odeDataViewer.getVcDataIdentifier());
                } else {
                    this.odeDataViewer.setOdeSolverResultSet(null);
                }
            } else {
                if (listReset != null && this.pdeDataViewer != null && this.pdeDataViewer.getPdeDataContext() != null && this.pdeDataViewer.getPdeDataContext().getVCDataIdentifier() != null) {
                    listReset.reset(this.pdeDataViewer.getPdeDataContext().getVCDataIdentifier());
                } else {
                    this.pdeDataViewer.setPdeDataContext(null);
                }
            }
            return;
        }

        final VCSimulationDataIdentifier vcdid = new VCSimulationDataIdentifier(this.getSimulation().getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), selectedJobIndex);
        if (this.isODEData) {
            AsynchClientTask task1 = new AsynchClientTask("get ode results", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
                @Override
                public void run(Hashtable<String, Object> hashTable) throws Exception {
                    try {
                        ODEDataManager odeDatamanager = ((ODEDataManager) SimResultsViewer.this.dataManager).createNewODEDataManager(vcdid);
                        hashTable.put("odeDatamanager", odeDatamanager);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        if (e.getMessage() != null && e.getMessage().contains("no results are available yet")) {
                            throw UserCancelException.CANCEL_GENERIC;
                        }
                        throw e;
                    }
                }
            };
            AsynchClientTask task2 = new AsynchClientTask("show results", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false) {
                @Override
                public void run(Hashtable<String, Object> hashTable) throws Exception {
                    if (hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR) == null && hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_USER) == null) {
                        ODEDataManager odeDatamanager = (ODEDataManager) hashTable.get("odeDatamanager");
                        SimResultsViewer.this.odeDataViewer.setOdeSolverResultSet(odeDatamanager.getODESolverResultSet());
                        SimResultsViewer.this.odeDataViewer.setVcDataIdentifier(vcdid);
                    } else {
                        SimResultsViewer.this.odeDataViewer.setOdeSolverResultSet(null);
                    }
                }
            };
            ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[]{task1, task2});
        } else {
            AsynchClientTask task1 = new AsynchClientTask("get pde results", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
                @Override
                public void run(Hashtable<String, Object> hashTable) throws Exception {
                    PDEDataManager pdeDatamanager = ((PDEDataManager) SimResultsViewer.this.dataManager).createNewPDEDataManager(vcdid, null);
                    PDEDataContext newPDEDC = pdeDatamanager.getPDEDataContext();
                    PDEDataContext oldPDEDC = SimResultsViewer.this.pdeDataViewer.getPdeDataContext();
                    hashTable.put("newPDEDC", newPDEDC);
                    if (oldPDEDC != null && oldPDEDC.getTimePoints().length <= newPDEDC.getTimePoints().length) {
                        DataIdentifier setDid = (newPDEDC.getDataIdentifier() == null ? newPDEDC.getDataIdentifiers()[0] : newPDEDC.getDataIdentifier());
                        if (Arrays.asList(newPDEDC.getDataIdentifiers()).contains(oldPDEDC.getDataIdentifier())) {
                            setDid = oldPDEDC.getDataIdentifier();
                            int newTimePointIndex = ArrayUtils.firstIndexOf(Arrays.stream(oldPDEDC.getTimePoints()).boxed().toArray(Double[]::new), oldPDEDC.getTimePoint());
                            newPDEDC.setVariableAndTime(setDid, newPDEDC.getTimePoints()[newTimePointIndex]);
                        }
                    }
                }
            };
            AsynchClientTask task2 = new AsynchClientTask("show results", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false) {
                @Override
                public void run(Hashtable<String, Object> hashTable) throws Exception {
                    if (hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR) == null) {
                        ClientPDEDataContext newPDEDC = (ClientPDEDataContext) hashTable.get("newPDEDC");
                        SimResultsViewer.this.pdeDataViewer.setPdeDataContext(newPDEDC);
                        SimResultsViewer.this.pdeDataViewer.setSimNameSimDataID(new ExportSpecs.SimNameSimDataID(SimResultsViewer.this.getSimulation().getName(), SimResultsViewer.this.getSimulation().getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), ExportSpecs.getParamScanInfo(SimResultsViewer.this.getSimulation(), vcdid.getJobIndex())));
                    } else {
                        if (listReset != null && SimResultsViewer.this.pdeDataViewer != null && SimResultsViewer.this.pdeDataViewer.getPdeDataContext() != null && SimResultsViewer.this.pdeDataViewer.getPdeDataContext().getVCDataIdentifier() != null) {
                            listReset.reset(SimResultsViewer.this.pdeDataViewer.getPdeDataContext().getVCDataIdentifier());
                        } else {
                            SimResultsViewer.this.pdeDataViewer.setPdeDataContext(null);
                            SimResultsViewer.this.pdeDataViewer.setSimNameSimDataID(null);
                        }
                    }
                }
            };

//		AsynchClientTask refreshTask = new AsynchClientTask("",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
//			@Override
//			public void run(Hashtable<String, Object> hashTable) throws Exception {
//				((ArrayList<AsynchClientTask>)hashTable.get(ClientTaskDispatcher.INTERMEDIATE_TASKS)).addAll(Arrays.asList(pdeDataViewer.getRefreshTasks()));
//			}
//		};
            ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[]{task1, task2/*,refreshTask*/});
        }
    }

}
