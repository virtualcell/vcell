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
import javax.swing.JPanel;
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
	private VCellModelService vCellModelService;
	
	public VCellModelPanel(VCellModelService vCellModelService) {
		this.vCellModelService = vCellModelService;
		initializeComponents();
		setPreferredSize(new Dimension(600, 400));
		getModels();
	}
	
	private void initializeComponents() {
		setLayout(new BorderLayout());
		
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
		add(splitPane, BorderLayout.CENTER);
		
		list.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				selectedModel = list.getSelectedValue();
				displayImageOfSelectedModel();
				parameterInputPanel.setParameters(selectedModel.getParameters());
			}
		});
	}
	
	public void displayImageOfSelectedModel() {
		
		VCellModel selectedModel = list.getSelectedValue();
		if (selectedModel == null) return;
		
		Image image = selectedModel.getImage();
		if (image == null) return;
		
		image = image.getScaledInstance(-1, imageScrollPane.getHeight(), Image.SCALE_FAST);
		imageLabel.setIcon(new ImageIcon(image));
	}
	
	public VCellModel getSelectedModel() {
		return list.getSelectedValue();
	}
	
	private void getModels() {
		vCellModelService.getVCellModels(new FutureCallback<VCellModel[]>() {
			
			@Override
			public void onSuccess(VCellModel[] result) {
				System.out.println("success");
				updateList(result, listModel);
			}

			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		});
	}
	
	private void updateList(VCellModel[] models, DefaultListModel<VCellModel> listModel) {
		for (VCellModel vCellModel : models) {
			listModel.addElement(vCellModel);
		}
	}
}
