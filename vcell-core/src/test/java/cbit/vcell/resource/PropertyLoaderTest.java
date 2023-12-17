package cbit.vcell.resource;

import org.junit.jupiter.api.Tag;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

@Tag("Fast")
public class PropertyLoaderTest {


		@Test
		public void testSystemTempDir( ) throws IOException {
			File tempDir = PropertyLoader.getSystemTemporaryDirectory();
			System.out.println(tempDir);
			File secondCall  = PropertyLoader.getSystemTemporaryDirectory();
			assertTrue(tempDir == secondCall);
		}
}
