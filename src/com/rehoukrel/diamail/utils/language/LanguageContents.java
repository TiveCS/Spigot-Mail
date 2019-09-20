package com.rehoukrel.diamail.utils.language;

public enum LanguageContents{

    PLACEHOLDER("placeholders"), MESSAGE("messages"), HOVER("hover");

    private String path;

    LanguageContents(String path){
        this.path = path;
    }

    public String getPath(){
        return this.path;
    }
}
