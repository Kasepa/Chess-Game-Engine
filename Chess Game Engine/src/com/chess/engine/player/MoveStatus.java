package com.chess.engine.player;

public enum MoveStatus {
    DONE {
        @Override
        public boolean isDone() {
            // TODO Auto-generated method stub
            return true;
        }
    },
    ILLEGAL_MOVE {
        @Override
        public boolean isDone() {
            // TODO Auto-generated method stub
            return false;
        }
    },
    LEVES_PLAYER_IN_CHECK {
        @Override
        public boolean isDone() {
            // TODO Auto-generated method stub
            return false;
        }
    };
    public abstract boolean isDone();
}
