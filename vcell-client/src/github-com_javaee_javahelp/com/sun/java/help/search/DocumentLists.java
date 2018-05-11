/*
 * @(#)DocumentLists.java	1.7 06/10/30
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
 * @date   3/10/98
 * @author Jacek R. Ambroziak
 * @group  Sun Microsystems Laboratories
 */

package com.sun.java.help.search;

import java.io.*;

class DocumentLists
{
  private static final int RANGE = 1024;
  private static final int NConcepts = 16;
  private static final int K = 3;
  
  private final IntegerArray[] _arrays = new IntegerArray[RANGE];
  private int _minConcept = 0;
  private int _limit = RANGE;
  private final IntegerArray _concepts = new IntegerArray();
  private final IntegerArray _offsets = new IntegerArray();
  private final Compressor _compr = new Compressor();
  private final IntegerArray _diffs = new IntegerArray();
  private final ByteArrayDecompressor _decmp =
  new ByteArrayDecompressor(null, 0);
  private DataOutputStream _mainFile;
  // heap
  private int          _heapSize = 0;
  private MicroIndex[] _heap;
  
  private class MicroIndex
  {
    private int _currentRange;
    private final int _documentNumber;
    private int[] _concepts = new int[NConcepts + 1];
    private short _group;
    private short _ix;
    private IntegerArray _kTable = new IntegerArray();
    private IntegerArray _offsets = new IntegerArray();
    private IntegerArray _maxConcepts = new IntegerArray();
    private byte[] _data;
    private int _base;
    private int _limit;
    private int _nc;

    public MicroIndex(int documentNumber, byte[] positions, int index)
      throws Exception
    {
      _documentNumber = documentNumber;
      _data = positions;
      _base = index;
      openDocumentIndex();
    }
  
    public boolean smallerThan(MicroIndex other)
    {
      return _currentRange < other._currentRange ||
	_currentRange == other._currentRange &&
	_documentNumber < other._documentNumber;
    }

    private boolean next() throws Exception
    {
      if (_group <= _limit)
	{
	  int shift, index;
	  if (_group > 0)
	    {
	      index = _base + _offsets.at(_group - 1);
	      shift = _maxConcepts.at(_group - 1);
	    }
	  else
	    {
	      index = _base;
	      shift = 0;
	    }
	  _decmp.initReading(_data, index);
	  _nc = _decmp.ascendingDecode(_kTable.at(_group*2), shift, _concepts);
	  if (_group < _limit)
	    _concepts[_nc++] = _maxConcepts.at(_group);
	  _currentRange = _concepts[_ix = 0]/RANGE;
	  _group++;
	  return true;
	}
      else
	return false;
    }
  
    private void openDocumentIndex() throws Exception
    {
      int kk = _data[_base] & 0xFF;
      switch (kk >> 6)		// get type
	{
	case 0:			// single group, no extents
	  _decmp.initReading(_data, _base += 2);
	  _nc = _decmp.ascendingDecode(kk & 0x3F, 0, _concepts);
	  _currentRange = _concepts[_ix = 0]/RANGE;
	  _limit = 0;
	  _group = 1;
	  break;
      
	case 2:			// multi group, no extents
	  _decmp.initReading(_data, _base + 1);
	  _decmp.decode(kk & 0x3F, _kTable);
	  _decmp.ascDecode(_kTable.popLast(), _offsets);
	  _decmp.ascDecode(_kTable.popLast(), _maxConcepts);
	  _base += 1 + _decmp.bytesRead();
	  _limit = _maxConcepts.cardinality();
	  _group = 0;
	  next();
	  break;
      
	case 1:			// single group, extents
	case 3:			// multi group, extents
	  System.err.println("extents not yet implemented\n");
	  break;
	}
    }
  
    public boolean process(DocumentLists lists) throws Exception
    {
      boolean firstTime = true;
      while (true)
	{
	  short stop = lists.process(_documentNumber, _concepts, _nc, _ix,
				     firstTime);
	  if (stop < _nc)
	    {
	      _currentRange = _concepts[_ix = stop]/RANGE;
	      return true;
	    }
	  else if (next())
	    firstTime = false;
	  else
	    return false;
	}
    }
  }

  private DocumentLists(String indexDir) throws Exception
  {
    for (int i = 0; i < RANGE; i++)
      _arrays[i] = new IntegerArray();
    _mainFile = new DataOutputStream
      (new BufferedOutputStream
       (new FileOutputStream(indexDir + "DOCS")));
    // main work
    InputStream file =
      new BufferedInputStream(new FileInputStream(indexDir + "OFFSETS"));
    int k1 = file.read();
    IntegerArray array = new IntegerArray(4096);
    StreamDecompressor documents = new StreamDecompressor(file);
    documents.ascDecode(k1, array);
    int k2 = file.read();
    IntegerArray offsetArray = new IntegerArray(array.cardinality() + 1);
    StreamDecompressor offsets = new StreamDecompressor(file);
    offsets.ascDecode(k2, offsetArray);
    file.close();
    File listsFile = new File(indexDir + "POSITIONS");
    byte[] positions = new byte[(int)listsFile.length()];
    FileInputStream in = new FileInputStream(listsFile);
    in.read(positions);
    in.close();
    // build heap
    _heap = new MicroIndex[_heapSize = array.cardinality()];
    for (int i = 0; i < array.cardinality(); i++)
      _heap[i] = new MicroIndex(i, positions, offsetArray.at(i));
    debug(array.cardinality() + " documents");
    for (int i = _heapSize/2; i >= 0; i--)
      heapify(i);
    // process till exhausted
    while (true)
      if (_heap[0].process(this))
	heapify(0);
      else if (_heapSize > 1)
	{
	  _heap[0] = _heap[--_heapSize];
	  heapify(0);
	}
      else
	break;
    // closing
    flush();
    _mainFile.close();
    // compress index file
    DataOutputStream indexFile = new DataOutputStream
      (new BufferedOutputStream
       (new FileOutputStream(indexDir + "DOCS.TAB")));
    indexFile.write(_compr.compressAscending(_concepts)); // write k
    _compr.write(indexFile);
    _compr.clear();
    indexFile.write(_compr.minimize(_offsets, K)); // write k
    _compr.write(indexFile);
    indexFile.close();
  }

  public short process(int documentNumber, int[] concepts, int n, short start,
		       boolean firstTime)
    throws IOException
  {
    if (firstTime && concepts[start] >= _limit)
      flush();
    concepts[n] = _limit; // sentinel
    while (concepts[start] < _limit)
      _arrays[concepts[start++] - _minConcept].add(documentNumber);
    return start;
  }
  
  private void heapify(int i)
  {
    int r = (i + 1) << 1, l = r - 1;
    int smallest = l < _heapSize && _heap[l].smallerThan(_heap[i]) ? l : i;
    if (r < _heapSize && _heap[r].smallerThan(_heap[smallest]))
      smallest = r;
    if (smallest != i)
      {
	MicroIndex temp = _heap[smallest];
	_heap[smallest] = _heap[i];
	_heap[i] = temp;
	heapify(smallest);
      }
  }
  
  private void flush() throws IOException
  {
    for (int i = 0; i < RANGE; i++)
      if (_arrays[i].cardinality() > 0)
	{
	  _arrays[i].toDifferences(_diffs);
	  _mainFile.write(_compr.minimize(_diffs, K)); // write k
	  _offsets.add(_compr.byteCount() + 1);
	  _compr.write(_mainFile);
	  _concepts.add(_minConcept + i);
	  _arrays[i].clear();
	  _diffs.clear();
	  _compr.clear();
	}
    _limit += RANGE;
    _minConcept += RANGE;
  }

  public static void invert(String indexDir) throws Exception {
    new DocumentLists(indexDir);
  }

  public static void main(String[] args)
  {
    String indexDir = args[0];
    try {
      new DocumentLists(indexDir);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
    /**
     * Debug code
     */

    private boolean debug=false;
    private void debug(String msg) {
        if (debug) {
            System.err.println("DocumentLists: "+msg);
        }
    }

}
