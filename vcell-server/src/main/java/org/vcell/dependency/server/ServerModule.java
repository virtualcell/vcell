package org.vcell.dependency.server;

import cbit.vcell.field.db.LocalExternalDataIdentifierServiceImpl;
import cbit.vcell.message.server.bootstrap.LocalVCellConnectionServiceImpl;
import cbit.vcell.server.LocalVCellConnectionService;
import cbit.vcell.simdata.ExternalDataIdentifierService;
import com.google.inject.AbstractModule;
import org.vcell.service.registration.RegistrationService;
import org.vcell.service.registration.localdb.LocaldbRegistrationService;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        // only one implementation, to break compile-time cyclic dependency (vcell-core -> vcell-service)
        bind(ExternalDataIdentifierService.class).toInstance(new LocalExternalDataIdentifierServiceImpl());

        // only one implementation, to break compile-time cyclic dependency (vcell-core -> vcell-service)
        bind(LocalVCellConnectionService.class).toInstance(new LocalVCellConnectionServiceImpl());

        // server-side implementation (talk directly to database)
        // RegistrationService interface is not clean - mixes new registration with updates - is there another way?
        bind(RegistrationService.class).toInstance(new LocaldbRegistrationService());
        //bind(RegistrationService.class).toInstance(new RemoteRegistrationService()); // used on remote client.

        bind(VCMessagingService.class).toInstance(new VCMessagingServiceActiveMQ());
        //bind(VCMessagingService.class).toInstance(new VCMessagingServiceEmbedded()); // used for testing.

        MapBinder<String, Database> mapBinder = MapBinder.newMapBinder(binder(), String.class, Database.class);
    }
}
