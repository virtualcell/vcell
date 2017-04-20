package cbit.vcell.modeldb;

import org.vcell.util.document.KeyValue;

/**
 * visitor which is notified of every bad math load
 */
public interface BadMathVisitor extends VCDatabaseVisitor {
	/**
	 * @param vk that can't be loaded
	 * @param e received when trying to load
	 */
	public void unableToLoad(KeyValue vk, Exception e);

}