package cbit.vcell.client.desktop.biomodel.annotations;

import cbit.vcell.biomodel.meta.MiriamManager;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
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

public class AddAnnotationsPanel extends JFrame implements ActionListener {

    private JButton searchButton;
    private JTextField searchBar;
    private JList<String> list;
    private JComboBox<String> ontologiesBox;
    private JComboBox<String> containsBox;
    private JComboBox<String> limitBox;
    private JTextArea descriptionArea;
    private List<searchElement> searchElements;
    private final JButton addButton, oKButton;
    private final AnnotationsPanel annotationsPanel; //to connect AnnotationsPanel with AddAnnotationsPanel
    private String annotDescription;
    private JComboBox<MIRIAMQualifier> jComboBoxQualifier;
    private DefaultComboBoxModel<MIRIAMQualifier> defaultComboBoxModelQualifier;

    public AddAnnotationsPanel (AnnotationsPanel annotationsPanel) {
        this.annotationsPanel = annotationsPanel;
//        initializeComboBoxQualifier();

        setTitle("Add Annotations");
        setResizable(false);
        setLocationRelativeTo(null);

        GridBagLayout gbl = new GridBagLayout();
        JPanel mainPanel = new JPanel(gbl);


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
        mainPanel.add(annotationsPanel.getJComboBoxQualifier(),gbc);

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
        oKButton = new JButton("OK");
        oKButton.setSize(10,20);
        oKButton.setMinimumSize(oKButton.getPreferredSize());
        oKButton.addActionListener(this);
        mainPanel.add(oKButton, gbc);

        this.getContentPane().add(mainPanel);
        this.pack();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//        this.setDefaultCloseOperation();
        this.setVisible(true);
    }

    public JButton getOkButton() {
        return oKButton;
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
        JLabel selectOntologyLabel = new JLabel("Select Ontology:");
        leftPanel.add(selectOntologyLabel, sGbc);

        //adding ontologies combobox
        sGbc.gridy = 0;
        sGbc.gridx = 1;
        sGbc.insets = new Insets(0,10,0,0);

        ontologiesBox = new JComboBox<>();
        ontologiesBox.addItem("All Selected");
        ontologiesBox.addItem("GO");
        ontologiesBox.addItem("NCIT");
        ontologiesBox.addItem("CHEBI");
        ontologiesBox.addItem("FMA");
        ontologiesBox.addItem("BTO");
//        leftPanel.add(ontologiesBox, sGbc);
        leftPanel.add(annotationsPanel.getJComboBoxURI(), sGbc);

        //adding search label
        sGbc.gridy = 1;
        sGbc.gridx = 0;
        sGbc.anchor = GridBagConstraints.EAST;
        sGbc.insets = new Insets(15,0,0,0);
        JLabel searchLabel = new JLabel("Search term:");
        leftPanel.add(searchLabel, sGbc);

        //adding contains/exaxt Jcombobox
        sGbc.gridy = 1;
        sGbc.gridx = 1;
        sGbc.anchor = GridBagConstraints.CENTER;
        sGbc.insets = new Insets(15,0,0,5);

        containsBox = new JComboBox<>();
        containsBox.addItem("contains");
        containsBox.addItem("exact");
        containsBox.addItem("suggest");
        leftPanel.add(containsBox, sGbc);

        //adding search bar
        sGbc.gridy = 1;
        sGbc.fill = GridBagConstraints.HORIZONTAL;
        sGbc.gridx = 2;
        sGbc.gridwidth = 2;
        sGbc.anchor = GridBagConstraints.CENTER;
        sGbc.insets = new Insets(15,0,0,10);
        searchBar = new JTextField(25);
        leftPanel.add(searchBar, sGbc);

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
        JLabel descLabel = new JLabel("Description:");
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
            getSearchResults();
        } else if (evt.getSource() == addButton) {
            setAnnotationDescAndIdentifier();
        } else if (evt.getSource() == oKButton) {
            this.dispose();
        }
    }

    private void getSearchResults() {
        DefaultListModel<String> dlm = new DefaultListModel<>();
        String searchTerm = searchBar.getText();
        BioPortalSearch bioPortalSearch = new BioPortalSearch();

//        String bioPortalDatabases = Objects.equals(ontologiesBox.getSelectedItem(), "All Selected") ?
//                "GO,NCIT,CHEBI,FMA,BTO" : Objects.requireNonNull(ontologiesBox.getSelectedItem()).toString();
        MiriamManager.DataType dataType = (MiriamManager.DataType) annotationsPanel.getJComboBoxURI().getSelectedItem();
        String bioPortalDatabases = dataType.getDataTypeName();

        boolean exactMatch = Objects.equals(containsBox.getSelectedItem(), "exact");

        int pageSize = Integer.parseInt(Objects.requireNonNull(limitBox.getSelectedItem()).toString());

        try {
            searchElements = bioPortalSearch.search(searchTerm,pageSize,bioPortalDatabases,exactMatch);
            if (!searchElements.isEmpty()) {
                list.setEnabled(true);
                list.setFont(new Font("TimesRoman",Font.PLAIN,13));
                for (searchElement element: searchElements) {
                    dlm.addElement(element.getEntityName());
                }
            } else {
                list.setFont(new Font("Arial", Font.BOLD, 14));
                dlm.addElement("No matches found");
                list.setEnabled(false);
            }
            list.setModel(dlm);
        } catch (IOException | SAXException | URISyntaxException | ParserConfigurationException |
                 InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void getDescription() {
        if (!list.isSelectionEmpty()) {
            annotDescription = searchElements.get(list.getSelectedIndex()).toString();
            descriptionArea.setText(annotDescription);
            descriptionArea.setCaretPosition(0);
        }
    }

    private void setAnnotationDescAndIdentifier() {
        if (!list.isSelectionEmpty()) {
            annotationsPanel.addToAnnotationTextArea(annotDescription);
        }
        annotationsPanel.addIdentifier();
    }

}
