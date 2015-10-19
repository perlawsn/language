package org.dei.perla.lang.parser.ast;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 19/10/15.
 */
public class SelectionStatementASTTest {

    @Test
    public void testGroupByAST() {
        List<String> ids = Arrays.asList(new String[] {
                "temperature",
                "pressure"
        });
        GroupByAST group = new GroupByAST(ids);
        List<String> fields = group.getFields();
        assertThat(fields.size(), equalTo(ids.size()));
        assertTrue(fields.containsAll(ids));
    }

}
