package cbit.vcell.export.server;

import cbit.image.DisplayAdapterService;
import cbit.image.DisplayPreferences;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.Range;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("Fast")
public class ExportSpecsColorModeFallbackTest {

	@Test
	public void unknownColorModeFallsBackToBlueRed_8_2_e() {
		DisplayAdapterService das = registeredService();
		DisplayPreferences requested = new DisplayPreferences(
				"NoSuchMap",
				new Range(0, 1),
				DisplayAdapterService.createBlueRedSpecialColors(),
				true,
				false);

		ExportSpecs.setupDisplayAdapterService(requested, das, new Range(0, 1));

		assertEquals(DisplayAdapterService.BLUERED, das.getActiveColorModelID());
		assertEquals(DisplayAdapterService.BLUERED, requested.getColorMode());
	}

	@Test
	public void unregisteredCividisDoesNotStayOnTheRequest() {
		DisplayAdapterService das = new DisplayAdapterService();
		das.addColorModelForValues(
				DisplayAdapterService.createGrayColorModel(),
				DisplayAdapterService.createGraySpecialColors(),
				DisplayAdapterService.GRAY);
		das.addColorModelForValues(
				DisplayAdapterService.createBlueRedColorModel(),
				DisplayAdapterService.createBlueRedSpecialColors(),
				DisplayAdapterService.BLUERED);
		DisplayPreferences requested = new DisplayPreferences(
				DisplayAdapterService.CIVIDIS,
				new Range(0, 1),
				DisplayAdapterService.createCividisSpecialColors(),
				true,
				false);

		ExportSpecs.setupDisplayAdapterService(requested, das, new Range(0, 1));

		assertEquals(DisplayAdapterService.BLUERED, requested.getColorMode());
		assertEquals(DisplayAdapterService.BLUERED, das.getActiveColorModelID());
	}

	@Test
	public void registeredCividisStaysCividis_8_2_e() {
		DisplayAdapterService das = registeredService();
		DisplayPreferences requested = new DisplayPreferences(
				DisplayAdapterService.CIVIDIS,
				new Range(0, 1),
				DisplayAdapterService.createCividisSpecialColors(),
				true,
				false);

		ExportSpecs.setupDisplayAdapterService(requested, das, new Range(0, 1));

		assertEquals(DisplayAdapterService.CIVIDIS, das.getActiveColorModelID());
	}

	@Test
	public void missingBlueRedStillThrows_8_2_e() {
		DisplayAdapterService das = new DisplayAdapterService();
		DisplayPreferences requested = new DisplayPreferences(
				"NoSuchMap",
				new Range(0, 1),
				DisplayAdapterService.createBlueRedSpecialColors(),
				true,
				false);

		assertThrows(IllegalArgumentException.class, () ->
				ExportSpecs.setupDisplayAdapterService(requested, das, new Range(0, 1)));
	}

	private static DisplayAdapterService registeredService() {
		DisplayAdapterService das = new DisplayAdapterService();
		DisplayAdapterService.addStandardColorModels(das);
		return das;
	}
}
