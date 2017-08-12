package edu.northwestern.at.utils.math.distributions;

/*	Please see the license information at the end of this file. */
import edu.northwestern.at.utils.math.Constants;

/**	Fisher's F distribution functions.
 */

public class FishersF
{
	/** Compute probability for Fisher's F distribution.
	 *
	 *	@param	f		Percentage point of Fisher's F distribution
	 *	@param	dfn		Numerator degrees of freedom
	 *	@param	dfd		Denominator degrees of freedom
	 *
	 *	@return			The corresponding probabiity for
	 *					Fisher's F distribution.
	 *
	 *	@throws		    IllegalArgumentException
	 *                  	if dfn <= 0 or dfd <= 0 .
	 *
	 *	<p>
	 *	fprob(f) = incompleteBeta( dfd/(dfd*f*dfn), df/2, dfn/2 )
	 *	</p>
	 *
	 *	<p>
	 *	The result is accurate to about 14 decimal digits.
	 *	</p>
	 */

	public static double f( double f , double dfn , double dfd )
		throws IllegalArgumentException
	{
		double result = 0.0D;

		if ( ( dfn > 0.0D ) && ( dfd > 0.0D ) )
		{
			result	=
				Beta.incompleteBeta
				(
					dfd / ( dfd + f * dfn ) ,
					dfd / 2.0D ,
					dfn / 2.0D ,
					Constants.MAXPREC
				);
		}
		else
		{
			throw new IllegalArgumentException( "dfn or dfd <= 0" );
		}

		return result;
	}

	/** Compute percentage point for Fisher's F distribution.
	 *
	 *	@param	p		Probability level for percentage point.
	 *	@param	dfn		Numerator degrees of freedom.
	 *	@param	dfd		Denominator degrees of freedom.
	 *
	 *	@return			The corresponding probabiity for Fisher's F
	 *					distribution.
	 *
	 *	@throws			IllegalArgumentException
	 *                  	if p < 0 or p > 1 or dfn <= 0 or dfd <= 0 .
	 *
	 *	@throws			ArithmeticException
	 *						if incomplete beta evaluation fails
	 */

	public static double fInverse( double p, double dfn, double dfd )
		throws IllegalArgumentException
	{
		double result	= 0.0D;

		if ( ( dfn > 0.0D ) && ( dfd > 0.0D ) )
		{
			if ( ( p >= 0.0D ) && ( p <= 1.0D ) )
			{
	            result	=
	            	Beta.incompleteBetaInverse( 1.0D - p , dfn / 2.0D , dfd / 2.0D );

				if ( ( result >= 0.0D ) && ( result < 1.0D ) )
				{
					result  = result * dfd / ( dfn * ( 1.0D - result ) );
				}
				else
				{
					throw new ArithmeticException(
						"inverse incomplete beta evaluation failed" );
				}
			}
			else
			{
				throw new IllegalArgumentException( "p < 0 or p > 1" );
			}
		}
		else
		{
			throw new IllegalArgumentException( "dfn or dfd <= 0" );
		}

		return result;
	}

	/**	Make class non-instantiable but inheritable.
	 */

	protected FishersF()
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


