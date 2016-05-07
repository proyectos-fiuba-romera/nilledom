package com.nilledom.conversion.impl;

import javax.swing.*;

import com.nilledom.conversion.*;
import com.nilledom.conversion.model.*;
import com.nilledom.exception.CompactorException;
import com.nilledom.exception.ConversionCanceledException;
import com.nilledom.exception.ConversionException;
import com.nilledom.exception.ValidateException;
import com.nilledom.model.*;
import com.nilledom.ui.AppFrame;
import com.nilledom.ui.model.Project;
import com.nilledom.util.Msg;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

public class ConverterImpl implements Converter, PropertyChangeListener {

    private Validator validator = new ValidatorImpl();
    private EntityCompactor compactor = new EntityCompactorImpl();
    private DiagramResolver resolver = new DiagramResolverImpl();
    private ClassDiagramBuilder diagramBuilder = new ClassDiagramBuilderImpl();
    private ClassModelBuilder classModelBuilder = new ClassModelBuilderImpl();

    private ProgressMonitor progressMonitor;
    private ConversionTask task;
    private JTextArea taskOutput;
    private Map<String, DiagramBuilder>  diagramMap;
    private Map<String, List<UmlUseCase>> mainEntityMap;
    private Map<Class<? extends UmlClass>, List<UmlClass>> umlModel;
    private Thread taskThread;

    @Override
    public void convert(Project project) throws ConversionException, ConversionCanceledException {
        try {
            taskOutput = new JTextArea(5, 20);
            taskOutput.setMargin(new Insets(5,5,5,5));
            taskOutput.setEditable(false);

            UmlModel model = project.getModel();
            UmlModel compactedModel;
            try {
                validator.validate(model);
            } catch (ValidateException e) {
                throw new ConversionException(Msg.get("error.conversion.validation") + e.getMessage(), e);
            }
            try {
                compactedModel = compactor.compact(model);
            } catch (CompactorException e) {
                throw new ConversionException(Msg.get("error.conversion.compactor") + e.getMessage(), e);
            }


            Set<UmlUseCase> useCases = (Set<UmlUseCase>) compactedModel.getAll(UmlUseCase.class);
             mainEntityMap = buildMainEntityMap(useCases);

            ConversionModel conversionModel = classModelBuilder.buildConversionModel(mainEntityMap);
            umlModel = classModelBuilder.buildUmlModel(conversionModel);

            Set<UmlPackage> packages = (Set<UmlPackage>) compactedModel.getAll(UmlPackage.class);
            List<UmlPackage> packagesList = new ArrayList<>(packages);
            Collections.sort(packagesList);
            diagramMap = resolver.resolveEntitiesByDiagram(mainEntityMap,packagesList);

            task = new ConversionTask();
            task.addPropertyChangeListener(this);
            taskThread = new Thread(task);
            taskThread.start();



        }catch(RuntimeException e){
            if (e instanceof  ConversionCanceledException)
                throw e;
            throw new ConversionException(Msg.get("error.conversion"+ e.getMessage()),e);
        }
    }



    private Map<String,List<UmlUseCase>> buildMainEntityMap(Set<UmlUseCase> useCases) {
        Map<String,List<UmlUseCase>> mainEntityMap = new HashMap<>();
        for(UmlUseCase useCase : useCases){
            String mainEntity = useCase.getMainEntity();
            if( !mainEntityMap.containsKey(mainEntity))
                mainEntityMap.put(mainEntity,new ArrayList<UmlUseCase>());
            mainEntityMap.get(mainEntity).add(useCase);
        }
        return mainEntityMap;
    }

    private Set<UmlUseCase> getUseCases(Set<UmlModelElement> elements) {
        Set<UmlUseCase> useCases = new HashSet<>();
        for(UmlModelElement element: elements){
            if(element instanceof UmlUseCase){
                useCases.add((UmlUseCase) element);
            }
        }
        return  useCases;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(progressMonitor==null)
            return;
        if ("progress" == evt.getPropertyName() ) {
            int progress = (Integer) evt.getNewValue();
                progressMonitor.setProgress(progress);
            String message =
                    String.format(Msg.get("conversion.loading.completed")+" %d%%.\n", progress);
                progressMonitor.setNote(message);
            taskOutput.append(message);

            if (progressMonitor.isCanceled()) {
                task.cancel(true);
                try {

                    taskThread.join();
                    progressMonitor.close();

                    AppFrame.get().getAppState().getAppCommandDispatcher().openModelFromFile(AppFrame.get().getAppState().getCurrentFile());


                    progressMonitor.close();
                    progressMonitor=null;
                    AppFrame.get().repaint();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

        }

    }

    public class ConversionTask extends SwingWorker<Void, Void> {

        private int diagrams;
        private double progress;

        @Override
        public Void doInBackground() {
            progress = 0;
            progressMonitor = new ProgressMonitor(AppFrame.get(),
                    Msg.get("converting.loading.message"),
                    "", 0, 100);

            progressMonitor.setMillisToPopup(0);
            progressMonitor.setMillisToDecideToPopup(0);

            setProgress(0);

            diagrams = diagramMap.keySet().size();
            for (String diagram : diagramMap.keySet()) {
                if(isCancelled())
                    break;
                ConversionModel conversionDiagram = diagramMap.get(diagram).build(mainEntityMap);
                if(isCancelled())
                    break;
                progress+= 10.0/diagrams;
                setProgress((int) progress);
                diagramBuilder.buildClassDiagram(umlModel, diagram, conversionDiagram,this);
                if(isCancelled())
                    break;

            }

            progressMonitor.close();


            return null;
        }


        public void step1() {
            progress+= 10.0/diagrams;
            setProgress((int)progress);

        }
        public void step2() {
            progress+= 10.0/diagrams;
            setProgress((int)progress);

        }
        public void step3(int size) {
            progress+= 70.0/(diagrams*size);
            setProgress((int)progress);

        }

        @Override
        protected void done() {
            progressMonitor.close();


            super.done();
            if(!isCancelled()) {
                AppFrame.get().repaint();
            }
        }
    }
}
