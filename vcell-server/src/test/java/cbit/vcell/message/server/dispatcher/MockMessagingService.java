package cbit.vcell.message.server.dispatcher;

import cbit.vcell.message.*;

import java.util.ArrayList;
import java.util.List;

public class MockMessagingService implements VCMessagingService {

    public ArrayList<VCMessagingConsumer> messagingConsumers = new ArrayList<>();
    public final MockVCMessageSession mockVCMessageSession = new MockVCMessageSession();

    @Override
    public VCMessageSession createProducerSession() {
        return mockVCMessageSession;
    }

    @Override
    public void addMessageConsumer(VCMessagingConsumer vcMessagingConsumer) {
        messagingConsumers.add(vcMessagingConsumer);
    }

    @Override
    public void removeMessageConsumer(VCMessagingConsumer vcMessagingConsumer) {

    }

    @Override
    public List<VCMessagingConsumer> getMessageConsumers() {
        return List.of();
    }

    @Override
    public void close() throws VCMessagingException {

    }

    @Override
    public VCMessageSelector createSelector(String clientMessageFilter) {
        return null;
    }

    @Override
    public VCMessagingDelegate getDelegate() {
        return null;
    }

    @Override
    public void setConfiguration(VCMessagingDelegate delegate, String jmshost, int jmsport) {

    }
}
