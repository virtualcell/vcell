package cbit.vcell.desktop;

import java.awt.Component;
import java.awt.Font;
import java.text.DateFormat;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.desktop.biomodel.BioModelsNetModelInfo;
import cbit.vcell.desktop.ModelExplorerTree.Folder;
import cbit.vcell.desktop.ModelExplorerTree.Model;
import cbit.vcell.desktop.ModelExplorerTree.Owner;
import cbit.vcell.desktop.ModelExplorerTree.Publication;
import cbit.vcell.desktop.ModelExplorerTree.Section;

/**
 * The Model Explorer tree's entries, with the icons VCell's database tree has always given its folders: a model by its
 * kind, a version by when it was saved, an owner, a publication, and a BioModels Database model (dimmed if VCell can't
 * import it). Each model's tooltip is its Database File Info.
 */
@SuppressWarnings("serial")
public class ModelExplorerTreeRenderer extends DefaultTreeCellRenderer {
	private static final Map<Section, Icon> FOLDER_ICONS = new EnumMap<>(Section.class);
	private static final Icon OWNER_ICON = VCellIcons.makeIcon("/icons/userFolder.png");
	private static final Icon PUBLICATION_ICON = VCellIcons.makeIcon("/icons/publishedPublication.png");
	private static final Icon VERSION_ICON = VCellIcons.makeIcon("/icons/calendar.png");

	static {
		Icon folder = VCellIcons.makeIcon("/images/closedFolder_16x16.gif");
		Icon publicFolder = VCellIcons.makeIcon("/icons/publicFolder2.png");
		FOLDER_ICONS.put(Section.MY_MODELS, VCellIcons.makeIcon("/icons/selfFolder.png"));
		FOLDER_ICONS.put(Section.SHARED, VCellIcons.makeIcon("/icons/sharedFolder2.png"));
		FOLDER_ICONS.put(Section.PUBLIC, publicFolder);
		FOLDER_ICONS.put(Section.PUBLISHED, VCellIcons.makeIcon("/icons/publishedFolder.png"));
		FOLDER_ICONS.put(Section.CURATED, VCellIcons.makeIcon("/icons/curatedFolder.png"));
		FOLDER_ICONS.put(Section.MODEL_BRICKS, VCellIcons.makeIcon("/icons/bricksFolder.png"));
		FOLDER_ICONS.put(Section.UNCURATED, VCellIcons.makeIcon("/icons/publicFolder.png"));
		FOLDER_ICONS.put(Section.TUTORIALS, folder);
		FOLDER_ICONS.put(Section.GEOMETRIES, folder);
		FOLDER_ICONS.put(Section.MY_GEOMETRIES, VCellIcons.makeIcon("/icons/selfFolder.png"));
		FOLDER_ICONS.put(Section.SHARED_GEOMETRIES, VCellIcons.makeIcon("/icons/sharedFolder2.png"));
		FOLDER_ICONS.put(Section.PUBLIC_GEOMETRIES, publicFolder);
		FOLDER_ICONS.put(Section.BIOMODELS_DATABASE, VCellIcons.makeIcon("/icons/database.png"));
	}

	private VCDocumentInfo comparing = null; // the model another is being picked to compare with: marked in the tree

	/** Marks this model (and its version) in the tree, as the one being compared; null for none. */
	public void setComparing(VCDocumentInfo comparing) {
		this.comparing = comparing;
	}

	/** The icon of a folder of the tree, as the Model Explorer shows it. */
	public static Icon folderIcon(Section section) {
		return FOLDER_ICONS.get(section);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean focused) {
		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, focused);
		Object object = value instanceof DefaultMutableTreeNode node ? node.getUserObject() : value;
		this.setToolTipText(null);
		this.setFont(tree.getFont()); // not the last row's bold
		if (object instanceof Folder folder) {
			this.setIcon(FOLDER_ICONS.get(folder.getSection()));
		} else if (object instanceof Owner) {
			this.setIcon(OWNER_ICON);
		} else if (object instanceof Publication publication) {
			this.setIcon(PUBLICATION_ICON);
			this.setToolTipText(ModelListRenderer.wrapForToolTip(ModelExplorerTree.describe(publication.info())));
		} else if (object instanceof Model model) {
			this.setIcon(ModelListRenderer.iconFor(ModelListRenderer.getDocumentType(model.latest())));
			this.setToolTipText(ModelListRenderer.toolTipFor(model.latest()));
			if (this.isComparing(model.latest()) || model.versions().stream().anyMatch(this::isComparing)) this.markComparing(true);
		} else if (object instanceof VCDocumentInfo info) { // a version of the model above it
			Version version = info.getVersion();
			this.setIcon(VERSION_ICON);
			this.setText(null == version.getDate() ? version.getName()
					: DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(version.getDate()) + "  —  " + ModelListRenderer.permissionsOf(info));
			this.setToolTipText(ModelListRenderer.toolTipFor(info));
			if (this.isComparing(info)) this.markComparing(false); // its model says so
		} else if (object instanceof BioModelsNetModelInfo info) {
			this.setIcon(ModelListRenderer.iconFor(org.vcell.util.document.VCDocument.VCDocumentType.BIOMODEL_DOC));
			this.setText(info.getName());
			this.setToolTipText(info.getId() + (info.isSupported() ? "" : ": VCell can't import this model"));
			if (!info.isSupported() && !selected) this.setForeground(UIManager.getColor("Label.disabledForeground"));
		}
		return this;
	}

	private boolean isComparing(VCDocumentInfo info) {
		return null != this.comparing && this.comparing.getClass() == info.getClass()
				&& this.comparing.getVersion().getVersionKey().equals(info.getVersion().getVersionKey());
	}

	// In bold, and saying so (once, on the model): the model the one being picked will be compared with
	private void markComparing(boolean saySo) {
		this.setFont(this.getFont().deriveFont(Font.BOLD));
		if (saySo) this.setText(this.getText() + "   \u2014 comparing");
	}
}
