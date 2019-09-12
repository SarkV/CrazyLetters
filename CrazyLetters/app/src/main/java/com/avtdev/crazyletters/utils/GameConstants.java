package com.avtdev.crazyletters.utils;

public class GameConstants {

    public enum Mode{
        DEMO,
        SINGLE_PLAYER,
        INVITATION,
        MULTI_PLAYER
    }

    public enum Level{
        EASY(0x1),
        MEDIUM(0x2),
        DIFFICULT(0x4),
        IMPOSSIBLE(0x8);

        private long value;

        public long getValue() {
            return value;
        }

        private Level(long value){
            this.value = value;
        }
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
