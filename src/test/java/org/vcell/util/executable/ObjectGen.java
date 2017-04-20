package org.vcell.util.executable;

import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * test class for {@link LiveProcessTest}
 * @author gweatherby
 */
public class ObjectGen {

	public static void main(String[] args) {
		try { 
			Thread.sleep(1000);
			ObjectOutputStream oos = new ObjectOutputStream(System.out);
			
			oos.writeObject(new String("howdy"));
			oos.writeObject(new Integer(7) );
			ArrayList<Short> shorts = new ArrayList<>();
			shorts.add((short) 0);
			shorts.add((short) 6);
			shorts.add((short) 0);
			shorts.add((short) 3);
			shorts.add((short) 0);
			oos.writeObject(shorts);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
