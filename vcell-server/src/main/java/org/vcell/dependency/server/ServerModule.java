package org.vcell.dependency.server;

import cbit.vcell.field.db.LocalExternalDataIdentifierServiceImpl;
import cbit.vcell.simdata.ExternalDataIdentifierService;
import com.google.inject.AbstractModule;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ExternalDataIdentifierService.class).toInstance(new LocalExternalDataIdentifierServiceImpl());
    }
}
