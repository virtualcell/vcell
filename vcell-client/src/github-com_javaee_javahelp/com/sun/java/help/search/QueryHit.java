/*
 * @(#)QueryHit.java	1.6 06/10/30
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
 * @date   1/13/98
 * @author Jacek R. Ambroziak
 * @group  Sun Microsystems Laboratories
 */

package com.sun.java.help.search;

class QueryHit
{
  private int _doc;
  private int _begin;
  private int _end;
  private double _score;
  private final int[] _concepts;

  public QueryHit(Location loc, double penalty, int nColumns)
  {
    _score = penalty;
    _doc = loc.getDocument();
    _begin = loc.getBegin();
    _end = loc.getEnd();
    _concepts = new int[nColumns];
  }
  
  public String toString() {
    return "[doc = "+_doc+", "+_begin+", "+_end+", "+_score+"]";
  }

  public int getDocument() {
    return _doc;
  }

  public int getBegin() {
    return _begin;
  }

  public int getEnd() {
    return _end;
  }

  public double getScore() {
    return _score;
  }

  public int[] getArray() {
    return _concepts;
  }

  public boolean betterThan(QueryHit x)
  {
    if (_score < x._score) return true;
    if (_score > x._score) return false;
    if (_begin < x._begin) return true;
    if (_begin > x._begin) return false;
    if (_end < x._end) return true;
    if (_end > x._end) return false;
    return false;
  }
}
