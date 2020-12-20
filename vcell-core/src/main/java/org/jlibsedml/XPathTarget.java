package org.jlibsedml;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulates the <em>target</em> attribute of a {@link Change} element, providing
 *  additional services for XPath expression validation.
 * @author radams
 *
 */
public final  class XPathTarget {
     /**
      * Pattern acceptable for a prefix is '(/|@)(\\w[^ :/=\\?!]*):*'
      */
	 public final static Pattern XPATH_PREFIX = Pattern.compile("(/|@)(\\w[^ :/=\\?!]*):");
	 
	/**
	 * 
	 * @param xPathStr A non-null, non-empty <code>String</code>.
	 */
	public XPathTarget(String xPathStr) {
		super();
		Assert.checkNoNullArgs(xPathStr);
		Assert.stringsNotEmpty(xPathStr);
		this.xPathStr = xPathStr;
	}

	private String xPathStr;
	
	

	/**
	 * Returns a non-redundant set of XPath prefixes for this target.
	 *  The empty prefix /:element is also identified
	 *  by this method.
	 * @return A possibly empty but non-null set of prefix Strings matching 
	 * the regular expression {@link XPathTarget#XPATH_PREFIX}.
	 */
	public final Set<String> getXPathPrefixes() {
		Matcher m = XPATH_PREFIX.matcher(xPathStr);
		Set<String> prefixes = new HashSet<String>();
		while (m.find()) {
			prefixes.add(m.group(2));
		}
		return prefixes;
	}

	/**
	 * Gets this XPath target as a <code>String</code>
	 * @return A <code>String</code>
	 */
	public final String getTargetAsString() {
		return xPathStr;
	}
	
	/**
     * @see Object#toString()
     */
	public String toString(){
		return xPathStr;
	}
}
