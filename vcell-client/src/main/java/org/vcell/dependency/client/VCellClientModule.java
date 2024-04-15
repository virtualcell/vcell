package org.vcell.dependency.client;

import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory;
import cbit.vcell.server.VCellConnectionFactory;
import cbit.vcell.simdata.ExternalDataIdentifierService;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.vcell.DependencyConstants;
import org.vcell.service.registration.RegistrationService;
import org.vcell.service.registration.remote.RemoteRegistrationService;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.User;

import java.util.HashMap;
import java.util.Vector;

public class VCellClientModule extends AbstractModule {

    private final String apiHost;
    private final int apiPort;
    private final String apiPathPrefixV0;

    public VCellClientModule(String apiHost, int apiPort, String apiPathPrefixV0) {
        this.apiHost = apiHost;
        this.apiPort = apiPort;
        this.apiPathPrefixV0 = apiPathPrefixV0;
    }

    public interface UnimplementedService {
    }

    public static class UnimplementedExternalDataIdentifierService implements ExternalDataIdentifierService, UnimplementedService {
        @Override
        public HashMap<User, Vector<ExternalDataIdentifier>> getAllExternalDataIdentifiers()  {
            throw new UnsupportedOperationException("Field datda operations Not implemented for client-side simulations");
        }
    }


    @Override
    protected void configure() {
        // only one implementation, to break compile-time cyclic dependency (vcell-core -> vcell-service)
        bind(ExternalDataIdentifierService.class).toInstance(new UnimplementedExternalDataIdentifierService());

        bind(VCellConnectionFactory.class).to(RemoteProxyVCellConnectionFactory.class).asEagerSingleton();

        // RegistrationService interface is not clean - mixes new registration with updates - is there another way?
        bind(RegistrationService.class).toInstance(new RemoteRegistrationService()); // used on remote client.

        bind(String.class).annotatedWith(Names.named(DependencyConstants.VCELL_API_HOST)).toInstance(apiHost);
        bind(Integer.class).annotatedWith(Names.named(DependencyConstants.VCELL_API_PORT)).toInstance(apiPort);
        bind(String.class).annotatedWith(Names.named(DependencyConstants.VCELL_API_PATH_PREFIX_V0)).toInstance(apiPathPrefixV0);
    }
}
