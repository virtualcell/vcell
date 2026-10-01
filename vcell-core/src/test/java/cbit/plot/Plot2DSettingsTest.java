package cbit.plot;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class Plot2DSettingsTest {

	@Test
	public void varyLineStylesDefaultsTrueAndSkipsEqualChanges() {
		Plot2DSettings settings = new Plot2DSettings();
		assertTrue(settings.getVaryLineStyles());
		AtomicInteger events = new AtomicInteger();
		settings.addPropertyChangeListener("varyLineStyles", (PropertyChangeEvent event) -> events.incrementAndGet());
		settings.setVaryLineStyles(true);
		assertEquals(0, events.get());
		settings.setVaryLineStyles(false);
		assertEquals(1, events.get());
		assertFalse(settings.getVaryLineStyles());
	}

	@Test
	public void saveAndRestoreIncludesVaryLineStyles() {
		Plot2DSettings settings = new Plot2DSettings();
		settings.setVaryLineStyles(false);
		settings.saveSettings();
		settings.setVaryLineStyles(true);
		settings.restoreSavedSettings();
		assertFalse(settings.getVaryLineStyles());
	}
}
