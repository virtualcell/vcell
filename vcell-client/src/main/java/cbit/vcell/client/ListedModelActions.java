package cbit.vcell.client;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.CurateSpec;
import cbit.vcell.geometry.GeometryInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;

import cbit.vcell.clientdb.DocumentManager;

/**
 * The right-click menu of a saved model listed in the welcome window: what the database browser in model windows
 * offered (open, compare, permissions, archive, publish, delete, copy its name), plus export and copying its ID number.
 * <br/>
 * Items that don't apply stay in the menu, disabled, with a tooltip saying why, so the menu reads the same for every
 * model. The rules for owner-only items are the database browser's.
 */
public class ListedModelActions {

	/**
	 * The database browser's actions, without the browser: the listed model is its selection, and the welcome window
	 * owns its dialogs. Most of these actions read the selection later, from background tasks, so it stays set until
	 * the next model is chosen, as a browser selection did.
	 */
	private static final class ListedModelManager extends DatabaseWindowManager {
		private final TopLevelWindowManager owner;
		private VCDocumentInfo listedModel = null;

		ListedModelManager(RequestManager requestManager, TopLevelWindowManager owner) {
			super(null, requestManager);
			this.owner = owner;
		}

		@Override
		public VCDocumentInfo getPanelSelection() {
			return this.listedModel;
		}

		@Override
		public Component getComponent() {
			return this.owner.getComponent();
		}

		// the window an action blocks while it runs (export, compare): the lists' own, which has no database window of its own
		@Override
		public String getManagerID() {
			return this.owner.getManagerID();
		}
	}

	private final TopLevelWindowManager owner;
	private final Consumer<VCDocumentInfo> compareAnotherModel; // Compare With > Another Model, for the model; null: a picker dialog
	private ListedModelManager manager = null; // made on first use: it builds database trees of its own

	/** @param owner the window the lists are in, whose dialogs the actions' dialogs belong to */
	public ListedModelActions(TopLevelWindowManager owner) {
		this(owner, null);
	}

	/**
	 * As above; Compare With > Another Model does <code>compareAnotherModel</code> for the model (the Model Explorer, to
	 * pick the other model in), rather than ask for the other model in a dialog.
	 */
	public ListedModelActions(TopLevelWindowManager owner, Consumer<VCDocumentInfo> compareAnotherModel) {
		this.owner = owner;
		this.compareAnotherModel = compareAnotherModel;
	}

	/** The menu for one listed model; <code>open</code> is how the list opens a model (as a double-click does). */
	public JPopupMenu createMenu(VCDocumentInfo model, Runnable open) {
		return this.createMenu(model, open, null);
	}

	/** As above, with "Show in Model Explorer" after Open, which <code>explore</code> does (none, if null). */
	public JPopupMenu createMenu(VCDocumentInfo model, Runnable open, Runnable explore) {
		JPopupMenu menu = new JPopupMenu();
		menu.setName("ListedModelMenu");
		Version version = model.getVersion();
		boolean owned = this.isOwnedByUser(version);
		boolean published = version.getFlag().compareEqual(VersionFlag.Published);
		boolean archived = version.getFlag().compareEqual(VersionFlag.Archived);
		String typeName = ListedModelActions.documentTypeName(model);

		menu.add(ListedModelActions.item("Open", open));
		if (null != explore) menu.add(ListedModelActions.item("Show in Model Explorer", explore));
		menu.add(ListedModelActions.item("Export / Save to File...", () -> this.withListedModel(model).exportDocument()));
		if (model instanceof GeometryInfo) {
			menu.add(ListedModelActions.item("Models Using Geometry", () -> this.withListedModel(model).findModelsUsingSelectedGeometry()));
		} else {
			menu.add(this.createCompareMenu(model));
		}

		menu.addSeparator();
		menu.add(ListedModelActions.item("Copy Name", () -> ListedModelActions.copyToClipboard(version.getName())));
		menu.add(ListedModelActions.item("Copy " + typeName + " ID Number", () -> ListedModelActions.copyToClipboard(ListedModelActions.idNumber(model))));

		menu.addSeparator();
		String notYours = "Only for your own " + typeName + "s";
		JMenuItem permissions = ListedModelActions.item("Permissions...", () -> this.manager().accessPermissions(this.owner.getComponent(), model, false));
		ListedModelActions.enable(permissions, owned && !published, owned ? "Not for a published " + typeName : notYours);
		menu.add(permissions);
		JMenuItem archive = ListedModelActions.item("Archive", () -> this.owner.getRequestManager().curateDocument(model, CurateSpec.ARCHIVE, this.owner));
		ListedModelActions.enable(archive, owned && !archived && !published, owned ? "Already " + (published ? "published" : "archived") : notYours);
		menu.add(archive);
		JMenuItem publish = ListedModelActions.item("Publish", () -> this.owner.getRequestManager().curateDocument(model, CurateSpec.PUBLISH, this.owner));
		ListedModelActions.enable(publish, owned, notYours);
		menu.add(publish);
		JMenuItem delete = ListedModelActions.item("Delete...", () -> this.owner.getRequestManager().deleteDocument(model, this.owner)); // asks first
		ListedModelActions.enable(delete, owned && !archived && !published, owned ? "Not for a published or archived " + typeName : notYours);
		menu.add(delete);
		return menu;
	}

	/** The number in a model's ID: its version key, as in "biomodel-233648465". */
	public static String idNumber(VCDocumentInfo model) {
		return model.getVersion().getVersionKey().toString();
	}

	/** "BioModel", "MathModel" or "Geometry". */
	public static String documentTypeName(VCDocumentInfo model) {
		if (model instanceof BioModelInfo) return "BioModel";
		if (model instanceof MathModelInfo) return "MathModel";
		return "Geometry";
	}

	// Compare With: other editions of this model, when there are some, or another model
	private JMenu createCompareMenu(VCDocumentInfo model) {
		JMenu compare = new JMenu("Compare With");
		List<Date> otherEditions = this.otherEditionDates(model);
		Date date = model.getVersion().getDate();
		boolean olderEdition = otherEditions.stream().anyMatch(other -> other.before(date));
		boolean newerEdition = otherEditions.stream().anyMatch(other -> other.after(date));

		JMenuItem previous = ListedModelActions.item("Previous Edition", () -> this.withListedModel(model).comparePreviousEdition());
		ListedModelActions.enable(previous, olderEdition, "No earlier edition");
		compare.add(previous);
		JMenuItem latest = ListedModelActions.item("Latest Edition", () -> this.withListedModel(model).compareLatestEdition());
		ListedModelActions.enable(latest, newerEdition, "This is the latest edition");
		compare.add(latest);
		JMenuItem anotherEdition = ListedModelActions.item("Another Edition...", () -> this.withListedModel(model).compareAnotherEdition());
		ListedModelActions.enable(anotherEdition, !otherEditions.isEmpty(), "No other editions");
		compare.add(anotherEdition);
		compare.add(ListedModelActions.item("Another Model...", null != this.compareAnotherModel ? () -> this.compareAnotherModel.accept(model)
				: () -> this.withListedModel(model).compareAnotherModel()));
		return compare;
	}

	// the dates of the model's other saved editions (same branch), among what the user can see
	private List<Date> otherEditionDates(VCDocumentInfo model) {
		DocumentManager documentManager = this.owner.getRequestManager().getDocumentManager();
		List<Date> dates = new ArrayList<>();
		if (documentManager == null) return dates;
		VCDocumentInfo[] infos = model instanceof BioModelInfo ? documentManager.getBioModelInfos()
				: model instanceof MathModelInfo ? documentManager.getMathModelInfos()
				: documentManager.getGeometryInfos();
		Version version = model.getVersion();
		for (VCDocumentInfo info : infos) {
			Version other = info.getVersion();
			if (!version.getBranchID().equals(other.getBranchID())) continue;
			if (version.getVersionKey().equals(other.getVersionKey())) continue;
			dates.add(other.getDate());
		}
		return dates;
	}

	private boolean isOwnedByUser(Version version) {
		DocumentManager documentManager = this.owner.getRequestManager().getDocumentManager();
		User user = documentManager == null ? null : documentManager.getUser();
		return user != null && version.getOwner() != null && version.getOwner().compareEqual(user);
	}

	private ListedModelManager withListedModel(VCDocumentInfo model) {
		ListedModelManager listedModelManager = this.manager();
		listedModelManager.listedModel = model;
		return listedModelManager;
	}

	private ListedModelManager manager() {
		if (this.manager == null) this.manager = new ListedModelManager(this.owner.getRequestManager(), this.owner);
		return this.manager;
	}

	private static JMenuItem item(String label, Runnable action) {
		JMenuItem item = new JMenuItem(label);
		item.setName(label.replaceAll("[^A-Za-z]", "") + "MenuItem");
		item.addActionListener(e -> action.run());
		return item;
	}

	private static void enable(JMenuItem item, boolean enabled, String whyNot) {
		item.setEnabled(enabled);
		item.setToolTipText(enabled ? null : whyNot);
	}

	private static void copyToClipboard(String text) {
		StringSelection selection = new StringSelection(text);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
	}
}
