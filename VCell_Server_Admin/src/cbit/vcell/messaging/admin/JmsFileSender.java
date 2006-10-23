package cbit.vcell.messaging.admin;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import progress.message.jclient.channel.RecoverableFileChannel;
import progress.message.jclient.channel.RecoverableFileChannelFactory;
import cbit.util.PropertyLoader;
import cbit.util.SessionLog;
import cbit.vcell.messaging.JmsConnectionFactory;
import cbit.vcell.messaging.JmsConnectionFactoryImpl;
import cbit.vcell.messaging.JmsUtils;
import cbit.vcell.messaging.ManageConstants;
import cbit.vcell.messaging.VCellQueueConnection;
import cbit.vcell.messaging.VCellQueueSession;

/**
 * Insert the type's description here.
 * Creation date: (12/17/2003 3:52:25 PM)
 * @author: Fei Gao
 */
public class JmsFileSender {
	private VCellQueueSession session = null;
	private VCellQueueConnection queueConn = null;

	private boolean isComplete = false;
	private boolean continueBrowse = true;
	private SessionLog log = null;

/**
 * Inner class implementing ChannelListener interface and keep tracking of 
 * channel events.Cancel a channel on any event.
 */
private class LMSChannelListener implements progress.message.jclient.ChannelListener {
public void onChannelStatus(progress.message.jclient.Channel channel, Exception e) {
	try {
		progress.message.jclient.ChannelStatus cs = channel.getChannelStatus();

		switch (cs.getStatus()) {
			case RecoverableFileChannel.RFC_DISCONNECT:
				System.out.print("....Connection was lost");
				continueBrowse = false;
				((RecoverableFileChannel) channel).cancel();
				break;

			case RecoverableFileChannel.RFC_REMOTE_ERROR :
				System.out.print("....There was a remote error");
				continueBrowse = false;
				((RecoverableFileChannel) channel).cancel();
				break;

			case RecoverableFileChannel.RFC_LOCAL_ERROR:
				System.out.print("....There was a local error");
				continueBrowse = false;
				((RecoverableFileChannel) channel).cancel();
				break;

			case RecoverableFileChannel.RFC_RETRY_TIMEOUT: {
				System.out.print("....Connection timed out!");
				continueBrowse = false;
				((RecoverableFileChannel) channel).cancel();
				break;
			}

			case RecoverableFileChannel.RFC_CLOSED: {
				System.out.print("....Connect was closed on remote system");
				continueBrowse = false;
				((RecoverableFileChannel) channel).cancel();
				break;
			}

			case RecoverableFileChannel.RFC_CANCELLED: {
				System.out.print("....Transfer was cancelled by remote client!");
				continueBrowse = false;
				break;
			}
				
			case RecoverableFileChannel.RFC_TRANSFER_COMPLETE: {
				isComplete = true;
				channel.completeTransfer();
				break;
			}
		}
	} catch (javax.jms.JMSException ex) {
		log.exception(ex);
	}
}
}

/**
 * JmsFileSender constructor comment.
 */
public JmsFileSender(VCellQueueConnection queueConn0, SessionLog log0) throws javax.jms.JMSException {
	super();
	queueConn = queueConn0;
	log = log0;	
	session = queueConn.getAutoSession();
}
/**Keep tracking of transfer progress */
private void browseTransfer(progress.message.jclient.Channel channel) {
	isComplete = false;
	continueBrowse = true;

	try {
		progress.message.jclient.ChannelStatus status = channel.getChannelStatus();
		long fileSize = status.bytesToTransfer();
		log.print("File size to send - " + fileSize);

		System.out.print("Sending");
		byte percentProcessed = 0;

		//Check transferred bytes each 2sec and print transfer progress.
		//Each dot in printing equals 2% of transfer
		while (continueBrowse) {
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
			}

			percentProcessed = calculateProgress(percentProcessed, status.bytesTransferred(), fileSize);

			if (percentProcessed >= 100) {
				//((RecoverableFileChannel)channel).cancel();
				System.out.print("....Done!");
				continueBrowse = false;
				break;
			}
		}
		System.out.println();
	} catch (javax.jms.JMSException e) {
		log.exception(e);
	}
}
/**
 * Calculate transfer progress percent and print it
 */

private byte calculateProgress(byte _percentProcessed, long bytesTransferred, long _fileSize) {
	//percentDiff indicates the difference between previously processed percent 
	//and the latest transferred percent.
	//So, if for example previos percent was 28, and latest 37, then percentDiff=9
	int percentDiff = (int) ((100 * bytesTransferred) / _fileSize - _percentProcessed);

	//dotNumber reflects actual number of dots to print (each dot in every 2% of progress)
	//the following statement defines how many dots to print
	int dotNumber = (percentDiff - (percentDiff % 2)) / 2;

	//the following "if" statement defines what to print - dot or % - and increases
	//processed percent accordingly         

	if (dotNumber != 0) {
		for (int i = 1; i <= dotNumber; i ++) {
			_percentProcessed += 2;

			if (_percentProcessed % 10 == 0) {
				System.out.print(_percentProcessed + "%");
			} else {
				System.out.print(".");
			}
		}
	}

	//return new processed percent to browseTransfer() method, so we have updated 
	//processed percent before next calculation
	return _percentProcessed;
}
/**
 * Insert the method's description here.
 * Creation date: (12/18/2003 9:21:32 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		// Start the JMS client for the file transfer.
		PropertyLoader.loadProperties();
		JmsConnectionFactory jmsConnFactory = new JmsConnectionFactoryImpl();
		VCellQueueConnection queueConn = jmsConnFactory.createQueueConnection();

		SessionLog log = new cbit.util.StdoutSessionLog("test");
		JmsFileSender sender = new JmsFileSender(queueConn, log);
		queueConn.startConnection();
		java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
		while (true) {
			sender.toSend(new File("d:\\vcell\\dev2\\dispatchService.log"));			
			br.readLine();
		}
		//queueConn.close();
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}	
}
/**
 * Restore unfinished channel upon sender restart
 */

private void restoreChannels() {
	try {
		// Check if there are any unfinished channels to restore
		progress.message.jclient.QueueConnection connection = (progress.message.jclient.QueueConnection) queueConn.getConnection();
		if (connection.hasUnfinishedChannels()) {
			// Retrieve an enumeration of all available Channels of this client
			Enumeration enum1 = connection.getUnfinishedChannels();

			while (enum1.hasMoreElements()) {
				RecoverableFileChannel channel = (RecoverableFileChannel) enum1.nextElement();

				try {
					//// Set a channel listener
					//channel.setChannelListener(new LMSChannelListener());

					log.print("Try to cancel unfinished channel - " + channel.getChannelID());

					// Cancel the transfer
					channel.cancel();
				} catch (javax.jms.JMSException jmse) {
					//channel.cancel();
					log.exception(jmse);
				}
			}
		}
	} catch (javax.jms.JMSException e) {
		log.exception(e);
	} catch (IOException e) {
		log.exception(e);
	}
}
/**Create new message with channel and send it*/
private void sendNewMsg(File file) {
	//send new msg with a channel
	try {
		javax.jms.Message msg =	session.createTextMessage("Test");

		//create RecoverableFileChannel object and set ChannelListener to it
		RecoverableFileChannel rfc = RecoverableFileChannelFactory.createRecoverableFileChannel(file);
		rfc.setChannelListener(new LMSChannelListener());

		//set Timeout to a channel, so if during 30sec the channel is not established,
		//ChannelListener will cancel it
		rfc.setTimeout(20 * ManageConstants.SECOND);

		//set a channel to a message
		 ((progress.message.jclient.Message) msg).setChannel(rfc);

		//set message property (name of a file being sent), which is used by receiver 
		//to get file name 
		msg.setStringProperty("SenderFileName", file.getName());

		//send message
		log.print("Try to send header message and establish channel to send file - " + file.getAbsolutePath());

		// Notice that the message is being sent with the timeout value
		// that has been set on the RecoverableFileChannel.  This is to assist
		// in the case that the establishment of the channel does not occur
		// after timeout time.  If the establishment does not occur in timeout
		// time, a developer may not want the message with the channel to
		// remain on the queue after the sender is no longer available.  
		// If the message remained on the queue, a receiver may attempt to 
		// establish a channel with a sender that is no longer available.  
		// The TTL setting will help to prevent this from happening.

		session.sendMessage(JmsUtils.getQueueFileChannel(), msg, javax.jms.DeliveryMode.NON_PERSISTENT, rfc.getTimeout());
		log.print(rfc.getChannelID() + " channel established!");

		//begin to browse the transfer progress
		browseTransfer(rfc);
	} catch (javax.jms.JMSException jmse) {
		log.exception(jmse);
	} catch (Exception e) {
		log.exception(e);
	}
}
/** 
 * Create JMS session for sending large messages with channels.
 * Invoke methods to restore unfinished channel and send new message
 */
public void toSend(File file) {
	//Try to restore unfinished Channels first
	restoreChannels();	
	
	if (file != null) {
		sendNewMsg(file);
	}	
}
}
