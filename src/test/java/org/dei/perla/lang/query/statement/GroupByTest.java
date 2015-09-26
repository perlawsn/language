package org.dei.perla.lang.query.statement;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 16/03/15.
 */
public class GroupByTest {

    @Test
    public void testGroupBy() {
        List<String> atts = Arrays.asList(new String[] {
                "integer",
                "float",
                "string"
        });

        GroupBy gb = new GroupBy(atts);

        List<String> fields = gb.getFields();
        assertThat(fields.size(), equalTo(atts.size()));
        assertTrue(fields.containsAll(atts));
    }

    @Test(expected = IllegalStateException.class)
    public void testNone() {
        GroupBy none = GroupBy.NONE;
        none.getFields();
    }

}
