package cbit.vcell.mapping;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	
	
public static String md5(String s) {
	try {
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.update(s.getBytes(), 0, s.length());
		BigInteger i = new BigInteger(1,m.digest());
		return String.format("%1$032x", i);         
	} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	}
	return null;
}



}
