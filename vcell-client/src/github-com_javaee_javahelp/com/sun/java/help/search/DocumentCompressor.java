/*
 * @(#)DocumentCompressor.java	1.16 06/10/30
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

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

class DocumentCompressor
{
  static public final int NConceptsInGroup = 16;
  static public final int BitsInLabel = 4;
  static public final int DefaultSize = 32;
  
  private int _nGroups;
  private int _nExtents;
  private int _freeComp;
  private int _sizeComp = DefaultSize;
  private int _kk;
  private Compressor  _currentCompressor = null;
  private Compressor[] _compressors = new Compressor[DefaultSize];
  private Compressor   _kCompr = new Compressor();
  private Compressor   _lCompr = new Compressor();
  private Compressor   _mCompr = new Compressor();
  private Compressor   _posCompressor = new Compressor();
  private IntegerArray _kTable = new IntegerArray();// k's for the series
  private IntegerArray _lTable = new IntegerArray();// lengths of the C/P groups
  private IntegerArray _maxConcepts = new IntegerArray();// maximal concepts in CP
  private IntegerArray _concepts = new IntegerArray();
  private IntegerArray _documents = new IntegerArray();
  private IntegerArray _offsets = new IntegerArray();
  private IntegerArray _titles = new IntegerArray();
  
  private IntegerArray _positions = new IntegerArray();
  private IntegerArray _labels = new IntegerArray();
  private RandomAccessFile _posFile;

  public DocumentCompressor(URL url) throws Exception
  {
    URL posURL = new URL(url, "POSITIONS");
    if (isFileURL(posURL))
      {
	// this code should not be repeated
	try {
	  URL offURL = new URL(url, "OFFSETS");
	  URLConnection connect = offURL.openConnection();
	  BufferedInputStream in =
	    new BufferedInputStream(connect.getInputStream());
	
	  int k1 = in.read();
	  StreamDecompressor sddocs = new StreamDecompressor(in);
	  sddocs.ascDecode(k1, _documents);
	  int k2 = in.read();
	  StreamDecompressor sdoffsets = new StreamDecompressor(in);
	  sdoffsets.ascDecode(k2, _offsets);
	  // decompress titles' ids table
	  int k3 = in.read();
	  StreamDecompressor sdtitles = new StreamDecompressor(in);
	  sdtitles.decode(k3, _titles);
	  in.close();
	}
	catch (java.io.FileNotFoundException e) {;}
	_posFile = 
	    new RandomAccessFile(new File(posURL.toURI()),
				 "rw");
      }
    else
      throw new IOException();
  }
    
  private boolean isFileURL(URL url) {
    return url.getProtocol().equalsIgnoreCase("file");
  }

  public void close(String indexFile) throws java.io.IOException
  {
    _posFile.close();
    writeOutOffsets(indexFile);
  }

  public void compress(int docID, int titleID,
		       ConceptLocation[] locations, int count,
		       ConceptLocation[] extents, int extCount)
    throws java.io.IOException
  {
    long start = System.currentTimeMillis();
    encode(locations, count, NConceptsInGroup);
    if (extCount > 0)
      encodeExtents(extents, extCount);
    finalizeEncoding();
    debug((System.currentTimeMillis() - start) + " msec proc");
  
    int nBytes = byteCount();
    
    start = System.currentTimeMillis();
    long currentEnd = _posFile.length();
    _documents.add(docID);
    _offsets.add((int)currentEnd);
    _titles.add(titleID);
    _posFile.seek(currentEnd);
    writeOut(_posFile);
    debug((System.currentTimeMillis() - start) + " msec file");
    debug("nGroups = " + _nGroups);
  }

  private void writeOutOffsets(String indexFile) throws java.io.IOException
  {
    Compressor documents = new Compressor();
    int k1 = documents.compressAscending(_documents);
    Compressor offsets = new Compressor();
    int k2 = offsets.compressAscending(_offsets);
    Compressor titles = new Compressor();
    int k3 = titles.minimize(_titles, 8); // the starting k
    int nBytes = documents.byteCount();
    RandomAccessFile out = new RandomAccessFile(indexFile, "rw");
    out.seek(0);	// position at beginning
    out.write(k1);
    documents.write(out);
    out.write(k2);
    offsets.write(out);
    out.write(k3);
    titles.write(out);
    out.close();
  }
  
  private void encode(ConceptLocation[] locations, int count, int nConcepts)
  {
    final int initK = 4;
    // first sort by concept only
    ConceptLocation.sortByConcept(locations, 0, count);
    // using the fact that concepts are already sorted
    // count of groups of 'nConcepts'
    // go for differences directly
    clear();
    int conceptCounter = 0;
    int fromIndex = 0;
    int prevMax = 0;
    int last = locations[0].getConcept(); // init w/ first ID
    nextCompressor();
    _concepts.add(last);
    for (int i = 0;;)
      {
	for (; i < count && locations[i].getConcept() == last; i++)
	  locations[i].setConcept(conceptCounter);
	if (i == count)
	  {
	    if (_concepts.cardinality() > 0)
	      {
		++_nGroups;
		_kTable.add(_currentCompressor.minimize(_concepts, initK));
	      }
	    encodePositions(locations, fromIndex, i, BitsInLabel);
	    break;
	  }
	else				// new concept (group?)
	  {
	    if (++conceptCounter == nConcepts)
	      {
		++_nGroups;
		// we are looking at the beginning of a new group
		// last is maximal for the group just finished
		// it won't be stored in concepts array but maxConcepts
		_concepts.popLast();
		_maxConcepts.add(last - prevMax);
		prevMax = last;
		_kTable.add(_currentCompressor.minimize(_concepts, initK));
		encodePositions(locations, fromIndex, i, BitsInLabel);
		fromIndex = i;
		nextCompressor();
		_concepts.clear();
		conceptCounter = 0;
	      }
	    _concepts.add(locations[i].getConcept() - last);
	    last = locations[i].getConcept();
	  }
      }
  }
  
  private void encodePositions(ConceptLocation[] locations, int from, int to,
			       int cK)
  {
    final int initK = 3;
    int lastPos, k;
    // sort in place by positions only
    ConceptLocation.sortByPosition(locations, from, to);
    _positions.clear();
    _labels.clear();
    _positions.add(lastPos = locations[from].getBegin());
    _labels.add(locations[from].getConcept()); // now: a label
    // skip duplicates
    for (int i = from, j = from + 1; j < to; j++)
      if (locations[i].equals(locations[j]) == false)
	{
	  i = j;
	  _positions.add(locations[i].getBegin() - lastPos);
	  lastPos = locations[i].getBegin();
	  _labels.add(locations[i].getConcept()); // now: a label
	}
    // first find k by minimizing just positions w/o labels
    _kTable.add(k = _posCompressor.minimize(_positions, initK));
    _posCompressor.clear();
    _posCompressor.encode(_positions, _labels, k, cK);
    _currentCompressor.concatenate(_posCompressor);
  }
  
  private void encodeExtents(ConceptLocation[] extents, int extCount)
  {
    // side effects:
    // 'k3' added to _kTable
    // a number of compressors populated: header + lengths' lists
    final int initK = 4;
    int c = 0;
    IntegerArray concepts = new IntegerArray(extCount); // differences
    IntegerArray lengths = new IntegerArray();
    IntegerArray kTable = new IntegerArray();
    IntegerArray lTable = new IntegerArray();
    // reserve a compressor for concatenated tables
    nextCompressor();
    Compressor extentsHeader = _currentCompressor;
    for (int i = 0; i < extCount; i++)
      if (extents[i].getConcept() != c)
	{
	  if (c != 0)
	    {
	      _nExtents++;
	      nextCompressor();
	      kTable.add(_currentCompressor.minimize(lengths, initK));
	      lTable.add(_currentCompressor.byteCount());
	    }
	  concepts.add(extents[i].getConcept() - c);
	  c = extents[i].getConcept();
	  lengths.clear();
	  lengths.add(extents[i].getLength());
	}
      else
	lengths.add(extents[i].getLength());
    // last table of lengths
    nextCompressor();
    kTable.add(_currentCompressor.minimize(lengths, initK));
    lTable.add(_currentCompressor.byteCount());
    Compressor compressor1 = new Compressor();
    kTable.add(compressor1.minimize(lTable, initK));
    Compressor compressor2 = new Compressor();
    kTable.add(compressor2.minimize(concepts, initK));
    _kTable.add(extentsHeader.minimize(kTable, initK)); // k3
    extentsHeader.concatenate(compressor1);
    extentsHeader.concatenate(compressor2);
  }
  
  private void finalizeEncoding()
  {
    if (_nGroups > 1)
      {
	// if extents follow C/P groups we need the length of the last group
	int limit = _nExtents > 0 ? _freeComp : _freeComp - 1;
	for (int j = 0; j < limit; j++) // length of last not saved
	  _lTable.add(_compressors[j].byteCount());
	
	_kTable.add(_mCompr.minimize(_maxConcepts, 3));
	_kTable.add(_lCompr.minimize(_lTable, 3));
	_kk = _kCompr.minimize(_kTable, 3);
	_kCompr.concatenate(_lCompr);
	_kCompr.concatenate(_mCompr);
      }
    else if (_nGroups == 1 && _nExtents > 0)
      {
	// length of the single C/P group packed with k-s
	_kTable.add(_compressors[0].byteCount());
	_kk = _kCompr.minimize(_kTable, 3);
      }
    debug("compr: "+byteCount()+" bytes");
  }

  private void writeOut(DataOutput out) throws java.io.IOException
  {
    if (_nExtents == 0)
      if (_nGroups > 1)
	{
	  out.write(0x80 | _kk);
	  _kCompr.write(out); // concatenated k,l,m
	  for (int j = 0; j < _freeComp; j++)
	    _compressors[j].write(out);
	}
      else			// single group, no extents; code: 00
	{
	  out.write(_kTable.at(0)); // k1
	  out.write(_kTable.at(1)); // k2
	  _compressors[0].write(out);	// C/P
	}
    else				// extents
      {
	out.write((_nGroups > 1 ? 0xC0 : 0x40) | _kk);
	_kCompr.write(out);
	for (int j = 0; j < _freeComp; j++)
	  _compressors[j].write(out);
      }
  }
  
  private Compressor nextCompressor()
  {
    if (_freeComp == _sizeComp)
      {
	Compressor[] newArray = new Compressor[_sizeComp *= 2];
	System.arraycopy(_compressors, 0, newArray, 0, _freeComp);
	_compressors = newArray;
      }
    if (_compressors[_freeComp] == null)
      _compressors[_freeComp] = new Compressor();
    return _currentCompressor = _compressors[_freeComp++];
  }

  private int byteCount()
  {
    if (_nGroups == 1 && _nExtents == 0)
      return 2 + _compressors[0].byteCount();
    else
      {
	int result = 1;		// initial kk
	result += _kCompr.byteCount();
	for (int j = 0; j < _freeComp; j++)
	  result += _compressors[j].byteCount();
	return result;
      }
  }
  
  private void clear()
  {
    _nGroups = 0;
    _nExtents = 0;
    _kTable.clear();
    _lTable.clear();
    _concepts.clear();
    _maxConcepts.clear();
    _kCompr.clear();
    _lCompr.clear();
    _mCompr.clear();
    for (int i = 0; i < _sizeComp; i++)
      if (_compressors[i] != null)
	_compressors[i].clear();
    _freeComp = 0;
    _currentCompressor = null;
  }
    /**
     * Debug code
     */

    private static boolean debug=false;
    private static void debug(String msg) {
        if (debug) {
            System.err.println("DocumentCompressor: "+msg);
        }
    }

}
