package cbit.util.graph;
/**
 * Insert the type's description here.
 * Creation date: (2/10/2002 10:44:41 PM)
 * @author: Jim Schaff
 */
public class Node {
    private String name = null;
    private Object data = null;

/**
 * Node constructor comment.
 */
public Node(String name) {
	this(name,null);
}


/**
 * Node constructor comment.
 */
public Node(String name, Object argData) {
	if (name==null || name.length()==0){
		throw new IllegalArgumentException("name is either null or empty");
	}
	this.name = name;
	this.data = argData;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:20:36 PM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (obj instanceof Node){
		Node node = (Node)obj;
		if (!node.getName().equals(getName())){
			return false;
		}
		if (data != null && node.data != null){
			if (!data.equals(node.data)){
				return false; // if data is not .equal(), then not equal
			}
		}else if (data != null || node.data != null){
			return false; // if only one data is null, then not equal
		}
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 11:15:03 AM)
 * @return java.lang.Object
 */
public Object getData() {
	return data;
}


/**
 * Insert the method's description here.
 * Creation date: (1/30/2006 11:41:19 AM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:21:50 PM)
 * @return int
 */
public int hashCode() {
	return name.hashCode()+(data!=null?data.hashCode():0);
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 11:14:39 AM)
 * @param data java.lang.Object
 */
public void setData(Object argData) {
	this.data = argData;
}


/**
 * Insert the method's description here.
 * Creation date: (1/30/2006 11:41:19 AM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:19:51 PM)
 * @return java.lang.String
 */
public String toString() {
	return getClass().getName() + "@" + Integer.toHexString(hashCode())+" "+getName()+"::"+data;
}
}