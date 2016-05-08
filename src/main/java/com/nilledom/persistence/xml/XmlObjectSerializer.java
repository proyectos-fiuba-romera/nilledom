package com.nilledom.persistence.xml;





import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nilledom.exception.ObjectSerializerException;
import com.nilledom.exception.RegisterExistentIdException;
import com.nilledom.model.StepType;
import com.nilledom.persistence.Registerer;
import com.nilledom.persistence.serializer.ObjectSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class XmlObjectSerializer implements ObjectSerializer{

    private static final String INT_TAG = "int";
    private static final String DOUBLE_TAG = "double";
    private static final String BOOLEAN_TAG = "boolean";
    private static final String CHAR_TAG = "char";
    private static final String BYTE_TAG = "byte";
    private static final String SHORT_TAG = "short";
    private static final String LONG_TAG = "long";
    private static final String FLOAT_TAG = "float";
    private static final String ENUM_TAG = "enum";
    private static final String STRING_TAG = "string";
    private static final String OBJECT_TAG = "object";
    private static final String CLASS_ATT = "class";
    private static final String ID_ATT = "id";
    private static final String ID_REF_ATT = "id_ref";
    private static final String PARENT_TAG = "parent";
    private static final String FIELD_TAG = "field";
    private static final String NAME_ATT = "name";
    private static final String MODIFIERS_ATT = "modifiers";
    private static final String TYPE_ATT = "type";
    private static final String ARRAY_TAG = "array";
    private static final String LENGTH_ATT = "length";
    private static final String ROOT_TAG = "XmlObjectSerializer";
    private static final String NULL_TAG = "null";
    private static final String SET_TAG = "set";
    private static final String MAP_TAG = "map";
    private static final String KEY_TAG = "key";
    private static final String VALUE_TAG = "value";
    private static final String LIST_TAG = "list";
    private static final String NUMBER_TAG = "number";
    private static final String CLASS_TAG = "class";
    private static final String CHARACTER_TAG = "character";
    private static final String BOOLEAN_OBJ_TAG = "boolean-obj";
    private static final String CONTAINING_TAG = "containingObject";

    private File file;
    private Document doc;
    private Element root;
    private int nextObject =0;
    private boolean reading=false;
    private boolean writing=false;


    public XmlObjectSerializer(String filePath) throws ObjectSerializerException {
        try {
            file = new File(filePath);

        } catch (Exception e) {
            throw new ObjectSerializerException(
                "Error creating Serializer for file: " + filePath + " .\n", e);
        }
    }

    public Object readObject( ) throws Exception {
        if(!reading){
            if(writing)
                Registerer.clean();
            writing=false;
            reading=true;
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = docBuilder.parse(file);
            root =  doc.getDocumentElement();
        }

        NodeList nodes = root.getChildNodes();
        Object response;
        while( nextObject < nodes.getLength()){
            Node node = nodes.item(nextObject);
            if (node instanceof Element){
                response = fromXml((Element)node);
                nextObject++;
                return response;
            }
            nextObject++;

        }
        return null;
    }
    public void writeObject(Object obj) throws Exception {
        if(!writing){
            if(reading)
                Registerer.clean();
            reading=false;
            writing=true;
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = docBuilder.newDocument();
            root = doc.createElement(ROOT_TAG);
            doc.appendChild(root);
        }
        Element element = toXml(obj);
        root.appendChild(element);
        saveXml(doc);

    }



    private Object fromXml(Element element)  {
        try {
            String tagName = element.getTagName();

            if (tagName.equals(NULL_TAG))
                return null;
            if (tagName.equals(STRING_TAG))
                return element.getTextContent();




            Class<? extends Object> clazz = Class.forName(
                XmlHelper.getAttribute(element, CLASS_ATT));

            if (Enum.class.isAssignableFrom(clazz)) {
                Class<? extends Enum> enumClazz =(Class<? extends Enum>) clazz;
                Enum<? extends Object> anEnumValue = Enum.valueOf(enumClazz, element.getTextContent());
                return anEnumValue;
            }
            if (Double.class.isAssignableFrom(clazz))
                return Double.valueOf(element.getTextContent());
            if (Float.class.isAssignableFrom(clazz))
                return Float.valueOf(element.getTextContent());
            if (Integer.class.isAssignableFrom(clazz))
                return Integer.valueOf(element.getTextContent());
            if (Long.class.isAssignableFrom(clazz))
                return Long.valueOf(element.getTextContent());
            if (Byte.class.isAssignableFrom(clazz))
                return Byte.valueOf(element.getTextContent());
            if (BigInteger.class.isAssignableFrom(clazz))
                return new BigInteger(element.getTextContent());
            if (BigDecimal.class.isAssignableFrom(clazz))
                return new BigDecimal(element.getTextContent());
            if (Short.class.isAssignableFrom(clazz))
                return Short.valueOf(element.getTextContent());
            if (AtomicInteger.class.isAssignableFrom(clazz))
                return new AtomicInteger(Integer.valueOf(element.getTextContent()));
            if (AtomicLong.class.isAssignableFrom(clazz))
                return new AtomicLong(Long.valueOf(element.getTextContent()));
            if (Boolean.class.isAssignableFrom(clazz))
                return Boolean.valueOf(element.getTextContent());
            if (Character.class.isAssignableFrom(clazz))
                return Character.valueOf(element.getTextContent().charAt(0));



            String id = element.getAttribute(ID_REF_ATT);
            if (!id.isEmpty())
                return Registerer.getObject(Long.valueOf(id));

            Object enclosing=null;
            if(hasAnEnclosingObject(clazz))
                enclosing = enclosingFromXml(element);

                id = element.getAttribute(ID_ATT);
            Object object = createInstance(clazz,enclosing);
            Registerer.register(Long.valueOf(id),object);

            if (Set.class.isAssignableFrom(clazz))
                return setFromXml(element,clazz);
            if (Map.class.isAssignableFrom(clazz))
                return mapFromXml(element,clazz);
            if (List.class.isAssignableFrom(clazz))
                return listFromXml(element,clazz);

            return objectFromXml(element,object);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private Object enclosingFromXml(Element element) {
        Element containing = XmlHelper.getChild(element,CONTAINING_TAG);
        Element objectElement = XmlHelper.getFirstChild(containing);
        Object containingObject = fromXml(objectElement);
        return containingObject;
    }

    private Object createInstance(Class<? extends Object> clazz,Object enclosing) {
        try {

        Constructor[] ctors = clazz.getDeclaredConstructors();
        int minArg=9999;
        Constructor ctor = null;
        for (int i = 0; i < ctors.length; i++) {
            if (minArg > ctors[i].getParameterTypes().length ){
                ctor = ctors[i];
                minArg=ctor.getParameterTypes().length;
            }
        }
            if(ctor ==null)
                return null;
            ctor.setAccessible(true);
            Object [] initargs= new Object[minArg];
            Class[] types = ctor.getParameterTypes();
            int i=0;
            boolean enclosingSet=false;
            for(Class type : types){
                if (type == Integer.TYPE) {
                    initargs[i++]=0;
                } else if (type == Double.TYPE) {
                    initargs[i++]=0.0;
                } else if (type == Boolean.TYPE) {
                    initargs[i++]=false;
                } else if (type == Character.TYPE) {
                    initargs[i++]='\0';
                } else if (type == Byte.TYPE) {
                    initargs[i++]=0x00;
                } else if (type == Short.TYPE) {
                    initargs[i++]=(short)0;
                } else if (type == Long.TYPE) {
                    initargs[i++]=0L;
                } else if (type == Float.TYPE) {
                    initargs[i++]=0.0f;
                } else if (type.isArray()) {
                    initargs[i++] = Array.newInstance(type.getComponentType(), 0);
                }else if(type.isEnum()){
                    initargs[i++] = null;
                }else{
                    if(!enclosingSet && enclosing!=null && type.isAssignableFrom(enclosing.getClass()) )
                        initargs[i++]=enclosing;
                    else
                        initargs[i++]=createInstance(type,null);
                }



            }
            if(initargs.length==0)
                return ctor.newInstance();
            return ctor.newInstance(initargs);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object listFromXml(Element element, Class<?> clazz) {
        try {
            List list   = (List) clazz.newInstance();

            NodeList nodelist = element.getChildNodes();

            for(int i = 0 ; i < nodelist.getLength() ; i++ ){
                if( ! (nodelist.item(i) instanceof Element))
                    continue;
                list.add(fromXml((Element) nodelist.item(i)));

            }

            return list;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object mapFromXml(Element element, Class<?> clazz) {
        try {
            Map map   = (Map) clazz.newInstance();

            NodeList nodelist = element.getChildNodes();

            Object key=null;
            Object value=null;

            for(int i = 0 ; i < nodelist.getLength() ; i++ ){
                if( ! (nodelist.item(i) instanceof Element))
                    continue;

                Element childElement = (Element) nodelist.item(i);

                if(childElement.getTagName().equals(KEY_TAG)) {
                    Element keyChild =  XmlHelper.getFirstChild(childElement);
                    key = fromXml(keyChild);
                }else if (childElement.getTagName().equals(VALUE_TAG)) {
                    if(key==null)
                        continue;
                    Element valueChild =XmlHelper.getFirstChild(childElement);
                    value = fromXml(valueChild);
                    map.put(key,value);
                    key=null;
                }

            }

            return map;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object setFromXml(Element element, Class<?> clazz) {
        try {
            Set set   = (Set) clazz.newInstance();

            NodeList nodelist = element.getChildNodes();

            for(int i = 0 ; i < nodelist.getLength() ; i++ ){
                if( ! (nodelist.item(i) instanceof Element))
                    continue;
                set.add(fromXml((Element) nodelist.item(i)));

            }

            return set;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object objectFromXml(Element element,Object obj) throws ClassNotFoundException {
        Element classElement = XmlHelper.getChild(element,CLASS_TAG);
        String className = XmlHelper.getAttribute(classElement,NAME_ATT);
        Class<?> clazz = Class.forName(className);
        if(!Registerer.isRegistered(clazz)){
            Long id = Long.valueOf(classElement.getAttribute(ID_ATT));
            try {
                Registerer.register(id,clazz);
            } catch (RegisterExistentIdException e) {
                e.printStackTrace();
            }
            NodeList classChildren = classElement.getChildNodes();
            for(int i =0 ; i < classChildren.getLength() ; i++){
                Node classChild = classChildren.item(i);
                if(! (classChild instanceof  Element))
                    continue;
                Element classChildElement = (Element) classChild;
                if(classChildElement.getTagName().equals(FIELD_TAG)){
                    setFieldFromXml(clazz,null,classChildElement);
                }

            }
        }
        if (clazz.getSuperclass() != Object.class && clazz.getSuperclass() != null) {
            Element parentElement = XmlHelper.getChild(element,PARENT_TAG );
            objectFromXml(parentElement,obj);
        }

        NodeList elementChildren = element.getChildNodes();
        for(int i =0 ; i < elementChildren.getLength() ; i++){
            Node elementChild = elementChildren.item(i);
            if(! (elementChild instanceof  Element))
                continue;
            Element objectChildElement = (Element) elementChild;
            if(objectChildElement.getTagName().equals(FIELD_TAG)){
                setFieldFromXml(obj,clazz,objectChildElement);
            }

        }



        return obj;
    }

    private void setFieldFromXml(Object obj,Class clazz, Element fieldElement) {
        try {
            Field field;
            if (obj instanceof Class) {
                clazz = (Class) obj;
                obj = null;
            }
            String name = XmlHelper.getAttribute(fieldElement,NAME_ATT);
            field = clazz.getDeclaredField(name);

            field.setAccessible(true);
            Class<?> type = field.getType();
            if (type.isPrimitive()) {
                setPrimitiveFromXml(obj, field,XmlHelper.getFirstChild(fieldElement));
            } else if (type.isEnum()) {
                setEnumFromXml(obj,field,XmlHelper.getFirstChild(fieldElement));

            } else if (type.isArray()) {
                setArrayFromXml(obj,field,XmlHelper.getFirstChild(fieldElement));


            } else if (Object.class.isAssignableFrom(type)) {
                setObjectFromXml(obj,field,XmlHelper.getFirstChild(fieldElement));
            }


        } catch (Exception e) {

        }
    }

    private void setObjectFromXml(Object obj, Field field, Element firstChild) {
        try {
            Object toBeSet = fromXml(firstChild);
            field.set(obj, toBeSet);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setArrayFromXml(Object obj, Field field, Element fieldElement) {
        try {
            int length = Integer.valueOf( fieldElement.getAttribute(LENGTH_ATT));

            Class<?> componentType = field.getType().getComponentType();
            Object array = Array.newInstance(componentType, length);
            NodeList nodes = fieldElement.getChildNodes();
            for (int i = 0, j=0; i < nodes.getLength(); i++) {

                Node node = nodes.item(i);
                if(! (node instanceof Element))
                    continue;
                Element component = (Element) node;
                if (componentType == Integer.TYPE) {
                    Array.set(array,j++,Integer.valueOf(component.getTextContent()));
                } else if (componentType == Double.TYPE) {
                    Array.set(array,j++,Double.valueOf(component.getTextContent()));
                } else if (componentType == Boolean.TYPE) {
                    Array.set(array,j++,Boolean.valueOf(component.getTextContent()));
                } else if (componentType == Character.TYPE) {
                    Array.set(array,j++,Character.valueOf(component.getTextContent().charAt(0)));
                } else if (componentType == Byte.TYPE) {
                    Array.set(array,j++,Byte.valueOf(component.getTextContent()));
                } else if (componentType == Short.TYPE) {
                    Array.set(array,j++,Short.valueOf(component.getTextContent()));
                } else if (componentType == Long.TYPE) {
                    Array.set(array,j++,Long.valueOf(component.getTextContent()));
                } else if (componentType == Float.TYPE) {
                    Array.set(array,j++,Float.valueOf(component.getTextContent()));
                }else
                    Array.set(array,j++,fromXml(component));

            }
            field.set(obj,array);


        }catch (Exception e  ){

        }
    }

    private void setEnumFromXml(Object obj, Field field, Element fieldElement) {

        try {
        if(obj instanceof Class)
            obj=null;

            Class<? extends Enum> clazz =
                (Class<? extends Enum>) Class.forName(field.getType().getName());
            Enum<?> anEnum = Enum.valueOf(clazz, fieldElement.getTextContent());
           ;
            field.set(obj, anEnum);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void setPrimitiveFromXml(Object obj, Field field, Element classChildElement) {
        if(obj instanceof Class)
            obj=null;
        try {
            Class<?> type = field.getType();
            if (type == Integer.TYPE) {
                field.set(obj,Integer.valueOf(classChildElement.getTextContent()));
            } else if (type == Double.TYPE) {
                field.set(obj,Double.valueOf(classChildElement.getTextContent()));
            } else if (type == Boolean.TYPE) {
                field.set(obj,Boolean.valueOf(classChildElement.getTextContent()));
            } else if (type == Character.TYPE) {
                field.set(obj,Character.valueOf(classChildElement.getTextContent().charAt(0)));
            } else if (type == Byte.TYPE) {
                field.set(obj,Byte.valueOf(classChildElement.getTextContent()));
            } else if (type == Short.TYPE) {
                field.set(obj,Short.valueOf(classChildElement.getTextContent()));
            } else if (type == Long.TYPE) {
                field.set(obj,Long.valueOf(classChildElement.getTextContent()));
            } else if (type == Float.TYPE) {
                field.set(obj,Float.valueOf(classChildElement.getTextContent()));
            }

        } catch (IllegalAccessException e) {


        }
    }


    private void saveXml(Document doc) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(doc);
        FileOutputStream fos = new FileOutputStream(file,false);
        StreamResult result = new StreamResult(fos);
        transformer.transform(source, result);
    }




    private Element toXml(Object obj) throws Exception {
        if (obj == null)
            return XmlHelper.getNewElement(root, NULL_TAG);

        Class<? extends Object> clazz = obj.getClass();

        String tag = getObjectTag(obj);
        Element element = XmlHelper.getNewElement(root, tag);
        XmlHelper.addAtribute(root, element, CLASS_ATT, clazz.getName());

        //these are not registered because they are inmutables
        if (String.class.isAssignableFrom(clazz) || Number.class.isAssignableFrom(clazz)
            || Boolean.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz)
            || Enum.class.isAssignableFrom(clazz)    ) {
            element.setTextContent(obj.toString());
            return element;
        }


        if(hasAnEnclosingObject(clazz)) {
            int level = getInnerLevel(clazz);
            Field enclosingField = clazz.getDeclaredField("this$" + level);
            enclosingField.setAccessible(true);
            Element enclosingObjectElement = toXml(enclosingField.get(obj));
            Element containingElement=XmlHelper.getNewElement(root,CONTAINING_TAG);
            containingElement.appendChild(enclosingObjectElement);
            element.appendChild(containingElement);
        }


        if (Registerer.isRegistered(obj)) {
            XmlHelper.addAtribute(root, element, ID_REF_ATT, Registerer.getId(obj).toString());
            return element;
        }
        Registerer.register(obj);
        XmlHelper.addAtribute(root, element, ID_ATT, Registerer.getId(obj).toString());



        if (Collection.class.isAssignableFrom(clazz))
            return collectionToXml(obj, element);
        if (Map.class.isAssignableFrom(clazz))
            return mapToXml((Map) obj, element);

        return toXml(obj, obj.getClass(), element);
    }

    private int getInnerLevel(Class<? extends Object> clazz) {
        int i = -1;
        Class currentClass = clazz;
        while(currentClass.getEnclosingClass()!=null){
            i++;
            currentClass=currentClass.getEnclosingClass();
        }
        return i;
    }

    private String getObjectTag(Object obj) {
        if (Set.class.isAssignableFrom(obj.getClass()))
            return SET_TAG;
        if (Map.class.isAssignableFrom(obj.getClass()))
            return MAP_TAG;
        if (List.class.isAssignableFrom(obj.getClass()))
            return LIST_TAG;
        if (Number.class.isAssignableFrom(obj.getClass()))
            return NUMBER_TAG;
        if (String.class.isAssignableFrom(obj.getClass()))
            return STRING_TAG;
        if (Character.class.isAssignableFrom(obj.getClass()))
            return CHARACTER_TAG;
        if (Boolean.class.isAssignableFrom(obj.getClass()))
            return BOOLEAN_OBJ_TAG;
        if(Enum.class.isAssignableFrom(obj.getClass()))
            return ENUM_TAG;
        return OBJECT_TAG;
    }


    private Element collectionToXml(Object obj, Element element) throws Exception {
        if (Set.class.isAssignableFrom(obj.getClass()))
            return setToXml((Set) obj, element);
        if (List.class.isAssignableFrom(obj.getClass()))
            return listToXml((List) obj, element);

        return null;
    }

    private Element listToXml(List list, Element element) throws Exception {
        for (Object listEntry : list) {
            Element objectElement = toXml(listEntry);
            element.appendChild(objectElement);
        }
        return element;
    }

    private Element mapToXml(Map map, Element element) throws Exception {
        ;
        Set<Map.Entry> entries = map.entrySet();
        for (Map.Entry mapEntry : entries) {
            Element keyElement = XmlHelper.addChildElement(root, element, KEY_TAG);
            Element key = toXml(mapEntry.getKey());
            keyElement.appendChild(key);
            Element valueElement = XmlHelper.addChildElement(root, element, VALUE_TAG);
            Element value = toXml(mapEntry.getValue());
            valueElement.appendChild(value);
        }
        return element;
    }

    private Element setToXml(Set set, Element element) throws Exception {
        for (Object setEntry : set) {
            Element objectElement = toXml(setEntry);
            element.appendChild(objectElement);
        }
        return element;
    }


    private Element toXml(Object obj, Class<?> clazz, Element element)
        throws Exception {

        Element classElement = XmlHelper.getNewElement(root, CLASS_TAG);
        XmlHelper.addAtribute(root, classElement, NAME_ATT, clazz.getName());
        element.appendChild(classElement);

        Field[] fields = clazz.getDeclaredFields();

        if (Registerer.isRegistered(clazz)) {
            XmlHelper
                .addAtribute(root, classElement, ID_REF_ATT, Registerer.getId(clazz).toString());
        } else {
            Registerer.register(clazz);
            XmlHelper.addAtribute(root, classElement, ID_ATT, Registerer.getId(clazz).toString());

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                int modifiers = field.getModifiers();
                if (Modifier.isTransient(modifiers) || !Modifier.isStatic(modifiers)||Modifier.isFinal(modifiers))
                    continue;
                addFieldToObject(clazz, field, classElement);

            }
        }
        if (clazz.getSuperclass() != Object.class && clazz.getSuperclass() != null) {
            Element parent = XmlHelper.getNewElement(root, PARENT_TAG);
            XmlHelper.addAtribute(root, parent, CLASS_ATT, clazz.getSuperclass().getName());
            parent = toXml(obj, clazz.getSuperclass(), parent);
            element.appendChild(parent);
        }


        for (int i = 0; i < fields.length; i++) {
            try {
                Field field = fields[i];
                int modifiers = field.getModifiers();
                if (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers))
                    continue;

                addFieldToObject(obj, field, element);

            } catch (Exception e) {
                continue;
            }

        }
        return element;
    }

    private void addFieldToObject(Object obj, Field field, Element element) {
        try {
            field.setAccessible(true);
            int modifiers = field.getModifiers();
            Element fieldElement = XmlHelper.addChildElement(root, element, FIELD_TAG);
            XmlHelper.addAtribute(root, fieldElement, NAME_ATT, field.getName());
            XmlHelper.addAtribute(root, fieldElement, MODIFIERS_ATT, Modifier.toString(modifiers));
            XmlHelper.addAtribute(root, fieldElement, TYPE_ATT, field.getType().getName());

            Class<?> type = field.getType();
            if (type.isPrimitive()) {

                Element primitive = primitiveToXml(type, field.get(obj));
                fieldElement.appendChild(primitive);

            } else if (type.isEnum()) {
                Element enumElement = XmlHelper.addChildElement(root, fieldElement, ENUM_TAG);
                enumElement.setTextContent(((Enum)field.get(obj)).name());

            } else if (type.isArray()) {
                Element arrayElement = XmlHelper.addChildElement(root, fieldElement, ARRAY_TAG);
                Object array = field.get(obj);
                int length = Array.getLength(array);
                XmlHelper.addAtribute(root, arrayElement, LENGTH_ATT, String.valueOf(length));
                XmlHelper
                    .addAtribute(root, arrayElement, TYPE_ATT, type.getComponentType().getName());

                for (int j = 0; j < length; j++) {
                    Class<?> componentType = type.getComponentType();
                    Object componentObject = Array.get(array, j);
                    Element componentElement;
                    if (componentType.isPrimitive())
                        componentElement = primitiveToXml(componentType, componentObject);
                    else if (componentType == String.class) {
                        componentElement = XmlHelper.getNewElement(root, STRING_TAG);
                        componentElement.setTextContent(componentObject.toString());
                    } else {
                        componentElement = toXml(componentObject);
                    }
                    arrayElement.appendChild(componentElement);

                }


            } else if (Object.class.isAssignableFrom(type)) {
                Element objectElement = toXml(field.get(obj));
                fieldElement.appendChild(objectElement);
            }

        } catch (Exception e) {

        }
    }

    private Element primitiveToXml(Class<?> type, Object obj) {
        Element primitive = null;

        if (type == Integer.TYPE) {
            primitive = XmlHelper.getNewElement(root, INT_TAG);
        } else if (type == Double.TYPE) {
            primitive = XmlHelper.getNewElement(root, DOUBLE_TAG);
        } else if (type == Boolean.TYPE) {
            primitive = XmlHelper.getNewElement(root, BOOLEAN_TAG);
        } else if (type == Character.TYPE) {
            primitive = XmlHelper.getNewElement(root, CHAR_TAG);
        } else if (type == Byte.TYPE) {
            primitive = XmlHelper.getNewElement(root, BYTE_TAG);
        } else if (type == Short.TYPE) {
            primitive = XmlHelper.getNewElement(root, SHORT_TAG);
        } else if (type == Long.TYPE) {
            primitive = XmlHelper.getNewElement(root, LONG_TAG);
        } else if (type == Float.TYPE) {
            primitive = XmlHelper.getNewElement(root, FLOAT_TAG);
        }
        primitive.setTextContent(obj.toString());

        return primitive;

    }

    public static Field findFieldWithName(Class<?> type, String name) {
        List<Field> all = getAllFields(type);
        for (Field field : all) {
            if (field.getName().equals(name))
                return field;
        }
        return null;
    }

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }


    private boolean hasAnEnclosingObject(Class clazz){
        if(clazz.isMemberClass()){
           if(Modifier.isStatic(clazz.getModifiers()))
                return false;
            return true;
        }
        return clazz.isAnonymousClass();
    }

    private List<Field> getStaticFields(Class clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<Field> staticFields = new ArrayList<>();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                staticFields.add(field);
            }
        }
        return staticFields;
    }



}
