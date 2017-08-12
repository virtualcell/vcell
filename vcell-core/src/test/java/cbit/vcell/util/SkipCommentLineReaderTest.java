package cbit.vcell.util;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.vcell.util.SkipCommentLineNumberReader;

public class SkipCommentLineReaderTest {
	private static final String UNCOMMENTED_TEXT = "Mary\nhad\na\nlittle\nlamb\n";
	private static final String COMMENTED_TEXT_1 = "Mary\nhad\n#did she?\na\nlittle\nlamb\n";
	private static final String COMMENTED_TEXT_2 = "Mary\nhad\n#did she?\na\nlittle\nlamb\n#but she ate it\n";

	public void readAFile(Reader reader) throws Exception {
		try (SkipCommentLineNumberReader sclnr = new SkipCommentLineNumberReader(reader);
			 SkipCommentLineNumberReader standard = new SkipCommentLineNumberReader(new StringReader(UNCOMMENTED_TEXT));) {
			while (sclnr.ready() && standard.ready()) {
				String line_file = sclnr.readLine();
				String line_standard = standard.readLine();
				Assert.assertEquals(line_file,line_standard);
				//Assert.assertEquals(sclnr.getLineNumber(), standard.getLineNumber());
			}
		}
	}
	
	@Test
	public void rfish( ) throws Exception {
		readAFile(new StringReader(COMMENTED_TEXT_1));
	}
	
	@Test
	public void bfish( ) throws Exception {
		readAFile(new StringReader(COMMENTED_TEXT_2));
	}
}
