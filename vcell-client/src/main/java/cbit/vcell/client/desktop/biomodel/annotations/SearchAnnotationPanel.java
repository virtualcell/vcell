package cbit.vcell.client.desktop.biomodel.annotations;

import cbit.vcell.biomodel.meta.VCMetaDataMiriamManager;
import cbit.vcell.client.desktop.biomodel.AnnotationsPanel;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cbit.vcell.biomodel.meta.VCMetaDataMiriamManager.VCMetaDataDataType.DataType_UNIPROT;

public class SearchAnnotationPanel extends JFrame implements ActionListener {

    private JButton searchButton;
    private JTextField searchBar;
    private JTextField organismSearchField;
    private JList<String> list;
    private DefaultListModel<String> dlm;
    private JComboBox<String> ontologiesBox;
    private JComboBox<String> containsBox;
    private JComboBox<String> limitBox;
    private JTextArea descriptionArea;
    private List<SearchElement> SearchElements = new ArrayList<>();
    private final JButton addButton, closeWinButton;
    private final AnnotationsPanel annotationsPanel; //to connect AnnotationsPanel with AddAnnotationsPanel
    private String annotDescription;
    private JComboBox jComboBoxURI, jComboBoxQualifier;			// identity provider combo
    private JPanel searchComponents;

    public SearchAnnotationPanel (AnnotationsPanel annotationsPanel, JComboBox JComboBoxURI, JComboBox JComboBoxQualifier) {
        this.annotationsPanel = annotationsPanel;
        this.jComboBoxURI = (JComboBoxURI==null) ? new JComboBox<>(): JComboBoxURI;
        this.jComboBoxQualifier = (JComboBoxQualifier==null) ? new JComboBox<>(): JComboBoxQualifier;

        setTitle("Add Annotations");
        setResizable(false);
        setLocationRelativeTo(null);

        GridBagLayout gbl = new GridBagLayout();
        JPanel mainPanel = new JPanel(gbl);
        searchComponents = new JPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15,0,0,0);

        //Add left panel for searching annotations
        gbc.gridy = 0;
        JPanel searchPanel = new JPanel();
        addSearchPanelComponents(searchPanel);
        mainPanel.add(searchPanel, gbc);

        //Add right panel for descriptions
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0,15,0,0);
        JPanel descriptionPanel = new JPanel();
        addDescriptionPanelComponents(descriptionPanel);
        mainPanel.add(descriptionPanel,gbc);

        //Add JComboBox for qualifier
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,0,12,10);
        mainPanel.add(jComboBoxQualifier,gbc);

        //Add "add" button to main panel
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,0,12,0);
        addButton = new JButton("Add");
        addButton.addActionListener(this);
        mainPanel.add(addButton, gbc);

        //Add OK button to main panel
        gbc.gridy = 1;
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,0,12,15);
        closeWinButton = new JButton("Close");
        closeWinButton.setSize(10,20);
        closeWinButton.setMinimumSize(closeWinButton.getPreferredSize());
        closeWinButton.addActionListener(this);
        mainPanel.add(closeWinButton, gbc);

        this.getContentPane().add(mainPanel);
        this.pack();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private void addSearchPanelComponents(JPanel leftPanel) {
        GridBagLayout sGbl = new GridBagLayout();
        GridBagConstraints sGbc = new GridBagConstraints();
        leftPanel.setLayout(sGbl);

        sGbc.weightx = 1;
        sGbc.weighty = 1;
        sGbc.fill = GridBagConstraints.NONE;

        //adding select ontology Label
        sGbc.gridy = 0;
        sGbc.gridx = 0;
        sGbc.insets = new Insets(0, 10, 0, 0);
        JLabel selectOntologyLabel = new JLabel("Select Provider:");
        leftPanel.add(selectOntologyLabel, sGbc);


    }

    private void addDescriptionPanelComponents(JPanel rightPanel) {
        GridBagLayout dGbl = new GridBagLayout();
        GridBagConstraints dGbc = new GridBagConstraints();
        rightPanel.setLayout(dGbl);

        dGbc.weightx = 1;
        dGbc.weighty = 0.5;
        dGbc.fill = GridBagConstraints.HORIZONTAL;

        //adding label
        dGbc.gridy = 0;
        dGbc.anchor = GridBagConstraints.SOUTH;
        dGbc.insets = new Insets(0,0,5,0);
        JLabel descLabel = new JLabel("Select an item for more info:");
        rightPanel.add(descLabel, dGbc);

        //adding text area
        dGbc.weighty = 1;
        dGbc.gridy = 1;
        dGbc.ipady = 30;
        dGbc.anchor = GridBagConstraints.NORTH;
        dGbc.fill = GridBagConstraints.BOTH;
        dGbc.insets = new Insets(0,0,20,15);
        descriptionArea = new JTextArea();
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setColumns(35);
        descriptionArea.setEditable(false);
//        descriptionArea.setMaximumSize(new Dimension(10,10));
        rightPanel.add(new JScrollPane(descriptionArea),dGbc);
    }





    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == searchButton) {
            dlm.clear();
            try {
                getSearchResults();
            } catch (IOException | URISyntaxException | ParserConfigurationException | InterruptedException |
                     SAXException e) {
                throw new RuntimeException(e);
            }
        }else if (evt.getSource() == addButton) {
            setAnnotationDescAndIdentifier();
        }else if (evt.getSource() == closeWinButton) {
            this.dispose();
        }

    }

    private void getSearchResults() throws IOException, URISyntaxException, ParserConfigurationException, InterruptedException, SAXException {
        //clear description area and search list before new search
//        dlm.clear();

        String searchTerm = searchBar.getText();
        VCMetaDataMiriamManager.VCMetaDataDataType mdt = (VCMetaDataMiriamManager.VCMetaDataDataType)jComboBoxURI.getSelectedItem();

        String selectedProvider = mdt != null ? mdt.getDataTypeName() : null;

        int searchSize = Integer.parseInt(Objects.requireNonNull(limitBox.getSelectedItem()).toString());

        if (mdt == DataType_UNIPROT) {
            UniProtSearch uniProtSearch = new UniProtSearch();
            SearchElements = uniProtSearch.search(searchTerm,searchSize,organismSearchField.getText());
        } else {
            boolean isExactMatch = Objects.equals(containsBox.getSelectedItem(), "exact");
            BioPortalSearch bioPortalSearch = new BioPortalSearch();
            SearchElements = bioPortalSearch.search(searchTerm,searchSize,selectedProvider,isExactMatch);
        }


        if (!SearchElements.isEmpty()) {
            list.setEnabled(true);
            list.setFont(new Font("TimesRoman",Font.PLAIN,14));
            for (SearchElement element: SearchElements) {
                dlm.addElement(element.getEntityName());
            }
        } else {
            list.setFont(new Font("Arial", Font.BOLD, 16));
            dlm.addElement("No matches found");
            list.setEnabled(false);
        }
    }

    private void setAnnotationDescAndIdentifier() {
        if (!list.isSelectionEmpty()) {
            annotationsPanel.addToAnnotationTextArea(annotDescription);
        }
        annotationsPanel.addIdentifier(SearchElements.get(list.getSelectedIndex()));
    }


    public JButton getOkButton() {
        return closeWinButton;
    }

}
