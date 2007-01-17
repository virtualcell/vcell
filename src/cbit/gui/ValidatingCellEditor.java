package cbit.gui;
/**
 * Insert the type's description here.
 * Creation date: (9/13/2005 2:08:03 PM)
 * @author: Frank Morgan
 */
public class ValidatingCellEditor extends javax.swing.DefaultCellEditor {

	private EditorValueProvider editorValueProvider;
	private javax.swing.JTable lastTable = null;
	private int lastRow = -1;
	private int lastCol = -1;
	
/**
 * ExpressionCellEditor constructor comment.
 * @param checkBox javax.swing.JCheckBox
 */
public ValidatingCellEditor(javax.swing.JCheckBox checkBox,EditorValueProvider evp) {
	super(checkBox);
	editorValueProvider = evp;

}


/**
 * ExpressionCellEditor constructor comment.
 * @param comboBox javax.swing.JComboBox
 */
public ValidatingCellEditor(javax.swing.JComboBox comboBox,EditorValueProvider evp) {
	super(comboBox);
	editorValueProvider = evp;

}


/**
 * ExpressionCellEditor constructor comment.
 * @param textField javax.swing.JTextField
 */
public ValidatingCellEditor(javax.swing.JTextField textField,EditorValueProvider evp) {
	super(textField);
	editorValueProvider = evp;
}


public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column) {

	lastTable = table;
	lastRow = row;
	lastCol = column;

	if(editorValueProvider != null){
		delegate.setValue(editorValueProvider.getEditorValue(value));
	}else{
		delegate.setValue(value);
	}

	return editorComponent;
}


public final boolean stopCellEditing() {

	//
	//Three things can happen:
	//1.  The current editor contains a value that is validated OK,
	//		continue normally.
	//2.  The current editor contains a value that is validated NOT OK,
	//		user re-enters value until VALIDATE_OK -OR- user CANCELS and edit is lost.
	//3.  The current editor contains a value that CANNOT be validated (Exceptions outside verify,verify not implemented,etc...),
	//		validation is UNKNOWN, keep unvalidated value and continue.
	//
	if(lastTable.getModel() instanceof cbit.gui.TableCellValidator){
		final cbit.gui.TableCellValidator editValidate = (cbit.gui.TableCellValidator)lastTable.getModel();
		try{
			Object newValue = delegate.getCellEditorValue();
			cbit.gui.TableCellValidator.EditValidation editValidation = null;
			while(!(editValidation = editValidate.validate(newValue,lastRow,lastCol)).isValidateOK()){
				newValue =
					DialogUtils.showInputDialog0(
						getComponent(),
						editValidation.getValidateFailedMessage()+"\nProvide new value.",
						newValue.toString());
			}
			delegate.setValue(newValue);//VALIDATE_OK, delegate gets New Good value
		}catch(UtilCancelException e){
			delegate.setValue(lastTable.getValueAt(lastRow,lastCol));//delegate gets Last Good value
		}catch(Throwable e){//Delegate keeps UNVALIDATED value
		}
	}
	return super.stopCellEditing();
}
}