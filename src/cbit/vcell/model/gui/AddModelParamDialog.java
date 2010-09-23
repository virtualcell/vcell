package cbit.vcell.model.gui;
import java.awt.GridBagConstraints;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.Document;

import org.vcell.util.Compare;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2003 2:07:01 PM)
 * @author: Anuradha
 */
public class AddModelParamDialog extends org.vcell.util.gui.JInternalFrameEnhanced {
	//
	private ModelParameter fieldModelParameter = null;
	private JPanel ivjJInternalFrameEnhancedContentPane = null;
	private JButton ivjOKJButton = null;
	private JButton ivjCancelJButton = null;
	private JLabel ivjNameJLabel = null;
	private JTextField ivjNameValueJTextField = null;
	private JLabel ivjExpressionJLabel = null;
	private JTextField ivjExpressionValueTextField = null;
	private JLabel ivjUnitsJLabel = null;
	private JComboBox ivjUnitsJComboBox = null;
	private DefaultComboBoxModel ivjUnitsComboBoxModel = null;
	private String ivjAnnotationString = null;
	private JPanel ivjJPanel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private Document ivjdocument1 = null;
	private Document ivjdocument2 = null;
	private cbit.vcell.model.Model fieldModel = null;
	private DocumentManager fieldDocumentManager = null;
	private JTextArea annotationTextField = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.DocumentListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == AddModelParamDialog.this.getCancelJButton()) 
				cancel(e);
			if (e.getSource() == AddModelParamDialog.this.getOKJButton()) 
				oK(e);
		};
		public void changedUpdate(javax.swing.event.DocumentEvent e) {
			if (e.getDocument() == AddModelParamDialog.this.getdocument1()) 
				updateInterface();
			if (e.getDocument() == AddModelParamDialog.this.getdocument2()) 
				updateInterface();
			if (e.getDocument() == AddModelParamDialog.this.annotationTextField.getDocument()) 
				updateInterface();
		};
		public void insertUpdate(javax.swing.event.DocumentEvent e) {
			if (e.getDocument() == AddModelParamDialog.this.getdocument1()) 
				updateInterface();
			if (e.getDocument() == AddModelParamDialog.this.getdocument2()) 
				updateInterface();
			if (e.getDocument() == AddModelParamDialog.this.annotationTextField.getDocument()) 
				updateInterface();
		};
		public void removeUpdate(javax.swing.event.DocumentEvent e) {
			if (e.getDocument() == AddModelParamDialog.this.getdocument1()) 
				updateInterface();
			if (e.getDocument() == AddModelParamDialog.this.getdocument2()) 
				updateInterface();
			if (e.getDocument() == AddModelParamDialog.this.annotationTextField.getDocument()) 
				updateInterface();
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == AddModelParamDialog.this.getNameValueJTextField() && (evt.getPropertyName().equals("document"))) 
				setdocument1(getNameValueJTextField().getDocument());
			if (evt.getSource() == AddModelParamDialog.this && (evt.getPropertyName().equals("globalParameter"))) 
				getExpressionValueTextField().setText(getModelParameter().getExpression().infix());
			if (evt.getSource() == AddModelParamDialog.this && (evt.getPropertyName().equals("documentManager"))) 
				updateInterface();
			if (evt.getSource() == AddModelParamDialog.this.getExpressionValueTextField() && (evt.getPropertyName().equals("document")))
				setdocument2(getExpressionValueTextField().getDocument());
		};
	};

/**
 * EditSpeciesDialog constructor comment.
 */
public AddModelParamDialog() {
	super();
	initialize();
}

/**
 * EditSpeciesDialog constructor comment.
 * @param title java.lang.String
 */
public AddModelParamDialog(String title) {
	super(title);
}


/**
 * EditSpeciesDialog constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 */
public AddModelParamDialog(String title, boolean resizable) {
	super(title, resizable);
}


/**
 * EditSpeciesDialog constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 * @param closable boolean
 */
public AddModelParamDialog(String title, boolean resizable, boolean closable) {
	super(title, resizable, closable);
}


/**
 * EditSpeciesDialog constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 * @param closable boolean
 * @param maximizable boolean
 */
public AddModelParamDialog(String title, boolean resizable, boolean closable, boolean maximizable) {
	super(title, resizable, closable, maximizable);
}


/**
 * EditSpeciesDialog constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 * @param closable boolean
 * @param maximizable boolean
 * @param iconifiable boolean
 */
public AddModelParamDialog(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
	super(title, resizable, closable, maximizable, iconifiable);
}

/**
 * Comment
 */
private void annotateJButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	javax.swing.JTextArea jta = new javax.swing.JTextArea(getAnnotationString(),10,40);
	jta.setLineWrap(true);
	jta.setFocusable(true);
	javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(jta);
	javax.swing.JOptionPane.showMessageDialog(this,jsp,"Edit Species Annotation",javax.swing.JOptionPane.PLAIN_MESSAGE);
	String result = jta.getText();
	if((result != null) && (result.length() == 0)){
		result = null;
	}
	setAnnotationString(result);
}


/**
 * Comment
 */
private void cancel(java.awt.event.ActionEvent actionEvent) {
	dispose();
}

/**
 * Return the AnnotationString property value.
 * @return java.lang.String
 */
private java.lang.String getAnnotationString() {
	return ivjAnnotationString;
}


/**
 * Return the CancelJButton property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getCancelJButton() {
	if (ivjCancelJButton == null) {
		try {
			ivjCancelJButton = new javax.swing.JButton();
			ivjCancelJButton.setName("CancelJButton");
			ivjCancelJButton.setText("Cancel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCancelJButton;
}


/**
 * Return the ContextNameJLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getExpressionJLabel() {
	if (ivjExpressionJLabel == null) {
		try {
			ivjExpressionJLabel = new javax.swing.JLabel();
			ivjExpressionJLabel.setName("GlobalParamExprJLabel");
			ivjExpressionJLabel.setText("Expression:");
			ivjExpressionJLabel.setEnabled(true);
			ivjExpressionJLabel.setVisible(true);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjExpressionJLabel;
}

/**
 * Return the ContextNameValueJLabel property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getExpressionValueTextField() {
	if (ivjExpressionValueTextField == null) {
		try {
			ivjExpressionValueTextField = new javax.swing.JTextField();
			ivjExpressionValueTextField.setName("GlobalParamExprTextField");
			ivjExpressionValueTextField.setEnabled(true);
			ivjExpressionValueTextField.setVisible(true);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjExpressionValueTextField;
}

/**
 * Return the document1 property value.
 * @return javax.swing.text.Document
 */
private javax.swing.text.Document getdocument1() {
	return ivjdocument1;
}


/**
 * Return the document2 property value.
 * @return javax.swing.text.Document
 */
private javax.swing.text.Document getdocument2() {
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
 * Return the JInternalFrameEnhancedContentPane property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJInternalFrameEnhancedContentPane() {
	if (ivjJInternalFrameEnhancedContentPane == null) {
		try {
			ivjJInternalFrameEnhancedContentPane = new javax.swing.JPanel();
			ivjJInternalFrameEnhancedContentPane.setName("JInternalFrameEnhancedContentPane");
			ivjJInternalFrameEnhancedContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNameJLabel = new java.awt.GridBagConstraints();
			constraintsNameJLabel.gridx = 0; constraintsNameJLabel.gridy = 0;
			constraintsNameJLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsNameJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getNameJLabel(), constraintsNameJLabel);

			java.awt.GridBagConstraints constraintsNameValueJTextField = new java.awt.GridBagConstraints();
			constraintsNameValueJTextField.gridx = 1; constraintsNameValueJTextField.gridy = 0;
			constraintsNameValueJTextField.gridwidth = 3;
			constraintsNameValueJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameValueJTextField.weightx = 1.0;
			constraintsNameValueJTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getNameValueJTextField(), constraintsNameValueJTextField);

			java.awt.GridBagConstraints constraintsExpressionJLabel = new java.awt.GridBagConstraints();
			constraintsExpressionJLabel.gridx = 0; constraintsExpressionJLabel.gridy = 1;
			constraintsExpressionJLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsExpressionJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getExpressionJLabel(), constraintsExpressionJLabel);

			java.awt.GridBagConstraints constraintsExpressionValueTextField = new java.awt.GridBagConstraints();
			constraintsExpressionValueTextField.gridx = 1; constraintsExpressionValueTextField.gridy = 1;
			constraintsExpressionValueTextField.gridwidth = 3;
			constraintsExpressionValueTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsExpressionValueTextField.weightx = 1.0;
			constraintsExpressionValueTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getExpressionValueTextField(), constraintsExpressionValueTextField);

			java.awt.GridBagConstraints constraintsUnitsJLabel = new java.awt.GridBagConstraints();
			constraintsUnitsJLabel.gridx = 0; constraintsUnitsJLabel.gridy = 2;
			constraintsUnitsJLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsUnitsJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getUnitsJLabel(), constraintsUnitsJLabel);

			java.awt.GridBagConstraints constraintsUnitsComboBox = new java.awt.GridBagConstraints();
			constraintsUnitsComboBox.gridx = 1; constraintsUnitsComboBox.gridy = 2;
			constraintsUnitsComboBox.gridwidth = 2;
			constraintsUnitsComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsUnitsComboBox.weightx = 1.0;
			constraintsUnitsComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getUnitsJComboBox(), constraintsUnitsComboBox);
			
			java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; gbc.gridy = 3;
			gbc.insets = new java.awt.Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.NORTHEAST;
			getJInternalFrameEnhancedContentPane().add(new JLabel("Annotation"), gbc);

			annotationTextField = new javax.swing.JTextArea("", 4, 30);
			annotationTextField.setLineWrap(true);
			annotationTextField.setWrapStyleWord(true);
			javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextField);
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 1; gbc.gridy = 3;
			gbc.gridwidth = 2;
			gbc.gridheight = 4;
			gbc.fill = java.awt.GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			gbc.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(jsp, gbc);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 7;
			constraintsJPanel1.gridwidth = 4;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJInternalFrameEnhancedContentPane().add(getJPanel1(), constraintsJPanel1);

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJInternalFrameEnhancedContentPane;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setBorder(new org.vcell.util.gui.LineBorderBean());
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOKJButton = new java.awt.GridBagConstraints();
			constraintsOKJButton.gridx = 1; constraintsOKJButton.gridy = 1;
			constraintsOKJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getOKJButton(), constraintsOKJButton);

			java.awt.GridBagConstraints constraintsCancelJButton = new java.awt.GridBagConstraints();
			constraintsCancelJButton.gridx = 2; constraintsCancelJButton.gridy = 1;
			constraintsCancelJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getCancelJButton(), constraintsCancelJButton);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
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
 * Return the NameJLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getNameJLabel() {
	if (ivjNameJLabel == null) {
		try {
			ivjNameJLabel = new javax.swing.JLabel();
			ivjNameJLabel.setName("NameJLabel");
			ivjNameJLabel.setText("Name:");
			ivjNameJLabel.setEnabled(true);
			ivjNameJLabel.setVisible(true);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNameJLabel;
}

/**
 * Return the NameValueJTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getNameValueJTextField() {
	if (ivjNameValueJTextField == null) {
		try {
			ivjNameValueJTextField = new javax.swing.JTextField();
			ivjNameValueJTextField.setName("NameValueJTextField");
			ivjNameValueJTextField.setEnabled(true);
			ivjNameValueJTextField.setVisible(true);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNameValueJTextField;
}

/**
 * Return the NameJLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getUnitsJLabel() {
	if (ivjUnitsJLabel == null) {
		try {
			ivjUnitsJLabel = new javax.swing.JLabel();
			ivjUnitsJLabel.setName("UnitsJLabel");
			ivjUnitsJLabel.setText("Units:");
			ivjUnitsJLabel.setEnabled(true);
			ivjUnitsJLabel.setVisible(true);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUnitsJLabel;
}

/**
 * Return the UnitsJComboBox property value.
 * 
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getUnitsJComboBox() {
	if (ivjUnitsJComboBox == null) {
		try {
			ivjUnitsJComboBox = new javax.swing.JComboBox();
			ivjUnitsJComboBox.setName("UnitsComboBox");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUnitsJComboBox;
}

/**
 * Return the ivjUnitsComboBoxModel property value.
 * @return javax.swing.DefaultComboBoxModel
 */
private javax.swing.DefaultComboBoxModel getUnitsComboBoxModel() {
	if (ivjUnitsComboBoxModel == null) {
		try {
			ivjUnitsComboBoxModel = new javax.swing.DefaultComboBoxModel();
			// fill comboboxmodel with units list
			Iterator<VCUnitDefinition> vcUnits = VCUnitDefinition.getKnownUnits();
			while (vcUnits.hasNext()) {
				getUnitsComboBoxModel().addElement(vcUnits.next().getSymbol());
			}
			getUnitsComboBoxModel().addElement(VCUnitDefinition.TBD_SYMBOL);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	return ivjUnitsComboBoxModel;
}


/**
 * Return the OKJButton property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getOKJButton() {
	if (ivjOKJButton == null) {
		try {
			ivjOKJButton = new javax.swing.JButton();
			ivjOKJButton.setName("OKJButton");
			ivjOKJButton.setText("OK");
			ivjOKJButton.setEnabled(true);
			ivjOKJButton.setVisible(true);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOKJButton;
}

/**
 * Gets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @return The speciesContext property value.
 * @see #setSpeciesContext
 */
public ModelParameter getModelParameter() {
	return fieldModelParameter;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}


/**
 * Insert the method's description here.
 * Creation date: (5/20/2003 7:55:25 AM)
 * @param argModel cbit.vcell.model.Model
 * @param argDocumentManager cbit.vcell.clientdb.DocumentManager
 */
public void initAddGlobalParam(Model argModel, DocumentManager argDocumentManager) {
	if(argModel != null){
		setTitle("Add New Global Parameter: ");
		getOKJButton().setText("Add");
	}
	ModelParameter newModelParam = argModel.new ModelParameter(argModel.getFreeModelParamName(),new Expression(0.0), Model.ROLE_UserDefined, VCUnitDefinition.UNIT_TBD);
	setModelParameter(newModelParam);
	setModel(argModel);
	setDocumentManager(argDocumentManager);
}

public void initAddModelParam(Model argModel) {
	if(argModel != null){
		setTitle("Add New Global Parameter: ");
		getOKJButton().setText("Add");
	}
	ModelParameter newModelParam = argModel.new ModelParameter(argModel.getFreeModelParamName(),new Expression(0.0), Model.ROLE_UserDefined, VCUnitDefinition.UNIT_TBD);
	setModelParameter(newModelParam);
	setModel(argModel);
}

/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	getNameValueJTextField().addPropertyChangeListener(ivjEventHandler);
	getCancelJButton().addActionListener(ivjEventHandler);
	getOKJButton().addActionListener(ivjEventHandler);
	annotationTextField.getDocument().addDocumentListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getExpressionValueTextField().addPropertyChangeListener(ivjEventHandler);
	// set comboboxmodel on combobox for units
	getUnitsJComboBox().setModel(getUnitsComboBoxModel());
	setdocument1(getNameValueJTextField().getDocument());
	setdocument2(getExpressionValueTextField().getDocument());
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("AddModelParameter");
		setContentPane(getJInternalFrameEnhancedContentPane());
		initConnections();
		pack();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		AddModelParamDialog aAddModelParamDialog;
		aAddModelParamDialog = new AddModelParamDialog();
		frame.setContentPane(aAddModelParamDialog);
		frame.setSize(aAddModelParamDialog.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		ModelParameter mp = aAddModelParamDialog.getModelParameter();
		System.out.println("GlobalParam name : " + mp.getName() + "val = " + mp.getExpression());
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of cbit.gui.JInternalFrameEnhanced");
		exception.printStackTrace(System.out);
	}
}

private void checkModelParamUniqueness(String paramName) {
	// check if parameter has same name as species : do not allow
	Species[] sps = fieldModel.getSpecies();
	for (int i = 0; i < sps.length; i++) {
		if (sps[i].getCommonName().equals(paramName)) {
			throw new RuntimeException("Model Parameter \'" + paramName + "\' cannot have same name as model species");
		}
	}
	// check if parameter has same name as structures : do not allow
	Structure[] structs = fieldModel.getStructures();
	for (int i = 0; i < structs.length; i++) {
		if (structs[i].getName().equals(paramName)) {
			throw new RuntimeException("Model Parameter \'" + paramName + "\' cannot have same name as model structure");
		}
	}
	// check if parameter has same name as speciescontexts : do not allow
	SpeciesContext[] spcs = fieldModel.getSpeciesContexts();
	for (int i = 0; i < spcs.length; i++) {
		if (spcs[i].getName().equals(paramName)) {
			throw new RuntimeException("Model Parameter \'" + paramName + "\' cannot have same name as model speciesContext");
		}
	}
	// check if parameter has same name as reactionsteps : do not allow
	ReactionStep[] rs = fieldModel.getReactionSteps();
	for (int i = 0; i < rs.length; i++) {
		if (rs[i].getName().equals(paramName)) {
			throw new RuntimeException("Model Parameter \'" + paramName + "\' cannot have same name as model speciesContext");
		}
		KineticsParameter[] kps = rs[i].getKinetics().getKineticsParameters();
		for (int j = 0; j < kps.length; j++) {
			if (kps[j].getName().equals(paramName)) {
				throw new RuntimeException("Model Parameter \'" + paramName + "\' cannot have same name as a kinetic parameter defined in reaction \'" + rs[i].getName() + "\'");
			}
		}
	}
	// If it reaches this point, the name is probably unique, hence can be used for a (model) global parameter!
}

/**
 * Comment
 */
private void oK(java.awt.event.ActionEvent actionEvent) {
	try{
		String modelParamName = getNameValueJTextField().getText();
		// check for uniqueness of model (global) parameter name
		// checkModelParamUniqueness(modelParamName);
		
		getModelParameter().setName(getNameValueJTextField().getText());

		Expression exp = new Expression(getExpressionValueTextField().getText());
		try {
			exp.bindExpression(getModel());
		} catch (ExpressionBindingException e) {
			String msg = "";
			if (getModel().getSpecies(e.getIdentifier()) != null) {
				msg = ": If '" + e.getIdentifier() + "' is a species, please use its speciesContext name (eg: " + e.getIdentifier() + "_" + getModel().getStructures()[0].getName() + ") instead.";
			} else {
				msg = ": '" + e.getIdentifier() + "' is undefined in the global scope.";
			}
			throw new RuntimeException(e.getMessage() + msg);
		}

		getModelParameter().setExpression(exp);
		getModelParameter().setUnitDefinition(VCUnitDefinition.getInstance((String)getUnitsJComboBox().getSelectedItem()));
		setAnnotationString(annotationTextField.getText());
		getModelParameter().setModelParameterAnnotation(getAnnotationString());

		getModel().addModelParameter(getModelParameter());
		dispose();
	}catch(Exception e){
		e.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this,"Add Global Parameter Error: \n"+e.getMessage(), e);
	}
}


/**
 * Set the AnnotationString to a new value.
 * @param newValue java.lang.String
 */
private void setAnnotationString(java.lang.String newValue) {
	if (ivjAnnotationString != newValue) {
		try {
			ivjAnnotationString = newValue;
			updateInterface();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}


/**
 * Set the document1 to a new value.
 * @param newValue javax.swing.text.Document
 */
private void setdocument1(javax.swing.text.Document newValue) {
	if (ivjdocument1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjdocument1 != null) {
				ivjdocument1.removeDocumentListener(ivjEventHandler);
			}
			ivjdocument1 = newValue;

			/* Listen for events from the new object */
			if (ivjdocument1 != null) {
				ivjdocument1.addDocumentListener(ivjEventHandler);
			}
			if ((getdocument1() != null)) {
				getNameValueJTextField().setDocument(getdocument1());
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}

/**
 * Set the document2 to a new value.
 * @param newValue javax.swing.text.Document
 */
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
			if ((getdocument2() != null)) {
				getExpressionValueTextField().setDocument(getdocument2());
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
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
 * Sets the globalParameter property value.
 * @param speciesContext The new value for the property.
 * @see #getGlobalParameter
 */
public void setModelParameter(ModelParameter modelParam) {
	ModelParameter oldValue = fieldModelParameter;
	fieldModelParameter = modelParam;
	firePropertyChange("modelParameter", oldValue, fieldModelParameter);
}

/**
 * Comment
 */
private void updateInterface() {
	if(getModelParameter() == null){
		getNameValueJTextField().setText(null);
	}
	getNameValueJTextField().setEnabled(getModelParameter() != null);

	getExpressionJLabel().setEnabled(getModelParameter() != null);

	getExpressionValueTextField().setEnabled(getModelParameter() != null);

	getOKJButton().setEnabled(
			(getModelParameter() != null) && (getModel() != null) &&
			(getNameValueJTextField().getText() != null) && (getNameValueJTextField().getText().length() > 0) && 
			(getExpressionValueTextField().getText() != null) && (getExpressionValueTextField().getText().length() > 0) && 
				(
					!Compare.isEqualOrNull(getNameValueJTextField().getText(),getModelParameter().getName()) ||
					!Compare.isEqualOrNull(getAnnotationString(),annotationTextField.getText()) ||
					!Compare.isEqualOrNull(getModelParameter().getExpression().infix(),getExpressionValueTextField().getText())
				)
			);
}

}