package com.avtdev.crazyletters.utils;

public class Constants {

    public enum ActionType{
        CREATED,
        UPDATED,
        DELETED;
    }

    public enum SignInStatus{
        OK,
        ERROR_SIGN_IN,
        ERROR_PLAYER
    }

    public enum Preferences {
        NAME,
        SHOW_NOTIFICATIONS,
        ALLOW_INVITATIONS,
        ENABLE_SOUND,
        LAST_SYNC_DICTIONARY,
        LAST_SYNC_GAME_MODES,
        WITHOUT_ADS,
        SIGN_IN_REFUSED
    }

    public static class Firebase{
        public static int NUMSINCRO = 2;
        public static String DICTIONARY = "dictionary";
        public static String GAMEMODE = "gameMode";
        public static String CREATEDAT = "createdAt";
    }

    public enum Extras {
        GAME,
        GAME_MODE,
        GAME_MODIFIED,
        LANGUAGE_LIST
    }

    public static final String ARRAY_SEPARATOR = ";";
    public static final int WORD_DELAY = 500;
}
