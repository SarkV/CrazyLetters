package com.avtdev.crazyletters.models.realm;

import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Utils;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.internal.fields.FieldDescriptor;

public class Game extends RealmObject {

    public static final class PROPERTIES{
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String VELOCITY = "velocity";
        public static final String LETTERS_TYPE = "lettersType";
        public static final String LANGUAGES = "languages";
        public static final String ACCENT = "accent";
        public static final String TIME = "time";
        public static final String DEFAULT_GAME = "defaultGame";
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

    @SerializedName(PROPERTIES.ACCENT)
    private boolean accent;

    @SerializedName(PROPERTIES.TIME)
    private int time;

    @SerializedName(PROPERTIES.LAST_USED)
    private Date lastUsed;

    @SerializedName(PROPERTIES.DEFAULT_GAME)
    private boolean defaultGame;

    public Game() {
    }

    public Game(String name, Integer[] velocity, Object[] lettersType, String[] languages, boolean accent, int time, boolean custom) {
        this.id = UUID.randomUUID().getMostSignificantBits();
        this.name = name;
        setVelocity(velocity);
        setLettersType(lettersType);
        setLanguages(languages);
        this.accent = accent;
        this.time = time;
        this.defaultGame = custom;
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

    public String getVelocityString() {
        return this.velocity;
    }

    public void setVelocity(Integer[] velocity) {
        this.velocity = Utils.listToString(Arrays.asList(velocity));
    }

    public GameConstants.LettersType[] getLettersType() {
        return Utils.stringToList(this.lettersType, GameConstants.LettersType.class);
    }

    public String getLettersTypeString() {
        return this.lettersType;
    }

    public void setLettersType(Object[] lettersType) {
        this.lettersType = Utils.listToString(Arrays.asList(lettersType));
    }

    public String[] getLanguages() {
        return Utils.stringToList(this.languages, String.class);
    }

    public String getLanguagesString() {
        return this.languages;
    }

    public void setLanguages(String[] language) {
        this.languages = Utils.listToString(Arrays.asList(language));
    }

    public boolean hasAccent() {
        return accent;
    }

    public void setAccent(boolean accent) {
        this.accent = accent;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isDefaultGame() {
        return defaultGame;
    }

    public void setDefaultGame(boolean defaultGame) {
        this.defaultGame = defaultGame;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }


}
