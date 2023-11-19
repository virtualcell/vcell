package org.vcell.rest;

import cbit.vcell.resource.PropertyLoader;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.vcell.db.DatabaseService;
import org.vcell.rest.config.CDIVCellConfigProvider;
import org.vcell.rest.db.OracleAgroalConnectionFactory;

@QuarkusMain
public class Main {
    public static void main(String... args) {
        Quarkus.run(MyApp.class, args);
    }

    public static class MyApp implements QuarkusApplication {

        @Override
        public int run(String... args) throws Exception {
            // do startup logic here
            setupConfiguration();
            setupDatabase();

            System.out.println("Starting VCell REST with " + io.quarkus.runtime.LaunchMode.current() + " mode.");

            Quarkus.waitForExit();
            return 0;
        }

        private void setupDatabase() {
            // this reconciles the CDI database with the PropertyLoader, replacing PropertyLoader's default
            // database provider which is backed by System properties.
            DatabaseService.getInstance().setConnectionFactory(new OracleAgroalConnectionFactory());
        }

        private void setupConfiguration() {
            // this reconciles the CDI configuration with the PropertyLoader, replacing PropertyLoader's default
            // config provider which is backed by System properties.
            PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
        }
    }
}