package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.vcell.util.BeanUtils;
import org.vcell.util.NumberUtils;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.mapping.gui.StructureMappingTableRenderer;
import cbit.vcell.microscopy.EstimatedParameter;
import cbit.vcell.microscopy.EstimatedParameterTableModel;
import cbit.vcell.microscopy.EstimatedParameterTableRenderer;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ScopedExpression;
import cbit.vcell.units.VCUnitDefinition;

public class FRAPReacDiffEstimationGuidePanel extends JPanel {
	private JTextField bsRadiusTextField;
	private JTextField fRadiusTextField;
	private JRadioButton pureDiff = new JRadioButton("Pure Diffusion ");
	private JRadioButton effectiveDiff = new JRadioButton("Effective Diffusion");
	
	private JPanel diffTypePanel = null;
	private JRadioButton koffButton = new JRadioButton("Off Rate (K_off)");
	private JRadioButton bsButton = new JRadioButton("[BS] (C_BS)");
	private JTextField koffTextField = new JTextField(8);
	private JTextField bsTextField = new JTextField(8);
	private JButton estButton = new JButton("Estimate");
	
	private JScrollPane tableScroll = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JTable paramTable = new JTable();
	private EstimatedParameterTableModel paramTableModel = new EstimatedParameterTableModel(FRAPReacDiffEstimationGuidePanel.this);
	
	private static final double PI = 3.14159;
	private static final String piStr = "PI";
	public static final double RELATIVE_TOTAL_CONCENTRATION = 1;
	//The following parameters are used to store parameters from pure diffusion
	Double diffRate = null;
	Double mFraction = null;
	Double bwmRate = null;
	Double secDiffRate = null;
	Double secMFraction = null;
	Boolean isSecDiffusionApplied = null;
	
	EstimatedParameter[] estimatedParameters = new EstimatedParameter[17];
	public static final int IDX_PrimDiffRate	= 0;
	public static final int IDX_PrimMFraction	= 1;
	public static final int IDX_SecDiffRate	= 2;
	public static final int IDX_SecMFraction = 3;
	public static final int IDX_BleachMonitorRate	= 4;
	public static final int IDX_RelativeTotalConc = 5;
	public static final int IDX_FreePartDiffRate	= 6;
	public static final int IDX_FreePartFraction	= 7;
	public static final int IDX_FreePartConc = 8;
	public static final int IDX_ComplexDiffRate		= 9;
	public static final int IDX_ComplexFraction		= 10;
	public static final int IDX_ComplexConc = 11;
	public static final int IDX_ImmFraction   	= 12;
	public static final int IDX_BSDiffRate	=13;
	public static final int IDX_BSConc = 14;
	public static final int IDX_ReacOnRate         = 15;
	public static final int IDX_ReacOffRate    	= 16;
	
	public static final String[] paramNames = new String[]{"D_prim",
		                                                   "Frac_prim",
		                                                   "D_sec",
		                                                   "Frac_sec",
		                                                   "R_bwm",
		                                                   "C_tol",
		                                                   "D_f",
		                                                   "Frac_f",
		                                                   "C_f",
		                                                   "D_c",
		                                                   "Frac_c",
		                                                   "C_c",
		                                                   "Frac_imm",
		                                                   "D_BS",
		                                                   "C_BS",
		                                                   "K_on",
		                                                   "K_off"};
	
	public static final String[] paramDescriptions = new String[]{"Primary Diffusion Rate",
		                                                          "Primary Mobile Fraction",
		                                                          "Secondary Diffusion Rate",
		                                                          "Secondary Mobile Fraction",
		                                                          "Bleach While Monitoring Rate",
		                                                          "Relative total concentration",
		                                                          "Free Particle Diffusion Rate",
		                                                          "Free Particle fraction",
		                                                          "Free Particle Concentration",
		                                                          "Binding Complex Diffusion Rate",
		                                                          "Binding Complex fraction",
		                                                          "Binding Complex concentration",
		                                                          "Immobile Fraction",
		                                                          "Free Binding Site diffusion Rate",
		                                                          "Free Binding Site Concentration",
		                                                          "Reaction On Rate",
		                                                          "Reaction Off Rate"};
	
	public static final String freeDiffRateStr_eff = "D_prim*(1+(C_c/C_f))"; 
	public static final String complexFracStr_oneDiffComponent = "1-Frac_prim";
	//if we have two diffusion components, otherwise we'll use different exp for free diff rate, complex diff rate
	public static final String[] paramExpStr = new String[]{      "", //prim diff rate
															      "", //prim mobile fraction
															      "", //sec diff rate
															      "", //sec m fraction
															      "", //bwm rate
															      "", //total fluor. 1 = prebleachAvg / prebleachAvg (normalized by itself)
															      "D_prim", //free diff rate
															      "Frac_prim", //free m fraction
															      "C_tol*Frac_f", //free concentration
															      "D_sec", //complex diff rate
															      "Frac_sec", //complex m fraction
															      "C_tol*Frac_c", //complex concentration
															      "1.0-Frac_f-Frac_c", //immobile fraction
															      "", //binding site diff rate
															      "(K_off*C_c)/(K_on*C_f)", //binding site concentration
															      "(4.0*"+piStr+"*(D_f+D_BS)*(R_f+R_BS))/602", //reaction on rate
															      "(C_BS*K_on*C_f)/C_c"}; //reaction off rate
	
	public FRAPReacDiffEstimationGuidePanel() {
		super();
		setPreferredSize(new Dimension(580, 450));
		final GridBagLayout gridBagLayout = new GridBagLayout();
		this.setLayout(new GridBagLayout());
		gridBagLayout.columnWidths = new int[] {7,0};
		gridBagLayout.rowHeights = new int[] {7,7,7,7,7,7,0,0,7,7};

		//the top panel
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.columnWidths = new int[] {0,7,7,7};
		diffTypePanel = new JPanel(gridBagLayout_2);
		diffTypePanel.setBorder(new LineBorder(Color.gray, 1, false));
		JLabel diffTypeLabel = new JLabel();
		diffTypeLabel.setText("The redistribution of a single population is considered as  a result of: ");
		final GridBagConstraints gridBagConstraints_dtl = new GridBagConstraints();
		gridBagConstraints_dtl.anchor = GridBagConstraints.WEST;
		gridBagConstraints_dtl.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_dtl.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_dtl.gridy = 0;
		gridBagConstraints_dtl.gridx = 0;
		gridBagConstraints_dtl.gridwidth = 6;
		diffTypePanel.add(diffTypeLabel, gridBagConstraints_dtl);
		
		final GridBagConstraints gridBagConstraints_pd = new GridBagConstraints();
		gridBagConstraints_pd.anchor = GridBagConstraints.WEST;
		gridBagConstraints_pd.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_pd.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_pd.gridy = 1;
		gridBagConstraints_pd.gridx = 0;
		diffTypePanel.add(pureDiff, gridBagConstraints_pd);
		
		final GridBagConstraints gridBagConstraints_ed = new GridBagConstraints();
		gridBagConstraints_ed.anchor = GridBagConstraints.EAST;
		gridBagConstraints_ed.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_ed.gridy = 1;
		gridBagConstraints_ed.gridx = 5;
		diffTypePanel.add(effectiveDiff, gridBagConstraints_ed);
 
		ButtonGroup bg = new ButtonGroup();
		bg.add(pureDiff);
		bg.add(effectiveDiff);
		pureDiff.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(pureDiff.isSelected())
				{
					koffButton.setEnabled(false);
					bsButton.setEnabled(false);
					koffTextField.setText("0");
					koffTextField.setEnabled(false);
					bsTextField.setText("0");
					bsTextField.setEnabled(false);
					fRadiusTextField.setText("0");
					fRadiusTextField.setEnabled(false);
					bsRadiusTextField.setText("0");
					bsRadiusTextField.setEnabled(false);
				}
			}
		});
		effectiveDiff.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(effectiveDiff.isSelected())
				{
					koffButton.setEnabled(true);
					bsButton.setEnabled(true);
					if(bsButton.isSelected())
					{
						bsTextField.setEnabled(true);
						koffTextField.setEnabled(false);
					}
					else
					{
						bsTextField.setEnabled(false);
						koffTextField.setEnabled(true);
					}
					fRadiusTextField.setEnabled(true);
					bsRadiusTextField.setEnabled(true);
				}
			}
		});
		pureDiff.setSelected(true);
		
		//the mid panel
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.rowHeights = new int[] {7,7,7};
		gridBagLayout_1.columnWidths = new int[] {0,7,7,0,0,7,0,7,7,7,0,7};
		JPanel buttonPanel = new JPanel(gridBagLayout_1);
		TitledBorder tb=new TitledBorder(new LineBorder(Color.gray, 1, false),"Input requried parameters to estimate", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11));
		buttonPanel.setBorder(tb);

		final JLabel koffUnitLabel = new JLabel();
		koffUnitLabel.setText("s-1");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.gridy = 2;
		gridBagConstraints_5.gridx = 5;
		buttonPanel.add(koffUnitLabel, gridBagConstraints_5);
		JLabel fParticleRadiusLabel = new JLabel("F  particle radius (R_f)");
		
		final GridBagConstraints gridBagConstraints_koffRadio = new GridBagConstraints();
		gridBagConstraints_koffRadio.anchor = GridBagConstraints.WEST;
		gridBagConstraints_koffRadio.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_koffRadio.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_koffRadio.gridy = 2;
		gridBagConstraints_koffRadio.gridx = 0;

		final JLabel bindingReactionLabel = new JLabel();
		bindingReactionLabel.setText("Binding Reaction: F + BS = C");
		bindingReactionLabel.setBorder(new LineBorder(new Color(120,120,188), 1));
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_1.gridwidth = 13;
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 0;
		buttonPanel.add(bindingReactionLabel, gridBagConstraints_1);
		buttonPanel.add(koffButton, gridBagConstraints_koffRadio);
		koffButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				koffTextField.setEnabled(true);
				bsTextField.setEnabled(false);
			}
		});
		
		final GridBagConstraints gridBagConstraints_koffTf = new GridBagConstraints();
		gridBagConstraints_koffTf.anchor = GridBagConstraints.WEST;
		gridBagConstraints_koffTf.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_koffTf.gridy = 2;
		gridBagConstraints_koffTf.gridx = 2;
		buttonPanel.add(koffTextField, gridBagConstraints_koffTf);
		
		final GridBagConstraints gridBagConstraints_konLabel = new GridBagConstraints();
		gridBagConstraints_konLabel.anchor = GridBagConstraints.WEST;
		gridBagConstraints_konLabel.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_konLabel.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_konLabel.gridy = 2;
		gridBagConstraints_konLabel.gridx = 9;
		buttonPanel.add(fParticleRadiusLabel, gridBagConstraints_konLabel);
		
		final GridBagConstraints gridBagConstraints_bsRadio = new GridBagConstraints();
		gridBagConstraints_bsRadio.anchor = GridBagConstraints.WEST;
		gridBagConstraints_bsRadio.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_bsRadio.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_bsRadio.gridy = 3;
		gridBagConstraints_bsRadio.gridx = 0;

		fRadiusTextField = new JTextField();
		fRadiusTextField.setColumns(8);
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.anchor = GridBagConstraints.WEST;
		gridBagConstraints_3.gridy = 2;
		gridBagConstraints_3.gridx = 12;
		buttonPanel.add(fRadiusTextField, gridBagConstraints_3);

		final JLabel freeRadiumUnitLabel = new JLabel();
		freeRadiumUnitLabel.setText("um");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.gridy = 2;
		gridBagConstraints_7.gridx = 13;
		buttonPanel.add(freeRadiumUnitLabel, gridBagConstraints_7);
		buttonPanel.add(bsButton, gridBagConstraints_bsRadio);
		bsButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				koffTextField.setEnabled(false);
				bsTextField.setEnabled(true);
			}

		});
		
		final GridBagConstraints gridBagConstraints_bsTf = new GridBagConstraints();
		gridBagConstraints_bsTf.anchor = GridBagConstraints.WEST;
		gridBagConstraints_bsTf.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_bsTf.gridy = 3;
		gridBagConstraints_bsTf.gridx = 2;
		buttonPanel.add(bsTextField, gridBagConstraints_bsTf);
		
		
		ButtonGroup bg2 = new ButtonGroup();
		bg2.add(koffButton);
		bg2.add(bsButton);
		bsButton.setSelected(true);
		bsTextField.setEnabled(true);
		koffTextField.setEnabled(false);
		
		//bottom panel----the table panel
		paramTable.setModel(paramTableModel);//set table model
		final EstimatedParameterTableRenderer renderer = new EstimatedParameterTableRenderer(8); //set table renderer
		for(int i=0; i<paramTable.getModel().getColumnCount(); i++)
		{
			TableColumn column = paramTable.getColumnModel().getColumn(i);
			column.setCellRenderer(renderer);			
		}
//		paramTable.setDefaultRenderer(paramTable.getColumnClass(EstimatedParameterTableModel.COLUMN_VALUE), renderer);
		tableScroll.setViewportView(paramTable);
//		tableScroll.setMinimumSize(new Dimension(0, 0));
		tableScroll.setName("");
		tableScroll.setPreferredSize(new Dimension(0, 0));
		paramTable.setName("");
//		paramTable.setMinimumSize(new Dimension(0, 0));
//		paramTable.setMaximumSize(new Dimension(0, 0));
				
		// The base panel
		final GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.ipadx = 170;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.gridx = 0;
		add(diffTypePanel, gridBagConstraints1);
		
		final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.BOTH;
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		gridBagConstraints2.gridy = 3;
		gridBagConstraints2.gridx = 0;
		add(buttonPanel, gridBagConstraints2);

		final JLabel BSUnitLabel = new JLabel();
		BSUnitLabel.setText(" ");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.gridy = 3;
		gridBagConstraints_6.gridx = 5;
		buttonPanel.add(BSUnitLabel, gridBagConstraints_6);

		final JLabel bsParticleRadiusLabel = new JLabel();
		bsParticleRadiusLabel.setText("BS particle radius(R_BS)");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.anchor = GridBagConstraints.WEST;
		gridBagConstraints_2.gridy = 3;
		gridBagConstraints_2.gridx = 9;
		buttonPanel.add(bsParticleRadiusLabel, gridBagConstraints_2);

		bsRadiusTextField = new JTextField();
		bsRadiusTextField.setColumns(8);
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.anchor = GridBagConstraints.WEST;
		gridBagConstraints_4.gridy = 3;
		gridBagConstraints_4.gridx = 12;
		buttonPanel.add(bsRadiusTextField, gridBagConstraints_4);

		final JLabel bsRadiusUnitLabel = new JLabel();
		bsRadiusUnitLabel.setText("um");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.gridy = 3;
		gridBagConstraints_8.gridx = 13;
		buttonPanel.add(bsRadiusUnitLabel, gridBagConstraints_8);

		final JLabel inputbsAsLabel = new JLabel();
		inputbsAsLabel.setText("(Input [BS] as a ratio of total Fluor)");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridwidth = 5;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridx = 0;
		buttonPanel.add(inputbsAsLabel, gridBagConstraints);
		
		final GridBagConstraints gridBagConstraints_estButton = new GridBagConstraints();
		gridBagConstraints_estButton.gridwidth = 4;
		gridBagConstraints_estButton.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints_estButton.anchor = GridBagConstraints.CENTER;
//		gridBagConstraints_estButton.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_estButton.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_estButton.gridy = 4;
		gridBagConstraints_estButton.gridx = 9;
		buttonPanel.add(estButton, gridBagConstraints_estButton);
		estButton.setMargin(new Insets(0, 14, 0, 14));
		estButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				autoEstimateReactionBindingParameters();
			}
		});
		
		final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.ipadx = 15;
		gridBagConstraints3.ipady = 220;
		gridBagConstraints3.gridheight = 5;
		gridBagConstraints3.gridy = 6;
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.fill = GridBagConstraints.BOTH;
		add(tableScroll, gridBagConstraints3);
	}

	public void autoEstimateReactionBindingParameters()
	{
		if(diffRate != null && mFraction !=  null) // parameters have been set
		{	
			// Second diffusion applied
			if(isSecDiffusionApplied != null && isSecDiffusionApplied.booleanValue())
			{
				double df = diffRate.doubleValue();
				double ff = mFraction.doubleValue();
				double cf = RELATIVE_TOTAL_CONCENTRATION * ff;
				double dc = secDiffRate.doubleValue();
				double fc = secMFraction.doubleValue();
				double cc = RELATIVE_TOTAL_CONCENTRATION * fc;
				double kon;
				double immFrac = 1-ff-fc;
				double fRadius;
				double bsRadius;
				double dbs = 0; //binding site diff rate set to 0
				//check if required inputs are available
				if(bsButton.isSelected() && !bsTextField.getText().equals("") && !fRadiusTextField.getText().equals("") && !bsRadiusTextField.getText().equals(""))
				{
					double bs;
					try{
						bs = Double.parseDouble(bsTextField.getText());
						fRadius = Double.parseDouble(fRadiusTextField.getText());
						bsRadius = Double.parseDouble(bsRadiusTextField.getText());
					}catch(NumberFormatException ex)
					{
						ex.printStackTrace(System.out);
						DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this,"Parameter input error:" + ex.getMessage());
						return;
					}
					//calculate diffusion-limited Kon (Kobs=4*PI*D'*R, D'=Df+Dbs   R=Rf+Rbs)
					kon = 4*PI*(df+dbs)*(fRadius+bsRadius)/602.0;
						double koff = (bs*kon*cf)/cc;
					//update textfield and table
					koffTextField.setText(koff+"");

					try{
						updateTableParameters(diffRate.doubleValue(), mFraction.doubleValue(), secDiffRate.doubleValue(), secMFraction.doubleValue(), bwmRate.doubleValue(),
							              RELATIVE_TOTAL_CONCENTRATION, df, new Expression(paramExpStr[IDX_FreePartDiffRate]), ff,
							              cf, dc, new Expression(paramExpStr[IDX_ComplexDiffRate]), fc, new Expression(paramExpStr[IDX_ComplexFraction]), cc, immFrac,
							              0, bs, null, kon, koff, new Expression(paramExpStr[IDX_ReacOffRate]));
					}catch(ExpressionException ee)
					{
						ee.printStackTrace(System.out);
						DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this,"Error creating expression when trying to update table: " + ee.getMessage());
					}
				}
				else if(koffButton.isSelected() && !koffTextField.getText().equals("") && !fRadiusTextField.getText().equals("") && !bsRadiusTextField.getText().equals(""))
				{
					double koff;
					try{
						koff = Double.parseDouble(koffTextField.getText());
						fRadius = Double.parseDouble(fRadiusTextField.getText());
						bsRadius = Double.parseDouble(bsRadiusTextField.getText());
					}catch(NumberFormatException ex)
					{
						ex.printStackTrace(System.out);
						DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this,"Parameter input error:" + ex.getMessage());
						return;
					}
					//calculate diffusion-limited Kon (Kobs=4*PI*D'*R, D'=Df+Dbs   R=Rf+Rbs)
					kon = 4*PI*(df+dbs)*(fRadius+bsRadius)/602.0;
					double bs = (koff*cc)/(kon*cf);
					//update textfield and table
					bsTextField.setText(bs+"");
					try{
						updateTableParameters(diffRate.doubleValue(), mFraction.doubleValue(), secDiffRate.doubleValue(), secMFraction.doubleValue(), bwmRate.doubleValue(),
								RELATIVE_TOTAL_CONCENTRATION, df, new Expression(paramExpStr[IDX_FreePartDiffRate]), ff,
							              cf, dc, new Expression(paramExpStr[IDX_ComplexDiffRate]),fc, new Expression(paramExpStr[IDX_ComplexFraction]), cc, immFrac,
							              0, bs, new Expression(paramExpStr[IDX_BSConc]), kon, koff, null);
					}catch(ExpressionException ee)
					{
						ee.printStackTrace(System.out);
						DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this, "Error creating expression when trying to update table: " + ee.getMessage());
					}
				}
				else
				{
					DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this, "Estimation cannot be performed. Please input requied parameters.");
				}
			}//
			else //One diffusion (second diffusion is NOT applied) 
			{
				double ff = mFraction.doubleValue(); //fraction of free particle
				double cf = RELATIVE_TOTAL_CONCENTRATION * ff; //concentration of free particle
				double dc = 0; //diffusion rate of complex
				double fc = 1-ff; //fraction of complex
				double cc = RELATIVE_TOTAL_CONCENTRATION * fc; //concentration of complex
				double dbs = 0; //binding site diff rate set to 0 for now???
				String diffExpStr = ""; 
				double df;
				double kon;
				double fRadius;
				double bsRadius;
				double immFrac;
				if(pureDiff.isSelected()) //pure diffusion 
				{
					diffExpStr = paramExpStr[IDX_FreePartDiffRate];
					df = diffRate.doubleValue();
					fc = 0;
					cc = 0;
					kon = 0;
					immFrac = 1-ff-fc;
					double koff = 0;
					double bs = 0;
					try{
						updateTableParameters(diffRate.doubleValue(), mFraction.doubleValue(), -1, -1, bwmRate.doubleValue(),
								RELATIVE_TOTAL_CONCENTRATION, df, new Expression(diffExpStr), ff,
							              cf, dc, null, fc, null, cc, immFrac,
							              0, bs, null, kon, koff, null);
					}catch(ExpressionException ee)
					{
						ee.printStackTrace(System.out);
						DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this, "Error creating expression when trying to update table: " + ee.getMessage());
					}
				}
				else //effective diffusion
				{
					diffExpStr = freeDiffRateStr_eff;
					df = diffRate.doubleValue()*(1+cc/cf); //estimation at equilibrium
					fc = 1-ff;	
					immFrac = 1-ff-fc;
					//check if required inputs are available
					if(bsButton.isSelected() && !bsTextField.getText().equals("") && !fRadiusTextField.getText().equals("") && !bsRadiusTextField.getText().equals(""))
					{
						double bs;
						try{
							bs = Double.parseDouble(bsTextField.getText());
							fRadius = Double.parseDouble(fRadiusTextField.getText());
							bsRadius = Double.parseDouble(bsRadiusTextField.getText());
						}catch(NumberFormatException ex)
						{
							ex.printStackTrace(System.out);
							DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this, "Parameter input error:" + ex.getMessage());
							return;
						}
						//calculate diffusion-limited Kon (Kobs=4*PI*D'*R, D'=Df+Dbs   R=Rf+Rbs)
						kon = 4*PI*(df+dbs)*(fRadius+bsRadius)/602.0;
							double koff = (bs*kon*cf)/cc;
						//update textfield and table
						koffTextField.setText(koff+"");
						try{
							updateTableParameters(diffRate.doubleValue(), mFraction.doubleValue(), -1, -1, bwmRate.doubleValue(),
									RELATIVE_TOTAL_CONCENTRATION, df, new Expression(diffExpStr), ff,
								              cf, dc, null, fc, new Expression(complexFracStr_oneDiffComponent), cc, immFrac,
								              0, bs, null, kon, koff, new Expression(paramExpStr[IDX_ReacOffRate]));
						}catch(ExpressionException ee)
						{
							ee.printStackTrace(System.out);
							DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this, "Error creating expression when trying to update table: " + ee.getMessage());
						}
					}
					else if(koffButton.isSelected() && !koffTextField.getText().equals("") && !fRadiusTextField.getText().equals("") && !bsRadiusTextField.getText().equals(""))
					{
						double koff;
						try{
							koff = Double.parseDouble(koffTextField.getText());
							fRadius = Double.parseDouble(fRadiusTextField.getText());
							bsRadius = Double.parseDouble(bsRadiusTextField.getText());
						}catch(NumberFormatException ex)
						{
							ex.printStackTrace(System.out);
							DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this, "Parameter input error:" + ex.getMessage());
							return;
						}
						//calculate diffusion-limited Kon (Kobs=4*PI*D'*R, D'=Df+Dbs   R=Rf+Rbs)
						kon = 4*PI*(df+dbs)*(fRadius+bsRadius)/602.0;
						double bs = (koff*cc)/(kon*cf);
						//update textfield and table
						bsTextField.setText(bs+"");
						try{
							updateTableParameters(diffRate.doubleValue(), mFraction.doubleValue(), -1, -1, bwmRate.doubleValue(),
									RELATIVE_TOTAL_CONCENTRATION, df, new Expression(diffExpStr), ff,
								              cf, dc, null, fc, new Expression(complexFracStr_oneDiffComponent), cc, immFrac,
								              0, bs, new Expression(paramExpStr[IDX_BSConc]), kon, koff, null);
						}catch(ExpressionException ee)
						{
							ee.printStackTrace(System.out);
							DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this, "Error creating expression when trying to update table: " + ee.getMessage());
						}
					}
					else
					{
						DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this, "Estimation cannot be performed. Please input requied parameters.");
					}
				}
			}
		}
	}
	public void setIniParamFromPureDiffusion(String diffusionRateStr, String mobileFractionStr, boolean isSecondDiffusionApplied, String secondDiffStr, String secondMobileFracStr, String bleachWhileMonitorRateStr)
	{
		if(diffusionRateStr != null)
		{
			diffRate = new Double(diffusionRateStr);
		}
		else
		{
			diffRate = null;
		}
		if(mobileFractionStr != null)
		{
			mFraction = new Double(mobileFractionStr);
		}
		else
		{
			mFraction = null;
		}
		isSecDiffusionApplied = new Boolean(isSecondDiffusionApplied);
		//set diffTypePanel enable or disable
		if(isSecondDiffusionApplied)
		{
			BeanUtils.enableComponents(diffTypePanel, false);
		}
		else
		{
			BeanUtils.enableComponents(diffTypePanel, true);
		}
		if(isSecondDiffusionApplied && secondDiffStr != null)
		{
			secDiffRate = new Double(secondDiffStr);
		}
		else
		{
			secDiffRate = null;
		}
		if(isSecondDiffusionApplied && secondMobileFracStr != null)
		{
			secMFraction = new Double(secondMobileFracStr);
		}
		else
		{
			secMFraction = null;
		}
		if(bleachWhileMonitorRateStr != null)
		{
			bwmRate = new Double(bleachWhileMonitorRateStr);
		}
		else
		{
			bwmRate = null;
		}
	}
	//To use this function, remember to set unavailable parameters to -1, so that the table wont display those parameters.
	public void updateTableParameters(double diffRate, double mFraction, double secDiffRate, double secMFraction, double bwmRate,
			                          double tolConcentration, double freeDiffRate, Expression freeDiffExp, double freeFraction,
			                          double freeConcentration, double complexDiffRate, Expression complexDiffExp, double complexFraction, Expression complexFracExp,
			                          double complexConcentration, double immFraction, double bsDiffRate, double bsConcentration, Expression bsExp,
			                          double onRate, double offRate, Expression offExp) throws ExpressionException
	{
	
		//clear estimatedParameters list for table
		for (int i=0; i<estimatedParameters.length; i++)
		{
			estimatedParameters[i]=null;
		}
		if(diffRate != -1)
		{
			estimatedParameters[IDX_PrimDiffRate] = new EstimatedParameter(paramNames[IDX_PrimDiffRate], paramDescriptions[IDX_PrimDiffRate], null, new Double(diffRate), VCUnitDefinition.UNIT_um2_per_s);
		}	
		else
		{
			estimatedParameters[IDX_PrimDiffRate] = null;
		}
		if(mFraction != -1)
		{
			estimatedParameters[IDX_PrimMFraction]	= new EstimatedParameter(paramNames[IDX_PrimMFraction], paramDescriptions[IDX_PrimMFraction], null, new Double(mFraction), VCUnitDefinition.UNIT_TBD);
		}
		else
		{
			estimatedParameters[IDX_PrimMFraction]	= null;
		}
		if(secDiffRate != -1)
		{
			estimatedParameters[IDX_SecDiffRate] = new EstimatedParameter(paramNames[IDX_SecDiffRate], paramDescriptions[IDX_SecDiffRate], null, new Double(secDiffRate), VCUnitDefinition.UNIT_um2_per_s);
		}
		else
		{
			estimatedParameters[IDX_SecDiffRate] = null;
		}
		if(secMFraction != -1)
		{
			estimatedParameters[IDX_SecMFraction] = new EstimatedParameter(paramNames[IDX_SecMFraction], paramDescriptions[IDX_SecMFraction], null, new Double(secMFraction), VCUnitDefinition.UNIT_TBD);
		}
		else
		{
			estimatedParameters[IDX_SecMFraction] = null;
		}
		if(bwmRate != -1)
		{
			estimatedParameters[IDX_BleachMonitorRate]	= new EstimatedParameter(paramNames[IDX_BleachMonitorRate], paramDescriptions[IDX_BleachMonitorRate], null, new Double(bwmRate), VCUnitDefinition.UNIT_per_s);
		}
		else
		{
			estimatedParameters[IDX_BleachMonitorRate]	= null;
		}
		if(tolConcentration != -1)
		{
			estimatedParameters[IDX_RelativeTotalConc] = new EstimatedParameter(paramNames[IDX_RelativeTotalConc], paramDescriptions[IDX_RelativeTotalConc], null, new Double(tolConcentration), VCUnitDefinition.UNIT_uM);
		}
		else
		{
			estimatedParameters[IDX_RelativeTotalConc] = null;
		}
		if(freeDiffRate != -1)
		{
			estimatedParameters[IDX_FreePartDiffRate] = new EstimatedParameter(paramNames[IDX_FreePartDiffRate], paramDescriptions[IDX_FreePartDiffRate], freeDiffExp, new Double(freeDiffRate), VCUnitDefinition.UNIT_um2_per_s);
		}
		else
		{
			estimatedParameters[IDX_FreePartDiffRate] = null;
		}
		if(freeFraction != -1)
		{
			estimatedParameters[IDX_FreePartFraction]	= new EstimatedParameter(paramNames[IDX_FreePartFraction], paramDescriptions[IDX_FreePartFraction], new Expression("Frac_prim") , new Double(freeFraction), VCUnitDefinition.UNIT_TBD);
		}
		else
		{
			estimatedParameters[IDX_FreePartFraction] = null;
		}
		if(freeConcentration != -1)
		{
			estimatedParameters[IDX_FreePartConc] = new EstimatedParameter(paramNames[IDX_FreePartConc], paramDescriptions[IDX_FreePartConc], new Expression("C_tol * Frac_f"), new Double(freeConcentration), VCUnitDefinition.UNIT_uM);
		}
		else
		{
			estimatedParameters[IDX_FreePartConc] = null;
		}
		if(complexDiffRate != -1)
		{
			estimatedParameters[IDX_ComplexDiffRate] = new EstimatedParameter(paramNames[IDX_ComplexDiffRate], paramDescriptions[IDX_ComplexDiffRate], complexDiffExp, new Double(complexDiffRate), VCUnitDefinition.UNIT_um2_per_s);
		}
		else
		{
			estimatedParameters[IDX_ComplexDiffRate] = null;
		}
		if(complexFraction != -1)
		{
			estimatedParameters[IDX_ComplexFraction]	= new EstimatedParameter(paramNames[IDX_ComplexFraction], paramDescriptions[IDX_ComplexFraction], complexFracExp, new Double(complexFraction), VCUnitDefinition.UNIT_TBD);
		}
		else
		{
			estimatedParameters[IDX_ComplexFraction] = null;
		}
		if(complexConcentration != -1)
		{
			estimatedParameters[IDX_ComplexConc] = new EstimatedParameter(paramNames[IDX_ComplexConc], paramDescriptions[IDX_ComplexConc], new Expression("C_tol * Frac_c"), new Double(complexConcentration), VCUnitDefinition.UNIT_uM);
		}
		else
		{
			estimatedParameters[IDX_ComplexConc] = null;
		}
		if(immFraction != -1)
		{
			estimatedParameters[IDX_ImmFraction]	= new EstimatedParameter(paramNames[IDX_ImmFraction], paramDescriptions[IDX_ImmFraction], new Expression("1 - Frac_f - Frac_c"), new Double(immFraction), VCUnitDefinition.UNIT_TBD);
		}
		else
		{
			estimatedParameters[IDX_ImmFraction] = null;
		}
		if(bsDiffRate != -1)
		{
			estimatedParameters[IDX_BSDiffRate] = new EstimatedParameter(paramNames[IDX_BSDiffRate], paramDescriptions[IDX_BSDiffRate], null, new Double(0), VCUnitDefinition.UNIT_um2_per_s);
		}
		if(bsConcentration != -1)
		{
			estimatedParameters[IDX_BSConc] = new EstimatedParameter(paramNames[IDX_BSConc], paramDescriptions[IDX_BSConc], bsExp, new Double(bsConcentration), VCUnitDefinition.UNIT_uM);
		}
		else
		{
			estimatedParameters[IDX_BSConc] = null;
		}
		if(onRate != -1)
		{
			estimatedParameters[IDX_ReacOnRate] = new EstimatedParameter(paramNames[IDX_ReacOnRate], paramDescriptions[IDX_ReacOnRate], new Expression("(4*PI*(D_f+D_BS)*(R_f+R_BS))/602"), new Double(onRate), VCUnitDefinition.UNIT_per_uM_per_s);
		}
		else
		{
			estimatedParameters[IDX_ReacOnRate] = null;
		}
		if(offRate != -1)
		{
			estimatedParameters[IDX_ReacOffRate] = new EstimatedParameter(paramNames[IDX_ReacOffRate], paramDescriptions[IDX_ReacOffRate], offExp, new Double(offRate), VCUnitDefinition.UNIT_per_s);
		}
		else
		{
			estimatedParameters[IDX_ReacOffRate] = null;
		}
		paramTableModel.setEstimatedParameters(estimatedParameters);
		
	}
	
	public JTable getParamTable()
	{
		return paramTable;
	}
	public JPanel getDiffTypePanel()
	{
		return diffTypePanel;
	}

	public void updateUIForPureDiffusion()
	{
		pureDiff.doClick();
	}

	public void updateUIForReacDiffusion() 
	{
		koffButton.setEnabled(true);
		bsButton.setEnabled(true);
		if(bsButton.isSelected())
		{
			bsTextField.setEnabled(true);
			koffTextField.setEnabled(false);
		}
		else
		{
			bsTextField.setEnabled(false);
			koffTextField.setEnabled(true);
		}
		fRadiusTextField.setEnabled(true);
		bsRadiusTextField.setEnabled(true);
		
	}
	
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			FRAPReacDiffEstimationGuidePanel aPanel = new FRAPReacDiffEstimationGuidePanel();
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
