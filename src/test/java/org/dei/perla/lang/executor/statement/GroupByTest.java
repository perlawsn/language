package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.Field;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 16/03/15.
 */
public class GroupByTest {

    private static final Duration SEC_10 = Duration.ofSeconds(10);
    private static final Duration MIN_10 = Duration.ofMinutes(10);

    private static final List<Field> fields;
    static {
        fields = new ArrayList<>();
        fields.add(new Field("integer"));
        fields.add(new Field("float"));
        fields.add(new Field("string"));
    }

    private static final Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static final Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);
    private static final Attribute stringAtt =
            Attribute.create("string", DataType.STRING);

    private static final List<Attribute> atts;
    static {
        atts = new ArrayList<>();
        atts.add(intAtt);
        atts.add(floatAtt);
        atts.add(stringAtt);
    }

    @Test
    public void creation() {
        GroupBy g;

        g = new GroupBy(SEC_10, 5);
        assertThat(g.getDuration(), equalTo(SEC_10));
        assertThat(g.getCount(), equalTo(5));
        assertThat(g.getFields(), nullValue());

        g = new GroupBy(fields);
        assertThat(g.getDuration(), nullValue());
        assertThat(g.getCount(), equalTo(-1));
        assertThat(g.getFields().size(), equalTo(fields.size()));
        for (int i = 0; i < fields.size(); i++) {
            assertThat(g.getFields().get(i), equalTo(fields.get(i)));
        }

        g = new GroupBy(MIN_10, 12, fields);
        assertThat(g.getDuration(), equalTo(MIN_10));
        assertThat(g.getCount(), equalTo(12));
        assertThat(g.getFields().size(), equalTo(fields.size()));
        for (int i = 0; i < fields.size(); i++) {
            assertThat(g.getFields().get(i), equalTo(fields.get(i)));
        }
    }

    @Test
    public void rebuild() {
        GroupBy g = new GroupBy(SEC_10, 54, fields);
        for (Expression e : g.getFields()) {
            assertFalse(e.isComplete());
        }

        GroupBy ng = g.rebuild(atts);
        assertThat(ng.getDuration(), equalTo(g.getDuration()));
        assertThat(ng.getCount(), equalTo(g.getCount()));
        assertThat(g.getFields().size(), equalTo(ng.getFields().size()));
        for (int i = 0; i < ng.getFields().size(); i++) {
            assertTrue(ng.getFields().get(i).isComplete());
        }
    }

}