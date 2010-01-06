package cbit.vcell.microscopy.batchrun.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.io.File;
import java.util.Hashtable;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.MicroscopyXmlReader;
import cbit.vcell.microscopy.gui.FRAPDataPanel;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;

public class BatchRunDisplayPanel extends JPanel
{
	private JSplitPane rightSplit = null;
	private FRAPDataPanel frapDataPanel = null;
	private JobStatusPanel jobStatusPanel = null;
	
	//constructor
	public BatchRunDisplayPanel()
	{
		super();
	    rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getFRAPDataPanel(), getJobStatusPanel());
	    rightSplit.setDividerSize(2);
	    rightSplit.setDividerLocation(Math.round(Toolkit.getDefaultToolkit().getScreenSize().height*1/2));
	    setLayout(new BorderLayout());
	    add(rightSplit, BorderLayout.CENTER);
	    setBorder(new EmptyBorder(0,0,0,0));
	    //addMouseListener(th);
	    setVisible(true);
	}

	public FRAPDataPanel getFRAPDataPanel() {
		if (frapDataPanel == null) 
		{
			frapDataPanel = new FRAPDataPanel(false);//the frap data panel in the main frame is not editable
			//set display mode
			frapDataPanel.adjustComponents(OverlayEditorPanelJAI.DISPLAY_WITH_ROIS);
		}
		return frapDataPanel;
	}
	
	public JobStatusPanel getJobStatusPanel()
	{
		if(jobStatusPanel == null)
		{
			jobStatusPanel = new JobStatusPanel();
		}
		return jobStatusPanel;
	}
	
	public static void main(java.lang.String[] args) {
		try {
			try{
		    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    }catch(Exception e){
		    	throw new RuntimeException(e.getMessage(),e);
		    }
			javax.swing.JFrame frame = new javax.swing.JFrame();
			BatchRunDisplayPanel aPanel = new BatchRunDisplayPanel();
			frame.add(aPanel);
			frame.pack();
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}
	
}
