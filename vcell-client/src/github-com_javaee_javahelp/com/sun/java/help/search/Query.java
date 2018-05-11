/*
 * @(#)Query.java	1.12 06/10/30
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

import java.util.*;
import javax.help.search.SearchQuery;
import javax.help.search.SearchItem;

class Query
{

  private double  _currentStandard;
  private final int _nColumns;
  private double[] _missingPenalty;
  private double[] _upperboundTemplate;
  private double[] _penalties;
  
  private final SearchEnvironment _env;
  private int                 _hitCounter;
  private boolean                 _vote;
  
  private HitStore  _store;
  
  public Query(SearchEnvironment env, int nColumns, double[] missingPenalties)
  {
    _env = env;
    _nColumns = nColumns;
    _missingPenalty = new double[nColumns];
    _upperboundTemplate = new double[nColumns];
    _penalties = missingPenalties;
    _hitCounter = 0;
    _vote = false;
    _currentStandard = (nColumns - 1) * 10.0 + 9.9999;
    _store = new HitStore(_currentStandard);
    for (int i = 0; i < _nColumns; i++)
      _missingPenalty[i] = missingPenalties != null
	? missingPenalties[i]
	: 10.0;
    makePenaltiesTable();
  }

  public void makeEvent(int n, SearchQuery searchQuery)
    {
	Vector hits = new Vector(n);

    if (n > 0)
      {
	int N = n;
	QueryHit qh = _store.firstBestQueryHit();
	n = N;
	for ( ; qh != null; qh = --n > 0 ? _store.nextBestQueryHit() : null)
	  try {
	    hits.addElement(_env.makeItem(qh));
	  }
	catch (Exception e) {
	  System.err.println(e + "hit not translated");
	}
      }
    // Params not know at this time
    searchQuery.itemsFound(true, hits);
    }

  public double lookupPenalty(int pattern) {
    return _penalties[pattern];
  }
  
  public double getOutOufOrderPenalty() {
    return 0.25;
  }
  
  public double getGapPenalty() {
    return 0.005;
  }
  
  public int getNColumns() {
    return _nColumns;
  }
  
  public boolean goodEnough(double penalty) {
    return penalty <= _currentStandard;
  }
  
  public int[] getConceptArrayOfNewHit(double penalty, Location loc)
  {
    QueryHit hit = new QueryHit(loc, penalty, _nColumns);
    _store.addQueryHit(hit);
    _hitCounter++;
    return hit.getArray();
  }

  public void resetForNextDocument()
  {
    _currentStandard = _store.getCurrentStandard();
    // "everything's missing"
    for (int i = 0; i < _nColumns; i++)
      _upperboundTemplate[i] = _missingPenalty[i];
    _vote = false;
  }

  public boolean vote()
  {
    double sum = 0.0;
    for (int i = 0; i < _nColumns; i++)
      sum += _upperboundTemplate[i];
    return _vote = (sum <= _currentStandard);
  }

  public void updateEstimate(int role, double penalty) {
    if (penalty < _upperboundTemplate[role])
      _upperboundTemplate[role] = penalty;
  }
  
  public void printHits(int n)
  {
    if (n > 0)
      {
	int N = n;
	QueryHit qh = _store.firstBestQueryHit();
	n = N;
	for ( ; qh != null; qh = --n > 0 ? _store.nextBestQueryHit() : null)
	  try {
	    System.out.println(_env.hitToString(qh));
	  }
	catch (Exception e) {
	  System.err.println(e + "hit not translated");
	}
      }
  }

  private void makePenaltiesTable()
  {
    int nPatterns = 1 << _nColumns;
    _penalties = new double[nPatterns];
    for (int i = 0; i < nPatterns; i++)
      _penalties[i] = computePenalty(i);
  }

  private double computePenalty(int n)
  {
    double penalty = 0.0;
    for (int i = 0; i < _nColumns; i++)
      if ((n & 1 << i) == 0)
	penalty += _missingPenalty[i];
    return penalty;
  }
}
