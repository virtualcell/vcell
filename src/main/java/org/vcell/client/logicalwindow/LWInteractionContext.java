package org.vcell.client.logicalwindow;

/**
 * graphical implementation of {@link InteractionContext}
 */
public class LWInteractionContext implements InteractionContext {
	private final LWContainerHandle parent;

	public LWInteractionContext(LWContainerHandle parent) {
		super();
		this.parent = parent;
	}


	@Override
	public Response query(String title, String question) {
		LWOkCancelDialog okc = new LWOkCancelDialog(parent, title,question);
		okc.setVisible(true);
		if (okc.isSaidYes()) {
			return Response.YES;
		}
		return Response.NO;
	}

}
