package com.avtdev.crazyletters.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FullDictionaryResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("language")
    private String language;

    @SerializedName("createdAt")
    private List<DictionaryResponse> dictionaryList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DictionaryResponse> getWords() {
        return dictionaryList;
    }

    public void setDictionaryList(List<DictionaryResponse> dictionaryList) {
        this.dictionaryList = dictionaryList;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
