package org.dei.perla.lang;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;

/**
 * @author Guido Rota 22/10/15.
 */
public class Common {

    public static final Attribute INT_ATTRIBUTE =
            Attribute.create("integer", DataType.INTEGER);
    public static final Attribute FLOAT_ATTRIBUTE =
            Attribute.create("float", DataType.FLOAT);

}
