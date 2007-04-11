package cbit.vcell.client;

import java.awt.Component;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Hashtable;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;

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
import cbit.vcell.field.FieldDataGUIPanel;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.vcell.modeldb.VersionableTypeVersion;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.simdata.ExternalDataIdentifier;
import cbit.vcell.simdata.PDEDataContext;
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
		protected SimInfoHolder(
				SimulationInfo argSimInfo,
				int argJobIndex,
				boolean argistu
				){
			simInfo = argSimInfo;
			jobIndex = argJobIndex;
			isTimeUniform = argistu;
			
		}
	}
	public static class FDSimMathModelInfo extends SimInfoHolder{
		private KeyValue mathModelKey;
		public FDSimMathModelInfo(
				KeyValue mmK,
				SimulationInfo argSI,
				int jobIndex,
				boolean argistu
				){
			super(argSI,jobIndex,/*argorigin,argextent,argISize,argvariableNames,argtimebounds,argdts,*/argistu);
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
				boolean argistu
			){
			super(argSI,jobIndex,/*argorigin,argextent,argISize,argvariableNames,argtimebounds,argdts,*/argistu);
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
		String[] colNames = new String[] {"Simulation","Model","Type","Application","Owner","Date"};
		String[][] rows = new String[simInfoHolders.length][colNames.length];
		for(int i=0;i<simInfoHolders.length;i+= 1){
			if(simInfoHolders[i] instanceof FDSimMathModelInfo){
				MathModelInfo mmInfo =
					getRequestManager().getDocumentManager().getMathModelInfo(
							((FDSimMathModelInfo)simInfoHolders[i]).getMathModelKey());
				rows[i][0] = simInfoHolders[i].simInfo.getName();
				rows[i][1] = mmInfo.getVersion().getName();
				rows[i][2] = "MathModel";
				rows[i][3] = "";
				rows[i][4] = simInfoHolders[i].simInfo.getOwner().getName();
				rows[i][5] = mmInfo.getVersion().getDate().toString();
				
			}else if(simInfoHolders[i] instanceof FDSimBioModelInfo){
				BioModelInfo bmInfo =
					getRequestManager().getDocumentManager().getBioModelInfo(
							((FDSimBioModelInfo)simInfoHolders[i]).getBioModelKey());					
				rows[i][0] = simInfoHolders[i].simInfo.getName();
				rows[i][1] = bmInfo.getVersion().getName();
				rows[i][2] = "BioModel";
				rows[i][3] = ((FDSimBioModelInfo)simInfoHolders[i]).getSimulationContextName();
				rows[i][4] = simInfoHolders[i].simInfo.getOwner().getName();
				rows[i][5] = bmInfo.getVersion().getDate().toString();
			}
		}
		
		int[] selectionIndexArr =  PopupGenerator.showComponentOKCancelTableList(
				getComponent(), "Select Simulation for Field Data",
				colNames, rows, ListSelectionModel.SINGLE_SELECTION);
		if(selectionIndexArr != null && selectionIndexArr.length > 0){
			return simInfoHolders[selectionIndexArr[0]];
		}
		throw UserCancelException.CANCEL_GENERIC;
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
			getLocalRequestManager().getAsynchMessageManager().removeDataJobListener(currentlyViewedPDEDV);
			currentlyViewedPDEDV.getPdeDataContext().removePropertyChangeListener(this);
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
			PDEDataManager pdeDatamanager =
				(PDEDataManager)getRequestManager().getDataManager(eDI, true);
			PDEDataContext newPDEDataContext = pdeDatamanager.getPDEDataContext();
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
				getLocalRequestManager().getAsynchMessageManager().removeDataJobListener(currentlyViewedPDEDV);
				currentlyViewedPDEDV.getPdeDataContext().removePropertyChangeListener(this);
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
												(isBioModel?"BioModel":"MathModel"),
												(isBioModel?vrArr2[k].from().getVersion().getName():""),
												dependants[i].getVersion().getDate().toString()
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
			
			
			
//	}
	boolean bHasReferences = false;
	if(choices.size() > 0){
		bHasReferences = true;
		if(bShowReferencingModelsList){
			String[] columnNames = new String[] {"Model","Type","Application","Date"};
			VersionableTypeVersion[] vtvArr = choices.keySet().toArray(new VersionableTypeVersion[0]);
			String[][] modelListData = choices.values().toArray(new String[0][0]);
			int[] selectionArr =
				PopupGenerator.showComponentOKCancelTableList(
					getComponent(), "Models Referencing Field Data (Select To Open) "+targetExtDataID.getName(),
					columnNames, modelListData, ListSelectionModel.SINGLE_SELECTION);
			if(selectionArr != null && selectionArr.length > 0){
				cbit.vcell.modeldb.VersionableTypeVersion v = vtvArr[selectionArr[0]];
				//System.out.println(v);
				if(v.getVType().equals(VersionableType.BioModelMetaData)){
					BioModelInfo bmi = getRequestManager().getDocumentManager().getBioModelInfo(v.getVersion().getVersionKey());
					getRequestManager().openDocument(bmi,FieldDataWindowManager.this,true);
				}else if(v.getVType().equals(VersionableType.MathModelMetaData)){
					MathModelInfo mmi = getRequestManager().getDocumentManager().getMathModelInfo(v.getVersion().getVersionKey());
					getRequestManager().openDocument(mmi,FieldDataWindowManager.this,true);
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
