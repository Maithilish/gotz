package org.codetab.gotz.metrics;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

import org.apache.commons.lang3.time.DurationFormatUtils;

public class SystemStat {

    private static final long MB_DIVISOR = 1048576; // in MB

    private Runtime runtime;
    private OperatingSystemMXBean osMx;
    private RuntimeMXBean rtMx;

    public SystemStat() {
        osMx = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        rtMx = (RuntimeMXBean) ManagementFactory.getRuntimeMXBean();
        runtime = Runtime.getRuntime();
    }

    public long getMaxMemory() {
        return runtime.maxMemory() / MB_DIVISOR;
    }

    public long getFreeMemory() {
        return runtime.freeMemory() / MB_DIVISOR;
    }

    public long getTotalMemory() {
        return runtime.totalMemory() / MB_DIVISOR;
    }

    public double getSystemLoad() {
        return osMx.getSystemLoadAverage();
    }

    public String getUptime() {
        return DurationFormatUtils.formatDuration(rtMx.getUptime(), "H:m:s");
    }
}
