package cbit.vcell.client.configuration;

import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.resource.VCellConfiguration;
import org.vcell.util.gui.VCFileChooser;
import org.vcell.util.gui.exporter.FileFilters;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;

// http://commons.apache.org/proper/commons-io/javadocs/api-2.4/org/apache/commons/io

public class PythonConfigurationPanel extends JPanel {

	private JTextField pythonExeTextField = null;

	public PythonConfigurationPanel() {
		super();
		initialize();
	}
	
	private void initialize() {

		setLayout(new BorderLayout());
		
		Border margin = new EmptyBorder(5,3,1,1);
		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder panelBorder = BorderFactory.createTitledBorder(loweredEtchedBorder, " Python Properties ");
		panelBorder.setTitleJustification(TitledBorder.LEFT);
		panelBorder.setTitlePosition(TitledBorder.TOP);
		panelBorder.setTitleFont(getFont().deriveFont(Font.BOLD));
		
		JPanel jpanel = new JPanel();
		jpanel.setBorder(new CompoundBorder(margin, panelBorder));
		add(jpanel,BorderLayout.CENTER);
		
		jpanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weighty=0;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridheight=1;
		
		//CondaSupport.installInBackground();  // or not in background ... ?????
		
		//=============================================================================
		//
		// python (anaconda) executable
		//
		//=============================================================================
		int gridy = 0;
		String managedMiniconda = new File(ResourceUtil.getVcellHome(),"Miniconda").getAbsolutePath();
		String vcellPythonText = "Python is required for parameter estimation and other analysis\n\n"
				+ "VCell manages a dedicated Miniconda python installation \n"
				+ "at "+managedMiniconda+".  \n"
				+ "For more information about Miniconda, \n"
				+ "see https://www.continuum.io\n\n";
		JTextArea vcellPythonTextArea = new JTextArea(vcellPythonText);
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		gbc.insets = new Insets(4,4,2,4);			// top, left bottom, right
		jpanel.add(vcellPythonTextArea, gbc);
		gridy++;
		
		String providedPythonText = "Alternate Anaconda/Miniconda installation directory.";
		JLabel providedPythonLabel = new JLabel(providedPythonText);
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(4,4,1,4);			// top, left bottom, right
		jpanel.add(providedPythonLabel, gbc);
		gridy++;
		
		pythonExeTextField = new JTextField();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 0.0;
		gbc.ipady = 1;
		gbc.insets = new Insets(0,4,2,2);
		jpanel.add(pythonExeTextField, gbc);
		pythonExeTextField.addActionListener((ActionEvent e) -> { VCellConfiguration.setFileProperty(PropertyLoader.pythonExe, new File(pythonExeTextField.getText())); });
		
		JButton findPythonButton = new JButton("Browse...");
		findPythonButton.addActionListener((ActionEvent e) -> browsePythonExe() );
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.ipady = 0;
		gbc.insets = new Insets(3,2,2,4);
		jpanel.add(findPythonButton, gbc);
		
//		gridy += 4;
//		gbc.gridx=0;
//		gbc.gridy=5;
//		gbc.gridwidth=2;
//		jpanel.add(new JSeparator(), gbc);
				
		pythonExeTextField.addActionListener((ActionEvent e) -> { VCellConfiguration.setFileProperty(PropertyLoader.pythonExe, new File(pythonExeTextField.getText())); });
		initPythonValues();
	}
	
	private void initPythonValues(){
		File pythonExe = VCellConfiguration.getFileProperty(PropertyLoader.pythonExe);
		if (pythonExe!=null){
			pythonExeTextField.setText(pythonExe.getAbsolutePath());
		}		
		VCellConfiguration.showProperties();
	}
	
	private void browsePythonExe() {
		File pythonExe = chooseExecutableFile("find Anaconda or Miniconda Python executable", 
				(File f) -> { return f.getAbsolutePath().toUpperCase().contains("CONDA") && f.getName().toUpperCase().contains("PYTHON"); });
		if (pythonExe == null){
			return;
		}
		
		if (pythonExe.exists()){
			VCellConfiguration.setFileProperty(PropertyLoader.pythonExe, pythonExe);
			initPythonValues();
		}
	}
	
	private static String[] likelyes = {
//			"c:\\Program Files\\LLNL"
			};

	private File chooseExecutableFile(String title, FileFilter fileFilter){
		
		VCFileChooser fileChooser = new VCFileChooser();
		for(String likely : likelyes) {
			File f = new File(likely);
			if (f.exists() && f.isDirectory()) {
				fileChooser = new VCFileChooser(likely);
				break;
			}
		}
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if (OperatingSystemInfo.getInstance().isWindows()){
			fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_EXE);
			// Set the default file filter...
			fileChooser.setFileFilter(FileFilters.FILE_FILTER_EXE);
			// remove all selector
			fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());		    
		}
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
