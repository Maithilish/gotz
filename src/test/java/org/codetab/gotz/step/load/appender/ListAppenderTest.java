package org.codetab.gotz.step.load.appender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.codetab.gotz.exception.FieldsException;
import org.codetab.gotz.model.Activity.Type;
import org.codetab.gotz.model.Fields;
import org.codetab.gotz.model.helper.FieldsHelper;
import org.codetab.gotz.shared.ActivityService;
import org.codetab.gotz.shared.ConfigService;
import org.codetab.gotz.step.load.appender.Appender.Marker;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 * <p>
 * ListAppender tests.
 * @author Maithilish
 *
 */
public class ListAppenderTest {

    @Mock
    private ConfigService configService;
    @Mock
    private ActivityService activityService;
    @Spy
    private FieldsHelper fieldsHelper;

    @InjectMocks
    private ListAppender appender;

    @Rule
    public ExpectedException testRule = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInit() {
        appender.init();

        assertThat(appender.isInitialized()).isTrue();
    }

    @Test
    public void testAppend() throws InterruptedException {
        String obj = "test object";

        Fields fields = new Fields();
        appender.setFields(fields);

        appender.initializeQueue();

        appender.append(obj);

        assertThat(appender.getQueue().take()).isEqualTo(obj);
    }

    @Test
    public void testAppendNullParams() throws InterruptedException {
        try {
            appender.append(null);
            fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("object must not be null");
        }
    }

    @Test
    public void testRun() throws InterruptedException, FieldsException {
        String obj1 = "test1";
        String obj2 = "test2";

        Fields fields = new Fields();
        appender.setFields(fields);

        appender.initializeQueue();

        Thread t = new Thread(appender);
        t.start();

        appender.append(obj1);
        appender.append(obj2);
        appender.append(Marker.EOF);

        t.join();

        assertThat(appender.getList()).containsExactly(obj1, obj2);
    }

    @Test
    public void testRunLogAcivityOnInterruptedException()
            throws InterruptedException, IllegalAccessException {

        @SuppressWarnings("unchecked")
        BlockingQueue<Object> queue = Mockito.mock(BlockingQueue.class);
        FieldUtils.writeField(appender, "queue", queue, true);

        given(queue.take()).willThrow(InterruptedException.class)
                .willReturn(Marker.EOF);

        appender.run();

        verify(activityService).addActivity(eq(Type.FAIL), any(String.class),
                any(InterruptedException.class));
    }

    @Test
    public void testGetList() throws IllegalAccessException {
        List<Object> list = new ArrayList<>();

        FieldUtils.writeDeclaredField(appender, "list", list, true);

        assertThat(appender.getList()).isSameAs(list);
    }
}
