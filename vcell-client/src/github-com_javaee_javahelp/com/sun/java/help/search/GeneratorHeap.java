/*
 * @(#)GeneratorHeap.java	1.6 06/10/30
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

class GeneratorHeap
{
  private static final int        InitSize = 128;
  private int                     _heapSize = 0;
  private ConceptGroupGenerator[] _heap;
  private int                     _size = InitSize;
  private int                     _free = 0;
  
  public GeneratorHeap() {
    _heap = new ConceptGroupGenerator[InitSize];
  }
  
  public void reset() {
    _free = 0;
  }
  
  public void addGenerator(ConceptGroupGenerator gen)
  {
    if (_free == _size)
      {
	ConceptGroupGenerator[] newArray =
	  new ConceptGroupGenerator[_size *= 2];
	System.arraycopy(_heap, 0, newArray, 0, _free);
	_heap = newArray;
      }
    _heap[_free++] = gen;
  }

  private void buildHeap()
  {
    for (int i = _heapSize/2; i >= 0; i--)
      heapify(i);
  }

  private void heapify(int i)
  {
    int r = (i + 1) << 1, l = r - 1;
    int smallest = l<_heapSize&&_heap[l].position()<_heap[i].position()?l:i;
  
    if (r < _heapSize && _heap[r].position() < _heap[smallest].position())
      smallest = r;
    if (smallest != i)
      {
	ConceptGroupGenerator temp = _heap[smallest];
	_heap[smallest] = _heap[i];
	_heap[i] = temp;
	heapify(smallest);
      }
  }

  public boolean start(RoleFiller[] array) throws Exception
  {
    if ((_heapSize = _free) > 0)
      {
	for (int i = 0; i < _free; i++)
	  _heap[i].next();
	buildHeap();
	_heap[0].generateFillers(array);
	return true;
      }
    else
      return false;
  }

  public boolean next(RoleFiller[] array) throws Exception
  {
    if (_heapSize > 0)
      {
	if (!_heap[0].next()) // no more
	  if (_heapSize > 1)
	    _heap[0] = _heap[--_heapSize];
	  else
	    {
	      _heapSize = 0;
	      return false;
	    }
	heapify(0);
	_heap[0].generateFillers(array);
	return true;
      }
    else
      return false;
  }
}
