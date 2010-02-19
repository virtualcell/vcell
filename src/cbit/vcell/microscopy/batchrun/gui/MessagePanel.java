package cbit.vcell.microscopy.batchrun.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import cbit.vcell.client.task.ClientTaskStatusSupport;

public class MessagePanel extends JPanel implements ClientTaskStatusSupport
{
	private JLabel message = null;
	private JProgressBar progress = null;
	
	public MessagePanel(String msg, boolean bProgress)
	{
		super();
		setBackground(Color.white);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		if(msg != null)
		{
			message = new JLabel(msg);
		}
		else
		{
			message = new JLabel();
		}
		add(message);
		if(bProgress)
		{
			progress = new JProgressBar();
			progress.setMaximum(100);
			progress.setMinimum(0);
			progress.setStringPainted(true);
			add(progress);
		}
//		setSize(600,35);
		setPreferredSize(new Dimension(600,30));
	}
	
	public void setProgress(final int prog)
	{
		if(progress != null)
		{
			SwingUtilities.invokeLater(new Runnable() {
				
				public void run() {
					progress.setValue(prog);
//					progress.updateUI();
				}
			});
			
		}
	}
	
	public void setProgressCompleted()
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				MessagePanel.this.remove(progress);
				MessagePanel.this.add(new JLabel("  Done."));
			}
		});
		
	}

	public int getProgress() {
		return progress.getValue();
	}

	public boolean isInterrupted() {
		return false;
	}

	public void setMessage(String msg) {
		message.setText(msg);
	}
}
