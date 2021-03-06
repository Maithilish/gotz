package org.codetab.gotz.misc;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.codetab.gotz.messages.Messages;
import org.codetab.gotz.shared.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * JVM shutdown hook.
 * @author Maithilish
 *
 */
@Singleton
public class ShutdownHook extends Thread {

    /**
     * logger.
     */
    private final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    /**
     * activity service.
     */
    @Inject
    private ActivityService activityService;

    /**
     * <p>
     * public constructor.
     */
    @Inject
    public ShutdownHook() {
        // cs - if private then class has to be final which is unable to mock
        logger.info(Messages.getString("ShutdownHook.0")); //$NON-NLS-1$
    }

    /**
     * log activities and memory stats.
     */
    @Override
    public synchronized void start() {
        activityService.logActivities();
        activityService.logMemoryUsage();
    }
}
