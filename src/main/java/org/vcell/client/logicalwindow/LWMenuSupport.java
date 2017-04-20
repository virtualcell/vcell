package org.vcell.client.logicalwindow;

import org.vcell.util.ProgrammingException;

/**
 * mix-in support for using window titles as menuDescriptions
 */
public class LWMenuSupport {
	final LWFrameOrDialog client;

	/**
	 * @param client not null
	 * @param title not null
	 * @throws IllegalArgumentException if null
	 */
	public LWMenuSupport(LWFrameOrDialog client, String title) {
		super();
		this.client = client;
		setTitle(title);
	}
	
	/**
	 * @param title not null
	 * @throws IllegalArgumentException if null
	 */
	public void setTitle(String title) {
		if (title == null) { 
			throw new IllegalArgumentException("title may not be null");
		}
	}
	
	/**
	 * @return title as menu description
	 */
	public String menuDescription( ) {
		String t =  client.getTitle(); 
		if (t != null) {
			return t;
		}
		throw new ProgrammingException("title is null");
	}

}
