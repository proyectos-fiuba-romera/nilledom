package com.nilledom.conversion;

import java.util.List;
import java.util.Map;

import com.nilledom.conversion.model.*;
import com.nilledom.model.UmlClass;
import com.nilledom.model.UmlUseCase;

public interface ClassModelBuilder {

    ConversionModel buildConversionModel(Map<String,List<UmlUseCase>> mainEntityMap);

    Map<Class<? extends UmlClass>, List<UmlClass>> buildUmlModel(ConversionModel model);

}
