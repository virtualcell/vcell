package cbit.vcell.client.desktop.biomodel;

public class BioModelsNetModelInfo {
	private String id;
	private String name;
	private String link;
	public BioModelsNetModelInfo(String id, String name, String link) {
		super();
		this.id = id;
		this.name = name;
		this.link = link;
	}
	public final String getId() {
		return id;
	}
	public final String getName() {
		return name;
	}
	public final String getLink() {
		return link;
	}
	
}
