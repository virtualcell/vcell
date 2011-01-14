package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Parameter;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2003 2:07:01 PM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class ParameterPropertiesPanel extends DocumentEditorSubPanel {
	private Parameter parameter = null;
	private EventHandler eventHandler = new EventHandler();
	private JTextArea annotationTextArea;
	private JTextField nameTextField = null;
	private JTextField descriptionTextField = null;

	private class EventHandler implements FocusListener, PropertyChangeListener {
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == annotationTextArea) {
				changeAnnotation();
			} else if (e.getSource() == nameTextField) {
				changeName();
			}
		}
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == parameter) {
				updateInterface();
			}
		}
	};

/**
 * EditSpeciesDialog constructor comment.
 */
public ParameterPropertiesPanel() {
	super();
	initialize();
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
private void initialize() {
	try {
		nameTextField = new JTextField();
		nameTextField.setEditable(false);
		descriptionTextField = new JTextField();
		descriptionTextField.setEditable(false);
		annotationTextArea = new javax.swing.JTextArea("", 1, 30);
		annotationTextArea.setLineWrap(true);
		annotationTextArea.setWrapStyleWord(true);
		annotationTextArea.setEditable(false);
		annotationTextArea.setBackground(UIManager.getColor("TextField.inactiveBackground"));

		setBackground(Color.white);
		setLayout(new GridBagLayout());
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.insets = new java.awt.Insets(0, 4, 0, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		JLabel label = new JLabel("<html><u>Select only one parameter to view/edit properties</u></html>");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		add(label, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel("Parameter Name");
		add(label, gbc);
		
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(nameTextField, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel("Description");
		add(label, gbc);
		
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(descriptionTextField, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("Annotation"), gbc);

		javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 1;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		add(jsp, gbc);		
		
		annotationTextArea.addFocusListener(eventHandler);
		nameTextField.addFocusListener(eventHandler);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void changeAnnotation() {
	try{
		if (parameter == null) {
			return;
		}
		String text = annotationTextArea.getText();
		if (parameter instanceof ModelParameter) {
			((ModelParameter)parameter).setModelParameterAnnotation(text);
		}
	} catch(Exception e){
		e.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this,"Edit Species Error\n"+e.getMessage(), e);
	}
}

/**
 * Sets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @param speciesContext The new value for the property.
 * @see #getSpeciesContext
 */
void setParameter(Parameter newValue) {
	if (newValue == parameter) {
		return;
	}
	Parameter oldValue = parameter;
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(eventHandler);
	}
//	// commit the changes before switch to another parameter
//	changeName();
//	changeAnnotation();
	parameter = newValue;
	if (newValue != null) {
		newValue.addPropertyChangeListener(eventHandler);
	}
	updateInterface();
}

/**
 * Comment
 */
private void updateInterface() {
	boolean bNonNullParameter = parameter != null;
	nameTextField.setEditable(bNonNullParameter && (parameter instanceof ModelParameter || parameter.isNameEditable()));
	descriptionTextField.setEditable(bNonNullParameter && parameter.isDescriptionEditable());
	boolean bAnnotationEditable = bNonNullParameter && parameter instanceof ModelParameter;
	annotationTextArea.setEditable(bAnnotationEditable);
	annotationTextArea.setBackground(bAnnotationEditable ? UIManager.getColor("TextField.background") : UIManager.getColor("TextField.inactiveBackground"));		
	if (bNonNullParameter) {
		nameTextField.setText(parameter.getName());
		descriptionTextField.setText(parameter.getDescription());
		if (parameter instanceof ModelParameter) {
			annotationTextArea.setText(((ModelParameter)parameter).getModelParameterAnnotation());
		}
	} else {
		annotationTextArea.setText(null);
		nameTextField.setText(null);
		descriptionTextField.setText(null);
	}
}

private void changeName() {
	if (parameter == null) {
		return;
	}
	String newName = nameTextField.getText();
	if (newName == null || newName.length() == 0) {
		nameTextField.setText(parameter.getName());
		return;
	}
	if (newName.equals(parameter.getName())) {
		return;
	}
	try {
		parameter.setName(newName);
	} catch (PropertyVetoException e1) {
		e1.printStackTrace();
		DialogUtils.showErrorDialog(ParameterPropertiesPanel.this, e1.getMessage());
	}
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length != 1) {
		return;
	}
	if (selectedObjects[0] instanceof Parameter) {
		setParameter((Parameter) selectedObjects[0]);
	} else {
		setParameter(null);
	}	
}
}
