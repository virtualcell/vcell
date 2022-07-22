package org.vcell.cli.commands;

import org.vcell.cli.VersionImplementation;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;


/**
 * Command wrapper to connect API request for VCell version to proper functionality
 */
@Command(name = "version", description = "display software version")
public class VersionCommand implements Callable<Integer> {

    public Integer call() {
       VersionImplementation vc = new VersionImplementation();
       return vc.call();
    }
}
