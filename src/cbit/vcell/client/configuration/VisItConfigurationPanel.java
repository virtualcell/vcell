package cbit.vcell.client.configuration;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

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
		
		Border margin = new EmptyBorder(5,3,1,1);
		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder panelBorder = BorderFactory.createTitledBorder(loweredEtchedBorder, " VisIt Properties ");
		panelBorder.setTitleJustification(TitledBorder.LEFT);
		panelBorder.setTitlePosition(TitledBorder.TOP);
		panelBorder.setTitleFont(getFont().deriveFont(Font.BOLD));
		
		JPanel jpanel = new JPanel();
		jpanel.setBorder(new CompoundBorder(margin, panelBorder));
		add(jpanel,BorderLayout.CENTER);
		
		jpanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridheight = 1;
		
		//=============================================================================
		//
		// visit exe
		//
		//=============================================================================
		int gridy = 0;
		JLabel visitExeLabel = new JLabel("<html>VisIt executable, see <a href='https://wci.llnl.gov/simulation/computer-codes/visit'>VisIt</a> at LLNL (llnl.gov)</html>");
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(4,4,2,4);			// top, left bottom, right
		jpanel.add(visitExeLabel,gbc);

		gridy++;
		visitExeTextField = new JTextField();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		gbc.ipady = 1;
		gbc.insets = new Insets(4,4,2,2);
		jpanel.add(visitExeTextField,gbc);
		visitExeTextField.addActionListener((ActionEvent e) -> { VCellConfiguration.setFileProperty(PropertyLoader.visitExe, new File(visitExeTextField.getText())); });
		
		JButton findVisitExeButton = new JButton("Browse...");
		findVisitExeButton.addActionListener((ActionEvent e) -> browseVisitExe() );
		gbc.gridx=1;
		gbc.gridy = gridy;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.ipady = 0;
		gbc.insets = new Insets(3,2,2,4);
		jpanel.add(findVisitExeButton,gbc);
		
//		gbc.gridx=0;
//		gbc.gridy=2;
//		gbc.gridwidth=2;
//		jpanel.add(new JSeparator(),gbc);
		
		gridy++;
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.weightx = 1;
		gbc.weighty = 1;		// fake cell used for filling all the vertical empty space
		gbc.anchor = GridBagConstraints.WEST;
		jpanel.add(new JLabel(""), gbc);

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

}
