/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.matrix;

import java.util.Vector;

/*   Matrix  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   A two dimensional matrix
 */

public class Matrix<E> {

	protected int iXMin, iYMin, iXMax, iYMax, iDX, iDY;
	protected Vector<E> v;
	
	public Matrix(int iXMinNew, int iYMinNew, int iXMaxNew, int iYMaxNew) {
		iXMin = iXMinNew;
		iYMin = iYMinNew;
		iXMax = iXMaxNew;
		iYMax = iYMaxNew;
		iDX = (1 + iXMax - iXMin);
		iDY = (1 + iYMax - iYMin);
		v = new Vector<E>(iDX*iDY);
		for(int iX = iXMin; iX <= iXMax; iX++) {
			for(int iY = iYMin; iY <= iYMax; iY++) {
				v.add(null);
			}			
		}

	}
	
	protected int iV(int iX, int iY) { return (iX - iXMin) + iDX*(iY - iYMin); }
	public void put(E e, int iX, int iY) { v.set(iV(iX, iY), e); }
	public E get(int iX, int iY) { return v.get(iV(iX, iY)); }
	
}
