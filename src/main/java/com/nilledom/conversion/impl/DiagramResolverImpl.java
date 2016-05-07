package com.nilledom.conversion.impl;

import java.util.*;

import com.nilledom.conversion.DiagramResolver;
import com.nilledom.conversion.dialog.DiagramResolverDialog;
import com.nilledom.conversion.model.DiagramBuilder;
import com.nilledom.exception.ConversionCanceledException;
import com.nilledom.model.UmlPackage;
import com.nilledom.model.UmlUseCase;
import com.nilledom.ui.AppFrame;
import com.nilledom.util.Msg;
import com.nilledom.util.StringHelper;

public class DiagramResolverImpl implements DiagramResolver {
    @Override
    public Map<String, DiagramBuilder> resolveEntitiesByDiagram(Map<String, List<UmlUseCase>> mainEntityMap , List<UmlPackage> packages) throws ConversionCanceledException {
        Map<String, DiagramBuilder> diagramMap = new HashMap<>();
        UmlPackage rootPackage = UmlPackage.getPrototype();
        rootPackage.setName(Msg.get("conversion.defaultDiagram"));

        List<String> mainEntities = new ArrayList<>(mainEntityMap.keySet());
        Collections.sort(mainEntities);


        for(UmlPackage umlPackage:packages){
            Set<String> mainEntitiesOfPkg =new HashSet<>();
            DiagramBuilder diagramBuilder = new DiagramBuilder();
            diagramBuilder.setName(umlPackage.getName());
            diagramBuilder.setUmlPackage(umlPackage);
            diagramBuilder.setPackageRelated(true);
            for (String mainEntity : mainEntities) {
                for (UmlUseCase useCase : mainEntityMap.get(mainEntity))
                    if (useCase.getPackage() == umlPackage) {
                        mainEntitiesOfPkg.add(mainEntity);
                        break;
                    }
            }
            diagramBuilder.setMainEntities(new ArrayList<>(mainEntitiesOfPkg));
            Collections.sort(diagramBuilder.getMainEntities());
            diagramMap.put(diagramBuilder.getName(),diagramBuilder);
        }

        if(packages.isEmpty()){
            DiagramBuilder diagramBuilder = new DiagramBuilder();
            diagramBuilder.setName(AppFrame.get().getName());
            diagramBuilder.setUmlPackage(null);
            diagramBuilder.setPackageRelated(false);
            diagramBuilder.setMainEntities(mainEntities);
            diagramMap.put(AppFrame.get().getName(),diagramBuilder);
        }

        openDialog(diagramMap,mainEntities,packages);
        return removeEmpty(diagramMap);
    }

    private Map<String, DiagramBuilder> removeEmpty(Map<String, DiagramBuilder> diagramMap) {
        for (String diagram :
                diagramMap.keySet()) {
            if (diagramMap.get(diagram).getMainEntities().isEmpty())
                diagramMap.remove(diagram);
        }
        return diagramMap;
    }

    private void openDialog(Map<String, DiagramBuilder> diagramMap, List<String> mainEntities, List<UmlPackage>packages) throws ConversionCanceledException {
        DiagramResolverDialog dialog = new DiagramResolverDialog(diagramMap,mainEntities,packages);
        dialog.setLocationRelativeTo(AppFrame.get());
        dialog.setVisible(true);
        if(dialog.hasCanceled())
            throw new ConversionCanceledException();
    }


}
