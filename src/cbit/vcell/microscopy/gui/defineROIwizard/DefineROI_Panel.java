package cbit.vcell.microscopy.gui.defineROIwizard;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSeparator;

import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.gui.FRAPDataPanel;

public class DefineROI_Panel extends JPanel
{
	DefineROITopTitlePanel topPanel = null;
	FRAPDataPanel centerPanel = null;
	
	public DefineROI_Panel() {
		super();
		initialize();
	}

	public void initialize()
	{
		setLayout(new BorderLayout());
		JPanel tPanel = new JPanel(new BorderLayout());
		tPanel.add(getTopPanel(), BorderLayout.NORTH);
		tPanel.add(new JSeparator(), BorderLayout.CENTER);
		add(tPanel, BorderLayout.NORTH);
		add(getCenterPanel(), BorderLayout.CENTER);
	}
	
	public FRAPDataPanel getCenterPanel()
	{
		if(centerPanel == null)
		{
			centerPanel = new FRAPDataPanel();
		}
		return centerPanel;
	}
	
	public DefineROITopTitlePanel getTopPanel()
	{
		if(topPanel == null)
		{
			topPanel = new DefineROITopTitlePanel();
		}
		return topPanel;
	}
	
	public void setFRAPStudy(FRAPStudy fStudy)
	{
		centerPanel.setFrapStudy(fStudy, true);
	}
	
	public void adjustComponents(int choice)
	{
		topPanel.adjustComponent(choice);
		centerPanel.adjustComponents(choice);
	}
	
	public void setCurrentROI(String roiName)
	{
		centerPanel.setCurrentROI(roiName);
	}
}
