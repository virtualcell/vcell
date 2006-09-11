package cbit.vcell.messaging.admin;

import java.util.*;
import java.io.*;
import java.util.Enumeration;
import cbit.vcell.messaging.*;
import progress.message.jclient.channel.*;

/**
 * Insert the type's description here.
 * Creation date: (12/17/2003 3:52:09 PM)
 * @author: Fei Gao
 */
public class JmsFileReceiver implements QueueListener {
    private VCellQueueSession session = null;    
    private cbit.vcell.server.SessionLog log = null;
   
    private String saveDirName = null;
    private File currentFile = null;
    private progress.message.jclient.channel.RecoverableFileChannel rfc = null;
    private boolean isDone = true;
    private ServerManageConsole console = null;
               
/**
 * Our ChannelListener interface that keeps tracking of channel events.
 * We cancel a channel on any event.
 */

private class LMSChannelListener implements progress.message.jclient.ChannelListener {
public void onChannelStatus(progress.message.jclient.Channel channel,Exception e) {
	try {
		// Get the channel Status object
		progress.message.jclient.ChannelStatus cs = channel.getChannelStatus();

		switch (cs.getStatus()) {
			case RecoverableFileChannel.RFC_DISCONNECT :
				channelMessage = "Connection was lost.";
				((RecoverableFileChannel) channel).cancel();
				isDone = true;
				break;

			case RecoverableFileChannel.RFC_REMOTE_ERROR :
				channelMessage = "There was a remote error.";
				((RecoverableFileChannel) channel).cancel();
				isDone = true;
				break;

			case RecoverableFileChannel.RFC_LOCAL_ERROR :
				channelMessage = "There was a local error.";
				((RecoverableFileChannel) channel).cancel();
				isDone = true;
				break;

			case RecoverableFileChannel.RFC_RETRY_TIMEOUT :
				channelMessage = "Connection timed out.";
				((RecoverableFileChannel) channel).cancel();
				isDone = true;
				break;

			case RecoverableFileChannel.RFC_CLOSED :
				channelMessage = "Connect was closed on remote client.";
				((RecoverableFileChannel) channel).cancel();
				isDone = true;
				break;

			case RecoverableFileChannel.RFC_CANCELLED :
				channelMessage = "Transfer was cancelled by remote client.";
				isDone = true;
				break;

			case RecoverableFileChannel.RFC_TRANSFER_COMPLETE :
				log.print(channel.getChannelID() + " channel transfer is complete.");
				isComplete = true;
				isDone = true;
				break;
		}
	} catch (javax.jms.JMSException jmse) {
		log.exception(jmse);
	}
}
}
  
	private cbit.vcell.messaging.VCellQueueConnection queueConn = null;
	private boolean isComplete = false;
	private java.lang.String channelMessage = "";
/**
 * JmsFileReceiver constructor comment.
 */
public JmsFileReceiver(VCellQueueConnection queueConn0, cbit.vcell.server.SessionLog log0) {
	super();
	queueConn = queueConn0;
	log = log0;
}
/** Cleanup resources and then exit. */
public void exit() {
	log.print("Close connection and exit");
	try {
		queueConn.closeSession(session);
	} catch (javax.jms.JMSException e) {
		log.exception(e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/23/2003 10:49:28 AM)
 * @return java.lang.String
 */
public java.lang.String getChannelMessage() {
	return channelMessage;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2003 1:12:31 PM)
 * @return java.io.File
 */
public java.io.File getCurrentFile() {
	return currentFile;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2003 1:24:33 PM)
 * @return boolean
 */
public boolean isComplete() {
	return isComplete;
}
/**
 * Insert the method's description here.
 * Creation date: (12/23/2003 10:48:30 AM)
 * @return boolean
 */
public boolean isDone() {
	return isDone;
}
/**
 * Insert the method's description here.
 * Creation date: (12/18/2003 9:17:49 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		// Start the JMS client for the file transfer.
		cbit.vcell.server.PropertyLoader.loadProperties();
		JmsConnectionFactory jmsConnFactory = new JmsConnectionFactoryImpl();
		VCellQueueConnection queueConn = jmsConnFactory.createQueueConnection();

		cbit.vcell.server.SessionLog log = new cbit.vcell.server.StdoutSessionLog("test");
		JmsFileReceiver receiver = new JmsFileReceiver(queueConn, log);		
		receiver.toReceive();
		queueConn.startConnection();
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
}
/**
 * Receive new messages, choose those with channels and process them 
 */
public void onQueueMessage(javax.jms.Message msg) throws javax.jms.JMSException {
	try {
		log.print("onMessge - " + JmsUtils.toString(msg));
		startOver();
		progress.message.jclient.Message message = (progress.message.jclient.Message) msg;
		message.acknowledge();

		// Check if this message has a channel
		if (message.hasChannel()) {
			progress.message.jclient.Channel channel = message.getChannel();
			log.print("Received header message with channel - " + channel.getChannelID());
			rfc = (progress.message.jclient.channel.RecoverableFileChannel) channel;

			rfc.setChannelListener(new LMSChannelListener());
			rfc.setTimeout(30 * ManageConstants.SECOND);

			// Save record channelID=filename in our property file before 
			// completeConnect().
			//
			// There are some interesting scenarios we must account for if we crash
			// exactly at this point.
			//
			// If we chose to save the property file after completeConnect(), we could
			// potentially get into a failure situation where we have a recovery log 
			// but no property information.
			//
			// By saving the property file now, the worse case is that we have 
			// property information, but no recovery log information.  To resolve
			// this situation, a cleanPropFile() method is executed during recovery
			// to remove excess property file information.

			rfc.completeConnect();
			log.print("Connection is completed");

			String senderFileName = (String)JmsUtils.parseProperty(message, "SenderFileName", String.class);
			String filename = saveDirName + File.separator + senderFileName;
			log.print("Begin to save fragments in file - " + filename);

			// If we crashed exactly at this point, the file would not
			// have started saving.  Hence we provide the isSaving()
			// method that is utilized during recovery.
			currentFile = new File(filename);
			rfc.save(currentFile);			
		}
	} catch (javax.jms.JMSException e) {
		log.exception(e);
	} catch (Exception e) {
		log.exception(e);
	}
}
/**
 * Restore unfinished channels upon receiver restart
 */

private void restoreChannels() {
	// First clean the property log file. There can be extra records 
	// ( see comments in onMessage() )
	try {
		// Check if there are any unfinished channels to restore
		progress.message.jclient.QueueConnection connection = (progress.message.jclient.QueueConnection) queueConn.getConnection();
		if (connection.hasUnfinishedChannels()) {
			// Retrieve an enumeration of Strings contains all available
			// channelIDs
			Enumeration enum1 = connection.getUnfinishedChannelIDs();

			while (enum1.hasMoreElements()) {
				String channelFile = null;

				// Get the channelID
				String channelID = (String) enum1.nextElement();

				// Retrieve the channel reference. If failed try the next element
				RecoverableFileChannel channel = (RecoverableFileChannel) connection.getChannel(channelID);

				try {
					//// Set a channel listener
					//channel.setChannelListener(new LMSChannelListener());

					//// Access our properties to print useful information
					//channelFile = saveDirName + File.separator + msgProp.getProperty(channelID);

					log.print("Try to cancel unfinished channel -" + channelID);

					// Cancel the transfer
					channel.cancel();
				} catch (javax.jms.JMSException jmse) {
					//channel.cancel();
					log.exception(jmse);
				}
			}
		}
	} catch (javax.jms.JMSException jmse) {
		log.exception(jmse);
	} catch (IOException ioe) {
		log.exception(ioe);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2003 1:27:34 PM)
 * @param newIsComplete boolean
 */
public void startOver() {
	currentFile = null;
	channelMessage = null;
	isDone = false;
	isComplete = false;
}
/** 
 * Create a JMS session for receiving messages with RecoverableFileChannels.
 * Invoke methods to restore unfinished channels and receive new messages.
 */
public void toReceive() {
	saveDirName = System.getProperty("user.home");	
	if (saveDirName == null) {
		saveDirName = ManageUtils.getEnvVariable("TEMP", log);
	}

	try {
		session = queueConn.getClientAckSession();
		//Try to restore unfinished Channels first, and then prepare to receive new msgs
		restoreChannels();
		//Prepare to receive any new msgs
		session.setupListener(JmsUtils.getQueueFileChannel(), null, new QueueMessageCollector(this));

	} catch (javax.jms.JMSException jmse) {
		log.exception(jmse);
	}	
}
}
