package cbit.vcell.message.jms;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.message.RollbackException;
import cbit.vcell.message.VCMessagingConsumer;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCTopicConsumer;

public class ConsumerContextJms implements Runnable {
	public static final long CONSUMER_POLLING_INTERVAL_MS = 2000;
	private VCMessagingServiceJms vcMessagingServiceJms = null;
	private VCMessagingConsumer vcConsumer = null;
	private Session jmsSession = null;
	private Connection jmsConnection = null;
	private MessageConsumer jmsMessageConsumer = null;
	private boolean bProcessing = false;
	private Thread thread = null;
	private static Logger lg = LogManager.getLogger(ConsumerContextJms.class);
	
	public ConsumerContextJms(VCMessagingServiceJms vcMessagingServiceJms, VCMessagingConsumer consumer){
		this.vcMessagingServiceJms = vcMessagingServiceJms;
		this.vcConsumer = consumer;
	}
	
	public void run(){
		bProcessing = true;
		System.out.println(toString()+" consumer thread starting.");
		while (bProcessing){
			MessageProducerSessionJms temporaryMessageProducerSession = null;
			try {
				Message jmsMessage = jmsMessageConsumer.receive(CONSUMER_POLLING_INTERVAL_MS);
				if (jmsMessage!=null){
//						System.out.println(toString()+"===============message received within "+CONSUMER_POLLING_INTERVAL_MS+" ms");
					if (vcConsumer instanceof VCQueueConsumer){
						VCQueueConsumer queueConsumer = (VCQueueConsumer)vcConsumer;
						VCMessageJms vcMessage = new VCMessageJms(jmsMessage, vcMessagingServiceJms.getDelegate());
						vcMessage.loadBlobFile();
						vcMessagingServiceJms.getDelegate().onMessageReceived(vcMessage,vcConsumer.getVCDestination());
						temporaryMessageProducerSession = new MessageProducerSessionJms(vcMessagingServiceJms);
						queueConsumer.getQueueListener().onQueueMessage(vcMessage, temporaryMessageProducerSession);
						temporaryMessageProducerSession.commit();
						jmsSession.commit();
						vcMessage.removeBlobFile();
					} else if (vcConsumer instanceof VCTopicConsumer){
						VCTopicConsumer topicConsumer = (VCTopicConsumer)vcConsumer;
						VCMessageJms vcMessage = new VCMessageJms(jmsMessage, vcMessagingServiceJms.getDelegate());
						vcMessage.loadBlobFile();
						vcMessagingServiceJms.getDelegate().onMessageReceived(vcMessage,vcConsumer.getVCDestination());
						temporaryMessageProducerSession = new MessageProducerSessionJms(vcMessagingServiceJms);
						topicConsumer.getTopicListener().onTopicMessage(vcMessage, temporaryMessageProducerSession);
						temporaryMessageProducerSession.commit();
						jmsSession.commit();
						//
						// if we knew this was the only subscriber for this topic, then remove file immediately.
						// since we don't know for sure, don't remove the file here.
						// instead, we need to periodically remove old Blob files (maybe daily) 
						//
						//  vcMessage.removeBlobFile();
					}else{
						throw new RuntimeException("unexpected VCConsumer type "+vcConsumer);
					}
				}else{
//						System.out.println(toString()+"no message received within "+CONSUMER_POLLING_INTERVAL_MS+" ms");
				}
			} catch (JMSException e) {
				onException(e);
			} catch (RollbackException e) {
				lg.error(e.getMessage(),e);
				try {
					jmsSession.rollback();
				} catch (JMSException e1) {
					onException(e1);
				}
			} catch (Exception e) {
				lg.error(e.getMessage(),e);
			}finally{
				if(temporaryMessageProducerSession != null){
					temporaryMessageProducerSession.close();
				}
			}
		}
		System.out.println(toString()+" consumer thread exiting.");
	}
	
	
	
	public void start() {
		if (bProcessing){
			throw new RuntimeException("consumer already started");
		}
		setThread(new Thread(this,vcConsumer.getThreadName()));
		getThread().setDaemon(true);
		getThread().start();
	}
	public void stop(){
		if (bProcessing){
			bProcessing=false;
			System.out.println(toString()+" consumer thread stop requested");
		}
	}
	public void init() throws JMSException {
		boolean bTransacted = true;
		int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
		try {
			this.jmsConnection = vcMessagingServiceJms.createConnectionFactory().createConnection();
			this.jmsConnection.setExceptionListener(new ExceptionListener() {
				public void onException(JMSException arg0) {
					ConsumerContextJms.this.onException(arg0);
				}
			});
			this.jmsConnection.start();
			this.jmsSession = this.jmsConnection.createSession(bTransacted, acknowledgeMode);
			this.jmsMessageConsumer = this.vcMessagingServiceJms.createConsumer(this.jmsSession, vcConsumer.getVCDestination(), vcConsumer.getSelector(), vcConsumer.getPrefetchLimit());
		}catch (JMSException | VCMessagingException e){
			e.printStackTrace(System.out);
			onException(e);
		}
	}
	
	private void onException(Exception e){
		vcMessagingServiceJms.getDelegate().onException(e);
		lg.error(e.getMessage(),e);
	}
	
	public VCMessagingConsumer getVCConsumer() {
		return vcConsumer;
	}
	
	void close() {
		try {
			if (jmsMessageConsumer!=null){
				jmsMessageConsumer.close();
			}
			if (jmsSession!=null){
				jmsSession.close();
			}
			if (jmsConnection!=null){
				jmsConnection.stop();
				jmsConnection.close();
			}
		}catch (JMSException e){
			onException(e);
		}
	}
	public Thread getThread() {
		return thread;
	}
	public void setThread(Thread thread) {
		this.thread = thread;
	}
}