package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.PopupGenerator;

/*
 * This dialog is used to input file series.
 * The file series are either time series or Z series.
 * For time series, the total time span and the index for recovery need to be specified.
 */
public class MultiFileInputDialog extends JDialog implements ActionListener
{
	VirtualFrapMainFrame parent = null;
	
	private JRadioButton zSeries_radioButton = null;
	private JRadioButton tSeries_radioButton = null;
	private ButtonGroup butGroup = null;
	private JLabel zSampleIntervalLabel = null;
	private JTextField zSampleIntervalTextField = null;
	private JLabel tSampleIntervalLabel = null;
	private JTextField tSampleIntervalTextField = null;
	private JTextArea fileTextArea = null;
	private JScrollPane scroll = null;
	private JButton inputFileButton = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	
	private File[] files = null;
	private double zInterval, tInterval;
		
	public MultiFileInputDialog(VirtualFrapMainFrame arg_parent)
	{
		super(arg_parent, "File Series Input", true);
		parent = arg_parent;
		initialize();
	}
	
	private void initialize()
	{
		Container contentPane = getContentPane();
	    contentPane.setLayout(new BorderLayout());
	    
	    zSeries_radioButton = new JRadioButton("Z Series");
	    zSeries_radioButton.addActionListener(this);
		tSeries_radioButton = new JRadioButton("Time Series");
		tSeries_radioButton.setSelected(true);
		tSeries_radioButton.addActionListener(this);
		butGroup = new ButtonGroup();
		butGroup.add(zSeries_radioButton);
		butGroup.add(tSeries_radioButton);
		zSampleIntervalLabel = new JLabel("  Z Sample Intv. (um)");
		zSampleIntervalTextField = new JTextField(8);
		zSampleIntervalTextField.setText("0.01");
		zSampleIntervalTextField.setEnabled(false);
		tSampleIntervalLabel = new JLabel("  Time Sample Intv. (s)");
		tSampleIntervalTextField = new JTextField(8);
		tSampleIntervalTextField.setText("0.01");
		
		fileTextArea = new JTextArea(20,10);
		fileTextArea.setEditable(false);
		scroll = new JScrollPane(fileTextArea);
		scroll.setAutoscrolls(true);
		inputFileButton = new JButton("Choose Files");
		inputFileButton.addActionListener(this);
		okButton = new JButton("O K");
		okButton.addActionListener(this);
		cancelButton = new JButton ("Cancel");
		cancelButton.addActionListener(this);
	    
		//separate to leftPanel, rightPanel and botPanel
		//leftPanel puts those buttons and textfields
		//rightPanel puts testarea and the inputfile button
		//botPanel puts ok and cancel buttons
		JPanel leftPanel = new JPanel(new GridLayout(0,1));
		leftPanel.add(tSeries_radioButton);
	    JPanel p1 = new JPanel(new GridLayout(0,2)); // put time interval label and text field togeter
		p1.add(tSampleIntervalLabel);
		p1.add(tSampleIntervalTextField);
		leftPanel.add(p1);
		leftPanel.add(new JLabel(""));
		leftPanel.add(zSeries_radioButton);
		p1 = new JPanel(new GridLayout(0,2)); // put z interval label and text field together
		p1.add(zSampleIntervalLabel);
		p1.add(zSampleIntervalTextField);
		leftPanel.add(p1);
				
		JPanel rightPanel = new JPanel(new BorderLayout());
		JPanel fileTextPanel = new JPanel(new BorderLayout());
		fileTextPanel.add(scroll, BorderLayout.CENTER);
		fileTextPanel.setBorder(new TitledBorder(new EtchedBorder(),"Files to be input:", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11)));
		rightPanel.add(fileTextPanel, BorderLayout.CENTER);
		JPanel p2 = new JPanel(new BorderLayout());
		p2.add(new JLabel(" "), BorderLayout.WEST);
		p2.add(inputFileButton, BorderLayout.CENTER);
		p2.add(new JLabel(" "), BorderLayout.EAST);
		rightPanel.add(p2, BorderLayout.SOUTH);
		
		// put leftPanel and rightPanel together
		JPanel upPanel = new JPanel(new GridLayout(0,2));
		upPanel.add(leftPanel);
		upPanel.add(rightPanel);
		
		//create botPanel
		JPanel botPanel = new JPanel(new GridLayout(0,1));
		JPanel p3 = new JPanel(new GridLayout(0,5));
		p3.add(new JLabel(" "));
		p3.add(okButton);
		p3.add(new JLabel(" "));
		p3.add(cancelButton);
		p3.add(new JLabel(" "));
		botPanel.add(new JLabel(" "));
		botPanel.add(p3);
				
		contentPane.add(upPanel, BorderLayout.CENTER);
		contentPane.add(botPanel, BorderLayout.SOUTH);
		    
	    setSize(440,200);
	}

	public void actionPerformed(ActionEvent evt)
	{
		if(evt.getSource() instanceof JButton && ((JButton)evt.getSource()).equals(inputFileButton))
		{
			int option = VirtualFrapLoader.multiOpenFileChooser.showOpenDialog(this);
			if (option == JFileChooser.APPROVE_OPTION) 
			{
			    files = VirtualFrapLoader.multiOpenFileChooser.getSelectedFiles();
			}
			String fileString = "";
			for(int i =0; i < files.length; i++)
			{
				fileString = fileString + files[i].getName()+"\n";
			}
			fileTextArea.setText(fileString);
		}
		else if(evt.getSource() instanceof JButton && ((JButton)evt.getSource()).equals(okButton))
		{
			if((zSeries_radioButton.isSelected() || tSeries_radioButton.isSelected()) &&  checkValidity().equals(""))
			{
				if(files != null && files.length > 0)
				{
					try{
					VirtualFrapMainFrame.frapStudyPanel.load(files, new FRAPStudyPanel.MultiFileImportInfo(tSeries_radioButton.isSelected(), tInterval, zInterval));
					}catch(Exception e){
						PopupGenerator.showErrorDialog(this, e.getMessage());
					}
				}
				this.setVisible(false);
			}
			else
			{
				DialogUtils.showErrorDialog(this, "Error: " + checkValidity());
			}
		}
		else if(evt.getSource() instanceof JButton && ((JButton)evt.getSource()).equals(cancelButton))
		{
			this.setVisible(false);
		}
		else if(evt.getSource() instanceof JRadioButton && ((JRadioButton)evt.getSource()).equals(zSeries_radioButton))
		{
			zSampleIntervalTextField.setEnabled(true);
			tSampleIntervalTextField.setEnabled(false);
		}
		else if(evt.getSource() instanceof JRadioButton && ((JRadioButton)evt.getSource()).equals(tSeries_radioButton))
		{
			tSampleIntervalTextField.setEnabled(true);
			zSampleIntervalTextField.setEnabled(false);
		}
	}
	
	private String checkValidity()
	{
		if(tSeries_radioButton.isSelected())
		{
			try{
				tInterval = Double.parseDouble(tSampleIntervalTextField.getText());
			}catch(Exception e)
			{
				return e.getMessage(); 
			}
		}
		else if(zSeries_radioButton.isSelected())
		{
			try{
				zInterval = Double.parseDouble(zSampleIntervalTextField.getText());
			}catch(Exception e)
			{
				return e.getMessage(); 
			}
		}
		
		return "";
	}
}
