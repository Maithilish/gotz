package in.m.picks.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DataDefTest {

	private DataDef dataDef;

	@Before
	public void setup() {
		dataDef = new DataDef();
	}

	@Test
	public void testAxis() {
		assertNotNull(dataDef.getAxes());
	}

	@Test
	public void testAddAxis() {
		DAxis col = new DAxis();
		col.setName("col");
		dataDef.addAxis(col);
		DAxis row = new DAxis();
		row.setName("row");
		dataDef.addAxis(row);

		List<DAxis> axes = dataDef.getAxes();
		assertEquals(2, axes.size());

		assertSame(col, axes.get(0));
		assertSame(row, axes.get(1));
	}

	@Test
	public void testGetAxisAfieldsByGroup() {
		Afield query1 = new Afield("tname1", "tvalue1");
		query1.setGroup("query");
		Afield query2 = new Afield("tname2", "tvalue2");
		query2.setGroup("query");

		DAxis col = new DAxis();
		col.setName("col");
		col.addAfield(query1);
		col.addAfield(query2);

		dataDef.addAxis(col);

		Afields afields = dataDef.getAxisAfieldsByGroup("col", "query");
		assertEquals(2, afields.getAfields().size());
		assertTrue(afields.getAfields().contains(query1));
		assertTrue(afields.getAfields().contains(query2));

		// test - axis is present but no afields
		assertEquals(0,
				dataDef.getAxisAfieldsByGroup("col", "script").getAfields().size());
	}

	@Test
	public void testGetAxisAfieldsByGroupNull() {
		// test - axis is absent
		assertNull(null, dataDef.getAxisAfieldsByGroup("col", "query"));
	}

	@Test
	public void testGetAxisAfieldsByName() {
		Afield query1 = new Afield("tname1", "tvalue1");
		query1.setGroup("query");
		Afield query2 = new Afield("tname2", "tvalue2");
		query2.setGroup("query");

		DAxis col = new DAxis();
		col.setName("col");
		col.addAfield(query1);
		col.addAfield(query2);

		dataDef.addAxis(col);

		Afields afields = dataDef.getAxisAfieldsByName("col", "tname1");
		assertEquals(1, afields.getAfields().size());
		assertTrue(afields.getAfields().contains(query1));

		// test - axis is present but no afields
		assertEquals(0,
				dataDef.getAxisAfieldsByName("col", "tnamex").getAfields().size());
	}

	@Test
	public void testGetAxisAfieldsByNameNull() {
		// test - axis is absent
		assertNull(null, dataDef.getAxisAfieldsByName("col", "tname"));
	}

	@Test
	public void testDefaultType() {
		assertNull(dataDef.getType());
		dataDef.setType("locator");
		assertEquals("locator", dataDef.getType());
		dataDef.setType(null);
		assertEquals("data", dataDef.getType());
	}

	@Test
	public void testHashCode() {
		DataDef df = createDataDef();
		assertEquals(-2033534992, df.hashCode());
	}

	@Test
	public void testEqualsObject() {
		DataDef df1 = createDataDef();
		DataDef df2 = createDataDef();

		assertTrue(df1 != df2);
		assertTrue(df1.equals(df2) && df2.equals(df1));
		assertTrue(df1.hashCode() == df2.hashCode());
	}

	private DataDef createDataDef() {
		Afield query1 = new Afield("tname1", "tvalue1");
		query1.setGroup("query");
		Afield query2 = new Afield("tname2", "tvalue2");
		query2.setGroup("query");

		DAxis col = new DAxis();
		col.setName("col");
		col.addAfield(query1);
		col.addAfield(query2);

		DataDef df = new DataDef();
		df.addAxis(col);
		df.setName("tname");
		df.setParser("tparser");
		df.setType("tlocator");
		return df;
	}
}
