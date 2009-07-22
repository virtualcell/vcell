package cbit.vcell.client.desktop;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.vcell.util.BeanUtils;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.PopupGenerator;

public class BioModelsNetJPanel extends JPanel {
	private DocumentWindow documentWindow;
	private JButton openCuratedModelButton;
	private static final String BIOMODELS_DATABASE_URL = "http://www.ebi.ac.uk/biomodels/";
	
	public BioModelsNetJPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {7,0,7,0,0,7,7};
		setLayout(gridBagLayout);

		final JLabel biomodelsnetLabel = new JLabel();
		biomodelsnetLabel.setFont(new Font("", Font.BOLD, 20));
		biomodelsnetLabel.setText("BioModels DataBase");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.gridx = 0;
		gridBagConstraints_4.gridy = 0;
		add(biomodelsnetLabel, gridBagConstraints_4);

		final JLabel helpingToDefineLabel_1 = new JLabel();
		helpingToDefineLabel_1.setFont(new Font("", Font.BOLD, 14));
		helpingToDefineLabel_1.setText("A data resource that allows researchers to ");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.gridx = 0;
		gridBagConstraints_2.gridy = 1;
		add(helpingToDefineLabel_1, gridBagConstraints_2);

		final JLabel helpingToDefineLabel = new JLabel();
		helpingToDefineLabel.setFont(new Font("", Font.BOLD, 14));
		helpingToDefineLabel.setText("store, search and retrieve published mathematical models");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 2;
		add(helpingToDefineLabel, gridBagConstraints_1);

		final JLabel modelsOfBiologicalLabel = new JLabel();
		modelsOfBiologicalLabel.setFont(new Font("", Font.BOLD, 14));
		modelsOfBiologicalLabel.setText("of biological interest.");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.gridy = 3;
		gridBagConstraints_3.gridx = 0;
		add(modelsOfBiologicalLabel, gridBagConstraints_3);

		final JButton httpbiomodelsnetLabel = new JButton();
		httpbiomodelsnetLabel.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				DialogUtils.browserLauncher(BioModelsNetJPanel.this, BIOMODELS_DATABASE_URL,BIOMODELS_DATABASE_URL, false);
			}
		});
		httpbiomodelsnetLabel.setFont(new Font("", Font.BOLD, 14));
		httpbiomodelsnetLabel.setText(BIOMODELS_DATABASE_URL);
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridx = 0;
		add(httpbiomodelsnetLabel, gridBagConstraints);

		openCuratedModelButton = new JButton();
		openCuratedModelButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if(documentWindow != null){
					try {
						BioModelInfo[] bioModelInfoArr =
							documentWindow.getTopLevelWindowManager().getRequestManager().getDocumentManager().getBioModelInfos();
						Vector<BioModelInfo> bioModelsNetBMInfoV = new Vector<BioModelInfo>();
						for (int i = 0; i < bioModelInfoArr.length; i++) {
							if(bioModelInfoArr[i].getVersion().getOwner().getName().equals("BioModelsNet")){
								bioModelsNetBMInfoV.add(bioModelInfoArr[i]);
							}
						}
						if(bioModelsNetBMInfoV.size() > 0){
							BioModelInfo[] versionArr = new BioModelInfo[bioModelsNetBMInfoV.size()];
							bioModelsNetBMInfoV.copyInto(versionArr);
							final DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
							BioModelInfo selectedBMInfo = (BioModelInfo)DialogUtils.showListDialog(BioModelsNetJPanel.this,
									versionArr,
									"Select BioModels Database curated BioModel to Open",
									new ListCellRenderer(){
										public Component getListCellRendererComponent(
												JList list, Object value,
												int index, boolean isSelected,
												boolean cellHasFocus) {
											return dlcr.getListCellRendererComponent(
													list,
													((BioModelInfo)value).getVersion().getName(),
													index, isSelected, cellHasFocus);
										}}
							);
							if(selectedBMInfo != null){
								disposeParentDialog();
								documentWindow.getTopLevelWindowManager().getRequestManager().openDocument(
									selectedBMInfo,documentWindow.getTopLevelWindowManager(), true);
							}
						}else{
							throw new Exception("Error: No BioModels Database curated models found.");
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						PopupGenerator.showErrorDialog(BioModelsNetJPanel.this, "Error opening BioModel.\n"+e1.getMessage());
					}
				}else{
					PopupGenerator.showErrorDialog(BioModelsNetJPanel.this, "Error: needs DocumentWindow.");
				}
			}
		});
		openCuratedModelButton.setFont(new Font("", Font.BOLD, 14));
		openCuratedModelButton.setText("Open Curated Model in VCell...");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_5.gridy = 5;
		gridBagConstraints_5.gridx = 0;
		add(openCuratedModelButton, gridBagConstraints_5);
	}

	public void setDocumentWindow(DocumentWindow documentWindow) {
		this.documentWindow = documentWindow;
	}

	private void disposeParentDialog(){
		Container parent =
			BeanUtils.findTypeParentOfComponent(BioModelsNetJPanel.this, Dialog.class);
		if(parent instanceof JDialog){
			((JDialog)parent).dispose();
		}

	}
}
