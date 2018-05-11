/*
 * @(#)ConceptLocation.java	1.6 06/10/30
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
 * @date   3/5/98
 * @author Jacek R. Ambroziak
 * @group  Sun Microsystems Laboratories
 */

package com.sun.java.help.search;

class ConceptLocation
{
  private int _concept;
  private int _begin;
  private int _end;
  
  private abstract class ConceptLocationSorter
  {
    abstract public boolean smallerThan(ConceptLocation a, ConceptLocation b);
    // part of quicksearch
    private int partition(ConceptLocation[] array, int p, int r)
    {
      ConceptLocation x = array[p];
      int i = p - 1, j = r + 1;
      while (true)
	{
	  while (smallerThan(x, array[--j]))
	    ;
	  while (smallerThan(array[++i], x))
	    ;
	  if (i < j)
	    {
	      ConceptLocation t = array[i];
	      array[i] = array[j];
	      array[j] = t;
	    }
	  else
	    return j;
	}
    }

    public void quicksort(ConceptLocation[] array, int p, int r)
    {
      if (p < r)
	{
	  int q = partition(array, p, r);
	  quicksort(array, p, q);
	  quicksort(array, q + 1, r);
	}
    }
  }

  private class ConceptSorter extends ConceptLocationSorter
  {
    public boolean smallerThan(ConceptLocation a, ConceptLocation b) {
      return a._concept < b._concept;
    }
  }
  
  private class PositionSorter extends ConceptLocationSorter
  {
    public boolean smallerThan(ConceptLocation a, ConceptLocation b) {
      return a._begin < b._begin || a._begin == b._begin && a._end < b._end;
    }
  }
  // sorter by concept ID
  private static ConceptLocationSorter _cComp;
  // sorter by position
  private static ConceptLocationSorter _pComp;

  private ConceptLocation()
  {
    _cComp = new ConceptSorter();
    _pComp = new PositionSorter();
  }

  static { new ConceptLocation(); }

  public ConceptLocation(int conceptID, int begin, int end)
  {
    _concept = conceptID;
    _begin = begin;
    _end = end;
  }
  
  public boolean equals(ConceptLocation other) {
    return _concept==other._concept&&_begin==other._begin&&_end==other._end;
  }

  public void setConcept(int concept) {
    _concept = concept;
  }
  
  public int getConcept() {
    return _concept;
  }
  
  public int getBegin() {
    return _begin;
  }
  
  public int getEnd() {
    return _end;
  }
  
  public int getLength() {
    return _end - _begin;
  }
  
  public static void sortByConcept(ConceptLocation[] array, int i1, int i2) {
    _cComp.quicksort(array, i1, i2 - 1);
  }
  
  public static void sortByPosition(ConceptLocation[] array, int i1, int i2) {
    _pComp.quicksort(array, i1, i2 - 1);
  }
  
  public void print() {
    System.out.println(_concept+"\t"+_begin+"\t"+_end);
  }

  public static void main(String[] args)
  {
    int limit = 30, b;
    ConceptLocation[] array = new ConceptLocation[limit];
    for (int i = 0; i < limit; i++)
      array[i] = new ConceptLocation((int)(Math.random()*1000),
				     b = (int)(Math.random()*1000),
				     b + (int)(Math.random()*10));
    for (int i = 0; i < limit; i++)
      array[i].print();
    ConceptLocation.sortByConcept(array, 0, limit);
    System.out.println("----------------------------------");
    for (int i = 0; i < limit; i++)
      array[i].print();
    ConceptLocation.sortByPosition(array, 0, limit);
    System.out.println("----------------------------------");
    for (int i = 0; i < limit; i++)
      array[i].print();
  }
}

