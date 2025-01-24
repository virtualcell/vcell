package cbit.vcell.client.server;

public interface InteractiveClientServerContext {
    void showErrorDialog(String errorMessage);

    void showWarningDialog(String warningMessage);

    void clearConnectWarning();

    void showConnectWarning(String message);
}
