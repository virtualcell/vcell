package org.jlibsedml;

/**
 * Class to hold parsing errors. Equality is based on line number, message string and severity.
 * @author Richard Adams
 *
 */
public final class SedMLError {
	
	/**
	 * Categorization of  the error severity. It is up to clients to decide how to handle these 
	 *  errors, in general though, these codes correspond to :
	 *  <ul>
	 * <li> <span style=font-weight:bold;>FATAL</span>: For example, if XML is invalid syntax, so that  processing can no longer continue. 
	 * <li> <span style=font-weight:bold;>ERROR</span>: A consistent SEDML model cannot be generated.
	 * <li> <span style=font-weight:bold;>WARNING</span>: A consistent SEDML model can be generated, but not all operations on the model may be possible.
	 * <li> <span style=font-weight:bold;>INFO</span>: Information for the user.
	 *  </ul>
	 * @author radams
	 *
	 */
	public enum ERROR_SEVERITY {
		INFO, WARNING, ERROR, FATAL
	};
	

	private int  lineNo;
	private ERROR_SEVERITY severity;
	private String message;
	
	/**
	 * Default no arg constructor. This is for internal use and should not be used by clients.
	 */
	public SedMLError(){}

	/**
	 * Public constructor for a SedMLError. Clients should use this constructor to create SedMLError objects.
	 * @param lineNo
	 * @param message
	 * @param severity
	 */
	public SedMLError(int lineNo, String message, ERROR_SEVERITY severity) {
		super();
		this.lineNo = lineNo;
		this.message = message;
		this.severity = severity;
	}
	
	/**
	 * Getter for  the severity of this error.
	 * @return An ERROR_SEVERITY object.
	 */
	public ERROR_SEVERITY getSeverity() {
		return severity;
	}
	
	/**
	 * Gets the line number for this error, if it can be localized.
	 * @return An <code>int</code>.
	 */
	public int getLineNo() {
		return lineNo;
	}
	
	/**
     * Gets the message for this error.
     * @return A <code>String</code>.
     */
	public String getMessage() {
		return message;
	}
	
	/**
     * @see Object#toString()
     */
	public String toString (){
		return message + " at line:" + lineNo + ",  severity:" + severity;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lineNo;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result
				+ ((severity == null) ? 0 : severity.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SedMLError other = (SedMLError) obj;
		if (lineNo != other.lineNo)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (severity == null) {
			if (other.severity != null)
				return false;
		} else if (!severity.equals(other.severity))
			return false;
		return true;
	}
	

}
