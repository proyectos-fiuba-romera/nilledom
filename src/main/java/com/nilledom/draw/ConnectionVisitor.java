package com.nilledom.draw;

import com.nilledom.exception.AddConnectionException;
import com.nilledom.model.RelationType;
import com.nilledom.umldraw.clazz.Dependency;
import com.nilledom.umldraw.shared.*;
import com.nilledom.umldraw.usecase.Extend;
import com.nilledom.umldraw.usecase.Include;

public interface ConnectionVisitor {


    void addConcreteConnection(Connection connection);
    void addConcreteConnection(Nest connection);
    void addConcreteConnection(Inheritance connection);
    void addConcreteConnection(Association connection);
    void addConcreteConnection(Extend connection);
    void addConcreteConnection(Include connection);
    void addConcreteConnection(NoteConnection connection);
    void addConcreteConnection(Dependency connection);



    void removeConcreteConnection(Connection connection);
    void removeConcreteConnection(Nest connection);
    void removeConcreteConnection(Inheritance connection);
    void removeConcreteConnection(Association connection);
    void removeConcreteConnection(Extend connection);
    void removeConcreteConnection(Include connection);
    void removeConcreteConnection(NoteConnection connection);
    void removeConcreteConnection(Dependency connection);


    boolean acceptsConnectionAsSource(RelationType relationType);
    void validateConnectionAsTarget(RelationType relationType,UmlNode node)throws AddConnectionException;
}
