/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.util.xml;


/**
 * First draft for a multipurpose logger. Implementors may also support user interaction. 
 * Creation date: (9/24/2004 12:03:08 PM)
 * @author: Rashad Badrawi
 */
public abstract class VCLogger {

	//importance of messages from the translator, decides whether to bail out, interrupt/request feedback, or just report.
	private static final int LOW_PRIORITY = 0;
	private static final int MEDIUM_PRIORITY = 1;
	private static final int HIGH_PRIORITY = 2;
	
	private static final int SCHEMA_VALIDATION_ERROR = 0;
	private static final int UNSUPPORED_ELEMENTS_OR_ATTS = 1;	//useful for SBML imports
	private static final int COMPARTMENT_ERROR = 2;           
	private static final int UNIT_ERROR = 3;
	private static final int SPECIES_ERROR = 4;
	private static final int REACTION_ERROR = 5;
	private static final int OVERALL_WARNINGS = 6;

	public enum Priority {
		LowPriority(LOW_PRIORITY),
		MediumPriority(MEDIUM_PRIORITY),
		HighPriority(HIGH_PRIORITY);

		Priority(int constant) {
			eCheck(this,constant);
		}
		
		/**
		 * convert ordinal to priority
		 * @param p
		 * @return {@link #LowPriority} if p invalid
		 */
		public static Priority fromOrdinal(int p) {
			if (p >= 0 && p < arr.length) {
				return arr[p];
			}
			return LowPriority;
		}
		
		private static Priority[] arr = Priority.values( ) ;
		
	}

	public enum ErrorType {
		SchemaValidation(SCHEMA_VALIDATION_ERROR,"The source document has invalid elements and/or attributes" ),
		UnsupportedConstruct(UNSUPPORED_ELEMENTS_OR_ATTS,"The source document has elements and/or attributes that cannot be translated" ),
		CompartmentError(COMPARTMENT_ERROR, "Problem in the compartments section"),
		UnitError(UNIT_ERROR , "Problem in the unit translation"),
		SpeciesError(SPECIES_ERROR, "Problem in the species section"),
		ReactionError(REACTION_ERROR, "Problem in the reactions section"),
		OverallWarning(OVERALL_WARNINGS,"Warnings in the Import process" );
		ErrorType(int constant,String message) {
			eCheck(this,constant);
			this.message = message;
		}
		
		public final String message;
		
		public static boolean isValidOrdinal(int messageType) {
			return (messageType >= 0 && messageType < errorTypes.length);
		}
		
		/**
		 * @param m
		 * @return {@link #OverallWarning} if m invalid
		 */
		/*
		public static ErrorType fromOrdinal(int m)  {
			if (isValidOrdinal(m)) {
				return errorTypes[m];
			}
			return OverallWarning;
		}
		*/
		
		/**
		 * cache for faster access
		 */
		private static final ErrorType[] errorTypes = ErrorType.values();

	}
	
	private static void eCheck(Enum<?> e, int i) {
		if (e.ordinal() != i) {
			throw new IllegalStateException(e.name() + " not initialized to " + i);
		}
	}
	
	public abstract boolean hasMessages();


	public abstract void sendAllMessages();

	public void sendMessage(Priority p, ErrorType et) throws VCLoggerException {
		sendMessage(p,et,et.message);
		
	}
	
	public abstract void sendMessage(Priority p, ErrorType et, String message) throws VCLoggerException; 


	/*
	public static String getDefaultMessage(int messageType) {
		return ErrorType.fromOrdinal(messageType).message;
	}
	*/

	/**
	 * prefer {@link ErrorType#isValidOrdinal(int)}
	 */
    @Deprecated
	public static boolean isValidMessageType(int messageType) {
		return ErrorType.isValidOrdinal(messageType);
	}
}
