package org.codetab.gotz;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.codetab.gotz.exception.ConfigNotFoundException;
import org.codetab.gotz.exception.CriticalException;
import org.codetab.gotz.metrics.MetricsHelper;
import org.codetab.gotz.metrics.MetricsServer;
import org.codetab.gotz.metrics.SystemStat;
import org.codetab.gotz.misc.ShutdownHook;
import org.codetab.gotz.model.helper.LocatorFieldsHelper;
import org.codetab.gotz.shared.BeanService;
import org.codetab.gotz.shared.ConfigService;
import org.codetab.gotz.shared.DataDefService;
import org.codetab.gotz.shared.StepService;
import org.codetab.gotz.step.IStep;
import org.codetab.gotz.step.Task;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GSystemTest {

    @Mock
    private ConfigService configService;
    @Mock
    private BeanService beanService;
    @Mock
    private DataDefService dataDefService;
    @Mock
    private StepService stepService;
    @Mock
    private IStep locatorSeeder;
    @Mock
    private LocatorFieldsHelper locatorFieldsHelper;
    @Mock
    private Task task;
    @Mock
    private ShutdownHook shutdownHook;
    @Mock
    private MetricsServer metricsServer;
    @Mock
    private MetricsHelper metricsHelper;
    @Mock
    private Runtime runTime;

    @InjectMocks
    private GSystem gSystem;

    @Rule
    public ExpectedException expect = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInitSystem()
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, ConfigNotFoundException {
        // given
        when(configService.getConfig("gotz.beanFile")).thenReturn("bean.xml");
        when(configService.getConfig("gotz.schemaFile"))
                .thenReturn("schema.xsd");

        // when
        gSystem.initSystem();

        // then
        verify(runTime).addShutdownHook(any(ShutdownHook.class));

        String userProvidedFile = "gotz.properties";
        String defaultsFile = "gotz-default.xml";

        InOrder inOrder = inOrder(beanService, dataDefService, configService,
                metricsServer, metricsHelper);
        inOrder.verify(configService).init(userProvidedFile, defaultsFile);
        inOrder.verify(metricsServer).start();
        inOrder.verify(metricsHelper).registerGuage(any(SystemStat.class),
                eq(gSystem), eq("system"), eq("stats"));

        inOrder.verify(configService).isTestMode();
        inOrder.verify(configService).isDevMode();
        inOrder.verify(configService).getRunDate();
        inOrder.verify(configService).getConfig("gotz.beanFile");
        inOrder.verify(configService).getConfig("gotz.schemaFile");
        inOrder.verify(beanService).init("bean.xml", "schema.xsd");
        inOrder.verify(dataDefService).init();
        inOrder.verify(dataDefService).getCount();
        verifyNoMoreInteractions(beanService, dataDefService, configService,
                metricsServer);
    }

    @Test
    public void testInitSystemShouldCatchFatal()
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, ConfigNotFoundException {
        // given
        given(configService.getConfig("gotz.beanFile"))
                .willThrow(ConfigNotFoundException.class);

        expect.expect(CriticalException.class);

        // when
        gSystem.initSystem();
    }

    @Test
    public void testCreateInitialTask()
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, ConfigNotFoundException {
        String seederClass = "org.codetab.gotz.ext.LocatorSeeder";

        // given
        when(configService.getConfig("gotz.seederClass"))
                .thenReturn(seederClass);
        when(stepService.getStep(any(String.class))).thenReturn(locatorSeeder);
        when(locatorSeeder.instance()).thenReturn(locatorSeeder);
        when(stepService.createTask(locatorSeeder)).thenReturn(task);

        // when
        gSystem.createInitialTask();

        // then
        verify(configService).getConfig("gotz.seederClass");
        verify(stepService).getStep(seederClass);
        verify(locatorSeeder).instance();
        verify(stepService).createTask(locatorSeeder);
    }

    @Test
    public void testCreateTaskShouldCatchFatal()
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, ConfigNotFoundException {
        // given
        String seederClass = "org.codetab.gotz.ext.XYZ";
        given(configService.getConfig("gotz.seederClass"))
                .willReturn(seederClass);
        given(stepService.getStep(any(String.class)))
                .willThrow(ClassNotFoundException.class);

        expect.expect(CriticalException.class);

        // when
        gSystem.createInitialTask();
    }

}
