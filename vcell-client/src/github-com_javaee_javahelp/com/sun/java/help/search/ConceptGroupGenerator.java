/*
 * @(#)ConceptGroupGenerator.java	1.6 06/10/30
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

import java.io.InputStream;

class ConceptGroupGenerator implements CompressorIterator
{
  private static final int NConceptsInGroup =
  DocumentCompressor.NConceptsInGroup;
  private static final int BitsInLabel =
  DocumentCompressor.BitsInLabel;
  
  private int           _last;
  private ConceptData[] _table;
  private Decompressor    _bits;
  private int          _k1;
  private final int     _k2 = BitsInLabel;
  private ConceptData  _cData;

  public ConceptGroupGenerator()
  {
    _k1 = 0;
    _table = new ConceptData[NConceptsInGroup];
    _last = 0;
    _bits = null;
  }
    
  public ConceptGroupGenerator(byte[] bytes, int index, int k)
  {
    _k1 = k;
    _table = new ConceptData[NConceptsInGroup];
    _last = 0;
    _bits = new ByteArrayDecompressor(bytes, index);
  }

  public void init(byte[] bytes, int index, int k)
  {
    _k1 = k;
    _bits = new ByteArrayDecompressor(bytes, index);
    _last = 0;
    for (int i = 0; i < NConceptsInGroup; i++)
      _table[i] = null;
  }

  public void addTerms(int index, ConceptData terms) {
    _table[index] = terms;
  }

  public int decodeConcepts(int k, int shift, int[] concepts) throws Exception {
    return _bits.ascendingDecode(k, shift, concepts);
  }

  public int position() {
    return _last;
  }
  
  public void value(int value) {
    _last += value;
  }
  
  boolean next() throws Exception
  {
    try {
      while (_bits.readNext(_k1, this))
	if ((_cData = _table[_bits.read(_k2)]) != null)
	  return true;
      return false;
    }
    catch (Exception e) {
      e.printStackTrace();
      System.err.println(_bits);
      System.err.println(_table);
      throw e;
    }
  }
  
  public void generateFillers(RoleFiller[] array) {
    _cData.generateFillers(array, _last);
  }
}
