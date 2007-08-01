/**
 * 
 */
package cbit.vcell.xml.sbml_transform;

/**
 * @author mlevin
 *
 */
public class SbmlTransformException extends RuntimeException {
	private static final long serialVersionUID = -1389435452311195040L;

	public SbmlTransformException() {
		super();
	}

	public SbmlTransformException(String message, Throwable cause) {
		super(message, cause);
	}

	public SbmlTransformException(String message) {
		super(message);
	}

	public SbmlTransformException(Throwable cause) {
		super(cause);
	}

	public String getMessage() {
		String msg1 = super.getMessage();
		Throwable parent = this;
		Throwable child;
		
		while( (child = parent.getCause()) != null ) {
			String msg2 = child.getMessage();
			if( msg2 != null ) {
				if( msg1 != null ) msg1 += ": " + msg2;
				else msg1 = msg2;
			}
			
			if( child instanceof SbmlTransformException ) break;
			parent = child;
		}
		return msg1;
	}

}
