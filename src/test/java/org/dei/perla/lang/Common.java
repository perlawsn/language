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

    public static final Attribute TEMP_INT =
            Attribute.create("temperature", DataType.INTEGER);
    public static final Attribute TEMP_FLOAT =
            Attribute.create("temperature", DataType.FLOAT);
    public static final Attribute PRESS_INT =
            Attribute.create("pressure", DataType.INTEGER);
    public static final Attribute PRESS_FLOAT =
        Attribute.create("pressure", DataType.FLOAT);
    public static final Attribute HUM_INT =
            Attribute.create("humidity", DataType.INTEGER);
    public static final Attribute HUM_FLOAT =
            Attribute.create("humidity", DataType.FLOAT);

}
