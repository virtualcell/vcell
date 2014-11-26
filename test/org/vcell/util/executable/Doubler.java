package org.vcell.util.executable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileWriter;;

/**
 * test class for {@link LiveProcessTest}
 * @author gweatherby
 */
public class Doubler {

	public static void main(String[] args) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in) ); PrintWriter pw = new PrintWriter(new FileWriter("doubler.log"),true)) {
			pw.println("starting");
			for (;;) {
				String numberMaybe = br.readLine();
				pw.println("read " + numberMaybe);
				try {
					int n = Integer.parseInt(numberMaybe);
					String ans = Integer.toString(n + n);
					System.out.println(ans);
					pw.println("sent " + ans);
				}
				catch (NumberFormatException nfe) {
					if (numberMaybe.toLowerCase().equals("quit")) {
						System.exit(0);
					}
					System.out.println(numberMaybe + " is not an integer");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
