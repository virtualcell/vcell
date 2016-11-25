package cbit.vcell.client;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.vcell.util.PropertyLoader;

import cbit.vcell.resource.VCellConfiguration;

public class VCellConfigurationPanel extends JPanel {
	
	public VCellConfigurationPanel(){
		super();
		setLayout(new BorderLayout());
		
		JPanel jpanel = new JPanel();
		add(jpanel,BorderLayout.CENTER);
		
		jpanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.ipady=10;
		c.weightx=0.5;
		c.weighty=0.5;
		c.fill=GridBagConstraints.BOTH;
		c.gridwidth=1;
		c.gridheight=1;
		
		JLabel comsolRootLabel = new JLabel("Comsol Multiphysics directory (vcell.comsol.rootdir)");
		c.gridx=0;
		c.gridy=0;
		jpanel.add(comsolRootLabel,c);

		JTextField comsolRootTextField = new JTextField();
		c.gridx=0;
		c.gridy=1;
		jpanel.add(comsolRootTextField,c);
		comsolRootTextField.addActionListener((ActionEvent e) -> { VCellConfiguration.setFileProperty(PropertyLoader.comsolRootDir, new File(comsolRootTextField.getText())); });
		
		c.gridx=0;
		c.gridy=2;
		jpanel.add(new JSeparator(),c);
		
		setLayout(new GridBagLayout());
		JLabel comsolJarLabel = new JLabel("Comsol plugins directory (vcell.comsol.jardir)");
		c.gridx=0;
		c.gridy=3;
		jpanel.add(comsolJarLabel,c);

		JTextField comsolJarTextField = new JTextField();
		c.gridx=0;
		c.gridy=4;
		jpanel.add(comsolJarTextField,c);
		comsolRootTextField.addActionListener((ActionEvent e) -> { VCellConfiguration.setFileProperty(PropertyLoader.comsolJarDir, new File(comsolJarTextField.getText())); });
		
		
		File rootDir = VCellConfiguration.getFileProperty(PropertyLoader.comsolRootDir);
		if (rootDir!=null){
			comsolRootTextField.setText(rootDir.getAbsolutePath());
		}
		File jarDir = VCellConfiguration.getFileProperty(PropertyLoader.comsolJarDir);
		if (jarDir!=null){
			comsolJarTextField.setText(jarDir.getAbsolutePath());
		}
		
	}

}
