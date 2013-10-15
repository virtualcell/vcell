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

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.ListSelectionModel;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.ReferenceQueryResult;
import org.vcell.util.document.ReferenceQuerySpec;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionableRelationship;
import org.vcell.util.document.VersionableType;
import org.vcell.util.document.VersionableTypeVersion;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.ExportEvent;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.data.OutputContext;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.client.server.PDEDataManager;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.controls.DataListener;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.field.FieldDataDBEvent;
import cbit.vcell.field.FieldDataDBEventListener;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.field.FieldDataFileOperationResults;
import cbit.vcell.field.FieldDataGUIPanel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.solver.SimulationInfo;

public class FieldDataWindowManager 
	extends TopLevelWindowManager
		implements PropertyChangeListener,FieldDataDBEventListener{
	
	public interface DataSymbolCallBack {
		void createDataSymbol(ExternalDataIdentifier dataSetID,String fieldDataVarName,VariableType fieldDataVarType,double fieldDataVarTime);
	}

	private FieldDataGUIPanel fieldDataGUIPanel;
	private ExternalDataIdentifier currentlyViewedEDI;
	private PDEDataViewer currentlyViewedPDEDV;
	
	
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
	
	
	public PDEDataContext getPDEDataContext(ExternalDataIdentifier eDI) throws DataAccessException{
		return 
			((PDEDataManager)getRequestManager().getDataManager(null, eDI, true)).getPDEDataContext();
	}
	
public void viewData(final ExternalDataIdentifier eDI){
	
	ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(getComponent());
	if(eDI != null && eDI.equals(currentlyViewedEDI) && childWindowManager != null && childWindowManager.getChildWindowFromContext(eDI) != null){
		childWindowManager.getChildWindowFromContext(eDI).show();
	} else {
		if(currentlyViewedPDEDV != null){
			if(getLocalRequestManager() != null && getLocalRequestManager().getAsynchMessageManager() != null){
				getLocalRequestManager().getAsynchMessageManager().removeDataJobListener(currentlyViewedPDEDV);
			}
			if(currentlyViewedPDEDV.getPdeDataContext() != null){
				currentlyViewedPDEDV.getPdeDataContext().removePropertyChangeListener(this);
			}
		}
		if(currentlyViewedPDEDV != null){
			ChildWindow childWindow = childWindowManager.getChildWindowFromContext(eDI);
			if (childWindow!=null){
				childWindow.close();
			}
		}
		currentlyViewedEDI = null;
		currentlyViewedPDEDV = null;
		if(eDI == null){
			return;
		}
		
		AsynchClientTask task1 = new AsynchClientTask("retrieve data", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				PDEDataContext newPDEDataContext = getPDEDataContext(eDI);
				hashTable.put("newPDEDataContext", newPDEDataContext);
			}				
		};
		AsynchClientTask task2 = new AsynchClientTask("show data", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {				
				try{
					currentlyViewedPDEDV = new PDEDataViewer();
					PDEDataContext newPDEDataContext = (PDEDataContext)hashTable.get("newPDEDataContext");
					currentlyViewedPDEDV.setPdeDataContext(newPDEDataContext);
					newPDEDataContext.addPropertyChangeListener(FieldDataWindowManager.this);
					getLocalRequestManager().getAsynchMessageManager().addDataJobListener(currentlyViewedPDEDV);
					
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
						public void startExport(
								OutputContext outputContext,ExportSpecs exportSpecs){
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
					ChildWindow childWindow = childWindowManager.addChildWindow(currentlyViewedPDEDV, currentlyViewedEDI, "Field Data Viewer ("+eDI.getName()+")");
					childWindow.setSize(600,500);
					childWindow.setIsCenteredOnParent();
					childWindow.show();

				} catch (Exception e){
					if(currentlyViewedPDEDV != null){
						if(getLocalRequestManager() != null && getLocalRequestManager().getAsynchMessageManager() != null){
							getLocalRequestManager().getAsynchMessageManager().removeDataJobListener(currentlyViewedPDEDV);
						}
						if(currentlyViewedPDEDV.getPdeDataContext() != null){
							currentlyViewedPDEDV.getPdeDataContext().removePropertyChangeListener(FieldDataWindowManager.this);
						}
					}
					throw e;
				}
			}
		};
		ClientTaskDispatcher.dispatch(this.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, false);
	}
}
public void propertyChange(PropertyChangeEvent evt) {
	if(evt.getSource() == currentlyViewedPDEDV.getPdeDataContext() &&
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
