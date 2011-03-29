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
import org.vcell.util.gui.JDesktopPaneEnhanced;
import org.vcell.util.gui.JInternalFrameEnhanced;


import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.ExportEvent;
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
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.solver.SimulationInfo;

public class FieldDataWindowManager 
	extends TopLevelWindowManager
		implements PropertyChangeListener,FieldDataDBEventListener{
	
	private FieldDataGUIPanel fieldDataGUIPanel;
	private ExternalDataIdentifier currentlyViewedEDI;
	private JFrame currentlyViewedJFrame;
	private PDEDataViewer currentlyViewedPDEDV;
	
	public static abstract class OpenModelInfoHolder{
		public final SimulationInfo simInfo;
		public final int jobIndex;
		//public final boolean isTimeUniform;
		public final boolean isCompartmental;
		protected OpenModelInfoHolder(
				SimulationInfo argSimInfo,
				int argJobIndex,
				//boolean argistu,
				boolean argisc
				){
			simInfo = argSimInfo;
			jobIndex = argJobIndex;
			//isTimeUniform = argistu;
			isCompartmental = argisc;
			
		}
	}
	public static class FDSimMathModelInfo extends OpenModelInfoHolder{
		private Version version;
		private MathDescription mathDescription;
		public FDSimMathModelInfo(
				Version version,
				MathDescription mathDescription,
				SimulationInfo argSI,
				int jobIndex,
				//boolean argistu,
				boolean argisc
				){
			super(argSI,jobIndex,/*argorigin,argextent,argISize,argvariableNames,argtimebounds,argdts,argistu,*/argisc);
			this.version = version;
			this.mathDescription = mathDescription;
		}
		public Version getMathModelVersion(){
			return version;
		}
		public MathDescription getMathDescription(){
			return mathDescription;
		}
	}
	public static class FDSimBioModelInfo extends OpenModelInfoHolder{
		private Version version;
		private SimulationContext simulationContext;
		public FDSimBioModelInfo(
				Version version,
				SimulationContext simulationContext,
				SimulationInfo argSI,
				int jobIndex,
				//boolean argistu,
				boolean argisc
			){
			super(argSI,jobIndex,/*argorigin,argextent,argISize,argvariableNames,argtimebounds,argdts,argistu,*/argisc);
			this.version = version;
			this.simulationContext = simulationContext;
		}
		public Version getBioModelVersion(){
			return version;
		}
		public SimulationContext getSimulationContext(){
			return simulationContext;
		}
	}
	
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
	
	public OpenModelInfoHolder selectOpenModelsFromDesktop(Container c,boolean bIncludeSimulations,String title) throws UserCancelException,DataAccessException{
		try {
			BeanUtils.setCursorThroughout(c, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			OpenModelInfoHolder[] simInfoHolders = getRequestManager().getOpenDesktopDocumentInfos(bIncludeSimulations);
			if(simInfoHolders == null || simInfoHolders.length == 0){
				return null;
			}
			String[] colNames = null;
			if(bIncludeSimulations){
				colNames = new String[] {"Simulation","Scan Index","Model","Type","Application","Owner","Date"};
			}else{
				colNames = new String[] {"Model","Type","Application","Owner","Date"};
			}
			Vector<String[]> rowsV = new Vector<String[]>();
			Vector<OpenModelInfoHolder> simInfoHolderV = new Vector<OpenModelInfoHolder>();
			for(int i=0;i<simInfoHolders.length;i+= 1){
				if(bIncludeSimulations && simInfoHolders[i].isCompartmental){
					continue;//skip, only spatial simInfos are used for Field Data
				}
				String[] rows = new String[colNames.length];
				int colIndex = 0;
				if(simInfoHolders[i] instanceof FDSimMathModelInfo){
					MathModelInfo mmInfo = null;
					if(((FDSimMathModelInfo)simInfoHolders[i]).getMathModelVersion() != null){
						mmInfo = getRequestManager().getDocumentManager().getMathModelInfo(
								((FDSimMathModelInfo)simInfoHolders[i]).getMathModelVersion().getVersionKey());
					}
					if(bIncludeSimulations){
						rows[colIndex++] = simInfoHolders[i].simInfo.getName();
						rows[colIndex++] = simInfoHolders[i].jobIndex+"";
					}
					rows[colIndex++] = (mmInfo==null?"New Document":mmInfo.getVersion().getName());
					rows[colIndex++] = "MathModel";
					rows[colIndex++] = "";
					rows[colIndex++] = (simInfoHolders[i].simInfo==null?"never saved":simInfoHolders[i].simInfo.getOwner().getName());
					rows[colIndex++] = (mmInfo==null?"never saved":mmInfo.getVersion().getDate().toString());
					
				}else if(simInfoHolders[i] instanceof FDSimBioModelInfo){
					BioModelInfo bmInfo = null;
					if(((FDSimBioModelInfo)simInfoHolders[i]).getBioModelVersion() != null){
						bmInfo = getRequestManager().getDocumentManager().getBioModelInfo(
								((FDSimBioModelInfo)simInfoHolders[i]).getBioModelVersion().getVersionKey());
					}
					if(bIncludeSimulations){
						rows[colIndex++] = simInfoHolders[i].simInfo.getName();
						rows[colIndex++] = simInfoHolders[i].jobIndex+"";
					}
					rows[colIndex++] = (bmInfo==null?"New Document":bmInfo.getVersion().getName());
					rows[colIndex++] = "BioModel";
					rows[colIndex++] = ((FDSimBioModelInfo)simInfoHolders[i]).getSimulationContext().getName();
					rows[colIndex++] = (simInfoHolders[i].simInfo==null?"never saved":simInfoHolders[i].simInfo.getOwner().getName());
					rows[colIndex++] = (bmInfo==null?"never saved":bmInfo.getVersion().getDate().toString());
				}
				rowsV.add(rows);
				simInfoHolderV.add(simInfoHolders[i]);
			}
			if(rowsV.size() == 0){
				return null;
			}
			String[][] rows = new String[rowsV.size()][];
			rowsV.copyInto(rows);
			BeanUtils.setCursorThroughout(c, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			int[] selectionIndexArr =  PopupGenerator.showComponentOKCancelTableList(
					getComponent(), title,
					colNames, rows, ListSelectionModel.SINGLE_SELECTION);
			if(selectionIndexArr != null && selectionIndexArr.length > 0){
				return simInfoHolderV.elementAt(selectionIndexArr[0]);//simInfoHolders[selectionIndexArr[0]];
			}
			throw UserCancelException.CANCEL_GENERIC;
		} finally {
			BeanUtils.setCursorThroughout(c, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public PDEDataContext getPDEDataContext(ExternalDataIdentifier eDI) throws DataAccessException{
		return 
			((PDEDataManager)getRequestManager().getDataManager(null, eDI, true)).getPDEDataContext();
	}
public void viewData(final ExternalDataIdentifier eDI){
	
	if(eDI != null && eDI.equals(currentlyViewedEDI)){
		if((currentlyViewedJFrame.getExtendedState() & Frame.ICONIFIED) != 0){
			currentlyViewedJFrame.setExtendedState(
					currentlyViewedJFrame.getExtendedState() & (~Frame.ICONIFIED));
		}
		currentlyViewedJFrame.setVisible(true);
	} else {
		if(currentlyViewedPDEDV != null){
			if(getLocalRequestManager() != null && getLocalRequestManager().getAsynchMessageManager() != null){
				getLocalRequestManager().getAsynchMessageManager().removeDataJobListener(currentlyViewedPDEDV);
			}
			if(currentlyViewedPDEDV.getPdeDataContext() != null){
				currentlyViewedPDEDV.getPdeDataContext().removePropertyChangeListener(this);
			}
		}
		if(currentlyViewedJFrame != null){
			currentlyViewedJFrame.dispose();
		}
		currentlyViewedJFrame = null;
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
					final JDesktopPaneEnhanced jdp = new JDesktopPaneEnhanced();
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
						public void showDataViewerPlotsFrames(javax.swing.JInternalFrame[] plotFrames){
							for(int i=0;i<plotFrames.length;i+= 1){
								plotFrames[i].setLocation(100,100);
								DocumentWindowManager.showFrame(plotFrames[i], jdp);
							}
						}
						public void startExport(
								OutputContext outputContext,ExportSpecs exportSpecs){
						}
						public void simStatusChanged(SimStatusEvent simStatusEvent) {
						}
						public User getUser() {
							return getRequestManager().getDocumentManager().getUser();
						}
					};
				
					try {
						currentlyViewedPDEDV.setDataViewerManager(dvm);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
					
					JInternalFrameEnhanced jif = new JInternalFrameEnhanced("Field Data", true, false, true, true);
					jif.setContentPane(currentlyViewedPDEDV);
					jif.setSize(600,500);
					jif.setClosable(false);
					jif.setVisible(true);
					
					jdp.add(jif);
		
					JFrame jFrame = new JFrame("Field Data Viewer ("+eDI.getName()+")");
					jFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					jFrame.setContentPane(jdp);
					jFrame.setSize(650, 550);
					jFrame.setVisible(true);
					
					currentlyViewedJFrame = jFrame;
					currentlyViewedEDI = eDI;
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
