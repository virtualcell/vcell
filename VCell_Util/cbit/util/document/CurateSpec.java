package cbit.util.document;
/**
 * Insert the type's description here.
 * Creation date: (5/23/2006 9:32:49 AM)
 * @author: Frank Morgan
 */
public class CurateSpec implements java.io.Serializable{

	public static final int ARCHIVE = 0;
	public static final int PUBLISH = 1;

	public static final String[] CURATE_TYPE_NAMES = {"ARCHIVE","PUBLISH"};
	public static final String[] CURATE_TYPE_ACTIONS = {"ARCHIVING","PUBLISHING"};
	public static final String[] CURATE_TYPE_STATES = {"ARCHIVED","PUBLISHED"};
	
	private int curateType;
	private VCDocumentInfo vcDocumentInfo;


/**
 * CurateSpec constructor comment.
 */
public CurateSpec(VCDocumentInfo argVCDocumentInfo,int argCurateType) {
	curateType = argCurateType;
	vcDocumentInfo = argVCDocumentInfo;
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 9:37:10 AM)
 * @return int
 */
public int getCurateType() {
	return curateType;
}


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 10:19:10 AM)
 * @return cbit.vcell.document.VCDocumentInfo
 */
public VCDocumentInfo getVCDocumentInfo() {
	return vcDocumentInfo;
}
}