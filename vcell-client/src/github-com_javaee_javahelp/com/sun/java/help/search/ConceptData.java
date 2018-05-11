/*
 * @(#)ConceptData.java	1.6 06/10/30
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

class ConceptData
{
  private final byte    _query;
  private final byte    _nColumns;
  private final byte    _role;
  private final int    _concept;
  private int          _conceptLength;
  private final int    _proximity;
  private final double _penalty;
  private ConceptData _next;
  
  private static final int ProxPerTerm = 100;
  
  public ConceptData(int id, int role, double score, int query, int nColumns)
  {
    _query = (byte)query;
    _nColumns = (byte)nColumns;
    _concept = id;
    _proximity = nColumns * ProxPerTerm;
    _role = (byte)role;
    _penalty = score;
    _next = null;
  }
  
  public int getConcept() {
    return _concept;
  }
  
  public double getPenalty() {
    return _penalty;
  }
  
  public int getConceptLength() {
    return _conceptLength;
  }
  
  public byte getRole() {
    return _role;
  }

  public byte getQuery() {
    return _query;
  }

  public byte getNColumns() {
    return _nColumns;
  }

  public double getScore() {
    return _penalty;
  }
  
  public ConceptData getNext() {
    return _next;
  }
  
  public int getQueryMask() {
    return (_next != null ? _next.getQueryMask() : 0) | (1 << _query);
  }

  public void setConceptLength(int length)
  {
    _conceptLength = length;
    if (_next != null)
      _next.setConceptLength(length);
  }
  
  public void setNext(ConceptData next) {
    _next = next;
  }
  
  boolean cEquals(ConceptData other) {
    return _concept == other._concept;
  }

  boolean crEquals(ConceptData other) {
    return _concept == other._concept && _role == other._role;
  }
  
  boolean crqEquals(ConceptData other) {
    return _concept == other._concept && _role == other._role &&
      _query == other._query;
  }
  
  void addLast(ConceptData other)
  {
    if (_next != null)
      _next.addLast(other);
    else
      _next = other;
  }

  boolean compareWith(ConceptData other) {
    return _concept < other._concept
      || cEquals(other)  && _role < other._role
      || crEquals(other) && _penalty < other._penalty;
  }

  public void runBy(Query[] queries)
  {
    ConceptData cd = this;
    do
      queries[cd._query].updateEstimate(cd._role, cd._penalty);
    while ((cd = cd._next) != null);
  }

  public void generateFillers(RoleFiller[] array, int pos)
  {
    if (array[_query] != RoleFiller.STOP) // 'prohibited'
      (new RoleFiller(_nColumns, this, _role, pos, pos + _proximity))
	.use(array, _query);
    if (_next != null)
      _next.generateFillers(array, pos);
  }
}
