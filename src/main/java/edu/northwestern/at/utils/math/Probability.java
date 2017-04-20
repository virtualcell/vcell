package edu.northwestern.at.utils.math;

/*	Please see the license information at the end of this file. */

import java.io.Serializable;

//import edu.northwestern.at.utils.XCloneable;
import edu.northwestern.at.utils.math.ArithUtils;

/** Probability: stores and operates on log probabilties.
 *
 *	<p>
 *	When working with large numbers of small probabilities,
 *	it is helpful to work in "log space", e.g., the natural log
 *	of the probabilities, to avoid problems with overflow and
 *	underflow.  This class provides for storing a probability in
 *	log form, and adding, substracting, multiplying, and dividing
 *	the log probabilities to minimize accuracy loss.
 *	</p>
 *
 *	<p>
 *	Probabilities must be floating point values from zero through one
 *	inclusive, e.g., [0 , 1].
 *	</p>
 *
 *	<p>
 *	A probability of zero is stored as -infinity, which
 *	correctly propagates through arithmetic operations.  The
 *	value of exp(-infinity) is 0.0 as it should be.
 *	</p>
 */

public class Probability implements /* XCloneable, */ Comparable, Serializable
{
	/** Natural log of the probability.
	 */

	protected double logProbability;

	/**	Zero probability.
	 */

	public static final Probability ZERO_PROBABILITY	=
		new Probability( 0.0D );

	/**	One probability.
	 */

	public static final Probability ONE_PROBABILITY		=
		new Probability( 1.0D );

	/**	Create a Probability.
	 */

	public Probability()
		throws IllegalArgumentException
	{
		logProbability			= Double.NEGATIVE_INFINITY;
	}

	/**	Create a Probability.
	 *
	 *	@param	probability		Probability value in range 0 through 1
	 *							inclusive.
	 */

	public Probability( double probability )
		throws IllegalArgumentException
	{
		setProbability( probability );
	}

	/**	Set the probability.
	 *
	 *	@param	probability		Probability value in range 0 through 1
	 *							inclusive.
	 *
	 *	@throws	IllegalArgumentException
	 *		If probability is not in the range 0 through 1 inclusive.
	 */

	public void setProbability( double probability )
		throws IllegalArgumentException
	{
								//	Check that probability is in [0,1].

		if ( ( probability < 0.0D )  || ( probability > 1.0 ) )
		{
			throw new IllegalArgumentException
			(
				"Bad probability (" + probability + "), should be a number" +
				" from 0 through 1 inclusive."
			);
		}
								//	Save the probability.
								//	Note:  the following tests for 0
								//	and 1 must be exact!

		if ( probability == 0.0D )
		{
			logProbability	= Double.NEGATIVE_INFINITY;
		}
		else if ( probability == 1.0D )
		{
			logProbability	= 0.0D;
		}
		else
		{
			logProbability	= Math.log( probability );
		}
	}

	/**	Set the log probability.
	 *
	 *	@param	logProbability	Log probability value in range -infinity
	 *							through 0 inclusive.
	 *
	 *	@throws	IllegalArgumentException
	 *		If probability is not in the range -infinity through 0 inclusive.
	 */

	public void setLogProbability( double logProbability )
		throws IllegalArgumentException
	{
								//	Check that probability is in
								//	[-infinity, 0] inclusive.

		if ( logProbability > 0.0D )
		{
			throw new IllegalArgumentException
			(
				"logProbability should be a number from " +
				"-infinity through 0 inclusive."
			);
		}
								//	Save the log probability.

		this.logProbability			= logProbability;
	}

	/**	Get probability.
	 *
	 *	@return		probability from 0 through 1 inclusive.
	 *
	 *	<p>
	 *	Note:  This can overflow!
	 *	</p>
	 */

	public double getProbability()
	{
		return Math.exp( logProbability );
	}

	/**	Get log probability.
	 *
	 *	@return		log probability from -infinity through 0 inclusive.
	 */

	public double getLogProbability()
	{
		return logProbability;
	}

	/**	Multiply this probability by another.
	 *
	 *	@param	otherProbability	The other probability.
	 *
	 *	@return						Resulting probability.
	 */

	public Probability multiply( Probability otherProbability )
	{
		Probability result	= new Probability();

		result.setLogProbability
		(
			this.logProbability + otherProbability.logProbability
		);

		return result;
	}

	/**	Multiply this probability by two others.
	 *
	 *	@param	otherProbability1	First other probability.
	 *	@param	otherProbability2	Second other probability.
	 *
	 *	@return						Resulting probability.
	 */

	public Probability multiply
	(
		Probability otherProbability1 ,
		Probability otherProbability2
	)
	{
		Probability result	= new Probability();

		result.setLogProbability
		(
			this.logProbability +
				otherProbability1.logProbability +
				otherProbability2.logProbability
		);

		return result;
	}

	/**	Multiply this probability by three others.
	 *
	 *	@param	otherProbability1	First other probability.
	 *	@param	otherProbability2	Second other probability.
	 *	@param	otherProbability3	Third other probability.
	 *
	 *	@return						Resulting probability.
	 */

	public Probability multiply
	(
		Probability otherProbability1 ,
		Probability otherProbability2 ,
		Probability otherProbability3
	)
	{
		Probability result	= new Probability();

		result.setLogProbability
		(
			this.logProbability +
				otherProbability1.logProbability +
				otherProbability2.logProbability +
				otherProbability3.logProbability
		);

		return result;
	}

	/**	Return string version of probability.
	 *
	 *	@return		Probability converted to string.
	 */

	public String toString()
	{
		return getProbability() + "";
	}

	/**	Get clone of this object.
	 *
	 *	@return		Clone of this object.
	 */

	public Object clone()
	{
		return new Probability( logProbability );
	}

	/**	Compare this probability to another.
	 *
	 *	@param	object	The other probability.
	 */

	public int compareTo( Object object )
	{
		return Double.compare
		(
			this.logProbability ,
			((Probability)object).logProbability
		);
	}

	/**	Test for this probability equaling another.
	 *
	 *	@param	object	Other object, presumably a Probability.
	 *
	 *	@return			true if other object is equal to this one.
	 */

	public boolean equals( Object object )
	{
		return
			Double.compare
			(
				this.logProbability ,
				((Probability)object).logProbability
			) == 0;
	}

    /**	Get a hash code for this object.
     *
     *	@return		The hash code.
     *
     *	<p>
     *	Hash code same as for a Double.
     *	</p>
     */

	public int hashCode()
	{

		long bits	= Double.doubleToLongBits( logProbability );
		return (int)( bits ^ ( bits >>> 32 ) );
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



