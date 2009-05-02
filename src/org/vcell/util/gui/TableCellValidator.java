package org.vcell.util.gui;
/**
 * Insert the type's description here.
 * Creation date: (9/13/2005 11:48:37 AM)
 * @author: Frank Morgan
 */
public interface TableCellValidator {

	public static final EditValidation VALIDATE_OK = new EditValidation();
	
	public static class EditValidation {
		private boolean bValidateOK = true;
		private String validateFailedMessage = null;

		private EditValidation(){//VALIDATE_OK
		}
		public EditValidation(String argVerifyFailedMessage){
			if(argVerifyFailedMessage == null){
				throw new IllegalArgumentException("EditValidation(=false) message cannot be null");
			}
			bValidateOK = false;
			validateFailedMessage = argVerifyFailedMessage;
		}
		public boolean isValidateOK(){
			return bValidateOK;
		}
		public String getValidateFailedMessage(){
			return validateFailedMessage;
		}
	}

/**
 * Insert the method's description here.
 * Creation date: (9/13/2005 11:49:06 AM)
 * @return boolean
 * @param obj java.lang.Object
 */
EditValidation validate(Object obj,int row,int col);
}