/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client;

import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.client.desktop.NetworkProxyPreferences;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.server.DynamicClientProperties;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.*;
import cbit.vcell.xml.XmlHelper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jdom.Document;
import org.vcell.dependency.client.VCellClientModule;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.logging.ConsoleCapture;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import javax.swing.*;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.prefs.Preferences;


@Command(name = "vcell", version = "vcellclient 1.0", description = "VCell Client")
public class VCellClientMain implements Callable<Integer> {

    /**
     * array of properties required for correct operation
     */
    private static final String[] REQUIRED_CLIENT_PROPERTIES = {
            PropertyLoader.installationRoot,
            PropertyLoader.vcellSoftwareVersion,
    };
    public static ArrayBlockingQueue<String> recordedUserEvents = new ArrayBlockingQueue<>(50, true);
    @Parameters(description = "optional vcell model file, accepts drag and drop install4j VCell launcher", arity = "0..1")
    private final File vcellModelFile = null;
    @Option(names = {"--api-host"}, description = "VCell api server host[:port], " +
            "defaults to -Dvcell.serverHost property, or vcellapi.cam.uchc.edu if not specified")
    private String host = System.getProperty(PropertyLoader.vcellServerHost, "vcellapi.cam.uchc.edu");
    @Option(names = {"--api-prefix-v0"}, description = "VCell api server path prefix for api version 0, " +
            "defaults to -Dvcell.serverPrefix.v0 property, or /api/v0 if not specified")
    private String pathPrefixV0 = System.getProperty(PropertyLoader.vcellServerPrefixV0, "/api/v0");
    @Option(names = {"--userid"}, hidden = true, description = "vcell userid")
    private String userid = null;
    @Option(names = {"--password"}, hidden = true, description = "vcell password")
    private String password = null;
    @Option(names = {"-console"}, type = Boolean.class, description = "Install4J parameter, ignored")
    private boolean _console = false;
    private VCellClient vcellClient;
    @Option(names = {"--quarkus-api-host"}, hidden = true, description = "Quarkus API server host[:port]")
    private String quarkusAPIHost = null;

    private boolean isHTTP = PropertyLoader.getBooleanProperty(PropertyLoader.isHTTP, false);

    private VCellClientMain() {
    }

    /**
     * Starts the application.
     *
     * @param args an array of command-line arguments
     */
    public static void main(java.lang.String[] args) {
        System.out.println("starting with arguments " + Arrays.asList(args));
        int exitCode = 1;
        try {
            CommandLine commandLine = new CommandLine(new VCellClientMain());
            exitCode = commandLine.execute(args);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void setProxyConfiguration() {
        String proxy_http_host = System.getProperty(NetworkProxyUtils.PROXY_HTTP_HOST);
        String proxy_http_port = System.getProperty(NetworkProxyUtils.PROXY_HTTP_PORT);
        String proxy_socks_host = System.getProperty(NetworkProxyUtils.PROXY_SOCKS_HOST);
        String proxy_socks_port = System.getProperty(NetworkProxyUtils.PROXY_SOCKS_PORT);
        String currentProxyHost = (proxy_http_host != null) ? proxy_http_host : proxy_socks_host;
        String currentProxyPort = (proxy_http_host != null) ? proxy_http_port : proxy_socks_port;
        Preferences prefs = Preferences.userNodeForPackage(RemoteProxyVCellConnectionFactory.class);
        NetworkProxyUtils.setProxyProperties(false, null,
                prefs.get(NetworkProxyPreferences.prefProxyType, NetworkProxyPreferences.prefProxyType),
                currentProxyHost,
                currentProxyPort,
                prefs.get(NetworkProxyPreferences.prefProxyType, NetworkProxyPreferences.prefProxyType),
                prefs.get(NetworkProxyPreferences.prefProxyHost, NetworkProxyPreferences.prefProxyHost),
                prefs.get(NetworkProxyPreferences.prefProxyPort, NetworkProxyPreferences.prefProxyPort));
    }

    @Override
    public Integer call() throws Exception {
        String[] hostAndPort = this.host.split(":");
        String host = hostAndPort[0];
        int port = hostAndPort.length < 2 ? 443 : Integer.parseInt(hostAndPort[1]);

        String quarkusHost = quarkusAPIHost != null ? quarkusAPIHost.split(":")[0] : host;
        int quarkusPort = quarkusAPIHost != null ? Integer.parseInt(quarkusAPIHost.split(":")[1]) : port;

        PropertyLoader.loadProperties(REQUIRED_CLIENT_PROPERTIES);
        Injector injector = Guice.createInjector(new VCellClientModule(host, port, pathPrefixV0,
                quarkusHost, quarkusPort));
        this.vcellClient = injector.getInstance(VCellClient.class);

        // see static-files-config ConfigMap for definitions of dynamic properties as deployed
        String url_path = PropertyLoader.getProperty(PropertyLoader.DYNAMIC_PROPERTIES_URL_PATH, "/vcell_dynamic_properties.csv");

        String protocol = isHTTP ? "http" : "https";
        String webapp_base_url = protocol + "://" + host + ":" + port;
        URL vcell_dynamic_client_properties_url = new URL(webapp_base_url + url_path);
        Thread dynamicClientPropertiesThread = new Thread(() -> DynamicClientProperties.updateDynamicClientProperties(vcell_dynamic_client_properties_url));
        dynamicClientPropertiesThread.setDaemon(false); // non-daemon thread to keep JVM running
        dynamicClientPropertiesThread.start();

        setProxyConfiguration();

        final boolean IS_DEBUG = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
        if (!IS_DEBUG) {
            String siteName = VCellSoftwareVersion.fromSystemProperty().getSite().name().toLowerCase();
            ConsoleCapture.getInstance().captureStandardOutAndError(new File(ResourceUtil.getLogDir(), "vcellrun_" + siteName + ".log"));
        }
        ErrorUtils.setDebug(IS_DEBUG);

        VCDocument initialDocument = null;
        if (vcellModelFile != null && vcellModelFile.exists() && vcellModelFile.isFile()) {
            initialDocument = startupWithOpen(vcellModelFile);
        }

        UserLoginInfo.DigestedPassword digestedPassword = null;
        if (password != null && password.length() > 0) {
            digestedPassword = new UserLoginInfo.DigestedPassword(password);
        }
        ClientServerInfo csInfo = ClientServerInfo.createRemoteServerInfo(host, port, this.pathPrefixV0, userid, digestedPassword);

        try {
            VCMongoMessage.enabled = false;

            vcellClient.startClient(initialDocument, csInfo);
            ErrorUtils.setClientServerInfo(csInfo);

            //starting loading libraries
            new LibraryLoaderThread(true).start();

        } catch (Exception exception) {
            ErrorUtils.sendRemoteLogMessage(csInfo.getUserLoginInfo(), csInfo + "\nvcell startup failed\n\n" + exception.getMessage());
            JOptionPane.showMessageDialog(null, exception.getMessage(), "Fatal Error", JOptionPane.OK_OPTION);
            System.err.println("Exception occurred in main() of VCellApplication");
            exception.printStackTrace(System.out);
            throw exception;
        }
        return 0;
    }

    private VCDocument startupWithOpen(File documentFile) {
        VCDocument initialDocument = null;
        try {
            Document xmlDoc = XmlUtil.readXML(documentFile);
            String vcmlString = XmlUtil.xmlToString(xmlDoc, false);
            java.awt.Component parent = null;
            VCLogger vcLogger = new TranslationLogger(parent);
            initialDocument = XmlHelper.XMLToDocument(vcLogger, vcmlString);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            JOptionPane.showMessageDialog(null, e.getMessage(), "vcell startup error", JOptionPane.ERROR_MESSAGE);
        }
        return initialDocument;
    }
}
