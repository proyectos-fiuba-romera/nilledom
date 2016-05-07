package com.nilledom.conversion.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.nilledom.conversion.EntityCompactor;
import com.nilledom.conversion.dialog.EntityCompactorDialog;
import com.nilledom.conversion.dialog.MainEntityDialog;
import com.nilledom.exception.CompactorException;
import com.nilledom.exception.ConversionCanceledException;
import com.nilledom.model.UmlModel;
import com.nilledom.model.UmlUseCase;
import com.nilledom.ui.AppFrame;

public class EntityCompactorImpl implements EntityCompactor {


    @Override
    public UmlModel compact( UmlModel model) throws CompactorException, ConversionCanceledException {
        Set<UmlUseCase> useCases = (Set<UmlUseCase>) model.getAll(UmlUseCase.class);
        Map<String,String> entities = new HashMap<>();
        for(UmlUseCase useCase: useCases){
            for(String entity : useCase.getAllEntities())
                entities.put(entity,entity);
        }
        openCompactorDialog(entities);
        updateModel(useCases,entities);
        return model;
    }

    private void updateModel(Set<UmlUseCase> useCases, Map<String, String> entities) {
        for (UmlUseCase usecase:  useCases) {
            for(String entity : entities.keySet())
            usecase.replaceEntity(entity,entities.get(entity));
        }
    }

    private void openCompactorDialog(Map<String, String> entities) throws ConversionCanceledException {
        EntityCompactorDialog dialog = new EntityCompactorDialog(AppFrame.get(),entities);
        dialog.setLocationRelativeTo(AppFrame.get());
        dialog.setVisible(true);
        if(dialog.hasCanceled())
            throw new ConversionCanceledException();

    }
}
