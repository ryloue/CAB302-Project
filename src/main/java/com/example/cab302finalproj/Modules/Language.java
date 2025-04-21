package com.example.cab302finalproj.Modules;

public class Language {
    private String code;
    private String name;

    public String getCode() { return code; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name; // This is what will be shown in the ComboBox
    }
}