/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image;

/*
	File: DragFix.java
	3/7/97    Larry Barowski
*/

/**
 *	Drag fix queues events and eliminates repeated mouse
 *      drag events and scrollbar events (most annoying on Win95).
 *      Be sure the Component doesn't post an event to itself
 *      after every mouse drag or scrollbar event, or this will do
 *      no good. I suggest getParent().postEvent() instead.</p>
 *
 *      The constructor for the Component that uses it should have
 *      (to be safe, as the first line):</p>
 *
 *      <PRE>dragFix_ = new DragFix(this);  // dragFix_ is a member variable.</PRE>
 *
 *
 *      <br><br>The handleEvent() function should look like this:</p>
 *
 *      <PRE>public boolean handleEvent(Event e)
 *      {
 *         if(e.id == DragFix.QUEUED)
 *         {
 *            deal with (Event)e.arg
 *            if necessary, super.handleEvent((Event)e.arg);
 *            if necessary, getParent().postEvent((Event)e.arg);
 *            return true;
 *         }
 *         dragFix_.queueEvent(e);
 *         return true;
 *      }</PRE>
 *
 *     <br><br>and removeNotify should look like this, to kill the thread
 *     immediately - otherwise it will be there (asleep) until
 *     finalize() gets called (if ever):</p>
 *
 *     <PRE>public synchronized void removeNotify()
 *     {
 *        dragFix_.killThread();
 *        super.removeNotify();
 *     }</pre></p><br><br>
 *
 *
 *
 *	</p>Here is the <a href="../gui/DragFix.java">source</a>.
 *
 *@author	Larry Barowski
**/
   public class CommandCache implements Runnable
   {
	  private CommandListener user_;
	  private Thread thread_;
   
	  private Command[] eventQueue_ = null;
	  private int eventQueueHead_ = 0, eventQueueTail_ = 0;
	  private int eventQueueSize_ = 20;  // More than enough, in most cases.
	  private final static int eventQueueLimit_ = 2000;
	  private Object lock_ = new Object();
	  private boolean bPoisonPill = false;
   
	  public final static int QUEUED = 179583;
   
   	  public CommandCache(CommandListener user)
	  {
		 user_ = user;
		 eventQueue_ = new Command[eventQueueSize_ + 1];
	  
		 thread_ = new Thread(this);
		 thread_.setName("CommandCache");
		 thread_.start();
	  }  
	  protected void finalize()
	  {
		 killThread();
	  }  
	  public void killThread()
	  {
		  bPoisonPill = true;
		  lock_.notify();
	  }  
/**
  * Queue or dequeue a command.
  **/
private Command Queue(Command command) {
	synchronized (lock_) {
		if (command == null) {
			// Dequeue
			if (eventQueueHead_ != eventQueueTail_) {
				// Not empty.
				eventQueueTail_ = (eventQueueTail_ + 1) % eventQueueSize_;
				return eventQueue_[eventQueueTail_];
			}
			return null; // To indicate empty.
		}

		// Add to queue.
		if (eventQueueHead_ != eventQueueTail_) {
			if (command.getClass() == eventQueue_[eventQueueHead_].getClass()) {
				// Eliminate the previous command.
				command.integrateOldCommand(eventQueue_[eventQueueHead_]);
				eventQueue_[eventQueueHead_] = command;
				return null;
			}
		}
		eventQueue_[ (eventQueueHead_ + 1) % eventQueueSize_] = command;
		eventQueueHead_ = (eventQueueHead_ + 1) % eventQueueSize_;
		lock_.notify();
		if ((eventQueueHead_ + 1) % eventQueueSize_ == eventQueueTail_) {
			// Queue is full, grow it.
			int new_size = eventQueueSize_ * 2;
			if (new_size > eventQueueLimit_){
				throw new Error("DragFix event queue size limit " + "exceeded.");
			}
			Command[] new_queue = new Command[new_size + 1];
			int i = 0, j;
			for (j = eventQueueTail_; j != eventQueueHead_; j = (j + 1) % eventQueueSize_, i++){
				new_queue[i] = eventQueue_[j];
			}
			new_queue[i] = eventQueue_[j];
			eventQueue_ = new_queue;
			eventQueueHead_ = i;
			eventQueueTail_ = 0;
			eventQueueSize_ = new_size;
		}
		return null;
	}
}
	  /**
	  * Queue or ignore a command. Call this from handleEvent()
	  * and return true.
	  **/
	  public void queueCommand(Command command)
	  {
		 Queue(command);
	  }  
   /**
   * Process queued events.
   **/
	  public synchronized void run()
	  {
		 Command command;
		 while(true)
		 {
		 
			// Process commands until the queue is empty.
			while((command = Queue(null)) != null)
			{
				user_.command(command);
			}
		 
			// Now that it is empty, wait for the next event.
			synchronized(lock_)
			{
			   try {
				  lock_.wait(); }
				  catch(InterruptedException ex)
				  {}
			}
			if (bPoisonPill){
				break;
			}
		 }
	  }  
}
