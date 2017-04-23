package org.vcell.client.logicalwindow;

public interface InteractionContext {
	public enum Response {
		YES,
		NO
	}
	
	/**
	 * prompt user / system for response to discretionary warning condition
	 * alternatively throw {@link RuntimeException} to be caught higher up stack
	 * @param title non null
	 * @param question non null
	 * @return reply (not null) 
	 */
	public Response query(String title, String question);

}
