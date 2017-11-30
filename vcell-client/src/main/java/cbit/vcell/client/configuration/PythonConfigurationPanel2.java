package cbit.vcell.client.configuration;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import cbit.vcell.resource.PythonSupport;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.resource.PythonSupport.PythonPackage;

// http://commons.apache.org/proper/commons-io/javadocs/api-2.4/org/apache/commons/io

public class PythonConfigurationPanel2 extends JPanel {

	private static String textContinuum = "<html><a href=\"\">Continuum web site</a></html>";
	private static String urlContinuum = "https://www.continuum.io";

	private JLabel websiteLabel = new JLabel();
	private JButton testConfigurationButton;
	private JButton installPythonButton;
	private JLabel testConfigurationResults = new JLabel();
	private EventHandler eventHandler = new EventHandler();
	

	
	private class EventHandler implements MouseListener, FocusListener, ActionListener, PropertyChangeListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == getTestConfigurationButton()) {
				verifyInstallation();
			} else if(e.getSource() == getInstallPythonButton()) {
				installPython();
			}


		}
		@Override
		public void focusGained(FocusEvent e) {
		}
		@Override
		public void focusLost(FocusEvent e) {
		}
		
		@Override
		public void propertyChange(java.beans.PropertyChangeEvent event) {
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			try {
                Desktop.getDesktop().browse(new URI(urlContinuum));
			} catch (URISyntaxException | IOException ex) {
				System.out.println("URL problem");
			}
        }
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
		@Override
		public void mousePressed(MouseEvent e) {
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}
	
	public PythonConfigurationPanel2() {
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
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new CompoundBorder(margin, panelBorder));
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weighty=0;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridheight=1;
		add(mainPanel, BorderLayout.CENTER);
		
		JPanel upper = new JPanel();
//		TitledBorder titleLeft = BorderFactory.createTitledBorder(loweredEtchedBorder, " Original Physiology ");
//		titleLeft.setTitleJustification(TitledBorder.LEFT);
//		titleLeft.setTitlePosition(TitledBorder.TOP);
//		upper.setBorder(titleLeft);
		upper.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);	//  top, left, bottom, right 
		mainPanel.add(upper, gbc);
		
		// ============================================= Populating the upper group box =================
		String managedMiniconda = new File(ResourceUtil.getVcellHome(),"Miniconda").getAbsolutePath();
		String vcellPythonText = "<html>"
				+ "Python is required for parameter estimation and other analysis.<br>"
				+ "VCell manages a dedicated Miniconda python installation "
				+ "at <font color=#8C001A>" + managedMiniconda + "</font>.<br>"
				+ "</html>";
		String vcellPythonText2 = "<html>"
				+ "For more information about Miniconda, see:"
				+ "</html>";
		websiteLabel.setText(textContinuum);
		websiteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

		int gridy = 0;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(4, 4, 2, 10);
		upper.add(new JLabel(vcellPythonText), gbc);
		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(10, 4, 0, 10);
		upper.add(new JLabel(vcellPythonText2), gbc);

		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(0, 4, 2, 10);
		upper.add(websiteLabel, gbc);

		// --------------------------------------------------
		gridy++;
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);
		upper.add(new JLabel(""), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		//gbc.weightx = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(4, 4, 4, 10);		//  top, left, bottom, right 
		upper.add(getTestConfigurationButton(), gbc);
		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		//gbc.weightx = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(4, 4, 4, 10);
		upper.add(getInstallPythonButton(), gbc);
		
//		Dimension size = getTestConfigurationButton().getPreferredSize();
//		getTestConfigurationButton().setPreferredSize(size);
//		getTestConfigurationButton().setMaximumSize(size);
//		getInstallPythonButton().setSize(size);
//		getInstallPythonButton().setPreferredSize(size);
//		getInstallPythonButton().setMaximumSize(size);

		// --------------------------------------------------------
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weighty = 1;			// fake cell used for filling all the vertical empty space
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);
		upper.add(new JLabel(""), gbc);
		
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(4, 4, 2, 10);
		upper.add(testConfigurationResults, gbc);


		
		getTestConfigurationButton().addActionListener(eventHandler);
		getInstallPythonButton().addActionListener(eventHandler);
		addFocusListener(eventHandler);
		websiteLabel.addMouseListener(eventHandler);
	}
	
	private JButton getTestConfigurationButton() {
		if (testConfigurationButton == null) {
			testConfigurationButton = new javax.swing.JButton("Test Configuration");
			testConfigurationButton.setName("TestConfigurationButton");
		}
		return testConfigurationButton;
	}
	private JButton getInstallPythonButton() {
		if (installPythonButton == null) {
			installPythonButton = new javax.swing.JButton("Install Python");
			installPythonButton.setName("InstallPythonButton");
		}
		return installPythonButton;
	}

	private void verifyInstallation() {
		getTestConfigurationButton().setEnabled(false);
		getInstallPythonButton().setEnabled(false);
		try {
			PythonSupport.verifyInstallation(PythonPackage.values());
		} catch (Exception e) {
			String ret = e.getMessage();
			testConfigurationResults.setText("<html><font color=#8C001A>" + ret + "</font></html>");
			delayedDisplay(10000);
			return;
		}
		String ret = "Python configuration is up to date, all needed packages are present";
		testConfigurationResults.setText("<html>" + ret + "</html>");
		delayedDisplay(5000);
	}
	
	private void installPython() {
		testConfigurationResults.setText("<html>" + "Installing Python... This may take a while." + "</html>");
		getTestConfigurationButton().setEnabled(false);
		getInstallPythonButton().setEnabled(false);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					//Thread.sleep(5000);	// use this for faster testing of the UI
					StringBuffer packages = new StringBuffer();
					for (PythonSupport.PythonPackage pkg : PythonSupport.PythonPackage.values()){
						packages.append(pkg.condaName+"  conda install -p "+pkg.condaRepo+" "+pkg.condaName+"\n");
					}
					throw new RuntimeException("mananged Python not available, please install Miniconda (or Anaconda) and the following packages: "+packages.toString());
					//CondaSupport.installAsNeeded(true, true, true);
				} catch (Exception e) {
					String ret = e.getMessage();
					if(ret.length() > 250) {
						ret = ret.substring(0, 249) + "...";
					}
					testConfigurationResults.setText("<html><font color=#8C001A>" + ret + "</font></html>");
					delayedDisplay(10000);
					return;
				}
//				String ret = "Python installation was successful.";
//				testConfigurationResults.setText("<html>" + ret + "</html>");
//				delayedDisplay(5000);
			}
		};
		Thread pythonInstallThread = new Thread(runnable,"Python Install Thread");
		pythonInstallThread.setDaemon(true);
		pythonInstallThread.start();
	}

	private void delayedDisplay(int miliseconds) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
		@Override
		public void run() {
			testConfigurationResults.setText("");
			getInstallPythonButton().setEnabled(true);
			getTestConfigurationButton().setEnabled(true);
			}
		}, miliseconds);
	}

}
