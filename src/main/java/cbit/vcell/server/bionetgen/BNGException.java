package cbit.vcell.server.bionetgen;

/**
 * BNG specific {@link RuntimeException}
 * @author gweatherby
 *
 */
@SuppressWarnings("serial")
public class BNGException extends RuntimeException {

	public BNGException() {
		super();
	}

	public BNGException(String message, Throwable cause) {
		super(message, cause);
	}

	public BNGException(String message) {
		super(message);
	}

}
