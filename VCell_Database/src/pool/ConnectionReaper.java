package pool;

import java.sql.*;
import java.util.*;
import java.io.*;

class ConnectionReaper extends Thread {

	private JDCConnectionPool pool;
	private final static long minute = 60000;
	private final long delay=20*minute;

    ConnectionReaper(JDCConnectionPool pool) {
        this.pool=pool;
    }
    public void run() {
        while(true) {
           try {
              sleep(delay);
           } catch( InterruptedException e) { }
           pool.reapConnections();
        }
    }
}
