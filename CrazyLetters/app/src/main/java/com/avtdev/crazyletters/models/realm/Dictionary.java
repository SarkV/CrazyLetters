package com.avtdev.crazyletters.models.realm;

import com.avtdev.crazyletters.models.response.DictionaryResponse;
import com.google.gson.annotations.SerializedName;

import java.text.Normalizer;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Dictionary extends RealmObject {

    public static final class PROPERTIES{
        public static final String ID = "id";
        public static final String WORD = "word";
        public static final String WORD_NO_ACENT = "word";
        public static final String LANGUAGE = "language";
    }

    @PrimaryKey
    @SerializedName(PROPERTIES.ID)
    private String id;

    @SerializedName(PROPERTIES.WORD)
    private String word;

    @SerializedName(PROPERTIES.WORD_NO_ACENT)
    private String wordNoAcent;

    @SerializedName(PROPERTIES.LANGUAGE)
    private String language;

    public Dictionary() {
    }

    public Dictionary(DictionaryResponse dictionaryResponse) {
        this.id = dictionaryResponse.getId();
        this.word = dictionaryResponse.getWord();
        if(dictionaryResponse.getWord() != null){
            this.wordNoAcent = Normalizer.normalize(dictionaryResponse.getWord(), Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        }
        this.language = dictionaryResponse.getLanguage();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWordNoAcent() {
        return wordNoAcent;
    }

    public void setWordNoAcent(String wordNoAcent) {
        this.wordNoAcent = wordNoAcent;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
