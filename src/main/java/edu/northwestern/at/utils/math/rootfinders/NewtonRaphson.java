package edu.northwestern.at.utils.math.rootfinders;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.math.*;

/** Find roots of equations using Newton/Raphson iteration.
 *
 *	<p>
 *	The Method of NewtonRaphson is a root-finding method which requires an
 *	initial estimate x0 for a root and that the function be
 *	continuous and everywhere differentiable.
 *	</p>
 *
 *	<p>
 *	If the derivative of the function whose root is being sought
 *	is difficult or expensive to compute, the Method of Secants or
 *	Brent's Method is a better choice.  If the function is not
 *	everywhere differentiable, Bisection is the method to use.
 *	</p>
 */

public class NewtonRaphson implements MonadicFunctionRootFinder
{
	/**	Find root using the Method of Newton/Raphson.
	 *
	 *	@param	x0						First approximation to root value.
	 *	@param	tol						Desired accuracy for root value.
	 *	@param	maxIter					Maximum number of iterations.
	 *	@param	function				Class implementing MonadicFunction
	 *									interface to provide function values.
	 *	@param	derivativeFunction		Class implementing MonadicFunction
	 *									interface to provide function
	 *									derivative values.
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
	 *	@return							Approximation to root.
	 *
	 *	@throws							IllegalArgumentException
	 *										if function or
	 *										derivativeFunction is null.
	 */

	public static double newtonRaphson
	(
		double x0 ,
		double tol ,
		int maxIter ,
		MonadicFunction function ,
		MonadicFunction derivativeFunction ,
		RootFinderConvergenceTest convergenceTest ,
		RootFinderIterationInformation iterationInformation
	)
		throws IllegalArgumentException
	{
		/* Calculated value of x at each iteration. */

		double x;

		/* Function value at calculated value of x . */

		double fx;

		/* Function derivative value at calculated value of x . */

		double dfx;

		/* Previous function value. */

		double xPrevious;
								// Make sure function and derivativeFunction are not null.

		if ( function == null )
		{
			throw new IllegalArgumentException(
				"Function cannot be null" );
		}

		if ( derivativeFunction == null )
		{
			throw new IllegalArgumentException(
				"Derivative function cannot be null" );
		}
								// Begin Newton/Raphson iteration loop.
		x	= x0;

		for ( int iter = 0 ; iter < maxIter ; iter++ )
		{
								// Compute new approximant from first order
								// Taylor series.
        	xPrevious	= x;

			fx			= function.f( xPrevious );
			dfx			= derivativeFunction.f( xPrevious );

			x			= xPrevious - ( fx / dfx );

								// Post updated iteration information.

			if ( iterationInformation != null )
			{
				iterationInformation.iterationInformation( x , fx , dfx , iter );
			}
								// See if updated function value is close
								// enough to root to stop iterations.

			if	( convergenceTest.converged
			(
				x , xPrevious , fx , tol , tol )
			)
			{
				break;
			}
		}

		return x;
	}

	/**	Find root using the Method of Newton/Raphson.
	 *
	 *	@param	x0						First approximation to root value.
	 *	@param	tol						Desired accuracy for root value.
	 *	@param	maxIter					Maximum number of iterations.
	 *	@param	function				Class implementing MonadicFunction
	 *									interface to provide function values.
	 *	@param	derivativeFunction		Class implementing MonadicFunction
	 *									interface to provide function
	 *									derivative values.
	 *
	 *	@return							Approximation to root.
	 *
	 *	@throws							IllegalArgumentException
	 *										if function or
	 *										derivativeFunction is null.
	 */

	public static double newtonRaphson
	(
		double x0 ,
		double tol ,
		int maxIter ,
		MonadicFunction function ,
		MonadicFunction derivativeFunction
	)
		throws IllegalArgumentException
	{
		return newtonRaphson(
			x0 , tol , maxIter , function , derivativeFunction ,
			new StandardRootFinderConvergenceTest() , null );
	}

	/**	Find root using the Method of Newton/Raphson.
	 *
	 *	@param	x0						First approximation to root value.
	 *	@param	function				Class implementing MonadicFunction
	 *									interface to provide function values.
	 *	@param	derivativeFunction		Class implementing MonadicFunction
	 *									interface to provide function
	 *									derivative values.
	 *
	 *	@return							Approximation to root.
	 *
	 *	@throws							IllegalArgumentException
	 *										if function or
	 *										derivativeFunction is null.
	 *
	 *	<p>
	 *	Up to 100 iterations are attempted with the convergence tolerance
	 *	set to Constants.MACHEPS .
	 *	</p>
	 */

	public static double newtonRaphson
	(
		double x0 ,
		MonadicFunction function ,
		MonadicFunction derivativeFunction
	)
		throws IllegalArgumentException
	{
		return newtonRaphson(
			x0 , Constants.MACHEPS , 100 , function , derivativeFunction ,
			new StandardRootFinderConvergenceTest() , null );
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
		return newtonRaphson(
			x0 , tol , maxIter , function , derivativeFunction ,
			convergenceTest , iterationInformation );
	}

	/** Constructor if RootFinder interface used.
	 */

	public NewtonRaphson()
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


