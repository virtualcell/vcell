package cbit.vcell.model.gui;
import cbit.util.KeyValue;
import cbit.util.UserCancelException;
import cbit.vcell.desktop.controls.AsynchClientTask;
import cbit.vcell.dictionary.ReactionDescription;
import cbit.vcell.dictionary.DBNonFormalUnboundSpecies;
import cbit.vcell.dictionary.ReactionQuerySpec;
import cbit.vcell.dictionary.SpeciesDescription;
import cbit.vcell.model.*;
import cbit.vcell.client.task.ClientTaskDispatcher;

/**
 * Insert the type's description here.
 * Creation date: (8/13/2003 11:00:32 AM)
 * @author: Frank Morgan
 */
public class DBReactionWizardPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {

	//
	//
	public class MapStringToObject {
		String mapString;
		Object toObject;
		public MapStringToObject(String argMapString,Object argToObject){
			mapString = argMapString;
			toObject = argToObject;
		}
		public Object getToObject(){
			return toObject;
		}
		public String getMappedString(){
			return mapString;
		}
		public String toString(){
			return mapString;
		}
	}
	//
	private javax.swing.JComboBox[] speciesAssignmentJCB = null;
	private javax.swing.JComboBox[] structureAssignmentJCB = null;
	private Species[] speciesOrder = null;
	private Object[] lastSearchChangeInfo = null;
	private Object lastReactionSelection = null;
	private ReactionStep lastReactStepSelection = null;
	//
	//
	private java.util.Hashtable mapRXStringtoRXIDs = new java.util.Hashtable();
	//
	private cbit.vcell.dictionary.ReactionDescription resolvedReaction = null;
	private javax.swing.JButton ivjBackJButton = null;
	private javax.swing.JPanel ivjBFNJPanel = null;
	private javax.swing.JButton ivjFinishJButton = null;
	private javax.swing.JButton ivjNextJButton = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JScrollPane ivjJScrollPane3 = null;
	private javax.swing.JPanel ivjParameterJPanel = null;
	private javax.swing.JList ivjParameterNamesJList = null;
	private javax.swing.JScrollPane ivjParameterNamesJScrollPane = null;
	private javax.swing.JList ivjParameterValuesJList = null;
	private javax.swing.JScrollPane ivjParameterValuesJScrollPane = null;
	private javax.swing.JList ivjReactionsJList = null;
	private javax.swing.JLabel ivjResolveHighlightJLabel = null;
	private javax.swing.JPanel ivjResolverJPanel = null;
	private javax.swing.JPanel ivjSearchCriteriaJPanel = null;
	private javax.swing.JPanel ivjSearchResultsJPanel = null;
	private cbit.vcell.client.database.DocumentManager fieldDocumentManager = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JPanel ivjCardLayoutJPanel = null;
	private boolean ivjConnPtoP3Aligning = false;
	private javax.swing.ListSelectionModel ivjReactionSelectionModel = null;
	private cbit.vcell.model.Model fieldModel = null;
	private cbit.vcell.model.Structure fieldStructure = null;
	private boolean ivjConnPtoP4Aligning = false;
	private javax.swing.ListSelectionModel ivjParameterNameSelectionModel = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JRadioButton ivjSearchDictionaryJRadioButton = null;
	private javax.swing.JRadioButton ivjSearchUserJRadioButton = null;
	private javax.swing.ButtonGroup ivjSearchTypeButtonGroup = null;
	private cbit.vcell.model.ReactionStep fieldReactionStep = null;
	private javax.swing.JButton ivjJButton1 = null;
	private cbit.vcell.dictionary.ReactionDescription fieldReactionDescription = null;
	private boolean ivjConnPtoP5Aligning = false;
	private javax.swing.ListSelectionModel ivjRXDescriptionLSM = null;
	private javax.swing.JPanel ivjJPanel = null;
	private javax.swing.ButtonGroup ivjFindRXButtonGroup = null;
	private javax.swing.JRadioButton ivjFindRXTextRadioButton = null;
	private javax.swing.JTextField ivjFindTextJTextField = null;
	private cbit.vcell.dictionary.DBFormalSpecies ivjCurrentDBFormalSpecies = null;
	private boolean ivjConnPtoP6Aligning = false;
	private javax.swing.text.Document ivjdocument2 = null;
	private javax.swing.JRadioButton ivjKeggMoleculeJRadioButton = null;
	private javax.swing.JButton ivjKeggSpecifyJButton = null;
	private javax.swing.JLabel ivjKeggTypeJLabel = null;
	private javax.swing.JPanel ivjRXParticipantsJPanel = null;
	private ReactionCanvas ivjReactionCanvas1 = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JScrollPane ivjJScrollPane2 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.DocumentListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DBReactionWizardPanel.this.getBackJButton()) 
				connEtoC12(e);
			if (e.getSource() == DBReactionWizardPanel.this.getFinishJButton()) 
				connEtoC13(e);
			if (e.getSource() == DBReactionWizardPanel.this.getNextJButton()) 
				connEtoC14(e);
			if (e.getSource() == DBReactionWizardPanel.this.getJButton1()) 
				connEtoC26(e);
			if (e.getSource() == DBReactionWizardPanel.this.getFindRXTextRadioButton()) 
				connEtoC15(e);
			if (e.getSource() == DBReactionWizardPanel.this.getKeggMoleculeJRadioButton()) 
				connEtoC28(e);
			if (e.getSource() == DBReactionWizardPanel.this.getKeggSpecifyJButton()) 
				connEtoC29(e);
			if (e.getSource() == DBReactionWizardPanel.this.getFindTextJTextField()) 
				connEtoM1(e);
		};
		public void changedUpdate(javax.swing.event.DocumentEvent e) {
			if (e.getDocument() == DBReactionWizardPanel.this.getdocument2()) 
				connEtoC31(e);
		};
		public void insertUpdate(javax.swing.event.DocumentEvent e) {
			if (e.getDocument() == DBReactionWizardPanel.this.getdocument2()) 
				connEtoC31(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == DBReactionWizardPanel.this.getReactionsJList() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == DBReactionWizardPanel.this.getParameterNamesJList() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == DBReactionWizardPanel.this && (evt.getPropertyName().equals("reactionDescription"))) 
				connEtoC22(evt);
			if (evt.getSource() == DBReactionWizardPanel.this.getParameterValuesJList() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP5SetTarget();
			if (evt.getSource() == DBReactionWizardPanel.this.getFindTextJTextField() && (evt.getPropertyName().equals("document"))) 
				connPtoP6SetTarget();
		};
		public void removeUpdate(javax.swing.event.DocumentEvent e) {
			if (e.getDocument() == DBReactionWizardPanel.this.getdocument2()) 
				connEtoC31(e);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == DBReactionWizardPanel.this.getParameterNameSelectionModel()) 
				connEtoC23(e);
			if (e.getSource() == DBReactionWizardPanel.this.getRXDescriptionLSM()) 
				connEtoM6();
			if (e.getSource() == DBReactionWizardPanel.this.getReactionSelectionModel()) 
				connEtoC27(e);
		};
	};

/**
 * DBReactionWizardPanel constructor comment.
 */
public DBReactionWizardPanel() {
	super();
	initialize();
}


/**
 * DBReactionWizardPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public DBReactionWizardPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * DBReactionWizardPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public DBReactionWizardPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * DBReactionWizardPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public DBReactionWizardPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {


	if(resolvedReaction.isFluxReaction()){
		if(e.getSource() == speciesAssignmentJCB[0]){
			if(speciesAssignmentJCB[1].getSelectedIndex() != speciesAssignmentJCB[0].getSelectedIndex()){
				speciesAssignmentJCB[1].setSelectedIndex(speciesAssignmentJCB[0].getSelectedIndex());
			}
		}else if(e.getSource() == speciesAssignmentJCB[1]){
			if(speciesAssignmentJCB[0].getSelectedIndex() != speciesAssignmentJCB[1].getSelectedIndex()){
				speciesAssignmentJCB[0].setSelectedIndex(speciesAssignmentJCB[1].getSelectedIndex());
			}
		}
	}
	//for(int i=0;i<speciesAssignmentJCB.length;i+= 1){
		//Species species = speciesOrder[speciesAssignmentJCB[i].getSelectedIndex()];
		//Structure structure = null;
		//if(getStructure() instanceof Feature){
			//structure = getStructure();
		//}else if(resolvedReaction.isFluxReaction() && i == 0){
			//structure = ((Membrane)getStructure()).getOutsideFeature();
		//}else if(resolvedReaction.isFluxReaction() && i == 1){
			//structure = ((Membrane)getStructure()).getInsideFeature();
		//}else if(structureAssignmentJCB[i].getSelectedIndex() == 0){
			//structure = getStructure();
		//}else if(structureAssignmentJCB[i].getSelectedIndex() == 1){
			//structure = ((Membrane)getStructure()).getOutsideFeature();
		//}else if(structureAssignmentJCB[i].getSelectedIndex() == 2){
			//structure = ((Membrane)getStructure()).getInsideFeature();
		//}
		//System.out.println(
			//resolvedReaction.getReactionElement(i).getPreferredName()+
			//" assigned to "+(species != null?species.getCommonName():"New Species")+
			//" in structure "+ structure.getName());
	//}
	
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 2:38:49 PM)
 */
private void afterSearchConfigure() {

	if(getReactionsJList().getModel().getSize() == 0){
		lastSearchChangeInfo = null;
		cbit.vcell.client.PopupGenerator.showInfoDialog("No Reactions found matching search criteria");
	}else{
		lastSearchSaveInfo();
    	((java.awt.CardLayout) getCardLayoutJPanel().getLayout()).next(getCardLayoutJPanel());
	}
    configureBFN();
}


/**
 * Comment
 */
private void bfnActionPerformed(java.awt.event.ActionEvent actionEvent) {
	//
	javax.swing.DefaultListModel pndlm = (javax.swing.DefaultListModel)getParameterNamesJList().getModel();
	//
	if(actionEvent.getSource().equals(getBackJButton())){
		if(getResolverJPanel().isVisible() && pndlm.size() == 0 && getSearchDictionaryJRadioButton().isSelected() == false){
			//skip Parameters if there are none
			((java.awt.CardLayout)getCardLayoutJPanel().getLayout()).previous(getCardLayoutJPanel());
		}
		((java.awt.CardLayout)getCardLayoutJPanel().getLayout()).previous(getCardLayoutJPanel());
	}else if(actionEvent.getSource().equals(getNextJButton())){
		if(getSearchCriteriaJPanel().isVisible()){
			if(getSearchDictionaryJRadioButton().isSelected()){
				getParameterJPanel().setVisible(false);
			}else{
				getParameterJPanel().setVisible(true);
			}
			if(!lastSearchIsSameAsCurrent()){
				search();
				lastReactionSelection = null;
				return;
			}
		}else if(getParameterJPanel().isVisible()){
			if(lastReactStepSelection == null || !lastReactStepSelection.equals(getReactionStep())){
				lastReactStepSelection = getReactionStep();
				String rxType = null;
				if(getReactionStep() instanceof FluxReaction){
					rxType = cbit.vcell.modeldb.DatabaseConstants.REACTTYPE_FLUX;
				}else{
					rxType = cbit.vcell.modeldb.DatabaseConstants.REACTTYPE_SIMPLE;
				}
				cbit.vcell.dictionary.ReactionDescription dbfr =
					new cbit.vcell.dictionary.ReactionDescription(
						getReactionStep().getName(),rxType,getReactionStep().getKey());
				//
				ReactionParticipant[] rpArr = getReactionStep().getReactionParticipants();
				for(int i=0;i < rpArr.length;i+= 1){
					cbit.vcell.dictionary.DBNonFormalUnboundSpecies dbnfu = new cbit.vcell.dictionary.DBNonFormalUnboundSpecies(rpArr[i].getSpecies().getCommonName());
					char role;
					if(rpArr[i] instanceof Reactant){
						role = cbit.vcell.dictionary.ReactionDescription.RX_ELEMENT_REACTANT;
					}else if(rpArr[i] instanceof Product){
						role = cbit.vcell.dictionary.ReactionDescription.RX_ELEMENT_PRODUCT;
					}else if(rpArr[i] instanceof Catalyst){
						role = cbit.vcell.dictionary.ReactionDescription.RX_ELEMENT_CATALYST;
					}else if(rpArr[i] instanceof Flux){
						role = cbit.vcell.dictionary.ReactionDescription.RX_ELEMENT_FLUX;
					}else{
						throw new RuntimeException("Unsupported ReationParticiapnt="+rpArr[i].getClass().getName());
					}
					dbfr.addReactionElement(dbnfu,rpArr[i].getSpeciesContext().getName(),rpArr[i].getStoichiometry(),role);
				}
				if(dbfr.isFluxReaction()){//make sure flux is in right direction
					Structure outsideStruct = ((Membrane)getReactionStep().getStructure()).getOutsideFeature();
					String defaultOutsideSCName = dbfr.getOrigSpeciesContextName(dbfr.getFluxIndexOutside());
					for(int i=0;i < rpArr.length;i+= 1){
						if(rpArr[i].getSpeciesContext().getName().equals(defaultOutsideSCName)){
							if(!rpArr[i].getStructure().equals(outsideStruct)){
								dbfr.swapFluxSCNames();
							}
							break;
						}
					}
				}
				setupRX(dbfr);
			}
		}
		//
		((java.awt.CardLayout)getCardLayoutJPanel().getLayout()).next(getCardLayoutJPanel());
	}else if(actionEvent.getSource().equals(getFinishJButton())){
		//done();
		resolve2();
	}
	//
	configureBFN();	
}


/**
 * Insert the method's description here.
 * Creation date: (9/18/2003 2:01:32 PM)
 */
private void closeParent() {
    //Try to close whoever contains us
    javax.swing.JInternalFrame jif =
        (javax.swing.JInternalFrame) cbit.util.BeanUtils.findTypeParentOfComponent(
            this,
            javax.swing.JInternalFrame.class);
    if (jif != null) {
        jif.dispose();
    } else {
        java.awt.Window window =
            (java.awt.Window) cbit.util.BeanUtils.findTypeParentOfComponent(
                this,
                java.awt.Window.class);
        if (window != null) {
            window.dispose();
        }
    }
}


/**
 * Insert the method's description here.
 * Creation date: (8/14/2003 2:38:54 PM)
 */
private void configureBFN() {
		
	boolean bBackEnabled = true;
	boolean bFinishEnabled = false;
	boolean bNextEnabled = false;
	
	if(getSearchCriteriaJPanel().isShowing()){
		String sText = getFindTextJTextField().getText();
		sText = (sText != null && sText.length() > 0?sText:null);
		bNextEnabled =
			(getFindRXTextRadioButton().isSelected()?sText != null:false) ||
			(!getFindRXTextRadioButton().isSelected()?getCurrentDBFormalSpecies() != null:false);
		bBackEnabled = false;
	}else if(getSearchResultsJPanel().isShowing() && getSearchDictionaryJRadioButton().isSelected()){
		bNextEnabled = !getReactionsJList().isSelectionEmpty();
	}else if(getParameterJPanel().isShowing()){
		bNextEnabled = getReactionStep() != null;
	}else if(getResolverJPanel().isShowing()){
		bFinishEnabled = true;
	}

	if(getBackJButton().isEnabled() != bBackEnabled){
		getBackJButton().setEnabled(bBackEnabled);
	}
	if(getFinishJButton().isEnabled() != bFinishEnabled){
		getFinishJButton().setEnabled(bFinishEnabled);
	}
	if(getNextJButton().isEnabled() != bNextEnabled){
		getNextJButton().setEnabled(bNextEnabled);
	}
}


/**
 * Comment
 */
private void configureRXParameterList(javax.swing.event.ListSelectionEvent listSelectionEvent) {
	//
	javax.swing.DefaultListModel pndlm = (javax.swing.DefaultListModel)getParameterNamesJList().getModel();
	//
	if(lastReactionSelection == null || !lastReactionSelection.equals(getReactionsJList().getSelectedValue())){
		lastReactionSelection = getReactionsJList().getSelectedValue();
		setReactionStep(null);
		setReactionDescription(null);
		//
		pndlm.removeAllElements();
		javax.swing.DefaultListModel pvdlm = (javax.swing.DefaultListModel)getParameterValuesJList().getModel();
		pvdlm.removeAllElements();
		//
		if(getReactionsJList().getSelectedValue() instanceof String){//User Reactions
			java.util.Vector rxIDV = (java.util.Vector)mapRXStringtoRXIDs.get(getReactionsJList().getSelectedValue());
			//String[] rxIDArr = (String[])rxIDV.toArray(new String[rxIDV.size()]);
			KeyValue rxKeys[] = new KeyValue[rxIDV.size()];
			for (int i = 0; i < rxKeys.length; i++){
				rxKeys[i] = new KeyValue((String)rxIDV.elementAt(i));
			}
			try{
				ReactionStepInfo reactionStepInfos[] = getDocumentManager().getUserReactionStepInfos(rxKeys);
				for (int i = 0;reactionStepInfos!=null && i < reactionStepInfos.length; i++){
					String descriptiveText =
						reactionStepInfos[i].getOwner().getName()+" - "+
						"  "+reactionStepInfos[i].getBioModelName()+"  "+
						"("+reactionStepInfos[i].getReactionName()+")"+" "+
						reactionStepInfos[i].getBioModelVersionDate();
						
					pndlm.addElement(new MapStringToObject(descriptiveText/*reactionStepInfos[i].getDescriptiveText()*/,reactionStepInfos[i]));
				}
			}catch(cbit.util.DataAccessException e){
				cbit.vcell.client.PopupGenerator.showErrorDialog("Error Getting Parameter names\n"+e.getMessage());
			}
		}else{//Dictionary ReactionDescription
			cbit.vcell.dictionary.ReactionDescription dbfr  = (cbit.vcell.dictionary.ReactionDescription)getReactionsJList().getSelectedValue();
			setupRX(dbfr);
		}
	}
	//if(pndlm.size() == 0){
		////Skip parameters if there are none
		//((java.awt.CardLayout)getCardLayoutJPanel().getLayout()).next(getCardLayoutJPanel());
	//}
}


/**
 * connEtoC12:  (BackJButton.action.actionPerformed(java.awt.event.ActionEvent) --> DBReactionWizardPanel.bfnActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.bfnActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC13:  (FinishJButton.action.actionPerformed(java.awt.event.ActionEvent) --> DBReactionWizardPanel.bfnActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.bfnActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC14:  (NextJButton.action.actionPerformed(java.awt.event.ActionEvent) --> DBReactionWizardPanel.bfnActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.bfnActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC15:  (FindRXTextRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> DBReactionWizardPanel.findCriteriaChanged(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.findCriteriaChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC22:  (DBReactionWizardPanel.ReactionDescription --> DBReactionWizardPanel.configureBFN()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC22(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.configureBFN();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC23:  (ParameterNameSelectionModel.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> DBReactionWizardPanel.parameterNameSelectionChanged()V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC23(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.parameterNameSelectionChanged();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC26:  (JButton1.action.actionPerformed(java.awt.event.ActionEvent) --> DBReactionWizardPanel.closeParent()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC26(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.closeParent();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC27:  (ReactionSelectionModel.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> DBReactionWizardPanel.configureRXParameterList(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC27(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.configureRXParameterList(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC28:  (FindRXTypeJRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> DBReactionWizardPanel.findCriteriaChanged(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC28(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.findCriteriaChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC29:  (SpecifyTypeJButton.action.actionPerformed(java.awt.event.ActionEvent) --> DBReactionWizardPanel.showSpeciesBrowser()Lcbit.vcell.dictionary.DBFormalSpecies;)
 * @return cbit.vcell.dictionary.DBFormalSpecies
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.dictionary.DBFormalSpecies connEtoC29(java.awt.event.ActionEvent arg1) {
	cbit.vcell.dictionary.DBFormalSpecies connEtoC29Result = null;
	try {
		// user code begin {1}
		// user code end
		connEtoC29Result = this.showSpeciesBrowser();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	return connEtoC29Result;
}

/**
 * connEtoC30:  (CurrentDBFormalSpecies.this --> DBReactionWizardPanel.findCriteriaDetailsChanged()V)
 * @param value cbit.vcell.dictionary.DBFormalSpecies
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC30(cbit.vcell.dictionary.DBFormalSpecies value) {
	try {
		// user code begin {1}
		// user code end
		this.findCriteriaDetailsChanged();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC31:  (document2.document. --> DBReactionWizardPanel.findCriteriaDetailsChanged()V)
 * @param evt javax.swing.event.DocumentEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC31(javax.swing.event.DocumentEvent evt) {
	try {
		// user code begin {1}
		// user code end
		this.findCriteriaDetailsChanged();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (DBReactionWizardPanel.initialize() --> DBReactionWizardPanel.dBReactionWizardPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4() {
	try {
		// user code begin {1}
		// user code end
		this.dBReactionWizardPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (FindTextJTextField.action.actionPerformed(java.awt.event.ActionEvent) --> NextJButton.doClick(I)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getNextJButton().doClick(1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM6:  (RXDescriptionLSM.listSelection. --> ParameterValuesJList.clearSelection()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6() {
	try {
		// user code begin {1}
		// user code end
		getParameterValuesJList().clearSelection();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (ReactionsJList.selectionModel <--> selectionModel2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getReactionSelectionModel() != null)) {
				getReactionsJList().setSelectionModel(getReactionSelectionModel());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetTarget:  (ReactionsJList.selectionModel <--> selectionModel2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setReactionSelectionModel(getReactionsJList().getSelectionModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetSource:  (ParameterNamesJList.selectionModel <--> ParameterNameSelectionModel.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getParameterNameSelectionModel() != null)) {
				getParameterNamesJList().setSelectionModel(getParameterNameSelectionModel());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetTarget:  (ParameterNamesJList.selectionModel <--> ParameterNameSelectionModel.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			setParameterNameSelectionModel(getParameterNamesJList().getSelectionModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP5SetSource:  (ParameterValuesJList.selectionModel <--> RXDescriptionLSM.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			if ((getRXDescriptionLSM() != null)) {
				getParameterValuesJList().setSelectionModel(getRXDescriptionLSM());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP5SetTarget:  (ParameterValuesJList.selectionModel <--> RXDescriptionLSM.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			setRXDescriptionLSM(getParameterValuesJList().getSelectionModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP6SetSource:  (FindTextJTextField.document <--> document2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			if ((getdocument2() != null)) {
				getFindTextJTextField().setDocument(getdocument2());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP6Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP6Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP6SetTarget:  (FindTextJTextField.document <--> document2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			setdocument2(getFindTextJTextField().getDocument());
			// user code begin {2}
			// user code end
			ivjConnPtoP6Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP6Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/24/2003 12:24:22 PM)
 * @return java.lang.String
 * @param sc cbit.vcell.model.SpeciesContext
 */
private String createMappingDisplayName(SpeciesContext sc) {
	return "Existing "+sc.getSpecies().getCommonName()+" in "+sc.getStructure().getName();
}


/**
 * Insert the method's description here.
 * Creation date: (8/24/2003 12:24:22 PM)
 * @return java.lang.String
 * @param sc cbit.vcell.model.SpeciesContext
 */
private String createMappingDisplayNameNew(Structure structure) {
	return "New in "+structure.getName();
}


/**
 * Comment
 */
private void dBReactionWizardPanel_Initialize() {

	getFindRXButtonGroup().add(getFindRXTextRadioButton());
	getFindRXButtonGroup().add(getKeggMoleculeJRadioButton());
	
	getSearchTypeButtonGroup().add(getSearchUserJRadioButton());
	getSearchTypeButtonGroup().add(getSearchDictionaryJRadioButton());

	getParameterValuesJList().setModel(new javax.swing.DefaultListModel());
	getParameterNamesJList().setModel(new javax.swing.DefaultListModel());

	//((javax.swing.table.DefaultTableModel)getScrollPaneTable().getModel()).addColumn("rcp");
	//((javax.swing.table.DefaultTableModel)getScrollPaneTable().getModel()).addColumn("structure");
	//((javax.swing.table.DefaultTableModel)getScrollPaneTable().getModel()).addColumn("mapped to");
	
}


/**
 * Comment
 */
private void done() {

	java.util.Vector newlyAddedSpeciesContexts = new java.util.Vector();
	java.util.Vector newlyAddedSpecies = new java.util.Vector();
	java.util.Vector speciesWithChangedBindings = new java.util.Vector();
	final int SPECIES_INDEX = 0;
	final int ORIG_DBS_INDEX = 1;
	final int CHANGED_DBS_INDEX = 2;
	//
	boolean bClose = false;
	//
	try{
		if(getReactionDescription() != null){
			cbit.vcell.dictionary.ReactionDescription dbfr = getReactionDescription();
			String uniqueName = (dbfr.isFluxReaction()?"Flux":"Reaction");
			while(true){
				boolean bUnique = true;
				ReactionStep[] rsArr = getModel().getReactionSteps();
				for(int i=0;i<rsArr.length;i+= 1){
					if(rsArr[i].getName().equals(uniqueName)){
						bUnique = false;
						break;
					}
				}
				if(bUnique){
					break;
				}
				uniqueName = cbit.util.TokenMangler.getNextEnumeratedToken(uniqueName);
			}
			//Create RX components and determine which must be newly added to model
			java.util.Vector speciesContextV = new java.util.Vector();
			java.util.Vector scrxTypeV = new java.util.Vector();
			java.util.Vector scStoichV = new java.util.Vector();
			Species newFluxSpecies = null;
			for(int i = 0;i < dbfr.elementCount();i+= 1){
				Species currentSpecies = dbfr.getResolved(i).getSpecies();
				Structure currentStructure = dbfr.getResolved(i).getStructure();
				if(dbfr.isFlux(i) && newFluxSpecies != null){
					currentSpecies = newFluxSpecies;
				}else if(!getModel().contains(currentSpecies)){
					//No mapping from user so we must add new Species
					newlyAddedSpecies.add(currentSpecies);
					//Make sure species name doesn't conflict
					while(getModel().getSpecies(currentSpecies.getCommonName()) != null){
						currentSpecies.setCommonName(cbit.util.TokenMangler.getNextEnumeratedToken(currentSpecies.getCommonName()));
					}
					if(dbfr.isFlux(i)){
						newFluxSpecies = currentSpecies;
					}
				}else {
					SpeciesDescription speciesDescription = dbfr.getReactionElement(i);
					if(speciesDescription instanceof cbit.vcell.dictionary.DBFormalSpecies){
						cbit.vcell.dictionary.DBSpecies reDBSpecies =
							getDocumentManager().getBoundSpecies((cbit.vcell.dictionary.DBFormalSpecies)speciesDescription);
						if(currentSpecies.getDBSpecies() != null){
							if(!currentSpecies.getDBSpecies().compareEqual(reDBSpecies)){
								String message = "Error Mapping RXParticipant:\n\n"+
								"Existing Species '"+currentSpecies.getCommonName()+
								"' has binding \n'"+
								currentSpecies.getDBSpecies().getFormalSpeciesInfo().getFormalID()+" : "+
								currentSpecies.getDBSpecies().getFormalSpeciesInfo().getPreferredName()+
								"'\n but mapped ReactionElement has binding\n'"+
								reDBSpecies.getFormalSpeciesInfo().getFormalID()+" : "+
								reDBSpecies.getFormalSpeciesInfo().getPreferredName()+"'";

								cbit.vcell.client.PopupGenerator.showInfoDialog(message);
								//bClose = false;
								return;
							}
						}else{
							Object[] sbc = new Object[3];
							sbc[SPECIES_INDEX] = currentSpecies;
							sbc[ORIG_DBS_INDEX] = currentSpecies.getDBSpecies();
							sbc[CHANGED_DBS_INDEX] = reDBSpecies;
							speciesWithChangedBindings.add(sbc);
						}
					}
				}
				SpeciesContext currentSpeciesContext = getModel().getSpeciesContext(currentSpecies,currentStructure);
				if(currentSpeciesContext == null){
					currentSpeciesContext = dbfr.getResolved(i);//new SpeciesContext(currentSpecies,currentStructure);
					newlyAddedSpeciesContexts.add(currentSpeciesContext);
				}
				speciesContextV.add(currentSpeciesContext);
				scrxTypeV.add(new Character(dbfr.getType(i)));
				scStoichV.add(new Integer(dbfr.getStoich(i)));
			}
			////Add new species to Model
			//for(int i=0;i<newlyAddedSpecies.size();i+= 1){
				//getModel().addSpecies((Species)newlyAddedSpecies.get(i));
			//}
			////Add new SpeciesContexts to Model
			//for(int i=0;i<newlyAddedSpeciesContexts.size();i+= 1){
				//getModel().addSpeciesContext((SpeciesContext)newlyAddedSpeciesContexts.get(i));
			//}
			////Add RX Species Bindings to Model Species if necessary
			//for(int i = 0;i < speciesWithChangedBindings.size();i+= 1){
				//Object[] sbc = (Object[])speciesWithChangedBindings.get(i);
				//Species changedSpecies = (Species)sbc[SPECIES_INDEX];
				//cbit.vcell.dictionary.DBSpecies changedDBSpecies = (cbit.vcell.dictionary.DBSpecies)sbc[CHANGED_DBS_INDEX];
				//changedSpecies.setDBSpecies(changedDBSpecies);
			//}
			//
			//
			ReactionStep reaction = null;
			if(getReactionStep() == null){//Must have been from the EnzymeReaction dictionary, has no Kinetics
				//Create Default Kinetics for Dictionary Reaction
				reaction = new SimpleReaction(getStructure(),uniqueName);
				//Add Components to Reaction
				for(int i=0;i<speciesContextV.size();i+= 1){
					SpeciesContext sc = (SpeciesContext)speciesContextV.get(i);
					char rxType = ((Character)scrxTypeV.get(i)).charValue();
					int rxStoich = ((Integer)scStoichV.get(i)).intValue();
					//
					if(rxType == cbit.vcell.dictionary.ReactionDescription.RX_ELEMENT_CATALYST){
						reaction.addCatalyst(sc);
					}else if(rxType == cbit.vcell.dictionary.ReactionDescription.RX_ELEMENT_FLUX){
						((FluxReaction)reaction).setFluxCarrier(sc.getSpecies(),getModel());
					}else if(rxType == cbit.vcell.dictionary.ReactionDescription.RX_ELEMENT_PRODUCT){
						((SimpleReaction)reaction).addProduct(sc,rxStoich);
					}else if(rxType == cbit.vcell.dictionary.ReactionDescription.RX_ELEMENT_REACTANT){
						((SimpleReaction)reaction).addReactant(sc,rxStoich);
					}else{
						throw new Exception("Unknown ReactionElementType="+dbfr.getType(i)+", should be (C)atalyst,(F)lux,(R)eactant,(P)roduct");
					}
				}
				//
				Kinetics kinetics = new HMM_IRRKinetics(reaction);
				cbit.vcell.parser.Expression kmExpression = new cbit.vcell.parser.Expression("1.0");
				cbit.vcell.parser.Expression vmaxExpression = new cbit.vcell.parser.Expression("1.0");
				kinetics.setParameterValue(((HMM_IRRKinetics)kinetics).getKmParameter(),kmExpression);
				kinetics.setParameterValue(((HMM_IRRKinetics)kinetics).getVmaxParameter(),vmaxExpression);
				//reaction = new SimpleReaction(getStructure(),uniqueName);
				reaction.setKinetics(kinetics);
			}else{//Must be user reaction with kinetics
				reaction = getReactionStep();
				reaction.refreshDependencies();
				//
				reaction.setName(uniqueName);
				reaction.setStructure(getStructure());
				//Make sure Kinetics parameters don't conflict
				Kinetics kinetics = reaction.getKinetics();
				Kinetics.KineticsParameter[] kpArr = kinetics.getKineticsParameters();
				for(int i=0;i < kpArr.length;i+= 1){
					String kpName = kpArr[i].getName();
					Kinetics.KineticsParameter kp = null;
					while(getModel().getKineticsParameter(kpName) != null){
						String newKPName = cbit.util.TokenMangler.getNextEnumeratedToken(kpName);
						kinetics.renameParameter(kpName,newKPName);
						kpName = newKPName;
					}
				}
				//change old rxParticpant contextNames to new contextNames
				ReactionParticipant[] rpArr = reaction.getReactionParticipants();
				for(int i=0;i< rpArr.length;i+= 1){
					for(int j=0;j< dbfr.elementCount();j+= 1){
						if(dbfr.getOrigSpeciesContextName(j).equals(rpArr[i].getSpeciesContext().getName())){
							rpArr[i].setSpeciesContext(dbfr.getResolved(j));
						}
					}
				}
			}
			reaction.refreshDependencies();
			//
			//Now Add everything to the Model
			//
			//Add new species to Model
			for(int i=0;i<newlyAddedSpecies.size();i+= 1){
				getModel().addSpecies((Species)newlyAddedSpecies.get(i));
			}
			//Add new SpeciesContexts to Model
			for(int i=0;i<newlyAddedSpeciesContexts.size();i+= 1){
				getModel().addSpeciesContext((SpeciesContext)newlyAddedSpeciesContexts.get(i));
			}
			//Add RX Species Bindings to Model Species if necessary
			for(int i = 0;i < speciesWithChangedBindings.size();i+= 1){
				Object[] sbc = (Object[])speciesWithChangedBindings.get(i);
				Species changedSpecies = (Species)sbc[SPECIES_INDEX];
				cbit.vcell.dictionary.DBSpecies changedDBSpecies = (cbit.vcell.dictionary.DBSpecies)sbc[CHANGED_DBS_INDEX];
				changedSpecies.setDBSpecies(changedDBSpecies);
			}
			//Add Reaction
			getModel().addReactionStep(reaction);
		}
		
		bClose = true;
		
	}catch(Exception e){
		//Undo anything we added to this model
		String fixFailedS = "";
		for(int i = 0;i < newlyAddedSpeciesContexts.size();i+= 1){
			try{
				getModel().removeSpeciesContext((SpeciesContext)newlyAddedSpeciesContexts.get(i));
			}catch(Exception ee){
				fixFailedS = e.getMessage()+"\n";
			}
		}
		for(int i = 0;i < newlyAddedSpecies.size();i+= 1){
			try{
				getModel().removeSpecies((Species)newlyAddedSpecies.get(i));
			}catch(Exception ee){
				fixFailedS = e.getMessage()+"\n";
			}
		}
		for(int i = 0;i < speciesWithChangedBindings.size();i+= 1){
			try{
				Object[] sbc = (Object[])speciesWithChangedBindings.get(i);
				Species changedSpecies = (Species)sbc[SPECIES_INDEX];
				cbit.vcell.dictionary.DBSpecies originalDBSpecies = (cbit.vcell.dictionary.DBSpecies)sbc[ORIG_DBS_INDEX];
				if(!cbit.util.Compare.isEqualOrNull(originalDBSpecies,changedSpecies.getDBSpecies())){
					changedSpecies.setDBSpecies(originalDBSpecies);
				}
			}catch(Exception ee){
				fixFailedS = e.getMessage()+"\n";
			}
		}
		cbit.vcell.client.PopupGenerator.showErrorDialog(this,"Error adding reaction: \n"+
				e.getClass().getName()+"\n"+e.getMessage()+"\n"+
				(fixFailedS.length() != 0?"\nYour Model may have been Corrupted.\n"+fixFailedS:""));
	}finally{
		if(bClose){closeParent();}
	}
	
}


/**
 * Comment
 */
private void findCriteriaChanged(java.awt.event.ActionEvent actionEvent) {
	
	if(getFindRXTextRadioButton().isSelected()){
		getFindTextJTextField().setEnabled(true);
		getKeggSpecifyJButton().setEnabled(false);
		getKeggTypeJLabel().setEnabled(false);
	}else if(getKeggMoleculeJRadioButton().isSelected()){
		getFindTextJTextField().setEnabled(false);
		getKeggSpecifyJButton().setEnabled(true);
		getKeggTypeJLabel().setEnabled(true);
	}
	configureBFN();
}


/**
 * Comment
 */
private void findCriteriaDetailsChanged() {
	
	if(getKeggMoleculeJRadioButton().isSelected()){
		getKeggTypeJLabel().setText("Current: "+(getCurrentDBFormalSpecies() == null?"None Specified":getCurrentDBFormalSpecies().getFormalSpeciesInfo().toString()));
	}
	configureBFN();
}


/**
 * Insert the method's description here.
 * Creation date: (8/8/2003 7:19:52 PM)
 * @return cbit.vcell.model.Species[]
 */
private cbit.vcell.model.SpeciesContext[] findLegalFluxSpeciesContexts(cbit.vcell.model.Membrane membrane) {
	java.util.Vector legalSpeciesContexts = new java.util.Vector();
	if(getModel() != null){
		cbit.vcell.model.SpeciesContext[] outSC = getModel().getSpeciesContexts(membrane.getOutsideFeature());
		cbit.vcell.model.SpeciesContext[]  inSC = getModel().getSpeciesContexts(membrane.getInsideFeature());
		if(outSC != null && inSC != null){
			for(int i=0;i < outSC.length;i+= 1){
				for(int j=0;j<inSC.length;j+= 1){
					if(	outSC[i].getSpecies().equals(inSC[j].getSpecies()) && 
						!legalSpeciesContexts.contains(outSC[i]) &&
						!legalSpeciesContexts.contains(inSC[i])
					){
						legalSpeciesContexts.add(outSC[i]);
						legalSpeciesContexts.add(inSC[i]);
					}
				}
			}
		}
	}
	if(legalSpeciesContexts.size() > 0){
		cbit.vcell.model.SpeciesContext[] spArr = new cbit.vcell.model.SpeciesContext[legalSpeciesContexts.size()];
		legalSpeciesContexts.copyInto(spArr);
		return spArr;
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (8/4/2003 2:40:51 PM)
 * @return java.lang.String
 * @param origS java.lang.String
 */
private String formatLikeString(String origS) {
	
	if(origS == null || origS.length() == 0){
		return null;
	}
	
	StringBuffer sb = new StringBuffer(origS);
	for(int i=0;i<sb.length();i+= 1){
		if(sb.charAt(i) == '*'){
			sb.replace(i,i+1,"%");
		}
	}

	origS = sb.toString();
	if(origS.indexOf("%") == -1 && origS.indexOf("_") == -1){
		origS = "%"+origS+"%";
	}
	//The character "%" matches any string of zero or more characters except null.
	//The character "_" matches any single character.
	//A wildcard character is treated as a literal if preceded by the character designated as the escape character.
	//Default ESCAPE character for VCell = '/' defined in DictionaryDbDriver.getDatabaseSpecies

	return origS;
}


/**
 * Return the BackJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getBackJButton() {
	if (ivjBackJButton == null) {
		try {
			ivjBackJButton = new javax.swing.JButton();
			ivjBackJButton.setName("BackJButton");
			ivjBackJButton.setText("Back");
			ivjBackJButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBackJButton;
}


/**
 * Return the BFNJPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getBFNJPanel() {
	if (ivjBFNJPanel == null) {
		try {
			ivjBFNJPanel = new javax.swing.JPanel();
			ivjBFNJPanel.setName("BFNJPanel");
			ivjBFNJPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsBackJButton = new java.awt.GridBagConstraints();
			constraintsBackJButton.gridx = 0; constraintsBackJButton.gridy = 0;
			constraintsBackJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getBFNJPanel().add(getBackJButton(), constraintsBackJButton);

			java.awt.GridBagConstraints constraintsFinishJButton = new java.awt.GridBagConstraints();
			constraintsFinishJButton.gridx = 1; constraintsFinishJButton.gridy = 0;
			constraintsFinishJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getBFNJPanel().add(getFinishJButton(), constraintsFinishJButton);

			java.awt.GridBagConstraints constraintsNextJButton = new java.awt.GridBagConstraints();
			constraintsNextJButton.gridx = 2; constraintsNextJButton.gridy = 0;
			constraintsNextJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getBFNJPanel().add(getNextJButton(), constraintsNextJButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBFNJPanel;
}


/**
 * Return the CardLayouJPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getCardLayoutJPanel() {
	if (ivjCardLayoutJPanel == null) {
		try {
			ivjCardLayoutJPanel = new javax.swing.JPanel();
			ivjCardLayoutJPanel.setName("CardLayoutJPanel");
			ivjCardLayoutJPanel.setLayout(new java.awt.CardLayout());
			getCardLayoutJPanel().add(getSearchCriteriaJPanel(), getSearchCriteriaJPanel().getName());
			getCardLayoutJPanel().add(getSearchResultsJPanel(), getSearchResultsJPanel().getName());
			getCardLayoutJPanel().add(getResolverJPanel(), getResolverJPanel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCardLayoutJPanel;
}

/**
 * Return the CurrentDBFormalSpecies property value.
 * @return cbit.vcell.dictionary.DBFormalSpecies
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.dictionary.DBFormalSpecies getCurrentDBFormalSpecies() {
	// user code begin {1}
	// user code end
	return ivjCurrentDBFormalSpecies;
}


/**
 * Return the document2 property value.
 * @return javax.swing.text.Document
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.text.Document getdocument2() {
	// user code begin {1}
	// user code end
	return ivjdocument2;
}


/**
 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @return The documentManager property value.
 * @see #setDocumentManager
 */
public cbit.vcell.client.database.DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}


/**
 * Return the FindRXButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getFindRXButtonGroup() {
	if (ivjFindRXButtonGroup == null) {
		try {
			ivjFindRXButtonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFindRXButtonGroup;
}


/**
 * Return the SearchUserJRadioButton1 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getFindRXTextRadioButton() {
	if (ivjFindRXTextRadioButton == null) {
		try {
			ivjFindRXTextRadioButton = new javax.swing.JRadioButton();
			ivjFindRXTextRadioButton.setName("FindRXTextRadioButton");
			ivjFindRXTextRadioButton.setSelected(true);
			ivjFindRXTextRadioButton.setText("Text (Enter Search Text, *(star) matches any character)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFindRXTextRadioButton;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getFindTextJTextField() {
	if (ivjFindTextJTextField == null) {
		try {
			ivjFindTextJTextField = new javax.swing.JTextField();
			ivjFindTextJTextField.setName("FindTextJTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFindTextJTextField;
}

/**
 * Return the FinishJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getFinishJButton() {
	if (ivjFinishJButton == null) {
		try {
			ivjFinishJButton = new javax.swing.JButton();
			ivjFinishJButton.setName("FinishJButton");
			ivjFinishJButton.setText("Finish");
			ivjFinishJButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFinishJButton;
}


/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton1() {
	if (ivjJButton1 == null) {
		try {
			ivjJButton1 = new javax.swing.JButton();
			ivjJButton1.setName("JButton1");
			ivjJButton1.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton1;
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Reaction Details");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}

/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("Reaction Version(s)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}

/**
 * Return the JPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel() {
	if (ivjJPanel == null) {
		try {
			cbit.gui.LineBorderBean ivjLocalBorder4;
			ivjLocalBorder4 = new cbit.gui.LineBorderBean();
			ivjLocalBorder4.setThickness(2);
			cbit.gui.TitledBorderBean ivjLocalBorder3;
			ivjLocalBorder3 = new cbit.gui.TitledBorderBean();
			ivjLocalBorder3.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder3.setBorder(ivjLocalBorder4);
			ivjLocalBorder3.setTitleColor(java.awt.Color.black);
			ivjLocalBorder3.setTitle("2.  Find Any Reaction Containing:");
			ivjJPanel = new javax.swing.JPanel();
			ivjJPanel.setName("JPanel");
			ivjJPanel.setBorder(ivjLocalBorder3);
			ivjJPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsFindRXTextRadioButton = new java.awt.GridBagConstraints();
			constraintsFindRXTextRadioButton.gridx = 0; constraintsFindRXTextRadioButton.gridy = 0;
			constraintsFindRXTextRadioButton.gridwidth = 2;
			constraintsFindRXTextRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFindRXTextRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFindRXTextRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel().add(getFindRXTextRadioButton(), constraintsFindRXTextRadioButton);

			java.awt.GridBagConstraints constraintsKeggMoleculeJRadioButton = new java.awt.GridBagConstraints();
			constraintsKeggMoleculeJRadioButton.gridx = 0; constraintsKeggMoleculeJRadioButton.gridy = 2;
			constraintsKeggMoleculeJRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsKeggMoleculeJRadioButton.insets = new java.awt.Insets(4, 4, 4, 0);
			getJPanel().add(getKeggMoleculeJRadioButton(), constraintsKeggMoleculeJRadioButton);

			java.awt.GridBagConstraints constraintsFindTextJTextField = new java.awt.GridBagConstraints();
			constraintsFindTextJTextField.gridx = 0; constraintsFindTextJTextField.gridy = 1;
			constraintsFindTextJTextField.gridwidth = 2;
			constraintsFindTextJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFindTextJTextField.weightx = 1.0;
			constraintsFindTextJTextField.insets = new java.awt.Insets(4, 20, 20, 4);
			getJPanel().add(getFindTextJTextField(), constraintsFindTextJTextField);

			java.awt.GridBagConstraints constraintsKeggSpecifyJButton = new java.awt.GridBagConstraints();
			constraintsKeggSpecifyJButton.gridx = 1; constraintsKeggSpecifyJButton.gridy = 2;
			constraintsKeggSpecifyJButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKeggSpecifyJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel().add(getKeggSpecifyJButton(), constraintsKeggSpecifyJButton);

			java.awt.GridBagConstraints constraintsKeggTypeJLabel = new java.awt.GridBagConstraints();
			constraintsKeggTypeJLabel.gridx = 0; constraintsKeggTypeJLabel.gridy = 3;
			constraintsKeggTypeJLabel.gridwidth = 2;
			constraintsKeggTypeJLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKeggTypeJLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsKeggTypeJLabel.insets = new java.awt.Insets(0, 20, 20, 4);
			getJPanel().add(getKeggTypeJLabel(), constraintsKeggTypeJLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			cbit.gui.LineBorderBean ivjLocalBorder2;
			ivjLocalBorder2 = new cbit.gui.LineBorderBean();
			ivjLocalBorder2.setThickness(2);
			cbit.gui.TitledBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new cbit.gui.TitledBorderBean();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setBorder(ivjLocalBorder2);
			ivjLocalBorder1.setTitleColor(java.awt.Color.black);
			ivjLocalBorder1.setTitle("1.  Where to Search");
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setBorder(ivjLocalBorder1);
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSearchUserJRadioButton = new java.awt.GridBagConstraints();
			constraintsSearchUserJRadioButton.gridx = 0; constraintsSearchUserJRadioButton.gridy = 0;
			constraintsSearchUserJRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getSearchUserJRadioButton(), constraintsSearchUserJRadioButton);

			java.awt.GridBagConstraints constraintsSearchDictionaryJRadioButton = new java.awt.GridBagConstraints();
			constraintsSearchDictionaryJRadioButton.gridx = 1; constraintsSearchDictionaryJRadioButton.gridy = 0;
			constraintsSearchDictionaryJRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getSearchDictionaryJRadioButton(), constraintsSearchDictionaryJRadioButton);
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
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			getJScrollPane1().setViewportView(getReactionCanvas1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}


/**
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane2() {
	if (ivjJScrollPane2 == null) {
		try {
			ivjJScrollPane2 = new javax.swing.JScrollPane();
			ivjJScrollPane2.setName("JScrollPane2");
			getJScrollPane2().setViewportView(getRXParticipantsJPanel());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane2;
}


/**
 * Return the JScrollPane3 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane3() {
	if (ivjJScrollPane3 == null) {
		try {
			ivjJScrollPane3 = new javax.swing.JScrollPane();
			ivjJScrollPane3.setName("JScrollPane3");
			getJScrollPane3().setViewportView(getReactionsJList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane3;
}


/**
 * Return the SearchDictionaryJRadioButton1 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKeggMoleculeJRadioButton() {
	if (ivjKeggMoleculeJRadioButton == null) {
		try {
			ivjKeggMoleculeJRadioButton = new javax.swing.JRadioButton();
			ivjKeggMoleculeJRadioButton.setName("KeggMoleculeJRadioButton");
			ivjKeggMoleculeJRadioButton.setText("KEGG Molecule / SWISSPROT Protein");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeggMoleculeJRadioButton;
}

/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getKeggSpecifyJButton() {
	if (ivjKeggSpecifyJButton == null) {
		try {
			ivjKeggSpecifyJButton = new javax.swing.JButton();
			ivjKeggSpecifyJButton.setName("KeggSpecifyJButton");
			ivjKeggSpecifyJButton.setText("Specify...");
			ivjKeggSpecifyJButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeggSpecifyJButton;
}

/**
 * Return the JLabel6 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKeggTypeJLabel() {
	if (ivjKeggTypeJLabel == null) {
		try {
			ivjKeggTypeJLabel = new javax.swing.JLabel();
			ivjKeggTypeJLabel.setName("KeggTypeJLabel");
			ivjKeggTypeJLabel.setFont(new java.awt.Font("Arial", 1, 14));
			ivjKeggTypeJLabel.setText("Current: None Specified");
			ivjKeggTypeJLabel.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeggTypeJLabel;
}

/**
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
public cbit.vcell.model.Model getModel() {
	return fieldModel;
}


/**
 * Return the NextJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getNextJButton() {
	if (ivjNextJButton == null) {
		try {
			ivjNextJButton = new javax.swing.JButton();
			ivjNextJButton.setName("NextJButton");
			ivjNextJButton.setText("Next");
			ivjNextJButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNextJButton;
}


/**
 * Return the ParameterJPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getParameterJPanel() {
	if (ivjParameterJPanel == null) {
		try {
			cbit.gui.LineBorderBean ivjLocalBorder8;
			ivjLocalBorder8 = new cbit.gui.LineBorderBean();
			ivjLocalBorder8.setThickness(1);
			cbit.gui.TitledBorderBean ivjLocalBorder7;
			ivjLocalBorder7 = new cbit.gui.TitledBorderBean();
			ivjLocalBorder7.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder7.setBorder(ivjLocalBorder8);
			ivjLocalBorder7.setTitleColor(java.awt.Color.black);
			ivjLocalBorder7.setTitle("2.  Choose a Version of the Selected Reaction");
			ivjParameterJPanel = new javax.swing.JPanel();
			ivjParameterJPanel.setName("ParameterJPanel");
			ivjParameterJPanel.setBorder(ivjLocalBorder7);
			ivjParameterJPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsParameterNamesJScrollPane = new java.awt.GridBagConstraints();
			constraintsParameterNamesJScrollPane.gridx = 0; constraintsParameterNamesJScrollPane.gridy = 1;
			constraintsParameterNamesJScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsParameterNamesJScrollPane.weightx = 1.0;
			constraintsParameterNamesJScrollPane.weighty = 1.0;
			constraintsParameterNamesJScrollPane.insets = new java.awt.Insets(4, 4, 4, 4);
			getParameterJPanel().add(getParameterNamesJScrollPane(), constraintsParameterNamesJScrollPane);

			java.awt.GridBagConstraints constraintsParameterValuesJScrollPane = new java.awt.GridBagConstraints();
			constraintsParameterValuesJScrollPane.gridx = 1; constraintsParameterValuesJScrollPane.gridy = 1;
			constraintsParameterValuesJScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsParameterValuesJScrollPane.weightx = 1.0;
			constraintsParameterValuesJScrollPane.weighty = 1.0;
			constraintsParameterValuesJScrollPane.insets = new java.awt.Insets(4, 4, 4, 4);
			getParameterJPanel().add(getParameterValuesJScrollPane(), constraintsParameterValuesJScrollPane);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 1; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.weightx = 1.0;
			constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getParameterJPanel().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getParameterJPanel().add(getJLabel2(), constraintsJLabel2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjParameterJPanel;
}

/**
 * Return the ParameterNameSelectionModel property value.
 * @return javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ListSelectionModel getParameterNameSelectionModel() {
	// user code begin {1}
	// user code end
	return ivjParameterNameSelectionModel;
}


/**
 * Return the ParameterNamesJList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getParameterNamesJList() {
	if (ivjParameterNamesJList == null) {
		try {
			ivjParameterNamesJList = new javax.swing.JList();
			ivjParameterNamesJList.setName("ParameterNamesJList");
			ivjParameterNamesJList.setBounds(0, 0, 160, 120);
			ivjParameterNamesJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjParameterNamesJList;
}

/**
 * Return the ParameterNamesJScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getParameterNamesJScrollPane() {
	if (ivjParameterNamesJScrollPane == null) {
		try {
			ivjParameterNamesJScrollPane = new javax.swing.JScrollPane();
			ivjParameterNamesJScrollPane.setName("ParameterNamesJScrollPane");
			getParameterNamesJScrollPane().setViewportView(getParameterNamesJList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjParameterNamesJScrollPane;
}


/**
 * Return the ParameterValuesJList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getParameterValuesJList() {
	if (ivjParameterValuesJList == null) {
		try {
			ivjParameterValuesJList = new javax.swing.JList();
			ivjParameterValuesJList.setName("ParameterValuesJList");
			ivjParameterValuesJList.setBounds(0, 0, 160, 120);
			ivjParameterValuesJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjParameterValuesJList;
}

/**
 * Return the ParameterValuesJScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getParameterValuesJScrollPane() {
	if (ivjParameterValuesJScrollPane == null) {
		try {
			ivjParameterValuesJScrollPane = new javax.swing.JScrollPane();
			ivjParameterValuesJScrollPane.setName("ParameterValuesJScrollPane");
			getParameterValuesJScrollPane().setViewportView(getParameterValuesJList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjParameterValuesJScrollPane;
}


/**
 * Return the ReactionCanvas1 property value.
 * @return cbit.vcell.model.gui.ReactionCanvas
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReactionCanvas getReactionCanvas1() {
	if (ivjReactionCanvas1 == null) {
		try {
			cbit.gui.LineBorderBean ivjLocalBorder12;
			ivjLocalBorder12 = new cbit.gui.LineBorderBean();
			ivjLocalBorder12.setThickness(2);
			cbit.gui.TitledBorderBean ivjLocalBorder11;
			ivjLocalBorder11 = new cbit.gui.TitledBorderBean();
			ivjLocalBorder11.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder11.setBorder(ivjLocalBorder12);
			ivjLocalBorder11.setTitle("Reaction Stoichiometry");
			ivjReactionCanvas1 = new cbit.vcell.model.gui.ReactionCanvas();
			ivjReactionCanvas1.setName("ReactionCanvas1");
			ivjReactionCanvas1.setBorder(ivjLocalBorder11);
			ivjReactionCanvas1.setLocation(0, 0);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReactionCanvas1;
}

/**
 * Gets the reactionDescription property (cbit.vcell.dictionary.ReactionDescription) value.
 * @return The reactionDescription property value.
 * @see #setReactionDescription
 */
public cbit.vcell.dictionary.ReactionDescription getReactionDescription() {
	return fieldReactionDescription;
}


/**
 * Return the selectionModel2 property value.
 * @return javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ListSelectionModel getReactionSelectionModel() {
	// user code begin {1}
	// user code end
	return ivjReactionSelectionModel;
}


/**
 * Return the ReactionsJList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getReactionsJList() {
	if (ivjReactionsJList == null) {
		try {
			ivjReactionsJList = new javax.swing.JList();
			ivjReactionsJList.setName("ReactionsJList");
			ivjReactionsJList.setBounds(0, 0, 160, 120);
			ivjReactionsJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReactionsJList;
}

/**
 * Gets the reactionStep property (cbit.vcell.model.ReactionStep) value.
 * @return The reactionStep property value.
 * @see #setReactionStep
 */
public cbit.vcell.model.ReactionStep getReactionStep() {
	return fieldReactionStep;
}


/**
 * Return the ResolveHighlightJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getResolveHighlightJLabel() {
	if (ivjResolveHighlightJLabel == null) {
		try {
			ivjResolveHighlightJLabel = new javax.swing.JLabel();
			ivjResolveHighlightJLabel.setName("ResolveHighlightJLabel");
			ivjResolveHighlightJLabel.setText(" ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResolveHighlightJLabel;
}


/**
 * Return the ResolverJPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getResolverJPanel() {
	if (ivjResolverJPanel == null) {
		try {
			cbit.gui.LineBorderBean ivjLocalBorder10;
			ivjLocalBorder10 = new cbit.gui.LineBorderBean();
			ivjLocalBorder10.setThickness(2);
			cbit.gui.TitledBorderBean ivjLocalBorder9;
			ivjLocalBorder9 = new cbit.gui.TitledBorderBean();
			ivjLocalBorder9.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder9.setBorder(ivjLocalBorder10);
			ivjLocalBorder9.setTitle("Resolve Reaction Participants with Model");
			ivjResolverJPanel = new javax.swing.JPanel();
			ivjResolverJPanel.setName("ResolverJPanel");
			ivjResolverJPanel.setBorder(ivjLocalBorder9);
			ivjResolverJPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsResolveHighlightJLabel = new java.awt.GridBagConstraints();
			constraintsResolveHighlightJLabel.gridx = 0; constraintsResolveHighlightJLabel.gridy = 2;
			constraintsResolveHighlightJLabel.gridwidth = 2;
			constraintsResolveHighlightJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getResolverJPanel().add(getResolveHighlightJLabel(), constraintsResolveHighlightJLabel);

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 0;
			constraintsJScrollPane1.gridwidth = 2;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
			getResolverJPanel().add(getJScrollPane1(), constraintsJScrollPane1);

			java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
			constraintsJScrollPane2.gridx = 0; constraintsJScrollPane2.gridy = 1;
			constraintsJScrollPane2.gridwidth = 2;
			constraintsJScrollPane2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane2.weightx = 1.0;
			constraintsJScrollPane2.weighty = 1.0;
			constraintsJScrollPane2.insets = new java.awt.Insets(4, 4, 4, 4);
			getResolverJPanel().add(getJScrollPane2(), constraintsJScrollPane2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResolverJPanel;
}

/**
 * Return the RXDescriptionLSM property value.
 * @return javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ListSelectionModel getRXDescriptionLSM() {
	// user code begin {1}
	// user code end
	return ivjRXDescriptionLSM;
}


/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRXParticipantsJPanel() {
	if (ivjRXParticipantsJPanel == null) {
		try {
			cbit.gui.LineBorderBean ivjLocalBorder14;
			ivjLocalBorder14 = new cbit.gui.LineBorderBean();
			ivjLocalBorder14.setThickness(2);
			cbit.gui.TitledBorderBean ivjLocalBorder13;
			ivjLocalBorder13 = new cbit.gui.TitledBorderBean();
			ivjLocalBorder13.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder13.setBorder(ivjLocalBorder14);
			ivjLocalBorder13.setTitle("Assign Reaction Participants To Model");
			ivjRXParticipantsJPanel = new javax.swing.JPanel();
			ivjRXParticipantsJPanel.setName("RXParticipantsJPanel");
			ivjRXParticipantsJPanel.setBorder(ivjLocalBorder13);
			ivjRXParticipantsJPanel.setLayout(new java.awt.GridBagLayout());
			ivjRXParticipantsJPanel.setBounds(0, 0, 459, 47);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRXParticipantsJPanel;
}

/**
 * Return the SearchJPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getSearchCriteriaJPanel() {
	if (ivjSearchCriteriaJPanel == null) {
		try {
			cbit.gui.TitledBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.TitledBorderBean();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Reaction Search");
			ivjSearchCriteriaJPanel = new javax.swing.JPanel();
			ivjSearchCriteriaJPanel.setName("SearchCriteriaJPanel");
			ivjSearchCriteriaJPanel.setBorder(ivjLocalBorder);
			ivjSearchCriteriaJPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
			constraintsJPanel1.gridwidth = 2;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getSearchCriteriaJPanel().add(getJPanel1(), constraintsJPanel1);

			java.awt.GridBagConstraints constraintsJPanel = new java.awt.GridBagConstraints();
			constraintsJPanel.gridx = 0; constraintsJPanel.gridy = 1;
			constraintsJPanel.gridwidth = 2;
			constraintsJPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel.weightx = 1.0;
			constraintsJPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getSearchCriteriaJPanel().add(getJPanel(), constraintsJPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSearchCriteriaJPanel;
}

/**
 * Return the SearchDictionaryJRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getSearchDictionaryJRadioButton() {
	if (ivjSearchDictionaryJRadioButton == null) {
		try {
			ivjSearchDictionaryJRadioButton = new javax.swing.JRadioButton();
			ivjSearchDictionaryJRadioButton.setName("SearchDictionaryJRadioButton");
			ivjSearchDictionaryJRadioButton.setText("KEGG Enzymatic Reactions");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSearchDictionaryJRadioButton;
}

/**
 * Return the SearchResultsJPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getSearchResultsJPanel() {
	if (ivjSearchResultsJPanel == null) {
		try {
			cbit.gui.LineBorderBean ivjLocalBorder6;
			ivjLocalBorder6 = new cbit.gui.LineBorderBean();
			ivjLocalBorder6.setThickness(2);
			cbit.gui.TitledBorderBean ivjLocalBorder5;
			ivjLocalBorder5 = new cbit.gui.TitledBorderBean();
			ivjLocalBorder5.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder5.setBorder(ivjLocalBorder6);
			ivjLocalBorder5.setTitleColor(java.awt.Color.black);
			ivjLocalBorder5.setTitle("1.  Select Reaction from List of Search Results");
			ivjSearchResultsJPanel = new javax.swing.JPanel();
			ivjSearchResultsJPanel.setName("SearchResultsJPanel");
			ivjSearchResultsJPanel.setBorder(ivjLocalBorder5);
			ivjSearchResultsJPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJScrollPane3 = new java.awt.GridBagConstraints();
			constraintsJScrollPane3.gridx = 0; constraintsJScrollPane3.gridy = 0;
			constraintsJScrollPane3.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane3.weightx = 1.0;
			constraintsJScrollPane3.weighty = 1.0;
			constraintsJScrollPane3.ipadx = 4;
			constraintsJScrollPane3.ipady = 4;
			constraintsJScrollPane3.insets = new java.awt.Insets(4, 4, 4, 4);
			getSearchResultsJPanel().add(getJScrollPane3(), constraintsJScrollPane3);

			java.awt.GridBagConstraints constraintsParameterJPanel = new java.awt.GridBagConstraints();
			constraintsParameterJPanel.gridx = 0; constraintsParameterJPanel.gridy = 1;
			constraintsParameterJPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsParameterJPanel.weightx = 1.0;
			constraintsParameterJPanel.weighty = 1.0;
			constraintsParameterJPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getSearchResultsJPanel().add(getParameterJPanel(), constraintsParameterJPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSearchResultsJPanel;
}

/**
 * Return the NameTypeButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getSearchTypeButtonGroup() {
	if (ivjSearchTypeButtonGroup == null) {
		try {
			ivjSearchTypeButtonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSearchTypeButtonGroup;
}


/**
 * Return the SearchUserJRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getSearchUserJRadioButton() {
	if (ivjSearchUserJRadioButton == null) {
		try {
			ivjSearchUserJRadioButton = new javax.swing.JRadioButton();
			ivjSearchUserJRadioButton.setName("SearchUserJRadioButton");
			ivjSearchUserJRadioButton.setSelected(true);
			ivjSearchUserJRadioButton.setText("VCell User Reactions");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSearchUserJRadioButton;
}

/**
 * Gets the structure property (cbit.vcell.model.Structure) value.
 * @return The structure property value.
 * @see #setStructure
 */
public cbit.vcell.model.Structure getStructure() {
	return fieldStructure;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getBackJButton().addActionListener(ivjEventHandler);
	getFinishJButton().addActionListener(ivjEventHandler);
	getNextJButton().addActionListener(ivjEventHandler);
	getReactionsJList().addPropertyChangeListener(ivjEventHandler);
	getParameterNamesJList().addPropertyChangeListener(ivjEventHandler);
	getJButton1().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getParameterValuesJList().addPropertyChangeListener(ivjEventHandler);
	getFindRXTextRadioButton().addActionListener(ivjEventHandler);
	getKeggMoleculeJRadioButton().addActionListener(ivjEventHandler);
	getKeggSpecifyJButton().addActionListener(ivjEventHandler);
	getFindTextJTextField().addPropertyChangeListener(ivjEventHandler);
	getFindTextJTextField().addActionListener(ivjEventHandler);
	connPtoP3SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
	connPtoP6SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DBReactionWizardPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(487, 438);

		java.awt.GridBagConstraints constraintsBFNJPanel = new java.awt.GridBagConstraints();
		constraintsBFNJPanel.gridx = 0; constraintsBFNJPanel.gridy = 1;
		constraintsBFNJPanel.gridwidth = 2;
		constraintsBFNJPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getBFNJPanel(), constraintsBFNJPanel);

		java.awt.GridBagConstraints constraintsCardLayoutJPanel = new java.awt.GridBagConstraints();
		constraintsCardLayoutJPanel.gridx = 0; constraintsCardLayoutJPanel.gridy = 0;
		constraintsCardLayoutJPanel.gridwidth = 2;
		constraintsCardLayoutJPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsCardLayoutJPanel.weightx = 1.0;
		constraintsCardLayoutJPanel.weighty = 1.0;
		constraintsCardLayoutJPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getCardLayoutJPanel(), constraintsCardLayoutJPanel);

		java.awt.GridBagConstraints constraintsJButton1 = new java.awt.GridBagConstraints();
		constraintsJButton1.gridx = 1; constraintsJButton1.gridy = 1;
		constraintsJButton1.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJButton1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJButton1(), constraintsJButton1);
		initConnections();
		connEtoC4();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Insert the method's description here.
 * Creation date: (8/15/2003 6:07:56 PM)
 * @return boolean
 * @param sc cbit.vcell.model.SpeciesContext
 */
private boolean isSCResolvable(cbit.vcell.model.SpeciesContext speciesContext) {
	return
		(getStructure().equals(speciesContext.getStructure())) ||
	(
		getStructure() instanceof cbit.vcell.model.Membrane &&
		(
			((cbit.vcell.model.Membrane)getStructure()).getOutsideFeature().equals(speciesContext.getStructure()) ||
			((cbit.vcell.model.Membrane)getStructure()).getInsideFeature().equals(speciesContext.getStructure())
		)
	);

}


/**
 * Insert the method's description here.
 * Creation date: (4/5/2005 11:01:36 AM)
 */
private boolean lastSearchIsSameAsCurrent() {
	
	if(lastSearchChangeInfo == null){
		return false;
	}

	if(!((javax.swing.JRadioButton)lastSearchChangeInfo[0]).isSelected()){//Where search button
		return false;
	}

	if(!((javax.swing.JRadioButton)lastSearchChangeInfo[1]).isSelected()){//What to search for radio button
		return false;
	}

	String s = getFindTextJTextField().getText();
	if(s != null && s.length() == 0){
		s = null;
	}
	if(lastSearchChangeInfo[2] instanceof String){
		if(!cbit.util.Compare.isEqualOrNull(s,((String)lastSearchChangeInfo[2]))){
			return false;
		}
	}
	if(lastSearchChangeInfo[2] instanceof cbit.vcell.dictionary.DBFormalSpecies){
		if(((cbit.vcell.dictionary.DBFormalSpecies)lastSearchChangeInfo[2]) != getCurrentDBFormalSpecies()){
			return false;
		}
	}
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (4/5/2005 11:01:36 AM)
 */
private void lastSearchSaveInfo() {
	
	lastSearchChangeInfo = new Object[3];

	lastSearchChangeInfo[0] =
		(getSearchUserJRadioButton().isSelected()?getSearchUserJRadioButton():getSearchDictionaryJRadioButton());
		
	lastSearchChangeInfo[1] =
		(getFindRXTextRadioButton().isSelected()?getFindRXTextRadioButton():getKeggMoleculeJRadioButton());

	if(getFindRXTextRadioButton().isSelected()){
		String s = getFindTextJTextField().getText();
		if(s != null && s.length() == 0){
			s = null;
		}
		lastSearchChangeInfo[2] = s;
	}else{
		lastSearchChangeInfo[2] = getCurrentDBFormalSpecies();
	}
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DBReactionWizardPanel aDBReactionWizardPanel;
		aDBReactionWizardPanel = new DBReactionWizardPanel();
		frame.setContentPane(aDBReactionWizardPanel);
		frame.setSize(aDBReactionWizardPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
private void parameterNameSelectionChanged() {

	try{
		javax.swing.DefaultListModel dlm = (javax.swing.DefaultListModel)getParameterValuesJList().getModel();
		dlm.removeAllElements();
		//
		if(getParameterNamesJList().getSelectedValue() != null){

			final cbit.vcell.client.database.DocumentManager docManager = getDocumentManager();
			final javax.swing.JList jlist = getReactionsJList();
			final MapStringToObject parameNameMSO = (MapStringToObject)getParameterNamesJList().getSelectedValue();
			final KeyValue reactionStepKey = ((cbit.vcell.model.ReactionStepInfo)parameNameMSO.getToObject()).getReactionKey();
			//
			final String RXSTEP_HASH_VALUE_KEY = "rxStep";
			
			AsynchClientTask searchReactions = new AsynchClientTask() {
				public String getTaskName() {
					return "Getting Full Reaction";
				}
				public int getTaskType() {
					return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_NONSWING_BLOCKING;
				}
				public void run(java.util.Hashtable hash) throws cbit.util.DataAccessException{
					ReactionStep rStep = getDocumentManager().getReactionStep(reactionStepKey);
					if(rStep != null){
						hash.put(RXSTEP_HASH_VALUE_KEY,rStep);
					}
				}
				public boolean skipIfAbort() {
					return true;
				}
				public boolean skipIfCancel(UserCancelException e) {
					return true;
				}
			};
			//
			AsynchClientTask updateRXParams = new AsynchClientTask() {
				public String getTaskName() {
					return "updateRXParams";
				}
				public int getTaskType() {
					return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_SWING_BLOCKING;
				}
				public void run(java.util.Hashtable hash) throws cbit.util.DataAccessException{
					ReactionStep rStep = (ReactionStep)hash.get(RXSTEP_HASH_VALUE_KEY);
					if(rStep != null){
						Kinetics.KineticsParameter[] kpArr = rStep.getKinetics().getKineticsParameters();
						ReactionParticipant[] rpArr = rStep.getReactionParticipants();
						//
						javax.swing.DefaultListModel pvdlm = (javax.swing.DefaultListModel)getParameterValuesJList().getModel();
						pvdlm.removeAllElements();
						for(int i=0;i < kpArr.length;i+= 1){
							pvdlm.addElement("Parameter - "+kpArr[i].getName().toString()+" = "+kpArr[i].getExpression().infix());
						}
						pvdlm.addElement("ChargeCarrierValence="+rStep.getChargeCarrierValence().getExpression().infix());
						pvdlm.addElement("PhysicsOption="+rStep.getPhysicsOptions());
						for(int i=0;i < rpArr.length;i+= 1){
							String role = "Unknown";
							if(rpArr[i] instanceof Reactant){
								role = "Reactant";
							}else if(rpArr[i] instanceof Product){
								role = "Product";
							}else if(rpArr[i] instanceof Catalyst){
								role = "Catalyst";
							}else if(rpArr[i] instanceof Flux){
								role = "Flux";
							}
							String fluxFlag = "";
							if(rStep instanceof FluxReaction){
								Membrane rStepStruct = (Membrane)rStep.getStructure();
								if(rpArr[i] instanceof Flux){
									if(rpArr[i].getStructure().equals(rStepStruct.getOutsideFeature())){
										fluxFlag = "Outside";
									}else{
										fluxFlag = "Inside";
									}
								}
							}
							pvdlm.addElement("RXParticipant("+role+") "+fluxFlag+" "+(rpArr[i].getSpecies().getDBSpecies() != null?"*":"-")+" "+rpArr[i].getSpeciesContext().getName());
						}
					}
					setReactionStep(rStep);
					configureBFN();
				}
				public boolean skipIfAbort() {
					return true;
				}
				public boolean skipIfCancel(UserCancelException e) {
					return true;
				}
			};
			//	
			java.util.Hashtable hashTemp = new java.util.Hashtable();
			ClientTaskDispatcher.dispatch(this,hashTemp,new AsynchClientTask[] {searchReactions,updateRXParams},true);
		}else{
			setReactionStep(null);
		}
	}catch(Exception e){
		cbit.vcell.client.PopupGenerator.showErrorDialog(this,e.getMessage());
	}
	//
	configureBFN();
	
}


/**
 * Comment
 */
private void reactionListSelectionChanged() {
	//
	if(!getReactionSelectionModel().getValueIsAdjusting()){
		configureBFN();
	}
}


/**
 * Comment
 */
private void resolve2() {

	setReactionDescription(null);
	
	for(int i=0;i<resolvedReaction.elementCount();i+= 1){
		Species species = speciesOrder[speciesAssignmentJCB[i].getSelectedIndex()];
		Structure structure = null;
		SpeciesContext speciesContext = null;
		
		if(getStructure() instanceof Feature){
			structure = getStructure();
		}else if(resolvedReaction.isFluxReaction() && i == 0){
			structure = ((Membrane)getStructure()).getOutsideFeature();
		}else if(resolvedReaction.isFluxReaction() && i == 1){
			structure = ((Membrane)getStructure()).getInsideFeature();
		}else if(structureAssignmentJCB[i].getSelectedIndex() == 0){
			structure = getStructure();
		}else if(structureAssignmentJCB[i].getSelectedIndex() == 1){
			structure = ((Membrane)getStructure()).getOutsideFeature();
		}else if(structureAssignmentJCB[i].getSelectedIndex() == 2){
			structure = ((Membrane)getStructure()).getInsideFeature();
		}

		if(species != null){
			speciesContext = getModel().getSpeciesContext(species,structure);
		}else if(resolvedReaction.isFluxReaction() && i == resolvedReaction.getFluxIndexInside()){
			//get the same species(flux carrier) for "Inside" as we generated for "Outside"
			//Note: ReactionDescription always has Outside flux at index 0 and Inside flux at index 1
			species = resolvedReaction.getResolved(resolvedReaction.getFluxIndexOutside()).getSpecies();
		}else{
			cbit.vcell.dictionary.DBSpecies dbSpecies = null;
			cbit.vcell.dictionary.SpeciesDescription dbsd = resolvedReaction.getReactionElement(i);
			if(dbsd instanceof cbit.vcell.dictionary.DBFormalSpecies){//get DBSpecies (Dictionary Reactions)
				try{
					dbSpecies = getDocumentManager().getBoundSpecies((cbit.vcell.dictionary.DBFormalSpecies)dbsd);
				}catch(cbit.util.DataAccessException e){
					//Do Nothing, this SC won't be bound in database, user can do it later
				}
			}else{//get DBSpecies (user Reactions)
				String origSCName = resolvedReaction.getOrigSpeciesContextName(resolvedReaction.getDBSDIndex(dbsd));
				ReactionParticipant[] rPart = getReactionStep().getReactionParticipants();
				for(int j=0;j<rPart.length;j+= 1){
					if(rPart[j].getSpeciesContext().getName().equals(origSCName)){
						dbSpecies = rPart[j].getSpecies().getDBSpecies();
					}
				}
			}
			species =
				new cbit.vcell.model.Species(cbit.util.TokenMangler.fixTokenStrict(dbsd.getPreferredName()),
					null,
					dbSpecies);
		}

		if(speciesContext == null){
			speciesContext = new SpeciesContext(species,structure);
		}

		try{
			resolvedReaction.resolve(i,speciesContext);
		}catch(IllegalArgumentException e){
			cbit.vcell.client.PopupGenerator.showErrorDialog("Error Resolving RX Elements --\n"+e.getMessage());
			return;
		}
		
		//System.out.println("\n"+
			//resolvedReaction.getReactionElement(i).getPreferredName()+
			//" resolved to "+speciesContext.toString()+"\n");
	}

	setReactionDescription(resolvedReaction);

	done();
}


/**
 * Comment
 */
private void search(){

	if(getDocumentManager() != null){
		String textSearchS = getFindTextJTextField().getText();
		if(textSearchS != null && textSearchS.length() == 0){
			textSearchS = null;
		}
		final ReactionQuerySpec reactionQuerySpec =
			new ReactionQuerySpec(
				(getFindRXTextRadioButton().isSelected()?formatLikeString(textSearchS):null),
				(getKeggMoleculeJRadioButton().isSelected()?getCurrentDBFormalSpecies():null));
			
		if(getSearchUserJRadioButton().isSelected()){
			searchUserReactions(reactionQuerySpec);
			return;
		}
		final cbit.vcell.client.database.DocumentManager docManager = getDocumentManager();
		final javax.swing.JList jlist = getReactionsJList();
		//
		final String RXDESC_VALUE_KEY = "rxDesc";
		AsynchClientTask searchReactions = new AsynchClientTask() {
			public String getTaskName() {
				return "searching reactions";
			}
			public int getTaskType() {
				return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_NONSWING_BLOCKING;
			}
			public void run(java.util.Hashtable hash) throws cbit.util.DataAccessException{
				ReactionDescription[] dbfr = docManager.getDictionaryReactions(reactionQuerySpec);
				if(dbfr != null && dbfr.length >0){
					hash.put(RXDESC_VALUE_KEY,dbfr);
				}
			}
			public boolean skipIfAbort() {
				return true;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return true;
			}
		};
		//
		AsynchClientTask updateRXList = new AsynchClientTask() {
			public String getTaskName() {
				return "updateRXList";
			}
			public int getTaskType() {
				return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_SWING_BLOCKING;
			}
			public void run(java.util.Hashtable hash){
				ReactionDescription[] dbfr = (ReactionDescription[])hash.get(RXDESC_VALUE_KEY);
				if(dbfr != null){
					jlist.setListData(dbfr);
				}else{
					jlist.setListData(new cbit.vcell.dictionary.ReactionDescription[0]);
				}
				afterSearchConfigure();
			}
			public boolean skipIfAbort() {
				return true;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return true;
			}
		};
		//	
		java.util.Hashtable hashTemp = new java.util.Hashtable();
		ClientTaskDispatcher.dispatch(this,hashTemp,new AsynchClientTask[] {searchReactions,updateRXList},false);
	}
}


/**
 * Comment
 */
public void searchDictionaryJRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (7/12/2003 2:45:44 PM)
 */
private void searchUserReactions(final ReactionQuerySpec reactionQuerySpec){

	if(getDocumentManager() != null){
		final cbit.vcell.client.database.DocumentManager docManager = getDocumentManager();
		final javax.swing.JList jlist = getReactionsJList();
		//
		final String RXSTRING_VALUE_KEY = "rxString";
		//
		AsynchClientTask searchReactions = new AsynchClientTask() {
			public String getTaskName() {
				return "searching reactions";
			}
			public int getTaskType() {
				return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_NONSWING_BLOCKING;
			}
			public void run(java.util.Hashtable hash){
				try{
					mapRXStringtoRXIDs.clear();
					//
					cbit.vcell.dictionary.ReactionDescription[] dbrd = docManager.getUserReactionDescriptions(reactionQuerySpec);
					//
					if(dbrd != null && !(getStructure() instanceof cbit.vcell.model.Membrane)){
						java.util.Vector noflux = new java.util.Vector();
						for(int i=0;i<dbrd.length;i+= 1){
							if(!dbrd[i].isFluxReaction()){
								noflux.add(dbrd[i]);
							}
						}
						if(noflux.size() > 0){
							dbrd = new cbit.vcell.dictionary.ReactionDescription[noflux.size()];
							noflux.copyInto(dbrd);
						}
					}
					//
					String[] dbrdS = null;
					if(dbrd != null){
						//if(mapRXStringtoRXIDs == null){mapRXStringtoRXIDs = new java.util.Hashtable();}
						for(int i=0;i<dbrd.length;i+= 1){
							String rxString = dbrd[i].toString();
							if(!mapRXStringtoRXIDs.containsKey(rxString)){
								mapRXStringtoRXIDs.put(rxString,new java.util.Vector());
							}
							((java.util.Vector)mapRXStringtoRXIDs.get(rxString)).add(dbrd[i].getVCellRXID().toString());
						}
						dbrdS = (String[])mapRXStringtoRXIDs.keySet().toArray(new String[0]);
					}
					//
					if(dbrd != null && dbrd.length >0){
						hash.put(RXSTRING_VALUE_KEY,dbrdS);
					}
				}catch(cbit.util.DataAccessException e){
					cbit.vcell.client.PopupGenerator.showErrorDialog(DBReactionWizardPanel.this,e.getMessage());
				}
			}
			public boolean skipIfAbort() {
				return true;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return true;
			}
		};
		//
		AsynchClientTask updateRXList = new AsynchClientTask() {
			public String getTaskName() {
				return "updateRXList";
			}
			public int getTaskType() {
				return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_SWING_BLOCKING;
			}
			public void run(java.util.Hashtable hash){
				String[] dbrdS = (String[])hash.get(RXSTRING_VALUE_KEY);
				if(dbrdS != null){
					jlist.setListData(dbrdS);
				}else{
					jlist.setListData(new String[0]);
				}
				afterSearchConfigure();
			}
			public boolean skipIfAbort() {
				return true;
			}
			public boolean skipIfCancel(UserCancelException e) {
				return true;
			}
		};
		//	
		java.util.Hashtable hashTemp = new java.util.Hashtable();
		ClientTaskDispatcher.dispatch(this,hashTemp,new AsynchClientTask[] {searchReactions,updateRXList},false);
	}	
}


/**
 * Set the CurrentDBFormalSpecies to a new value.
 * @param newValue cbit.vcell.dictionary.DBFormalSpecies
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setCurrentDBFormalSpecies(cbit.vcell.dictionary.DBFormalSpecies newValue) {
	if (ivjCurrentDBFormalSpecies != newValue) {
		try {
			ivjCurrentDBFormalSpecies = newValue;
			connEtoC30(ivjCurrentDBFormalSpecies);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

/**
 * Set the document2 to a new value.
 * @param newValue javax.swing.text.Document
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdocument2(javax.swing.text.Document newValue) {
	if (ivjdocument2 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjdocument2 != null) {
				ivjdocument2.removeDocumentListener(ivjEventHandler);
			}
			ivjdocument2 = newValue;

			/* Listen for events from the new object */
			if (ivjdocument2 != null) {
				ivjdocument2.addDocumentListener(ivjEventHandler);
			}
			connPtoP6SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}


/**
 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @param documentManager The new value for the property.
 * @see #getDocumentManager
 */
public void setDocumentManager(cbit.vcell.client.database.DocumentManager documentManager) {
	cbit.vcell.client.database.DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange("documentManager", oldValue, documentManager);
}


/**
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(cbit.vcell.model.Model model) {
	cbit.vcell.model.Model oldValue = fieldModel;
	fieldModel = model;
	firePropertyChange("model", oldValue, model);
}


/**
 * Set the ParameterNameSelectionModel to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setParameterNameSelectionModel(javax.swing.ListSelectionModel newValue) {
	if (ivjParameterNameSelectionModel != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjParameterNameSelectionModel != null) {
				ivjParameterNameSelectionModel.removeListSelectionListener(ivjEventHandler);
			}
			ivjParameterNameSelectionModel = newValue;

			/* Listen for events from the new object */
			if (ivjParameterNameSelectionModel != null) {
				ivjParameterNameSelectionModel.addListSelectionListener(ivjEventHandler);
			}
			connPtoP4SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}


/**
 * Sets the reactionDescription property (cbit.vcell.dictionary.ReactionDescription) value.
 * @param reactionDescription The new value for the property.
 * @see #getReactionDescription
 */
private void setReactionDescription(cbit.vcell.dictionary.ReactionDescription reactionDescription) {
	cbit.vcell.dictionary.ReactionDescription oldValue = fieldReactionDescription;
	fieldReactionDescription = reactionDescription;
	firePropertyChange("reactionDescription", oldValue, reactionDescription);
}


/**
 * Set the selectionModel2 to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setReactionSelectionModel(javax.swing.ListSelectionModel newValue) {
	if (ivjReactionSelectionModel != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjReactionSelectionModel != null) {
				ivjReactionSelectionModel.removeListSelectionListener(ivjEventHandler);
			}
			ivjReactionSelectionModel = newValue;

			/* Listen for events from the new object */
			if (ivjReactionSelectionModel != null) {
				ivjReactionSelectionModel.addListSelectionListener(ivjEventHandler);
			}
			connPtoP3SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}


/**
 * Sets the reactionStep property (cbit.vcell.model.ReactionStep) value.
 * @param reactionStep The new value for the property.
 * @see #getReactionStep
 */
private void setReactionStep(cbit.vcell.model.ReactionStep reactionStep) {
	ReactionStep oldValue = fieldReactionStep;
	fieldReactionStep = reactionStep;
	firePropertyChange("reactionStep", oldValue, reactionStep);
}


/**
 * Set the RXDescriptionLSM to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setRXDescriptionLSM(javax.swing.ListSelectionModel newValue) {
	if (ivjRXDescriptionLSM != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjRXDescriptionLSM != null) {
				ivjRXDescriptionLSM.removeListSelectionListener(ivjEventHandler);
			}
			ivjRXDescriptionLSM = newValue;

			/* Listen for events from the new object */
			if (ivjRXDescriptionLSM != null) {
				ivjRXDescriptionLSM.addListSelectionListener(ivjEventHandler);
			}
			connPtoP5SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}


/**
 * Sets the structure property (cbit.vcell.model.Structure) value.
 * @param structure The new value for the property.
 * @see #getStructure
 */
public void setStructure(cbit.vcell.model.Structure structure) {
	cbit.vcell.model.Structure oldValue = fieldStructure;
	fieldStructure = structure;
	firePropertyChange("structure", oldValue, structure);
}


/**
 * Insert the method's description here.
 * Creation date: (8/5/2003 2:50:56 PM)
 * @param dbfr cbit.vcell.dictionary.ReactionDescription
 */
private void setupRX(ReactionDescription dbfr) {

	resolvedReaction = dbfr;

	if(resolvedReaction != null){

		if(speciesAssignmentJCB != null){
			for(int i=0;i<speciesAssignmentJCB.length;i+= 1){
				speciesAssignmentJCB[i].removeActionListener(this);
			}
		}
		if(structureAssignmentJCB != null){
			for(int i=0;i<structureAssignmentJCB.length;i+= 1){
				structureAssignmentJCB[i].removeActionListener(this);
			}
		}

		getReactionCanvas1().setReactionCanvasDisplaySpec(resolvedReaction.toReactionCanvasDisplaySpec());

		getRXParticipantsJPanel().removeAll();
		java.awt.Insets zeroInsets = new java.awt.Insets(0,0,0,0);
		java.awt.Insets fourInsets = new java.awt.Insets(4,4,4,4);
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = fourInsets;
		gbc.gridx = 0;
		gbc.gridy = 0;

		javax.swing.JLabel rxjlabel = new javax.swing.JLabel("RX Elements");
		//rxjlabel.setForeground(java.awt.Color.white);
		//rxjlabel.setOpaque(true);
		//rxjlabel.setBackground(java.awt.Color.white);
		getRXParticipantsJPanel().add(rxjlabel,gbc);
		
		//gbc.insets = zeroInsets;
		for(int i=0;i<resolvedReaction.elementCount();i+= 1){
			gbc.gridy = i+1;
			javax.swing.JLabel jlabel =
				new javax.swing.JLabel(
					resolvedReaction.getReactionElement(i).getPreferredName()+
					(resolvedReaction.isFluxReaction() && resolvedReaction.getFluxIndexOutside()==i?" (Outside)":"") +
					(resolvedReaction.isFluxReaction() && resolvedReaction.getFluxIndexInside()==i?" (Inside)":""));
			//jlabel.setOpaque(true);
			//jlabel.setBackground(java.awt.Color.white);
			//jlabel.setForeground(java.awt.Color.black);
			getRXParticipantsJPanel().add(jlabel,gbc);
		}

		//gbc.insets = fourInsets;
		gbc.gridx = 1;
		gbc.gridy = 0;

		speciesAssignmentJCB = new javax.swing.JComboBox[resolvedReaction.elementCount()];
		
		javax.swing.JLabel rspjlabel = new javax.swing.JLabel("Assign to Model Species");
		//rspjlabel.setForeground(java.awt.Color.white);
		//rspjlabel.setOpaque(true);
		//rspjlabel.setBackground(java.awt.Color.white);
		getRXParticipantsJPanel().add(rspjlabel,gbc);
		//getRXParticipantsJPanel().add(new javax.swing.JLabel("Resolve to Model Species"),gbc);
		speciesOrder = new Species[getModel().getSpecies().length+1];
		speciesOrder[0] = null;
		for(int j=0;j<getModel().getSpecies().length;j+= 1){
			speciesOrder[j+1] = getModel().getSpecies(j);
		}
		for(int i=0;i<resolvedReaction.elementCount();i+= 1){
			javax.swing.JComboBox jcb = new javax.swing.JComboBox();
			speciesAssignmentJCB[i] = jcb;
			jcb.addItem("New Species");
			for(int j=1;j<speciesOrder.length;j+= 1){
				jcb.addItem("Existing "+speciesOrder[j].getCommonName());
			}
			gbc.gridy = i+1;
			getRXParticipantsJPanel().add(jcb,gbc);
		}
		
		gbc.gridx = 2;
		gbc.gridy = 0;
		structureAssignmentJCB = new javax.swing.JComboBox[resolvedReaction.elementCount()];

		javax.swing.JLabel rstjlabel = new javax.swing.JLabel("Assign to Model Compartment");
		//rstjlabel.setForeground(java.awt.Color.white);
		//rstjlabel.setOpaque(true);
		//rstjlabel.setBackground(java.awt.Color.white);
		getRXParticipantsJPanel().add(rstjlabel,gbc);
		//getRXParticipantsJPanel().add(new javax.swing.JLabel("Resolve to Model Compartment"),gbc);
		for(int i=0;i<resolvedReaction.elementCount();i+= 1){
			javax.swing.JComboBox jcb = new javax.swing.JComboBox();
			structureAssignmentJCB[i] = jcb;
			if(resolvedReaction.isFluxReaction() && resolvedReaction.isFlux(i) && resolvedReaction.getFluxIndexOutside() == i){
				jcb.addItem(((Membrane)getStructure()).getOutsideFeature().getName());
				jcb.setEnabled(false);
			}else if(resolvedReaction.isFluxReaction() && resolvedReaction.isFlux(i) && resolvedReaction.getFluxIndexInside() == i){
				jcb.addItem(((Membrane)getStructure()).getInsideFeature().getName());
				jcb.setEnabled(false);
			}else{
				jcb.addItem(getStructure().getName());
				if(getStructure() instanceof Membrane){
					jcb.addItem(((Membrane)getStructure()).getOutsideFeature().getName());
					jcb.addItem(((Membrane)getStructure()).getInsideFeature().getName());
				}else{
					jcb.setEnabled(false);
				}
			}
			gbc.gridy = i+1;
			getRXParticipantsJPanel().add(jcb,gbc);
		}
		//
		for(int i=0;i<resolvedReaction.elementCount();i+= 1){
			speciesAssignmentJCB[i].addActionListener(this);
			structureAssignmentJCB[i].addActionListener(this);
		}
	}
}


/**
 * Comment
 */
private cbit.vcell.dictionary.DBFormalSpecies showSpeciesBrowser() {

	SpeciesQueryDialog sqd = new SpeciesQueryDialog((java.awt.Frame)null,true);
	sqd.setSearchableTypes(
		SpeciesQueryPanel.SEARCHABLE_ENZYME | 
		SpeciesQueryPanel.SEARCHABLE_COMPOUND | 
		(getSearchUserJRadioButton().isSelected()?SpeciesQueryPanel.SEARCHABLE_PROTEIN:0x00L));
	//boolean isDictSearch = getJTabbedPane1().getTitleAt(getJTabbedPane1().getSelectedIndex()).equals("Dictionary");
	//if(getSearchDictionaryJRadioButton().isSelected()){
		//if(getAnyJRadioButton().isSelected()){
			//sqd.setSearchableTypes(SpeciesQueryPanel.SEARCHABLE_ENZYME | SpeciesQueryPanel.SEARCHABLE_COMPOUND);
		//}else{
			//sqd.setSearchableTypes((getCatalystJRadioButton().isSelected()?SpeciesQueryPanel.SEARCHABLE_ENZYME:SpeciesQueryPanel.SEARCHABLE_COMPOUND));
		//}
	//}
	sqd.setDocumentManager(getDocumentManager());
	sqd.setSize(550,500);
	cbit.util.BeanUtils.centerOnScreen(sqd);
	cbit.gui.ZEnforcer.showModalDialogOnTop(sqd,this);
	//sqd.setVisible(true);

	cbit.vcell.dictionary.DBFormalSpecies dbfs = null;
	cbit.vcell.dictionary.database.DictionaryQueryResults dqr = sqd.getDictionaryQueryResults();
	if(dqr != null && dqr.getSelection() != null){
		dbfs = dqr.getDBFormalSpecies()[dqr.getSelection()[0]];
	}

	if(dbfs != null){
		setCurrentDBFormalSpecies(dbfs);
	}
	//if(dbfs != null){
		//if(getReactantsJRadioButton().isSelected()){
			//getSearchDBReactionElementsPanel().reactionAddReactant(dbfs,1);
		//}else if(getEnzymeJRadioButton().isSelected()){
			//getSearchDBReactionElementsPanel().reactionAddCatalyst(dbfs);
		//}else if(getProductsJRadioButton().isSelected()){
			//getSearchDBReactionElementsPanel().reactionAddProduct(dbfs,1);
		//}
	//}
	
	return dbfs;
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G4E0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15D3D8DDCD4D57AB80EE5E5C99BEDD45417EDA929304825222215DAAB36E825160A0AAD3628E82396DBACF6F30B7272E22220E212DAD20DE686C93CA3AC22022021222192D222B2B097989918F1E6D0D44C5FF34E394F19BBF76E4CGADFE767F7763433D67B94FB94F4BF94EB94FBD675CBBC24E97FE73D2D2F384A1E51EA07D2BCC910458E78561E56B7A7508739AE84D94E47FD683AC95366952616A87
	F9BE5B8EE4E68BBF65E7C0BA9852F9D587B2D7407DB261C8FE6F23F06304CFAD646DE8DBFA13EBBE878B114FFC1A968EE641F5B1C0886048B55D588A7CD79EDCA961E3A4BCC2CAAA68C4E94A8E66CA38C6C8F7G7882849999FDB8DC97D04E97D4D7C9F46D178788C9FFB227AEA275486AC448B33132CD52D5A33C1B12E4A23C5A291C244CC4C807829065538441DC8C571A663E1E4DDD1DFAEB6171AE51E0A86CB76AC4C3E14F203E3A3AE16BBED15BE5559B878E68CFE94D3AE66D00E8B09DA3F57A9F4CE0F588
	D510CE263896B361938C7791GF19CFF63F1022FB537E63681184C285B262FEBA4595BCE5FA344757CF14192A73ACDB2B31D024CB65D1A3F69CC7B0436437F9591FBA7C0DE3625B5F3ADC0A3C0AF40A400D5347DEFA87C862EF51D56FAE3FF3FF1204EE45AEA5019F793DD613E3A9AF2A45CBE314BE8568982EB634E0385AA723882D64EB46AF24CA681E5585E017B2689B1F7DE15BADF45A6B18127D37DAC4C96A1961BCD986F29CEF9C7DB6C7D511E7781C14EDBF83DF47EBC955E394F144ECF106A912CB65ECD
	D65256F15487B4F01F427A0840707F24F8AF991E7966CF94CF52211057B8083E513698E5E9CA3FC148AE39DA558EBE8FFA26C68DB299928615325CFB02543D72B86379B51765E90AB74970CC16DB291F12F4B864258F35E6B27F72AFA1FD3576E4EBE6BB00954093A090A09CA066A47A585F8B0BC668E34DDA33F56B205E20934D82AB6BDF349C2E6210552C5DB1E031EA873A44D62DD92F6DB40896C122392D085820532E5C9D54773B0047F76200E856124E8A35EB7BC52BE836B01A69C5443EDDE6116636687B
	C5E050EF92305E070A1641552035D8FFB06934D651C64172E79611B6B92EED059A218238E7F6F9221458AB99744FB9857D6E945AE1FBB129EF17E80651223A3A49E86C9BB43590CD846BA9E26799F46C28027B66D3E863474A88AE8452113C1EB3459F496AE9505311C8EBBE09FD6C8D33862EF1C01FF13AB533G2099E088409BA098A07AB47A531BED68CF8D0BEE938A1E3ED2551F3C403793CFB3BF4A98D6762B77A8BFA2F3B164B58318G3CG02GA2819207F1EE68B0FB486AC5F9C6B39EC4CB748512CE28
	67C84AB6CBF4CC16736DE4A1734712D14831D86A1FD89F19984938BFEF04E597C8D7564E9BAB084FE41F017682E8BA03ED9DD22CE1FDE72B56A2E2EF795E8C0EAE6AECAD4F74794309A6C9A69298FC8BC07ADD8940F7GBBGFAGCEG002B882781FEG508374817483DCGF084C08BD0466D74BC2DD787B2F982848344G247F8C328234G8C837883C4GA481641EED4DECGB0GF88304824481E4F934E616FB20AF5CFE3CB8E564B69E8DA875AF9BE1B93413DA5E8FAAB8349526FB9476D4524BED2F4C3BE0
	04B2CAB156091158AEED8C361BA771A07EC9FC7A6B9116F355FFBED141FD897015221D094FFF8670058ABD99BBBF86D8211237FA04B232BEB52D0448A99E97872C8D22C99C50410DC54A3B2B0468AD4DB75DC6DEE6BA451BE0FE2A07894142718F14903935D62BD95FB9E895770BD62BFE2047167FA865E5996CB45926B66495D9C2ECEDBC81739327FF3A040C6DDD46018111F6EEDEC44AFF164EAD22C16C87216B0D0383D6067FEBA909417B4413525C76D2690782191B8C03E41EE4F1ED10163427492C6F07A1
	03CC35C2208719F71E65F1B901F64011785C832E5E9EB8CF1C789267093527268AB30E18D255E2A01F9BAFCBCD721066098936F9024974E4193A6C51F40E137096A66B5B39242DD583ED492F17898F4EA7B47BC52D392B376524C95CBAE8359A873EB39B87CD1C660E8292BFE0AD7B01893D21418752CC4FA5F27068E08FDC94040CEEE1C9B6292B2DC134F41975A6125D34FFB74BFBFEAB71036D7A815D3EB6B5B93EBAC064289FB41B0957ED5DEEB477EB8D7BCDE2171E348F23B9F60458DAE76C9AA406A895F0
	EE30EEA0F64B83FB56G34CC403921251C64E39CA0CD8A75C603A4BAE90F8924FD37D9F2E4EDE418405A173651F9A47F984F0F053CB98E794B2D185F8C695E73147953F97E90243DE4790CFF4720EB7E1EBC5F8F52A18E75279E477CC4C8E71CEF4BE7F6282BA06DE2910F8D0A909E1200EA976F870D959FA972DA25BE41721BABC81FB048E2312173894F4F0770B9F425442FD83AB61E4FF8B33AE8CE97C6728112A53A1A09F2BA5F13C8B78CF86F094E681ACFA1DDB060A3D4681AAFA0F4692711AE81706900DB
	FB810B3DEA617AB927FB1C5379C2BA98A06A82961FA5CAF40C2F4FB0528997327C1597B23E3A8B65729DB903F4DE008FG881768B2AE1253FD79B3522D3DC812DF3A36DFA45763C5CEB784F8AF00200BD86C98F191132FF892211BFA9669E2247CA409BEFB121C2FCE60F61E446C61BD09710D3523DB1F02F44597C31B8274DE4C6882AE16537D03534583DE3844995D14D42453GCD150C2E6D92B95D01B424733B044546A117B03A70CB64F6D9120EF4C9004F1A8C3156E496C793FADB3BDD27C13ADAA93FED32
	FDFD4CFEED2B114EB41955B7E9B2732FF03846CE46350F377349DCC057BE0EEA0FEB078AADA7E0BE2B2E5EA9DB6328EBA763B521CF1A0CEB997DC74898D56089F19FC03BA74E9BCFAC42358C6F3EGA1667512794E56E92C1E521861E91BB7FA936BBEE4E3607D1504E7B46445FB6278B733C27EBCD5E731983B7474D1CE6A5B27B648679506BB3BE1A1DC3B75E60BF587CC7CC3B88EDC300148CB1E75A434B4E73EDF78192ABD6C47F8266B196DF2DDC3171CC94DF422EB3224E36E25B6DD7D86DA663367ED9FCB
	16406604F6487786083814BE1FDB489CCF7AB96357D5067C3CCE5DA8F4DCF92DD3FE593F12FC61D7B6FE85BF4F194778ED32A23FDE48777B9572BBE5A57C48F84168DE66F451C013FD9952FDB1C868C83FE5F4F78E623ADAA350F8DC063E9530FF3D60382E2663CFB1E4E14DFEE2E475659FC73EA1D0D7A2675BFD1C70A57D0251FDB714A535DB1ED7EBE687G1BD727D13CFEC0EF55EB8D7AD3E2DEBE13E7FB9309C1F5EAB146119DB26AD629FDEFD8D6AB59B3E25A8DC24D594329EA6BF2131674267AF8B1BB4F
	703259FB68571DE91EGCC561413A8EB566550E7G58DC796FE37239F298707EG611723AE3DA7C9DC4064ADE417E6515C8DD693F5F944C38B35A72C05B436AD2C23595B48FDBE7A7379F964F92833FB20CED13649F9610DE081DC8B7AA317BB86E44A79B54A1D28D7133B695758B6A7CFE1398ED206175BA12A150B64653EBD0DED9F87385CAB30DC56D1F93996408F8178DE01F67A3B0560AD2A71DE48E962B756DE3D05356BDBF572F68D196F295A2E3DB77B2486DD415AB37A8ADB3B6A2E6FCC9B3A9E5B759D
	AEEF1EB77820B74A7BD90AD2DE9F488B75C6F93D2C4E65BDEF9865E5BC9E98C6DBC6C2790CAB310E3CB3C48F035EE26561EBA1FD82286F558E74C87EA10DE0D2ABB749091927B0BF39EF33C6204FBBAAB1B1D6175F4A6419F486652985D9062EC41D57F5AA5B162C9185F1F97FF1661779F2EBAB29374BE03408903CC38E692A121FB67D0CB205813FE44E734E1F491CE116FA3CAC12176A1CE8A2ED8F4FDF5D7A1EC13338F57B9ED2A92BB363AC36ED79D52D19062BD04FCF3A15ED6B8FF961D7A14F0AC167EDFB
	45D97B36FD1273080172B9D7E39D7FE13133DD5B229DA6F452F5CC83BC8871CCEA8AE95C3A6DF339FFE7E8AFD075EF4F57FBD21B2EE6FEED3D5A665F31E44C62635625825AB594E81238FCC942FF4F7776FED5A7EF03FDED7C510C94E1A3153B2A64E3D6CB8A4ADC6083712C8F774F92F93B7982BE9CA04187F51A1E724B7CF356D2D23E9B1E896B4DFA10DE2FC5E3389BEC195CABD3D1EE619A08A92FC139DFA8D7135BF48D0E19913C5CC4400572F2495FA87BA3A917F48D4E4FAF97EEA4FE5AEBBCA14D3FDB4D
	469396511C175F24BADF97AA266B0D125C9EE948BF63DAF89E39966D79DA9A7ABA637717344F9476E9902DDA3D41E29BE0F89BD625E33F98027A022EC51D169549F50A3F1669147DFF48F378B8098171191A5575088665DB8BF46D1C36F0357C793B10731E309A693D01B60253472F7EE57E6135B013722BB7683B7A72F60036AC3E7EFA8D6EA1267B62DA6976F6655ED50D2FED4D3BD9F60F4076F4DE22F1394E176D694C6778D994BFAC43B3DDAD94CF525E10B7479777A1AE28A64F856D3F01719D408BA0GA0
	9CA07EB7980FAF2AA46B2CAE77F9F68CC0BB8F004758AD0861FECBA55139CFBCA9259F29A4F622230800EBA1EFEFA46D1DFEDDEB2686208A209940F49D4A30274A2D8C5B06D4E5E0F63E4EC26C6CF55D4877A4D839C0DAAEE0946558581C7F13FC9FBBFD694E54B4153119469CD25D71575946E6E6935FCD44A6F93F05B892209D408A607DDB3409CFD501BB1B601E1AE48C26D33DF5EC3658EB9D2DADD83978B1727B5798795DBBB836BD6CC746D82E687858745BF4FCEC7A6D9ED24A997CDB7BBAA295E9D66E20
	C339C2C5BAC9D10E7961DB1B499AC316DFEBE69EC0ADC0BB008700AF408CBF7447D72BB63973C7BAD036986B8157E8B47655AB5743392C73CF0ECD470F9D4AFD2EC8477A5917E3E376127DC4D715759136A0GF9ECCDFDDD07F2CD1D443EF3204E2C6BE13C2263FC31B9C312E76D75B84E4F9C23BEFFF12BCF7B757665243966A84AE065B21CBDB5B699AEF5A82714A1D0D5868F91E58863B2141DF6574754E528F3DBAE79FA35F61D193FC23DDDC9E8A93569EFBA65ED1AF38378B8008EE06886EC537A2338B666
	F5834E1D7ADA52AF42A19D87108C30F2CAEBE68DC09B4070946C97D57F596A2EDF5461C6D30B115F2AF48EE60B7747E8434CE1F7653C275817E37639ABCFE91FC0200B84081B0276E9C9C17BA4CF4136FE44015F487C6DE907F24AF12A5CDF2D5C2F4E0C0D1F2FC3B9A53FA6FFB57FBE5409BA6B7CD167FDBF0FCD068E07F2CA99FCD5F5DEFCF6EC3E106B36DC303F1AAFC4242A79C28450467A23AFB4F723DD66F03BC40FD14E3F39ADD7FC235A1C75C29D691B0D1027839886708188850886C83E917B66A57FA1
	4FC6A31933D8246F58A9191CD788E3EB776B9D4AA947565C1B544A7D14B2B6FE3D8E65147C4AEFD29BC717EFA5F4AA6368BE2DCEEF44C9127948C35D4AB932896AB481CC3C89FD64D0894E8D3EB7210FFC1EBAB61D8A9C4AA9F50A3AC92D7FFEDC01FE1A40E5F8F20CB244BA14D3765F35B72B49D0369E6DD0F3B34AF0E15A58E470F6A8271441FA33DA394D69E36B1BCDEE4BF95E2C66CBDF65652A7BD20BB8E45D2E978DBAE61B1DC5783C6C4FED7318E6EC36F95E211C52B6B12A3639E8755878DD69D0CE49AF
	C915DF419AD2AEABE0644F846C0C61A13AF72701F265G8CD7FD18605AA0ED8AC07B3DC657813CA19D88904E71EFD3BCD9230F89E07B3F8CFF72C81B9B7E61CEE475F6028F1461990FDF3B65B15F895E59B9A879D9BDB64E7F2A5A795920AB2B554F867DDF357A59206B2B17B2BC54A1BF590A79D32BDD1F9D3A3D5A79592177B6BA17B335C6DD4E0C9AF5B997573816F3F90DB3B9BF2336FD062EB71A87872C7AFE11D64D7A2FB8C44EE5414C38EFF000CD1B24F60BF24DC140397873FA32BEA8CC45B977361AD2
	41F14E5D2653DB0DE62C24C5541A3F6F6E6641B0D90AC4397DB7B937512D1B54EDF47BA6F59B5D3349350D7E3049350D9EDD3E041637D8C48B8D949A34D62D14771A151CE3F2B873C576DC845CA718B834C02259E6B55617F33E491B1D6B1836C5DD4785DB489E3C45B8E86E9215FAE4EFD94973ECB14C8E2B58CF44E1B41F5072D62D39C7F430030616E7F94E4A67EFF1E6AB266B66138BE53A1614629CF8388367615C29F86E207698713D96C89BGA6CE457128422CB66778CE155E1700EB58D47BF10771CEBD
	E5473B0C0F37C7556A0B166AC8002BF00BDAFDF75B57D70E75DDA02A5517A3551187579A557A0E1EC6BFE8369A1B4B841CABFB36291FD358393F4BECB49848263600F1570CC20773FEB056B683BF5DAD98F7BD5D09F36A70ADE86B83FA7A9E8B24A3G62EEC1DBB72A5A86A1D921C7762D583FAD354EFD77D42D3A6FFA54BA775D0BEA5D7B2ED72DF35F3D3A563D6F7A5512B57BCE3DCEEF16CE5931F6A99F1637CBB937F5FC1F5256E44DE0AD58206AD634752CBC34F5732DE86BDE3A4FB68952FEGA137222DFF
	F4084B6DE20CDB6DED9DF7AB6ED5FCB0AC774352B27427BF1E3143EF606F77F42A7119A3551F9568B8E7337AAE7E592E3ED244EFBBAB37CF0500FDBABD8DF55784226EBA11685E86E9934024C0547D07EED73A7BA9E48A8EC45D17883231F377865E5E71F54E7D6F9FF56A7E774FBA677E17DC675E7F66EDF56EFFD9DB5D7B5F47DBDD0F1DFF926C6CDF09784BD36C46218A744F399B544602F030DDCCA07A67FB5D786C11445BC8DFCC4FFB5D561A59G50FE9B36D1E789E9A36BED76EDB1C9CA7B5E664CEFFEC8
	35133B8A7D70538A35B68F126A89F3DA5F02B43B7A7E0375D52DD79D4725FA9260AACCD39BF76F332B2F2C98710369F23F5EA0A01F6F0BDC79EA4EB4E7F2272F3663D302787BD74BFD181C61E131FBBD5DAB4C037A2A26213FDB5B4959EF037624F150AA88A153885D64F6EC67A6C86BGBC817C2631F5E9B6075CFE020CED5DE6EDBF38948D243CFFC74A6EBD0CE585C867G148334GD8FF07BC2FA9A772ED5D3EE7277D996D660564ECEE3D562CEB227230EC16577EBD11DDDAB9AF11560D34EA6B66B140A771
	F7B8EF2C29DFA46F83D21C446C5E552134FBC6104D5639C10EF6E77B2141B40EA88ED26E134E207846A065BE6976E338CF5A81F9E1C178AC55C4577473EE0776389DF1371B71792A01633C7584678D69G0EE3EB4724ECA847799F4532619C77168E4B2E3C83F13EA216AD603877C5ACDB45F103DAACBB698E6C5FF38A799A4B9D583F7F26A57AC5C1BA81A07DCE6C5F4F294E8B5C3EF7BA336FCC9ABB945F496C184671CFD3FC2D8C4F6CFBC80F7B7D4D1097FCA74AFF045BE3E5B062BC7A882E8652ED41281346
	00BA9902D127F73F264F2E108E85088AC61D5E4AF325D3C230B31DE2689CAD5C255469EF941FFD17D2278181F552C05E443BD0FED38A6A944FF117274AF5E23813B4A64B3A3BB5B377EE443D5009E5736EC6FD96B82C6F48E168EEE77AC453F95773EE25BEAFD33C6F5DCAFD0E0A28CFA064E55F03B2993AD1A6AB47DD5A6358C6151558C6BE77E09B99F713BAA3A19D8FA00470F74AFB64BA2D84FCC108CD0F5A90E7BA7D154ED3ADA1CC76CC0E7FBB45DBE5F826131162C95A9B726604F05B53F54876FBC176FB
	9177A045CD0434B747FDDD0A3A7BF1DC89DDBFCC043470FB44DDD501F4D99C777DFA2C2F1D630256A3DDAF473DC2FDA38452919C57DD04F4313F47B17927B564F99A4209125A69544F78391262697C2CEEBB1E551D88385069B89714F4582D3D1113AC55555F19753A2D5A9EB612CB3181DDCFA2651326E39B669F2231C1A80CF7G6D21F8DEF9E89D1ED78E3C6986A172D575336031ADE7D22D2152D970D059B94BEFBA53984F930711E79464E75DC7F0B79E161F83A2BA76273135B4167F2CDDBE092126264873
	979C116793F97FECD7FE23DDBE31510AD4164FF41B79956A16BCB3C8B05CB551697788DA6E135E87102E1A472FC98973E97ED00A0779DED150FDE84F6D747D1FF8C88B774338F7BF3679298EDC4BAB6638DB78F850F2BF761D5F53394A9F5221G519C7FB04513F7D63C25F7CD24755AE3ED923D33B1C48921B25AE8B765988F1172984D7B097394F53822E57E5FE526674DA008CF3C1FFC3742301334D46F9F081D424D8D92ED799F9C65CD11A0CD02C620A1703104EF7F839BB7D2B82FDB75C4A6F100ACE86984
	565F9E54231F8681BD136132DDB699269FC59942D4E4C8F3A3C334CC8646EF15087C62B83FBB1AEC7CF23A115F9C95FE735C704B1A2164F71D96792D1C013E73FE21FC1CAEGFC6D8C9B2F16994E46691FBA09AF76CABC66F13CC87143B2BC9B379234F8A64F8B7212E6207F4F667E5F76G4A74041E78C1GCFBFCF53641DA9A9E6543BDBD7C6A0F50C54DF6D7DFDF844BC46724EAE0B530F9F4558B98C640C8618833072C1087F9E44B9616ABE7A7CAABF0B4C5BB3288F5B336DC1344DD786F9FBFA83BE60C11B
	8DC29FF4561E83F9F49EFF10355B820EBFCD7189B2BCEBCF0162C9BA632156CC43C3581E8B7231BDA39FC219DE96C87BE942B01DC053413CBD2D42C8ED2D8624DE776DF969AF62612E0DD9FBDE14026DD98BB235GD8813C81024378FB831D76E35C9B3BA1A97577FFF6E2FF0FD251A9CD8171C0C3E015648349E1367E4E5AC21306ED1171C734758BA2615F465371B4CD5E4B133E77A2AA6DC4E847EAB7753638580147D87A106B36B81F6FD58F017C1EG01G91G89DC771ACAF9DF49FA38B5D37330CD577207
	1D7515D7E84C5B7830F26CFB13625B9FD60EFD033BF16CB3C1DE544358D7D650983B6A11564C26C790B7C45F01B3C1FAA2473DD3096D9870884A7ECAB109EF66C0BAB79C46EC008EG0FF04C1FCD739B25F7A0497B03649DC7DF1EBFD80CEDC04A48F54C8DF7541B701127EB95B49D0A34F4EE077348D45427764D8D258FA8E30685AA74501FE8CC4F46C7D14746451BCBB0EE8E8D47FE7717927BFE677BBDA425FE3739947BDD040A6E582724EFCD8931C0C360CBEC63F0E5EC51D8E14FABFD2F0D57ED9548CBF8
	D41D9772D172A2F9B90F5AF8313A3F5EE72B7B1175D837C6256EB4C55D65C0C360E3895F7828D20FF7D39DE32425E9B86734GBD63FB26887976BA51C94EFBD841574B016FC5EB48FD0900313A5F8979196B2A056729FE4B00002F44BFC83AFA4E3DC67A664D3D8E6771C7B6D6BDB246F242215116E3E348FB74D9ACDEE69306CFBFCC70C9B2BC9BDB0A8E617320702756CC5D1F708C635E7DE47C8B07F49CC0B2C04ECC30BBC073CCDC173B251EFCB74D659929962329C9BCAE9AE4DB28D2FCF4F8347A3136FCE0
	BEBEF7857F7E96A1644286276F406AE632B61CA8DDC3A22EC9A1D726DFDC8B51AF9872A28112G32A2E07C86280AC07D5E2A174E5113AD68BA2BD55B55CB5FAC669B5AFA71048DCD96D025759807B53326881BDFF7C4B81B97FECF1F7B2C914AB54907A95EB3C239A659348157A47DA1AF633156CC3676BB051FC5B6BC4656FEA7425567B1762EA56BF38F70D8A168B177FDAE8CE8229F33BD9BA4BC266CF3C7D710F6582A6D6A4313518C7FC9AF09BF376B877416DE5B19E9E96FF58BA9338746B94CE1ED7C6703
	727E1AFD2DA195F9B94CAB126C690F5B568D58F3753F4D78DCDD85F9DFD0FE90EB8A36FFEC7CFB163CF2A471FDA98BFDAB7756A9C243338D29DFBA61EB12784D10716D68AADFA04D95FA4237DBEB3010D81B4913404F894FF99C1F556A4874A65922D4F4AC330D1F17AC7EB316197B232D4CDDDAF999B6576750F521DE209F7EB34E7F95F45D408F52419C972C4518A07A4F7CDBDD74D9AA9952B911086B23B8722EEDA0BF3F7B1656D5AC0C65C753379D473F4CCED7BC50B7474A0375010C2E8697B44AB501E250
	29A112758112EE0BE46BDC8BF8FBFC70B1A907EF17DB58D1F229ADFD0F623C270B44B52CBF9DD52EE14D9664EBD049C715EBD8A9F67905FD4AB52CBA3BB52E0EBE659A9616E772BE52C73E17A5FF3B526E3DCABA10976E5F31673B26EDC776EFEB5AD65F32637BBD121F555075F6DF50A5A892FD6A19C2DC1F0A623866E27433B95C272CF9E40F28710956CC93008FC0609318B7016611F72549BB35B3G9F4B73BE4CF36693249E3CEFFC425E77FCE46940A71C7BA5637105D39EF2FFF395EBAA63CCC73F536473
	986EC967E3AFC28E5064BDE9733B1AA715E36F731F12314D6EFBC61273B15F3BAD85FD2F71C9743D67D3DC7B5E22D4573E37A555356F9DCBF566FBCC26F5DDB836F9BC69EAFCDEF3D060F62BCCC33B052858CD79FC9B81B431B23BA5B958ADBABBC7EEB77621A0EAB8A66354EE341B70945A6D59EE57F6DB50635AEE9BFBDC5B2D3F47195DD8FF5ADF097D2E6AA96CE38F6D42FE574EF11BF6E33F73F88A7B4F6B94678F69900ECB2238082758FB348CF7FAB7698304C64D077D1D60C78A5219E44A4359987ECB1F
	672577FBFB483EF82751CC8FDD3058E8DB8F46C60920C7C6944434G0DD1CCFFD66E328D0E7B69B771F3939DD1AC269A022B27D40E796762AA74CF7FA877B1D58850C4C4597CB3B6CA691F8307C89E7B4698B982EDF724DE22793A194835CB6C6959ED04F1FB50A04ACF5EB31ABB73BE126AA13558CED3E39D46CFBE126A20AFF6F71F64019A4B8F28D8AC65132F2E6DEC52F6128DFF76BE7616B4393FA6C87648FE9AEDF9BFFBEEB68F0A2CCF99CB315F2FFD5AD53F5F56CE6ED9199778F97C7627B18E6ABDE44B
	1F339E733DDD56B97930406D16176698EFCDAB42FAA2D46BC95C426A19C6BE83A9BDC79C2431D57AB3706C708CBEB79876116F5E34EA0FBA1EE085E43359E8924D56138CCB879D29CF24D079587DC259FD4521DF3276B8327E38E7DCF9B03B7C345F7979AB6DFE7573D7BD7B554F67766FF7FD16FAF03F733354437B655FE1E46DF8D2CB7C38965A309D4083409BA00037694216B582FD96D4F98E5C266BA1673F48570A2D48FFD60BF3DD13DA5CFF63787D96F2AE2853887D301F540F78349F683B1BC693C5CA67
	3CFFA0B6B26BFBFA2D0C944FF97FC0FC53A0F6D33434265B09F3C558B3B8D7DC5B454FA3BC03F3408C7AECD975ECEBE69B405033080F20F8725E8EB957684D717BB628798FA9B7927FC0BE2EFD34CF15073B3DC1791C606A1901BDD34F6C4075109010A771D9BC3B70A85F7B4A0B46FD5AF7681ED4872407G3C221176AD3E279B902DBE9EB99F231A3B846E136F53FB5616577DC03E1D2ABA2600876E04987E7B4E23903737E8FB383DBE30BB939D8B32A4C53BEE8F46B7CFA473D846DFDAB35782B482F4GD881
	BC817CG02814281E28166GE4BD87639FC0B9C083C09B0061B96CBB3987721D498EC86910816DC0E62D1EF4A1C97E59F6670CBD20BE6F67C632D733536E5CE8G1489F5D30E4D896B0B9C63052DC5B86FC7BE476205F838A6BF477C1F15EB6AF00C973A789ACC4E73ACDEA800EB6D732CDC5B7354576C4E3E294BB4F99171B159296EB206FFEEB199DFF86CAA1D42D31B331C4FE36944D570FDC317E73A1D4BF7345251E622817572FB1E59AC842E914F33B584C6B3171FC90F136C13CCEC9243E8D8BC55D30C71
	D4DE0C7B67249A20E90E3145D33A98E5BC95D77A0960701CE4F7DEDE72032D1F8A0E4F85F2C2C6671DCDEC47622192BCB73A4D06AB15E224709EE50CC4F4B44560B7EDEAD473A74660F832B79F47931F98753844F99BFF5DEBEB63BBDC1EA5F75E1F56F7494BC5028CF1AA6DA187A6F7EF9ADF739A355CBB75B6392739BC872E864C47A6D4220F957F1579E1A3DC5B7F4A5EBBE5B4893B10467AD7691CACDC7DA49A56CFDBF63B6F2717B651F6275D30845F0D6D7A106378F7A276283D839B827CA27E0AE7347AFB
	701CDD9C60F2DF40B3DA0707083DEC5FD3243FCD5222379AC45DD65A81370A5A814976F7513DC19297343C0073559F69B767BDA16D8F907A8272FBFC834EF9D1004BFA917DEEF9A54EF98DAFE29D8D1574FC86243D8182B96D16AAEC6B3097C75A56B867759F9584FCAEFA3B5B4977C8E90FE23C0E2721EE49AFE27C712E5DDEECF9AC34F56CC8660AD76C4E1B37C39953084A3DEFF72EFCA2147199D139BB6D4E0987C21999EE4A31B93423035C73F817BDB6E00C5CC1468F7E3DCB7AC9547DC0B8504372A7E81D
	472213346EE3514BE96CA3C28CC91E7F986EAAAD71EDE3F737C564B16F7FE933B9EE072559E85193C9D8BF5B413F2591857AA6G643C541A197792FB7F000D77377375585A175C6F2336GCD6FCB3671FE78A565F8FFE6A9318B7BDEB8FEF1CCFE9E7D375FAF63797BC44B2041EA11BAAB4BFF7684618BB9C643F1BE45CA7D78C9D16DBD0AC9A0036FCB681397E947B65EDEB946F20F0F3A9C5BDF6A23E72E02DFD26E99DAA9FE46CB4ABD437735B8DEC4C0DE714B286FBBF67BDE5E008FF85935EF33670F0FAD44
	3FC2DFE6FC986ED30A0B12F04C3FEAC467FEDCAF3A7763A6D17D19EA1F6878CCF5C0D4FF26BAA44A1F29982E136E5D74C09C5B4669FAB86EC70EB350BA896E00DEE76D65F8B3150B609BC5429071C3D42E939457A6614E48F0BFCA3873C45297C165DE23193F9BC74F65F46B8D86161ECCE9748350B539BD3C280CFA13D6A677159C77A3EF0B0CBE4776D954A7EF9F0653F6BB52F5F74B6924F364C5B83FA582BE6F951CC36EA842F13D63951CEF4E236F1F7BC0BA88A07C951C33A6736734D8402D0C43BA1E6A40
	B92BB98E6BC8217EBA8952FEGA1F1F8BEB97C107C8C2FE67D772AE7F8679CBA159A99477CB8A94E365F5754D53EC07AC626817549FB951E15DE656FD49AD09F1FD7D116AF8B69B9B9C8478388FFC3FD3EAEC2FDD6822E79EFD8073188751974B72C63B714F68624238192B92DCFB14E41992F0DB65EFA1A7C040994BB4C6D41FAEADF433AAD9CB7749A767756FCF9FF8F82FC58EBA3696FDB3B9C7D643BAE391F307A7B6D5EEB08063A935C544FECB935925BC308EF4D2C0AC79DE6D7228E26F834E576AEC29BG
	6999G5171589EB9CD589E09004BFB9D6BE8EF427668F89D6B3804527AC0BA88A01C539E5E036FBB44F25A3B68BB94AB5F00F19320718D342525475D585D71065A992989EE4BA93FF9A21D9127650625BA3D606A7F06F35B06C09E1B937EC3E34508B714F345D60A0FFEC3B9D74C2CC0BB4691FD7F0E7AC6ED985B9C7772984B2D2A9C5B5C98C2FDA9706FCAFD9F2078103FAB755DD9097A06C1DEDE826A5BD0A5676F89F83F0411440B276D7AC030D4A6DC3A4640B5D1D18F6BCFCB2B9C7B5872AA47B17B5D8AC7
	3A39950EF4B9850EF41F94B8527D2948116E4FC50EF34031CE34D37A2C564C1AD9780C13314A6EFBFB6CBD945B6B04820ED177F6E2BF344E423E759A2DB39052E1GB133309F3E4C779A13E60DF6DCFC1F7C2C1344F3A1FFAF3660CD6439EEAB61290374B000771B585E47BA855E0627E477E9AE4FB57CD2E0BCF2471587EB5345AAE378B23BB11C3527F69D310F74ECC2439E064F9DDAE443E358033F996972DDDA67E3614FEE4B290F057750F201EFCAE7FA609A7526733E997FA60E057F2231C9721B4A31E1AE
	45E77CC3B9B6CCBA02BE1F83F9C37FC0DF395C619D56113573EF47D8AEFA546518DE03B40E0A7907D25F1FA9BE5EC15F797CB9A189725A9271EC6133836459A9945251G49GB933E12C84281D0DEB46379F4D931C2F9933414036E44C743A42B0B63F395EEDB9F53FF90E16EB194D6A6C056B70EC67FE63B59B7D66EA9A837A4FD65A510F6203E6AB6D18DF08F68C05BC4DDB68B75F3A75F775765FB846F256D117E3FAAD256FD8CEFCCB296F27946F7516D25FA9BC967605BC61ED5477B745F27EBA408F3DBD12
	B9B43DD7CDEECF292C1FCA9D2C5C10EAEC95AC51072B14E363E0FF3E6358E84EF71CABA726B952DD1C6698877F4EED6C226E577739AD276E577F2265E25C5896A171ED746B09B416CECFD2367325941F1D24EC67653B301DF3A14FA3895B7953263179E75E984BE9C7DD0E6975810D7B639C741DCF5F57C8F450570BBFD3A4C3DE47BF797BE8FB647C43G9F7DCF1B6D937E69588E0CCFFEB729CF781772BD12F20A4F1161997F69A23EC71287F91EG6CFBD4B316313958209F906B59AF68F2FFFC02BE77A582ED
	46BB38DFFC083EE3128B69E20E4B2131E58B248D9C371B46119E1076FE87F55DC2BE19A1593DD1F67FC4457847C5EF1C839E77D75AF4FAC4F65FDCB97E3C75634A033569C115670A8E15670ADF53F14C8F5ABD087BC34C86FC5FBD0C63B671F713A2B9CEDF086F3BEB66205F1CF0799D8A67B639E00C656E9FF5B956778EF611BED6BEC7B9A668A83EF60EF2CC18451F031AA0AF086B7B1A5D1EDB4E3FA1BE7A77C8663AA5F6FBE5B5D026D9D6CE27D287136FDA7A9CB374EF653B87B7D13C178C4F64AE580A6F9E
	84C0DE563B58F60F53E7BD9324A7F15C2CE6FAE69152C19C673F0F7A8C2493B8EE61917A7C759E44036F219D1269D9ABF6BF2F43F67FAB375F067C657D6966F3406334562653C4597DA217E76C7FBB3C570DAB8F9647478EB8DF33FFED407D1AFD62007A1A7D3F869C576C5F9BD0DF33CFB1BA1E031ACD57E6B560E7D56F217FCDAAA738A6C8F7F05C96B6E6C15A1763EE7D166002A09D467D74B70536363BC9F6DFD4B87E6D58F88EF82CA8366924115DCFAD9AFF5E8F0EAB8F69FD18E247F96D5EE247F9EDD5AF
	41116FAEC5F2FF986E45B9AC0663AE6BC13A02E444A576A0DD9547AD22F367A4C87BF11C4FCE028B06F4F8B27A57A53BECB63FC2F61F3BEB7C6DDFF18EF8246F366934C8F6FFD55378737E5D3872103E5DC277822220BD63F9BB9F256F84E53D8FF14E7B08FB3D8FFD2461FDEC7BA23A6E688D69900E2B2638D8C88B9FA06E817EDEBC21D34A612D029B3D2D1E63BCC6728E76E8016D3B477710F15CCA8E36ECB75273006C5DE10176B38D68DB70817F765D864773E71379D97DF6203382788184FF4074174EF8D5
	ADC49E75C68359B7E5BC6AE9FF0CG5AE84EC3D7B18FE985BC33E2A2BF83AB55B5157E4ED9B7084A6A78830DFD67C0797439F8DE7D0AE3041E1DF3E829645F22192BFE4EE13EE0BB67006FD9637B620DF3155FE579F5A12E05374CC5DF505233236C7E286C7E6DF3307E1C55B57E7D6983157567B41D63F83D0E7613DE304B70DC6CE3C67ADC6A8769E00ECB2A43FE974DED68C7E35CE4C8EB048319D5G8C9FC8714D10B681F8F3FC69D6395E045E159E49AAEDCFC063249C63314B0E073B334F4E7A232B77EE16
	892F9629773F77E9EC9F82720581B05A743D0E7DEF55DE6CFFC9C0C77ADFD64A01CCCD4A81D97F1B3ECF3D7FBDC51F8B2A0036A185F92CBE62587FDEA6C777253AB27A55655574133AF4D00F0957755BF2C7F96FAE6767D5D3183CA1F00D301337F5033A3C83B4BE0C835AC44E6341EF9D651DC744126E738649BD9B8B2E684531A0BB55315D155F04DA8BB472EFB4D5C19A4782D6770163363A67733A9BD46ACED1542D7C7ED31B2CEE26575AAAF5BB5750B1D3877426D434416FF6BA5A605489DB1BDD3E47319D
	827859C8BF28071E9B06EB840127F0AC1EF054712C358F7F8ED29C50523344F04DCEE36518AD8483BFCB1C665C96B86656GCDB3GBFCB1C263445D3AB48338BBBBB7603C5B45B7F5E892371484865B48D7CD7B465146C7C2F416E1DC2727B1EC4EE9370E47947FA5472A7C2BE336B04E11BDD77EED88D57817104067FA0A5E92B3CB5F570E0E5D8D320A9017F6564BC3E74BD408DF8D642876A0B84E0B638EA9F7F3E386D2CE913FEC0141DB445FD5177E89FA0F5E4249F1076C5ADF49F3C9652EDG26F4147547
	1FED32EE2BA0FAF1B9E93FAA6CB4E83B7A98EDF50A0DB6F244FAB119AE60E7BDA6F15E07D2ED75CDAD143F7F45F01B3A9DFD3205FF87E8C6BA7349E838A6G10F7DFD8398BAB9CFDF2CAAD16CB1F47FCB2972E4573D8B9F62EB8F32B7BF345BB5690BF134EA0B642232D01BC5E62BBF80CA6A4CB710EC089438BA79478D266CFAFEED51EB7A7B2B502EC6CEC63951B143F65C4725BA15F44E9E6EFD6231984793E73505EF3F75A6C7DD273687C32FEAF7AE5B05417C0F853F61D3C97FDED6D0768EBE97B88EDBB24
	2DG1E9FA2FF314946BF7508EBDFFBE00F0DF638FCF4323ED00E327A836F188FD156F595A8EB4EFC1475BAFA9E268952BA006179A86B574DB67ED33EF5A6AB2BB72C173F6F8865DD1C21275F8DE2E52AF819884E331FFE7B075D377CE0636F39F3F47AC76EC17D53B38EE456E4207EAF6C4573EF568C547F9E7A1D5FC0C8078144G30BAE69C65FB4500ABD800F5E470EF546996E09D9B7A882DAF240381A2B8ED0981F50CDB20EE9767E750CF345AECB5051F7348DD08F5877073EFAD8BB15E3B576DD9A475FD39
	C75C16D35F1793DC6E67F9AEF41C57589A6EEBF4AC76DBF8C031A61D484EB649702CADCE72B3CCA110171B097ADEE2475F83705E192EE3DCD65FF0B77AC7G5047E622CD43381DD7E6E15B06523E548C69DEG0FAC740F1935A8138F60A233300EE5F5689F990B300E3ABABA77C03A8D403488E9175773B90163E6EE42BEBA03178F58C14A4F01F476628319F90B11B6FF8B5256AEC65AC31B683AB9243DG8296232DF66C9D1BEF9CF0DBCE5DB73CDC161B315819EF1C20E3E754E225EFF8D0FC6CE225EFAC5807
	6D10GF94DCBD05F387231697B165BF26A7A2ED77D1DG0460A54E747D0846BB61CB147A2E227828A5CAFD7D797945D848EB48C6FD9F35BBD390G7850EC57FD01155BE4D7AE924A440D281C6F987D6916B17A53AB345C9CC9362C25707C3654391C6510476C381E0E630DCB1576AD27781625CA7B5E27C77BF6C0DE58D2346F4323FE9FD1BAD7BA46F20B7A46365FF78B1D0B72BED26AFBA74517FF245437268F752D05BC7F0FD05F8DF6E710B21641383BECA4FE91C0777B0A95348DD23ACD258ED66EB65533AD
	86095EC3251C744DC99AD7FBC3FE60B214FD16EC1F7BDF327B3367E04F5B679C6CF9DFDF693856F4D32563B91D735C3E27285EC73D5C16D36F230F52F2B324360AF263AF714B300F9653B1ADF91952E77FC37199B9CA1FFD301C1FDB043C219CEC77A7EA475657DE9CE33915F5E36B23F751F8A0A6C7296F7D949F6F206FF7F5BC96053C360FD15FD575EA7CC3BFE6F60F7C58D9DF0B5C64CA6EB815F2CC3E5C1DB48E79780062779956D1FC46F29B1E49BDED8746D9ABA1EFF8B94ADD3845957F58654E78BFC1E3
	1B2465CA7E4FD3FC7A8AA57F2ECD48BF877206GD8DF3919BE8744C0BAE9854AF406BEFB307B8BE577CB760D7F1811B72EBC24F7DC6833DF46CA18B7D7224EABE5671DD64B6EEFB98767129E9AD79ECC6758DE47F17255DE47B57996953A9FD5685E25FED38E76EBDA093E3471AC41F510B4473DC51F67FCA09D44F1DB68377843A09D4D6DDFF254E673EA597DC3A37ED67B586D9FF38EF8847559F43AD7F63F35EF7CF99F9CD79E52F9C203235F6CB3B87A4D9F68F3E59C347B9C6E8F997479BD6B93082BBEC1DC
	BF5F0F4F6338B0CEB744F1DF717D7D091F607AF6E1A5DD576855F7758D08743BC4ECADFCBBDFF70C6265E319BC10CE6638CB69F73032BFBD103976D344B55073A70D10EE7F1447059536F65CA23B8FAF9F7FB6FD699C7018DEE753A9CCF63F3BEE7CF9F70FAB8F562625F44D21975AF308377355755C1FB86E35CD080B62ED3FE6A759D7DE3B8A5385B4DD4B5341BBC6ABBF29EBF4B6FAEC54BCC6F38600693EED8B6A2E633ABD3B097E4683CF3FC453D1BC2D5F745FEE3BE019ED24F87E3F4EA3CDF13FCC38FF2F
	7AB8724CDE9CC7662C42B167478AF55AE3FC1DB9A397E9176ED35F2B4B277B2BAB01EEAD275DF756F15FC3FF965777DBF2591E28812E9E3972BD5185A96AFB222B6977C6FD00561F73E855BB6E87DEFBC060F5BDC347C87B6F704F6AC63D42F8BDDF7458FF9B7FBE7A8DE53647B731077F7EC52E7B7D4EF820A1007B1D49394A3D54589FEDF557703D540C4F5C6F53AA6B4E7DCC39FFF828125C177274BB5D6A6D35041DA30372651F218D7CCB9D5BEBFAA936176EB356DE43F0757ACC5EDE874B9D5BA06D202D8D
	F6545B5BF6DD3BCD7FE97528FF000A7E38F70A7B2721C0C3G750F14694F6A7BE09356976324BE79FEECA25024FFEE5B0FDD7939521E250B48B9D37C76F23DF6603856D2A2FD2B6350CE79DE555A4F59390DF6F285E07234EFC1F9A6FD2EAE0F7C5B1D3EC093A413A74CC11E688F499E4E3EB67261FBFD175E249D400F0BB019BE325BBFA35F8FA167C8A2211E384F311DE76D357711C995A4DD425B6EBC5977AC0B4E0EEE4F667233782C168C7C4A3F40B56E3F1C45BEE77882573877525FB17407F4A8C0549738
	FF94FF44463FD26FEC4FCABAA752E37F9D503B78FECA4297EAFBB528E3A6BD934D4A7473BD2C359A14213053C6FB094EA64F045251596315D234C7BB546D2DC1FB2CAFC3FBC4E8509ED34A68736FEA08A5814AD7A36D0BFCFF21E935AB1D1E90EDBA95ED46B293D54B38FAB7B929DB60BE297B1168E25B2FBF5EA59A8C057D74935EF46F5E3EEBF07ED76EC47E212BD13F4D1B69F3BF24335640BC3186753BF48B52562C992D2CABFAEDBA6F5C0C7BBE56B57C3CGFDD7B49052E1GB1EBF01E3F7A6C585654EEF4
	DBCEFDCD6D8517651256B8DBAB79A36DA399DFAA57189E27781CAF15EBCC8784DCE352C05E44AFD15F5F5B2D4B4683BE794B112C831B6D76953257C23B2D9DC93957CB47E65FF75C16D337EFF1192BF2D5AAB2B3BB2E20EB10CDEB157655D0FC5BDA25FD271621FDF510973E166FD3592D6195FCF5A033762B115829FC333CDC8B14699DD139EBF74A4B8DAB4AF8295441743A0376C17F2F147ACE2778202F147AB6ECC6FDC3A1CF338E757D355D5A5FC4407B2C9B095C5F5B699B88E5E6484AC52954A16DB533F7
	4157B96CB5D3FC52BA073DE67E0E38707501CC5D5778BC74CFBA8F05C3BA76EB54E5A62142FD9E597D21DF745BEC6E0071B8BE2EBC646FDA5A2FEFBC5363383EF1314A7A592FD4564F3E23F3D79258AFAB8FED78D9394DEE6BE4773FAB9BFF9B060FAB8F2673FB750E36919A9CED33EB33A3DD73E6C73A5B697C28817B156721EF0A7CD93D0D5BB5E4334D168F486EF7EE9EFF3BF60DAB8F267354AD0E761A364551DE5D1B51DE435CDE77505F6F7503F4B047ED6860672D398DE7E9EDF67B176C7E6CB9F8974EE7
	DCF93018B4CBD0FFFEDFABE0DC151C07317F61B2F55A63E5C81B150F3461BB9D1F87DF644FBC39796C5CE2B9DC9B81487A917B0D43E1FE4E2FA31F0D67D6696A8DD7D64FE57CB9A5D82AA7822E31GD1FCDD6271430E679FDFBD0C65666433730F595FC0CC728DAB47786F665FC82E81FCB30089408BA0689B2CFB23E07F8CF4E9992625EF0C6C4238BD924AE42C47D8757BDD98E3562E4798730E9AFAD68752DEG811C7606CDE8539900E33F43715CA6573F63113559596FF4B0396E590CF24D01BA0B8B1057DC
	9E7B769620DC6C37597CA09D8290D900E71E96716F454483CE73AD56714391BC735476AD56F1BE257502F4G408CCEEB69C05D223FC55DEE6DD07E064991BB5D7E5A61DA77A5DA65EF18DC1DA64F2F53BA330DF456E25758E24ED5EE4B29471CAD34DCF6A1736D35052E631F2AC25CA7DFC777B31B8A1531CC91453795AAE319109A9EF3C2DEF8A10EEBB75B6D7F9694C19FAC9AC98CF65F663159E9265BF26AF6CA2731E20BA4DB2F9B99070B50CEC354FF3D0A14F61AC0713EC5CABBADBC0CF68A003C2CE23453
	32A3E3BB9730FA0C65FE9AF5B92657BB342F190A157A8A741B144345CAFDAF667DF89264A5F0FD076C3E4354DC8263E409EB1B3339330F0EEDC3C06BD5027369795584678F69900E2B2E217B6E10CEAC61FB881B842EFF056C7E7E1A711FE31F9DD79ECC677BB7E11CF2F99A629E5A04F14AD4CE370A46AE49E0171CD23457538769DAAB24EBB84E73905DEFA669D234614F325FE639C8F61FFDF87CED18FF8EF83CAF7B6642079D367B4B4E4177908246150774EEF607230FF8F7200FDCADE02CF15EAE75B579EB
	E97F73GFF70AC45B8655AEA47986C76EA0CEFC2CB591AFC94DC63CB65EB72FBEB9C57642B69376F25F56DAD6AB26C247EAB144178D906B2A49CF414A12D1D47D8E5CC069638761649E518F248D106C374FC843B3F57E01367C4756856D57FD20DF11087708CA94338E475C6DCD7CDAC4398661A0D74ACE5B94C0FGED6538467B296CDD5BC78F0E0E7F7AF664EF027A824AF9DC540E7CE34B117F5FA96D4A8A0857G9AAB303F3FD8BD36F9F8165BF26A73703A0D2E4AF5D4B8DBFBD9C4638FEB05F2DEDACE719E
	954AF969379BF1DE72043CC42EEF095B5FACD657F70B5BF26A7A8E39AC573241193EC768EF9777EED06A5BCB71568DCAFDE737233E9E109737814778CD54979BAB8FE4F6D472D804FC9A9A3F31A03B3FF163B978065438721046C06A7FD65057136BAC9E3269B9A03BFF27FD7CF54E9E575F9FE3E3C74BC675E7EA43C6FCC6722B44317426F6F55A7B5B11B608531E3F517E997569F6CC2F2069DB0FF4AEA25F25A5AF8127131FC8368A9F32DFC406B47979E3C857132F5F982CC2A579F5BECD5E36812BE85630D7
	C7B584D52079A64FE2551A73B57DDAEBD72FE851E887CEEA3AFA35E6ED971066DB05553B36FD771D86FF85D0D32459FFE0477E7D4D7B3EEF51B41B0DD6117C3252C269877E8A8B8BADE6E1899FC2F7506F42479EAF2BCEFBE5CADA2A103EC37810FDF3E70710CEBE1C33C318C73F2B33C3C8339ACDC3E4C0CE1703651870B3ED4971468AA3204D11605CCAB6EAACB4D9E921C6C3FE17D1D3870D09BBF41AFA185034609F83BD55D6E1C57D2059AC8ED82BB5FB0C832206B520DE54D9052292A84DFE2D57D6365BEC
	6C57101FB055983B51FF245F36308A5F92FE753DC623C554E8B52D2259C20AG2135D714EA92F53CAE2B104D2BED90C1A283D4310C232462F996703DA59C4BD802F3AD321B1F7FF8FD455FA2DEE83524899F6E90BEA11F4A58CFFE0D4320BFA56A9A34D62DD459F6E10149880E3A9F7060F996FA19E7311EB408CDC2CE77BE715820E881996B70F7D0C0220F1B882722C12B5ED0542805BEC53E9F3531C9C89FA0BF5FA4FC44328D5A011EA27623A89B297B2654F5D0E7A243C38A483B0C4ACBEAA96435AC1B3E64
	2B55BB63C25204546D42C203D12B5B8EEA9A4DBB048556DE33E869B59AF4C326AD3534220C93E4E448246DED96D6F175093205248BF3217B06FE6339A8FE5176D4F808B38BC514121C3AA864943AFA6C58BBG3440F2AD4F1B7039F55DE35F2DE8CF95D2A4A509F7E906CCD6E6B05E31094A055BC1EA6BF3339E3D61791A4484A832DD18F71C7CF68CB04E36D16D996C6F944D9786DE79D926F76CE4A2170918077A6DDA981D3A357DFA43C938E900B6B376F0BC7B9D8A1A4CA3D6400C85D633F64052EDB47703B4
	9A05A9B00BEBD4F8452345D7CEFD77A72A112D28D57802359D94AD6A910D7DC5F55D5D7A8111D7G8D8955FF462BA7B4058A1A0F06AE7FF86753F3BB41719A04347E62E27A37047EADA5FFCBA82604E2CACA21B59385177F1479C3C3C467EF07C0124576479FD2EEDCF1FE76E5E50329C246F6E2C20329D73BC3B2FF131059C987D4518C345053EC1A5232603F29DD605C2BEC78AE02E7397BCD5AAE62628BF4DAF39F2D0148701F034202EEF3CFA7E5E095D28FEEBF88ECE90AE4837DC26867E4F021D34E8ADB55
	4D649B4AD2866B9D42DC01FC63067C23ED7A39C30FA158B50E2ECF506B18C390F2F2331E7B8DC99512BF4564CF89A5AB23FFB70C4D6CF5C32A033E703FACF3A313396A7FBBD67E5E39442319G7FC735EB9BF2B6BDAFA3936C01DE51ACEA2CC6E9E6338AB92D75224120A19F7D60D3A5745415B47C5AB6F06AA484E87AAEF9D61E7479865BACADBF9526B9213776C25C26A361DF0A66FF59D43A7FDFBBC23F93C798DDCC375C968AD90D7A2EDE3D912290B34C1DDF55D9AC7A1E81A7CE50E2E4AE703FEC2021217F
	E569FE9EE2FBD10210FF36CA922D7D72GE14E85DD29388725FCAE0C594656969225AB79DDA7769B85B574F73E8A369F48EC84E05F917554FDABB87F8D5E7D7DDA03412AB574558D68486F71EEBDE995ED3FC7DDA7127AADC8A3E5323CC67A3BE0C7E5E57E9FD0CB87889FD29D43ACBDGG8858GGD0CB818294G94G88G88G4E0171B49FD29D43ACBDGG8858GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE6BDGGGG
**end of data**/
}
}