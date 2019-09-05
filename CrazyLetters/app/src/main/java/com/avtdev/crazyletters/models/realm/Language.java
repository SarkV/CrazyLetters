package com.avtdev.crazyletters.models.realm;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmField;

public class Language extends RealmObject {

    public static final class PROPERTIES{
        public static final String LANGUAGE = "language";
        public static final String OCCURENCES = "occurrences";
    }

    @PrimaryKey
    @RealmField(PROPERTIES.LANGUAGE)
    private String language;

    @RealmField(PROPERTIES.OCCURENCES)
    private long ocurrences;

    @Ignore
    private boolean selected;

    public Language() {
        selected = false;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
