package edu.northwestern.at.utils.math.rootfinders;

/*	Please see the license information at the end of this file. */
import edu.northwestern.at.utils.math.Constants;
import edu.northwestern.at.utils.math.MonadicFunction;

/** Find roots of equations using variants of the Method of Secants.
 *
 *	<p>
 *	The Method of Secants is a root-finding method which requires an
 *	initial interval [x0,x1] bracketing a root, that the function be
 *	continuous, and that the derivative exist at each point
 *	in the interval.
 *	</p>
 *
 *	<p>
 *	An updated estimate of the root value is given by the formula:
 *	</p>
 *
 *	<p>
 *	x[i+1]	= x[i] - ( f(x[i]) * ( x[i] - x[i-1]) ) / (f(x[i]) - f(x[i-1]))
 *	</p>
 *
 *	<p>
 *	To avoid problems with overflow and division by zero, we rewrite
 *	this update formula as follows:
 *	</p>
 *
 *	<p>
 *	r[i+1] = f(x[i+1]) / f(x[i]) )<br />
 *	x[i+1] = x[i] + ( r[i+1] / ( 1 - r[i+1] ) ) * ( x[i] - x[i-1] )
 *	</p>
 *
 *	<p>
 *	and only perform the division when the divisor ( 1 - s[i+1] ) is
 *	large enough.
 *	</p>
 *
 *	<p>
 *	The updated root estimate x[i+1] is the point where the secant
 *	to f(x) intersects the x axis, hence the name of the method.
 *	</p>
 *
 *	<p>
 *	The plain vanilla version of the Method of Secants does
 *	not keep the root bracketed between successive estimates and
 *	can diverge.
 *	The Method of False Position is a variant which
 *	ensures that the two estimates used to calculate the
 *	secant to f(x) always bracket the root.  It does this by
 *	retaining an old end point as long as necessary to keep
 *	the root bracketed.  This ensures convergence at the cost
 *	of extra iterations.  False Position modifies the
 *	root estimate selection as follows.
 *	</p>
 *
 *	<ul>
 *  <li>
 *	If ( f(x[i+1] * f(x[i])) < 0, ( x[i-1] , f(x[i-1]) ) is
 *	replaced by ( x[i] , f(x[i] ) as above.
 *	</li>
 *	<li>
 *	If ( f(x[i+1] * f(x[i])) > 0, ( x[i-1] , F(x[i-1]) )is
 *	replaced by ( x[i-1] , f(x[i-1])/2 ).
 *	</li>
 *	</ul>
 *
 *	<p>
 *	The Illinois method is a another variant which modifies the
 *	root estimate selection process as follows.
 *	</p>
 *
 *	<ul>
 *  <li>
 *	If ( f(x[i+1] * f(x[i])) < 0, ( x[i-1] , f(x[i-1]) ) is
 *	replaced by ( x[i] , f(x[i] ) as above.
 *	</li>
 *	<li>
 *	If ( f(x[i+1] * f(x[i])) > 0, ( x[i-1] , F(x[i-1]) )is
 *	replaced by ( x[i-1] , f(x[i-1])/2 ).
 *	</li>
 *	</ul>
 *
 *	<p>
 *	These modifications prevent retention of an endpoint and speed
 *	convergence in many cases.
 *	</p>
 *
 *	<p>
 *	The Illinois variant is the recommended version of the Method
 *	of Secants for practical use.  For most purposes, it is better to use
 *	{@link Brent} rather than the Method of Secants
 *	because Brent's Method handles ill-behaved functions
 *	better and converges more quickly in such cases.
 *	</p>
 */

public class Secant implements MonadicFunctionRootFinder
{
	/**	Constant defining the plain Method of Secants. */

	public static final int PLAIN			= 0;

	/**	Constant defining the plain Method of Secants. */

	public static final int FALSEPOSITION	= 1;

	/**	Constant defining the plain Method of Secants. */

	public static final int ILLINOIS		= 2;

	/**	Find root using the Method of Secants.
	 *
	 *	@param	x0						First approximation to root value.
	 *	@param	x1						Second approximation to root value.
	 *	@param	tol						Desired accuracy for root value.
	 *	@param	maxIter					Maximum number of iterations.
	 *	@param	function				Class implementing MonadicFunction
	 *									interface to provide function values.
	 *	@param	convergenceTest			RootFinderConvergenceTest which
 	 *									tests for convergence of the root-finding
 	 *									process.
	 *	@param	iterationInformation	Class implementing
	 *									RootFinderIterationInformation
	 *									for retrieving information about
	 *									each iteration of root finding
	 *									process.  Set to null if you don't
	 *									want this information.
	 *
	 *  @param	variant					Variant of the method of secants
	 *									to use.
	 *									=	PLAIN :
	 *										Unmodified Method of Secants.
	 *									=	FALSEPOSITION :
	 *										Method of False Position.
	 *									=	ILLINOIS :
	 *										Illinois method.
	 *
	 *	@return							Approximation to root of function.
	 *
	 *	@throws							IllegalArgumentException
	 *										if [x0,x1] cannot be expanded
	 *										to bracket a root (if FALSEPOSITION
	 *										variant selected) or function
	 *										is null.
	 *
	 *	<p>
	 *	When the FALSEPOSITION variant is chosen, we try to ensure that the
	 *	the two initial approximations bracket the root by expanding
	 *	the interval as needed.
	 *	</p>
	 */

	public static double secant
	(
		double x0 ,
		double x1 ,
		double tol ,
		int maxIter ,
		MonadicFunction function ,
		RootFinderConvergenceTest convergenceTest ,
		RootFinderIterationInformation iterationInformation ,
		int variant
	)
		throws IllegalArgumentException
	{
		/* Calculated value of x at each iteration. */

		double x;

		/* Function value at x0 . */

		double f0;

		/* Function value at x1 . */

		double f1;

		/* Function value at calculated value of x . */

		double fx;

		/* Ratio of function values at two successive approximants. */

		double r;

		/* Root, if within desired tolerance. */

		double root;
								// Make sure function is not null.

		if ( function == null )
		{
			throw new IllegalArgumentException(
				"Function cannot be null" );
		}
								// Set initial function values.

		f0 = function.f( x0 );
		f1 = function.f( x1 );

								// Test if there is a root in the
								// provided interval, when the FALSEPOSITION
								// variant is chosen.
								//
								// For this to be true, the function values
								// at the left and right end of the interval
								// must have different signs.  If the signs
								// are the same, try expanding the interval
								// geometrically and see if we can find a
								// new interval bracketing the root.

		if ( variant == FALSEPOSITION )
		{
			if (	( ( f0 > 0.0 ) && ( f1 > 0.0 ) ) ||
					( ( f0 < 0.0 ) && ( f1 < 0.0 ) ) )
			{
				double[] bracket	= new double[]{ x0 , x1 };

				if ( !BracketRoot.bracketRoot( bracket, function, maxIter, 1.6 ) )
				{
								// Give up if we can't find a new interval
								// bracketing a root.

					throw new IllegalArgumentException(
						"Cannot expand interval [x0,x1] to contain root." );
				}
								// Use new bracketing interval.
				else
				{
					x0	= bracket[ 0 ];
					x1	= bracket[ 1 ];
					f0	= function.f( x0 );
					f1	= function.f( x1 );
				}
			}
        }
								// If abs(f1) < abs(f0), swap the
								// function values and corresponding
								// approximants.

		if ( Math.abs( f1 ) < Math.abs( f0 ) )
		{
			fx	= f0;
			f0	= f1;
			f1	= fx;

			x	= x0;
			x0	= x1;
			x1	= x;
		}
								// If f0 is exactly zero, the corresponding
								// x0 is the root we are seeking.
		if ( f0 == 0.0D )
		{
			return x0;
		}
								// Begin method of secants loop.

		for( int iter = 0; iter < maxIter; iter++ )
		{
								// Get next approximant, taking care
								// to avoid zero divide and overflow.
			r	= f1 / f0;

			if ( Math.abs( 1.0D - r ) >= Constants.MACHEPS ) r	= r / ( 1.0D - r );

			x	= x1 + r * ( x1 - x0 );

								//	DO NOT USE the following expression
								//	for updating the estimate!

//			x	= x1 - f1 * ( ( x1 - x0 ) / ( f1 - f0 ) );

								// Evaluate function at this approximant.

			fx	= function.f( x );

								// Update last two function values for next
								// iteration if needed.

			if ( variant == FALSEPOSITION )
			{
				if ( ( fx * f1 ) <= 0.0D )
				{
					x0	= x1;
					x1	= x;
					f0	= f1;
					f1	= fx;
				}
				else
				{
					x1	= x;
					f1	= fx;
				}
			}
			else if ( variant == ILLINOIS )
			{
				if ( ( fx * f1 ) <= 0.0D )
				{
					x0	= x1;
					x1	= x;
					f0	= f1;
					f1	= fx;
				}
				else
				{
					x1	= x;
					f0	= f0 * 0.5D;
					f1	= fx;
				}
            }
            else // Plain vanilla variant
            {
				x0	= x1;
				x1	= x;
				f0	= f1;
				f1	= fx;
			}
								// Post updated iteration information.

			if ( iterationInformation != null )
			{
				iterationInformation.iterationInformation(
					x1 , f1 , Double.NaN , iter );
			}
								// Check if new approximant is accurate enough.

			if	( convergenceTest.converged( x1 , x0 , f1 , tol , tol ) ) break;
		}

		return x1;
	}

	/**	Find root using the Method of Secants.
	 *
	 *	@param	x0						First approximation to root value.
	 *	@param	x1						Second approximation to root value.
	 *	@param	tol						Desired accuracy for root value.
	 *	@param	maxIter					Maximum number of iterations.
	 *	@param	function				Class implementing MonadicFunction
	 *									interface to provide function values.
	 *  @param	variant					Variant of the method of secants
	 *									to use.
	 *									=	PLAIN :
	 *										Unmodified Method of Secants.
	 *									=	FALSEPOSITION :
	 *										Method of False Position.
	 *									=	ILLINOIS :
	 *										Illinois method.
	 *
	 *	@return							Approximation to root of function.
	 *
	 *	@throws							IllegalArgumentException
	 *										if [x0,x1] cannot be expanded
	 *										to bracket a root or function
	 *										is null.
	 *
	 *	<p>
	 *	This implementation always starts by attempting to expand the root-
	 *	bracketing interval to enclose a root.
	 *	</p>
	 */

	public static double secant
	(
		double x0 ,
		double x1 ,
		double tol ,
		int maxIter ,
		MonadicFunction function ,
		int variant
	)
		throws IllegalArgumentException
	{
		return secant(
			x0 , x1 , tol , maxIter , function ,
			new StandardRootFinderConvergenceTest() , null , variant );
	}

	/**	Find root using the Method of Secants.
	 *
	 *	@param	x0						First approximation to root value.
	 *	@param	x1						Second approximation to root value.
	 *	@param	tol						Desired accuracy for root value.
	 *	@param	maxIter					Maximum number of iterations.
	 *	@param	function				Class implementing MonadicFunction
	 *									interace to provide function values.
	 *	@param	iterationInformation	Class implementing
	 *									RootFinderIterationInformation
	 *									for retrieving information about
	 *									each iteration of root finding
	 *									process.  Set to null if you don't
	 *									want this information.
	 *
	 *	@return							Approximation to root of function.
	 *
	 *	@throws	IllegalArgumentException
	 *									if [x0,x1] cannot be expanded
	 *									to bracket a root or function
	 *									is null.
	 *
	 *	<p>
	 *	The Illinois variant of the Method of Secants is used to find
	 *	the function.
	 *	</p>
	 */

	public static double secant
	(
		double x0 ,
		double x1 ,
		double tol ,
		int maxIter ,
		MonadicFunction function ,
		RootFinderIterationInformation iterationInformation
	)
		throws IllegalArgumentException
	{
		return secant(
			x0 , x1 , tol , maxIter , function ,
			new StandardRootFinderConvergenceTest() ,
			iterationInformation ,
			ILLINOIS );
	}

	/**	Find root using the Method of Secants.
	 *
	 *	@param	x0			First approximation to root value.
	 *	@param	x1			Second approximation to root value.
	 *	@param	tol			Desired accuracy for root value.
	 *	@param	maxIter		Maximum number of iterations.
	 *	@param	function	Class implementing MonadicFunction interface
	 *						to provide function values.
	 *
	 *	@return				Approximation to root of the function.
	 *
	 *	@throws	IllegalArgumentException
	 *		if [x0,x1] cannot be expanded to bracket a root or
	 *		function is null.
	 *
	 *	<p>
	 *	The Illinois variant of the Method of Secants is used to find
	 *	the function.
	 *	</p>
	 */

	public static double secant
	(
		double x0 ,
		double x1 ,
		double tol ,
		int maxIter ,
		MonadicFunction function
	)
		throws IllegalArgumentException
	{
		return secant(
			x0 , x1 , tol , maxIter , function ,
			new StandardRootFinderConvergenceTest() ,
			null , ILLINOIS );
	}

	/**	Find root using the Method of Secants.
	 *
	 *	@param	x0			First approximation to root value.
	 *	@param	x1			Second approximation to root value.
	 *	@param	function	Class implementing MonadicFunction interface
	 *						to provide function values.
	 *
	 *	@return				Approximation to root of the function.
	 *
	 *	@throws	IllegalArgumentException
	 *		if [x0,x1] cannot be expanded to bracket a root or
	 *		function is null.
	 *
	 *	<p>
	 *	The Illinois variant of the Method of Secants is used to find
	 *	the function.  The tolerance used is Constants.MACHEPS, and up
	 *	to 100 iterations are attempted.
	 *	</p>
	 */

	public static double secant
	(
		double x0 ,
		double x1 ,
		MonadicFunction function
	)
		throws IllegalArgumentException
	{
		return secant(
			x0 , x1 , Constants.MACHEPS , 100 , function ,
			new StandardRootFinderConvergenceTest() ,
			null , ILLINOIS );
	}

	/** Implementation for {@link MonadicFunctionRootFinder} interface.
	 */

	public double findRoot
	(
		double x0 ,
		double x1 ,
		double tol ,
		int maxIter ,
		MonadicFunction function ,
		MonadicFunction derivativeFunction ,
		RootFinderConvergenceTest convergenceTest ,
		RootFinderIterationInformation iterationInformation
	)
		throws IllegalArgumentException
	{
		return secant(
			x0 , x1 , tol , maxIter , function ,
			convergenceTest , iterationInformation , ILLINOIS );
	}

	/** Constructor if RootFinder interface used.
	 */

	public Secant()
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


