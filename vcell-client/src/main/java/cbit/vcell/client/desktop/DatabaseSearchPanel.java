/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.EventListenerList;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelChildSummary;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.generic.DatePanel;

import cbit.gui.TextFieldAutoCompletion;
import cbit.vcell.client.desktop.DatabaseSearchPanel.SearchCriterion;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.model.DBFormalSpecies;
import cbit.vcell.model.FormalSpeciesType;
import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public class DatabaseSearchPanel extends CollapsiblePanel {
	public static interface SearchCriterion {
		boolean meetCriterion(VCDocumentInfo docInfo);
	}	
	static class SearchByString implements SearchCriterion {
		private String stringPattern = null;
		
		public SearchByString(String np) {
			stringPattern = np;
		}

		public boolean meetCriterion(VCDocumentInfo docInfo) {
			if (stringPattern == null || stringPattern.trim().length() == 0) { // no constraints
				return true;
			}
			String lowerCaseNamePattern = stringPattern.toLowerCase();
			if (docInfo.getVersion().getOwner().getName().toLowerCase().contains(lowerCaseNamePattern)) {
				return true;
			}
			if (docInfo.getVersion().getName().toLowerCase().contains(lowerCaseNamePattern)) {
				return true;
			}
			if (docInfo.getSoftwareVersion() != null && docInfo.getSoftwareVersion().getDescription() != null 
					&& docInfo.getSoftwareVersion().getDescription().toLowerCase().contains(lowerCaseNamePattern)) {
				return true;
			}
			if (docInfo instanceof BioModelInfo) {
				BioModelChildSummary bioModelChildSummary = ((BioModelInfo) docInfo).getBioModelChildSummary();
				if (bioModelChildSummary != null) {
					if (bioModelChildSummary.toDatabaseSerialization().toLowerCase().contains(lowerCaseNamePattern)) {
						return true;
					}
				}
			}
			if (docInfo instanceof MathModelInfo) {
				MathModelChildSummary mathModelChildSummary = ((MathModelInfo) docInfo).getMathModelChildSummary();
				if (mathModelChildSummary != null) {
					if (mathModelChildSummary.toDatabaseSerialization().toLowerCase().contains(lowerCaseNamePattern)) {
						return true;
					}
				}
			}
			if(docInfo.getPublicationInfos().length > 0) {	// search in the publication info
				for(PublicationInfo pi : docInfo.getPublicationInfos()) {
					if(pi.getTitle() != null && pi.getTitle().length() > 0) {
						if(pi.getTitle().toLowerCase().contains(lowerCaseNamePattern)) {
							return true;
						}
					}
					if(pi.getAuthors().length > 0) {
						for(String author : pi.getAuthors()) {
							if(author.toLowerCase().contains(lowerCaseNamePattern)) {
								return true;
							}
						}
					}
					if(pi.getCitation() != null && pi.getCitation().length() > 0) {
						if(pi.getCitation().toLowerCase().contains(lowerCaseNamePattern)) {
							return true;
						}
					}
					if(pi.getPubmedid() != null && pi.getPubmedid().length() > 0) {
						if(pi.getPubmedid().toLowerCase().contains(lowerCaseNamePattern)) {
							return true;
						}
						if("PubMed PMID:".toLowerCase().contains(lowerCaseNamePattern)) {
							return true;
						}
					}
					if(pi.getDoi() != null && pi.getDoi().length() > 0) {
						if(pi.getDoi().toLowerCase().contains(lowerCaseNamePattern)) {
							return true;
						}
						if("DOI:".toLowerCase().contains(lowerCaseNamePattern)) {
							return true;
						}
					}
					
				}
			}
			return false;
		}
	}
	
	static class SearchByDate implements SearchCriterion {
		private Date startDate = null;
		private Date endDate = null;
		
		public SearchByDate(Date sd, Date ed) {
			startDate = sd;
			endDate = ed;
		}

		public boolean meetCriterion(VCDocumentInfo docInfo) {
			
			Date versionDate = docInfo.getVersion().getDate();	
			Date newEndDate = new Date(endDate.getTime() + 24*3600*1000);	// add one day to end date
			if (versionDate.compareTo(startDate) >= 0 && versionDate.compareTo(newEndDate) <= 0) {
				return true;
			}
			return false;
		}
	}
	
	static class SearchBySpeciesName implements SearchCriterion {
		private String speciesNamePattern;
		private ArrayList<VCDocumentInfo> matchedVCDocumentInfos;
		public SearchBySpeciesName(String speciesNamePattern){
			if(speciesNamePattern == null || speciesNamePattern.length() == 0){
				throw new IllegalArgumentException("SearchBySpeciesName search string cannot be empty.");	
			}
			this.speciesNamePattern = speciesNamePattern;
		}
		public boolean meetCriterion(VCDocumentInfo docInfo) {
			if(matchedVCDocumentInfos != null && matchedVCDocumentInfos.contains(docInfo)){
				return true;
			}
			return false;
		}
		public void initializeSearch(DocumentManager documentManager) throws DataAccessException{
			FormalSpeciesType.MatchSearchFormalSpeciesType matchSearchFormalSpeciesType = FormalSpeciesType.speciesMatchSearch;
			StringTokenizer st = new StringTokenizer(speciesNamePattern, " ");
			ArrayList<String> matchCriterias = new ArrayList<String>();
			while(st.hasMoreTokens()){
				String namePattern = st.nextToken();
				matchCriterias.add(BeanUtils.convertToSQLSearchString(namePattern));
			}
			matchSearchFormalSpeciesType.setSQLMatchCriteria(matchCriterias.toArray(new String[0]));
			DBFormalSpecies[] matchedVCDocumentsFromSearchs =
				documentManager.getDatabaseSpecies(null,false/*bound not used*/,matchSearchFormalSpeciesType, 0/*restrict not used*/, -1, true);
			if(matchedVCDocumentsFromSearchs != null && matchedVCDocumentsFromSearchs.length > 0){
				matchedVCDocumentInfos = ((DBFormalSpecies.MatchedVCDocumentsFromSearch)matchedVCDocumentsFromSearchs[0]).getMatchedVCDocumentInfos();
			}
		}
	}
	public static final String SEARCH_SHOW_ALL_COMMAND = "showall";
	public static final String SEARCH_Command = "search";
	public static final String Search_Doc_Type[] = {"BioModel", "MathModel", "Geometri"};
	
	private EventListenerList listenerList = new EventListenerList();
	private SearchEventHandler searchEventHandler = new SearchEventHandler();
	private static Set<String> searchWordSet = new HashSet<String>();
	private static Set<TextFieldAutoCompletion> textFieldAutoComSet = new HashSet<TextFieldAutoCompletion>();
	private TextFieldAutoCompletion nameSearchTextField = null;
	private JButton searchButton = null;
	private JButton cancelButton = null;
	private JLabel advancedButton = null;
	private DatePanel startDatePanel = null;
	private DatePanel endDatePanel = null;
	private ArrayList<JComponent> advancedOptions = new ArrayList<JComponent>();
	private JTextField textFieldSpeciesName;
	private JLabel lblSpeciesName;
	private boolean bEnableSpeciesSearch = false;
	private JCheckBox chckbxHasSpatial;

	class SearchEventHandler extends MouseAdapter implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {	
			if (e.getSource() == searchButton || e.getSource() == nameSearchTextField) {		
				String name = nameSearchTextField.getText();
				if (name != null && name.trim().length() >= 0) {
					addSearchWord(name.trim());
				}
			} else if (e.getSource() == cancelButton) {
				nameSearchTextField.setText(null);
			}
			fireActionPerformed(e);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (advancedOptions.size() == 0) {
				return;
			}
			if (e.getSource() == advancedButton) {
				boolean bVisible = !advancedOptions.get(0).isVisible();
				if (bVisible) {
					advancedButton.setText("<html><u>Advanced &lt;&lt;</u></html>");
				} else {
					advancedButton.setText("<html><u>Advanced &gt;&gt;</u></html>");
				}
				setAdvancedOptionsVisible(bVisible);
			}
		}
	}
	public DatabaseSearchPanel() {
		super("Search", true);
		initialize();
		setAdvancedOptionsVisible(false);
	}

	public void addActionListener(ActionListener l) {
		 listenerList.add(ActionListener.class, l);
	}
	
	private void setAdvancedOptionsVisible(boolean bVisible) {
		for (JComponent comp : advancedOptions) {
			if(comp == lblSpeciesName || comp == textFieldSpeciesName){
				comp.setVisible(bVisible && bEnableSpeciesSearch);
			}else{
				comp.setVisible(bVisible);
			}
		}
		revalidate();
	}
	
	public ArrayList<SearchCriterion> getSearchCriterionList(DocumentManager documentManager) throws DataAccessException{
		ArrayList<SearchCriterion> searchCriterionList = null;
		if (isVisible()) {
			searchCriterionList = new ArrayList<SearchCriterion>();
			String namePattern = nameSearchTextField.getText();
			if (namePattern != null && namePattern.trim().length() >= 0) {
				StringTokenizer tokens = new StringTokenizer(namePattern," ");
				while (tokens.hasMoreTokens()){
					SearchByString nameCriterion = new SearchByString(tokens.nextToken());
					searchCriterionList.add(nameCriterion);
				}
			}
			
			if (startDatePanel.isVisible()) {
				Date startDate = startDatePanel.getDate();
				Date endDate = endDatePanel.getDate();
				SearchByDate dateScn = new SearchByDate(startDate, endDate);
				searchCriterionList.add(dateScn);
			}
			if(hasRemoteDatabaseSearchDefined()){
				SearchBySpeciesName searchBySpeciesName = new SearchBySpeciesName(textFieldSpeciesName.getText().trim());
				searchBySpeciesName.initializeSearch(documentManager);
				searchCriterionList.add(searchBySpeciesName);
			}
		}
		if(chckbxHasSpatial.isSelected()) {
			SearchCriterion sc = new SearchCriterion() {
				@Override
				public boolean meetCriterion(VCDocumentInfo docInfo) {
					if(docInfo instanceof BioModelInfo) {
						BioModelChildSummary bmcs = ((BioModelInfo)docInfo).getBioModelChildSummary();
						for(int i=0;bmcs != null && bmcs.getGeometryDimensions() != null && i<bmcs.getGeometryDimensions().length;i++) {
							if(bmcs.getGeometryDimensions()[i] > 0){
								return true;
							}
						}
					}else if(docInfo instanceof MathModelInfo) {
						MathModelChildSummary mmcs = ((MathModelInfo)docInfo).getMathModelChildSummary();
						if(mmcs != null && mmcs.getGeometryDimension()>0) {
							return true;
						}
					}
					return false;
				}};
			searchCriterionList.add(sc);
		}
		
		return searchCriterionList;
	}
	
	public boolean hasRemoteDatabaseSearchDefined(){
		return textFieldSpeciesName.isVisible() && textFieldSpeciesName.getText() != null && textFieldSpeciesName.getText().trim().length()>0;
	}
	private void initialize() {
//		JLabel nameLabel = new JLabel("Search ");
		nameSearchTextField = new TextFieldAutoCompletion();
		nameSearchTextField.putClientProperty("JTextField.variant", "search");
		textFieldAutoComSet.add(nameSearchTextField);
		
		advancedButton = new JLabel("<html><u>Advanced &gt;&gt;</u></html>");
		advancedButton.setFont(advancedButton.getFont().deriveFont(advancedButton.getFont().getSize2D() - 1));
		advancedButton.setForeground(Color.blue);
		advancedButton.setVerticalAlignment(SwingConstants.BOTTOM);
		
		JLabel dateLabel1 = new JLabel("Modified between");
		JLabel dateLabel2 = new JLabel("and");
		advancedOptions.add(dateLabel1);
		advancedOptions.add(dateLabel2);
		startDatePanel = new DatePanel();
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, -7);
		startDatePanel.setCalendar(cal);
		advancedOptions.add(startDatePanel);
		endDatePanel = new DatePanel();
		advancedOptions.add(endDatePanel);
		
		searchButton = new JButton("Search");		
		searchButton.setActionCommand(SEARCH_Command);
		cancelButton = new JButton("Show All");
		cancelButton.setActionCommand(SEARCH_SHOW_ALL_COMMAND);
		 
		JPanel mainPanel = new JPanel();
		GridBagLayout gbl_mainPanel = new GridBagLayout();
		gbl_mainPanel.columnWeights = new double[]{0.0, 1.0};
		mainPanel.setLayout(gbl_mainPanel);

		// 0
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2, 5, 5, 0);
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(nameSearchTextField, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 5, 5, 0);
		gbc.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(advancedButton, gbc);

		chckbxHasSpatial = new JCheckBox("Has Spatial");
		GridBagConstraints gbc_chckbxHasSpatial = new GridBagConstraints();
		gbc_chckbxHasSpatial.anchor = GridBagConstraints.WEST;
		gbc_chckbxHasSpatial.insets = new Insets(0, 5,5,0);
		gbc_chckbxHasSpatial.gridx = 1;
		gbc_chckbxHasSpatial.gridy = gridy;
		gbc.anchor = GridBagConstraints.WEST;
		mainPanel.add(chckbxHasSpatial, gbc_chckbxHasSpatial);

		gridy ++;		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2, 5, 5, 5);
		gbc.anchor = GridBagConstraints.LINE_END;
		mainPanel.add(dateLabel1, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(2, 0, 5, 0);
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(startDatePanel, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2, 5, 5, 5);
		gbc.anchor = GridBagConstraints.LINE_END;
		mainPanel.add(dateLabel2, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(2, 0, 5, 0);
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(endDatePanel, gbc);			
		
		gridy ++;
		
		lblSpeciesName = new JLabel("Species Names:");
		GridBagConstraints gbc_lblSpeciesName = new GridBagConstraints();
		gbc_lblSpeciesName.anchor = GridBagConstraints.EAST;
		gbc_lblSpeciesName.insets = new Insets(0, 0, 5, 5);
		gbc_lblSpeciesName.gridx = 0;
		gbc_lblSpeciesName.gridy = 4;
		mainPanel.add(lblSpeciesName, gbc_lblSpeciesName);
		advancedOptions.add(lblSpeciesName);
		lblSpeciesName.setVisible(bEnableSpeciesSearch);
		
		textFieldSpeciesName = new JTextField();
		GridBagConstraints gbc_textFieldSpeciesName = new GridBagConstraints();
		gbc_textFieldSpeciesName.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldSpeciesName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldSpeciesName.gridx = 1;
		gbc_textFieldSpeciesName.gridy = 4;
		mainPanel.add(textFieldSpeciesName, gbc_textFieldSpeciesName);
		textFieldSpeciesName.setColumns(10);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.insets = new Insets(10, 5, 0, 5);
		mainPanel.add(searchButton, gbc);
		advancedOptions.add(textFieldSpeciesName);
		textFieldSpeciesName.setVisible(bEnableSpeciesSearch);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.insets = new Insets(10, 5, 0, 0);
		gbc.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(cancelButton, gbc);

		JScrollPane scroll = new JScrollPane(mainPanel);		
		getContentPanel().setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.fill = GridBagConstraints.BOTH;
		getContentPanel().add(scroll, gbc);
		
		initConnections();
	}
	
	private void initConnections() {
		cancelButton.addActionListener(searchEventHandler);
		searchButton.addActionListener(searchEventHandler);
		advancedButton.addMouseListener(searchEventHandler);
		nameSearchTextField.addActionListener(searchEventHandler);
	}
	
	private void addSearchWord(String newWord) {
		if (searchWordSet.contains(newWord)) {
			return;
		}
		searchWordSet.add(newWord);
		for (TextFieldAutoCompletion tf : textFieldAutoComSet) {
			tf.setAutoCompletionWords(searchWordSet);
		}
	}
	
   private void fireActionPerformed(ActionEvent event) {
	   ActionEvent newEvent = new ActionEvent(this,
               ActionEvent.ACTION_PERFORMED,
               event.getActionCommand(),
               event.getWhen(),
               event.getModifiers());
	   
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                ((ActionListener)listeners[i+1]).actionPerformed(newEvent);
            }          
        }
    }
   public void enableSpeciesSearch(){
	   bEnableSpeciesSearch = true;
   }
   public void addCollapsiblePropertyChangeListener(PropertyChangeListener listener){
	   super.addPropertyChangeListener(listener);
   }
}
