package org.vcell.util.gui;

import java.awt.Font;
import java.util.EventObject;

import javax.swing.JDialog;

import org.vcell.util.ProgressDialogListener;


/**
 * test {@link DefiniteProgressDialog}
 */
public class DefiniteProgressTest implements ProgressDialogListener {
	/**
	 * seconds, percent
	 */
	private static final Font font = new Font("Dialog", Font.PLAIN, 12);
	private static int UPDATE_SCHEDULE[][] = {
		{1,1},
		{2,1},
		{3,1},
		{4,1},
		{5,1},
		{6,1},
		{7,1},
		{8,1},
		{9,1},
		{10,2},
		{11,5},
		{12,10},
		{13,20},
		{14,50},
		{15,90},
		{16,91},
		{17,92},
		{18,93},
		{19,94},
		{20,95},
		{21,96},
		{22,97},
		{23,98},
		{24,99},
		{25,100},
	};
	
	private DefiniteProgressDialog progDialog; 

	public static void main(String[] args)  {
		try {
			new DefiniteProgressTest();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public DefiniteProgressTest( ) throws InterruptedException {
		progDialog = new DefiniteProgressDialog(null);
		progDialog.setFont(font);
		progDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		progDialog.setProgressBarString("Working");
		progDialog.setMessage("Hi!");
		progDialog.setModal(true);
		progDialog.pack();
		new Update(progDialog).start( );
		progDialog.setToVisible();
		Thread.sleep(30000);
		progDialog.dispose(); //do this to get the AWT thread to exit
	}
	
	@Override
	public void cancelButton_actionPerformed(EventObject newEvent) {
		progDialog.setVisible(false);
	}

	
	static class Update extends Thread {
		final DefiniteProgressDialog progressDialog;
		int current;

		public Update(DefiniteProgressDialog ipd) {
			setName("update message");
			setDaemon(true);
			this.progressDialog = ipd;
			current = 0;
		}


		@Override
		public void run() {
			try {
				final long start = System.currentTimeMillis(); 
				for (;;) {
					if (current < UPDATE_SCHEDULE.length) {
						int seconds = UPDATE_SCHEDULE[current][0];
						int percent = UPDATE_SCHEDULE[current][1];
						
						final long now = System.currentTimeMillis();
						long endTime = start + seconds * 1000;
						long waitTime = endTime - now;
						if (waitTime > 0 ) { //depending on Thread startup time, this can be negative
							Thread.sleep(waitTime);
						}
						progressDialog.setMessage(Integer.toString(percent) + " percent");
						progressDialog.setProgress(percent);
						System.err.println("set " + percent);
						++current;
					}
					else {
						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
