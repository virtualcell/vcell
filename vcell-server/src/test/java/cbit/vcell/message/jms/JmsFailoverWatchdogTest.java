package cbit.vcell.message.jms;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Fast")
public class JmsFailoverWatchdogTest {

	/**
	 * Spin up an embedded broker, attach the watchdog to a connection backed by
	 * a tightly-bounded failover URL, then stop the broker. After the failover
	 * transport exhausts its reconnect budget, the TransportListener fires with
	 * an IOException and the watchdog should run the injected terminal handler.
	 */
	@Test
	public void attach_firesTerminalHandlerWhenFailoverGivesUp() throws Exception {
		BrokerService broker = new BrokerService();
		broker.setPersistent(false);
		broker.setUseJmx(false);
		broker.setUseShutdownHook(false);
		TransportConnector connector = broker.addConnector("tcp://localhost:0");
		broker.start();
		broker.waitUntilStarted();
		int port = connector.getConnectUri().getPort();

		String url = "failover:(tcp://localhost:" + port + ")"
				+ "?maxReconnectAttempts=2"
				+ "&startupMaxReconnectAttempts=-1"
				+ "&initialReconnectDelay=50"
				+ "&maxReconnectDelay=100";
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
		Connection connection = factory.createConnection();
		try {
			CountDownLatch terminal = new CountDownLatch(1);
			new JmsFailoverWatchdog(terminal::countDown).attach(connection);
			connection.start();

			broker.stop();
			broker.waitUntilStopped();

			assertTrue(terminal.await(10, TimeUnit.SECONDS),
					"watchdog terminal handler should fire after maxReconnectAttempts exhausted");
		} finally {
			try { connection.close(); } catch (Exception ignored) {}
			try { broker.stop(); } catch (Exception ignored) {}
		}
	}
}
