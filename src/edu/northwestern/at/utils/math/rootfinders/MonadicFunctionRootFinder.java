package edu.northwestern.at.utils.math.rootfinders;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.math.*;

/**	Interface implemented by monadic function root finders.
 */

public interface MonadicFunctionRootFinder
{
	/** MonadicFunctionRootFinder interface.
	 *
	 *	@param	x0						Left bracket value for root.
	 *	@param	x1						Right bracket value for root.
	 *									Not used by some root-finder
	 *									(e.g., Newton/Raphson),
	 *									set to same value as x0 in those cases.
	 *	@param	tol						Convergence tolerance.
	 *	@param	maxIter					Maximum number of iterations.
	 *	@param	function				MonadicFunction computes value for
	 *									function whose root is being sought.
	 *	@param	derivativeFunction		MonadicFunction computes derivative
	 *									value for function whose root is
	 *									being sought.  Currently used only
	 *									by Newton/Raphson, set to null for
	 *									other methods.
	 *	@param	convergenceTest			RootFinderConvergenceTest which
	 *									tests for convergence of the root-finding
	 *									process.
	 *	@param	iterationInformation	Method implementing the
	 *									RootFinderIterationInformation
	 *									interace.  Allows retrieval of
	 *									function, function derivative, and
	 *									iteration number for each iteration
	 *									in the root-finding process.
	 *									Can be set to null if you don't want
	 *									to get that information.
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
	);
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


