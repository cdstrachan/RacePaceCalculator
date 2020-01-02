package com.cds.paceservices;

import com.timgroup.statsd.StatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;

// Todo: move to Java config
public final class DiagnosticsService {
    private static String LoggingNamespace;
    private static String LoggingIP;
    private static int LoggingPort;

    static {
        // initiate the static variables

        // Todo: figure out how to make this read from application properties
        /*
         * ConfigProperties configProp = new ConfigProperties(); LogginNamespace =
         * configProp.getConfigValue("cds.logging.namespace");
         */
        LoggingNamespace = "com.cds.pacecalculatorservices";
        LoggingIP = "104.154.79.10";
        LoggingPort = 8125;
    }

    public static final StatsDClient statsd = new NonBlockingStatsDClient(LoggingNamespace, LoggingIP, LoggingPort);

    public static final void incrementCounter(String counterName) {
        statsd.incrementCounter(counterName);
    }

    public static final void decrementCounter(String counterName) {
        statsd.decrementCounter(counterName);
    }

    public static final void gauge(String gaugeName, long value) {
        statsd.gauge(gaugeName, value);
    }

    public static final void recordExecutionTime(String timerName, long value) {
        statsd.recordExecutionTime(timerName, value);
    }

}