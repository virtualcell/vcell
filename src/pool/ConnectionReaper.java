package pool;

import java.sql.*;
import java.util.*;
import java.io.*;

import cbit.vcell.server.PropertyLoader;

class ConnectionReaper extends Thread {

	private JDCConnectionPool pool;
	private final static int minute = 60; // in seconds
	private long delayMs=20*minute*1000;

    ConnectionReaper(JDCConnectionPool pool) {
        this.pool=pool;
        delayMs = PropertyLoader.getIntProperty(PropertyLoader.dbPoolTimeoutSec, 20*minute) * 1000;
    }
    public void run() {
        while(true) {
           try {
              sleep(delayMs);
           } catch( InterruptedException e) { }
           pool.reapConnections();
        }
    }
}
