package com.nilledom.conversion;


import com.nilledom.exception.ConversionCanceledException;
import com.nilledom.exception.ValidateException;
import com.nilledom.model.UmlModel;

public interface Validator {

    void validate(UmlModel model) throws ValidateException, ConversionCanceledException;
}
