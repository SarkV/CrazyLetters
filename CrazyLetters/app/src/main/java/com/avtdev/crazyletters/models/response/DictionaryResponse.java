package com.avtdev.crazyletters.models.response;

import com.google.gson.annotations.SerializedName;

public class DictionaryResponse{

    @SerializedName("id")
    private String id;

    @SerializedName("word")
    private String word;

    @SerializedName("language")
    private String language;

    @SerializedName("createdAt")
    private Long createdAt;

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

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
