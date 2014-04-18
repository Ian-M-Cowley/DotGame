package com.icowley.dotgame;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Text;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DotGameActivity extends Activity implements OnClickListener {

    public static final String NUM_PLAYERS_EXTRA = "num_players";
    private int mNumPlayers = 0;
    private int mActivePlayer = 0;
    private int[] mPlayerColors = { Color.BLUE, Color.RED };
    private int[] mScores = new int[2];
    private Button[][] mButtons = new Button[6][6];
    private View[][] mHorizontalLines = new View[6][5];
    private HashMap<Pair<Integer, Integer>, Boolean> mHorizontalLinesFilledMap = new HashMap<Pair<Integer, Integer>, Boolean>();
    private View[][] mVerticalLines = new View[5][6];
    private HashMap<Pair<Integer, Integer>, Boolean> mVerticalLinesFilledMap = new HashMap<Pair<Integer, Integer>, Boolean>();
    private View[][] mBoxes = new View[5][5];
    private TextView[] mScoreTexts = new TextView[2];
    private Pair<Integer, Integer> mFirstClickLoc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mNumPlayers = savedInstanceState.getInt(NUM_PLAYERS_EXTRA, 1);
        }
        setContentView(R.layout.activity_dot_game);
        Resources res = getResources();
        // Get a handle on all of our views.
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                int id = res.getIdentifier("btn_" + (i + 1) + (j + 1), "id", getPackageName());
                mButtons[i][j] = (Button) findViewById(id);
                mButtons[i][j].setOnClickListener(this);
                if (i != 5) {
                    id = res.getIdentifier("v_" + (i + 1) + (j + 1), "id", getPackageName());
                    mVerticalLines[i][j] = findViewById(id);
                }
                if (j != 5) {
                    id = res.getIdentifier("h_" + (i + 1) + (j + 1), "id", getPackageName());
                    mHorizontalLines[i][j] = findViewById(id);
                }
                if (i != 5 && j != 5) {
                    id = res.getIdentifier("b_" + (i + 1) + (j + 1), "id", getPackageName());
                    mBoxes[i][j] = findViewById(id);
                }
            }
        }
        mScoreTexts[0] = (TextView) findViewById(R.id.p1_score);
        mScoreTexts[1] = (TextView) findViewById(R.id.p2_score);
    }

    /**
     * Does the work to switch from one player's turn to the other.
     */
    private void switchTurns() {
        mFirstClickLoc = null;
        if (mActivePlayer == 0) {
            mActivePlayer = 1;
        } else {
            mActivePlayer = 0;
        }
    }

    /**
     * Increments the score of the player whose player number is passed in.
     * 
     * @param playerNumber
     *            : The number of the player whose score we increment.
     */
    private void incrementScore(int playerNumber) {
        mScores[playerNumber]++;
        mScoreTexts[playerNumber].setText("Player " + (playerNumber + 1) + ": "
                + mScores[playerNumber]);
    }

    /**
     * Finds the location of the button that was clicked.
     * 
     * @param b
     *            : The button to find.
     * @return The location of the button clicked.
     */
    private Pair<Integer, Integer> findButton(Button b) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (b == mButtons[i][j]) {
                    return Pair.create(i, j);
                }
            }
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        Pair<Integer, Integer> clickLocation = findButton((Button) view);
        if (clickLocation != null) {
            Log.d("Location", clickLocation.first + " , " + clickLocation.second);
            if (mFirstClickLoc == null) { // If we haven't made a first "dot"
                                          // choice.
                mFirstClickLoc = clickLocation; // Set our first choice.
                view.setEnabled(false); // Disable the button so they can't
                                        // click it again.
            } else { // We already have a first choice, so go on to check if the
                     // selection is valid.
                boolean validChoice = onSecondChoiceMade(clickLocation);
                if (validChoice) {
                    switchTurns(); // The choice was valid so we switch player
                                   // turns.
                }
            }
        }

    }

    private boolean onSecondChoiceMade(Pair<Integer, Integer> secondLocation) {
        int i1 = mFirstClickLoc.first;
        int j1 = mFirstClickLoc.second;
        int i2 = secondLocation.first;
        int j2 = secondLocation.second;
        int horDist = j2 - j1;
        int vertDist = i2 - i1;
        Pair<Integer, Integer> lineLocation;
        mButtons[i1][j1].setEnabled(true);
        if (horDist == 1 || horDist == -1) { // The player clicked 2 buttons next to eachother.
            if (horDist == 1) { // The left button was clicked first.
                lineLocation = Pair.create(i1, j1);
            } else { // The right button in the pair was clicked first.
                lineLocation = Pair.create(i2, j2);
            }

            if (!mHorizontalLinesFilledMap.containsKey(lineLocation)) { // If the line is not already filled in.
                mHorizontalLines[lineLocation.first][lineLocation.second]
                        .setBackgroundColor(mPlayerColors[mActivePlayer]); // Set the lines color.
                mHorizontalLinesFilledMap.put(lineLocation, true); // Set the line as filled.
                Toast.makeText(this,
                        "Player " + mActivePlayer + " successfully made a horizontal line.",
                        Toast.LENGTH_LONG).show();
                return true;
            }

        } else if (vertDist == 1 || vertDist == -1) { // the player clicked 2 buttons on top of eachother.
            if (vertDist == 1) { // The bottom button was clicked first.
                lineLocation = Pair.create(i1, j1);
            } else { // The top button in the pair was clicked first.
                lineLocation = Pair.create(i2, j2);
            }

            if (!mVerticalLinesFilledMap.containsKey(lineLocation)) { // If the line is not already filled in.
                mVerticalLines[lineLocation.first][lineLocation.second]
                        .setBackgroundColor(mPlayerColors[mActivePlayer]); // Set the lines color.
                mVerticalLinesFilledMap.put(lineLocation, true); // Set the line as filled.
                Toast.makeText(this,
                        "Player " + mActivePlayer + " successfully made a vertical line.",
                        Toast.LENGTH_LONG).show();
                return true;
            }

        }
        Toast.makeText(this,
                "Player " + mActivePlayer + " did not choose two buttons next to eachother.",
                Toast.LENGTH_LONG).show();
        mFirstClickLoc = null;
        return false;
    }

}
