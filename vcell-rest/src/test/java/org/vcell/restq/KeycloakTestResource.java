package org.vcell.restq;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom Keycloak test resource that patches the master realm's sslRequired=NONE
 * before Quarkus starts. This works around the "HTTPS required" error in Keycloak 26+
 * when Dev Services tries to acquire an admin token over HTTP.
 *
 * See: https://github.com/quarkusio/quarkus/issues/38884
 */
public class KeycloakTestResource implements QuarkusTestResourceLifecycleManager {

    private GenericContainer<?> keycloak;

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.5.4";
    private static final String REALM = "quarkus";
    private static final String CLIENT_ID = "backend-service";
    private static final String CLIENT_SECRET = "secret";

    @Override
    public Map<String, String> start() {
        keycloak = new GenericContainer<>(DockerImageName.parse(KEYCLOAK_IMAGE))
                .withExposedPorts(8080)
                .withEnv("KC_BOOTSTRAP_ADMIN_USERNAME", ADMIN_USER)
                .withEnv("KC_BOOTSTRAP_ADMIN_PASSWORD", ADMIN_PASSWORD)
                .withEnv("KC_HTTP_ENABLED", "true")
                .withEnv("KC_HOSTNAME_STRICT", "false")
                .withEnv("JAVA_OPTS", "-XX:+IgnoreUnrecognizedVMOptions -XX:UseSVE=0")
                .withCommand("start-dev")
                .withStartupTimeout(Duration.ofMinutes(3));

        keycloak.start();

        String baseUrl = "http://" + keycloak.getHost() + ":" + keycloak.getMappedPort(8080);

        // Authenticate kcadm against localhost inside the container
        execOrThrow(
                "/opt/keycloak/bin/kcadm.sh", "config", "credentials",
                "--server", "http://localhost:8080",
                "--realm", "master",
                "--user", ADMIN_USER,
                "--password", ADMIN_PASSWORD
        );

        // Patch master realm to allow HTTP (workaround for HTTPS required error)
        execOrThrow(
                "/opt/keycloak/bin/kcadm.sh", "update", "realms/master",
                "-s", "sslRequired=NONE"
        );

        // Create test realm, client, roles, and users
        createRealmAndClient();

        String realmUrl = baseUrl + "/realms/" + REALM;

        Map<String, String> props = new HashMap<>();
        props.put("quarkus.oidc.auth-server-url", realmUrl);
        props.put("quarkus.oidc.client-id", CLIENT_ID);
        props.put("quarkus.oidc.credentials.secret", CLIENT_SECRET);
        props.put("quarkus.oidc.application-type", "service");
        props.put("quarkus.oidc.token.issuer", realmUrl);
        props.put("quarkus.oidc.token.allow-jwt-introspection", "false");
        props.put("quarkus.oidc.token.allow-opaque-token-introspection", "false");
        props.put("quarkus.keycloak.devservices.enabled", "false");
        // KeycloakTestClient needs keycloak.url to find the token endpoint
        props.put("keycloak.url", baseUrl);
        return props;
    }

    private void createRealmAndClient() {
        // Create realm
        execOrThrow("/opt/keycloak/bin/kcadm.sh", "create", "realms",
                "-s", "realm=" + REALM, "-s", "enabled=true", "-s", "sslRequired=NONE");

        // Create client
        execOrThrow("/opt/keycloak/bin/kcadm.sh", "create", "clients", "-r", REALM,
                "-s", "clientId=" + CLIENT_ID,
                "-s", "enabled=true",
                "-s", "protocol=openid-connect",
                "-s", "publicClient=false",
                "-s", "secret=" + CLIENT_SECRET,
                "-s", "serviceAccountsEnabled=true",
                "-s", "directAccessGrantsEnabled=true");

        // Create roles
        for (String role : new String[]{"user", "curator", "owner", "admin"}) {
            execOrThrow("/opt/keycloak/bin/kcadm.sh", "create", "roles", "-r", REALM,
                    "-s", "name=" + role);
        }

        // Create alice with all roles
        createUser("alice", "alice", "user", "curator", "owner", "admin");

        // Create bob with user role
        createUser("bob", "bob", "user");
    }

    private void createUser(String username, String password, String... roles) {
        execOrThrow("/opt/keycloak/bin/kcadm.sh", "create", "users", "-r", REALM,
                "-s", "username=" + username,
                "-s", "enabled=true",
                "-s", "emailVerified=true",
                "-s", "email=" + username + "@example.com",
                "-s", "firstName=" + username,
                "-s", "lastName=Test",
                "-s", "requiredActions=[]");

        execOrThrow("/opt/keycloak/bin/kcadm.sh", "set-password", "-r", REALM,
                "--username", username,
                "--new-password", password);

        for (String role : roles) {
            execOrThrow("/opt/keycloak/bin/kcadm.sh", "add-roles", "-r", REALM,
                    "--uusername", username,
                    "--rolename", role);
        }
    }

    private void execOrThrow(String... command) {
        try {
            var result = keycloak.execInContainer(command);
            if (result.getExitCode() != 0) {
                throw new RuntimeException(
                        "Keycloak command failed: " + String.join(" ", command) +
                        "\nstdout: " + result.getStdout() +
                        "\nstderr: " + result.getStderr());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to run command in Keycloak container", e);
        }
    }

    @Override
    public void stop() {
        if (keycloak != null) {
            keycloak.stop();
        }
    }
}
