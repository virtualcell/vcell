package org.vcell.util.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Map.Entry;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("Fast")
public class VCellIconsTest {

	@Test
	public void test() throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		
		for (Entry<String, URL> entry : VCellIcons.imagePaths.entrySet()){
			assertNotNull(entry.getValue(), "file path " + entry.getKey() + " not found as resource");
		}
	}

}
