package com.nilledom.persistence.serializer;

import java.awt.geom.Dimension2D;
import java.beans.XMLDecoder;
import java.io.*;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import com.nilledom.draw.*;
import com.nilledom.exception.ObjectSerializerException;
import com.nilledom.exception.ProjectSerializerException;
import com.nilledom.model.*;
import com.nilledom.persistence.*;
import com.nilledom.persistence.xml.XmlHelper;
import com.nilledom.ui.model.Project;
import com.nilledom.umldraw.shared.GeneralDiagram;
import com.nilledom.umldraw.shared.UmlDiagramElement;
import com.nilledom.util.Msg;

public abstract class ProjectSerializer implements Serializer {

    protected ObjectSerializer projectObjectSerializer ;
    protected ObjectSerializer modelObjectSerializer ;
    protected ObjectSerializer viewObjectSerializer ;
    protected String projectPath;
    protected String modelPath;
    protected String viewPath;

    public ProjectSerializer(String path) throws ObjectSerializerException {
        this.projectPath = getProjectPath(path);;
        this.modelPath = getModelPath(path);
        this.viewPath = getViewPath(path);
        this.projectObjectSerializer = buildObjectSerializer(projectPath);
        this.modelObjectSerializer = buildObjectSerializer(modelPath);
        this.viewObjectSerializer = buildObjectSerializer(viewPath);

    }

    protected abstract ObjectSerializer buildObjectSerializer(String path) throws ObjectSerializerException;



    @Override public Object read() throws ProjectSerializerException {
        try {

            Project project = new Project(new UmlModelImpl());


            Registerer.clean();
            ModelSerializer modelXmlSerializer = new ModelSerializer(modelObjectSerializer);
            ModelPersistence modelPersistence = (ModelPersistence) modelXmlSerializer.read();


            ViewSerializer viewXmlSerializer = new ViewSerializer(viewObjectSerializer);
            ViewPersistence viewPersistence = (ViewPersistence) viewXmlSerializer.read();

            project.setOpenDiagrams(viewPersistence.getOpenedDiagrams());

            for(UmlDiagram diagram : viewPersistence.getUmlDiagrams()) {
                if(diagram instanceof GeneralDiagram) {
                    Dimension2D size = ((GeneralDiagram) diagram).getSize();
                    size = new DoubleDimension(size.getWidth(),size.getHeight());
                    GeneralDiagram generalDiagram = (GeneralDiagram) diagram;
                    generalDiagram.initialize(project.getModel());
                    generalDiagram.setSize(size);
                    for(Connection connection: generalDiagram.getConnections()){
                        if(connection instanceof NodeChangeListener){
                            NodeChangeListener conn = (NodeChangeListener)connection;
                            Node node1 = connection.getNode1();
                            Node node2 = connection.getNode2();
                            node1.addNodeChangeListener(conn);
                            node2.addNodeChangeListener(conn);
                        }


                    }
                }
                project.getModel().addDiagram(diagram);


                for(UmlDiagramElement umlDiagramElement :diagram.getElements()) {
                    UmlModelElement model = umlDiagramElement.getModelElement();
                    if(model==null)
                        continue;
                    if(model instanceof PackageableUmlModelElement && umlDiagramElement instanceof PackageListener)
                        ((PackageableUmlModelElement) model).addPackageListener((PackageListener) umlDiagramElement);
                    project.getModel().addElement(model,diagram);
                }

            }

        return project;

        } catch (Exception e) {
            String msg = Msg.get("error.loadproject.message");
            throw new ProjectSerializerException(msg + " \"" + projectPath + "\".", e);
        }
    }


    private boolean validateFormat(Element root) {
        if (XmlHelper.querySingle(root, "./" + Constants.MODEL_TAG) != null
            && XmlHelper.querySingle(root, "./" + Constants.VIEW_TAG) != null)
            return true;

        return false;
    }

    @Override public void write(Project project) throws ProjectSerializerException {
        try {
            ProjectPersistence projectPersistence = new ProjectPersistence();
            projectPersistence.setModelPath(modelPath);
            projectPersistence.setViewPath(viewPath);

            projectObjectSerializer.writeObject(projectPersistence);

            Registerer.clean();
            ModelSerializer modelSerializer = new ModelSerializer(modelObjectSerializer);
            modelSerializer.write(project);

            ViewSerializer viewXmlSerializer = new ViewSerializer(viewObjectSerializer);
            viewXmlSerializer.write(project);

        } catch (Exception e) {
            String msg = Msg.get("error.saveproject.message");
            throw new ProjectSerializerException(msg + " \"" + projectPath + "\".", e);
        }


    }

    public String getViewPath(String path) {
        return getPathWithoutExtention(path) +"."+ Constants.VIEW_EXTENTION;
    }

    public String getModelPath(String path) {
        return getPathWithoutExtention(path) +"."+ Constants.MODEL_EXTENTION;
    }

    public String getProjectPath(String path) {
        return getPathWithoutExtention(path) +"."+ Constants.PROJECT_EXTENTION;
    }

    public String getPathWithoutExtention(String path) {
        Pattern projectPattern = Pattern.compile("(.*)\\." + Constants.PROJECT_EXTENTION);
        Pattern modelPattern = Pattern.compile("(.*)\\." + Constants.MODEL_EXTENTION);
        Pattern viewPattern = Pattern.compile("(.*)\\." + Constants.VIEW_EXTENTION);
        String pathWithoutExtention = path;
        if (projectPattern.matcher(path).matches())
            pathWithoutExtention = projectPattern.matcher(path).replaceFirst("$1");
        if (modelPattern.matcher(path).matches())
            pathWithoutExtention = modelPattern.matcher(path).replaceFirst("$1");
        if (viewPattern.matcher(path).matches())
            pathWithoutExtention = viewPattern.matcher(path).replaceFirst("$1");
        return pathWithoutExtention;
    }



}
