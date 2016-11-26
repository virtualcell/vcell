package cbit.vcell.client;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.vcell.util.PropertyLoader;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.VCFileChooser;

import cbit.vcell.resource.VCellConfiguration;

public class VCellConfigurationPanel extends JPanel {
	
	private JTextField comsolRootTextField = null;
	private JTextField comsolJarTextField = null;
	
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
		c.gridheight=1;
		
		JLabel comsolRootLabel = new JLabel("Comsol Multiphysics directory (vcell.comsol.rootdir)");
		c.gridx=0;
		c.gridy=0;
		c.gridwidth=2;
		jpanel.add(comsolRootLabel,c);

		comsolRootTextField = new JTextField();
		c.gridx=0;
		c.gridy=1;
		c.gridwidth=1;
		jpanel.add(comsolRootTextField,c);
		comsolRootTextField.addActionListener((ActionEvent e) -> { VCellConfiguration.setFileProperty(PropertyLoader.comsolRootDir, new File(comsolRootTextField.getText())); });
		
		JButton findComsolButton = new JButton("Browse...");
		findComsolButton.addActionListener((ActionEvent e) -> browseComsolDirectory() );
		c.gridx=1;
		c.gridy=1;
		c.gridwidth=1;
		jpanel.add(findComsolButton,c);
		
		
		c.gridx=0;
		c.gridy=2;
		c.gridwidth=2;
		jpanel.add(new JSeparator(),c);
		
		setLayout(new GridBagLayout());
		JLabel comsolJarLabel = new JLabel("Comsol plugins directory (vcell.comsol.jardir)");
		c.gridx=0;
		c.gridy=3;
		c.gridwidth=2;
		jpanel.add(comsolJarLabel,c);

		comsolJarTextField = new JTextField();
		c.gridx=0;
		c.gridy=4;
		c.gridwidth=2;
		jpanel.add(comsolJarTextField,c);
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
