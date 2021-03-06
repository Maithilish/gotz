package org.codetab.gotz.step.extract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.codetab.gotz.di.DInjector;
import org.codetab.gotz.exception.FieldsException;
import org.codetab.gotz.exception.StepRunException;
import org.codetab.gotz.helper.ThreadSleep;
import org.codetab.gotz.metrics.MetricsHelper;
import org.codetab.gotz.model.Fields;
import org.codetab.gotz.model.Labels;
import org.codetab.gotz.model.Locator;
import org.codetab.gotz.model.helper.FieldsHelper;
import org.codetab.gotz.model.helper.LocatorFieldsHelper;
import org.codetab.gotz.model.helper.LocatorHelper;
import org.codetab.gotz.shared.StepService;
import org.codetab.gotz.testutil.XOBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.codahale.metrics.Meter;

/**
 * <p>
 * LocatorSeeder Tests.
 *
 * @author Maithilish
 *
 */

public class LocatorSeederTest {

    @Mock
    private StepService stepService;
    @Mock
    private LocatorHelper locatorHelper;
    @Mock
    private MetricsHelper metricsHelper;
    @Mock
    private LocatorFieldsHelper locatorFieldsHelper;
    @Mock
    private ThreadSleep threadSleep;
    @Spy
    private FieldsHelper fieldsHelper;
    @Spy
    private DInjector dInjector;

    @InjectMocks
    private LocatorSeeder locatorSeeder;

    @Rule
    public ExpectedException testRule = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInstance() {
        assertThat(locatorSeeder.isConsistent()).isFalse();
        assertThat(locatorSeeder.getStepType()).isNull();
        assertThat(locatorSeeder.instance().getStepType()).isEqualTo("seeder");
    }

    @Test
    public void testSetInput() throws IllegalAccessException, FieldsException {
        //@formatter:off
        Fields fields = new XOBuilder<Fields>()
          .add("<xf:f1>v1</xf:f1>")
          .buildFields();
        //@formatter:on

        Locator locator = new Locator();
        locator.setName("x");
        locator.setGroup("gx");
        locator.setFields(fields);

        Meter meter = new Meter();
        given(metricsHelper.getMeter(locatorSeeder, "locator", "parsed"))
                .willReturn(meter);

        // when
        locatorSeeder.setInput(locator);

        @SuppressWarnings("unchecked")
        List<Locator> actual = (List<Locator>) FieldUtils
                .readDeclaredField(locatorSeeder, "locatorList", true);

        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual).contains(locator);
        assertThat(actual.get(0).getFields().getNodes().size()).isEqualTo(1);
        assertThat(meter.getCount()).isEqualTo(1L);
    }

    @Test
    public void testSetInputOtherThanLocator() {
        // when
        testRule.expect(StepRunException.class);
        locatorSeeder.setInput("some object");
    }

    @Test
    public void testInitialize() throws IllegalAccessException {
        List<Locator> locators = createTestObjects();
        Meter meter = new Meter();

        given(locatorHelper.getLocatorsFromBeans()).willReturn(locators);
        given(metricsHelper.getMeter(locatorSeeder, "locator", "provided"))
                .willReturn(meter);

        // when
        locatorSeeder.initialize();

        @SuppressWarnings("unchecked")
        List<Locator> actual = (List<Locator>) FieldUtils
                .readDeclaredField(locatorSeeder, "locatorList", true);

        assertThat(actual).isEqualTo(locators);
        assertThat(meter.getCount()).isEqualTo(3L);
    }

    @Test
    public void testInitializeAfterSetInput() throws IllegalAccessException {
        Locator locator = new Locator();
        locator.setName("x");
        locator.setGroup("gx");
        Meter meter1 = new Meter();
        Meter meter2 = new Meter();

        given(metricsHelper.getMeter(locatorSeeder, "locator", "provided"))
                .willReturn(meter1);
        given(metricsHelper.getMeter(locatorSeeder, "locator", "parsed"))
                .willReturn(meter2);

        locatorSeeder.setInput(locator);

        // when
        locatorSeeder.initialize();

        @SuppressWarnings("unchecked")
        List<Locator> actual = (List<Locator>) FieldUtils
                .readDeclaredField(locatorSeeder, "locatorList", true);

        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual).contains(locator);
        assertThat(meter1.getCount()).isEqualTo(0);
        assertThat(meter2.getCount()).isEqualTo(1L);

        verifyZeroInteractions(locatorHelper);
    }

    @Test
    public void testInitializeForkedLocators() throws IllegalAccessException {
        List<Locator> locators = createTestObjects();

        List<Locator> forkedLocators = createTestObjects();
        Locator extraLocator = new Locator();
        extraLocator.setName("x");
        extraLocator.setGroup("gx");
        forkedLocators.add(extraLocator);

        Meter meter = new Meter();

        given(locatorHelper.getLocatorsFromBeans()).willReturn(locators);
        given(locatorHelper.forkLocators(locators)).willReturn(forkedLocators);
        given(metricsHelper.getMeter(locatorSeeder, "locator", "provided"))
                .willReturn(meter);

        // when
        locatorSeeder.initialize();

        @SuppressWarnings("unchecked")
        List<Locator> actual = (List<Locator>) FieldUtils
                .readDeclaredField(locatorSeeder, "locatorList", true);

        assertThat(actual).isEqualTo(forkedLocators);
        assertThat(meter.getCount()).isEqualTo(4L);
    }

    @Test
    public void testInitializeForkedLocatorsEmptyList()
            throws IllegalAccessException {
        List<Locator> locators = createTestObjects();
        List<Locator> forkedLocators = new ArrayList<>();
        Meter meter = new Meter();

        given(locatorHelper.getLocatorsFromBeans()).willReturn(locators);
        given(locatorHelper.forkLocators(locators)).willReturn(forkedLocators);
        given(metricsHelper.getMeter(locatorSeeder, "locator", "provided"))
                .willReturn(meter);

        // when
        locatorSeeder.initialize();

        @SuppressWarnings("unchecked")
        List<Locator> actual = (List<Locator>) FieldUtils
                .readDeclaredField(locatorSeeder, "locatorList", true);

        assertThat(actual).isEqualTo(locators);
        assertThat(meter.getCount()).isEqualTo(3L);
    }

    @Test
    public void testProcessSetGroupFields() throws FieldsException {
        List<Locator> locators = createTestObjects();
        Meter meter = new Meter();

        given(locatorHelper.getLocatorsFromBeans()).willReturn(locators);
        given(metricsHelper.getMeter(locatorSeeder, "locator", "provided"))
                .willReturn(meter);
        locatorSeeder.initialize();

        Fields groupOneFields = fieldsHelper.createFields();
        fieldsHelper.addElement("x", "xv", groupOneFields);
        Fields groupTwoFields = fieldsHelper.createFields();
        fieldsHelper.addElement("y", "yv", groupOneFields);

        given(locatorFieldsHelper.getFields(Locator.class.getName(), "g1"))
                .willReturn(groupOneFields);
        given(locatorFieldsHelper.getFields(Locator.class.getName(), "g2"))
                .willReturn(groupTwoFields);

        // when
        locatorSeeder.process();

        assertThat(locators.get(0).getFields()).isEqualTo(groupOneFields);
        assertThat(locators.get(1).getFields()).isEqualTo(groupOneFields);
        assertThat(locators.get(2).getFields()).isEqualTo(groupTwoFields);
    }

    @Test
    public void testProcessThrowsException() throws FieldsException {
        List<Locator> locators = createTestObjects();
        given(locatorHelper.getLocatorsFromBeans()).willReturn(locators);
        given(metricsHelper.getMeter(locatorSeeder, "locator", "provided"))
                .willReturn(new Meter());
        locatorSeeder.initialize();

        Fields groupOneFields = fieldsHelper.createFields();
        fieldsHelper.addElement("x", "xv", groupOneFields);

        given(locatorFieldsHelper.getFields(Locator.class.getName(), "g1"))
                .willThrow(FieldsException.class);

        testRule.expect(StepRunException.class);
        locatorSeeder.process();
    }

    @Test
    public void testHandover() throws FieldsException, ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        Fields fields1 = fieldsHelper.createFields();
        fieldsHelper.addElement("x", "xv", fields1);
        Fields fields2 = fieldsHelper.createFields();
        fieldsHelper.addElement("y", "yv", fields2);
        Fields fields3 = fieldsHelper.createFields();
        fieldsHelper.addElement("z", "zv", fields3);

        List<Locator> locators = createTestObjects();
        Locator locator1 = locators.get(0);
        locator1.setFields(fields1);
        Locator locator2 = locators.get(1);
        locator2.setFields(fields2);
        Locator locator3 = locators.get(2);
        locator3.setFields(fields3);

        Labels labels1 = new Labels(locator1.getName(), locator1.getGroup());
        Labels labels2 = new Labels(locator2.getName(), locator2.getGroup());
        Labels labels3 = new Labels(locator3.getName(), locator3.getGroup());

        LocatorSeeder locatorSeeder1 = dInjector.instance(LocatorSeeder.class);
        LocatorSeeder locatorSeeder2 = dInjector.instance(LocatorSeeder.class);
        LocatorSeeder locatorSeeder3 = dInjector.instance(LocatorSeeder.class);

        Meter meter1 = new Meter();
        Meter meter2 = new Meter();

        given(locatorHelper.getLocatorsFromBeans()).willReturn(locators);
        when(stepService.getStep(LocatorSeeder.class.getName()))
                .thenReturn(locatorSeeder1, locatorSeeder2, locatorSeeder3);
        given(locatorHelper.createLabels(locator1)).willReturn(labels1);
        given(locatorHelper.createLabels(locator2)).willReturn(labels2);
        given(locatorHelper.createLabels(locator3)).willReturn(labels3);
        given(metricsHelper.getMeter(locatorSeeder, "locator", "provided"))
                .willReturn(meter1);
        given(metricsHelper.getMeter(locatorSeeder, "locator", "seeded"))
                .willReturn(meter2);

        locatorSeeder.initialize();

        locatorSeeder.handover();

        assertThat(meter1.getCount()).isEqualTo(3L);
        assertThat(meter2.getCount()).isEqualTo(3L);

        InOrder inOrder = inOrder(stepService, threadSleep);
        inOrder.verify(stepService).pushTask(locatorSeeder1, locator1, labels1,
                locator1.getFields());
        inOrder.verify(threadSleep).sleep(1000);
        inOrder.verify(stepService).pushTask(locatorSeeder2, locator2, labels2,
                locator2.getFields());
        inOrder.verify(threadSleep).sleep(1000);
        inOrder.verify(stepService).pushTask(locatorSeeder3, locator3, labels3,
                locator3.getFields());
        inOrder.verify(threadSleep).sleep(1000);
    }

    @Test
    public void testHandoverGetSeederStepThrowsException()
            throws FieldsException, ClassNotFoundException,
            InstantiationException, IllegalAccessException {

        List<Locator> locators = createTestObjects();

        Meter meter1 = new Meter();
        Meter meter2 = new Meter();

        given(locatorHelper.getLocatorsFromBeans()).willReturn(locators);

        given(stepService.getStep(LocatorSeeder.class.getName()))
                .willThrow(ClassNotFoundException.class)
                .willThrow(InstantiationException.class)
                .willThrow(IllegalAccessException.class);

        given(metricsHelper.getMeter(locatorSeeder, "locator", "provided"))
                .willReturn(meter1);
        given(metricsHelper.getMeter(locatorSeeder, "locator", "seeded"))
                .willReturn(meter2);

        locatorSeeder.initialize();

        try {
            locatorSeeder.handover();
            fail("should throw exception");
        } catch (StepRunException e) {
            assertThat(e.getCause()).isInstanceOf(ClassNotFoundException.class);
        }

        try {
            locatorSeeder.handover();
            fail("should throw exception");
        } catch (StepRunException e) {
            assertThat(e.getCause()).isInstanceOf(InstantiationException.class);
        }

        try {
            locatorSeeder.handover();
            fail("should throw exception");
        } catch (StepRunException e) {
            assertThat(e.getCause()).isInstanceOf(IllegalAccessException.class);
        }

        assertThat(meter1.getCount()).isEqualTo(3L); // provided
        assertThat(meter2.getCount()).isEqualTo(0L); // seeded
    }

    private List<Locator> createTestObjects() {
        Locator locator1 = new Locator();
        locator1.setName("n1");
        locator1.setGroup("g1");

        Locator locator2 = new Locator();
        locator2.setName("n2");
        locator2.setGroup("g1");

        Locator locator3 = new Locator();
        locator3.setName("n3");
        locator3.setGroup("g2");

        List<Locator> locators = new ArrayList<>();
        locators.add(locator1);
        locators.add(locator2);
        locators.add(locator3);

        return locators;
    }

}
