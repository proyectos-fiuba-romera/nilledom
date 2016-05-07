package com.nilledom.conversion.impl;


import javax.swing.*;

import com.nilledom.conversion.Validator;
import com.nilledom.conversion.dialog.MainEntityDialog;
import com.nilledom.exception.ConversionCanceledException;
import com.nilledom.exception.ValidateException;
import com.nilledom.model.UmlModel;
import com.nilledom.model.UmlUseCase;
import com.nilledom.ui.AppFrame;
import com.nilledom.util.Msg;

import java.awt.*;
import java.util.Set;

public class ValidatorImpl implements Validator{
    @Override
    public void validate(UmlModel model) throws ValidateException, ConversionCanceledException {
        Set<UmlUseCase> useCases = (Set<UmlUseCase>) model.getAll(UmlUseCase.class);
        validate(useCases);

    }

    private void validate(Set<UmlUseCase> useCases) throws ValidateException, ConversionCanceledException {

        for(UmlUseCase useCase : useCases){
            if(useCase.getMainActors().isEmpty() && !useCase.isExtending()){
                boolean isIncluded =false;
                for(UmlUseCase including : useCases)
                    if (including.getIncluded().contains(useCase)) {
                        isIncluded = true;
                        break;
                    }
                if(!isIncluded)
                    throw new ValidateException(Msg.get("error.validator.useCaseNoActor").replaceAll("@USECASE", useCase.getName()));
            }
            if(useCase.getMainEntity()==null || useCase.getMainEntity().isEmpty())
                openMainEntityDialog(useCase);
        }

    }

    private void openMainEntityDialog(UmlUseCase useCase) throws ConversionCanceledException {
        MainEntityDialog dialog = new MainEntityDialog(AppFrame.get(),useCase);
        dialog.setLocationRelativeTo(AppFrame.get());
        dialog.setVisible(true);
        if(dialog.hasCanceled())
            throw new ConversionCanceledException();
    }


}
