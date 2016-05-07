package com.nilledom.persistence.serializer;

import java.io.File;

import com.nilledom.ui.model.Project;

public interface Serializer {

    public Object read() throws Exception;

    public void write(Project project) throws Exception;


}
