package org.dei.perla.lang.query.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.query.expression.Field;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Guido Rota 16/03/15.
 */
public class GroupByTest {

    private static final Duration SEC_10 = Duration.ofSeconds(10);
    private static final Duration MIN_10 = Duration.ofMinutes(10);

    private static final List<Field> fields;
    static {
        fields = new ArrayList<>();
        fields.add(new Field("integer", DataType.INTEGER, 0));
        fields.add(new Field("float", DataType.FLOAT, 1));
        fields.add(new Field("string", DataType.STRING, 2));
    }

    private static final Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static final Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);
    private static final Attribute stringAtt =
            Attribute.create("string", DataType.STRING);

    @Test
    public void creation() {
        GroupBy g;

        g = new GroupBy(SEC_10, 5);
        assertThat(g.getDuration(), equalTo(SEC_10));
        assertThat(g.getCount(), equalTo(5));

        g = new GroupBy(fields);
        for (int i = 0; i < fields.size(); i++) {
            assertThat(g.getGroups().get(i), equalTo(fields.get(i)));
        }

        g = new GroupBy(MIN_10, 12, fields);
        assertThat(g.getDuration(), equalTo(MIN_10));
        assertThat(g.getCount(), equalTo(12));
        for (int i = 0; i < fields.size(); i++) {
            assertThat(g.getGroups().get(i), equalTo(fields.get(i)));
        }
    }

}
