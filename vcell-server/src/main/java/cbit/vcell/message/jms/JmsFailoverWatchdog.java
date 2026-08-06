package cbit.vcell.message.jms;

import java.io.IOException;
import java.util.Objects;

import javax.jms.Connection;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.transport.TransportListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Attaches a {@link TransportListener} to an {@link ActiveMQConnection} that
 * runs a caller-supplied terminal action when the underlying failover transport
 * gives up (e.g. {@code maxReconnectAttempts} exhausted, or a "Timer already
 * cancelled" inactivity-monitor wedge).
 *
 * Production wiring should pass {@link #jvmExitOnTerminal()} so K8s recycles
 * the pod. Tests can pass any {@link Runnable} (e.g. a {@code CountDownLatch}
 * countdown) to verify the watchdog fires without killing the JVM.
 */
public final class JmsFailoverWatchdog {

	private static final Logger lg = LogManager.getLogger(JmsFailoverWatchdog.class);

	private final Runnable onTerminal;

	public JmsFailoverWatchdog(Runnable onTerminal) {
		this.onTerminal = Objects.requireNonNull(onTerminal, "onTerminal");
	}

	/**
	 * Exit the JVM with status 1 so K8s recycles the pod. Use this only at
	 * server entry points where pod recycling is the desired response to a
	 * sustained JMS outage (e.g. SimDataServer).
	 */
	public static JmsFailoverWatchdog jvmExitOnTerminal() {
		return new JmsFailoverWatchdog(() -> System.exit(1));
	}

	/**
	 * Default for everything else: the TransportListener still logs the
	 * interrupted/resumed/terminal events but takes no further action.
	 */
	public static JmsFailoverWatchdog logOnly() {
		return new JmsFailoverWatchdog(() -> {});
	}

	public void attach(Connection connection) {
		if (!(connection instanceof ActiveMQConnection)) {
			return;
		}
		((ActiveMQConnection) connection).addTransportListener(new TransportListener() {
			@Override
			public void onCommand(Object command) {
			}
			@Override
			public void onException(IOException error) {
				lg.fatal("JMS transport unrecoverable, invoking terminal handler", error);
				onTerminal.run();
			}
			@Override
			public void transportInterupted() {
				lg.warn("JMS transport interrupted, failover reconnecting");
			}
			@Override
			public void transportResumed() {
				lg.info("JMS transport resumed");
			}
		});
	}
}
