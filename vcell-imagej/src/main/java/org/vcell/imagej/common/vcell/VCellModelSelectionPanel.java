package org.vcell.imagej.common.vcell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import org.vcell.imagej.common.gui.Task;


@SuppressWarnings("serial")
public class VCellModelSelectionPanel extends JPanel {
	
	private VCellModelService vCellModelService;
	
	private JList<VCellModel> list;
	private DefaultListModel<VCellModel> listModel;
	private VCellModel selectedModel;
	private JLabel imageLabel;
	private JScrollPane imageScrollPane;
	private ModelParameterListPanel parameterListPanel;
	
	public VCellModelSelectionPanel(VCellModelService vCellModelService) {
		this.vCellModelService = vCellModelService;
		initializeComponents();
		setPreferredSize(new Dimension(600, 400));
	}
	
	public VCellModel getSelectedModel() {
		return list.getSelectedValue();
	}
	
	public void displayImageOfSelectedModel() {
		VCellModel vCellModel = list.getSelectedValue();
		if (vCellModel == null) return;
		displayImage(vCellModel);
	}
	
	public void displayImage(VCellModel vCellModel) {
		displayScaledImage(vCellModel, imageScrollPane.getHeight() - 6);
	}
	
	public void setModels(List<VCellModel> models) {
		listModel.clear();
		for (VCellModel vCellModel : models) {
			listModel.addElement(vCellModel);
			System.out.println(vCellModel.toString());
		}
	}
	
	private void displayScaledImage(VCellModel vCellModel, int height) {
		Image image = vCellModel.getImage();
		if (image == null) {
			imageLabel.setIcon(null);
			imageLabel.setText("Loading image...");
			Task<BufferedImage, Void> task = vCellModelService.getVCellImageForModel(vCellModel);
			task.addDoneListener(propertyChangeEvent -> {
				try {
					vCellModel.setImage(task.get());
					displayImageOfSelectedModel();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			});
			task.execute();
			return;
		}
		
		
		image = image.getScaledInstance(-1, height, Image.SCALE_DEFAULT);
		imageLabel.setText(null);
		imageLabel.setIcon(new ImageIcon(image));
		
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
		
		parameterListPanel = new ModelParameterListPanel();
		parameterListPanel.setPreferredSize(new Dimension(-1, 200));
		JScrollPane parameterScrollPane = new JScrollPane(parameterListPanel);
		parameterScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		JSplitPane verticalSplitPane = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, 
				imageScrollPane,
				parameterScrollPane);
		JSplitPane splitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, 
				leftScrollPane, verticalSplitPane);
		
		verticalSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, event -> {
			displayImageOfSelectedModel();
		});
		
		add(splitPane, BorderLayout.CENTER);
		
		list.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				selectedModel = list.getSelectedValue();
				displayImageOfSelectedModel();
				parameterListPanel.setModel(selectedModel);
			}
		});
	}

}
