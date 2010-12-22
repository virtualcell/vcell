package cbit.vcell.client.desktop.mathmodel;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JMenu;
import javax.swing.JScrollPane;

import cbit.vcell.document.GeometryOwner;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.gui.MathDescEditor;
import cbit.vcell.mathmodel.MathModel;

/**
 * Insert the type's description here.
 * Creation date: (5/20/2004 3:35:42 PM)
 * @author: Anuradha Lakshminarayana
 */
@SuppressWarnings("serial")
public class VCMLEditorPanel extends javax.swing.JPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private MathDescEditor mathDescEditor = null;
	private MathModel fieldMathModel = new MathModel(null);
	private javax.swing.JTextPane mathWarningTextPane = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == fieldMathModel && (evt.getPropertyName().equals(MathModel.PROPERTY_NAME_MATH_DESCRIPTION))) { 
				getMathDescEditor().setMathDescription(fieldMathModel.getMathDescription());
				updateWarningText();
			}
			if (evt.getSource() == fieldMathModel && evt.getPropertyName().equals(GeometryOwner.PROPERTY_NAME_GEOMETRY)) {
				Geometry oldValue = (Geometry) evt.getOldValue();
				if (oldValue != null) {
					oldValue.getGeometrySpec().removePropertyChangeListener(this);
				}
				Geometry newValue = (Geometry) evt.getNewValue();
				if (newValue != null) {
					newValue.getGeometrySpec().addPropertyChangeListener(this);
				}
				updateWarningText();
			}
			if (fieldMathModel.getGeometry() != null && evt.getSource() == fieldMathModel.getGeometry().getGeometrySpec()) {
				updateWarningText();
			}
			if (evt.getSource() == VCMLEditorPanel.this.getMathDescEditor() && (evt.getPropertyName().equals("mathDescription"))) {
				fieldMathModel.setMathDescription(getMathDescEditor().getMathDescription()); 
			}
		};
	};

/**
 * VCMLEditorPanel constructor comment.
 */
public VCMLEditorPanel() {
	super();
	initialize();
}

/**
 * Return the mathDescEditor property value.
 * @return cbit.vcell.math.gui.MathDescEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathDescEditor getMathDescEditor() {
	if (mathDescEditor == null) {
		try {
			mathDescEditor = new MathDescEditor();
			mathDescEditor.setName("mathDescEditor");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return mathDescEditor;
}

/**
 * Return the MathWarningTextPane property value.
 * @return javax.swing.JTextPane
 */
private javax.swing.JTextPane getMathWarningTextPane() {
	if (mathWarningTextPane == null) {
		try {
			mathWarningTextPane = new javax.swing.JTextPane();
			mathWarningTextPane.setName("MathWarningTextPane");
			mathWarningTextPane.setForeground(java.awt.Color.red);
			mathWarningTextPane.setFont(mathWarningTextPane.getFont().deriveFont(Font.BOLD));
			mathWarningTextPane.setPreferredSize(new java.awt.Dimension(11, 50));
			mathWarningTextPane.setMinimumSize(new java.awt.Dimension(11, 50));
			mathWarningTextPane.setEditable(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return mathWarningTextPane;
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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("VCMLEditorPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(354, 336);
		add(getMathDescEditor(), BorderLayout.CENTER);
		add(new JScrollPane(getMathWarningTextPane()), BorderLayout.SOUTH);
		
		getMathDescEditor().addPropertyChangeListener(ivjEventHandler);
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
		VCMLEditorPanel aVCMLEditorPanel;
		aVCMLEditorPanel = new VCMLEditorPanel();
		frame.setContentPane(aVCMLEditorPanel);
		frame.setSize(aVCMLEditorPanel.getSize());
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
 * Sets the mathModel property (cbit.vcell.mathmodel.MathModel) value.
 * @param mathModel The new value for the property.
 * @see #getMathModel
 */
public void setMathModel(MathModel newValue) {
	if (fieldMathModel == newValue) {
		return;
	}
	MathModel oldValue = fieldMathModel;
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(ivjEventHandler);
		if (oldValue.getGeometry() != null) {
			oldValue.getGeometry().getGeometrySpec().removePropertyChangeListener(ivjEventHandler);
		}
	}
	fieldMathModel = newValue;
	if (newValue != null) {
		newValue.addPropertyChangeListener(ivjEventHandler);
		if (newValue.getGeometry() != null) {
			newValue.getGeometry().getGeometrySpec().addPropertyChangeListener(ivjEventHandler);
		}
		getMathDescEditor().setMathDescription(fieldMathModel.getMathDescription());
		updateWarningText();
	}
}

/**
 * Comment
 */
public void updateWarningText() {
	if (fieldMathModel == null) {
		return;
	}
	if (fieldMathModel.getMathDescription().isValid()){
		getMathWarningTextPane().setText("");
	}else{
		getMathWarningTextPane().setText(fieldMathModel.getMathDescription().getWarning());
	}
}

public boolean hasUnappliedChanges() {
	return getMathDescEditor().hasUnappliedChanges();
}

public JMenu getEditMenu() {
	return getMathDescEditor().getEditMenu();
}
}