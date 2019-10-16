/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client;
import cbit.vcell.client.server.UserPreferences;
/**
 * Insert the type's description here.
 * Creation date: (9/7/2004 9:48:34 AM)
 * @author: Jim Schaff
 */
public class UserMessage {
	private String message = null;
	private String options[] = null;
	private String defaultSelection = null;
	private final int userPreferenceWarning;
	public final static String TEXT_REPLACE = "<<<REPLACE>>>";

	public final static String OPTION_REVERT_TO_SAVED = "Revert to saved";
	public final static String OPTION_USE_EXISTING_SPECIES = "Use existing species";
	public final static String OPTION_YES = "Yes";
	public final static String OPTION_NO = "No";
	public final static String OPTION_OK = "OK";
	public final static String OPTION_CANCEL = "Cancel";
	public final static String OPTION_DISCARD_CHANGES = "Discard changes";
	public final static String OPTION_EXIT = "Exit";
	public final static String OPTION_OVERWRITE_FILE = "Overwrite file";
	public final static String OPTION_SAVE_AS_NEW_EDITION = "Create new edition";
	public final static String OPTION_DELETE = "Delete";
	public final static String OPTION_CONTINUE= "Continue";
	public final static String OPTION_DISCARD_RESULTS = "Save and Discard results";
	public final static String OPTION_KEEP_OLD_RESULTS = "Save, Keep old results";
	public final static String OPTION_CLOSE = "Close";
	public final static String OPTION_SAVE_AS_NEW = "Save As New...";
	public final static String OPTION_UPDATE_DATABASE = "Update Database";


	public final static UserMessage choice_AlreadyOpened = new UserMessage("Document already opened - do you want to revert to the saved version?", 
				new String[] {OPTION_REVERT_TO_SAVED, OPTION_CANCEL},			OPTION_REVERT_TO_SAVED, -1);

	public final static UserMessage info_SaveBeforeRunning = new UserMessage("If document has changed it will be saved prior to running the simulations", 
				new String[] {OPTION_OK}, 										OPTION_OK, 				UserPreferences.WARN_SAVE_BEFORE_RUNNING);

	public final static UserMessage warn_UnableToCheckForChanges = new UserMessage("Unable to check for changes since last save - continue?",
				new String[] {OPTION_CONTINUE, OPTION_CANCEL},					OPTION_CONTINUE,		UserPreferences.WARN_UNABLE_CHECK_CHANGES);
	
	public final static UserMessage warn_closeWithoutSave = new UserMessage("You will lose any changes since last save - close document anyway?",
				new String[] {OPTION_CLOSE, OPTION_CANCEL},						OPTION_CLOSE,			-1); // UserPreferences.WARN_FOR_CLOSE_WITHOUT_SAVE

	public final static UserMessage warn_close = new UserMessage("Do you want to save the changes you made?",
			new String[] {OPTION_YES, OPTION_NO, OPTION_CANCEL},						OPTION_YES,			-1); // UserPreferences.WARN_FOR_CLOSE_WITHOUT_SAVE
	
	public final static UserMessage warn_UnchangedDocument = new UserMessage("Document has not changed - do you want to save it as a new document?",
				new String[] {OPTION_CANCEL, OPTION_SAVE_AS_NEW},				OPTION_CANCEL,		UserPreferences.WARN_SAVE_UNCHANGED_DOCUMENT);

	public final static UserMessage warn_SaveNotOwner = new UserMessage("You are not the owner of the document - do you want to save a copy of the document under your account?",
				new String[] {OPTION_SAVE_AS_NEW, OPTION_CANCEL},				OPTION_SAVE_AS_NEW,		UserPreferences.WARN_SAVE_NOT_OWNER);

	public final static UserMessage warn_DeleteSelectedAppWithSims = new UserMessage("Application '"+TEXT_REPLACE+"' has simulations. Are you sure you want to delete application '"+TEXT_REPLACE+"'?",
				new String[] {OPTION_DELETE, OPTION_CANCEL},					OPTION_DELETE,			UserPreferences.WARN_DELETE_APPLICATION);
	
	public final static UserMessage warn_DeleteSelectedApp = new UserMessage("Are you sure you want to delete application '"+TEXT_REPLACE+"'?",
			new String[] {OPTION_DELETE, OPTION_CANCEL},					OPTION_DELETE,			UserPreferences.WARN_DELETE_APPLICATION);

	public final static UserMessage question_LostResults = new UserMessage("Saving the model will erase all existing simulation results. Save model and discard simulation results or create a new model edition?",
				new String[] {OPTION_CANCEL, OPTION_DISCARD_RESULTS, OPTION_SAVE_AS_NEW_EDITION},		OPTION_CANCEL,		-1);

	public final static UserMessage question_LostResultsLowPrecision = new UserMessage(
			"Note: Several universal constants now have higher precision than in previous VCell versions.\n"+
					"(see BioModel->'Parameters, Functions, Units, etc.'->'Predefined Constants and Math Functions').\n"+
					"_F_      9.648e4 -> 9.64853321e4\n"+
					"_F_nmol_ 9.648e-5 -> 9.64853321e-5\n"+
					"_N_pmol_ 6.02e11 -> 6.02214179e11\n"+
					"_R_      8314.0 -> 8314.46261815\n"+
					"KMOLE    (1.0/602.0) -> (1.0/602.214179)\n"+
			 "  This causes the math to be slightly modified and thus the newly generated math will yield very small differences if the simulations are rerun.  You can either:\n" +
	"1. "+UserMessage.OPTION_KEEP_OLD_RESULTS+".\n     If you later rerun the simulations, you may notice very small differences in the numerical results because of the higher precision constants.\n"+
	"2. "+UserMessage.OPTION_SAVE_AS_NEW+" model.\n     Simulation results are not retained in the new model.  The existing simulation results can be accessed by reopening the original model.",
			new String[] {OPTION_CANCEL, OPTION_KEEP_OLD_RESULTS,OPTION_SAVE_AS_NEW},		OPTION_CANCEL,		-1);

	public final static UserMessage warn_RevertToSaved = new UserMessage("Are you sure you want to discard changes and revert to saved version?",
				new String[] {OPTION_DISCARD_CHANGES, OPTION_CANCEL}, 			OPTION_DISCARD_CHANGES,	UserPreferences.WARN_REVERT_TO_SAVED);

	public final static UserMessage warn_OverwriteFile = new UserMessage("Overwrite file "+TEXT_REPLACE+"?",
				new String[] {OPTION_OVERWRITE_FILE, OPTION_CANCEL},			OPTION_OVERWRITE_FILE,	UserPreferences.WARN_OVERWRITE_FILE);

	public final static UserMessage warn_timePlotOnlyForPoints = new UserMessage("The plot will show only time series data for points\nUse export to get time series data for lines and curves",
				new String[] {OPTION_OK, OPTION_CANCEL},						OPTION_OK,				UserPreferences.WARN_TIME_PLOT_ONLY_FOR_POINTS);
	
	public final static UserMessage warn_noScaleSettings = new UserMessage("You have not specified scale settings for some of the variables\nDefault scale range will be used",
				new String[] {OPTION_OK, OPTION_CANCEL},						OPTION_OK,				UserPreferences.WARN_NO_SCALE_SETTINGS_FOR_EXPORT);

	public final static UserMessage warn_changeUser = new UserMessage("All documents will be closed before logging in as a new user - continue?",
				new String[] {OPTION_CONTINUE, OPTION_CANCEL},					OPTION_CONTINUE,		UserPreferences.WARN_CHANGE_USER);
	
	public final static UserMessage warn_deleteDocument = new UserMessage("Deleting "+TEXT_REPLACE+", - are you sure?",
				new String[] {OPTION_DELETE, OPTION_CANCEL},					OPTION_DELETE,			UserPreferences.WARN_DELETE_DOCUMENT);

	public final static UserMessage warn_exportMembraneData3D = new UserMessage("There is one or more membrane variable selected\n\nMembrane variable data will not be exported as slice data, but as a list of values at all points over the entire 3D membrane area",
				new String[] {OPTION_OK, OPTION_CANCEL},						OPTION_OK,				UserPreferences.WARN_EXPORT_MEMBRANE_DATA_3D);

	public final static UserMessage warn_geom_histogram_apply = new UserMessage("Histogram tool has an active pixel range selection, press the 'Apply...' button to update/create Domain Regions.",
			new String[] {OPTION_OK},					OPTION_OK,		UserPreferences.WARN_GEOMEDIT_HISTOGRAM_APPLY);
	
	public final static UserMessage QUESTION_BEFORE_SAVE = new UserMessage("Saving the model will erase all existing simulation results. Save model and discard simulation results, create a new model edition, or cancel?",
				new String[] {OPTION_DISCARD_RESULTS, OPTION_SAVE_AS_NEW_EDITION, OPTION_CANCEL},		OPTION_DISCARD_RESULTS);

/**
 * UserMessage; user preference to no longer display not active 
 */
public UserMessage(String argMessage, String[] argOptions, String argDefaultSelection) {
	super();
	this.message = argMessage;
	this.options = argOptions;
	this.defaultSelection = argDefaultSelection;
	this.userPreferenceWarning = -1; 
}


/**
 * UserMessage constructor comment.
 */
private UserMessage(String argMessage, String[] argOptions, String argDefaultSelection, int argUserPreferenceWarning) {
	super();
	this.message = argMessage;
	this.options = argOptions;
	this.defaultSelection = argDefaultSelection;
	this.userPreferenceWarning = argUserPreferenceWarning;
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2004 2:42:37 PM)
 * @return java.lang.String
 */
public java.lang.String getDefaultSelection() {
	return defaultSelection;
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2004 10:17:05 AM)
 * @return java.lang.String
 */
public java.lang.String getMessage(String replacementText) {
	if (replacementText!=null){
		if (message.indexOf(TEXT_REPLACE)==-1){
			throw new RuntimeException("not expecting replacement text");
		}else{
			return org.vcell.util.TokenMangler.replaceSubString(message,TEXT_REPLACE,replacementText);
		}
	}else if (message.indexOf(TEXT_REPLACE) > -1){
		throw new RuntimeException("expecting non-null replacement text");
	}
	return message;
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2004 10:17:05 AM)
 * @return java.lang.String[]
 */
public java.lang.String[] getOptions() {
	return options;
}


/**
 * Insert the method's description here.
 * Creation date: (9/7/2004 2:39:41 PM)
 * @return int
 */
public int getUserPreferenceWarning() {
	return userPreferenceWarning;
}
}
