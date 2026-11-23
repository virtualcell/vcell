package org.vcell.restclient.auth;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Opens web pages in the user's browser, including on Linux desktops where {@link Desktop} cannot.
 * <p>
 * On Linux the JDK only reports {@link Desktop.Action#BROWSE} when GIO can handle {@code http}, which needs
 * gvfs-backends. GNOME installs it; KDE Plasma and other desktops usually don't, although {@code xdg-open}
 * opens the browser fine there.
 */
public final class SystemBrowser {
    /** Long enough for xdg-open to hand the address to the browser and report whether it could. */
    private static final long XDG_OPEN_WAIT_SECONDS = 10;

    private SystemBrowser() {
    }

    /** Open {@code uri} in the user's default browser. False if there was no way to. */
    public static boolean open(URI uri) {
        if (SystemBrowser.openWithDesktop(uri)) return true;
        if (!SystemBrowser.isFreedesktopPlatform()) return false;
        return SystemBrowser.openWithXdgOpen(uri);
    }

    /** Bring VCell back in front of the browser, where the platform allows it. */
    public static void requestForeground() {
        if (!Desktop.isDesktopSupported()) return;
        if (!Desktop.getDesktop().isSupported(Desktop.Action.APP_REQUEST_FOREGROUND)) {
            System.err.println("foreground not supported");
            return;
        }
        try {
            Desktop.getDesktop().requestForeground(true);
        } catch (Exception e) {
            System.err.println("failed to request foreground: " + e.getMessage());
        }
    }

    private static boolean openWithDesktop(URI uri) {
        if (!Desktop.isDesktopSupported()) return false;
        if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) return false;
        try {
            Desktop.getDesktop().browse(uri);
            return true;
        } catch (IOException | UnsupportedOperationException e) {
            System.err.println("Desktop.browse failed: " + e.getMessage());
            return false;
        }
    }

    private static boolean openWithXdgOpen(URI uri) {
        Process process;
        try {
            process = new ProcessBuilder("xdg-open", uri.toString())
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .start();
        } catch (IOException e) {
            System.err.println("xdg-open is not available: " + e.getMessage());
            return false;
        }
        try {
            // Still running means it is waiting on a browser it started, which is a success.
            if (!process.waitFor(SystemBrowser.XDG_OPEN_WAIT_SECONDS, TimeUnit.SECONDS)) return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        int exitCode = process.exitValue();
        if (exitCode == 0) return true;
        System.err.println("xdg-open failed with exit code " + exitCode);
        return false;
    }

    /** Linux and the BSDs, where desktops follow freedesktop.org and provide xdg-open. */
    private static boolean isFreedesktopPlatform() {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        return !osName.startsWith("windows") && !osName.startsWith("mac");
    }
}
