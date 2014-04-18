package org.vcell.util;


/**
 * utility for {@link java.lang.Serializable}
 * @author gweatherby
 *
 */
public class Serial {
	private static final char REV_SEP = ':';
	private static final char REV_TERM = '$';

	/**
	 * static methods only
	 */
	private Serial() { }
	
	/**
	 * convert SVN $Revision keyword into serial number.
	 * svn::keywords property for "Revision" should be set for the code that calls this
	 * @param revision
	 * @return just numeric part of java serialization
	 * @throws: {@link IllegalArgumentException}, {@link NumberFormatException}, {@link NullPointerException}
	 */
	public static long serialFromSVNRevision(String revision) {
		int colonIndex = revision.indexOf(REV_SEP);
		int termIndex = revision.indexOf(REV_TERM,colonIndex);
		if (colonIndex > 0 && termIndex > 0) {
			String numberPart = revision.substring(colonIndex + 1,termIndex).trim( );
			return Long.parseLong(numberPart);
		}
		throw new IllegalArgumentException("Revision string " + revision + " does not contain " + REV_SEP + " and/or " + REV_TERM);
	}
	

}
