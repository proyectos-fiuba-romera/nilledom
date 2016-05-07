package com.nilledom.conversion;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nilledom.conversion.model.DiagramBuilder;
import com.nilledom.exception.ConversionCanceledException;
import com.nilledom.model.UmlModel;
import com.nilledom.model.UmlPackage;
import com.nilledom.model.UmlUseCase;

public interface DiagramResolver {

    Map<String,DiagramBuilder> resolveEntitiesByDiagram(Map<String, List<UmlUseCase>> mainEntityMap , List<UmlPackage> packages) throws ConversionCanceledException;
}
