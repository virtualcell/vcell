package cbit.vcell.client;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.task.UserCancelException;
import cbit.vcell.vcml.TranslationMessage;

import cbit.util.xml.VCLogger;

import java.util.ArrayList;
/**
This class represents the otherwise missing link between the GUI layer classes and the XML translation package. 
It allows user interaction while importing/exporting a document, like providing extra parameters, or the option to 
cancel the process altogether.

 * Creation date: (9/21/2004 10:36:23 AM)
 * @author: Rashad Badrawi
 */
public class TranslationLogger implements VCLogger {
	  
	private static String OPTIONS [] = {"Continue", "Cancel"};
	private static String OK_OPTION = "OK";
	private static String CANCEL_OPTION = "Cancel";
	private java.awt.Component requester;
	protected ArrayList messages = new ArrayList();

	public TranslationLogger(TopLevelWindowManager topLevelWindow) {
		
		if (topLevelWindow == null) {
			throw new IllegalArgumentException("Invalid top level window");
		}
		this.requester = topLevelWindow.getComponent();
	}


	public TranslationLogger(java.awt.Component requester) {
		this.requester = requester;
	}


	public boolean hasMessages() {
		return messages.size() > 0;
	}


//for now, same for all 
	private void processException(int messageType) throws UserCancelException {

		throw UserCancelException.CANCEL_XML_TRANSLATION;
	}


	public void sendAllMessages() {

		StringBuffer messageBuf = new StringBuffer("The translation process has encountered the following problem(s):\n ");
													//"which can affect the quality of the translation:\n");
		for (int i = 0; i < messages.size(); i++) {
			messageBuf.append(i+1 + ") " + messages.get(i) + "\n");
		}
		UserMessage userMessage = new UserMessage(messageBuf.toString(), new String [] {TranslationLogger.OK_OPTION}, 
			                                      TranslationLogger.OK_OPTION);
		String value = PopupGenerator.showWarningDialog(requester, null, userMessage, null);       //'value' not used.
	}


	public void sendMessage(int messageLevel, int messageType) throws UserCancelException {

		String message = TranslationMessage.getDefaultMessage(messageType);
		sendMessage(messageLevel, messageType, message);	
	}


	public void sendMessage(int messageLevel, int messageType, String message) throws UserCancelException {

		if (message == null || message.length() == 0 || messageLevel < 0 || messageLevel > 2 || 
			!TranslationMessage.isValidMessageType(messageType)) {
			throw new IllegalArgumentException("Invalid params for sending translation message.");
		}
		if (messageLevel == TranslationLogger.LOW_PRIORITY) {
			messages.add(message);
		} else if (messageLevel == TranslationLogger.MEDIUM_PRIORITY) {
			UserMessage userMessage = new UserMessage(message, TranslationLogger.OPTIONS, TranslationLogger.OPTIONS[0]);
			String value = PopupGenerator.showWarningDialog(requester, null, userMessage, null);
			if (!value.equals(OPTIONS[0])) {
				processException(messageType);
			}
		} else if (messageLevel == TranslationLogger.HIGH_PRIORITY) {      
			UserMessage userMessage = new UserMessage(message, new String [] {TranslationLogger.CANCEL_OPTION}, TranslationLogger.CANCEL_OPTION);
			String value = PopupGenerator.showWarningDialog(requester, null, userMessage, null);
			processException(messageType);                                        //regardless of the 'value'
		}
	}
}