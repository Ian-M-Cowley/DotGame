package com.icowley.dotgame.model;

import java.util.ArrayList;

import com.icowley.dotgame.DotGameActivity.ActivePlayer;
import com.icowley.dotgame.DotGameActivity.LineType;

import android.util.Log;
import android.util.Pair;

public class BoardState {
    private int[] mScores;
    private ArrayList<Line> mOpenVertLines;
    private ArrayList<Line> mOpenHorizLines;
    private ActivePlayer mActivePlayer;
    private ArrayList<BoardState> mNextStates = null;
    private int mTotalOpenMoves;
    private int mGridSize;

    private static UiUpdateListener mListener;

    public BoardState(int[] scores, ActivePlayer activePlayer, ArrayList<Line> openHorizLines,
                    ArrayList<Line> openVertLines, int gridSize) {
        mScores = scores;
        setOpenHorizLines(openHorizLines);
        setOpenVertLines(openVertLines);
        mActivePlayer = activePlayer;
        mTotalOpenMoves = getOpenHorizLines().size() + getOpenVertLines().size();
        mGridSize = gridSize;
    }

    public BoardState(UiUpdateListener listener) {
        mScores = new int[2];
        mActivePlayer = ActivePlayer.First;
        mOpenVertLines = new ArrayList<Line>();
        mOpenHorizLines = new ArrayList<Line>();
        mTotalOpenMoves = 0;
        mListener = listener;
    }

    public BoardState(BoardState other) {
        this.mOpenHorizLines = new ArrayList<Line>(other.mOpenHorizLines);
        this.mOpenVertLines = new ArrayList<Line>(other.mOpenVertLines);
        this.mScores = new int[2];
        this.mScores[0] = other.mScores[0];
        this.mScores[1] = other.mScores[1];
        this.mTotalOpenMoves = other.mTotalOpenMoves;
        this.mActivePlayer = other.mActivePlayer;
        this.mGridSize = other.mGridSize;
    }

    public int[] getScores() {
        return mScores;
    }

    public ActivePlayer getActivePlayer() {
        return mActivePlayer;
    }

    public ArrayList<Line> getOpenVertLines() {
        return mOpenVertLines;
    }

    public void setOpenVertLines(ArrayList<Line> mOpenVertLines) {
        this.mOpenVertLines = mOpenVertLines;
    }

    public ArrayList<Line> getOpenHorizLines() {
        return mOpenHorizLines;
    }

    public void setOpenHorizLines(ArrayList<Line> openHorizLines) {
        mOpenHorizLines = openHorizLines;
    }

    public int getGridSize() {
        return mGridSize;
    }

    public void setGridSize(int gridSize) {
        mGridSize = gridSize;
    }

    public void setScore(ActivePlayer player, int score) {
        if (player == ActivePlayer.First) {
            mScores[0] = score;
        } else {
            mScores[1] = score;
        }
    }

    public void setListener(UiUpdateListener listener) {
        mListener = listener;
    }

    public void removeLine(Line line) {
        if (line.lineType == LineType.Horizontal) {
            mOpenHorizLines.remove(line);
        } else {
            mOpenVertLines.remove(line);
        }
        mTotalOpenMoves--;
    }

    public void addLine(Line line) {
        if (line.lineType == LineType.Horizontal) {
            mOpenHorizLines.add(line);
        } else {
            mOpenVertLines.add(line);
        }
        mTotalOpenMoves++;
    }

    private void generateAllPossibleNextStatesOfGame() {

    }

    public Pair<Pair<Integer, Integer>, LineType> getBestMove(int player1Score, int player2Score) {
        return null;
    }

    public boolean onLineSelected(Line line, boolean shouldUpdateUI) {
        Log.d("LINE", line.toString());
        removeLine(line);
        if (shouldUpdateUI) {
            mListener.colorLine(line);
        }
        boolean shouldSwitch = !checkForCompleteSquare(line, shouldUpdateUI);
        if (shouldSwitch) {
            switchPlayers();
        }
        return shouldSwitch;
    }

    private void switchPlayers() {
        if (mActivePlayer == ActivePlayer.First) {
            mActivePlayer = ActivePlayer.Second;
        } else {
            mActivePlayer = ActivePlayer.First;
        }
    }

    private boolean checkForCompleteSquare(Line line, boolean shouldUpdateUI) {
        boolean didCompleteSquare = false;
        int row = line.row;
        int col = line.col;
        if (line.lineType == LineType.Vertical) {
            // If this is the right side of the square.
            if (col != 0) {
                Line hT = new Line(row, col - 1, LineType.Horizontal);
                Line hB = new Line(row + 1, col - 1, LineType.Horizontal);
                Line vL = new Line(row, col - 1, LineType.Vertical);
                if (!mOpenHorizLines.contains(hT) && !mOpenHorizLines.contains(hB) && !mOpenVertLines.contains(vL)) {
                    mScores[mActivePlayer.ordinal()]++;
                    if (shouldUpdateUI) {
                        mListener.colorBox(Pair.create(row, col - 1));
                    }
                    didCompleteSquare = true;
                }
            }

            // If this is the left side of the square.
            if (col != (mGridSize - 1)) {
                Line hT = new Line(row, col, LineType.Horizontal);
                Line hB = new Line(row + 1, col, LineType.Horizontal);
                Line vR = new Line(row, col + 1, LineType.Vertical);
                if (!mOpenHorizLines.contains(hT) && !mOpenHorizLines.contains(hB) && !mOpenVertLines.contains(vR)) {
                    mScores[mActivePlayer.ordinal()]++;
                    if (shouldUpdateUI) {
                        mListener.colorBox(Pair.create(row, col));
                    }
                    didCompleteSquare = true;
                }
            }
        } else {

            // If this is the top side of the square.
            if (row != (mGridSize - 1)) {
                Line hB = new Line(row + 1, col, LineType.Horizontal);
                Line vL = new Line(row, col, LineType.Vertical);
                Line vR = new Line(row, col + 1, LineType.Vertical);
                if (!mOpenVertLines.contains(vR) && !mOpenHorizLines.contains(hB) && !mOpenVertLines.contains(vL)) {
                    mScores[mActivePlayer.ordinal()]++;
                    if (shouldUpdateUI) {
                        mListener.colorBox(Pair.create(row, col));
                    }
                    didCompleteSquare = true;
                }
            }

            // If this is the bottom side of the square.
            if (row != 0) {
                Line hT = new Line(row - 1, col, LineType.Horizontal);
                Line vL = new Line(row - 1, col, LineType.Vertical);
                Line vR = new Line(row - 1, col + 1, LineType.Vertical);
                if (!mOpenHorizLines.contains(hT) && !mOpenVertLines.contains(vL) && !mOpenVertLines.contains(vR)) {
                    mScores[mActivePlayer.ordinal()]++;
                    if (shouldUpdateUI) {
                        mListener.colorBox(Pair.create(row - 1, col));
                    }
                    didCompleteSquare = true;
                }
            }
        }
        return didCompleteSquare;
    }

    @Override
    public String toString() {
        String s = "I have " + mOpenHorizLines.size() + " horizontal lines and " + mOpenVertLines.size()
                        + " vertical lines.";
        return s;
    }

    public interface UiUpdateListener {
        public void colorLine(Line line);

        public void colorBox(Pair<Integer, Integer> location);
    }
}
