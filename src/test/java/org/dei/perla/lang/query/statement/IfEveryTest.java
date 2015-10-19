package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.query.expression.*;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Guido Rota 23/03/15.
 */
public class IfEveryTest {

    private static final Object[][] samples;
    static {
        samples = new Object[4][];
        for (int i = 0; i < samples.length; i++) {
            samples[i] = new Object[1];
            samples[i][0] = i;
        }
    }

    @Test
    public void testSingle() {
        Duration d;
        Errors err = new Errors();
        Expression cInt = Constant.create(5, DataType.INTEGER);
        Expression cFloat = Constant.create(3.4f, DataType.FLOAT);

        IfEvery e = new IfEvery(Constant.TRUE, cInt, ChronoUnit.DAYS);
        assertThat(e.getCondition(), equalTo(Constant.TRUE));
        assertThat(e.getUnit(), equalTo(ChronoUnit.DAYS));
        Object v = e.getValue().run(null, null);
        assertThat(v, equalTo(5));
    }

    @Test
    public void testMultiple() {
        Expression ref = new AttributeReference("test", DataType.INTEGER, 0);
        Expression c0 = Constant.create(0, DataType.INTEGER);
        Expression c1 = Constant.create(1, DataType.INTEGER);
        Expression c2 = Constant.create(2, DataType.INTEGER);

        List<IfEvery> ifevery = new ArrayList<>();

        Expression cond = new Comparison(ComparisonOperation.EQ, ref, c0);
        IfEvery ife = new IfEvery(cond, ref, ChronoUnit.SECONDS);
        ifevery.add(ife);

        cond = new Comparison(ComparisonOperation.EQ, ref, c1);
        ife = new IfEvery(cond, ref, ChronoUnit.SECONDS);
        ifevery.add(ife);

        cond = new Comparison(ComparisonOperation.EQ, ref, c2);
        ife = new IfEvery(cond, ref, ChronoUnit.SECONDS);
        ifevery.add(ife);


        Expression c = Constant.create(500, DataType.INTEGER);
        ife = new IfEvery(Constant.TRUE, c, ChronoUnit.SECONDS);
        ifevery.add(ife);

        Duration d = IfEvery.evaluate(ifevery, samples[0]);
        assertThat(d, equalTo(Duration.ofSeconds(0)));
        d = IfEvery.evaluate(ifevery, samples[1]);
        assertThat(d, equalTo(Duration.ofSeconds(1)));
        d = IfEvery.evaluate(ifevery, samples[2]);
        assertThat(d, equalTo(Duration.ofSeconds(2)));
        d = IfEvery.evaluate(ifevery, samples[3]);
        assertThat(d, equalTo(Duration.ofSeconds(500)));
    }

}
