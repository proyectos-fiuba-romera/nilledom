package com.nilledom.conversion;


import java.util.List;
import java.util.Map;

import com.nilledom.conversion.impl.ConverterImpl;
import com.nilledom.conversion.model.ConversionModel;
import com.nilledom.model.UmlClass;

public interface ClassDiagramBuilder {

    void buildClassDiagram(Map<Class<? extends UmlClass>, List<UmlClass>> classModel, String diagramName, ConversionModel conversionModel, ConverterImpl.ConversionTask conversionTask);
}
