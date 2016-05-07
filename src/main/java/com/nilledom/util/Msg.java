package com.nilledom.util;


public class Msg {

    private static ApplicationResources applicationResources = ApplicationResources.getInstance();

    public static String get(String code){
        return applicationResources.getString(code);
    }
}
