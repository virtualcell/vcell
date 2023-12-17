package cbit.vcell.resource;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertSame;

@Tag("Fast")
public class PropertyLoaderTest {


		@Test
		public void testSystemTempDir( ) throws IOException {
			File tempDir = PropertyLoader.getSystemTemporaryDirectory();
			System.out.println(tempDir);
			File secondCall  = PropertyLoader.getSystemTemporaryDirectory();
            assertSame(tempDir, secondCall);
		}
}
