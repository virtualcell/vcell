package org.vcell.core;


import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

@Category(Fast.class)
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
        Assert.assertTrue("expecting to return 3", testService.add(1,2) == 3);
        Assert.assertNotNull(injector);
    }

    @Test
    public void testFieldInjectionFromInjector() {
        Injector injector = Guice.createInjector(new TestModule());
        TestService testService = injector.getInstance(TestService.class);
        TestApplication testApplication = injector.getInstance(TestApplication.class);
        Assert.assertTrue("expecting to return 3", testApplication.add(1,2) == 3);
        Assert.assertNotNull(injector);
    }

    @Test
    public void testFieldInjectionFromConstructor() {
        Injector injector = Guice.createInjector(new TestModule());
        TestService testService = injector.getInstance(TestService.class);
        TestApplication testApplication = new TestApplication(new TestServiceImpl());
        Assert.assertTrue("expecting to return 3", testApplication.add(1,2) == 3);
        Assert.assertNotNull(injector);
    }

    @Test
    public void testDelayedFieldInjectionFromInjector() {
        Injector injector = Guice.createInjector(new TestModule());
        TestService testService = injector.getInstance(TestService.class);
        TestApplicationDelayedRunner testApplication = injector.getInstance(TestApplicationDelayedRunner.class);
        Assert.assertTrue("expecting to return 3", testApplication.add(1,2) == 3);
        Assert.assertNotNull(injector);
    }

    @Test(expected = NullPointerException.class)
    public void testDelayedFieldInjectionFromConstructor_shouldFail() {
        Injector injector = Guice.createInjector(new TestModule());
        TestApplicationDelayedRunner testApplication = new TestApplicationDelayedRunner(null);
        Assert.assertTrue("expecting to return 3", testApplication.add(1,2) == 3);
        Assert.assertNotNull(injector);
    }

    @Test
    public void testTransitiveFieldInjectionFromInjector() {
        Injector injector = Guice.createInjector(new TestModule());
        TestService testService = injector.getInstance(TestService.class);
        TestApplicationTransitiveRunner testApplication = injector.getInstance(TestApplicationTransitiveRunner.class);
        Assert.assertTrue("expecting to return 3", testApplication.add(1,2) == 3);
        Assert.assertNotNull(injector);
    }

    @Test(expected = NullPointerException.class)
    public void testTransitiveFieldInjectionFromConstructor_shouldFail() {
        Injector injector = Guice.createInjector(new TestModule());
        TestService testService = injector.getInstance(TestService.class);
        TestApplicationTransitiveRunner testApplication = new TestApplicationTransitiveRunner();
        Assert.assertTrue("expecting to return 3", testApplication.add(1,2) == 3);
        Assert.assertNotNull(injector);
    }

    @Test
    public void testTransitiveImplicitFieldInjectionFromInjector() {
        Injector injector = Guice.createInjector(new TestModule());
        TestService testService = injector.getInstance(TestService.class);
        TestApplicationTransitiveImplicitRunner testApplication = injector.getInstance(TestApplicationTransitiveImplicitRunner.class);
        Assert.assertTrue("expecting to return 3", testApplication.add(1,2) == 3);
        Assert.assertNotNull(injector);
    }

    @Test(expected = NullPointerException.class)
    public void testTransitiveImplicitFieldInjectionFromConstructor_shouldFail() {
        Injector injector = Guice.createInjector(new TestModule());
        TestService testService = injector.getInstance(TestService.class);
        TestApplicationTransitiveImplicitRunner testApplication = new TestApplicationTransitiveImplicitRunner();
        Assert.assertTrue("expecting to return 3", testApplication.add(1,2) == 3);
        Assert.assertNotNull(injector);
    }



}
