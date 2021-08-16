package com.github.zflxw.papertemplate.utils.localization;

public enum Language {
    ENGLISH("English", "en_us"),
    GERMAN("Deutsch", "de_de");

    private final String languageName;
    private final String languageKey;

    Language(String languageName, String key) {
        this.languageName = languageName;
        this.languageKey = key;
    }

    public String getLanguageName() {
        return this.languageName;
    }

    public String getLanguageKey() {
        return this.languageKey;
    }
}