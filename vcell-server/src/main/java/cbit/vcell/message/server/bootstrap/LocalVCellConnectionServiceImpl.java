package cbit.vcell.message.server.bootstrap;

import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.server.LocalVCellConnectionService;
import cbit.vcell.server.VCellConnectionFactory;

@Plugin(type = LocalVCellConnectionService.class)
public class LocalVCellConnectionServiceImpl extends AbstractService implements LocalVCellConnectionService {

	@Override
	public VCellConnectionFactory getLocalVCellConnectionFactory(UserLoginInfo userLoginInfo) {
		return new LocalVCellConnectionFactory(userLoginInfo);
	}

}
