package cbit.vcell.simdata.gui;

import javax.swing.JPanel;

import java.awt.GridBagLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.Document;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.lang.String;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import cbit.gui.TextFieldAutoCompletion;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.Function;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solvers.FVSolver;

import javax.swing.JComboBox;

import org.vcell.util.BeanUtils;
import org.vcell.util.TokenMangler;

public class FunctionSpecifierPanel extends JPanel implements ActionListener,UndoableEditListener{

	private Vector<ActionListener> actionListenersV = new Vector<ActionListener>();  //  @jve:decl-index=0:
	private DataIdentifier[] allIdentifiers;
	private AnnotatedFunction[] allFunctions;
	private Document documentFuncName = null;
	private Document documentExpression = null;
	private boolean isErrorCurrent = false;
	private boolean isFunctionCurrent = false;
	private boolean isUserDefinedfunctionCurrent = false;
	private int functionOp = FUNC_OP_CANCEL;
	private AnnotatedFunction currentAnnotatedFunction = null;  //  @jve:decl-index=0:
	private String currentAnnotatedFunctionString = null;  //  @jve:decl-index=0:
	
	public static final int FUNC_OP_ADDNEW = 0;
	public static final int FUNC_OP_REPLACE = 1;
	public static final int FUNC_OP_DELETE = 2;
	public static final int FUNC_OP_CANCEL = 3;
	


	private JPanel jPanel = null;
	private JButton jButtonAddFunc = null;
	private JButton jButtonReplaceFunc = null;
	private JButton jButtonCancel = null;
	private JButton jButtonDelete = null;
	private JPanel jPanelEditArea = null;
	private JLabel jLabelFuncName = null;
	private JTextField jTextFieldFuncName = null;
	private JLabel jLabelExpression = null;
	private TextFieldAutoCompletion jTextFieldFuncExpression = null;
	private JComboBox jComboBoxAllIdentifiers = null;
	private JLabel jLabelAlldataIdentifiers = null;
	private JLabel jLabelErrorInfo = null;
	private JPanel jPanel1 = null;
	/**
	 * This method initializes 
	 * 
	 */
	public FunctionSpecifierPanel() {
		super();
		initialize();
		init_private();
	}

	private void init_private(){
		documentFuncName = getJTextFieldFuncName().getDocument();
		documentFuncName.addUndoableEditListener(this);
		
		documentExpression = getJTextFieldFuncExpression().getDocument();
		documentExpression.addUndoableEditListener(this);
		
		
		getJButtonAddFunc().addActionListener(this);
		getJButtonCancel().addActionListener(this);
		getJButtonDelete().addActionListener(this);
		getJButtonReplaceFunc().addActionListener(this);
	}
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
        gridBagConstraints13.gridx = 0;
        gridBagConstraints13.weightx = 1.0;
        gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints13.insets = new Insets(4, 4, 4, 4);
        gridBagConstraints13.gridy = 1;
        GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
        gridBagConstraints12.gridx = 0;
        gridBagConstraints12.insets = new Insets(4, 4, 4, 4);
        gridBagConstraints12.fill = GridBagConstraints.NONE;
        gridBagConstraints12.weightx = 0.0;
        gridBagConstraints12.weighty = 0.0;
        gridBagConstraints12.gridy = 3;
        jLabelErrorInfo = new JLabel();
        jLabelErrorInfo.setText("Only User Defined Functions allow 'Delete' or 'Replace Expression'");
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 0;
        gridBagConstraints11.insets = new Insets(4, 4, 4, 4);
        gridBagConstraints11.gridy = 0;
        jLabelAlldataIdentifiers = new JLabel();
        jLabelAlldataIdentifiers.setText("All Data Identifiers");
        GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
        gridBagConstraints9.gridx = 0;
        gridBagConstraints9.weightx = 1.0;
        gridBagConstraints9.weighty = 1.0;
        gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints9.insets = new Insets(8, 0, 0, 0);
        gridBagConstraints9.gridy = 2;
        GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
        gridBagConstraints8.gridx = 0;
        gridBagConstraints8.gridy = 4;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(409, 222));
        this.add(getJPanel(), gridBagConstraints8);
        this.add(getJPanelEditArea(), gridBagConstraints9);
        this.add(jLabelAlldataIdentifiers, gridBagConstraints11);
        this.add(jLabelErrorInfo, gridBagConstraints12);
        this.add(getJPanel1(), gridBagConstraints13);
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 3;
			gridBagConstraints2.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints2.gridy = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints.gridy = 0;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getJButtonAddFunc(), gridBagConstraints);
			jPanel.add(getJButtonReplaceFunc(), gridBagConstraints1);
			jPanel.add(getJButtonCancel(), gridBagConstraints2);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButtonAddFunc	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAddFunc() {
		if (jButtonAddFunc == null) {
			jButtonAddFunc = new JButton();
			jButtonAddFunc.setText("Add New Function");
			jButtonAddFunc.setEnabled(false);
		}
		return jButtonAddFunc;
	}

	/**
	 * This method initializes jButtonReplaceFunc	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonReplaceFunc() {
		if (jButtonReplaceFunc == null) {
			jButtonReplaceFunc = new JButton();
			jButtonReplaceFunc.setText("Replace Expression");
			jButtonReplaceFunc.setEnabled(false);
		}
		return jButtonReplaceFunc;
	}

	public AnnotatedFunction getSelectedAnnotatedFunction(){
		return currentAnnotatedFunction;
	}
	public AnnotatedFunction getNewUserCreatedAnnotatedFunction(boolean isSpatial) throws ExpressionException{
		Function func =
			new Function("temp",new Expression(getJTextFieldFuncExpression().getText()));
		
		String[] dataIdNames = new String[allIdentifiers.length];
		VariableType[] dataIdVarTypes = new VariableType[allIdentifiers.length];
		
		for (int i = 0; i < allIdentifiers.length; i++){
			dataIdNames[i] = allIdentifiers[i].getName();
			dataIdVarTypes[i] = allIdentifiers[i].getVariableType();
		}
		VariableType funcType = FVSolver.getFunctionVariableType(func, dataIdNames, dataIdVarTypes,isSpatial);
		if (funcType.equals(VariableType.UNKNOWN)) {
			throw new IllegalArgumentException("Must specify variable type for field function");
		}
		
		return
			new AnnotatedFunction(
					getJTextFieldFuncName().getText(),
					new Expression(getJTextFieldFuncExpression().getText()),
					null,
					funcType,
					true
			);
	}
	public int getFunctionOp(){
		return functionOp;
	}
	/**
	 * This method initializes jButtonCancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText("Cancel");
		}
		return jButtonCancel;
	}

	/**
	 * This method initializes jButtonDelete	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonDelete() {
		if (jButtonDelete == null) {
			jButtonDelete = new JButton();
			jButtonDelete.setText("Delete");
			jButtonDelete.setEnabled(false);
		}
		return jButtonDelete;
	}

	/**
	 * This method initializes jPanelEditArea	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelEditArea() {
		if (jPanelEditArea == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 4;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.gridx = 0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.insets = new Insets(4, 0, 4, 0);
			gridBagConstraints6.gridy = 3;
			jLabelExpression = new JLabel();
			jLabelExpression.setText("Function Expression (Edit to 'Replace Expression')");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.gridx = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.insets = new Insets(4, 0, 4, 0);
			gridBagConstraints4.gridy = 1;
			jLabelFuncName = new JLabel();
			jLabelFuncName.setText("Function Name (Edit to 'Add New' User Defined Function)");
			jPanelEditArea = new JPanel();
			jPanelEditArea.setLayout(new GridBagLayout());
			jPanelEditArea.add(jLabelFuncName, gridBagConstraints4);
			jPanelEditArea.add(getJTextFieldFuncName(), gridBagConstraints5);
			jPanelEditArea.add(jLabelExpression, gridBagConstraints6);
			jPanelEditArea.add(getJTextFieldFuncExpression(), gridBagConstraints7);
		}
		return jPanelEditArea;
	}

	/**
	 * This method initializes jTextFieldFuncName	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldFuncName() {
		if (jTextFieldFuncName == null) {
			jTextFieldFuncName = new JTextField();
		}
		return jTextFieldFuncName;
	}

	/**
	 * This method initializes jTextFieldFuncExpression	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private TextFieldAutoCompletion getJTextFieldFuncExpression() {
		if (jTextFieldFuncExpression == null) {
			jTextFieldFuncExpression = new TextFieldAutoCompletion();
		}
		return jTextFieldFuncExpression;
	}

	public void initFunctionInfo(String identifierInit,DataIdentifier[] argAllIdentifiers,AnnotatedFunction[] argAllFunctions, int dimension){

		allIdentifiers = Arrays.asList(argAllIdentifiers).toArray(new DataIdentifier[0]);
		Arrays.sort(allIdentifiers,
			new Comparator<DataIdentifier>(){
				public int compare(DataIdentifier o1, DataIdentifier o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
		}});
		allFunctions = argAllFunctions;
		
		getJComboBoxAllIdentifiers().removeActionListener(this);
		getJComboBoxAllIdentifiers().removeAllItems();
		
		Set<String> varNames = new HashSet<String>();
		int selectIndex = -1;
		for(int i=0;i< allIdentifiers.length;i+= 1){
			String varname = allIdentifiers[i].getName();
			varNames.add(varname);
			getJComboBoxAllIdentifiers().addItem(varname);
			if(varname.equals(identifierInit)){
				selectIndex = i;
			}
		}
		varNames.add(ReservedSymbol.X.getName());
		if (dimension > 1) {
			varNames.add(ReservedSymbol.Y.getName());
			if (dimension > 2) {
				varNames.add(ReservedSymbol.Z.getName());
			}
		}
		getJComboBoxAllIdentifiers().addActionListener(this);
		if(selectIndex != -1){
			getJComboBoxAllIdentifiers().setSelectedIndex(selectIndex);
		}
		getJTextFieldFuncExpression().setAutoCompletionWords(varNames);
	}

	/**
	 * This method initializes jComboBoxAllIdentifiers	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBoxAllIdentifiers() {
		if (jComboBoxAllIdentifiers == null) {
			jComboBoxAllIdentifiers = new JComboBox();
		}
		return jComboBoxAllIdentifiers;
	}

	public void addActionListener(ActionListener actionListener){
		if(!actionListenersV.contains(actionListener)){
			actionListenersV.add(actionListener);
		}
	}
	public void actionPerformed(ActionEvent e) {
		int tempFuncOp = -1;
		if(e.getSource() == getJButtonAddFunc()){
			tempFuncOp = FUNC_OP_ADDNEW;
			String editFuncNameString = getJTextFieldFuncName().getText();
			if(!editFuncNameString.equals(TokenMangler.fixTokenStrict(editFuncNameString))){
				PopupGenerator.showInfoDialog(
						"Function name '"+editFuncNameString+"' has illegal characters.\nOnly letters,numbers and underscore allowed.");
				return;
			}
		}else if(e.getSource() == getJButtonCancel()){
			tempFuncOp = FUNC_OP_CANCEL;
		}else if(e.getSource() == getJButtonDelete()){
			if(PopupGenerator.showComponentOKCancelDialog(this,
					new JLabel("OK to Delete Function '"+currentAnnotatedFunction.getName()+"'"),
					"Confirm Delete") != JOptionPane.OK_OPTION){
				return;
			}
			tempFuncOp = FUNC_OP_DELETE;
		}else if(e.getSource() == getJButtonReplaceFunc()){
			JTextArea jta = new JTextArea("Replace function '"+currentAnnotatedFunction.getName()+"'\n"+
			"original= '"+currentAnnotatedFunction.getExpression().infix()+"'\n"+
			"new     = '"+getJTextFieldFuncExpression().getText()+"'");
			jta.setEditable(false);
			JScrollPane jsp = new JScrollPane(jta);
			jsp.setPreferredSize(new Dimension(400,100));
			jsp.setMinimumSize(new Dimension(400,100));
			if(PopupGenerator.showComponentOKCancelDialog(this,
					jsp,"Confirm Replace") != JOptionPane.OK_OPTION){
				return;
			}
			tempFuncOp = FUNC_OP_REPLACE;
		}
		if(tempFuncOp != -1){
			functionOp = tempFuncOp;
			ActionEvent actionEvent = new ActionEvent(this,functionOp,null);
			for(int i=0;i<actionListenersV.size();i+= 1){
				actionListenersV.elementAt(i).actionPerformed(actionEvent);
			}
			return;
		}
		if(e.getSource() == getJComboBoxAllIdentifiers()){
			currentAnnotatedFunction = null;
			currentAnnotatedFunctionString = null;
			
			String selectedDataID = null;
			String expression = null;
			try{
				documentFuncName.removeUndoableEditListener(this);
				BeanUtils.enableComponents(jPanelEditArea, true);
				isErrorCurrent = false;
				isFunctionCurrent = false;
				isUserDefinedfunctionCurrent = false;
				
				selectedDataID = getJComboBoxAllIdentifiers().getSelectedItem().toString();
				expression = selectedDataID;
				currentAnnotatedFunction = null;
				for(int i=0;i<allFunctions.length;i+= 1){
					if(allFunctions[i].getName().equals(selectedDataID)){
						currentAnnotatedFunction = allFunctions[i];
						isFunctionCurrent = true;
						isUserDefinedfunctionCurrent = currentAnnotatedFunction.isUserDefined();
						expression = currentAnnotatedFunction.getExpression().infix();
						currentAnnotatedFunctionString = expression;
						break;
					}
				}
				

				if(isErrorCurrent){
					BeanUtils.enableComponents(jPanelEditArea, false);
				}
				
				String varType = "";
				for(int i=0;i<allIdentifiers.length;i+= 1){
					if(allIdentifiers[i].getName().equals(selectedDataID)){
						varType =
							(allIdentifiers[i].getVariableType().equals(VariableType.VOLUME)?"Vol":"") +
							(allIdentifiers[i].getVariableType().equals(VariableType.VOLUME_REGION)?"VolReg":"") +
							(allIdentifiers[i].getVariableType().equals(VariableType.MEMBRANE)?"Memb":"") +
							(allIdentifiers[i].getVariableType().equals(VariableType.MEMBRANE_REGION)?"MembReg":"");
						break;
					}
				}
				jLabelAlldataIdentifiers.setText("All Data Identifiers (current is "+
						varType+" "+
						(isFunctionCurrent?(isUserDefinedfunctionCurrent?"'User Func'":"'Sim Func'"):"'Sim Var'")
					+")");
			}finally{
				documentFuncName.addUndoableEditListener(this);
				getJTextFieldFuncExpression().setText(expression);
				getJTextFieldFuncName().setText(selectedDataID);
				getJTextFieldFuncExpression().setCaretPosition(0);
				getJTextFieldFuncName().setCaretPosition(0);

				
			}
		}
		
	}

	public void undoableEditHappened(UndoableEditEvent e) {
		getJButtonAddFunc().setEnabled(false);
		getJButtonDelete().setEnabled(false);
		getJButtonReplaceFunc().setEnabled(false);
		
		boolean bNameConflict = false;
		boolean bNameSameAsSelected = false;
		String editFuncNametext = getJTextFieldFuncName().getText();
		boolean bEnableEditFuncName = editFuncNametext != null && editFuncNametext.length() > 0;
		for(int i=0;i<allIdentifiers.length;i+= 1){
			if(allIdentifiers[i].getName().equals(editFuncNametext)){
				bNameConflict = true;
				bNameSameAsSelected =
					getJComboBoxAllIdentifiers().getSelectedItem().toString().equals(editFuncNametext);
				break;
			}
		}
		String editFuncExprtext = getJTextFieldFuncExpression().getText();
		boolean bEnableEditFuncExpr = editFuncExprtext != null && editFuncExprtext.length() > 0;
		if(!isErrorCurrent){
			getJButtonAddFunc().setEnabled(!bNameConflict && bEnableEditFuncName && bEnableEditFuncExpr);
			getJButtonDelete().setEnabled(bNameSameAsSelected && isUserDefinedfunctionCurrent);
			getJButtonReplaceFunc().setEnabled(bEnableEditFuncExpr &&
					bNameSameAsSelected &&
					isUserDefinedfunctionCurrent &&
					(currentAnnotatedFunctionString != null?
							!currentAnnotatedFunctionString.equals(editFuncExprtext):
							true)
			);
//			if((bNameConflict && !bNameSameAsSelected) || (bNameSameAsSelected && !isFunctionCurrent)){
//				jLabelErrorInfo.setText(EDIT_STATUS_HEADER+" Edit name to 'Add New'. Note: Only 'User Func' allows replace or delete.");
//			}else if(bNameSameAsSelected && isFunctionCurrent && !isUserDefinedfunctionCurrent){
//				jLabelErrorInfo.setText(EDIT_STATUS_HEADER+" Edit name to 'Add New'. Note: Only 'User Func' allows replace or delete.");
//			}else if(bNameConflict && bNameSameAsSelected && isUserDefinedfunctionCurrent){
//				jLabelErrorInfo.setText(EDIT_STATUS_HEADER+" Edit Function Name to 'Add New' Note: Only 'User Func' allows replace or delete.");
//			}else{
//				jLabelErrorInfo.setText(EDIT_STATUS_HEADER+" OK.");
//			}
		}
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 1;
			gridBagConstraints10.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints10.gridy = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.insets = new Insets(4, 4, 4, 4);
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.add(getJComboBoxAllIdentifiers(), gridBagConstraints3);
			jPanel1.add(getJButtonDelete(), gridBagConstraints10);
		}
		return jPanel1;
	}
}  //  @jve:decl-index=0:visual-constraint="10,38"
