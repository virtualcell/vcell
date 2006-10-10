package cbit.vcell.xml;
import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (3/22/2001 11:17:04 AM)
 * @author: Daniel Lucio
 */
public abstract class XmlBase {


/**
 * Default XmlBase constructor .
 */
public XmlBase() {
	super();
}


/**
 * This method returns a mangled String.
 * Creation date: (3/22/2001 11:18:39 AM)
 * @return java.lang.String
 * @param param java.lang.String
 */
public static String mangle(String param) {

	/*
	StringBuffer buf = new StringBuffer();
	
	for (int i = 0; i < param.length(); i++) {
		char c = param.charAt(i);
		if (Character.isISOControl(c)) {
			if (c != 0x9 && c != 0xA && c != 0xD) {         //tab, newline, carriage return
				System.out.println("Invalid control character: " + Integer.toHexString(c) + ". Roundtrip test might fail.");
				buf.append("&#0x" + Integer.toHexString(c) + ";");
				continue;
				 
			}
		}
		buf.append(c);
	}
	if (!param.equals(buf.toString())) {
		System.out.println("Before: " + param + " After: " + buf.toString());
		param = buf.toString();
	}
	*/
	//return cbit.util.TokenMangler.getEscapedString(param);
	return param;
}


/**
 * This method returns a mangled String.
 * Creation date: (3/22/2001 11:18:39 AM)
 * @return java.lang.String
 * @param param java.lang.String
 */
public static String mangleExpression(IExpression expression) {
	IExpression tempExp = ExpressionFactory.createExpression(expression);
	try {
		tempExp.bindExpression(null);
		tempExp = tempExp.flatten();
	}catch (ExpressionException e){
		e.printStackTrace(System.out);
	}
	return mangle(tempExp.infix());
}


/**
 * This method returns an unmangled string.
 * Creation date: (3/22/2001 11:19:33 AM)
 * @return java.lang.String
 * @param param java.lang.String
 */
public static String unMangle(String param) {

	/*
	//escape for hexadecimals. 
	String beginSeq = "&#0x"; 
	String endSeq = ";";
	while (true) {
		int startIndex = param.indexOf(beginSeq);
		//System.out.println("startIndex: " + startIndex);
		if (startIndex == -1)
			break;
		int endIndex = param.indexOf(endSeq, startIndex + 4);              //skip the '&#0x' and the terminating ';'
		//System.out.println("endIndex:" + endIndex);
		if (endIndex == -1)
			break;
		String hex = param.substring(startIndex + 4, endIndex);
		System.out.println("Escaped Hexa:" + (beginSeq + hex + endSeq) + " " + Integer.toHexString(Integer.parseInt(hex, 16)) + " "
			                + startIndex + " " + endIndex);
		String oldName = beginSeq + hex + endSeq;
		String newName = Integer.toHexString(Integer.parseInt(hex, 16));
		StringBuffer buf = new StringBuffer(param);
		buf.replace(startIndex, endIndex, newName);
		param = buf.toString();
	}
	//System.out.println(param);
	*/
	
	return param; //cbit.util.TokenMangler.getRestoredString(param);
}


	public static IExpression unMangleExpression(String expStr) {

		IExpression tempExp = null;

		//
		// if parsing fails, that's a show-stopper.
		//
		try {
			tempExp = ExpressionFactory.createExpression(unMangle(expStr)); 
		}catch (ExpressionException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
		//
		// if flatten fails, it's ok (maybe a divide-by-zero ... but this shouldn't be judgemental on expression quality)
		//
		try {
			tempExp = tempExp.flatten();
		}catch (ExpressionException e){
			e.printStackTrace(System.out);
		}
		
		return tempExp;
	}
}