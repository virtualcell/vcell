package cbit.vcell.vcml;
/**
 A controlled vocabulary class for the types of messages generated when translating an XML document.
 * Creation date: (9/24/2004 2:17:37 PM)
 * @author: Rashad Badrawi
 */
public abstract class TranslationMessage {

	public static final int SCHEMA_VALIDATION_ERROR = 0;
	public static final int UNSUPPORED_ELEMENTS_OR_ATTS = 1;
	public static final int COMPARTMENT_ERROR = 2;           //useful for SBML imports
	public static final int UNIT_ERROR = 3;
	public static final int SPECIES_ERROR = 4;
	public static final int REACTION_ERROR = 5;

	public static String getDefaultMessage(int messageType) {

		if (!isValidMessageType(messageType)) {
			throw new IllegalArgumentException("Unknown translation message type: " + messageType);
		}
		String tempStr;
		
		switch (messageType) {
			case TranslationMessage.SCHEMA_VALIDATION_ERROR:
				tempStr = "The source document has invalid elements and/or attributes";
				break;
			case TranslationMessage.UNSUPPORED_ELEMENTS_OR_ATTS:
				tempStr = ".The source document has elements and/or attributes that cannot be translated";
				break;
			case TranslationMessage.COMPARTMENT_ERROR:
				tempStr = "Problem in the compartments section";
				break;
			case TranslationMessage.UNIT_ERROR:
				tempStr = "Problem in the unit translation";
				break;
			case TranslationMessage.SPECIES_ERROR:
				tempStr = "Problem in the species section";
				break;
			case TranslationMessage.REACTION_ERROR:
				tempStr = "Problem in the reactions section";
				break;
			default:
				tempStr = "";
		}

		return tempStr;
	}


	public static boolean isValidMessageType(int messageType) {

		if (messageType == TranslationMessage.SCHEMA_VALIDATION_ERROR ||
			messageType == TranslationMessage.UNSUPPORED_ELEMENTS_OR_ATTS ||
			messageType == TranslationMessage.COMPARTMENT_ERROR ||
			messageType == TranslationMessage.UNIT_ERROR ||
			messageType == TranslationMessage.SPECIES_ERROR ||
			messageType == TranslationMessage.REACTION_ERROR) {
			return true;
		}
		return false;
	}
}