package cbit.vcell.xml;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.text.*;

public class NameList {
  Hashtable nameList;

  public NameList() {
    nameList = new Hashtable();
  }

  public String mangleString(String expression) {
    String mangled = expression;
    for (Enumeration e = nameList.keys() ; e.hasMoreElements() ;) {
      String key = e.nextElement().toString();
      int dummy = key.lastIndexOf(".dummy");
      String name = key.substring(0,dummy);
      int start = 0;
      int index = mangled.indexOf(name);
      while(index > 0) {
        StringBuffer buf = new StringBuffer(mangled);
        buf.replace(index,index+name.length(),(String)nameList.get(key));
        mangled = buf.toString();
        index = mangled.indexOf(name,index+1);
      }
    }
    return(mangled);
  }

  public String getMangledName(String variable_name,
    String component_name) {
    String mangledName = 
      (String)nameList.get(variable_name+"."+component_name);
    if (mangledName == null) {
      mangledName = new String("");
    }
    return (mangledName);
  }

  public void setMangledName(String variable_name,
    String component_name,String mangledName) {
    String tmp = (String)nameList.get(variable_name+"."+component_name);
    if (tmp == null) {
      nameList.put(variable_name+"."+component_name,mangledName);
    }
  }

}
