package com.avtdev.crazyletters.models.realm;

import com.avtdev.crazyletters.models.response.DictionaryResponse;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Dictionary extends RealmObject {

    public static final class PROPERTIES{
        public static final String ID = "id";
        public static final String WORD = "word";
        public static final String LANGUAGE = "language";
        public static final String UPDATE_DATE = "updateDate";
    }

    @PrimaryKey
    @SerializedName(PROPERTIES.ID)
    private String id;

    @SerializedName(PROPERTIES.WORD)
    private String word;

    @SerializedName(PROPERTIES.LANGUAGE)
    private String language;

    @SerializedName(PROPERTIES.UPDATE_DATE)
    private Date updateDate;

    public Dictionary() {
    }

    public Dictionary(DictionaryResponse dictionaryResponse) {
        this.id = dictionaryResponse.getId();
        this.word = dictionaryResponse.getWord();
        this.language = dictionaryResponse.getLanguage();
        this.updateDate = dictionaryResponse.getUpdateDate();
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
