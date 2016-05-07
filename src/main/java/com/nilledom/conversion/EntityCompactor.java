package com.nilledom.conversion;


import com.nilledom.exception.CompactorException;
import com.nilledom.exception.ConversionCanceledException;
import com.nilledom.model.UmlModel;

public interface EntityCompactor {

    UmlModel compact(final UmlModel model) throws CompactorException, ConversionCanceledException;
}
