package org.vcell;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class ProgressDialog extends JDialog {
	
	private static final long serialVersionUID = -5906334441510649483L;
	
	private JProgressBar progressBar;
	
	public ProgressDialog(Frame owner, String title, boolean indeterminate) {
		super(owner, title, true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		initializeContentPane(indeterminate);
		pack();
		setLocationRelativeTo(owner);
	}
	
	public void setProgress(int progress) {
		progressBar.setValue(progress);
	}
	
	public void setProgressText(String text) {
		progressBar.setString(text);
	}
	
	public void setTask(Task<?, String> task) {
		
		task.addPropertyChangeListener(propertyChangeEvent -> {
			
        	String propertyName = propertyChangeEvent.getPropertyName();
        	
        	if (propertyName.equals(Task.PROGRESS)) {
        		setProgress((Integer) propertyChangeEvent.getNewValue());
        		
        	} else if (propertyName.equals(Task.SUBTASK)) {
        		setProgressText(task.getSubtask());
        		
        	} else if (propertyName.equals(Task.STATE) && propertyChangeEvent.getNewValue() == SwingWorker.StateValue.DONE) {
        		setVisible(false);
        		dispose();
        	} 
        });
	}
	
	private void initializeContentPane(boolean indeterminate) {
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(indeterminate);
		final JOptionPane optionPane = new JOptionPane(
				progressBar, 
				JOptionPane.PLAIN_MESSAGE, 
				JOptionPane.DEFAULT_OPTION, 
				null, 
				new Object[]{});
		setContentPane(optionPane);
		setPreferredSize(new Dimension(500, 100));
	}
}
