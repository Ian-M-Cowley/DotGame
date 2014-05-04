package com.icowley.dotgame.model;

import java.util.ArrayList;

import android.util.Pair;

import com.icowley.dotgame.DotGameActivity.ActivePlayer;
import com.icowley.dotgame.DotGameActivity.LineType;

public class BoardState {
    private int[] mScores;
    private ArrayList<Line> mOpenVertLines;
    private ArrayList<Line> mOpenHorizLines;
    private ActivePlayer mActivePlayer;
    private MoveSet mMoveSetToGetToThisState;
    private ArrayList<BoardState> mNextStates = null;
    private int mTotalOpenMoves;
    private int mGridSize;
    private int mDepth = 0;
    private int mLeftRight = 0;

    private static UiUpdateListener mListener;

    public BoardState(int[] scores, ActivePlayer activePlayer, ArrayList<Line> openHorizLines,
            ArrayList<Line> openVertLines, int gridSize) {
        mScores = scores;
        setOpenHorizLines(openHorizLines);
        setOpenVertLines(openVertLines);
        mActivePlayer = activePlayer;
        mTotalOpenMoves = getOpenHorizLines().size() + getOpenVertLines().size();
        mMoveSetToGetToThisState = new MoveSet();
        mNextStates = new ArrayList<BoardState>();
        mGridSize = gridSize;
    }

    public BoardState(UiUpdateListener listener) {
        mScores = new int[2];
        mActivePlayer = ActivePlayer.First;
        mOpenVertLines = new ArrayList<Line>();
        mOpenHorizLines = new ArrayList<Line>();
        mTotalOpenMoves = 0;
        mMoveSetToGetToThisState = new MoveSet();
        mNextStates = new ArrayList<BoardState>();
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
        this.mNextStates = new ArrayList<BoardState>();
        this.mMoveSetToGetToThisState = new MoveSet(other.mMoveSetToGetToThisState);
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

    public int getDepth() {
        return mDepth;
    }

    public void setDepth(int depth) {
        mDepth = depth;
    }

    public int getLeftRight() {
        return mLeftRight;
    }

    public void setLeftRight(int leftRight) {
        mLeftRight = leftRight;
    }

    private void generateAllPossibleNextStatesOfGame() {
        generateNextStateRecursive(this, this, 0);
    }

    private void generateNextStateRecursive(BoardState stateToAddTo, BoardState initialState, int depthOfStateToAddTo) {
        if (depthOfStateToAddTo == 3)
            return;
        if (initialState.mOpenHorizLines.size() == 0 && initialState.mOpenVertLines.size() == 0) {
            return;
        } else {
            for (int i = 0; i < initialState.mOpenHorizLines.size(); i++) {
                //Log.d("IC", "Making H move " + i + " and a depth of " + (depthOfStateToAddTo + 1));
                BoardState nextState = new BoardState(initialState);
                nextState.setDepth(depthOfStateToAddTo + 1);
                boolean shouldKeepGoing = !nextState.onLineSelected(initialState.mOpenHorizLines.get(i), false);
                nextState.mMoveSetToGetToThisState.addMove(mOpenHorizLines.get(i));
                stateToAddTo.mNextStates.add(nextState);
                // Case when the move made leads to an extra turn.
                if (shouldKeepGoing) {
                    generateNextStateRecursive(stateToAddTo, nextState, depthOfStateToAddTo);
                }
                // Now for this state, generate all following states.
                generateNextStateRecursive(nextState, nextState, nextState.mDepth);
            }
            for (int j = 0; j < initialState.mOpenVertLines.size(); j++) {
                //Log.d("IC", "Making V move " + j + " and a depth of " + (depthOfStateToAddTo + 1));
                BoardState nextState = new BoardState(initialState);
                nextState.setDepth(depthOfStateToAddTo + 1);
                boolean shouldKeepGoing = !nextState.onLineSelected(initialState.mOpenVertLines.get(j), false);
                nextState.mMoveSetToGetToThisState.addMove(mOpenVertLines.get(j));
                stateToAddTo.mNextStates.add(nextState);
                // Case when the move made leads to an extra turn.
                if (shouldKeepGoing) {
                    generateNextStateRecursive(stateToAddTo, nextState, depthOfStateToAddTo);
                }
                // Now for this state generate all next possible states.
                generateNextStateRecursive(nextState, nextState, nextState.mDepth);
            }
        }
    }

    public MoveSet getBestMove(int player1Score, int player2Score) {
        generateAllPossibleNextStatesOfGame();
        return null;
    }
    
    /**
     * 
     * @param state
     * @return <Location in next state array, ending score of second player if that state is chosen>
     *          <Location, Score>
     */
    public Pair<Integer,Integer> aStarSearch(BoardState state) {
        if(state.mNextStates == null || state.mNextStates.size() == 0) { // If the passed in node is a leaf, return it.
            return Pair.create(0, state.mScores[1]);
        }
        if(state.mDepth % 2 == 0) { // We are at a max state
            int maxScore = -1;
            int location = -1;
            for(int i = 0; i < mNextStates.size(); i++) {
                Pair<Integer, Integer> info = aStarSearch(mNextStates.get(i));
                if(info.second > maxScore) {
                    maxScore = info.second;
                    location = info.first;
                }
            }
            if(state.mDepth == 0) {
                if(mListener != null) {
                    mListener.makeSetOfMoves(state.mNextStates.get(location).mMoveSetToGetToThisState);
                }
            }
            return Pair.create(location, maxScore);
        } else { // We are at a min state
            int minScore = Integer.MAX_VALUE;
            int location = -1;
            for(int i = 0; i < mNextStates.size(); i++) {
                Pair<Integer, Integer> info = aStarSearch(mNextStates.get(i));
                if(info.second < minScore) {
                    minScore = info.second;
                    location = info.first;
                }
            }
            return Pair.create(location, minScore);
        }
    }

    /**
     * Update state of the board when a line is selected.
     * 
     * @param line
     * @param shouldUpdateUI
     * @return whether you should switch players
     */
    public boolean onLineSelected(Line line, boolean shouldUpdateUI) {
        //Log.d("LINE", line.toString());
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
        
        public void makeSetOfMoves(MoveSet set);
    }
}
