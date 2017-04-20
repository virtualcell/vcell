package cbit.vcell.util;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;
import org.vcell.util.CountingLineReader;

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
	@Test(expected = IOException.class)
	public void toosmall( ) throws IOException {
		try (CountingLineReader flr = new CountingLineReader(new StringReader(BLB),5)) {
			System.out.println(flr.readLine());
		}
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
