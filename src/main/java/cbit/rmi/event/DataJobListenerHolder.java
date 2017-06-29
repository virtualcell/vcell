package cbit.rmi.event;

public interface DataJobListenerHolder {
	void addDataJobListener(DataJobListener dataJobListener);
	void removeDataJobListener(DataJobListener dataJobListener);
}
