package org.vcell.restq.testresources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

/**
 * Starts an Artemis broker testcontainer with both AMQP 1.0 (5672) and OpenWire (61616) ports exposed.
 * Replaces Quarkus AMQP devservices so that cross-protocol tests can connect via OpenWire JMS
 * (the same protocol vcell-submit uses in production).
 */
public class ArtemisTestResource implements QuarkusTestResourceLifecycleManager {

    private GenericContainer<?> artemis;

    @Override
    public Map<String, String> start() {
        artemis = new GenericContainer<>("quay.io/artemiscloud/activemq-artemis-broker:1.0.25")
                .withExposedPorts(5672, 61616)
                .withEnv("AMQ_USER", "guest")
                .withEnv("AMQ_PASSWORD", "guest")
                .waitingFor(Wait.forListeningPort());
        artemis.start();

        String host = artemis.getHost();
        int amqpPort = artemis.getMappedPort(5672);
        int openwirePort = artemis.getMappedPort(61616);

        return Map.of(
                "amqp-host", host,
                "amqp-port", String.valueOf(amqpPort),
                "amqp-username", "guest",
                "amqp-password", "guest",
                "quarkus.amqp.devservices.enabled", "false",
                "artemis.openwire.host", host,
                "artemis.openwire.port", String.valueOf(openwirePort)
        );
    }

    @Override
    public void stop() {
        if (artemis != null) {
            artemis.stop();
        }
    }
}
