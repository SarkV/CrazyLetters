package com.avtdev.crazyletters.models.realm;

import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Utils;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Game extends RealmObject {

    public static final class PROPERTIES{
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String VELOCITY = "velocity";
        public static final String LETTERS_TYPE = "lettersType";
        public static final String LANGUAGES = "languages";
        public static final String TIME = "time";
        public static final String CUSTOM = "custom";
        public static final String LAST_USED = "lastUsed";
    }

    @PrimaryKey
    @SerializedName(PROPERTIES.ID)
    private long id;

    @SerializedName(PROPERTIES.NAME)
    private String name;

    @SerializedName(PROPERTIES.VELOCITY)
    private String velocity;

    @SerializedName(PROPERTIES.LETTERS_TYPE)
    private String lettersType;

    @SerializedName(PROPERTIES.LANGUAGES)
    private String languages;

    @SerializedName(PROPERTIES.TIME)
    private int time;

    @SerializedName(PROPERTIES.LAST_USED)
    private Date lastUsed;

    @SerializedName(PROPERTIES.CUSTOM)
    private boolean custom;

    public Game() {
    }

    public Game(String name, Integer[] velocity, GameConstants.LettersType[] lettersType, String[] languages, int time, boolean custom) {
        this.id = UUID.randomUUID().getMostSignificantBits();
        this.name = name;
        setVelocity(velocity);
        setLettersType(lettersType);
        setLanguages(languages);
        this.time = time;
        this.custom = custom;
        this.lastUsed = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer[] getVelocity() {
        return Utils.stringToList(this.velocity, Integer.class);
    }

    public void setVelocity(Integer[] velocity) {
        this.velocity = Utils.listToString(Arrays.asList(velocity));
    }

    public GameConstants.LettersType[] getLettersType() {
        return Utils.stringToList(this.lettersType, GameConstants.LettersType.class);
    }

    public void setLettersType(GameConstants.LettersType[] lettersType) {
        this.lettersType = Utils.listToString(Arrays.asList(lettersType));
    }

    public String[] getLanguages() {
        return Utils.stringToList(this.languages, String.class);
    }

    public void setLanguages(String[] language) {
        this.languages = Utils.listToString(Arrays.asList(language));
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }


}
