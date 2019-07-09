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
        ALLOW_INVITATIONS
    }

    public enum Extras {
        GAME,
        GAME_MODE
    }
}
