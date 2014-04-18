package com.icowley.dotgame.model;

import com.icowley.dotgame.DotGameActivity.LineType;

public class Line {
    public int row;
    public int col;
    public LineType lineType;
    
    public Line(int row, int col, LineType type) {
        this.row = row;
        this.col = col;
        this.lineType = type;
    }
    
    public Line(int row, int col) {
        this.row = row;
        this.col = col;
        lineType = null;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof Line) {
            Line other = (Line)o;
            if(this.row == other.row && this.col == other.col && this.lineType == other.lineType) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        String type;
        if(lineType == LineType.Horizontal) {
            type = "Horizontal";
        } else {
            type = "Vertical";
        }
        String s = "Line type = " + type + " | location is ( " + row + " , " + col + " )";
        return s;
    }
}
