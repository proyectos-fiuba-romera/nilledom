package com.nilledom.util;


public class StringHelper {

    public static String toLowerCamelCase( String string){
        String trimmed = string.trim();
        String[] words = trimmed.split("[\\s\\-_]");
        String result="";
        words[0]=words[0].toLowerCase();
        result+= words[0];
        for(int i=1 ; i< words.length; i++){
            String word = words[i];
            String firstChar = word.substring(0,1).toUpperCase();
            String append = word.length() > 1 ? word.substring(1) : "";
            word= firstChar + append;
            result+=word;
        }
        return result;
    }

    public static String toUpperCamelCase(String name) {
        String lower = toLowerCamelCase(name);
        if(lower.length() == 1)
            return lower.toUpperCase();
        return lower.substring(0,1).toUpperCase() + lower.substring(1);
    }
}

