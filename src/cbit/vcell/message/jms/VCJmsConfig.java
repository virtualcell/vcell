package cbit.vcell.message.jms;

/**
 * XML properties shared between {@link VCActiveMQMonitor} and DeployVCell
 */
public interface VCJmsConfig {
	/**
	 * top level element
	 */
	public static final String DEPLOYPROP = "deployProperties";
	/**
	 * broker specific element
	 */
	public static final String PROVIDER = "jmsprovider";
	
	/**
	 * type of provider attribute 
	 */
	public static final String PROVIDER_TYPE = "provider";
	/**
	 * url for activemq 
	 */
	public static final String PROVIDER_URL = "url";
}
