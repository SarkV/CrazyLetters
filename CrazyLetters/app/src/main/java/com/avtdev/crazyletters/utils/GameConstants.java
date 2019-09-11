package com.avtdev.crazyletters.utils;

public class GameConstants {

    public enum Mode{
        DEMO,
        SINGLE_PLAYER,
        INVITATION,
        MULTI_PLAYER
    }

    public enum Level{
        EASY,
        MEDIUM,
        DIFFICULT,
        IMPOSSIBLE
    }

    public enum LettersType{
        HORIZONTAL_MOVE,
        VERTICAL_MOVE,
        DIAGONAL_MOVE,
        SHOW_HIDE
    }

    public static int[] Velocity = new int[]{1, 2, 3, 4, 5, 6, 7};
    public static int LETTERS_DELAY = 1;
    public static int SECONDS_TO_SOUND = 10;

    public enum WordError{
        NOT_EXIST,
        ALREADY_DONE,
        CREATED
    }
}
