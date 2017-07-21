package org.vcell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import com.google.common.util.concurrent.FutureCallback;


@SuppressWarnings("serial")
public class VCellModelPanel extends JPanel {
	
	private JList<VCellModel> list;
	private DefaultListModel<VCellModel> listModel;
	private VCellModel selectedModel;
	private JLabel imageLabel;
	private JScrollPane imageScrollPane;
	private JProgressBar progressBar;
	private VCellModelService vCellModelService;
	
	public VCellModelPanel(VCellModelService vCellModelService) {
		this.vCellModelService = vCellModelService;
		initializeComponents();
		setPreferredSize(new Dimension(600, 400));
	}
	
	public VCellModel getSelectedModel() {
		return list.getSelectedValue();
	}
	
	public void displayImageOfSelectedModel() {
		displayScaledImageOfSelectedModel(imageScrollPane.getHeight() - 6);
	}
	
	private void displayScaledImageOfSelectedModel(int height) {
		Image image = getImageOfSelectedModel();
		if (image == null) return;
		
		image = image.getScaledInstance(-1, height, Image.SCALE_FAST);
		imageLabel.setIcon(new ImageIcon(image));
	}
	
	private Image getImageOfSelectedModel() {
		VCellModel selectedModel = list.getSelectedValue();
		if (selectedModel == null) return null;
		return selectedModel.getImage();
	}
	
	private void initializeComponents() {
		setLayout(new BorderLayout());
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setString("Loading models...");
		progressBar.setIndeterminate(false);
		add(progressBar, BorderLayout.PAGE_START);
		
		listModel = new DefaultListModel<>();
		
		list = new JList<>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new DefaultListCellRenderer());
		list.setFixedCellWidth(200);
		
		JScrollPane leftScrollPane = new JScrollPane(list);
		
		ImageIcon imageIcon = new ImageIcon();
		imageLabel = new JLabel(imageIcon);
		imageScrollPane = new JScrollPane(imageLabel);
		imageScrollPane.setMinimumSize(new Dimension(0, 200));
		
		ModelParameterInputPanel parameterInputPanel = new ModelParameterInputPanel(new ArrayList<>());
		
		JSplitPane verticalSplitPane = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, 
				imageScrollPane,
				new JScrollPane(parameterInputPanel));
		JSplitPane splitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, 
				leftScrollPane, verticalSplitPane);
		
		verticalSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, event -> {
			displayScaledImageOfSelectedModel((int) event.getNewValue() - 6);
		});
		
		add(splitPane, BorderLayout.CENTER);
		
		list.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				selectedModel = list.getSelectedValue();
				displayImageOfSelectedModel();
				parameterInputPanel.setParameters(selectedModel.getParameters());
			}
		});
	}
	
	public void getModels() {
		vCellModelService.getVCellModels(new FutureProgressCallback<VCellModel[]>() {
			
			@Override
			public void onSuccess(VCellModel[] result) {
				progressBar.setVisible(false);
				updateList(result, listModel);
			}

			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
			
			@Override
			public void progressOccurred(int current, int max) {
				progressBar.setMaximum(max);
				progressBar.setValue(current);
			}
		});
	}
	
	private void updateList(VCellModel[] models, DefaultListModel<VCellModel> listModel) {
		for (VCellModel vCellModel : models) {
			listModel.addElement(vCellModel);
		}
	}
}
