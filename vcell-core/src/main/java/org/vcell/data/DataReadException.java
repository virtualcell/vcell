package org.vcell.data;

/**
 * Thrown when a {@link ValueSource} cannot produce its values. Unchecked so lazy sources can be read through
 * streams and views without every caller declaring the I/O failures of every possible source.
 */
public class DataReadException extends RuntimeException {
	public DataReadException(String message) {
		super(message);
	}

	public DataReadException(String message, Throwable cause) {
		super(message, cause);
	}
}
