package org.vcell.api.client.examples;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JTextArea;

import org.apache.http.client.ClientProtocolException;
import org.vcell.api.client.VCellApiClient;
import org.vcell.api.client.query.BioModelsQuerySpec;
import org.vcell.api.client.query.SimTasksQuerySpec;
import org.vcell.api.common.ApplicationRepresentation;
import org.vcell.api.common.BiomodelRepresentation;
import org.vcell.api.common.SimulationRepresentation;
import org.vcell.api.common.SimulationTaskRepresentation;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class VCSchedulerPanel extends JPanel {
	
	private final static String DEFAULT_CLIENTID = "85133f8d-26f7-4247-8356-d175399fc2e6";

	private JPanel panel;
	private JButton btnLogin;
	private JLabel lblUser;
	private JTextField textFieldUser;
	private JLabel lblPassword;
	private JPasswordField passwordField;
	private JButton btnLogout;
	private JTextArea textArea;
	private JButton btnRefresh;
	private JPanel panel_1;
	
	private VCellApiClient vcellApiClient = null;
	
	private ActionListener actionListener = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnLogin){
				try {
					login();
				} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException e1) {
					e1.printStackTrace();
				}
			}
			if (e.getSource() == btnRefresh){
				try {
					refresh();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (e.getSource() == btnRefreshCount){
				try {
					refreshCount();
				}catch (Exception e1){
					e1.printStackTrace();
				}
			}
			if (e.getSource() == btnLogout){
				try {
					logout();
				}catch (Exception e1){
					e1.printStackTrace();
				}
			}
		}
		
	};
	private JScrollPane scrollPane;
	private JPanel panel_2;
	private JLabel lblRunning;
	private JLabel lblRunningValue;
	private JLabel lblWaiting;
	private JLabel lblWaitingValue;
	private JLabel lblRecentlyFinished;
	private JLabel lblRecentlyfinishedvalue;
	private JButton btnRefreshCount;
	private JLabel lblQueued;
	private JLabel lblQueuedValue;
	private JLabel lblDispatched;
	private JLabel lblDispatchedValue;

	public VCSchedulerPanel() {
		super();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {611};
		gridBagLayout.rowHeights = new int[] {39, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0};
		setLayout(gridBagLayout);
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.weightx = 1.0;
		gbc_panel.anchor = GridBagConstraints.NORTHWEST;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		
		lblUser = new JLabel("user");
		panel.add(lblUser);
		
		textFieldUser = new JTextField();
		panel.add(textFieldUser);
		textFieldUser.setColumns(10);
		
		lblPassword = new JLabel("password");
		panel.add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setColumns(15);
		panel.add(passwordField);
		
		btnLogin = new JButton("login");
		btnLogin.addActionListener(actionListener);
		panel.add(btnLogin);
		
		btnLogout = new JButton("logout");
		btnLogout.addActionListener(actionListener);
		panel.add(btnLogout);
		
		panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 1;
		add(panel_2, gbc_panel_2);
		
		lblWaiting = new JLabel("waiting");
		panel_2.add(lblWaiting);
		
		lblWaitingValue = new JLabel("________");
		panel_2.add(lblWaitingValue);
		
		lblQueued = new JLabel("queued");
		panel_2.add(lblQueued);
		
		lblQueuedValue = new JLabel("________");
		panel_2.add(lblQueuedValue);
		
		lblDispatched = new JLabel("dispatched");
		panel_2.add(lblDispatched);
		
		lblDispatchedValue = new JLabel("________");
		panel_2.add(lblDispatchedValue);
		
		lblRunning = new JLabel("running");
		panel_2.add(lblRunning);
		
		lblRunningValue = new JLabel();
		lblRunningValue.setText("________");
		panel_2.add(lblRunningValue);
		
		lblRecentlyFinished = new JLabel("recently finished");
		panel_2.add(lblRecentlyFinished);
		
		lblRecentlyfinishedvalue = new JLabel("________");
		panel_2.add(lblRecentlyfinishedvalue);
		
		btnRefreshCount = new JButton("refresh");
		btnRefreshCount.addActionListener(actionListener);
		panel_2.add(btnRefreshCount);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.weighty = 10.0;
		gbc_scrollPane.weightx = 1.0;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		add(scrollPane, gbc_scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.weightx = 1.0;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 3;
		add(panel_1, gbc_panel_1);
		
		btnRefresh = new JButton("refresh");
		btnRefresh.addActionListener(actionListener);
		panel_1.add(btnRefresh);
	}
	
	private void login() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ClientProtocolException, IOException {
		vcellApiClient.authenticate(textFieldUser.getText(),passwordField.getText());
		setText("logged in as user "+textFieldUser.getText());
	}
	
	private void logout() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		vcellApiClient.clearAuthentication();
		setText("not logged in");
	}
	
	private void refreshCount() throws IOException{
		if (vcellApiClient!=null){
			StringBuffer buffer = new StringBuffer();
			SimTasksQuerySpec simTasksQuerySpec = new SimTasksQuerySpec();
			simTasksQuerySpec.waiting = "on";
			simTasksQuerySpec.queued = "on";
			simTasksQuerySpec.dispatched = "on";
			simTasksQuerySpec.running = "on";
			simTasksQuerySpec.completed = "off";
			simTasksQuerySpec.failed = "off";
			simTasksQuerySpec.stopped = "off";
			simTasksQuerySpec.maxRows = "1000";
			SimulationTaskRepresentation[] simTaskReps = vcellApiClient.getSimTasks(simTasksQuerySpec);
			int numWaiting = 0;
			int numQueued = 0;
			int numDispatched = 0;
			int numRunning = 0;
			for (SimulationTaskRepresentation simTaskRep : simTaskReps){
				buffer.append(toString(simTaskRep));
				if (simTaskRep.getStatus().equals("waiting")){
					numWaiting++;
				}else if (simTaskRep.getStatus().equals("queued")){
					numQueued++;
				}else if (simTaskRep.getStatus().equals("dispatched")){
					numDispatched++;
				}else if (simTaskRep.getStatus().equals("running")){
					numRunning++;
				}
			}
			this.lblWaitingValue.setText(""+numWaiting);
			this.lblQueuedValue.setText(""+numQueued);
			this.lblDispatchedValue.setText(""+numDispatched);
			this.lblRunningValue.setText(""+numRunning);
			setText(buffer.toString());
		}else{
			setText("vcellApiClient is null");
		}
	}
	
	private String toString(SimulationTaskRepresentation simTaskRep){
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd   HH:mm:ss");
        long tms = System.currentTimeMillis();
		StringBuffer buffer = new StringBuffer();
		if (simTaskRep.getBioModelLink()!=null){
			buffer.append("BioModel(\""+simTaskRep.getBioModelLink().getBioModelName()+"\","+simTaskRep.getBioModelLink().getBioModelKey()+") : ");
		}else if (simTaskRep.getMathModelLink()!=null){
			buffer.append("BioModel(\""+simTaskRep.getMathModelLink().getMathModelName()+"\","+simTaskRep.getMathModelLink().getMathModelKey()+") : ");
		}
		long elapsedTime = tms-simTaskRep.getStartdate();
		long timeLeft = -1;
//		if (simTaskRep.get)
		buffer.append("Simulation(\""+simTaskRep.getSimName()+"\","+simTaskRep.getSimKey()+") : status(start="+df.format(new Date(simTaskRep.getStartdate()))+",runningTime="+formatInterval(elapsedTime)+",status="+simTaskRep.getStatus()+",msg="+simTaskRep.getMessage()+")" + "\n");
		return buffer.toString();
	}		

	private static String formatInterval(final long interval_ms){
		final long hr = TimeUnit.MILLISECONDS.toHours(interval_ms);
		long hours_ms = TimeUnit.HOURS.toMillis(hr);
		final long min = TimeUnit.MILLISECONDS.toMinutes(interval_ms - hours_ms);
		long minutes_ms = TimeUnit.MINUTES.toMillis(min);
		final long sec = TimeUnit.MILLISECONDS.toSeconds(interval_ms - hours_ms - minutes_ms);
		long seconds_ms = TimeUnit.SECONDS.toMillis(sec);
		final long ms = TimeUnit.MILLISECONDS.toMillis(interval_ms   - hours_ms - minutes_ms - seconds_ms);
		return String.format("%02d:%02d:%02d.%03d",  hr, min, sec, ms);
	}
	
	private void refresh() throws IOException{
		if (vcellApiClient!=null){
			StringBuffer buffer = new StringBuffer();
			BiomodelRepresentation[] biomodelReps = vcellApiClient.getBioModels(new BioModelsQuerySpec());
			for (BiomodelRepresentation biomodelRep : biomodelReps){
				buffer.append("biomodel : "+biomodelRep.getBmKey()+" : "+biomodelRep.getName()+"\n");
				for (ApplicationRepresentation appRep : biomodelRep.getApplications()){
					buffer.append("   app : "+appRep.getName()+"\n");
				}
				for (SimulationRepresentation simRep : biomodelRep.getSimulations()){
					buffer.append("   sim : "+simRep.getName()+"\n");
				}
			}
			setText(buffer.toString());
		}else{
			setText("vcellApiClient is null");
		}
	}
	
	private void setText(String string){
		textArea.setText(string);
	}


	private void setPassword(String password) {
		this.passwordField.setText(password);
	}

	private void setUsername(String username) {
		this.textFieldUser.setText(username);
	}
		
	public static void main(String[] args) {
		try {
			if (args.length != 3 && args.length != 4){
				System.out.println("usage: VCellApiClient host userid password [clientID]");
				System.exit(1);
			}
			String host = args[0];
			int port = 8080;
			String username = args[1];
			String password = args[2];
			String clientID = DEFAULT_CLIENTID;
			if (args.length>3){
				clientID = args[3];
			}
			
			JFrame jframe = new JFrame();
			VCSchedulerPanel panel = new VCSchedulerPanel();
			panel.setUsername(username);
			panel.setPassword(password);
			boolean bIgnoreCertProblems = true;
			boolean bIgnoreHostMismatch = true;
			panel.vcellApiClient = new VCellApiClient(host,port,clientID,bIgnoreCertProblems, bIgnoreHostMismatch);

			jframe.getContentPane().add(panel);
			jframe.setSize(800,500);
			jframe.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					super.windowClosing(e);
					System.exit(0);
				}
			});	
			jframe.setVisible(true);
			// test /biomodel[? query string]

//			boolean bFirstSimulationToStartStop = true;
//			if (biomodelReps.length>0){
//				// test /biomodel/[bmkey]
//				System.out.println(" ... re-fetching first biomodel owned by me ...");
//				BioModelsQuerySpec bioModelsQuerySpec = new BioModelsQuerySpec();
//				bioModelsQuerySpec.owner = username;
//				BiomodelRepresentation firstBiomodelRep = vcellApiClient.getBioModels(bioModelsQuerySpec)[0];
//				System.out.println("biomodel : "+firstBiomodelRep.getBmKey()+" : "+firstBiomodelRep.getName());
//				for (ApplicationRepresentation appRep : firstBiomodelRep.getApplications()){
//					System.out.println("   appRep : "+appRep.getName());
//				}
//				for (SimulationRepresentation simRep : firstBiomodelRep.getSimulations()){
//					System.out.println("   simRep (returned with BioModelRep) : "+simRep.getKey()+" : "+simRep.getName());
//
//					// test /biomodel/[bmkey]/simulation/simkey
//					SimulationRepresentation simulation = vcellApiClient.getSimulation(firstBiomodelRep.getBmKey(), simRep.getKey());
//					System.out.println("   simRep (retrieved separately) : "+simulation.getKey()+" : "+simulation.getName());
//					
//					if (bFirstSimulationToStartStop){
//						bFirstSimulationToStartStop = false;
//						// test /biomodel/[bmkey]/simulation/[simkey]/startSimulation
//						SimTasksQuerySpec simTasksQuerySpec = new SimTasksQuerySpec();
//						simTasksQuerySpec.simId = simRep.getKey();
//						SimulationTaskRepresentation[] beforeStartSimTasks = vcellApiClient.getSimTasks(simTasksQuerySpec);
//						System.out.println("SENDING START SIMULATION");
//						SimulationTaskRepresentation[] justAfterStartSimTasks = vcellApiClient.startSimulation(firstBiomodelRep.getBmKey(), simRep.getKey());
//						System.out.println("SENT START SIMULATION");
//						
//						System.out.println("WAITING 5 seconds");
//						try {
//							Thread.sleep(5000);
//						}catch (Exception e){}
//						SimulationTaskRepresentation[] longAfterStartSimTasks = vcellApiClient.getSimTasks(simTasksQuerySpec);
//						
//						
//						System.out.println("SENDING STOP SIMULATION");
//						SimulationTaskRepresentation[] justAfterStopSimTasks = vcellApiClient.stopSimulation(firstBiomodelRep.getBmKey(), simRep.getKey());
//						System.out.println("SENT STOP SIMULATION");
//						
//						System.out.println("WAITING 5 seconds");
//						try {
//							Thread.sleep(5000);
//						}catch (Exception e){}
//						SimulationTaskRepresentation[] longAfterStopSimTasks = vcellApiClient.getSimTasks(simTasksQuerySpec);
//						
//						System.out.println("\n\nsimulation status:");
//						for (SimulationTaskRepresentation simTaskRep : beforeStartSimTasks){
//							System.out.println("    BEFORE START Job = "+simTaskRep.getJobIndex()+", Task = "+simTaskRep.getTaskId()+", Status = "+simTaskRep.getStatus());
//						}
//						for (SimulationTaskRepresentation simTaskRep : justAfterStartSimTasks){
//							System.out.println("    JUST AFTER START Job = "+simTaskRep.getJobIndex()+", Task = "+simTaskRep.getTaskId()+", Status = "+simTaskRep.getStatus());
//						}
//						for (SimulationTaskRepresentation simTaskRep : longAfterStartSimTasks){
//							System.out.println("    LONG AFTER START Job = "+simTaskRep.getJobIndex()+", Task = "+simTaskRep.getTaskId()+", Status = "+simTaskRep.getStatus());
//						}
//						for (SimulationTaskRepresentation simTaskRep : justAfterStopSimTasks){
//							System.out.println("    JUST AFTER STOP Job = "+simTaskRep.getJobIndex()+", Task = "+simTaskRep.getTaskId()+", Status = "+simTaskRep.getStatus());
//						}
//						for (SimulationTaskRepresentation simTaskRep : longAfterStopSimTasks){
//							System.out.println("    LONG AFTER STOP Job = "+simTaskRep.getJobIndex()+", Task = "+simTaskRep.getTaskId()+", Status = "+simTaskRep.getStatus());
//						}
//						System.out.println("\n\n");
//					}					
//					System.out.println("\n");
//				}
//			}
//			
//			// test /simtask
//			SimulationTaskRepresentation[] simTaskReps = vcellApiClient.getSimTasks(new SimTasksQuerySpec());
//			for (SimulationTaskRepresentation simTaskRep : simTaskReps){
//				System.out.println("simTask : "+simTaskRep.getSimKey()+" : "+simTaskRep.getSimName());
//			}
			
		} catch (Throwable e){
			e.printStackTrace(System.out);
		}
	}

}
