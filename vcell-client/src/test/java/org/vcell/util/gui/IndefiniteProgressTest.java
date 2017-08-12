package org.vcell.util.gui;

import java.util.EventObject;

import javax.swing.JDialog;

import org.vcell.util.ProgressDialogListener;


public class IndefiniteProgressTest implements ProgressDialogListener {
		private IndefiniteProgressDialog progDialog; 

	public static void main(String[] args)  {
		try {
			new IndefiniteProgressTest();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public IndefiniteProgressTest( ) throws InterruptedException {
		progDialog = new IndefiniteProgressDialog(null);
		//progDialog = new DefaultProgressDialog(null);
//		progDialog.addProgressDialogListener(this);
		progDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
//		progDialog.getCancelButton();
		progDialog.setProgressBarString("Working");
		//progDialog.setMessage("Something long and slow and important but slow and hopefully useful is happening here");
		progDialog.setMessage("Hi!");
		progDialog.setModal(true);
		progDialog.pack();
		new Update(progDialog).start( );
		progDialog.setToVisible();
		Thread.sleep(20000);
		progDialog.dispose(); //do this to get the AWT thread to exit
	}
	
	@Override
	public void cancelButton_actionPerformed(EventObject newEvent) {
		progDialog.setVisible(false);
	}

	
	static class Update extends Thread {
		final IndefiniteProgressDialog ipd;

		public Update(IndefiniteProgressDialog ipd) {
			setName("update message");
			setDaemon(true);
			this.ipd = ipd;
		}


		@Override
		public void run() {
			try {
				Thread.sleep(7000);
				ipd.setMessage("Something long and slow and important but slow and hopefully useful is happening here");
			//	ipd.setMessage("Time has passed");
			} catch (Exception e) {
				
			}
		}
		
		
	}

}
