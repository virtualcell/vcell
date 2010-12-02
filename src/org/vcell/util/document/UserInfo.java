package org.vcell.util.document;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class UserInfo implements java.io.Serializable,java.lang.Cloneable {	
	public KeyValue id = null;
	public String userid = null;
	public String password = null;
	public transient String password2 = null;
	public String email = null;
	public String firstName = null;
	public String lastName = null;
	public String title = null;
	public String company = null;
	public String address1 = null;
	public String address2 = null;
	public String city = null;
	public String state = null;
	public String country = null;
	public String zip = null;
	public boolean notify = false;
	public java.util.Date insertDate = null;

	public static int FIELDLENGTH_USERID = 255;
	public static int FIELDLENGTH_PASSWORD = 255;
	public static int FIELDLENGTH_EMAIL = 255;
	public static int FIELDLENGTH_FIRSTNAME = 255;
	public static int FIELDLENGTH_LASTNAME = 255;
	public static int FIELDLENGTH_TITLE = 255;
	public static int FIELDLENGTH_COMPANY = 255;
	public static int FIELDLENGTH_ADDRESS1 = 255;
	public static int FIELDLENGTH_ADDRESS2 = 255;
	public static int FIELDLENGTH_CITY = 255;
	public static int FIELDLENGTH_STATE = 255;
	public static int FIELDLENGTH_COUNTRY = 255;
	public static int FIELDLENGTH_ZIP = 255;
/**
 * This method was created in VisualAge.
 * @return cbit.sql.UserInfo
 */
public Object clone() {
	UserInfo newUI = null;
	try {
		newUI = (UserInfo) super.clone();
	} catch (CloneNotSupportedException e) {
		// this shouldn't happen, since we are Cloneable
		throw new InternalError();
	}
	
	return newUI;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	UserInfo userInfo = null;
	if (object == null){
		return false;
	}
	if (!(object instanceof UserInfo)){
		return false;
	}else{
		userInfo = (UserInfo)object;
	}
	
System.out.println("this="+toString());
System.out.println("other="+userInfo.toString());
	
	if (toString().equals(userInfo.toString())){
		return true;
	}else{
		return false;
	}
}
/**
 * This method was created in VisualAge.
 */
public static UserInfo getExample1() {
	UserInfo userInfo = new UserInfo();

	userInfo.userid = "schaff";
	userInfo.password = "me&jan";
	userInfo.email = "schaff@panda";
	userInfo.firstName = "Jim";
	userInfo.lastName = "Schaff";
	userInfo.title = "???";
	userInfo.company = "uchc";
	userInfo.address1 = "here";
	userInfo.address2 = "there";
	userInfo.city = "Farmington";
	userInfo.state = "CT";
	userInfo.country = "USA";
	userInfo.zip = "06030";
//	userInfo.notify = "yes";

	return userInfo;
}
/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String toHTML() {
	StringBuffer buffer = new StringBuffer();

	User user = new User(userid,id);
	
	buffer.append("<TR ALIGN=LEFT VALIGN=TOP>");
	buffer.append("<TD>"+user+" '"+password+"'</TD>\n");
	buffer.append("<TD>"+email+" / notify "+notify+"</TD>\n");
	buffer.append("<TD>"+firstName+" "+lastName+" / "+title+"</TD>\n");
	buffer.append("<TD>"+company+"\n");
	buffer.append("<BR>"+address1+"\n");
	buffer.append("<BR>"+address2+"\n");
	buffer.append("<BR>"+city+", "+state+", "+country+" "+zip+"\n");
	buffer.append("<BR>"+insertDate+"</TD>\n");
	buffer.append("</TR>");

	return buffer.toString();
}
/**
 * This method was created in VisualAge.
 */
public String toString() {
	return "["+id+","+userid+","+password+","+email+","+firstName+","+lastName+","+title+","+company+","+address1+","+address2+","+
				city+","+state+","+country+","+zip+","+notify+","+insertDate+"]";
}
}
