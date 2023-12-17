package cbit.vcell.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.CountingLineReader;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled
@Tag("Fast")
public class CountingLineReaderTest {
	
	private final static String BLB =
			"Now is the winter\nof our discontent.\r"
			+ "Made glorious by this son of York\r\n"
			+ "And all the clouds yada yada";
	
	@Test
	public void basic( ) throws IOException {
		execute(BLB);
	}
	
	@Test
	public void crEnd( ) throws IOException {
		execute("Fame!\r"); 
	}
	
	@Test
	public void lfEnd( ) throws IOException {
		execute("Shazam\n"); 
	}
	
	@Test
	public void crlfEnd( ) throws IOException {
		execute("Holy smokes, Batman!\r\n");
	}
	
	/**
	 * verify buffer too small exception
	 * @throws IOException
	 */
	@Test
	public void toosmall( ) throws IOException {
		assertThrows(IOException.class, () -> {
			try (CountingLineReader flr = new CountingLineReader(new StringReader(BLB),5)) {
				System.out.println(flr.readLine());
			}
		});
	}
	
	private void execute(String s) throws IOException {
		System.out.println("## " + s + "##");
		try (CountingLineReader flr = new CountingLineReader(new StringReader(s))) {
			String line = flr.readLine();
			while (line != null) {
				System.out.println(line);
				System.out.println(flr.lastStringPosition());
				System.out.println(s.charAt(flr.lastStringPosition()));
				line = flr.readLine();
			}
		}
	}
	
	

}
