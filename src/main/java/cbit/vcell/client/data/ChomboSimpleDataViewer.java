package cbit.vcell.client.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.vcell.util.BeanUtils;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.gui.DialogUtils;

import com.lowagie.text.Font;

import cbit.gui.TextFieldAutoCompletion;
import cbit.vcell.client.VCellLookAndFeel;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.simdata.DataSetIdentifier;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationDataSpatialHdf5;
import cbit.vcell.simdata.SimulationDataSpatialHdf5.SimDataSet;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

public class ChomboSimpleDataViewer extends JFrame {
	
	static
	{
	    Logger rootLogger = Logger.getRootLogger();
	    rootLogger.setLevel(Level.OFF);
	    rootLogger.removeAllAppenders();
	    rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%-6r [%p] %c - %m%n")));
	}
	
	private static class MeshMetricsTableModel extends AbstractTableModel
	{
		private String[] cols = new String[0];
		private List<Number[]> values = new ArrayList<Number[]>();
		
		@Override
		public int getRowCount() {
			return values.size();
		}

		@Override
		public int getColumnCount() {
			return cols.length;
		}

//		private boolean isIndexColumn(int columnIndex)
//		{
//			String col = cols.get(columnIndex);
//			return col.equalsIgnoreCase("i") || col.equalsIgnoreCase("j")
//					|| col.equalsIgnoreCase("k") || col.equalsIgnoreCase("index");
//		}
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Number d = values.get(rowIndex)[columnIndex];
			return d;
		}

		@Override
		public String getColumnName(int column) {
			return cols[column];
		}
		
		public void setData(String[] cols, List<Number[]> values)
		{
			this.cols = cols;
			this.values = values;
			fireTableDataChanged();
		}
		
		public void refreshTable()
		{
			fireTableStructureChanged();
			fireTableDataChanged();
		}
		
		public void clear()
		{
			cols = new String[0];
			values.clear();
			refreshTable();
		}
	}
	
	private static class SolTableCellRenderer extends DefaultTableCellRenderer
	{

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			// TODO Auto-generated method stub
			Component label = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
					row, column);
			setForeground(Color.black);
			if (!isSelected)
			{
				if (value instanceof Number)
				{
					if (((Number) value).doubleValue() == SimDataConstants.BASEFAB_REAL_SETVAL)
					{
						setForeground(Color.gray);
					}
				}
			}
			return label;
		}
		
	}
	
	private static class SolTableModel extends AbstractTableModel
	{
		private static final int COL_INDEX = 0;
		private static final int COL_VALUE = 1;
		private final static String[] cols = {"index", "value"};
		private double[] values = null;

		@Override
		public int getRowCount() {
			return values == null ? 0 : values.length;
		}

		@Override
		public int getColumnCount() {
			return cols.length;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return Number.class;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == COL_INDEX)
			{
				return rowIndex;
			}
			return values[rowIndex];
		}

		@Override
		public String getColumnName(int column) {
			return cols[column];
		}
		
		public void setValues(double[] v)
		{
			values = v;
			fireTableDataChanged();
		}
		
		public void clear()
		{
			setValues(new double[0]);
		}
	}
	
	private static class TimePlotTableModel extends AbstractTableModel
	{
		private static final int COL_TIME = 0;
		private static final int COL_VALUE = 1;
		private final static String[] cols = {"time", "value"};
		private double[] values = null;
		private double[] times = null;
		
		@Override
		public int getRowCount() {
			return values == null ? 0 : values.length;
		}
		
		@Override
		public int getColumnCount() {
			return cols.length;
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return columnIndex == COL_TIME ? times[rowIndex] : values[rowIndex];
		}
		
		@Override
		public String getColumnName(int column) {
			return cols[column];
		}
		
		public void setTimesAndValues(double[] t, double[] v)
		{
			times = t;
			values = v;
			fireTableDataChanged();
		}
		public void clear()
		{
			setTimesAndValues(new double[0], new double[0]);
		}
	}
	private class EventListener implements ActionListener, ListSelectionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == resetButton)
			{
				reset();
			}
			else if (e.getSource() == okButton)
			{
				retrieveVariablesAndTimes();
			}
			else if (e.getSource() == timeComboBox)
			{
				retrieveData();
			}
			else if (e.getSource() == timePlotButton)
			{
				retrieveTimePlot();
			}
			else if (e.getSource() == exitButton)
			{
				System.exit(0);
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
			{
				return;
			}
			if (e.getSource() == varList)
			{
				retrieveData();
			}
			else if (e.getSource() == solTable.getSelectionModel())
			{
				timePlotButton.setEnabled(solTable.getSelectedRowCount() == 1);
			}
		}
	}
	
	private JPanel mainPanel = new JPanel();
	private JList varList = new JList();
	private JButton okButton = new JButton("Go");
	private JButton resetButton = new JButton("Reset");
	private JButton timePlotButton = new JButton("Time Plot");
	private JButton exitButton = new JButton("Exit");
	private JTable solTable= new JTable();
	private SolTableModel solTableModel = new SolTableModel();
	private JTable timePlotTable= new JTable();
	private TimePlotTableModel timePlotTableModel = new TimePlotTableModel();
	private TextFieldAutoCompletion dataDirTextField = new TextFieldAutoCompletion();
	private TextFieldAutoCompletion userNameTextField = new TextFieldAutoCompletion();
	private TextFieldAutoCompletion simIdField = new TextFieldAutoCompletion();
//	private JPasswordField remotePasswordField = new JPasswordField();
	private JComboBox timeComboBox = new JComboBox();
	private JLabel solLabel = new JLabel("Solution");
	private JLabel timePlotLabel = new JLabel("Time Plot");
	private SimulationDataSpatialHdf5 simData = null;
	private EventListener listener = new EventListener();
	private Set<String> simIds = new HashSet<String>();
	private Set<String> usernames = new HashSet<String>();
	private Set<String> datadirs = new HashSet<String>();
	private JTabbedPane dataTabbedPane = new JTabbedPane();
	private JTable meshMetricsTable = new JTable();
	private MeshMetricsTableModel meshMetricsTableModel = new MeshMetricsTableModel();
	private static boolean debug = false;
	private JPanel timePlotPanel;
	private JTextField maxErrorTextField = new JTextField();
	private JTextField l2ErrorTextField = new JTextField();
	private JTextField meanTextField = new JTextField();
	private JTextField sumVolFracTextField = new JTextField();
	private JPanel errorPanel = null;
	
	private ChomboSimpleDataViewer()
	{
		setTitle("Chombo Simple Data Viewer");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		initialize();
	}

	private JPanel getErrorPanel()
	{
		if (errorPanel == null)
		{
			errorPanel = new JPanel();
			errorPanel.setLayout(new GridBagLayout());
			errorPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
			
			meanTextField.setEditable(false);
			sumVolFracTextField.setEditable(false);
			maxErrorTextField.setEditable(false);
			l2ErrorTextField.setEditable(false);
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.insets = new Insets(2, 2, 2, 2);
			JLabel label = new JLabel("Mean");
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			errorPanel.add(label, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			errorPanel.add(meanTextField, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.insets = new Insets(2, 2, 2, 2);
			label = new JLabel("Vol Frac Sum");
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			errorPanel.add(label, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 3;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			errorPanel.add(sumVolFracTextField, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.insets = new Insets(2, 2, 2, 2);
			label = new JLabel("Max Error");
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			errorPanel.add(label, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			errorPanel.add(maxErrorTextField, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 1;
			gbc.insets = new Insets(2, 2, 2, 2);
			label = new JLabel("Relative L2 Error");
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			errorPanel.add(label, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 3;
			gbc.gridy = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			errorPanel.add(l2ErrorTextField, gbc);
		}
		
		return errorPanel;
	}
	
	private JPanel createSolPanel()
	{
		JPanel solPanel = new JPanel(new GridBagLayout());
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.LINE_START;
		solLabel.setFont(solLabel.getFont().deriveFont(Font.BOLD));
		solPanel.add(solLabel, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2, 2, 2, 10);
		gbc.anchor = GridBagConstraints.LINE_END;
		solPanel.add(timePlotButton, gbc);
				
		++ gridy;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.BOTH;
		solPanel.add(new JScrollPane(solTable), gbc);
		
		++ gridy;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		solPanel.add(getErrorPanel(), gbc);

		return solPanel;
	}
	
	JFileChooser jFileChooser;
	private ActionListener dataBrowseActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(jFileChooser == null){
				jFileChooser = new JFileChooser();
//				jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			}
			int returnVal = jFileChooser.showOpenDialog(ChomboSimpleDataViewer.this);

	        if (returnVal == JFileChooser.APPROVE_OPTION){
	        	File selectedFile = jFileChooser.getSelectedFile();
	        	String formattedName = null;
	        	String userName = null;
	        	if(selectedFile.isFile() && selectedFile.getName().startsWith("SimID_")){
	        		formattedName = selectedFile.getName();
	        		userName = selectedFile.getParentFile().getName();
	        		dataDirTextField.setText(jFileChooser.getSelectedFile().getParentFile().getParentFile().getAbsolutePath());
	        	}else{
	        		dataDirTextField.setText(jFileChooser.getSelectedFile().getAbsolutePath());
	        	}
	        	if(formattedName != null){
	        		StringTokenizer st = new StringTokenizer(formattedName,"_");
	        		st.nextToken();
	        		simIdField.setText(st.nextToken());
	        		if(userName != null){
	        			userNameTextField.setText(userName);
	        		}
	        	}
	        } else {
	            return;
	        }
		}
	};
	private JPanel createInputPanel()
	{
		JPanel inputPanel = new JPanel(new GridBagLayout());
		inputPanel.setBorder(BorderFactory.createTitledBorder(GuiConstants.TAB_PANEL_BORDER, "Input"));
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.LINE_END;
		JLabel label = new JLabel("User");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		inputPanel.add(label, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		inputPanel.add(userNameTextField, gbc);
				
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.LINE_END;
		JButton dataBrowseButton = new JButton("Data Dir");
		dataBrowseButton.setFont(label.getFont().deriveFont(Font.BOLD));
		inputPanel.add(dataBrowseButton, gbc);
		dataBrowseButton.addActionListener(dataBrowseActionListener);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		inputPanel.add(dataDirTextField, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.LINE_END;
		label = new JLabel("Sim ID");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		inputPanel.add(label, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		inputPanel.add(simIdField, gbc);

//		gridy ++;
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = gridy;
//		gbc.insets = new Insets(2, 2, 2, 2);
//		gbc.anchor = GridBagConstraints.LINE_END;
//		label = new JLabel("RmtPW");
//		label.setFont(label.getFont().deriveFont(Font.BOLD));
//		inputPanel.add(label, gbc);
//		
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = gridy;
//		gbc.insets = new Insets(2, 2, 2, 2);
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		inputPanel.add(remotePasswordField, gbc);
		
		gridy ++;
		JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel1.add(resetButton);
		panel1.add(okButton);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.weightx = 0.2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		inputPanel.add(panel1, gbc);
		return inputPanel;
	}
	
	private JPanel createSelectionPanel()
	{
		JPanel selectionPanel = new JPanel(new GridBagLayout());
		selectionPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.LINE_END;
		JLabel label = new JLabel("Time");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		selectionPanel.add(label, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		selectionPanel.add(timeComboBox, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		label = new JLabel("Variable");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		selectionPanel.add(label, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(2, 2, 10, 2);
		gbc.fill = GridBagConstraints.BOTH;
		selectionPanel.add(new JScrollPane(varList), gbc);
		return selectionPanel;
	}
	
	private JPanel createTimePlotPanel()
	{
		timePlotPanel = new JPanel(new GridBagLayout());
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.LINE_START;
		timePlotLabel.setFont(timePlotLabel.getFont().deriveFont(Font.BOLD));
		timePlotPanel.add(timePlotLabel, gbc);
		
		++ gridy;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.BOTH;
		timePlotPanel.add(new JScrollPane(timePlotTable), gbc);
		
		return timePlotPanel;
	}
	
	private void initialize() {
		setSize(1000, 500);
		BeanUtils.centerOnScreen(this);
		
		solTable.setModel(solTableModel);
		solTable.setDefaultRenderer(Number.class, new SolTableCellRenderer());
		meshMetricsTable.setModel(meshMetricsTableModel);
		timePlotTable.setModel(timePlotTableModel);
		varList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		mainPanel.setLayout(new GridBagLayout());
		
		JPanel meshMetricsPanel = new JPanel(new BorderLayout());
		meshMetricsPanel.add(new JScrollPane(meshMetricsTable), BorderLayout.CENTER);
	
		dataTabbedPane.addTab("Solution", createSolPanel());
		dataTabbedPane.addTab("Mesh Metrics", meshMetricsPanel);
		dataTabbedPane.addTab("Time Plot", createTimePlotPanel());
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 0.4;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		mainPanel.add(createInputPanel(), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.BOTH;
		mainPanel.add(dataTabbedPane, gbc);

		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;
		gbc.weightx = 0.2;
		mainPanel.add(createSelectionPanel(), gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2, 2, 20, 20);
		gbc.anchor = GridBagConstraints.LINE_END;
		mainPanel.add(exitButton, gbc);
		
		add(mainPanel);
		reset();
		
		resetButton.addActionListener(listener);
		okButton.addActionListener(listener);
		timeComboBox.addActionListener(listener);
		exitButton.addActionListener(listener);
		varList.addListSelectionListener(listener);
		timePlotButton.setEnabled(false);
		timePlotButton.addActionListener(listener);
		solTable.getSelectionModel().addListSelectionListener(listener);
		
		dataDirTextField.addMouseListener(new MouseAdapter() {			
			@Override
			public void mouseEntered(MouseEvent e) {
				dataDirTextField.setToolTipText(dataDirTextField.getText());
			}
		});
		varList.setCellRenderer(new DefaultListCellRenderer(){

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof DataSetIdentifier)
				{
					setText(((DataSetIdentifier) value).getName());
				}
				return this;
			}
			
		});
	}

	private void reset()
	{
		userNameTextField.setText("boris");
		dataDirTextField.setText("\\\\cfs01.cam.uchc.edu\\ifs\\RAID\\Vcell\\users\\");
		
		if (debug)
		{
			userNameTextField.setText("fgao1");
			dataDirTextField.setText("C:\\chombo\\data\\users\\");
//			simIdField.setText("77396269");
			simIdField.setText("77764707");
		}
	}
	
	private static class SimDataInfoHolder{
		public SimulationDataSpatialHdf5 simData;
		public File userDir;
		public SimDataInfoHolder(SimulationDataSpatialHdf5 simData, File userDir) {
			this.simData = simData;
			this.userDir = userDir;
		}
	}
	private SimDataInfoHolder createSimulationDataFromDir(File dataDir,String userid,VCSimulationDataIdentifier vcDataId) throws Exception{
		File userDir = new File(dataDir, userid);
		return new SimDataInfoHolder(new SimulationDataSpatialHdf5(vcDataId, userDir, null),userDir);
	}
	private JPasswordField jPasswordField = new JPasswordField();
	private SimDataInfoHolder createSimulationDataFromRemote(String userid,VCSimulationDataIdentifier vcDataId) throws Exception{
		SimDataInfoHolder simDataInfoHolder = null;
		SimulationDataSpatialHdf5 simData = null;
		try{
			//Try well known primary data dir from windows
//			if(true){throw new Exception();}
			File userDir = new File("\\\\cfs01.cam.uchc.edu\\ifs\\raid\\vcell\\users",userid);
			simData = new SimulationDataSpatialHdf5(vcDataId, userDir, null);
			simDataInfoHolder = new SimDataInfoHolder(simData,userDir);
		}catch(Exception e){
			try{
				//Try well known secondary data dir from windows
//				if(true){throw new Exception();}
				File userDir = new File("\\\\cfs02.cam.uchc.edu\\raid\\vcell\\users",userid);
				simData = new SimulationDataSpatialHdf5(vcDataId,userDir, null);
				simDataInfoHolder = new SimDataInfoHolder(simData,userDir);					
			}catch(Exception e2){
				//try ssh download from linux server
				if(DialogUtils.showComponentOKCancelDialog(ChomboSimpleDataViewer.this, jPasswordField, "Enter cluster password for 'vcell'") != JOptionPane.OK_OPTION){
					throw UserCancelException.CANCEL_GENERIC;
				}

				File tempSimDir = File.createTempFile("VCellUsersDir", ".dir");
				tempSimDir.delete();
	        	File tmpdir = new File(tempSimDir.getParentFile(),"VCellUsersDir");
	        	if(!tmpdir.exists() && !tmpdir.mkdir()){
	        		throw new Exception("Couldn't make local dir "+tmpdir);
	        	}				
	        	File downloadDir = SimDataConnection.downloadSimData(tmpdir, new String(jPasswordField.getPassword()), userid, vcDataId.getSimulationKey(), 0, false);
				simData = new SimulationDataSpatialHdf5(vcDataId,downloadDir, null);
				simDataInfoHolder = new SimDataInfoHolder(simData,downloadDir);
			}
		}
		return simDataInfoHolder;
	}
	private void retrieveVariablesAndTimes()
	{
		AsynchClientTask task0 = new AsynchClientTask("clear", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				DefaultComboBoxModel dcm = new DefaultComboBoxModel();
				timeComboBox.setModel(dcm);
				DefaultListModel dlm = new DefaultListModel();
				varList.setModel(dlm);
				solTableModel.clear();
				meshMetricsTableModel.clear();
				meanTextField.setText(null);
				maxErrorTextField.setText(null);
				sumVolFracTextField.setText(null);
				l2ErrorTextField.setText(null);
			}
		};
		
		AsynchClientTask task1 = new AsynchClientTask("retrieve data", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				String simId = simIdField.getText().trim();
				if (simId == null || simId.length() == 0)
				{
					throw new RuntimeException("Please provide a simulation id.");
				}
				String username = userNameTextField.getText().trim();
				if (username == null || username.length() == 0)
				{
					throw new RuntimeException("Please provide a user name.");
				}
				VCSimulationDataIdentifier vcDataId = new VCSimulationDataIdentifier(new VCSimulationIdentifier(new KeyValue(simId), new User(username, null)), 0);
				SimDataInfoHolder simDataInfoHolder = null;
				String datadir = dataDirTextField.getText();
				if (datadir == null || datadir.length() == 0){
					simDataInfoHolder = createSimulationDataFromRemote(username, vcDataId);
					datadir = simDataInfoHolder.userDir.getParent();
					dataDirTextField.setText(datadir);
				}else{
					simDataInfoHolder = createSimulationDataFromDir(new File(datadir), username, vcDataId);					
				}
				simData = simDataInfoHolder.simData;
				simData.readVarAndFunctionDataIdentifiers();
				usernames.add(username);
				userNameTextField.setAutoCompletionWords(usernames);
				datadirs.add(datadir);
				dataDirTextField.setAutoCompletionWords(datadirs);
				simIds.add(simId);
				simIdField.setAutoCompletionWords(simIds);
			}
		};
		
		AsynchClientTask task2 = new AsynchClientTask("show data", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				double[] times = simData.getDataTimes();
				DefaultComboBoxModel dcm = new DefaultComboBoxModel();
				for(double t : times)
				{
					dcm.addElement(t);
				}
				timeComboBox.setModel(dcm);
				meshMetricsTableModel.setData(simData.getChomboMesh().getMetricsColumnNames(), simData.getChomboMesh().getMetricsNumbers());
				List<DataSetIdentifier> dsiList = simData.getDataSetIdentifiers();
				DefaultListModel dlm = new DefaultListModel();
				for (DataSetIdentifier dsi : dsiList)
				{
					dlm.addElement(dsi);
				}
				varList.setModel(dlm);
				if (times.length > 0)
				{
					timeComboBox.setSelectedIndex(0);
				}
				if (dsiList.size() > 0)
				{
					varList.setSelectedIndex(0);
				}
				meshMetricsTableModel.refreshTable();
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task0, task1, task2}, false);
	}
	
	private void retrieveTimePlot()
	{
		if (solTable.getSelectedRow() < 0 || varList.getSelectedIndex() < 0)
		{
			return;
		}
		final int index = (Integer) solTable.getValueAt(solTable.getSelectedRow(), SolTableModel.COL_INDEX);
		DataSetIdentifier selectedVar = (DataSetIdentifier)varList.getSelectedValue();
		final String varName = selectedVar.getName();
		
		AsynchClientTask task0 = new AsynchClientTask("clear", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				timePlotTableModel.setTimesAndValues(new double[0], new double[0]);
			}
		};
		
		AsynchClientTask task1 = new AsynchClientTask("retrieve data", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				double[] times = simData.getDataTimes();
				double[] values = new double[times.length];
				for (int i = 0; i < times.length; ++ i)
				{
					SimDataSet simDataBlock = simData.retrieveSimDataSet(times[i], varName);
					values[i] = simDataBlock.solValues[index];
				}
				hashTable.put("values", values);
			}
		};
		
		AsynchClientTask task2 = new AsynchClientTask("show data", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				timePlotLabel.setText("Varaible " + varName + " @ Index " + index);
				double[] times = simData.getDataTimes();
				double[] values = (double[]) hashTable.get("values");
				timePlotTableModel.setTimesAndValues(times, values);
				dataTabbedPane.setSelectedComponent(timePlotPanel);
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task0, task1, task2}, false);
	}
	
//	private void readMeshMetricsFile(File userDir, VCSimulationDataIdentifier vcDataId, String simId) throws IOException 
//	{
//		File meshMetricsFile = new File(userDir, vcDataId.getID() + ".chombo.memmetrics"); 
//		if (!meshMetricsFile.exists())
//		{
//			return;
//		}
//		BufferedReader br = null;
//		try
//		{
//			br = new BufferedReader(new FileReader(meshMetricsFile));
//			List<String> cols = new ArrayList<String>();
//			List<double[]> values = new ArrayList<double[]>();
//			String line = br.readLine();
//			if (line != null)
//			{
//				StringTokenizer st = new StringTokenizer(line, ",");
//				while (st.hasMoreTokens())
//				{
//					String token = st.nextToken();
//					cols.add(token);
//				}
//			}
//			while (true)
//			{
//				line = br.readLine();
//				if (line == null)
//				{
//					break;
//				}
//				double[] dvalues = new double[cols.size()];
//				StringTokenizer st = new StringTokenizer(line, ",");
//				int cnt = 0;
//				while (st.hasMoreTokens())
//				{
//					String token = st.nextToken();
//					dvalues[cnt] = Double.parseDouble(token);
//					++ cnt;
//				}
//				assert cnt == cols.size();
//				values.add(dvalues);
//			}
//			meshMetricsTableModel.setData(cols, values);
//		}
//		finally
//		{
//			if (br != null)
//			{
//				br.close();
//			}
//		}
//	}

	private void retrieveData()
	{
		final Double time = (Double)timeComboBox.getSelectedItem();
		if (time == null)
		{
			return;
		}
		DataSetIdentifier selectedVar = (DataSetIdentifier)varList.getSelectedValue();
		if (selectedVar == null)
		{
			return;
		}
		final String varName = selectedVar.getName();
		AsynchClientTask task0 = new AsynchClientTask("clear", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				solTableModel.clear();
				timePlotTableModel.clear();
				solLabel.setText("Solution");
				timePlotLabel.setText("Time Plot");
				meanTextField.setText(null);
				maxErrorTextField.setText(null);
				sumVolFracTextField.setText(null);
				l2ErrorTextField.setText(null);
			}
		};
		
		AsynchClientTask task1 = new AsynchClientTask("retrieve data", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				if (timeComboBox.getSelectedIndex() < 0 || varList.getSelectedIndex() < 0)
				{
					return;
				}
				SimDataSet simDataSet = simData.retrieveSimDataSet(time, varName);
				hashTable.put("simDataSet", simDataSet);
			}
		};
		
		AsynchClientTask task2 = new AsynchClientTask("show data", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				SimDataSet simDataSet = (SimDataSet) hashTable.get("simDataSet");
				if (simDataSet == null)
				{
					return;
				}
				solLabel.setText("Variable " + varName + " @ Time " + time);
				solTableModel.setValues(simDataSet.solValues);
				meanTextField.setText(simDataSet.mean == null ? "" : simDataSet.mean.toString());
				sumVolFracTextField.setText(simDataSet.sumVolFrac == null ? "" : simDataSet.sumVolFrac.toString());
				maxErrorTextField.setText(simDataSet.maxError == null ? "" : simDataSet.maxError.toString());
				l2ErrorTextField.setText(simDataSet.l2Error == null ? "" : simDataSet.l2Error + "".toString());
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task0, task1, task2}, false);
	}
	
	public static void main(String[] args)
	{
		if (args.length > 0 && new Boolean(args[0]))
		{
			ChomboSimpleDataViewer.debug = true;
		}
		VCellLookAndFeel.setVCellLookAndFeel();
		ChomboSimpleDataViewer chomboSimpleDataViewer = new ChomboSimpleDataViewer();
		chomboSimpleDataViewer.setVisible(true);
	}
}
