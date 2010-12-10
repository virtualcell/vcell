package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import org.vcell.sybil.gui.pcsearch.test.PCKeywordQueryPanel;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.models.miriam.MIRIAMRef.URNParseFailureException;
import org.vcell.sybil.util.http.pathwaycommons.search.XRef;
import org.vcell.sybil.util.http.uniprot.UniProtConstants;
import org.vcell.sybil.util.miriam.XRefToURN;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;

import uk.ac.ebi.miriam.lib.MiriamLink;
import cbit.vcell.biomodel.meta.MiriamManager;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.model.Model;
import cbit.vcell.model.SpeciesContext;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2003 2:07:01 PM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class SpeciesPropertiesPanel extends JPanel {
	private SpeciesContext fieldSpeciesContext = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private Model fieldModel = null;
	private JTextArea annotationTextArea;
	private JButton pathwayDBJButton = null;
	private JEditorPane PCLinkValueEditorPane = null;
	private JTextField speciesNameTextField = null;
	
	public void saveSelectedXRef(final XRef selectedXRef, final MIRIAMQualifier miriamQualifier) {
		AsynchClientTask task1 = new AsynchClientTask("retrieving metadata", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				String urn = XRefToURN.createURN(selectedXRef.db(), selectedXRef.id());
				try {
					MiriamManager miriamManager = getModel().getVcMetaData().getMiriamManager();
					MiriamResource resource = miriamManager.createMiriamResource(urn);
					String urnstr = resource.getMiriamURN(); 
					if (urnstr != null && urnstr.toLowerCase().contains("uniprot")) {
						String prettyName = UniProtConstants.getNameFromID(urnstr);	
						miriamManager.setPrettyName(resource, prettyName);
					}

					Set<MiriamResource> miriamResources = new HashSet<MiriamResource>();
					miriamResources.add(resource);
					miriamManager.addMiriamRefGroup(getSpeciesContext().getSpecies(), miriamQualifier, miriamResources);
					
					MiriamLink link = new MiriamLink();
					if (!link.isLibraryUpdated()) {
						System.err.println("MirianLink library is not up to date!");
					}
					String pcLink = resource.getMiriamURN();
					if (pcLink != null && pcLink.length() > 0) {
						String[] locations = link.getLocations(pcLink);
						if (locations != null){
							for(String url : locations) {
								try {
									miriamManager.addStoredCrossReferencedLink(resource, new URL(url));
								} catch (MalformedURLException e) {
									e.printStackTrace(System.out);
								}
							}
						}
					}
				} catch (URNParseFailureException e) {
					e.printStackTrace();
					DialogUtils.showErrorDialog(SpeciesPropertiesPanel.this, e.getMessage());
				}
			}
		};
		AsynchClientTask task2 = new AsynchClientTask("displaying metadata", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				updatePCLink();
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2 });
	}

	private void updatePCLink() {
		try {
			StringBuffer buffer = new StringBuffer("<html>");
			MiriamManager miriamManager = getModel().getVcMetaData().getMiriamManager();
			Map<MiriamRefGroup,MIRIAMQualifier> refGroups = miriamManager.getAllMiriamRefGroups(getSpeciesContext().getSpecies());
			if (refGroups != null && refGroups.size()>0) {
				for (MiriamRefGroup refGroup : refGroups.keySet()){
					Set<MiriamResource> miriamResources = refGroup.getMiriamRefs();
					for (MiriamResource resource : miriamResources){
						String urn = resource.getMiriamURN();
						String preferredName = ""; 
						if (urn != null && urn.length() > 0) {
							String prettyName = miriamManager.getPrettyName(resource);
							if (prettyName != null) {
								preferredName = "[" + prettyName + "]";	
							}
							String prettyResourceName = urn.replaceFirst("urn:miriam:", "");
							buffer.append("&#x95;&nbsp;" + prettyResourceName + "&nbsp;<b>" + preferredName + "</b><br>");
							List<URL> linkURLs = miriamManager.getStoredCrossReferencedLinks(resource);
							for (URL url : linkURLs) {
								buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;-&nbsp;<a href=\"" + url.toString() + "\">" + url.toString() + "</a><br>");
							}
						}
					}
				}
			}
			buffer.append("</html>");
			getPCLinkValueEditorPane().setText(buffer.toString());
			getPCLinkValueEditorPane().setCaretPosition(0);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}

	}

	private class IvjEventHandler implements java.awt.event.ActionListener, HyperlinkListener, FocusListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getPathwayDBbutton()) 
				showPCKeywordQueryPanel();
		};
		// @Override
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == EventType.ACTIVATED) {
				URL link = e.getURL();
				if (link != null) {
					DialogUtils.browserLauncher(SpeciesPropertiesPanel.this, link.toExternalForm(), "failed to launch", false);
				}
			}
		}
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == annotationTextArea) {
				changeFreeTextAnnotation();
			} else if (e.getSource() == speciesNameTextField) {
				changeName();
			}
		};
	};

/**
 * EditSpeciesDialog constructor comment.
 */
public SpeciesPropertiesPanel() {
	super();
	initialize();
}

/**
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
private Model getModel() {
	return fieldModel;
}

/**
 * Gets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @return The speciesContext property value.
 * @see #setSpeciesContext
 */
public SpeciesContext getSpeciesContext() {
	return fieldSpeciesContext;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}

/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
	annotationTextArea.addFocusListener(ivjEventHandler);
	speciesNameTextField.addFocusListener(ivjEventHandler);
	getPathwayDBbutton().addActionListener(ivjEventHandler);
	getPCLinkValueEditorPane().addHyperlinkListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("SpeciesEditorPanel");
		setLayout(new GridBagLayout());
		
		speciesNameTextField = new JTextField();
		
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		JLabel label = new JLabel("Species Name");
		add(label, gbc);
		
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(speciesNameTextField, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Annotation"), gbc);

		annotationTextArea = new javax.swing.JTextArea("", 1, 30);
		annotationTextArea.setLineWrap(true);
		annotationTextArea.setWrapStyleWord(true);
		javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 0.2;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		add(jsp, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.gridx = 0;
		gbc.gridy = gridy;
		add(getPathwayDBbutton(), gbc);
		
		gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		JScrollPane scollPane = new JScrollPane(getPCLinkValueEditorPane());
		add(scollPane, gbc);
		
		setBackground(Color.white);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SpeciesPropertiesPanel aEditSpeciesPanel = new SpeciesPropertiesPanel();
		frame.add(aEditSpeciesPanel);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.pack();
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of cbit.gui.JInternalFrameEnhanced");
		exception.printStackTrace(System.out);
	}
}

/**
 * Comment
 */
private void changeFreeTextAnnotation() {
	try{
		if (getSpeciesContext() == null) {
			return;
		}
		// set text from annotationTextField in free text annotation for species in vcMetaData (from model)
		VCMetaData vcMetaData = getModel().getVcMetaData();
		vcMetaData.setFreeTextAnnotation(getSpeciesContext().getSpecies(), annotationTextArea.getText());				
	} catch(Exception e){
		e.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this,"Edit Species Error\n"+e.getMessage(), e);
	}
}

/**
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(Model model) {
	fieldModel = model;
}

/**
 * Sets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @param speciesContext The new value for the property.
 * @see #getSpeciesContext
 */
public void setSpeciesContext(SpeciesContext speciesContext) {
	if (fieldSpeciesContext == speciesContext) {
		return;
	}
	fieldSpeciesContext = speciesContext;
	updateInterface();
}

private void showPCKeywordQueryPanel() {
	 PCKeywordQueryPanel aPCKeywordQueryPanel = new PCKeywordQueryPanel();
	 int returnVal = DialogUtils.showComponentOKCancelDialog(this, aPCKeywordQueryPanel, "Search External Database");
	 
	 if (returnVal == JOptionPane.OK_OPTION) {
		 saveSelectedXRef(aPCKeywordQueryPanel.getSelectedXRef(),aPCKeywordQueryPanel.getMiriamQualifier());
	 }
}


/**
 * Comment
 */
private void updateInterface() {
	if (fieldSpeciesContext == null || fieldModel == null) {
		annotationTextArea.setText(null);
		getPCLinkValueEditorPane().setText(null);
		speciesNameTextField.setText(null);
	} else {
		speciesNameTextField.setText(getSpeciesContext().getName());
		annotationTextArea.setText(getModel().getVcMetaData().getFreeTextAnnotation(getSpeciesContext().getSpecies()));	
		updatePCLink();
	}
}

private JButton getPathwayDBbutton() {
	if (pathwayDBJButton == null) {
		try {
			pathwayDBJButton = new javax.swing.JButton();
			pathwayDBJButton.setName("pathwayDBJButton");
			pathwayDBJButton.setText("<html><center>Add Links<br> to Databases</center></html>");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return pathwayDBJButton;
}

	private JEditorPane getPCLinkValueEditorPane() {
		if (PCLinkValueEditorPane == null) {
			PCLinkValueEditorPane = new JEditorPane();
			PCLinkValueEditorPane.setContentType("text/html");
			PCLinkValueEditorPane.setEditable(false);
			PCLinkValueEditorPane.setBackground(Color.white);
			PCLinkValueEditorPane.setForeground(DefaultScrollTableCellRenderer.uneditableForeground);
			PCLinkValueEditorPane.setText(null);
		}
		return PCLinkValueEditorPane;
	}

	private void changeName() {
		if (fieldSpeciesContext == null) {
			return;
		}
		String newName = speciesNameTextField.getText();
		if (newName == null || newName.length() == 0) {
			speciesNameTextField.setText(getSpeciesContext().getName());
			return;
		}
		try {
			getSpeciesContext().setName(speciesNameTextField.getText());
		} catch (PropertyVetoException e1) {
			e1.printStackTrace();
			DialogUtils.showErrorDialog(SpeciesPropertiesPanel.this, e1.getMessage());
		}
	}

}