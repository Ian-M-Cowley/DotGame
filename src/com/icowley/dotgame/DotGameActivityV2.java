package com.icowley.dotgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.icowley.dotgame.model.BoardState;
import com.icowley.dotgame.model.BoardState.UiUpdateListener;
import com.icowley.dotgame.model.Line;

public class DotGameActivityV2 extends Activity implements OnClickListener, UiUpdateListener {
    public static final String TAG = DotGameActivityV2.class.getSimpleName();
    private static final String NUM_BOTS_EXTRA = "num_players";
    private static final String GRID_SIZE_EXTRA = "grid_size";

    private int mNumBots;
    private ActivePlayer mActivePlayer;
    private int mGridSize;
    private static int[] mPlayerColors = { Color.BLUE, Color.RED };
    private static int[] mBoxColors = { R.color.light_blue, R.color.light_red };

    private Button[][] mHorizontalLineViews;
    private Button[][] mVerticalLineViews;
    private View[][] mBoxes;
    private TextView[] mScoreTexts;
    private ProgressBar mProgress;
    private LinearLayout mGridContainer;

    private BoardState mCurrentBoardState;

    public static enum LineType {
        Horizontal, Vertical
    }

    public static enum ActivePlayer {
        First, Second
    }

    public static Intent newIntent(Context context, int numBots, int gridSize) {
        Intent intent = new Intent(context, DotGameActivityV2.class);
        intent.putExtra(NUM_BOTS_EXTRA, numBots);
        intent.putExtra(GRID_SIZE_EXTRA, gridSize);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dot_game_v2);
        Intent intent = getIntent();
        mGridContainer = (LinearLayout) findViewById(R.id.content);
        mGridSize = intent.getIntExtra(GRID_SIZE_EXTRA, 0);
        mNumBots = intent.getIntExtra(NUM_BOTS_EXTRA, 0);
        mScoreTexts[0] = (TextView) findViewById(R.id.p1_score);
        mScoreTexts[1] = (TextView) findViewById(R.id.p2_score);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        mHorizontalLineViews = new Button[mGridSize][mGridSize - 1];
        mVerticalLineViews = new Button[mGridSize - 1][mGridSize];
        mBoxes = new View[mGridSize - 1][mGridSize - 1];
        createGrid(mGridSize);
        createBoardState();
        onDoneCreatingGrid();
        mActivePlayer = ActivePlayer.First;
        if (mNumBots == 2) {
            makeComputerMove(mActivePlayer);
        }
    }

    private void createBoardState() {
        mCurrentBoardState = new BoardState(this);
        for (int row = 0; row < mGridSize; row++) {
            for (int col = 0; col < mGridSize; col++) {
                if (col != mGridSize - 1) {
                    Line line = new Line(row, col, LineType.Horizontal);
                    mCurrentBoardState.addLine(line);
                }
                if (row != mGridSize - 1) {
                    Line line = new Line(row, col, LineType.Vertical);
                    mCurrentBoardState.addLine(line);
                }
            }
        }
        mCurrentBoardState.setGridSize(mGridSize);
        Log.d("IC", mCurrentBoardState.toString());
    }

    private void createGrid(int gridSize) {
        int l = 150;
        int s = 50;
        LayoutInflater inflater = LayoutInflater.from(this);
        LayoutParams dotParams = new LayoutParams(s, s);
        LayoutParams boxParams = new LayoutParams(l, l);
        LayoutParams hParams = new LayoutParams(l, s);
        LayoutParams vParams = new LayoutParams(s, l);
        for (int row = 0; row < (gridSize * 2) - 1; row++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            LayoutParams params;
            if (row % 2 == 0) {
                params = new LayoutParams(mGridSize * s + (mGridSize - 1) * l, s);
            } else {
                params = new LayoutParams(mGridSize * s + (mGridSize - 1) * l, l);
            }
            for (int col = 0; col < (gridSize * 2) - 1; col++) {
                if (row % 2 == 0) { // Horizontal Button row
                    if (col % 2 == 0) { // Add a dot
                        View dot = inflater.inflate(R.layout.dot, null);
                        dot.setLayoutParams(dotParams);
                        rowLayout.addView(dot);
                    } else { // Add a Horizontal Button
                        Button button = (Button) inflater.inflate(R.layout.horizontal_button, null);
                        button.setLayoutParams(hParams);
                        button.setOnClickListener(this);
                        button.setTag(new Line(row / 2, col / 2, LineType.Horizontal));
                        mHorizontalLineViews[row / 2][col / 2] = button;
                        rowLayout.addView(button);
                    }
                } else { // Vertical Button row
                    if (col % 2 == 0) { // Add a Vertical Button
                        Button button = (Button) inflater.inflate(R.layout.vertical_button, null);
                        button.setLayoutParams(vParams);
                        button.setOnClickListener(this);
                        button.setTag(new Line(row / 2, col / 2, LineType.Vertical));
                        mVerticalLineViews[row / 2][col / 2] = button;
                        rowLayout.addView(button);
                    } else { // Add a box
                        View box = inflater.inflate(R.layout.box, null);
                        box.setLayoutParams(boxParams);
                        mBoxes[row / 2][col / 2] = box;
                        rowLayout.addView(box);
                    }
                }
            }
            rowLayout.setLayoutParams(params);
            mGridContainer.addView(rowLayout);
        }
    }

    private void onDoneCreatingGrid() {
        mProgress.setVisibility(View.GONE);
        mGridContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Makes Computer Move
     * 
     * @return whether to have the same player go again.
     */
    private boolean makeComputerMove(ActivePlayer player) {
        return false;
    }

    private void onLineSelected(Line line) {
        boolean shouldSwitchPlayers = mCurrentBoardState.onLineSelected(line, true);
        if (shouldSwitchPlayers) {
            switchPlayers();
        }
    }

    private void switchPlayers() {
        if (mActivePlayer == ActivePlayer.First) {
            mActivePlayer = ActivePlayer.Second;
        } else {
            mActivePlayer = ActivePlayer.First;
        }
    }

    @Override
    public void onClick(View v) {
        Line tag = null;
        if (v.getTag() instanceof Line) {
            tag = (Line) v.getTag();
            onLineSelected(tag);
            v.setEnabled(false);
        }
    }

    @Override
    public void colorLine(Line line) {
        if (line.lineType == LineType.Horizontal) {
            mHorizontalLineViews[line.row][line.col].setBackgroundColor(mPlayerColors[mActivePlayer.ordinal()]);
        } else {
            mVerticalLineViews[line.row][line.col].setBackgroundColor(mPlayerColors[mActivePlayer.ordinal()]);
        }
    }

    @Override
    public void colorBox(Pair<Integer, Integer> location) {
        mBoxes[location.first][location.second].setBackgroundColor(getResources().getColor(
                mBoxColors[mActivePlayer.ordinal()]));
        int playerNum = mActivePlayer.ordinal() + 1;
        mScoreTexts[playerNum - 1]
                .setText("Player " + playerNum + ": " + mCurrentBoardState.getScores()[playerNum - 1]);
    }
}
