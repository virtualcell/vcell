package cbit.vcell.microscopy.batchrun.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class MessagePanel extends JPanel
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
//			progress.setSize(100, 25);
			add(progress);
		}
//		setSize(600,35);
		setPreferredSize(new Dimension(600,30));
	}
	
	public void setProgress(int prog)
	{
		if(progress != null)
		{
			progress.setValue(prog);
			progress.updateUI();
		}
	}
	
	public void setProgressCompleted()
	{
		this.remove(progress);
		this.add(new JLabel("  Done."));
	}
}
