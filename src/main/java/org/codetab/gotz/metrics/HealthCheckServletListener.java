package org.codetab.gotz.metrics;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.SharedHealthCheckRegistries;
import com.codahale.metrics.servlets.HealthCheckServlet;

public class HealthCheckServletListener
        extends HealthCheckServlet.ContextListener {

    static final HealthCheckRegistry HEALTH_CHECK_REGISTRY =
            SharedHealthCheckRegistries.getOrCreate("gotz");

    @Override
    protected HealthCheckRegistry getHealthCheckRegistry() {
        return HEALTH_CHECK_REGISTRY;
    }

}
