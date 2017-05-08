package cbit.vcell.message.server.bootstrap;

import org.scijava.service.AbstractService;
import org.vcell.util.SessionLog;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.server.LocalVCellConnectionService;
import cbit.vcell.server.VCellConnectionFactory;

public class LocalVCellConnectionServiceImpl extends AbstractService implements LocalVCellConnectionService {

	@Override
	public VCellConnectionFactory getLocalVCellConnectionFactory(UserLoginInfo userLoginInfo, SessionLog sessionLog) {
		return new LocalVCellConnectionFactory(userLoginInfo, sessionLog);
	}

}
