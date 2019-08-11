package com.avtdev.crazyletters.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GameModeResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("velocity")
    private List<Integer> velocity;

    @SerializedName("lettersType")
    private List<String> lettersType;

    @SerializedName("language")
    private List<String> languages;

    @SerializedName("accent")
    private Boolean accent;

    @SerializedName("time")
    private Integer time;

    @SerializedName("createdAt")
    private Long createdAt;

    @SerializedName("removed")
    private Boolean removed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getVelocity() {
        if(velocity == null){
            return new ArrayList<>();
        }
        return velocity;
    }

    public void setVelocity(List<Integer> velocity) {
        this.velocity = velocity;
    }

    public List<String> getLettersType() {
        if(lettersType == null){
            return new ArrayList<>();
        }
        return lettersType;
    }

    public void setLettersType(List<String> lettersType) {
        this.lettersType = lettersType;
    }

    public List<String> getLanguages() {
        if(languages == null){
            return new ArrayList<>();
        }
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public Boolean getAccent() {
        return accent;
    }

    public void setAccent(Boolean accent) {
        this.accent = accent;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean isRemoved() {
        return removed;
    }

    public void setRemoved(Boolean removed) {
        this.removed = removed;
    }
}
