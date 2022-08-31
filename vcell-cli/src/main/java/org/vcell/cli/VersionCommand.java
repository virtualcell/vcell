package org.vcell.cli;

import cbit.vcell.resource.PropertyLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.util.concurrent.Callable;

import static java.lang.System.getProperty;
import static java.lang.System.out;

@Command(name = "version", description = "display software version")
class VersionCommand implements Callable<Integer> {

    public Integer call() {
        try {
            PropertyLoader.loadProperties();

            String osName = getProperty("os.name");
            String osVersion = getProperty("os.version");
            String javaVersion = getProperty("java.version");
            String javaVendor = getProperty("java.vendor");
            String machineArch = getProperty("os.arch");

            out.println("VCell: " + getVersion() + "\nOS: " + osName + " " + osVersion + "\nJava Version: " +
                    javaVersion + "\nJava Vendor: " + javaVendor + "\nMachine: " + machineArch);

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getVersion() {
        final String fetchFailed = "Failed fetching VCell version";
        final String url = "http://vcell.org/webstart/Alpha/updates.xml";
        Document document;
        String version;
        try {
            document = Jsoup.connect(url).get();
            Elements entryElements = document.select("entry");
            version = entryElements.attr("newVersion");
            return version;
        } catch (IOException ignored) {
            return fetchFailed;
        }
    }

}





