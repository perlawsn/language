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
    public static final Attribute STRING_ATTRIBUTE =
            Attribute.create("string", DataType.STRING);
    public static final Attribute BOOL_ATTRIBUTE =
            Attribute.create("boolean", DataType.BOOLEAN);

}
