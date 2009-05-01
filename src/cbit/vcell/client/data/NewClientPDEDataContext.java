package cbit.vcell.client.data;
import javax.swing.SwingUtilities;

import cbit.vcell.simdata.*;
/**
 * Insert the type's description here.
 * Creation date: (6/13/2004 3:36:26 PM)
 * @author: Ion Moraru
 */
public class NewClientPDEDataContext extends ClientPDEDataContext {
/**
 * NewClientPDEDataContext constructor comment.
 * @param dataManager cbit.vcell.desktop.controls.DataManager
 */
public NewClientPDEDataContext(cbit.vcell.desktop.controls.DataManager dataManager) {
	super(dataManager);
}


/**
 * This method was created in VisualAge.
 *
 * @param exportSpec cbit.vcell.export.server.ExportSpecs
 */
public void makeRemoteFile(cbit.vcell.export.server.ExportSpecs exportSpecs) throws org.vcell.util.DataAccessException {
	throw new RuntimeException("should not use this method in NewClientPDEDataContext");
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:03:43 PM)
 */
public void refreshIdentifiers() {
	try {
		final DataIdentifier[] newDataIdentifiers = getDataManager().getDataIdentifiers();
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						try{
							setDataIdentifiers(newDataIdentifiers);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
		);
	} catch (org.vcell.util.DataAccessException exc) {
		exc.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:03:43 PM)
 */
public void refreshTimes() throws org.vcell.util.DataAccessException {
	setTimePoints(getDataManager().getDataSetTimes());
}
}