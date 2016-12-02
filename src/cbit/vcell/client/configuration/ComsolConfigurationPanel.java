package cbit.vcell.client.configuration;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.vcell.util.PropertyLoader;
import org.vcell.util.gui.VCFileChooser;

import cbit.vcell.resource.VCellConfiguration;

public class ComsolConfigurationPanel extends JPanel {

	private JTextField comsolRootTextField = null;
	private JTextField comsolJarTextField = null;

	public ComsolConfigurationPanel() {
		super();
		initialize();
	}
	
	private void initialize() {

		setLayout(new BorderLayout());
		
		JPanel jpanel = new JPanel();
		jpanel.setBorder(new EmptyBorder(15,15,15,15));
		add(jpanel,BorderLayout.CENTER);
		
		jpanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.ipady=10;
		gbc.weighty=0;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridheight=1;
		
		
		//=============================================================================
		//
		// comsol root dir
		//
		//=============================================================================
		int gridy = 0;
		JLabel comsolRootLabel = new JLabel("<html>Comsol Multiphysics directory (requires a local COMSOL Multiphysics installation, see <a href='http://comsol.com'>Comsol.com</a>)</html>");
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.weightx = 0.5;
		jpanel.add(comsolRootLabel, gbc);

		gridy++;
		comsolRootTextField = new JTextField();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		jpanel.add(comsolRootTextField, gbc);
		comsolRootTextField.addActionListener((ActionEvent e) -> { VCellConfiguration.setFileProperty(PropertyLoader.comsolRootDir, new File(comsolRootTextField.getText())); });
		
		JButton findComsolButton = new JButton("Browse...");
		findComsolButton.addActionListener((ActionEvent e) -> browseComsolDirectory() );
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		jpanel.add(findComsolButton, gbc);
		
//		gridy += 4;		// !!!
//		gbc.gridx=0;
//		gbc.gridy=5;
//		gbc.gridwidth=2;
//		jpanel.add(new JSeparator(), gbc);
		
		//=============================================================================
		//
		// comsol plugins dir
		//
		//=============================================================================
		gridy++;
		JLabel comsolJarLabel = new JLabel("Comsol plugins directory (vcell.comsol.jardir)");
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.weightx = 0.5;
		jpanel.add(comsolJarLabel, gbc);

		gridy++;
		comsolJarTextField = new JTextField();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.weightx = 0.5;
		jpanel.add(comsolJarTextField, gbc);
		
		gridy++;
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.weightx = 1;
		gbc.weighty = 1;		// fake cell used for filling all the vertical empty space
		gbc.anchor = GridBagConstraints.WEST;
		jpanel.add(new JLabel(""), gbc);

		
		comsolRootTextField.addActionListener((ActionEvent e) -> { VCellConfiguration.setFileProperty(PropertyLoader.comsolJarDir, new File(comsolJarTextField.getText())); });
		initComsolValues();
	}
	
	private void initComsolValues(){
		File rootDir = VCellConfiguration.getFileProperty(PropertyLoader.comsolRootDir);
		if (rootDir!=null){
			comsolRootTextField.setText(rootDir.getAbsolutePath());
		}
		File jarDir = VCellConfiguration.getFileProperty(PropertyLoader.comsolJarDir);
		if (jarDir!=null){
			comsolJarTextField.setText(jarDir.getAbsolutePath());
		}
		
		VCellConfiguration.showProperties();
	}
	private void browseComsolDirectory() {
		File selectedDir = chooseDirectory("find COMSOL Multiphysics directory", 
				(File f) -> { return f.getAbsolutePath().toUpperCase().contains("COMSOL") && f.getName().toUpperCase().equals("MULTIPHYSICS"); });
		if (selectedDir == null){
			return;
		}
		File rootDir = selectedDir;
		File pluginDir = new File(selectedDir,"plugins");
		
		if (pluginDir.exists()){
			VCellConfiguration.setFileProperty(PropertyLoader.comsolRootDir, rootDir);
			VCellConfiguration.setFileProperty(PropertyLoader.comsolJarDir, pluginDir);
			initComsolValues();
		}
	}
	private File chooseDirectory(String title, FileFilter fileFilter){
		VCFileChooser fileChooser = new VCFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
//		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_MOV);
		// Set the default file filter...
//		fileChooser.setFileFilter(FileFilters.FILE_FILTER_MOV);
		// remove all selector
//		fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());		    
		
		fileChooser.setDialogTitle(title);
		
		File selectedFile = null;
		while(true){
			if(fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION){
				selectedFile = null;
				break;
			}
			selectedFile = fileChooser.getSelectedFile();
			if (fileFilter.accept(selectedFile) && selectedFile.exists()){
				break;
			}
		}
		return selectedFile;
	}

}
