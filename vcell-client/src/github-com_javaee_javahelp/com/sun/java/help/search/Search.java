/*
 * @(#)Search.java	1.17 06/10/30
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
import javax.help.search.SearchQuery;

class Search
{
  private static final int InitNConcepts = 128;
  
  private SearchEnvironment _env;
  private int _max;
  private int _nConcepts;
  private int _nQueries;
  private ConceptGroupGenerator _firstGenerator = new ConceptGroupGenerator();
  private int[] _concepts = new int[DocumentCompressor.NConceptsInGroup];
  private int _free2;
  private int _size2;
  private int _startingIndex = 0;
  private int _limit = 0;
  private Query[] _query;
  private ConceptData[] _conceptData;
  private GeneratorHeap _genHeap = new GeneratorHeap();
  private int _document;
  private byte[] _data = null;
  private int _base = 0;	// index into _data
  private NextDocGeneratorHeap _nextDocGenHeap = new NextDocGeneratorHeap();
  private IntegerArray _kTable = new IntegerArray();
  private IntegerArray _offsets = new IntegerArray();
  private IntegerArray _maxConcepts = new IntegerArray();

  private IntegerArray _docConcepts = new IntegerArray();
  private IntegerArray _queryMasks = new IntegerArray();
  private int _maxHitsToShow = 100;

  public Search(SearchEnvironment se, int nColumns)
  {
    _env = se;
    _nQueries = 1;
    _query = new Query[_nQueries];

    _size2 = InitNConcepts;
    _free2 = 0;
    _conceptData = new ConceptData[_size2];

    _query[0] = new Query(se, nColumns, null);
    /*
      _query[1] = new Query(se, 3, null);
      _query[2] = new Query(se, 3, null);
      _query[3] = new Query(se, 3, null);
      */
  }

  public void addTerm(int col, int concept, double score, int query)
  {
    if (_env.occursInText(concept))
      {
	if (_free2 == _size2)
	  {
	    ConceptData[] newArray = new ConceptData[_size2 *= 2];
	    System.arraycopy(_conceptData, 0, newArray, 0, _free2);
	    _conceptData = newArray;
	  }
	_conceptData[_free2++] =
	  new ConceptData(concept, col, score, query,
			  _query[query].getNColumns());
      }
  }
  
  public void startSearch(SearchQuery searchQuery)
  {
    //  fprintf(stderr, "startSearch: setup\n");
    int i, j;
    // set up ConceptData lists
    // order search terms
    quicksort(0, _free2 - 1);
    // remove duplicates
    for (i = 0; i < _free2 - 1; i = j)
      for (j = i + 1; j < _free2; j++)
	if (_conceptData[i].crqEquals(_conceptData[j]))
	  _conceptData[j] = null;
	else
	  i = j;
    // create lists
    for (i = 0; i < _free2 - 1; i = j)
      for (j = i + 1; j < _free2; j++)
	if (_conceptData[j] != null)
	  if (_conceptData[i].cEquals(_conceptData[j]))
	    {
	      _conceptData[i].addLast(_conceptData[j]);
	      _conceptData[j] = null;
	    }
	  else
	    i = j;
    // densify
    for (i = 0; i < _free2 - 1; i++)
      if (_conceptData[i] == null)
	for (j = i + 1; j < _free2; j++)
	  if (_conceptData[j] != null)
	    {
	      _conceptData[i] = _conceptData[j];
	      _conceptData[j] = null;
	      break;
	    }
    // set up new document generators
    _nextDocGenHeap.reset();
    for (i = 0; i < _free2 && _conceptData[i] != null; i++)
      {
	NextDocGenerator gen = new NextDocGenerator(_conceptData[i], _env);
	try {
	  gen.first();
	  if (gen.getDocument() != NonnegativeIntegerGenerator.END)
	    {
	      _conceptData[i].
		setConceptLength(_env.
				 getConceptLength(_conceptData[i].getConcept()));
	      _nextDocGenHeap.addGenerator(gen);
	    }
	}
	catch (Exception e) {
	  e.printStackTrace();
	}
      }
    _nextDocGenHeap.start(); 
    searchDocument();
    if (searchQuery == null) {
	printResults(_maxHitsToShow);
    } else {
	_query[0].makeEvent(_maxHitsToShow, searchQuery);
    }
  }

  private void searchDocument()
  {
    RoleFiller[] start = new RoleFiller[_nQueries];
    do {
      try {
	switch (nextDocument(start))
	  {
	  case 0:		// multi group
	    _genHeap.start(start);
	    while (_genHeap.next(start))
	      ;
	    break;
	    
	  case 1:		// single group
	    if (_firstGenerator.next())
	      {
		_firstGenerator.generateFillers(start);
		while (_firstGenerator.next())
		  _firstGenerator.generateFillers(start);
	      }
	    break;
	    
	  case 2:		// reached the end
	    return;
	  }
      }
      catch (Exception e) {
	e.printStackTrace(System.err);
	continue;
      }
      
      for (int i = 0; i < _nQueries; i++)
	{
	  RoleFiller next;
	  if ((next = start[i]) != null && next != RoleFiller.STOP)
	    next.scoreList(_query[i], _document);
	  
	}
      _genHeap.reset();
    }
    while (_nextDocGenHeap.isNonEmpty());
  }
  
  // will be called for increasing values of c
  // searches index interval [_startingIndex, _nConcepts]
  // saves state in _startingIndex
  private int indexOf(int concept) throws Exception
  {
    int i = _startingIndex, j = _nConcepts, k;
    while (i <= j)
      if (_concepts[k = (i + j)/2] < concept)
	i = k + 1;
      else if (concept < _concepts[k])
	j = k - 1;
      else
	{
	  _startingIndex = k + 1;
	  return k;
	}
    throw new Exception("indexOf " + concept + " not found");
  }

  private void printResults(int nHits)
  {
    for (int q = 0; q < _nQueries; q++)
      {
	System.out.println("query " + q);
	if (_query[q] != null)
	  _query[q].printHits(nHits);
      }
  }

  private ConceptGroupGenerator makeGenerator(int group) throws Exception
  {
    int shift, index;
  
    if (group > 0)
      {
	index = _base + _offsets.at(group - 1);
	shift = _maxConcepts.at(group - 1);
      }
    else
      {
	index = _base;
	shift = 0;
      }
  
    // initialize generator
    ConceptGroupGenerator gen =
      new ConceptGroupGenerator(_data, index, _kTable.at(2*group + 1));
    // decode concept table
    _nConcepts = gen.decodeConcepts(_kTable.at(2*group), shift, _concepts);
    if (group < _limit)
      _max = _concepts[_nConcepts] = _maxConcepts.at(group);
    else
      _max = _concepts[_nConcepts - 1];
    _genHeap.addGenerator(gen);
    _startingIndex = 0;		// in _concepts; lower search index
    return gen;
  }

  // returns true if multigroup
  private boolean openDocumentIndex(int docID) throws Exception
  {
      // The data is only the data for this document id. Thus the base is set
      // to zero.
    _data = _env.getPositions(docID);
    _base = 0;
    _startingIndex = 0;
    int kk = _data[_base] & 0xFF, k2;
    switch (kk >> 6)		// get type
      {
      case 0:			// single group, no extents
	k2 = _data[_base + 1];
	_firstGenerator.init(_data, _base += 2, k2);
	// decode concept table
	_nConcepts = _firstGenerator.decodeConcepts(kk & 0x3F, 0, _concepts);
	return false;
      
      case 2:			// multi group, no extents
	_kTable.clear();
	_offsets.clear();
	_maxConcepts.clear();
	ByteArrayDecompressor compr =
	  new ByteArrayDecompressor(_data, _base + 1);
	compr.decode(kk & 0x3F, _kTable);
	compr.ascDecode(_kTable.popLast(), _offsets);
	compr.ascDecode(_kTable.popLast(), _maxConcepts);
	_base += 1 + compr.bytesRead();
	_limit = _maxConcepts.cardinality();
	return true;
      
      case 1:			// single group, extents
      case 3:			// multi group, extents
	throw new Exception("extents not yet implemented\n");
      }
    return false;
  }

  private int nextDocument(RoleFiller[] start) throws Exception
  {
    while (_nextDocGenHeap.isNonEmpty())		// still something to do
      {
	for (int i = 0; i < _nQueries; i++)
	  if (_query[i] != null)
	    _query[i].resetForNextDocument();
      
	// gather all concepts this document has and store associated conceptData
	int index = 0;
	_document = _nextDocGenHeap.getDocument();
	_docConcepts.clear();
	_queryMasks.clear();
	do {
	  _docConcepts.add(_nextDocGenHeap.getConcept());
	  _queryMasks.add(_nextDocGenHeap.getQueryMask());
	  (_conceptData[index++] = _nextDocGenHeap.getTerms()).runBy(_query);
	  _nextDocGenHeap.step();
	}
	while (_nextDocGenHeap.atDocument(_document));

	// if there is no saturation model, some query will always vote YES
	// and so every document will be opened
	// even if this case, however, savings can be achieved by not generating fillers
	// for some queries (not scoring, etc)
	// and, with more care, creation of some GroupGenerators can be avoided
	// saturating queries with lots of good hits will lead to best results
	int voteMask = 0;
	for (int i = 0; i < _nQueries; i++)
	  if (_query[i] != null)
	    if (_query[i].vote())
	      {
		start[i] = null;	// normal reset
		voteMask |= 1 << i;
	      }
	    else
	      start[i] = RoleFiller.STOP;	// prohibit setting
      
	// we may eliminate some ConceptGroupGenerators
	// those which would be used only by Queries which voted NO
	if (voteMask != 0)		// need to open up document
	  {
	    ConceptGroupGenerator gen;
	    // !!! don't gather Fillers for disinterested Queries
	    if (openDocumentIndex(_document)) // multi group
	      {
		// set up all needed generators
		int i = 0;
		while ((_queryMasks.at(i) & voteMask) == 0)
		  ++i;
		//		assert(i < index);
		int c = _docConcepts.at(i);
		int group = 0;
		// find first group
		while (c > _maxConcepts.at(group) && ++group < _limit)
		  ;
		gen = makeGenerator(group);
		gen.addTerms(indexOf(c), _conceptData[i]);
	      
		for (++i; i < index; i++)
		  if ((_queryMasks.at(i) & voteMask) > 0)
		    {
		      c = _docConcepts.at(i);
		      if (c > _max)	// need to find another group
			{
			  //			  assert(group < _limit);
			  while (c > _maxConcepts.at(group) && ++group < _limit)
			    ;
			  gen = makeGenerator(group);
			}
		      gen.addTerms(indexOf(c), _conceptData[i]);
		    }
		return 0;
	      }
	    else			// single group
	      {
		for (int i = 0; i < index; i++)
		  if ((_queryMasks.at(i) & voteMask) != 0)
		    _firstGenerator.addTerms(indexOf(_docConcepts.at(i)),
					     _conceptData[i]);
		return 1;
	      }
	  }
      }
    return 2;
  }

  // part of quicksearch
  private int partition(int p, int r)
  {
    ConceptData x = _conceptData[p];
    int i = p - 1, j = r + 1;
    while (true)
      {
	while (x.compareWith(_conceptData[--j]))
	  ;
	while (_conceptData[++i].compareWith(x))
	  ;
	if (i < j)
	  {
	    ConceptData t = _conceptData[i];
	    _conceptData[i] = _conceptData[j];
	    _conceptData[j] = t;
	  }
	else
	  return j;
      }
  }

  private void quicksort(int p, int r)
  {
    if (p < r)
      {
	int q = partition(p, r);
	quicksort(p, q);
	quicksort(q + 1, r);
      }
  }
}
