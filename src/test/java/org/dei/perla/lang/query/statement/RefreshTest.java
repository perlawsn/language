package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 27/03/15.
 */
public class RefreshTest {

    @Test
    public void neverRefresh() {
        Refresh r = Refresh.NEVER;
        assertThat(r.getType(), equalTo(RefreshType.NEVER));
    }

    @Test
    public void createTimedRefresh() {
        Duration d = Duration.ofSeconds(56);
        Refresh r = new Refresh(d);
        assertThat(r.getType(), equalTo(RefreshType.TIME));
        assertThat(r.getDuration(), equalTo(d));
    }

    @Test
    public void createEventRefresh() {
        Attribute lb = Attribute.create("low_battery", DataType.ANY);
        Attribute al = Attribute.create("alert", DataType.ANY);
        List<Attribute> atts = Arrays.asList(new Attribute[]{
                lb,
                al,
                Attribute.TIMESTAMP
        });

        Refresh r = new Refresh(atts);
        List<Attribute> bound = r.getEvents();
        assertThat(bound.size(), equalTo(3));
        assertTrue(bound.contains(lb));
        assertTrue(bound.contains(al));
    }

}
