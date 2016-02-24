package cbit.vcell.client.server;

/**
 * receive events from Reconnector
 * @author GWeatherby
 *
 */
public interface ReconnectListener {
	/**
	 * countdown until reconnect attempt. 0 if reconnect attempts stopped
	 * @param seconds
	 */
	public void refactorCountdown(long seconds);
}
