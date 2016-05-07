package com.nilledom.conversion;

import com.nilledom.exception.ConversionCanceledException;
import com.nilledom.exception.ConversionException;
import com.nilledom.ui.model.Project;

public interface Converter {

    void convert(Project project) throws ConversionException, ConversionCanceledException;

}
