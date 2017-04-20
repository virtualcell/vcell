package net.sourceforge.interval.ia_math;

/**
* IAException.java 
*   -- classes implementing interval arithmetic runtime exceptions,
*      as part of the "ia_math library" version 0.1beta1, 10/97
* <p>
* Copyright (C) 2000 Timothy J. Hickey
* <p>
* License: <a href="http://interval.sourceforge.net/java/ia_math/licence.txt">zlib/png</a>
* <p>
 * These exceptions are thrown when there is a runtime
 * error in the Interval Arithmetic methods. The most
 * common error is when the interval is empty. In this
 * case the message is "Empty Interval".
 */

public class IAException extends RuntimeException {

  public IAException() {
    super();
  }

  public IAException(String s) {
    super(s);
  }

}
