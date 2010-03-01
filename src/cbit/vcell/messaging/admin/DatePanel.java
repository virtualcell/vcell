package cbit.vcell.messaging.admin;

import java.awt.Dimension;
import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (8/29/2003 2:51:26 PM)
 * @author: Fei Gao
 */
public class DatePanel extends javax.swing.JPanel {
	private static final Dimension COMBO_BOX_SIZE = new java.awt.Dimension(50, 20);
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JComboBox ivjDayCombo = null;
	private javax.swing.JComboBox ivjMonthCombo = null;
	private javax.swing.JComboBox ivjYearCombo = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private java.awt.FlowLayout ivjDatePanelFlowLayout = null;
	java.util.Calendar currcal = null;

class IvjEventHandler implements java.awt.event.ItemListener {
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == DatePanel.this.getMonthCombo()) 
				connEtoC2();
			if (e.getSource() == DatePanel.this.getYearCombo()) 
				connEtoC1();
		};
	};
/**
 * DatePanel constructor comment.
 */
public DatePanel() {
	super();
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (8/29/2003 3:04:18 PM)
 */
public void changeMonth() {
	int month = Integer.parseInt((String)getMonthCombo().getSelectedItem());
	int year = Integer.parseInt((String)getYearCombo().getSelectedItem());
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar(year, month - 1, 1);
	getDayCombo().removeAllItems();

	int maxday = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
	for (int i = 1; i <= maxday; i++){
		getDayCombo().addItem(i + "");	
	}
}
/**
 * connEtoC1:  (YearCombo.item. --> DatePanel.yearCombo_ItemEvent()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.yearCombo_ItemEvent();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (MonthCombo.item. --> DatePanel.monthCombo_ItemEvent()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.monthCombo_ItemEvent();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (DatePanel.initialize() --> DatePanel.datePanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3() {
	try {
		// user code begin {1}
		// user code end
		this.datePanel_Initialize();
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
public void datePanel_Initialize() {
	currcal = new java.util.GregorianCalendar();
	for (int i = 0; i <= 10; i ++) {
		getYearCombo().addItem((i + currcal.get(java.util.Calendar.YEAR)) + "");
	}
	for (int i = 1; i <= 12; i ++) {
		getMonthCombo().addItem(i + "");
	}
	reset();	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/2003 3:17:06 PM)
 * @return java.util.Date
 */
public Date getDate() {
	int month = Integer.parseInt((String)getMonthCombo().getSelectedItem());
	int day  = Integer.parseInt((String)getDayCombo().getSelectedItem());
	int year = Integer.parseInt((String)getYearCombo().getSelectedItem());

	java.util.GregorianCalendar calendar = new java.util.GregorianCalendar(year, month - 1, day);
	return new Date(calendar.getTimeInMillis());
}

/**
 * Return the DatePanelFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getDatePanelFlowLayout() {
	java.awt.FlowLayout ivjDatePanelFlowLayout = null;
	try {
		/* Create part */
		ivjDatePanelFlowLayout = new java.awt.FlowLayout();
		ivjDatePanelFlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
		ivjDatePanelFlowLayout.setVgap(0);
		ivjDatePanelFlowLayout.setHgap(0);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjDatePanelFlowLayout;
}
/**
 * Return the JComboBox2 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getDayCombo() {
	if (ivjDayCombo == null) {
		try {
			ivjDayCombo = new javax.swing.JComboBox();
			ivjDayCombo.setName("DayCombo");
			ivjDayCombo.setToolTipText("Day");
			ivjDayCombo.setMaximumSize(COMBO_BOX_SIZE);
			ivjDayCombo.setPreferredSize(COMBO_BOX_SIZE);
			ivjDayCombo.setMinimumSize(COMBO_BOX_SIZE);
			ivjDayCombo.setEditable(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDayCombo;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("/");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("/");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getMonthCombo() {
	if (ivjMonthCombo == null) {
		try {
			ivjMonthCombo = new javax.swing.JComboBox();
			ivjMonthCombo.setName("MonthCombo");
			ivjMonthCombo.setToolTipText("Month");
			ivjMonthCombo.setEditable(true);
			ivjMonthCombo.setMaximumSize(COMBO_BOX_SIZE);
			ivjMonthCombo.setPreferredSize(COMBO_BOX_SIZE);
			ivjMonthCombo.setMinimumSize(COMBO_BOX_SIZE);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMonthCombo;
}
/**
 * Return the JComboBox3 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getYearCombo() {
	if (ivjYearCombo == null) {
		try {
			ivjYearCombo = new javax.swing.JComboBox();
			ivjYearCombo.setName("YearCombo");
			ivjYearCombo.setToolTipText("Year");
			ivjYearCombo.setMaximumSize(COMBO_BOX_SIZE);
			ivjYearCombo.setPreferredSize(COMBO_BOX_SIZE);
			ivjYearCombo.setMinimumSize(COMBO_BOX_SIZE);
			ivjYearCombo.setEditable(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYearCombo;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getMonthCombo().addItemListener(ivjEventHandler);
	getYearCombo().addItemListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DatePanel");
		setLayout(getDatePanelFlowLayout());
		setSize(159, 21);
		add(getMonthCombo(), getMonthCombo().getName());
		add(getJLabel1(), getJLabel1().getName());
		add(getDayCombo(), getDayCombo().getName());
		add(getJLabel2(), getJLabel2().getName());
		add(getYearCombo(), getYearCombo().getName());
		initConnections();
		connEtoC3();
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DatePanel aDatePanel;
		aDatePanel = new DatePanel();
		frame.setContentPane(aDatePanel);
		frame.setSize(aDatePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void monthCombo_ItemEvent() {
	changeMonth();
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (9/3/2003 8:02:44 AM)
 */
public void reset() {
	for (int i = 1; i <= currcal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH); i ++) {
		getDayCombo().addItem(i + "");
	}
	getYearCombo().setSelectedItem(currcal.get(java.util.Calendar.YEAR) + "");
	getMonthCombo().setSelectedItem((currcal.get(java.util.Calendar.MONTH) + 1) + "");
	getDayCombo().setSelectedItem(currcal.get(java.util.Calendar.DATE) + "");	
}

@Override
public void setEnabled(boolean enabled) {
	getYearCombo().setEnabled(enabled);
	getMonthCombo().setEnabled(enabled);
	getDayCombo().setEnabled(enabled);
	super.setEnabled(enabled);
}
/**
 * Comment
 */
public void yearCombo_ItemEvent() {
	changeMonth();
	return;
}
}
