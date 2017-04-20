package edu.northwestern.at.utils.math.rootfinders;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.math.*;

/**	Find a root of a function using Brent's method which combines quadratic
 *	interpolation with the method of bisection.
 *
 *	<p>
 *	Brent's method requires as input an initial interval [x0,x1] which
 *	brackets one (or an odd number) of roots.
 *	</p>
 *
 *	<p>
 *	During each iteration Brent's method constructs an interpolation curve
 *	to approximate the function.  For the initial iteration this is a
 *	simple linear interpolation using the initially supplied endpoints of
 *	the interval bracketing the root.  For subsequent iterations the
 *	interpolation curve is an inverse quadratic fit to the last three points.
 *	The intercept of the interpolation curve with the x-axis provides the
 *	root approximation.  If this value lies within the bounds of the current
 *	root bracketing interval the interpolation point is accepted and used
 *	to narrow the interval.  If the interpolated value lies outside the
 *	bounds of the current interval the algorithm switches to bisection
 *	steps to narrow the interval.  The algorithm switches between
 *	quadratic interpolation and bisection as often as necessary to ensure
 *	convergence.
 *  </p>
 *
 *	<p>
 *	The best estimate of the root is taken from the most recent interpolation
 *	or bisection after the value is found to a specified tolerance
 *	or a specified number of iterations is exhausted.
 *	</p>
 *
 *	<p>
 *	See Brent, Richard P.
 *	<strong>Algorithms for minimization without derivatives.</strong>
 *	Mineola, N.Y. : Dover Publications, 2002.
 *	</p>
 *	<p>
 *	and
 *	</p>
 *	<p>
 *	Brent, R. P.
 *	<em>An algorithm with guaranteed convergence for finding
 *	the zero of a function.</em>  <strong>The Computer Journal</strong>
 *	vol. 14, no. 4, pp. 422-425.
 *	</p>
 *
 *	<p>
 *	This Java code is a fairly straightforward translation of
 *	Brent's original Algol 60 version as published in his paper
 *	in The Computer Journal.
 *	</p>
 */

public class Brent implements MonadicFunctionRootFinder
{
	/** Find root of a function using Brent's method.
	 *
	 *	@param x0						Left endpoint of interval
	 *									containing root.
	 *
	 *	@param x1						Right endpoint of interval
	 *									containing root.
	 *
	 *	@param tol						Convergence tolerance to which root
	 *									is computed.
     *
	 *	@param maxIter					Maximum number of iterations.
	 *
	 *	@param function					The function whose root to find.
	 *
	 *	@param	convergenceTest			RootFinderConvergenceTest which
 	 *									tests for convergence of the root-finding
 	 *									process.
 	 *
	 *	@param iterationInformation		Class implementing
	 *									RootFinderIterationInformation
	 *									for retrieving information about
	 *									each iteration of root finding
	 *									process.  Set to null if you don't
	 *									want this information.
	 *
	 *	@return							Root of function in interval [x0,x1] .
	 *
	 *	@throws							InvalidArgumentException
	 *										when [x0,x1] does not contain
	 *										root and the attempt to expand
	 * 										the interval to contain a root
	 *										fails.
	 *
	 *	<p>
	 *	Function must have an odd # of roots in the interval [x0,x1] .
	 *	</p>
	 */

	public static double brent
	(
		double x0,
		double x1,
		double tol,
		int maxIter,
		MonadicFunction function ,
		RootFinderConvergenceTest convergenceTest ,
		RootFinderIterationInformation iterationInformation
	)
		throws IllegalArgumentException
	{
		final double EPS = Constants.MACHEPS / 2.0D;

		int iter;

		double min1;
		double min2;
		double fc;
		double p;
		double q;
		double r;
		double s;
		double tol1;
		double xm;

		double a	= x0;
		double b	= x1;
		double c	= x1;
		double d	= 0;
		double e	= 0;
								//	Evaluate the function at each end of
								//	the interval purported to bracket a root.

		double fa	= function.f( a );
		double fb	= function.f( b );

								//	Test if there is a root in this interval.
								//	For this to be true, the function values
								//	at the left and right end of the interval
								//	must have different signs.  If the signs
								//	are the same, try expanding the interval
								//	geometrically and see if we can find a
								//	new interval bracketing the root.

		if (	( ( fa > 0.0 ) && ( fb > 0.0 ) ) ||
				( ( fa < 0.0 ) && ( fb < 0.0 ) ) )
		{
			double[] bracket	= new double[]{ a , b };

			if ( !BracketRoot.bracketRoot( bracket, function, maxIter, 1.6 ) )
			{
								//	Give up if we can't find a new interval
								//	bracketing a root.

				throw new IllegalArgumentException(
					"Cannot expand interval [x0,x1] to contain root." );
			}
								//	Use new bracketing interval.
			else
			{
				a	= bracket[ 0 ];
				b	= bracket[ 1 ];
				fa	= function.f( a );
				fb	= function.f( b );
			}
        }

		fc	= fb;
								//	Begin iteration loop.

		for ( iter = 1; iter <= maxIter; iter++ )
		{
			if	(
					( ( fb > 0.0 ) && ( fc > 0.0 ) ) ||
					( ( fb < 0.0 ) && ( fc < 0.0 ) )
				)
			{
								//	Rename a, b, c so that root lies in [b,c] .
				c	= a;
				fc	= fa;
				e	= d = b - a;
			}

			if ( Math.abs( fc ) < Math.abs( fb ) )
			{
							    //	Rename a, b, c so that b is best root
							    //	estimate.
				a	= b;
				b	= c;
				c	= a;
				fa	= fb;
				fb	= fc;
				fc	= fa;
			}
								// Post updated iteration information.

			if ( iterationInformation != null )
			{
				iterationInformation.iterationInformation(
					b , fb , Double.NaN , iter );
			}
								//	Convergence tolerance check.

			tol1	= 2.0 * EPS * Math.abs( b ) + 0.5 * tol;
			xm		= 0.5 * ( c - b );
/*
			if ( ( Math.abs( xm ) <= tol1 ) || ( fb == 0.0 ) )
			{
				return b;
			}
*/
			if ( convergenceTest.converged(
				xm , 0.0D , fb , tol1 , 0.0D ) ) return b;

			if (	( Math.abs( e ) >= tol1 ) &&
					( Math.abs( fa ) > Math.abs( fb ) ) )
			{
								//	Try linear interpolation or
								//	inverse quadratic method

				s	= fb / fa;

								//	Only a & b are distinct: perform
								//	linear interpolation (e.g.,
								//	secant method)
				if ( a == c )
				{
					p	= 2.0 * xm * s;
					q	= 1.0 - s;
				}
								//	a, b, c are distinct:
								//	use inverse quadratic method
				else
				{
					q	= fa / fc;
					r	= fb / fc;
					p	= s * ( 2.0 * xm * q * ( q - r ) - ( b - a ) * ( r - 1.0 ) );
					q	= ( q - 1.0 ) * ( r - 1.0 ) * ( s - 1.0 );
				}
								//	Determine if next estimate is acceptable.

				if ( p > 0.0 ) q = -q;

				p		= Math.abs( p );

				min1	= 3.0 * xm * q - Math.abs( tol1 * q ) ;
				min2	= Math.abs( e * q );

				if ( ( 2.0 * p ) < ( min1 < min2 ? min1 : min2 ) )
				{
								//	Accept linear interpolation or
								//	inverse quadratic estimate.
					e	= d;
					d	= p / q;
				}
				else
				{
								//	Reject linear interpolation or
								//	inverse quadratic: use bisection.
					d	= xm;
					e	= d;
				}
			}
			else
			{
								//	Bounds decreasing too slowly: use bisection.
				d	= xm;
				e	= d;
			}
								//	Update a, the previous best root estimate.
			a	= b;
			fa	= fb;
								//	Update b, the latest best root estimate.

			if ( Math.abs( d ) > tol1 )
				b	+= d;
			else
				b	+= ( xm > 0.0 ? tol1 : -tol1 );

			fb = function.f( b );
		}
								//	Return last estimate when the number
								//	of iterations is exceeded.  This value
								//	will not be as accurate as requested.
		return b;
	}

	/** Find root of a function using Brent's method.
	 *
	 *	@param x0						Left endpoint of interval
	 *									containing root.
	 *
	 *	@param x1						Right endpoint of interval
	 *									containing root.
	 *
	 *	@param tol						Convergence tolerance to which root
	 *									is computed.
     *
	 *	@param maxIter					Maximum number of iterations.
	 *
	 *	@param function					The function whose root to find.
	 *
	 *	@return							Root of function in interval [x0,x1] .
	 *
	 *	@throws							InvalidArgumentException
	 *										when [x0,x1] does not contain
	 *										root and the attempt to expand
	 * 										the interval to contain a root
	 *										fails.
	 *
	 *	<p>
	 *	Function must have an odd # of roots in the interval [x0,x1] .
	 *	</p>
	 */

	public static double brent
	(
		double x0,
		double x1,
		double tol,
		int maxIter,
		MonadicFunction function
	)
		throws IllegalArgumentException
	{
		return brent(
			x0 , x1 , tol , maxIter , function ,
			new StandardRootFinderConvergenceTest() ,
			null );
	}

	/** Find root of a function using Brent's method.
	 *
	 *	@param x0						Left endpoint of interval
	 *									containing root.
	 *
	 *	@param x1						Right endpoint of interval
	 *									containing root.
	 *
	 *	@param function					The function whose root to find.
	 *
	 *	@return							Root of function in interval [x0,x1] .
	 *
	 *	@throws							InvalidArgumentException
	 *										when [x0,x1] does not contain
	 *										root and the attempt to expand
	 * 										the interval to contain a root
	 *										fails.
	 *
	 *	<p>
	 *	Function must have an odd # of roots in the interval [x0,x1] .
	 *	Up to 100 iterations are attempted with a convergence tolerance
	 *	set to Constants.MACHEPS .
	 *	</p>
	 */

	public static double brent
	(
		double x0,
		double x1,
		MonadicFunction function
	)
		throws IllegalArgumentException
	{
		return brent(
			x0 , x1 , Constants.MACHEPS , 100 , function ,
			new StandardRootFinderConvergenceTest() ,
			null );
	}

	/** Implementation for {@link MonadicFunctionRootFinder} interface.
	 */

	public double findRoot
	(
		double x0 ,
		double x1 ,
		double tol ,
		int maxIter,
		MonadicFunction function ,
		MonadicFunction derivativeFunction ,
		RootFinderConvergenceTest convergenceTest ,
		RootFinderIterationInformation iterationInformation
	)
		throws IllegalArgumentException
	{
		return brent(
			x0 , x1 , tol , maxIter , function ,
			convergenceTest , iterationInformation );
	}

	/** Constructor if RootFinder interface used.
	 */

	public Brent()
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


