package in.m.picks.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Test;

public class MemberTest {

	@Test
	public void testHashCode() {
		assertEquals(-551225437, getMember().hashCode());
	}

	@Test
	public void testEqualsObject() {
		Member m1 = getMember();
		Member m2 = getMember();

		assertNotSame(m1, m2);
		assertTrue(m1.equals(m2) && m2.equals(m1));
		assertTrue(m1.hashCode() == m2.hashCode());
	}

	@Test
	public void testGetAxis() {
		Member m1 = getMember();
		Axis a1 = m1.getAxis("col");
		assertNotNull(a1);
		Axis a2 = m1.getAxis("COL");
		assertNotNull(a2);
		assertSame(a1, a2);

		assertTrue(a1.getName().equals("col"));
		assertTrue(a1.getIndex() == 5);
		assertTrue(a1.getOrder() == 4);
		assertTrue(a1.getMatch().equals("tmatch"));
		assertTrue(a1.getValue().equals("tvalue"));

		assertNull(m1.getAxis("xyz"));
	}

	@Test
	public void testAddAxis() {
		Axis axis = new Axis();
		axis.setName("col");

		Member member = new Member();

		assertEquals(0, member.getAxes().size());
		member.addAxis(axis);
		assertEquals(1, member.getAxes().size());
	}

	@Test
	public void testMember() {
		Member member = new Member();

		assertNotNull(member.getAxes());
		assertEquals(0, member.getAxes().size());
		assertTrue(member.getAxes() instanceof HashSet);
	}

	@Test
	public void testGetValue() {
		Member m1 = getMember();
		assertEquals("tvalue", m1.getValue("col"));
		assertEquals("tvalue", m1.getValue("COL"));
	}

	private Member getMember() {
		Axis axis = new Axis();
		axis.setName("col");
		axis.setIndex(5);
		axis.setMatch("tmatch");
		axis.setOrder(4);
		axis.setValue("tvalue");

		Afield afield = new Afield("tname", "tvalue");
		axis.addAfield(afield);

		Member member = new Member();
		member.setName("tname");
		member.setGroup("tgroup");
		member.setId(2);
		member.addAxis(axis);

		return member;
	}

}
