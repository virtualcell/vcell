package cbit.vcell.client.desktop.simulation;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (9/13/2005 9:38:19 AM)
 * @author: Ion Moraru
 */
public class ParameterScanPanel extends JPanel {
	private org.vcell.util.gui.ButtonGroupCivilized ivjButtonGroupCivilized1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JCheckBox ivjJCheckBoxLog = null;
	private JLabel ivjJLabelMax = null;
	private JLabel ivjJLabelMin = null;
	private JLabel ivjJLabelNumber = null;
	private JLabel ivjJLabelValues = null;
	private JRadioButton ivjJRadioButtonList = null;
	private JRadioButton ivjJRadioButtonRange = null;
	private JTextField ivjJTextFieldMax = null;
	private JTextField ivjJTextFieldMin = null;
	private JTextField ivjJTextFieldNumber = null;
	private JTextField ivjJTextFieldValues = null;
	private cbit.vcell.solver.ConstantArraySpec fieldConstantArraySpec = cbit.vcell.solver.ConstantArraySpec.createIntervalSpec("", 0, 0, 0, false);

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ParameterScanPanel.this.getButtonGroupCivilized1() && (evt.getPropertyName().equals("selection"))) 
				connEtoC1(evt);
			if (evt.getSource() == ParameterScanPanel.this && (evt.getPropertyName().equals("constantArraySpec"))) 
				connEtoC2(evt);
		};
	};

public ParameterScanPanel() {
	super();
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (9/23/2005 2:25:09 PM)
 */
public void applyValues() throws cbit.vcell.parser.ExpressionException{
	if (getJRadioButtonList().isSelected()) {
		java.util.StringTokenizer tokens = new java.util.StringTokenizer(getJTextFieldValues().getText(), ", ");
		String[] values = new String[tokens.countTokens()];
		int i = 0;
		while (tokens.hasMoreElements()) {
			values[i] = tokens.nextToken();
			i++;
		}
		setConstantArraySpec(cbit.vcell.solver.ConstantArraySpec.createListSpec(getConstantArraySpec().getName(), values));
	} else if (getJRadioButtonRange().isSelected()) {
		System.out.println(getJTextFieldMin().getText());
		System.out.println(getJTextFieldMax().getText());
		setConstantArraySpec(cbit.vcell.solver.ConstantArraySpec.createIntervalSpec(
			getConstantArraySpec().getName(),
			Double.parseDouble(getJTextFieldMin().getText()),
			Double.parseDouble(getJTextFieldMax().getText()),
			Integer.parseInt(getJTextFieldNumber().getText()),
			getJCheckBoxLog().isSelected()
			));
	}
}
	
/**
 * connEtoC1:  (ButtonGroupCivilized1.selection --> ParameterScanPanel.enableComponents()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.enableComponents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (ParameterScanPanel.constantArraySpec --> ParameterScanPanel.initFields(Lcbit.vcell.solver.ConstantArraySpec;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.initFields(this.getConstantArraySpec());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (ParameterScanPanel.initialize() --> ButtonGroupCivilized1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getJRadioButtonList());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM2:  (ParameterScanPanel.initialize() --> ButtonGroupCivilized1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getJRadioButtonRange());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void enableComponents() {
	if (getJRadioButtonRange().isSelected()) {
		getJLabelMin().setEnabled(true);
		getJLabelMax().setEnabled(true);
		getJLabelNumber().setEnabled(true);
		getJTextFieldMin().setEnabled(true);
		getJTextFieldMax().setEnabled(true);
		getJTextFieldNumber().setEnabled(true);
		getJCheckBoxLog().setEnabled(true);
		getJLabelValues().setEnabled(false);
		getJTextFieldValues().setEnabled(false);
	} else {
		getJLabelMin().setEnabled(false);
		getJLabelMax().setEnabled(false);
		getJLabelNumber().setEnabled(false);
		getJTextFieldMin().setEnabled(false);
		getJTextFieldMax().setEnabled(false);
		getJTextFieldNumber().setEnabled(false);
		getJCheckBoxLog().setEnabled(false);
		getJLabelValues().setEnabled(true);
		getJTextFieldValues().setEnabled(true);
	}		
}


/**
 * Return the ButtonGroupCivilized1 property value.
 * @return cbit.gui.ButtonGroupCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.ButtonGroupCivilized getButtonGroupCivilized1() {
	if (ivjButtonGroupCivilized1 == null) {
		try {
			ivjButtonGroupCivilized1 = new org.vcell.util.gui.ButtonGroupCivilized();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroupCivilized1;
}


/**
 * Gets the constantArraySpec property (cbit.vcell.solver.ConstantArraySpec) value.
 * @return The constantArraySpec property value.
 * @see #setConstantArraySpec
 */
public cbit.vcell.solver.ConstantArraySpec getConstantArraySpec() {
	return fieldConstantArraySpec;
}


/**
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxLog() {
	if (ivjJCheckBoxLog == null) {
		try {
			ivjJCheckBoxLog = new javax.swing.JCheckBox();
			ivjJCheckBoxLog.setName("JCheckBoxLog");
			ivjJCheckBoxLog.setText("Log");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxLog;
}

/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMax() {
	if (ivjJLabelMax == null) {
		try {
			ivjJLabelMax = new javax.swing.JLabel();
			ivjJLabelMax.setName("JLabelMax");
			ivjJLabelMax.setText("Max");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMax;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMin() {
	if (ivjJLabelMin == null) {
		try {
			ivjJLabelMin = new javax.swing.JLabel();
			ivjJLabelMin.setName("JLabelMin");
			ivjJLabelMin.setText("Min");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMin;
}

/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNumber() {
	if (ivjJLabelNumber == null) {
		try {
			ivjJLabelNumber = new javax.swing.JLabel();
			ivjJLabelNumber.setName("JLabelNumber");
			ivjJLabelNumber.setText("# of values");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNumber;
}

/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelValues() {
	if (ivjJLabelValues == null) {
		try {
			ivjJLabelValues = new javax.swing.JLabel();
			ivjJLabelValues.setName("JLabelValues");
			ivjJLabelValues.setText("Values");
			ivjJLabelValues.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelValues;
}

/**
 * Return the JRadioButton1 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonList() {
	if (ivjJRadioButtonList == null) {
		try {
			ivjJRadioButtonList = new javax.swing.JRadioButton();
			ivjJRadioButtonList.setName("JRadioButtonList");
			ivjJRadioButtonList.setSelected(false);
			ivjJRadioButtonList.setText("List");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonList;
}

/**
 * Return the JRadioButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonRange() {
	if (ivjJRadioButtonRange == null) {
		try {
			ivjJRadioButtonRange = new javax.swing.JRadioButton();
			ivjJRadioButtonRange.setName("JRadioButtonRange");
			ivjJRadioButtonRange.setSelected(true);
			ivjJRadioButtonRange.setText("Range");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonRange;
}

/**
 * Return the JTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMax() {
	if (ivjJTextFieldMax == null) {
		try {
			ivjJTextFieldMax = new javax.swing.JTextField();
			ivjJTextFieldMax.setName("JTextFieldMax");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMax;
}

/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMin() {
	if (ivjJTextFieldMin == null) {
		try {
			ivjJTextFieldMin = new javax.swing.JTextField();
			ivjJTextFieldMin.setName("JTextFieldMin");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMin;
}

/**
 * Return the JTextField4 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldNumber() {
	if (ivjJTextFieldNumber == null) {
		try {
			ivjJTextFieldNumber = new javax.swing.JTextField();
			ivjJTextFieldNumber.setName("JTextFieldNumber");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldNumber;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldValues() {
	if (ivjJTextFieldValues == null) {
		try {
			ivjJTextFieldValues = new javax.swing.JTextField();
			ivjJTextFieldValues.setName("JTextFieldValues");
			ivjJTextFieldValues.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldValues;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getButtonGroupCivilized1().addPropertyChangeListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
}

/**
 * Comment
 */
private void initFields(cbit.vcell.solver.ConstantArraySpec spec) {
	switch (spec.getType()) {
		case cbit.vcell.solver.ConstantArraySpec.TYPE_LIST: {
			getJRadioButtonList().setSelected(true);
			cbit.vcell.math.Constant[] cs = spec.getConstants();
			String list = "";
			for (int i = 0; i < cs.length; i++){
				list += cs[i].getExpression().infix() + ", ";
			}
			list = list.substring(0, list.length() - 2);
			getJTextFieldValues().setText(list);
			break;
		}
		case cbit.vcell.solver.ConstantArraySpec.TYPE_INTERVAL: {
			getJRadioButtonRange().setSelected(true);
			getJTextFieldMin().setText(""+spec.getMinValue());
			getJTextFieldMax().setText(""+spec.getMaxValue());
			getJTextFieldNumber().setText(""+spec.getNumValues());
			getJCheckBoxLog().setSelected(spec.isLogInterval());
			break;
		}
	}
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ParameterScanPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(324, 231);

		java.awt.GridBagConstraints constraintsJRadioButtonList = new java.awt.GridBagConstraints();
		constraintsJRadioButtonList.gridx = 0; constraintsJRadioButtonList.gridy = 0;
		constraintsJRadioButtonList.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonList.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJRadioButtonList(), constraintsJRadioButtonList);

		java.awt.GridBagConstraints constraintsJRadioButtonRange = new java.awt.GridBagConstraints();
		constraintsJRadioButtonRange.gridx = 0; constraintsJRadioButtonRange.gridy = 1;
		constraintsJRadioButtonRange.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonRange.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJRadioButtonRange(), constraintsJRadioButtonRange);

		java.awt.GridBagConstraints constraintsJTextFieldValues = new java.awt.GridBagConstraints();
		constraintsJTextFieldValues.gridx = 2; constraintsJTextFieldValues.gridy = 0;
		constraintsJTextFieldValues.gridwidth = 2;
		constraintsJTextFieldValues.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldValues.weightx = 1.0;
		constraintsJTextFieldValues.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJTextFieldValues(), constraintsJTextFieldValues);

		java.awt.GridBagConstraints constraintsJTextFieldMin = new java.awt.GridBagConstraints();
		constraintsJTextFieldMin.gridx = 2; constraintsJTextFieldMin.gridy = 1;
		constraintsJTextFieldMin.gridwidth = 2;
		constraintsJTextFieldMin.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldMin.weightx = 1.0;
		constraintsJTextFieldMin.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJTextFieldMin(), constraintsJTextFieldMin);

		java.awt.GridBagConstraints constraintsJCheckBoxLog = new java.awt.GridBagConstraints();
		constraintsJCheckBoxLog.gridx = 3; constraintsJCheckBoxLog.gridy = 3;
		constraintsJCheckBoxLog.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJCheckBoxLog(), constraintsJCheckBoxLog);

		java.awt.GridBagConstraints constraintsJTextFieldMax = new java.awt.GridBagConstraints();
		constraintsJTextFieldMax.gridx = 2; constraintsJTextFieldMax.gridy = 2;
		constraintsJTextFieldMax.gridwidth = 2;
		constraintsJTextFieldMax.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldMax.weightx = 1.0;
		constraintsJTextFieldMax.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJTextFieldMax(), constraintsJTextFieldMax);

		java.awt.GridBagConstraints constraintsJTextFieldNumber = new java.awt.GridBagConstraints();
		constraintsJTextFieldNumber.gridx = 2; constraintsJTextFieldNumber.gridy = 3;
		constraintsJTextFieldNumber.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldNumber.weightx = 1.0;
		constraintsJTextFieldNumber.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJTextFieldNumber(), constraintsJTextFieldNumber);

		java.awt.GridBagConstraints constraintsJLabelMin = new java.awt.GridBagConstraints();
		constraintsJLabelMin.gridx = 1; constraintsJLabelMin.gridy = 1;
		constraintsJLabelMin.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMin.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelMin(), constraintsJLabelMin);

		java.awt.GridBagConstraints constraintsJLabelMax = new java.awt.GridBagConstraints();
		constraintsJLabelMax.gridx = 1; constraintsJLabelMax.gridy = 2;
		constraintsJLabelMax.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMax.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelMax(), constraintsJLabelMax);

		java.awt.GridBagConstraints constraintsJLabelNumber = new java.awt.GridBagConstraints();
		constraintsJLabelNumber.gridx = 1; constraintsJLabelNumber.gridy = 3;
		constraintsJLabelNumber.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelNumber.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelNumber(), constraintsJLabelNumber);

		java.awt.GridBagConstraints constraintsJLabelValues = new java.awt.GridBagConstraints();
		constraintsJLabelValues.gridx = 1; constraintsJLabelValues.gridy = 0;
		constraintsJLabelValues.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelValues.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelValues(), constraintsJLabelValues);
		initConnections();
		connEtoM1();
		connEtoM2();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new javax.swing.JFrame();
		ParameterScanPanel aParameterScanPanel;
		aParameterScanPanel = new ParameterScanPanel();
		frame.setContentPane(aParameterScanPanel);
		frame.setSize(aParameterScanPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Sets the constantArraySpec property (cbit.vcell.solver.ConstantArraySpec) value.
 * @param constantArraySpec The new value for the property.
 * @see #getConstantArraySpec
 */
public void setConstantArraySpec(cbit.vcell.solver.ConstantArraySpec constantArraySpec) {
	cbit.vcell.solver.ConstantArraySpec oldValue = fieldConstantArraySpec;
	fieldConstantArraySpec = constantArraySpec;
	firePropertyChange("constantArraySpec", oldValue, constantArraySpec);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE0FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BBFFF09367153E54E9392B1B121E13129CB9B8C2FAEE0EB666CAA6CC623BF2A9137A9A52D247C938E2A6CE623952192443B4DC0E364CF4AD49448AA61070234E0426EE01E203B1B698B046847F82ECA3C0B60A9158C93C12D6765A32D6D92DAC598EF06F7B353B12F6A507397B6318F92C767D7E5E773E773D6F134C6D78637C32D24B8E0EABAD63683F93259CD7FC0A63FE7B425297982EE9FF011D53
	7D7B9340BBDCF158864FAE2035FA8A6C5B38751EF2F89F047795C385765F43676D5C2FBD3E13704144CE8B50EEEE7F2F53296DBC275AF9933F57A89570FC9BE088F06839E8BF325F26D4D2FCA645B3A835F05C1CEA44B3286C2638F540D38EB89740C8B5614F00A7F1604E66A64AB7F8F78E377EC732E58B5223534920184A5AEA48330A7BCD69229AE46BCE6CA712E901F785G8D5E4A8D5A5100DB1CE3439FFABD22D2B761958201BAEFC09402CA1DCF880FA9D228AEAC0EC7823CA2CA4166E6A7AF736302A248
	D73CFC50498705G48D79FC0B61ED14A31CE988F68DF02F1F982329B004F19GF38FB07CE2BF42EFC01F8F96582B8F32311E5E5FC247621EFE0CABBCB2EE5BEAB2565603E40CA3873531960D2D313C0767E5E78445FFA15072810AG385A82FB95C0AB407BB89FF6AB87605966D1BA2471F1A958968A3587FC72C549A78460F3F3B3D0A82ECF70CA320F63481CFFD529B570A79590397B3E309C09C94ED1B67F0B7A3E4B95FD6FDE4B1B86B1A94A193638EBC9ACC235DACC086D82D35B33EB6373B35E76D5CEEFBB
	7648F356B2835B59F31F33AE24FA166AEC3FDA0B663A9E4F75AA780C6410BC411F42787AC39A1E646A13B8E750FBA750F28E315CF03F4DFCF15896F05B9A3EE99807B9CFE6D9AA6A088FFB6B92FD2999C63A1DF54466965517A60CD7F4F8624BE358C774BE87E8364385F612DF9FC4515A8D41FB9640C200BC00A2008DG9575AC478465708CF38C9616529E9183BEC166086C8711DD7094E20A4CF787438A9F748A7D3CAC721E009066A84F7E880A01A7CE2E96673E97EC1C9702020C972E3AEC430467D8649D44
	A38B186A924785B0B09E62185E53116DE85D72E16523100FD7840D03504FC6501CCC707D20919440E792178BB1942FDA98FFA700CB0D43B22C6F02A0C341E8EEF6C852D8A464C2A361965623B87F82570E91781CD14FE2FCFE92615642BB5740747CF764DD1D1ECE510B5C6765C936460E7B114C8E60EFECE0F973CBB74B1B4EAD5F61EA1F3B47B0EF4A9FF84E6AEE206B27A1F17D34E13D4837B98D2C76678B6E992C61460664753543GE7DCB3DE1A110DB2831CF586F2A44E0FCE21B9DB8ABE95822CD7E3FDE4
	220A64EEBB9F96D836DE16A151841F96F962676161CA9AD3AB758761D17CB72BE3799073D49C01398198844884D884D0F8044574DF7CC3A974FC115A19BE362B30ADA3BBDB7E97ED11D8D6C751BEA6CC40365AA90404208FBE04A96DF09459256B5CAF29B247B0BE84F5218396E2D8451F0A22354BAB0AACFAA20AF0C5D094B1B82C51BB302DF0449352CA8A335593C5E31522D097D47E4BD134263CD2B0A860C9D5C5A83D972FE9A1A00C03539DD2A428907C55986A054604C9DAD386E21BB8D4938291D41FC8FF
	114B23F806E4F19CD28515B84EF1942DF7AED2290B51C6148B5083D5812D71A8DB3751BDEC5D6E1D7AB6376C531045E86F19735057AD59C749FA4AB92A2DDB6213E3525877BEDCDBA8BECCFC7D87814541284131E219A7F66C04E7FBC4D12460F9D90A04BA4489B1A0CE893E86F5AD7DF29A594B87BF0AG569FE5EB694713084E6A96DDC49DD2A0C0C28E3416C65C2779F76846D651C86222655193D35B505C8A1FC584588ED0813D20467F4929F793E87DF4AE887DC7D3E8AE823A5ADB02EDAA432C8F4D247666
	B612BA372411581EF58C71550DB03EFAF8C76B388B1EEE0098C09D60093E0DAADF6131D47AF2C546776AB1D28F10BE5BF1CD1FF29C71CD0CB23E3D47091E1663C42F6BB851C7786A47985FB2CADFC51F9BA89F196757EBD06E611EF7141F60E3F561A82C1F66669EBC5B24179D0D14D37E26932CE73D972F03903C4F8218FF026569CF37301EB5FBAC07F36596DA4D7A7325A7089FC5A734BC2D4E0ADAC96EB45DC0B6F7B79558EB1BD85E1C1D566F1BED613064957116CD67F47D34BE0F896E5753EF4153AF4AE1
	259B8ACD0C4D7F6F26113FE8CF27E89A0FEF6D5943694FG4391313959A84DE9CFF8631CFE3CF9DBEFD8B61B0C3735090CF328C99BEFD3569BD652DBBD00FBAE34366683FD95GD9E7369104970322A27248EEF58D7133BE78E7D477FC3EEA07EE5E6A5ABCE1281CDE05F8FC2A261F752E5F7920157A31623B8B38F27ECE0B51D9A26B974316EDA709FFCDA7F5FEBE3B4662GA03EDE9DE13EE6G4F52134C57FF9DC1B51017079B487A1DB729DF3F6BCE12FEE6F7B3637FCF4C2F0C08E162636757180F99AB16F0
	155B3FE668A3DEF34D44B7F73366236D25B5164A17180F7BC466E38E709483907CFF28784AD385F6A7G717518EC646B2CD3CCEE5EA86B4367822E70949B4326B1BB2EB3DA6DA2737379A6142BC2901596E8F7C3D29065E4B5CC88996B026D7A79E839750961D8E7ED9D31D43410B1B636E8E3DDD93046D2D24046FAED0C791681BCCBDB18EF4B46F0DFAB61FED9E913E5FE72CAC8702ABEBA5B0F505CBAA78A81DF385A21BB7606254004A057F5A4CA631CA2BD0394C9BCC72DEB51FAB95B69643E0E1CAFFA70BE
	D4F91A1CAFFE2F62BF41F8278ECF46B797579A74BE8BE81D9F321E7831DB28F6F1E7A027G9A819CG81002CB32CE6FC657323DC1AB3C7F790B638A09F389257A4305E7F860AA56C6B743D6D860A9D5E57B9364F6DABC5391383B6178394812C8758514AFC483F11560733B1C39FC81C9F9CCEF7D6EEEC0D0FB511DB1CD64E1DA0C746B3ED4167B920E581AC84D886D08240359558773631F16D3AF9AA6D38D01367125A706268C86C2A281F9F0F5C5E7846524AF536454B113C5F557E81E738677571BED1A22513
	5D63BC0FFBA3E4EF300D655DEF2DA8BE59701E8B108FD082D05E8E318168EAE7719939591EAEBEEA0385B1E2BDECF21868F88F0A37972713E965065A0D64DE3CCDFB2F25150BA558A3318E48AC56335B5999682151C4DD79893246BEFCAFC92EAE613D245DA877C3E5E8EEB7822D322340DE8F60G8881E48164F63039FD6266699966FE87603A204FB418D66A6B27E337B74671A4398F925ED7F6444B117B05AF63FEEEAD50B682901CB70AF0D51D857626CE96FB67A452BF886FB100B9GCBGD6822C85301D05
	B38AC0930093E064AC1373E1B9B76DDFB3GBF9FE099C0B1GF78E64GDA8106GB24EB13985315CB4F9036C4665D1E7729E138848FF64A77ABC47003F69ECB24EE600E3E3F76AE85A18FF1A567744F33451BDC5A2D831EEED4E93695B8DF0DB8DF06CAC3FD307C37BA93D0F3AE5FE9E7F61AD637378B3370C4F634F5EFA0760C1077E960F51D75FCAFDDEFF651679F97DD029391F0D2546FEB6179A7B59DA1A5A4F2ED2B3BF7F006765678A0AE552BD41A528669CBBA37FEC58CEF94EAA524586D5B7693D0D4711
	E5B19E47B770779CE1A9A2FB05C4DF6F33D42AB42D4AF4AB42B8F20770AC40728AB467C252D83F0D65894DCC7E1FADE671A0E34D9E59CC476A744B5238330156285538964CBF973F1E1650777CF3496B0C58AB3319472842E69C23F7ED66B1FA4F16BEC6FF361947284616BEC6C7ECA8869E51A74A74B4C0E2639175F9F0C40D592E5178189D21B17BCCC6B1ABCA084DBA7AEEBB1F9CB312137F3EEBAB4E69F0D888634D2713D7F8EA2BE8D467C307160BD3E566F17E2BB263B8FF254CBC4EF715250F73B74B4C63
	7C77E569637C0FE529F3715EB17DD835F8BF90C1F7F281FED20AA89C17F99E6DFBCB543B37CAF82F87F083844E13739F699797C751BC7921D383B37884B55405E42722CCF685709783F0002F82E86DE2E7304D23680ECA5FD9BA4430C2E8AF6EFFA70156478707357AF17E9216F5418C60839A3941A2343527364451AE0AC1D56E44C1F4DE6A6998913CE36DD24CA18D93FCB81AA04347D4199F9E0E37F5A9B26E91E46AE7B50AE70F037788813D1D2CF19DDE270B3FD1216275FA3EF4C6431331103377FCC9FF76
	4E69A231DF59454E66616BC67492157EE0391E3E01522B3A997D751811FC23CABF98B422F7297430D7CF9F6CA6748C157E11E0C44FD669BFB0144FD169738C65733AD9EF76ABFC86D9896FA59AEE8A61B640FBF98F43DD1DE6BD5C8E95B7CBE632BE9537C2E632B195F7A84CE4B3FB581DC7D104FDCFDE88380A8BECBDB48C219ED9BDBF5571D12539793CAC7A5A79E187DEDD241F5FBBD1CE65BBAF30FB433E892473F3F84F86D8F4015D9B467E426E5196FDEB81573548785E3058F543327C8229FFEBAFE877A1
	6B4656D8084D79D1E6F32F2340BE68C0389FC77571C5397813D2F29EA0F4AEAEBF501A2C617474A3F1F4646FDFDB74F4DF9C3DCCA7CF467674BEB6360DCF67F201C7B3CDFF333050C177C57A1CDDF2DFE9496AA9AB097B3E6A3838EB67D6F55512983CA0317BDA6EE2011D609E3E4EFE0F50F4114D63077D6F9B6BE315031E4BFED64E7405D4FD87A6193E9C956789B2DC31EA63F53F497DB22E8254B7AF53DFF909694A92984E2962FE23724DD2F14F8B6C4C386C922B39F63CB6481D1C602B380C782A26D8DEEC
	507826717DA3844FF9997D3EE5F1978AD9BE668FCEF752B9493E1C3C5F16F25A77F2B8864003E08F4567DDA6F7D888087D45B20B4DB6A71CA1303D42EE4DDEE718592BF29A5B3326305768546C119E6265243B39F4C064565D26DC4D449715A3F7FEFB23289E346A7CA778C3986F5061C9CDF2CF301A6486DA1ED33D8B6CC1F9D6558BB595408590G48824869E5E7761ECD75DC1AB33BCB8AB920998A680E84749EA2FAFB3FD77937B7595ADF7278435C52AFF71AFEBF1057CB3F8F214F6AD76EABC5CFB23E9E87
	7E3E388F729E4081608308814C6EE3637B4626DAB23E33BED1E9D3945EBB023F89E1036B97052806C60D8C99DB1114EAEC8BC17F52BE237B07C72E2712DB51171C4774DE295CC82E18722FB715BBBC194ADE7995B33961E0AA395D26F20FF80D646AA9FF27295C5D02111C1B7287CC65FEE7E8AF0372E71B4A2DB63417C379736039B2C116543F5726D0AD7573013020666B7C287E77F5CDEF5DE178BDB302922AF383FDEE5CF9FFA9517113813D0EED7787AC25A6BA6ADDC43645257B8E905775F9536A1E8334BF
	F2642E231C537E119A7A7CA5CE757D258A3656F67F5303DC678B1D16BDA6F6F3283D8DBA3B5941AAAB09CB3E0C7785B9A290DFA2E166CB7DC72CF6DFF4F2EA7C8FC4354FC7F51FBF1918496FD5EE97080D687F298DFABF541362FE284764FE284764FE28A7757D4F6A9E737B1F17FB743F57A0F5718F3D28779A01F9498448814883D879912B09218B68FBD2744B3264FAF856B7AC306F37187DAB8E7331DEF7247F8D1A57013EEF76C8F01A9BC77A99DEF42079D22490C692DC5001E2A40B43A38AE125F8450172
	ADA078B11A74672FCB2CBFFF75A35633AFF6A3DCCBBF64E4BF43BDF51D7179D45C9B652C5FDF22622E5A995FB295F7EE126136DDE5381BC1069BD2F1CFF9193EDC95375F477872D4DC0E4078AA5C8CE777B33ECA95374B4778C6D4DCD455272838DF297A162B38AB2A3E15AACE90D8DC2A2E317E20398F619A615DF90D65C6594503706C67C7E9A70036CDC9465981C827AC0584D919A4D8FCDE25B9F76C8A274E55050B7B4CF4C00675C04BFB59B38AFD280B9FD6576C42E9FD3D8E01BF1957D25FE3933BC3AE54
	A34E815EA5G2BG5681582E9758F7835483F4823881C2G33G66826482AC87A884D88B3071BA9B7F7B17EBD20C1F9E68708211F991AD916A7FD4D86F7F3601827B5E01547E93FB7BFA4D57D6EDEF7A35F52C977F3E835604EBB2C46F021AFB511D0E64770785F58D1F695D26623A43CEA9AC22BBA452E73F95E2FDF613014FEC2FA3E79768BF87C81F3D1362068634FE146E95EE949372BD3F0F767AAF3A59D9A3G7C446E1A6B4CEE0609DD2B6E191DE0B7C7E7176CBFAD7DA88769D9454EF4AFB1507DA60544
	3343CE2AAFDF271B687BF81269ABB47135CC676B5A84DFB7A6693BA74874158F9A6B537B37FBB0DEDF7DE06258BF3D2A0D7DC39F535DE2205B4A25BEB7B913F45F6156F4D779196EC1935DE5A9F40712F4BFFECD53FD176A7767A63A0D660C691E1D243BE1C053BD2A7ABD57C4F72A33EB6EE0625975AE0165F35EA02B9DE77A10AEF59FA765112E3FF3FDE8CFD6FF3304FF22F58598034324BEBF01FB3B15548E41AD43383594C777C117F93D18F6252F9719AE633D78EFDC49FB7157DD46FB71BDAE7DDECCF077
	3B503C8FC3FDF42BFC73D45CB59537906BC4382B22CF99D1718FE33F903ECBC08699BE977B9545B8B745BD2A43DD233847DD28664110C7A41949FE5F05FAD63F988810779FE09EB188A5D00DC7BE76D18C71BA3F1FD1F157543938BB1CBCBF77067573C37A62D3A156975BBEB66A51571DA139B9BA48F12CDE3E61561FB9081E3787181E1614FA3E76B1274A4C33B39965E3561367FE22516F1AE474C5A9F5BE7B29A673D41049142414F9E3C81311FCCCE677A74C0F77BC9A3D574F68AE151E6155685FD76533D4
	7AB9C1237F1DAA3FDC250BFEAD1E8F897AF812FA447FC5771BC11CFA843FBB36C543335423FBFAEBC83F27EF84DB5BBECD3D2793396D83FA393DA053B4A33905F63D1C83E406E6A47737F1F7G8A484C1A115C53F1F7GF3C1A6F7C6F20ACFAF379CE4D64DC82E5F2F17FB95E438211948AD0F333783E42AE7A4670F7B4DCD8B48B8534851F3C0A8F92D8F0674EB1D608E8CA4739D9EC866FB42EED05B6D49FC8FCFA673BDB2194C77F2B0196F6741E43E2F7A1279E67B1279467D49FC32BF19EF35013EE283FDB7
	44E43EBBC4BD9F79FB4B33329FDE963D4615A91C8DFD8528F0E5785B3E304CEDE55F2254F563FADDBCF134597A4A03D68BE76B66B6117D2B1B3321CD281BAB43FBD4B7E7057DAB067EC649260770E75CCD4E1208D5B82BC39A067FAF22B65EFA110FA95C66BDD5123F0A7C010242EDA25F47990B7FFF43327B201ADB7AFB288DF7FACD6F32B2BD7477C19E6DBE081C39DE40FF0BD58878F59E567B6CD78EF166E73EAB63FCA02070013136208F1D7F5AA795C13B5FA8D2107EB06321C47A5B978579BD2A13799FD0
	CB8788B88A100FA594GG88BCGGD0CB818294G94G88G88GE0FBB0B6B88A100FA594GG88BCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGDF94GGGG
**end of data**/
}
}