package cbit.vcell.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;

import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.geometry.GeometryInfo;

@Tag("Fast")
public class ListedModelActionsTest {
	private static final User ME = new User("me", new KeyValue("1"));
	private static final User SOMEONE_ELSE = new User("someone", new KeyValue("2"));

	private final List<BioModelInfo> bioModels = new ArrayList<>();
	private int nextKey = 100;

	@Test
	public void idNumberIsTheVersionKeyAlone() {
		BioModelInfo model = this.bioModel("FRAP", ListedModelActionsTest.ME, 1, 0, VersionFlag.Current);
		assertEquals(model.getVersion().getVersionKey().toString(), ListedModelActions.idNumber(model));
		assertFalse(ListedModelActions.idNumber(model).contains("biomodel"), "just the number");
	}

	@Test
	public void theCopyIdItemIsNamedForTheDocumentType() {
		assertNotNull(this.find(this.menuFor(this.bioModel("A", ListedModelActionsTest.ME, 1, 0, VersionFlag.Current)), "Copy BioModel ID Number"));
		MathModelInfo math = new MathModelInfo(this.version("M", ListedModelActionsTest.ME, 2, 0, VersionFlag.Current), new KeyValue("7"), (String) null, null);
		assertNotNull(this.find(this.menuFor(math), "Copy MathModel ID Number"));
	}

	@Test
	public void ownerOnlyItemsAreEnabledForYourOwnEditableModel() {
		JPopupMenu menu = this.menuFor(this.bioModel("Mine", ListedModelActionsTest.ME, 1, 0, VersionFlag.Current));
		for (String label : new String[] { "Permissions...", "Archive", "Publish", "Delete..." }) {
			assertTrue(this.find(menu, label).isEnabled(), label);
		}
	}

	@Test
	public void ownerOnlyItemsAreDisabledWithAReasonForSomeoneElsesModel() {
		JPopupMenu menu = this.menuFor(this.bioModel("Theirs", ListedModelActionsTest.SOMEONE_ELSE, 1, 0, VersionFlag.Current));
		for (String label : new String[] { "Permissions...", "Archive", "Publish", "Delete..." }) {
			JMenuItem item = this.find(menu, label);
			assertFalse(item.isEnabled(), label);
			assertEquals("Only for your own BioModels", item.getToolTipText(), label);
		}
		assertTrue(this.find(menu, "Open").isEnabled());
		assertTrue(this.find(menu, "Copy Name").isEnabled());
	}

	@Test
	public void aPublishedModelCanStillBePublishedButNotDeletedArchivedOrReshared() {
		JPopupMenu menu = this.menuFor(this.bioModel("Published", ListedModelActionsTest.ME, 1, 0, VersionFlag.Published));
		assertFalse(this.find(menu, "Delete...").isEnabled());
		assertFalse(this.find(menu, "Archive").isEnabled());
		assertEquals("Already published", this.find(menu, "Archive").getToolTipText());
		assertFalse(this.find(menu, "Permissions...").isEnabled());
		assertTrue(this.find(menu, "Publish").isEnabled(), "as the database browser allowed");
	}

	@Test
	public void compareWithFollowsTheModelsOtherEditions() {
		BioModelInfo first = this.bioModel("FRAP", ListedModelActionsTest.ME, 5, 0, VersionFlag.Current);
		BioModelInfo middle = this.bioModel("FRAP", ListedModelActionsTest.ME, 5, 1, VersionFlag.Current);
		BioModelInfo last = this.bioModel("FRAP", ListedModelActionsTest.ME, 5, 2, VersionFlag.Current);
		BioModelInfo alone = this.bioModel("Alone", ListedModelActionsTest.ME, 6, 0, VersionFlag.Current);

		assertEquals(List.of(false, true, true, true), this.compareState(this.menuFor(first)), "first edition: no previous");
		assertEquals(List.of(true, true, true, true), this.compareState(this.menuFor(middle)));
		assertEquals(List.of(true, false, true, true), this.compareState(this.menuFor(last)), "latest edition: no later one");
		assertEquals(List.of(false, false, false, true), this.compareState(this.menuFor(alone)), "only Another Model");
	}

	@Test
	public void aGeometryOffersModelsUsingItInsteadOfCompare() {
		GeometryInfo geometry = new GeometryInfo(this.version("G", ListedModelActionsTest.ME, 9, 0, VersionFlag.Current), 2, null, null, null, null);
		JPopupMenu menu = this.menuFor(geometry);
		assertNotNull(this.find(menu, "Models Using Geometry"));
		assertNull(this.find(menu, "Compare With"));
		assertNotNull(this.find(menu, "Copy Geometry ID Number"));
	}

	// Previous Edition, Latest Edition, Another Edition..., Another Model...: enabled or not
	private List<Boolean> compareState(JPopupMenu menu) {
		JMenu compare = (JMenu) this.find(menu, "Compare With");
		List<Boolean> state = new ArrayList<>();
		for (int i = 0; i < compare.getItemCount(); i++) state.add(compare.getItem(i).isEnabled());
		return state;
	}

	private JPopupMenu menuFor(VCDocumentInfo model) {
		return new ListedModelActions(this.windowManager()).createMenu(model, () -> {});
	}

	private JMenuItem find(MenuElement menu, String label) {
		for (MenuElement element : menu.getSubElements()) {
			if (element instanceof JMenuItem item && label.equals(item.getText())) return item;
			JMenuItem found = this.find(element, label);
			if (found != null) return found;
		}
		return null;
	}

	private TopLevelWindowManager windowManager() {
		DocumentManager documentManager = (DocumentManager) Proxy.newProxyInstance(DocumentManager.class.getClassLoader(), new Class<?>[] { DocumentManager.class },
				(proxy, method, args) -> switch (method.getName()) {
					case "getUser" -> ListedModelActionsTest.ME;
					case "getBioModelInfos" -> this.bioModels.toArray(new BioModelInfo[0]);
					case "getMathModelInfos" -> new MathModelInfo[0];
					case "getGeometryInfos" -> new GeometryInfo[0];
					case "hashCode" -> System.identityHashCode(proxy);
					case "equals" -> proxy == args[0];
					default -> throw new UnsupportedOperationException(method.getName());
				});
		RequestManager requestManager = new RequestManagerAdapter() {
			@Override
			public DocumentManager getDocumentManager() {
				return documentManager;
			}
		};
		return new TopLevelWindowManager(requestManager) {
			@Override public Component getComponent() { return new JPanel(); }
			@Override public String getManagerID() { return "test"; }
			@Override public boolean isRecyclable() { return false; }
		};
	}

	private BioModelInfo bioModel(String name, User owner, int branch, int day, VersionFlag flag) {
		BioModelInfo model = new BioModelInfo(this.version(name, owner, branch, day, flag), (String) null, null);
		this.bioModels.add(model);
		return model;
	}

	private Version version(String name, User owner, int branch, int day, VersionFlag flag) {
		return new Version(new KeyValue(String.valueOf(this.nextKey++)), name, owner, new GroupAccessNone(), null,
				BigDecimal.valueOf(branch), new Date(1_700_000_000_000L + day * 86_400_000L), flag, null);
	}
}
