package org.vcell.util.gui;

import java.net.URL;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;

public class VCellIconsTest {

	@Test
	public void test() throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		
		for (Entry<String, URL> entry : VCellIcons.imagePaths.entrySet()){
			Assert.assertNotNull("file path "+entry.getKey()+" not found as resource",entry.getValue());
		}
	}

}
