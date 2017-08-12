package org.vcell.util.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.util.EventObject;

import javax.swing.JDialog;

import org.vcell.util.ProgressDialogListener;


/**
 * test {@link LinearDefiniteProgressDialog}
 */
public class LinearDefiniteProgressTest implements ProgressDialogListener {
	/**
	 * seconds, percent
	 */
	private static final Font font = new Font("Dialog", Font.PLAIN, 12);
	private static int UPDATE_SCHEDULE[][] = {
		{1,1},
		{2,2},
		{3,3},
		{4,4},
		{5,5},
		{6,10},
		{7,20},
		{8,30},
		{9,40},
		{10,50},
		{11,60},
		{12,70},
		{13,80},
		{14,85},
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
	
	private LinearDefiniteProgressDialog progDialog; 

	public static void main(String[] args)  {
		try {
			new LinearDefiniteProgressTest();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public LinearDefiniteProgressTest( ) throws InterruptedException {
		progDialog = new LinearDefiniteProgressDialog(null);
		progDialog.setFont(font);
		progDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		progDialog.setPreferredSize(new Dimension(350,120));
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
		final LinearDefiniteProgressDialog progressDialog;
		int current;

		public Update(LinearDefiniteProgressDialog ipd) {
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
//						progressDialog.setMessage("12345678901234567890123456789012345678901234567890abcdefghij");
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
