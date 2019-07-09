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
        NORMAL,
        DIFFICULT
    }

    public enum LettersType{
        HORIZONTAL_MOVE,
        VERTICAL_MOVE,
        DIAGONAL_MOVE,
        SHOW_HIDE
    }

    public static int[] Velocity = new int[]{1, 2, 3, 4, 5, 6, 7};
}
