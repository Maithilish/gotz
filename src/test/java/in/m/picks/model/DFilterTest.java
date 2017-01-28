package in.m.picks.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DFilterTest {

	@Test
	public void testHashCode() {
		DFilter df = getFilter();
		assertEquals(-911592512, df.hashCode());
	}

	@Test
	public void testEqualsObject() {
		DFilter df1 = getFilter();
		DFilter df2 = getFilter();

		assertNotSame(df1, df2);
		assertTrue(df1.equals(df2) && df2.equals(df1));
		assertTrue(df1.hashCode() == df2.hashCode());
	}

	private DFilter getFilter() {
		Afield af1 = new Afield("tname1", "tvalue1");
		Afield af2 = new Afield("tname2", "tvalue2");

		DFilter df = new DFilter();
		df.setAxis("col");
		df.addAfield(af1);
		df.addAfield(af2);
		return df;
	}

}
