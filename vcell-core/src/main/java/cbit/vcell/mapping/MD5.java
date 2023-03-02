package cbit.vcell.mapping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	private final static Logger lg = LogManager.getLogger(MD5.class);
	
	
	public static String md5(String s) {
		return md5(s,32);
	}

	public static String md5(String s, int length) {
		if (length < 1 || length > 32){
			throw new RuntimeException("expecting length between 1 and 32 hexidecimal characters");
		}
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(), 0, s.length());
			BigInteger i = new BigInteger(1,m.digest());
			return String.format("%1$032x", i).substring(0, length);         
		} catch (NoSuchAlgorithmException e) {
			lg.error(e);
		}
		return null;
	}



}
