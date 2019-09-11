package com.avtdev.crazyletters.models.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmField;

public class LetterFrequency extends RealmObject {

    public static final class PROPERTIES{
        public static final String ID = "id";
        public static final String LANGUAGE = "language";
        public static final String LETTER = "letter";
        public static final String FREQUENCY = "frequency";
    }

    @PrimaryKey
    @RealmField(PROPERTIES.ID)
    private String id;

    @RealmField(PROPERTIES.LANGUAGE)
    private String language;

    @RealmField(PROPERTIES.LETTER)
    private String letter;

    @RealmField(PROPERTIES.FREQUENCY)
    private long frequency;

    public LetterFrequency() {
    }

    public LetterFrequency(String language, String letter, long frequency) {
        this.id = language + "_" + letter;
        this.language = language;
        this.letter = letter;
        this.frequency = frequency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }
}
