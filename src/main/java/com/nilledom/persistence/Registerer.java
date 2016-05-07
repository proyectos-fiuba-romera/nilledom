package com.nilledom.persistence;

import java.util.HashMap;
import java.util.Map;

import com.nilledom.exception.RegisterExistentIdException;

public class Registerer {

    private static HashMap<Integer, Long> idsByHashCode = new HashMap<>();
    private static HashMap<Long, Object> objects = new HashMap<>();
    private static HashMap<Class, Long> classesIds = new HashMap<>();
    private static HashMap<Long, Class> classes = new HashMap<>();
    private static Long nextId = 0L;

    public static Long register(Object object) {
        if (object instanceof Class)
            return registerClass((Class) object);
        Long id = getId(object);
        if (id != null)
            return id;
        idsByHashCode.put(System.identityHashCode(object), nextId);
        objects.put(nextId, object);
        nextId++;
        return nextId - 1;
    }

    private static Long registerClass(Class clazz) {
        if (isRegistered(clazz))
            return getId(clazz);
        classesIds.put(clazz, nextId);
        classes.put(nextId, clazz);
        nextId++;
        return nextId - 1;
    }

    public static boolean isRegistered(Object object) {
        return getId(object) != null;

    }

    public static boolean isRegistered(Long id) {
        return objects.get(id) != null;
    }

    public static Long getId(Object object) {
        if (object instanceof Class)
            return getIdOfClass((Class) object);

        Long sameHashId = idsByHashCode.get(System.identityHashCode(object));
        if (sameHashId != null) {
            Object sameHashObject = objects.get(sameHashId);
            if (sameHashObject == object)
                return sameHashId;
            for (Map.Entry<Long, Object> entry : objects.entrySet()) {
                Long anId = entry.getKey();
                Object anObject = entry.getValue();
                if (anObject == object) {
                    return anId;
                }
            }
        }
        return null;
    }

    private static Long getIdOfClass(Class clazz) {
        return classesIds.get(clazz);
    }

    public static Object getObject(Long id) {
        Object object = objects.get(id);
        if (object != null)
            return object;


        return classes.get(id);
    }

    public static Long register(Long id, Object object) throws RegisterExistentIdException {
        if (object instanceof Class)
            return registerClass(id, (Class) object);
        if (objects.containsKey(id))
            throw new RegisterExistentIdException(
                "The id=" + id + " has already been registered for another object");
        idsByHashCode.put(System.identityHashCode(object), id);
        objects.put(id, object);
        if (nextId < id)
            nextId = id;
        nextId++;
        return id;
    }

    private static Long registerClass(Long id, Class clazz) throws RegisterExistentIdException {
        if (classes.containsKey(id))
            throw new RegisterExistentIdException(
                "The id=" + id + " has already been registered for another class");
        classesIds.put(clazz, id);
        classes.put(id, clazz);
        if (nextId < id)
            nextId = id;
        nextId++;
        return id;
    }

    public static void clean() {
        idsByHashCode = new HashMap<>();
        objects = new HashMap<>();
        classesIds = new HashMap<>();
        classes = new HashMap<>();
        nextId = 0L;
    }

}
