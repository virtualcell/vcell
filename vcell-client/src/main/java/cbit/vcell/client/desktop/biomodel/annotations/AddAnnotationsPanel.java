package cbit.vcell.client.desktop.biomodel.annotations;

import cbit.vcell.biomodel.meta.VCMetaDataMiriamManager;
import cbit.vcell.client.desktop.biomodel.AnnotationsPanel;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static cbit.vcell.biomodel.meta.VCMetaDataMiriamManager.VCMetaDataDataType.DataType_UNIPROT;

public class AddAnnotationsPanel extends JFrame implements ActionListener {

    private JButton searchButton;
    private JTextField searchBar;
    private JTextField organismSearchField = new JTextField(20);
    private JList<String> list;
    private DefaultListModel<String> dlm;
    private JComboBox<String> ontologiesBox;
    private JComboBox<String> containsBox;
    private JComboBox<String> limitBox;
    private JTextArea descriptionArea;
    private List<SearchElement> SearchElements;
    private final JButton addButton, closeWinButton;
    private final AnnotationsPanel annotationsPanel; //to connect AnnotationsPanel with AddAnnotationsPanel
    private String annotDescription;
    private JComboBox jComboBoxURI, jComboBoxQualifier;			// identity provider combo
    private JPanel searchComponentsPanel = null;

    public AddAnnotationsPanel (AnnotationsPanel annotationsPanel, JComboBox JComboBoxURI, JComboBox JComboBoxQualifier) {
        this.annotationsPanel = annotationsPanel;
        this.jComboBoxURI = (JComboBoxURI==null) ? new JComboBox<>(): JComboBoxURI;
        this.jComboBoxQualifier = (JComboBoxQualifier==null) ? new JComboBox<>(): JComboBoxQualifier;

        setTitle("Add Annotations");
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 5;
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        JPanel searchPanel = new JPanel();
        addSearchPanelComponents(searchPanel);              // --- top panel (Search) ------------------------
        mainPanel.add(searchPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.5;
        gbc.weighty = 1;
        gbc.insets = new Insets(0,10,10,0);
        gbc.fill = GridBagConstraints.BOTH;
        JPanel resultsPanel = new JPanel();
        addResultsPanelComponents(resultsPanel);            // --- left panel (Search Results) ---------
        mainPanel.add(resultsPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.5;
        gbc.weighty = 1;
        gbc.insets = new Insets(0,10,10,10);
        gbc.fill = GridBagConstraints.BOTH;
        JPanel descriptionPanel = new JPanel();
        addDescriptionPanelComponents(descriptionPanel);    // --- right panel (Description) ---------
        mainPanel.add(descriptionPanel, gbc);

        // -------------------------------- bottom part of main panel: qualifier combobox, Add and Close button
        gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,10,10,10);             // Qualifier label
        mainPanel.add(new JLabel("Qualifier: "), gbc);

        gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,0,10,10);             // JComboBox for qualifier
        mainPanel.add(jComboBoxQualifier, gbc);

        //Add "add" button to main panel
        gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,0,10,0);
        addButton = new JButton("Add");                                 // the Add button
        addButton.addActionListener(this);
        mainPanel.add(addButton, gbc);

        gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,0,10,10);             // filler
        mainPanel.add(new JLabel(""), gbc);

        gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.gridx = 4;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,0,10,10);
        closeWinButton = new JButton("Close");
        closeWinButton.setSize(10,20);
        closeWinButton.setMinimumSize(closeWinButton.getPreferredSize());   // the Close button
        closeWinButton.addActionListener(this);
        mainPanel.add(closeWinButton, gbc);

        this.getContentPane().add(mainPanel);
        this.pack();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    public JButton getOkButton() {
        return closeWinButton;
    }

    private void addSearchPanelComponents(JPanel topPanel) {
        topPanel.setLayout(new GridBagLayout());

        GridBagConstraints sGbc = new GridBagConstraints();
        //adding select ontology Label
        sGbc.gridy = 0;
        sGbc.gridx = 0;
        sGbc.insets = new Insets(0,0,10,0);
        sGbc.anchor = GridBagConstraints.WEST;
        JLabel selectOntologyLabel = new JLabel("Select Provider:");
        topPanel.add(selectOntologyLabel, sGbc);

        sGbc = new GridBagConstraints();
        sGbc.gridy = 0;
        sGbc.gridx = 1;
        sGbc.insets = new Insets(0,10,10,20);
        sGbc.anchor = GridBagConstraints.WEST;
        jComboBoxURI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGUI();
            }
        });
        topPanel.add(jComboBoxURI, sGbc);      // ontologies combobox

        sGbc = new GridBagConstraints();
        sGbc.gridy = 0;
        sGbc.gridx = 2;
        sGbc.weightx = 1;
        topPanel.add(new javax.swing.JLabel(""), sGbc);      // empty filler

        // ---------- second row -----------------------------------------------------------------
        sGbc = new GridBagConstraints();
        sGbc.gridy = 1;
        sGbc.gridx = 0;
        sGbc.anchor = GridBagConstraints.WEST;
        sGbc.insets = new Insets(0,0,10,0);
        JLabel searchLabel = new JLabel("Search term:");        // search label
        topPanel.add(searchLabel, sGbc);

        sGbc = new GridBagConstraints();
        sGbc.gridy = 1;
        sGbc.gridx = 1;
        sGbc.anchor = GridBagConstraints.WEST;
        sGbc.insets = new Insets(0,10,10,0);
        containsBox = new JComboBox<>();            // contains/exact Jcombobox
        containsBox.addItem("contains");
        containsBox.addItem("exact");
        topPanel.add(containsBox, sGbc);

        sGbc = new GridBagConstraints();
        sGbc.gridy = 1;
        sGbc.fill = GridBagConstraints.HORIZONTAL;
        sGbc.gridx = 2;
        sGbc.weightx = 1;
        sGbc.gridwidth = 3;
        sGbc.anchor = GridBagConstraints.WEST;
        sGbc.insets = new Insets(0,20,10,0);
//        searchBar = new JTextField(25);
//        searchBar = new JTextField(15);
//        leftPanel.add(searchBar, sGbc);
        topPanel.add(getSearchComponentsPanel(), sGbc);    // the searchComponentsPanel

        // --------- third row -------------------------------------------------------------------
        sGbc.fill = GridBagConstraints.NONE;
        sGbc.anchor = GridBagConstraints.WEST;
        sGbc.gridwidth = 1;
        sGbc.gridy = 2;
        sGbc.gridx = 0;
        sGbc.insets = new Insets(0,0,0,10);
        JLabel limitLabel = new JLabel("Limit to");         // limit label
        topPanel.add(limitLabel, sGbc);

        //adding limit JComboBox
        sGbc.fill = GridBagConstraints.HORIZONTAL;
        sGbc.anchor = GridBagConstraints.WEST;
        sGbc.gridy = 2;
        sGbc.gridx = 1;
        sGbc.insets = new Insets(0,10,0,10);
        limitBox = new JComboBox<>();
        limitBox.addItem("50");
        limitBox.addItem("100");
        limitBox.addItem("500");
        topPanel.add(limitBox, sGbc);

        //adding limit term label
        sGbc.fill = GridBagConstraints.NONE;
        sGbc.anchor = GridBagConstraints.WEST;
        sGbc.gridy = 2;
        sGbc.gridx = 2;
        sGbc.insets = new Insets(0,0,0,20);
        JLabel elementLabel = new JLabel("elements");
        topPanel.add(elementLabel, sGbc);

        sGbc = new GridBagConstraints();
        sGbc.gridy = 2;
        sGbc.gridx = 3;
        sGbc.weightx = 1;
        topPanel.add(new javax.swing.JLabel(""), sGbc);      // empty filler

        sGbc.anchor = GridBagConstraints.EAST;
        sGbc.gridy = 2;
        sGbc.gridx = 4;
        sGbc.insets = new Insets(0,10,0,0);
        searchButton = new JButton("Search");                   // the Search button
        searchButton.addActionListener(this);
        topPanel.add(searchButton, sGbc);

/*
        //adding results label
        sGbc.fill = GridBagConstraints.HORIZONTAL;
        sGbc.gridy = 3;
        sGbc.gridx = 0;
        sGbc.gridwidth = 4;
        sGbc.insets = new Insets(8,10,0,0);
        JLabel resultLabel = new JLabel("Search Results:");
        leftPanel.add(resultLabel, sGbc);

        //adding term list
        sGbc.gridy = 4;
        sGbc.gridx = 0;
        sGbc.insets = new Insets(0,10,15,0);
        sGbc.ipady = 80;
        list = new JList<>();
        dlm = new DefaultListModel<>();
        list.setModel(dlm);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    getDescription();
                }
            }
        });
        leftPanel.add(new JScrollPane(list), sGbc);
 */
    }

    private JPanel getSearchComponentsPanel() {
        if(searchComponentsPanel != null) {
            return searchComponentsPanel;
        }
        searchComponentsPanel = new JPanel();
//        searchComponents.removeAll();

//        GridBagLayout gbl = new GridBagLayout();
        searchComponentsPanel.setLayout(new GridBagLayout());

        //adding search bar
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        searchBar = new JTextField(25);
        searchComponentsPanel.add(searchBar, gbc);

        VCMetaDataMiriamManager.VCMetaDataDataType mdt = (VCMetaDataMiriamManager.VCMetaDataDataType)jComboBoxURI.getSelectedItem();
//        if (mdt == DataType_UNIPROT) {
//            searchBar.setColumns(15);
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(0,25,0,0);
            JLabel organismLabel = new JLabel("in Organism:");
            searchComponentsPanel.add(organismLabel, gbc);
//            organismLabel.setVisible(false);

            gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.weightx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(0,10,0,0);
//            organismSearchField = new JTextField(8);
        searchComponentsPanel.add(organismSearchField, gbc);
//            organismSearchField.setVisible(false);
//            organismLabel.setVisible(true);
//            organismSearchField.setVisible(true);
            this.pack();
//        }
        updateGUI();
        return searchComponentsPanel;
    }

    private void addResultsPanelComponents(JPanel leftPanel) {
        leftPanel.setLayout(new GridBagLayout());

        GridBagConstraints sGbc = new GridBagConstraints();
        sGbc.fill = GridBagConstraints.HORIZONTAL;
        sGbc.anchor = GridBagConstraints.WEST;
        sGbc.gridy = 0;
        sGbc.gridx = 0;
        sGbc.insets = new Insets(0,0,5,0);
        JLabel resultLabel = new JLabel("Search Results:");     // search results label
        leftPanel.add(resultLabel, sGbc);

        sGbc = new GridBagConstraints();
        sGbc.gridy = 1;
        sGbc.gridx = 0;
        sGbc.weightx = 1;
        sGbc.weighty = 1;
        sGbc.insets = new Insets(0,0,0,0);
//        sGbc.ipady = 80;
        sGbc.fill = GridBagConstraints.BOTH;
        sGbc.anchor = GridBagConstraints.WEST;
        list = new JList<>();                                       // search results list
        dlm = new DefaultListModel<>();
        list.setModel(dlm);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    getDescription();
                }
            }
        });
        leftPanel.add(new JScrollPane(list), sGbc);
    }

    private void addDescriptionPanelComponents(JPanel rightPanel) {
        rightPanel.setLayout(new GridBagLayout());

        GridBagConstraints dGbc = new GridBagConstraints();
        dGbc.gridy = 0;
        dGbc.gridx = 0;
        dGbc.anchor = GridBagConstraints.WEST;
        dGbc.fill = GridBagConstraints.HORIZONTAL;
        dGbc.insets = new Insets(0,0,5,0);
        JLabel descLabel = new JLabel("Select an item for more info:");     // label
        rightPanel.add(descLabel, dGbc);

        //adding text area
        dGbc = new GridBagConstraints();
        dGbc.weightx = 1;
        dGbc.weighty = 1;
        dGbc.gridx = 0;
        dGbc.gridy = 1;
//        dGbc.ipady = 30;
        dGbc.anchor = GridBagConstraints.WEST;
        dGbc.fill = GridBagConstraints.BOTH;
        dGbc.insets = new Insets(0,0,0,0);
        descriptionArea = new JTextArea();                                          // text area
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

    public void updateGUI() {
        if(dlm == null) {
            organismSearchField.setEnabled(false);
            return;
        }
        dlm.clear();
        searchBar.setText(null);
//        searchBar.requestFocusInWindow();

        VCMetaDataMiriamManager.VCMetaDataDataType mdt = (VCMetaDataMiriamManager.VCMetaDataDataType)jComboBoxURI.getSelectedItem();
        if (mdt == DataType_UNIPROT) {
            containsBox.removeItem("exact");
        } else {
            if (containsBox.getItemCount() < 2) {
                containsBox.addItem("exact");
            }
        }
//        setSearchComponents();

        if (mdt == DataType_UNIPROT) {
            organismSearchField.setEnabled(true);
        } else {
            organismSearchField.setEnabled(false);
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

    private void getDescription() {
        if (!list.isSelectionEmpty()) {
            annotDescription = SearchElements.get(list.getSelectedIndex()).toString();
            descriptionArea.setText(annotDescription);
            descriptionArea.setCaretPosition(0);
        } else {
            descriptionArea.setText(null);
        }
    }

    private void setAnnotationDescAndIdentifier() {
        if (!list.isSelectionEmpty()) {
            annotationsPanel.addToAnnotationTextArea(annotDescription);
        }
        annotationsPanel.addIdentifier(SearchElements.get(list.getSelectedIndex()));
    }

}
