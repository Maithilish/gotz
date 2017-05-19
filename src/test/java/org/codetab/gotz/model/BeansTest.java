package org.codetab.gotz.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;

public class BeansTest {

    // beans is just a holder and not compared with each other.
    // tests for hashCode, equals are for coverage
    @Test
    public void testHashCode() {
        Beans t1 = new Beans();
        Beans t2 = new Beans();

        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
    }

    @Test
    public void testEqualsObject() {
        Beans t1 = new Beans();
        Beans t2 = new Beans();

        assertThat(t1).isEqualTo(t2);
        assertThat(t2).isEqualTo(t1);
    }

    @Test
    public void testToString() {
        Beans t1 = new Beans();
        String expected = ToStringBuilder.reflectionToString(t1,
                ToStringStyle.MULTI_LINE_STYLE);

        assertThat(t1.toString()).isEqualTo(expected);
    }

    @Test
    public void testGetLocator() {
        Beans t1 = new Beans();
        List<Bean> list = t1.getBean();

        assertThat(list).isNotNull();
        // for coverage when not null
        assertThat(t1.getBean()).isSameAs(list);
    }
}