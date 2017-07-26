package org.vcell;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.imagej.Dataset;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;

public class ChangeAxesPanel extends JPanel {
	
	private static final long serialVersionUID = -3922731675251506625L;

	private List<JComboBox<AxisType>> axesComboBoxList;
	
	private final AxisType[] axisTypes = new AxisType[] {
			Axes.X,
			Axes.Y,
			Axes.Z,
			Axes.TIME,
			Axes.CHANNEL,
			Axes.unknown()
	};

	public ChangeAxesPanel(Dataset dataset) {
		
		setLayout(new GridLayout(0, 2));
		
		axesComboBoxList = new ArrayList<>();
		
		for (int i = 0; i < dataset.numDimensions(); i++) {
			
			JComboBox<AxisType> comboBox = new JComboBox<>(axisTypes);
			comboBox.setSelectedItem(dataset.axis(i).type());
			axesComboBoxList.add(comboBox);
			add(new JLabel((i + 1) + ": "));
			add(comboBox);
		}
	}
	
	public List<AxisType> getSelectedAxisTypes() {
		return axesComboBoxList.stream()
				.map(comboBox -> (AxisType) comboBox.getSelectedItem())
				.collect(Collectors.toList());
	}
}
