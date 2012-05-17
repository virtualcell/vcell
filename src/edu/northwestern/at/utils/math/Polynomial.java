package edu.northwestern.at.utils.math;

/*	Please see the license information at the end of this file. */

/**	Polynomial functions.
 */

public class Polynomial
{
	/** Evaluate a polynomial expression using Horner's method.
	 *
	 *	@param	polycoefs	Array of polynomial coefficients.
	 *						The coefficients should be ordered
	 *						with the constant term first and the
	 *						coefficient for the highest powered term
	 *						last.
	 *
	 *	@param	x	    	Value for which to evaluate polynomial.
	 *
	 *	@return				Polynomial evaluated using Horner's method.
	 *
	 *	<p>
	 *	Horner's method is given by the following recurrence relation:
	 *	c[i]*x^i + ... + c[1]*x + c[0] = c[0] + x*(c[i-1]*x^[i-1] + ... + c[1])
	 *	</p>
	 *
	 *	<p>
	 *	Horner's method avoids loss of precision which can occur
	 *	when the higher-power values of x are computed directly.
	 *	</p>
	 */

	public static double hornersMethod( double polycoefs[] , double x )
	{
		double result	= 0.0;

		for ( int i = polycoefs.length - 1 ; i >= 0 ; i-- )
		{
			result	= result * x + polycoefs[ i ] ;
        }

		return result;
	}

	/**	Evaluates a Chebyschev series.
	 *
	 *	@param	coeffs		The Chebyschev polynomial coefficients.
	 *
	 *	@param	x			The value for which to evaluate
	 *						the polynomial.
	 *
	 *	@return				The Chebyschev polynomial evaluated
	 *						at x.
	 */

	public static double evaluateChebyschev
	(
		double coeffs[] ,
		double x
	)
	{
		double b0;
		double b1;
		double b2;
		double twox;

		int		i;

		b0		= 0.0D;
		b1		= 0.0D;
		b2		= 0.0D;

		twox	= x + x;

		for ( i = coeffs.length - 1 ;  i >= 0 ;  i-- )
		{
			b2	= b1;
			b1	= b0;
			b0	= twox * b1 - b2 + coeffs[ i ];
		}

		return 0.5D * ( b0 - b2 );
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected Polynomial()
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


