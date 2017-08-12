package cbit.vcell.server;

import org.vcell.service.VCellService;
import org.vcell.util.SessionLog;
import org.vcell.util.document.UserLoginInfo;

public interface LocalVCellConnectionService extends VCellService {
	VCellConnectionFactory getLocalVCellConnectionFactory(UserLoginInfo userLoginInfo, SessionLog sessionLog);
}
