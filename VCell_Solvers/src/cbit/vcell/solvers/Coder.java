package cbit.vcell.solvers;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.OutputStream;


public interface Coder
{
   public void code(OutputStream headerStream,
					OutputStream codeStream) throws Exception;
}
