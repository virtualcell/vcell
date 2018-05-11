/*
 * @(#)Entry.java	1.7 06/10/30
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

package com.sun.java.help.search;

class Entry
{
  public byte[] key;
  public int    id;
  public int    block = -1;
  
  public Entry(byte[] key, int length, int id)
  {
    this.key = new byte[length + 1];
    System.arraycopy(key, 0, this.key, 0, length);
    this.key[length] = 0;
    this.id = id;
  }
  
  public byte[] getKey() {
    return key;
  }

  public int getID() {
    return id;
  }

  public boolean smallerThan(Entry other)
  {
    for (int i = 0; i < Math.min(key.length, other.key.length); i++)
      if (key[i] != other.key[i])
	return (key[i]&0xFF) - (other.key[i]&0xFF) < 0;
    return false;
  }
}
