package test.mdauml.persistence;

import junit.framework.TestCase;
import test.mdauml.persistence.classes.*;

import java.util.ArrayList;
import java.util.List;

import com.nilledom.model.ElementType;
import com.nilledom.model.StepType;
import com.nilledom.persistence.Registerer;
import com.nilledom.persistence.xml.XmlObjectSerializer;


public class SerializerTest extends TestCase {


    public void before() {
        Registerer.clean();
    }


    public void testSerializeString() throws Exception {

        XmlObjectSerializer s = new XmlObjectSerializer("testSerializeString.xml");
        s.writeObject("Hello World!");
        String readed = (String) s.readObject();

        assertEquals("Hello World!", readed);


    }

    public void testSerializeStrings() throws Exception {

        XmlObjectSerializer s = new XmlObjectSerializer("testSerializeStrings.xml");
        s.writeObject("Hello");
        s.writeObject("World");
        s.writeObject("");
        String hello = (String) s.readObject();
        String world = (String) s.readObject();
        String empty = (String) s.readObject();

        assertEquals("Hello", hello);
        assertEquals("World", world);
        assertEquals("", empty);


    }


    public void testSerializePrimitives() throws Exception {
        XmlObjectSerializer s = new XmlObjectSerializer("testSerializePrimitives.xml");

        Primitives original = new Primitives();

        original.setB((byte) 0x1F);
        original.setC('c');
        original.setD(12.235464);
        original.setF(0.0002346F);
        original.setSh((short) 5);
        original.setI(546);
        original.setL(123456789L);
        original.setFa(false);
        original.setTr(true);


        s.writeObject(original);
        Primitives primitives = (Primitives) s.readObject();

        assertEquals(original, primitives);

    }


    public void testArrayFields() throws Exception {
        XmlObjectSerializer s = new XmlObjectSerializer("testArrayFields.xml");
        int[] ia = {1, 2, 3};
        String[] sa = {"a", "b", "c"};
        ArrayClass original = new ArrayClass();
        original.setArrayOfInt(ia);
        original.setArrayOfString(sa);

        s.writeObject(original);

        ArrayClass primitives = (ArrayClass) s.readObject();

        assertEquals(original, primitives);


    }

    public void testStaticFields() throws Exception {
        StaticFields original = new StaticFields();

        original.setNonStaticField("Im not static");
        StaticFields.setStaticInt(9);
        StaticFields.setStaticString("9");
        Primitives primitives = new Primitives();

        primitives.setB((byte) 0x1F);
        primitives.setC('c');
        primitives.setD(12.235464);
        primitives.setF(0.0002346F);
        primitives.setSh((short) 5);
        primitives.setI(546);
        primitives.setL(123456789L);
        primitives.setFa(false);
        primitives.setTr(true);

        StaticFields.setStaticPrimitives(primitives);

        XmlObjectSerializer s = new XmlObjectSerializer("testStaticFields.xml");
        s.writeObject(original);

        StaticFields.setStaticInt(0);
        StaticFields.setStaticString("");
        StaticFields.setStaticPrimitives(new Primitives());

        StaticFields readed = (StaticFields) s.readObject();

        assertEquals(original.getNonStaticField(), readed.getNonStaticField());
        assertEquals(9, StaticFields.getStaticInt());
        assertEquals("9", StaticFields.getStaticString());
        assertEquals(primitives, StaticFields.getStaticPrimitives());


    }


    public void testSingletonClass() throws Exception {

        XmlObjectSerializer s = new XmlObjectSerializer("testSingletonClass.xml");
        SingletonClass.instance.setValue("hola");
        s.writeObject(SingletonClass.instance);

        SingletonClass readed = (SingletonClass) s.readObject();


        assertEquals(readed, SingletonClass.instance);
    }

    public void testNonDefaultConstructorClass() throws Exception {

        XmlObjectSerializer s = new XmlObjectSerializer("testNonDefaultConstructorClass.xml");
        int[] ia = {1, 2};
        String[] sa = {"a", "b"};
        String str = "hola";
        List<String> l = new ArrayList<String>();

        NonDefaultConstructorClass original = new NonDefaultConstructorClass(5, 2, ia, sa, str, l);

        Object[] args = new Object[1];
        original.ellipsis(args);
        s.writeObject(original);

        NonDefaultConstructorClass readed = (NonDefaultConstructorClass) s.readObject();

        assertEquals(readed, original);


    }

    public void testMap() throws Exception {
        XmlObjectSerializer s = new XmlObjectSerializer("testMap.xml");
        MapClass original = new MapClass();
        original.getMap().put("this", 4);
        original.getMap().put("is", 2);
        original.getMap().put("a", 1);
        original.getMap().put("map", 3);
        s.writeObject(original);
        MapClass readed = (MapClass) s.readObject();
        assertEquals(readed, original);
    }

    public void testSet() throws Exception {
        XmlObjectSerializer s = new XmlObjectSerializer("testSet.xml");
        SetClass original = new SetClass();
        original.getSet().add("this");
        original.getSet().add("is");
        original.getSet().add("a");
        original.getSet().add("set");
        s.writeObject(original);
        SetClass readed = (SetClass) s.readObject();
        assertEquals(readed, original);
    }

    public void testList() throws Exception {
        XmlObjectSerializer s = new XmlObjectSerializer("testList.xml");
        ListClass original = new ListClass();
        original.getList().add("this");
        original.getList().add("is");
        original.getList().add("a");
        original.getList().add("list");
        s.writeObject(original);
        ListClass readed = (ListClass) s.readObject();
        assertEquals(readed, original);
    }

    public void testTransientField() throws Exception {
        XmlObjectSerializer s = new XmlObjectSerializer("testTransientField.xml");
        TransientField original = new TransientField();
        int firstValue = original.getDoNotPersist();
        original.setDoNotPersist(456);
        s.writeObject(original);
        TransientField readed = (TransientField) s.readObject();
        assertEquals(readed.getDoNotPersist(), firstValue);
        assertEquals(original.getDoNotPersist(), 456);
    }

    public void testWrappedPrimitives() throws Exception {
        XmlObjectSerializer s = new XmlObjectSerializer("testWrappedPrimitives.xml");
        WrappedPrimitives original = new WrappedPrimitives();
        original.setB((byte) 0x1F);
        original.setC('c');
        original.setD(12.235464);
        original.setF(0.0002346F);
        original.setSh((short) 5);
        original.setI(546);
        original.setL(123456789L);
        original.setFa(false);
        original.setTr(true);
        s.writeObject(original);
        WrappedPrimitives readed = (WrappedPrimitives) s.readObject();
        assertEquals(readed, original);

    }

    public void testInnerClaas() throws Exception {
        XmlObjectSerializer s = new XmlObjectSerializer("testInnerClaas.xml");
        InnerClass original = new InnerClass();
        original.addChild("these");
        original.addChild("are");
        original.addChild("my");
        original.addChild("children");
        original.setStaticInner(new InnerClass.StaticInner());
        original.setInnerWithoutConstructor(
                original.new InnerWithoutConstructor());
        original.initialize();
        s.writeObject(original);
        InnerClass readed = (InnerClass) s.readObject();
        assertEquals(readed, original);

    }

    public void testEnumObject() throws Exception {
        XmlObjectSerializer s = new XmlObjectSerializer("testEnumObject.xml");
        ElementType original = ElementType.ACTOR;
        s.writeObject(original);
        ElementType readed = (ElementType) s.readObject();
        assertEquals(readed, original);

    }

    public void testEnumContainer() throws Exception {
        XmlObjectSerializer s = new XmlObjectSerializer("testEnumContainer.xml");
        EnumContainer original = new EnumContainer(StepType.REGULAR);
        s.writeObject(original);
        EnumContainer readed = (EnumContainer) s.readObject();
        assertEquals(readed.getType(), original.getType());

    }
}