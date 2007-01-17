package cbit.vcell.math;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (4/18/2001 11:10:00 AM)
 * @author: Jim Schaff
 */
public class Token {
	private String value = null;
	private int startColIndex = -1;
	private int endColIndex = -1;
	private int lineIndex = -1;
/**
 * Token constructor comment.
 */
public Token(String argTokenString, int argStartColIndex, int argLineIndex) {
	if (argTokenString==null || argTokenString.length()==0){
		throw new IllegalArgumentException("tokenString is null or zero length");
	}
	this.value = argTokenString;
	this.startColIndex = argStartColIndex;
	this.endColIndex = argStartColIndex+value.length()-1;
	this.lineIndex = argLineIndex;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 2:18:22 PM)
 * @return int
 */
public int getEndColumnIndex() {
	return endColIndex;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 2:18:22 PM)
 * @return int
 */
public int getLineIndex() {
	return lineIndex;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 2:18:22 PM)
 * @return int
 */
public int getStartColumnIndex() {
	return startColIndex;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 11:38:59 AM)
 * @return java.lang.String
 */
public String getValue() {
	return value;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 11:32:16 AM)
 * @param token cbit.vcell.math.Token
 */
public void join(Token token) {
	if (this.lineIndex != token.lineIndex){
		throw new IllegalArgumentException("can't join tokens from different lines, "+toString()+", "+token.toString());
	}
	if (this.endColIndex != token.startColIndex-1){
		throw new IllegalArgumentException("can't join tokens that aren't contiguous, "+toString()+", "+token.toString());
	}
	this.value += token.value;
	this.endColIndex = token.endColIndex;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 11:15:07 AM)
 * @return java.lang.String
 */
public String toString() {
	return "Token('"+value+"', line="+lineIndex+", col="+startColIndex+")";
}
}
