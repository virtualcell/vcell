package cbit.vcell.server;

import org.vcell.util.document.UserLoginInfo;

public interface LocalVCellConnectionService {
	VCellConnectionFactory getLocalVCellConnectionFactory(UserLoginInfo userLoginInfo);
}
