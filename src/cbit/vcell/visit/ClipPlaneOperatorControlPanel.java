package cbit.vcell.visit;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.Component;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Box;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.gui.DialogUtils;

import llnl.visit.Plot;
import llnl.visit.operators.ClipAttributes;

@SuppressWarnings("serial")
public class ClipPlaneOperatorControlPanel extends JPanel {

	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	ThreePlaneSpecPanel threePlaneSpecPanel = new ThreePlaneSpecPanel();
	private SphericalSpecPanel sphericalSpecPanel = new SphericalSpecPanel();
	JPanel specHolderPanel = new JPanel(new CardLayout());
	
	double[] center;
	
	
	private boolean newClip = true;
	private Plot thisPlot;
	private VisitSession thisSession;
	private ClipAttributes lastAppliedClipAttributes, lastPolledClipAttributes, latestViewerClipAttributes;
	private JRadioButton rdbtnFast;
	private JRadioButton rdbtnAccurate;
	private JRadioButton rdbtnPlane; 
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() { 
			public void run() {
				try {
					JFrame frame = new JFrame();
					ClipPlaneOperatorControlPanel panel = new ClipPlaneOperatorControlPanel();
					frame.getContentPane().add(panel);
					frame.setSize(600,400);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
//	public ClipPlaneOperatorControlPanel(Plot thisPlot, VisitSession thisSession){
//		
//	}
	
	public ClipPlaneOperatorControlPanel() {
		setBounds(100, 100, 651, 548);
		setLayout(new GridBagLayout());
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		setLayout(gbl_contentPane);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblQuality = new JLabel("Quality:");
		GridBagConstraints gbc_lblQuality = new GridBagConstraints();
		gbc_lblQuality.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuality.gridx = 0;
		gbc_lblQuality.gridy = 0;
		panel.add(lblQuality, gbc_lblQuality);
		
		rdbtnFast = new JRadioButton("Fast");
		buttonGroup.add(rdbtnFast);
		GridBagConstraints gbc_rdbtnFast = new GridBagConstraints();
		gbc_rdbtnFast.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnFast.gridx = 1;
		gbc_rdbtnFast.gridy = 0;
		panel.add(rdbtnFast, gbc_rdbtnFast);
		
		rdbtnAccurate = new JRadioButton("Accurate");
		buttonGroup.add(rdbtnAccurate);
		GridBagConstraints gbc_rdbtnAccurate = new GridBagConstraints();
		gbc_rdbtnAccurate.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnAccurate.gridx = 2;
		gbc_rdbtnAccurate.gridy = 0;
		panel.add(rdbtnAccurate, gbc_rdbtnAccurate);
		
		JLabel lblSliceType = new JLabel("Slice Type:");
		GridBagConstraints gbc_lblSliceType = new GridBagConstraints();
		gbc_lblSliceType.insets = new Insets(0, 0, 0, 5);
		gbc_lblSliceType.gridx = 0;
		gbc_lblSliceType.gridy = 1;
		panel.add(lblSliceType, gbc_lblSliceType);
		
		rdbtnPlane = new JRadioButton("Plane");
		rdbtnPlane.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (rdbtnPlane.isSelected())
				{
					
					((CardLayout)specHolderPanel.getLayout()).first(specHolderPanel);
					
					
				}
			}
		});
		buttonGroup_1.add(rdbtnPlane);
		GridBagConstraints gbc_rdbtnPlane = new GridBagConstraints();
		gbc_rdbtnPlane.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnPlane.gridx = 1;
		gbc_rdbtnPlane.gridy = 1;
		panel.add(rdbtnPlane, gbc_rdbtnPlane);
		
		final JRadioButton rdbtnSphere = new JRadioButton("Sphere");
		rdbtnSphere.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (rdbtnSphere.isSelected())
				{
					
					((CardLayout)specHolderPanel.getLayout()).last(specHolderPanel);
				}
			}
		});
		buttonGroup_1.add(rdbtnSphere);
		GridBagConstraints gbc_rdbtnSphere = new GridBagConstraints();
		gbc_rdbtnSphere.gridx = 2;
		gbc_rdbtnSphere.gridy = 1;
		panel.add(rdbtnSphere, gbc_rdbtnSphere);
		
		
		specHolderPanel.add(threePlaneSpecPanel,"threePlaneSpecPanel");
		specHolderPanel.add(sphericalSpecPanel,"sphericalSpecPanel");
		
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		add(specHolderPanel, gbc_panel_1);
		
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 2;
		add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		JCheckBox chckbxInverse = new JCheckBox("Inverse");
		GridBagConstraints gbc_chckbxInverse = new GridBagConstraints();
		gbc_chckbxInverse.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxInverse.gridx = 0;
		gbc_chckbxInverse.gridy = 0;
		panel_2.add(chckbxInverse, gbc_chckbxInverse);
		
		JLabel lblPlaneToolControls = new JLabel("Plane tool controls:");
		GridBagConstraints gbc_lblPlaneToolControls = new GridBagConstraints();
		gbc_lblPlaneToolControls.insets = new Insets(0, 0, 5, 5);
		gbc_lblPlaneToolControls.gridx = 0;
		gbc_lblPlaneToolControls.gridy = 1;
		panel_2.add(lblPlaneToolControls, gbc_lblPlaneToolControls);
		
		JRadioButton rdbtnNone = new JRadioButton("None");
		GridBagConstraints gbc_rdbtnNone = new GridBagConstraints();
		gbc_rdbtnNone.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnNone.gridx = 0;
		gbc_rdbtnNone.gridy = 2;
		panel_2.add(rdbtnNone, gbc_rdbtnNone);
		
		JRadioButton rdbtnPlane_1 = new JRadioButton("Plane 1");
		GridBagConstraints gbc_rdbtnPlane_1 = new GridBagConstraints();
		gbc_rdbtnPlane_1.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnPlane_1.gridx = 1;
		gbc_rdbtnPlane_1.gridy = 2;
		panel_2.add(rdbtnPlane_1, gbc_rdbtnPlane_1);
		
		JRadioButton rdbtnPlane_2 = new JRadioButton("Plane 2");
		GridBagConstraints gbc_rdbtnPlane_2 = new GridBagConstraints();
		gbc_rdbtnPlane_2.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnPlane_2.gridx = 2;
		gbc_rdbtnPlane_2.gridy = 2;
		panel_2.add(rdbtnPlane_2, gbc_rdbtnPlane_2);
		
		JRadioButton rdbtnPlane_3 = new JRadioButton("Plane 3");
		GridBagConstraints gbc_rdbtnPlane_3 = new GridBagConstraints();
		gbc_rdbtnPlane_3.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnPlane_3.gridx = 3;
		gbc_rdbtnPlane_3.gridy = 2;
		panel_2.add(rdbtnPlane_3, gbc_rdbtnPlane_3);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 0, 5);
		gbc_horizontalStrut.gridx = 4;
		gbc_horizontalStrut.gridy = 2;
		panel_2.add(horizontalStrut, gbc_horizontalStrut);
		
		JButton btnApply = new JButton("Apply");
		GridBagConstraints gbc_btnApply = new GridBagConstraints();
		gbc_btnApply.insets = new Insets(0, 0, 0, 5);
		gbc_btnApply.gridx = 5;
		gbc_btnApply.gridy = 2;
		panel_2.add(btnApply, gbc_btnApply);
		
		JButton btnClose = new JButton("Close");
		GridBagConstraints gbc_btnClose = new GridBagConstraints();
		gbc_btnClose.gridx = 6;
		gbc_btnClose.gridy = 2;
		panel_2.add(btnClose, gbc_btnClose);
		
		//initializeValues();
	}

    public void initializeVisitSessionInfo(Plot thisPlot, VisitSession thisSession){
    	this.thisPlot=thisPlot;
    	this.thisSession=thisSession;
    }
	
	private void initializeValues(){
		lastPolledClipAttributes=getExistingClipAttributesIfAny();
		if (newClip){
			lastPolledClipAttributes.SetCenter(0, 0, 0);  //TODO: find a way to set the X coordinate center in the middle of the x coordinate's range
		}
		
		if (lastPolledClipAttributes.GetQuality()==1) rdbtnAccurate.setSelected(true);
		if (lastPolledClipAttributes.GetFuncType()==0) rdbtnPlane.setSelected(true);
	}
	
	
	private void updateControlPanelValues(ClipAttributes clipAtts){
		if (lastAppliedClipAttributes==null) lastAppliedClipAttributes = new ClipAttributes();
		if (this.rdbtnPlane.isSelected()) 
			lastAppliedClipAttributes.SetFuncType(0);
		else
			lastAppliedClipAttributes.SetFuncType(1);
		
		double[] center=new double[3];
		double[] radius=new double[3];
		
		center=getTripletValues(sphericalSpecPanel.getCenterAsString());
		radius=getTripletValues(sphericalSpecPanel.getRadiusAsString());
	
	}
	
	private void applyNewValues(){
		
	}
	
	private ClipAttributes getExistingClipAttributesIfAny(){
		if (thisPlot.GetOperatorNames().contains("Clip")){
			newClip=false;
			int index = thisPlot.GetOperatorNames().indexOf("Clip");
			return (ClipAttributes)thisSession.getViewerState().GetOperatorAttributes(index);
		}
		
		else {
			newClip=true;
			return new ClipAttributes();
		}
	}
	
	double[] getTripletValues(String s){
		Vector<Double> returnVector = new Vector<Double>();
		double[] returnArray = new double[3];
	
		String numString = null;
		try {
			
			StringTokenizer stringTokenizer = new StringTokenizer(s," ");
			while (stringTokenizer.hasMoreElements()){
				returnVector.add(Double.valueOf(stringTokenizer.nextToken()));
			}
			
//			s=s.trim();
//			
//			int s1=s.indexOf(" ");
//			numString=s.substring(0,(s1-1));
//			Double aDouble = Double.parseDouble(numString);
//			returnVector.add(aDouble);
//			s=s.substring(s1+1);
//			s=s.trim();
//			s1=s.indexOf(" ");
//			numString=s.substring(0,(s1-1));
//			aDouble = Double.parseDouble(numString);
//			returnVector.add(aDouble);
//			s=s.substring(s1+1);
//			s=s.trim();
//			aDouble = Double.parseDouble(s);
//			returnVector.add(aDouble);
			
			returnArray[0]=returnVector.elementAt(0);
			returnArray[1]=returnVector.elementAt(1);
			returnArray[2]=returnVector.elementAt(2);
			
			return(returnArray);
		}
		
		catch (Exception e) {
			e.printStackTrace();
			DialogUtils.showErrorDialog(this, "Please enter three numbers separated by spaces");
			return(null);
		}
		
	
		
		
		
	}
	
}
