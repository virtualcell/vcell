package cbit.vcell.client.data;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.gui.DialogUtils;

import ucar.ma2.ArrayDouble;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import cbit.plot.Plot2D;
import cbit.plot.PlotPane;
import cbit.plot.SingleXPlot2D;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.solver.DataProcessingOutput;

public class DataProcessingResultsPanel extends JPanel implements PropertyChangeListener {

	private PDEDataContext pdeDataContext;
	private NetcdfFile ncfile;
	private JList varJList;
	private PlotPane plotPane = null;
	private double[] timeArray;

	public DataProcessingResultsPanel() {
		super();
		initialize();
	}
	private void initialize() {		
		setLayout(new GridBagLayout());
		varJList = new JList();
		varJList.setVisibleRowCount(5);
		varJList.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				onVariableChange((String) varJList.getSelectedValue());
			}
		});
		plotPane = new PlotPane();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 1.0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(10,10,10,10);
		gbc.fill = GridBagConstraints.BOTH;
		add(new JScrollPane(varJList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10,10,10,10);
		add(plotPane, gbc);
	}
	
	public void setPdeDataContext(PDEDataContext newValue) {
		if (this.pdeDataContext == newValue) {
			return;
		}
		PDEDataContext oldValue = pdeDataContext;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(this);
		}
		this.pdeDataContext = newValue;	
		if (newValue != null) {
			newValue.addPropertyChangeListener(this);
		}
		update();
	}

	private void read() {
		try {
			DataProcessingOutput dpo = pdeDataContext.getDataProcessingOutput();
			if (dpo != null) {
				byte[] nc_content = dpo.toBytes();
				ncfile = NetcdfFile.openInMemory("temp.inmemory", nc_content);
				
				ucar.nc2.Variable tVar = ncfile.findVariable(ReservedSymbol.TIME.getName());
				int[] shape = tVar.getShape();
				int[] origin = new int[1];
				timeArray = new double[shape[0]];
				ArrayDouble.D1 data = null;
				try {
					data = (ArrayDouble.D1)tVar.read(origin, shape);
				} catch (Exception e) {
					e.printStackTrace(System.err);
					throw new IOException("Can not read volVar data.");
				}
				for (int i = 0; i < shape[0]; i++) {
					timeArray[i] = data.get(i);					
				}
			}

		} catch (Exception e1) {
			DialogUtils.showErrorDialog(this, e1.getMessage(), e1);
			e1.printStackTrace();
		}
	}
	
	private void update() {
		AsynchClientTask task1 = new AsynchClientTask("retrieving data", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {		
				read();
			}
		};
		
		AsynchClientTask task2 = new AsynchClientTask("showing data", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {		
				varJList.removeAll();
				if (ncfile == null) {
					plotPane.setPlot2D(null);
				} else {
					List<Variable> varList = ncfile.getVariables();
					DefaultListModel dlm = new DefaultListModel();
					for (Variable var : varList) {
						if (!var.getName().equals(ReservedSymbol.TIME.getName())) {
							dlm.addElement(var.getName());
						}
					}
					varJList.setModel(dlm);
					varJList.setSelectedIndex(0);
				}
			}
		};
		
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2});
	}
	
	private void onVariableChange(String varName) {
		try {
			ucar.nc2.Variable volVar = ncfile.findVariable(varName);
			int[] shape = volVar.getShape();
			int numRegions = shape[1];
			int numTimes = shape[0];
			int[] origin = new int[2];
			double[][] plotDatas = new double[numRegions + 1][numTimes];
			plotDatas[0] = timeArray;

			ArrayDouble.D2 data = null;
			try {
				data = (ArrayDouble.D2) volVar.read(origin, shape);
			} catch (Exception e) {
				e.printStackTrace(System.err);
				throw new IOException("Can not read volVar data.");
			}
			String[] colNames = new String[shape[1]];
			for (int j = 0; j < numRegions; j++) {
				colNames[j] = "col" + j;
			}
			for (int i = 0; i < numRegions; i++) {
				for (int j = 0; j < numTimes; j++) {
					plotDatas[i + 1][j] = data.get(j, i);
				}
			}
			Plot2D plot2D = new SingleXPlot2D(null, ReservedSymbol.TIME.getName(), colNames, plotDatas, 
					new String[] {"Time Plot", ReservedSymbol.TIME.getName(), varName});				
			plotPane.setPlot2D(plot2D);
		} catch (Exception e1) {
			DialogUtils.showErrorDialog(this, e1.getMessage(), e1);
			e1.printStackTrace();
		}
	}
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == pdeDataContext && evt.getPropertyName().equals(PDEDataContext.PROPERTY_NAME_TIME_POINTS)) {
			update();
		}
	}
}
