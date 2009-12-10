package org.vcell.sybil.util.event;

/*   Acceptor  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Accept objects of some class
 */

public interface Accepter<T> {

	public void accept(T t);
	
}
