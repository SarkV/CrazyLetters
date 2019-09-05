package com.avtdev.crazyletters.models.realm;

import com.avtdev.crazyletters.models.response.DictionaryResponse;

import java.text.Normalizer;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmField;

public class Dictionary extends RealmObject {

    public static final class PROPERTIES{
        public static final String ID = "id";
        public static final String WORD = "word";
        public static final String WORD_NO_ACCENT = "wordNoAccent";
        public static final String WORD_LENGTH = "wordLength";
        public static final String LANGUAGE = "language";
    }

    @PrimaryKey
    @RealmField(PROPERTIES.ID)
    private String id;

    @Index
    @RealmField(PROPERTIES.WORD)
    private String word;

    @Index
    @RealmField(PROPERTIES.WORD_NO_ACCENT)
    private String wordNoAccent;

    @RealmField(PROPERTIES.WORD_LENGTH)
    private int wordLength;

    @RealmField(PROPERTIES.LANGUAGE)
    private String language;

    public Dictionary() {
    }

    public Dictionary(DictionaryResponse dictionaryResponse) {
        this.id = dictionaryResponse.getId();
        this.word = dictionaryResponse.getWord();
        if(dictionaryResponse.getWord() != null){
            this.wordNoAccent = Normalizer.normalize(dictionaryResponse.getWord(), Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        }
        this.wordLength = Math.max(this.word.length(), this.wordNoAccent.length());
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

    public String getWordNoAccent() {
        return wordNoAccent;
    }

    public void setWordNoAccent(String wordNoAccent) {
        this.wordNoAccent = wordNoAccent;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getWordLength() {
        return wordLength;
    }

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }
}
