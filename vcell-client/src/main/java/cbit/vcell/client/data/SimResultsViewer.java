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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.VCDataIdentifier;

import com.google.common.io.Files;

import cbit.rmi.event.DataJobEvent;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.ClientTaskDispatcher.BlockingTimer;
import cbit.vcell.export.gui.ExportMonitorPanel;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.Constant;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.ClientPDEDataContext;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataManager;
import cbit.vcell.simdata.ODEDataManager;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.ode.ODESimData;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5ScalarDS;
/**
 * Insert the type's description here.
 * Creation date: (10/17/2005 11:22:58 PM)
 * @author: Ion Moraru
 */
public class SimResultsViewer extends DataViewer {
	private Simulation simulation = null;
	private DataViewer mainViewer = null;
	private JPanel paramChoicesPanel = null;
	private ODEDataViewer odeDataViewer = null;
	private PDEDataViewer pdeDataViewer = null;
	private boolean isODEData;
	private Hashtable<String, JTable> choicesHash = new Hashtable<String, JTable>();
	private DataManager dataManager = null;

/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:30:45 PM)
 * @param simulation cbit.vcell.solver.Simulation
 * @param vcDataManager cbit.vcell.client.server.VCDataManager
 */
public SimResultsViewer(Simulation simulation, DataManager arg_dataManager) throws DataAccessException {
	super();
	setSimulation(simulation);
	this.isODEData = !simulation.isSpatial();
	this.dataManager = arg_dataManager;
	initialize();
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 2:33:44 PM)
 * @return javax.swing.JPanel
 * @throws DataAccessException 
 */
private DataViewer createODEDataViewer() throws DataAccessException {
	odeDataViewer = new ODEDataViewer();
	odeDataViewer.setSimulation(getSimulation());
	odeDataViewer.setOdeSolverResultSet(((ODEDataManager)dataManager).getODESolverResultSet());
	odeDataViewer.setNFSimMolecularConfigurations(((ODEDataManager)dataManager).getNFSimMolecularConfigurations());
	odeDataViewer.setVcDataIdentifier(dataManager.getVCDataIdentifier());
	if(getSimulation() != null) {
		odeDataViewer.setHDF5DescriptionText(
				(simulation.getSimulationOwner() instanceof SimulationContext
						?((SimulationContext)simulation.getSimulationOwner()).getBioModel().toString()
						:"")+"\n"+
				simulation.getSimulationOwner()+"\n"+
				"Simulation:"+(simulation.getSimulationVersion() != null?simulation.getSimulationVersion().toString():simulation.getName()+" (unversioned)"));
	}
	//
	//Example code for reading stats data from Stochastic multitrial non-histogram
	//
	FileFormat hdf5FileFormat = null;
	File to = null;
	try {
		if(odeDataViewer.getOdeSolverResultSet() instanceof ODESimData) {
			byte[] hdf5FileBytes = ((ODESimData)odeDataViewer.getOdeSolverResultSet()).getHdf5FileBytes();
			if(hdf5FileBytes != null) {
				to = File.createTempFile("odeStats_"+getSimulation().getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), ".hdf5");
				Files.write(hdf5FileBytes, to);
				FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
				if (fileFormat == null){
					throw new Exception("Cannot find HDF5 FileFormat.");
				}
				// open the file with read-only access	
				hdf5FileFormat = fileFormat.createInstance(to.getAbsolutePath(), FileFormat.READ);
				// open the file and retrieve the file structure
				hdf5FileFormat.open();
				Group root = (Group)((javax.swing.tree.DefaultMutableTreeNode)hdf5FileFormat.getRootNode()).getUserObject();
				List<HObject> postProcessMembers = ((Group)root).getMemberList();
				for(HObject nextHObject:postProcessMembers){
					//System.out.println(nextHObject.getName()+"\n"+nextHObject.getClass().getName());
					H5ScalarDS h5ScalarDS = (H5ScalarDS)nextHObject;
					h5ScalarDS.init();
					try {
						long[] dims = h5ScalarDS.getDims();
						System.out.println("---"+nextHObject.getName()+" "+nextHObject.getClass().getName()+" Dimensions="+Arrays.toString(dims));
						Object obj = h5ScalarDS.read();
						if(dims.length == 2) {
							//dims[0]=numTimes (will be the same as 'SimTimes' data length)
							//dims[1]=numVars (will be the same as 'VarNames' data length)
							//if name='StatMean' this is the same as the default data saved in the odeSolverresultSet
							double[] columns = new double[(int)dims[1]];
							for(int row=0;row<dims[0];row++) {
								System.arraycopy(obj, row*columns.length, columns, 0, columns.length);
								System.out.println(Arrays.toString(columns));
							}
						}else {
							if(obj instanceof double[]) {
								System.out.println(Arrays.toString((double[])obj));
							}else {
								System.out.println(Arrays.toString((String[])obj));
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(hdf5FileFormat != null) {try{hdf5FileFormat.close();}catch(Exception e2) {e2.printStackTrace();}}
		if(to != null){try{to.delete();}catch(Exception e2) {e2.printStackTrace();}}
	}
	return odeDataViewer;
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 2:33:44 PM)
 * @return javax.swing.JPanel
 */
private DataViewer createPDEDataViewer() throws DataAccessException {
	pdeDataViewer = new PDEDataViewer();
	pdeDataViewer.setSimulation(getSimulation());
	pdeDataViewer.setPdeDataContext(((PDEDataManager)dataManager).getPDEDataContext());
	return pdeDataViewer;
}

public void dataJobMessage(DataJobEvent dje) {
	getMainViewer().dataJobMessage(dje);
}

/**
 * Method generated to support the promotion of the exportMonitorPanel attribute.
 * @return cbit.vcell.export.ExportMonitorPanel
 */
public ExportMonitorPanel getExportMonitorPanel() {
	return getMainViewer().getExportMonitorPanel();
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:36:17 PM)
 * @return cbit.vcell.client.data.DataViewer
 */
public DataViewer getMainViewer() {
	return mainViewer;
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:36:17 PM)
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getParamChoicesPanel() {
	return paramChoicesPanel;
}

/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:36:17 PM)
 * @return cbit.vcell.solver.Simulation
 */
private Simulation getSimulation() {
	return simulation;
}

private JLabel label = new JLabel("<html><b>Choose Parameter Values</b></html>");
private int localScanProgress;
public void setLocalScanProgress(int localScanProgress) {
	this.localScanProgress = localScanProgress;
	SwingUtilities.invokeLater(new Runnable() {
	@Override
	public void run() {
		label.setText("<html><b>Choose Parameter Values progress:("+(localScanProgress)+"/"+getSimulation().getScanCount()+")</b></html>");
	}});
}
public int getLocalScanProgress() {
	return localScanProgress;
}
/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:37:52 PM)
 * @exception org.vcell.util.DataAccessException The exception description.
 */
private void initialize() throws DataAccessException {
	
	// create main viewer for jobIndex 0 and wire it up
	if (isODEData) {
		setMainViewer(createODEDataViewer());
	} else {
		setMainViewer(createPDEDataViewer());
	}
	java.beans.PropertyChangeListener pcl = new java.beans.PropertyChangeListener() {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SimResultsViewer.this && (evt.getPropertyName().equals("dataViewerManager"))) {
				try {
					getMainViewer().setDataViewerManager(getDataViewerManager());
				} catch (java.beans.PropertyVetoException exc) {
					exc.printStackTrace();
				}
			}
			if (evt.getSource() == SimResultsViewer.this && (evt.getPropertyName().equals("simulationModelInfo"))) {
				getMainViewer().setSimulationModelInfo(getSimulationModelInfo());
			}
		}
	};
	addPropertyChangeListener(pcl);
		
	
	// if necessarry, create parameter choices panel and wire it up
	if (getSimulation().getScanCount() > 1) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(5, 0));
		panel.setBorder(BorderFactory.createEtchedBorder());
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 2));
		panel.add(label, BorderLayout.NORTH);

		String[] scanParams = getSimulation().getMathOverrides().getScannedConstantNames();
		Arrays.sort(scanParams);
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));
		for (int i = 0; i < scanParams.length; i++){
			Constant[] scanConstants = getSimulation().getMathOverrides().getConstantArraySpec(scanParams[i]).getConstants();
			String[][] values = new String[scanConstants.length][1]; 
			for (int j = 0; j < scanConstants.length; j++){
				values[j][0] = scanConstants[j].getExpression().infix();
			}
			class ScanChoicesTableModel extends javax.swing.table.AbstractTableModel {
				String[] columnNames;
				Object[][] rowData;
				ScanChoicesTableModel(Object[][] argData, String[] argNames) {
					columnNames = argNames;
					rowData = argData;
				}
				public String getColumnName(int column) { return columnNames[column].toString(); }
				public int getRowCount() { return rowData.length; }
				public int getColumnCount() { return columnNames.length; }
				public Object getValueAt(int row, int col) { return rowData[row][col]; }
				public boolean isCellEditable(int row, int column) { return false; }
				public void setValueAt(Object value, int row, int col) {
					rowData[row][col] = value;
					fireTableCellUpdated(row, col);
				}
			};
			ScanChoicesTableModel tm = new ScanChoicesTableModel(values, new String[] {scanParams[i]});
			final JTable table = new JTable(tm);
			choicesHash.put(scanParams[i], table);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.getSelectionModel().setSelectionInterval(0,0);
			final ListSelectionListener[] nextListSelectionListener = new ListSelectionListener[1];
			nextListSelectionListener[0] = new javax.swing.event.ListSelectionListener() {
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
					    DefaultListSelectionModel list = (DefaultListSelectionModel)e.getSource();
					    int selected = list.getAnchorSelectionIndex();
					    final int previous = (selected == e.getFirstIndex() ? e.getLastIndex() : e.getFirstIndex());
					    ListReset listReset = new ListReset() {
							@Override
							public void reset(VCDataIdentifier myVcDataIdentifier) {
								if(myVcDataIdentifier instanceof VCSimulationDataIdentifier){
									int paramScanIndex = ((VCSimulationDataIdentifier)myVcDataIdentifier).getJobIndex();
									table.getSelectionModel().removeListSelectionListener(nextListSelectionListener[0]);
									try{
										table.setRowSelectionInterval(paramScanIndex,paramScanIndex);
									}finally{
										table.getSelectionModel().addListSelectionListener(nextListSelectionListener[0]);
									}
								}else{
									table.setRowSelectionInterval(previous, previous);
								}
							}
						};
						updateScanParamChoices("SimResultsViewer set paramScan index="+getSelectedParamScanJobIndex(),listReset);
					}
				}
			};
			table.getSelectionModel().addListSelectionListener(nextListSelectionListener[0]);
			JScrollPane scr = new JScrollPane(table);
			JPanel p = new JPanel();
			scr.setPreferredSize(new java.awt.Dimension (100, Math.min(150, table.getPreferredSize().height + table.getTableHeader().getPreferredSize().height + 5)));
			p.setLayout(new java.awt.BorderLayout());
			p.add(scr, java.awt.BorderLayout.CENTER);
			p.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			tablePanel.add(p);
		}
		panel.add(tablePanel, BorderLayout.CENTER);
				
		if (isODEData) {
			JPanel buttonPanel = new JPanel(new FlowLayout());
			JButton button = new JButton("Time Plot with Multiple Parameter Value Sets");
			buttonPanel.add(button);
			panel.add(buttonPanel, BorderLayout.SOUTH);
			
			button.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					mainViewer.showTimePlotMultipleScans(dataManager);
				}
			});
		} else {
			pdeDataViewer.setSimNameSimDataID(new ExportSpecs.SimNameSimDataID(getSimulation().getName(), getSimulation().getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), SimResultsViewer.getParamScanInfo(getSimulation(), getSelectedParamScanJobIndex())));
		}
		
		setParamChoicesPanel(panel);
		
	}

	// put things together
	setLayout(new java.awt.BorderLayout());
	add(getMainViewer(), java.awt.BorderLayout.CENTER);
	if (getSimulation().getScanCount() > 1) {
		add(getParamChoicesPanel(), java.awt.BorderLayout.SOUTH);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 2:43:49 PM)
 * @exception org.vcell.util.DataAccessException The exception description.
 */
public void refreshData() throws DataAccessException {
	if (isODEData) {
		updateScanParamChoices("SimResultsViewer refreshData (ODE)...",null); // this takes care of all logic to get the fresh data
	} else {
		pdeDataViewer.getPdeDataContext().refreshTimes();
	}
}

public void refreshFunctions() throws DataAccessException {
	if (isODEData) {
		updateScanParamChoices("SimResultsViewer refreshFunctions (ODE)...",null);
	} else {
		// no other reliable way until the PDE context/viewer/manager/dataset furball will be cleaned up... 
		updateScanParamChoices("SimResultsViewer refreshData (PDE)...",null);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:36:17 PM)
 * @param newMainViewer cbit.vcell.client.data.DataViewer
 */
private void setMainViewer(DataViewer newMainViewer) {
	mainViewer = newMainViewer;
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:36:17 PM)
 * @param newParamChoicesPanel javax.swing.JPanel
 */
private void setParamChoicesPanel(javax.swing.JPanel newParamChoicesPanel) {
	paramChoicesPanel = newParamChoicesPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 11:36:17 PM)
 * @param newSimulation cbit.vcell.solver.Simulation
 */
private void setSimulation(Simulation newSimulation) {
	simulation = newSimulation;
}


private int getSelectedParamScanJobIndex(){
	// figure out what job data we are looking for
	final String[] scanConstantNames = getSimulation().getMathOverrides().getScannedConstantNames();
	java.util.Arrays.sort(scanConstantNames);
	int[] indices = new int[scanConstantNames.length];
	int[] bounds = new int[scanConstantNames.length];
	for (int i = 0; i < indices.length; i++){
		indices[i] = choicesHash.get(scanConstantNames[i]).getSelectedRow();
		bounds[i] = getSimulation().getMathOverrides().getConstantArraySpec(scanConstantNames[i]).getNumValues() - 1;
	}
	int selectedJobIndex = -1;
	try {
		selectedJobIndex = BeanUtils.coordinateToIndex(indices, bounds);
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
private void updateScanParamChoices(final String message,ListReset listReset){
	if((paramScanChoiceTimer = ClientTaskDispatcher.getBlockingTimer(this,null,null,paramScanChoiceTimer,new ActionListener() {@Override public void actionPerformed(ActionEvent e2) {updateScanParamChoices(message,listReset);}},message))!=null){
		return;
	}
	
	int selectedJobIndex = getSelectedParamScanJobIndex();
	// update viewer
	if (selectedJobIndex == -1) {
		if (isODEData) {
			if(listReset != null && odeDataViewer != null && odeDataViewer.getVcDataIdentifier() != null){
				listReset.reset(odeDataViewer.getVcDataIdentifier());
			}else{
				odeDataViewer.setOdeSolverResultSet(null);
			}
		} else {
			if(listReset != null && pdeDataViewer != null && pdeDataViewer.getPdeDataContext() != null &&  pdeDataViewer.getPdeDataContext().getVCDataIdentifier() != null){
				listReset.reset(pdeDataViewer.getPdeDataContext().getVCDataIdentifier());
			}else{
				pdeDataViewer.setPdeDataContext(null);
			}
		}
		return;
	}
	
	final VCSimulationDataIdentifier vcdid = new VCSimulationDataIdentifier(getSimulation().getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), selectedJobIndex);
	if (isODEData) {
		AsynchClientTask task1 = new AsynchClientTask("get ode results", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				try {
					ODEDataManager odeDatamanager = ((ODEDataManager)dataManager).createNewODEDataManager(vcdid);
					hashTable.put("odeDatamanager", odeDatamanager);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if(e.getMessage() != null && e.getMessage().contains("no results are available yet")) {
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
					ODEDataManager odeDatamanager = (ODEDataManager)hashTable.get("odeDatamanager");
					odeDataViewer.setOdeSolverResultSet(odeDatamanager.getODESolverResultSet());
					odeDataViewer.setVcDataIdentifier(vcdid);
				} else {
					odeDataViewer.setOdeSolverResultSet(null);
				}
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2});
	} else {
		AsynchClientTask task1 = new AsynchClientTask("get pde results", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				PDEDataManager pdeDatamanager = ((PDEDataManager)dataManager).createNewPDEDataManager(vcdid, null);
				PDEDataContext newPDEDC = pdeDatamanager.getPDEDataContext();
				PDEDataContext oldPDEDC = pdeDataViewer.getPdeDataContext();
				hashTable.put("newPDEDC", newPDEDC);
				if(oldPDEDC != null && oldPDEDC.getTimePoints().length <= newPDEDC.getTimePoints().length){
					DataIdentifier setDid = (newPDEDC.getDataIdentifier()==null?newPDEDC.getDataIdentifiers()[0]:newPDEDC.getDataIdentifier());
					if(Arrays.asList(newPDEDC.getDataIdentifiers()).contains(oldPDEDC.getDataIdentifier())){
						setDid = oldPDEDC.getDataIdentifier();
						newPDEDC.setVariableAndTime(setDid,newPDEDC.getTimePoints()[BeanUtils.firstIndexOf(oldPDEDC.getTimePoints(), oldPDEDC.getTimePoint())]);
					}
				}
			}
		};
		AsynchClientTask task2 = new AsynchClientTask("show results", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				if (hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR) == null) {
					ClientPDEDataContext newPDEDC = (ClientPDEDataContext)hashTable.get("newPDEDC");
					pdeDataViewer.setPdeDataContext(newPDEDC);
					pdeDataViewer.setSimNameSimDataID(new ExportSpecs.SimNameSimDataID(getSimulation().getName(), getSimulation().getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), SimResultsViewer.getParamScanInfo(getSimulation(), vcdid.getJobIndex())));
				}else{
					if(listReset != null && pdeDataViewer != null && pdeDataViewer.getPdeDataContext() != null && pdeDataViewer.getPdeDataContext().getVCDataIdentifier() != null){
						listReset.reset(pdeDataViewer.getPdeDataContext().getVCDataIdentifier());
					}else{
						pdeDataViewer.setPdeDataContext(null);
						pdeDataViewer.setSimNameSimDataID(null);
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
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1,task2/*,refreshTask*/});
	}
}

public static ExportSpecs.ExportParamScanInfo getParamScanInfo(Simulation simulation,int selectedParamScanJobIndex){
	int scanCount = simulation.getScanCount();
	if(scanCount == 1){//no parameter scan
		return null;
	}
	String[] scanConstantNames = simulation.getMathOverrides().getScannedConstantNames();
	Arrays.sort(scanConstantNames);
	int[] paramScanJobIndexes = new int[scanCount];
	String[][] scanConstValues = new String[scanCount][scanConstantNames.length];
	for (int i = 0; i < scanCount; i++) {
		paramScanJobIndexes[i] = i;
		for (int j = 0; j < scanConstantNames.length; j++) {
			String paramScanValue = simulation.getMathOverrides().getActualExpression(scanConstantNames[j], i).infix();
//			System.out.println("ScanIndex="+i+" ScanConstName='"+scanConstantNames[j]+"' paramScanValue="+paramScanValue);
			scanConstValues[i][j] = paramScanValue;
		}
	}
	return new ExportSpecs.ExportParamScanInfo(paramScanJobIndexes, selectedParamScanJobIndex, scanConstantNames, scanConstValues);
}

}
