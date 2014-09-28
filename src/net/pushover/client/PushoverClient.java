package net.pushover.client;

import java.util.Set;

/**
 * Pushover.net client interface.
 * 
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 * 
 * @since Dec 18, 2012
 */
public interface PushoverClient {

    /**
     * Push a message to the service
     * 
     * @param msg
     * @return a {@link Status}
     * @throws PushoverException
     */
    Status pushMessage(PushoverMessage msg) throws PushoverException;

    /**
     * Retrieve a list of available sounds from the service
     * 
     * @return
     * @throws PushoverException
     */
    Set<PushOverSound> getSounds() throws PushoverException;
}
