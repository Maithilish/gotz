package org.codetab.gotz;

import javax.inject.Inject;

import org.codetab.gotz.exception.CriticalException;
import org.codetab.gotz.model.Activity.Type;
import org.codetab.gotz.shared.ActivityService;
import org.codetab.gotz.step.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GotzEngine {

    static final Logger LOGGER = LoggerFactory.getLogger(GotzEngine.class);

    @Inject
    private ActivityService activityService;
    @Inject
    private GSystem gSystem;
    @Inject
    private GTaskRunner gTaskRunner;

    /*
     * single thread env throws CriticalException and terminates the app and
     * multi thread env may also throw CriticalException but they terminates
     * just the executing thread
     *
     */
    public void start() {
        LOGGER.info("starting GotzEngine");
        activityService.start();
        try {
            // single thread env
            gSystem.initSystem();
            Task task = gSystem.createInitialTask();
            LOGGER.info("basic system initialized");
            gSystem.waitForHeapDump();

            LOGGER.info("switching to multi thread environment");
            gTaskRunner.executeInitalTask(task);
            task = null;
            gTaskRunner.waitForFinish();
            gSystem.waitForHeapDump();
            LOGGER.info("shutting down GotzEngine...");
        } catch (CriticalException e) {
            LOGGER.error("{}", e.getMessage());
            LOGGER.warn("terminating GotzEngine...");
            LOGGER.debug("{}", e);
            activityService.addActivity(Type.FATAL, e.getMessage(), e);
        }
        activityService.end();
        LOGGER.info("GotzEngine finished");
    }

}
