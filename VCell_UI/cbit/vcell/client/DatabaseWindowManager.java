package cbit.vcell.client;
import cbit.vcell.xml.merge.gui.TMLPanel;
import java.util.*;
import java.io.*;
import cbit.image.VCImage;
import cbit.image.VCImageInfo;
import cbit.vcell.client.database.DocumentManager;
import cbit.vcell.client.desktop.*;
import cbit.vcell.geometry.*;
import cbit.vcell.mathmodel.*;
import cbit.vcell.client.server.*;
import cbit.util.*;
import cbit.sql.*;
import java.awt.*;
import cbit.vcell.biomodel.*;
import javax.swing.*;
import cbit.vcell.desktop.*;
import swingthreads.*;
import cbit.vcell.client.desktop.geometry.ImageBrowser;
import cbit.vcell.math.MathDescription;
import cbit.vcell.modelapp.SimulationContext;

import javax.swing.filechooser.FileFilter;

/**
 * Insert the type's description here.
 * Creation date: (5/14/2004 5:06:46 PM)
 * @author: Ion Moraru
 */
public class DatabaseWindowManager extends TopLevelWindowManager implements java.awt.event.ActionListener{

	private final int PIXEL_CLASS_WARNING_LIMIT = 10;
	
	private class ImageHelper{
		public int[] imageData;
		public int xsize;
		public int ysize;
		public int zsize;
	};
	//
	//
	private RequestManager requestManager = null;
	
	private BioModelDbTreePanel bioModelDbTreePanel = new BioModelDbTreePanel();
	private ACLEditor aclEditor = new ACLEditor();
	private ImageBrowser imageBrowser = new ImageBrowser();
	private cbit.vcell.geometry.gui.ImageAttributePanel imageAttributePanel = new cbit.vcell.geometry.gui.ImageAttributePanel();
	private GeometryTreePanel geometryTreePanel = new GeometryTreePanel();
	private MathModelDbTreePanel MathModelDbTreePanel = new MathModelDbTreePanel();

	private JDialog theJDialog = null;
	private Object doubleClickValue = null;
	public static final String BM_MM_GM_DOUBLE_CLICK_ACTION = "bm_mm_gm_dca";

	private JOptionPane openDialog = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"Open","Cancel"});
	private JOptionPane accessPermissionDialog = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"OK", "Cancel"});
	private JOptionPane imageSelectDialog = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"OK", "Cancel"});
	private JOptionPane saveDialog = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"Save", "Cancel"});
	private JOptionPane importDialog = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"Import", "Cancel"});

	private DatabaseWindowPanel databaseWindowPanel = null;

/**
 * Insert the method's description here.
 * Creation date: (5/17/2004 1:50:08 PM)
 * @param vcellClient cbit.vcell.client.VCellClient
 */
public DatabaseWindowManager(DatabaseWindowPanel databaseWindowPanel, RequestManager requestManager) {
	super(requestManager);
	setDatabaseWindowPanel(databaseWindowPanel);
	getBioModelDbTreePanel().setPopupMenuDisabled(true);
	getMathModelDbTreePanel().setPopupMenuDisabled(true);
	getMathModelDbTreePanel().setMetadataPanelPopupDisabled(true);
	getGeometryTreePanel().setPopupMenuDisabled(true);
	getGeometryTreePanel().setMetadataPanelPopupDisabled(true);

	//Listen for double-clicks
	getBioModelDbTreePanel().removeActionListener(this);
	getBioModelDbTreePanel().addActionListener(this);
	getMathModelDbTreePanel().removeActionListener(this);
	getMathModelDbTreePanel().addActionListener(this);
	getGeometryTreePanel().removeActionListener(this);
	getGeometryTreePanel().addActionListener(this);

}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:35:55 PM)
 */
public void accessPermissions()  {
	VersionInfo selectedVersionInfo = null;

	if (getPanelSelection() != null) {
		selectedVersionInfo = getPanelSelection();
	}

	getAclEditor().clearACLList();	
	GroupAccess groupAccess = selectedVersionInfo.getVersion().getGroupAccess();
	getAclEditor().setACLState(new ACLEditor.ACLState(groupAccess));
	DocumentManager docManager = getRequestManager().getDocumentManager();
	
	Object choice = showAccessPermissionDialog(getAclEditor(), null);

	if (choice != null && choice.equals("OK")) {
		ACLEditor.ACLState aclState = getAclEditor().getACLState();
		if (aclState != null) {
			if (aclState.isAccessPrivate() || (aclState.getAccessList() != null && aclState.getAccessList().length == 0)) {
				try {
					VersionInfo vInfo = null;
					if(selectedVersionInfo instanceof BioModelInfo){
						vInfo = docManager.setGroupPrivate((BioModelInfo)selectedVersionInfo);
					}else if(selectedVersionInfo instanceof MathModelInfo){
						vInfo = docManager.setGroupPrivate((MathModelInfo)selectedVersionInfo);
					}else if(selectedVersionInfo instanceof GeometryInfo){
						vInfo = docManager.setGroupPrivate((GeometryInfo)selectedVersionInfo);
					}else if(selectedVersionInfo instanceof VCImageInfo){
						vInfo = docManager.setGroupPrivate((VCImageInfo)selectedVersionInfo);
					}
				} catch (DataAccessException e) {
					PopupGenerator.showErrorDialog("Error Changing Permission "+e.getMessage());
				}
			} else if (aclState.isAccessPublic()) {
				try {
					VersionInfo vInfo = null;
					if(selectedVersionInfo instanceof BioModelInfo){
						vInfo = docManager.setGroupPublic((BioModelInfo)selectedVersionInfo);
					}else if(selectedVersionInfo instanceof MathModelInfo){
						vInfo = docManager.setGroupPublic((MathModelInfo)selectedVersionInfo);
					}else if(selectedVersionInfo instanceof GeometryInfo){
						vInfo = docManager.setGroupPublic((GeometryInfo)selectedVersionInfo);
					}else if(selectedVersionInfo instanceof VCImageInfo){
						vInfo = docManager.setGroupPublic((VCImageInfo)selectedVersionInfo);
					}
				} catch (DataAccessException e) {
					PopupGenerator.showErrorDialog("Error Changing Permission "+e.getMessage());
				}
			} else {
				String[] aclUserNames = aclState.getAccessList();
				String[] originalGroupAccesNames = new String[0];
				//Turn User[] into String[]
				if (groupAccess instanceof GroupAccessSome){
					GroupAccessSome gas = (GroupAccessSome)groupAccess;
					User[] originalUsers = gas.getNormalGroupMembers();
					for(int i=0;i<originalUsers.length;i+= 1){
						originalGroupAccesNames = (String[])BeanUtils.addElement(originalGroupAccesNames,originalUsers[i].getName());
					}
				}
				//Determine users needing adding
				String[] needToAddUsers = new String[0];
				for(int i=0;i<aclUserNames.length;i+= 1){
					if(!BeanUtils.arrayContains(originalGroupAccesNames,aclUserNames[i])){
System.out.println("Added user="+aclUserNames[i]);
						needToAddUsers = (String[])BeanUtils.addElement(needToAddUsers,aclUserNames[i]);
					}
				}
				//Determine users needing removing
				String[] needToRemoveUsers = new String[0];
				for(int i=0;i<originalGroupAccesNames.length;i+= 1){
					if(!BeanUtils.arrayContains(aclUserNames,originalGroupAccesNames[i])){
System.out.println("Removed user="+originalGroupAccesNames[i]);
						needToRemoveUsers = (String[])BeanUtils.addElement(needToRemoveUsers,originalGroupAccesNames[i]);
					}
				}
				
				VersionInfo vInfo = null;
				String errorNames = "";
				//Add Users to Group Access List
				for (int i = 0; i < needToAddUsers.length; i++) {
					try {
						if(selectedVersionInfo instanceof BioModelInfo){
							vInfo = docManager.addUserToGroup((BioModelInfo)selectedVersionInfo, needToAddUsers[i]);
						}else if(selectedVersionInfo instanceof MathModelInfo){
							vInfo = docManager.addUserToGroup((MathModelInfo)selectedVersionInfo, needToAddUsers[i]);
						}else if(selectedVersionInfo instanceof GeometryInfo){
							vInfo = docManager.addUserToGroup((GeometryInfo)selectedVersionInfo, needToAddUsers[i]);
						}else if(selectedVersionInfo instanceof VCImageInfo){
							vInfo = docManager.addUserToGroup((cbit.image.VCImageInfo)selectedVersionInfo, needToAddUsers[i]);
						}
					} catch (DataAccessException e) {
						errorNames += "Error Adding name '"+needToAddUsers[i]+"'\n  -----"+e.getMessage()+"\n";
					}
				}
				// Remove users from Group Access List
				for (int i = 0; i < needToRemoveUsers.length; i++) {
					try {
						if(selectedVersionInfo instanceof BioModelInfo){
							vInfo = docManager.removeUserFromGroup((BioModelInfo)selectedVersionInfo, needToRemoveUsers[i]);
						}else if(selectedVersionInfo instanceof MathModelInfo){
							vInfo = docManager.removeUserFromGroup((MathModelInfo)selectedVersionInfo, needToRemoveUsers[i]);
						}else if(selectedVersionInfo instanceof GeometryInfo){
							vInfo = docManager.removeUserFromGroup((GeometryInfo)selectedVersionInfo, needToRemoveUsers[i]);
						}else if(selectedVersionInfo instanceof VCImageInfo){
							vInfo = docManager.removeUserFromGroup((cbit.image.VCImageInfo)selectedVersionInfo, needToRemoveUsers[i]);
						}
					} catch (DataAccessException e) {
						errorNames += "Error Removing name '"+needToRemoveUsers[i]+"'\n  -----"+e.getMessage()+"\n";
					}
				}
				if (errorNames.length() > 0) {
					PopupGenerator.showErrorDialog("Error Adding Users\n"+errorNames);
					accessPermissions();
				}
			}

		}
	}
	//else if choice == JOptionPane.CANCEL_OPTION) {
		//throw UserCancelException.CANCEL_DB_SELECTION;
	//}
}


	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {

	if(e.getActionCommand().equals(BM_MM_GM_DOUBLE_CLICK_ACTION)){

		if(!theJDialog.isShowing()){
			throw new RuntimeException(DatabaseWindowManager.class.getName()+" Unexpected double-click from invisible dialog");
		}
		
		if(e.getSource() == getBioModelDbTreePanel()){
			doubleClickValue = getBioModelDbTreePanel().getSelectedVersionInfo();
		}else if(e.getSource() == getMathModelDbTreePanel()){
			doubleClickValue = getMathModelDbTreePanel().getSelectedVersionInfo();
		}else if(e.getSource() == getGeometryTreePanel()){
			doubleClickValue = getGeometryTreePanel().getSelectedVersionInfo();
		}

		//force dialog to return
		theJDialog.dispose();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 8:54:01 AM)
 */
public void archive() {

	getRequestManager().curateDocument(getPanelSelection(),CurateSpec.ARCHIVE,this);
}


/**
 * Comment
 */
public void compareAnotherEdition() {
	//
	// get selected DocumentInfo info from original Tree.
	//
	if (getPanelSelection()==null){
		PopupGenerator.showErrorDialog("Error Comparing documents : No first document selected");
		return;
	}
	VCDocumentInfo thisDocumentInfo = getPanelSelection();
	//
	// Get the previous version of the documentInfo
	//
	VCDocumentInfo[] documentVersionsList = null;
	try {
		documentVersionsList = getDocumentVersionDates(thisDocumentInfo);
	} catch (DataAccessException e) {
		PopupGenerator.showErrorDialog("Error accessing second document!");
	}
	
	if (documentVersionsList == null || documentVersionsList.length == 0) {
		PopupGenerator.showErrorDialog("Error Comparing documents : Not Enough Versions to Compare!");
		return;
	}

	//
	// Obtaining the Dates of the versions as a String, to be displayed as a list
	//
	String versionDatesList[] = new String[documentVersionsList.length];
	for (int i = 0; i < documentVersionsList.length; i++) {
		versionDatesList[i] = documentVersionsList[i].getVersion().getDate().toString();
	}

	//
	// Get the user's choice of document version from the list box, use it to get the documentInfo for the
	// corresponding version 
	//

	String newVersionChoice = (String)PopupGenerator.showListDialog(this, versionDatesList, "Please select edition");

	if (newVersionChoice == null) {
		PopupGenerator.showErrorDialog("Error Comparing documents : Second document not selected!");
		return;
	}

	int versionIndex = -1;
	for (int i=0;i < versionDatesList.length;i++){
		if (versionDatesList[i].equals(newVersionChoice)){
			versionIndex = i;
		}
	}
	if (versionIndex == -1){
		PopupGenerator.showErrorDialog("Error Comparing documents : No such Version Exists "+newVersionChoice);
		return;
	}

	VCDocumentInfo anotherDocumentInfo = documentVersionsList[versionIndex];
	// Check if both document types are of the same kind. If not, throw an error. 
	if (((thisDocumentInfo instanceof BioModelInfo) && !(anotherDocumentInfo instanceof BioModelInfo)) ||
		((thisDocumentInfo instanceof MathModelInfo) && !(anotherDocumentInfo instanceof MathModelInfo)) ||
		((thisDocumentInfo instanceof GeometryInfo) && !(anotherDocumentInfo instanceof GeometryInfo))) {
		PopupGenerator.showErrorDialog("Error Comparing documents : The two documents are not of the same type!");
		return;
	}
	// Now that we have both the document versions to be compared, do the comparison and display the result
	compareWithOther(thisDocumentInfo, anotherDocumentInfo);
}

/**
 * Comment
 */
public void compareAnotherModel() {
	//
	// get selected DocumentInfo info from original Tree.
	//
	if (getPanelSelection()==null){
		PopupGenerator.showErrorDialog("Error Comparing documents : First document not selected");
		return;
	}
	VCDocumentInfo thisDocumentInfo = getPanelSelection();

	// Choose the other documentInfo. Bring up the appropriate dbTreePanel depending on the type of thisDocumentInfo
	VCDocumentInfo otherDocumentInfo = null;
	if (thisDocumentInfo instanceof BioModelInfo) {
		Object choice = showOpenDialog(getBioModelDbTreePanel(), this);
		if (choice != null && choice.equals("Open")) {
			otherDocumentInfo = (BioModelInfo)getBioModelDbTreePanel().getSelectedVersionInfo();
		}
	} else 	if (thisDocumentInfo instanceof MathModelInfo) {
		Object choice = showOpenDialog(getMathModelDbTreePanel(), this);
		if (choice != null && choice.equals("Open")) {
			otherDocumentInfo = (MathModelInfo)getMathModelDbTreePanel().getSelectedVersionInfo();
		}
	} else 	if (thisDocumentInfo instanceof GeometryInfo) {
		Object choice = showOpenDialog(getGeometryTreePanel(), this);
		if (choice != null && choice.equals("Open")) {
			otherDocumentInfo = (GeometryInfo)getGeometryTreePanel().getSelectedVersionInfo();
		}
	} 

	if (otherDocumentInfo == null){
		PopupGenerator.showErrorDialog("Error Comparing documents : Second document is null ");
		return;
	}
	// Check if both document types are of the same kind. If not, throw an error. 
	if (((thisDocumentInfo instanceof BioModelInfo) && !(otherDocumentInfo instanceof BioModelInfo)) ||
		((thisDocumentInfo instanceof MathModelInfo) && !(otherDocumentInfo instanceof MathModelInfo)) ||
		((thisDocumentInfo instanceof GeometryInfo) && !(otherDocumentInfo instanceof GeometryInfo))) {
		PopupGenerator.showErrorDialog("Error Comparing documents : The two documents are not of the same type!");
		return;
	}
	// Now that we have both the document versions to be compared, do the comparison and display the result
	compareWithOther(thisDocumentInfo, otherDocumentInfo);
}


/**
 * Comment
 */
public void compareLatestEdition()  {
	//
	// get selected DocumentInfo info from original Tree.
	//
	if (getPanelSelection()==null){
		PopupGenerator.showErrorDialog("Error Comparing documents : No first document selected");
		return;
	}
	VCDocumentInfo thisDocumentInfo = getPanelSelection();

	//
	// Get the latest version of the documentInfo
	//
	VCDocumentInfo[] documentVersionsList = null;
	try {
		documentVersionsList = getDocumentVersionDates(thisDocumentInfo);
	} catch (DataAccessException e) {
		PopupGenerator.showErrorDialog("Error accessing second document!");
	}
	
	if (documentVersionsList == null || documentVersionsList.length == 0) {
		PopupGenerator.showErrorDialog("Error Comparing documents : Not Enough Versions to Compare!");
		return;
	}
	//
	// Obtaining the latest version of the current documentInfo
	//
	VCDocumentInfo latestDocumentInfo = documentVersionsList[documentVersionsList.length-1];

	for (int i = 0; i < documentVersionsList.length; i++) {
		if (documentVersionsList[i].getVersion().getDate().after(latestDocumentInfo.getVersion().getDate())) {
			latestDocumentInfo = documentVersionsList[i];
		}
	}

	if (thisDocumentInfo.getVersion().getDate().after(latestDocumentInfo.getVersion().getDate())) {
		PopupGenerator.showErrorDialog("Current Version is the latest! Choose another Version or Model to compare!");
		return;
	}

	// Check if both document types are of the same kind. If not, throw an error. 
	if (((thisDocumentInfo instanceof BioModelInfo) && !(latestDocumentInfo instanceof BioModelInfo)) ||
		((thisDocumentInfo instanceof MathModelInfo) && !(latestDocumentInfo instanceof MathModelInfo)) ||
		((thisDocumentInfo instanceof GeometryInfo) && !(latestDocumentInfo instanceof GeometryInfo))) {
		PopupGenerator.showErrorDialog("Error Comparing documents : The two documents are not of the same type!");
		return;
	}
	//
	// Now that we have both the document versions to be compared, do the comparison and display the result
	//
	compareWithOther(thisDocumentInfo, latestDocumentInfo);
}


/**
 * Comment
 */
public void comparePreviousEdition()  {
	//
	// get selected DocumentInfo info from original Tree.
	//
	if (getPanelSelection()==null){
		PopupGenerator.showErrorDialog("Error Comparing documents : No first document selected");
		return;
	}
	VCDocumentInfo thisDocumentInfo = getPanelSelection();
	//
	// Get the previous version of the documentInfo
	//
	VCDocumentInfo[] documentVersionsList = null;
	try {
		documentVersionsList = getDocumentVersionDates(thisDocumentInfo);
	} catch (DataAccessException e) {
		PopupGenerator.showErrorDialog("Error accessing second document!");
	}
	
	if (documentVersionsList == null || documentVersionsList.length == 0) {
		PopupGenerator.showErrorDialog("Error Comparing documents : Not Enough Versions to Compare!");
		return;
	}
	//
	// Obtaining the previous version of the current biomodel. Set the previousBioModelInfo to
	// the first version in the bioModelVersionList. Then compare all the versions in the list
	// with the previousBioModelInfo to see if any of them are before previousBioModelInfo
	// datewise. If so, update previousBioModelInfo. The biomodelinfo stored in previousBioModelInfo
	// when it comes out of the loop is the previous version of the biomodel.
	//
	VCDocumentInfo previousDocumentInfo = documentVersionsList[0];
	boolean bPrevious = false;

	for (int i = 0; i < documentVersionsList.length; i++) {
		if (documentVersionsList[i].getVersion().getDate().before(thisDocumentInfo.getVersion().getDate())) {
			bPrevious = true;
			previousDocumentInfo = documentVersionsList[i];
		} else {
			break;
		}
	}

	if (previousDocumentInfo.equals(documentVersionsList[0]) && !bPrevious) {
		PopupGenerator.showErrorDialog("Current Version is the oldest! Choose another Version or Model to compare!");
		return;
	}

	// Check if both document types are of the same kind. If not, throw an error. 
	if (((thisDocumentInfo instanceof BioModelInfo) && !(previousDocumentInfo instanceof BioModelInfo)) ||
		((thisDocumentInfo instanceof MathModelInfo) && !(previousDocumentInfo instanceof MathModelInfo)) ||
		((thisDocumentInfo instanceof GeometryInfo) && !(previousDocumentInfo instanceof GeometryInfo))) {
		PopupGenerator.showErrorDialog("Error Comparing documents : The two documents are not of the same type!");
		return;
	}

	// Now that we have both the document versions to be compared, do the comparison and display the result
	compareWithOther(thisDocumentInfo, previousDocumentInfo);
}


//Processes the model comparison,
	private void compareWithOther(final VCDocumentInfo docInfo1, final VCDocumentInfo docInfo2) {
		
		final MDIManager mdiManager = new ClientMDIManager(DatabaseWindowManager.this.getRequestManager());       
		mdiManager.blockWindow(DatabaseWindowManager.this.getManagerID());
		SwingWorker worker = new SwingWorker() {
			AsynchProgressPopup pp = new AsynchProgressPopup(null, "Working", "Comparing editions...", false, false);
			Throwable e = null;
			public Object construct() {
				try {
					pp.start();
					pp.setMessage("Comparing...");
					TMLPanel comparePanel = DatabaseWindowManager.this.getRequestManager().compareWithOther(docInfo1, docInfo2);
					return comparePanel;
				} catch (Throwable exc) {
					e = exc;
					return null;
				}
			}
			public void finished() {
				if (e != null) {
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(DatabaseWindowManager.this, "Unable to compare editions\n"+e);
			 	} 
				// done
				pp.stop();
				//display the comparison window.
				JOptionPane comparePane = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"Apply Changes", "Discard"});
				TMLPanel comparePanel = (TMLPanel)get(); 
				comparePane.setMessage(comparePanel);
				JDialog compareDialog = comparePane.createDialog(DatabaseWindowManager.this.getDatabaseWindowPanel(), "Compare Models");
				compareDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				cbit.gui.ZEnforcer.showModalDialogOnTop(compareDialog,DatabaseWindowManager.this.getDatabaseWindowPanel());
				if ("Apply Changes".equals(comparePane.getValue())) {
					if (!comparePanel.tagsResolved()) {
						JOptionPane messagePane = new javax.swing.JOptionPane("Please resolve all tagged elements/attributes before proceeding.");
						JDialog messageDialog = messagePane.createDialog(comparePanel, "Error");
						messageDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						cbit.gui.ZEnforcer.showModalDialogOnTop(messageDialog,comparePanel);
					} else {
						BeanUtils.setCursorThroughout((Container)getComponent(), Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						try {
							getRequestManager().processComparisonResult(comparePanel, DatabaseWindowManager.this);
						} catch (RuntimeException e) {
							throw e;
						} finally {
							BeanUtils.setCursorThroughout((Container)getComponent(), Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
					}
				}       
				mdiManager.unBlockWindow(DatabaseWindowManager.this.getManagerID());   
			}
		};
		worker.start();
	}


/**
 * Insert the method's description here.
 * Creation date: (10/11/2004 12:10:45 PM)
 * @return cbit.image.VCImage
 * @param gifData byte[]
 */
private ImageHelper convertGIF(byte[] gifData) throws Exception, cbit.image.ImageException{

	ImageHelper ih = new ImageHelper();
	Image tempImage = Toolkit.getDefaultToolkit().createImage(gifData);
	tempImage = new ImageIcon(tempImage).getImage();
	ih.xsize = tempImage.getWidth(null);
	ih.ysize = tempImage.getHeight(null);

	java.awt.image.BufferedImage nativeImage = new java.awt.image.BufferedImage(ih.xsize,ih.ysize,java.awt.image.BufferedImage.TYPE_INT_RGB);
	Graphics2D g = nativeImage.createGraphics();
	g.drawImage(tempImage,0,0,null);
	g.dispose();
	
	ih.imageData = new int[ih.xsize*ih.ysize];
	nativeImage.getRGB(0,0,ih.xsize,ih.ysize,ih.imageData,0,ih.xsize);
	ih.zsize = 1;

	return ih;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/2004 12:10:45 PM)
 * @return cbit.image.VCImage
 * @param gifData byte[]
 */
private ImageHelper convertTIF(byte[] tifData) throws cbit.image.TiffException,IOException,cbit.image.ImageException{
	
	cbit.image.TiffImage tiffImage = new cbit.image.TiffImage();
	cbit.image.ByteArrayTiffInputSource batis = new cbit.image.ByteArrayTiffInputSource(tifData);
	tiffImage.read(batis);
	//TiffImage doesn't work properly, only trust the following format from TiffImage
	if(!tiffImage.getDataType().isByte() || tiffImage.getSizeZ() > 1){
		throw new cbit.image.ImageException("for TIFF, only single,8 bit(grayscale) tiff images supported");
	}
	ImageHelper ih = new ImageHelper();
	ih.imageData = tiffImage.getRGB();
	ih.xsize = tiffImage.getSizeX();
	ih.ysize = tiffImage.getSizeY();
	ih.zsize = 1;//TiffImage doesn't handle Z correctly
	if((ih.xsize*ih.ysize) != ih.imageData.length){
		throw new cbit.image.ImageException("Tif size="+ih.imageData.length+" does not match dimension "+ih.xsize+" x "+ih.ysize);
	}
	return ih;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/2004 12:21:47 PM)
 * @return cbit.image.VCImage
 */
private ImageHelper convertZIP(byte[] zipData,AsynchProgressPopup pp) {

	//
	// Read individual entries from zip as z-sections
	//
	java.util.zip.ZipInputStream zis = null;
	try {
		int finalxsize = 0;
		int finalysize = 0;
		int totalsize = 0;
		
		java.util.TreeMap sortedHash = new java.util.TreeMap();
		zis = new java.util.zip.ZipInputStream(new java.io.ByteArrayInputStream(zipData));
		java.util.zip.ZipEntry ze = null;
		while ((ze = zis.getNextEntry()) != null) {
			if(pp != null){
				pp.setMessage("Reading("+(sortedHash.size()+1)+") name=" + ze.getName() + (totalsize !=0?"   dim=("+finalxsize+" X "+finalysize+")":""));
			}
			if (ze.isDirectory() == false) {
				java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
				int count;
				byte b[] = new byte[65536];
				while ((count = zis.read(b, 0, b.length)) != -1) {
					bos.write(b, 0, count);
				}
				ImageHelper ih = null;
				if(	ze.getName().toLowerCase().endsWith(".gif")){
					ih = convertGIF(bos.toByteArray());
				}else if(ze.getName().toLowerCase().endsWith(".tif") || ze.getName().toLowerCase().endsWith(".tiff")){
					ih = convertTIF(bos.toByteArray());
				}else{
					throw new Exception("Zip file entry "+ze.getName()+" not recognized, must end with .gif,.tif,.tiff");
				}
				
				if(totalsize == 0){
					finalxsize = ih.xsize;
					finalysize = ih.ysize;
				}
				if(finalxsize != ih.xsize || finalysize != ih.ysize){
					throw new cbit.image.ImageException("Zip file entries must all have same X and Y dimension");
				}
				totalsize+= ih.imageData.length;
				if(totalsize > GeometrySpec.IMAGE_SIZE_LIMIT){
					throw new Exception("Zip Uncompressed total size exceeds limit of "+GeometrySpec.IMAGE_SIZE_LIMIT);
				}
				sortedHash.put(ze.getName(), ih);
				bos.close();
			}else{
				throw new RuntimeException("Zip files with directory entries not supported");
			}
			zis.closeEntry();
		}
		if (sortedHash.size() > 0) {
			ImageHelper[] entries = new ImageHelper[sortedHash.size()];
			sortedHash.values().toArray(entries);
			ImageHelper finalIH = new ImageHelper();
			finalIH.xsize = finalxsize;
			finalIH.ysize = finalysize;
			finalIH.zsize = entries.length;
			finalIH.imageData = new int[totalsize];
			int offset = 0;
			for(int i=0;i<entries.length;i+= 1){
				System.arraycopy(entries[i].imageData, 0, finalIH.imageData, offset, entries[i].imageData.length);
				offset+= entries[i].imageData.length;
			}
			return finalIH;
		}else{
			return null;
		}
	} catch (java.util.zip.ZipException zex) {
		throw new RuntimeException("Zip Data corrupt " + (zex.getMessage() != null?zex.getMessage():zex.getClass().getName()) );
	} catch (Exception e) {
		throw new RuntimeException("Error Reading Zip Data " + (e.getMessage() != null?e.getMessage():e.getClass().getName()));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 9:49:06 PM)
 */
public void deleteSelected() {
	getRequestManager().deleteDocument(getPanelSelection(), this);
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:35:55 PM)
 */
private VCImage editImageAttributes(VCImage image,AsynchProgressPopup pp) throws UserCancelException,cbit.image.ImageException,DataAccessException{

	VCImage editedImage = null;
	if (image == null) {
		PopupGenerator.showErrorDialog("No image!");
		return null;
	}

	//Set image on panel and see if there are any error before proceeding
	try{
		imageAttributePanel.setImage(image);
	}catch(Throwable e){
		throw new cbit.image.ImageException("Failed to setup ImageAttributes\n"+(e.getMessage() != null?e.getMessage():null));
	}

	Object choice = showImagePropertiesDialog();
	
	if (choice != null && choice.equals("Import")) {
		VCImageInfo imageInfos[] = null;
		pp.setMessage("Getting existing Image names");
		try {
			imageInfos = getRequestManager().getDocumentManager().getImageInfos();
		}catch (DataAccessException e){
			e.printStackTrace(System.out);
		}
		pp.setMessage("found "+(imageInfos != null?imageInfos.length:0)+" existing image names");
		String newName = null;
		boolean bNameIsGood = false;
		while (!bNameIsGood){
			newName = PopupGenerator.showInputDialog(this,
					"type a name for this IMAGE and proceed to view/edit GEOMETRY",image.getName());
			if (newName == null || newName.length() == 0){
				bNameIsGood = false;
				continue;
			}
			if (imageInfos==null){
				bNameIsGood = true; // if no image information assume image name is good
			}else{	
				boolean bNameExists = false;
				for (int i = 0; i < imageInfos.length; i++){
					if (imageInfos[i].getVersion().getName().equals(newName)){
						bNameExists = true;
						break;
					}
				}
				if (bNameExists){
					PopupGenerator.showErrorDialog("IMAGE name '"+newName+"' already exists, please enter new name");
				}else{
					bNameIsGood = true;
				}
			}
		}
		pp.setMessage("Saving new Image "+newName);
		try {
			editedImage = getRequestManager().getDocumentManager().saveAsNew(image,newName);
		} catch (DataAccessException e) {
			throw new DataAccessException((e.getMessage() != null?e.getMessage():null));
		}
		pp.setMessage("Save finished for "+newName);
		//
		// check that save actually occured (it should have since an insert should be new)
		//
		Version newVersion = editedImage.getVersion();
		Version oldVersion = image.getVersion();
		if ((oldVersion != null) && newVersion.getVersionKey().compareEqual(oldVersion.getVersionKey())){
			throw new DataAccessException("Save New Image failed, Old version has same id as New");
		}	
	}else{
		throw UserCancelException.CANCEL_EDIT_IMG_ATTR;
	}
	
	
	return editedImage;
}


	public void exportDocument() {

		getRequestManager().exportDocument(this);
	}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 9:15:25 AM)
 */
public void findModelsUsingSelectedGeometry() {
	
	VCDocumentInfo selectedDocument = getPanelSelection();
	
	if(!(selectedDocument instanceof GeometryInfo)){
		cbit.gui.DialogUtils.showErrorDialog("DatabaseWindowManager.findModelsUsingSelectedGeometry expected a GeometryInfo\nbut got type="+selectedDocument.getClass().getName()+" instead");
		return;
	}

	cbit.vcell.modeldb.ReferenceQuerySpec rqs = new cbit.vcell.modeldb.ReferenceQuerySpec(VersionableType.Geometry,selectedDocument.getVersion().getVersionKey());
	try{
		cbit.vcell.modeldb.ReferenceQueryResult rqr = getRequestManager().getDocumentManager().findReferences(rqs);
		//cbit.vcell.modeldb.VersionableTypeVersion[] children = (rqr.getVersionableFamily().bChildren()?rqr.getVersionableFamily().getUniqueChildren():null);
		cbit.vcell.modeldb.VersionableTypeVersion[] dependants = (rqr.getVersionableFamily().bDependants()?rqr.getVersionableFamily().getUniqueDependants():null);
		//System.out.println("\n");
		//if(children != null){
			//for(int i=0;i<children.length;i+= 1){
				//if( children[i] != rqr.getVersionableFamily().getTarget()){
					//System.out.println("Children "+children[i]+" key="+children[i].getVersion().getVersionKey()+" date="+children[i].getVersion().getDate());
				//}
			//}
		//}else{
			//System.out.println("No Children");
		//}

		//if(dependants != null){
			//for(int i=0;i<dependants.length;i+= 1){
				//if( dependants[i] != rqr.getVersionableFamily().getTarget()){
					//System.out.println("Dependants "+dependants[i]+" key="+dependants[i].getVersion().getVersionKey()+" date="+dependants[i].getVersion().getDate());
				//}
			//}
		//}else{
			//System.out.println("No Dependants");
		//}

		//System.out.println("\nVersionableRelationships");
		//cbit.vcell.modeldb.VersionableRelationship[] vrArr = rqr.getVersionableFamily().getDependantRelationships();
		//for(int i=0;i<vrArr.length;i+= 1){
			//System.out.println(vrArr[i].from() +" -> "+vrArr[i].to());
		//}

		Hashtable choices = new Hashtable();
		if(dependants != null){
			//System.out.println("\nMajor Relationships");
			for(int i=0;i<dependants.length;i+= 1){
				boolean isBioModel = dependants[i].getVType().equals(VersionableType.BioModelMetaData);
				boolean isTop = isBioModel || dependants[i].getVType().equals(VersionableType.MathModelMetaData);
				if(isTop){
					cbit.vcell.modeldb.VersionableRelationship[] vrArr2 = rqr.getVersionableFamily().getDependantRelationships();
					for(int j=0;j<vrArr2.length;j+= 1){
						if( (vrArr2[j].from() == dependants[i]) &&
							vrArr2[j].to().getVType().equals((isBioModel?VersionableType.SimulationContext:VersionableType.MathDescription))){
								for(int k=0;k<vrArr2.length;k+= 1){
									if( (vrArr2[k].from() == vrArr2[j].to()) &&
										vrArr2[k].to().getVType().equals(VersionableType.Geometry)){
											String s = (isBioModel?"BioModel":"MathModel")+"  "+
												"\""+dependants[i].getVersion().getName()+"\"  ("+dependants[i].getVersion().getDate() +")"+
												(isBioModel?" (App=\""+vrArr2[k].from().getVersion().getName()+"\")"/*+" -> "*/:"");
												//+" Geometry="+vrArr2[k].to().getVersion().getName()+" "+vrArr2[k].to().getVersion().getDate();
											choices.put(s,dependants[i]);
											//System.out.println(s);
										}
								}
						}
					}
				}
				
			}
		}

		if(choices.size() > 0){
			Object[] listObj = choices.keySet().toArray();
			Object o = cbit.gui.DialogUtils.showListDialog(getComponent(),listObj,"Models Referencing Geometry (Select To Open) "+selectedDocument.getVersion().getName()+" "+selectedDocument.getVersion().getDate());
			if(o != null){
				cbit.vcell.modeldb.VersionableTypeVersion v = (cbit.vcell.modeldb.VersionableTypeVersion)choices.get(o);
				//System.out.println(v);
				if(v.getVType().equals(VersionableType.BioModelMetaData)){
					BioModelInfo bmi = getRequestManager().getDocumentManager().getBioModelInfo(v.getVersion().getVersionKey());
					getRequestManager().openDocument(bmi,this,true);
				}else if(v.getVType().equals(VersionableType.MathModelMetaData)){
					MathModelInfo mmi = getRequestManager().getDocumentManager().getMathModelInfo(v.getVersion().getVersionKey());
					getRequestManager().openDocument(mmi,this,true);
				}
			}
		}else{
			if(dependants == null){
				cbit.gui.DialogUtils.showInfoDialog(
					"No Model references found.\n"+
					(rqr.getVersionableFamily().getTarget().getVersion().getFlag().isArchived()?"Info: Not Deletable (key="+rqr.getVersionableFamily().getTarget().getVersion().getVersionKey()+") because legacy ARCHIVE set":""));
			}else{
				cbit.gui.DialogUtils.showInfoDialog(
					"No current Model references found.\n"+
					"Geometry has internal database references from\n"+
					"previously linked Model(s).\n"+
					"Not Deletable until database is culled (daily).");
			}
			return;
		}

		
		}catch(DataAccessException e){
		cbit.gui.DialogUtils.showErrorDialog("Error find Geometry Model references\n"+e.getClass().getName()+"\n"+e.getMessage());
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 6:45:52 PM)
 * @return javax.swing.JOptionPane
 */
private javax.swing.JOptionPane getAccessPermissionDialog() {
	return accessPermissionDialog;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:39:00 PM)
 * @return cbit.vcell.desktop.BioModelDbTreePanel
 */
private ACLEditor getAclEditor() {
	return aclEditor;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:39:00 PM)
 * @return cbit.vcell.desktop.BioModelDbTreePanel
 */
public cbit.vcell.desktop.BioModelDbTreePanel getBioModelDbTreePanel() {
	return bioModelDbTreePanel;
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 1:08:29 AM)
 * @return java.lang.String
 */
java.awt.Component getComponent() {
	return getDatabaseWindowPanel();
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 2:28:00 PM)
 * @return cbit.vcell.client.desktop.DatabaseWindowPanel
 */
public DatabaseWindowPanel getDatabaseWindowPanel() {
	return databaseWindowPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/2002 10:34:00 AM)
 */
private VCDocumentInfo[] getDocumentVersionDates(VCDocumentInfo thisDocumentInfo) throws DataAccessException {
	//
	// Get list of VCDocumentInfos in workspace
	//
    if (thisDocumentInfo==null){
	    return new VCDocumentInfo[0];
    }

    VCDocumentInfo vcDocumentInfos[] = null;
    if (thisDocumentInfo instanceof BioModelInfo) {
		vcDocumentInfos = getRequestManager().getDocumentManager().getBioModelInfos();
    } else if (thisDocumentInfo instanceof MathModelInfo) {
   		vcDocumentInfos = getRequestManager().getDocumentManager().getMathModelInfos();
    }  else if (thisDocumentInfo instanceof GeometryInfo) {
   		vcDocumentInfos = getRequestManager().getDocumentManager().getGeometryInfos();
    }

	//
	// From the list of biomodels in the workspace, get list of biomodels with the same branch ID.
	// This is the list of different versions of the same biomodel.
	//
 	Vector documentBranchList = new Vector();
 	for (int i = 0; i < vcDocumentInfos.length; i++) {
	 	VCDocumentInfo vcDocumentInfo = vcDocumentInfos[i];
	 	if (vcDocumentInfo.getVersion().getBranchID().equals(thisDocumentInfo.getVersion().getBranchID())) {
		 	documentBranchList.add(vcDocumentInfo);
	 	}
 	}

 	if (documentBranchList == null) {
	 	PopupGenerator.showErrorDialog("Error comparing BioModels : No Versions of document ");
	 	return new VCDocumentInfo[0];
 	}

 	VCDocumentInfo vcDocumentInfosInBranch[] = new VCDocumentInfo[documentBranchList.size()];
 	documentBranchList.copyInto(vcDocumentInfosInBranch);

 	//
 	// From the versions list, remove the currently selected version and return the remaining list of
 	// versions for the biomodel
 	//

 	VCDocumentInfo revisedDocInfosInBranch[] = new VCDocumentInfo[vcDocumentInfosInBranch.length-1];
 	int j=0;
 	
 	for (int i = 0; i < vcDocumentInfosInBranch.length; i++) {
		if (!thisDocumentInfo.getVersion().getDate().equals(vcDocumentInfosInBranch[i].getVersion().getDate())) {
			revisedDocInfosInBranch[j] = vcDocumentInfosInBranch[i];
			j++;
		}
 	}
			 	
	return revisedDocInfosInBranch;	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:39:00 PM)
 * @return cbit.vcell.desktop.GeometryTreePanel
 */
public cbit.vcell.desktop.GeometryTreePanel getGeometryTreePanel() {
	return geometryTreePanel;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:39:00 PM)
 * @return cbit.vcell.desktop.GeometryTreePanel
 */
private ImageBrowser getImageBrowser() {
	return imageBrowser;
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 6:45:52 PM)
 * @return javax.swing.JOptionPane
 */
private javax.swing.JOptionPane getImageSelectDialog() {
	return imageSelectDialog;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:14:42 PM)
 * @return javax.swing.JOptionPane
 */
private javax.swing.JOptionPane getImportDialog() {
	return importDialog;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 2:51:36 AM)
 * @return java.lang.String
 */
public java.lang.String getManagerID() {
	// there's only one of these...
	return ClientMDIManager.DATABASE_WINDOW_ID;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:39:00 PM)
 * @return cbit.vcell.desktop.MathModelDbTreePanel
 */
public cbit.vcell.desktop.MathModelDbTreePanel getMathModelDbTreePanel() {
	return MathModelDbTreePanel;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:14:42 PM)
 * @return javax.swing.JOptionPane
 */
private javax.swing.JOptionPane getOpenDialog() {
	return openDialog;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 3:01:47 PM)
 * @return cbit.vcell.document.VCDocumentInfo
 */
public VCDocumentInfo getPanelSelection() {
	return getDatabaseWindowPanel().getSelectedDocumentInfo();
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 12:46:54 AM)
 * @return javax.swing.JOptionPane
 */
private javax.swing.JOptionPane getSaveDialog() {
	saveDialog.setWantsInput(true);
	return saveDialog;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:28:23 PM)
 */
public void initializeAll() {
	try {
		DocumentManager documentManager = getRequestManager().getDocumentManager();
		getBioModelDbTreePanel().setDocumentManager(documentManager);
		getMathModelDbTreePanel().setDocumentManager(documentManager);
		getGeometryTreePanel().setDocumentManager(documentManager);
		getDatabaseWindowPanel().setDocumentManager(documentManager);
		getImageBrowser().getImageDbTreePanel1().setDocumentManager(documentManager);
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:35:55 PM)
 */
public boolean isOwnerUserEqual()  {
	User currentUser = getRequestManager().getDocumentManager().getUser();
	User selectedDocOwner = null;
	if (getPanelSelection() != null) {
		selectedDocOwner = getPanelSelection().getVersion().getOwner();
	}

	// Check if the current user is the owner of current selection in database panel.
	// If so, return true to enable the edit and access permissions menu items on Database window.
	// (these buttons should be disabled if user isn't owner of current selection on database window).
	
	if (Compare.isEqual(currentUser,selectedDocOwner)) {
		return true;
	} else {
		return false;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 3:31:47 PM)
 * @return boolean
 */
public boolean isRecyclable() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 9:49:06 PM)
 */
public void openLatest() {

	VCDocumentInfo latestDocumentInfo = null;
	if (getPanelSelection() != null) {
		VCDocumentInfo thisDocumentInfo = getPanelSelection();
		//
		// Get the latest version of the documentInfo
		//
		VCDocumentInfo[] documentVersionsList = null;
		try {
			documentVersionsList = getDocumentVersionDates(thisDocumentInfo);
		} catch (DataAccessException e) {
			PopupGenerator.showErrorDialog("Error accessing document!");
		}
		
		//
		// Obtaining the latest version of the current documentInfo
		//
		if (documentVersionsList != null && documentVersionsList.length > 0) {
			latestDocumentInfo = documentVersionsList[documentVersionsList.length-1];

			for (int i = 0; i < documentVersionsList.length; i++) {
				if (documentVersionsList[i].getVersion().getDate().after(latestDocumentInfo.getVersion().getDate())) {
					latestDocumentInfo = documentVersionsList[i];
				}
			}

			if (thisDocumentInfo.getVersion().getDate().after(latestDocumentInfo.getVersion().getDate())) {
				latestDocumentInfo = thisDocumentInfo;
			}
		} else {
			latestDocumentInfo = thisDocumentInfo;
		}
	} else {
		PopupGenerator.showErrorDialog("Error Opening Latest Document : no document currently selected.");
		return;
	}	
	getRequestManager().openDocument(latestDocumentInfo, this, true);
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 9:49:06 PM)
 */
public void openSelected() {
	getRequestManager().openDocument(getPanelSelection(), this, true);
}


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 8:54:11 AM)
 */
public void publish() {
	
	getRequestManager().curateDocument(getPanelSelection(),CurateSpec.PUBLISH,this);	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:35:55 PM)
 */
public VCDocumentInfo selectDocument(int documentType, TopLevelWindowManager requester) throws UserCancelException, FileNotFoundException, IOException {

	// Set doubleClickValue to null.
	// if doubleClickValue is not null when dialog returns, use doubleClickValue value
	// otherwise use dialog return value
	doubleClickValue = null;
	switch (documentType) {
		case VCDocument.BIOMODEL_DOC: {
			Object choice = showOpenDialog(getBioModelDbTreePanel(), requester);
			if (doubleClickValue == null && choice != null && choice.equals("Open")) {
				return (BioModelInfo)getBioModelDbTreePanel().getSelectedVersionInfo();
			}else if (doubleClickValue != null){
				return (BioModelInfo)doubleClickValue;
			}
			throw UserCancelException.CANCEL_DB_SELECTION;
		} 
		case VCDocument.MATHMODEL_DOC: {
			Object choice = showOpenDialog(getMathModelDbTreePanel(), requester);
			if (doubleClickValue == null && choice != null && choice.equals("Open")) {
				return (MathModelInfo)getMathModelDbTreePanel().getSelectedVersionInfo();
			}else if (doubleClickValue != null){
				return (MathModelInfo)doubleClickValue;
			}
			throw UserCancelException.CANCEL_DB_SELECTION;
		} 
		case VCDocument.GEOMETRY_DOC: {
			Object choice = showOpenDialog(getGeometryTreePanel(), requester);
			if (doubleClickValue == null && choice != null && choice.equals("Open")) {
				return (GeometryInfo)getGeometryTreePanel().getSelectedVersionInfo();
			}else if (doubleClickValue != null){
				return (GeometryInfo)doubleClickValue;
			}
			throw UserCancelException.CANCEL_DB_SELECTION;
		}
		case VCDocument.XML_DOC: {
			// Get XML FIle, read the chars into a stringBuffer and create new XMLInfo.
			File xmlFile = showFileChooserDialog(true);
			char[] array = new char[1000];
			StringBuffer xmlString = new StringBuffer();
			int n;
			BufferedReader reader = new BufferedReader( new FileReader(xmlFile) );
			do {
				n = reader.read(array);
				xmlString.append(array,0,n);
			} while (n==1000);
			reader.close();
			return new cbit.vcell.xml.XMLInfo(xmlString.toString());
		}		
		default: {
			throw new RuntimeException("ERROR: Unknown document type: " + documentType);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:35:55 PM)
 */
public VCImage selectImageFromDatabase() throws DataAccessException, UserCancelException {
	Geometry newGeometry = null;
	Object choice = showImageSelectorDialog(getImageBrowser(), null);
	if (choice != null && choice.equals("OK")) {
		if (getImageBrowser().getImageDbTreePanel1().getSelectedVersionInfo() != null) {
			return getRequestManager().getDocumentManager().getImage((VCImageInfo)getImageBrowser().getImageDbTreePanel1().getSelectedVersionInfo());
		} 
	}
	throw UserCancelException.CANCEL_DB_SELECTION;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:35:55 PM)
 */
public VCImage selectImageFromFile(AsynchProgressPopup pp)
			throws UserCancelException, cbit.image.ImageException, FileNotFoundException,IOException,DataAccessException{

	VCImage vcImage = null;
	
	// Choose image from File
	File imageFile = showFileChooserDialog(false);
	long fileSize = imageFile.length();
	pp.setMessage("Reading file "+imageFile.getName()+" size="+fileSize);
	if(fileSize > GeometrySpec.IMAGE_SIZE_LIMIT){
		throw new cbit.image.ImageException("File "+imageFile.getName()+" size="+fileSize+"\ntoo large.  Size must be less than "+GeometrySpec.IMAGE_SIZE_LIMIT);
	}
	// Get file bytes
	byte[] fileBytes = new byte[(int)fileSize];
	FileInputStream fis = null;
	try{
		fis = new FileInputStream(imageFile);
		fis.read(fileBytes);
	}finally{
		if(fis != null){fis.close();}
	}
	// Parse bytes
	try{
		ImageHelper ih = null;
		if(imageFile.getName().toLowerCase().endsWith(".gif")){
			pp.setMessage("Parsing file "+imageFile.getName()+" size="+fileSize+" as gif");
			ih = convertGIF(fileBytes);
			
		}else if(imageFile.getName().toLowerCase().endsWith(".tif") || imageFile.getName().toLowerCase().endsWith(".tiff")){
			pp.setMessage("Parsing file "+imageFile.getName()+" size="+fileSize+" as tif");
			ih = convertTIF(fileBytes);
				
		}else if(	imageFile.getName().toLowerCase().endsWith(".zip")){
			pp.setMessage("Parsing file "+imageFile.getName()+" size="+fileSize+" as zip");
			ih = convertZIP(fileBytes,pp);
		}else{
			throw new Exception("File name "+imageFile.getName()+" not recognized, must end with .gif,.tif,.tiff -or- zip containing gif,tif for each Z-slice");
		}

		vcImage = new cbit.image.VCImageUncompressed(null,ih.imageData,new Extent(ih.xsize,ih.ysize,ih.zsize),ih.xsize,ih.ysize,ih.zsize);
		
		pp.setMessage("Finished loading "+imageFile.getName()+" ("+vcImage.getNumX()+","+vcImage.getNumY()+","+vcImage.getNumZ()+")");
	}catch(Throwable e){
		throw new cbit.image.ImageException("Error loading image "+imageFile.getAbsolutePath()+"\n"+(e.getMessage() == null?e.getClass().getName():e.getMessage()));
	}

	if(vcImage.getPixelClasses().length > PIXEL_CLASS_WARNING_LIMIT){
		PopupGenerator.showInfoDialog(
					"Warning: IMAGE "+imageFile.getName()+" has "+vcImage.getPixelClasses().length+
					" distinct values, all will be assigned regions.\n"+
					"If this is unexpected, process the IMAGE to remove noise or unwanted values and re-load");
	}
    //Edit image and save , 
	return editImageAttributes(vcImage,pp);
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:35:55 PM)
 */
public MathModel selectMathFromBio() throws UserCancelException {

	MathModel newMathModel = new MathModel(null);
	Object choice = showOpenDialog(getBioModelDbTreePanel(), this);

	// Get Biomodelinfo from bioMOdelDBTreePanel
	BioModelInfo bioModelInfo = null;
	if (choice != null && choice.equals("Open")) {
		bioModelInfo = (BioModelInfo)getBioModelDbTreePanel().getSelectedVersionInfo();
	} else {
		throw UserCancelException.CANCEL_DB_SELECTION;
	}

	try {
		// Get the simContexts in the corresponding BioModel 
		SimulationContext[] simContexts = getRequestManager().getDocumentManager().getBioModel(bioModelInfo).getSimulationContexts();

		String[] simContextNames = new String[simContexts.length];
		if (simContextNames.length > 0) {
			for (int i = 0; i < simContexts.length; i++){
				simContextNames[i] = simContexts[i].getName();
			}
			// Get the simContext names, so that user can choose which simContext math to import
			String simContextChoice = (String)PopupGenerator.showListDialog(this, simContextNames, "Please select Application");
			if (simContextChoice == null) {
				PopupGenerator.showErrorDialog("User cancelled application selection");
				return null;
			}
			SimulationContext chosenSimContext = null;
			for (int i = 0; i < simContexts.length; i++){
				if (simContexts[i].getName().equals(simContextChoice)) {
					chosenSimContext = simContexts[i];
					break;
				}
			}
			//Get corresponding mathDesc to create new mathModel and return.
			String newName = bioModelInfo.getVersion().getName()+"_"+chosenSimContext.getName();
			MathDescription bioMathDesc = chosenSimContext.getMathDescription();
			MathDescription newMathDesc = null;
			newMathDesc = new MathDescription(newName+"_"+(new java.util.Random()).nextInt());
			try {
				if (bioMathDesc.getGeometry().getDimension()>0 && bioMathDesc.getGeometry().getGeometrySurfaceDescription().getGeometricRegions() == null){
					bioMathDesc.getGeometry().getGeometrySurfaceDescription().updateAll();
				}
			}catch (cbit.image.ImageException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("Geometric surface generation error:\n"+e.getMessage());
			}catch (cbit.vcell.geometry.GeometryException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("Geometric surface generation error:\n"+e.getMessage());
			}
			newMathDesc.setGeometry(bioMathDesc.getGeometry());
			newMathDesc.read_database(new CommentStringTokenizer(bioMathDesc.getVCML_database()));
			newMathDesc.isValid();

			newMathModel.setName(newName);
			newMathModel.setMathDescription(newMathDesc);		
		}
	} catch (Exception e) {
		e.printStackTrace(System.out);
	}

	return newMathModel;
}			

/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 2:28:00 PM)
 * @param newDatabaseWindowPanel cbit.vcell.client.desktop.DatabaseWindowPanel
 */
private void setDatabaseWindowPanel(DatabaseWindowPanel newDatabaseWindowPanel) {
	databaseWindowPanel = newDatabaseWindowPanel;
}


/**
 * Comment
 */
public void setLatestOnly(boolean latestOnly) {
	getDatabaseWindowPanel().setLatestOnly(latestOnly);
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
private Object showAccessPermissionDialog(JComponent aclEditor, Component requester) {

	aclEditor.setPreferredSize(new java.awt.Dimension(300, 400));
	getAccessPermissionDialog().setMessage("");
	getAccessPermissionDialog().setMessage(aclEditor);
	getAccessPermissionDialog().setValue(null);
	JDialog d = getAccessPermissionDialog().createDialog(requester, "Changing Permissions:");
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	cbit.gui.ZEnforcer.showModalDialogOnTop(d,requester);
	return getAccessPermissionDialog().getValue();
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
private File showFileChooserDialog(boolean isXMLNotImage) throws UserCancelException {
	// the boolean isXMLNotImage is true if we are trying to choose an XML file
	// It is false if we are trying to choose an image file
	// This is used to set the appropriate File filters.

	String defaultPath = getUserPreferences().getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
	cbit.gui.VCFileChooser fileChooser = new cbit.gui.VCFileChooser(defaultPath);
	fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);

	if (isXMLNotImage) {
		// setting fileFilter for xml files
		fileChooser.setFileFilter(FileFilters.FILE_FILTER_XML);
	} else {
		// File filter for images.
		FileFilter filter = new FileFilter() {
			public String getDescription() {
				return "image (.gif .tif .tiff .zip)";
			}
			public boolean accept(java.io.File file) {
				return accept(file.getParentFile(), file.getName());
			}
			public boolean accept(java.io.File dir, String name) {
				if (dir != null && name != null) {
					java.io.File file = new java.io.File(dir, name);
					if (file.isDirectory()) {
						return true;
					} else if (name.toLowerCase().endsWith(".gif") ||
						name.toLowerCase().endsWith(".tif") ||
						name.toLowerCase().endsWith(".tiff") ||
						name.toLowerCase().endsWith(".zip")){
						return true;
					} else{
						return false;
					}
				} else {
					return false;
				}
			}
		};
		fileChooser.setFileFilter(filter);
	}
	
    int returnval = fileChooser.showOpenDialog(null);
    if (returnval == fileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        //reset the user preference for the default path, if needed.
        String newPath = selectedFile.getParent();
        if (!newPath.equals(defaultPath)) {
			getUserPreferences().setGenPref(UserPreferences.GENERAL_LAST_PATH_USED, newPath);
        }
        System.out.println("New preferred file path: " + newPath + ", Old preferred file path: " + defaultPath);
        return selectedFile;
    }else{ // user didn't select a file
	    throw UserCancelException.CANCEL_FILE_SELECTION;
    }
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
private Object showImagePropertiesDialog() {

	// Cannot use JOptionPane because it will not allow some children to resize
	JDialog d = new JDialog();
	d.setModal(true);
	d.getContentPane().add(imageAttributePanel);
	imageAttributePanel.setDialogParent(d);
	d.setSize(400,600);
	d.setLocation(300,200);
	BeanUtils.centerOnComponent(d,null);
	cbit.gui.ZEnforcer.showModalDialogOnTop(d,null);
	return imageAttributePanel.getStatus();
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
private Object showImageSelectorDialog(JComponent imageBrowser, Component requester) {
	imageBrowser.setPreferredSize(new java.awt.Dimension(200, 400));
	getImageSelectDialog().setMessage("");
	getImageSelectDialog().setMessage(imageBrowser);
	JDialog d = getImageSelectDialog().createDialog(requester, "Select Image:");
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	cbit.gui.ZEnforcer.showModalDialogOnTop(d,requester);
	return getImageSelectDialog().getValue();
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
private Object showOpenDialog(JComponent tree, TopLevelWindowManager requester) {
	tree.setPreferredSize(new java.awt.Dimension(300, 600));
	getOpenDialog().setMessage("");
	getOpenDialog().setMessage(tree);
	theJDialog = getOpenDialog().createDialog(requester.getComponent(), "Select document:");
	theJDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	cbit.gui.ZEnforcer.showModalDialogOnTop(theJDialog,requester.getComponent());
	return getOpenDialog().getValue();
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
public String showSaveDialog(int documentType, Component requester) throws UserCancelException {
	JPanel panel = new JPanel(new BorderLayout());
	JComponent tree = null;
	switch (documentType) {
		case VCDocument.BIOMODEL_DOC: {
			tree = getBioModelDbTreePanel();
			break;
		}
		case VCDocument.MATHMODEL_DOC: {
			tree = getMathModelDbTreePanel();
			break;
		}
		case VCDocument.GEOMETRY_DOC: {
			tree = getGeometryTreePanel();
			break;
		}
		default: {
			throw new RuntimeException("DatabaseWindowManager.showSaveDialog() - unknown document type");
		}
	}
	tree.setPreferredSize(new java.awt.Dimension(300, 600));
	panel.add(tree, BorderLayout.CENTER);
	panel.add(new JLabel("Please type a new name:"), BorderLayout.SOUTH);
	getSaveDialog().setMessage("");
	getSaveDialog().setMessage(panel);
	JDialog d = getSaveDialog().createDialog(requester, "Save document:");
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	cbit.gui.ZEnforcer.showModalDialogOnTop(d,requester);
	if ("Save".equals(getSaveDialog().getValue())) {
		return getSaveDialog().getInputValue() == null ? null : getSaveDialog().getInputValue().toString();
	} else {
		// user cancelled
		throw UserCancelException.CANCEL_NEW_NAME;
	}
}
}