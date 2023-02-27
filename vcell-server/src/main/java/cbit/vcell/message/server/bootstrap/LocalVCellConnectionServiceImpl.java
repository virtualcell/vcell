package cbit.vcell.message.server.bootstrap;

import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.server.LocalVCellConnectionService;
import cbit.vcell.server.VCellConnectionFactory;

public class LocalVCellConnectionServiceImpl implements LocalVCellConnectionService {

	@Override
	public VCellConnectionFactory getLocalVCellConnectionFactory(UserLoginInfo userLoginInfo) {
		return new LocalVCellConnectionFactory(userLoginInfo);
	}

}
