/*
 * @(#)NextDocGeneratorHeap.java	1.7 06/10/30
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

class NextDocGeneratorHeap
{
  private static final int InitSize = 1024;
  
  private int                _heapSize = 0;
  private int                _size = InitSize;
  private NextDocGenerator[] _heap = new NextDocGenerator[InitSize];
  private int                _free = 0;
  private boolean             _nonEmpty = false;
  
  public boolean isNonEmpty() {
    return _nonEmpty;
  }
  
  public void addGenerator(NextDocGenerator gen)
  {
    if (_free == _size)
      {
	NextDocGenerator[] newArray = new NextDocGenerator[_size *= 2];
	System.arraycopy(_heap, 0, newArray, 0, _free);
	_heap = newArray;
      }
    _heap[_free++] = gen;
  }
  
  public void start()
  {
    if ((_heapSize = _free) > 0)
      {
	// build heap
	for (int i = _heapSize/2; i >= 0; i--)
	  heapify(i);
	_nonEmpty = true;
      }
    else
      _nonEmpty = false;
  }
  
  public void step() throws Exception
  {
    if (_heap[0].next() != NonnegativeIntegerGenerator.END)
      heapify(0);
    else if (_heapSize > 1)
      {
	_heap[0] = _heap[--_heapSize];
	heapify(0);
      }
    else
      _nonEmpty = false;
  }
  
  public int getDocument() {
    return _heap[0].getDocument();
  }
  
  public int getConcept() {
    return _heap[0].getConcept();
  }
  
  public ConceptData getTerms() {
    return _heap[0].getTerms();
  }
  
  public int getQueryMask() {
    return _heap[0].getQueryMask();
  }
  
  public void reset()
  {
    _nonEmpty = false;
    _free = 0;
  }
  
  public boolean atDocument(int document) {
    return _nonEmpty && _heap[0].getDocument() == document;
  }
  
  private void heapify(int i)
  {
    int r = (i + 1) << 1, l = r - 1;
    int smallest = l < _heapSize && _heap[l].smallerThan(_heap[i]) ? l : i;
    if (r < _heapSize && _heap[r].smallerThan(_heap[smallest]))
      smallest = r;
    if (smallest != i)
      {
	NextDocGenerator temp = _heap[smallest];
	_heap[smallest] = _heap[i];
	_heap[i] = temp;
	heapify(smallest);
      }
  }
}
