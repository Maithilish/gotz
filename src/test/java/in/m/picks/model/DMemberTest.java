package in.m.picks.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DMemberTest {

	@Test
	public void testDMemberDMember() {
		DMember m1 = getDMember();
		DMember m2 = new DMember(m1);

		assertTrue(m1.equals(m2) && m2.equals(m1));
		assertEquals(m1.hashCode(), m2.hashCode());

		assertNotSame(m1, m2);
		assertNotSame(m1.getAfields(), m2.getAfields());
		assertNotSame(m1.getAfield("tname"), m2.getAfield("tname"));

	}

	@Test
	public void testHashCode() {
		DMember m1 = getDMember();
		assertEquals(-1900680561, m1.hashCode());
	}

	@Test
	public void testEqualsObject() {
		DMember m1 = getDMember();
		DMember m2 = getDMember();

		assertTrue(m1 != m2); // objects are different
		assertTrue(m1.equals(m2) && m2.equals(m1)); // but they are equal
		assertTrue(m1.hashCode() == m2.hashCode());
	}

	private DMember getDMember() {
		DMember member = new DMember();
		member.setName("tname");
		member.setAxis("col");
		member.setGroup("tgroup");
		member.setIndex(5);
		member.setOrder(4);
		member.setMatch("tmatch");

		Afield af = new Afield();
		af.setName("tname");
		af.setGroup("tgroup");
		af.setValue("tvalue");

		member.addAfield(af);
		return member;
	}
}
