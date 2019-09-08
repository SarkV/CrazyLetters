package com.avtdev.crazyletters.models.realm;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmField;

public class Language extends RealmObject {

    public static final class PROPERTIES{
        public static final String LANGUAGE = "language";
        public static final String OCCURRENCES = "occurrences";
    }

    @PrimaryKey
    @RealmField(PROPERTIES.LANGUAGE)
    private String language;

    @RealmField(PROPERTIES.OCCURRENCES)
    private long occurrences;

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
        this.occurrences = ocurrences;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public long getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(long occurrences) {
        this.occurrences = occurrences;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
