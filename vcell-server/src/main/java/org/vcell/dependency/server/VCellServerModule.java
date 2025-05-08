package org.vcell.dependency.server;

import cbit.vcell.field.db.LocalExternalDataIdentifierServiceImpl;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.message.server.bootstrap.LocalVCellConnectionFactory;
import cbit.vcell.message.server.bootstrap.LocalVCellConnectionServiceImpl;
import cbit.vcell.server.LocalVCellConnectionService;
import cbit.vcell.server.VCellConnectionFactory;
import cbit.vcell.simdata.ExternalDataIdentifierService;

public class VCellServerModule extends com.google.inject.AbstractModule {
    @Override
    protected void configure() {
        // only one implementation, to break compile-time cyclic dependency (vcell-core -> vcell-service)
        bind(ExternalDataIdentifierService.class).toInstance(new LocalExternalDataIdentifierServiceImpl());

        // only one implementation, to break compile-time cyclic dependency (vcell-core -> vcell-service)
        bind(LocalVCellConnectionService.class).toInstance(new LocalVCellConnectionServiceImpl());
        bind(VCellConnectionFactory.class).to(LocalVCellConnectionFactory.class).asEagerSingleton();

        bind(VCMessagingService.class).toInstance(new VCMessagingServiceActiveMQ());
        //bind(VCMessagingService.class).toInstance(new VCMessagingServiceEmbedded()); // used for testing.

//        MapBinder<String, Database> mapBinder = MapBinder.newMapBinder(binder(), String.class, Database.class);
//        mapBinder.addBinding(POSTGRESQL_DRIVER_NAME).to(PostgresConnectionFactoryProvider.class);
//        try {
//            Class<Database> oracleFactoryProviderClass = (Class<Database>) getClass().getClassLoader().loadClass("org.vcell.db.oracle.OraclePoolingConnectionFactoryProvider");
//            mapBinder.addBinding("oracle.jdbc.OracleDriver").to(oracleFactoryProviderClass);
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        bind(Database.class).toProvider(DatabaseProvider.class);
        //bind(Database.class).toInstance(new PostgresConnectionFactoryProvider());
    }
}
