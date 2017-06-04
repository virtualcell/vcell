/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import org.vcell.util.DataAccessException;
import org.vcell.util.DataJobListenerHolder;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.ReferenceQueryResult;
import org.vcell.util.document.ReferenceQuerySpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionableRelationship;
import org.vcell.util.document.VersionableType;
import org.vcell.util.document.VersionableTypeVersion;

import cbit.image.VCImageUncompressed;
import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.ExportEvent;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.client.data.SimulationModelInfo;
import cbit.vcell.client.data.SimulationWorkspaceModelInfo;
import cbit.vcell.client.desktop.biomodel.DocumentEditor;
import cbit.vcell.client.desktop.simulation.OutputFunctionsPanel;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.desktop.simulation.SimulationWorkspace;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.server.SimStatusEvent;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.ExportSpecs.SimNameSimDataID;
import cbit.vcell.field.db.FieldDataDBOperationSpec;
import cbit.vcell.field.gui.FieldDataDBEvent;
import cbit.vcell.field.gui.FieldDataDBEventListener;
import cbit.vcell.field.gui.FieldDataGUIPanel;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VolVariable;
import cbit.vcell.simdata.ClientPDEDataContext;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataListener;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.OutputFunctionContext;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMesh;

public class FieldDataWindowManager 
	extends TopLevelWindowManager
		implements PropertyChangeListener,FieldDataDBEventListener,DataJobListenerHolder{
	
	public interface DataSymbolCallBack {
		void createDataSymbol(ExternalDataIdentifier dataSetID,String fieldDataVarName,VariableType fieldDataVarType,double fieldDataVarTime);
	}

	private FieldDataGUIPanel fieldDataGUIPanel;
	private ExternalDataIdentifier currentlyViewedEDI;
	private OutputFunctionViewer currentlyViewedOutputFunctionViewer;
	
	
	public FieldDataWindowManager(FieldDataGUIPanel fdgp, RequestManager requestManager){
		super(requestManager);
		fieldDataGUIPanel = fdgp;
		requestManager.getDocumentManager().addFieldDataDBListener(this);
	}
	public FieldDataGUIPanel getFieldDataGUIPanel(){
		return fieldDataGUIPanel;
	}
	@Override
	public Component getComponent() {
		return fieldDataGUIPanel;
	}

	@Override
	public String getManagerID() {
		return ClientMDIManager.FIELDDATA_WINDOW_ID;
	}

	@Override
	public boolean isRecyclable() {
		return true;
	}
	
	public void deleteExternalDataIdentifier(ExternalDataIdentifier deleteExtDataID) throws DataAccessException{
		getRequestManager().getDocumentManager().fieldDataDBOperation(
				FieldDataDBOperationSpec.createDeleteExtDataIDSpec(deleteExtDataID));
		if(deleteExtDataID.equals(currentlyViewedEDI)){
			viewData(null);
		}
	}
	public RequestManager getLocalRequestManager(){
		return getRequestManager();
	}
	public void updateJTree(){
		fieldDataGUIPanel.updateJTree(getRequestManager());
	}
	
	
	public ClientPDEDataContext getPDEDataContext(ExternalDataIdentifier eDI,OutputContext outputContext) throws DataAccessException{
		return 
			((PDEDataManager)getRequestManager().getDataManager(outputContext, eDI, true)).getPDEDataContext();
	}
	
public void viewData(final ExternalDataIdentifier eDI){
	
	ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(getComponent());
	if(eDI != null && eDI.equals(currentlyViewedEDI) && childWindowManager != null && childWindowManager.getChildWindowFromContext(eDI) != null){
		childWindowManager.getChildWindowFromContext(eDI).show();
	} else {
		if(currentlyViewedOutputFunctionViewer != null){
			if(getLocalRequestManager() != null && getLocalRequestManager().getAsynchMessageManager() != null){
				getLocalRequestManager().getAsynchMessageManager().removeDataJobListener(currentlyViewedOutputFunctionViewer.getPDEDataViewer());
			}
			if(currentlyViewedOutputFunctionViewer != null){
				currentlyViewedOutputFunctionViewer.getPDEDataViewer().getPdeDataContext().removePropertyChangeListener(this);
			}
		}
		if(currentlyViewedOutputFunctionViewer != null){
			ChildWindow childWindow = childWindowManager.getChildWindowFromContext(eDI);
			if (childWindow!=null){
				childWindow.close();
			}
		}
		currentlyViewedEDI = null;
		currentlyViewedOutputFunctionViewer = null;
		if(eDI == null){
			return;
		}
		
		AsynchClientTask task1 = new AsynchClientTask("retrieve data", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				ClientPDEDataContext newPDEDataContext = getPDEDataContext(eDI,new OutputContext(new AnnotatedFunction[0])/*(currentlyViewedOutputFunctionViewer==null?null:currentlyViewedOutputFunctionViewer.getOutputContext())*/);
				hashTable.put("newPDEDataContext", newPDEDataContext);
			}				
		};
		AsynchClientTask task2 = new AsynchClientTask("show data", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {				
				try{
					PDEDataViewer currentlyViewedPDEDV = new PDEDataViewer();
					ClientPDEDataContext newPDEDataContext = (ClientPDEDataContext)hashTable.get("newPDEDataContext");
					currentlyViewedPDEDV.setPdeDataContext(newPDEDataContext);
					newPDEDataContext.addPropertyChangeListener(FieldDataWindowManager.this);
					getLocalRequestManager().getAsynchMessageManager().addDataJobListener(currentlyViewedPDEDV);
					currentlyViewedOutputFunctionViewer = new OutputFunctionViewer(currentlyViewedPDEDV, FieldDataWindowManager.this,eDI);
					
					DataViewerManager dvm = new DataViewerManager(){
						public void dataJobMessage(DataJobEvent event){
						}
						public void exportMessage(ExportEvent event){
						}
						public void addDataListener(DataListener newListener){
						}
						public UserPreferences getUserPreferences(){
							return getRequestManager().getUserPreferences();
						}
						public void removeDataListener(DataListener newListener){
						}
						public void startExport(OutputContext outputContext,ExportSpecs exportSpecs){
							getLocalRequestManager().startExport(outputContext, FieldDataWindowManager.this, exportSpecs);
						}
						public void simStatusChanged(SimStatusEvent simStatusEvent) {
						}
						public User getUser() {
							return getRequestManager().getDocumentManager().getUser();
						}
						public RequestManager getRequestManager() {
							return FieldDataWindowManager.this.getRequestManager();
						}
					};
				
					try {
						currentlyViewedPDEDV.setDataViewerManager(dvm);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
					
					ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(getComponent());
					currentlyViewedEDI = eDI;
					ChildWindow childWindow = childWindowManager.addChildWindow(currentlyViewedOutputFunctionViewer, currentlyViewedEDI, "Field Data Viewer ("+eDI.getName()+")");
					childWindow.setSize(600,500);
					childWindow.setIsCenteredOnParent();
					childWindow.show();

				} catch (Exception e){
					if(currentlyViewedOutputFunctionViewer != null){
						if(getLocalRequestManager() != null && getLocalRequestManager().getAsynchMessageManager() != null){
							getLocalRequestManager().getAsynchMessageManager().removeDataJobListener(currentlyViewedOutputFunctionViewer.getPDEDataViewer());
						}
						if(currentlyViewedOutputFunctionViewer != null){
							currentlyViewedOutputFunctionViewer.getPDEDataViewer().getPdeDataContext().removePropertyChangeListener(FieldDataWindowManager.this);
						}
					}
					throw e;
				}
			}
		};
		ClientTaskDispatcher.dispatch(this.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, false);
	}
}
private static class OutputFunctionViewer extends JPanel{
	private PDEDataViewer pdeDataViewer;
	private OutputFunctionsPanel outputFunctionsPanel;
	private Geometry geom;
	private MathDescription mathDescription;
	private ExternalDataIdentifier edi;
	private FieldDataWindowManager fieldDataWindowManager;
	public OutputFunctionViewer(final PDEDataViewer pdeDataViewer,FieldDataWindowManager fieldDataWindowManager,ExternalDataIdentifier edi) throws Exception{
		this.pdeDataViewer = pdeDataViewer;
		this.edi = edi;
		this.fieldDataWindowManager = fieldDataWindowManager;
		this.outputFunctionsPanel = new OutputFunctionsPanel();
		CartesianMesh cartesianMesh = pdeDataViewer.getPdeDataContext().getCartesianMesh();
		VCImageUncompressed vcImage = new VCImageUncompressed(null, new byte[cartesianMesh.getNumVolumeElements()], cartesianMesh.getExtent(), cartesianMesh.getSizeX(),cartesianMesh.getSizeY(),cartesianMesh.getSizeZ());
		this.geom = new Geometry("temp",vcImage);
		this.mathDescription = new MathDescription("temp");
		mathDescription.setGeometry(geom);
		setMathDescVariables();
		this.pdeDataViewer.setSimNameSimDataID(new SimNameSimDataID("temp", new VCSimulationIdentifier(edi.getKey(), edi.getOwner()),null));
		this.pdeDataViewer.setSimulationModelInfo(new SimulationModelInfo() {
			
			@Override
			public String getVolumeNamePhysiology(int subVolumeID) {
				// TODO Auto-generated method stub
				return "volPhysiology";
			}
			
			@Override
			public String getVolumeNameGeometry(int subVolumeID) {
				// TODO Auto-generated method stub
				return "volGeometry";
			}
			
			@Override
			public String getSimulationName() {
				// TODO Auto-generated method stub
				return "simName";
			}
			
			@Override
			public String getMembraneName(int subVolumeIdIn, int subVolumeIdOut, boolean bFromGeometry) {
				// TODO Auto-generated method stub
				return "membraneName";
			}
			
			@Override
			public String getContextName() {
				// TODO Auto-generated method stub
				return "contextName";
			}

			@Override
			public DataSymbolMetadataResolver getDataSymbolMetadataResolver(){
				return pdeDataViewer.getSimulationModelInfo().getDataSymbolMetadataResolver();
			}
		});
		
		SimulationOwner simulationOwner = new SimulationOwner() {
			private OutputFunctionContext outputFunctionContext = new OutputFunctionContext(this);
			{
				outputFunctionContext.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						final String PDEDC_KEY = "PDEDC_KEY";
						AsynchClientTask task1 = new AsynchClientTask("getPDEDataContext...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
							@Override
							public void run(Hashtable<String, Object> hashTable) throws Exception {
								OutputContext outputContext = OutputFunctionViewer.this.getOutputContext();
								ClientPDEDataContext pdeDataContext = OutputFunctionViewer.this.fieldDataWindowManager.getPDEDataContext(OutputFunctionViewer.this.edi,outputContext);
								hashTable.put(PDEDC_KEY, pdeDataContext);
							}
						};
						AsynchClientTask task2 = new AsynchClientTask("setPDEDataContext...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
							@Override
							public void run(Hashtable<String, Object> hashTable) throws Exception {
								OutputFunctionViewer.this.pdeDataViewer.setPdeDataContext((ClientPDEDataContext)hashTable.get(PDEDC_KEY));
								setMathDescVariables();
							}
						};
						ClientTaskDispatcher.dispatch(OutputFunctionViewer.this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1,task2},true,false,false,null,true);
					}
				});
			}
			
			@Override
			public void removePropertyChangeListener(PropertyChangeListener listener) {
				// TODO Auto-generated method stub
				System.out.println("somebody remove mysimowner listener "+listener);
			}
			
			@Override
			public Geometry getGeometry() {
				// TODO Auto-generated method stub
				return geom;
			}
			
			@Override
			public void addPropertyChangeListener(PropertyChangeListener listener) {
				// TODO Auto-generated method stub
				System.out.println("somebody add mysimowner listener "+listener);
				
			}
			
			@Override
			public void removeSimulation(Simulation simulation)
					throws PropertyVetoException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Simulation[] getSimulations() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public OutputFunctionContext getOutputFunctionContext() {
				// TODO Auto-generated method stub
				return outputFunctionContext;
			}
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "temp";
			}
			
			@Override
			public MathDescription getMathDescription() {
				// TODO Auto-generated method stub
				return mathDescription;
			}
			
			@Override
			public Simulation copySimulation(Simulation simulation)
					throws PropertyVetoException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Issue gatherIssueForMathOverride(IssueContext issueContext, Simulation simulation, String name) {
				// TODO Auto-generated method stub
				return null;
			}

			/**
			 * @throws UnsupportedOperationException (always)
			 */
			@Override
			public UnitInfo getUnitInfo() throws UnsupportedOperationException {
				throw new UnsupportedOperationException();
			}
		};
		VCDocument fakeVCDocument = new VCDocument(){

			@Override
			public boolean compareEqual(Matchable obj) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public VCDocumentType getDocumentType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Version getVersion() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void refreshDependencies() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setDescription(String description)
					throws PropertyVetoException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setName(String newName) throws PropertyVetoException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
				// TODO Auto-generated method stub
				
			}
			
		};
		DocumentWindowManager documentWindowManager = new DocumentWindowManager(pdeDataViewer, fieldDataWindowManager.getRequestManager(), fakeVCDocument){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void simStatusChanged(SimStatusEvent simStatusEvent) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void addResultsFrame(SimulationWindow simWindow) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public VCDocument getVCDocument() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			SimulationWindow haveSimulationWindow(
					VCSimulationIdentifier vcSimulationIdentifier) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void resetDocument(VCDocument newDocument) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateConnectionStatus(ConnectionStatus connStatus) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean isRecyclable() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public RequestManager getRequestManager() {
				// TODO Auto-generated method stub
				return OutputFunctionViewer.this.fieldDataWindowManager.getRequestManager();
			}

			@Override
			public DocumentEditor getDocumentEditor() {
				return null;
			}
			
		};
		
		SimulationWorkspace simulationWorkspace = new SimulationWorkspace(documentWindowManager, simulationOwner);
		this.pdeDataViewer.setSimulationModelInfo(new SimulationWorkspaceModelInfo(simulationOwner, "temp"));

		setLayout(new BorderLayout());
		add(pdeDataViewer,BorderLayout.CENTER);
		add(outputFunctionsPanel,BorderLayout.SOUTH);
		outputFunctionsPanel.setSimulationWorkspace(simulationWorkspace);
	}
	public PDEDataViewer getPDEDataViewer(){
		return pdeDataViewer;
	}
	public OutputContext getOutputContext() throws Exception{
		Field outputfunctioncontextField = OutputFunctionsPanel.class.getDeclaredField("outputFunctionContext");
		outputfunctioncontextField.setAccessible(true);
		OutputFunctionContext outputFunctionContext = (OutputFunctionContext)outputfunctioncontextField.get(outputFunctionsPanel);
		OutputContext outputContext = new OutputContext(outputFunctionContext.getOutputFunctionsList().toArray(new AnnotatedFunction[0]));
		return outputContext;
	}
	private void setMathDescVariables() throws Exception{
		AnnotatedFunction[] functions = this.pdeDataViewer.getPdeDataContext().getFunctions();
		for (int i = 0; i < functions.length; i++) {
			if(functions[i].getFunctionType().equals(VariableType.VOLUME) && mathDescription.getVariable(functions[i].getName()) == null){
				mathDescription.addVariable(functions[i]);
			}
		}
		DataIdentifier[] dataIds = this.pdeDataViewer.getPdeDataContext().getDataIdentifiers();
		for (int i = 0; i < dataIds.length; i++) {
			if(dataIds[i].getVariableType().equals(VariableType.VOLUME) && mathDescription.getVariable(dataIds[i].getName()) == null){
				if(!dataIds[i].isFunction()){
					mathDescription.addVariable(new VolVariable(dataIds[i].getName(), dataIds[i].getDomain()));
				}
			}
		}
	}
}

public void propertyChange(PropertyChangeEvent evt) {
	if(evt.getSource() == currentlyViewedOutputFunctionViewer.getPDEDataViewer().getPdeDataContext() &&
			(
					evt.getPropertyName().equals(PDEDataContext.PROP_CHANGE_FUNC_ADDED) ||
					evt.getPropertyName().equals(PDEDataContext.PROP_CHANGE_FUNC_REMOVED)
			)){
			getFieldDataGUIPanel().refreshExternalDataIdentifierNode(currentlyViewedEDI);
		}
}


public boolean findReferencingModels(final ExternalDataIdentifier targetExtDataID,boolean bShowReferencingModelsList)
	throws DataAccessException,UserCancelException{

	ReferenceQuerySpec rqs = new ReferenceQuerySpec(targetExtDataID);

	ReferenceQueryResult rqr = getRequestManager().getDocumentManager().findReferences(rqs);
	VersionableTypeVersion[] dependants = null;
	Hashtable<VersionableTypeVersion,String[]> choices = new Hashtable<VersionableTypeVersion,String[]>();
	boolean bDanglingReferences = false;
	
	VersionableType bioModelType = VersionableType.BioModelMetaData;
	VersionableType mathModelType = VersionableType.MathModelMetaData;
	
	if(rqr != null){
		dependants = (rqr.getVersionableFamily().bDependants()?rqr.getVersionableFamily().getUniqueDependants():null);
		if(dependants != null){
			for(int i=0;i<dependants.length;i+= 1){
				boolean isBioModel = dependants[i].getVType().equals(bioModelType);
				boolean isTop = isBioModel || dependants[i].getVType().equals(mathModelType);
				if(isTop){
					VersionableRelationship[] vrArr2 = rqr.getVersionableFamily().getDependantRelationships();
					for(int j=0;j<vrArr2.length;j+= 1){
						if( (vrArr2[j].from() == dependants[i]) &&
								vrArr2[j].to().getVType().equals((isBioModel?VersionableType.SimulationContext:VersionableType.MathDescription))){
							for(int k=0;k<vrArr2.length;k+= 1){
								boolean bAdd =false;
								if(k==j && vrArr2[k].from().getVType().equals(mathModelType)){
									bAdd = true;
								}
								if((vrArr2[k].from() == vrArr2[j].to()) &&
									vrArr2[k].to().getVType().equals(VersionableType.MathDescription)){
									bAdd = true;
								}
								if(bAdd){
									choices.put(dependants[i],
											new String[] {
												dependants[i].getVersion().getName(),
												(isBioModel?bioModelType.getTypeName():mathModelType.getTypeName()),
												(isBioModel?vrArr2[k].from().getVersion().getName():""),
												dependants[i].getVersion().getVersionKey().toString()
											}
									);
								}
							}
						}
					}
				}
			}
			bDanglingReferences = (choices.size() == 0);
		}else{
			bDanglingReferences = true;
		}
	}
			
//	FieldDataFileOperationResults fdfor = getRequestManager().getDocumentManager().fieldDataFileOperation(
//			FieldDataFileOperationSpec.createDependantFuncsFieldDataFileOperationSpec(targetExtDataID));	
	FieldDataFileOperationResults fdfor = null;
	
	boolean bHasReferences = false;
	if(choices.size() > 0 || fdfor != null){
		bHasReferences = true;
		if(bShowReferencingModelsList){
			String[] columnNames = new String[] {"Model","Type","Description"};
			Vector<VersionableType> varTypeV = new Vector<VersionableType>();
			Vector<KeyValue> keyValV = new Vector<KeyValue>();
			Vector<String[]> choicesV= new Vector<String[]>();
			String[][] modelListData = choices.values().toArray(new String[0][0]);
			for (int i = 0; i < modelListData.length; i++) {
				choicesV.add(new String[]{modelListData[i][0], modelListData[i][1],
						"Model Variable - "+(modelListData[i][2].length() == 0?"":"App='"+modelListData[i][2]+"'")+" version["+modelListData[i][3]+"]"
					}
				);
				varTypeV.add((modelListData[i][1].equals(bioModelType.getTypeName())?bioModelType:mathModelType));
				keyValV.add(new KeyValue(modelListData[i][3]));
			}
			for (int i = 0; fdfor != null && i < fdfor.dependantFunctionInfo.length; i++) {
				String functionNames = "";
				for (int j = 0; j < fdfor.dependantFunctionInfo[i].funcNames.length; j++) {
					functionNames+=(j>0?",":"")+fdfor.dependantFunctionInfo[i].funcNames[j];
				}
				choicesV.add(new String[] {
						fdfor.dependantFunctionInfo[i].referenceSourceName,
						fdfor.dependantFunctionInfo[i].referenceSourceType,
						"Data Viewer Function(s) '"+functionNames+"' - "+
							(fdfor.dependantFunctionInfo[i].applicationName == null?"":"App='"+fdfor.dependantFunctionInfo[i].applicationName+"' ")+
							(fdfor.dependantFunctionInfo[i].simulationName == null?"":"Sim='"+fdfor.dependantFunctionInfo[i].simulationName+"' ")+
							"version["+fdfor.dependantFunctionInfo[i].refSourceVersionDate+"]"
					}
				);				
				if(fdfor.dependantFunctionInfo[i].referenceSourceType.equals(FieldDataFileOperationResults.FieldDataReferenceInfo.FIELDDATATYPENAME)){
					varTypeV.add(null);
				}else if(fdfor.dependantFunctionInfo[i].referenceSourceType.equals(bioModelType.getTypeName())){
					varTypeV.add(bioModelType);
				}else if(fdfor.dependantFunctionInfo[i].referenceSourceType.equals(mathModelType.getTypeName())){
					varTypeV.add(mathModelType);
				}else{
					throw new IllegalArgumentException("Unknown reference source type "+fdfor.dependantFunctionInfo[i].referenceSourceType);
				}
				keyValV.add(fdfor.dependantFunctionInfo[i].refSourceVersionKey);
			}
			int[] selectionArr = PopupGenerator.showComponentOKCancelTableList(
					getComponent(), "References to Field Data (Select To Open) "+targetExtDataID.getName(),
					columnNames, choicesV.toArray(new String[0][0]), ListSelectionModel.SINGLE_SELECTION);
			if(selectionArr != null && selectionArr.length > 0){
				if(varTypeV.elementAt(selectionArr[0]) != null){
					if(varTypeV.elementAt(selectionArr[0]).equals(bioModelType)){
						BioModelInfo bmi = getRequestManager().getDocumentManager().getBioModelInfo(keyValV.elementAt(selectionArr[0]));
						getRequestManager().openDocument(bmi,FieldDataWindowManager.this,true);
					}else if(varTypeV.elementAt(selectionArr[0]).equals(mathModelType)){
						MathModelInfo mmi = getRequestManager().getDocumentManager().getMathModelInfo(keyValV.elementAt(selectionArr[0]));
						getRequestManager().openDocument(mmi,FieldDataWindowManager.this,true);
					}else{
						throw new IllegalArgumentException("Not expecting varType "+varTypeV.elementAt(selectionArr[0]));
					}
				}else{
					PopupGenerator.showInfoDialog(this, "use FiledDataManager to view FieldData '" + choicesV.elementAt(selectionArr[0])[0]+"'");
				}
			}
		}
	}else{
		if(!bDanglingReferences){
			bHasReferences = false;
			if(bShowReferencingModelsList){
				PopupGenerator.showInfoDialog(this, "No Model references found for Field Data "+targetExtDataID.getName());
			}
		}else{
			bHasReferences = true;
			if(bShowReferencingModelsList){
				PopupGenerator.showInfoDialog(this, "No current Model references found.\n"+
					"Field Data has internal database references from\n"+
					"previously linked Model(s) that have been deleted.\n"+
					"Note: Field Data '"+targetExtDataID.getName()+"' is not deletable\n"+
					"until database is culled (daily).");
			}
		}
	}
	return bHasReferences;
}
public void fieldDataDBEvent(FieldDataDBEvent fieldDataDBEvent) {
	updateJTree();
	
}
}
