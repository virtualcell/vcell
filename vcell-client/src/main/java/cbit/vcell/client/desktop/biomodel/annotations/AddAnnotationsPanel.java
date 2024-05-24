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
    private JTextField organismSearchField;
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
    private JPanel searchComponents;

    public AddAnnotationsPanel (AnnotationsPanel annotationsPanel, JComboBox JComboBoxURI, JComboBox JComboBoxQualifier) {
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

    public JButton getOkButton() {
        return closeWinButton;
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
        sGbc.insets = new Insets(0,10,0,0);
        JLabel selectOntologyLabel = new JLabel("Select Provider:");
        leftPanel.add(selectOntologyLabel, sGbc);

        //adding ontologies combobox
        sGbc.gridy = 0;
        sGbc.gridx = 1;
        sGbc.insets = new Insets(0,10,0,0);
        jComboBoxURI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGUI();
            }
        });
        leftPanel.add(jComboBoxURI, sGbc);

        //adding search label
        sGbc.gridy = 1;
        sGbc.gridx = 0;
        sGbc.anchor = GridBagConstraints.EAST;
        sGbc.insets = new Insets(15,0,0,0);
        JLabel searchLabel = new JLabel("Search term:");
        leftPanel.add(searchLabel, sGbc);
/////////////////////////////////////////////////////////////////////////////////////////////////
        //adding contains/exaxt Jcombobox
        sGbc.gridy = 1;
        sGbc.gridx = 1;
        sGbc.anchor = GridBagConstraints.CENTER;
        sGbc.insets = new Insets(15,0,0,0);

        containsBox = new JComboBox<>();
        containsBox.addItem("contains");
        containsBox.addItem("exact");
        leftPanel.add(containsBox, sGbc);

        //adding search bar
        sGbc.gridy = 1;
        sGbc.fill = GridBagConstraints.HORIZONTAL;
        sGbc.gridx = 2;
        sGbc.gridwidth = 1;
        sGbc.anchor = GridBagConstraints.WEST;
        sGbc.insets = new Insets(15,0,0,6);
        searchBar = new JTextField(25);
//        searchBar = new JTextField(15);
//        leftPanel.add(searchBar, sGbc);
        setSearchComponents();
        leftPanel.add(searchComponents, sGbc);




////////////////////////////////////////////////////////////////////////////////////////////////////////
        //adding limit label
        sGbc.fill = GridBagConstraints.NONE;
        sGbc.anchor = GridBagConstraints.EAST;
        sGbc.gridwidth = 1;
        sGbc.gridy = 2;
        sGbc.gridx = 0;
        sGbc.insets = new Insets(8,0,0,3);
        JLabel limitLabel = new JLabel("Limit to");
        leftPanel.add(limitLabel, sGbc);

        //adding limit JComboBox
        sGbc.fill = GridBagConstraints.HORIZONTAL;
        sGbc.anchor = GridBagConstraints.CENTER;
        sGbc.gridy = 2;
        sGbc.gridx = 1;
        sGbc.insets = new Insets(8,0,0,2);
        limitBox = new JComboBox<>();
        limitBox.addItem("50");
        limitBox.addItem("100");
        limitBox.addItem("500");

        leftPanel.add(limitBox, sGbc);

        //adding limit term label
        sGbc.fill = GridBagConstraints.NONE;
        sGbc.anchor = GridBagConstraints.WEST;
        sGbc.gridy = 2;
        sGbc.gridx = 2;
        sGbc.insets = new Insets(8,0,0,3);
        JLabel elementLabel = new JLabel("elements");
        leftPanel.add(elementLabel, sGbc);

        //adding search button
        sGbc.anchor = GridBagConstraints.EAST;
        sGbc.gridy = 2;
        sGbc.gridx = 3;
//        sGbc.insets = new Insets(8,0,0,0);
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        leftPanel.add(searchButton, sGbc);


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
    }

    private void setSearchComponents() {
//        searchComponents = new JPanel();
        searchComponents.removeAll();

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        searchComponents.setLayout(gbl);

        //adding search bar
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
//        gbc.insets = new Insets(15,0,0,10);
//        searchBar = new JTextField(25);
        searchBar = new JTextField(25);
        searchComponents.add(searchBar, gbc);

        VCMetaDataMiriamManager.VCMetaDataDataType mdt = (VCMetaDataMiriamManager.VCMetaDataDataType)jComboBoxURI.getSelectedItem();
//        if (mdt == DataType_UNIPROT) {
            searchBar.setColumns(15);

            gbc.gridx++;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(0,5,0,0);
            JLabel organismLabel = new JLabel("in Organism:");
            searchComponents.add(organismLabel, gbc);
//            organismLabel.setVisible(false);

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx++;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(0,5,0,10);
            organismSearchField = new JTextField(8);
            searchComponents.add(organismSearchField, gbc);
//            organismSearchField.setVisible(false);
//            organismLabel.setVisible(true);
//            organismSearchField.setVisible(true);
            this.pack();
//        }
        updateGUI();
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

    public void updateGUI() {
        if(dlm == null) {
            organismSearchField.setEnabled(false);
            return;
        }
        dlm.clear();
        searchBar.setText(null);
        searchBar.requestFocusInWindow();

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
