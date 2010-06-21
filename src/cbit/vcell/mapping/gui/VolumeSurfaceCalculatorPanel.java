package cbit.vcell.mapping.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.vcell.util.gui.DialogUtils;

public class VolumeSurfaceCalculatorPanel extends JPanel {
	private JLabel volumeSurfaceCalculatorLabel = null; 
	private JPanel volumeSurfaceCalculatorPanel = null;
	
	private JComboBox shapeComboBox = null;
	private JLabel attrLabel = null;
	private JTextField attrTextField = null;
	private JButton calculateButton = null;
	private JTextField volumeTextField = null;
	private JTextField surfaceTextField = null;
	private JLabel volumeFormulaLabel = null;
	private JLabel surfaceFormulaLabel = null; 

	private static final String SHAPE_SPHERE = "Sphere";
	private static final String SHAPE_HEMISPHERE = "Hemisphere";
	private static final String SHAPE_CYLINDER = "Cylinder";
	private static final String SHAPE_CONE = "Cone";
	private static final String SHAPE_CUBE = "Cube";
	private static final String SHAPE_BOX = "Box";
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	class IvjEventHandler implements MouseListener, ActionListener, ItemListener {		
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == volumeSurfaceCalculatorLabel) {
				if (volumeSurfaceCalculatorPanel.isVisible()) {
					volumeSurfaceCalculatorLabel.setText("<html><font color=blue><u>Volume and Surface Calculator &gt;&gt;</u></font></html>");
					volumeSurfaceCalculatorPanel.setVisible(false);
				} else {
					volumeSurfaceCalculatorLabel.setText("<html><font color=blue><u>Volume and Surface Calculator &lt;&lt;</u></font></html>");
					volumeSurfaceCalculatorPanel.setVisible(true);
					shapeComboBox.setSelectedIndex(0);
				}
			}
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == calculateButton) {
				Object selectedItem = shapeComboBox.getSelectedItem();
				String rtext = attrTextField.getText();
				double v = 0, s = 0;
				try {
					if (selectedItem.equals(SHAPE_SPHERE)) {
						double r = Double.parseDouble(rtext);
						v = 4.0 * Math.PI * r * r * r / 3.0;
						s = 4.0 * Math.PI * r * r;
					} else if (selectedItem.equals(SHAPE_HEMISPHERE)) {
						double r = Double.parseDouble(rtext);
						v = 2.0 * Math.PI * r * r * r / 3.0;
						s = 3.0 * Math.PI * r * r;
					} else if (selectedItem.equals(SHAPE_CYLINDER)) {
						StringTokenizer st = new StringTokenizer(rtext," ,;");
						double r = Double.parseDouble(st.nextToken());
						double h = Double.parseDouble(st.nextToken());
						v = Math.PI * r * r * h;
						s = 2 * Math.PI * r * (r + h);
					} else if (selectedItem.equals(SHAPE_CONE)) {
						StringTokenizer st = new StringTokenizer(rtext," ,;");
						double r = Double.parseDouble(st.nextToken());
						double h = Double.parseDouble(st.nextToken());
						v = Math.PI * r * r * h / 3.0;
						s = Math.PI * r * (r + Math.sqrt(r * r + h * h));
					} else if (selectedItem.equals(SHAPE_CUBE)) {
						double a = Double.parseDouble(rtext);
						v = a * a * a;
						s = 6 * a * a;
					} else if (selectedItem.equals(SHAPE_BOX)) {
						StringTokenizer st = new StringTokenizer(rtext," ,;");
						double l = Double.parseDouble(st.nextToken());
						double w = Double.parseDouble(st.nextToken());
						double h = Double.parseDouble(st.nextToken());
						v = l * w * h;
						s = 2 * (l * w + l * h + w * h);
					}
				} catch (Exception ex) {
					DialogUtils.showErrorDialog(VolumeSurfaceCalculatorPanel.this, "Wrong input for selected shape " + selectedItem + "!", ex);
				}
				volumeTextField.setText(v + "");
				surfaceTextField.setText(s + "");
			}
			
		}
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() != ItemEvent.SELECTED || shapeComboBox.getSelectedIndex() < 0) {
				return;
			}
			if (e.getSource() == shapeComboBox) {
				volumeTextField.setText(null);
				surfaceTextField.setText(null);
				attrTextField.setText(null);
				Object selectedItem = shapeComboBox.getSelectedItem();
				String piStr = "<font face=Symbol>&pi;</font>";
				if (selectedItem.equals(SHAPE_SPHERE)) {
					attrLabel.setText("r ");
					volumeFormulaLabel.setText("<html><i>4 " + piStr + " r <sup>3</sup> / 3</i></html>");//4 " + multChar + " " + piChar + " " + multChar + " r " + multChar + " r " + multChar + " r / 3");
					surfaceFormulaLabel.setText("<html><i>4 " + piStr + " r <sup>2</sup></i></html");//4 " + multChar + " " + piChar + " " + multChar + " r " + multChar + " r");
				} else if (selectedItem.equals(SHAPE_HEMISPHERE)) {
					attrLabel.setText("r ");
					volumeFormulaLabel.setText("<html><i>2 " + piStr + " r <sup>3</sup> / 3</i></html>");//4 " + multChar + " " + piChar + " " + multChar + " r " + multChar + " r " + multChar + " r / 3");
					surfaceFormulaLabel.setText("<html><i>3 " + piStr + " r <sup>2</sup></i></html");//4 " + multChar + " " + piChar + " " + multChar + " r " + multChar + " r");
				} else if (selectedItem.equals(SHAPE_CYLINDER)) {
					attrLabel.setText("r, h ");
					volumeFormulaLabel.setText("<html><i>" + piStr + " r <sup>2</sup> h</i></html>");//piChar + " " + multChar + " r " + multChar + " r " + multChar + " h");
					surfaceFormulaLabel.setText("<html><i>2 " + piStr + " r <sup>2</sup> + 2 " + piStr + " r h</i></html>");//"2 " + multChar + " " + piChar + " " + multChar + " r " + multChar + " r + 2 " + multChar + " " + piChar + " " + multChar + " r " + multChar + " h");
				} else if (selectedItem.equals(SHAPE_CONE)) {
					attrLabel.setText("r, h ");
					volumeFormulaLabel.setText("<html><i>" + piStr +" r <sup>2</sup> h / 3<i></html>");//piChar + " " + multChar + " r " + multChar + " r " + multChar + " h / 3");
					surfaceFormulaLabel.setText("<html><table cellspacing=0 cellpadding=0 border=0>" +
							"<tr><td>&nbsp;</td><td>_________</td>" +
							"</tr><tr><td><i>" + piStr + " r <sup>2</sup> + " + piStr +" r</i>&nbsp;&radic;</td><td><i>&nbsp;r <sup>2</sup> + h <sup>2</sup></i></td></tr></table></html>");//piChar + " " + multChar + " r " + multChar + " r + " + piChar + " " + multChar + " r " + multChar + " sqrt ( r " + multChar + " r + h " + multChar + " h )");
				} else if (selectedItem.equals(SHAPE_CUBE)) {
					attrLabel.setText("a ");
					volumeFormulaLabel.setText("<html><i> a <sup>3</sup> <i></html>");//piChar + " " + multChar + " r " + multChar + " r " + multChar + " h / 3");
					surfaceFormulaLabel.setText("<html><i>6 a <sup>2</sup></i></html>");//piChar + " " + multChar + " r " + multChar + " r + " + piChar + " " + multChar + " r " + multChar + " sqrt ( r " + multChar + " r + h " + multChar + " h )");
				} else if (selectedItem.equals(SHAPE_BOX)) {
					attrLabel.setText("l, w, h ");
					volumeFormulaLabel.setText("<html><i>l w h</i></html>");
					surfaceFormulaLabel.setText("<html><i>2 * ( l w + l h + w h )</i></html>");
				}
			} 			
		};
	};
	
	public VolumeSurfaceCalculatorPanel() {
		super();
		initialize();
	}

	private void initialize() {		
		volumeSurfaceCalculatorPanel = new JPanel();
		volumeSurfaceCalculatorPanel.setBorder(new JScrollPane().getBorder());			
		volumeSurfaceCalculatorPanel.setLayout(new GridBagLayout());
		
		int gridy = 0;			
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		volumeSurfaceCalculatorPanel.add(new JLabel("Shape"), gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		shapeComboBox = new JComboBox();
		DefaultComboBoxModel dcb = new DefaultComboBoxModel();
		dcb.addElement(SHAPE_SPHERE);
		dcb.addElement(SHAPE_HEMISPHERE);
		dcb.addElement(SHAPE_CYLINDER);
		dcb.addElement(SHAPE_CONE);
		dcb.addElement(SHAPE_CUBE);
		dcb.addElement(SHAPE_BOX);
		shapeComboBox.setModel(dcb);
		volumeSurfaceCalculatorPanel.add(shapeComboBox, gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 2; 
		gbc.gridy = gridy;
		gbc.gridheight = 3;
		gbc.fill = GridBagConstraints.VERTICAL;
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0; 
		gbc1.gridy = 0;
		gbc1.weighty = 1.0;
		calculateButton = new JButton("Go >>");
		calculateButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		panel.add(calculateButton, gbc1);
		volumeSurfaceCalculatorPanel.add(panel, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 3; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		volumeSurfaceCalculatorPanel.add(new JLabel("Volume"), gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 4; 
		gbc.gridy = gridy;
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new java.awt.Insets(4, 4, 0, 4);
		volumeTextField = new JTextField(20);
		volumeTextField.setEditable(false);
		volumeTextField.setBackground(Color.WHITE);
		volumeSurfaceCalculatorPanel.add(volumeTextField, gbc);

		//
		gridy ++;
		volumeFormulaLabel = new JLabel();
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 4; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0; 
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new java.awt.Insets(0, 4, 4, 4);
		volumeSurfaceCalculatorPanel.add(volumeFormulaLabel, gbc);
		
		//
		gridy ++;
		attrLabel = new JLabel("r ");
		java.awt.Font font = attrLabel.getFont();
		attrLabel.setFont(font.deriveFont(Font.ITALIC + Font.BOLD));
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		volumeSurfaceCalculatorPanel.add(attrLabel, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		attrTextField = new JTextField(20);
		volumeSurfaceCalculatorPanel.add(attrTextField, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 3; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		volumeSurfaceCalculatorPanel.add(new JLabel("Surface"), gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 4; 
		gbc.gridy = gridy;
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new java.awt.Insets(4, 4, 0, 4);
		surfaceTextField = new JTextField(20);
		surfaceTextField.setEditable(false);
		surfaceTextField.setBackground(Color.WHITE);
		volumeSurfaceCalculatorPanel.add(surfaceTextField, gbc);
		
		//
		gridy ++;
		surfaceFormulaLabel = new JLabel();
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 4; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new java.awt.Insets(0, 4, 4, 4);
		volumeSurfaceCalculatorPanel.add(surfaceFormulaLabel, gbc);
		
		
		setLayout(new GridBagLayout());
		
		volumeSurfaceCalculatorLabel = new JLabel("<html><font color=blue><u>Volume and Surface Calculator &gt;&gt;</u></font></html>");
		volumeSurfaceCalculatorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(volumeSurfaceCalculatorLabel, gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(volumeSurfaceCalculatorPanel, gbc);
		
		volumeSurfaceCalculatorPanel.setVisible(false);
		shapeComboBox.setSelectedIndex(-1);
		initConnections();
	}
	
	private void initConnections() {
		volumeSurfaceCalculatorLabel.addMouseListener(ivjEventHandler);
		calculateButton.addActionListener(ivjEventHandler);
		shapeComboBox.addItemListener(ivjEventHandler);
	}	
}
