package cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cbit.vcell.microscopy.gui.VirtualFrapLoader;

@SuppressWarnings("serial")
public class FileSavePanel extends JPanel
{
	private JTextField fileNameTextField;
	public FileSavePanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7,0,7,7,0,7};
		gridBagLayout.columnWidths = new int[] {0,7,7,7,7,7};
		setLayout(gridBagLayout);

		final JLabel saveFrapDataLabel = new JLabel();
		saveFrapDataLabel.setForeground(new Color(0, 0, 128));
		saveFrapDataLabel.setFont(new Font("", Font.BOLD | Font.ITALIC, 16));
		saveFrapDataLabel.setText("Save to FRAP Document ");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		add(saveFrapDataLabel, gridBagConstraints);

		final JLabel selectedFileLabel = new JLabel();
		selectedFileLabel.setText("Selected save file name :");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.anchor = GridBagConstraints.WEST;
		gridBagConstraints_2.gridy = 5;
		gridBagConstraints_2.gridx = 0;
		add(selectedFileLabel, gridBagConstraints_2);

		fileNameTextField = new JTextField();
		fileNameTextField.setColumns(25);
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.anchor = GridBagConstraints.WEST;
		gridBagConstraints_3.gridwidth = 4;
		gridBagConstraints_3.gridy = 7;
		gridBagConstraints_3.gridx = 0;
		add(fileNameTextField, gridBagConstraints_3);

		final JButton browserButton = new JButton(new ImageIcon(getClass().getResource("/images/save.gif")));
		browserButton.setMargin(new Insets(0, 2, 0, 2));
		browserButton.setText("");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.anchor = GridBagConstraints.WEST;
		gridBagConstraints_4.gridy = 7;
		gridBagConstraints_4.gridx = 5;
		add(browserButton, gridBagConstraints_4);
		browserButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int option = VirtualFrapLoader.saveFileChooser.showOpenDialog(FileSavePanel.this);
				if(option == JFileChooser.APPROVE_OPTION)
				{
					String fileStr = VirtualFrapLoader.saveFileChooser.getSelectedFile().getAbsolutePath();
					setFileName(fileStr);
				}
			}
			
		});
	}

	public String getFileName()
	{
		if(fileNameTextField != null)
		{
			return fileNameTextField.getText();
		}
		return null;
	}
	
	public void setFileName(String fileName)
	{
		fileNameTextField.setText(fileName);
	}
}
