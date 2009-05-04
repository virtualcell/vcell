package cbit.vcell.client.task;

import java.util.Hashtable;
import javax.swing.*;

import org.vcell.util.UserCancelException;
import org.vcell.util.document.VCDocument;

import cbit.vcell.client.*;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class NewName extends AsynchClientTask {
	
	public NewName() {
		super("Getting document name", TASKTYPE_SWING_BLOCKING);
	}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get("documentWindowManager");
	VCDocument document = documentWindowManager.getVCDocument();
	if (document.getDocumentType() == VCDocument.MATHMODEL_DOC) {
		if (((MathModelWindowManager)documentWindowManager).hasUnappliedChanges()) {
			String msg = "Changes have been made in VCML Editor, please click \"Apply Changes\" or \"Cancel\" to proceed.";
			PopupGenerator.showErrorDialog(documentWindowManager, msg);
			throw UserCancelException.CANCEL_UNAPPLIED_CHANGES;			
		}
	}

	MDIManager mdiManager = (MDIManager)hashTable.get("mdiManager");
	String oldName = document.getName();
	String oldVersionName = document.getVersion() == null ? "" : oldName;
	String myself = documentWindowManager.getRequestManager().getConnectionStatus().getUserName();
	// if the version is null which means this is a new document, so the owner would be myself
	String owner = document.getVersion() == null ? myself : document.getVersion().getOwner().getName();
	String newName = mdiManager.getDatabaseWindowManager().showSaveDialog(document.getDocumentType(), (JFrame)hashTable.get("currentDocumentWindow"), oldName);	
	if (newName == null || newName.trim().length()==0){
		throw new Exception("A name must be given to save");
	} else if (newName.contains("'")){
		throw new Exception("Apostrophe is not allowed in names");
	} else if (owner.equals(myself) && oldVersionName.equals(newName)) {
		throw new Exception("A model with name '" + newName + "' already exists. Please give a different name.");
	}
	document.setName(newName);
	hashTable.put("newName", newName);
}

}