package cbit.vcell.model.gui;
import cbit.vcell.modeldb.ReactionQuerySpec;
import cbit.vcell.dictionary.ReactionDescription;
import cbit.vcell.dictionary.DBNonFormalUnboundSpecies;
import cbit.vcell.dictionary.SpeciesDescription;
import cbit.vcell.model.*;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.UserCancelException;

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
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
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
			cbit.sql.KeyValue rxKeys[] = new cbit.sql.KeyValue[rxIDV.size()];
			for (int i = 0; i < rxKeys.length; i++){
				rxKeys[i] = new cbit.sql.KeyValue((String)rxIDV.elementAt(i));
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
			}catch(cbit.vcell.server.DataAccessException e){
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
				////
				////Remove current if not in membrane -or- Add current if in membrane
				////
				//if(kinetics instanceof HMM_IRRKinetics){
					//((HMM_IRRKinetics)kinetics).resolveCurrentWithStructure(getStructure());
				//}
				kinetics.resolveCurrentWithStructure(getStructure());
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
public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
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

			final cbit.vcell.clientdb.DocumentManager docManager = getDocumentManager();
			final javax.swing.JList jlist = getReactionsJList();
			final MapStringToObject parameNameMSO = (MapStringToObject)getParameterNamesJList().getSelectedValue();
			final cbit.sql.KeyValue reactionStepKey = ((cbit.vcell.model.ReactionStepInfo)parameNameMSO.getToObject()).getReactionKey();
			//
			final String RXSTEP_HASH_VALUE_KEY = "rxStep";
			
			AsynchClientTask searchReactions = new AsynchClientTask() {
				public String getTaskName() {
					return "Getting Full Reaction";
				}
				public int getTaskType() {
					return AsynchClientTask.TASKTYPE_NONSWING_BLOCKING;
				}
				public void run(java.util.Hashtable hash) throws cbit.vcell.server.DataAccessException{
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
					return AsynchClientTask.TASKTYPE_SWING_BLOCKING;
				}
				public void run(java.util.Hashtable hash) throws cbit.vcell.server.DataAccessException{
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
				}catch(cbit.vcell.server.DataAccessException e){
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
		final cbit.vcell.clientdb.DocumentManager docManager = getDocumentManager();
		final javax.swing.JList jlist = getReactionsJList();
		//
		final String RXDESC_VALUE_KEY = "rxDesc";
		AsynchClientTask searchReactions = new AsynchClientTask() {
			public String getTaskName() {
				return "searching reactions";
			}
			public int getTaskType() {
				return AsynchClientTask.TASKTYPE_NONSWING_BLOCKING;
			}
			public void run(java.util.Hashtable hash) throws cbit.vcell.server.DataAccessException{
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
				return AsynchClientTask.TASKTYPE_SWING_BLOCKING;
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
		final cbit.vcell.clientdb.DocumentManager docManager = getDocumentManager();
		final javax.swing.JList jlist = getReactionsJList();
		//
		final String RXSTRING_VALUE_KEY = "rxString";
		//
		AsynchClientTask searchReactions = new AsynchClientTask() {
			public String getTaskName() {
				return "searching reactions";
			}
			public int getTaskType() {
				return AsynchClientTask.TASKTYPE_NONSWING_BLOCKING;
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
				}catch(cbit.vcell.server.DataAccessException e){
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
				return AsynchClientTask.TASKTYPE_SWING_BLOCKING;
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
public void setDocumentManager(cbit.vcell.clientdb.DocumentManager documentManager) {
	cbit.vcell.clientdb.DocumentManager oldValue = fieldDocumentManager;
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
	cbit.vcell.dictionary.DictionaryQueryResults dqr = sqd.getDictionaryQueryResults();
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
	D0CB838494G88G88G22F4B1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15D3D8DDCD4D57AB8CE29C545969595961599ED38D9D1D2D131C52D1516EDD4D4D86CC6E5AD6D6A469A1BD46CF685CCD0D4D4D4ACAAAAABEC89D150C851F89341C8C99030D09918019918B7E7869DFC7DBD671C7B1C39F36F1D99E03F74597F5F0F8F771E67BC67BCAF67B96F671E91F2BF1930E0FEDA2EA04CDFA0087F3E1DAF88894F8942196D0E93080B79A9AECB107C7B94E039F0D01F814F84080BAB
	1C1915A3583E4904F0B204759F4E4CDA836FAB05E35F6827430B87BE159097DE7A7B695E7958B7A11F05B43C3AA39B1ECB81D2GC71E357B6200FFF1C71E084F5F47708873538421201950B4F4640BB8FF2019G908F10A452E7828D93E05C56F2112E6362B0A179D1DB5A92120FA4CF84FDB3CB9BA0BE8B04F766173590DEB6AAA7C9B39562D28108726902E0AB01E7C5D3FF6F0E6EAE032378E8374EE8ACB6D934BAE3F16F00E16B565A6AFDBACD3743E0B137990EEBEC5AA60DD9E78C27F95E53114972901C90
	2E58CFF0EF68891FE488E7C3F85DFE44D71A885E8F5E0381427723EE5B3F2A90E5EFBFF117D0F24766454BBC68B6EDBF53A9E93FCB375CD371E9EBE9B9CCA9A576AEEA0D4BEA82508278830481CC837810167FBD1B3E06E7D5172346E2B2D94CD5D6EB35D1EB7B11688A6FDB37C20C085B276B36583402404A5859D124A20FB7E0694E99F6BAE61330D2AC6F497BEE916227DC1E36D045A671E1A75292DA19ADD2DADDB6E13C6F7748BB2F4D5D9F5DF93789D25E61C7E2539728701EF1A8B63DC84C27324D45BB20
	0D14759983A96BCEF81FCFE581FF2478C00A0F116019EF7E137A8C8927C21CFFBB7AC67BD21425B1E3221033718AD5BB84BF981016FB0049D0F4C0AECB0B19645DFE00717C0A4BE22578F0891E49D2C77D1404B3FF0E4B2A7DA5AE0B79D754E6D2D77DBA6232C2GA2GE6G4C8648BC981715FF90FDAC3DF44390FD2CC9E3F3D48F980CDA1DCDE0E923CAD743D367F458B48DE63BC3E36E5635EAEC86CD17D1E797C41A07CA098D3A5C52BDCAFD3F9BF86C5219F5B68D292C103B4624F368ECF6C673F4A931EF37
	CDC7E3DB8CA69DB0B0D9854C7705521570B4EA6C0E7DD62D4621F3D13078D7CAC9199C5534C20E10813CB33B3C3E05582B8274EF8150F2BB9CAAA3797D2033C1EB31F5EB234552BFE0EDA11A889187091D1F25ED07935E838F220DDF7D1660D2C87CA14C6732329512FCEA8D34A5525886310E0DB39440F39D50578398818281A281E2811281728E23BF7D3D9D7D29F6494DC251B317297AD36561587412434C0F6A8F4B6B5565149F11598AF101G61GB1GC9GB91DF1D9A51D58B73C2B7713640B728C27BD48
	93799BC51DD04F21248D946918AC9F9B2432107EE359B064D8AA56CF4CEF31405A7D85C3CC1BA5BED9B9B7FFC7FC26896CE484886844320EA9ABE0F527DAE357E1ED59EB83C757E9DDA54F7479595CA84A749F00EFGC83D5B88308B208120976098G380AC0868F7B818CG7DGA600F1G0781B4G255CCEC7E93E21A0538C00E400FC8D3483GC600A000A800D9G59DDF1D9E5G9DGE381A683448224822C6B0E4BEA84F0F423AFB41ACA668F5D464381397E25C3CC07F6D20B5B2F02C3DBE138D7E6CFB93D
	54767238F1C31411596E2AA159AEFD8436DBA072A07EC9FC7A2BA12673D6FF562A60BE8778C2D64E4467BF8278C4051E341D2BG562844FDBAC499D91DFAF8B311D3F7D4E7F6546A2CBA3396DE6CE25C631B095EE2FF53E361E91E26F8AB74CFB550A158B9FE66E6A237466130993A869C3AE61D43E1B0773A62DF223C6C83DDD6D757063C92B793DBDB0EC17F446967ECA6EDFB3745EC56514A4D130871EF503EC5E75419C0689A4B005941707F5CC246607D3AC131EFFBFB4B7B82691B0C83241FE4635AF08DA9
	CF2B4DE002A603F435426CEE526F4C6163F282A100A36373E8F846F6E3BFF16CF36CA7569D1FA4C49F3626290D0102EF38A82DCCAB7693DAD7BF41E47A6B376A32A751BECE445B192CEF661332D69DE80BFE3DD2F8F0A121E956E9EC5D7A16C12B2EFA40613018F759AC83D6CEF3FB9199BFE0AE4D40043E504187525C1BCF64602303BD701091B23AC59BF3C8DE6D35BAFB374DE0A5510D4DBF3238972A099F54994C5AFD6DEAF2FC51C664289930590857D557D9ECA60D31592A6BB61072E1B4C78E93DBEBAD5D
	8344901B846C9B8236923B69411EA781023458B7984BC9BC0E8344CE2146E294C50738B8ADA95F84C32E240CA6EBD97952B25211786CBE0CAF00F039CE9EFF9B0F8F04F018A23E359F63A3A19CA309E77C5F6D774E7FABA346A7C0B8C5117F55BC3E24877A7A9ED7BC3343016FC83958F5C786F4B0BCA4832A9FF8BD687CEE05AC2ED52C93ACDE7F9D2993C649D8AC3207703C5B0C7350F6111FD5FC7A77B05E0C4EEEC13A0CDE08872868E5F4A7FB25F45FDA11AE8A70319E697E466912811F2DD752856A895D35
	C7102E884235GDABD9BFB794173E3CEE730A1DDBC041381F28DECFCD6E2E0F40CEF299D69AA4578F683639BEA104A778E270B81FCA2C0E69F232B6F13525D67C0BA2D98FFD2FC0674C975B8BE00F411000F81C86AE3E3476CFEA61F351F50559EC53A7CFE96DF564F681B7A25FC5F63F4D17D4C9E317D0CEF01D1CA7770B12433825E9FA04C4868925D684EF7A2DD21A9AE2B526409EE47A052F5820DD3C297EC12524D65F489A6B6B6CEB1B13ACC3354AE3FBB0EF4E500EF84B01A59B81A503B4A4DFA82697C44
	78E033FBFE4CFEDF1FC43AF0B13FE9E666DF1996300D8557BE5EDCCF7A823A765127B92AF1965B0FC1FF36F56BEE499AC75BC152DE937AB28B2EE50C694C26758846FDGA1966CB71EDC02EB99C17DE1C2006D69F4CF6BB49196314D30387A0D40A583692C8DFCF69B6119E70D4BAA34E27B774AF75279D4155DEE69B650291CD837AF59AA6DD7986E0A2D0B6059E330599D8D5071BB319D38FAAB11174C75C43458E77E2B78A3D5FB383771CC57D3F5D2DD23161DCADB6CC1578AAB5331436A5235732D64F4B65F
	AE6D4B96D81F90827131G59C7E8D97711BE1E54F3462F3B9479859E3FC1683C6CCA0F7C1A0E083EF04445AF3A21D46497477905C1FC429164372A1F70A36D8523BB550FF4F9B628F7B6243B075211FA4B687698F1DD4D9FE822ED68DBE14D7F91146BEADABE0BA18BEB6E9DA34BEF2A917926C0DEA5F664BB5FC47812FA4168DE266B9704179EE8A6G30FE3541C270863341E150988D47F5056B19BCF50DE48C2AD59BE39CEE10D0370A65BBF1E525E84F9937CC94AA4E9CCAD3DB1773DF26CF0B33B3BB8B8E17
	3DB3E745276583B0D9C3ACA8EBA3500C85E0FDE52BD95AD7C681FE96C066G6A12EEA56382A6EFB1FBB469ECBDE0B51D3610F8E831661823180EED0B2BE874CE723E9E7DF9CCA1198FF575182BE4E99BBDA75E8696403520CEAB4A1D8FB2358C205C7DFDEAF2878CE059649E41F493C89A1EAE432016AE67A8261BF6844BFE9D60BA0EE23AD70C52F4C1000F8408BF0AF6BA270F606D2A633DE69B719B075EE0E765BA37CADA2E118B83D44BD5FFE3F0DA52D1D61EF947DC651ABCB7BEAD73CDAC57CBECA82F9EE8
	A69CC3F9137A647246C1DC6AB11477BF7D1E657D480E72B29E9DF634E50E937ADDA7667187BB5143E830BB787035184EGEA749AF32F689FE28BA616F2231418F98A73135F6FA89068FCC7E5CC0C592DEFE572FC6EC0F97CC016C8A76A3C31D5DE36E40DA8094BFB3E633F736556D612EF3751E25741609DE2C8D5957D74AFDC260CC168238611671E8152E758449AAF99490BF90E3512320779D70F21F740262B2E5BC3B2E5F9DEBF00E56B007CA68F221E3FD57862AC084BBC0EBCE33C14ED71D17732B59DC59E
	79103E056791FBF42922EC518EE7F551F5CCA3CC04F8A4B50558EE5D7C31543FB3B563D47DBB60755E3400634C2FA30E3B7C5B8AED56D85EEE959EC33B2682CD5989146F22E37FF73E775397D552B258574E27E6A489EB29FCE5A5ED335E60B29BC15E5093D8EEEF8CCA4BAD9E7019A763320ACE22CE2D4E7F4EBF67ACA769FBE0CED8E3B30074868D9A43D7E34B641E62C439ABC1A667C914FB4C91B53943CFE21B59BB08692682AE152733D964751124ABBB057D734B455B081F6AAD47447E375AE6B9E657598A
	57B72A765745326EFA1BA877074711FFBD646DF78A6DF976F174F546EFE67AC7B27B546A9C9A03516EEAE0F899BEFD9C6BC5A46417F48AF58AB3C9F5AABC4DF4EABA0DBC57184898906754AC2F71A7D0BEAD5005F0DA6124F47EDD4CF9B71FC47AD8204DBE0374771C7C6F7CA3F051E24A2F46E8686EAFECGED59787A6D133807D8FB8657CABB8E4A772ECE1EF12DF987CA5EF9FD26FBBA736838BC748C5B53D94871699A020F1260192E67D1FA920E0538F2BF5C0738FA87199704C0B892A086A091A073AC980F
	1F056371AF3611F5D62F7BBC8DE6A8E7B3F80C5B02986E37ECA3BA776B8645700B5B081DE8ABA260DA4862CAD25E3540339340899088904EE5682970A943CE272A8C4C4E5F7591BB471CB574BD8916EE97CD17B80CF42CEDDE7F33F49FBBE3796E34F41536190EB94EE6F9971E6DEA1B19CDA22A08CD74009F8B9082908190FBB65AA43C224817CDF0CFCDB48653694FA334456CE15B02259B53BFB2FE97770F0CDF0BF1E4FB58DDA3CC7728F1E47A4599C726DF22D9AEE772596EF9640CF18F33F4EFA952954B42
	E532F44C8F975510B506C6085383788104GC483448388E3519F7FD5315D17BF52063245D28338FA0B253FC63E9E4EE51DE899190E610AF49F4B4285E35D5331B6FBD9B351D5E5FD04AD88C09CDBD32F504857544958379C72EC84E06D7CEF4C1922BC5A31584E77D8C7260FC311CE2ECF08CC9FA64344BE14A1024B7041111149D028C8A717E1362A8C5B8CA8C346B814610F0A3C6420AEC33C4FF49563544A753175EB544B158CAD45B2CD6E1516E98B646387908A90B98E4B7483932E0D450C433E732CDAD2AF
	B24F094BDA87D081508EF092A098A06A9C2C97CD5B2BFD550BAA5CE8EA3170D7154A41EC91EC9B198DFF6BB3DD6CB96E6918FD5A96496DB39B68F24E853D4FC57BFC550F7629B8974B3A4BBEB2FF33AA52495BA94739EA6956BBC6466FDBC5BAB93F00F3557CDB5F0ABA07F21D670CD006F7FD4A902F2A732401117942DDBE53A51F2B668B579A55FCA15BBFAE2B409FFD61CFC634CB39BF5A45FEF4E4F21E65B31D55DF2D4FFAEBA7291B7E90978A90859087108A10F79E54557330EECE5CCE66C6C3693358C8DF
	D9A9191C4547C6D66E950AF472363563BC35F47F9CA13FF427AFFE0E73545A51555504CE259D5D27519AAC58C9B29FB934D35EC786C01E61GD34FC39F7972B8768D7167210F5C2D10EDE8BABD62D3275C73556A6FBDC750CF0B4EC799CC03A3136104A21D3C7EEAD5E518E4C1BB1C64B2FCFAFCE4B2ECD024134B90F13EDA3A190AF4C32B1BFF7119EE4679EA3E74C5E13E3AAF35681C0EBA034E28E536F951047365D95CB6571E9819EDEED624135BA67F8235F487CE0E0CDF2FA21D1CDF19AA3FFF53F40D32B8
	EF404E985A695EDDA724F3GB0DC53E1028B06F07885E83FFF51F500D8884F86488CC0FC96DDB7A0EB747981EC7F1761AF6AEC77411F6421060F752043EC891E71887349E321873C27F3D05233FA2C1D7F6D8E4FE703EE5E21FEB6E84A8E7533C1F76FD84E70100774E4AB46BF30437B592169BBBC1F9DDAD969D94E8FAA5565AC28D417734BCA6FF296D7FA1273A3EA5BE75308CF58864C8E03C9C733E675D767A4673220E75CB7E0E65DA6495DAEDFF39030AF5E320B2C8FD686E01F7BD065A6C159676E549A9C
	969BE6522253587676747041B0D90AC439232ABC5B688FD56AB61ADE25EE23A72A3C5B6839AA6FB61A3EFA99CDEF376B6CF420D02BF1E844383FB848B9A64519AF3267A260BE41A716AC11E62743D2D346796656F85671439AF59DBF2DA1FB70F64B002DDBA7572330A60F473946B08D8E1D090843E8B652748E0D2DD72730C3B9CD4F62BC252F2A71E4AB266BCC4BA2092E9BB7E19F6850E0BF5C9100678E2E26E75303A0BC99E0AAEF27AED12CE19008975B20A4F8E67C463D5DE13C2F363A71AE45366746BE35
	7C7244BC0A60D9291A5FCFC75C72AB437CAA8DEA793508F96861F9D2B53F85B67403A6072529D4403E32F7277AB9055D4D5DB60B51C8B635859CF7BD33D9F15E8F6C91887CC2FF03632EA2BD7629D13FC1DB9753FD78E488E7DF88F33A8B51561F183C756F95973A6B51F4A1566F8BEABD7B6EA5356A3E9BDC6B59F72F2D756D3BE1351EFD7756DA5F3E9BD9CB566C3B8CDA03CDBCE5474A658937F2A963363E5FAC37B5D9B350028D1C97222D9FDB01368E3C90ED3D0D1E49188661840094EE6B22FEEF364E3948
	5D566BAE423D0A71F6299FEEAAC5FF6AF438613720FF1ADA5578140B79B7DE246C33D9FE6B865C725B0478048129FDB68BD8271FB3226E1D97216E7F2067CC02A19C8EB06DA2547DE52F6B2E89B21912396E3FBBAAE9BBFF5C424BDB28736CFF8B6B547DEFC91DE77F4B2D736DFF9F55F9763FF5F53E7D6F1BBA6FED276E181B7D3FC5FC11532D9D5A0C7E79CB19DADB1099884FC0744F4BF7625C23AC904BE8935DE3B4C2F8ACC0C8A01651FF68D8BFA2503DAC26096178C0CFFE1364A65F26F274437BD5577312
	44FCB2AE7614DF6820DBFE5FE1FE37A856FDE9BBAE66D3844F4A0B555A5D7663527CCACB901FF3C26A57DB8464B34BEB1B5862D16EE7CF38715908784013D29FA6E7F85858FD55A9D2B6FA484FF9B1BFD7F1101C7DB6EA86AD838EC1C8391850DDF7904BB9G4221GB3G92AEE66B522C8F396D98E95BFAEC9A9338949DC845DEC277138FE35ACA8837GB8G02GA2AE616DCB99112F3AEE4FEE77B35ACD0B48595C9A0DCD5BC865E151AC2EE3AF11DDDCB95F487CB121C2ED5DBC7F52382C12CB315F583AEB09
	348E0863A4E677C10D5C6E75173AEC5DF129526EECBFF4B9BDBBE63DD43ECF7A91457BDFAA5FA73D5E047B2493A0AEA38867D213681A3E9E42CE0EDBEA417955D80EDBC6575DE3A11C48F17775E15AD40E7B249F53E6DE06381B7518361D635A8C185648F11186CC6B643873AAB06D344B30FEAF594257D8AE437A3D3D176817FBF9DCD691C06D65D83FB76A3D79F547651E6CBB110E9D2C17B3BB26F37CE40A77136019FD4B78193FC008CB3E9C655FE2C21D5A2F603AD31B1F04F070952853BC3E37B2798A54A9
	A40B4EDDA11C8A109B0CBA452C7026D3D130A71DEE25FDF4E530DC273BA93EA9D82ED395BFE75989F1D303D17E9ADE4605639127B5CAF5E23831F49D3991E8BAB85D6B2D18D6BF9E7509775ACFC60E77240F0E5AFC46F839BE960A0F9FAF57E79EBF8FBA9B621A2EC4195EB622CC919C7701CA99DDDA06E594F7A51651BA3A3E10F39558822072AA54692A1DD21D5A81EF3C4A25075FD51EF47AAD6D27022EE232AF6678880A0F106019CE6594CF423190D79E0272D753F548908807F35C8594B79542319C17F49C
	F5CF6038E7697AE14955204F55085BE6C33AC60EFB48027905F05C17D6248B633894EA5F9488E7DF0338F7F93DA838865B6422D3E4BE8D43090DFE57D09DCE61D84BFA8D1E951AFE904F6ACE85DC6AB55897EC6CF4DBFBA3A7D936EE5DE5B3E82BB53D2CA5E7E5387CD026183E6CDAAC436F8F51318104478204DC0B67151DDF62F965493F1DA87819554F02A7341CCA0B38D6BC8BFE2D6B2CC3612978B4F15F74B0724C1DG761BC0F0779E161E83A2BA1A5259DA9A0B1F6396CF46D01366CB63BF69144693F91F
	F0CB7F13DBBC31511AB4964FF4FB6C8B54AD7531F041F847D80F779184CD903F87901F0133464F0F5C6CCCE73A65ED47B9EE5284346744D2E24F426B40FFG5A2F43B28F5460DA1E1563223B518F022E433A330676E133A01C8A10B791711FD3FC3E78BD8A7996C05CABB2350B741E5A90B924CAE8C97E5EE8990F87A90F61FC1F38C01607B7DA667F51E6FA5E8C8671A593493D95E37710128A1BC86C740525D624F52848BBDF04F4917C010640AA919FB211359B73B92FAF7B08CCBAB3D950528A2C3EED6FC3BF
	CD82FAA6430DFBDDB2EC69C799B22ED79F8FFA13A16FFA178C0C5F33FA64376EFA6477E8130B5F938664D72E42EF018FFE0D8AFE37D7A03F766B51F7D6EC113653C6407BC9F8852970E56D71EDF43C91A672D84071D1949FA54133F62B26974F6445C0DCD9A87AFF3D9E7DBFB894E58AB392BFC864615BE938167B7BD6232FF5E5841247D07D555D5F1B074CE3A45F6C32F17A0FA69CBBE75CG759C209C209D40F9837689E6B31D3FCA4FA27372BC570C6599FC835AA655ED9DAD967009B738EC10FA0327724CD9
	C47B715F32F24B62780FA83EC802E765B9110E4DC8389E62A67F964B73AFAB30BCF3EEC4196E205F41F472707DB41C4C4BF3D75FD0ED2D86A4DF5F655979DF7170D5462CBCFF6E477274831902GA2GE28112EF44723C2A553D0DCB5B87C1313E8BEDD85FF343545B97A994828D018FC59F2888F355F7D696EF72F1DDFD985A3A1F5625E09E9E20E172DD1E380F54AF37D3300AACA38546E33522EE0D248EF9AF0B9CDE37A2C17E99G338132A7417CE8926A9ADCA62DAB0D006F1C645255B149D3DD8926E3DE7F
	C972366FFA0A8F19A4EF7B3E56E05B978EF1393F433AF2BBDD27F1C2B80063BEBBC3F061901E4AF15789D80E093FC359A753337065B7415C82408FE082C074CD98FF8F0DA75FC912EFA0497703649B47F89E3FE3904B0024116A58F113D26FF2994ECF961EA08BB39EED1C47E2D9FEEAF7EE48FDC0BEE648D221DFA99C2063F756BE6A342CDD349E47F1F36ACDD86FA69EF72FF7914D90946B5D4293D86F32A7FB9EDB08F7CD8985C0C360F3AC63497231451CA36E3C72773BF86D33A12FCA8F3C100F1C9709EB11
	70E2F9FF5F6A4AFB3B95736ED449BBDD163783E8882C92717E8ABD161914E324AFCD58678481BD633B420CFC43BC68A4659DA5639B2360FB6094F95FA860D8FD42B169196B72C5E72B5EE5C0A0DE641FA4BED3D7DDA95EF9730462BC7E505A2ABF0DB05D6AC343CDA72EEB9D26F3071BDDB6E1788FA83ECC02E7ED4B4EC3B89F2C0438501B718C23210D34FF193744E52D8328GE881F0G845E026BF28F6EA27726F9BDA355E231B66A0E6A0C12ADD4F13F6E70F075E3E57940C21CF7C55C73BBA1721CDA0F5F40
	065E424AF02A78ACFCE57CFC72E47A2536937D12A1AE67D698CF82B482F482B8EFC57D96550B67684996F4154321695653AF0B790636C1F74C05A68B28CC370F364BF58B3855655793EE7554AF2CAE23EB94374A57A4BF237899374A57A443CAF1CDF29644550747E531355F3B79D9644961EC6DF7AABC63425937162C4EBD570DF5AEA95CF71D4B388D461737396686C537496BDC5F9AD28E551A6EFEBC994D70EB75E47CD9E7B09B6CFA5719E9961776BDC933875AB90CE1E57C44AF527A1AF325B18DF9A97A15
	5B186C353739568D583C3A550273EAA744FDC27989349F44FF2C7DFB63E7017B56CB5968DB79B7DDAF54BED71B7639873E61A2BF61F6975F6017BEC9E7FA3574913EBD9A231D0C3519BC3D7C9CEC796DB8D7BB50A1F0DB4C51AB5388FD18464A532CB964CAB33DC21A0675756B695AEE9850C75D0E7DFF000E6092A01C4CF10BFBF1CC10B7857B743BA82E8242AD9C77A04511EFED49F9CE06EB6875B696467443295B4A762F4BAB8F744D1172E0F560303E568B0DFCCD408ABA0D1D426AG8987CFE16BDCD93CBC
	7E3D0A24432F4B6D6CA839D816F65E6F05CE41B52C6BCC72B52CFF8852B52827CD72B52C79EE718B4C72B52CAA37B52EEF4D72B5ACCC4F643D46CC6E4B12FEDD6976DDA5ED480B1B9B766CEA5CF938F9E7634E1A16063DFBC4BFDBC96BD7BC6812B485FD6A0FA538BE159B4167671B504F4AA378FD21F40F489F4261GF1G33F95C67B40EFCABCD3E29956E00714B9DFCDE1F6549A7C8BE786E9F616EFBF112706C884FFE49F8BC601107545F3C0DB56563CC255F7DFB859F435D61396DC5E8819A7D9DAE3FBBF9
	073C6DFD61835236395DE7A4BA9F73BD3FFE74BD7FBB5077EE69776EFB897D5EFDEF05513B6F559ABD799E13E9E39B36ED51F7F8EB1F3712AB57C43BADB2215DD2D46CA61F5FE65F893EF3274BEEE5F74A6D961713AB359B3BA8089A0E49380557594ABB51EEDA9FF576F29FF5F61A493B5D5EB1F9329B2BCF3716E13DF35E09F54CF9986BDD88474D6D44FA97FDA7561FEB687A71AC8827F05CA40A4B3E0BFDC74BF0A59AD287880D1A8F4F72009FAA08FD886561298D7FEF6773E27F38136C0BF7D9EC7450859B
	9BA570B368A520F3BD008340DF541F257B6E0CF2BF3D698C261BF0979BD3C542F306180E79E724007EB96BAE5FE32A9420490EF479E7C1245CBF4D87C99C3BE30C9C01F6BBD2AF52FC55C4647AC1575B7B23855A6D8123CEFA721E51CCA9DCA166C3F2F11D2646BCACEBD708F9508F3BFB8679C00D4507EDDEAA4613DB57F6B7EA3A4806BF7B9E3B454DDF0BA2456F31A351166732F933EDC0476AD479892C775AC8EF757EE0A7F9E7E94658B0CDC8A40E037CB42EF8339563E33D66F9FD17406D365D249CEF5518
	B11F6C3B5572C97A1E6573F0B78931F904DDCB62EB013E73EE1CB70CEDA5775E34EA7A14A7D88159E433D8F5B647A04352C6C72C93EBE8DB475E8BA46FBF9C7AEF569E07D69F35234A0359455666797C55E01B7A792BB3ED6A67F34735F9BFCB7D1BB64FE729035A2477B032F23CD8C77C588F4AB084A09AA096A0111769E76D1F89F4AE28B28F5C296DA5673F48ED458E643F6000E7DD338F783E63F85581F2AE284B827550C472C77C0787C8F9B9ACD60A944FF99FA0B63299FA758EC60A673C8F905FB46AFAA8
	1A758BE55B312F4838877B0ADBF670738877E09F10CF671CCE888783C4F2FCA145136FF6483946D80E3F25D44DFFC83A21788772716E230F28727035B7A86D933C4D99581C7AA58D2E072400BCA577625905973BF0AC203F9777E917F4935A89900E8408613419DAECC7926FD5EF0FBC37D17ADE017B64AA3D404B724E83646ED455B685BCF0B70C6177F67541383DC5534B6D750B5B59370228382C32A86F654178D652FD43FA2055827883CCG08G188110G108C1079FB98DF811483B482F482B8G46828483
	CC7EBD561D32DF56FB129D1062A183DA01EC9A8329C2227CC637332F5110DF6C6F0732D733406D6CF6A224C975110E758915DB14630536AD586F675C47468B05702C380F79BFCBF73DCEB9DE38C70769DA6EE36385A3C9A3268B3E0F2E6B38FDDF2CAE5385CB080FC9CEF517B27C73CBC97B42472E62A9BC35BE4BF3BF16CF2EAD467B7D3D1EDF74AC5F43E5CA1BD5D8D02F8451E6A9704C3E1F2DA1B01AF9A5C8336EFEE61F8AF8B609B4627CFC930E2774777B1EA71D841A407BDD6329507B656329D91B568A0A
	F9125BF9F9C6771F6A8F84653CC0CA486802F2086D58F8E823087B7DCE97EE13B8C65AEF160F11080E6177631DB6DD7DEA71D36F4776E443CAECCF626ED79F17F8AE636F0D2EB29E6775AC39677A3425CD1AAEA7BAAEEBDD34777AA4FE674F3F0DAF0F9E2E5CDDA6175C5B066D1B4C477E444FBADB23199F7A43B3A41AFDF7AA1E1365E7DDA32245F33270CC90E9D8BD4D52782E273FE92465CE2B61C67CB6367BFD0E437357AFA86E092032G3F6C87700CD6E69D1E33DB87380E87700C56A1A731176BBEC57A5B
	A4AD8607D1272D269530DA27B10B36EF2AA3B6A563022087303F321C266B48901E85104A79B94FE01F177BA034658F225FC51F41BEEF6C0318475FA9ED9404E381E6F35A5785AC6B0CBF8C352C314F9BE796841C973D596361BED2DA23982F8BCC28DB459FF07C7187C16AA38E407B7FE1A8FD053F5B5D87A110A6FCC8696CEE77EFCC05B4F1C3CAD76FF63EE2B6249126FACF477AD027163C7371AE1BB66098D9CB5A8FFE5FA53D923599884D3DACFE3C4E73D8F4024E77D8F492DDBF564198124C7F986E965A67
	D9FAFA6CBABE661D224B613886FB1345EEA01230FAF6F0BB7AFDAE68DB845082201F4A3EBFE06D7DC3BB303D77D33109FC9FB588E842263A5A7B282972767E54F2E297F6DFB85EB8A6BD0FFE6D5E15BCFE1F4EBEE0F458454A4A621FBBC678C20C45F814F731E2BDBEE61457E32247B410A1FEAA7A6455DDA3EBEFEF9AE13AD943CE4776174E226358642972BD4373A9DEF8C83EE738329B5B0BEC0833BE047AAE513A1D9D82FC62C35EFD1B4DBFECF4ADB9D56443F047A8AE77E106E37E55E2706C473F98FC7B31
	5620BE276AB3A867D496037A1CEA40A01DD3B15CF1837149DE984736F33AB39CF700634652BC892E4D20F568B97EFCAA9741576B88C34487D2390ED1DC3B083BCC02BBA0622EB4103A88AA6BADB67EED1C012C3B74980CC6963E1E52984CD0B539BD42280C862BC6A2774D9CF700174518FEE5797877CB4B0761727B14F41F76C969187F1C59027DDB8960758FE39F724896EC57A7BC0C7D4DCCAD210D03F092C066A37C6EDF9D7AE081605A9F41BC3E6E41BEAB70914C233D07FEDB8761840014C770FC72340352
	B33C857F592BFA06B7656071341CE94C0F4B2639767B463E7409F81F3256027A68A1FE42B414E5328575091B0632FCD8C24F49CD07F984C065F45467130D28CFBB608227E39E3D9BD11FE953B10F4BA82D70E8DCD69EC04923C89B38897B607AC707BB5E1AC316DC4431C3GDF877363F90F677B17110FE2FDAFDBA92D6FC9004FF8ECA8757D47B6251F6CEB137A09784D2C5B38AF8F72AE7211BF3365024DD89E15C06FFC8CF578F1B36A907E985A72A1FA7EA19142428C30678CFECE7EB016C7896074B3B00F65
	1DD89E93E670736F746E04B888A781E4BE0E34AB3B70FB070247113625165036C3588A607FB85AF2E91D2F36FB4263EAE72472FD26135FF942526D27692244BCE360B96BF14F36CD01B856A73CC2471059FF14779533A9BE6F0F723EE260B45AF19D44797F9175BDFDE6E4FDDC408853BDB46CF4CC2FAE0148BFDB21EFBF4527A874CD66E3690CA740EF1EC0FD7F6EB6BE1D817804A706B2DE8CF42B8349E21A4C98764C07E7C90CFBBE2CBEBDAFA86B580B02324D3E5626248B35A9694E37AA69AE32AA698E1B15
	F4DA33328F98379D6DD48B721F0C41B9CE6607EE776D316FD0DC1F938A58C6BD3E9D6BE1C48C562DF7E91E33A11C71A40C471E44FA78568E2C77E5CF8E37DDDCC51608C51E379A31BE9B1FC41E97B7901E21900E8208FD924B7BDC3AC7A0162164FDED77682F65EF9AD59E2CCC57E915653CDEABADE7D61E1AAF09FD4439899D76B0FC3EF3098B0F439EDC2758B2323670D21F69545B4299B45D6CA745B3BDCF45E565BE6539EE96BE05ED610FF48CD271143CEDE825787A2764ED430BDA747996080BFC8AFDA5D1
	B732B66D2D91265B50B33236F0869DC765BFAD5777990AAFFCDA2E6F219E54378C62021F463305EF58485CA99542F931D00FGDAG74GFE3138E6BC5DDCA8F8DEB3E60D01EB49186955277A2D37EF3FF17ACC276EB73751F4C131AC4FB0F8C645FA761B18D8741B0B6898F0D62C5C0E41941F94AB376327A5E847D4086BFC867D660B0DA3AB7F529126B38EBB9D53ABEB9311FF6AB3F2FDD7D0FC4CB3F2FD2F62E361F8082BFC96753DEC1314FFA860A31F9DCA9FFA316A5DB0B3443489AAF90867A5D457CB13C5
	7A4C671469D8BB7843CAE55B5834D259D7361914F49DA665B8F87166117975DA1F69547DFA9FCD172F221F9A14BC07FE7D9D9DB357BEA7AF67EA0AEFFACEDE4E0F7133479D90977D9C16739343BE9BAC1E359EE13A551DA3736BA9F45C3FAECE2EEF9445174449755D59097AD6C05C04B854B74FED5DA6E3A6340BB3DD36AF1A29AC8746E79A1DCBD44E14FFC772B44537C8700C7F4F35789D099E62E6G307B2822D7323E58E8B06BEA58AF68F2FFB452F564126763326A1F477D62A9741B538E88DBB92E0F2E91
	86C1F8B2474D235F9FC7C3B876F95475755382377BDC497B095323BFAE8A7A95F850ADDB717D08647D1DB3234FFB25B07A633E857315ED5622794A366DC65A0EA5C039A7F1FF8858065F3BE73C0038B95B7877FE9CF7FAB3FE6F5E79827A4D850AB375C33349789126FBEA5869443B93F610BA66F8C15EA6B8A95E6F85F91BB0FF8736898190174475FDF70714FFCBBC0C0F62075257C5385D85F1925284CA52052A644164DB301B489199AF7F76E0B94547C870CC6EAB9A705B03C408EB7C93165DABF48DAF9C42
	53B86E5D96FAE69142C99CF7BB3D47A2774FF1D9C5FFC61C064E9FAAA15C72E734C3B3D9FA95F56B103C3F2A9D7D7A746FDF0147D3BDAE1DDE103CEBFBC61F7729D165414671A9B64FEB766F59FC2F59E75A55576C175A15EB76AB6D6AEB76EB6D4AF3D053685AECA77819137B1F35146082A0BC0163B65136E8AA0463B96E7A8D841784610C9779583444D5F657C85E3FAA997DF22C7C95F8AC5B6452A9CF72FE5D465167FD4F2872E0E5BAF91332DF3BED1332DF8B6377BD653C087E10456FFB4A6738BB7A114E
	48F12B797754CE0EB3D07F1A8661840E7B115E31128C614C84742F6FA567C16BA56FCF7E8AE7C37F7AAB70383B5325530312779FFC5E817C5F73560EAA8FD6269B68DEC0AE14E7E18216737EDD84578861CE0E0B2DC79F994B4B7E0F3584978B61940E1BC9F185AF417C78A544F556229E04CEAEC72C8AEE78369A64BC06728D76F0016D3B0F21F7B1BA484116BA8BBD8F483E9D3E0A4EC5BAC1DF63CBF8766E51EE657933173AF1EFBE8468A2G92G12DFE27A33F4E76996A10F9A0B116C1BB29E6369BCA67BE5
	18033D0CBC161EDE0034821ED971A7E209F95DC7FF173487C4E5F9445231EFB9242FFD994F2B5FB4C06859B9070C8AFE974D4B6A679C968A2EF38E781DB5FEAF6E7F327CDE169BB763DAF8504B7CDE069D82AF3353127705BBC63FBE950CAA8FD6CFD6FC2FEC2F57FC2FEC2F9F23EDEE9858A56AE52CE34F53FBEC92A01C4CF1079B305E653D02B6DCCC473E95906E84F0F27C9A0A8F04F0B8C0AC47DF5DA0551B50FB532342C3FC2787BC63F1279B8FDFE71FBD55C7EF5F5D2C945E5922DE7FD6FCCB67E5A0DF46
	AC2C9BAB76AB6B5F7A7DD87F4AE6317A57884F4ED9527AB73DD53D7E3DC46785CE209D4BF9B8354A7A774ED101677522C5DD5EF934DF8E05FC42F9DE6716AA653D26141FD79565CD01E7762BD2F99BB62B4B2B21F5EF9D50163C4AFFA7F003D25E0F48CFCC0B6F1503641D3585857CDE2A26D7FD5F8925FD557D0EA6672B727B9CAC47DDF9FFE3443C472A64BDDF16377C7E27E0C95ECCAF3F4D6AF68E26B688857AF0EE836DA1258DAEB961AA333AAEE5B99C6C6265702AF8EE981E592F815C05799AF7A94FDA0F
	59454F923FA61EA506E713180E5962EF757CAC71EB3E4F161D841A4057A4E709DF135B6269B5E46E424E0E6D376BEC6E3FF7A25AABB31F535472DF5114D232733F0BF74AFF5F13489D7E9A1E8FBED92396BF75B56CDB468BAE3B5E587DA9BC4D3AE3857C87A9C9D995FED68593AB63E7C5859B61FF99B90F4F6671D9DABCAB918779657C8577E1AF5F476F97F71DB5EDB418F512132638AF7A931D3713BC6A7F027B2253E93EFE908E86887F8B4AFAA44DA56B9B2708DEDCCEDA2F0A3B0C1A6EFEC63BEF010B56EF
	5BD075E2B2ED2E40BD5FE91C77407B2E7C6E5BAC7D7E0B61DEB6AAFD72EDFE4ECFC8E4BE19874F22C4766D8BCB770F8A25CFAE2D47F435094CA7BB60E9955331F345C79BFC1FABEE780C7819F886319E2636C6B23D45EF7098CDE4366C9B010D8CAF9C1361B7B1FF5A3DDBFE5E1C48648F3231330DD1D5725FF2A271A1909F4EE93E68D1231986717109E86F0E1FA4E55DB2BC3F7CE9BF7AE5B231776B7C9C52FE74B56D6B68EB4BDB89ED8804A3GE63C0E7C9F5C6762FFC06B5D57BE5E6F2255168ECF5607CBD1
	56D940BB7F2FA86B52B21435652F7C3CG7BFE8A4221GD1FFC5D947363A78DF3541132CE2FD63F7DB447E555BF70AB736893CBE9C623F3D157DB7FED7A099CA08ED73ED3F3878D79D9A1E7E633AD07FDA487B645F78FD2FBABC7F9671B7FE37A5BD5BBE9B42993341D633713C55CD3B78DEB1600C33B10FB7F76158B6F4B66671E9BD210D07F0B2C0769C244DEFC09D574D996E9974588E172D2A797D679DBC6FA9B518F7509C9C6FAD537ADAA3D65F177B48E7BA75FD39D62F69E64C7134363B1D1EC7C818A3DF
	132E237859F364EB52CF70F38AA9905771F7FEEFE20514FFB460E37FBE1473F81F1A50BF92013E608D34294104F6EEFF834B762B8ABA3606F098C0749B689FDB6AD126B84065A4E19E276B51BF6A13B00F4914568F4241040E531E351B77899C17DC05F5D47887262F2BA6694BA15C84207F87529E6CC1DABFCE3B1D7EE6F7A404E3G927F0136EA5BBFB25F5079CC276E9B57F8CDA74C75649B63E85B19BBD76E9B97D2FC41DC39EF2CE945F2A802384039286F0C5211697B3C4FF46A7A2EAC7316AE59233E6CEC
	E8661BF2FD17D2FC6E1BF2FD2FA863670CA1EE6C1B28EF2B5B1911C440273EB9147D19EE377D191C64382CF549C3C977134F33BE6A76BD6CB31D3AFD03E93AF2D136C6F8F6FA1153110CE7GD653F65CBFD9EE5FCFA8BEA8D9EE5F30BA346F84084B78A75A7736DDA35B373BFF0469D2061D0E69F599EDEB747F146BFB8D45BB7EA95777AB5E0E79C15CAC2E6F278DD27E75EFC13B7B56D07CE2973DA75DAA239DAB0603D572E069A2EB54741DAC52C72B24E36BA4A6BA560F057859EF216CB512FD39C6497BB33F
	427E595FC715875379278A65DAD3EB05721C4ED7BE3FD3D42F231BFD26D32F23E61ACE1847724C1D675DDF8A67E19D3D0A7631957364BE9BCA71757364BE7BCD85BF3788F11173305CAB6AC7D657F60FB05D18E127E3FA95503D6A7C94393E6BA93EB0C52E6F6D3C0E16C1DCF08A6AFB4EEEB57E29A94C6EB9EFFB2AEBEFD7F913FB1DCABAA65FBDEC9C72367C779926D2FC3D844F64EE2C46F1D6BB44C53D4DFFEF596BF94F02F7BC5E51CD47B6E56F48795FCB71356F487957B811FF8B44C53E437742681CAD7F
	DD984B3D4B4FF350790EF8AEC7727ED16B683799DBC615875379EBBA772B87FDBB394E0BB73B744C153CDFDDB67ABA5FB92ABC443EC165BBBE1B5BF7FC8CF75C2824BBE3D4525DCA670F8E30DF403BFC1DFE8F41CDA0E10EDB5DC4F0F190CE62382F69DED1C6EADCD6DEAA3F53E4174B66DFC85EEF299BFD7BBF72AB70382A5E25D328647D1B7A5167DDB72ABCD81916B7A87DE6FB0352EF8E51797BBAA87772D474079BA92E91429D9C17DD07FBBEFA0EFB228669A2B92E2496692626621A5FD8ADDD8B579B3A7B
	4DBAFAAF91DB8B8F61F73F647E8B530F23735CF5902E6038146F882E8942DA0E3B151E057307F0483F50E72F2BF015636FA46F259523DF260D3F820FAF9BDCBAED103CC77E8ABE7B642F6033D753B507B0A84FC8DE4EE9BFF0FF62381CEA44657E9B4BBE3A1A6CABEBF978D19A766361FD5543151F64B5BC9B758F1B47F04EGB05D87FBD077D02E5BDDCE7A9B8FBC7CG8D673E476F20F57EDF17DD3244B66238697F1CC73A6CFD25B0E33FFABBB2EBBF36A3656FE11BD3D526CEFB002FB357F35A07F56AFBF5FF
	26FDEABB50E9B96D51BD4AFD0F5F6C41757D2077581E68E4F8C63FA75D937D26C9FDCFF4A76B0F01F616CB76BA65FE603C03824FEB00364F6E7770876CC43DB20419E2BE776DF43F9B7F83497D201F57715F3FGFA396D657B1D05C0C3G77BBAB605DFDAFF5FB27AB6FDD3518F73DCA5E72FDDAF95E9D123C45BB993564FD938F0718544BAB02769DFAC86F60B6D836D5D9DEDFEE45728A85BAD2DED1700C91E595FDE21B328CBAB52EB208586DEE5BB1DDAE7D577E007AA7FA306D824135FF1A8AB429927DF366
	3B74E779FDD50D79654F77DCD618DF8950544E1F4977E35B674B6D39E989B9E70AF7AF57E84CC7B5760D62DD9D6FD6CB772A3410161C5B88A1CFGA6CFE08F4AB34D03BC523BBB6301A6C9A2CFC61ADC1E3877499E4E3EF6F2713D215BE0551871F291A653DC277C7E90F20EA48772D91706657C6EFEF79F31161170C6DEF6FF553A4A2EF94F7076EC6E5803F3358A6067GE0EB5C7F5A03F5EEB260589A7740DE7A3D9A04D381F253E70A7BC7CDBDAE7E1F55F95A33926FC85B69FE8FE8CB9D36EFC5101F67FD99
	FBAFF9E7E91C3518C64BE538DC6F221D5B6712E776F74333471E2DE80F9048BBB69D6DF179D634C7F6865AE369B6BA7F05F08700A383E9F77F007285E4F85369BB23CB2776EACCB3D5B50D37EF13BB689DD94CA7358708AE2E7D7A235DBA233158C42F7026FB776ED50373AF657BDA29DCBF9B5D97AADBB0B32B91C03BG75FB2087E9CFAE982E2CE74CAE1D3F9E44FD1F08854873B45D6F1D8D610C77E7E6658330BE785EBDA3DBD31B6EB31D7A1ADA5ADEEF694A5ED7368DECCD64F5DAC76A5F1FA9DBE31ACB71
	AD92BC733793FBF10D29936226F2FD8F385D67D038906C33D049D3A97729DAE93AA6C8239DD23A162DA333EF174FF46A763D4AEBBA270A4C4C0EE753BA9730D0EE5F8BA8BEF8215C3E395B503E2190171909765D7B03143F9170FE19C3315341EAE93AA0C893B624F465EE6B6CD132B4B1AAF9B03DDE23F5F0D626DC5FB7A8BEA9D32E2F135F07138AF11DD9286F43EEEBFFD3819F17B59439B78CCA534D06B442A2D73A5CC51E641ECB6BF161A2395C6FD2FC59A2395CE377235C1590978A406684EB69EFC5E7AE
	1E19D53098F5F9037E162238DFA3F9BFEB6F684F952F9CD59E6277A7F54A750D01BA657AC6D02D12EEFC2D126EF65AF715017D9A398D7D37396CF631647D03ED23EF4352D16541F46E79C1E91B7E9F1436F1D4AB69862B15F4FBE97F588976F3ACC65F9C2F4579EAB0376BEC493E57BFA56F27F523EF57ABC615875379CFBDCAFB3D5C2334D75DA05AAB0A5B6B41FD743CB50413B92E2A051F374EC69B266CF759ED3EE4CF607C7D23EF436BC715879B13166CD51F3F576F45F1D5C5B60E7D27EFD527FDE9AB52B6
	F25A1E9A65FC70D48D4EAFBA000E7E4E81BC7D81487A917B0D43F0AD7FCD03EC561EC7084FD8F832FCAC03C813AC66133D845AE400DCBE371F39CFF97EB1E99F26AB87DAF27E31891EDAB19D635F5E02B4A7819F88908E9083103484F5BB3C57FD8E143295432C7F382186476DB9CBC13E25B8D61DD303E3CC3F25B846FC0D5EDB9889619800C4CE7BACDF3395164D94FF67E3294E7B6FF8D43A45CBFF27C3BCB7B208F215C31E56E5486BC5BE768DDB06F2AD5BC74FE7C1B885A0A7E726F866A92F997B46C240F5
	66E09E1D4DF866A9B8877338105246C0B891C0D80E3427DBD0373C65285B1DAD725FB0B96C26DBE20BF75DBF58AF7F8D13AB5225710D7BBD590655351BD44F9EC8C1FD4CF9374FF46AE34E39B4DD53F2665B5A655E47BFCE08E7E3161F69B93800657231CCB74587AF170FE5E673FBB1C3A1AEF38536EBAF381D5BB3825EEF45D046E0D78F0E4CCEB779CC27EE27D9B4DD10A8DB188F9923D6201D46D07F0DD9A137D3G45472F105BE955BE34D3A244B52EC4BBFD5CBC32F381C5A3CC279BF6BA26D7DA8B11BFFC
	25DC5F45949F35D22EEF902F4753A02EA897759D53A2659F887850DC6FB6973FB725E33DC8200D49457E74AE765BBE90CE61B8B33D27A3FF554C2C12D54833132E9931F70364FD6E2FF0AF5962DF612C4EDCFEAF594569081B476FA51B44695E256DF0855825E5955AAB017643FA881F6438ABFE227B4DA44CEDF85EBE175DAE113CFF38EF74EDD876AB7048ECF16934DC72FEE57368731EB22ABCD819DE59227411EBDB50C72E90F02CD1D02B3EA6DFCE6BDFB478430CD5B8CE7923C6B986FB155F751E3A0A2D49
	672E1E19D538DA3AA6BF2EDB39A6BF115E45AAFEDFF7DCDD062F69EF94D7C2DE0D2BD106F9FB15B2ACED62E32C55CC06A0F8063949F04BCFCA999E328A7C7DD33BCB1E1BB443DBD7DD423F8D08861EA92BF1DC323F8B57D5CB56609866F1CA2B07309FC0709ADC63BDDB722DDF6C5E61715F52047C43A13F44B5FCDC54047C8B72107FDB14369D42D6G7FBC2C6FABFD5E2B2D5E8FFF6AB31DFABF7C3357F49372BC2D3DD45171C7C41E3CDF5ACD7151F972FE6969AE6C17E6C0DC49DA543722E9E47A6E7619CEDD
	5FB1DE53852D7524EF5FCFC47E3035F2FD6D949F31D62EEFEA936A9B8DF16BBE40B67E8875C5FF88CF78GED30CBF2E7E71364FDE657683701F3C715075886D27F0FGFDE7F01D2DBF39749C143C277DB47ABAFFB82ABCD85B615F25BE271E5005F324048F30AD3D2DC91DF6FA9352A6F15A3B3A5C672889CD98DEC343FF686EDAA2881367888B9CC2867909E40770BE7BC5E488131FBF06F08D397D4668903EA53F4ED7D038536C50598A5827238584D5D470F5215D21312DAFB0E99C5DFA1D3DC0E39EAC6856EB
	EC1AEEA0DD6F90BE7DE1672EDD8578AB00859B8A1A5B9A1A1B1B766DEDA9E832D99CBA724BCA0B449F78ABAEAE365B04E53C89ED207742A79CAD5D1A7E4A7569E9C2C60370BE3BF327C148A09767B4888B683DBA8DC23A43E2F5124EA1C38A76A342E9DA12230D9506C01BAB422FAB593031D0E41B0A8B8A486FB296D4C1E1628EDDC18DF4E89A708FF36FD6073026E640E653199DDB8B76D84C3A82D600861D56A1EC588829592F753A527658AC268272932685169E749F7137AD9C42B704DF0D5EE2316B8AB485
	2DBA1B1DA481C207DEA7662453723C9CC28E4F36D6879299A10B159CA5A6AF34036FAD63D846921CEB09DB7F7C50F51BDF1B7167D6FB3A70FE033016DC1551CCFE0D43E8B82E5356EA9C9AB133BAA14BEA81C7ED86BCF81E1DBE96589D03C6DD23105B33CFF7E4C0E787992B70F7D0C022D50D0453068107413821DE83F50A5C9F352DD148B0131FEF92D630E823465C3B017DA84AB66A3E732BBA29B31166E1BE483B124ACBF2A966392C3CF759971F6E1E9519AE2455890B0C960D368E5434589A04AC075E2633
	6BADC62D537AFDA54DA87398E99996537236899FF275093245248AF3217B1DD787ACC9DCD217A688CE1B3001D212D397451CC2DB03953B81C083AC57713833BEF6FC79782EB59DE942FCD1C962DD85CEAB34A2BD9A1341B888AF35E0DBCBAF753BF590E43F97412BBCB1C6F19D6863F8FE4E7409AFD4A44D064C6A0485C7492F4A00C8B9AE2ABD8326AE1D6D1C4917FD34B8A86182A23115280BB9E7B9EC9A333D47E2B3C16685B21DB10A0BDEFC6974124BA63D73B3955D1554A1FC428A8912EE6855D9CC9B2AFA
	FA8CE69D4FGCA8C327F08E7CFE80AE5B4AB1C972F5A7D4CFBDD60E135C23A2924047E5DC87FEEA2FFB7D24CC60A5938890AADC9707ACF9E6FC41D29DB5240FF088FB949DB219384DC6AFE7CE17E8DEB4664DCD4BA10A6E4561122B1DA751A8651480D4262AE5A206AECC08BB54DE5G1A967CB72D9B1C7BC3973E1B60D9EC33D553CDDCBCCB2B3175539C088C5F7DA2E47558FA3BA883071076CB5DAF401606C8B450AF02FACE9A975A652CF1E55DC46ED096A3D86D905E833052FFD4530F95B506E0BFD33ABEC1
	FF499D10045613BF5F10BF45B4D1897DDBBAEC7313D41BE5E62FF42AB67A427F324C3B184C657F5F31729E4F928F27837C9F552E4D6929FBDEC9BA58B63D4E26ABF0D8441E4DA1643656680C4682F269876FAA212666516157CE7371C19820993A25D10562758D2EDEDAFAAA2C6018412107F11B168C7F66977CAF1B2A7B7F570EE0746088439B532DF68D059C96C3375EE001D1088D7A64AF2A6CF6C32F59039334D8188B7CAF9B681873FFD93AD3CE3697A5886B4F140B22F5DC9CA6240C6BCE43BDA8793CB0FF
	BDDBDBA8911F64F71D58EF9434305F79823CBFG3BC7B4D47F0D6079B7F81BCD9A235121B176D719356477F82B879DBA576FD19FB0107C6DC8A3C63238CE7A3BE0FD12B47F8FD0CB8788EB7139258CBDGG8858GGD0CB818294G94G88G88G22F4B1B6EB7139258CBDGG8858GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC6BDGGGG
**end of data**/
}
}