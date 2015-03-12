package org.vcell.model.bngl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;

import org.vcell.model.rbm.RbmUtils;

import cbit.gui.MultiPurposeTextPanel;
import cbit.vcell.client.ClientRequestManager.BngUnitSystem;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.math.VCML;
import cbit.vcell.math.gui.MathDescEditor;

public class BNGLUnitsPanel extends JPanel {
	
	private class EventListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			JRadioButton button = (JRadioButton) e.getSource();
			if (button.getText().equals("Concentration")){
				System.out.println("Concentration");
				lowerConPanel.setVisible(true);
				lowerMolPanel.setVisible(false);
			}else{
				System.out.println("Molecules");
				lowerConPanel.setVisible(false);
				lowerMolPanel.setVisible(true);
			}
		}
	}  
	
	BngUnitSystem us;
	JPanel lowerConPanel;
	JPanel lowerMolPanel;
	
	public BNGLUnitsPanel(BngUnitSystem us) {
		super();
		this.us = new BngUnitSystem(us);
		this.us.setOrigin(BngUnitSystem.origin.USER);
		initialize();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {		
				doSomething();
			}
		});
	}
	
	private void initialize(){

		JPanel upperPanel = new JPanel();
		JRadioButton con = new JRadioButton("Concentration");
		JRadioButton mol = new JRadioButton("Molecules");
		ButtonGroup bG = new ButtonGroup();
		bG.add(con);
		bG.add(mol);
		upperPanel.setSize(100,150);
		upperPanel.setLayout( new FlowLayout());
		upperPanel.add(con);
		upperPanel.add(mol);
		con.setSelected(true);

		EventListener listener = new EventListener();
		con.addActionListener(listener);
		mol.addActionListener(listener);
		
		lowerConPanel = new JPanel();
		lowerConPanel.setLayout(new FlowLayout());
		lowerConPanel.add(new JLabel("Scale"));
		JTextField tc = new JTextField("uM");
		lowerConPanel.add(tc);
		lowerConPanel.setPreferredSize(new Dimension(350,100));
		lowerConPanel.setVisible(true);

		lowerMolPanel = new JPanel();
		lowerMolPanel.setLayout(new FlowLayout());
		lowerMolPanel.add(new JLabel("Volume"));
		JTextField tm = new JTextField("1");
		lowerMolPanel.add(tm);
		lowerMolPanel.add(new JLabel("molecules"));
		lowerMolPanel.setPreferredSize(new Dimension(350,100));
		lowerMolPanel.setVisible(false);

		setLayout( new FlowLayout());
		add(upperPanel);
		add(lowerConPanel);
		add(lowerMolPanel);
		setPreferredSize(new Dimension(350,150));
	}


	public void doSomething(){

		updateSomething();
	}
	private void updateSomething(){

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

			}
		});
	}

	public BngUnitSystem getUnits() {
		return us;
	}
	
}

