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
	D0CB838494G88G88GC5FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15D3D8DDCD4D57AB8CEC5C5059BEDD45852E6C59B959535D4D4D43631C5C59BCBD6ECD9D132AD1595DBFEDBEB293D82A998962925EDD4D3D9E2920202A22271A6AF060AC00A0A4AB0B34040B0B34EDCE4B42C5FF34E394F19BBF76E4C406C62E77FFFBFBE5CB94FF94EF9DE4EF35E4FBDD7D87A7934F766E5AD9504F96F88523F8D7384A175B1C1A83BE9532F91D739B2B9CF107D7B8260836199E38EBC1B
	A12E666D643C82617512DC88F7C3B8A9A3B9EF997CDEA27C33E460BE786101CF8D44BD7B27528A6FFC7E318E79AC20616D7B7361799EC08F6048B3EAA561EF583FDC4287CAF804F9D90290D4C4E8827697CA388C204985DC83C0D7912337421389F0EAD525C457FDCE24B067FEDB56A2120FACCF04A4A96D9C6929915E1C973E0A703A124AC95294C3D88FC0144F9684DBA9BC2B5B067A3768FA0DE249911D41E4AA9936688D26127E91E3D5D5C3DD3BC12B930D96739E63B12DCD5F26B59BCC4B3FA5F97EFDFFAE
	4BC3C8057CA2A8EEEC107069065FA2C0703708FF50CC706970DBD81D1C37FCB56A36F9E535A4FB57CFB78BB3568E673C6FC1375255CC272E55CE5D82061F4C7A181643068AE26FD0080B85C88448GA8A8CE4EAB857804167F1672EF61D95BAB56DB0607AD66DA2B354E2437EDA73A426F2AAA0811F06D861D45269784D646576DAFD611479B30F437CFB89D33C964BAAC6F2876EB05193FBFBFEB010ACDE6C67E1455DC4CEC51D36C3489637D3AC75E81452E7E684AFB0FA0671D3368516CF7D4F8EB5EFCA4BBD4
	4AA7D246FBCEB1A96B7CA1D2568970FB9E15857C1162BF25784A6F1CF8661BA7D31FA1E1BD44E5FC073E51759E4A521AF329D0D0F6012A9D427E901CB5D642E488ADD14A720C0564BD3D0471DC49E5F90B62172FF16219AC8FD3BFA5E1AB44C515A667B17F2A59C06AEAFAD9F2DE4E5A643CC200F200CEGABC060DA7431534B570C53475A34B631EE44E8529BEC82CBDBD37191BC8D8E5126EDB15BC52DD9E7582D3599353DA603DD10E8DAAA088DFADD526D203E2F839ECD86334126A51595F2578E9BC4034D4E
	E876D4907B6AEC869A5BE99CB6G03E12B00799E2AD882CF1356AE7EE855EBC50313024577D710B2B9225D8DB9C286701B59E5381258AB82740F83C864F6F81666770341862DC5D5D52B45B2B4E26DA41A88056B081D97F51336A39568672EC59B9BB792DC8F44BB56E1BE9F2FDFAC4B2741C8DBA22D6DA8563145C39AF88683FDB4408C0039G1A72643CEA00EE0000F27427173B501F9A96DDA394BFF21E2ABF0502EF0715B3BF0AAED7562B2FA8BFA2F38A444D85D8DE111CD789508560G882B403EA1E8B0C0
	16AF4AB31176A0C0221FA16904FA0EA76DDC090E49F2350BAC247FF8FF82F23CA755CF4C6FDD01357B6F0CB3ED1E74E4657CD8957119D830538C00B995D8561D6BB52C6E54E96D862CADBBED6068863D336419BEFF3334CAB22D86788E00543BB200A6009600FE00D1GF09561A7009F810CGC3G43G27829C8250822C63F64A3612FC7357C37D866886888408821881108930E2C3F2DEAB0088908E908FB09BE0E9A5789C009EA098A086E0E6A57A42DF4C257346EF630900D27FF563CC07F6D20B7BD1850736
	42F03F421ECAFA396D15F1270ED3C6E63BA24BF8EC176D076D5E11F8907FA4BE3DF21C693C551F0FD5F0DF81FC2DA8E762731F82FC2EC2CF5A4E8F81162944FDB1CE99D91D5A360148E9B8E2B00B8D862B412C079FF6A9EE57862237545F74D9F81ABD94EF057E299EBA84BB479F58C064560A224D58BBA29ABA8C22E8B477BB638D1417FD24576A6C5A1017E9833135E5947AA7CEFFF883E95BF5963359C0ABB7CFA2455BE95FE2B09906C168FA4B08D9E4782315E48CBEE4B8AA75ED47AB678B24EFB20D10FE12
	0DEB233524BC2DB663B0B499242B95BAB712FEE7C577F2190DF2GC74667C5702C5E087D4468D758CF2CB8F625B07D10B5CBED8C94F67959D9619B25FEE223331FE0B219B72A4BAE52BECE425B192C2F951232D69DE8CBFE3DC4784382C253E1505AF4831DC72D063A91D134181BEC1691AB273921180C9FB0178EE0C2FF504187525CD6C8646023039D70D090B23A05E585A42F2E8603DDE7B3DAC9F4EB47F69677C49D7103C623D95F5E25A64757FB089C75A3B69B713A3AC60BEDD8EB6A309AF4C6D2BE0C6670
	A1E2EB3DC5B7C28C31D6403E21201A58AD896C19861039917B060C0DA49E4781D227D0EFB1C922C3DCCD95A95F61212532B2DADA454A1716910D1F4A6303A0BC4DADFE0B8963671270A6E57CAC9E2F0138CAD9BC639FB46C1DFF8C0FEF0634BDEE79FFEB4678B0080B1645B3BBBCD9C54A45EEB8BCE20061A199D07D406BC1DA55E2C55CEE29CE307859D524CE18E4E3B1CDB56159E845F968F409DF0A744C58A479B525938EA31D894201G91550CAE2DDACE974F69D6EC06FA3559931D4306F45DC0A32A504D5D
	CC680A6DC8978A61A800444DEC6C158E4F4FB85D6B1C2EE18B745DGE3DB5878ACEC8B23E3FC270BC897A945CF5F427866FFAF176F7491242B84FC9B0075FBC6976DC277439124CB14625324E7CE0DDC0F8523C82781FCA5C0D78D9BBB0AB5CC3E149AC277E7CE97A84507CB7431B5F23E418EEE67DAE60F6ADA46B728D6CE57F19469D2G1F81D0D047685A6A64F44B0EA1DD8860A36BBC51BD406992G1FAA23936A65F43F7C04F44D75ECEC5CD34F682C75F23BEC98C33AF04047G4C28E763E8C26FAC3737B8
	DD3A94AFB43866476CF773F124DB5E4072ABEDE07EE505E7D0832EFD3C360A7485F46DE3D0FBC46BA8310FC2FFD6D535CD364671ABBACFA174618D3816F16BA152C64D0070EC001C2D58EF7CF9912EE5048EC58A290B9E497634CED338D5EAB336BA7B0D19570DE633B6F05FE642B3G62C236E27B573FC9BE1F2A355BADBAA31D4AC9F57B23EAF93F42F01FD4AF04E71F51E697DB2063F7E0BB70C5B511174C75A43454E73ED572292ABDDC5BF8266B71C6392EF16F9F4FFA57032E91120E71B2DD631FFFBD1B4D
	372753B58152A764B4027F82080D342C07C89FCF6AB96327DB077CC20EDDAE741CF721C7FE310D12AFB4BA79C5FDD7A171B31810DFC1D3F2DEF393723B54C47811760251AD9CC63AG200965F4CD43040E54DBC67744B02E2BE5GCDD1B37AD6E447EA41FDDDCD4FE7B1E4E14D35E3E4796DB2A35F9E48AB2C99797E4ACC7812FA4168AC56FC295C1200A637A5B90F752B0FD23C51EC940DDA13711821E895132731150CC175EAE30CC3ADB26A5DD279DE3A24865B34EBAF952AFFB9182536AE97747EC0D6CD8B33
	73C00B535E620CA73302G182C2B8E232CB1C0B39B007515275864FD650AEDB02F8230EEC3DDC28E13F181133704BD5A8C36BE301AC1DFC4BC34C4BBAA165031EDC9AD0D5ECAFE2FC2FFBE25084C07FA7B4C350A342D1E93EF828B60DA50CBDC6EC010A9EE9B4A7D4D101A5CF336E159DCE243F439BFC01A9FB05DF623DABA478F186EC7BB16FDB06062F93A2996F93ACC40EBDA13739ADA51CECBCC84EFD79D6F255A095F0883C6BBAB57D7EB65659A3BA0D835DC872E884B6AEAE56599506AAC571ED9CFE60933
	30DCBF97D15EA420495D0E72CE1F2F14378662745BD15ED8AF72DEA5223C0C4773A25A52816923F99EEDA3C48F1351AE7261EB891D81548FE84D7D12FFC8AD18D44A2DF2E266A94CCFEE5F229168FCC7E5CC0C592D5A4D64099EC1F952C1964D8E54790DCEE55912B5222E9DA86F32A37F19AF6F5ECD725519ACF6838C5EA106D4D549CF45A3A8138978C5F01ECF9CA1FD06CD2A7132113C14E7201514BD4C3F7A0C7DA3B6C3DD638E12A94B33F4944BF6A66437F4A76A39F5C8D9362D90E75D09BC8F8CFBAE5B5B
	C6DD4B76CD4EA390524771BC340E775C4A966DF0D2AFDD47B441C408C7D2D3C86D56B51F497DBBD7FB2A2AFF87BF5F1FB5E7A7736B42B6D99F8CEDD686EF37EEF420DD75C0935E0672959E7D6F795E2E2FEB65E5505E4527E6A489EB29FCE5A5EF33460E224CB3C05E7CDDD8EE9BAA646556GF8ABC068AE54E956517F4CBFDF7A00246F03B9E13D4D88529B35E88CDF0DAD133B66984A9D89B225F239C3B62A493D3C9D5B4CB9BCDDB160746D18AE2CD4D99FC93A70F66C1F1FAD59C47CF440B2AA753FF5B64B28
	5DE0ABDA552A5ADF17A83A6BCD125C11BFA17FE848BB3D9D6D797E987ABA6377176CCF9576E9B008DA23496EECE0F899760CE13D50F4C09B5201BA452E136B945241F40A6DC01E91E6B28644B9B54B6B1B63A8DFA25065F4A26D14637279F789671D4A692B01D66474CDBF7FE77E913270DD4A2F5EE4548D95350036ECFCFD5A4F3807985509EB2567F6AB772E52BA1DEB5EF3E53F91581ECEC98F11BD7FC736273300632BA8FE058CAF0DBD69B81E042BA1EE5A0F388F71C58D1997646C06769D2092208D408A90
	329B4763D7D613F5D62F7BBCADE6A8E7B3F80C4B02986E37ECA6BA8F990ECAE15DE6E2A75A0A883896F2F6ADA96FA86019G108AB097E0799E1461054DBEE5586AD0150159791EA1E2674ABD635F13E069E650F4ED93C8475A66D57B647B58B99FEC4B4AD6E91B6918C34ABBE40F33EDE6B6292CA7B6C9827CEC001C2E643CC2006AAE34C97916E2DFB641BDB54998CC27A113FF36B8EA1A28ADD83A02E17F78FDB66C9F3FE7067D5B43FE454FF4FB4C7E69E7B07B275F08D5A9E7F717EB9E8EC51825BB45ADDD09
	A29C2EC8477C70D78DE40DA1866212G5281F276427C8F2081E0E0AF7A63C9DBB67B72C75AD0F6DA6A8157EC318C55AB5743392CEB8E7B2763E637F41FA942C1FBDD5331B67B7D8E222B4A7A88DB9000B83626EE6FD12E2913316FB448B386003573375AF2A5F99277E2BB7F1A1F7A4C337952A7E71FEBBAA64301A11421F09F4AF005DBDE631361C61FB2F42A4A90E8C499CCDC067DF6DFF5CCDD86234FF4917B544A7501D54B544B158CAD25B23D2BDFDE26F110CFBAC07EFE689377E319A6D9F0ED2CF2BF761D
	6FB412FAE105F0B0C08440F400B4G21BBB9EFC5B7560B99DB6AFC550BDA5CE86A34701FAA15035962EB51BF9B2E73192E3A5BB59D334FC9A5CA7BF4821D83A0389B6D730F79E81F08EEAC6BD75C780D4F5F5EF2CB27EC27E6F62B253BE944BFFE71EE69147C66F42B79F7690F28F37E81547958917FE488F2CB2714216100DA3A75237E79C2034FF45D8754FC41122B668BA250869DC0DF989BC23BCC63F6919C7E49791E4FF4A987547A2CE3CD24EEE6C0DC7EC1280BGB5GFA00G000803D8B73F791E4C0D46
	53E731113EFB25E4F25E66A667784A7DBE37F44A36B57E20DA3A130F7A47AF44AD1D125F4C03EA6D68C7F504CE259DED576A0D966CA4190FBCB2246CA367C01E4B8FA567959FC29FB1D7E05F50F088FD247118FFBAF53A25D36AB4F6C82D7E5E3B917DB4144B70261FB2647E642B7EA62A4A70D0A95AA10D4BF02DDBDE6313A14E278C05BDEA697A467C2B1B224FF465BDEA3E74F5D1213AAFF59A9CE22351E052B35B3C328E674B2DBDE81B62637E59264AAD1D52B6012A36F94ECFFEF3FC728BD765F7564FA4DD
	0CA24E9B30B306D7513D3B84C8B7930061CEBFC8F002966A1C966DD7CE57872AA15C89E065789A0AA7EB7401DA367FAB4D1B8FF57960CF72D043D7FB40F74A700C4753BEF9AC700077F48ECAFED60F35735F56F8BE9BD4D623FEB6E8C30D7A59204DB59FB0BC64A1BF590A71DBEB3C1F9D5ADE63796C5085351E657CED2D3A1C91356AF2DED56BDD4E9BEABD4979A9356D23D944A7ECA3E651B8EC20D9337AEBF010F3D950B3360F18D937C9F237AB579C846C0BEFEDA66B0311DA6CF3DB6A568A6EFD6ED63DD134
	58B013CE035636332F0F8F0649D2A44ADDD36F59C6CD756AB65ADE2FEE230EFA6FB65ADF6F5DC677FD74BECDEF379B6CF42050209535D25C5FC4F20E496D4C9759F391F01FA04A1AA751EC95AD75651CEFD803E79DAFEED0577172863287EF370C58F486259ED18D4BF91CF38C53A29A0609B80C66C61ADE545A7A8DEEF6082369D91C2774FFEC70E4AB26EB5F6105B2DD4B56E29FF8F58F764371DABCF7F0BF5D0F4C047052DE98B774E2FBF4275BDC14C0C32F743E84BCCD3D2E6D8E637D2D4D0577BAECFBFA07
	54728B10728805E724EAFECF3966D70E791DE1D44BAFCE4AA3891EE92A797D5A0EFE50A6DA5A568958D776EFD5BF27302DC3E73318CCE4D3DB40F1D7CB395BF9BF305FDC60172F43F1579B7D58272E50212D5368BEFCB704C500E09D5A7A910B377EBDC2672AC72C8E6B777B0D1EFD7723C6F55FADEC746C3BDFB57A765D62C64F3E3B2E51376FD6B712B57BDE235EE813CE5931F269966565D24EEDBDF2D8E9EB32E61088B6C865363E2584EDBD175B7A5D794456257A643CE6009EBD5ABAF33EB7DBBB742E368E
	5663DE45B7A3F2BFDC3B8E7D69A5D77CFA744F0A9F5578CC13720F513B7759AC3F6B0F38643796714323F27B10974A186ED764226E89FA54BD043E7BA29840BEG2586547DA455BA0850ECF015295B003AEFF0485A4E6D6BF9F987B6F9763F331A547D6F1CA64F7E9756645B7FAEE9726CFF11CD3E7D6F7AA66FED672B8E977BEFC07CADC7DD5A218A744FBB5D7C133485D630DD20817DF31189679E6186AC23E365743C9704E7836474E19959ABC89995763916C52994EE687364B7470F3948DD09FE1838D12D4C
	3B24FCCC9E73AB7B49A53F0DFC1F26D4359D17728905E7E41FDA3B7BD7177C5615A27E22B139DF2F977838FE1DB7DF0D73A8376E388B1FB244FF7A335C0749999EB6F63F146E95A6C1FE29FD686FF7F513335FA66DD14B08A888BD7D04AE051F711E83F47900AB87E86EE76B522C8F39FE1434EDFDB66DB038949DC8D58F10343FBD08E9A301BE8EE0A6C0A6C061G721CDACE642BEB5C314D750CF65BC2F2B637DEEB5337D2F9D83454E66EA432CBAB67E54C9F679A54564D8301CF58G769BF7B4AF125781E91C
	446C9E5D2334FB7400535671836EF6E77B21DB687924148165BE69B60A4F98D06E13A69D46FD52DCA30CA50CB817FA142E69A7C1B81563B659F1FEB51B636E9FA4386AC118C38DA26E68902655F35C63C318564AF12F8FE05A699CB74D08E9E7F05C3CC1CC1B4AF11798B0ED6990566F134A799A4B90566F057DC43FB1880782C48DE17DFE3B5F1BDF478FF932EF97FD37B0E50859B11B63FBA9BEDD06E776FD011F791B8BF15DA614FF3685F51A4EF10B8E93DC9A0405E154691A43285352E154691E6F685C9542
	FA0031E154690E92EFBA058EFB5269975AC7C78EABF5BA0362E30715BAFDB09FF5CA0038E2B34A3FE4816A9442F16539F21D982E152EA347GCDBC273B7EC7CC1BE4C6FD22F23D692331F852A7071EBBAD37A875F90F629BACCAFD4E9FC4FDBAA1AE5602B205F11FAD34A26E9613FB997D0317D10D954B688F747D968704C3GA22D2853E213DC2769001FE1F56A11EE7524534B341F4A34B2595F6578CC0AAFBC6C44B31D82A81E042BA1EE5AE1147FCC3A8E19E38359ED08FB18620AA1DC4DF1156BD177E60E1B
	C5578F43A09C49F14DD5C89743F1AF16E2FEB9F6DE66E5C8D740F1C154B7FAA0ACF25C3B65C897E447B679161F49FC9A0693E569F63A06484F152458712C10F0G4F6A960BD0AFC46C8B4AFADC565E48C9162A2AA61BD1DF276DE7ADB9AB43668339D27AF0914B70884DF38604E783640C60F9E547B7F8DEB96AF7178AB372544F0227F69E4FAA9C114E020FB84FBA048EBF1945F8469FC41EE3909FB3C2F02F9E141F83A2BA8EE73335B4963F6210BC1E0C212E1CA70F5F6D92CF64FD48A5FD10CBBC315132AC96
	4FF4FB60EB54AD7301E841F4E320477B88B2D127A352F90E342973427EF5ACDB5AAB6A43B9EE57913467FC5A5704C0B892E07A91AC73FBFBF0ADAF05637E510BFE10F9846BCE836D43DAC721FC8182C6915FC671641D15EA69DD9306FF74F017C46F298DD102DEC69B60A31D3467C7F9CC647D44F794F9F8231566B6B6FA5E8C8671E123645E0A5CB6E21A828731D33B3DC1221D29A26FBC8932A548G9A829FCA789C87EBB766F1DE4F8C92198CE632202597D8FD1BBB08FE5A8574CC06553B1CB27CFD88E5B0B9
	5447035EE48810494078FDEEC4FE411C5F0EF6A73F650348EF1A8A3FF7FC700BF163F73F8179CDF7206F1CD6AEEF27E7GBEDD46ABD305AFEB0FFF26630D02230C47BB9CFF86452F1061D93BD5500FE772AAA1AE7CA87A7F0783687F42B11469200578C19B8F8F50F09477778C332FF5E5841247F87D55555F73AC6365614FBB3BEC1C1EED45313389648CG1886B09DA075987689AB8E53792B7CACB2AF4F4D0731BC051F50B6B7381CC72986FC5BCFCE9B68FF72D41ED5A5349F7F0915DB9E47B7D1FC288C4F4A
	B3110E4DC8B89A62160EE1F9BED202656998C319CE1DCF4AAF01074F26E172EE9B8BAF1BBFDEDB2B814957F7F9167EC7BCFC15B1AB4FEF97E0F92603CC1944A647212D8668BE0E65297B51350DBB2D03180B5577049F313E0F9DD7EFDF649082B484BE11FCA06238333E333238BB974BA27AB85AFAA1FDA7C9789943CBE9183C1747426103CABB91DAFF6D26DE96B73A7170278EF9AF0B6B78DE358664AF8768849081C2396E19AEF5A5867089B2DDE72A684D6A44FA7ADEF4464F4A362F1662F3FED136FD891558
	76AD073831DF302E2C22E36CD4884F6138AF68BBF04B83E03E9140670A1B319C5B82D076B965E4FCB38D4271G69G39A7A567951D0471D9742CBCF9CF123C83C95E9FA46FB8B61C64EC77318CC89A390EA4BF255E539438F4C5B8D7118763F1B167712EA2BF35BBB714BE209CB3642950AF91AE21E3FA56BE9A742CDD0C2D40F1337EA42CF7AFD438563BCAF2BD04D46FF62E47FAA71E64F9ECA15DB5A584818D012F300CCFD20EAD5E5C684AEB5AEEA72FC3D548AB5283AF642364C56262E43CD85EF76FF1662D
	AB433C93D4724ED664BD93E888FCA861B35C7438B857FD0CF4D3AE76B919C04F78FED90EFC8BCED657C94EFB45492EFCABCFD672FD6EE7723BCC40317A34D179196B4A05A72B5EE5C020C1625FA5BDB3BF3CD03A73E67E817F4EGAF75B35D768927E3EDC873C1BAF710590461F7D1FC388CAF3DAB436703119017FF8A1EE1FC290B34FFD6888783C4G44814C8418FB8A2E4BB53610FB533C1E116A34D8DB8DC78CA659962AF43E6960C475E3E5F957821CF7455CFA1590FBDA0347F7E07383D99996CB4F22D9D3
	6711A753AFE09F512F9B629CG61GB1G89G2901285F2FDA24F374E48B3AD694353A817AE6B15F50B69AC61DE83200AA6D2574A9F51B9368746B5CD3BD758B47683C2F70D4651A64E994DFFE2AF2CDB21F2FC936C2DCB4GDB7B4D60E711171E4656FE0B61D9F39AFB5712553977F4D8673ACE73DD67CCC093F01AF3EE90FA1A324E8DAEA365D027558D6149E8067FF8000CBF9B0DE623FD40F9E61A45E5FDCF526C00F68EE3D899BF34DFDEDF8BAEB4E5A1AF37FEC512BD6AB4673A811BD71FE247F9F5AA44FD
	CE79016D8567BF567E593B856EDB4F6423EF95DEFD195070D8C356D79E78AEBF1D719BB85D49B76530A6DBFA6FE908706D531A6CE42C4D647952444FBE1E0EF335A78F885C966B0D6EE9DA86B1CD8ACF731BC34EB44F9864E9D8DFFF91DD97AA88CA4EDB91047D7FDEBAEFEB06F0B74725746118A0A0887B74C03A879C8161B80EBB1B62483B361D7C7C6EC77D5E464218FEA2F55B3D7DDB6315877A263FBCD89DA89EE870C223DC93C8811DE68731BAC0424299EC1DAB0F17475B9F12F478F6391D9DA5174AF215
	957B3D7CB3F08DEB0FD5390675F7C13E86F506D53906B54FA57EB62BF28D2B56E50D6BA52BF28D8B53B3F9BB8F137B3264EFD73A3CD7C99B72120E169DCD2DDB8FF5ECED5DDA5F5932F30764E7775139E3836852F5867AD4FBB92ECF0DF1DCE2857A5934B37819329232C714816165E7C23B8450F9A63FCB144611F72549BB3583008F62F1495FF97289128F7EA6794A63EAE4F11DE7FA76CB4663C50FBC647E66ED2C299CE73A7B5D6C92BE06D311C76EF3846200A669CC275F251D29ECFB1F78B7E95BDC6EB312
	1C0F79DE5B82743D0CB35177FEDE605D772297F8773DE797F8773D25391EFCCFBAEF424F48971D652DFD3E23C760F6FBBC976D56F3166F792D88B4C1E7B96D96FE1652EEA985CB65F6E39785D143B1999F99C23BC51E05F64BB6F937DB05493B5D7ACD5E6DB6556449EE525E118D6BDD6AD9D847BE5A0075AEA798F1A115D86F0A02317ED453FD09D68877F0DC934509416CBDDA067BD3A5298304C64D07DBBD60478B0C475FA88FCFED78FFBA1FE765DBE3A27B623D969BBDF44146C6FBCCB8B68A83BD2281E682
	E4C87A33F415556E7B69BB2BB1DD6E94B626524033FC8ACB477C73664D681F2DD3FC0F29FA00C61C62744F20A9CA7FB49FA0F16C0EB1F2845A65C83DC433320D487503213FFF3B855A6D9113C1FE721E517C3EE831148F4945F91A9A7330FC3CD84A033E585DF7148F54D8FCE445FBD2BC39F5EDDB2B3617EC7833773153DC6AF928E40F58A9E84B07583C59B6E2E0F5AAE0BD567B44A95E6A7DB3641AC5292D882942B4B93F42F1D00E4E999FDA0671553F721667CA3D406D76E02E7BF82B239C7391D57349781E
	65330DA41166917F20E32BA820CF78954E9BF23B483DB73B35036EA7D88159E633D88DB671A84352C6C72A93BB8E8A3CFE6C117DBE7940FF3276B83E7A781BC965416C72FA176773D7EFF5291F3F4A5E2BFEBEB7EF2F7733549F6C75FC16FA79DE79BD0C2C9CBF6CA7FE1C8EE518F3B63419G55GEDE7E3197EEE6F17821D8B2A4C83376A7B4979AFF2DB31087C4F5867D95710FD3E6FB81E3A0F1C8B6A35C0BD9CA679A37E62FD243CC40B15A225F35E7B080DEC467E81111162B96FFD44B7CD06BE0A1676177B
	302FB01D0DFD45BD7D7CBC42595887845039E5AA040590E86BC2909FCC71643D9DF22E311A63979FD6739F12EEBC7E01FC3C7B68EAD59E3E768665FD0237B9831BD3AF5762FAC88F48939602E797BE6A4531C0D2886E53B674925A5CF340BEG1567A06D77BAECC75A4ED1EF0FBC37D13387846E13D3495532D2D9D66DA7F7272A36A960015BE08C3F33F790466D1D5AFEEE2FA017F7E303C01670F33C1787639BCF778D2301B691A083A0775760ABG65G4DG5DGD600A000E9GB1G89GB381E6838867A667
	ADBD976BCEEC77AACF3283D2BAE4C0AB10CDEBA4D5C812FF254B9910A2482F7A5C716C556C981427EB03B4FA9F69D81F90D36EBEDEF8001F1DF61C4B468BA1700CB81779BFCBE7B5380F97CE67675A624EE5630599A40D14CE88A5B42F74AD77A953D90B080F49CEF52FE378475FA36D8B9F3BCA2770547AAC4F7D58B4F2ECCF32D9095B9DG6329CF647C3C3B4DDE65E7CC1BC3194DFA60A90632B584C6739337EBF0A833CF84BCE3A59AB61E1AD501632924D05F7324B420199B6A9CCF651F279CCF252D7DD8F0
	1BA7391C17E7F42B6B7EAD384F8B64040CAE3400580E0D074AA45C6DDB1D38355298E90EC9B9C6A2BAAEBF8F6F34F9D5B53E78BCECCFBEDD036DC94DF96A63924FE5FC7730330CFFDC605FDA745BAEE7439DA0C370F95E6B93137B5DBC3E66B5E1391FB3BB65FE5C6BB9F0B5E0BED6EBC39FCBB90F79E186BCF34EE76F1DB21A6FB7A0CD617952B9D9F8B6CBB42C1EDED1693B1ECEE92565CE2BE1993E9B2B1B4FF1F87E7A68C635F7E0FB001FF8BE1E517A055F4F988C387873710C56C187311773BEC57AED12CE
	23E8B2686BE8852CB3E84D788EABDDEFA463024C73313FB2EF226B4897A467358268AFC0FE235558670D81AE6682743BBB2B314F1BFD8166719225DD9186EDB1C0E7985226EF4632B6050D372C314F4BA3D7EACAB6FB2D4F43FD2434C6B1DE1F6421EE91E1B87EF837DA6EA3B3811F91B61E3E623CAAF93A1C29204F5471241BDAA6CFD78CE9EA46152E4B653CCCA7249970110E75211986721B0FF75934814748FD247D6077DD52ABD1BB00505C4F62BF6D77BC96DD516FFBAC7A9D9D9F9AE08CC966FF8C37361F
	7836252F4FEE60E35E8D7D859C57E2EF33580DC492D64FC6F8DFB7867A06824481A4CDE56F9F30767E49FEEC6F53D5EC225CC74D841A028B1D6D7D0A8B156D7D718F08DD58FD61F863187CBC7A45BB177078F603FD44A45A254A4A629F9BA5FCA146E2BA423BD8299E7FEBD06DBD0AD2102161C2744999DA7F5A5BE77DCC77C92FFFFB0631F49C5BFD21F24F70AE0A9F38D039E7382D975B8B9162D2383E8DBA1733C33F017953EF3C7BB61BFFDCCC77EA743FE1FC98AE026246A49C732F14C14FFE7C7420EFBFCE
	9FD41FD33DBC68BE27FAEDD0FDCE754620FCCE45F0EF8F921F6C07F1EC97274B9EC25CDE0E4B9D4A13F0FB0CFAF100635F9BDAA8611B8D04A162178E9139C6A92ECB42FDAC436D15F01F8D113A88AA8FD8EC7C5DB8FAAE274FE8B23170CACAE3B4C3556476A826B29A2DDA195CE59C371717459613FB79541B646543F006C1F73A41C1B99D731F574A31FF8B83FC52EF308F59D4016DFAEEB876B73F53935A9A88F781D843314F3A4E00BE98843869617C0CB9BF23B8176771F59FFD376EA2685781FAAE42734989
	8764E7F8B52BF72A1E611DFB60D81663A266476197B9777B628FEB247B147FEDC3FD12A0BE7737A8CB2F8D752979AD4AF271BAFACE8E42A1G113FC5FDAEABC7FD2683EEAE4F6345F2542774E24C63638ADAAFA19CG90F6B152AE65EBEB5197CFF43CF578B08931FCD68FE3BE69BC6FD59C271906757D4BB5727A5E85F853347154778FF73B7B493FF74B7D0465DF10A74FBFG728E7511BF33251117C7A45027CEC39D2E37218E4BAFC1DB6A56935AB6888FG84DC0265513F814BA38CF0C917E09E57ED407248
	3D9473F8125256C0388B40FAA96737915FF7886234A9749D0A6990CE814838141FF1B479EA3BF3AFD3BBA3F52E4FF44ABBCF24776CE83A95D21E1570EC3D4C33EDFBA00E75894F52B124F819322FF8116283AED3769562A634E3B044E5DC067A9E2F762F0FBB4B4FF47193CE477452EEA672F7C6A8759D24781E88253E2F713134896212A2D05F17B64B7917DF8EED51656399AFEADC6AC0371446AABD8361992648075527AF2B5D6B584AEA77B6BB2A4A1D6E7AAAF73A68B2F73A9B4B5C69AC656EF4E239FB9FF0
	D19F5AA98A64CF3B9C67B8391F385C37475EC3F13ECEA8E09B7557BE2C07053F433A35041E916F043089A070F7D88F5F9B40FA9F7E3B09360BD349123244F3833FBBEF8667392819704C3FA2B9EF85C075957C9D33DE0117613D325FED3D133F16EF1CD49E2CCC776B5C4B39C7A7AFE7D61E5AEF08FD2439899D76B0FC21E311930F439EC97EB74A7DEB8B97D47857965650F41DD7C8E7FA60B9F605673A99F28536057F26E31008AB14ED43D7949FFD0532ED0852234F47C11CA692FD65C603FFED5A5DFE264B1C
	F0BA2657D4DAE783A3157ADECA71A111CAFDD773BA9E8EF14215F83650AE1239139E4281G91GF1GC9G69D7621A71F6EB1160F94D18B5864EA5E3292DD43D8344375F1478CC276EB77DB4DD6615AC4F022B208D384A335FD4DE05FE33040E75DA2FD25A3110623B2ED25A7172F5E8C7BD44A5DC05FEF3254FFA22DE7EB77B19AEE3426918DED368F83578EA253E67D3FC6555CAFDBF2FC0FD9BA0AE72EA54776375F27E79D1D0072246756E066A7D3D65D25AE615BCA43FD0CD57AD51DBD55231F6F0639A7736
	F14B9A773EF2E51EBBDDF11E7BB8586227DF9F77532FEF226982FD5896A1AC8A7D7AB1BA160E0AD216F3AA4547C6A94B59389E4BB99E620A2E41F29E5E601FFF7E64E73A188927134ED45377B2022FD16A3B0F62432ED16ABB0B4FA9A2A0AE77DA5477A617BBAFCC008F3856E97B50EB5D4B0171E9256FE9C7DE2BFC0FE48F454749700C7FD303789EC9924415DF171C476E231A3E047545A62359D04F3E204B7DF1961D670781ED74F538DF9C3E096062A11C42F19DF47DB79342CB23793D85F49CD984616AE854
	B51DDC2BA9597D6F325F47B6CD7E38685C934023FF33D3A72B6C779B55134FFB7166499F7765ECF1EF3372363837ED9BE8BB568C655E457D21E0B33E6FEE6238437C5DA487473D3A8E5FF7CF60FEF3195B197A715964FABF533DB061F42C6E1DCE67A0B32315EDC2884527C7AB5B040F7B31CD18C3ECF5BD6A3BE4C04EBF8E70C9570F272F2BF659ABCB03B4F3E56972EF7054161CC467B11A9B146F9ECC2178CA991E493D24995FBDE80338189B306C7650B53C653FCF4EAB7DBD624E20EBBB8D906E62381AAE6A
	B3908E653892BD41C5C2B86E77E8878DBDEB45FE2F127D3EC1BF7975690193406362BE27CE11325FE5FD134FFB5B2472E06378B151731A7DA9226FB57BE0D1FD4D7E57227B1A7D85227A1A7DC5227BB9281F69DAE08278D9AA773F29343F1A83615C98443551B513E288B7F05CCA5ABEF5C15894434726654E327BD2767B6AF513DF0E71A700473995CE1D42E53F3F2E18FC5EDBA61587AB5352756E7DDA45FA77FE2D092E0313FB179C5C9FA207318F8B64382D1CEE86477D0E5325F25C03548FCBEFCC4EEB3E11
	5F8BC35B50EE88DBEFC47FFAF23D53664F49FEDB56CF3E7D03CEG0FBD15CE1DFAE43F1F5FB0793C33AAA75F3F9E24FB81E3D01EA13C1CEF5392DC8C0493B82ECA07BEB21B17FDBBDDF72C3EA9B92F67A644316FF884C1B812635278FB7104CEA9C7358AEE6236FA0B73984FBB589385697DE793E947C5F2302551C24F8332F70797537A13GFA4C38894F5E7DFE137B79337BB7615EFC4E4DE0D700E6006E1B197E527B0A1B97A20FFA0B096C1BB29EA7537128883481B7A30FC51B5EC1DA814F2C649F91F8DEBB
	7268DEAB084A725053316FB4C89FF5B31ED7DFB3CA6859B907A07E1E404C1B554FB9AC901C679C70BDEBFCDFBC63E6653DAC1F70BD424C1B51972610E320D2191DAF7B7D3957776A7EBB7529F2D2F930FA726D00FBFB3DE6403D3D6E207524A0B6B9EFC5AC56315DF47E5A8C61EE0EFB22896BDDC0AC5A70F4BA460D00F082C0AA471FCB71F3A13C7C96185B5E4247G4DF23D893DB7BDA2BC44A7F840B39EEBDDF878BA7B6C29BEFAFB6FE609E02ED02FFF2751FE298774B55D02F5E35AEE777AF773EE2CFF6137
	307A97834F04DB6475EFCF17FA7D3350F9C1AA504E66BC366A5D6B5F4F64134DD2DE675954653D06762D7937C2B95D0AF9C5D7394BFBEF95BF2FFAAB1337871E622DF2F97B2B55657D050E8F0301B60C7358324EDD5ECB0E8A7CF77CB1721B3585F78EE3DB90FB2B6FBB2192EFF53D23A975D665FD8E2F7D644C7BF61E77EC153C67A972D65E7FA45C66F6FE56031DB3191D01FE79EDE8032C756EB6786838334C52B63A1743BF795948665B24F34370948984E33E49DA7733564FEB79D9625B243344700C1552B1
	DB6C626F3BA75D667BECD99A504C3DCDF616B8CEE90B071711398BBBBB76235DE0F37D5E89238948AD64B48D7CAB1AF2CAF67E77EC176F49126FFB92391747617960B7CDEA7145F15837FC1A6534EB4826AF60E9B60CEA7887A9C9D995FDD98B93AB5317451AB278DFCE4E6333F97C2B1B702CC48D64670843FD58735B797D624E33262DC633C1F6529477C52BE89DA0F9C447613E688DF49FBC9D42426D204B6DA86B7FE5BBE5FD390A68456524752A245724558DB15AC767BBE9F3B60FD7AF690EEDFE9E351473
	FEE301B33F356572773F24B141103BCF1E446F819A381D79E4GBCC3EFE76F3E30F4431B5CFD724CC6CC97F5BB7349F8F826C869583962D71BFC1FABEE79127819F48631992636A6B23D45F77098CDEC3E629D01B20697C69578354C1F6EE9D61EB7A7B2E500EC6CECE3DD3D72DBCEA4BE678EA847BB1066CE37EF23911AD208EF38836D1D3253E96F633BA76617CF6FC63F6C067CC26F403D7C15FB5057926F60E72A76905A1CBB216D86A83F9379EF5965647F0D5E3B2FDD5861241DD2B5B1D9DF29C2D9DB01F7
	601DA86B06CDA8EB5C1DA86BF5343E4D01F07EF49813CCC7D9059F1D7CBFDF67C9D616EF86BF0BD5BD5D5BFB0ADF51FB03D81A17F99A1173EC24F77F305F5F6DF372FF3AE2E27AFF45750F023C5326237EE51B717CDB61DD28FFAEFD3F2A9342A600403B703C55877CCCE9B860E65C05F96C656FDD657F8173B81D52B6C0389BC07C83521E4C5F3B8A7E43C44F20376DF75A6A9EFE4EA31E67DD466D1779879C6F4D5D645F3E5CFC1FE7115477653E771A2E7CEECFEB3B5F5033CA4DF7AB5724573033CDF7AB5724
	E37999269E080B3F1B1FE9F1B943D4948FF5BEFEBC67712E9CC27FE8837A20F834695F391D2747E359DEC6FDE9AE048B7E181CD774C7740F9B9AD1269A40B97E08F93C59087E917DC74C23280976BD90966E813AFB10762DE65EA7F05C31FA2C238377E07A1BEBC87AE9900E85C862347F605F48C8673471743B731AFB1373AA815A6EC5DB7DF537FF3E711A4FF46A3ED13C47DB3A01FBBD794612AEA27F583DEEE791A8BE68DE373388FB309CC2A1EEAE57777F2A7C53B75BE79DD257375AEB3A6E84CF7AFEC5FD
	5B1A20543718624792147A5E484FAF86C15C6C842E2F4B5900367B127374770DE7FF66B517F48EC893BC2EF42F78ECB3546D7B161FED4DFADA5727C9324540B3410B1CB36F43B3GE334FD4F38CFE95FD3A9BE73BE25FD5F636F5B665E8FFD427D7CCE4F893F4BA02DC77B196E201F6FCE7C1376C5C977AB754D5151FD407B157ACE617DDCBA4435BE40579ADC5E1D0886FC7C8363710B8768FEDF0A02F636949692BD1DEDD92FFA36E529C4DF2412CEDABB21E37DEA086FCC645F5E136DF3D748FE5FF182763C9F
	1BD49E521D341B5D571A9E5F6CFECE67BA1F6FA92A57515BFD26D32F232F53F483D2D90D2914199CC292310EAE23EDDAC42252E7B5949F1D287459AB2B7839C50853BC08659E5B68DFDD3B474FF47F1AF0BA26D7B89D8F84BE285477F20A8FF9D029EFC9939F0BC21C70A7FEA7EA0B9AFF7D1F185D9DFF72D457FE2977A6F730CABAA65F6ADABA8E7913727B8C95949FAD43B339EF2F41F156F408DB71903F27566BB7BC029E72443F124E9F439FD2722F23782807147C6FBF027C63A0CE1304EB2B75F44E968861
	70A43ED6CA2AAD6E174AFEDF3D67846C17CEAA8FE95D194E7D22C15F842E732AEA271EEBE53FEFBB816712E6CCAA8F26F33E593D1D7C406C3EA63FD0056EFD953A6D340D1D89761B43FD6961F6024B7DB3047F4C3F5551C9F0B5906E6238907A2D849304837E0C76BFD96B3479D9325FCB3513EF7F6FCEG0FF9BA27CE8BE53F4FBD816F9CC41D00F78EFE2B724E41E5BAF73FF9164EAB03215C27F1FFD8CD4FB146C0B85E69A3A65C73C962386D43C827C9C65C35FC5F3EB8192F79D55335708123EE48EC207792
	313570D32BF10EB94653336F2985C3B80263C4FABFDBAC0493B9EEB5BDFF1281611C0751E7BF151D117CC6767B3A2A49AF530493406352A627CED74BFE17B7CDBE6F6D134A03156947ADF47F914AD373B016734FAD5C1FB86E7CFA440D7132AF2BA57B4A09BC3C090653F9786E1A094ACF721A180DE6CE1847C44EGC8F79A72EFF8644FC05D360C52EFBC70F0B38D0F71F052117FF65991DE2E3ABF7FDF67112D783DC45837C73D9D316D41F6E45A8CECF386B729531E4C571923B92DFE337ADE5D986DD32783DD
	A227FD215DFD5FE3DE872E6FE74EE0FB22CB9FC94EABFAC43EA7FAC9277A1E68AD2CBF865A56C7104795BD6E7B016C6EE5967F86ED33DD6F61AFB021DEA61ECF1D4975EE7C73699D4AEC0F6F8A937F7E45A33E77BBC30006G6EF7C6BC225CCB3D6520B36FBF723D54E8153C157B344A3C639FD16E9F06D7135FEBF9F80507B3AF555486C910FEA63741D9156E65F5A93F23A67FD1D6DEAB60D979283C3C6A2A5C4BE0266CBE695296D75B7E2B47297FF9AD28FF5B231E3FBB007B27FA20A1007ABB9EF56A4F723B
	269E738B74101FFCBFB68CE8229EF56E47CEFFD4E94F350B48B9D33CFB39DEEBBE223517C9F7F518EA65FBD5090F32F39BB90F4173B15C9F7E165F85DD7A186F3BBB9B00266BB127BC26475C6E161DCF76F05A3B484577C61D512AB56365A252BDC0234A7BC348B9928764937C98167399FBDCFDE46AA692AE63E537DF67AC3BFB5BA736E7332A8367EA9140EF66E33846FD38836B5C5294DC63FE1E5236C2D88FB016027BC7F7496E538C6A7134E74562D71ADC6F81CD6177210726F83B33B30D1E79E6E9DE67FB
	B009DC06857DCEDA1D51A94FC065446CF1FFA55AA367AFD096FFC1FBAC5E0876907F0276B813FE27A58A4271GB3B96DBDADA85F1C3FF853692AC127CECF55E31A6247A77AEE7203C38277C97DDE220BF33F7E084EE0B2158C53AB3C695E3DEB55607CA3EA113F7EF154AF1F3E13908E6198004447D13F7D7CBD6A34894B1AE1F26AFC7DA86E7B94BE01BC9BBB69DE9404CDG01CFE0BF3F26433FB5354DBE53292F299D711AAE7C89CFEBA587287C51CFA857187AA8BE6E89659A53AB9D384614GF145B379B9DF
	177B9CC2G9FB1F3BC6B407FF4D94F0D05B40963CA17D4691FFD1F7219CE5D3E4BD43F6F0C102AA2B333635B344E4D1929346F823AB6A9BC2934EF48C634EFBE44D91FC47BBE603206B7837069CF0E47CE33DC5670B2A1CD41D363C9F7E52DBC5D8AC51ACA15BC18DE81348E36BE255417FD9F2F6BA9253E0B79FBC4FA08CBF88A756DF1F90F287869643C1A2747A377CD2372F41D10E6C016EECCA58FA65F2D545FC31ED64AFDB74507BF2D143B3593650E04387CD41C8FCD217D109542C129280B2ED3607219E4
	3F7F2FE372670A6FF5CE7EDCF10E4ABDCBEF285C3334D8E57DEC194A7AD9896D3B4241FEB15C06B9D5CE3BAD127DBEFF63645B703AC965A12D7D34385B66EFAD6E36F9335E1D6EDF756EF48F507EB1816CB7137B66BA7E6D9461997EBDB459996B33E53F171F007356E5A7603C75284AF96BB11573568F0E223DD6BC4357E7F7517356906E66383C8EFE5E1A5BF03A2CCE5FA77B5DBE29F50F71981AD49EECCC9A5121BEFF0F6B40F1D544B37C4C73C6F55A839B11B60653BEDB6BBE9FFC034FF9621F113EF3G4F
	8CG32FE443EF1380D5F49187BACEB4F8B25E77533F8BF6A6BFC1E527DAC4BC704E7904098DFBF38E4177B794758DD18EE5A336C7CE3ACBC9325F40C3F062FB12481FEAE4072D9B0E785681A05F9BF5C61BA8792B7E218759FDF5461385D81E922E761D855D807E34C74D9B8468C207732E8520087C0DB9A52DA79B718875270BB9F11FD5E3F637138CB3C7CBB9DCC2E4F9CA857B448B305736AF320DC85FFC5399EEB236733A05C836078AB1EF9CA5D05FDE38860927E0AF994356319A761B94CE3F7BBFDCF9742
	EDG834FA1EDA3772D00677837DFBA145FB0B96422DBF407F75D1FF509A7FEFAC1364B37DFBABD5906553515F57E0DB9ABFC26D39FF31AE93A5867D81E094FF99F7F24BE077B64F3283F4CF9CEB916F90B62053FA947B22771BB8973A14E7AB7EC57C6DC5EA51F8178743F0DE78C3642611F1D567ACC27EE27A11AAED3122DA05D3B0CAB5251CEBFD0BF2DCCD75A2913629B5215F6FA328D6D5486F1B169E82767F679F7AEE00E1F693EEB776FBC417D342EADFFDE296F43943F62F9253E5D6D28EFA94405BE0F7A
	7E60B2571C8B787C9746F3FF622934ED53GED658B581F6E227BE02D906E6138DCFA97E6A00443DEC01EEF4A5E214F163DEF9EEC187CBE7672C96541F48E6E43F14AB95908BB278F47A9D7F23A13E9BB9C81F60963760A5CC157DAA11C46F1075A68FE730B90FE91ED38EB1753EE7BE43FE735CD3E8DB3CFG0F24F627CEFF117D6EED1FFC5EBFCDAA8FD6263D9D6EBE525F01BEF2014057686B545764E3E87DAB82FFA8FF9147A983BA77B158AFBA9C5F68DFE4EB72E3708CF9D13EA67F3941FDCDFE09D96071D3
	9D6AB2DCCF7DB792720A61B25C3943DD06C7F770B116A4C3A6BC8BE64BE5B85266AE43FA72899E69772F8FBB65592A1B583A6A95BA9C8795814F1E59B8AEF9C5076B2AE133F18C53CF6F47CB02F0BA0070922E713EA6BA79076F18987F37F7A07F6510DF5BCBFCDC349379873D047C6F58C9E827C3B885A063A52C6F57687C6B07EF7319CE3D9F1E63B5DD6E4B1E56DE1E2076ABFCD959AF25D1FC514B4AFEE9185FC7D88EF1E1AF73B5021D7E693B58E7BAF5FDFF701AAE53233EEB69B8226095253E1BA83E7095
	253E71BBD15FA2088BFE855B78F7693DB399904E4DC09BBCC377B6586F97E43F6DA760CE1860C965A13501547F8BC15FF22EF3E51BD34FDA596F7BF7CE3E4E33A61587EBBB3E542B4F292B74B8C7EA4E40367468CEF55AF3F6A2ED97275D25F71D23C66E4470B29AEEB274AE9204281704F7C4A107FCA2D9946633AFA2C318FC7E98427564769B13A8ECA0DF675394EDB50B861B063DBA2AA128E24D37C5F6D1EBDB2599560A3A8103DD23B59F55688634B62D8EC8D7094297BFECEDEA5260D7GB5EBB49DFBDABA
	BA5A5AF7F6EA5AEC9651C03E2C34D07A40DFC9C9095DA63C4F1B5096FAAFFC6A11F5D55933AE4B4E92F2DA04796C4E1D96A107DC1C53A23CC36F55E99132C50B55C1BE5811A3877BE161E7DA12130D9546C13BD40293AB590431D0E4EBCBB49A72DDC6CDAD94A66E50E96A21C35302FF187B2BC4E1D97D084DE6B00BD51A9D9633C1438A50E8500B421AB2C84D3E566BCC5BE7338CEB48A7CCB516BE749F695B9622709D61D7BFE031588D9A2DE63741E6A7C900D09CB0C8B99974BCAFD1A86059B698C0A293E431
	04232464C5F6703D77B916318467DA6452BF7F71120A67921F5AED4F966637889F132BB2BA4857B8CC46E386FD03D654CA19B58AF9D68BB8EA876041736C74710EDDBCEAB2348ACB7B5A8D07C78CF61031963F038292FD58CAB82D99910D26B54DDA28D3647E28CD2DC20E19FC3EC9D84C22CDDAF37F9A76D114CD54FD675576D0E7A24D43BC10F789151764D242F3D9F25B7BDFFF31ADADB6DB48EA94961AACDAFDA328E9313588F962004DE09F3018748E6B77B5B423DC3A7279AEADEF1B7089D71FA8DBC22AB0
	97FA48F1D1702274C50DD902603089EBA8A5B9F5D142A97475D831DBG3440F2850FBB69B3711B871B16F5E78973A4A509F7E99CD6E8C57A3443C653D17851G363574D33FDB81C1763D88DE6509B1CA9AC19F7171176E3B74096A0C5910D923704E9172D599902940C935E3E43857E0BBAD6A3CCF5F8DCDBD0BC8ECA56AE24EF922CDEB3677D9EC431039C621B3C6F151CB4E3D2F743CAB5F5CC7C5F7A695054FD9A1C152B57D864B701A5A3EBE235940B30092036CBF6559931A92854DE247B99FEEFB64DF3D60
	E18DC276F0E9A97DDBC67F2EA5FF4BA8260CE24A56C231E588DE7FA9639D28B3F5CB9AD8CDFC48C1FE95B9C04077DC0FBF4C3BFC59A985E72F9B4992F29BC951182C835A9649482D423B3D34C1B5580096EA1A53GB4AD78EF168E1C7B93A7DEC770AC36432A5591974F53EBEDC3B487A243467DC2DE1F2D3F17B290052C7D0D7B01AD8D11E820DF88751CB4AE344BD9664C3A0D5C21ACC5305AA17C8B40CA7FD1CDBFF32BB1847B253B6B9374B75C81C9E89579739D79D3C29315523F6BA6ECFE122AC2E1769A07
	EA23AF7CAF4B5C44E42E7C7F0E15F7F816F8A29D607F28F6FB9C1E3A67A524035DB3E03099B422C56A59C4E1696EFA034924A117FE702E12FCEA1D8E3F361A0F9D05811AD1A70FAA122EEFF0765272D3E11AD123B8G63B6BD997E4D537CAF1BCA777FEBC7B0F9F004090D69BEF28E05C40BD1B7E03440A84486FD725735F63B315F6C4189BAAD4C857E178DB46A78DF166E38036DC589422ADFAAA5513A4F0994661E2A4B42BDA8653CB070ED3636907636733BCE6C9B85A9743BD5B3G1F71B6FE1320E870BB41
	73B7F8BB0635A61328B58D551A7564FB3CF5C7C50373FB54CF8E127C6DC8A3C53238B47A2D31C1D91A7F87D0CB8788C7999CE571BCGG8858GGD0CB818294G94G88G88GC5FBB0B6C7999CE571BCGG8858GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGABBDGGGG
**end of data**/
}
}