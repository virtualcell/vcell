package org.vcell.client.logicalwindow;

/**
 * extension characterstics of logical Windows
 */
public class LWTraits {
	public enum InitialPosition {
		/**
		 * managed by AWT / Swing calls
		 */
		NOT_LW_MANAGED,
		/**
		 * slightly down and to the right
		 */
		STAGGERED_ON_PARENT,
		CENTERED_ON_PARENT
	}
	
	private final InitialPosition initialPosition;

	public LWTraits(InitialPosition initialPosition) {
		this.initialPosition = initialPosition;
	}

	public InitialPosition getInitialPosition() {
		return initialPosition;
	}
	
}
