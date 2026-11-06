package cbit.vcell.desktop;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.*;
import org.vcell.util.document.VCDocument.VCDocumentType;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A model's tooltip in the welcome window's lists is its Database File Info (name, owner, when saved, permissions, VCell
 * ID), as VCell's database trees give it, then its description.
 */
@Tag("Fast")
public class ModelListRendererTest {
	private static final User JESSICA = new User("jessica", new KeyValue("1"));
	private static final User BORIS = new User("boris", new KeyValue("2"));
	private static final User HIDDEN = new User("hidden", new KeyValue("3"));

	@Test
	public void givesTheDatabaseFileInfoThenTheDescription() {
		BioModelInfo model = this.model("Calcium <waves>", new GroupAccessAll(), VersionFlag.Current, "Release from the ER & more");
		String toolTip = ModelListRenderer.toolTipFor(model);
		assertTrue(toolTip.startsWith("<html><b>Calcium &lt;waves&gt;</b>"), toolTip); // the name shown as typed
		assertTrue(toolTip.contains("<br>Owner: jessica"), toolTip);
		assertTrue(toolTip.contains("<br>Saved: "), toolTip);
		assertTrue(toolTip.contains("<br>Permissions: Public"), toolTip);
		assertTrue(toolTip.contains("<br>VCell ID: 42"), toolTip);
		assertTrue(toolTip.endsWith("<br><br>Release from the ER &amp; more</html>"), toolTip);
		assertFalse(ModelListRenderer.toolTipFor(this.model("Bare", new GroupAccessNone(), VersionFlag.Current, " ")).contains("<br><br>"));
	}

	@Test
	public void saysWhoMaySeeTheModelAsTheDatabaseTreesDo() {
		assertEquals("Private", ModelListRenderer.permissionsOf(this.model("A", new GroupAccessNone(), VersionFlag.Current, null)));
		assertEquals("Public, Archived", ModelListRenderer.permissionsOf(this.model("A", new GroupAccessAll(), VersionFlag.Archived, null)));
		boolean[] hidden = {false, true};
		BigDecimal hash = GroupAccess.calculateHash(new KeyValue[] {BORIS.getID(), HIDDEN.getID()}, hidden);
		GroupAccessSome some = new GroupAccessSome(BigDecimal.TEN, hash, new User[] {BORIS, HIDDEN}, hidden);
		assertEquals("Shared with boris", ModelListRenderer.permissionsOf(this.model("A", some, VersionFlag.Current, null)));

		BioModelInfo curated = this.model("Paper", new GroupAccessAll(), VersionFlag.Current, null);
		curated.addPublicationInfo(new PublicationInfo(new KeyValue("9"), curated.getVersion().getVersionKey(), "A paper",
				new String[] {"An author"}, "A citation", null, null, null, VCDocumentType.BIOMODEL_DOC, JESSICA, new Date()));
		assertEquals("Curated", ModelListRenderer.permissionsOf(curated));
	}

	private BioModelInfo model(String name, GroupAccess access, VersionFlag flag, String description) {
		Version version = new Version(new KeyValue("42"), name, JESSICA, access, null, BigDecimal.ONE,
				new Date(1_700_000_000_000L), flag, description);
		return new BioModelInfo(version, (String) null, null);
	}
}
