package cbit.vcell.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

/**
 * Model specific exceptions
 * @author gweatherby
 *
 */
@SuppressWarnings("serial")
public class ModelPropertyVetoException extends PropertyVetoException {
	public enum Category {
		UNSPECIFIED,
		RESERVED_SYMBOL
	}
	
	public final Category category;

	public ModelPropertyVetoException(String mess, PropertyChangeEvent evt, Category cat) {
		super(mess, evt);
		category = cat;
		
	}
	
	public ModelPropertyVetoException(String mess, PropertyChangeEvent evt) {
		this(mess,evt,Category.UNSPECIFIED);
	}

}
