package org.codetab.gotz.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.codetab.gotz.dao.DaoFactoryProvider;
import org.codetab.gotz.dao.IDataDefDao;
import org.codetab.gotz.dao.ORM;
import org.codetab.gotz.dao.jdo.JdoDaoFactory;
import org.codetab.gotz.exception.CriticalException;
import org.codetab.gotz.exception.FieldsNotFoundException;
import org.codetab.gotz.model.DAxis;
import org.codetab.gotz.model.DataDef;
import org.codetab.gotz.model.helper.FieldsHelper;
import org.codetab.gotz.shared.ConfigService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * <p>
 * DataDefPersistence tests.
 * @author Maithilish
 *
 */
public class DataDefPersistenceTest {

    @Mock
    private ConfigService configService;

    @Mock
    private DaoFactoryProvider daoFactoryProvider;

    @Mock
    private JdoDaoFactory jdoDao;

    @Mock
    private IDataDefDao dataDefDao;

    @Mock
    private FieldsHelper fieldsHelper;

    @InjectMocks
    private DataDefPersistence dataDefPersistence;

    @Rule
    public ExpectedException testRule = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoadDataDefs() {
        Date runDate = new Date();
        List<DataDef> dataDefs = new ArrayList<>();
        given(configService.getOrmType()).willReturn(ORM.JDO);
        given(daoFactoryProvider.getDaoFactory(ORM.JDO)).willReturn(jdoDao);
        given(jdoDao.getDataDefDao()).willReturn(dataDefDao);
        given(configService.getRunDateTime()).willReturn(runDate);
        given(dataDefDao.getDataDefs(runDate)).willReturn(dataDefs);

        given(configService.isPersist("gotz.useDatastore")).willReturn(true);

        List<DataDef> actual = dataDefPersistence.loadDataDefs();

        InOrder inOrder =
                inOrder(configService, daoFactoryProvider, dataDefDao, jdoDao);
        inOrder.verify(configService).getOrmType();
        inOrder.verify(daoFactoryProvider).getDaoFactory(ORM.JDO);
        inOrder.verify(jdoDao).getDataDefDao();
        inOrder.verify(configService).getRunDateTime();
        inOrder.verify(dataDefDao).getDataDefs(runDate);
        assertThat(actual).isSameAs(dataDefs);
    }

    @Test
    public void testLoadDataDefsShouldThrowException() {
        given(configService.getOrmType()).willReturn(ORM.JDO);
        given(daoFactoryProvider.getDaoFactory(ORM.JDO))
                .willThrow(RuntimeException.class);

        given(configService.isPersist("gotz.useDatastore")).willReturn(true);

        testRule.expect(CriticalException.class);
        dataDefPersistence.loadDataDefs();
    }

    @Test
    public void testStoreDataDef() throws FieldsNotFoundException {
        DataDef dataDef = new DataDef();
        given(configService.getOrmType()).willReturn(ORM.JDO);
        given(daoFactoryProvider.getDaoFactory(ORM.JDO)).willReturn(jdoDao);
        given(jdoDao.getDataDefDao()).willReturn(dataDefDao);

        given(configService.isPersist("gotz.useDatastore")).willReturn(true);
        given(configService.isPersist("gotz.persist.dataDef")).willReturn(true);
        given(fieldsHelper.isTrue("//xf:persist", dataDef.getFields()))
                .willReturn(true);

        dataDefPersistence.storeDataDef(dataDef);

        InOrder inOrder =
                inOrder(configService, daoFactoryProvider, dataDefDao, jdoDao);
        inOrder.verify(configService).getOrmType();
        inOrder.verify(daoFactoryProvider).getDaoFactory(ORM.JDO);
        inOrder.verify(jdoDao).getDataDefDao();
        inOrder.verify(dataDefDao).storeDataDef(dataDef);
    }

    @Test
    public void testStoreDataDefShouldThrowException()
            throws FieldsNotFoundException {
        DataDef dataDef = new DataDef();
        given(configService.getOrmType()).willReturn(ORM.JDO);
        given(daoFactoryProvider.getDaoFactory(ORM.JDO))
                .willThrow(RuntimeException.class);

        given(configService.isPersist("gotz.useDatastore")).willReturn(true);
        given(configService.isPersist("gotz.persist.dataDef")).willReturn(true);
        given(fieldsHelper.isTrue("//xf:persist", dataDef.getFields()))
                .willReturn(true);

        testRule.expect(CriticalException.class);
        dataDefPersistence.storeDataDef(dataDef);
    }

    @Test
    public void testStoreNullParams() {
        try {
            dataDefPersistence.storeDataDef(null);
            fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("dataDef must not be null");
        }
    }

    @Test
    public void testMarkForUpdationNoChanges() {
        DAxis axis = new DAxis();
        axis.setName("col");

        DataDef dataDefX = new DataDef();
        dataDefX.setName("x");
        dataDefX.getAxis().add(axis);
        DataDef dataDefY = new DataDef();
        dataDefY.setName("y");
        dataDefY.getAxis().add(axis);

        List<DataDef> dataDefs = new ArrayList<>();
        dataDefs.add(dataDefX);
        dataDefs.add(dataDefY);

        DAxis newAxis = new DAxis();
        newAxis.setName("col");
        DataDef newDataDefX = new DataDef();
        newDataDefX.setName("x");
        newDataDefX.getAxis().add(newAxis);
        DataDef newDataDefY = new DataDef();
        newDataDefY.setName("y");
        newDataDefY.getAxis().add(newAxis);

        List<DataDef> newDataDefs = new ArrayList<>();
        newDataDefs.add(newDataDefX);
        newDataDefs.add(newDataDefY);

        boolean updates =
                dataDefPersistence.markForUpdation(dataDefs, newDataDefs);

        assertThat(updates).isEqualTo(false);
        assertThat(dataDefs.size()).isEqualTo(2);
    }

    @Test
    public void testMarkForUpdationItemNew() {
        DataDef dataDefX = new DataDef();
        dataDefX.setName("x");
        DataDef dataDefY = new DataDef();
        dataDefY.setName("y");

        List<DataDef> newDataDefs = new ArrayList<>();
        newDataDefs.add(dataDefX);
        newDataDefs.add(dataDefY);

        List<DataDef> dataDefs = new ArrayList<>();

        boolean updates =
                dataDefPersistence.markForUpdation(dataDefs, newDataDefs);

        assertThat(updates).isEqualTo(true);
        assertThat(dataDefs).contains(dataDefX);
        assertThat(dataDefs).contains(dataDefY);
    }

    @Test
    public void testMarkForUpdationItemChanged() {

        Date runDate = new Date();
        Date highDate = DateUtils.addYears(runDate, 50);

        DataDef dataDefX = new DataDef();
        dataDefX.setName("x");
        dataDefX.setToDate(highDate);
        DataDef dataDefY = new DataDef();
        dataDefY.setName("y");
        dataDefY.setToDate(highDate);

        List<DataDef> dataDefs = new ArrayList<>();
        dataDefs.add(dataDefX);
        dataDefs.add(dataDefY);

        DAxis axis = new DAxis();
        axis.setName("col");
        DataDef newDataDefX = new DataDef();
        newDataDefX.setName("x");
        newDataDefX.setToDate(highDate);
        newDataDefX.getAxis().add(axis);
        DataDef newDataDefY = new DataDef();
        newDataDefY.setName("y");
        newDataDefY.setToDate(highDate);
        newDataDefY.getAxis().add(axis);

        List<DataDef> newDataDefs = new ArrayList<>();
        newDataDefs.add(newDataDefX);
        newDataDefs.add(newDataDefY);

        given(configService.getRunDateTime()).willReturn(runDate);

        boolean updates =
                dataDefPersistence.markForUpdation(dataDefs, newDataDefs);

        assertThat(updates).isEqualTo(true);
        assertThat(dataDefs).contains(dataDefX);
        assertThat(dataDefs).contains(dataDefY);
        assertThat(dataDefs).contains(newDataDefX);
        assertThat(dataDefs).contains(newDataDefY);

        assertThat(dataDefX.getToDate()).isEqualTo(runDate);
        assertThat(dataDefY.getToDate()).isEqualTo(runDate);
        assertThat(newDataDefX.getToDate()).isEqualTo(highDate);
        assertThat(newDataDefY.getToDate()).isEqualTo(highDate);
    }

    @Test
    public void testMarkForUpdationNullParams() {
        try {
            dataDefPersistence.markForUpdation(null, new ArrayList<>());
            fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("dataDefs must not be null");
        }

        try {
            dataDefPersistence.markForUpdation(new ArrayList<>(), null);
            fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage())
                    .isEqualTo("newDataDefs must not be null");
        }
    }

}
