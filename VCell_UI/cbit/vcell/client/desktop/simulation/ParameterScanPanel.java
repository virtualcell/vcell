package cbit.vcell.client.desktop.simulation;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (9/13/2005 9:38:19 AM)
 * @author: Ion Moraru
 */
public class ParameterScanPanel extends JPanel {
	private cbit.gui.ButtonGroupCivilized ivjButtonGroupCivilized1 = null;
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
private cbit.gui.ButtonGroupCivilized getButtonGroupCivilized1() {
	if (ivjButtonGroupCivilized1 == null) {
		try {
			ivjButtonGroupCivilized1 = new cbit.gui.ButtonGroupCivilized();
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
	D0CB838494G88G88G620171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BBFFD4DC5519C42392031198CCC8C40B89AAD1F46991EDCEF5F7D3CF5A52CAB53BC75B6C9ECECBCFE31B1CE637D94DF653B53617DF8991E2D009C5CD8D5BC4A50A18441CB5A5D8C90090A291B6A1A3D68C73E66641BC98668DB3EF188186586F7EFCEF1877864C697E31B9674B1B7B7D3E5F3D773B5F3D6F012A8E24179696D5A1D4D80A583F8F8B91AAB809501B4B1F1B6438E42F2D9C997E7D97400B
	2845DB824FF420252E3615D7221EAF4B201D836DC31936725F436F175117DF3A7E84BFAC6CB4826D496F34CC473773A7E1E797E9AF692F00678BGEBG071FA5AA36FFCFFFB54367B3BC0742A20436921E6F746FE73833503683E4812CD5A97F86F86EA6162FE9B84178FA97E722ED5F77976D41FA8CBAB91C70D159DE762CC17FD9D8B202ED7D0A7809E5D200168700BBDF0CFAF7FF844F462E2101BFBB9D0AD6BF6A14BD1EFA27C7113DDA3DCB8E8CE92A2FBE208C87BD1226285E0606AE49AF8D4B1A6C3F6814
	3CDD12D77600FC82317161C09951897D01F6059F631E33E33B6B201D8F30CD601F16B03E8BFE3B81927C3C2F9F3D5D487A52B3718D34F1605B3B77DA74B555CF7B38562F7735669DC7712BE4DC46D49C7F9DC02B89584A0F8374G0481D281DEA773A1417B8EBCCFB934E6F5F8D8751E7279CEFBDC7E732ACB76406F068620B05C8559297ADD8851B17F777EBA93FF628115AB9E782AF2B4A6199F70714F3AF09F4A3FFFE951AE131864E7CE94E587E8ACF283FACC286DC6CB5B1B825173B35A76E748E89BEDBE36
	2B5444767E9F9E5B3503692954F45BAD9A9E6B91B256C760B7164372949FA178C4831E4E55E202476DB42021A01F9BBDAFF0DFBACAD6224A234BCC63107AC8D211ABC8FD0884E77AB268403AB30354669E614B984167997054176D44975C5E8A345ED1DBB91DDFD70D60351B8B6D0DGBBG2AC2B04FG3AGDCA1BE473E67FDFF0EF38C9616F6BA28F8DC329FD1592B54D760A907B53F546E8DE81257A95BA43FA2B9BCF2GB11E05AA0E01A3CAAE194CFDA758E8133D321FACDC31EC8314E7397AA266714B045A
	2D8C4BE0E05807385E5B5517616911825AA51FCB52E41D0352D72BF8CCC6A59BE88485701B4665EF7CB8DE73217FE9GD9A28E07FCD85FA7329F92C6C3C3072A8E85FD5D38A7E8C7884779A2499DEB617706900F71BD810CEB8A435A8DF3BDDF78DEB268E9D11C58FD49BF4657D8C842B29E60CF8A73F9732FBDFC5E346C399755BDF933693CC906B91C9DE66BA7BCF37DDCC574E25F3606F96E5FAD754CE18DA705E357174784E71EB37EB8A79B25A63862B94851B83F9A44E3D6B9E6AB2F8368986331DE6A2B21
	F37734941079EC6D7443C413DDFA64291FBEC7B50BE9B1738763F17C4BC5DF26890F8B74A78164G6483140C035DF19E535FCA76B8FA3ECA6E1CBD362FBA6C96F6767C95ED51D8A60D60FDCC9E05ED35C57649DE977C88B05A1291EC172D73FED548ACA7F89F6405E6D80881017F5A88DE3B12267995C7D013AF4A1A26F887F47ADD44D6A0687069A9055B3AEF8477D58DC1DE907C770F60B565D43DDE198C2A90E17487481A16BD72B0B85D2C863D9A45BF6447355010BC46F24A5F7BF7A21C93BCC11C1FE8FD11
	AD61F87A7C4AB0CC951C62D046B8494383550698F5838E57C9E1F8A60D73F59BBA48576D217155E8ED1F2F48EC6FC93D634622AD63F4BD2189FD5DD21F3283663EBFC8F28B4387282F374BB88EE685CEB16DEF55BEF81E8EEA1A6AED732BC1DF33B22AF814F159F5D42C258B2358DE8D78F19C20E3022F25E39A26733C4596D1336A71501083ADE5025404D21521EF2E899A97FD9E9D88D6623115C702B2EC87B801FEA2627FD6702599B49B9B8BCA2F8F62317098F2EFE3845BFC40416B50FCE6EF5B844DF39591
	EA3B007029CE4E1788ED3C0E5361198D309E209E70D45FC7AE4ED7B799CF5F3F893E16C91A8F303E5EC9DDDF5EA4667B064C79A213D4CF8A531BB5C975D13E91D1CFD7CFD17A91766C1A22FCF41C1FB90C679E29F98725D1A9DC9F88417AE9E8B8C7C61B5632CF107D8873A7CF711A958D61314D05F6814076A9BECF7FE18F2FD9D306B2D17E66233B2C6A734AE96A4771E9FD1E667C6A54CEBAF752C2582686347953FC5E2C8E9A774DD301006AD448164D4634E754B80FA96E4B516761592F7883DABBA41AB09F
	FF67A876976F698C4D62F167410348F8869888AA8D8DE6531C550413E70C7DFDF86FE4D139C5FFD3D9BF57987AFBE838FB972D2DFECCEAAE3C3636837DC8022D1C2E33D6A746ABDEC5D3A4EC3776B0757308770FB86F39DC359D06F12BBF6588C066F4EA54631307ED3CF63D770DA666C75EFDABD119F4CD1159D9A2E973C011BB017A171C207B19FD1323F89DG75B55549FD5DGBC1573382F3FF3629CA879870E52753B5BE3DC3FE76751FAC6937CEF3BB03F66D682544748675C47043C9CD47D728D26BE12B5
	B70F7A16BDCF77B1126EA8CECE67BE763B380F68AADB7989G0AFFCD66F89F60B281282F9F8E1A79DAA0648AE4DE07EF83DC5D553C8FED7D3BC91E51F3979D1F48CEBCD7E5AFCEADD06E7AD4AF1E1335B0A0342FABDFB60EC76374E5533EA66EF597392E26FDCC3ADA6F6BD9980BDEB19EAB7A39EF9B0027F2BE772DF0005435AA291735D3FE3FB4F651A7BB050FDD270F3139F5C611BD2EC0ED0761589BD0BD23323F3EF926B419D334E6782718F3545979FAAD57BBBF362E23670B9C12BBFC7369796277821FCB
	701986BC6D5FCE02476D822025DD43EB625747F16E6A39966694C092C08640BA000D57721C714B518F50ACE70EF6AFECF0DE49F3B12AC86035FF88479276F5563EA304E3C776F54477398593F86E206BEC657B810E83F4GF82E63BE5405E77521B5EC6A830D73C647ECE765246B22E3CD651E1ED5AEFB069C6D4FCFA264BC87340DGBBG2A93ED650DGBDG11C45E2F717049D97B050B3CEE7594D99C4DB32BAA6667ED4EAB6BDF562CF2E90951F2F45E3FF27A8DE43A67DD10DC0ACAD3A73F47E97214B1FBB9
	09FC5E75CD6178EC01F65982DBF98DC0A3009DA082103E0047A7AFFCFA367808828BE244EB5858B0317EFADCD7962771D96556ACB0132B112F4CDE7D2CF26BE7582331768E72D8EFDA404FC0BF0F51D5F3FD342C398F5BE2646AE7349B2FB71B7BCFCC63316D861A8FA091A083A097E0A3C059C2BE368746BE1A635CEF865CD928B3CD0615791A9EF307B637BE5E97A377460CF66D42E8B9FA3F301554F3CDC06B8620389FA8989706F672C29E7BEF8EE17DB950DE8F309520A249D6DE8B5084508B9086C8864884
	D81B44659EA3F2592CFE5DG786DG55B740BE8C50839086C885D883306186AED7B114BD4B3C41F62366514258BDE6A6E07F43CCEE2B89FFF2D2AC2E5784477B1EE92069FDBEB92B6FB34F51E67794B32118685647FCA67DE5935CDE939CBF4B6FB36070FE4A6E23462D4F636F0E1B1F470F0E1B1F47BF9CFF1162C10771960F53BF9E0FFFDEEF9B37BE2FAF1E3076B3F5425C4F5BA64C7D4C18086FE75604151FFFA063728B8D47B2661E60D34866081F11DFF76EE6BC2D1AFA7E28503DB9E25D0F2D9173FE7CBA
	0267CFC08D7A1D72CCDF1F0BD48B1A1EE55AB5F9983BC3F9F6CEE2F98D0AF3B9262FFBA62B854DCA7E15C92BF8503EBE69ACE7FD6D6A772B43DDC7D90EEA7560DC307D066875D4310855320BE257993527CCD94748B7E59E2351A96B98CDCE4D9E23EB272CE3F443546CB1DABA05E360D0DC0A1F1D86E8ECD6390C7360180859E7F2F44C0E31188586F14C0E4F084DD9566EB509991D135FFBE5AF195301009CA01BCF0B24C94C569F7A8DBEB46BF371475356F1FEEA5ABC4E3F1C360E73535333477959E96BB897
	215963DC0E624F459FC575D50F77AEB73E1373C8E3EAD0C3A8FF915E775EF0733BB79F349381328156AD2267BFDAAFFEBD0447299FAAB5B0C3CED0EBEE44329B95AEFB885AA7GFAG42G29B772B3D879A03E23B2D6169DCAC023341F3C7D628C5A8549BB20670F36CF09ECB70CGB920519BACCA5BF4F2CF946D3C6295F29F3E0B4FCB671A5D32F368349A6ED087A8BE901AA1A305054CF75F0F3675E9F058A17B191F35B81E67BAA4076CB15AC99AB6608D3A24498A01B76A397AE39DCF7BC24F5E6FDDB21E3D
	D1B20DFDEDB2BF1B7F6D24993DD1509F9DB5523B98BDAC685F9EB013CF9274E92799BDDD50DBAE9B69B90C3EC1504F7519513788FA0A29BCDA4C697FE1AADF351857E63D648CD28B6DC69DB70CF1DD50368B5C3706F98D6791386B87396C43827758A017DDAFF06F8FF1597C457C4E630C1B3FA72F3B49D66E3A092F0723F6DCA30B73D33D94529A9A5A7C0A6B34B450C1D6972D6797394B18FC5ACD7C5E70E1227389E8EF81A8D9426F8D43EF71FB342CBBD72275CF195F9B96F4CF966DDFC273DF53927DBE6478
	BB0EE2EA7379C1EEB382741CA598F7A26AEE874F45478B69F90052872368F8CD9EC6C6FADA949D7B3B2048C8FFAC0ADEEA1027FDFB74CD5E379D0FE6A34F8373AD3FD958417B14421E0FADAF2CCC6D5AC9637EE6EDD45C75F32BD835B4862ECB7C3E368774D05C36C97EBDC2F28A9F47BF5BDEB75747B387BB179505383EDC21EF07783E815D4CF18FBAB96E444D5C46B37D96774BA48BD039094BDC3FCF681A5747F11982772860AB9038FABBBFB3D6AF65B9B7C0568B3D1313DD2E25E49DF8783C68527906497D
	A384AFF3A97E3EE5F1B36ECE4DB24C3FE138050D491625317BEDA1525F4B1198008C06038CDF350C5EE1E120766F9F6431F1830D5A4BE9516D1D996276424B4C6D95473117E430C7EB088E7715FD177117AB14DB721565681D5FA2856703D4037F943F0C60B38CF81A139E91B9A99BE8D5297CCEE6D9AB1EE7E1E827GE4812C835888001673B37BFD68881A654C5E2D7ABA209872980E8434FFFB872F6CFB15EF6D62EBBF671BF723B55736D83E9F28DA4E5E0730671A3C650578C97B77CE9BF9DF8C3484008C00
	07815683EC927D7BB52A237DEBF5A95AA9CD131CEE72A604F74E2648A19D0D8B995AB765D23C3E6DG7D15AB4C6E9FFEB399CF6E500A58F9CC65F6054C64CEB07E8ECB394238F34CEEA977D05CFBC24DD2AE613219DCA263CF33143B364FCCAE1B712F3314DBEFEAEF83635FE2A977015DCC8E5DC2792B60D9FBCB34ACBBFF8C63DC5AAFF98232182F69A16377F5A71E3F4A74BDB306C626330BBD13FE373C106AF87C8B230E4A951E22C28B9D09CCB66596FDAF27F9FD37D76CB9C0BB0068DDC799527F519C7AE0
	BB923E7F34022F357D772CC2ADBFEAA9BAE8E19725B15F53F43B73FB0F6F26F1797EA059977CC1197A929A623EA426715C7D5EA7C844BFC551FF5FEA781D6B1E4B77AAD78A5446CF7FCFED503C125EEAFDC63C2B557C0CF8EF2B793D4B7D2D716FFF9EEA353E7FF92455783D864D0B4FFD02EB2F35B0AE79G68D65893GEAEF65B9316B8CFECF0A3FAC0B4D072D2E81193F5F6276BFE8336EEBC35B6C5F20B53761774D8E95CEF343D8BF473737617152D49FC1D25C27EDB8C6FEE5402DD1D6066FEEA35FED497D
	84CD6B7304CB3CBEEF3915576C4FCEE0DC8A34B3852EF20A73BDACF0EF04F83DDFF19B47CD07B8DF3540D53BB94EADF07F686438B582376CB257D776B50E5BFE1973D5895C2DFD1C4FA5F03F6D63FCBE017B055037D6604E8A3EBC01BBE767FC7B53B96E9EBB672B9538BB6DBCAE61F4DE9F3CF69E6312201D194E6746D3ED6F4253A68D32CAGEF1B2A1F4C8EC0F679D51F6C5746A8161CD7591C5B77A992E3756E59B72DF4408CBB87A5EF27E3906A50EEE9C02C59973C46FC1D8B7E6427473F4726F61BBAF10D
	3895F8ABEE37159F81B88B508B2081A4822481E483648294GEC83A8DB8971822883E88268DE497B7FEC7B61B87DE787BA32C07C1202178873BFB2E4745F8D7AA2AB637BCF6D15F6D82F2D0A0E5957563E8E72FD872C096EB19F3B8BFA2D835F69287D7D81D92C61839D1582579E68D2838A3EC322F57632CB3C4ECEB671196FF5746C827567CADAE76FE338B5AB75FA146E95AFC5F0CC68FBFE972B758FC778D9E39D70D33BEFCFF13B9BAC6C969B1EDBE658C52BF43BF47F597FBF98CDF5AF8DF15D952B4CEB04
	DDA8766C300F692BB168267A5EF7F3FDF5A67ACA8D0075B52D0A7635BBC65FE6A757E73750E774CF1B21AFB1266F9FDF507B3E370F6BCEB151DD0C621F1BB2E3F4BBAE6A3A7759396E9C8B5D25F1F4674668DE512D6B6E93FEBFE1215BEC4C386ECDB13ADF34693A9F91FEEF33509D6F6CDA16B1736C7A381D4F672A8C717E73BC56A576F11A9E597AFB6BBC5E1345B7CB64932D0B40689D207979CDD25B55B2BB94778E41B5B19C5B87BB2D7345971D33678BD72779DEBC58993B972B1D66FBF130533897336F9E
	BA71388FC0FE6C91FC53827739404DA7BAB16EB3452539857EFA6297461F1531C10ECFA6FE05882E0761EEB660BEE738951DB867C1175D2ADFFC875B09EB56FE4563216DD504C771C28A947148A4BEAABE4960773D827739980BA5C3316313BAE49C9FDA97670AFB265E8C339AFD6B47F4EE1E7B8CA11EAF1BA246B3875553B34575244455B30C5DE6B25F95F7D2F9993CA6CF7EDC275B5D1CDEF2C7BC1D777FC517F94D45E59A634A7C708BDD26C95CD3E9F7F0BFFE532B534F58B9BDCB506D17F5FA32105FA868
	EF59F57AB3C2FE7F1D1C5EAE69717C395D98CF1A0F24378C5F8C1229C7717B43FBF4BC1FFAECCF7FC1549DC0B758F25F99FFCF27F2DFCE9965A2A013BCA7393C28BB008C10D9B3A7B9B96A8EA08FE48A66A4F7A06A9B18EDA0D3F657DC64DA234E727BC166481C64BE09BA133700CC4F1C64EE0E3267811904B9497DDD549DC78A48E44EA2C7577546CB31EBFD73A563DA27B8EDAA16EFECAA166FE721D83ECD21D83E08BB16EF1EBB166F3FDD31FCA7DD31FC3DFD31FCD2DFAC1F439E4BB7E00F654BB25177F593
	FDCFBAE3797E59E96423FFEF395A538F0D2CFFC1259AAA412FGB5D4CA5E76857CE8AFFF0BD25FCE72F54168878D45BFDBD5DC04CA5A51CE3AFF3523923C893523D232C73523E2583F4278EF14CA0C9098C1D328E8A6D6C3459D6AG7CFF9E177145672530064A8F56287DB5748F94B434133E0FB3977F7F066577C107270D77D05B2FF1DA5EE5652FE65F872D566F0368196B240A47326EEEB8EB5D4DEB1F055E771075196F62306471E812E7681457054FFF2747B4D93F5FE8D2317EG67E1C4766D8B79BB32C1
	034C7F82D0CB8788C87C2E88AA94GG88BCGGD0CB818294G94G88G88G620171B4C87C2E88AA94GG88BCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE494GGGG
**end of data**/
}
}