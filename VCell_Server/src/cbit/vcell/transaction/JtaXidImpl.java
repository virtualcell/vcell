package cbit.vcell.transaction;

import java.io.Serializable;

import javax.transaction.xa.Xid;

/**
 * Xid implementation for JTA
 */
public class JtaXidImpl implements Xid, Serializable {

    private static final int FORMAT_ID = 0;    
    
    /**
     * Branch qualifier.
     */
    private byte[] branchQualifier; 

    
    
    /**
     * Global transaction id.
     */
    private byte[] globalTransactionId;
    
    
/**
 * Constructor.
 */
public JtaXidImpl(byte[] globalTransactionId0, byte[] branchQualifier0) {
	setGlobalTransactionId(globalTransactionId0);
	setBranchQualifier(branchQualifier0);
}
/**
 * Obtain the transaction branch identifier part of XID as an array of 
 * bytes.
 * 
 * @return Global transaction identifier.
 */
public byte[] getBranchQualifier() {
	return branchQualifier;
}
/**
 * The formatID is usually zero, meaning that you are using the 
 * OSI CCR (Open Systems Interconnection Commitment, Concurrency, and Recovery standard) 
 * for naming. If you are using another format, the formatID should be greater than zero. 
 * A value of -1 means that the Xid is null.
 * 
 * Obtain the format identifier part of the XID.
 * 
 * @return Format identifier. O means the OSI CCR format.
 */
public int getFormatId() {
	return FORMAT_ID;
}
/**
 * Obtain the global transaction identifier part of XID as an array of 
 * bytes.
 * 
 * @return Global transaction identifier.
 */
public byte[] getGlobalTransactionId() {
	return globalTransactionId;
}
/**
 * Create a new branch based on this Xid.
 */
public Xid newBranch(int branchNumber) {
	return new JtaXidImpl(getGlobalTransactionId(), Integer.toString(branchNumber).getBytes());
}
/**
 * Insert the method's description here.
 * Creation date: (1/27/2004 1:26:39 PM)
 * @param newBranchQualifier byte[]
 */
private void setBranchQualifier(byte[] aBranchQualifier) {
	int length = 0;
	if (aBranchQualifier == null) {
		length = 0;
		branchQualifier = new byte[length];
	} else {
		length = aBranchQualifier.length <= 64 ? aBranchQualifier.length : 64;
		branchQualifier = new byte[length];
		System.arraycopy(aBranchQualifier, 0, branchQualifier, 0, length);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/27/2004 1:26:39 PM)
 * @param newGlobalTransactionId byte[]
 */
private void setGlobalTransactionId(byte[] aGlobalTransactionId) {
	int length = 0;
	if (aGlobalTransactionId == null) {
		length = 0;
		globalTransactionId = new byte[length];
	} else {
		length = aGlobalTransactionId.length <= 64 ? aGlobalTransactionId.length : 64;
		globalTransactionId = new byte[length];
		System.arraycopy(aGlobalTransactionId, 0, globalTransactionId, 0, length);
	}
}
/**
 * Obtain a String representation of this xid.
 */
public String toString() {
	return "[" + new String(getGlobalTransactionId()) + "," + new String(getBranchQualifier()) + "]";
}
}
