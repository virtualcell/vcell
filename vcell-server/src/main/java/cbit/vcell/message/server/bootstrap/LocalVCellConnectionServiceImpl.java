package cbit.vcell.message.server.bootstrap;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.server.LocalVCellConnectionService;
import cbit.vcell.server.VCellConnectionFactory;

@Singleton
public class LocalVCellConnectionServiceImpl implements LocalVCellConnectionService {

	public LocalVCellConnectionServiceImpl() {
	}


	@Override
	public VCellConnectionFactory getLocalVCellConnectionFactory() {
		return new LocalVCellConnectionFactory();
	}

}
