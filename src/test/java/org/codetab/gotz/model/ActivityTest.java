package org.codetab.gotz.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.codetab.gotz.model.Activity.Type;
import org.codetab.gotz.util.Util;
import org.junit.Test;

public class ActivityTest {

    @Test
    public void testActivityTypeString() {
        Activity act = new Activity(Type.FATAL, "x");
        assertThat(act.getType()).isEqualTo(Type.FATAL);
        assertThat(act.getMessage()).isEqualTo("x");
        assertThat(act.getThrowable()).isNull();
    }

    @Test
    public void testActivityTypeStringThrowable() {
        Throwable t = new Throwable("exception");
        Activity act = new Activity(Type.FATAL, "x", t);
        assertThat(act.getType()).isEqualTo(Type.FATAL);
        assertThat(act.getMessage()).isEqualTo("x");
        assertThat(act.getThrowable()).isSameAs(t);
    }

    @Test
    public void testType() {
        // for test coverage of enum, we need to run both values and valueOf
        assertThat(Type.values()[0]).isEqualTo(Type.FAIL);
        assertThat(Type.values()[1]).isEqualTo(Type.CONFIG);
        assertThat(Type.values()[2]).isEqualTo(Type.SUMMARY);
        assertThat(Type.values()[3]).isEqualTo(Type.FATAL);

        assertThat(Type.valueOf("FAIL")).isEqualTo(Type.FAIL);
        assertThat(Type.valueOf("CONFIG")).isEqualTo(Type.CONFIG);
        assertThat(Type.valueOf("SUMMARY")).isEqualTo(Type.SUMMARY);
        assertThat(Type.valueOf("FATAL")).isEqualTo(Type.FATAL);
    }

    @Test
    public void testActivityToString() {
        Activity act = new Activity(Type.FATAL, "test");
        String expected =
                getExprectedString(Type.FATAL, act.getLabel(), "test", null);
        assertThat(act.toString()).isEqualTo(expected);
    }

    @Test
    public void testActivityToStringWithThrowable() {
        Throwable t = new Throwable("exception");
        Activity act = new Activity(Type.FATAL, "test", t);
        String expected =
                getExprectedString(Type.FATAL, act.getLabel(), "test", t);
        assertThat(act.toString()).isEqualTo(expected);
    }

    private String getExprectedString(final Type type, final String label,
            final String message, final Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append("Activity [type=");
        sb.append(type);
        sb.append(" label=");
        sb.append(label);
        sb.append(" message=");
        sb.append(message);
        sb.append("]");
        if (throwable != null) {
            sb.append(Util.LINE);
            sb.append("          throwable=");
            sb.append(throwable);
        }
        return sb.toString();
    }

}
