package cbit.vcell.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vcell.util.SkipCommentLineNumberReader;

public class SkipCommentLineReaderTest {
	private static final String REDFISH = "redfish.txt";
	private static final String BLUEFISH = "bluefish.txt";

	private File redFish;
	private File blueFish;

	private File safeSetup(String n) {
		File f = new File(n);
		if (f.exists()) {
			throw new RuntimeException("File " + f.getAbsolutePath() + "already exists");
		}
		f.deleteOnExit();
		return f;
	}

	private void populate(File f, String s) throws IOException {
		try (FileWriter fw =new FileWriter(f)) {
			fw.write(s);
		}
	}

	@Before
	public void setup ( ) throws IOException { 
		redFish = safeSetup(REDFISH);
		blueFish = safeSetup(BLUEFISH);
		populate(redFish, "Mary\nhad\n#did she?\na\nlittle\nlamb");
		populate(blueFish, "Mary\nhad\n#did she?\na\nlittle\nlamb\n#but she ate it");
	}

	private void safeDelete(File f) throws IOException {
		if (f.exists())
			Files.delete(f.toPath());
	}

	@After
	public void cleanup ( ) throws IOException {
		safeDelete(redFish);
		safeDelete(blueFish);
	}

	public void readAFile(File tfile) throws Exception {
		try (SkipCommentLineNumberReader sclnr = new SkipCommentLineNumberReader(new FileReader(tfile)) ) {
			while (sclnr.ready()) {
				String line = sclnr.readLine();
				System.out.println("line " + sclnr.getLineNumber() + ": " + line); 
			}
		}
	}
	
	@Test
	public void rfish( ) throws Exception {
		readAFile(redFish);
	}
	
	@Test
	public void bfish( ) throws Exception {
		readAFile(blueFish);
	}
}
