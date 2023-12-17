package org.vcell.core;


import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("Fast")
public class DependencyInjectionTest {
    interface TestService {
        int add(int a, int b);
    }

    public static class TestServiceImpl implements TestService {
        @Override
        public int add(int a, int b) {
            return a + b;
        }
    }

    static class TestApplicationDelayedRunner {

        private final TestService testService;

        @Inject
        public TestApplicationDelayedRunner(TestService testService) {
            this.testService = testService;
        }

        public int add(int a, int b) {
            return new TestApplication(testService).add(a, b);
        }
    }

    static class TestApplicationTransitiveRunner {
        @Inject
        TestService testService;

        public void setTestService(TestService testService) {
            this.testService = testService;
        }

        public int add(int a, int b) {
            return testService.add(a, b);
        }
    }

    static class TestApplicationTransitiveImplicitRunner {

        @Inject
        TestApplication testApplication;

        public void setTestApplication(TestApplication testApplication) {
            this.testApplication = testApplication;
        }

        public int add(int a, int b) {
            return testApplication.add(a, b);
        }
    }

    static class TestApplication {
        @Inject
        TestService testService;

        @Inject
        public TestApplication(TestService testService) {
            this.testService = testService;
        }

        public int add(int a, int b) {
            return testService.add(a, b);
        }
    }

    static class TestModule extends AbstractModule {
        @Override
        public void configure() {
            bind(TestService.class).to(TestServiceImpl.class);
            requestStaticInjection(TestApplicationDelayedRunner.class);
        }

    }

    @Test
    public void testSimple() {
        Injector injector = Guice.createInjector(new TestModule());
        TestService testService = injector.getInstance(TestService.class);
        assertEquals(3, testService.add(1, 2), "expecting to return 3");
        assertNotNull(injector);
    }

    @Test
    public void testFieldInjectionFromInjector() {
        Injector injector = Guice.createInjector(new TestModule());
        TestService testService = injector.getInstance(TestService.class);
        TestApplication testApplication = injector.getInstance(TestApplication.class);
        assertEquals(3, testApplication.add(1, 2), "expecting to return 3");
        assertNotNull(injector);
    }

    @Test
    public void testFieldInjectionFromConstructor() {
        Injector injector = Guice.createInjector(new TestModule());
        TestService testService = injector.getInstance(TestService.class);
        TestApplication testApplication = new TestApplication(new TestServiceImpl());
        assertEquals(3, testApplication.add(1, 2), "expecting to return 3");
        assertNotNull(injector);
    }

    @Test
    public void testDelayedFieldInjectionFromInjector() {
        Injector injector = Guice.createInjector(new TestModule());
        TestService testService = injector.getInstance(TestService.class);
        TestApplicationDelayedRunner testApplication = injector.getInstance(TestApplicationDelayedRunner.class);
        assertEquals(3, testApplication.add(1, 2), "expecting to return 3");
        assertNotNull(injector);
    }

    @Test
    public void testDelayedFieldInjectionFromConstructor_shouldFail() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            Injector injector = Guice.createInjector(new TestModule());
            TestApplicationDelayedRunner testApplication = new TestApplicationDelayedRunner(null);
            assertEquals(3, testApplication.add(1, 2), "expecting to return 3");
            assertNotNull(injector);
        });
    }

    @Test
    public void testTransitiveFieldInjectionFromInjector() {
        Injector injector = Guice.createInjector(new TestModule());
        TestService testService = injector.getInstance(TestService.class);
        TestApplicationTransitiveRunner testApplication = injector.getInstance(TestApplicationTransitiveRunner.class);
        assertEquals(3, testApplication.add(1, 2), "expecting to return 3");
        assertNotNull(injector);
    }

    @Test
    public void testTransitiveFieldInjectionFromConstructor_shouldFail() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            Injector injector = Guice.createInjector(new TestModule());
            TestService testService = injector.getInstance(TestService.class);
            TestApplicationTransitiveRunner testApplication = new TestApplicationTransitiveRunner();
            assertEquals(3, testApplication.add(1, 2), "expecting to return 3");
            assertNotNull(injector);
        });
    }

    @Test
    public void testTransitiveImplicitFieldInjectionFromInjector() {
        Injector injector = Guice.createInjector(new TestModule());
        TestService testService = injector.getInstance(TestService.class);
        TestApplicationTransitiveImplicitRunner testApplication = injector.getInstance(TestApplicationTransitiveImplicitRunner.class);
        assertEquals(3, testApplication.add(1, 2), "expecting to return 3");
        assertNotNull(injector);
    }

    @Test
    public void testTransitiveImplicitFieldInjectionFromConstructor_shouldFail() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            Injector injector = Guice.createInjector(new TestModule());
            TestService testService = injector.getInstance(TestService.class);
            TestApplicationTransitiveImplicitRunner testApplication = new TestApplicationTransitiveImplicitRunner();
            assertEquals(3, testApplication.add(1, 2), "expecting to return 3");
            assertNotNull(injector);
        });
    }
}
