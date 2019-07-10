package com.avtdev.crazyletters.models.realm;

import com.avtdev.crazyletters.utils.GameConstants;
import com.avtdev.crazyletters.utils.Utils;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Game extends RealmObject {

    public static final class PROPERTIES{
        public static final String ID = "id";
        public static final String VELOCITY = "velocity";
        public static final String LETTERS_TYPE = "lettersType";
        public static final String LANGUAGES = "languages";
        public static final String TIME = "time";
        public static final String LAST_USED = "mLastUsed";
    }

    @PrimaryKey
    @SerializedName(PROPERTIES.ID)
    private long id;

    @SerializedName(PROPERTIES.VELOCITY)
    private String mVelocity;

    @SerializedName(PROPERTIES.LETTERS_TYPE)
    private String mLettersType;

    @SerializedName(PROPERTIES.LANGUAGES)
    private String mLanguages;

    @SerializedName(PROPERTIES.TIME)
    private int mTime;

    @SerializedName(PROPERTIES.LAST_USED)
    private Date mLastUsed;

    public Game() {
    }

    public Game(int[] mVelocity, GameConstants.LettersType[] mLettersType, String[] mLanguages, int mTime) {
        this.id = UUID.randomUUID().getMostSignificantBits();
        setVelocity(mVelocity);
        setLettersType(mLettersType);
        setLanguages(mLanguages);
        this.mTime = mTime;
        this.mLastUsed = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer[] getVelocity() {
        return Utils.stringToList(this.mLettersType, Integer.class);
    }

    public void setVelocity(int[] mVelocity) {
        this.mVelocity = Utils.listToString(Arrays.asList(mVelocity));
    }

    public GameConstants.LettersType[] getLettersType() {
        return Utils.stringToList(this.mLettersType, GameConstants.LettersType.class);
    }

    public void setLettersType(GameConstants.LettersType[] lettersType) {
        this.mLettersType = Utils.listToString(Arrays.asList(lettersType));
    }

    public String[] getLanguages() {
        return Utils.stringToList(this.mLanguages, String.class);
    }

    public void setLanguages(String[] mLanguage) {
        this.mLanguages = Utils.listToString(Arrays.asList(mLanguage));
    }

    public int getTime() {
        return mTime;
    }

    public void setTime(int mTime) {
        this.mTime = mTime;
    }

    public Date getLastUsed() {
        return mLastUsed;
    }

    public void setLastUsed(Date mLastUsed) {
        this.mLastUsed = mLastUsed;
    }
}
