package org.codetab.gotz.itest.jdo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.jdo.JDODataStoreException;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManagerFactory;

import org.codetab.gotz.dao.ORM;
import org.codetab.gotz.dao.jdo.DataDao;
import org.codetab.gotz.itest.DaoUtilFactory;
import org.codetab.gotz.itest.IDaoUtil;
import org.codetab.gotz.model.Axis;
import org.codetab.gotz.model.AxisName;
import org.codetab.gotz.model.Data;
import org.codetab.gotz.model.Member;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * <p>
 * DataDao tests.
 * @author Maithilish
 *
 */
public class DataDaoTestIT {

    private static IDaoUtil daoUtil;
    private static PersistenceManagerFactory pmf;
    private static HashSet<String> schemaClasses;

    @Rule
    public ExpectedException testRule = ExpectedException.none();

    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        daoUtil = DaoUtilFactory.getDaoFactory(ORM.JDO).getUtilDao();
        pmf = daoUtil.getPersistenceManagerFactory();

        schemaClasses = new HashSet<>();
        schemaClasses.add("org.codetab.gotz.model.Data");
        daoUtil.deleteSchemaForClasses(schemaClasses);
    }

    @Before
    public void setUp() {
        // create schema
        daoUtil.createSchemaForClasses(schemaClasses);
    }

    @After
    public void tearDown() {
        // drop schema
        daoUtil.deleteSchemaForClasses(schemaClasses);
        // clear cache
        daoUtil.clearCache();
    }

    @Test
    public void testDaoPMFNull() {
        try {
            new DataDao(null);
            fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("pmf must not be null");
        }
    }

    @Test
    public void testStoreData() {

        Data data = createData();

        DataDao dataDao = new DataDao(pmf);
        dataDao.storeData(data);

        assertNotNull(data.getId());
    }

    @Test
    public void testStoreDataUnique()
            throws ClassNotFoundException, SQLException, IOException {

        Data data = createData();
        Data data2 = createData();

        DataDao dataDao = new DataDao(pmf);
        dataDao.storeData(data);

        assertNotNull(data.getId());

        testRule.expect(JDODataStoreException.class);
        dataDao.storeData(data2);
    }

    @Test
    public void testStoreNullParams() {

        DataDao dataDao = new DataDao(pmf);

        try {
            dataDao.storeData(null);
            fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("data must not be null");
        }
    }

    @Test
    public void testGetData() {

        DataDao dataDao = new DataDao(pmf);
        Data data = createData();
        dataDao.storeData(data);

        Data actual =
                dataDao.getData(data.getDocumentId(), data.getDataDefId());

        assertThat(actual.getId()).isEqualTo(data.getId());
        assertThat(actual.getName()).isEqualTo(data.getName());
        assertThat(actual.getDataDefId()).isEqualTo(data.getDataDefId());
        assertThat(actual.getDocumentId()).isEqualTo(data.getDocumentId());
        assertThat(actual.getDataDef()).isEqualTo(data.getDataDef());
        assertThat(actual.getMembers()).isEqualTo(data.getMembers());
    }

    @Test
    public void testGetDataNonExistent() {
        DataDao dataDao = new DataDao(pmf);

        Long docId = 100L;
        Long dataDefId = 100L;
        Data actualData = dataDao.getData(docId, dataDefId);
        assertNull(actualData);
    }

    @Test
    public void testGetDataNotUnique()
            throws ClassNotFoundException, SQLException, IOException {

        Data data = createData();
        Data data2 = createData();

        DataDao dataDao = new DataDao(pmf);
        dataDao.storeData(data);
        daoUtil.dropConstraint(pmf, "data", "unique_data");
        dataDao.storeData(data2);

        testRule.expect(IllegalStateException.class);
        dataDao.getData(data.getDocumentId(), data.getDataDefId());
    }

    @Test
    public void testGetDataById() {

        DataDao dataDao = new DataDao(pmf);
        Data data = createData();
        dataDao.storeData(data);

        Data actual = dataDao.getData(data.getId());

        assertThat(actual.getId()).isEqualTo(data.getId());
        assertThat(actual.getName()).isEqualTo(data.getName());
        assertThat(actual.getDataDefId()).isEqualTo(data.getDataDefId());
        assertThat(actual.getDocumentId()).isEqualTo(data.getDocumentId());
        assertThat(actual.getDataDef()).isEqualTo(data.getDataDef());
        assertThat(actual.getMembers()).isEqualTo(data.getMembers());
    }

    @Test
    public void testGetDataByIdNonExists() {
        DataDao dataDao = new DataDao(pmf);

        long id = 100L;

        testRule.expect(JDOObjectNotFoundException.class);
        dataDao.getData(id);
    }

    private Data createData() {
        long dataDefId = 0L;
        long documentId = 0L;
        String dataName = "tdataname";
        String dataDef = "tdatadef";

        List<Member> members = createMembers();

        Data data = new Data();
        data.setName(dataName);
        data.setDataDefId(dataDefId);
        data.setDocumentId(documentId);
        data.setDataDef(dataDef);
        data.getMembers().addAll(members);
        return data;
    }

    private List<Member> createMembers() {
        List<Member> members = new ArrayList<>();

        Axis col = new Axis();
        col.setName(AxisName.COL);
        Axis row = new Axis();
        row.setName(AxisName.ROW);

        Member m1 = new Member();
        m1.setId(0L);
        m1.setName("tmember1");
        m1.setGroup("tgroup1");
        m1.getAxes().add(col);
        m1.getAxes().add(row);
        members.add(m1);

        col = new Axis();
        col.setName(AxisName.COL);
        row = new Axis();
        row.setName(AxisName.ROW);

        Member m2 = new Member();
        m2.setId(1L);
        m2.setName("tmember2");
        m2.setGroup("tgroup2");
        m2.getAxes().add(col);
        m2.getAxes().add(row);
        members.add(m2);

        return members;
    }
}
