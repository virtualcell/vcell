package org.vcell.util;

/**
 * exception indicating programming error
 * @author gweatherby
 *
 */
@SuppressWarnings("serial")
public class ProgrammingException extends RuntimeException {

	public ProgrammingException() {
		super("Programming Exception");
	}

	public ProgrammingException(String paramString) {
		super(paramString);
	}

	public ProgrammingException(Throwable paramThrowable) {
		super(paramThrowable);
	}

	public ProgrammingException(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
	}

	public ProgrammingException(String paramString, Throwable paramThrowable,
			boolean paramBoolean1, boolean paramBoolean2) {
		super(paramString, paramThrowable, paramBoolean1, paramBoolean2);
	}
}
