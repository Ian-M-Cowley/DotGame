package com.icowley.dotgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
    
    private Button mOnePlayerButton;
    private Button mTwoPlayerButton;
    private static final int GRIDSIZE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOnePlayerButton = (Button)findViewById(R.id.one_player);
        mOnePlayerButton.setOnClickListener(this);
        mTwoPlayerButton = (Button)findViewById(R.id.two_player);
        mTwoPlayerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if(v == mOnePlayerButton) {
            intent = DotGameActivity.newIntent(this, 1, GRIDSIZE);
        } else if(v == mTwoPlayerButton) {
            intent = DotGameActivity.newIntent(this, 0, GRIDSIZE);
        }
        startActivity(intent);
    }

}
