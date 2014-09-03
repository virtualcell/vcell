package org.vcell.util;

/**
 * Mixin interface to add commenting capability to components of VCML. An interface is
 * used to allow classes with existing base classes to be of type Commented
 * 
 * Comments may occur either before or after the element in VCML. The separators are not 
 * included with the comment.
 * 
 * @author gweatherby
 *
 */
public interface Commented {
	/**
	 * beginning of before comment
	 */
	public final static String BEFORE_COMMENT = "/*";
	public final static int BEFORE_COMMENT_LENGTH = BEFORE_COMMENT.length();
	
	/**
	 * end of before comment
	 */
	public final static String END_BEFORE_COMMENT = "*/";
	public final static int END_BEFORE_COMMENT_LENGTH = END_BEFORE_COMMENT.length();
	
	/**
	 * standard pattern for after comment
	 */
	public final static String AFTER_COMMENT = "//";
	public final static int AFTER_COMMENT_LENGTH = AFTER_COMMENT.length();
	public final static int CHAR_NOT_FOUND = -1;

	/**
	 * get comment, if any
	 * @return comment or null if no comment present
	 */
	
	public String getBeforeComment( );
	/**
	 * set or clear comment
	 * @param comment new comment or null to clear comment
	 */
	
	public void setBeforeComment(String comment);
	/**
	 * get comment, if any
	 * @return comment or null if no comment present
	 */
	public String getAfterComment( );
	
	/**
	 * set or clear comment
	 * @param comment new comment or null to clear comment
	 */
	public void setAfterComment(String comment);
}
