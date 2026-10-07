package cbit.vcell.export.server;

import cbit.image.DisplayAdapterService;
import cbit.image.DisplayPreferences;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.DataAccessException;
import org.vcell.util.Range;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class ExportColorModeNegotiationTest {

	@Test
	public void upgradedServerKeepsCividis() {
		DisplayPreferences requested = preferences(DisplayAdapterService.CIVIDIS);

		DisplayPreferences written = ExportColorModeNegotiation.preferencesForServer(
				requested, ExportColorModeNegotiation.currentModes());

		assertEquals(requested, written);
		assertEquals(DisplayAdapterService.CIVIDIS, written.getColorMode());
		assertNull(ExportColorModeNegotiation.notice(DisplayAdapterService.CIVIDIS, written.getColorMode()));
	}

	@Test
	public void oldServerRewritesCividisToBlueRedAndNamesTheMode() {
		DisplayPreferences requested = preferences(DisplayAdapterService.CIVIDIS);

		DisplayPreferences written = ExportColorModeNegotiation.preferencesForServer(
				requested, ExportColorModeNegotiation.legacyModes());

		assertEquals(DisplayAdapterService.BLUERED, written.getColorMode());
		assertArrayEquals(DisplayAdapterService.createBlueRedSpecialColors(), written.getSpecialColors());
		assertEquals(DisplayAdapterService.CIVIDIS, requested.getColorMode());
		String notice = ExportColorModeNegotiation.notice(requested.getColorMode(), written.getColorMode());
		assertTrue(notice.contains(DisplayAdapterService.BLUERED), notice);
		assertTrue(notice.contains(DisplayAdapterService.CIVIDIS), notice);
	}

	@Test
	public void grayStaysGrayOnAnOldServer() {
		DisplayPreferences requested = preferences(DisplayAdapterService.GRAY);

		DisplayPreferences written = ExportColorModeNegotiation.preferencesForServer(
				requested, ExportColorModeNegotiation.legacyModes());

		assertEquals(requested, written);
		assertNull(ExportColorModeNegotiation.notice(DisplayAdapterService.GRAY, written.getColorMode()));
	}

	@Test
	public void missingQueryLooksLikeAnOldServer() {
		DataAccessException error = new DataAccessException(
				"No such method: getSupportedExportColorModes()");
		assertTrue(ExportColorModeNegotiation.isUnsupportedColorModeQuery(error));
		assertTrue(ExportColorModeNegotiation.isUnsupportedColorModeQuery(new RuntimeException("rpc", error)));
	}

	private static DisplayPreferences preferences(String mode) {
		int[] specials = DisplayAdapterService.CIVIDIS.equals(mode)
				? DisplayAdapterService.createCividisSpecialColors()
				: DisplayAdapterService.createBlueRedSpecialColors();
		if (DisplayAdapterService.GRAY.equals(mode)) {
			specials = DisplayAdapterService.createGraySpecialColors();
		}
		return new DisplayPreferences(mode, new Range(0, 1), specials, true, false);
	}
}
