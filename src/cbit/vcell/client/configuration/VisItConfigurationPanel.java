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
import org.vcell.util.gui.exporter.FileFilters;

import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.VCellConfiguration;

public class VisItConfigurationPanel extends JPanel {

	private JTextField visitExeTextField = null;

	
	public VisItConfigurationPanel(){
		super();
		initialize();
	}
	
	private void initialize() {
		setLayout(new BorderLayout());
		
		JPanel jpanel = new JPanel();
		jpanel.setBorder(new EmptyBorder(15,15,15,15));
		add(jpanel,BorderLayout.CENTER);
		
		jpanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.ipady=10;
		c.weighty=0;
		c.fill=GridBagConstraints.HORIZONTAL;
		c.gridheight=1;
		
		//=============================================================================
		//
		// visit exe
		//
		//=============================================================================
		JLabel visitExeLabel = new JLabel("<html>VisIt executable, see <a href='https://wci.llnl.gov/simulation/computer-codes/visit'>VisIt</a> at LLNL (llnl.gov)</html>");
		c.gridx=0;
		c.gridy=0;
		c.gridwidth=2;
		c.weightx=0.5;
		jpanel.add(visitExeLabel,c);

		visitExeTextField = new JTextField();
		c.gridx=0;
		c.gridy=1;
		c.gridwidth=1;
		c.weightx=0.5;
		jpanel.add(visitExeTextField,c);
		visitExeTextField.addActionListener((ActionEvent e) -> { VCellConfiguration.setFileProperty(PropertyLoader.visitExe, new File(visitExeTextField.getText())); });
		
		JButton findVisitExeButton = new JButton("Browse...");
		findVisitExeButton.addActionListener((ActionEvent e) -> browseVisitExe() );
		c.gridx=1;
		c.gridy=1;
		c.gridwidth=1;
		c.weightx=0;
		jpanel.add(findVisitExeButton,c);
		
		c.gridx=0;
		c.gridy=2;
		c.gridwidth=2;
		jpanel.add(new JSeparator(),c);
		
		initVisitValues();
	}
	
	
	private void initVisitValues(){
		File visitExe = VCellConfiguration.getFileProperty(PropertyLoader.visitExe);
		if (visitExe!=null){
			visitExeTextField.setText(visitExe.getAbsolutePath());
		}
		
		VCellConfiguration.showProperties();
	}
	
	private void browseVisitExe() {
		final String suffix;
		if (OperatingSystemInfo.getInstance().isWindows()){
			suffix = ".exe";
		}else{
			suffix = "";
		}
		File selectedExe = chooseExecutableFile("find Visit executable", 
				(File f) -> { return f.getName().toLowerCase().equals("visit"+suffix); });
		if (selectedExe == null){
			return;
		}
		File visitExe = selectedExe;
		
		VCellConfiguration.setFileProperty(PropertyLoader.visitExe, visitExe);
		initVisitValues();
	}

	private File chooseExecutableFile(String title, FileFilter fileFilter){
		VCFileChooser fileChooser = new VCFileChooser();
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

//	private void initializeOld() {
//		
//		setLayout(new GridBagLayout());
//
//		JLabel jl = new JLabel("VisItConfigurationPanel");
//		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.anchor = GridBagConstraints.WEST;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(2, 4, 2, 4);
//		add(jl, gbc);
//	}

}
