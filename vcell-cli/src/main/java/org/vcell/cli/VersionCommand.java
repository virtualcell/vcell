package org.vcell.cli;

import cbit.vcell.resource.PropertyLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.vcell.cli.vcml.VcmlOmexConverter;
import org.vcell.util.DataAccessException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import static java.lang.System.getProperty;
import static java.lang.System.out;

@Command(name = "version")
class VersionCommand implements Callable<Integer> {

    public Integer call() {
        System.out.println("in version()");
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





