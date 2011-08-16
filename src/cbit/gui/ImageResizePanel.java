package cbit.gui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.vcell.util.ISize;

import cbit.vcell.field.FieldDataFileOperationSpec;

public class ImageResizePanel extends JPanel {
	private JLabel lblNewLabel;
	private JLabel ImageNameLabel;
	private JLabel lblNewLabel_2;
	private JLabel originalSizeLabel;
	private JLabel lblNewLabel_4;
	private JLabel newSizeLabel;
	private JLabel lblNewLabel_6;
	private JSlider resizeSlider;
	public ImageResizePanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0,0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		lblNewLabel = new JLabel("Image Name:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);
		
		ImageNameLabel = new JLabel("New label");
		GridBagConstraints gbc_ImageNameLabel = new GridBagConstraints();
		gbc_ImageNameLabel.anchor = GridBagConstraints.WEST;
		gbc_ImageNameLabel.insets = new Insets(0, 0, 5, 0);
		gbc_ImageNameLabel.gridx = 1;
		gbc_ImageNameLabel.gridy = 0;
		add(ImageNameLabel, gbc_ImageNameLabel);
		
		lblNewLabel_2 = new JLabel("Original Size (x,y,z):");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 1;
		add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		originalSizeLabel = new JLabel("New label");
		GridBagConstraints gbc_originalSizeLabel = new GridBagConstraints();
		gbc_originalSizeLabel.anchor = GridBagConstraints.WEST;
		gbc_originalSizeLabel.insets = new Insets(0, 0, 5, 0);
		gbc_originalSizeLabel.gridx = 1;
		gbc_originalSizeLabel.gridy = 1;
		add(originalSizeLabel, gbc_originalSizeLabel);
		
		lblNewLabel_4 = new JLabel("Reduced Size (x,y,z):");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 2;
		add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		newSizeLabel = new JLabel("New label");
		GridBagConstraints gbc_newSizeLabel = new GridBagConstraints();
		gbc_newSizeLabel.anchor = GridBagConstraints.WEST;
		gbc_newSizeLabel.insets = new Insets(0, 0, 5, 0);
		gbc_newSizeLabel.gridx = 1;
		gbc_newSizeLabel.gridy = 2;
		add(newSizeLabel, gbc_newSizeLabel);
		
		lblNewLabel_6 = new JLabel("Move slider to reduce X,Y size (% of original image)");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_6.gridwidth = 2;
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 3;
		add(lblNewLabel_6, gbc_lblNewLabel_6);
		
		resizeSlider = new JSlider();
		resizeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				resizeChanged();
			}
		});
		resizeSlider.setValue(100);
		resizeSlider.setPaintTicks(true);
		resizeSlider.setPaintLabels(true);
		resizeSlider.setMajorTickSpacing(10);
		GridBagConstraints gbc_resizeSlider = new GridBagConstraints();
		gbc_resizeSlider.weightx = 1.0;
		gbc_resizeSlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_resizeSlider.gridwidth = 2;
		gbc_resizeSlider.insets = new Insets(0, 0, 0, 5);
		gbc_resizeSlider.gridx = 0;
		gbc_resizeSlider.gridy = 4;
		add(resizeSlider, gbc_resizeSlider);
	}

	private ISize originalISize;
	private ISize newISize;
	public void init(ISize originalISize,String imageName){
		this.originalISize = originalISize;
		ImageNameLabel.setText(imageName);
		originalSizeLabel.setText(originalISize.toString());
		newSizeLabel.setText(originalISize.toString());
	}
	private void resizeChanged(){
		if(originalISize == null){
			return;
		}
		newISize = new ISize(originalISize.getX()*resizeSlider.getValue()/100, originalISize.getY()*resizeSlider.getValue()/100, originalISize.getZ());
		newSizeLabel.setText("<html><font color=red>"+newISize.toString()+"</font></html>");
	}
	public ISize getNewISize(){
		return newISize;
	}
}
