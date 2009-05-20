package cbit.vcell.microscopy.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import cbit.vcell.client.data.PDEDataViewer;
import javax.swing.border.EmptyBorder;

public class FRAPSimDataViewerPanel extends JPanel {
//	private final PDEDataViewer simulationDataViewer;
	private final PDEDataViewer originalDataViewer;


	public FRAPSimDataViewerPanel() {
		super();
		setLayout(new GridBagLayout());

		final JPanel tabbedPane = new JPanel(new BorderLayout());
		tabbedPane.setForeground(new Color(0,0,244));
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.ipadx = 20;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		add(tabbedPane, gridBagConstraints);

		final JScrollPane scrollPane_1 = new JScrollPane();
		tabbedPane.add(scrollPane_1, BorderLayout.CENTER);

		originalDataViewer = new PDEDataViewer();
		originalDataViewer.setPreferredSize(new Dimension(0, 500));
		originalDataViewer.setMaximumSize(new Dimension(0, 500));
		originalDataViewer.setBorder(new EmptyBorder(8, 0, 0, 0));
		scrollPane_1.setViewportView(originalDataViewer);
		
		init();
	}

	private void init(){
		try{
			VirtualFrapWindowManager expDataViewerManager = new VirtualFrapWindowManager();
			originalDataViewer.setDataViewerManager(expDataViewerManager);
			expDataViewerManager.addDataJobListener(originalDataViewer);
			expDataViewerManager.addExportListener(originalDataViewer);
			
			VirtualFrapWindowManager simulationDataViewerManager = new VirtualFrapWindowManager();
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public PDEDataViewer getOriginalDataViewer(){
		return originalDataViewer;
	}
}
