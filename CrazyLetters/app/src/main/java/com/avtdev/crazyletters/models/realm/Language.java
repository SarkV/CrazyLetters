package com.avtdev.crazyletters.models.realm;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Language extends RealmObject {

    public static final class PROPERTIES{
        public static final String LANGUAGE = "language";
        public static final String OCCURENCES = "occurrences";
    }

    @PrimaryKey
    @SerializedName(PROPERTIES.LANGUAGE)
    private String language;

    @SerializedName(PROPERTIES.OCCURENCES)
    private long ocurrences;

    public Language() {
    }

    public Language(String language) {
        this.language = language;
    }

    public Language(String language, long ocurrences) {
        this.language = language;
        this.ocurrences = ocurrences;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public long getOcurrences() {
        return ocurrences;
    }

    public void setOcurrences(long ocurrences) {
        this.ocurrences = ocurrences;
    }
}
