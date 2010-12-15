package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import cbit.vcell.client.data.PDEDataViewer;

@SuppressWarnings("serial")
public class FRAPSimDataViewerPanel extends JPanel {
//	private final PDEDataViewer simulationDataViewer;
	private final PDEDataViewer originalDataViewer;


	public FRAPSimDataViewerPanel() {
		super();
		setLayout(new BorderLayout());

		originalDataViewer = new PDEDataViewer();
		originalDataViewer.setPreferredSize(new Dimension(0, 500));
		originalDataViewer.setMaximumSize(new Dimension(0, 500));
		originalDataViewer.setBorder(new EmptyBorder(8, 0, 0, 0));

		add(originalDataViewer, BorderLayout.CENTER);
		
		init();
	}

	private void init(){
		try{
			VirtualFrapWindowManager expDataViewerManager = new VirtualFrapWindowManager();
			originalDataViewer.setDataViewerManager(expDataViewerManager);
			expDataViewerManager.addDataJobListener(originalDataViewer);
			expDataViewerManager.addExportListener(originalDataViewer);
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public PDEDataViewer getOriginalDataViewer(){
		return originalDataViewer;
	}
}
