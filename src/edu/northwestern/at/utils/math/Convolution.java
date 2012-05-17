package edu.northwestern.at.utils.math;

/*	Please see the license information at the end of this file. */

public class Convolution
{
	/**	Convolute data values with a mask.
  	 *
 	 *	@param	v	Data values.
 	 *	@param	m	Convolution mask.
 	 *
 	 *	@return		Convoluted values.
 	 */

	public static double[] convolute( double[] v , double[] m )
	{
		final int mid = m.length / 2;
		double[] r = new double[v.length];
		double sum;

		for (int i=0, ie=v.length; i<ie; i++)
		{
			sum = 0;
			for (int jv=i-mid, j=0, je=m.length; j<je; j++, jv++)
			{
				if (jv >= 0 && jv < v.length)
				{
					sum += m[j];
					r[i] += v[jv] * m[j];
				}
			}

			r[i] /= sum;
		}

		return r;
	}

	/* Don't allow instantiation but allow overrides. */

	protected Convolution()
	{
	}

}

/*
Copyright (c) 2008, 2009 by Northwestern University.
All rights reserved.

Developed by:
   Academic and Research Technologies
   Northwestern University
   http://www.it.northwestern.edu/about/departments/at/

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal with the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimers.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimers in the documentation and/or other materials provided
      with the distribution.

    * Neither the names of Academic and Research Technologies,
      Northwestern University, nor the names of its contributors may be
      used to endorse or promote products derived from this Software
      without specific prior written permission.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE CONTRIBUTORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE SOFTWARE.
*/



