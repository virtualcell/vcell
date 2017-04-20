/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.cellml;
import java.util.*;

public class NameList {
  Hashtable<String, String> nameList;

  public NameList() {
    nameList = new Hashtable<String, String>();
  }

  public String mangleString(String expression) {
    String mangled = expression;
    for (Enumeration<String> e = nameList.keys() ; e.hasMoreElements() ;) {
      String key = e.nextElement().toString();
      int dummy = key.lastIndexOf(".dummy");
      String name = key.substring(0,dummy);
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
