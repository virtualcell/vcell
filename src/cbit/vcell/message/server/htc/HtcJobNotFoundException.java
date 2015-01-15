package cbit.vcell.message.server.htc;

public class HtcJobNotFoundException extends HtcException {
	private final HtcJobID id;

	public HtcJobNotFoundException(String message, HtcJobID id) {
		super(message);
		this.id = id;
	}

	public HtcJobID getId() {
		return id;
	}
}
