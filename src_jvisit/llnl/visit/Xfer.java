// ****************************************************************************
//
// Copyright (c) 2000 - 2011, Lawrence Livermore National Security, LLC
// Produced at the Lawrence Livermore National Laboratory
// LLNL-CODE-442911
// All rights reserved.
//
// This file is  part of VisIt. For  details, see https://visit.llnl.gov/.  The
// full copyright notice is contained in the file COPYRIGHT located at the root
// of the VisIt distribution or at http://www.llnl.gov/visit/copyright.html.
//
// Redistribution  and  use  in  source  and  binary  forms,  with  or  without
// modification, are permitted provided that the following conditions are met:
//
//  - Redistributions of  source code must  retain the above  copyright notice,
//    this list of conditions and the disclaimer below.
//  - Redistributions in binary form must reproduce the above copyright notice,
//    this  list of  conditions  and  the  disclaimer (as noted below)  in  the
//    documentation and/or other materials provided with the distribution.
//  - Neither the name of  the LLNS/LLNL nor the names of  its contributors may
//    be used to endorse or promote products derived from this software without
//    specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT  HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR  IMPLIED WARRANTIES, INCLUDING,  BUT NOT  LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND  FITNESS FOR A PARTICULAR  PURPOSE
// ARE  DISCLAIMED. IN  NO EVENT  SHALL LAWRENCE  LIVERMORE NATIONAL  SECURITY,
// LLC, THE  U.S.  DEPARTMENT OF  ENERGY  OR  CONTRIBUTORS BE  LIABLE  FOR  ANY
// DIRECT,  INDIRECT,   INCIDENTAL,   SPECIAL,   EXEMPLARY,  OR   CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT  LIMITED TO, PROCUREMENT OF  SUBSTITUTE GOODS OR
// SERVICES; LOSS OF  USE, DATA, OR PROFITS; OR  BUSINESS INTERRUPTION) HOWEVER
// CAUSED  AND  ON  ANY  THEORY  OF  LIABILITY,  WHETHER  IN  CONTRACT,  STRICT
// LIABILITY, OR TORT  (INCLUDING NEGLIGENCE OR OTHERWISE)  ARISING IN ANY  WAY
// OUT OF THE  USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
// DAMAGE.
//
// ****************************************************************************

package llnl.visit;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

// ****************************************************************************
// Class: Xfer
//
// Purpose:
//   This class is a special observer that observes the state objects that
//   we use to communicate with the viewer. When the state objects change in
//   Java, Xfer is notified and it sends the state object that changed to the
//   viewer. When the viewer sends a state object to Java, Xfer makes sure
//   the state object reads itself and that it notifies all of its observers.
//
// Notes:      
//
// Programmer: Brad Whitlock
// Creation:   Thu Aug 8 12:38:47 PDT 2002
//
// Modifications:
//   Brad Whitlock, Thu Dec 12 12:47:12 PDT 2002
//   Added verbose flag to allow us to print trace information.
//
//   Brad Whitlock, Fri Jan 7 17:03:14 PST 2005
//   Added support for inserting null entries into the subjects list so
//   we can maintain the same object ordering as the viewer without
//   having to write a state object if we don't want to.
//
//   Brad Whitlock, Mon Jun 6 10:19:14 PDT 2005
//   I added code code to make the working thread sleep a little to reduce
//   workload on the CPU if nothing's getting processed.
//
//   Brad Whitlock, Thu Jun 16 17:22:46 PST 2005
//   Moved the code that made the thread sleep into Yielder class because I
//   needed the same logic elsewhere. The real solution is to figure out the
//   Java equivalent of select() so we don't have to poll.
//
// ****************************************************************************

class Xfer implements SimpleObserver, Runnable
{
	public Xfer()
    {
        subjects = new AttributeSubject[200];
        for(int i = 0; i < 200; ++i)
            subjects[i] = null;
        nSubjects = 0;

        doUpdate = true;
        header = new CommunicationBuffer();
        message = new CommunicationBuffer();
        input = new CommunicationBuffer();
        viewerInit = false;
        haveStoredHeader = false;
        length = -1;
        opcode = 0;
        processing = false;
        communicating = false;
        verbose = false;
        
    }

    public void Add(AttributeSubject subject)
    {
        subject.Attach(this);
        subjects[nSubjects] = subject;
        subject.SetAttributeId(nSubjects);
        ++nSubjects;
    }

    // This is a temporary method.
    public void Add(int index, AttributeSubject subject)
    {
        subject.Attach(this);
        subjects[index] = subject;
        subject.SetAttributeId(index);
    }

    // This is a temporary method that lets us add an empty item
    // into the subjects array so we have the same object ordering
    // as the viewer without having to necessarily write a state
    // object to do the job.
    public void AddDummy()
    {
        subjects[nSubjects] = null;
        ++nSubjects;
    }

    public void Remove(AttributeSubject subject)
    {
        subject.Detach(this);
    }

    public void SetRemoteProcess(RemoteProcess rp)
    {
        viewer = rp;
        viewerInit = true;
    }

    public void SetRemoteInfo(CommunicationHeader remoteInfo)
    {
        header.SetRemoteInfo(remoteInfo);
        message.SetRemoteInfo(remoteInfo);
        input.SetRemoteInfo(remoteInfo);
    }

    public void SetVerbose(boolean val)
    {
        verbose = val;
    }

    private synchronized void PrintMessage(String msg)
    {
        if(verbose)
            System.out.println(msg);
    }

    public synchronized void Update(AttributeSubject subject)
    {
        // Write the message
    	VisitMessage visitMessage = new VisitMessage(subject,message.remoteInfo);
    	messageOutbox.add(visitMessage);
    	PrintMessage("Xfer::Update: "+visitMessage);
    	SendMessages();
    }
    
    private void SendMessages() {
    	VisitMessage queuedMessage = messageOutbox.peek();
    	while (queuedMessage!=null && viewerInit){
	        // Write the message header
	        header.WriteInt(queuedMessage.getOpCode());
	        header.WriteInt(queuedMessage.getDataLength());

	        // Write to the socket.
            try
            {
                int hlen = viewer.DirectWrite(header.GetBytes());
                int mlen = viewer.DirectWrite(queuedMessage.getMessageData().GetBytes());
                PrintMessage("Sent " + (hlen+mlen) + " bytes to viewer: "+queuedMessage);
             	messageOutbox.remove(queuedMessage);
            	messageSent.add(queuedMessage);
            }
            catch(IOException e)
            {
            	System.err.println("error writing attributeSubject "+subjects[queuedMessage.getOpCode()]+", "+e.getMessage());
            	e.printStackTrace(System.err);
            }
	
	        header.Flush();
	        message.Flush();
	        queuedMessage = messageOutbox.peek();
    	}
    }

    public void SetUpdate(boolean val) { doUpdate = val; }
    public boolean GetUpdate() { return doUpdate; }

    public void SendInterruption()
    {
    	VisitMessage visitMessage = new VisitMessage(-1,0,message);
    	PrintMessage("Xfer::SendInterruption: "+visitMessage);
    	messageOutbox.add(visitMessage);
    	SendMessages();
    }

    public synchronized boolean Process() throws LostConnectionException
    {

    	boolean retval = false;
    	VisitMessage message = inputInbox.poll();
    	while (message!=null){
    		int opcode = message.getOpCode();
    		
            if(opcode < 200 && subjects[opcode] != null)
            {
                 PrintMessage("Xfer::Process: "+message);

                 // Read the object into its local copy.
                 subjects[opcode].Read(message.getMessageData());

                 // Indicate that we want Xfer to ignore update messages if
                 // it gets them while processing the Notify.
                 SetUpdate(false);
                 subjects[opcode].Notify();
                 inputRead.add(message);
            }
            else
            {
                // Dispose of the message.
                PrintMessage("Xfer::Process: opcode="+opcode+
                             " disposed of "+length+ " bytes");
                inputJunk.add(message);
            }
            retval = true;
            
            message = inputInbox.poll();
        }

        return retval;
    }

    boolean ReadMessages(boolean bProcessImmediately) throws LostConnectionException
    {
        boolean retval = false;

        // Try and read from the viewer.
        int nbytes = viewer.CanRead();
        if(nbytes > 0)
        {
            try
            {
                byte[] b = viewer.DirectRead(nbytes);
                input.WriteByteArray(b, false);
            }
            catch(IOException e)
            {
            	e.printStackTrace();
            }
        }

        // While there are complete messages, read and process them.
        while(ReadHeader())
        {
        	VisitMessage receivedVisitMessage = new VisitMessage(opcode,length,input);
        	inputInbox.add(receivedVisitMessage);
        	PrintMessage("Xfer::ReadMessages: "+receivedVisitMessage);
 
			if (bProcessImmediately){
				Process();
			}
        	
            retval = true;
        }

        return retval;
    }
    
    public void StartProcessing()
    {
        if(!processing)
        {
            processing = true;
            Thread messageProcessingThread = new Thread(this,"Xfer Process Thread");
            messageProcessingThread.start();
            
            Runnable socketRunnable = new Runnable(){
                public void run(){
                	socketCommunicationLoop();
                }
            };

            communicating = true;
            Thread socketCommunicationThread = new Thread(socketRunnable,"Socket communication");
            socketCommunicationThread.setPriority(socketCommunicationThread.getPriority()+1);
            socketCommunicationThread.start();
         }
    }
    
    public void StopProcessing()
    {
        processing = false;
    }

    public boolean IsProcessing()
    {
        return processing;
    }

    private boolean ReadHeader()
    {
        boolean retval = false;

        // If the connection has more than the header in its buffer, try
        // reading the header. Also try if there is a stored header. That
        // means that we've tried to read for this operation before. Maybe
        // the message is all there this time.
        if((input.Size() >= messageHeaderSize) || haveStoredHeader)
        {
            if(!haveStoredHeader)
            {
                opcode = input.ReadInt();
                length = input.ReadInt();
            }

            // Check the message size against the size of what's actually
            // arrived in the buffer.
            retval = (length <= input.Size());
            haveStoredHeader = !retval;
        }

       return retval;
    }

    //
    // This is a thread callback function that processes queued messages from the
    // viewer (stored in inputInbox queue)
    //
    public void run()
    {
		PrintMessage("Starting Process Thread");
        int idlecount = 0;
        Yielder y = new Yielder(2000);
        while(processing && viewerInit)
        {
            try
            {
                if(!Process())
                    y.yield();
                else
                    y.reset();
            }
            catch(LostConnectionException e3)
            {
                processing = false;
            }
            catch(java.lang.InterruptedException e)
            {
                processing = false;
            }
        }
        PrintMessage("Ended Process Thread");
    }
    
    //
    // This is a thread callback function that reads back from the viewer
    // and fills the inputInbox queue.
    //
    private void socketCommunicationLoop(){
    	
       	PrintMessage("Socket communication thread started");
       	while (communicating){
       		//
       		// read all pending messages
       		//
       		if (viewerInit){
	       		try {
		    		ReadMessages(false);
	       		} catch (LostConnectionException e){
	       			e.printStackTrace();
	       		} catch (Exception e){
	       			e.printStackTrace(System.out);
	       		}
	       		
//	       		try {
//	       			SendMessages();
//	       		} catch (Exception e){
//	       			e.printStackTrace();
//	       		}
       		}
       		
    		try {
    			Thread.sleep(100);
    		}catch (InterruptedException e){	
    		}
    	}
       	PrintMessage("Socket communication thread stopped");
    }


    public class VisitMessage {
    	private long timestamp;
    	private int opCode;
    	private CommunicationBuffer messageData;
    	
    	//
    	// incoming messages
    	//
    	public VisitMessage(int opCode, int length, CommunicationBuffer inputBuffer){
    		this.opCode = opCode;
    		
    		// grab data from the CommunicationBuffer
    		if (inputBuffer.length < length){
    			throw new RuntimeException("failed to create VisitMessage ... CommunicationBuffer doesn't have correct length");
    		}
    		messageData = new CommunicationBuffer();
    		if (messageData.nAlloc < length){
    			messageData.nAlloc = length;
    			messageData.buf = new byte[length];
    		}
    		System.arraycopy(inputBuffer.GetBytes(),0,messageData.buf,0,length);
    		inputBuffer.Shift(length);
    		messageData.length = length;
    		messageData.remoteInfo = inputBuffer.remoteInfo;
    		
    		this.timestamp = System.currentTimeMillis();
    	}
    	
    	//
    	// outgoing messages
    	//
    	public VisitMessage(AttributeSubject subject, CommunicationHeader communicationHeader){
    	    this.opCode = subject.GetAttributeId();
    		messageData = new CommunicationBuffer();
    		messageData.remoteInfo = communicationHeader;
    		subject.Write(messageData);
    		
    		this.timestamp = System.currentTimeMillis();
    	}
    	
    	public CommunicationBuffer getMessageData(){
    		return messageData;
    	}
    	
    	public int getOpCode(){
    		return opCode;
    	}
    	
    	public int getDataLength(){
    		return messageData.length;
    	}
    	
    	public long getTimestamp(){
    		return timestamp;
    	}
    	
    	public String toString(){
    		String className = "?";
    		if (opCode>=0 && opCode<subjects.length && subjects[opCode]!=null){
    			className = subjects[opCode].GetClassName();
    		}
    		return "VisitMessage(opcode="+opCode+", length="+getDataLength()+", class="+className+")";
    	}
    }

    final static int sizeof_int = 4;
    final static int messageHeaderSize = 2*sizeof_int;
    
    private boolean             doUpdate;
    private CommunicationBuffer header;
    
    private CommunicationBuffer message;
    private ConcurrentLinkedQueue<VisitMessage> messageOutbox = new ConcurrentLinkedQueue<VisitMessage>();
    private ConcurrentLinkedQueue<VisitMessage> messageSent = new ConcurrentLinkedQueue<VisitMessage>();

    private CommunicationBuffer input;
    private ConcurrentLinkedQueue<VisitMessage> inputInbox = new ConcurrentLinkedQueue<VisitMessage>();
    private ConcurrentLinkedQueue<VisitMessage> inputRead = new ConcurrentLinkedQueue<VisitMessage>();
    private ConcurrentLinkedQueue<VisitMessage> inputJunk = new ConcurrentLinkedQueue<VisitMessage>();

    private AttributeSubject[]  subjects;
    private int                 nSubjects;
    private RemoteProcess       viewer;
    private boolean             viewerInit;
    private boolean             haveStoredHeader;
    private int                 length;
    private int                 opcode;
    private boolean             processing;
    private boolean             communicating;
    private boolean             verbose;
}
