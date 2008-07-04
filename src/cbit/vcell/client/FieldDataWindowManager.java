package cbit.vcell.client;

import java.awt.Component;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;

import cbit.gui.DialogUtils;
import cbit.gui.JInternalFrameEnhanced;
import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.ExportEvent;
import cbit.sql.KeyValue;
import cbit.sql.VersionableType;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.client.server.PDEDataManager;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.UserCancelException;
import cbit.vcell.desktop.controls.DataListener;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.field.FieldDataDBEvent;
import cbit.vcell.field.FieldDataDBEventListener;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.field.FieldDataFileOperationResults;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.field.FieldDataGUIPanel;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.vcell.model.ModelInfo;
import cbit.vcell.modeldb.VersionableTypeVersion;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.server.User;
import cbit.vcell.simdata.ExternalDataIdentifier;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.SimulationInfo;

public class FieldDataWindowManager 
	extends TopLevelWindowManager
		implements PropertyChangeListener,FieldDataDBEventListener{
	
	private FieldDataGUIPanel fieldDataGUIPanel;
	private ExternalDataIdentifier currentlyViewedEDI;
	private JFrame currentlyViewedJFrame;
	private PDEDataViewer currentlyViewedPDEDV;
	
	public static abstract class SimInfoHolder{
		public final SimulationInfo simInfo;
		public final int jobIndex;
		public final boolean isTimeUniform;
		public final boolean isCompartmental;
		protected SimInfoHolder(
				SimulationInfo argSimInfo,
				int argJobIndex,
				boolean argistu,
				boolean argisc
				){
			simInfo = argSimInfo;
			jobIndex = argJobIndex;
			isTimeUniform = argistu;
			isCompartmental = argisc;
			
		}
	}
	public static class FDSimMathModelInfo extends SimInfoHolder{
		private KeyValue mathModelKey;
		public FDSimMathModelInfo(
				KeyValue mmK,
				SimulationInfo argSI,
				int jobIndex,
				boolean argistu,
				boolean argisc
				){
			super(argSI,jobIndex,/*argorigin,argextent,argISize,argvariableNames,argtimebounds,argdts,*/argistu,argisc);
			mathModelKey = mmK;
		}
		public KeyValue getMathModelKey(){
			return mathModelKey;
		}
	}
	public static class FDSimBioModelInfo extends SimInfoHolder{
		private KeyValue bioModelKey;
		private String simulationContextName;
		public FDSimBioModelInfo(
				KeyValue bmK,String scName,
				SimulationInfo argSI,
				int jobIndex,
				boolean argistu,
				boolean argisc
			){
			super(argSI,jobIndex,/*argorigin,argextent,argISize,argvariableNames,argtimebounds,argdts,*/argistu,argisc);
			bioModelKey = bmK;
			simulationContextName = scName;
		}
		public KeyValue getBioModelKey(){
			return bioModelKey;
		}
		public String getSimulationContextName(){
			return simulationContextName;
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
	Component getComponent() {
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
	
	public SimInfoHolder selectSimulationFromDesktop() throws UserCancelException,DataAccessException{
		SimInfoHolder[] simInfoHolders =
			getRequestManager().getOpenDesktopDocumentInfos();
		if(simInfoHolders == null || simInfoHolders.length == 0){
			return null;
		}
		String[] colNames = new String[] {"Simulation","Scan Index","Model","Type","Application","Owner","Date"};
		Vector<String[]> rowsV = new Vector<String[]>();
		Vector<SimInfoHolder> simInfoHolderV = new Vector<SimInfoHolder>();
		for(int i=0;i<simInfoHolders.length;i+= 1){
			if(simInfoHolders[i].isCompartmental){
				continue;//skip, only spatial simInfos are used for Field Data
			}
			String[] rows = new String[colNames.length];
			if(simInfoHolders[i] instanceof FDSimMathModelInfo){
				MathModelInfo mmInfo =
					getRequestManager().getDocumentManager().getMathModelInfo(
							((FDSimMathModelInfo)simInfoHolders[i]).getMathModelKey());
				rows[0] = simInfoHolders[i].simInfo.getName();
				rows[1] = simInfoHolders[i].jobIndex+"";
				rows[2] = mmInfo.getVersion().getName();
				rows[3] = "MathModel";
				rows[4] = "";
				rows[5] = simInfoHolders[i].simInfo.getOwner().getName();
				rows[6] = mmInfo.getVersion().getDate().toString();
				
			}else if(simInfoHolders[i] instanceof FDSimBioModelInfo){
				BioModelInfo bmInfo =
					getRequestManager().getDocumentManager().getBioModelInfo(
							((FDSimBioModelInfo)simInfoHolders[i]).getBioModelKey());					
				rows[0] = simInfoHolders[i].simInfo.getName();
				rows[1] = simInfoHolders[i].jobIndex+"";
				rows[2] = bmInfo.getVersion().getName();
				rows[3] = "BioModel";
				rows[4] = ((FDSimBioModelInfo)simInfoHolders[i]).getSimulationContextName();
				rows[5] = simInfoHolders[i].simInfo.getOwner().getName();
				rows[6] = bmInfo.getVersion().getDate().toString();
			}
			rowsV.add(rows);
			simInfoHolderV.add(simInfoHolders[i]);
		}
		if(rowsV.size() == 0){
			return null;
		}
		String[][] rows = new String[rowsV.size()][];
		rowsV.copyInto(rows);
		int[] selectionIndexArr =  PopupGenerator.showComponentOKCancelTableList(
				getComponent(), "Select Simulation for Field Data",
				colNames, rows, ListSelectionModel.SINGLE_SELECTION);
		if(selectionIndexArr != null && selectionIndexArr.length > 0){
			return simInfoHolderV.elementAt(selectionIndexArr[0]);//simInfoHolders[selectionIndexArr[0]];
		}
		throw UserCancelException.CANCEL_GENERIC;
	}
	
	public PDEDataContext getPDEDataContext(ExternalDataIdentifier eDI) throws DataAccessException{
		return 
			((PDEDataManager)getRequestManager().getDataManager(eDI, true)).getPDEDataContext();
	}
public void viewData(final ExternalDataIdentifier eDI){
	
	if(eDI != null && eDI.equals(currentlyViewedEDI)){
		if((currentlyViewedJFrame.getExtendedState() & Frame.ICONIFIED) != 0){
			currentlyViewedJFrame.setExtendedState(
					currentlyViewedJFrame.getExtendedState() & (~Frame.ICONIFIED));
		}
		currentlyViewedJFrame.setVisible(true);
	}else{
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
		try{
			final JDesktopPane jdp = new JDesktopPane();
		
			currentlyViewedPDEDV = new PDEDataViewer();
//			PDEDataManager pdeDatamanager =
//				(PDEDataManager)getRequestManager().getDataManager(eDI, true);
//			PDEDataContext newPDEDataContext = pdeDatamanager.getPDEDataContext();
			PDEDataContext newPDEDataContext = getPDEDataContext(eDI);
			currentlyViewedPDEDV.setPdeDataContext(newPDEDataContext);
			newPDEDataContext.addPropertyChangeListener(this);
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
				public void startExport(ExportSpecs exportSpecs){
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
		}catch(Throwable e){
			if(currentlyViewedPDEDV != null){
				if(getLocalRequestManager() != null && getLocalRequestManager().getAsynchMessageManager() != null){
					getLocalRequestManager().getAsynchMessageManager().removeDataJobListener(currentlyViewedPDEDV);
				}
				if(currentlyViewedPDEDV.getPdeDataContext() != null){
					currentlyViewedPDEDV.getPdeDataContext().removePropertyChangeListener(this);
				}
			}
			PopupGenerator.showErrorDialog("Error showing Field Data Viewer\n"+e.getMessage());
		}
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

	cbit.vcell.modeldb.ReferenceQuerySpec rqs =
		new cbit.vcell.modeldb.ReferenceQuerySpec(targetExtDataID);

	cbit.vcell.modeldb.ReferenceQueryResult rqr =
		getRequestManager().getDocumentManager().findReferences(rqs);
	cbit.vcell.modeldb.VersionableTypeVersion[] dependants = null;
	Hashtable<VersionableTypeVersion,String[]> choices = new Hashtable<VersionableTypeVersion,String[]>();
	boolean bDanglingReferences = false;
	if(rqr != null){
		dependants =
			(rqr.getVersionableFamily().bDependants()?rqr.getVersionableFamily().getUniqueDependants():null);
		if(dependants != null){
			for(int i=0;i<dependants.length;i+= 1){
				boolean isBioModel = dependants[i].getVType().equals(VersionableType.BioModelMetaData);
				boolean isTop = isBioModel || dependants[i].getVType().equals(VersionableType.MathModelMetaData);
				if(isTop){
					cbit.vcell.modeldb.VersionableRelationship[] vrArr2 = rqr.getVersionableFamily().getDependantRelationships();
					for(int j=0;j<vrArr2.length;j+= 1){
						if( (vrArr2[j].from() == dependants[i]) &&
							vrArr2[j].to().getVType().equals((isBioModel?VersionableType.SimulationContext:VersionableType.MathDescription))){
							for(int k=0;k<vrArr2.length;k+= 1){
								boolean bAdd =false;
								if(k==j && vrArr2[k].from().getVType().equals(VersionableType.MathModelMetaData)){
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
												(isBioModel?VersionableType.BioModelMetaData.getTypeName():VersionableType.MathModelMetaData.getTypeName()),
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
			
	FieldDataFileOperationResults fdfor =
		getRequestManager().getDocumentManager().fieldDataFileOperation(
			FieldDataFileOperationSpec.createDependantFuncsFieldDataFileOperationSpec(targetExtDataID)
		);
	
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
				choicesV.add(new String[]{
						modelListData[i][0],
						modelListData[i][1],
						"Model Variable - "+(modelListData[i][2].length() == 0?"":"App='"+modelListData[i][2]+"'")+" version["+modelListData[i][3]+"]"
					}
				);
				varTypeV.add((modelListData[i][1].equals(VersionableType.BioModelMetaData.getTypeName())?VersionableType.BioModelMetaData:VersionableType.MathModelMetaData));
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
				}else if(fdfor.dependantFunctionInfo[i].referenceSourceType.equals(VersionableType.BioModelMetaData.getTypeName())){
					varTypeV.add(VersionableType.BioModelMetaData);
				}else if(fdfor.dependantFunctionInfo[i].referenceSourceType.equals(VersionableType.MathModelMetaData.getTypeName())){
					varTypeV.add(VersionableType.MathModelMetaData);
				}else{
					throw new IllegalArgumentException("Unknown reference source type "+fdfor.dependantFunctionInfo[i].referenceSourceType);
				}
				keyValV.add(fdfor.dependantFunctionInfo[i].refSourceVersionKey);
			}
			int[] selectionArr =
				PopupGenerator.showComponentOKCancelTableList(
					getComponent(), "References to Field Data (Select To Open) "+targetExtDataID.getName(),
					columnNames, choicesV.toArray(new String[0][0]), ListSelectionModel.SINGLE_SELECTION);
			if(selectionArr != null && selectionArr.length > 0){
				if(varTypeV.elementAt(selectionArr[0]) != null){
//					VersionableTypeVersion[] vtvArr = choices.keySet().toArray(new VersionableTypeVersion[0]);
//					cbit.vcell.modeldb.VersionableTypeVersion v = vtvArr[selectionArr[0]];
					//System.out.println(v);
					if(varTypeV.elementAt(selectionArr[0]).equals(VersionableType.BioModelMetaData)){
						BioModelInfo bmi = getRequestManager().getDocumentManager().getBioModelInfo(keyValV.elementAt(selectionArr[0]));
						getRequestManager().openDocument(bmi,FieldDataWindowManager.this,true);
					}else if(varTypeV.elementAt(selectionArr[0]).equals(VersionableType.MathModelMetaData)){
						MathModelInfo mmi = getRequestManager().getDocumentManager().getMathModelInfo(keyValV.elementAt(selectionArr[0]));
						getRequestManager().openDocument(mmi,FieldDataWindowManager.this,true);
					}else{
						throw new IllegalArgumentException("Not expecting varType "+varTypeV.elementAt(selectionArr[0]));
					}
				}else{
					DialogUtils.showInfoDialog("use FiledDataManager to view FieldData '"+
							choicesV.elementAt(selectionArr[0])[0]+"'");
				}
			}
		}
	}else{
		if(!bDanglingReferences){
			bHasReferences = false;
			if(bShowReferencingModelsList){
				cbit.gui.DialogUtils.showInfoDialog(
					"No Model references found for Field Data "+targetExtDataID.getName());
			}
		}else{
			bHasReferences = true;
			if(bShowReferencingModelsList){
				cbit.gui.DialogUtils.showInfoDialog(
					"No current Model references found.\n"+
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
