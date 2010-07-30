package cbit.vcell.client.desktop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.EventListenerList;

import org.vcell.util.document.VCDocumentInfo;

import cbit.gui.TextFieldAutoCompletion;
import cbit.vcell.messaging.admin.DatePanel;

public class DatabaseSearchPanel extends JPanel {
	public static interface SearchCriterion {
		boolean meetCriterion(VCDocumentInfo docInfo);
	}	
	static class SearchByName implements SearchCriterion {
		private String namePattern = null;
		
		public SearchByName(String np) {
			namePattern = np;
		}

		public boolean meetCriterion(VCDocumentInfo docInfo) {
			if (namePattern == null || namePattern.trim().length() == 0) { // no constraints
				return true;
			}
			if (docInfo.getVersion().getName().toLowerCase().indexOf(namePattern.toLowerCase()) >= 0) {
				return true;
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
	private int docType = 0;
	private DatePanel startDatePanel = null;
	private DatePanel endDatePanel = null;
	private ArrayList<JComponent> advancedOptions = new ArrayList<JComponent>();
	
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
	public DatabaseSearchPanel(int ti) {
		super();
		docType = ti;
		initialize();
		setAdvancedOptionsVisible(false);
	}

	public void addActionListener(ActionListener l) {
		 listenerList.add(ActionListener.class, l);
	}
	
	private void setAdvancedOptionsVisible(boolean bVisible) {
		for (JComponent comp : advancedOptions) {
			comp.setVisible(bVisible);
		}
	}
	
	public ArrayList<SearchCriterion> getSearchCriterionList() {
		ArrayList<SearchCriterion> searchCriterionList = null;
		if (isVisible()) {
			searchCriterionList = new ArrayList<SearchCriterion>();
			String namePattern = nameSearchTextField.getText();
			if (namePattern != null && namePattern.trim().length() >= 0) {
				SearchByName nameCriterion = new SearchByName(namePattern);
				searchCriterionList.add(nameCriterion);
			}
			
			if (startDatePanel.isVisible()) {
				Date startDate = startDatePanel.getDate();
				Date endDate = endDatePanel.getDate();
				SearchByDate dateScn = new SearchByDate(startDate, endDate);
				searchCriterionList.add(dateScn);
			}
		}
		return searchCriterionList;
	}
	
	private void initialize() {
		JLabel nameLabel = new JLabel("Name Containing : ");
		nameSearchTextField = new TextFieldAutoCompletion();
		textFieldAutoComSet.add(nameSearchTextField);
		
		advancedButton = new JLabel("<html><u>Advanced &gt;&gt;</u></html>");
		advancedButton.setFont(advancedButton.getFont().deriveFont(advancedButton.getFont().getSize2D() - 1));
		advancedButton.setForeground(Color.blue);
		advancedButton.setVerticalAlignment(SwingConstants.BOTTOM);
		
		JLabel dateLabel = new JLabel("Modified Between : ");
		advancedOptions.add(dateLabel);
		startDatePanel = new DatePanel();
		advancedOptions.add(startDatePanel);
		JLabel andLabel = new JLabel("and");
		advancedOptions.add(andLabel);
		endDatePanel = new DatePanel();
		advancedOptions.add(endDatePanel);
		
		searchButton = new JButton("Search " + Search_Doc_Type[docType] + "s");		
		searchButton.setActionCommand(SEARCH_Command);
		cancelButton = new JButton("Show All");
		cancelButton.setActionCommand(SEARCH_SHOW_ALL_COMMAND);
		 
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());

		// 0
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 5, 0, 0);
		gbc.anchor = GridBagConstraints.LINE_END;
		mainPanel.add(nameLabel, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(nameSearchTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0, 5, 0, 0);
		gbc.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(advancedButton, gbc);
		
		// 1
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(2, 5, 0, 0);
		gbc.anchor = GridBagConstraints.LINE_END;
		mainPanel.add(dateLabel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(2, 0, 0, 0);
		mainPanel.add(startDatePanel, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.insets = new Insets(2, 5, 0, 5);
		mainPanel.add(andLabel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.insets = new Insets(2, 0, 0, 0);
		mainPanel.add(endDatePanel, gbc);			
		
		// 2
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(10, 5, 0, 0);
		mainPanel.add(searchButton, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.insets = new Insets(10, 5, 0, 0);
		mainPanel.add(cancelButton, gbc);

		JScrollPane scroll = new JScrollPane(mainPanel);		
		scroll.setViewportBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),"Search", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, getFont()));
		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);
		
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
}
