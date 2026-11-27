package cbit.vcell.desktop;

import java.awt.Component;
import java.awt.Font;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;

import cbit.vcell.desktop.ModelExplorerTree.Section;
import cbit.vcell.desktop.ModelExplorerTree.TableRow;

/**
 * The Model Explorer's table: the tree's models, each once, without its folders' hierarchy; a column each for what the
 * tree arranges them by (folder, owner, publication) and more, each sortable as its kind of value (dates by date,
 * counts by number).
 */
@SuppressWarnings("serial")
public class ModelExplorerTableModel extends AbstractTableModel {

	/** The table's columns. */
	public enum Column {
		NAME("Name", String.class),
		TYPE("Type", String.class),
		FOLDERS("Folders", String.class),
		OWNER("Owner", String.class),
		SAVED("Saved", Date.class),
		VERSIONS("Versions", Integer.class),
		PERMISSIONS("Permissions", String.class),
		PUBLICATION("Publication", String.class),
		VCELL_ID("VCell ID", Long.class);

		public final String title;
		public final Class<?> type;

		Column(String title, Class<?> type) {
			this.title = title;
			this.type = type;
		}
	}

	private List<TableRow> rows = new ArrayList<>();

	/** Lists these rows, in place of those listed. */
	public void setRows(List<TableRow> rows) {
		this.rows = new ArrayList<>(rows);
		this.fireTableDataChanged();
	}

	public TableRow getRow(int modelRow) {
		return this.rows.get(modelRow);
	}

	public List<TableRow> getRows() {
		return List.copyOf(this.rows);
	}

	@Override public int getRowCount() { return this.rows.size(); }
	@Override public int getColumnCount() { return Column.values().length; }
	@Override public String getColumnName(int column) { return Column.values()[column].title; }
	@Override public Class<?> getColumnClass(int column) { return Column.values()[column].type; }

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return ModelExplorerTableModel.valueOf(this.rows.get(rowIndex), Column.values()[columnIndex]);
	}

	/** A row's value in a column, as the table sorts it. */
	static Object valueOf(TableRow row, Column column) {
		VCDocumentInfo latest = row.latest();
		Version version = latest.getVersion();
		return switch (column) {
			case NAME -> version.getName();
			case TYPE -> ModelListRenderer.typeName(ModelListRenderer.getDocumentType(latest));
			case FOLDERS -> row.folders().stream().map(ModelExplorerTableModel::folderName).collect(Collectors.joining(", "));
			case OWNER -> null == version.getOwner() ? "" : version.getOwner().getName();
			case SAVED -> version.getDate();
			case VERSIONS -> Math.max(1, row.model().versions().size());
			case PERMISSIONS -> ModelListRenderer.permissionsOf(latest);
			case PUBLICATION -> {
				PublicationInfo[] publications = latest.getPublicationInfos();
				yield null == publications || 0 == publications.length ? "" : ModelExplorerTree.describe(publications[0]);
			}
			case VCELL_ID -> null == version.getVersionKey() ? null : Long.valueOf(version.getVersionKey().toString());
		};
	}

	// A folder as the table names it: the shared ones say whose ("Shared With Me" is both models' and geometries')
	private static String folderName(Section section) {
		return switch (section) {
			case SHARED_GEOMETRIES -> "Shared Geometries";
			default -> section.title;
		};
	}

	/**
	 * Draws the name with its kind's icon (in bold, while it's the model being compared), and dates as dates; rows
	 * striped and selected as VCell's other tables are.
	 */
	public static class Renderer extends DefaultScrollTableCellRenderer {
		private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
		private VCDocumentInfo comparing = null;

		public Renderer() {
			this.disableUneditableForeground(); // nothing here is edited: the text in the table's own color, not marked read-only
		}

		/** Marks this model's row, as the one being compared; null for none. */
		public void setComparing(VCDocumentInfo comparing) {
			this.comparing = comparing;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
			Object shown = value instanceof Date date ? this.dateFormat.format(date) : value;
			super.getTableCellRendererComponent(table, shown, selected, focused, row, column);
			this.setIcon(null);
			this.setFont(table.getFont());
			this.setHorizontalAlignment(value instanceof Number ? JLabel.TRAILING : JLabel.LEADING);
			TableRow tableRow = ((ModelExplorerTableModel) table.getModel()).getRow(table.convertRowIndexToModel(row));
			if (Column.NAME == Column.values()[table.convertColumnIndexToModel(column)]) {
				this.setIcon(ModelListRenderer.iconFor(ModelListRenderer.getDocumentType(tableRow.latest())));
				this.setToolTipText(ModelListRenderer.toolTipFor(tableRow.latest()));
			} else {
				this.setToolTipText(null);
			}
			if (this.isComparing(tableRow)) this.setFont(table.getFont().deriveFont(Font.BOLD));
			return this;
		}

		private boolean isComparing(TableRow row) {
			if (null == this.comparing) return false;
			for (VCDocumentInfo version : row.model().versions().isEmpty() ? List.of(row.latest()) : row.model().versions()) {
				if (this.comparing.getClass() == version.getClass()
						&& this.comparing.getVersion().getVersionKey().equals(version.getVersion().getVersionKey())) return true;
			}
			return this.comparing.getVersion().getVersionKey().equals(row.latest().getVersion().getVersionKey());
		}
	}
}
