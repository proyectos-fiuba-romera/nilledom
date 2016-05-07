package test.mdauml.persistence.classes;

/**
 * Created by Fernando on 22/08/2015.
 */
public class StaticFields {

    private static int staticInt;
    private static String staticString;
    private static Primitives staticPrimitives;
    private static final String constantString = "CONSTANT";
    private static final int constantInteger = 123;

    private String nonStaticField;



    public static int getStaticInt() {
        return staticInt;
    }

    public static void setStaticInt(int staticInt) {
        StaticFields.staticInt = staticInt;
    }

    public static String getStaticString() {
        return staticString;
    }

    public static void setStaticString(String staticString) {
        StaticFields.staticString = staticString;
    }

    public static Primitives getStaticPrimitives() {
        return staticPrimitives;
    }

    public static void setStaticPrimitives(Primitives staticPrimitives) {
        StaticFields.staticPrimitives = staticPrimitives;
    }

    public String getNonStaticField() {
        return nonStaticField;
    }

    public void setNonStaticField(String nonStaticField) {
        this.nonStaticField = nonStaticField;
    }



}
