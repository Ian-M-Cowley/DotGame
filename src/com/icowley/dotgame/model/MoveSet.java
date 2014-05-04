package com.icowley.dotgame.model;

import java.util.ArrayList;

public class MoveSet {

    private ArrayList<Line> mMoves;
    
    public MoveSet() {
        mMoves = new ArrayList<Line>();
    }
    
    public MoveSet(Line line) {
        mMoves = new ArrayList<Line>();
        mMoves.add(line);
    }
    
    public MoveSet(MoveSet other) {
        this.mMoves = new ArrayList<Line>(other.mMoves);
    }
    
    public void addMove(Line line) {
        mMoves.add(line);
    }
    
    public ArrayList<Line> getMoves() {
        return mMoves;
    }
}
