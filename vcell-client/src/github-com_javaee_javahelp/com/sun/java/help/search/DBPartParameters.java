/*
 * @(#)DBPartParameters.java	1.6 06/10/30
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

/**
 * @version	1.5	03/18/98
 * @author Jacek R. Ambroziak
 */

package com.sun.java.help.search;

import java.util.Hashtable;
import java.net.URL;

public class DBPartParameters
{
  protected Schema _schema;
  private String _partName;
  private Hashtable _parameters;

  protected DBPartParameters() {}

  public DBPartParameters(Schema schema, String partName)
  {
    _schema = schema;
    _partName = partName;
    _parameters = schema.parameters(partName);
  }
  
  protected boolean parametersKnown() {
    return _parameters != null;
  }
  
  protected void updateSchema(String parameters) {
    _schema.update(_partName, parameters);
  }
  
  public int integerParameter(String name) {
    return Integer.parseInt(((String)_parameters.get(name)));
  }

  public URL getURL() throws Exception {
    return _schema.getURL(_partName);
  }
}
