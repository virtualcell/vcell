package cbit.util.xml;


/**
 * First draft for a multipurpose logger. Implementors may also support user interaction. 
 * Creation date: (9/24/2004 12:03:08 PM)
 * @author: Rashad Badrawi
 */
public abstract class VCLogger {

	//importance of messages from the translator, decides whether to bail out, interrupt/request feedback, or just report.
	public static final int LOW_PRIORITY = 0;
	public static final int MEDIUM_PRIORITY = 1;
	public static final int HIGH_PRIORITY = 2;
	public static final int SCHEMA_VALIDATION_ERROR = 0;
	public static final int UNSUPPORED_ELEMENTS_OR_ATTS = 1;
	public static final int COMPARTMENT_ERROR = 2;           //useful for SBML imports
	public static final int UNIT_ERROR = 3;
	public static final int SPECIES_ERROR = 4;
	public static final int REACTION_ERROR = 5;

	public abstract boolean hasMessages();


	public abstract void sendAllMessages();


	public abstract void sendMessage(int messageLevel, int messageType) throws Exception;


	public abstract void sendMessage(int messageLevel, int messageType, String message) throws Exception;


	public static String getDefaultMessage(int messageType) {
	
		if (!isValidMessageType(messageType)) {
			throw new IllegalArgumentException("Unknown translation message type: " + messageType);
		}
		String tempStr;
		
		switch (messageType) {
			case VCLogger.SCHEMA_VALIDATION_ERROR:
				tempStr = "The source document has invalid elements and/or attributes";
				break;
			case VCLogger.UNSUPPORED_ELEMENTS_OR_ATTS:
				tempStr = ".The source document has elements and/or attributes that cannot be translated";
				break;
			case VCLogger.COMPARTMENT_ERROR:
				tempStr = "Problem in the compartments section";
				break;
			case VCLogger.UNIT_ERROR:
				tempStr = "Problem in the unit translation";
				break;
			case VCLogger.SPECIES_ERROR:
				tempStr = "Problem in the species section";
				break;
			case VCLogger.REACTION_ERROR:
				tempStr = "Problem in the reactions section";
				break;
			default:
				tempStr = "";
		}
	
		return tempStr;
	}


	public static boolean isValidMessageType(int messageType) {
	
		if (messageType == VCLogger.SCHEMA_VALIDATION_ERROR ||
			messageType == VCLogger.UNSUPPORED_ELEMENTS_OR_ATTS ||
			messageType == VCLogger.COMPARTMENT_ERROR ||
			messageType == VCLogger.UNIT_ERROR ||
			messageType == VCLogger.SPECIES_ERROR ||
			messageType == VCLogger.REACTION_ERROR) {
			return true;
		}
		return false;
	}
}