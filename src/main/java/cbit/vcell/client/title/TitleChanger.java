package cbit.vcell.client.title;

public interface TitleChanger {
	public void addTitleListener(TitleListener listener);
	public void removeTitleListener(TitleListener listener);
	public String getTitle();
}
