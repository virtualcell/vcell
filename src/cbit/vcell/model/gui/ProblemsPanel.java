package cbit.vcell.model.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.vcell.util.Issue;

import cbit.vcell.model.Model;

public class ProblemsPanel extends JPanel {
	private Model model = null;
	private JTextArea problemsTextArea = null;
	private JScrollPane scrollPane = null;
	private JButton refreshButton = null;
	EventHandler eventHandler = new EventHandler();
	
	class EventHandler implements ActionListener, PropertyChangeListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == ProblemsPanel.this.getRefreshButton()) 
				refreshButton_actionPerformed(e);
		}
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == ProblemsPanel.this && (evt.getPropertyName().equals("model"))) {
				// setModel(evt.get)
			}
		};
	}

	public ProblemsPanel() {
		super();
		initialize();
	}
	
	public void cleanupOnClose() {
		setModel(null);
	}

	public JTextArea getProblemsTextArea() {
		if (problemsTextArea == null) {
			problemsTextArea = new JTextArea();
		}
		return problemsTextArea;
	}
	
	public JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getProblemsTextArea());
		}
		return scrollPane;
	}
	
	public JButton getRefreshButton() {
		if (refreshButton == null) {
			refreshButton = new JButton("Refresh");
		}
		return refreshButton;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model argModel) {
		cbit.vcell.model.Model oldValue = model;
		model = argModel;
		firePropertyChange("model", oldValue, argModel);
	}

	public void refreshButton_actionPerformed(ActionEvent evt) {
		Vector<Issue> issueList = new Vector<Issue>();
		model.gatherIssues(issueList);
		StringBuffer buffer = new StringBuffer();
		for (int i=0;i<issueList.size();i++){
			buffer.append(issueList.elementAt(i)+"\n");
		}
		getProblemsTextArea().setText(buffer.toString());
	}
	
	private void initConnections() throws java.lang.Exception {
		getScrollPane().addPropertyChangeListener(eventHandler);
		getRefreshButton().addActionListener(eventHandler);
	}

	private void initialize() {
		try {
			setName("ProblemsPanel");
			setSize(456, 539);
			setLayout(new BorderLayout());
			JPanel p = new JPanel();
			p.add(getRefreshButton());
			add(p,BorderLayout.NORTH);
			add(getScrollPane(),BorderLayout.CENTER);

			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			System.out.println("--------- UNCAUGHT EXCEPTION --------- in ProblemsPanel");
			ivjExc.printStackTrace(System.out);
		}
	}
}
