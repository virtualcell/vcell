package cbit.vcell.microscopy.batchrun.gui;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import cbit.vcell.microscopy.gui.estparamwizard.MSEPanel;

public class BatchRunResultsPanel extends JPanel
{
	public final static String MODEL_TYPE_PREFIX = "Selected Model Type : ";
	private BatchRunResultsParameterPanel batchRunParamPanel = null;
	private BatchRunMSEPanel batchRunMSEPanel = null;
	private JScrollPane scrollPane = null;
	private JLabel modelTypeLabel = null;
	private JPanel centerPanel = null;
	private String modelType = "";
	
	public BatchRunResultsPanel()
	{
		super();
		setLayout(new BorderLayout());
		add(getModelTypeLabel(), BorderLayout.NORTH);
		
		scrollPane = new JScrollPane(getCenterPanel());
		scrollPane.setAutoscrolls(true);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    	//add scrollpane to the panel
		add(scrollPane, BorderLayout.CENTER);
	}
	
	public JLabel getModelTypeLabel()
	{
		if(modelTypeLabel == null)
		{
			modelTypeLabel = new JLabel(MODEL_TYPE_PREFIX + getModelType()); 
		}
		return modelTypeLabel;
	}
	
	public String getModelType()
	{
		return modelType;
	}
	
	public void setModelType(String arg_modelType)
	{
		modelType = arg_modelType;
	}
	
	private JPanel getCenterPanel()
	{
		if(centerPanel == null)
		{
			centerPanel= new JPanel();
			centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
			centerPanel.add(getBatchRunResultsParameterPanel());
			centerPanel.add(getBatchRunMSEPanel());
		}
		return centerPanel;
	}
	
	public BatchRunResultsParameterPanel getBatchRunResultsParameterPanel()
	{
		if(batchRunParamPanel == null)
		{
			batchRunParamPanel= new BatchRunResultsParameterPanel();
		}
		return batchRunParamPanel;
	}
	
	public BatchRunMSEPanel getBatchRunMSEPanel()
	{
		if(batchRunMSEPanel == null)
		{
			batchRunMSEPanel = new BatchRunMSEPanel();
		}
		return batchRunMSEPanel;
	}
	
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			BatchRunResultsPanel aPanel = new BatchRunResultsPanel();
			frame.setContentPane(aPanel);
//			frame.pack();
			frame.setSize(900,800);
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
