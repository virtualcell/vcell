package edu.northwestern.at.utils.math.distributions;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.math.*;
import edu.northwestern.at.utils.math.rootfinders.*;

/**	Chi-square distribution functions.
 */

public class ChiSquare
{
	/** Compute probability for chi-square distribution.
	 *
	 *	@param	chisq		Percentage point of chi-square distribution
	 *	@param	df			Degrees of freedom
	 *
	 *	@return				The corresponding probabiity for the
	 *						chi-square distribution.
	 *
	 *	<p>
	 *	The probability is determined using the relationship
	 *	between the incomplete gamma CDF and the chi-square
	 *	distribution:
	 *	</p>
	 *
	 *	<p>
	 *	chisqprob(chisq,df)	= incompleteGamma( chisq/2, df/2 )
	 *	</p>
	 *
	 *	<p>
	 *	The result is accurate to about 14 decimal digits.
	 *	</p>
	 */

	public static double chisquare( double chisq , double df )
	{
		return 1.0D - Gamma.incompleteGamma( chisq / 2.0D , df / 2.0D );
	}

	/** Compute percentage point for chi-square distribution.
	 *
	 *	@param	p	Probability level for which to compute
	 *				percentage point
	 *
	 *	@param	df	Degrees of freedom
	 *
	 *	@return		The corresponding percentage for the
	 *				chi-square distribution.
	 *
	 *	@throws		IllegalArgumentException
	 *					If df <= 0 or p very near zero or
	 *					p very near one.  A tolerance of
	 *					1.0e-15 is used for testing the
	 *					value of p.
	 *
	 *	<p>
	 *	Implements the method of Best and Roberts (1975).
	 *	The result accuracy varies, but should be good to at least 10
	 *	decimal digits in all cases.
	 *	</p>
	 */

	public static double chisquareInverse( double p , double df )
	{
		int dPrec	= Constants.MAXPREC - 1;
		double epsz	= Math.pow( 10 , -dPrec );
		int maxIter	= 100;

		double xx   ;
		double c    ;
		double ch   ;
		double q    ;
		double p1   ;
		double p2   ;
		double t    ;
		double x    ;
		double b    ;
		double a    ;
		double g    ;
		double s1   ;
		double s2   ;
		double s3   ;
		double s4   ;
		double s5   ;
		double s6   ;
		double cPrec;
        double cinv ;

		int iter;
								/* Test arguments for validity */

		if ( ( p < epsz ) || ( p > ( 1.0D - epsz ) ) )
		{
			throw new IllegalArgumentException( "p bad" );
		}

		if ( df <= 0.0D )
		{
			throw new IllegalArgumentException( "v bad" );
		}
								/* Initialize */
		p		= 1.0D - p;
		xx		= df / 2.0D;

		g      = Gamma.logGamma( xx );
		c      = xx - 1.0D;

								/* Starting approx. for small chi-square */

		if ( df < ( -1.24D * Math.log( p ) ) )
		{
			ch	=
				Math.pow
				(
					p * xx * Math.exp( g + xx * Constants.LN2 ) ,
					( 1.0D / xx )
				);

			if ( ch < epsz )
			{
				return ch;
			}
		}
								/* Starting approx. for df <= .32 */

		else if ( df <= 0.32D )
		{
			ch	= 0.4D;
			a	= Math.log( 1.0D - p );

			do
			{
				q	= ch;

				p1	= 1.0D + ch * ( 4.67D + ch );
				p2	= ch * ( 6.73D + ch * ( 6.66D + ch ) );

				t	 =
					-0.5D + ( 4.67D + 2.0D * ch ) / p1 -
					( 6.73D + ch * ( 13.32D + 3.0D * ch ) ) / p2;

				ch	=
					ch - ( 1.0D - Math.exp( a + g + 0.5D * ch + c * Constants.LN2 ) *
					p2 / p1 ) / t;
			}
            while ( Math.abs( q / ch - 1.0D ) > 0.01D );
		}
		else
		{
								/* Starting approx. using normal approximation. */

			x	= Normal.normalInverse( p );

			p1	= 2.0D / ( 9.0D * df );

			ch	= df * Math.pow( ( x * Math.sqrt( p1 ) + 1.0D - p1 ) , 3 );

								/* Starting approx. for P --> 1 */

			if ( ch > ( 2.2D * df + 6.0D ) )
			{
				ch	= -2.0D * ( Math.log( 1.0D - p ) - c * Math.log( 0.5D * ch ) + g );
			}
		};
								/* We have starting approximation.    */
								/* Begin improvement loop.            */
		do
		{
								/* Get probability of current approx. */
								/* to percentage point.               */
			q	= ch;
			p1	= 0.5D * ch;
			p2	= p - Gamma.incompleteGamma( p1, xx, dPrec, maxIter );

								/* Calculate seven-term Taylor series */
								/* and improve initial estimate using */
								/* the series.                        */

			t	=
				p2 *
				Math.exp(
					xx * Constants.LN2 + g + p1 - c * Math.log( ch ) );

			b	= t / ch;

			a	= 0.5D * t - b * c;

            s1 = ( 210.0D + a * ( 140.0D + a * ( 105.0D + a * ( 84.0D + a *
                  ( 70.0D + 60.0D * a ) ) ) ) ) / 420.0D;

            s2 = ( 420.0D + a * ( 735.0D + a * ( 966.0D + a * ( 1141.0D +
                    1278.0D * a ) ) ) ) / 2520.0D;

            s3 = ( 210.0D + a * ( 462.0D + a * ( 707.0D + 932.0D * a ) ) )
                  / 2520.0D;

            s4 = ( 252.0D + a * ( 672.0D + 1182.0D * a ) + c * ( 294.0D + a *
                  ( 889.0D + 1740.0D * a ) ) ) / 5040.0D;

            s5 = ( 84.0D + 264.0D * a + c * ( 175.0D + 606.0D * a ) ) / 2520.0D;

            s6 = ( 120.0D + c * ( 346.0D + 127.0D * c ) ) / 5040.0D;

            ch = ch + t * ( 1.0 + 0.5 * t * s1 - b * c * ( s1 - b *
                  ( s2 - b * ( s3 - b * ( s4 - b * ( s5 - b * s6 ) ) ) ) ) );
		}
		while ( Math.abs( ( q / ch ) - 1.0D ) > epsz );

		return ch;
	}

	/**	Make class non-instantiable but inheritable.
	 */

	protected ChiSquare()
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


