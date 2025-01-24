package org.vcell.standalone;

import cbit.vcell.field.db.LocalExternalDataIdentifierServiceImpl;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.message.server.bootstrap.LocalVCellConnectionFactory;
import cbit.vcell.message.server.bootstrap.LocalVCellConnectionServiceImpl;
import cbit.vcell.server.LocalVCellConnectionService;
import cbit.vcell.server.VCellConnectionFactory;
import cbit.vcell.simdata.ExternalDataIdentifierService;
import com.google.inject.AbstractModule;
import org.vcell.api.utils.Auth0ConnectionUtils;
import org.vcell.service.registration.RegistrationService;
import org.vcell.service.registration.localdb.LocaldbRegistrationService;

public class VCellStandaloneModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExternalDataIdentifierService.class).toInstance(new LocalExternalDataIdentifierServiceImpl());

        bind(LocalVCellConnectionService.class).toInstance(new LocalVCellConnectionServiceImpl());
        bind(Auth0ConnectionUtils.class).toInstance(new Auth0ConnectionUtils());
        bind(VCellConnectionFactory.class).to(LocalVCellConnectionFactory.class).asEagerSingleton();

        bind(RegistrationService.class).toInstance(new LocaldbRegistrationService());
    }
}
