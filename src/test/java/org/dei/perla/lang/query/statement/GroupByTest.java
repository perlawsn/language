package org.dei.perla.lang.query.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.query.expression.Attribute;
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

    private static final List<Attribute> atts;
    static {
        atts = new ArrayList<>();
        atts.add(new Attribute("integer", DataType.INTEGER, 0));
        atts.add(new Attribute("float", DataType.FLOAT, 1));
        atts.add(new Attribute("string", DataType.STRING, 2));
    }

    private static final org.dei.perla.core.sample.Attribute intAtt =
            org.dei.perla.core.sample.Attribute.create("integer", DataType.INTEGER);
    private static final org.dei.perla.core.sample.Attribute floatAtt =
            org.dei.perla.core.sample.Attribute.create("float", DataType.FLOAT);
    private static final org.dei.perla.core.sample.Attribute stringAtt =
            org.dei.perla.core.sample.Attribute.create("string", DataType.STRING);

    @Test
    public void creation() {
        GroupBy g;

        g = new GroupBy(SEC_10, 5);
        assertThat(g.getDuration(), equalTo(SEC_10));
        assertThat(g.getCount(), equalTo(5));

        g = new GroupBy(atts);
        for (int i = 0; i < atts.size(); i++) {
            assertThat(g.getGroups().get(i), equalTo(atts.get(i)));
        }

        g = new GroupBy(MIN_10, 12, atts);
        assertThat(g.getDuration(), equalTo(MIN_10));
        assertThat(g.getCount(), equalTo(12));
        for (int i = 0; i < atts.size(); i++) {
            assertThat(g.getGroups().get(i), equalTo(atts.get(i)));
        }
    }

}
