package org.vcell.restq;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.experimental.categories.Category;

@QuarkusIntegrationTest
@Category(org.vcell.test.Quarkus.class)
public class GreetingResourceIT extends GreetingResourceTest {
    // Execute the same tests but in packaged mode.
}
