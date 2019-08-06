package org.vcell.sbml.vcell;

import cbit.vcell.model.ModelPropertyVetoException;

/**
 * provide detail about import issues for classification
 * @author gweatherby
 */
@SuppressWarnings("serial")
public class SBMLImportException extends RuntimeException {
	public enum Category {
		UNSPECIFIED,
		INCONSISTENT_UNIT,
		/**
		 * reserved symbol or variable
		 */
		RESERVED_SPECIES,
		/**
		 * equation and rate rule
		 */
		RATE_RULE, 
		NON_INTEGER_STOICH, 
		/**
		 * in assignment rule, generally
		 */
		RESERVED_SPATIAL, 
		/**
		 * SBML delay element not supported
		 */
		DELAY;
	}
	
	final public Category category;

	public SBMLImportException(String message, Category cat) {
		super(message);
		category = cat; 
	}

	/**
	 * @param message
	 * @param cat
	 * @param cause if {@link SBMLImportException}, copy {@link #category} from
	 */
	public SBMLImportException(String message, Category cat, Throwable cause) {
		super(message, cause);
		if (cat == Category.UNSPECIFIED && cause instanceof SBMLImportException) {
			SBMLImportException scause = (SBMLImportException) cause;
			category = scause.category;
		}
		else {
			category = cat; 
		}
	}
	
	public SBMLImportException(String message) {
		this(message,Category.UNSPECIFIED);
	}

	public SBMLImportException(String message, Throwable cause) {
		this(message,Category.UNSPECIFIED,cause);
	}
	
	public SBMLImportException(String message, ModelPropertyVetoException  mpve) {
		this(message,adapt(mpve),mpve);
	}
	
	/**
	 * recursively concatenate all {@link #getMessage()} from included throwables
	 * @return colon separated messages
	 */
	public String getAllCauses( ) {
		StringBuilder sb = new StringBuilder(getMessage( ).trim( ));
		Throwable t = getCause();
		while (t != null) {
			sb.append(" : ");
			sb.append( (t.getMessage()==null?t.getClass().getName():t.getMessage().trim( )));
			t = t.getCause();
		}
		
		return sb.toString();
	}
	
	/**
	 * compiler did not like switch statement assigning final variable
	 * @param mpve
	 * @return {@link Category#RESERVED_SPECIES} if {@link ModelPropertyVetoException#category} is {@link ModelPropertyVetoException.Category#RESERVED_SYMBOL}
	 * 
	 */
	private static Category adapt(ModelPropertyVetoException mpve) {
		switch (mpve.category) {
		case RESERVED_SYMBOL:
			return Category.RESERVED_SPECIES;
		default:
			return Category.UNSPECIFIED;
		}
	}
	

}
