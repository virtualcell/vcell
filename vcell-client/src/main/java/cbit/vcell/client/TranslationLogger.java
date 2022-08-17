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
import java.util.LinkedHashSet;
import java.util.Set;

import org.vcell.util.UserCancelException;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
/**
	* This class represents the otherwise missing link between the GUI layer classes and the XML translation package. 
	* It allows user interaction while importing/exporting a document, like providing extra parameters, or the option to 
 	* cancel the process altogether.
 	* 
 	* For HIGH_PRIORITY messages, the logger halts execution by throwing an exception
 	*  
 	* Creation date: (9/21/2004 10:36:23 AM)
 	* @author: Rashad Badrawi
 */
public class TranslationLogger extends VCLogger implements AutoCloseable {


	private static String OK_OPTION = "OK";
	private java.awt.Component requester;
	protected Set<Message> messages = new LinkedHashSet< > ( ); //used LinkedHashSet to preserve order
	
	public static class Message{
		public final VCLogger.Priority priority;
		public final String message;
		public Message(Priority priority, String message) {
			super();
			assert(priority != null);
			assert(message != null);
			this.priority = priority;
			this.message = message;
		}
		@Override
		public int hashCode() {
			return priority.hashCode() ^ message.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null ||getClass() != obj.getClass())
				return false;
			Message rhs = (Message) obj;
			return message.equals(rhs.message) &&  priority.equals(rhs.priority);
		}
	}

	public TranslationLogger(TopLevelWindowManager topLevelWindow) {
		if (topLevelWindow == null) {
			throw new IllegalArgumentException("Invalid top level window");
		}
		this.requester = topLevelWindow.getComponent();
	}

	public TranslationLogger(java.awt.Component requester) {
		this.requester = requester;
	}

	@Override
	public boolean hasMessages() {
		return messages.size() > 0;
	}

	@Override
	public void sendAllMessages() {
		if (!messages.isEmpty()) {

			StringBuilder messageBuf = new StringBuilder("The translation process has encountered the following problem(s):\n ");
			//"which can affect the quality of the translation:\n");
			int i = 0;
			for (Message m : messages) {
				messageBuf.append(++i +") " + m.message + "\n");
			}
			String str = messageBuf.toString();
			UserMessage userMessage = new UserMessage(str, new String [] {TranslationLogger.OK_OPTION}, 
					TranslationLogger.OK_OPTION);
			PopupGenerator.showWarningDialog(requester, null, userMessage, null);       //'value' not used.
		}
	}


	@Override
	public void sendMessage(Priority p, ErrorType et, String message)
			throws VCLoggerException {

		if  (p == null ||et == null ) {
			throw new VCLoggerException(new IllegalArgumentException("Invalid params for sending translation message."));
		}
		switch (p) {
		case LowPriority:
		case MediumPriority: 
			messages.add(new Message(p, message));
			break;
		case HighPriority:
			PopupGenerator.showErrorDialog(requester, message);
		throw (UserCancelException.CANCEL_XML_TRANSLATION); // this shouldnt be wrapped as a VCLoggerException
		}
	}
	/**
	 * call {@link #sendAllMessages()}
	 */
	@Override
	public void close() throws Exception {
			sendAllMessages();
	}
}
