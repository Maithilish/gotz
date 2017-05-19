package org.codetab.gotz.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.codetab.gotz.di.DInjector;
import org.codetab.gotz.exception.CriticalException;
import org.codetab.gotz.exception.DataDefNotFoundException;
import org.codetab.gotz.helper.DataDefDefaults;
import org.codetab.gotz.model.Axis;
import org.codetab.gotz.model.AxisName;
import org.codetab.gotz.model.DAxis;
import org.codetab.gotz.model.DFilter;
import org.codetab.gotz.model.DMember;
import org.codetab.gotz.model.Data;
import org.codetab.gotz.model.DataDef;
import org.codetab.gotz.model.Field;
import org.codetab.gotz.model.Fields;
import org.codetab.gotz.model.FieldsBase;
import org.codetab.gotz.model.Member;
import org.codetab.gotz.persistence.DataDefPersistence;
import org.codetab.gotz.validation.DataDefValidator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DataDefServiceTest {

    @Mock
    private BeanService beanService;
    @Mock
    private ConfigService configService;
    @Mock
    private DataDefPersistence dataDefPersistence;
    @Mock
    private DataDefValidator validator;
    @Mock
    private DataDefDefaults defaults;

    @InjectMocks
    private DataDefService dataDefService;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSingleton() {
        // given
        DInjector dInjector = new DInjector().instance(DInjector.class);

        // when
        DataDefService instanceA = dInjector.instance(DataDefService.class);
        DataDefService instanceB = dInjector.instance(DataDefService.class);

        // then
        assertThat(instanceA).isNotNull();
        assertThat(instanceA).isSameAs(instanceB);
    }

    @Test
    public void testInit() throws IllegalAccessException {
        List<DataDef> dataDefs = createSimpleDataDefs();

        given(beanService.getBeans(DataDef.class)).willReturn(dataDefs);
        given(dataDefPersistence.loadDataDefs()).willReturn(dataDefs);
        given(validator.validate()).willReturn(true);
        given(dataDefPersistence.markForUpdation(dataDefs, dataDefs)).willReturn(false);

        // when
        dataDefService.init();

        // then
        InOrder inOrder = inOrder(dataDefPersistence, beanService, defaults, validator);

        inOrder.verify(beanService).getBeans(DataDef.class);
        inOrder.verify(defaults).addFact(dataDefs.get(0));
        inOrder.verify(defaults).setOrder(dataDefs.get(0));
        inOrder.verify(defaults).setDates(dataDefs.get(0));
        inOrder.verify(defaults).addIndexRange(dataDefs.get(0));
        inOrder.verify(defaults).addFact(dataDefs.get(1));
        inOrder.verify(defaults).setOrder(dataDefs.get(1));
        inOrder.verify(defaults).setDates(dataDefs.get(1));
        inOrder.verify(defaults).addIndexRange(dataDefs.get(1));

        inOrder.verify(validator).setDataDef(dataDefs.get(0));
        inOrder.verify(validator).validate();
        inOrder.verify(validator).setDataDef(dataDefs.get(1));
        inOrder.verify(validator).validate();

        inOrder.verify(dataDefPersistence).loadDataDefs();

        List<DataDef> actual = dataDefService.getDataDefs();
        assertThat(actual).isSameAs(dataDefs);
    }

    @Test
    public void testInitUpdateDataDefs() throws IllegalAccessException {
        List<DataDef> dataDefs = createSimpleDataDefs();

        given(beanService.getBeans(DataDef.class)).willReturn(dataDefs);
        given(dataDefPersistence.loadDataDefs()).willReturn(dataDefs);
        given(validator.validate()).willReturn(true);
        given(dataDefPersistence.markForUpdation(dataDefs, dataDefs)).willReturn(true);

        // when
        dataDefService.init();

        // then
        InOrder inOrder = inOrder(dataDefPersistence, beanService, defaults, validator);

        inOrder.verify(dataDefPersistence).loadDataDefs();
        inOrder.verify(dataDefPersistence).storeDataDef(dataDefs.get(0));
        inOrder.verify(dataDefPersistence).storeDataDef(dataDefs.get(1));
        inOrder.verify(dataDefPersistence).loadDataDefs();
    }

    @Test
    public void testInitValidationThrowCriticalException() throws IllegalAccessException {
        // given
        List<DataDef> dataDefs = createSimpleDataDefs();

        given(beanService.getBeans(DataDef.class)).willReturn(dataDefs);
        given(dataDefPersistence.loadDataDefs()).willReturn(dataDefs);
        given(validator.validate()).willReturn(false);

        // when then
        expected.expect(CriticalException.class);
        dataDefService.init();
    }

    @Test
    public void testGetDataDef() throws DataDefNotFoundException {
        // given
        List<DataDef> dataDefs = createSimpleDataDefs();
        given(dataDefPersistence.loadDataDefs()).willReturn(dataDefs);
        dataDefService.init();

        // when
        DataDef actual = dataDefService.getDataDef("x");

        // then
        DataDef expected = dataDefs.stream().filter(e -> e.getName().equals("x"))
                .findFirst().get();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGetDataDefThrowException() throws DataDefNotFoundException {
        // given
        List<DataDef> dataDefs = createSimpleDataDefs();
        given(dataDefPersistence.loadDataDefs()).willReturn(dataDefs);
        dataDefService.init();

        // when then
        expected.expect(DataDefNotFoundException.class);
        dataDefService.getDataDef("z");
    }

    @Test
    public void testGetDataTemplate()
            throws ClassNotFoundException, DataDefNotFoundException, IOException {
        List<DataDef> dataDefs = createTestDataDefs();

        given(beanService.getBeans(DataDef.class)).willReturn(dataDefs);
        given(dataDefPersistence.loadDataDefs()).willReturn(dataDefs);
        given(validator.validate()).willReturn(true);

        dataDefService.init();

        Data data = dataDefService.getDataTemplate("x");

        Axis col = createAxis(AxisName.COL, "colvalue", "colmatch", 11, 12);
        Axis row = createAxis(AxisName.ROW, "rowvalue", "rowmatch", 21, 22);
        Axis fact = createAxis(AxisName.FACT, "factvalue", "factmatch", 31, 32);

        assertThat(data.getDataDef()).isEqualTo("x");

        assertThat(data.getMembers().size()).isEqualTo(1);
        Set<Axis> axes = data.getMembers().get(0).getAxes();
        assertThat(axes.size()).isEqualTo(3);
        assertThat(axes).contains(col, row, fact);

        // for test coverage
        data = dataDefService.getDataTemplate("x");

        assertThat(data.getDataDef()).isEqualTo("x");

        assertThat(data.getMembers().size()).isEqualTo(1);
        axes = data.getMembers().get(0).getAxes();
        assertThat(axes.size()).isEqualTo(3);
        assertThat(axes).contains(col, row, fact);
    }

    @Test
    public void testGenerateMemberSetsSynchronized() {
        Method method = MethodUtils.getMatchingMethod(DataDefService.class,
                "generateMemberSets", DataDef.class);
        assertThat(method).isNotNull();
        assertThat(Modifier.isSynchronized(method.getModifiers())).isTrue();
    }

    @Test
    public void testMemberSets() throws ClassNotFoundException, DataDefNotFoundException,
    IOException, IllegalAccessException {

        @SuppressWarnings("unchecked")
        Map<String, Set<Set<DMember>>> memberSetsMap = (Map<String, Set<Set<DMember>>>) FieldUtils
        .readDeclaredField(dataDefService, "memberSetsMap", true);

        assertThat(memberSetsMap).isNotNull();
    }

    @Test
    public void testGetDataTemplateMemberFields()
            throws ClassNotFoundException, DataDefNotFoundException, IOException {
        List<DataDef> dataDefs = createTestDataDefs();
        DataDef dataDef = dataDefs.get(0);

        given(beanService.getBeans(DataDef.class)).willReturn(dataDefs);
        given(dataDefPersistence.loadDataDefs()).willReturn(dataDefs);
        given(validator.validate()).willReturn(true);

        dataDefService.init();

        Data data = dataDefService.getDataTemplate("x");

        Member member = data.getMembers().get(0);

        assertThat(member.getFields()).isEqualTo(dataDef.getFields());
        assertThat(member.getGroup()).isEqualTo("xyz");
    }

    @Test
    public void testGetDataTemplateNoFields()
            throws ClassNotFoundException, DataDefNotFoundException, IOException {
        List<DataDef> dataDefs = createTestDataDefs();
        DataDef dataDef = dataDefs.get(0);
        dataDef.getFields().clear();

        given(beanService.getBeans(DataDef.class)).willReturn(dataDefs);
        given(dataDefPersistence.loadDataDefs()).willReturn(dataDefs);
        given(validator.validate()).willReturn(true);

        dataDefService.init();

        Data data = dataDefService.getDataTemplate("x");

        Member member = data.getMembers().get(0);

        assertThat(member.getFields()).isEqualTo(dataDef.getFields());
        assertThat(member.getGroup()).isNull();
    }

    @Test
    public void testGetFilterMap()
            throws IllegalArgumentException, DataDefNotFoundException {
        List<DataDef> dataDefs = createTestDataDefs();

        given(beanService.getBeans(DataDef.class)).willReturn(dataDefs);
        given(dataDefPersistence.loadDataDefs()).willReturn(dataDefs);
        given(validator.validate()).willReturn(true);

        dataDefService.init();

        Map<AxisName, List<FieldsBase>> filterMap = dataDefService.getFilterMap("x");

        DFilter filter = createFilter();
        assertThat(filterMap.get(AxisName.FACT)).isNull();
        assertThat(filterMap.get(AxisName.COL)).isNull();
        assertThat(filterMap.get(AxisName.ROW)).isEqualTo(filter.getFields());
    }

    @Test
    public void testGetCount() {
        // given
        List<DataDef> dataDefs = createSimpleDataDefs();
        given(dataDefPersistence.loadDataDefs()).willReturn(dataDefs);
        dataDefService.init();

        // when
        int count = dataDefService.getCount();

        // then
        assertThat(count).isEqualTo(2);
    }

    private List<DataDef> createSimpleDataDefs() {
        DataDef dataDef1 = new DataDef();
        dataDef1.setName("x");
        DataDef dataDef2 = new DataDef();
        dataDef2.setName("y");

        List<DataDef> dataDefs = new ArrayList<>();
        dataDefs.add(dataDef1);
        dataDefs.add(dataDef2);
        return dataDefs;
    }

    private List<DataDef> createTestDataDefs() {
        DMember member = new DMember();
        member.setAxis("col");
        member.setIndex(11);
        member.setOrder(12);
        member.setMatch("colmatch");
        member.setName("col");
        member.setValue("colvalue");

        DAxis col = new DAxis();
        col.setName("col");
        col.getMember().add(member);

        member = new DMember();
        member.setAxis("row");
        member.setIndex(21);
        member.setOrder(22);
        member.setMatch("rowmatch");
        member.setName("row");
        member.setValue("rowvalue");

        DAxis row = new DAxis();
        row.setName("row");
        row.getMember().add(member);

        member = new DMember();
        member.setAxis("fact");
        member.setName("fact");
        member.setIndex(31);
        member.setOrder(32);
        member.setValue("factvalue");
        member.setMatch("factmatch");

        DAxis fact = new DAxis();
        fact.setName("fact");
        fact.getMember().add(member);

        Fields fields = new Fields();
        fields.setName("member");
        fields.setValue("row");

        Field field = new Field();
        field.setName("x1");
        field.setValue("y1");
        fields.getFields().add(field);

        field = new Field();
        field.setName("group");
        field.setValue("xyz");
        fields.getFields().add(field);

        DFilter dFilter = createFilter();
        row.setFilter(dFilter);

        DataDef dataDef = new DataDef();
        dataDef.setName("x");
        dataDef.getAxis().add(col);
        dataDef.getAxis().add(row);
        dataDef.getAxis().add(fact);
        dataDef.getFields().add(fields);

        List<DataDef> dataDefs = new ArrayList<>();
        dataDefs.add(dataDef);

        return dataDefs;
    }

    private DFilter createFilter() {
        Field field;
        Fields filter = new Fields();
        filter.setName("group");
        filter.setValue("value");
        field = new Field();
        field.setName("f1");
        field.setValue("v1");
        filter.getFields().add(field);
        field = new Field();
        field.setName("f2");
        field.setValue("v2");
        filter.getFields().add(field);

        DFilter dFilter = new DFilter();
        dFilter.setAxis("row");
        dFilter.getFields().add(filter);
        return dFilter;
    }

    private Axis createAxis(AxisName name, String value, String match, int index,
            int order) {
        Axis axis = new Axis();
        axis.setName(name);
        axis.setValue(value);
        axis.setMatch(match);
        axis.setIndex(index);
        axis.setOrder(order);
        axis.getFields();
        return axis;
    }

}