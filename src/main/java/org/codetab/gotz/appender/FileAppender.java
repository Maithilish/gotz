package org.codetab.gotz.appender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.codetab.gotz.exception.FieldNotFoundException;
import org.codetab.gotz.model.Activity.Type;
import org.codetab.gotz.shared.ActivityService;
import org.codetab.gotz.util.FieldsUtil;
import org.codetab.gotz.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileAppender extends Appender {

    static final Logger LOGGER = LoggerFactory.getLogger(FileAppender.class);

    private static final int QUEUE_CAPACITY = 1024;

    private BlockingQueue<Object> queue;

    private ActivityService activityService;

    public FileAppender() {

        queue = new ArrayBlockingQueue<Object>(QUEUE_CAPACITY);
    }

    @Override
    public void run() {
        PrintWriter writer = null;
        try {
            String file = FieldsUtil.getValue(getFields(), "file");
            FileOutputStream fos = FileUtils.openOutputStream(new File(file));
            writer = new PrintWriter(fos);
            for (;;) {
                Object item = queue.take();
                if (item == Marker.EOF) {
                    break;
                }
                String str = item.toString();
                writer.println(str);
            }
        } catch (FieldNotFoundException | IOException | InterruptedException e) {
            String message = "file appender ";
            LOGGER.error("{} {}", message, Util.getMessage(e));
            LOGGER.debug("{}", e);
            activityService.addActivity(Type.GIVENUP, message, e);
        } finally {
            writer.close();
        }
    }

    @Override
    public void append(final Object object) throws InterruptedException {
        queue.put(object);
    }

    @Inject
    public void setActivityService(ActivityService activityService) {
        this.activityService = activityService;
    }
}