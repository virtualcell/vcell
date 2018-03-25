package cbit.vcell.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class VCPooledQueueConsumer implements VCQueueConsumer.QueueListener {
	public static final Logger lg = Logger.getLogger(VCPooledQueueConsumer.class);

	private VCQueueConsumer.QueueListener queueListener = null;
	private int numThreads;
	private ExecutorService executorService = null;
	private ArrayList<Future<Boolean>> messageProcessorFutures = new ArrayList<Future<Boolean>>();
	private VCMessageSession producerSession = null;

	public VCPooledQueueConsumer(VCQueueConsumer.QueueListener queueListener, int numThreads, VCMessageSession producerSession) {
		this.queueListener = queueListener;
		this.numThreads = numThreads;
		this.producerSession = producerSession;
	}
	
	public void initThreadPool(){
		executorService = Executors.newFixedThreadPool(numThreads);
	}
	
	public void shutdownAndAwaitTermination() {
		executorService.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!executorService.awaitTermination(20, TimeUnit.SECONDS)) {
				executorService.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!executorService.awaitTermination(20, TimeUnit.SECONDS)) {
					lg.error("Pool did not terminate");
				}
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			executorService.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
	
	public void onQueueMessage(final VCMessage vcMessage, VCMessageSession consumerSession) {
		
		//
		// remove completed "Futures"
		//
		while (true){
			Iterator<Future<Boolean>> iter = messageProcessorFutures.iterator();
			while (iter.hasNext()){
				Future<Boolean> future = iter.next();
				if (future.isDone()){
					iter.remove();
				}
			}
			if (messageProcessorFutures.size()<numThreads){
				// there is room in thread pool for this simulation
				// we will submit this job and return from the callback.
				break;
			}else{
				// block until some tasks finish.
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		Callable<Boolean> messageProcessor = new Callable<Boolean>(){			
			@Override
			public Boolean call() throws Exception {
				queueListener.onQueueMessage(vcMessage, producerSession);
				return true;
			}
		};
System.out.println("Dispatching a worker thread to handle this message");
		Future<Boolean> messageProcessorFuture = executorService.submit(messageProcessor);
		messageProcessorFutures.add(messageProcessorFuture);
	}
}