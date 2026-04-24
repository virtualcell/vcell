package cbit.vcell.client.data;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.List;
import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class CheckBoxScrollList extends JPanel {
	private static final Map<ListModel<String>, CheckBoxEntries> modelMapping = new HashMap<>();
	private ListModel<String> listModel = null;
	private final JList<String> allEntries = new JList<>();

	public CheckBoxScrollList(){
		this(new DefaultListModel<>());
	}

	public CheckBoxScrollList(ListModel<String> model){
		this.setLayout(new BorderLayout());
		this.processAndSetNewModel(model);
		this.allEntries.setCellRenderer(new CheckBoxRenderer()); // Private Class, see below
		this.allEntries.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.allEntries.setBounds(0, 0, 160, 120);
		this.add(new JScrollPane(this.allEntries), BorderLayout.CENTER);

		// Toggle checkbox on click
		this.allEntries.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Point clickPoint = e.getPoint();
				int index = CheckBoxScrollList.this.allEntries.locationToIndex(clickPoint);
				CheckBoxEntries cbe = CheckBoxScrollList.modelMapping.get(CheckBoxScrollList.this.listModel);
				int maxIndex = cbe.entryToCheckBoxMap.size();
				if (index < 0 || index >= maxIndex) return;
				// the above "locationToIndex" finds the closest index. We need to confirm it's *actually* the item.
				Rectangle cellBounds = CheckBoxScrollList.this.allEntries.getCellBounds(index, index);
				if (cellBounds == null || !cellBounds.contains(e.getPoint())) return;
				JCheckBox checkBox = cbe.getEntryCheckBox(index);
				checkBox.doClick();
				CheckBoxScrollList.this.allEntries.repaint();
			}
		});
	}

	private void processAndSetNewModel(@Nonnull ListModel<String> model){
		if (model == this.listModel) return;
		boolean isNewModel = !modelMapping.containsKey(model);
		if (isNewModel) CheckBoxScrollList.modelMapping.put(model, new CheckBoxEntries(model));
		this.listModel = model;
		this.allEntries.setModel(model);
	}

	public boolean hasItemsSelected(){
		return CheckBoxScrollList.modelMapping.get(this.listModel).hasItemsSelected();
	}

	public List<String> getCheckedItems() {
		return CheckBoxScrollList.modelMapping.get(this.listModel).getCheckedItems();
	}

	public void applyListModelToEntries(javax.swing.ListModel<String> model){
		this.processAndSetNewModel(model);
	}

	public void addListSelectionListener(java.beans.PropertyChangeListener newListener){
		this.addPropertyChangeListener(newListener);
	}

	// Renderer: draws each row as a JCheckBox
	private class CheckBoxRenderer implements ListCellRenderer<String> {
		@Override
		public Component getListCellRendererComponent(
				JList<? extends String> list, String value,
				int index, boolean isSelected, boolean cellHasFocus) {

			CheckBoxEntries entries = CheckBoxScrollList.modelMapping.get(CheckBoxScrollList.this.listModel);
			JCheckBox checkBox = entries.getEntryCheckBox(index);
			checkBox.setBackground(checkBox.isSelected() ? list.getSelectionBackground() : list.getBackground());
			checkBox.setForeground(checkBox.isSelected() ? list.getSelectionForeground() : list.getForeground());
			return checkBox;
		}
	}

	private class CheckBoxEntries{
		private final ListModel<String> model;
		private final Map<String, JCheckBox> entryToCheckBoxMap;
		private final Set<Integer> checkedIndices;

		public CheckBoxEntries(ListModel<String> model){
			this.model = model;
			this.entryToCheckBoxMap = new HashMap<>();
			this.checkedIndices = new HashSet<>();

			// Add Listener
			ListDataListener modelListDataListener = new ListDataListener() {
				@Override
				public void intervalAdded(ListDataEvent e) {
					CheckBoxEntries.this.buildFromScratch();
				}

				@Override
				public void intervalRemoved(ListDataEvent e) {
					CheckBoxEntries.this.buildFromScratch();
				}

				@Override
				public void contentsChanged(ListDataEvent e) {
					CheckBoxEntries.this.buildFromScratch();
				}
			};
			this.model.addListDataListener(modelListDataListener);

			// Update
			this.buildFromScratch();
		}

		public Set<Integer> getCheckedIndices() {
			return this.checkedIndices;
		}

		public Map<String, JCheckBox> getEntryToCheckBoxMap() {
			return this.entryToCheckBoxMap;
		}

		public JCheckBox getEntryCheckBox(int index){
			if (index >= 0 && index < this.model.getSize()) return this.entryToCheckBoxMap.get(this.model.getElementAt(index));
			throw new IndexOutOfBoundsException(String.format("Index %d out of bounds for length %d", index, this.model.getSize()));
		}

		public boolean hasItemsSelected(){
			return !this.checkedIndices.isEmpty();
		}

		public List<String> getCheckedItems() {
			return this.checkedIndices.stream().sorted().map(this.model::getElementAt).toList();
		}

		private void buildFromScratch(){
			this.entryToCheckBoxMap.clear();
			this.checkedIndices.clear();

			for (int i = 0; i < this.model.getSize(); i++){
				final int indexCopy = i;
				String elem = this.model.getElementAt(i);
				JCheckBox newCheckBox = new JCheckBox(elem);
				newCheckBox.addActionListener(e -> {
					if (!(e.getSource() instanceof JCheckBox jcb)) return;
					boolean isSelected = jcb.isSelected();
					if (isSelected) CheckBoxEntries.this.checkedIndices.add(indexCopy);
					else CheckBoxEntries.this.checkedIndices.remove(indexCopy);
					CheckBoxScrollList.this.firePropertyChange(String.format("CheckBox[%s]::isSelected", jcb.getText()), !isSelected, isSelected);
				});
				this.entryToCheckBoxMap.put(elem, newCheckBox);
			}
		}

	}
}
