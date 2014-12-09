package org.vcell.vmicro.workflow;

import java.awt.Dimension;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.vcell.optimization.ProfileData;
import org.vcell.optimization.gui.ConfidenceIntervalPlotPanel;
import org.vcell.optimization.gui.ProfileDataPanel;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.scratch.FRAPOptimizationUtils;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

public class DisplayProfileLikelihoodPlots extends Task {
	
	//
	// inputs
	//
	public final DataInput<ProfileData[]> profileData;
	public final DataInput<String> title;
		
	//
	// outputs
	//
	public final DataHolder<Boolean> displayed;

	
	public DisplayProfileLikelihoodPlots(String id){
		super(id);
		profileData = new DataInput<ProfileData[]>(ProfileData[].class,"profileData", this);
		title = new DataInput<String>(String.class,"title",this);
		displayed = new DataHolder<Boolean>(Boolean.class,"displayed",this);
		addInput(profileData);
		addInput(title);
		addOutput(displayed);
	}

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		WindowListener listener = new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				displayed.setDirty();
				updateStatus();
			};
		};
		displayProfileLikelihoodPlots(profileData.getData(), title.getData(), listener);
		displayed.setData(true);
	}
	
	public static void displayProfileLikelihoodPlots(ProfileData[] profileData, String title, WindowListener listener){
		//put plotpanes of different parameters' profile likelihoods into a base panel
		JPanel basePanel= new JPanel();
    	basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));
		for(int i=0; i<profileData.length; i++)
		{
			ConfidenceIntervalPlotPanel plotPanel = new ConfidenceIntervalPlotPanel();
			plotPanel.setProfileSummaryData(FRAPOptimizationUtils.getSummaryFromProfileData(profileData[i]));
			plotPanel.setBorder(new EtchedBorder());
			String paramName = "";
			if(profileData[i].getProfileDataElements().size() > 0)
			{
				paramName = profileData[i].getProfileDataElements().get(0).getParamName();
			}
			ProfileDataPanel profileDataPanel = new ProfileDataPanel(plotPanel, paramName);
			basePanel.add(profileDataPanel);
		}
		JScrollPane scrollPane = new JScrollPane(basePanel);
    	scrollPane.setAutoscrolls(true);
    	scrollPane.setPreferredSize(new Dimension(620, 600));
    	scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    	//show plots in a dialog
		JFrame jframe = new JFrame();
		jframe.setTitle(title);
		jframe.getContentPane().add(scrollPane);
		jframe.setSize(500,500);
		jframe.addWindowListener(listener);
		jframe.setVisible(true);
 	}

	
}
