package cbit.vcell.microscopy.batchrun.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;

/**
 * A Panel shows the information of the running status.
 * It can be turned on and off.
 * @author Tracy Li 
 * @version 1.0
 */
public class JobStatusPanel extends JPanel implements ActionListener
{
    public URL[] iconFiles = {getClass().getResource("/images/close.gif"),
    						  getClass().getResource("/images/clear.gif")};
    
    public String[] buttonLabels = {"close", "clear"};
    //icon objects for initializ the image button
    private ImageIcon[] icons = new ImageIcon[iconFiles.length];
    private JButton closeButton, clearButton;

    private JTextArea statusText = null;
    private JScrollPane statusPane = null;
    private JToolBar toolbar = null;

    public JobStatusPanel()
    {
        super();
        setLayout(new BorderLayout());
        
        add(getToolBar(),BorderLayout.WEST);
        add(getStatusPane(),BorderLayout.CENTER);
        showMessage("\n"+"Virtual FRAP Batch Run Status: NO_BatchRun_Existed.");
    } //constructor
    
    public JToolBar getToolBar()
    {
    	if(toolbar == null)
    	{
	    	toolbar=new JToolBar(JToolBar.VERTICAL);
	        //System.out.println(buttonLabels.length);
	        for (int i = 0; i < buttonLabels.length; ++i)
	        {
	            icons[i]=new ImageIcon(iconFiles[i]);
	        }
	        closeButton=new JButton(icons[0]);
	        closeButton.setMargin(new Insets(0, 0, 0, 0));
	        closeButton.setToolTipText(buttonLabels[0]);
	        closeButton.setBorderPainted(false);
	        closeButton.addActionListener(this);
	        clearButton=new JButton(icons[1]);
	        clearButton.setMargin(new Insets(0, 0, 0, 0));
	        clearButton.setToolTipText(buttonLabels[1]);
	        clearButton.setBorderPainted(false);
	        clearButton.addActionListener(this);
	       
	        toolbar.add(closeButton);
	        toolbar.add(clearButton);
	       
    	}
    	return toolbar;
       
    }//end of method setupToolBar()

    public JScrollPane getStatusPane()
    {
    	if(statusPane ==  null)
    	{
    		statusText=new JTextArea();
            statusText.setEditable(false);
            statusPane=new JScrollPane(statusText);
    	}
    	return statusPane;
    }
    
    public void showMessage(String info)
    {
        statusText.append(info);
        statusText.setCaretPosition(statusText.getDocument().getLength());
    }

    public void clearMessage()
    {
        statusText.setText("");
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
  	    if (source == closeButton)
	    {
            //make status panel invisible
        }
        else if(source == clearButton)
        {
        	clearMessage();
        }
    }
}//end of Class JobStatusPanel

